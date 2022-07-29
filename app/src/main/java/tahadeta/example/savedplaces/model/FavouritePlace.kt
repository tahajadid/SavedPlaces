package tahadeta.example.savedplaces.model

import kotlinx.serialization.Serializable

@Serializable
data class FavouritePlace(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val title: String = ""
)
