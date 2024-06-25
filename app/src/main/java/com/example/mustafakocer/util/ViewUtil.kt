package com.example.mustafakocer.util

import android.content.Context
import android.health.connect.datatypes.units.Length
import android.view.View
import android.widget.Toast
import java.time.Duration

fun Context.showToast(message:String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, "$message", duration).show()
}

fun View.visibleProgressBar(isVisible:Boolean){
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

object UserId{
    var userId : Int=0
}
object IsAdapterAttached{
    var isAdapterAttached : Int=0
}