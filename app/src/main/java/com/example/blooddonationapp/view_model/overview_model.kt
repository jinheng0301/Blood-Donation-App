import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blooddonationapp.model.AppointmentDetails
import java.time.LocalDate
import java.time.LocalTime

class AppointmentViewModel : ViewModel() {
    private val _appointmentDate = MutableLiveData<LocalDate>()
    val appointmentDate: LiveData<LocalDate> = _appointmentDate

    private val _appointmentTime = MutableLiveData<LocalTime>()
    val appointmentTime: LiveData<LocalTime> = _appointmentTime

    private val _selectedLocation = MutableLiveData<String>()
    val selectedLocation: LiveData<String> = _selectedLocation

    private val _selectedGender = MutableLiveData<String>()
    val selectedGender: LiveData<String> = _selectedGender

    private val _appointmentsList = MutableLiveData<List<AppointmentDetails>>(emptyList())
    val appointmentsList: LiveData<List<AppointmentDetails>> = _appointmentsList

    private val _isAppointmentCancelled = MutableLiveData<Boolean>()
    val isAppointmentCancelled: LiveData<Boolean> = _isAppointmentCancelled

    // New flag to check if the submit button was clicked
    var saveTriggered = false

    // LiveData for save result
    private val _saveResult = MutableLiveData<Boolean?>()
    val saveResult: LiveData<Boolean?> = _saveResult

    fun updateLocation(location: String) {
        _selectedLocation.value = location
    }

    fun updateDate(date: LocalDate) {
        _appointmentDate.value = date
    }

    fun updateTime(time: LocalTime) {
        _appointmentTime.value = time
    }

    fun updateGender(gender: String){
        // Implement gender update logic
        _selectedGender.value = gender
    }

    // Save appointment with validation
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveAppointment(fullName: String, mobileNo: String, age: String) {
        saveTriggered = true  // Set flag when saving is attempted

        // Check if any required fields are missing
        if (fullName.isBlank() || mobileNo.isBlank() || age.isBlank()) {
            _saveResult.value = null  // Indicate missing data
            return
        }

        // Add new appointment to the list
        val newAppointment = AppointmentDetails(
            fullName = fullName,
            mobileNo = mobileNo,
            appointmentDate = _appointmentDate.value ?: LocalDate.now(),
            appointmentTime = _appointmentTime.value ?: LocalTime.of(9, 0),
            age = age,
            gender = _selectedGender.value.orEmpty(),
            location = _selectedLocation.value.orEmpty()
        )
        _appointmentsList.value = _appointmentsList.value.orEmpty() + newAppointment

        _saveResult.value = true  // Set save success flag
    }

    fun resetSaveResult() {
        _saveResult.value = null
        saveTriggered = false  // Reset save trigger
    }

    fun cancelAppointment() {
        // Implement cancel logic
        _isAppointmentCancelled.value = true
    }
}
