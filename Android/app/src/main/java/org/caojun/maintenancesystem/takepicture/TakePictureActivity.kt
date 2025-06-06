package org.caojun.maintenancesystem.takepicture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.caojun.maintenancesystem.R
import org.caojun.maintenancesystem.watermar.WatermarkDrawer
import org.caojun.maintenancesystem.watermar.WatermarkDrawer.WatermarkPosition
import org.caojun.maintenancesystem.watermar.WatermarkGeneratorActivity
import org.caojun.maintenancesystem.watermar.WatermarkHelper
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class TakePictureActivity : ComponentActivity() {

    private lateinit var ivPreview: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSave: Button
    private lateinit var rgWatermarkPosition: RadioGroup
    private lateinit var btnWatermark: Button
    private lateinit var tvTextSize: TextView
    private lateinit var sbTextSize: SeekBar

    private var currentPhotoPath: String? = null
    private var originalBitmap: Bitmap? = null
    private var watermarkedBitmap: Bitmap? = null

    // 1. 替换 startActivityForResult
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            currentPhotoPath?.let { path ->
                originalBitmap = BitmapFactory.decodeFile(path)
                originalBitmap?.let {
                    addWatermarkToBitmap(it)
                }
            }
        }
    }

    // 2. 替换权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "需要相机权限才能拍照", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)

        ivPreview = findViewById(R.id.ivPreview)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnSave = findViewById(R.id.btnSave)
        rgWatermarkPosition = findViewById(R.id.rgWatermarkPosition)
        btnWatermark = findViewById(R.id.btnWatermark)
        tvTextSize = findViewById(R.id.tvTextSize)
        sbTextSize = findViewById(R.id.sbTextSize)

        btnTakePhoto.setOnClickListener {
            checkPermissionsAndTakePhoto()
        }

        btnSave.setOnClickListener {
            saveWatermarkedImage()
        }

        btnWatermark.setOnClickListener {
            startActivity(Intent(this, WatermarkGeneratorActivity::class.java))
        }

        rgWatermarkPosition.setOnCheckedChangeListener { _, checkedId ->
            originalBitmap?.let {
                addWatermarkToBitmap(it)
            }
        }

        sbTextSize.progress = watermarkDrawer.getTextSize().toInt()
        sbTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                watermarkDrawer.setTextSize(p1.toFloat())
                originalBitmap?.let {
                    addWatermarkToBitmap(it)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //TODO("Not yet implemented")
            }

        })
    }

    private val watermarkDrawer = WatermarkDrawer()

    private var watermark = ""
    override fun onResume() {
        super.onResume()
        loadWatermark()
    }

    private fun loadWatermark() {
        WatermarkHelper.load(this, this, object : WatermarkHelper.LoadListener {
            override fun onReset(watermark: String) {
                this@TakePictureActivity.watermark = watermark
            }

        })
    }

    private fun checkPermissionsAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                // 3. 直接启动权限请求（无需重写 onRequestPermissionsResult）
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: Exception) {
            Toast.makeText(this, "创建照片文件失败: ${ex.message}", Toast.LENGTH_LONG).show()
            null
        }
        photoFile?.let { file ->
            val photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            takePictureLauncher.launch(takePictureIntent)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun addWatermarkToBitmap(originalBitmap: Bitmap) {

        loadWatermark()

//        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
//
//        val width = originalBitmap.width
//        val height = originalBitmap.height

        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

//        val paint = Paint().apply {
//            color = Color.WHITE
//            textSize = 256f
//            isAntiAlias = true
//            style = Paint.Style.FILL
//            setShadowLayer(5f, 0f, 0f, Color.BLACK)
//        }

//        val dateWidth = paint.measureText(currentDate)
//        val timeWidth = paint.measureText(currentTime)
//        val textHeight = paint.descent() - paint.ascent()
//        val lineSpacing = 10f // 行间距
//        val spacing = 20f
        // 确定两行文本的最大宽度
//        val maxTextWidth = dateWidth.coerceAtLeast(timeWidth)

        val position =  when (rgWatermarkPosition.checkedRadioButtonId) {
            // 左上角 - 左对齐
            R.id.rbTopLeft -> {
                WatermarkPosition.TOP_LEFT
            }

            // 右上角 - 右对齐
            R.id.rbTopRight -> {
                WatermarkPosition.TOP_RIGHT
            }

            // 左下角 - 左对齐
            R.id.rbBottomLeft -> {
                WatermarkPosition.BOTTOM_LEFT
            }

            // 右下角 - 右对齐
            else -> {
                WatermarkPosition.BOTTOM_RIGHT
            }
        }
        watermarkDrawer.drawWatermark(canvas, watermark, position)
//        when (rgWatermarkPosition.checkedRadioButtonId) {
//            // 左上角 - 左对齐
//            R.id.rbTopLeft -> {
//                val x = spacing
//                val y = textHeight + spacing
//                canvas.drawText(currentDate, x, y, paint)
//                canvas.drawText(currentTime, x, y + textHeight + lineSpacing, paint)
//            }
//
//            // 右上角 - 右对齐
//            R.id.rbTopRight -> {
//                paint.textAlign = Paint.Align.RIGHT
//                val x = width - spacing
//                val y = textHeight + spacing
//                canvas.drawText(currentDate, x, y, paint)
//                canvas.drawText(currentTime, x, y + textHeight + lineSpacing, paint)
//                paint.textAlign = Paint.Align.LEFT // 恢复默认对齐方式
//            }
//
//            // 左下角 - 左对齐
//            R.id.rbBottomLeft -> {
//                val x = spacing
//                val y = height - spacing - lineSpacing
//                canvas.drawText(currentTime, x, y, paint)
//                canvas.drawText(currentDate, x, y - textHeight, paint)
//            }
//
//            // 右下角 - 右对齐
//            else -> {
//                paint.textAlign = Paint.Align.RIGHT
//                val x = width - spacing
//                val y = height - spacing - lineSpacing
//                canvas.drawText(currentTime, x, y, paint)
//                canvas.drawText(currentDate, x, y - textHeight, paint)
//                paint.textAlign = Paint.Align.LEFT // 恢复默认对齐方式
//            }
//        }

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