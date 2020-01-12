'use strict'
const admin = require('firebase-admin');
const functions = require('firebase-functions');
admin.initializeApp(functions.config().firebase);

const firebaseTriggers = functions.region('europe-west1').firestore;
const db = admin.firestore();

exports.postNotification = firebaseTriggers
      .document('postNotifications/{notificatioId}').onCreate((snap, context) => {
      const notifcationRecieverId = snap.data().mReciever;
      console.log('reciever_id is ' + notifcationRecieverId);
      const payload = {
        data: {
            notification_type: 'POST',
            title: snap.data().mTitle,
            body: snap.data().mDescription,
            sender_id: snap.data().mSender,
            reciever_id: snap.data().mReciever,
            notification_id: context.params.notificatioId
        }
      };
      return db.collection('dogOwners')
        .doc(notifcationRecieverId)
        .get()
        .then(recieverDoc => {
            console.log('Retrieving FCM tokens');
            const tokens = recieverDoc.data().mTokens;
            console.log('Sending notification payload');
            return admin.messaging().sendToDevice(tokens, payload);
        });
});
