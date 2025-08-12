package com.example.mustafakocer.presentation.feature_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.databinding.ItemImageSliderBinding

class ImageViewPagerAdapter(
    private val images: List<String>,
) : RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder>() {

    // ViewHolder, tek bir item'ın view'larını tutar.
    inner class ImageViewHolder(private val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.sliderImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        // Yeni bir ViewHolder oluşturulurken, item_image_slider.xml'i inflate et.
        val binding = ItemImageSliderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // ViewHolder'ı o anki pozisyondaki veriyle bağla.
        holder.bind(images[position])
    }

    override fun getItemCount(): Int {
        // Listenin boyutunu döndür.
        return images.size
    }
}