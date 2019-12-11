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
from datetime import datetime

def detect(img, cascade):
    rects = cascade.detectMultiScale(img, scaleFactor=1.3, minNeighbors=4, minSize=(30, 30),
                                     flags=cv2.CASCADE_SCALE_IMAGE)
    if len(rects) == 0:
        return []
    rects[:,2:] += rects[:,:2]
    return rects

def draw_rects(img, rects, color):
    for x1, y1, x2, y2 in rects:
        cv2.rectangle(img, (x1, y1), (x2, y2), color, 2)

def upload_file(jpeg_bytes):
    now = datetime.now()
    date = now.strftime('%Y%m%d')
    time = now.strftime('%H%M')
    
    my_url = "https://firebasestorage.googleapis.com/v0/b/smartdoorviewer-85ca9.appspot.com/o/" + date + '%2F' + time + '.jpeg'
    print(my_url)
    my_headers = {"Content-Type": "image/jpeg"}
    my_request = urllib.request.Request(my_url, data=jpeg_bytes, headers=my_headers, method="POST")

    try:
        loader = urllib.request.urlopen(my_request)
    except urllib.error.URLError as e:
        message = json.loads(e.read())
        print(message["error"]["message"])
    else:
        print(loader.read())
        
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
        detect_flag = False
        cnt = 0
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
                if detect_flag == False:
                    upload_file(frame)
                    detect_flag = True
                
            if detect_flag:
                cnt+=1
            
            if cnt >= 50:
                detect_flag = False
                cnt = 0
                                
            cls.frame = frame
            
            stream.seek(0)
            stream.truncate()