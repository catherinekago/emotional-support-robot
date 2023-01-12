# info https://towardsdatascience.com/essentials-for-working-with-firestore-in-python-372f859851f7


# Step 1 Add Firebase Admin SDK to python app in terminal
# If you have pip in your PATH environment variable: pip install --upgrade firebase-admin 
# Otherwise: py -m pip install firebase-admin
# if this does not work, do: setx PATH "%PATH%;C:\Python34\Scripts"

# Step 2 connect firebase firestore instance
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

import threading
import time

# Use the private key file of the service account directly.
cred = credentials.Certificate("firebase-credentials/emotional-support-robot-firebase-adminsdk-pvqeh-b7cd18640e.json")
app = firebase_admin.initialize_app(cred)
db = firestore.client()

# Create Firebase Firestore listener

# Create an Event for notifying main thread.
callback_done = threading.Event()

# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
    print(f'Received message from: {doc_snapshot[0].get("sender")}')
    sender = doc_snapshot[0].get("sender");
    emotion = doc_snapshot[0].get("emotion");
    print("retrieved emotion ", emotion)

    # TODO: execute robot code depending on emotion
    if (sender == "ANDROID"):
        time.sleep(5)
        print("sleep timer as placeholder for robot interactions:  5 seconds")

        # and once robot execution is finished, update message:
        db.collection(u'android-robot-communication').document("MESSAGE").update({u'sender': "ROBOT"})
        callback_done.set()


docs_ref = db.collection(u'android-robot-communication').document("MESSAGE");

# Watch the document
doc_watch = docs_ref.on_snapshot(on_snapshot)


try:
    while True:
        pass
except KeyboardInterrupt: # Ctrl-C 
    pass