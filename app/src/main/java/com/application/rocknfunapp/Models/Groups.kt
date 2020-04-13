package com.application.rocknfunapp.Models

import com.application.rocknfunapp.Models.Artist
import com.application.rocknfunapp.Models.Concert

class Groups(
    var artists:List<Artist>,
    var upcomingConcert:List<Concert>
) {
}