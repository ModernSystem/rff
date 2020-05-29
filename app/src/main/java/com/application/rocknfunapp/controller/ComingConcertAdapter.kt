package com.application.rocknfunapp.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.formatdate2
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.R
import com.application.rocknfunapp.models.GlideApp

class ComingConcertAdapter(private var concertToCome:MutableList<Concert>,private val context: Context):RecyclerView.Adapter<ComingConcertAdapter.ConcertToComeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertToComeViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.concert_to_come_item,parent,false)
        return ConcertToComeViewHolder(view,context)
    }

    override fun getItemCount(): Int {
        return concertToCome.size
    }

    override fun onBindViewHolder(holder: ConcertToComeViewHolder, position: Int) {
        holder.updateUIInitInfo(concertToCome[position])
    }


    class ConcertToComeViewHolder(itemView:View,val context: Context):RecyclerView.ViewHolder(itemView){
        private var concertToComePicture:ImageView
        private var concertToComeInformation:TextView
        private var concertToComeDate:TextView

        init{
            with(itemView){
                concertToComePicture=findViewById(R.id.concert_to_come_item_picture)
                concertToComeInformation=findViewById(R.id.concert_to_come_item_artist)
                concertToComeDate=findViewById(R.id.concert_to_come_item_date)
            }
        }


        fun updateUIInitInfo( concert:Concert){
            concertToComeInformation.text=concert.artist
            concertToComeDate.text= formatdate2(concert.date!!)
            val storageReference= MainActivity.storage.getReference(concert.image!!)
            GlideApp.with(context.applicationContext).load(storageReference).into(concertToComePicture)

        }
    }
}