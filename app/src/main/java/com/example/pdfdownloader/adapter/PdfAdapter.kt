package com.example.pdfdownloader.adapter

import android.app.Activity
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfdownloader.databinding.LayoutViewBinding
import com.example.pdfdownloader.model.PDF

class PdfAdapter (var activity: Activity, var data: ArrayList<PDF>) :
RecyclerView.Adapter<PdfAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: LayoutViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutViewBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvName.setText(data[position].name)
        holder.binding.btnDown.setOnClickListener {
            Toast.makeText(activity,"Downloading...",Toast.LENGTH_SHORT).show()
            val request =DownloadManager.Request(Uri.parse(data[position].uri+""))
            request.setTitle(data[position].name)
            request.setMimeType("application/pdf")
            request.allowScanningByMediaScanner()
            request.setAllowedOverMetered(true)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,data[position].name)
            val mgr =activity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            mgr.enqueue(request)
        }

    }

    override fun getItemCount(): Int {
        return  data.size
    }
}