package com.application.rocknfunapp.Controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.Models.Concert
import com.application.rocknfunapp.R

class ComingConcertAdapter(var concertToCome:List<Concert>):RecyclerView.Adapter<ComingConcertAdapter.ConcertToComeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertToComeViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.concert_to_come_item,parent,false)
        return ConcertToComeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return concertToCome.size
    }

    override fun onBindViewHolder(holder: ConcertToComeViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class ConcertToComeViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private lateinit var concertToComePicture:ImageView
        private lateinit var concertToComeInformation:TextView

        init{
            with(itemView){
                concertToComePicture=findViewById(R.id.concert_to_come_item_picture)
                concertToComeInformation=findViewById(R.id.concert_to_come_item_information)
            }
        }

        fun updateUIwithInfos( concert:Concert){
            concertToComeInformation.text="${concert.name} @ ${concert.date}"
        }
    }
}