package org.caojun.maintenancesystem.takepicture

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.caojun.maintenancesystem.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class TakePictureActivity : ComponentActivity() {

    private lateinit var ivPreview: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSave: Button
    private lateinit var rgWatermarkPosition: RadioGroup

    private var currentPhotoPath: String? = null
    private var originalBitmap: Bitmap? = null
    private var watermarkedBitmap: Bitmap? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PERMISSION_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)

        ivPreview = findViewById(R.id.ivPreview)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnSave = findViewById(R.id.btnSave)
        rgWatermarkPosition = findViewById(R.id.rgWatermarkPosition)

        btnTakePhoto.setOnClickListener {
            checkPermissionsAndTakePhoto()
        }

        btnSave.setOnClickListener {
            saveWatermarkedImage()
        }

        rgWatermarkPosition.setOnCheckedChangeListener { _, checkedId ->
            originalBitmap?.let {
                addWatermarkToBitmap(it)
            }
        }
    }

    private fun checkPermissionsAndTakePhoto() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val allPermissionsGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE)
        }
    }

    private fun openCamera() {
        // 1. 检查设备是否有可用的相机应用
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 2. 创建临时文件存储照片
        val photoFile: File? = try {
            createImageFile() // 自定义方法，创建图片文件
        } catch (ex: Exception) {
            Toast.makeText(this, "创建照片文件失败: ${ex.message}", Toast.LENGTH_LONG).show()
            null
        }
        if (photoFile == null) return

        // 3. 生成 FileProvider URI
        val photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )

        // 4. 设置 Intent 并启动相机（弹出选择器）
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val chooserIntent = Intent.createChooser(takePictureIntent, "选择相机应用")
        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE)
    }

    /**
     * 创建图片文件
     */
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // 保存文件路径供后续使用（可选）
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                originalBitmap = BitmapFactory.decodeFile(path)
                originalBitmap?.let {
                    addWatermarkToBitmap(it)
                }
            }
        }
    }

    private fun addWatermarkToBitmap(originalBitmap: Bitmap) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val width = originalBitmap.width
        val height = originalBitmap.height

        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val paint = Paint().apply {
            color = Color.WHITE
            textSize = 256f
            isAntiAlias = true
            style = Paint.Style.FILL
            setShadowLayer(5f, 0f, 0f, Color.BLACK)
        }

        val dateWidth = paint.measureText(currentDate)
        val timeWidth = paint.measureText(currentTime)
        val textHeight = paint.descent() - paint.ascent()
        val lineSpacing = 10f // 行间距
        val spacing = 20f
        // 确定两行文本的最大宽度
        val maxTextWidth = max(dateWidth, timeWidth)

        when (rgWatermarkPosition.checkedRadioButtonId) {
            // 左上角 - 左对齐
            R.id.rbTopLeft -> {
                val x = spacing
                val y = textHeight + spacing
                canvas.drawText(currentDate, x, y, paint)
                canvas.drawText(currentTime, x, y + textHeight + lineSpacing, paint)
            }

            // 右上角 - 右对齐
            R.id.rbTopRight -> {
                paint.textAlign = Paint.Align.RIGHT
                val x = width - spacing
                val y = textHeight + spacing
                canvas.drawText(currentDate, x, y, paint)
                canvas.drawText(currentTime, x, y + textHeight + lineSpacing, paint)
                paint.textAlign = Paint.Align.LEFT // 恢复默认对齐方式
            }

            // 左下角 - 左对齐
            R.id.rbBottomLeft -> {
                val x = spacing
                val y = height - spacing - lineSpacing
                canvas.drawText(currentTime, x, y, paint)
                canvas.drawText(currentDate, x, y - textHeight, paint)
            }

            // 右下角 - 右对齐
            else -> {
                paint.textAlign = Paint.Align.RIGHT
                val x = width - spacing
                val y = height - spacing - lineSpacing
                canvas.drawText(currentTime, x, y, paint)
                canvas.drawText(currentDate, x, y - textHeight, paint)
                paint.textAlign = Paint.Align.LEFT // 恢复默认对齐方式
            }
        }

        watermarkedBitmap = mutableBitmap
        ivPreview.setImageBitmap(mutableBitmap)
    }

    private fun saveWatermarkedImage() {
        watermarkedBitmap?.let { bitmap ->
            try {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val imageFile = File(storageDir, "watermarked_$timeStamp.jpg")

                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                }

                // 通知系统图库更新
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    imageFile.absolutePath,
                    imageFile.name,
                    imageFile.name
                )

                Toast.makeText(this, "图片已保存: ${imageFile.absolutePath}", Toast.LENGTH_LONG).show()

                // 调用系统分享菜单
                shareImage(imageFile)
            } catch (e: Exception) {
                Toast.makeText(this, "保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } ?: run {
            Toast.makeText(this, "请先拍照并添加水印", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openCamera()
            } else {
                Toast.makeText(this, "需要权限才能拍照", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareImage(imageFile: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "分享图片"))
    }
}