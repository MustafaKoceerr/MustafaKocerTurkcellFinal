package com.example.mustafakocer.presentation.common

import androidx.navigation.navOptions
import com.example.mustafakocer.R

fun defaultSlideOptions() = navOptions {
    anim {
        enter = R.anim.slide_in_right
        exit = R.anim.slide_out_left
        popEnter = R.anim.slide_in_left
        popExit = R.anim.slide_out_right
    }
}
