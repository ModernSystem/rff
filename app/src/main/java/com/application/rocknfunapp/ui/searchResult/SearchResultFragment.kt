package com.application.rocknfunapp.ui.slideshow

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
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.R
import com.application.rocknfunapp.controller.HomeConcertAdapter
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.ui.home.BeThereButton
import com.application.rocknfunapp.ui.home.CreateConcertList

class SearchResultFragment : Fragment(),BeThereButton,CreateConcertList {

    private lateinit var recyclerView:RecyclerView
    private lateinit var concertList: MutableList<Concert>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_result, container, false)
        with (view){
            recyclerView=findViewById(R.id.search_result_recyclerview)
        }
        configureRecyclerView()

        return view
    }

    private fun configureRecyclerView(){
        recyclerView.adapter=HomeConcertAdapter(MainActivity.researchConcertList,requireContext(),this)
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
    }

    override fun onButtonPlayClicked(concert: Concert,position:Int) {
        TODO("Not yet implemented")
    }

    override fun onConcertConstruct(concert: Concert, id: String) {
        recyclerView.adapter!!.notifyDataSetChanged()
    }
}