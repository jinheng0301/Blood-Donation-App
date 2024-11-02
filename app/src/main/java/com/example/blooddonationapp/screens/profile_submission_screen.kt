import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.blooddonationapp.R
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    onNextButtonClicked: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val name by viewModel.name.observeAsState("")
    val age by viewModel.age.observeAsState("")
    val medicalHistory by viewModel.medicalHistory.observeAsState("")
    val bloodType by viewModel.bloodType.observeAsState("")
    val isProfileSaved by viewModel.isProfileSaved.observeAsState(false)
    val validationError by viewModel.validationError.observeAsState(null)
    val scrollState = rememberScrollState()

    var expanded by rememberSaveable { mutableStateOf(false) }
    var showEligibilityDialog by rememberSaveable { mutableStateOf(false) }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray)
            )
        }
    ) { contentPadding ->
        if (isProfileSaved) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Name: $name", style = MaterialTheme.typography.titleMedium)
                Text(text = "Age: $age", style = MaterialTheme.typography.titleMedium)
                Text(text = "Blood Type: $bloodType", style = MaterialTheme.typography.titleMedium)
                Text(text = "Medical History: $medicalHistory", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.editProfile() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.edit_profile))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = stringResource(R.string.name))
                TextField(
                    value = name,
                    onValueChange = { viewModel.updateName(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.age))
                TextField(
                    value = age,
                    onValueChange = { viewModel.updateAge(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.medical_history))
                TextField(
                    value = medicalHistory,
                    onValueChange = { viewModel.updateMedicalHistory(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.blood_type))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = bloodType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        bloodTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    viewModel.updateBloodType(type)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.saveProfile()
                        if (validationError == null) {
                            onNextButtonClicked()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.save_profile))
                }

                if (validationError != null) {
                    AlertDialog(
                        onDismissRequest = { viewModel.clearValidationError() },
                        confirmButton = {
                            Button(onClick = { viewModel.clearValidationError() }) {
                                Text("OK")
                            }
                        },
                        title = { Text("Invalid Profile") },
                        text = { Text(validationError ?: "") }
                    )
                }

                Button(
                    onClick = { showEligibilityDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Eligibility Information")
                }

                if (showEligibilityDialog) {
                    EligibilityInfoDialog(onDismiss = { showEligibilityDialog = false })
                }
            }
        }
    }
}

@Composable
fun EligibilityInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("OK")
            }
        },
        title = { Text("Eligibility & Preparation") },
        text = {
            Column {
                Text(text = "Donor Eligibility Criteria:")
                Text(text = "- Must be 18-65 years old\n- Weigh at least 50 kg\n- In good health\n- Not have donated blood in the last 3 months")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Things to Do Before Donation:")
                Text(text = "- Stay hydrated\n- Avoid alcohol for 24 hours before donation\n- Eat iron-rich foods\n- Get a good night's sleep")
            }
        }
    )
}
