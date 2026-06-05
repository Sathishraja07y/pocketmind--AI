package com.example.pocketmindai

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pocketmindai.ui.theme.PocketMindAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketMindAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        hasPermission = hasUsageStatsPermission(),
                        onGrantPermissionClick = { requestUsageStatsPermission() }
                    )
                }
            }
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }
}

@Composable
fun MainScreen(hasPermission: Boolean, onGrantPermissionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "PocketMind AI",
            style = MaterialTheme.typography.headlineMedium
        )
        
        if (!hasPermission) {
            Text(
                text = "Usage stats permission is required to track behavior.",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Button(onClick = onGrantPermissionClick) {
                Text("Grant Permission")
            }
        } else {
            Text(
                text = "System is learning your behavior...",
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}
