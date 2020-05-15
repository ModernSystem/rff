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

class HomeConcertAdapter(private var concertList:List<Concert>): RecyclerView.Adapter<HomeConcertAdapter.ConcertViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.home_concert_item,parent,false)
        return ConcertViewHolder(view)
        }

    override fun getItemCount(): Int {
        return concertList.size
    }

    override fun onBindViewHolder(holder: ConcertViewHolder, position: Int) {
        if (concertList.isNotEmpty())
        holder.updateUInitInfo(concertList[position])
    }


    class ConcertViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val concertImage: ImageView
        private val concertName: TextView
        private val concertDate: TextView
        private val concertPlace: TextView

        init {
            with(itemView){
                concertImage=findViewById(R.id.concert_item_image)
                concertName=findViewById(R.id.concert_item_name)
                concertDate=findViewById(R.id.concert_item_date)
                concertPlace=findViewById(R.id.concert_item_place)
            }
        }

        fun updateUInitInfo(concert:Concert){
            concertName.text=concert.name
            concertDate.text=formatDate(concert.date)
            concertPlace.text=concert.location.name
            concertImage.setImageDrawable(concert.image)
        }
    }
}