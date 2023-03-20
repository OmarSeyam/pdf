package com.example.pdfdownloader.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pdfdownloader.R
import com.example.pdfdownloader.databinding.ActivityMainBinding
import com.example.pdfdownloader.model.PDF
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

private val PICK_PDF_REQUEST = 111
var pdfURI: Uri? = null
private var fileURI: Uri? = null
private var progressDialog: ProgressDialog? = null
@SuppressLint("StaticFieldLeak")
lateinit var binding:ActivityMainBinding
val storage = Firebase.storage
val storageRef = storage.reference
val pdfRef = storageRef.child("files")
@SuppressLint("StaticFieldLeak")
var db =Firebase.firestore
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnSelect.setOnClickListener {
            selectPDF()
        }


        binding.btnUpload.setOnClickListener {
            uploadPDF()
        }
    }

    fun selectPDF(){
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, PICK_PDF_REQUEST)
    }
    @SuppressLint("SuspiciousIndentation")
    fun uploadPDF(){
        if(pdfURI !=null) {
            showDialog()
            var name = System.currentTimeMillis().toString() + "_omrpdf.pdf"
            val childRef =
                pdfRef.child(name)
            var uploadTask = childRef.putFile(pdfURI!!)
            uploadTask.addOnFailureListener { exception ->
                binding.imageView.setImageResource(R.drawable.no_results)
                Toast.makeText(
                    this,
                    "PDF Uploaded Fail(${exception.message})",
                    Toast.LENGTH_SHORT
                )
                    .show()
                hideDialog()
            }.addOnSuccessListener {
                childRef.downloadUrl.addOnSuccessListener { uri ->
                    fileURI = uri
                    var pdf = PDF("", name, fileURI.toString())

                    db.collection("pdfFiles")
                        .add(pdf)
                        .addOnSuccessListener {
                            binding.imageView.setImageResource(R.drawable.no_results)
                            val i = Intent(this, ViewFiles::class.java)
                            startActivity(i)
                            Toast.makeText(
                                this,
                                "Success Add This File",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            hideDialog()
                        }
                        .addOnFailureListener {
                            binding.imageView.setImageResource(R.drawable.no_results)
                            Toast.makeText(
                                this,
                                "Fail Add This File(${it.message})",
                                Toast.LENGTH_SHORT
                            ).show()
                            hideDialog()
                        }
                }

            }
        }
    }
    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading Product ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK) {
            pdfURI = data!!.data
            binding.imageView.setImageResource(R.drawable.pdf)
        }
    }
}