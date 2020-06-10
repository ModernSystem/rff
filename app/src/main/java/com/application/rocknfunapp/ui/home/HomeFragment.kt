package com.application.rocknfunapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.controller.HomeConcertAdapter
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.R
import com.google.firebase.firestore.ktx.toObject

class HomeFragment : Fragment(),BeThereButton,CreateConcertList {

    private lateinit var concertRecyclerView: RecyclerView
    private lateinit var waitingImage:ImageView
    private var waitingSize=0
    private var hashSet= hashSetOf<String>()
    private var concertList= mutableListOf<Concert>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        with (view){
            waitingImage=findViewById(R.id.waiting_icon)
            concertRecyclerView=findViewById(R.id.home_recyclerview)
        }
        getConcertList(this)
        configureWaitingImage()
        configureRecyclerView()




        return view
    }


    /**
     * Configure UI
     */

    private fun configureRecyclerView(){

        val adapter=HomeConcertAdapter(concertList,requireContext(),this)
        concertRecyclerView.adapter=adapter
        concertRecyclerView.layoutManager=LinearLayoutManager(requireContext())
    }

    private fun configureWaitingImage(){
        if (concertList.size>0){
            waitingImage.visibility=View.GONE
        }
    }



    /**
     * Get data from database and fetch inside a list using callback
     */
    private fun getConcertList(listener:CreateConcertList){
            MainActivity.dataBase.collection("Concert")
                .get()
                .addOnSuccessListener { result ->
                    waitingSize= result.size()
                    for (document in result) {
                        val concert = document.toObject<Concert>()
                        concert.id=document.reference.id
                        listener.onConcertConstruct(concert, document.id)
                    }
                }
    }

    override fun onConcertConstruct(concert: Concert, id: String) {
        if (!hashSet.contains(concert.id)) {
            concertList.add(concert)
            hashSet.add(concert.id!!)

        }

        concertRecyclerView.adapter!!.notifyDataSetChanged()
        if (concertList.size==waitingSize) {
            waitingImage.visibility = View.GONE
            concertRecyclerView.visibility=View.VISIBLE
        }
    }


    /**
     * Configure the "I'll be there Button"
     */

    override fun onButtonPlayClicked(concert: Concert) {

    }


}



interface BeThereButton{
    fun onButtonPlayClicked(concert: Concert)
}

interface CreateConcertList{
    fun onConcertConstruct(concert: Concert,id:String)
}