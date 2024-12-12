package com.example.login_portal.ui.information
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



data class InformationsForInformation(
    var FullName: String? = null,
    var Major: String? = null,
    var AcademicProgram: String? = null,
    var StudentId: String? = null,
    var YearOfAdmission: Int? = null,
    var Gender: String? = null,
    var DateOfBirth: Date? = null,
    var IdentityCardNumber: String? = null,
    var Nationality: String? = null,
    var Ethnicity: String? = null,
    var SchoolEmail: String? = null,
    var Address: String? = null,
    var PhoneNumber: String? = null,
    var PersonalEmail: String? = null,
    var AvatarUrl: String? = null,
    var AvatarBitmap: Bitmap? = null
) {
    fun setAvatarBitmap(inputStream: InputStream?) {
        AvatarBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
    }
}

