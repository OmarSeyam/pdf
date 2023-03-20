package com.example.pdfdownloader.activity

import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfdownloader.R
import com.example.pdfdownloader.adapter.PdfAdapter
import com.example.pdfdownloader.databinding.ActivityViewFilesBinding
import com.example.pdfdownloader.model.PDF
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewFiles : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityViewFilesBinding
    lateinit var data:ArrayList<PDF>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityViewFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db=Firebase.firestore
        data =ArrayList<PDF>()
        getAllFiles()
        binding.add.setOnClickListener {
            val i= Intent(this,MainActivity::class.java)
            startActivity(i)
        }
    }
    fun getAllFiles() {
        showDialog()
        db.collection("pdfFiles")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (doc in it) {
                        val pdf = PDF(
                            doc.id,
                            doc.getString("name")!!,
                            doc.getString("uri")!!,
                        )
                        data.add(pdf)
                    }
                    var contactAdapter = PdfAdapter(this, data)
                    binding.rv.layoutManager = LinearLayoutManager(this)
                    binding.rv.adapter = contactAdapter
                }
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }
    }
    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Wait ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
}