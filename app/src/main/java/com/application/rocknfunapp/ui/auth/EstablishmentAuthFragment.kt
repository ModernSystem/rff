package com.application.rocknfunapp.ui.auth

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.dataBase
import com.application.rocknfunapp.MainActivity.Companion.placesClient
import com.application.rocknfunapp.MainActivity.Companion.user
import com.application.rocknfunapp.R
import com.application.rocknfunapp.models.Establishment
import com.application.rocknfunapp.models.GlideApp
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


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
    private var imageFound=false
    private val AUTOCOMPLETE_REQUEST_CODE=254
    private val PICK_REQUEST_CODE=586

    var fields: List<Place.Field> =
        listOf(Place.Field.ID,Place.Field.ADDRESS,Place.Field.PHOTO_METADATAS,Place.Field.NAME,Place.Field.PHONE_NUMBER,Place.Field.OPENING_HOURS)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_establishment_auth, container, false)
        with (view){
            profileAddress=findViewById(R.id.establishment_settings_address)
            createButton=findViewById(R.id.establishment_settings_save_button)
            emailField=findViewById(R.id.profile_email)
            passwordField=findViewById(R.id.profile_password)
            name=findViewById(R.id.establishment_settings_name)
            phone=findViewById(R.id.establishment_settings_phone)
            addPictureButton=findViewById(R.id.establishment_settings_image_button)
            description=findViewById(R.id.establishment_settings_description)
            profilPicture=findViewById(R.id.establishment_settings_picture)

        }
        GlideApp.with(this).load(MainActivity.storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/rocknfunapp.appspot.com/o/image%2FdefaultConcert%2Fbuildings-1245953_1920%20(1).jpg?alt=media&token=2fc087e8-c086-460a-9cc7-4fe93dad741b")).into(profilPicture)
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
        name.setOnClickListener {
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
                        auth.currentUser!!.sendEmailVerification()
                        user = auth.currentUser
                        val establishment = Establishment(
                            name.text.toString(),
                            profileAddress.text.toString(),
                            phone.text.toString(),
                            description.text.toString(),
                            addPictureToFirebase(),
                            auth.currentUser!!.uid
                        )
                        dataBase.collection("EstablishmentUser").add(establishment)
                        auth.signOut()
                        Toast.makeText(requireContext(),R.string.verification_mail,Toast.LENGTH_LONG).show()
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
                name.setText(place.name)
                phone.setText(place.phoneNumber)
                addPictureButton.visibility=View.VISIBLE

                val field: List<Place.Field> = listOf(Place.Field.PHOTO_METADATAS)


                val placeRequest = FetchPlaceRequest.newInstance(place.id.toString(), field)

                placesClient!!.fetchPlace(placeRequest).addOnSuccessListener { response ->
                    val placeD: Place = response.place

                    // Get the photo metadata.
                    val photoMetadata: PhotoMetadata = placeD.photoMetadatas!![0]

                    // Get the attribution text.
                    val attributions: String = photoMetadata.attributions

                    // Create a FetchPhotoRequest.
                    val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .build()
                    placesClient!!.fetchPhoto(photoRequest)
                        .addOnSuccessListener { fetchPhotoResponse ->
                            val bitmap: Bitmap = fetchPhotoResponse.bitmap
                            profilPicture.setImageBitmap(bitmap)
                            imageFound=true

                        }
                }


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
        } else if (imageFound){
            val variable = System.currentTimeMillis()
            val storageRef = MainActivity.storage.reference
            val imageRef = storageRef.child("image/profilePicture/(${variable})")
            profilPicture.isDrawingCacheEnabled = true
            profilPicture.buildDrawingCache()
            val bitmap = (profilPicture.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = imageRef.putBytes(data)
            return imageRef.path
        } else "/Default concert images/default_4.jpg"

    }
}


