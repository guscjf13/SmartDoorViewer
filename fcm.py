# -*- coding: utf-8 -*-

import requests
import json


def send_fcm_notification():
    # fcm 푸시 메세지 요청 주소
    url = 'https://fcm.googleapis.com/fcm/send'
    
    # 인증 정보(서버 키)를 헤더에 담아 전달
    headers = {
        'Authorization': 'key=AAAAhOiQCxI:APA91bFFGmA7HuzH6NjXQWxep793C7uGZZmhHS4kG9l__PYzhupeXffrCo0-Fe8AeA1wZOmjpA7MtcQdfKBCq_aVtFA5t4XMQO4b3ryo5qhj4PaPCfMd23H1DCXnVdhZodFuxpiGQxUb',
        'Content-Type': 'application/json'
    }

    # 보낼 내용과 대상을 지정
    content = {
        'to': "cTCsFbxeuoI:APA91bFV85WmVi1nf-CWpmlk8lmon5iEfExpMlyps-btGhtNQMNe65P_jaTzfT7PiszrgQbMu7TsJQ0Axfpq0to4lLZtrKPBsTN_70ucg0eAQikZEpzV_kRsv6GnL55mCxcN__OEoVFG",
        'notification': {
            'title': "스마트 도어 뷰어",
            'body': "누가 나타났어요!!",
            'sound': 'default'
        }
    }

    # json 파싱 후 requests 모듈로 FCM 서버에 요청
    requests.post(url, data=json.dumps(content), headers=headers)
