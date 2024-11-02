import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ProfileViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _name = savedStateHandle.getLiveData("name", "")
    val name: LiveData<String> = _name

    private val _age = savedStateHandle.getLiveData("age", "")
    val age: LiveData<String> = _age

    private val _medicalHistory = savedStateHandle.getLiveData("medicalHistory", "")
    val medicalHistory: LiveData<String> = _medicalHistory

    private val _bloodType = savedStateHandle.getLiveData("bloodType", "")
    val bloodType: LiveData<String> = _bloodType

    private val _isProfileSaved = savedStateHandle.getLiveData("isProfileSaved", false)
    val isProfileSaved: LiveData<Boolean> = _isProfileSaved

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> = _validationError

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateAge(newAge: String) {
        _age.value = newAge
    }

    fun updateMedicalHistory(newHistory: String) {
        _medicalHistory.value = newHistory
    }

    fun updateBloodType(newBloodType: String) {
        _bloodType.value = newBloodType
    }

    private fun validateProfile(): Boolean {
        val ageValue = age.value?.toIntOrNull()
        if (ageValue == null || ageValue < 18 || ageValue > 65) {
            _validationError.value = "Age must be between 18 and 65."
            return false
        }
        return true
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun editProfile() {
        _isProfileSaved.value = false
    }

    fun saveProfile() {
        if (validateProfile()) {
            _isProfileSaved.value = true
            _validationError.value = null
        }
    }
}
