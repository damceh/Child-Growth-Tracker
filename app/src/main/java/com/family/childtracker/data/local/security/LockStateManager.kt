package com.family.childtracker.data.local.security

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

/**
 * Manager for handling app lock/unlock state and auto-lock functionality.
 */
class LockStateManager(private val context: Context) {
    
    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()
    
    private var lastActiveTime: Long = System.currentTimeMillis()
    private var autoLockTimeoutMillis: Long = TimeUnit.MINUTES.toMillis(5)
    
    /**
     * Lock the app.
     */
    fun lock() {
        _isLocked.value = true
    }
    
    /**
     * Unlock the app.
     */
    fun unlock() {
        _isLocked.value = false
        updateLastActiveTime()
    }
    
    /**
     * Update the last active time to current time.
     */
    fun updateLastActiveTime() {
        lastActiveTime = System.currentTimeMillis()
    }
    
    /**
     * Set the auto-lock timeout in minutes.
     */
    fun setAutoLockTimeout(minutes: Int) {
        autoLockTimeoutMillis = TimeUnit.MINUTES.toMillis(minutes.toLong())
    }
    
    /**
     * Check if the app should be locked based on inactivity.
     * Returns true if the app should be locked.
     */
    fun shouldAutoLock(): Boolean {
        val currentTime = System.currentTimeMillis()
        val inactiveTime = currentTime - lastActiveTime
        return inactiveTime >= autoLockTimeoutMillis
    }
    
    /**
     * Check and apply auto-lock if needed.
     * Returns true if the app was locked.
     */
    fun checkAndApplyAutoLock(): Boolean {
        if (!_isLocked.value && shouldAutoLock()) {
            lock()
            return true
        }
        return false
    }
    
    /**
     * Reset the lock state (for testing or initialization).
     */
    fun reset() {
        _isLocked.value = false
        lastActiveTime = System.currentTimeMillis()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: LockStateManager? = null
        
        fun getInstance(context: Context): LockStateManager {
            return INSTANCE ?: synchronized(this) {
                val instance = LockStateManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
