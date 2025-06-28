package ru.voboost.config.demo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.voboost.config.ConfigManager
import ru.voboost.config.OnConfigChangeListener
import ru.voboost.config.models.Config
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(), OnConfigChangeListener {

    private lateinit var configTextView: TextView
    private lateinit var reloadButton: Button
    private val configManager = ConfigManager()
    private val configFileName = "config.yaml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        copyDefaultConfigIfNeeded()
        loadAndDisplayConfig()
        startWatchingConfig()
    }

    private fun initViews() {
        configTextView = findViewById(R.id.configTextView)
        reloadButton = findViewById(R.id.reloadButton)
    }

    private fun setupListeners() {
        reloadButton.setOnClickListener {
            loadAndDisplayConfig()
        }
    }

    private fun copyDefaultConfigIfNeeded() {
        val configFile = File(filesDir, configFileName)
        if (!configFile.exists()) {
            try {
                assets.open(configFileName).use { inputStream ->
                    FileOutputStream(configFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                showToast("Default configuration copied to app directory")
            } catch (e: IOException) {
                showToast("Failed to copy default configuration: ${e.message}")
            }
        }
    }

    private fun loadAndDisplayConfig() {
        val result = configManager.loadConfig(this, configFileName)

        result.fold(
            onSuccess = { config ->
                Log.d("MainActivity", "Configuration loaded successfully: $config")
                displayConfig(config)
                showToast("Configuration loaded successfully")
            },
            onFailure = { error ->
                Log.e("MainActivity", "Failed to load configuration", error)
                configTextView.text = "Error loading configuration:\n${error.message}"
                showToast("Failed to load configuration")
            }
        )
    }

    private fun displayConfig(config: Config) {
        val configText = buildString {
            appendLine("=== VOBOOST CONFIG DEMO ===")
            appendLine()

            appendLine("SETTINGS:")
            appendLine("  Language: ${config.settings?.language ?: "Not set"}")
            appendLine("  Theme: ${config.settings?.theme ?: "Not set"}")
            appendLine("  Interface Shift X: ${config.settings?.interfaceShiftX ?: "Not set"}")
            appendLine("  Interface Shift Y: ${config.settings?.interfaceShiftY ?: "Not set"}")
            appendLine()

            appendLine("VEHICLE:")
            appendLine("  Fuel Mode: ${config.vehicle?.fuelMode ?: "Not set"}")
            appendLine("  Drive Mode: ${config.vehicle?.driveMode ?: "Not set"}")
            appendLine()

            appendLine("Configuration file location:")
            appendLine("${File(filesDir, configFileName).absolutePath}")
            appendLine()
            appendLine("You can modify the config.yaml file and see changes in real-time!")
        }

        configTextView.text = configText
    }

    private fun startWatchingConfig() {
        val result = configManager.startWatching(this, configFileName, this)

        result.fold(
            onSuccess = {
                showToast("Started watching configuration file")
            },
            onFailure = { error ->
                showToast("Failed to start watching: ${error.message}")
            }
        )
    }

    override fun onConfigChanged(newConfig: Config, diff: Config) {
        runOnUiThread {
            displayConfig(newConfig)

            // Show what changed
            val changedFields = mutableListOf<String>()

            diff.settings?.language?.let { changedFields.add("Language") }
            diff.settings?.theme?.let { changedFields.add("Theme") }
            diff.settings?.interfaceShiftX?.let { changedFields.add("Interface Shift X") }
            diff.settings?.interfaceShiftY?.let { changedFields.add("Interface Shift Y") }
            diff.vehicle?.fuelMode?.let { changedFields.add("Fuel Mode") }
            diff.vehicle?.driveMode?.let { changedFields.add("Drive Mode") }

            if (changedFields.isNotEmpty()) {
                showToast("Configuration changed: ${changedFields.joinToString(", ")}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        configManager.stopWatching()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}