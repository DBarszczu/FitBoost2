package com.example.fitboost2.Menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitboost2.R

class ProductAdapter(private var productList: List<ProductModel>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        listener = clickListener
    }

    fun setData(newData: List<ProductModel>) {
        productList = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.product_list_item, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProduct = productList[position]
        holder.tvProductName.text = currentProduct.ProductName
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
