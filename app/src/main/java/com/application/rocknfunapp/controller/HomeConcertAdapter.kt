package com.application.rocknfunapp.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.formatDate
import com.application.rocknfunapp.MainActivity.Companion.storage
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.R
import com.application.rocknfunapp.models.GlideApp
import com.application.rocknfunapp.ui.home.BeThereButton

class HomeConcertAdapter(
    private var concertList:List<Concert>,
    private val context: Context,
    private val buttonListener: BeThereButton
): RecyclerView.Adapter<HomeConcertAdapter.ConcertViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.home_concert_item,parent,false)
        return ConcertViewHolder(view,context)
        }

    override fun getItemCount(): Int {
        return concertList.size
    }

    override fun onBindViewHolder(holder: ConcertViewHolder, position: Int) {
        if (concertList.isNotEmpty())
        holder.updateUInitInfo(concertList[position])
        holder.button.setOnClickListener {
            buttonListener.onButtonPlayClicked(concertList[position],position)
        }
    }


    class ConcertViewHolder(itemView: View, private val context:Context): RecyclerView.ViewHolder(itemView) {
        private var concertImage: ImageView
        private val concertName: TextView
        private val concertDate: TextView
        private val concertPlace: TextView
        private val concertArtist: TextView
        val button: Button
        private val nbOfParticipant:TextView

        init {
            with(itemView){
                concertImage=findViewById(R.id.concert_item_image)
                concertName=findViewById(R.id.concert_item_name)
                concertDate=findViewById(R.id.concert_item_date)
                concertPlace=findViewById(R.id.concert_item_place)
                concertArtist=findViewById(R.id.concert_item_artist)
                button=findViewById(R.id.concert_item_button)
                nbOfParticipant=findViewById(R.id.concert_item_nm_of_participant)
            }
        }

        fun updateUInitInfo(concert:Concert){
            concertName.text=concert.name
            concertDate.text=formatDate(concert.date!!)
            concertPlace.text="@"+ concert.establishmentName
            concertArtist.text=concert.artist!!
            val storageReference= storage.getReference(concert.image!!)
            GlideApp.with(context.applicationContext).load(storageReference).into(concertImage)
            nbOfParticipant.text=context.getString(R.string.number_of_people,concert.nbOfParticipant)

            updateButtonUi(concert)

        }

        private fun updateButtonUi(concert: Concert){
            if (MainActivity.goingToConcert.contains(concert.id)){
                button.setText(R.string.be_there)
                button.setBackgroundResource(R.drawable.rounded_button_red)
            }
            else{
                button.setBackgroundResource(R.drawable.rounded_button)
                button.setText(R.string.add_to_wishlist)


            }
        }
    }
}