package com.example.abandonscanner

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.abandonscanner.data.AppDatabase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckScanActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // 检查条形码是否已废弃
            lifecycleScope.launch {
                val qrCode = withContext(Dispatchers.IO) {
                    database.qrCodeDao().getQRCodeByContent(result.contents.trim())
                }
                if (qrCode != null) {
                    // 命中废弃条形码，弹红色Dialog
                    showAbandonedBarcodeDialog(result.contents)
                } else {
                    // 未命中，继续扫码
                    Toast.makeText(this@CheckScanActivity, "Dieser Barcode kann verwendet werden", Toast.LENGTH_SHORT).show()
                    startZXingScan()
                }
            }
        } else {
            // 用户按返回键或扫码失败，直接返回主页面
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_scan)
        database = AppDatabase.getDatabase(this)
        
        // 设置返回按钮点击事件
        findViewById<android.widget.Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
        
        startZXingScan()
    }

    private fun showAbandonedBarcodeDialog(content: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_barcode_abandoned, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAbandonedTitle)
        val tvContent = dialogView.findViewById<TextView>(R.id.tvAbandonedContent)
        tvContent.text = content
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Weiter") { d, _ ->
                d.dismiss()
                startZXingScan()
            }
            .setNegativeButton("Zurück zum Hauptbildschirm") { d, _ ->
                d.dismiss()
                finish()
            }
            .create()
        dialog.show()
    }

    private fun startZXingScan() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
        options.setPrompt("Bitte richten Sie die Kamera auf den Barcode")
        options.setBeepEnabled(true)
        options.setCameraId(0)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }
} 