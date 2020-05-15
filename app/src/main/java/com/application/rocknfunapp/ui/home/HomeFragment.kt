package com.application.rocknfunapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.controller.HomeConcertAdapter
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.R
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var concertRecyclerView: RecyclerView
    private lateinit var concertList: MutableList<Concert>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        concertRecyclerView=view.findViewById(R.id.home_recyclerview)



        concertList= mutableListOf<Concert>(Concert("Concert 1",
            MainActivity.establishment,
            Calendar.getInstance().time,null,context?.getDrawable(R.drawable.defaut_1)!!,null),
            Concert("Concert 1",
                MainActivity.establishment,
                Calendar.getInstance().time,null,context?.getDrawable(R.drawable.defaut_2)!!,null),
            Concert("Concert 1",
                MainActivity.establishment,
                Calendar.getInstance().time,null,context?.getDrawable(R.drawable.defaut_3)!!,null),
            Concert("Concert 1",
                MainActivity.establishment,
                Calendar.getInstance().time,null,context?.getDrawable(R.drawable.defaut_1)!!,null),
            Concert("Concert 1",
                MainActivity.establishment,
                Calendar.getInstance().time,null,context?.getDrawable(R.drawable.defaut_4)!!,null))

        configureRecyclerView()
        return view
    }

    private fun configureRecyclerView(){

        val adapter=HomeConcertAdapter(concertList)
        concertRecyclerView.adapter=adapter
        concertRecyclerView.layoutManager=LinearLayoutManager(requireContext())
    }
}