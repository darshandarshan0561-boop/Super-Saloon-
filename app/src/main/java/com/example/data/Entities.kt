package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_status")
data class ShopStatusEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val status: String, // "OPEN", "CLOSED", "BREAK", "LEFT"
    val updatedBy: String = "Owner",
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "owner_attendance")
data class OwnerAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val actionType: String, // "PUNCH_IN", "PUNCH_OUT", "TEMPORARY_LEAVE", "RETURNED"
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "visitors")
data class VisitorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val visitorName: String,
    val phoneNumber: String,
    val serviceRequested: String,
    val status: String, // "WAITING", "IN_CHAIR", "COMPLETED", "LEFT"
    val queueToken: Int,
    val checkInTime: Long = System.currentTimeMillis(),
    val checkOutTime: Long? = null,
    val rating: Int = 5
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val price: Double,
    val durationMinutes: Int,
    val description: String,
    val isPopular: Boolean = false
)
