package com.example.fitboost2.Menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitboost2.R

class MealAdapter(private var mealList: List<ProductModel>) :
    RecyclerView.Adapter<MealAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        listener = clickListener
    }

    fun setData(newData: List<ProductModel>) {
        mealList = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.meal_list_item, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProduct = mealList[position]
        holder.tvMealName.text = currentProduct.ProductName
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val tvMealName: TextView = itemView.findViewById(R.id.tvMealName)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
