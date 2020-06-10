package com.application.rocknfunapp.ui.auth

import android.R.attr
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.dataBase
import com.application.rocknfunapp.MainActivity.Companion.user
import com.application.rocknfunapp.R
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.models.Establishment
import com.firebase.ui.auth.FirebaseUiUserCollisionException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.FirebaseError.ERROR_EMAIL_ALREADY_IN_USE
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.lang.Exception


class EstablishmentAuthFragment : Fragment() {

    private lateinit var profileAddress:EditText
    private lateinit var phone:EditText
    private lateinit var name:EditText
    private lateinit var addPictureButton: Button
    private lateinit var description: EditText
    private lateinit var createButton:Button
    private lateinit var emailField:EditText
    private lateinit var passwordField:EditText
    private lateinit var profilPicture:ImageView
    private lateinit var auth:FirebaseAuth
    private var uri: Uri?=null
    private val AUTOCOMPLETE_REQUEST_CODE=254
    private val PICK_REQUEST_CODE=586

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
            profilPicture=findViewById(R.id.profile_establishment_picture)

        }
        auth= Firebase.auth
        configureAddress()
        configureCreateButton()
        configureChoosePictureButton()
        return view
    }

    private fun configureChoosePictureButton(){
        addPictureButton.setOnClickListener {
            selectImage()
        }
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
            if (checkInfoCompleted()) {

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->



                    if (task.isSuccessful) {
                        auth.signInWithEmailAndPassword(email, password)
                        user = auth.currentUser
                        val establishment = Establishment(
                            name.text.toString(),
                            profileAddress.text.toString(),
                            phone.text.toString(),
                            description.text.toString(),
                            addPictureToFirebase(),
                            auth.currentUser!!.uid
                        )
                        dataBase.collection("user").add(establishment)
                        findNavController().navigate(R.id.nav_home)
                    }
                }.addOnFailureListener {
                    it as FirebaseAuthException
                    when (it.errorCode){
                        "ERROR_EMAIL_ALREADY_IN_USE"->{
                            emailField.error=getString(R.string.error_mail_taken)
                            emailField.requestFocus()
                        }
                        "ERROR_INVALID_EMAIL"->{
                            emailField.error=getString(R.string.error_mail_form)
                            emailField.requestFocus()
                        }
                        "ERROR_WEAK_PASSWORD"->{
                            passwordField.error=getString(R.string.error_password_form)
                            passwordField.requestFocus()
                        }
                    }
                }

            }
        }
    }

    private fun selectImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        val mimeTypes= arrayListOf<String>("image/jpeg","image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes)
        startActivityForResult(intent,PICK_REQUEST_CODE)
    }

    private fun checkInfoCompleted():Boolean{
        return when {
            name.text.isEmpty() -> {
                name.error=getString(R.string.choose_name)
                name.requestFocus()
                false
            }
            profileAddress.text.isEmpty() -> {
                profileAddress.error=getString(R.string.choose_address)
                profileAddress.requestFocus()
                false
            }
            phone.text.isEmpty() -> {
                phone.error=getString(R.string.choose_phone)
                phone.requestFocus()
                false
            }
            description.text.isEmpty() -> {
                description.error=getString(R.string.choose_description)
                description.requestFocus()
                false
            }
            emailField.text.isEmpty() -> {
                emailField.error=getString(R.string.choose_email)
                emailField.requestFocus()
                false
            }
            passwordField.text.isEmpty() -> {
                passwordField.error=getString(R.string.choose_passord)
                passwordField.requestFocus()
                false
            }
            else -> true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data!=null) {
                val place = Autocomplete.getPlaceFromIntent(data)
                profileAddress.setText(place.address )
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                val status  = Autocomplete.getStatusFromIntent(data!!)

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(requireContext(),R.string.problem,Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode==PICK_REQUEST_CODE){
            uri=data?.data
            profilPicture.setImageURI(uri)
            addPictureButton.visibility=View.GONE
            profilPicture.isClickable=true
            if (uri==null){
                profilPicture.setImageResource(R.drawable.defaut_1)
                addPictureButton.visibility=View.VISIBLE
            }
        }
    }

    private fun addPictureToFirebase():String?{
        return if (uri!=null) {
            val file = File(uri.toString())
            val variable = System.currentTimeMillis()
            val storageRef = MainActivity.storage.reference
            val imageRef = storageRef.child("image/profilePicture/${file.name}(${variable})")
            var uploadTask = imageRef.putFile(uri!!)
            imageRef.path
        } else "/Default concert images/default_4.jpg"

    }
}