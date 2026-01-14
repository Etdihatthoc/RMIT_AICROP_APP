package com.example.ai_crop_doctor.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ai_crop_doctor.data.local.dao.DiagnosisDao
import com.example.ai_crop_doctor.data.local.entity.DiagnosisEntity

@Database(
    entities = [DiagnosisEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AICropDoctorDatabase : RoomDatabase() {
    abstract fun diagnosisDao(): DiagnosisDao
}
