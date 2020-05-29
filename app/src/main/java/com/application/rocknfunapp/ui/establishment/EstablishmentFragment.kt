package com.application.rocknfunapp.ui.establishment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.controller.ComingConcertAdapter
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.R
import com.application.rocknfunapp.ui.home.CreateConcertList
import com.google.firebase.firestore.ktx.toObject

class EstablishmentFragment : Fragment(),CreateConcertList {

    private lateinit var recyclerView: RecyclerView
    private lateinit var profilPicture:ImageView
    private lateinit var name:TextView
    private lateinit var address:TextView
    private lateinit var contact:TextView
    private lateinit var description:TextView
    private lateinit var comingEvent:MutableList<Concert>
    private var concertList= mutableListOf<Concert>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_establishement, container, false)


        with(view) {
            profilPicture = findViewById(R.id.profile_picture_establishment)
            name = findViewById(R.id.establishment_layout_name)
            address=findViewById(R.id.establishment_layout_address)
            contact=findViewById(R.id.establishment_layout_contact)
            description=findViewById(R.id.establishment_layout_description)
            recyclerView=findViewById(R.id.establishment_layout_comingEventRecyclerView)

        }
        comingEvent= mutableListOf()
        getConcertList(this)
        configureRecyclerView()
        configureUi()

        return view
    }

    private fun configureRecyclerView(){

        recyclerView.adapter=ComingConcertAdapter(concertList,requireContext())
        recyclerView.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
    }

    private fun getConcertList(listener: CreateConcertList){
        concertList= mutableListOf()
        MainActivity.dataBase.collection("Concert")
            .get()
            .addOnSuccessListener {result->
                for (document in result){
                    val concert =document.toObject<Concert>()
                    listener.onConcertConstruct(concert,document.id)

                }
            }
    }

    override fun onConcertConstruct(concert: Concert, id: String) {
        concertList.add(concert)
        recyclerView.adapter!!.notifyDataSetChanged()
        Log.d("TAAAAAAAAAAAAAAAAAAAAAAAAAAAG", concertList.toString())

    }

    private fun configureUi() {
        val viewModel = ViewModelProviders.of(this).get(EstablishmentViewModel::class.java)
        viewModel.name.observe(viewLifecycleOwner, Observer {
            name.text = it
        })
        viewModel.address.observe(viewLifecycleOwner, Observer {
            address.text = it
        })
        viewModel.contact.observe(viewLifecycleOwner, Observer {
            contact.text = it
        })
        viewModel.description.observe(viewLifecycleOwner, Observer {
            description.text=it
        })
        profilPicture.setImageResource(R.drawable.defaut_2)




    }
}