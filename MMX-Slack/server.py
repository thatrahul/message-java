__author__ = 'sdatar'

import os;
import requests;
from flask import Flask, render_template
from flask import json
from flask import request

TARGET_URL = "http://localhost:5000/callback"
ASSETS_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'assets')

slack_obj = {}

print ASSETS_DIR

app = Flask(__name__, template_folder='templates')

def search_channel(name, dict):
    return [element for element in dict if element['name'] == name]

def get_channel_name(obj):
    params = {
        'token' : obj['token'],
        'pretty' : 1
    }
    response = requests.get('https://slack.com/api/channels.list', params = params)
    json = response.json();
    element = search_channel(obj['ch_name'], json['channels']);
    print 'Searched element : ', element
    return element[0]['id']

@app.route("/")
def home():
    return render_template('index.html')

@app.route("/createHook", methods=['POST'])
def createHook():
    if request.headers['Content-Type'] == 'application/json':
        payload = request.json
        url = payload['mmx_url']
        appId = payload['mmx_app_id']
        apiKey = payload['mmx_api_key']
        data = {
            'hookName': 'message queued',
            'eventType' : 'MESSAGE_WITH_META',
            'eventConfig' : {
                payload['mmx_hdr_key'] : payload['mmx_hdr_value']
            },
            'targetURL' : TARGET_URL
        }

        headers = {
            'X-mmx-app-id' : appId,
            'X-mmx-api-key' : apiKey,
            'Content-Type' : 'application/json'
        }

        slack_obj['token'] = payload['slack_api_token']
        slack_obj['ch_name'] = payload['slack_channel_name']
        slack_obj['msg'] = payload['slack_msg_body']
        slack_obj['ch_id'] = get_channel_name(slack_obj);
        print 'Constructed slack obj :', slack_obj
        response = requests.post(url, data=json.dumps(data), headers=headers);
        print "Response : ", response
        return "OK"

@app.errorhandler(500)
def internal_error(error):
    print "Error is ", error
    return "500 error"

@app.route("/callback", methods=['POST'])
def callback():
    print 'callback invoked'
    params = {
        'token' : slack_obj['token'],
        'channel' : slack_obj['ch_id'],
        'text' :  slack_obj['msg'],
        'pretty' : 1
    }
    response = requests.post('https://slack.com/api/chat.postMessage', params=params)
    print 'Response = ', response
    return "OK"

if __name__ == "__main__":
    app.run(host='0.0.0.0')
