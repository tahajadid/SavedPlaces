package tahadeta.example.savedplaces.model

import kotlinx.serialization.Serializable

@Serializable
data class FavouritePlace(
    val lat: String = "0.0",
    val lng: String = "0.0",
    val title: String = ""
)
