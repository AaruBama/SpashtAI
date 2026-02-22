package com.spashtai.navigator.data.database

import androidx.room.*
import com.spashtai.navigator.data.model.ReportHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM report_history ORDER BY uploadTimestamp DESC")
    fun getAllReports(): Flow<List<ReportHistory>>

    @Query("SELECT * FROM report_history WHERE id = :reportId")
    suspend fun getReportById(reportId: Long): ReportHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportHistory): Long

    @Update
    suspend fun updateReport(report: ReportHistory)

    @Delete
    suspend fun deleteReport(report: ReportHistory)

    @Query("DELETE FROM report_history WHERE id = :reportId")
    suspend fun deleteReportById(reportId: Long)
}
