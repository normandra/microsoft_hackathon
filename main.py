import cognitive_face as CF
from PIL import Image

KEY = '81666ab628ea49bf8d2663a91b68080b'  # Replace with a valid Subscription Key here.
CF.Key.set(KEY)

BASE_URL = 'https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect'  # Replace with your regional Base URL
CF.BaseUrl.set(BASE_URL)

img_url = 'https://raw.githubusercontent.com/Microsoft/Cognitive-Face-Windows/master/Data/detection1.jpg'
#test = Image.open("test/output_00001.jpg")

path = "test/output_00002.jpg"

attributes = (
                'age,gender,headPose,smile,facialHair,glasses,emotion,hair,'
                'makeup,occlusion,accessories,blur,exposure,noise')
result = CF.face.detect(img_url, False, False, attributes)

#result = CF.face.detect("test/output_00001.jpg")

print(result)