package com.application.rocknfunapp.ui.newConcert

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.dataBase
import com.application.rocknfunapp.MainActivity.Companion.establishment
import com.application.rocknfunapp.MainActivity.Companion.formatDate
import com.application.rocknfunapp.MainActivity.Companion.user
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.R
import com.application.rocknfunapp.models.GlideApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class NewConcertFragment : Fragment() {

    private val PICK_REQUEST_CODE=42
    private lateinit var newConcertName:EditText
    private lateinit var newConcertDate:EditText
    private lateinit var newConcertPicture:ImageView
    private lateinit var newConcertArtist:AutoCompleteTextView
    private lateinit var newConcertDescription:EditText
    private lateinit var okButton:Button
    private lateinit var addImageButton:Button
    private lateinit var gsReference: StorageReference
    private var calendar=Calendar.getInstance()
    private var calendarVerification=calendar.timeInMillis
    private var uri:Uri?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_new_concert, container, false)

        //Linking widgets

        with(view){
            newConcertName=findViewById(R.id.establishment_settings_name)
            newConcertDate=findViewById(R.id.establishment_settings_phone)
            newConcertPicture=findViewById(R.id.establishment_settings_picture)
            newConcertArtist=findViewById(R.id.establishment_settings_address)
            newConcertDescription=findViewById(R.id.establishment_settings_description)
            okButton=findViewById(R.id.new_concert_ok_button)
            addImageButton=findViewById(R.id.establishment_settings_image_button)

            val storage=FirebaseStorage.getInstance()
            gsReference = storage.getReferenceFromUrl(getRandomImage())
            GlideApp.with(this)
                .load(gsReference)
                .into(newConcertPicture)

        }
        configureDatePicker()
        configureAddImage()
        configureAndAddConcert()
        newConcertPicture.isClickable=false


        return view
    }

    override fun onStop() {
        super.onStop()
        view?.clearFocus()
    }

    private fun getRandomImage():String{
        val list= mutableListOf<String>(
            "https://firebasestorage.googleapis.com/v0/b/rocknfunapp.appspot.com/o/image%2FdefaultConcert%2Fadmission-2974645_1920%20(1).jpg?alt=media&token=ed12dbe5-da4e-4d08-873e-5c2ac3aa4ff5",
            "https://firebasestorage.googleapis.com/v0/b/rocknfunapp.appspot.com/o/image%2FdefaultConcert%2Fconcert-1129966_1920.jpg?alt=media&token=144d47b7-8b80-47fc-b9ab-c1feca2600cc",
            "https://firebasestorage.googleapis.com/v0/b/rocknfunapp.appspot.com/o/image%2FdefaultConcert%2Fconcert-336695_1920.jpg?alt=media&token=01b1b0d7-70bd-4d8c-bf06-8e56fd95da1d",
            "https://firebasestorage.googleapis.com/v0/b/rocknfunapp.appspot.com/o/image%2FdefaultConcert%2Fcrowd-1056764_1920%20(1).jpg?alt=media&token=73d8a4eb-6dd8-4ad4-87de-5bc27cb97ab1"
        )
        list.shuffle()
        return list[0]
    }

   private fun configureDatePicker(){
       val date=DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
           calendar.set(year,month,dayOfMonth)
           showTimePicker()

       }

       newConcertDate.setOnClickListener {
            val datePickerDialog=DatePickerDialog(requireContext(),
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE))

           datePickerDialog.datePicker.minDate=System.currentTimeMillis()-1000
           datePickerDialog.show()
       }
   }

    private fun showTimePicker(){
        val timePickerListener=TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
            calendar.set(Calendar.MINUTE,minute)

            newConcertDate.setText(formatDate(calendar.time))
        }


        val timePicker=TimePickerDialog(context,timePickerListener,0,0,true)
        timePicker.show()

    }



    private fun configureAddImage(){
        addImageButton.setOnClickListener {
            selectImage()
        }
        newConcertPicture.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        val mimeTypes= arrayListOf<String>("image/jpeg","image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes)
        startActivityForResult(intent,PICK_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==PICK_REQUEST_CODE){
            uri=data?.data
            newConcertPicture.setImageURI(uri)
            addImageButton.visibility=View.GONE
            newConcertPicture.isClickable=true
            if (uri==null){
                val id=R.drawable.defaut_1
                newConcertPicture.setImageResource(R.drawable.defaut_1)
                addImageButton.visibility=View.VISIBLE
            }
        }
    }

    private fun configureAndAddConcert(){
        okButton.setOnClickListener {
            if (checkInformation()) {
                val concert = Concert(
                    newConcertName.text.toString(),
                    user!!.uid,
                    calendar.time,
                    newConcertDescription.text.toString(),
                    null,
                    newConcertArtist.text.toString(),
                    0,
                    null,
                    establishment!!.name
                )


                concert.image= addConcertToFirebase(concert)
                dataBase.collection("Concert").add(concert).addOnSuccessListener {
                    it.update("id",it.id)
                }
                findNavController().navigateUp()

            }
        }
    }

    private fun addConcertToFirebase(concert: Concert):String?{
        if (uri!=null) {
            val file = File(uri.toString())
            val variable = System.currentTimeMillis()
            val storageRef = MainActivity.storage.reference
            val imageRef = storageRef.child("image/${file.name}(${variable})")
            var uploadTask = imageRef.putFile(uri!!)
            return imageRef.path
        }
        else return gsReference.path

    }


    private fun checkInformation():Boolean{

        return when {
            (newConcertName.text.toString().length<3 && newConcertName.text.isNotEmpty()) -> {
                Toast.makeText(context, getText(R.string.title_short),Toast.LENGTH_SHORT).show()
                false
            }
            (newConcertName.text.isEmpty()) -> {
                Toast.makeText(context, getText(R.string.title_missing),Toast.LENGTH_SHORT).show()
                false
            }

            (calendar.timeInMillis== calendarVerification)-> {
                Toast.makeText(context, getText(R.string.date_missing), Toast.LENGTH_SHORT).show()
                false
            }
            (newConcertArtist.text.toString().length<2&& newConcertArtist.text.isNotEmpty())->{
                Toast.makeText(context, getText(R.string.artist_name_short),Toast.LENGTH_SHORT).show()
                false
            }
            (newConcertArtist.text.toString().isEmpty())->{
                Toast.makeText(context, getText(R.string.artist_name_missing),Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }



}

