package com.example.blooddonationapp.screens

import AppointmentViewModel
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.blooddonationapp.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppointmentScreen(
    onNextButtonClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    onShareButtonClicked: (String, String) -> Unit,
    viewModel: AppointmentViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    modifier: Modifier,
){
    val appointmentDate by viewModel.appointmentDate.observeAsState(initial = LocalDate.now())
    val appointmentTime by viewModel.appointmentTime.observeAsState(initial = LocalTime.of(9, 0))
    val selectedLocation by viewModel.selectedLocation.observeAsState(initial = "")
    val selectedGender by viewModel.selectedGender.observeAsState(initial = "")
    val genders = listOf("Male", "Female")
    val scrollState = rememberScrollState()
    val saveResult by viewModel.saveResult.observeAsState()
    val pickedHour by remember { mutableStateOf(LocalTime.now().hour) }
    val pickedMinute by remember { mutableStateOf(LocalTime.now().minute) }

    var mobileNo by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showMapScreen by remember { mutableStateOf(false) }

    // In the UI code
    if (viewModel.saveTriggered) {  // Only show dialog if save was attempted
        when (viewModel.saveResult.value) {
            true -> {  // Success case
                AlertDialog(
                    onDismissRequest = { viewModel.resetSaveResult() },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.resetSaveResult()
                            navController.navigate("home_screen_route") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Appointment Saved") },
                    text = { Text("Your appointment has been successfully saved!") }
                )
            }
            false -> {  // Save failed case
                AlertDialog(
                    onDismissRequest = { viewModel.resetSaveResult() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetSaveResult() }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Save Failed") },
                    text = { Text("Failed to save appointment. Please try again.") }
                )
            }
            null -> {  // Missing input fields case
                AlertDialog(
                    onDismissRequest = { viewModel.resetSaveResult() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetSaveResult() }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Nothing to Save") },
                    text = { Text("Please fill in all required fields before saving.") }
                )
            }
        }
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.make_appointment)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray
                )
            )
        }
    ){ contentPadding ->  // Add contentPadding here to ensure the top bar doesn't overlap
        Column (
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(16.dp)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Spacer(modifier = Modifier.height(4.dp))

            // Mobile Number
            OutlinedTextField(
                value = mobileNo,
                onValueChange = {mobileNo = it},
                label = {Text("Enter mobile number: ")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = {fullName = it},
                label = {Text("Enter full name: ")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Date selector
            Text(text = stringResource(R.string.select_date))
            Button(onClick = {
                showDatePicker = true
                viewModel.updateDate(date = appointmentDate)
            }) {
                Text(appointmentDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Confirm")
                        }
                    }
                ) {
                    DatePicker(
                        state = rememberDatePickerState(initialSelectedDateMillis = appointmentDate.toEpochDay() * 24 * 60 * 60 * 1000),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time selector
            Text(text = stringResource(R.string.select_time))
            Button(onClick = {
                showTimePicker = true
                viewModel.updateTime(time = appointmentTime)
            }) {
                Text(appointmentTime.format(DateTimeFormatter.ofPattern("HH:mm")))
            }

            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.updateTime(time = LocalTime.of(pickedHour, pickedMinute))
                            showTimePicker = false
                        }) {
                            Text("Confirm")
                        }
                    }
                ) {
                    TimePicker(
                        state = rememberTimePickerState(
                            initialHour = appointmentTime.hour,
                            initialMinute = appointmentTime.minute
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Age and gender row
            Text(text = stringResource(R.string.select_age_and_gender))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                // Age Field
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Gender selector
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedGender,  // Display selected gender
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        genders.forEach { gender ->
                            DropdownMenuItem(
                                text = { Text(gender) },
                                onClick = {
                                    viewModel.updateGender(gender)  // Update ViewModel
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Location selector
            Text(text = stringResource(R.string.select_location))
            // Update in the AppointmentScreen function
            if (showMapScreen) {
                FullScreenMapScreen(
                    onLocationSelected = { location ->
                        viewModel.updateLocation(location)
                    },
                    onCloseMap = { showMapScreen = false }
                )
            } else {
                Button(onClick = { showMapScreen = true }) {
                    Text(selectedLocation.ifEmpty { "Select Nearest Donation Center" })
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Row {
                Button(
                    onClick = {
                        viewModel.saveAppointment(
                            fullName = fullName,
                            mobileNo = mobileNo,
                            age = age
                        )

                        // Call share function with subject and message for sharing appointment details
                        val subject = "Blood Donation Appointment Details"
                        val message = """
                       Appointment for $fullName
                       Date: ${appointmentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}
                       Time: ${appointmentTime.format(DateTimeFormatter.ofPattern("HH:mm"))}
                       Location: $selectedLocation
                       Mobile: $mobileNo
                       Age: $age
                       Gender: $selectedGender
                    """.trimIndent()
                        onShareButtonClicked(subject, message)
                    },
                ) {
                    Text(stringResource(R.string.submit_appointment))
                }

                Button(
                    onClick = {
                        onCancelButtonClicked()
                        viewModel.cancelAppointment()
                    },
                ) {
                    Text(stringResource(R.string.cancel_appointment))
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        text = content
    )
}

@Composable
fun FullScreenMapScreen(
    modifier: Modifier = Modifier,
    defaultLocation: LatLng = LatLng(1.35, 103.87),
    onLocationSelected: (String) -> Unit,  // Pass selected location back
    onCloseMap: () -> Unit  // Close map callback
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Location permission is required to show map.", Toast.LENGTH_LONG).show()
            }
        }
    )

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            Marker(
                title = "Selected Location",
                snippet = "This is your appointment location"
            )
        }

        FloatingActionButton(
            onClick = {
                onLocationSelected("Selected Location Address")
                onCloseMap()  // Close map after selection
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("Select")
        }
    }
}

fun shareDetails(context: Context, subject: String, message: String){
    // Create an ACTION_SEND implicit intent with the details in the intent extras
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.share_details)
        )
    )
}
