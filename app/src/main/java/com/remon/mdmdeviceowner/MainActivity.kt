package com.remon.mdmdeviceowner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remon.mdmdeviceowner.ui.theme.MdmDeviceOwnerTheme

class MainActivity : ComponentActivity() {

    private lateinit var deviceOwnerManager: DeviceOwnerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceOwnerManager = DeviceOwnerManager(this)

        setContent {
            MdmDeviceOwnerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeviceOwnerScreen(deviceOwnerManager)
                }
            }
        }
    }
}

@Composable
fun DeviceOwnerScreen(deviceOwnerManager: DeviceOwnerManager) {
    var deviceInfo by remember { mutableStateOf(deviceOwnerManager.getDeviceInfo()) }
    var cameraDisabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Refresh every 2 seconds
        while (true) {
            deviceInfo = deviceOwnerManager.getDeviceInfo()
            kotlinx.coroutines.delay(2000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Device Owner Test App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (deviceInfo["isDeviceOwner"] as Boolean)
                    Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (deviceInfo["isDeviceOwner"] as Boolean)
                        "DEVICE OWNER ACTIVE"
                    else "NOT DEVICE OWNER",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (!(deviceInfo["isDeviceOwner"] as Boolean)) {
                    Text(
                        text = "Set as device owner via ADB for full functionality",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Device Information
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            item {
                InfoSection("Device Information", deviceInfo)
            }
        }

        // Actions (only enabled if device owner)
        if (deviceInfo["isDeviceOwner"] as Boolean) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { deviceOwnerManager.lockDevice() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("Lock Device")
                }

                Button(
                    onClick = {
                        cameraDisabled = !cameraDisabled
                        deviceOwnerManager.setCameraDisabled(cameraDisabled)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (cameraDisabled) Color.Red else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (cameraDisabled) "Enable Camera" else "Disable Camera")
                }

                OutlinedButton(
                    onClick = { deviceOwnerManager.wipeData() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Factory Reset Device")
                }
            }
        }

        // Footer
        Text(
            text = "For Testing Purposes Only",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun InfoSection(title: String, info: Map<String, Any>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            info.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = value.toString(),
                        fontSize = 14.sp,
                        color = if (value is Boolean) {
                            if (value) Color(0xFF4CAF50) else Color(0xFFF44336)
                        } else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}