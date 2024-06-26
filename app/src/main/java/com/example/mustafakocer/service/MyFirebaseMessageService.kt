package com.example.mustafakocer.service


import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("newToken", "tokenim $token")
        // program ilk defa başlatıldığında firebase'e iletilmek için oluşturulan token
    }

    val tag = "message"
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(tag, "${message.from}")
        Log.d(tag, "${message.messageId}")
        Log.d(tag, "${message.data}")
        // data demek ekstra datalar demek, örneğin mesajın görülüp görülmediğini buradan görebilirsin
        message.notification?.let {
            Log.d(tag, "${it.body}")
        }
    }
}