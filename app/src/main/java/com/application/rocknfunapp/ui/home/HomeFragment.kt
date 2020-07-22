package com.application.rocknfunapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.application.rocknfunapp.controller.HomeConcertAdapter
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.MainActivity.Companion.dataBase
import com.application.rocknfunapp.MainActivity.Companion.getWishlist
import com.application.rocknfunapp.MainActivity.Companion.goingToConcert
import com.application.rocknfunapp.MainActivity.Companion.user
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject

class HomeFragment : Fragment(),BeThereButton,CreateConcertList {

    private lateinit var concertRecyclerView: RecyclerView
    private lateinit var waitingImage:ImageView
    private lateinit var swipeLayout:SwipeRefreshLayout
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
            swipeLayout=findViewById(R.id.swipeToRefreshLayout)
        }
        getWishlist()
        getConcertList(this)
        configureWaitingImage()
        configureRecyclerView()
        configureSwipeAction()

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
            dataBase.collection("Concert")
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

    override fun onButtonPlayClicked(concert: Concert,position: Int) {
        if (user!=null) {
            if (!goingToConcert.contains(concert.id)) {
                /**
                 * Update userWishlist
                 */
                goingToConcert.add(concert.id!!)
                /**
                 * Update numberOfParticipant
                 */
                concert.nbOfParticipant = (concert.nbOfParticipant!!.plus(1))
                val ref=concert.id!!
                Log.d("concertid",concert.id!!)

                dataBase.collection("Concert").document(ref)
                    .update("nbOfParticipant",FieldValue.increment(1))

                concertRecyclerView.adapter!!.notifyItemChanged(position)

            } else {
                /**
                 * Update userWishlist
                 */
                goingToConcert.remove(concert.id)
                /**
                 * Update numberOfParticipant
                 */
                concert.nbOfParticipant = (concert.nbOfParticipant!!.minus(1))
                dataBase.collection("Concert").document(concert.id!!)
                    .update("nbOfParticipant",FieldValue.increment(-1))
                concertRecyclerView.adapter!!.notifyItemChanged(position)

            }
            updateUserWishlist(concert)
            Log.d("wishlist", goingToConcert.toString())

        }
        else Toast.makeText(requireContext(),R.string.you_must_connect,Toast.LENGTH_LONG).show()
    }

    private fun updateUserWishlist(concert: Concert){
        dataBase.collection("userWishlist").whereEqualTo("id", user!!.uid).get()
            .addOnCompleteListener {
                if (it.result!!.isEmpty){
                    val concertCollection= hashMapOf(
                        "id" to user!!.uid,
                        "concertId" to concert.id
                    )
                    dataBase.collection("userWishlist").add(concertCollection)
                }
                else {
                    for (document in it.result!!.documents){
                        dataBase.collection("userWishlist").document(document.id)
                            .update("concertId", goingToConcert)
                    }
                }
            }


    }

    private fun configureSwipeAction(){
        swipeLayout.setOnRefreshListener {
            concertList= mutableListOf()
            getConcertList(this)
            concertRecyclerView.adapter!!.notifyDataSetChanged()
            swipeLayout.isRefreshing=false
        }
    }


}



interface BeThereButton{
    fun onButtonPlayClicked(concert: Concert,position:Int)
}

interface CreateConcertList{
    fun onConcertConstruct(concert: Concert,id:String)
}