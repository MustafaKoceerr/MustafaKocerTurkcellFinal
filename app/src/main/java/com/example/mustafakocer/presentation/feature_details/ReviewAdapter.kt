package com.example.mustafakocer.presentation.feature_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.databinding.RecyclerRowReviewBinding
import com.example.mustafakocer.domain.model.Review

/**
 * A [RecyclerView.Adapter] for displaying a list of product [Review]s.
 *
 * @param reviews The list of reviews to be displayed.
 */
class ReviewAdapter(
    private val reviews: List<Review>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    /**
     * A [RecyclerView.ViewHolder] that holds the view for a single review item.
     * It uses ViewBinding to safely and efficiently access the views.
     */
    inner class ReviewViewHolder(private val binding: RecyclerRowReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a [Review] object to the views held by this ViewHolder.
         */
        fun bind(review: Review) {
            binding.apply {
                txtName.text = review.reviewerName
                txtDate.text = review.formattedDate
                txtComment.text = review.comment
                ratingBar.rating = review.rating.toFloat()
                txtAvatar.text = review.reviewerName.firstOrNull()?.toString()?.uppercase() ?: ""
            }
        }
    }

    /**
     * Called when RecyclerView needs a new [ReviewViewHolder] to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = RecyclerRowReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return reviews.size
    }
}