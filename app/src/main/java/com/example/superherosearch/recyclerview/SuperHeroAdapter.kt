package com.example.superherosearch.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.superherosearch.R
import com.example.superherosearch.databinding.ViewSuperHeroBinding

class SuperHeroAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<SuperHeroItem> = emptyList()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SuperHeroItem.Image -> R.layout.view_super_hero
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is SuperHeroImageViewHolder -> holder.bind(item = item as SuperHeroItem.Image)
            else -> throw IllegalArgumentException(
                "${this.javaClass.simpleName}: Unsupported Super Hero View Holder!"
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = ViewSuperHeroBinding.inflate(inflater, parent, false)

        return when (viewType) {
            R.layout.view_super_hero -> SuperHeroImageViewHolder(
                viewBinding = viewBinding
            )
            else -> throw IllegalArgumentException(
                "${this.javaClass.simpleName}: Unsupported Super Hero View Type!"
            )
        }
    }
}