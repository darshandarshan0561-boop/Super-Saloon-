package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ShopStatusEntity::class,
        OwnerAttendanceEntity::class,
        VisitorEntity::class,
        ServiceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun saloonDao(): SaloonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "open_shop_saloon_db"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.saloonDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: SaloonDao) {
            // Initial Shop Status
            dao.insertShopStatus(
                ShopStatusEntity(
                    status = "OPEN",
                    updatedBy = "Owner",
                    timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000), // Opened 2 hrs ago
                    note = "Balale Open Shop Saloon open for morning appointments"
                )
            )

            // Initial Owner Attendance
            dao.insertOwnerAttendance(
                OwnerAttendanceEntity(
                    actionType = "PUNCH_IN",
                    timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000),
                    note = "Owner checked in at Balale Shop (Pin: 571219)"
                )
            )

            // Initial Services Menu
            val defaultServices = listOf(
                ServiceEntity(
                    name = "Signature Haircut & Styling",
                    category = "Hair",
                    price = 150.0,
                    durationMinutes = 25,
                    description = "Custom precision hair cut with scalp massager wash and luxury gold pomade styling.",
                    isPopular = true
                ),
                ServiceEntity(
                    name = "Royal Beard Trim & Hot Towel",
                    category = "Beard",
                    price = 100.0,
                    durationMinutes = 20,
                    description = "Beard shape-up, razor line finish with herbal steam and soothing aftershave balm.",
                    isPopular = true
                ),
                ServiceEntity(
                    name = "Gold Glow Herbal Facial",
                    category = "Facial",
                    price = 350.0,
                    durationMinutes = 40,
                    description = "Deep cleansing charcoal scrub, gold sheet mask, and face massage for instant glow.",
                    isPopular = true
                ),
                ServiceEntity(
                    name = "Premium Hair Color & Highlights",
                    category = "Coloring",
                    price = 450.0,
                    durationMinutes = 50,
                    description = "Ammonia-free dark brown or natural black hair color treatment with conditioner.",
                    isPopular = false
                ),
                ServiceEntity(
                    name = "Relaxing Head & Shoulder Massage",
                    category = "Spa",
                    price = 200.0,
                    durationMinutes = 30,
                    description = "Warm navratna or almond oil deep tissue head, neck, and shoulder pressure point massage.",
                    isPopular = true
                ),
                ServiceEntity(
                    name = "Balale Special Groom Package",
                    category = "Combo",
                    price = 700.0,
                    durationMinutes = 90,
                    description = "Complete Haircut + Beard Grooming + Herbal Facial + Head Massage + Detan Scrub.",
                    isPopular = true
                )
            )
            dao.insertServices(defaultServices)

            // Initial Sample Visitors
            val now = System.currentTimeMillis()
            val sampleVisitors = listOf(
                VisitorEntity(
                    visitorName = "Rohan Gowda",
                    phoneNumber = "+91 98450 12345",
                    serviceRequested = "Signature Haircut & Styling",
                    status = "IN_CHAIR",
                    queueToken = 1,
                    checkInTime = now - (25 * 60 * 1000)
                ),
                VisitorEntity(
                    visitorName = "Praveen Kumar",
                    phoneNumber = "+91 97412 88990",
                    serviceRequested = "Royal Beard Trim & Hot Towel",
                    status = "WAITING",
                    queueToken = 2,
                    checkInTime = now - (10 * 60 * 1000)
                ),
                VisitorEntity(
                    visitorName = "Siddharth B.",
                    phoneNumber = "+91 94801 33456",
                    serviceRequested = "Gold Glow Herbal Facial",
                    status = "WAITING",
                    queueToken = 3,
                    checkInTime = now - (2 * 60 * 1000)
                ),
                VisitorEntity(
                    visitorName = "Vikram Sharma",
                    phoneNumber = "+91 98805 77123",
                    serviceRequested = "Signature Haircut & Styling",
                    status = "COMPLETED",
                    queueToken = 0,
                    checkInTime = now - (80 * 60 * 1000),
                    checkOutTime = now - (35 * 60 * 1000)
                )
            )
            for (v in sampleVisitors) {
                dao.insertVisitor(v)
            }
        }
    }
}
