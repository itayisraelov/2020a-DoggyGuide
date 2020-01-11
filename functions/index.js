'use strict'
const admin = require('firebase-admin');
const functions = require('firebase-functions');
admin.initializeApp(functions.config().firebase);

const firebaseTriggers = functions.region('europe-west1').firestore;
const db = admin.firestore();

exports.postNotification = firebaseTriggers
      .document('postNotifications/{notificatioId}').onWrite((snap, context) => {
      const notifcationRecieverId = snap.data().mReciever;
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
      return db.collection('dog owners')
        .document(notifcationRecieverId)
        .get()
        .then(recieverDoc => {
            console.log('Retrieving FCM tokens');
            const tokens = recieverDoc.data().mTokens;
            console.log('Sending notification payload');
            return admin.message().sendToDevice(tokens, payload);
        });
});
