package com.example.abandonscanner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.abandonscanner.data.AppDatabase
import com.example.abandonscanner.data.QRCode
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntryScanActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // 录入废弃条形码
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    database.qrCodeDao().insert(QRCode(result.contents.trim()))
                }
                Toast.makeText(this@EntryScanActivity, "Entsorgter Barcode erfasst: ${result.contents}", Toast.LENGTH_LONG).show()
                // 返回主页面
                finish()
            }
        } else {
            Toast.makeText(this@EntryScanActivity, "Scan fehlgeschlagen, bitte versuchen Sie es erneut", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_scan)
        database = AppDatabase.getDatabase(this)
        startZXingScan()
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