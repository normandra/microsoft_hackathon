import cognitive_face as CF
from PIL import Image
import json
import time

KEY = 'd362531db61f41728ebff0e734b80fc1'  # Replace with a valid Subscription Key here.
CF.Key.set(KEY)

BASE_URL = 'https://westcentralus.api.cognitive.microsoft.com/face/v1.0'  # Replace with your regional Base URL
CF.BaseUrl.set(BASE_URL)

#img_url = 'https://raw.githubusercontent.com/Microsoft/Cognitive-Face-Windows/master/Data/detection1.jpg'
#test = Image.open("test/output_00001.jpg")
#path = "test/output_00002.jpg"

master_result = {}
time_factor = 10
folder = "aj"

for i in range(1,130):
    if len(str(i)) < 2:
        path = folder + "/output_0000" + str(i) + ".jpg"
    elif len(str(i)) < 3:
        path = folder + "/output_000" + str(i) + ".jpg"
    elif len(str(i)) < 4:
        path = folder + "/output_00" + str(i) + ".jpg"
    elif len(str(i)) < 5:
        path = folder + "/output_0" + str(i) + ".jpg"
    

    timestamp = (i - 1) * time_factor 

    attributes = (
                'age,gender,headPose,smile,facialHair,glasses,emotion,hair,'
                'makeup,occlusion,accessories,blur,exposure,noise')
    result = CF.face.detect(path, False, False, attributes)
    master_result[str(timestamp)] = result
    print("Sleeping now at: " + str(i))
    time.sleep(4)


#result = CF.face.detect("test/output_00001.jpg")
print(master_result)

with open('data.json', 'w') as outfile:
    json.dump(master_result, outfile)