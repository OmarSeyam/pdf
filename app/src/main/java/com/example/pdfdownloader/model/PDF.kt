package com.example.pdfdownloader.model

import com.google.firebase.firestore.DocumentId
import java.net.URI

data class PDF(@DocumentId var id:String, var name:String, var uri: String)