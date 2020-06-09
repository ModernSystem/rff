package com.application.rocknfunapp.ui.auth

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.user

import com.application.rocknfunapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var loginButton: Button
    private lateinit var noAccountText:TextView
    private lateinit var email:EditText
    private lateinit var password:EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_login, container, false)
        if (MainActivity.user !=null){
            findNavController().navigate(R.id.nav_home)
        }

        with(view){
            loginButton=findViewById(R.id.login_button)
            noAccountText=findViewById(R.id.login_text)
            email=findViewById(R.id.login_username)
            password=findViewById(R.id.login_password)
        }
        configureClickableText()
        configureLoginButton()

        return view
    }

    private fun configureClickableText(){
        noAccountText.setOnClickListener {
            findNavController().navigate(R.id.nav_auth_redirection)
        }
    }
    private fun configureLoginButton(){
        password.addTextChangedListener {
            if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                loginButton.isEnabled = true
                loginButton.setOnClickListener {
                    Firebase.auth.signInWithEmailAndPassword(
                        email.text.toString(),
                        password.text.toString()
                    ).addOnCompleteListener {
                        user=Firebase.auth.currentUser
                        findNavController().navigate(R.id.nav_home)

                    }

                }
            }
        }


    }


}