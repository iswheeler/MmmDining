package dev.altavision.mdininginfo.presentation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiningLocation(
    @SerialName(value = "officialbuildingid")
    val officialbuildingid: String,

    @SerialName(value = "displayName")
    val displayName: String,

    @SerialName(value = "name")
    val name: String,

    @SerialName(value = "campus")
    val campus: String,

//    @SerialName(value = "")
);


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