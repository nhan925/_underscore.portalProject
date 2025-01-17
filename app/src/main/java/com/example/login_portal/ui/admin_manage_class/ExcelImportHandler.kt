package com.example.login_portal.ui.admin_manage_class

import android.content.Context
import android.net.Uri
import com.example.login_portal.R
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.IOException

class ExcelImportHandler {
    companion object {
        // Message keys
        private const val KEY_INVALID_FORMAT = "error_excel_invalid_format"
        private const val KEY_EMPTY_FILE = "error_excel_empty"
        private const val KEY_READING_FILE = "error_excel_reading"
        private const val KEY_MISSING_HEADERS = "error_excel_missing_headers"
        private const val KEY_INVALID_DATA = "error_excel_invalid_data"
        private const val KEY_CANNOT_OPEN = "error_excel_cannot_open"
        private const val KEY_PERIOD_RANGE = "error_excel_period_range"
        private const val KEY_MAX_ENROLLMENT = "error_excel_max_enrollment"
        private const val KEY_MISSING_CELL = "error_excel_missing_cell"
        private const val KEY_INVALID_CELL = "error_excel_invalid_cell"
        private const val KEY_IMPORT_SUCCESS = "msg_excel_import_success"
        private const val KEY_IMPORT_PARTIAL = "msg_excel_import_partial"
        private const val KEY_IMPORT_FAILED = "msg_excel_import_failed"

        // Required headers in correct order
        private val REQUIRED_HEADERS = listOf(
            "class_id",
            "class_name",
            "start_period",
            "end_period",
            "day_of_week",
            "room",
            "max_enrollment",
            "registration_period_id",
            "course_id",
            "lecturer_id"
        )

        fun importClassesFromExcel(
            context: Context,
            uri: Uri,
            onProgress: (Int, Int) -> Unit,
            onComplete: (Boolean, String?) -> Unit
        ) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    try {
                        val workbook = WorkbookFactory.create(inputStream)
                        val sheet = workbook.getSheetAt(0)

                        if (sheet.physicalNumberOfRows < 2) {
                            onComplete(false, context.getString(R.string.error_excel_empty))
                            return
                        }

                        val headerRow = sheet.getRow(0)
                        if (!validateHeaders(headerRow)) {
                            onComplete(false, context.getString(R.string.error_excel_missing_headers))
                            return
                        }

                        val totalRows = sheet.physicalNumberOfRows - 1
                        var successCount = 0
                        var failedRows = mutableListOf<Int>()

                        for (i in 1 until sheet.physicalNumberOfRows) {
                            val row = sheet.getRow(i)
                            try {
                                val classInfo = extractClassInfo(row, context)
                                if (classInfo != null) {
                                    ClassDAO.manageClass("add", classInfo) { success ->
                                        if (success) {
                                            successCount++
                                        } else {
                                            failedRows.add(i + 1)
                                        }

                                        onProgress(successCount, totalRows)

                                        if (successCount + failedRows.size == totalRows) {
                                            handleCompletion(context, successCount, totalRows, failedRows, onComplete)
                                        }
                                    }
                                } else {
                                    failedRows.add(i + 1)
                                    onProgress(successCount, totalRows)
                                }
                            } catch (e: Exception) {
                                failedRows.add(i + 1)
                                onProgress(successCount, totalRows)
                            }
                        }
                    } catch (e: Exception) {
                        onComplete(false, context.getString(R.string.error_excel_reading, e.message))
                    }
                } ?: run {
                    onComplete(false, context.getString(R.string.error_excel_cannot_open))
                }
            } catch (e: IOException) {
                onComplete(false, context.getString(R.string.error_excel_reading, e.message))
            }
        }

        private fun validateHeaders(headerRow: Row?): Boolean {
            if (headerRow == null) return false
            return REQUIRED_HEADERS.withIndex().all { (index, expectedHeader) ->
                headerRow.getCell(index)?.stringCellValue?.trim()?.lowercase() == expectedHeader
            }
        }

        private fun extractClassInfo(row: Row, context: Context): ClassInfo? {
            try {
                fun getCellStringValue(cell: Cell?): String {
                    return when {
                        cell == null -> throw Exception(context.getString(R.string.error_excel_missing_cell))
                        cell.cellType == CellType.STRING -> cell.stringCellValue.trim()
                        cell.cellType == CellType.NUMERIC -> cell.numericCellValue.toInt().toString()
                        else -> throw Exception(context.getString(R.string.error_excel_invalid_cell))
                    }
                }

                fun getCellIntValue(cell: Cell?): Int {
                    return when {
                        cell == null -> throw Exception(context.getString(R.string.error_excel_missing_cell))
                        cell.cellType == CellType.NUMERIC -> cell.numericCellValue.toInt()
                        cell.cellType == CellType.STRING -> cell.stringCellValue.trim().toInt()
                        else -> throw Exception(context.getString(R.string.error_excel_invalid_cell))
                    }
                }

                return ClassInfo(
                    classId = getCellStringValue(row.getCell(0)),
                    className = getCellStringValue(row.getCell(1)),
                    startPeriod = getCellIntValue(row.getCell(2)).also {
                        require(it in 1..10) { context.getString(R.string.error_excel_period_range) }
                    },
                    endPeriod = getCellIntValue(row.getCell(3)).also {
                        require(it in 1..10) { context.getString(R.string.error_excel_period_range) }
                    },
                    dayOfWeek = getCellStringValue(row.getCell(4)),
                    room = getCellStringValue(row.getCell(5)),
                    enrollment = 0,
                    maxEnrollment = getCellIntValue(row.getCell(6)).also {
                        require(it > 0) { context.getString(R.string.error_excel_max_enrollment) }
                    },
                    registrationPeriodId = getCellIntValue(row.getCell(7)),
                    courseId = getCellStringValue(row.getCell(8)),
                    lecturerId = getCellStringValue(row.getCell(9))
                )
            } catch (e: Exception) {
                return null
            }
        }

        private fun handleCompletion(
            context: Context,
            successCount: Int,
            totalRows: Int,
            failedRows: List<Int>,
            onComplete: (Boolean, String?) -> Unit
        ) {
            val message = when {
                successCount == totalRows -> context.getString(R.string.msg_excel_import_success, successCount)
                successCount > 0 -> context.getString(
                    R.string.msg_excel_import_partial,
                    successCount,
                    totalRows,
                    failedRows.joinToString(", ")
                )
                else -> context.getString(R.string.msg_excel_import_failed, failedRows.joinToString(", "))
            }
            onComplete(successCount > 0, message)
        }
    }
}
