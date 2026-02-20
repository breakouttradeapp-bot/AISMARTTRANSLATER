package com.niv.aivoicetranslator

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.niv.aivoicetranslator.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnClearHistory.setOnClickListener {
            com.niv.aivoicetranslator.data.DatabaseHelper(this).clearAllHistory()
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()
        }
    }
}

