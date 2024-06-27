package com.example.mustafakocer.service


import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessageService : FirebaseMessagingService() {


    val tag = "message"
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(tag, "${message.from}")
        Log.d(tag, "${message.messageId}")
        Log.d(tag, "${message.data}")
        message.notification?.let {
            Log.d(tag, "${it.body}")
        }
    }
}