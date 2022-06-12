package com.mitsuru.insight

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetector
import com.mitsuru.insight.databinding.ActivityCameraBinding
import com.mitsuru.insight.util.Draw

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var objectDetector: ObjectDetector

    companion object {
        const val TAG = "CameraActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            startCamera(cameraProvider = cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        supportActionBar?.hide()
/*
        val localModel = LocalModel.Builder()
            .setAssetFilePath("model_baru.tflite")
            .build()

        val remoteModel =
            CustomRemoteModel.Builder(FirebaseModelSource.Builder("InSight-object-detection").build())
            .build()

        val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()

        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)


        binding.backButton.setOnClickListener {
            startActivity(Intent(this@CameraActivity, MainActivity::class.java))
            finish()
        }*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CameraActivity, MainActivity::class.java))
        finish()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()

        val cameraSelect = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->

            val rotationDegrees = imageProxy.imageInfo.rotationDegrees

            val image = imageProxy.image


            if (image != null) {
                val processImage = InputImage.fromMediaImage(image, rotationDegrees)

                /*objectDetector.process(processImage)
                    .addOnSuccessListener { objects ->

                        if (binding.parent.childCount > 1) binding.parent.removeViewAt(1)

                        for (i in objects){
                            val element = Draw(context = this,
                                rect = i.boundingBox,
                                text = i.labels.firstOrNull()?.text ?: "Undefined")

                            binding.parent.addView(element)
                        }

                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        Log.d("MainActivity","error ${it.message}")
                        imageProxy.close()
                    }
            }*/
            }

            cameraProvider.bindToLifecycle(this as LifecycleOwner,
                cameraSelect,
                imageAnalysis,
                preview)
        }

        /*private fun runObjectDetection(bitmap : Bitmap){
        val image = TensorImage.fromBitmap(bitmap)

        val options = org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()

        val detector = org.tensorflow.lite.task.vision.detector.ObjectDetector.createFromFileAndOptions(
            this,
            "model_baru.tflite",
            options
        )

        val result = detector.detect(image)

        debugPrint(result)

    }

    private fun debugPrint(result: List<Detection>){
        for ((i, obj) in result.withIndex()){
            val box = obj.boundingBox

            Log.d(TAG, "Detected object: ${i} ")
            Log.d(TAG, "  boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")

            for ((j, category) in obj.categories.withIndex()) {
                Log.d(TAG, "    Label $j: ${category.label}")
                val confidence: Int = category.score.times(100).toInt()
                Log.d(TAG, "    Confidence: ${confidence}%")
            }
        }
    }*/

    }
}