package com.example.plots.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plots.R
import com.example.plots.data.PropertyCardView
import com.example.plots.ui.activities.listingExpanded.ListingExpanded
import com.example.plots.ui.register.RegisterActivity

class MyListingsRecyclerViewAdapter(private val list: List<PropertyCardView>) :
    RecyclerView.Adapter<MyListingsRecyclerViewAdapter.ListingViewHolder>() {
    private val TAG = "MyListingsRecyclerViewAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.property_cardview,
            parent, false)

        return ListingViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val currentItem = list[position]

        holder.propertyImage.setImageBitmap(currentItem.imageResource)
        holder.listingType.text = currentItem.listingType
        holder.price.text = when(currentItem.listingType) {
            "For Rent" -> "$${currentItem.price}/month"
            "For Sale" -> "$${currentItem.price}"
            else -> "error"
        }
        holder.surface.text = "${currentItem.surface}sqm"
        holder.bedrooms.text = "${currentItem.bedrooms} bed"
        holder.bathrooms.text = "${currentItem.bathrooms} ba"
        holder.kitchens.text = "${currentItem.kitchens} kit"
    }

    override fun getItemCount(): Int = list.size

    inner class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val propertyImage: ImageView = itemView.findViewById(R.id.propertyImage)
        val listingType: TextView = itemView.findViewById(R.id.listingType)
        val price: TextView = itemView.findViewById(R.id.price)
        val surface: TextView = itemView.findViewById(R.id.surface)
        val bedrooms: TextView = itemView.findViewById(R.id.bedrooms)
        val bathrooms: TextView = itemView.findViewById(R.id.bathrooms)
        val kitchens: TextView = itemView.findViewById(R.id.kitchens)

        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ListingExpanded::class.java)
                intent.putExtra("propertyId", list[bindingAdapterPosition].propertyId)
                itemView.context.startActivity(intent)
            }
        }

    }
}