package com.mitsuru.insight

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mitsuru.insight.databinding.ActivityRecognitionBinding
import com.mitsuru.insight.ml.ModelBaru
import com.mitsuru.insight.util.rotateBitmap
import org.tensorflow.lite.support.image.TensorImage
import java.io.File

class RecognitionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecognitionBinding
    private var getFile: File? = null
    private lateinit var bitmap: Bitmap

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            bitmap = result

            binding.imageView2.setImageBitmap(result)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraXActivitty::class.java)
        launcherIntentCameraX.launch(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            toCamera.setOnClickListener { startCameraX() }
            analyze.setOnClickListener {
                var resized : Bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true)
                val model = ModelBaru.newInstance(this@RecognitionActivity)

                // Creates inputs for reference.
                val image = TensorImage.fromBitmap(resized)

                // Runs model inference and gets result.
                val outputs = model.process(image)
                val detectionResult = outputs.detectionResultList.get(0)

                // Gets result from DetectionResult.
                val location = detectionResult.scoreAsFloat;
                val category = detectionResult.locationAsRectF;
                val score = detectionResult.categoryAsString;

                binding.textView2.text = score

                // Releases model resources if no longer used.
                model.close()
            }
        }
    }


}