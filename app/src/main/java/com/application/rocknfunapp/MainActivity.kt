package com.application.rocknfunapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.models.Establishment
import com.application.rocknfunapp.ui.home.CreateConcertList
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions

import com.google.android.libraries.places.api.Places
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.app_bar_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(),CreateConcertList {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView
    private var hashSet= hashSetOf<String>()
    private val apiKey="AIzaSyBbRXNDchelYnUXcMsjCAUkk4Z8S3N-9KY"
    companion object{



        fun formatDate( date: Date):String{
            val formatDate="dd/MM/YYYY"
            val formatTime="HH:mm"
            val dateFormat= SimpleDateFormat(formatDate, Locale.FRANCE)
            val timeFormat= SimpleDateFormat(formatTime, Locale.FRANCE)
            return  "${dateFormat.format(date)} at ${timeFormat.format(date)}"
        }

        fun formatdate2(date: Date):String{
            val formatDate="dd/MM"
            val formatTime="HH:mm"
            val dateFormat= SimpleDateFormat(formatDate, Locale.FRANCE)
            val timeFormat= SimpleDateFormat(formatTime, Locale.FRANCE)
            return  "${dateFormat.format(date)} at ${timeFormat.format(date)}"
        }


        var researchConcertList= mutableListOf<Concert>()
        var goingToConcert= mutableListOf<Concert>()
        val storage=Firebase.storage
        val dataBase=Firebase.firestore
        private val auth=Firebase.auth
        var user= auth.currentUser
        var establishment:Establishment?=null
        val e=if (user!=null){
            dataBase.collection("user")
                .whereEqualTo("id",Firebase.auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        MainActivity.establishment = document.toObject<Establishment>()
                    }
                }

        }
        else null


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                search(query,this)
            }
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        /**
         * Configure floating action button
         */

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { _ ->
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_NewConcert)
        }

        /**
         *
         */

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView= findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.nav_home, R.id.nav_establishment
            ), drawerLayout = drawerLayout
        )

        Places.initialize(applicationContext, apiKey)

        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            configureMenuDrawer()
            when (destination.label) {
                getString(R.string.menu_home) -> {
                        configureFab()
                        toolbar.navigationIcon = scaledDrawable(R.drawable.logo, 96, 96)
                }
                getString(R.string.menu_create_concert)->{
                        fab.hide()
                        toolbar.navigationIcon = getDrawable(R.drawable.ic_arrow_back_black_24dp)
                }
                getString(R.string.menu_search_fragment)->{
                    fab.hide()
                    toolbar.navigationIcon = getDrawable(R.drawable.ic_arrow_back_black_24dp)
                }
                else ->{
                        fab.hide()
                        toolbar.navigationIcon = scaledDrawable(R.drawable.logo, 96, 96)
                }
            }

        }

        configureFab()


    }

    private fun search(query: String,listener: CreateConcertList) {

        hashSet= hashSetOf()
        researchConcertList= mutableListOf()
        dataBase.collection("Concert")
            .whereGreaterThanOrEqualTo("artist", query)
            .get()
            .addOnSuccessListener { results ->
                for (document in results){
                    val concert = document.toObject<Concert>()
                    concert.id=document.reference.id
                    listener.onConcertConstruct(concert, document.id)
                }
            }

    }

    override fun onConcertConstruct(concert: Concert, id: String) {
        if (!hashSet.contains(concert.id)) {
            researchConcertList.add(concert)
            hashSet.add(concert.id!!)
        }
        findNavController(R.id.nav_host_fragment).navigate(R.id.nav_search)


        }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as android.widget.SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isIconifiedByDefault = false // Do not iconify the widget; expand it by default
        }
        return true
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun Context.scaledDrawable(@DrawableRes id: Int, width: Int, height: Int): Drawable {
        val bmp = BitmapFactory.decodeResource(resources, id)
        val bmpScaled = Bitmap.createScaledBitmap(bmp, width, height, false)
        return BitmapDrawable(resources, bmpScaled)
    }

    override fun onBackPressed() {
        if (findNavController(R.id.nav_host_fragment).currentDestination!!.id== R.id.nav_home) {
            finish()

        }
        else {
            super.onBackPressed()
        }
    }

    private fun configureFab(){

        if (user!=null){
            fab.show()
        }
        else{
            fab.hide()
        }
    }

    private fun configureMenuDrawer(){
        if (user!=null) {
            navView.menu.findItem(R.id.nav_loginFragment).isVisible = false
            navView.menu.findItem(R.id.nav_establishment).isVisible = true
            navView.menu.findItem(R.id.nav_logoutFragment).isVisible = true
        }
        else {
            navView.menu.findItem(R.id.nav_establishment).isVisible = false
            navView.menu.findItem(R.id.nav_loginFragment).isVisible = true
            navView.menu.findItem(R.id.nav_logoutFragment).isVisible = false
        }
    }



}
