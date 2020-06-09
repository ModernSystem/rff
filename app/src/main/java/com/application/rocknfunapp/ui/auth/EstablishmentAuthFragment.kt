package com.application.rocknfunapp.ui.auth

import android.R.attr
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity.Companion.dataBase
import com.application.rocknfunapp.MainActivity.Companion.user
import com.application.rocknfunapp.R
import com.application.rocknfunapp.models.Establishment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class EstablishmentAuthFragment : Fragment() {

    private lateinit var profileAddress:EditText
    private lateinit var phone:EditText
    private lateinit var name:EditText
    private lateinit var addPictureButton: Button
    private lateinit var description: EditText
    private lateinit var createButton:Button
    private lateinit var emailField:EditText
    private lateinit var passwordField:EditText
    private lateinit var auth:FirebaseAuth
    private val AUTOCOMPLETE_REQUEST_CODE=254

    var fields: List<Place.Field> =
        listOf(Place.Field.ID,Place.Field.ADDRESS)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_establishment_auth, container, false)
        with (view){
            profileAddress=findViewById(R.id.profile_address)
            createButton=findViewById(R.id.profile_create_button)
            emailField=findViewById(R.id.profile_email)
            passwordField=findViewById(R.id.profile_password)
            name=findViewById(R.id.profile_establishment_name)
            phone=findViewById(R.id.profile_phone)
            addPictureButton=findViewById(R.id.profile_add_image_button)
            description=findViewById(R.id.profile_description)

        }
        auth= Firebase.auth
        configureAddress()
        configureCreateButton()
        return view
    }

    private fun configureAddress(){
        profileAddress.setOnClickListener {
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .build(requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    private fun configureCreateButton(){
        createButton.setOnClickListener {
            val email=emailField.text.toString()
            val password=passwordField.text.toString()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                if (task.isSuccessful){
                    auth.signInWithEmailAndPassword(email,password)
                    user=auth.currentUser
                    val establishment=Establishment(
                        name.text.toString(),
                        profileAddress.text.toString(),
                        phone.text.toString(),
                        description.text.toString(),
                        null,
                        auth.currentUser!!.uid
                    )

                    dataBase.collection("user").add(establishment)
                    findNavController().navigate(R.id.nav_home)
                }

            }



        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data!=null) {
                val place = Autocomplete.getPlaceFromIntent(data)
                profileAddress.setText(place.address )
                Log.d("PLACE3",place.toString())
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                val status  = Autocomplete.getStatusFromIntent(data!!)

            } else if (resultCode === RESULT_CANCELED) {
                // The user canceled he operation.
            }
        }
    }
}