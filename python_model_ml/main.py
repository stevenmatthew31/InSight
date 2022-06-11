#!/usr/bin/python3

import os
from flask import Flask, json
import requests

content = requests.get("https://storage.googleapis.com/capstone-ml-model-bucket/model.json")
data = json.loads(content.content)

api = Flask(__name__)

@api.route('/', methods=['GET'])
def get_home():
  return "Halaman Utama"

@api.route('/model', methods=['GET'])
def get_data():
  return json.dumps(data)

if __name__ == '__main__':
    api.run(debug=True, host="0.0.0.0", port=int(os.environ.get("PORT", 80))) 