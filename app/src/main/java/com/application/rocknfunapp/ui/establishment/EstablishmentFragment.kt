package com.application.rocknfunapp.ui.establishment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.Controller.ComingConcertAdapter
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.Models.Concert
import com.application.rocknfunapp.R

class EstablishmentFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var comingEvent:List<Concert>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_establishement, container, false)
        recyclerView=view.findViewById(R.id.comingEventRecyclerView)
        configureRecyclerView()
        return view
    }

    private fun configureRecyclerView(){

        recyclerView.adapter=ComingConcertAdapter(MainActivity.concertList)
        recyclerView.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
    }
}