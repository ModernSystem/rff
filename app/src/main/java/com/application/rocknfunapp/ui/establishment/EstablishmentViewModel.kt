package com.application.rocknfunapp.ui.establishment

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.rocknfunapp.MainActivity
import com.application.rocknfunapp.models.Establishment

class EstablishmentViewModel : ViewModel() {

    private val _name = MutableLiveData<String>().apply {
        value = MainActivity.establishment.name
    }
    val name: LiveData<String> = _name

    private val _contact = MutableLiveData<String>().apply {
        value = MainActivity.establishment.contact
    }
    val contact: LiveData<String> = _contact

    private val _address = MutableLiveData<String>().apply {
        value = MainActivity.establishment.location
    }
    val address: LiveData<String> = _address

    private val _description = MutableLiveData<String>().apply {
        value = MainActivity.establishment.description
    }
    val description: LiveData<String> = _description

    private val _profilPicture = MutableLiveData<Drawable>().apply {
        value = MainActivity.establishment.profilPicture
    }
    val picture: LiveData<Drawable> = _profilPicture

}