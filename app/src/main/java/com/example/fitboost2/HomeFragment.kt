package com.example.fitboost2

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.fitboost2.Diet.DietBreakfastFragment
import com.example.fitboost2.Diet.DietBrunchFragment
import com.example.fitboost2.Diet.DietDinnerFragment
import com.example.fitboost2.Diet.DietLunchFragment
import com.example.fitboost2.Diet.DietSnackFragment
import com.example.fitboost2.Menu.FetchingFragmentProduct
import com.example.fitboost2.Menu.InsertionFragment
import com.example.fitboost2.Menu.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var bmrEditText: TextView
    private lateinit var tvBmr: TextView
    private var userBmr: Double = 0.0
    private lateinit var progressBar: ProgressBar
    private lateinit var dayOfWeekHome: TextView
    private lateinit var Home_DetailsMenu: TextView
    private lateinit var HomeAddProduct: TextView
    private lateinit var HomeListProduct: TextView
    private lateinit var HomeBreakfast: TextView
    private lateinit var HomeBrunch: TextView
    private lateinit var HomeLunch: TextView
    private lateinit var HomeSnack: TextView
    private lateinit var HomeDinner: TextView
    private lateinit var progressBarLoading: ProgressBar
    private var currentDate = Calendar.getInstance()


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        tvBmr = rootView.findViewById(R.id.bmrAfterSubtraction)
        bmrEditText = rootView.findViewById(R.id.bmrAfterSubtraction)
        progressBar = rootView.findViewById(R.id.progressBar)
        HomeAddProduct = rootView.findViewById(R.id.HomeAddProduct)
        Home_DetailsMenu = rootView.findViewById(R.id.Home_DetailsMenu)
        HomeListProduct = rootView.findViewById(R.id.HomeListProduct)
        HomeBreakfast = rootView.findViewById(R.id.HomeBreakfast)
        HomeBrunch = rootView.findViewById(R.id.HomeBrunch)
        HomeLunch = rootView.findViewById(R.id.HomeLunch)
        HomeSnack = rootView.findViewById(R.id.HomeSnack)
        HomeDinner = rootView.findViewById(R.id.HomeDinner)
        progressBarLoading = rootView.findViewById(R.id.progressBarLoading)

        Home_DetailsMenu.setOnClickListener {
            val fragment = MenuFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        HomeAddProduct.setOnClickListener {
            val fragment = InsertionFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        HomeListProduct.setOnClickListener {
            val fragment = FetchingFragmentProduct()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        HomeBreakfast.setOnClickListener {
            val fragment = DietBreakfastFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        HomeBrunch.setOnClickListener {
            val fragment = DietBrunchFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        HomeLunch.setOnClickListener {
            val fragment = DietLunchFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        HomeSnack.setOnClickListener {
            val fragment = DietSnackFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        HomeDinner.setOnClickListener {
            val fragment = DietDinnerFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        progressBar.visibility = View.GONE
        progressBarLoading.visibility = View.VISIBLE

        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/Meals/$year/$month/$dayOfMonth")
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
                // odejmowanie kalorii od BMR i wyświetlanie wyniku
                val dbRefBmr = FirebaseDatabase.getInstance().getReference("Users/$userId/bmr")
                dbRefBmr.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(bmrSnapshot: DataSnapshot) {
                        userBmr = bmrSnapshot.value.toString().toDouble()
                        val bmrAfterSubtraction = userBmr - sumCalories
                        val tvBmrAfterSubtraction =
                            rootView.findViewById<TextView>(R.id.bmrAfterSubtraction)
                        val bmrLeftString =
                            String.format("<b>%.1f</b><br> calories left</br>", bmrAfterSubtraction)
                        tvBmrAfterSubtraction.text = Html.fromHtml(bmrLeftString)

                        progressBar.max = userBmr.toInt()
                        progressBar.progress = sumCalories.toInt()
                        progressBar.visibility = View.VISIBLE
                        progressBarLoading.visibility = View.GONE
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // obsługa błędów
                    }
                })


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

        dayOfWeekHome = view.findViewById(R.id.dayOfWeekHome)


        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val dayOfWeekText = when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> ""
        }

        dayOfWeekHome.text = dayOfWeekText
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