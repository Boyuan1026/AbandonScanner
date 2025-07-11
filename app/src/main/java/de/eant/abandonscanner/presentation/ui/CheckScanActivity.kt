package de.eant.abandonscanner.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.eant.abandonscanner.R
import de.eant.abandonscanner.presentation.viewmodel.CheckScanViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckScanActivity : AppCompatActivity() {
    private val viewModel: CheckScanViewModel by viewModels()

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // 检查条形码是否已废弃
            viewModel.checkBarcode(result.contents)
        } else {
            // 用户按返回键或扫码失败，直接返回主页面
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_scan)
        
        // 设置返回按钮点击事件
        findViewById<android.widget.Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
        
        observeViewModel()
        startZXingScan()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.checkResult.collect { result ->
                result?.let {
                    if (it.isAbandoned) {
                        // 命中废弃条形码，弹红色Dialog
                        showAbandonedBarcodeDialog(it.content)
                    } else {
                        // 未命中，继续扫码
                        Toast.makeText(this@CheckScanActivity, it.message, Toast.LENGTH_SHORT).show()
                        startZXingScan()
                    }
                    viewModel.clearCheckResult()
                }
            }
        }
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