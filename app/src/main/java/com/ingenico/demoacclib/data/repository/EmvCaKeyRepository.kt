package com.ingenico.demoacclib.data.repository

import android.util.Log
import com.ingenico.persistence.dao.EmvCAkeyDao
import com.ingenico.persistence.entity.EmvCAKeyEntity

class EmvCaKeyRepository constructor(
    private val emvCAkeyDao: EmvCAkeyDao
) {
    companion object {
        private val TAG = EmvCaKeyRepository::class.java.simpleName
    }

    fun getNumRecords() : Int{
        return emvCAkeyDao.getNumRows().also{
            Log.d(TAG, "getNumRecords = $it")
        }
    }

    fun getById(id: Int): EmvCAKeyEntity {
        return emvCAkeyDao.getById(id).also {
            Log.d(TAG, "getById = $it")
        }
    }

    fun addRecord(emvCAKeyEntity: EmvCAKeyEntity){
        emvCAkeyDao.insert(emvCAKeyEntity).also {
            Log.d(TAG, "addRecord = $emvCAKeyEntity")
        }
    }

    fun deleteAll(){
        emvCAkeyDao.deleteAll()
    }
}