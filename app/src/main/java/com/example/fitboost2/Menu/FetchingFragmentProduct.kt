package com.example.fitboost2.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitboost2.R
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class FetchingFragmentProduct : Fragment() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var ProductList: ArrayList<ProductModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var searchView: SearchView
    private lateinit var mAdapter: ProductAdapter
    private lateinit var addProduct: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fetching_product, container, false)
        productRecyclerView = view.findViewById(R.id.rvProduct)

        addProduct = view.findViewById(R.id.addProduct)

        productRecyclerView.layoutManager = LinearLayoutManager(context)
        productRecyclerView.setHasFixedSize(true)
        tvLoadingData = view.findViewById(R.id.tvLoadingData)
        searchView = view.findViewById(R.id.search)
        ProductList = arrayListOf<ProductModel>()
        mAdapter = ProductAdapter(ProductList)

        productRecyclerView.adapter = mAdapter


        mAdapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putString("ProductId", ProductList[position].ProductId)
                bundle.putString("ProductName", ProductList[position].ProductName)
                bundle.putString("ProductCalories", ProductList[position].ProductCalories.toString())
                bundle.putString("ProductFat", ProductList[position].ProductFat.toString())
                bundle.putString("ProductProtein", ProductList[position].ProductProtein.toString())
                bundle.putString("ProductCarbs", ProductList[position].ProductCarbs.toString())

                val productDetailsFragment = ProductDetailsFragment()
                productDetailsFragment.arguments = bundle

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, productDetailsFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotBlank()) {
                        searchProducts(it)
                    } else {
                        mAdapter.setData(ProductList)
                    }
                } ?: mAdapter.setData(ProductList)
                return true
            }

        })
        addProduct.setOnClickListener {
            val fragment = InsertionFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        getProductsData()

        return view
    }

    private fun getProductsData() {
        productRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("FitBoost/Products")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ProductList.clear()
                if (snapshot.exists()) {
                    for (productSnap in snapshot.children) {
                        val productData = productSnap.getValue(ProductModel::class.java)
                        productData?.let { ProductList.add(it) }
                    }
                }
                mAdapter.notifyDataSetChanged()
                productRecyclerView.visibility = View.VISIBLE
                tvLoadingData.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }

        })
    }

    private fun searchProducts(query: String) {
        val searchResults = ProductList.filter { product ->
            product.ProductName!!.toLowerCase(Locale.ROOT).contains(query.toLowerCase())
        }
        mAdapter.setData(searchResults)
    }

}
