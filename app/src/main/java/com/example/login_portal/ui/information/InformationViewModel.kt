package com.example.login_portal.ui.information


import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login_portal.R
import com.example.login_portal.ui.information.InformationsForInformationDao
import com.example.login_portal.ui.information.InformationsForInformation
import com.example.login_portal.utils.ApiServiceHelper
import com.example.login_portal.utils.ImgurService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide
import android.graphics.BitmapFactory
import kotlinx.coroutines.GlobalScope
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class InformationViewModel(private val context: Context) : ViewModel() {

    private val _informations = MutableLiveData<InformationsForInformation>()
    val informations : LiveData<InformationsForInformation> = _informations

    var editPersonalEmail = MutableLiveData<String>()
    var editPhoneNumber = MutableLiveData<String>()
    var editAddress = MutableLiveData<String>()
    var isEditing = MutableLiveData<Boolean>(false)

    private var isDefaultAvt : Boolean = false

    init {
        viewModelScope.launch {
        reset()
            informations.observeForever { info ->
            if (informations.value?.AvatarUrl.isNullOrEmpty()) {
                var defaultAvt = context.getDrawable(R.drawable.baseline_person_24)
                val currentInfo = informations.value ?: InformationsForInformation()
                currentInfo.AvatarBitmap = defaultAvt?.toBitmap()
                _informations.postValue(informations.value)
                isDefaultAvt = true
            } else {
                /*val inputStream = context.contentResolver.openInputStream(informations.value?.AvatarUrl!!.toUri())
                _informations.value?.setAvatarBitmap(inputStream)
                val currentInfo = _informations.value ?: InformationsForInformation()
                _informations.value = currentInfo*/
                _informations.postValue(informations.value)
                isDefaultAvt = false
            }

                editPersonalEmail.value = info.PersonalEmail ?: ""
                editPhoneNumber.value = info.PhoneNumber ?: ""
                editAddress.value = info.Address ?: ""
            }
        }
    }


    suspend fun reset(){
        withContext(Dispatchers.IO) {
        InformationsForInformationDao.getInformation { data ->
            _informations.value = data
            Log.d("GETINFOR",data.toString())
        }}
    }

    fun convert(value: Date?, targetType: Class<Date>, parameter: Any?, language: String): String {
        val currentLanguage = Locale.getDefault().language
        val locale = Locale(currentLanguage)
        val date = value as? Date ?: return ""

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", locale)
        return dateFormat.format(date)
    }

    fun AcceptChange(){
        informations.value?.PersonalEmail = editPersonalEmail.value ?: ""
        informations.value?.PhoneNumber = editPhoneNumber.value ?: ""
        informations.value?.Address = editAddress.value ?: ""
    }

    fun CheckAcceptChanges(): String {
        if (editPersonalEmail.value.isNullOrBlank() ||
            editPhoneNumber.value.isNullOrBlank() ||
            editAddress.value.isNullOrBlank()) {
            return context.getString(R.string.infor_student_frag_notEmpty)
        }

        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|org|net|edu)$")
        if (!emailPattern.matches(editPersonalEmail.value ?: "")) {
            return context.getString(R.string.infor_student_frag_error_mail)
        }

        if (editPhoneNumber.value?.length != 10 || !editPhoneNumber.value?.all { it.isDigit() }!!) {
            return context.getString(R.string.infor_student_frag_error_phoneNumber)
        }

        return "Success"
    }

    fun Save(){
        val personalEmailUpdate = editPersonalEmail.value ?: ""
        val phoneNumberUpdate = editPhoneNumber.value ?: ""
        val addressUpdate = editAddress.value ?: ""

        val contactInfo = mapOf(
            "personal_email_update" to personalEmailUpdate,
            "phone_number_update" to phoneNumberUpdate,
            "address_update" to addressUpdate
        )
        ApiServiceHelper.post("/rpc/update_contact_information", contactInfo) { responseBody ->
            if (responseBody != null) {
                val result = responseBody.string()
            } else {
                Log.e("Save", "Update failed")
            }
        }
    }

    fun CancelChanges(){
        editPersonalEmail.value = informations.value?.PersonalEmail ?:""
        editPhoneNumber.value = informations.value?.PhoneNumber ?:""
        editAddress.value = informations.value?.Address ?:""
    }

    fun UploadAvatar(fileUri: Uri) {
        if (fileUri != null)
        {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            _informations.value?.setAvatarBitmap(inputStream)
            //_informations.value?.AvatarUrl = fileUri.toString()
            val currentInfo = _informations.value ?: InformationsForInformation()
            _informations.value = currentInfo
            isDefaultAvt = false
            viewModelScope.launch {
                uploadAvatarToDatabase(fileUri)
            }
        }
    }

    fun removeAvatar() {
        if (isDefaultAvt)
        {
            Toast.makeText(context,"${context.getString(R.string.infor_student_frag_already_default)}",Toast.LENGTH_SHORT).show()
            return;
        }
        var defaultAvt = context.getDrawable(R.drawable.baseline_person_24)
        isDefaultAvt = true;
        if (defaultAvt != null) {
            _informations.value?.AvatarUrl = ""
            _informations.value?.AvatarBitmap = defaultAvt?.toBitmap()
            val currentInfo = _informations.value ?: InformationsForInformation()
            _informations.value = currentInfo
        }

        ApiServiceHelper.post("/rpc/update_avatar", mapOf("image_url" to "")) { responseBody ->
            if (responseBody != null) {
                val result = responseBody.string()
            } else {
                Log.e("Save", "Update failed")
            }
        }
    }

    suspend fun uploadAvatarToDatabase(fileUri: Uri) {
        val inputStream = context.contentResolver.openInputStream(fileUri)
        val mimeType = context.contentResolver.getType(fileUri) ?: "image/jpeg"
        val fileName = fileUri.lastPathSegment ?: "avatar.jpg"
        val imageUrl = ImgurService.uploadImageAsync(inputStream!!,fileName,mimeType)

        if (!imageUrl.isNullOrEmpty()) {
            val currentInfo = _informations.value ?: InformationsForInformation()
            currentInfo.AvatarUrl = imageUrl
            _informations.value = currentInfo
            ApiServiceHelper.post("/rpc/update_avatar", mapOf("image_url" to imageUrl)) { responseBody ->
                if (responseBody != null) {
                    val result = responseBody.string()
                } else {
                    Log.e("Save", "Update failed")
                }
            }
        } else {
            println("Image upload failed.")
        }
    }
}