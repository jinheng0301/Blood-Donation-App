package com.example.blooddonationapp.model

import java.time.LocalDate
import java.time.LocalTime

data class AppointmentDetails (
    val fullName: String,
    val mobileNo: String,
    val appointmentDate: LocalDate,
    val appointmentTime: LocalTime,
    val age: String,
    val gender: String,
    val location: String,
)