package com.ashaai.navigator.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object PdfUtils {
    private const val TAG = "PdfUtils"

    /**
     * Convert all pages of PDF to list of bitmaps for analysis
     */
    fun pdfToAllBitmaps(context: Context, pdfUri: Uri, maxPages: Int = 10): List<Bitmap> {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null
        val bitmaps = mutableListOf<Bitmap>()

        try {
            // Copy PDF to cache file (PdfRenderer requires a file descriptor)
            val cacheFile = File(context.cacheDir, "temp_pdf_all_${System.currentTimeMillis()}.pdf")
            context.contentResolver.openInputStream(pdfUri)?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }

            parcelFileDescriptor = ParcelFileDescriptor.open(
                cacheFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            if (pdfRenderer.pageCount == 0) {
                Log.e(TAG, "PDF has no pages")
                return emptyList()
            }

            val totalPages = minOf(pdfRenderer.pageCount, maxPages)
            Log.d(TAG, "Converting $totalPages pages from PDF (total: ${pdfRenderer.pageCount})")

            // Render all pages (up to maxPages)
            for (pageIndex in 0 until totalPages) {
                val page = pdfRenderer.openPage(pageIndex)

                // Create bitmap with page dimensions
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.ARGB_8888
                )

                // Render page to bitmap
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)

                page.close()
                Log.d(TAG, "Converted page ${pageIndex + 1}: ${bitmap.width}x${bitmap.height}")
            }

            // Clean up cache file
            cacheFile.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Error converting PDF to bitmaps: ${e.message}", e)
            // Clean up any bitmaps created before error
            bitmaps.forEach { it.recycle() }
            return emptyList()
        } finally {
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        }

        return bitmaps
    }

    /**
     * Convert first page of PDF to bitmap for preview
     */
    fun pdfToBitmap(context: Context, pdfUri: Uri): Bitmap? {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null

        return try {
            // Copy PDF to cache file (PdfRenderer requires a file descriptor)
            val cacheFile = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
            context.contentResolver.openInputStream(pdfUri)?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }

            parcelFileDescriptor = ParcelFileDescriptor.open(
                cacheFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            if (pdfRenderer.pageCount == 0) {
                Log.e(TAG, "PDF has no pages")
                return null
            }

            // Render first page
            val page = pdfRenderer.openPage(0)

            // Create bitmap with page dimensions
            val bitmap = Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )

            // Render page to bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            page.close()

            Log.d(TAG, "PDF converted to bitmap: ${bitmap.width}x${bitmap.height}")

            // Clean up cache file
            cacheFile.delete()

            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error converting PDF to bitmap: ${e.message}", e)
            null
        } finally {
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        }
    }

    /**
     * Get specific page from PDF as bitmap
     */
    fun getPdfPage(context: Context, pdfUri: Uri, pageIndex: Int): Bitmap? {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null

        return try {
            val cacheFile = File(context.cacheDir, "temp_pdf_page_${System.currentTimeMillis()}.pdf")
            context.contentResolver.openInputStream(pdfUri)?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }

            parcelFileDescriptor = ParcelFileDescriptor.open(
                cacheFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            if (pageIndex >= pdfRenderer.pageCount || pageIndex < 0) {
                Log.e(TAG, "Invalid page index: $pageIndex (total pages: ${pdfRenderer.pageCount})")
                return null
            }

            val page = pdfRenderer.openPage(pageIndex)

            val bitmap = Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            cacheFile.delete()
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error getting PDF page: ${e.message}", e)
            null
        } finally {
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        }
    }

    /**
     * Get page count from PDF
     */
    fun getPdfPageCount(context: Context, pdfUri: Uri): Int {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null

        return try {
            val cacheFile = File(context.cacheDir, "temp_pdf_count_${System.currentTimeMillis()}.pdf")
            context.contentResolver.openInputStream(pdfUri)?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }

            parcelFileDescriptor = ParcelFileDescriptor.open(
                cacheFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val count = pdfRenderer.pageCount

            cacheFile.delete()
            count
        } catch (e: Exception) {
            Log.e(TAG, "Error getting PDF page count: ${e.message}", e)
            0
        } finally {
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        }
    }
}
