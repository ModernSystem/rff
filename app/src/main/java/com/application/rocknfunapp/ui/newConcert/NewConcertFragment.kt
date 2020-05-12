package com.application.rocknfunapp.ui.newConcert

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.formatDate
import com.application.rocknfunapp.Models.Concert
import com.application.rocknfunapp.Models.Establishment
import com.application.rocknfunapp.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class NewConcertFragment : Fragment() {

    private val PICK_REQUEST_CODE=42
    private lateinit var newConcertName:EditText
    private lateinit var newConcertDate:EditText
    private lateinit var newConcertPicture:ImageView
    private lateinit var newConcertArtist:AutoCompleteTextView
    private lateinit var newConcertDescription:EditText
    private lateinit var okButton:Button
    private lateinit var addImageButton:MaterialButton
    private var calendar=Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_new_concert, container, false)

        //Linking widgets

        with(view){
            newConcertName=findViewById(R.id.new_concert_name)
            newConcertDate=findViewById(R.id.new_concert_date)
            newConcertPicture=findViewById(R.id.new_concert_picture)
            newConcertArtist=findViewById(R.id.new_concert_artist)
            newConcertDescription=findViewById(R.id.new_concert_description)
            okButton=findViewById(R.id.new_concert_ok_button)
            addImageButton=findViewById(R.id.add_image_button)

        }
        configureDatePicker()
        configureAddImage()
        configureAndAddConcert()
        newConcertPicture.isClickable=false

        return view
    }

   private fun configureDatePicker(){
       val date=DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
           calendar.set(year,month,dayOfMonth)
           showTimePicker()

       }

       newConcertDate.setOnClickListener {
            val datePickerDialog=DatePickerDialog(context!!,
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
            val uri=data?.data
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
            val establishment=Establishment("bleu","Brest","Description")
            val concert= Concert(
                newConcertName.text.toString(),
                establishment,
                calendar.time,
                newConcertDescription.text.toString(),
                newConcertPicture.drawable
            )
            MainActivity.concertList.add(concert)
            findNavController().navigateUp()
        }
    }
}