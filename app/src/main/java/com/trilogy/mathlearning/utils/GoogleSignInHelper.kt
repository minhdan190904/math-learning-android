package com.trilogy.mathlearning.utils

import android.content.Context
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.trilogy.mathlearning.R

object GoogleSignInHelper {
    private fun getRequest(context: Context): GetCredentialRequest {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()
        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }

    suspend fun fetchIdToken(
        context: Context,
        credentialManager: CredentialManager
    ): String {
        try {
            val result = credentialManager.getCredential(context, getRequest(context))
            val cred = result.credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return GoogleIdTokenCredential.createFrom(cred.data).idToken
            }
            throw kotlin.IllegalStateException("No Google ID Token Credential")
        } catch (e: GetCredentialException) {
            throw e
        }
    }
}