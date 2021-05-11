package com.example.imageclassification.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imageclassification.R
import com.example.imageclassification.models.MGlass
import com.example.imageclassification.navigators.DiscreteScrollViewListener
import com.example.imageclassification.util.Status.CHANGE_MODEL
import com.google.ar.sceneform.rendering.ModelRenderable
import com.yarolegovich.discretescrollview.DiscreteScrollView

class AdapterGlasses (
   private val context: Context,
   private var data: ArrayList<MGlass>,
   private val glasses: ArrayList<ModelRenderable>,
   private val listener: DiscreteScrollViewListener,
   private val view: DiscreteScrollView
): RecyclerView.Adapter<AdapterGlasses.ViewHolder>() {

    private var selectedIndex: Int? = null
    private var index: Int? = null

    class ViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        var image: AppCompatImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_glasses_discreteview,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        loadImage(context, data[holder.adapterPosition].image.toInt(), holder.image)

        holder.image.setOnClickListener {
            setSelectedIndex(holder.adapterPosition)
        }

        if(selectedIndex == holder.adapterPosition) {
            holder.image.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_oval_white_blue_stroke_2dp
            )
            if(index != holder.adapterPosition % glasses.size) {
                index = holder.adapterPosition % glasses.size
                listener.onClick(
                    CHANGE_MODEL,
                    index!!
                )
            }
        } else {
            holder.image.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_oval_white
            )
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setDataSet(data: ArrayList<MGlass>) {
        this.data = data
        notifyDataSetChanged()
        view.scrollToPosition(0)
    }

    private fun setSelectedIndex(index: Int) {
        selectedIndex = index
        notifyDataSetChanged()
    }

    private fun loadImage(context: Context?, resource: Int, view: ImageView?) {
        view?.let { imageView ->
            context?.let {
                Glide.with(it)
                    .load(resource)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(imageView)
            }
        }
    }
}