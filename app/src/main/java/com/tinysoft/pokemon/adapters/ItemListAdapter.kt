package com.tinysoft.pokemon.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.tinysoft.pokemon.EXTRA_POKEMON
import com.tinysoft.pokemon.GlideApp
import com.tinysoft.pokemon.R
import com.tinysoft.pokemon.adapters.base.BaseEntryViewHolder
import com.tinysoft.pokemon.db.Pokemon

class ItemListAdapter(
    val activity: FragmentActivity,
    var dataSet: List<Pokemon>,
    @param:LayoutRes @field:LayoutRes val itemLayoutRes: Int,
) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<Pokemon>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = dataSet[position]
        holder.title?.text = "${pokemon.id}. ${pokemon.name}"
        holder.text?.text = "${pokemon.type1} ${pokemon.type2}".trim()
        GlideApp.with(activity).asDrawable()
            .load(pokemon.image)
            .into(holder.image!!)
        holder.image?.let {
            ViewCompat.setTransitionName(it, pokemon.id.toString())
        }
    }

    inner class ViewHolder(itemView: View) : BaseEntryViewHolder(itemView) {

        override fun onClick(v: View?) {
            val item = dataSet[layoutPosition]

            activity.findNavController(R.id.fragment_container).navigate(
                R.id.detailsFragment,
                bundleOf(EXTRA_POKEMON to item),
                null,
                FragmentNavigatorExtras(image!! to "${item.id}")
            )
        }
    }
}