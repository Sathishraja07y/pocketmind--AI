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

import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.pocketmindai.data.entity.BatteryRecord
import com.example.pocketmindai.data.entity.BatteryPrediction
import java.text.SimpleDateFormat
import java.util.*

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
    val batteryPrediction by database.behaviorDao().getLatestBatteryPrediction().collectAsState(initial = null)
    val allBatteryRecords by database.behaviorDao().getAllBattery().collectAsState(initial = emptyList())
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Phase 10: Simple Voice Assistant Trigger
                val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "How can PocketMind AI help you?")
                }
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Assistant")
            }
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
                Text("AI Insights", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Battery Prediction", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Remaining: ${String.format("%.1f", batteryPrediction?.predictedHoursRemaining ?: 0f)} hours")
                        Text("Next Charge: ${batteryPrediction?.nextChargeTime?.let { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it)) } ?: "Calculating..."}")
                    }
                }
            }

            item {
                Text("Battery Trends", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                BatteryChart(allBatteryRecords)
            }

            item {
                Text("Manual Controls (Testing)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { 
                            val actionManager = com.example.pocketmindai.manager.SmartActionManager(context)
                            actionManager.executeActionsForContext("GYM")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Simulate Gym", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { 
                            val actionManager = com.example.pocketmindai.manager.SmartActionManager(context)
                            actionManager.executeActionsForContext("WORK")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Simulate Work", fontSize = 12.sp)
                    }
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
fun BatteryChart(records: List<BatteryRecord>) {
    val entries = records.take(10).reversed().mapIndexed { index, record ->
        Entry(index.toFloat(), record.percentage.toFloat())
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                axisRight.isEnabled = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        update = { chart ->
            val dataSet = LineDataSet(entries, "Battery level").apply {
                color = android.graphics.Color.BLUE
                setCircleColor(android.graphics.Color.BLUE)
                lineWidth = 2f
                setDrawValues(false)
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
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
