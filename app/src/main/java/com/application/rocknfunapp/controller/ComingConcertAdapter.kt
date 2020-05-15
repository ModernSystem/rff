package com.application.rocknfunapp.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.MainActivity.Companion.formatDate
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.R

class ComingConcertAdapter(private var concertToCome:MutableList<Concert>):RecyclerView.Adapter<ComingConcertAdapter.ConcertToComeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertToComeViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.concert_to_come_item,parent,false)
        return ConcertToComeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return concertToCome.size
    }

    override fun onBindViewHolder(holder: ConcertToComeViewHolder, position: Int) {
        holder.updateUIInitInfo(concertToCome[position])
    }


    class ConcertToComeViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private  var concertToComePicture:ImageView
        private  var concertToComeInformation:TextView

        init{
            with(itemView){
                concertToComePicture=findViewById(R.id.concert_to_come_item_picture)
                concertToComeInformation=findViewById(R.id.concert_to_come_item_information)
            }
        }


        fun updateUIInitInfo( concert:Concert){
            concertToComeInformation.text="${concert.name} @ ${formatDate( concert.date)}"
            concertToComePicture.setImageDrawable(concert.image)
        }
    }
}