package top.kikt.camerax.scopedstorage

import android.Manifest
import android.database.Cursor
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_media_scan.*

class MediaScanActivity : AppCompatActivity() {

    private val rxPermissions = RxPermissions(this)

    private val TAG = "MediaScanActivity"

    private val storeImageKeys = arrayOf(
        MediaStore.Images.Media.DISPLAY_NAME, // 显示的名字
        MediaStore.Images.Media.DATA, // 数据
        MediaStore.Images.Media.LONGITUDE, // 经度
        MediaStore.Images.Media._ID, // id
        MediaStore.Images.Media.MINI_THUMB_MAGIC, // id
        MediaStore.Images.Media.TITLE, // id
        MediaStore.Images.Media.BUCKET_ID, // dir id 目录
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // dir name 目录名字
//        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // dir name 目录名字
        MediaStore.Images.Media.WIDTH, // 宽
        MediaStore.Images.Media.HEIGHT, // 高
        MediaStore.Images.Media.DATE_TAKEN //日期
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_scan)

        bt_scan.setOnClickListener {
            Log.d(TAG, "准备申请权限")
            rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
                .subscribe {
                    if (it) {
                        Log.d(TAG, "申请权限成功")
                        scan()
                    } else {
                        Log.d(TAG, "申请失败")
                    }
                }
        }
    }

    private fun scan() {
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            storeImageKeys,
            null,
            null,
            MediaStore.Images.Media.DATE_TAKEN
        )

        cursor?.apply {
            val count = this.count
            Log.d(TAG, "scan count is $count")
            while (this.moveToNext()) {
                val date = this.getString(MediaStore.Images.Media.DATA)
                Log.d(TAG, "path : $date")
//                contentResolver.loadThumbnail()
                val width = this.getInteger(MediaStore.Images.Media.WIDTH) ?: 1024
                val height = this.getInteger(MediaStore.Images.Media.HEIGHT) ?: 1024
                Log.d(TAG, "width : $width")
                Log.d(TAG, "height : $height")

                var photoUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getString(MediaStore.Images.Media._ID)
                )

                Log.d(TAG, "version int = ${Build.VERSION.SDK_INT}")

//           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                photoUri = MediaStore.setRequireOriginal(photoUri)
//                    val stream = contentResolver.openInputStream(photoUri)
                val bitmap = contentResolver.loadThumbnail(photoUri, Size(width, height), null)
                iv_preview.setImageBitmap(bitmap)

                contentResolver.openInputStream(photoUri).use { stream ->
                    ExifInterface(stream).run {
                        val floatArrayOf = floatArrayOf(0f, 0f)
                        val latLongResult = this.getLatLong(floatArrayOf)
                        Log.d(TAG, "latLng request $latLongResult latlng = ${floatArrayOf.toList()}")
                    }
                }
//           }
            }
        }

        cursor?.close()
    }

    private fun Cursor.getString(columnName: String): String? {
        val columnIndex = getColumnIndex(columnName)
        if (columnIndex == -1) {
            return null
        }
        return this.getString(columnIndex)
    }

    private fun Cursor.getInteger(columnName: String): Int? {
        val columnIndex = getColumnIndex(columnName)
        if (columnIndex == -1) {
            return null
        }
        return this.getIntOrNull(columnIndex)
    }

    data class ImageEntity(val width: Int, val height: Int, val bitmap: Bitmap) {
        fun dispose() {
            bitmap.recycle()
        }
    }
}
