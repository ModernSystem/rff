package com.application.rocknfunapp.models

import android.graphics.drawable.Drawable
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import java.util.*

data class Concert(
    var name:String?=null,
    var establishmentId: String?=null,
    var date:Date?=null,
    var description:String?=null,
    var image: String?=null,
    var artist: String?=null,
    var nbOfParticipant:Int?=null,
    var id:String?=null,
    var establishmentName:String?=null
){
}