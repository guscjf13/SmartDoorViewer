#!/usr/bin/env python
#-*- coding: utf-8 -*-
from flask import Flask, render_template, Response
from camera import Camera

app = Flask(__name__)

#카메라 설정 - camera.py파일에서 설정 가능


@app.route('/')
def index():
    return render_template('index.html')

def gen(camera):
    while True:
        frame = camera.get_frame()
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

@app.route('/video_feed')
def video_feed():
    return Response(gen(Camera()),
                    mimetype='multipart/x-mixed-replace; boundary=frame')

if __name__ == '__main__':
    app.run(host='203.252.166.213', debug=True, threaded=True)
