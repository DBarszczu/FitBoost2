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

class DietDinnerFragment : Fragment() {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var mealPlanList: List<MealPlanDinner>
    private lateinit var goSnackFromDinner: TextView
    private lateinit var goBreakfastFromSnack: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diet_dinner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mealPlanNameTextView = view.findViewById<TextView>(R.id.mealPlanNameTextView)
        val mealPlanDescriptionTextView = view.findViewById<TextView>(R.id.mealPlanDescriptionTextView)
        mealPlanList = createMealPlans()

        goSnackFromDinner = view.findViewById(R.id.goSnackFromDinner)
        goBreakfastFromSnack = view.findViewById(R.id.goBreakfastFromSnack)

        goSnackFromDinner.setOnClickListener {
            val fragment = DietSnackFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        goBreakfastFromSnack.setOnClickListener {
            val fragment = DietBreakfastFragment()
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

    private fun createMealPlans(): List<MealPlanDinner> {
        return listOf(
            MealPlanDinner(
                "Breakfast: Here will be the recipe for breakfast",
                1300.0,
                1550.0
            ),
            MealPlanDinner(
                "Breakfast: Here will be the recipe for breakfast",
                1551.0,
                1850.0
            ),
            MealPlanDinner(
                "Breakfast: Here will be the recipe for breakfast",
                1851.0,
                2150.0
            ),
            MealPlanDinner(
                "Breakfast: Here will be the recipe for breakfast",
                2151.0,
                2350.0
            ),
            MealPlanDinner(
                "Duszona wołowina z kaszą gryczaną i warzywami\n\n" +
                        "Składniki:\n" +
                        "\n" +
                        "● 150g mięsa wołowego\n" +
                        "● 1/2 cebuli\n" +
                        "● 1 ząbek czosnku\n" +
                        "● 1 marchewka\n" +
                        "● 1 pietruszka\n" +
                        "● 1/2 selera\n" +
                        "● 100g kaszy gryczanej\n" +
                        "● 1 łyżka masła\n" +
                        "● 1 łyżka oleju rzepakowego\n" +
                        "● 1 łyżeczka suszonego tymianku\n" +
                        "● 1 liść laurowy\n" +
                        "● Sól i pieprz do smaku\n\n" +
                        "Sposób przygotowania:\n" +
                        "\n" +
                        "1. Kaszę gryczaną umyj i ugotuj zgodnie z instrukcją na opakowaniu.\n" +
                        "2. Mięso wołowe pokrój na mniejsze kawałki. Na patelni rozgrzej olej rzepakowy i obsmaż mięso na złoty kolor. Przełóż do garnka.\n" +
                        "3. Cebulę i czosnek posiekaj. Marchewkę, pietruszkę i seler obierz i pokrój w kostkę.\n" +
                        "4. Na patelni, na której smażyło się mięso, rozgrzej masło i podsmaż cebulę i czosnek, a następnie dodaj pokrojone warzywa i smaż przez kilka minut.\n" +
                        "5. Przełóż warzywa do garnka z mięsem.\n" +
                        "6. Dodaj do garnka suszony tymianek, liść laurowy, sól i pieprz. Zalej wszystko wodą do poziomu mięsa i warzyw i gotuj na wolnym ogniu przez około 1,5 godziny, aż mięso będzie miękkie i soczyste.\n" +
                        "7. Podawaj duszoną wołowinę z kaszą gryczaną na osobnych talerzach.",
                2351.0,
                2550.0
            ),
            MealPlanDinner(
                "Breakfast: Here will be the recipe for breakfast",
                2551.0,
                2750.0
            ),
            MealPlanDinner(
                "Breakfast: Here will be the recipe for breakfast",
                2751.0,
                2950.0
            ),
            MealPlanDinner(
                "Breakfast: Here will be the recipe for breakfast",
                2951.0,
                3150.0
            ),
            MealPlanDinner(
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
class MealPlanDinner(
    val description: String,
    val minCalories: Double,
    val maxCalories: Double
)