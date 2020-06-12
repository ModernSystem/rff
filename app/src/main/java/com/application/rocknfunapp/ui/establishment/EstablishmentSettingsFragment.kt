package com.application.rocknfunapp.ui.establishment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.dataBase
import com.application.rocknfunapp.MainActivity.Companion.establishment
import com.application.rocknfunapp.MainActivity.Companion.storage
import com.application.rocknfunapp.MainActivity.Companion.userDocId
import com.application.rocknfunapp.R
import com.application.rocknfunapp.models.GlideApp
import com.application.rocknfunapp.ui.newConcert.NewConcertFragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.File


class EstablishmentSettingsFragment : Fragment() {

    private lateinit var profilPicture: ImageView
    private lateinit var changeImageButton:Button
    private lateinit var name: TextView
    private lateinit var address: TextView
    private lateinit var contact: TextView
    private lateinit var description: TextView
    private lateinit var saveButton:Button
    private val PICK_REQUEST_CODE=42
    private val AUTOCOMPLETE_REQUEST_CODE=152
    private val previousPicture= establishment!!.profilePicture
    private var uri: Uri?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_establishment_settings, container, false)
        with(view){
            profilPicture=findViewById(R.id.establishment_settings_picture)
            changeImageButton=findViewById(R.id.establishment_settings_image_button)
            name=findViewById(R.id.establishment_settings_name)
            address=findViewById(R.id.establishment_settings_address)
            contact=findViewById(R.id.establishment_settings_phone)
            description=findViewById(R.id.establishment_settings_description)
            saveButton=findViewById(R.id.establishment_settings_save_button)
        }
        configureUI()
        configureAddressButton()
        configureImageButton()
        configureSaveButton()
        return view
    }

    private fun configureUI(){
        GlideApp.with(this).load(storage.getReference(establishment!!.profilePicture!!)).into(profilPicture)
        name.text= establishment!!.name
        address.text= establishment!!.location
        contact.text= establishment!!.contact
        description.text= establishment!!.description
    }

    private fun configureImageButton(){
        changeImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            val mimeTypes= arrayListOf<String>("image/jpeg","image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes)
            startActivityForResult(intent,PICK_REQUEST_CODE)
        }
    }

    private fun configureAddressButton(){
        address.setOnClickListener {
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, listOf(Place.Field.ID, Place.Field.ADDRESS)
            )
                .build(requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    private fun configureSaveButton(){
        saveButton.setOnClickListener {
            if (checkInfoCompleted()){
                dataBase.collection("user").document(userDocId)
                    .update(
                        mapOf(
                            "contact" to contact.text.toString(),
                            "description" to description.text.toString(),
                            "location" to address.text.toString(),
                            "profilePicture" to addPictureToFirebase(),
                            "name" to name.text.toString()
                        )
                    ).addOnCompleteListener {
                        MainActivity.getEstablishment()
                        findNavController().navigate(R.id.nav_home)
                        Toast.makeText(requireContext(),R.string.modification_complete,Toast.LENGTH_SHORT).show()
                    }.addOnCanceledListener {
                        Toast.makeText(requireContext(),R.string.modification_fail,Toast.LENGTH_SHORT).show()                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ICIII","oui")
        if (requestCode==PICK_REQUEST_CODE){
            Log.d("ICIII","oui")
            uri=data?.data
            GlideApp.with(this).load(uri).into(profilPicture)
            if (uri==null){
                val id=R.drawable.defaut_1
                GlideApp.with(this).load(storage.getReference(establishment!!.profilePicture!!)).into(profilPicture)
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data!=null) {
                val place = Autocomplete.getPlaceFromIntent(data)
                address.text =place.address
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Toast.makeText(requireContext(),R.string.problem,Toast.LENGTH_LONG).show()

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(),R.string.problem,Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun addPictureToFirebase():String?{
        return if (uri!=null) {
            var response=true
            //Delete previous picture
            val deleteRef= storage.reference.child(previousPicture!!)
            deleteRef.delete().addOnCompleteListener {response=true}.addOnCanceledListener {response=false}
            if (response){
                val file = File(uri.toString())
                val variable = System.currentTimeMillis()
                val storageRef = MainActivity.storage.reference
                val imageRef = storageRef.child("image/profilePicture/${file.name}(${variable})")
                var uploadTask = imageRef.putFile(uri!!)
                imageRef.path
            }
            else  establishment!!.profilePicture
        } else   establishment!!.profilePicture

    }

    private fun checkInfoCompleted():Boolean{
        return when {
            name.text.isEmpty() -> {
                name.error=getString(R.string.choose_name)
                name.requestFocus()
                false
            }
            address.text.isEmpty() -> {
                address.error=getString(R.string.choose_address)
                address.requestFocus()
                false
            }
            contact.text.isEmpty() -> {
                contact.error=getString(R.string.choose_phone)
                contact.requestFocus()
                false
            }
            description.text.isEmpty() -> {
                description.error=getString(R.string.choose_description)
                description.requestFocus()
                false
            }
            else -> true
        }
    }


}