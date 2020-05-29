package com.application.rocknfunapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.SearchView
import com.application.rocknfunapp.models.Concert
import com.application.rocknfunapp.models.Establishment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.app_bar_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    companion object{
        val establishment= Establishment("Le Nom","7 rue de la place, Brest","06.62.84.12.56","Le bar le nom vous acceuille tout les jours de 18h à " +
                "1h30 avec grand plaisir. \nA très vite !",null,12)

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
            return  "${dateFormat.format(date)}|${timeFormat.format(date)}"
        }

        var goingToConcert= mutableListOf<Concert>()
        val storage=Firebase.storage
        val dataBase=Firebase.firestore

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val appBarLayout:AppBarLayout=findViewById(R.id.appbar_layout)



        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { _ ->
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_NewConcert)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.nav_home, R.id.nav_establishment, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share
            ), drawerLayout = drawerLayout
        )

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.label) {
                getString(R.string.menu_home) -> {
                        fab.show()
                        toolbar.navigationIcon = scaledDrawable(R.drawable.logo, 96, 96)
                }

                getString(R.string.menu_create_concert)->{
                        fab.hide()
                        toolbar.navigationIcon = getDrawable(R.drawable.ic_arrow_back_black_24dp)
                }

                else ->{
                        fab.hide()
                        toolbar.navigationIcon = scaledDrawable(R.drawable.logo, 96, 96)
                }
            }

        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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


}
