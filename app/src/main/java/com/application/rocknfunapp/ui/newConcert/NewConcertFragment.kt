package com.application.rocknfunapp.ui.newConcert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.application.rocknfunapp.R

class NewConcertFragment : Fragment() {


    private lateinit var newConcertName:EditText
    private lateinit var newConcertDate:EditText
    private lateinit var newConcertPicture:ImageView
    private lateinit var newConcertArtist:AutoCompleteTextView
    private lateinit var newConcertDescription:EditText
    private lateinit var okButton:Button
    private lateinit var cancelButton:Button

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
            cancelButton=findViewById(R.id.new_concert_cancel_button)
        }



        return view
    }




}