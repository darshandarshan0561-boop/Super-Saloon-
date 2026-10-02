package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SaloonDao {

    // Shop Status Queries
    @Query("SELECT * FROM shop_status ORDER BY timestamp DESC LIMIT 1")
    fun getLatestShopStatus(): Flow<ShopStatusEntity?>

    @Query("SELECT * FROM shop_status ORDER BY timestamp DESC LIMIT 50")
    fun getAllShopStatusLogs(): Flow<List<ShopStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopStatus(status: ShopStatusEntity)

    // Owner Attendance Queries
    @Query("SELECT * FROM owner_attendance ORDER BY timestamp DESC LIMIT 1")
    fun getLatestOwnerAttendance(): Flow<OwnerAttendanceEntity?>

    @Query("SELECT * FROM owner_attendance ORDER BY timestamp DESC LIMIT 50")
    fun getAllOwnerAttendance(): Flow<List<OwnerAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnerAttendance(attendance: OwnerAttendanceEntity)

    // Visitor Registry Queries
    @Query("SELECT * FROM visitors ORDER BY checkInTime DESC")
    fun getAllVisitors(): Flow<List<VisitorEntity>>

    @Query("SELECT * FROM visitors WHERE status IN ('WAITING', 'IN_CHAIR') ORDER BY queueToken ASC")
    fun getActiveVisitors(): Flow<List<VisitorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitor(visitor: VisitorEntity): Long

    @Query("UPDATE visitors SET status = :status, checkOutTime = :checkOutTime WHERE id = :id")
    suspend fun updateVisitorStatus(id: Int, status: String, checkOutTime: Long? = null)

    @Query("DELETE FROM visitors WHERE id = :id")
    suspend fun deleteVisitor(id: Int)

    @Query("SELECT MAX(queueToken) FROM visitors")
    suspend fun getMaxQueueToken(): Int?

    // Services Catalog Queries
    @Query("SELECT * FROM services ORDER BY isPopular DESC, id ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Query("SELECT COUNT(*) FROM services")
    suspend fun getServicesCount(): Int
}
