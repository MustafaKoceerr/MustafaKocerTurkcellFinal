package com.example.mustafakocer.presentation.feature_details.util

import android.view.MotionEvent
import android.view.View

fun View.enableAutoRepeat(
    initialDelay: Long = 400L,   // uzun bası eşiği
    repeatInterval: Long = 120L, // sabit tekrar hızı
    onStep: () -> Unit,
) {
    var repeating = false

    val repeater = object : Runnable {
        override fun run() {
            if (!isPressed || !isEnabled || !isShown) { repeating = false; return }
            onStep()
            postDelayed(this, repeatInterval)
        }
    }

    val starter = Runnable {
        // uzun bası resmen başlıyor
        repeating = true
        onStep()                  // ilk adımı hemen ver
        postDelayed(repeater, repeatInterval)
    }

    // performClick() çağrıldığında tek vuruş yapsın
    setOnClickListener { if (!repeating) onStep() }

    setOnTouchListener { v, e ->
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // güvenli başlangıç
                v.isPressed = true
                removeCallbacks(starter); removeCallbacks(repeater)
                repeating = false
                postDelayed(starter, initialDelay)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                // parmak buton dışına çıktıysa iptal et
                val inside = e.x >= 0 && e.x < v.width && e.y >= 0 && e.y < v.height
                if (!inside) {
                    removeCallbacks(starter); removeCallbacks(repeater)
                    repeating = false
                    v.isPressed = false
                }
                true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                removeCallbacks(starter); removeCallbacks(repeater)
                val wasRepeating = repeating
                repeating = false
                v.isPressed = false

                // Tek tık: yalnızca UP + tekrar başlamamışsa ve parmak içerideyse
                val inside = e.actionMasked == MotionEvent.ACTION_UP &&
                        e.x >= 0 && e.x < v.width && e.y >= 0 && e.y < v.height
                if (inside && !wasRepeating) v.performClick()

                true
            }

            else -> false
        }
    }
}
