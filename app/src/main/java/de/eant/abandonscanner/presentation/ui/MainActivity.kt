package de.eant.abandonscanner.presentation.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import de.eant.abandonscanner.databinding.ActivityMainBinding
import de.eant.abandonscanner.presentation.adapter.QRCodeAdapter
import de.eant.abandonscanner.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var qrCodeAdapter: QRCodeAdapter
    private val viewModel: MainViewModel by viewModels()

    private val importTxtLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            viewModel.importBarcodesFromTxt(uri, contentResolver)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // 观察ViewModel数据
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.qrCodes.collect { qrCodes ->
                qrCodeAdapter.updateQRCodes(qrCodes)
            }
        }

        lifecycleScope.launch {
            viewModel.message.collect { message ->
                message?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.clearMessage()
                }
            }
        }
    }

    private fun showClearConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Bestätigung")
            .setMessage("Sind Sie sicher, dass Sie alle entsorgten Barcodes löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.")
            .setPositiveButton("Löschen bestätigen") { dialog, _ ->
                viewModel.clearAllBarcodes()
                dialog.dismiss()
            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 