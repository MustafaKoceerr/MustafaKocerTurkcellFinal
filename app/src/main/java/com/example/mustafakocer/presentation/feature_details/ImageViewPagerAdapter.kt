package com.example.mustafakocer.presentation.feature_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.databinding.ItemImageSliderBinding

/**
 * A [RecyclerView.Adapter] for populating a ViewPager2 with a list of image URLs.
 *
 * @param images The list of image URLs to be displayed.
 */
class ImageViewPagerAdapter(
    private val images: List<String>,
) : RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder>() {

    /**
     * A [RecyclerView.ViewHolder] that holds the view for a single image in the slider.
     */
    inner class ImageViewHolder(private val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds an image URL to the ImageView using Glide.
         */
        fun bind(imageUrl: String) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.sliderImageView)
        }
    }

    /**
     * Called when RecyclerView needs a new [ImageViewHolder] of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageSliderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return images.size
    }
}