package com.jigumulmi.place.repository

import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.domain.SubwayStationPlace
import org.springframework.data.jpa.repository.JpaRepository

interface AdminSubwayStationPlaceRepository : JpaRepository<SubwayStationPlace?, Long?> {
    fun deleteAllByPlace(place: Place?)
}
