package com.example.pocketmindai

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.pocketmindai.data.AppDatabase
import com.example.pocketmindai.ui.theme.PocketMindAITheme
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketMindAITheme {
                MainDashboard()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    val batteryRecord by database.behaviorDao().getLatestBattery().collectAsState(initial = null)
    val currentContext by database.behaviorDao().getLatestPrediction().collectAsState(initial = "Learning...")

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasNotificationPolicyAccess by remember {
        mutableStateOf(notificationManager.isNotificationPolicyAccessGranted)
    }

    // Refresh permission state when activity resumes
    DisposableEffect(context) {
        onDispose { }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PocketMind AI", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StatusCard(
                    title = "Current Context",
                    value = currentContext ?: "Calculating...",
                    icon = Icons.Default.Psychology,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatusCard(
                        title = "Battery",
                        value = "${batteryRecord?.percentage ?: "--"}%",
                        icon = if (batteryRecord?.isCharging == true) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    StatusCard(
                        title = "Monitoring",
                        value = "Active",
                        icon = Icons.Default.Radar,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            item {
                Text("Required Permissions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            item {
                PermissionItem(
                    name = "Usage Stats",
                    isGranted = (context as MainActivity).run { 
                        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
                        mode == AppOpsManager.MODE_ALLOWED
                    },
                    onGrant = {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    }
                )
            }

            item {
                PermissionItem(
                    name = "Location",
                    isGranted = hasLocationPermission,
                    onGrant = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                )
            }

            item {
                PermissionItem(
                    name = "DND Access",
                    isGranted = hasNotificationPolicyAccess,
                    onGrant = {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                    }
                )
            }
        }
    }
}


@Composable
fun StatusCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PermissionItem(name: String, isGranted: Boolean, onGrant: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name)
        if (isGranted) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Green)
        } else {
            Button(onClick = onGrant) {
                Text("Grant")
            }
        }
    }
}
