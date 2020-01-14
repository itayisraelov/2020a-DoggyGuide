'use strict'
const admin = require('firebase-admin');
const functions = require('firebase-functions');
admin.initializeApp(functions.config().firebase);

const firebaseTriggers = functions.region('europe-west1').firestore;
const db = admin.firestore();

exports.friendReqNotification = firebaseTriggers
      .document('notifications/{userRecieverId}/keysNotifications/{notificationId}').onCreate((snap, context) => {
      const notifcationRecieverId = snap.data().receiverId;
      console.log('reciever_id is ' + notifcationRecieverId);
      const payload = {
        notification:{
          click_action: "com.technion.doggyguide_TARGET_NOTIFICATION"
        },
        data: {
            notification_type: 'Friend_Req',
            title: snap.data().type,
            body: snap.data().text,
            sender_id: snap.data().from,
            reciever_id: snap.data().receiverId,
            notification_id: context.params.notificationId
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


exports.onAcceptPost = firebaseTriggers
      .document('dogOwners/{userId}/acceptedPosts/{postId}').onCreate((snap, context) => {
      const postId = context.params.postId;
      return db.collection('dogOwners')
        .get()
        .then(snapshot => {
          snapshot.forEach(doc => {
             const postCopyRef = db.collection('dogOwners')
                                   .doc(doc.id)
                                   .collection('posts')
                                   .doc(postId);
            postCopyRef.delete();
          });
          return;
        });
});


exports.onAcceptFriend = firebaseTriggers
      .document('dogOwners/{recieverId}/friends/{senderId}').onCreate((snap, context) => {
      const recieverId = context.params.recieverId;
      const senderId = context.params.senderId;
      const reciever = db.doc('dogOwners/' + recieverId);
      const friendRef = db.doc('dogOwners/' + senderId + "/friends/" + recieverId);
      const newFriend = {
        reference: reciever
      };
      return friendRef.set(newFriend);
});

exports.onCancelFriend = firebaseTriggers
      .document('dogOwners/{mCurrId}/friends/{deletedId}').onDelete((snap, context) => {
      const mCurrId = context.params.mCurrId;
      const deletedId = context.params.deletedId;
      const friendshipRef = db.doc('dogOwners/' + deletedId + "/friends/" + mCurrId);
      return friendshipRef.delete();
});
