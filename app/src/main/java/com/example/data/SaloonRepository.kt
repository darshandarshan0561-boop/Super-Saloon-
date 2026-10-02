package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SaloonRepository(private val saloonDao: SaloonDao) {

    val latestShopStatus: Flow<ShopStatusEntity?> = saloonDao.getLatestShopStatus()
    val allShopStatusLogs: Flow<List<ShopStatusEntity>> = saloonDao.getAllShopStatusLogs()

    val latestOwnerAttendance: Flow<OwnerAttendanceEntity?> = saloonDao.getLatestOwnerAttendance()
    val allOwnerAttendance: Flow<List<OwnerAttendanceEntity>> = saloonDao.getAllOwnerAttendance()

    val allVisitors: Flow<List<VisitorEntity>> = saloonDao.getAllVisitors()
    val activeVisitors: Flow<List<VisitorEntity>> = saloonDao.getActiveVisitors()

    val allServices: Flow<List<ServiceEntity>> = saloonDao.getAllServices()

    suspend fun checkAndSeedDataIfNeeded() {
        val count = saloonDao.getServicesCount()
        if (count == 0) {
            AppDatabase.populateInitialData(saloonDao)
        }
    }

    suspend fun updateShopStatus(status: String, note: String, updatedBy: String = "Owner") {
        val statusLog = ShopStatusEntity(
            status = status,
            updatedBy = updatedBy,
            timestamp = System.currentTimeMillis(),
            note = note
        )
        saloonDao.insertShopStatus(statusLog)

        // Automatically mirror to Owner Attendance log
        val action = when (status) {
            "OPEN" -> "PUNCH_IN"
            "CLOSED" -> "PUNCH_OUT"
            "LEFT" -> "TEMPORARY_LEAVE"
            "BREAK" -> "ON_BREAK"
            else -> "STATUS_CHANGE"
        }
        val attendance = OwnerAttendanceEntity(
            actionType = action,
            timestamp = System.currentTimeMillis(),
            note = note.ifBlank { "Status set to $status" }
        )
        saloonDao.insertOwnerAttendance(attendance)
    }

    suspend fun recordOwnerAttendance(actionType: String, note: String) {
        val attendance = OwnerAttendanceEntity(
            actionType = actionType,
            timestamp = System.currentTimeMillis(),
            note = note
        )
        saloonDao.insertOwnerAttendance(attendance)

        // Automatically sync shop status if Punch Out or Punch In
        if (actionType == "PUNCH_OUT") {
            saloonDao.insertShopStatus(
                ShopStatusEntity(
                    status = "CLOSED",
                    updatedBy = "Owner",
                    timestamp = System.currentTimeMillis(),
                    note = "Owner punched out / Left shop"
                )
            )
        } else if (actionType == "PUNCH_IN") {
            saloonDao.insertShopStatus(
                ShopStatusEntity(
                    status = "OPEN",
                    updatedBy = "Owner",
                    timestamp = System.currentTimeMillis(),
                    note = "Owner punched in / Shop is Open"
                )
            )
        } else if (actionType == "TEMPORARY_LEAVE") {
            saloonDao.insertShopStatus(
                ShopStatusEntity(
                    status = "LEFT",
                    updatedBy = "Owner",
                    timestamp = System.currentTimeMillis(),
                    note = note.ifBlank { "Owner temporarily away" }
                )
            )
        }
    }

    suspend fun addVisitor(name: String, phone: String, service: String): Long {
        val currentMax = saloonDao.getMaxQueueToken() ?: 0
        val nextToken = currentMax + 1
        val visitor = VisitorEntity(
            visitorName = name,
            phoneNumber = phone,
            serviceRequested = service,
            status = "WAITING",
            queueToken = nextToken,
            checkInTime = System.currentTimeMillis()
        )
        return saloonDao.insertVisitor(visitor)
    }

    suspend fun updateVisitorStatus(id: Int, newStatus: String) {
        val checkOutTime = if (newStatus == "COMPLETED" || newStatus == "LEFT") {
            System.currentTimeMillis()
        } else null
        saloonDao.updateVisitorStatus(id, newStatus, checkOutTime)
    }

    suspend fun deleteVisitor(id: Int) {
        saloonDao.deleteVisitor(id)
    }
}
