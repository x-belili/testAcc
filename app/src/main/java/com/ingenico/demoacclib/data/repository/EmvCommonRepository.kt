package com.ingenico.demoacclib.data.repository

import android.util.Log
import com.ingenico.persistence.dao.EmvCommonDao
import com.ingenico.persistence.entity.EmvCommonEntity

class EmvCommonRepository (private val emvCommonDao: EmvCommonDao) {
    companion object {
        private val TAG = EmvCommonRepository::class.java.simpleName
    }

    fun getNumRecords() : Int{
        return emvCommonDao.getNumRows().also{
            Log.d(TAG, "getNumRecords = $it")
        }
    }

    fun get(): EmvCommonEntity {
        return emvCommonDao.get().also {
            Log.d(TAG, "get = $it")
        }
    }

    fun set(emvCommonEntity: EmvCommonEntity) {
        emvCommonDao.set(emvCommonEntity).also {
            Log.d(TAG, "set = $emvCommonEntity")
        }
    }

    fun delete(){
        emvCommonDao.deleteAll()
    }
}