package com.example.fitboost2.Training

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.fitboost2.R

class Test : Fragment() {

    private lateinit var youtube_chest_1: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        youtube_chest_1 = view.findViewById(R.id.youtube_chest_1)

        youtube_chest_1.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())

            builder.setMessage("Do you want to play the YouTube video?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://www.youtube.com/watch?v=Uoz3pB-wk-U")
                    startActivity(intent)

                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()

        }
    }
}