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

    private var currentDate = Calendar.getInstance()

    private lateinit var previousButton: TextView
    private lateinit var nextButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)

        btnFetchData = rootView.findViewById(R.id.btnFetchData)

        dayOfWeekTextView = rootView.findViewById(R.id.DayOfTheWeek)

        previousButton = rootView.findViewById(R.id.previous_button)
        nextButton = rootView.findViewById(R.id.next_button)

        // Dodanie obsługi kliknięć dla przycisków
        previousButton.setOnClickListener { onPreviousDayClicked() }
        nextButton.setOnClickListener { onNextDayClicked() }

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





        updateDateText()
        getProductsData()
        bmr()



        return rootView
    }

    private fun onPreviousDayClicked() {
        currentDate.add(Calendar.DAY_OF_MONTH, -1)
        updateDateText()
        updateMealDataForCurrentDate()
    }

    // Metoda obsługująca przycisk "następny dzień"
    private fun onNextDayClicked() {
        currentDate.add(Calendar.DAY_OF_MONTH, 1)
        updateDateText()
        updateMealDataForCurrentDate()
    }


    private fun updateDateText() {
        val date = currentDate.time
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val dateString = dateFormat.format(date)

        val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)

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
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/Meals/$year/$month/$dayOfMonth")

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
                val tvSumCalories = view?.findViewById<TextView>(R.id.calories)
                if (tvSumCalories != null) {
                    tvSumCalories.text = String.format("%.1f", sumCalories)
                }

                // odejmowanie kalorii od BMR i wyświetlanie wyniku
                val dbRefBmr = FirebaseDatabase.getInstance().getReference("Users/$userId/bmr")
                dbRefBmr.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(bmrSnapshot: DataSnapshot) {
                        val userBmr = bmrSnapshot.value.toString().toDouble()
                        val bmrAfterSubtraction = userBmr - sumCalories
                        val tvBmrAfterSubtraction =
                            view?.findViewById<TextView>(R.id.bmrAfterSubtraction)
                        if (tvBmrAfterSubtraction != null) {
                            tvBmrAfterSubtraction.text = String.format("%.1f", bmrAfterSubtraction)
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // obsługa błędów
                    }
                })

                val tvSumCarbs = view?.findViewById<TextView>(R.id.carbs)
                if (tvSumCarbs != null) {
                    tvSumCarbs.text = String.format("%.1f", sumCarbs)
                }

                val tvSumProteins = view?.findViewById<TextView>(R.id.proteins)
                if (tvSumProteins != null) {
                    tvSumProteins.text = String.format("%.1f", sumProteins)
                }

                val tvSumFat = view?.findViewById<TextView>(R.id.fat)
                if (tvSumFat != null) {
                    tvSumFat.text = String.format("%.1f", sumFat)
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // obsługa błędów
            }
        })
    }

    private fun updateMealDataForCurrentDate() {
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/Meals/$year/$month/$dayOfMonth")

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
                val tvSumCalories = view?.findViewById<TextView>(R.id.calories)
                if (tvSumCalories != null) {
                    tvSumCalories.text = String.format("%.1f", sumCalories)
                }

                // odejmowanie kalorii od BMR i wyświetlanie wyniku
                val dbRefBmr = FirebaseDatabase.getInstance().getReference("Users/$userId/bmr")
                dbRefBmr.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(bmrSnapshot: DataSnapshot) {
                        val userBmr = bmrSnapshot.value.toString().toDouble()
                        val bmrAfterSubtraction = userBmr - sumCalories
                        val tvBmrAfterSubtraction =
                            view?.findViewById<TextView>(R.id.bmrAfterSubtraction)
                        if (tvBmrAfterSubtraction != null) {
                            tvBmrAfterSubtraction.text = String.format("%.1f", bmrAfterSubtraction)
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // obsługa błędów
                    }
                })

                val tvSumCarbs = view?.findViewById<TextView>(R.id.carbs)
                if (tvSumCarbs != null) {
                    tvSumCarbs.text = String.format("%.1f", sumCarbs)
                }

                val tvSumProteins = view?.findViewById<TextView>(R.id.proteins)
                if (tvSumProteins != null) {
                    tvSumProteins.text = String.format("%.1f", sumProteins)
                }

                val tvSumFat = view?.findViewById<TextView>(R.id.fat)
                if (tvSumFat != null) {
                    tvSumFat.text = String.format("%.1f", sumFat)
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // obsługa błędów
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