package com.example.login_portal.ui.admin_manage_class

import android.content.Context
import android.net.Uri
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.IOException

class ExcelImportHandler {
    companion object {
        // Error messages
        private const val ERROR_INVALID_FORMAT = "Invalid Excel format. Please check the template."
        private const val ERROR_EMPTY_FILE = "Excel file is empty."
        private const val ERROR_READING_FILE = "Error reading Excel file: "
        private const val ERROR_MISSING_HEADERS = "Missing required headers in Excel file."
        private const val ERROR_INVALID_DATA = "Invalid data in row "

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

                        // Validate basic sheet structure
                        if (sheet.physicalNumberOfRows < 2) {
                            onComplete(false, ERROR_EMPTY_FILE)
                            return
                        }

                        // Validate headers
                        val headerRow = sheet.getRow(0)
                        if (!validateHeaders(headerRow)) {
                            onComplete(false, ERROR_MISSING_HEADERS)
                            return
                        }

                        // Process data rows
                        val totalRows = sheet.physicalNumberOfRows - 1 // Excluding header
                        var successCount = 0
                        var failedRows = mutableListOf<Int>()

                        // Process each row
                        for (i in 1 until sheet.physicalNumberOfRows) {
                            val row = sheet.getRow(i)
                            try {
                                val classInfo = extractClassInfo(row)
                                if (classInfo != null) {
                                    ClassDAO.manageClass("add", classInfo) { success ->
                                        if (success) {
                                            successCount++
                                        } else {
                                            failedRows.add(i + 1) // Add 1 to account for 0-based index
                                        }

                                        onProgress(successCount, totalRows)

                                        // Check if all rows are processed
                                        if (successCount + failedRows.size == totalRows) {
                                            handleCompletion(successCount, totalRows, failedRows, onComplete)
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
                        onComplete(false, "$ERROR_READING_FILE${e.message}")
                    }
                } ?: run {
                    onComplete(false, "Could not open file")
                }
            } catch (e: IOException) {
                onComplete(false, "$ERROR_READING_FILE${e.message}")
            }
        }

        private fun validateHeaders(headerRow: Row?): Boolean {
            if (headerRow == null) return false

            return REQUIRED_HEADERS.withIndex().all { (index, expectedHeader) ->
                headerRow.getCell(index)?.stringCellValue?.trim()?.lowercase() == expectedHeader
            }
        }

        private fun extractClassInfo(row: Row): ClassInfo? {
            try {
                // Helper function to safely get cell values
                fun getCellStringValue(cell: Cell?): String {
                    return when {
                        cell == null -> throw Exception("Missing required cell")
                        cell.cellType == CellType.STRING -> cell.stringCellValue.trim()
                        cell.cellType == CellType.NUMERIC -> cell.numericCellValue.toInt().toString()
                        else -> throw Exception("Invalid cell type")
                    }
                }

                fun getCellIntValue(cell: Cell?): Int {
                    return when {
                        cell == null -> throw Exception("Missing required cell")
                        cell.cellType == CellType.NUMERIC -> cell.numericCellValue.toInt()
                        cell.cellType == CellType.STRING -> cell.stringCellValue.trim().toInt()
                        else -> throw Exception("Invalid cell type")
                    }
                }

                // Validate and extract data
                return ClassInfo(
                    classId = getCellStringValue(row.getCell(0)),
                    className = getCellStringValue(row.getCell(1)),
                    startPeriod = getCellIntValue(row.getCell(2)).also {
                        require(it in 1..10) { "Start period must be between 1 and 10" }
                    },
                    endPeriod = getCellIntValue(row.getCell(3)).also {
                        require(it in 1..10) { "End period must be between 1 and 10" }
                    },
                    dayOfWeek = getCellStringValue(row.getCell(4)),
                    room = getCellStringValue(row.getCell(5)),
                    enrollment = 0, // Always starts at 0 for new classes
                    maxEnrollment = getCellIntValue(row.getCell(6)).also {
                        require(it > 0) { "Max enrollment must be positive" }
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
            successCount: Int,
            totalRows: Int,
            failedRows: List<Int>,
            onComplete: (Boolean, String?) -> Unit
        ) {
            val message = when {
                successCount == totalRows ->
                    "Successfully imported all $successCount classes"
                successCount > 0 ->
                    "Partially successful: Imported $successCount out of $totalRows classes.\n" +
                            "Failed rows: ${failedRows.joinToString(", ")}"
                else ->
                    "Failed to import any classes.\n" +
                            "Failed rows: ${failedRows.joinToString(", ")}"
            }
            onComplete(successCount > 0, message)
        }
    }
}