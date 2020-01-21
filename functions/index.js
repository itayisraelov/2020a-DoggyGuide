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


exports.sendMessageInChatNotification = firebaseTriggers
      .document('Receiver/{senderId}/friends/{recieverId}/messages/{messageId}').onCreate((snap, context) => {
      const notifcationRecieverId = context.params.recieverId;
      console.log('reciever_id is ' + notifcationRecieverId);
      const name = snap.data().fromName;
      const payload = {
        data: {
            notification_type: 'CHAT',
            title: 'message from: ' + name,
            body: snap.data().message,
            sender_id: snap.data().from,
            reciever_id: context.params.recieverId,
            notification_id: context.params.messageId,
            user_name: snap.data().fromName,
            user_status: snap.data().user_status,
            user_image: snap.data().user_image
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
