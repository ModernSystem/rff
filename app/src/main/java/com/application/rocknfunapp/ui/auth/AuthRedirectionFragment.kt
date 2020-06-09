package com.application.rocknfunapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity.Companion.user
import com.application.rocknfunapp.R


class AuthRedirectionFragment : Fragment() {


    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var establishmentButton: Button
    private lateinit var particularButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auth_redirection, container, false)
        if (user!=null){
            findNavController().navigate(R.id.nav_home)
        }
        with(view) {
            buttonLayout = findViewById(R.id.auth_fragment_button_layout)
            establishmentButton =findViewById(R.id.auth_establishment_button)
            particularButton=findViewById(R.id.auth_particular_button)
        }
        configureFirstScreen()
        return view
    }

    private fun configureFirstScreen(){
        establishmentButton.setOnClickListener {
            findNavController().navigate(R.id.nav_establishmentAuth)

        }

        particularButton.setOnClickListener {
            findNavController().navigate(R.id.nav_home)
            Toast.makeText(requireContext(),"Welcome to the community", Toast.LENGTH_LONG).show()
        }

    }


}