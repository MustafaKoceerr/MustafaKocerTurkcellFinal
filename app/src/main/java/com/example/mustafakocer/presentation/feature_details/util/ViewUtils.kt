package com.example.mustafakocer.presentation.feature_details.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

/**
 * Enables an auto-repeat behavior on a [View] when it is long-pressed.
 * This is useful for increment/decrement buttons. The logic handles initial delay,
 * repeat intervals, and cancellation if the touch moves outside the view bounds.
 *
 * @param initialDelay The time in milliseconds to wait after the initial press before auto-repeating starts.
 * @param repeatInterval The time in milliseconds between each repeated action.
 * @param onStep The lambda function to be executed for each step (initial click and each repeat).
 */
@SuppressLint("ClickableViewAccessibility")
fun View.enableAutoRepeat(
    initialDelay: Long = 400L,
    repeatInterval: Long = 120L,
    onStep: () -> Unit,
) {
    val autoRepeatState = AutoRepeatState(this, onStep, repeatInterval)

    // Handle single clicks when not in repeating mode.
    setOnClickListener {
        if (!autoRepeatState.isRepeating) {
            onStep()
        }
    }

    setOnTouchListener { _, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> autoRepeatState.handleActionDown(initialDelay)
            MotionEvent.ACTION_MOVE -> autoRepeatState.handleActionMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE ->
                autoRepeatState.handleActionUp(event)

            else -> false
        }
    }
}

/**
 * A private helper class to encapsulate the state and logic for the auto-repeat feature.
 * This cleans up the main extension function by managing its own state variables and runnables.
 */
private class AutoRepeatState(
    private val view: View,
    private val onStep: () -> Unit,
    private val repeatInterval: Long,
) {
    var isRepeating = false
        private set

    // DÜZELTME: 'this' referansının doğru çalışması için 'object' ifadesi kullanılıyor.
    private val repeater = object : Runnable {
        override fun run() {
            if (!view.isPressed || !view.isEnabled || !view.isShown) {
                cancelRepeat()
                return
            }
            onStep()
            view.postDelayed(this, repeatInterval)
        }
    }

    private val starter = Runnable {
        isRepeating = true
        onStep() // Execute the first step immediately on long press.
        view.postDelayed(repeater, repeatInterval)
    }

    private fun cancelRepeat() {
        isRepeating = false
        view.removeCallbacks(starter)
        view.removeCallbacks(repeater)
    }

    fun handleActionDown(initialDelay: Long): Boolean {
        view.isPressed = true
        cancelRepeat()
        view.postDelayed(starter, initialDelay)
        return true
    }

    fun handleActionMove(event: MotionEvent): Boolean {
        val isInside = event.x >= 0 && event.x < view.width && event.y >= 0 && event.y < view.height
        if (!isInside) {
            cancelRepeat()
            view.isPressed = false
        }
        return true
    }

    fun handleActionUp(event: MotionEvent): Boolean {
        val wasRepeating = isRepeating
        cancelRepeat()
        view.isPressed = false

        val isInside = event.actionMasked == MotionEvent.ACTION_UP &&
                event.x >= 0 && event.x < view.width && event.y >= 0 && event.y < view.height

        if (isInside && !wasRepeating) {
            view.performClick()
        }
        return true
    }
}