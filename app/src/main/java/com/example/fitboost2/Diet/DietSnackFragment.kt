package com.example.fitboost2.Diet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.fitboost2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DietSnackFragment : Fragment() {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var mealPlanList: List<MealPlanLunch>
    private lateinit var goLunchFromSnack: TextView
    private lateinit var goDinnerFromSnack: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diet_snack, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mealPlanNameTextView = view.findViewById<TextView>(R.id.mealPlanNameTextView)
        val mealPlanDescriptionTextView = view.findViewById<TextView>(R.id.mealPlanDescriptionTextView)
        mealPlanList = createMealPlans()

        goLunchFromSnack = view.findViewById(R.id.goLunchFromSnack)
        goDinnerFromSnack = view.findViewById(R.id.goDinnerFromSnack)

        goLunchFromSnack.setOnClickListener {
            val fragment = DietLunchFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        goDinnerFromSnack.setOnClickListener {
            val fragment = DietDinnerFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }



        val dbRefBmr = FirebaseDatabase.getInstance().getReference("Users/$userId/bmr")

        dbRefBmr.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bmr = snapshot.getValue(Double::class.java) ?: 0.0
                showMealPlanForBmr(bmr, mealPlanNameTextView, mealPlanDescriptionTextView)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DietFragment", "Błąd odczytu BMR z bazy danych", error.toException())
            }
        })
    }

    private fun createMealPlans(): List<MealPlanLunch> {
        return listOf(
            MealPlanLunch(
                "Breakfast: Here will be the recipe for breakfast",
                1300.0,
                1550.0
            ),
            MealPlanLunch(
                "Breakfast: Here will be the recipe for breakfast",
                1551.0,
                1850.0
            ),
            MealPlanLunch(
                "Breakfast: Here will be the recipe for breakfast",
                1851.0,
                2150.0
            ),
            MealPlanLunch(
                "Breakfast: Here will be the recipe for breakfast",
                2151.0,
                2350.0
            ),
            MealPlanLunch(
                "Owoce sezonowe z jogurtem naturalnym\n\n" +
                        "Składniki:\n" +
                        "\n" +
                        "● 150g jogurtu naturalnego\n" +
                        "● Porcja owoców sezonowych (np. jabłko, banan, mandarynki, gruszka)\n" +
                        "Sposób przygotowania:\n" +
                        "\n" +
                        "1. Owoce umyj i pokrój na mniejsze kawałki.\n" +
                        "2. Na talerzu ułóż jogurt i na wierzchu ułóż owoce.\n" +
                        "3. Podawaj od razu lub odstaw na kilka minut do lodówki, aby przekąska była bardziej orzeźwiająca.",
                2351.0,
                2550.0
            ),
            MealPlanLunch(
                "Breakfast: Here will be the recipe for breakfast",
                2551.0,
                2750.0
            ),
            MealPlanLunch(
                "Breakfast: Here will be the recipe for breakfast",
                2751.0,
                2950.0
            ),
            MealPlanLunch(
                "Breakfast: Here will be the recipe for breakfast",
                2951.0,
                3150.0
            ),
            MealPlanLunch(
                "Breakfast: Here will be the recipe for breakfast",
                3151.0,
                3350.0
            )
            // Dodaj kolejne plany żywieniowe według potrzeb
        )
    }

    private fun showMealPlanForBmr(
        bmr: Double,
        mealPlanNameTextView: TextView,
        mealPlanDescriptionTextView: TextView
    ) {
        val mealPlan = mealPlanList.firstOrNull { bmr in it.minCalories..it.maxCalories }
        if (mealPlan == null) {
            Log.d("DietFragment", "Nie znaleziono planu żywieniowego dla BMR = $bmr")
            mealPlanNameTextView.text =
                "Nie znaleziono odpowiedniego planu żywieniowego dla tej liczby BMR."
            mealPlanDescriptionTextView.text = ""
        } else {
            Log.d("DietFragment", "Plan żywieniowy dla BMR = $bmr")
            mealPlanDescriptionTextView.text = mealPlan.description
        }
    }
}
class MealPlanSnack(
    val description: String,
    val minCalories: Double,
    val maxCalories: Double
)