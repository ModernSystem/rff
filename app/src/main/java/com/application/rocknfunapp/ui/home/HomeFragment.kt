package com.application.rocknfunapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.Controller.HomeConcertAdapter
import com.application.rocknfunapp.Models.Concert
import com.application.rocknfunapp.R

class HomeFragment : Fragment() {

    private lateinit var concertRecyclerView: RecyclerView
    private lateinit var concertList: List<Concert>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        concertRecyclerView=view.findViewById(R.id.home_recyclerview)
        configureRecyclerView()
        return view
    }

    private fun configureRecyclerView(){
        concertList= listOf()
        val adapter=HomeConcertAdapter(concertList)
        concertRecyclerView.adapter=adapter
        concertRecyclerView.layoutManager=LinearLayoutManager(requireContext())
    }
}