package com.example.superherosearch.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superherosearch.R
import com.example.superherosearch.databinding.ViewSuperHeroBinding

class SuperHeroImageViewHolder(
  private val viewBinding: ViewSuperHeroBinding
) : RecyclerView.ViewHolder(viewBinding.root) {
  companion object {
    private const val TIMEOUT_IN_SECONDS = 30000
  }

  fun bind(item: SuperHeroItem.Image) {
    viewBinding.superHeroName.text = item.name

    Glide.with(viewBinding.root)
      .load(item.imageUrl)
      .timeout(TIMEOUT_IN_SECONDS)
      .placeholder(R.drawable.ic_wallpaper_24dp)
      .error(R.drawable.ic_error_outline_24dp)
      .into(viewBinding.superHeroImage)
  }
}