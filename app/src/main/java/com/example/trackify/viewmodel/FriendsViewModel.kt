package com.example.trackify.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FriendsViewModel: ViewModel() {

    data class FriendDetail(
        val pending: Float,
        val mySpending: Float,
        val category: Map<String, Float>
    )

    data class CategoryCount(val category: String, val count: Float)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _friendDetails = mutableStateOf<Map<String, FriendDetail>>(emptyMap())
    val friendDetails: State<Map<String, FriendDetail>> get() = _friendDetails

    fun loadFriendDetails() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _friendDetails.value = fetchAllFriendDetails(uid)
        }
    }
    suspend fun fetchAllFriendDetails(uid: String): Map<String, FriendDetail> {
        val firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection(uid).document("FriendDetails").get().await()
            val data = snapshot.data ?: emptyMap()

            data.mapNotNull { (friendName, value) ->
                val friendMap = value as? Map<*, *> ?: return@mapNotNull null

                val pending = (friendMap["Pending"] as? Number)?.toFloat() ?: 0f
                val mySpending = (friendMap["MySpending"] as? Number)?.toFloat() ?: 0f
                val categoryMap = (friendMap["Category"] as? Map<String, *>)?.mapValues { (_, v) ->
                    (v as? Number)?.toFloat() ?: 0f
                } ?: emptyMap()

                friendName to FriendDetail(
                    pending = pending,
                    mySpending = mySpending,
                    category = categoryMap
                )
            }.toMap()
        } catch (e: Exception) {
            Log.e("FetchFriendDetails", "Error: ${e.message}")
            emptyMap()
        }
    }

    fun convertCategoryMapToChartData(friend: FriendsViewModel.FriendDetail?): List<CategoryCount> {
        return friend?.category?.map { (categoryName, value) ->
            CategoryCount(category = categoryName, count = value)
        } ?: emptyList()
    }

}