package com.example.timecrafted.ui.main.data

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Categories(
    val id: String = "",
    val name: String = "",
    val imagePath: String = "",
    val imageUrl: String = ""
) : Serializable
