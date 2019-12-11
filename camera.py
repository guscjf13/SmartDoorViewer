# import the necessary packages
from picamera.array import PiRGBArray
from picamera import PiCamera
import threading
import time
import cv2
import picamera
import io
import numpy as np
import urllib
from datetime import datetime, timedelta
import firebase_admin
from firebase_admin import db
from firebase_admin import credentials
from fcm import send_fcm_notification

def detect(img, cascade):
    rects = cascade.detectMultiScale(img, scaleFactor=1.3, minNeighbors=4, minSize=(30, 30),
                                     flags=cv2.CASCADE_SCALE_IMAGE)
    if len(rects) == 0:
        return []
    rects[:,2:] += rects[:,:2]
    return rects

def upload_file(jpeg_bytes): #firebase storage에 jpg 파일을 업로드합니다.
    now = datetime.now()
    date = now.strftime('%Y%m%d')
    time = now.strftime('%H%M')
    
    my_url = "https://firebasestorage.googleapis.com/v0/b/smartdoorviewer-85ca9.appspot.com/o/" + date + '%2F' + time + '.jpg'
    my_headers = {"Content-Type": "image/jpeg"}
    my_request = urllib.request.Request(my_url, data=jpeg_bytes, headers=my_headers, method="POST")
    
    try:
        loader = urllib.request.urlopen(my_request)
    except urllib.error.URLError as e:
        message = json.loads(e.read())
        print(message["error"]["message"])
    else:
        print(loader.read())
        save_database(date, time)
        send_fcm_notification()
        
def save_database(date, time): #firebase database에 data를 저장합니다.
    if (not len(firebase_admin._apps)):
        cred = credentials.Certificate('serviceAccountKey.json') 
        default_app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://smartdoorviewer-85ca9.firebaseio.com'})
    ref = db.reference()
    date_ref = ref.child(date) 
    if date_ref.get() is None:
        ref.update({date:'0'})
        date_ref = ref.child(date)  
    date_ref.push(time)
        
class Camera(object):
    camera = None
    thread = None
    frame = None
    
    def initialize(self):
        if Camera.thread is None:
            Camera.thread = threading.Thread(target=self._thread)
            Camera.thread.start()
            print("start")
        #if Camera.capture_thread is None:
        #    Camera.capture_thread = threading.Thread(target=self._capture_thread)
        #    Camera.capture_thread.start()

        while self.frame is None:
            time.sleep(0)
                
    def get_frame(self):
        self.initialize()
        return self.frame

    @classmethod
    def _thread(cls):
        if cls.camera is None:
            cls.camera = PiCamera()
            cls.resolution = (320, 240)
            cls.hflip = True
            cls.vflip = True
            cls.framerate = 32
            cascade = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")

        cls.camera.start_preview()
        time.sleep(2)

        stream = io.BytesIO()
        rescent_detect_time = None
        for foo in cls.camera.capture_continuous(stream, 'jpeg',
                                             use_video_port=True):
            stream.seek(0)
            
            frame = stream.read()
            img = cv2.imdecode(np.frombuffer(frame, np.uint8), -1)
            gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            gray = cv2.equalizeHist(gray)

            rects = detect(gray, cascade)
            if rects != []:
                print("face detect!")
                if rescent_detect_time is not None:
                    now = datetime.now()
                    delta = now-rescent_detect_time
                    if delta.seconds > 30:
                        my_thread = threading.Thread(target=upload_file, args=[frame])
                        my_thread.start()
                        rescent_detect_time = now
                if rescent_detect_time is None:
                    my_thread = threading.Thread(target=upload_file, args=[frame])
                    my_thread.start()
                    rescent_detect_time = datetime.now()
                                
            cls.frame = frame
            
            stream.seek(0)
            stream.truncate()