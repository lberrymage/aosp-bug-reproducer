package com.example.aospbugreproducer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.aospbugreproducer.ui.theme.AOSPBugReproducerTheme

private const val UNARCHIVE_APP_ID = "com.none.tom.exiferaser"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AOSPBugReproducerTheme {
                var status: Int? by remember { mutableStateOf(null) }

                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        status = intent.getIntExtra(PackageInstaller.EXTRA_UNARCHIVE_STATUS, -999)
                        Log.i("MAIN", "status: $status")
                    }
                }

                DisposableEffect(Unit) {
                    ContextCompat.registerReceiver(
                        applicationContext,
                        receiver,
                        IntentFilter("com.example.CUSTOM_ACTION"),
                        ContextCompat.RECEIVER_NOT_EXPORTED,
                    )

                    onDispose { applicationContext.unregisterReceiver(receiver) }
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    Intent("com.example.CUSTOM_ACTION").setPackage(applicationContext.packageName),
                    PendingIntent.FLAG_MUTABLE,
                )

                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            applicationContext
                                .packageManager
                                .packageInstaller
                                .requestUnarchive(UNARCHIVE_APP_ID, pendingIntent.intentSender)
                        },
                    ) {
                        Text("Try unarchiving")
                    }
                    Text("last received status: $status")
                }
            }
        }
    }
}
