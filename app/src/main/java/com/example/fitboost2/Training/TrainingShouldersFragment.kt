package com.example.fitboost2.Training

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.fitboost2.R

class TrainingShouldersFragment : Fragment() {

    private lateinit var youtube_shoulders_1: Button
    private lateinit var youtube_shoulders_2: Button
    private lateinit var youtube_shoulders_3: Button
    private lateinit var youtube_shoulders_4: Button
    private lateinit var youtube_shoulders_5: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_traininig_shoulders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        youtube_shoulders_1 = view.findViewById(R.id.youtube_shoulders_1)
        youtube_shoulders_1.setOnClickListener {
            openUpdateDialog()
        }
        youtube_shoulders_2 = view.findViewById(R.id.youtube_shoulders_2)
        youtube_shoulders_2.setOnClickListener {
            openUpdateDialog()
        }
        youtube_shoulders_3 = view.findViewById(R.id.youtube_shoulders_3)
        youtube_shoulders_3.setOnClickListener {
            openUpdateDialog()
        }
        youtube_shoulders_4 = view.findViewById(R.id.youtube_shoulders_4)
        youtube_shoulders_4.setOnClickListener {
            openUpdateDialog()
        }
        youtube_shoulders_5 = view.findViewById(R.id.youtube_shoulders_5)
        youtube_shoulders_5.setOnClickListener {
            openUpdateDialog()
        }
    }

    private fun openUpdateDialog() {
        val mDialog = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val mDialogView1 = inflater.inflate(R.layout.youtube_shoulders_1, null)
        val mDialogView2 = inflater.inflate(R.layout.youtube_shoulders_2, null)
        val mDialogView3 = inflater.inflate(R.layout.youtube_shoulders_3, null)
        val mDialogView4 = inflater.inflate(R.layout.youtube_shoulders_4, null)
        val mDialogView5 = inflater.inflate(R.layout.youtube_shoulders_5, null)

        mDialog.setView(mDialogView1)
        mDialog.setView(mDialogView2)
        mDialog.setView(mDialogView3)
        mDialog.setView(mDialogView4)
        mDialog.setView(mDialogView5)

        val alertDialog = mDialog.create()
        alertDialog.show()
    }
}