package com.example.fitboost2


import java.util.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitboost2.Menu.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat

class MenuFragment : Fragment() {

    private lateinit var btnFetchData: Button
    private lateinit var mealRecyclerView: RecyclerView
    private lateinit var tvLoadingDataMeals: ProgressBar
    private lateinit var mealList: ArrayList<ProductModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var mAdapter: MealAdapter
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var bmrEditText: TextView
    private lateinit var tvBmr: TextView
    private var userBmr: Double = 0.0
    private lateinit var dayOfWeekTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)

        btnFetchData = rootView.findViewById(R.id.btnFetchData)

        mealRecyclerView = rootView.findViewById(R.id.rvMeals)
        mealRecyclerView.layoutManager = LinearLayoutManager(context)
        mealRecyclerView.setHasFixedSize(true)
        tvLoadingDataMeals = rootView.findViewById(R.id.tvLoadingDataMeals)
        tvBmr = rootView.findViewById(R.id.bmrAfterSubtraction)
        bmrEditText = rootView.findViewById(R.id.bmrAfterSubtraction)

        mealList = arrayListOf<ProductModel>()
        mAdapter = MealAdapter(mealList)

        mealRecyclerView.adapter = mAdapter

        mAdapter.setOnItemClickListener(object : MealAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putString("ProductId", mealList[position].ProductId)
                bundle.putString("ProductName", mealList[position].ProductName)
                bundle.putString("ProductCalories", mealList[position].ProductCalories.toString())
                bundle.putString("ProductFat", mealList[position].ProductFat.toString())
                bundle.putString("ProductProtein", mealList[position].ProductProtein.toString())
                bundle.putString("ProductCarbs", mealList[position].ProductCarbs.toString())

                val mealDetailsFragment = MealDetailsFragment()
                mealDetailsFragment.arguments = bundle

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, mealDetailsFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })

        btnFetchData.setOnClickListener {
            val fragment = FetchingFragmentProduct()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val calendar = Calendar.getInstance()
        val date1 = calendar.time
        val date2 = calendar.time
        val date3 = calendar.time
        val dateFormatDay = SimpleDateFormat("dd", Locale.getDefault())
        val dateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val dateFormatMonth = SimpleDateFormat("MM", Locale.getDefault())
        val dateStringDay = dateFormatDay.format(date1)
        val dateStringYear = dateFormatYear.format(date2)
        val dateStringMonth = dateFormatMonth.format(date3)
        getProductsData()
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/Meals/$dateStringYear/$dateStringMonth/$dateStringDay")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sumCalories = 0.0
                var sumFat = 0.0
                var sumProteins = 0.0
                var sumCarbs = 0.0
                for (ds in dataSnapshot.children) {
                    val meal = ds.getValue(ProductModel::class.java)
                    sumCalories += meal?.ProductCalories?.toDouble() ?: 0.0
                    sumFat += meal?.ProductFat?.toDouble() ?: 0.0
                    sumProteins += meal?.ProductProtein?.toDouble() ?: 0.0
                    sumCarbs += meal?.ProductCarbs?.toDouble() ?: 0.0

                }
                // wyświetlenie sumy kalorii na ekranie
                val tvSumCalories = rootView.findViewById<TextView>(R.id.calories)
                tvSumCalories.text = String.format("%.1f", sumCalories)

                // odejmowanie kalorii od BMR i wyświetlanie wyniku
                val dbRefBmr = FirebaseDatabase.getInstance().getReference("Users/$userId/bmr")
                dbRefBmr.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(bmrSnapshot: DataSnapshot) {
                        val userBmr = bmrSnapshot.value.toString().toDouble()
                        val bmrAfterSubtraction = userBmr - sumCalories
                        val tvBmrAfterSubtraction =
                            rootView.findViewById<TextView>(R.id.bmrAfterSubtraction)
                        tvBmrAfterSubtraction.text = String.format("%.1f", bmrAfterSubtraction)

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // obsługa błędów
                    }
                })

                val tvSumCarbs = rootView.findViewById<TextView>(R.id.carbs)
                tvSumCarbs.text = String.format("%.1f", sumCarbs)

                val tvSumProteins = rootView.findViewById<TextView>(R.id.proteins)
                tvSumProteins.text = String.format("%.1f", sumProteins)

                val tvSumFat = rootView.findViewById<TextView>(R.id.fat)
                tvSumFat.text = String.format("%.1f", sumFat)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // obsługa błędów
            }
        })
        bmr()



        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        val date = calendar.time
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val dateString = dateFormat.format(date)

        dayOfWeekTextView = view.findViewById(R.id.DayOfTheWeek)


        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val dayOfWeekText = when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday \n$dateString"
            Calendar.MONDAY -> "Monday \n$dateString"
            Calendar.TUESDAY -> "Tuesday \n$dateString"
            Calendar.WEDNESDAY -> "Wednesday \n$dateString"
            Calendar.THURSDAY -> "Thursday \n$dateString"
            Calendar.FRIDAY -> "Friday \n$dateString"
            Calendar.SATURDAY -> "Saturday \n$dateString"
            else -> ""
        }

        dayOfWeekTextView.text = dayOfWeekText
    }

    private fun getProductsData() {
        mealRecyclerView.visibility = View.GONE
        tvLoadingDataMeals.visibility = View.VISIBLE
        val calendar = Calendar.getInstance()
        val date1 = calendar.time
        val date2 = calendar.time
        val date3 = calendar.time
        val dateFormatDay = SimpleDateFormat("dd", Locale.getDefault())
        val dateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val dateFormatMonth = SimpleDateFormat("MM", Locale.getDefault())
        val dateStringDay = dateFormatDay.format(date1)
        val dateStringYear = dateFormatYear.format(date2)
        val dateStringMonth = dateFormatMonth.format(date3)
        dbRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/Meals/$dateStringYear/$dateStringMonth/$dateStringDay")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mealList.clear()
                if (snapshot.exists()) {
                    for (mealSnap in snapshot.children) {
                        val mealData = mealSnap.getValue(ProductModel::class.java)
                        mealData?.let { mealList.add(it) }
                    }
                }
                mAdapter.notifyDataSetChanged()
                mealRecyclerView.visibility = View.VISIBLE
                tvLoadingDataMeals.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun bmr() {
        val dbRefBmr = FirebaseDatabase.getInstance().getReference("Users/$userId/bmr")
        dbRefBmr.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userBmr = dataSnapshot.value.toString().toDouble()
                tvBmr.text = userBmr.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // obsługa błędów
            }
        })
    }
}