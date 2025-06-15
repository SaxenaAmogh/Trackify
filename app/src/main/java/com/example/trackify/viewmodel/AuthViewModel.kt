package com.example.trackify.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _signupStatus = mutableStateOf<String?>(null)
    val signupStatus: State<String?> = _signupStatus

    private val _userName = mutableStateOf<String?>(null)
    val userName: State<String?> = _userName


    fun signUpUser(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val userData = hashMapOf(
                            "Name" to name,
                            "Email" to email,
                            "Net Balance" to 0
                        )
                        firestore.collection(uid)
                            .document("UserDetails")
                            .set(userData)
                            .addOnSuccessListener {
                                _signupStatus.value = "success"
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                _signupStatus.value = "Firestore error: ${e.message}"
                            }
                    } else {
                        _signupStatus.value = "User UID not found"
                    }
                } else {
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthUserCollisionException -> "Email already in use"
                        else -> task.exception?.message ?: "Signup failed"
                    }
                    _signupStatus.value = errorMessage
                }
            }
    }

    fun clearStatus() {
        _signupStatus.value = null
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signupStatus.value = "Login successful"
                    onSuccess()
                } else {
                    _signupStatus.value = task.exception?.message ?: "Login failed"
                }
            }
    }

    fun fetchUserName() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection(uid)
                .document("UserDetails")
                .get()
                .addOnSuccessListener { document ->
                    val name = document.getString("Name")
                    Log.d("UserNameFetch", "Fetched name: $name")
                    _userName.value = name
                }
                .addOnFailureListener { e ->
                    Log.e("UserNameFetch", "Error: ${e.message}")
                    _userName.value = null
                }
        } else {
            Log.e("UserNameFetch", "UID is null")
        }
    }


    fun logout() {
        auth.signOut()
    }

}
