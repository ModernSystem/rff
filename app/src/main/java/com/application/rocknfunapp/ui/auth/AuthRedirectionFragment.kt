package com.application.rocknfunapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.auth
import com.application.rocknfunapp.MainActivity.Companion.user
import com.application.rocknfunapp.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class AuthRedirectionFragment : Fragment() {


    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var establishmentButton: Button
    private lateinit var particularButton: Button
    private val RC_SIGN_IN=25556
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build())


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
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                auth.currentUser!!.sendEmailVerification()
                user = auth.currentUser
                findNavController().navigate(R.id.nav_home)
            } else {
                Toast.makeText(requireContext(),R.string.auth_error,Toast.LENGTH_SHORT).show()
            }
        }
    }


}