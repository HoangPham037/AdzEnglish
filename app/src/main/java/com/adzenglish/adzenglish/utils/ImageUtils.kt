package com.adzenglish.adzenglish.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.ByteArrayOutputStream
import java.io.IOException


object ImageUtils {
    fun loadImageView(fragment: Fragment) {
        val arrayPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            ) else arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        Dexter.withContext(fragment.requireContext())
            .withPermissions(*arrayPermission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) {
                        val intent = Intent()
                        intent.action = Intent.ACTION_GET_CONTENT
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                        fragment.startActivityForResult(
                            intent, Constants.REQUEST_CODE_GET_IMAGE_FROM_GALLERY
                        )
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(fragment.requireContext(), "Error occurred!", Toast.LENGTH_SHORT)
                    .show()
            }.onSameThread().check()
    }
    fun takePhotoFromCamera(fragment: Fragment) {
        Dexter.withContext(fragment.requireContext())
            .withPermissions(Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        fragment.startActivityForResult(
                            cameraIntent,
                            Constants.REQUEST_CODE_TAKE_PHOTO_FROM_CAMERA
                        )
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(fragment.requireContext(), "Error occurred!", Toast.LENGTH_SHORT)
                    .show()
            }.onSameThread().check()
    }

    fun bitmapToString(bitmap: Bitmap): String? {
        try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun stringToBitmap(encodedString: String?): Bitmap? {
        if (encodedString == null) return null
        try {
            val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}