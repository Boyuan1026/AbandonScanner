package com.example.abandonscanner

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abandonscanner.data.AppDatabase
import com.example.abandonscanner.data.QRCode
import com.example.abandonscanner.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var qrCodeAdapter: QRCodeAdapter

    private val importTxtLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            importBarcodesFromTxt(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        qrCodeAdapter = QRCodeAdapter()

        // 设置RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = qrCodeAdapter

        // 按钮点击事件
        binding.btnEntryBarcode.setOnClickListener {
            val intent = Intent(this, EntryScanActivity::class.java)
            startActivity(intent)
        }

        binding.btnCheckBarcode.setOnClickListener {
            val intent = Intent(this, CheckScanActivity::class.java)
            startActivity(intent)
        }

        binding.btnClearAll.setOnClickListener {
            showClearConfirmDialog()
        }

        binding.btnImportTxt.setOnClickListener {
            val mimeTypes = arrayOf("text/plain")
            importTxtLauncher.launch(mimeTypes)
        }

        // 开始观察数据变化
        lifecycleScope.launch {
            database.qrCodeDao().getAllQRCodes().collectLatest { qrCodes ->
                qrCodeAdapter.updateQRCodes(qrCodes)
            }
        }
    }

    private fun showClearConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Bestätigung")
            .setMessage("Sind Sie sicher, dass Sie alle entsorgten Barcodes löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.")
            .setPositiveButton("Löschen bestätigen") { dialog, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.qrCodeDao().deleteAll()
                    }
                    Toast.makeText(this@MainActivity, "Alle entsorgten Barcodes wurden gelöscht", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun importBarcodesFromTxt(uri: Uri) {
        lifecycleScope.launch {
            try {
                val barcodes = withContext(Dispatchers.IO) {
                    val inputStream = contentResolver.openInputStream(uri)
                        ?: throw Exception("Datei kann nicht geöffnet werden")
                    val lines = inputStream.bufferedReader().readLines()
                    inputStream.close()
                    val codes = lines.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
                    if (codes.isEmpty()) throw Exception("Datei ist leer oder enthält keine gültigen Barcodes")
                    codes
                }
                withContext(Dispatchers.IO) {
                    database.qrCodeDao().deleteAll()
                    barcodes.forEach { database.qrCodeDao().insert(QRCode(it)) }
                }
                Toast.makeText(this@MainActivity, "Import erfolgreich, ${barcodes.size} Barcodes importiert", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                showErrorDialog("Import fehlgeschlagen: ${e.message}")
            }
        }
    }

    private fun showErrorDialog(msg: String) {
        AlertDialog.Builder(this)
            .setTitle("Fehler")
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // 页面恢复时刷新列表
        lifecycleScope.launch {
            database.qrCodeDao().getAllQRCodes().collectLatest { qrCodes ->
                qrCodeAdapter.updateQRCodes(qrCodes)
            }
        }
    }
} 