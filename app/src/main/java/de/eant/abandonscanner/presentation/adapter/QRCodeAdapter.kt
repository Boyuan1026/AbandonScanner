package de.eant.abandonscanner.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.eant.abandonscanner.R
import de.eant.abandonscanner.data.local.QRCode
import java.text.SimpleDateFormat
import java.util.*

class QRCodeAdapter : RecyclerView.Adapter<QRCodeAdapter.ViewHolder>() {
    private var qrCodes: List<QRCode> = emptyList()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentText: TextView = view.findViewById(R.id.tvContent)
        val timestampText: TextView = view.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_qr_code, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val qrCode = qrCodes[position]
        holder.contentText.text = qrCode.content
        holder.timestampText.text = dateFormat.format(Date(qrCode.timestamp))
    }

    override fun getItemCount() = qrCodes.size

    fun updateQRCodes(newQRCodes: List<QRCode>) {
        qrCodes = newQRCodes
        notifyDataSetChanged()
    }
} 