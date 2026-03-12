package dev.altavision.mdininginfo.presentation

import android.view.MenuItem
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuResponseWrapper(
    @SerialName(value = "menu")
    val menu : MenuResponse
)

@Serializable
data class MenuResponse (
    @SerializedName(value = "name")
    val mealType : String, // e.g. "LUNCH" or "DINNER"

    @SerializedName(value = "category")
    val categories : List<MealCategory>
)
@Serializable
data class MealCategory(
    @SerializedName(value = "name")
    val categoryName: String, // e.g. "Soup" or "Signature Blue"

    @SerializedName(value = "menuItem")
    val menuItems: List<DiningMenuItem>

);

@Serializable
data class DiningMenuItem(
    @SerializedName(value = "name")
    val name : String
    // There's some other nutrition info in there too that I don't really care about right now
)


//  {
//    "officialbuildingid": 1000063,
//    "image": "dining-south-quad.jpg",
//    "address": {
//      "city": "Ann Arbor",
//      "postalCode": "48109",
//      "street1": "600 East Madison",
//      "state": "MI",
//      "street2": ""
//    },
//    "buildingpreferredname": "South Quad",
//    "lng": -83.7421333,
//    "displayName": "South Quad Dining Hall",
//    "restricted": false,
//    "campus": "DINING HALLS",
//    "contact": {
//      "phone": "734-764-0169",
//      "email": "dining-southquad@umich.edu"
//    },
//    "name": "South Quad Dining Hall",
//    "type": "B",
//    "lat": 42.2736964
//  },