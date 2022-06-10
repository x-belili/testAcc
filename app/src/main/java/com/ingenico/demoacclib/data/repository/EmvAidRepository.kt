package com.ingenico.demoacclib.data.repository

import android.util.Log
import com.ingenico.persistence.dao.EmvAidDao
import com.ingenico.persistence.entity.EmvAidEntity
import javax.inject.Inject

class EmvAidRepository @Inject constructor(private val emvAidDao: EmvAidDao) {
    companion object {
        private val TAG = EmvAidRepository::class.java.simpleName
    }

    fun getNumRecords() : Int{
        return emvAidDao.getNumRows().also{
            Log.d(TAG, "getNumRecords = $it")
        }
    }

    fun getById(id: Int): EmvAidEntity {
        return emvAidDao.getById(id).also {
            Log.d(TAG, "getById = $it")
        }
    }

    fun addRecord(emvAidEntity: EmvAidEntity){
        emvAidDao.insert(emvAidEntity).also {
            Log.d(TAG, "addRecord = $emvAidEntity")
        }
    }

    fun deleteAll(){
        emvAidDao.deleteAll()
    }
}