package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.OwnerAttendanceEntity
import com.example.data.SaloonRepository
import com.example.data.ServiceEntity
import com.example.data.ShopStatusEntity
import com.example.data.VisitorEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SaloonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SaloonRepository

    init {
        val dao = AppDatabase.getDatabase(application).saloonDao()
        repository = SaloonRepository(dao)

        viewModelScope.launch {
            repository.checkAndSeedDataIfNeeded()
        }

        // Ticker loop for live ticking timer (updates every second for real-time status duration)
        viewModelScope.launch {
            while (true) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    val latestShopStatus: StateFlow<ShopStatusEntity?> = repository.latestShopStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allShopStatusLogs: StateFlow<List<ShopStatusEntity>> = repository.allShopStatusLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestOwnerAttendance: StateFlow<OwnerAttendanceEntity?> = repository.latestOwnerAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allOwnerAttendance: StateFlow<List<OwnerAttendanceEntity>> = repository.allOwnerAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _visitorSearchQuery = MutableStateFlow("")
    val visitorSearchQuery: StateFlow<String> = _visitorSearchQuery.asStateFlow()

    private val _visitorStatusFilter = MutableStateFlow("ALL")
    val visitorStatusFilter: StateFlow<String> = _visitorStatusFilter.asStateFlow()

    val filteredVisitors: StateFlow<List<VisitorEntity>> = combine(
        repository.allVisitors,
        _visitorSearchQuery,
        _visitorStatusFilter
    ) { visitors, query, filter ->
        visitors.filter { visitor ->
            val matchesQuery = query.isBlank() ||
                visitor.visitorName.contains(query, ignoreCase = true) ||
                visitor.phoneNumber.contains(query, ignoreCase = true) ||
                visitor.serviceRequested.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "ALL" -> true
                else -> visitor.status == filter
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeVisitors: StateFlow<List<VisitorEntity>> = repository.activeVisitors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServices: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setVisitorSearchQuery(query: String) {
        _visitorSearchQuery.value = query
    }

    fun setVisitorStatusFilter(filter: String) {
        _visitorStatusFilter.value = filter
    }

    fun updateShopStatus(status: String, note: String) {
        viewModelScope.launch {
            repository.updateShopStatus(status, note)
        }
    }

    fun recordOwnerPunch(actionType: String, note: String) {
        viewModelScope.launch {
            repository.recordOwnerAttendance(actionType, note)
        }
    }

    fun addVisitor(name: String, phone: String, service: String) {
        viewModelScope.launch {
            repository.addVisitor(
                name = name.ifBlank { "Visitor" },
                phone = phone,
                service = service.ifBlank { "General Grooming" }
            )
        }
    }

    fun updateVisitorStatus(id: Int, newStatus: String) {
        viewModelScope.launch {
            repository.updateVisitorStatus(id, newStatus)
        }
    }

    fun deleteVisitor(id: Int) {
        viewModelScope.launch {
            repository.deleteVisitor(id)
        }
    }
}
