#-*-coding:utf-8 -*-
import time
from pyfcm import FCMNotification

# 파이어베이스 콘솔에서 얻어 온 API키를 넣어 줌
push_service = FCMNotification(api_key="AAAAhOiQCxI:APA91bFFGmA7HuzH6NjXQWxep793C7uGZZmhHS4kG9l__PYzhupeXffrCo0-Fe8AeA1wZOmjpA7MtcQdfKBCq_aVtFA5t4XMQO4b3ryo5qhj4PaPCfMd23H1DCXnVdhZodFuxpiGQxUb")
'''
여기서는 지정된 토큰 1개만 넣어서 사용함. 
좀 더 확장할려면 토큰을 앱으로 부터 받거나 앱서버 DB에서 가져와서 다수의 토큰에 알림을 발송 할 수도 있음.
'''
mToken = "dxDhqJNLgfQ:APA91bH56eOxySDPl3zzmck7OvxHWHRzPHuNm-H1-O1UGUa5fgjJW1nWCYEiUbW0gz3z5Kp1Ho8RVWe9hxaQzrWozp7R2n_HfwIzEcoyStqaJyEMq3066k4WqFQmgFqiuQVF_6HVkZ7H"
nth = 0

def sendMessage(p):

    global nth
    nth += 1
    registration_id = mToken

    data_message = {
        "body" : str(nth) + "번째 테스트 알림입니다."
    }
    
    #data payload만 보내야 안드로이드 앱에서 백그라운드/포그라운드 두가지 상황에서 onMessageReceived()가 실행됨
    result = push_service.single_device_data_message(registration_id=registration_id, data_message=data_message)
    print(result)

def _main():
    
    while True:
        sendMessage(80)
        time.sleep(60 * 1)

if __name__ == "__main__":
	_main()

