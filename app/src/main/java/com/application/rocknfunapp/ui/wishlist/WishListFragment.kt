package com.application.rocknfunapp.ui.wishlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.dataBase
import com.application.rocknfunapp.MainActivity.Companion.goingToConcert
import com.application.rocknfunapp.R
import com.application.rocknfunapp.controller.HomeConcertAdapter
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.ui.home.BeThereButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject


class WishListFragment : Fragment(),BeThereButton {

    private lateinit var recyclerView:RecyclerView
    private var concertList= mutableListOf<Concert>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_wish_list, container, false)
        recyclerView=view.findViewById(R.id.wishlist_recyclerView)

        getData()
        configureRecyclerView()
        return view
    }

    private fun getData(){
        if (goingToConcert.isNotEmpty()){
            for (id in goingToConcert){
                dataBase.collection("Concert").document(id).get()
                    .addOnSuccessListener{
                        val concert=it.toObject<Concert>()
                        concertList.add(concert!!)
                        recyclerView.adapter!!.notifyDataSetChanged()

                        }
                    }
            }
        }

    private fun configureRecyclerView(){
        val adapter=HomeConcertAdapter(concertList,requireContext(),this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
    }

    override fun onButtonPlayClicked(concert: Concert, position: Int) {
        goingToConcert.remove(concert.id)
        concertList.remove(concert)
        /**
         * Update numberOfParticipant
         */
        concert.nbOfParticipant = (concert.nbOfParticipant!!.minus(1))

        dataBase.collection("Concert").document(concert.id!!)
            .update("nbOfParticipant", FieldValue.increment(-1))
        recyclerView.adapter!!.notifyItemChanged(position)

        /**
         * update user wishlist in firestore
         */

        dataBase.collection("userWishlist").whereEqualTo("id", MainActivity.user!!.uid).get()
            .addOnCompleteListener {
                    for (document in it.result!!.documents){
                        dataBase.collection("userWishlist").document(document.id)
                            .update("concertId", goingToConcert)
                    }

            }
    }
}



