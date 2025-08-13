package com.example.mustafakocer.presentation.feature_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.databinding.RecyclerRowReviewBinding // ViewBinding sınıfını import et
import com.example.mustafakocer.domain.model.Review

class ReviewAdapter(
    private val reviews: List<Review>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    /**
     * ViewHolder, tek bir satırın (recycler_row_review.xml) view'larına referans tutar.
     * ViewBinding kullanarak bu referansları güvenli ve verimli bir şekilde alırız.
     * Bu, her seferinde `findViewById` çağırma maliyetinden kurtarır.
     */
    inner class ReviewViewHolder(private val binding: RecyclerRowReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bu yardımcı fonksiyon, bir `Review` nesnesini alıp ViewHolder'ın tuttuğu
         * view'lara bağlar. Bu, `onBindViewHolder`'daki kodu temiz tutar.
         */
        fun bind(review: Review) {
            binding.apply {
                txtName.text = review.reviewerName
                txtDate.text = review.formattedDate
                txtComment.text = review.comment
                ratingBar.rating = review.rating.toFloat() // RatingBar float değer bekler

                // Avatar için ismin baş harfini al, eğer isim boşsa boş bırak
                txtAvatar.text = review.reviewerName.firstOrNull()?.toString()?.uppercase() ?: ""
            }
        }
    }

    /**
     * RecyclerView yeni bir ViewHolder'a ihtiyaç duyduğunda çağrılır.
     * Bu metot, XML layout'umuzu inflate eder (bir View nesnesine dönüştürür)
     * ve onu bir ViewHolder içine sararak döndürür.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        // XML layout'u inflate etmek için LayoutInflater ve ViewBinding kullanılır.
        val binding = RecyclerRowReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    /**
     * RecyclerView bir satırı ekranda göstermek istediğinde bu metodu çağırır.
     * `position` parametresini kullanarak listeden doğru `Review` nesnesini alır
     * ve ViewHolder'ın `bind` metodu aracılığıyla veriyi view'lara yerleştirir.
     * Bu metot, scroll sırasında sürekli çağrılır.
     */
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentReview = reviews[position]
        holder.bind(currentReview)
    }

    /**
     * RecyclerView'a veri setinde toplam kaç tane eleman olduğunu söyler.
     * Bu, RecyclerView'ın ne kadar scroll edeceğini bilmesi için gereklidir.
     */
    override fun getItemCount(): Int {
        return reviews.size
    }
}