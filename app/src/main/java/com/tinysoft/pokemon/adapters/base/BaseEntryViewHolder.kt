package com.tinysoft.pokemon.adapters.base

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.tinysoft.pokemon.R

open class BaseEntryViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
    var imageContainer: MaterialCardView? = itemView.findViewById(R.id.imageContainer)
    var image: ImageView? = itemView.findViewById(R.id.image)
    var title: TextView? = itemView.findViewById(R.id.title)
    var text: TextView? = itemView.findViewById(R.id.text)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {}
}