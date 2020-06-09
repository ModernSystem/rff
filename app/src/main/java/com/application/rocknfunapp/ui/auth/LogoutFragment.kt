package com.application.rocknfunapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity.Companion.user
import com.application.rocknfunapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogoutFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Firebase.auth.signOut()
        user=null
        findNavController().navigate(R.id.nav_home)
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }


}