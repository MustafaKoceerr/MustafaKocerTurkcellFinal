package com.example.mustafakocer.ui.home

import android.view.View
import com.example.mustafakocer.data.model.Product

interface LikeButtonClickListener {

    fun onRecyclerViewItemClick(view: View, product: Product)
    // if you dont pass a view you cannot dettect that which view was clicked
    // because all the cases, this function will be called
    // and I wanna pass movie because I want to send the selected product


}