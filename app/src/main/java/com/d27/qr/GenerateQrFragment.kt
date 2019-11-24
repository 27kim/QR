package com.d27.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class GenerateQrFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_generate_qr, container, false)
        return view
    }

    companion object{
        fun newInstance() : Fragment{
            return GenerateQrFragment()
        }
    }
}