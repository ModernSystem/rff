package com.application.rocknfunapp.models

import android.graphics.drawable.Drawable
import java.util.*

class Concert(
    var name:String,
    var location: Establishment,
    var date:Date,
    var description:String?,
    var image:Drawable,
    var artist: Artist?
){
}