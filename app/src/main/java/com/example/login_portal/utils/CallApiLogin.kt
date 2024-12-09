package com.example.login_portal.utils

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.common.java.authorities.Authority
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalServiceException
import com.microsoft.identity.client.exception.MsalUiRequiredException
import com.microsoft.identity.common.java.ui.AuthorizationAgent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object CallApiLogin {
    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
    private const val SCOPE = "api://3e554bca-8316-4cbe-9532-d906633b298a/space_portal"
    private const val AUTHORITY = "https://login.microsoftonline.com/40127cd4-45f3-49a3-b05d-315a43a9f033"

    fun initMSAL(activity: Activity, callback: (Boolean) -> Unit) {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            activity.applicationContext,
            com.example.login_portal.R.raw.auth_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    mSingleAccountApp = application
                    callback(true)
                }

                override fun onError(exception: MsalException) {
                    Log.e("MSAL", "Error creating MSAL application", exception)
                    callback(false)
                }
            }
        )
    }
    fun signIn(activity: Activity, callback: (String?) -> Unit) {
        mSingleAccountApp?.let { app ->
            (activity as LifecycleOwner).lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val currentAccount = app.currentAccount?.currentAccount

                    withContext(Dispatchers.Main) {
                        if (currentAccount != null) {
                            app.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                                override fun onSignOut() {
                                    performSignIn(app, activity, callback)
                                }

                                override fun onError(exception: MsalException) {
                                    Log.e("MSAL", "Error signing out before new sign in", exception)
                                    callback(null)
                                }
                            })
                        } else {
                            performSignIn(app, activity, callback)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("MSAL", "Error checking current account", e)
                        callback(null)
                    }
                }
            }
        } ?: run {
            Log.e("MSAL", "MSAL not initialized")
            callback(null)
        }
    }
    private fun performSignIn(
        app: ISingleAccountPublicClientApplication,
        activity: Activity,
        callback: (String?) -> Unit
    ) {
        val parameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(activity)
            .withScopes(listOf(SCOPE))
            .withPrompt(Prompt.SELECT_ACCOUNT)
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    val accessToken = authenticationResult.accessToken
                    ApiServiceHelper.loginWithOutlook(accessToken) { success ->
                        if (success) {
                            callback(accessToken)
                        } else {
                            callback(null)
                        }
                    }
                }

                override fun onError(exception: MsalException) {
                    Log.e("MSAL", "Error during login", exception)
                    when (exception) {
                        is MsalUiRequiredException -> {
                            Log.e("MSAL", "UI required", exception)
                        }
                        is MsalClientException -> {
                            Log.e("MSAL", "Client error", exception)
                        }
                        is MsalServiceException -> {
                            Log.e("MSAL", "Service error", exception)
                        }
                    }
                    callback(null)
                }

                override fun onCancel() {
                    Log.d("MSAL", "User cancelled login")
                    callback(null)
                }
            })
            .build()

        try {
            app.acquireToken(parameters)
        } catch (e: Exception) {
            Log.e("MSAL", "Error acquiring token", e)
            callback(null)
        }
    }

    fun signOut(callback: (Boolean) -> Unit) {
        mSingleAccountApp?.let { app ->
            try {
                val account = app.currentAccount?.currentAccount
                if (account != null) {
                    app.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                        override fun onSignOut() {
                            callback(true)
                        }

                        override fun onError(exception: MsalException) {
                            Log.e("MSAL", "Error signing out", exception)
                            callback(false)
                        }
                    })
                } else {
                    callback(true)
                }
            } catch (e: Exception) {
                Log.e("MSAL", "Error during sign out", e)
                callback(false)
            }
        } ?: callback(false)
    }

//    fun signOut(callback: (Boolean) -> Unit) {
//        mSingleAccountApp?.let { app ->
//            val account = app.currentAccount?.currentAccount
//            if (account != null) {
//                app.signOut(
//                    object : ISingleAccountPublicClientApplication.SignOutCallback {
//                        override fun onSignOut() {
//                            callback(true)
//                        }
//
//
//                        override fun onError(exception: MsalException) {
//                            Log.e("MSAL", "Error signing out", exception)
//                            callback(false)
//                        }
//                    })
//            } else {
//                callback(true)
//            }
//        } ?: callback(false)
//    }
}