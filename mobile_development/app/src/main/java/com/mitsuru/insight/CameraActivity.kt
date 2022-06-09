package com.mitsuru.insight

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.linkfirebase.FirebaseModelSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.mitsuru.insight.databinding.ActivityCameraBinding
import com.mitsuru.insight.util.Draw
import java.lang.Exception

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var objectDetector: ObjectDetector

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

        /*val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("InSight-object-detection", DownloadType.LOCAL_MODEL, conditions)
            .addOnCompleteListener {
                // Download complete. Depending on your app, you could enable the ML
                // feature, or switch from the local model to the remote model, etc.
            }*/

        val localModel = LocalModel.Builder()
            .setAssetFilePath("model.tflite")
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

        /*val downloadCondition = DownloadConditions.Builder()
            .requireWifi()
            .build()
        RemoteModelManager.getInstance().download(remoteModel, downloadCondition)
            .addOnCompleteListener {
                //success
            }

        RemoteModelManager.getInstance().isModelDownloaded(remoteModel)
            .addOnSuccessListener { isDownloaded ->
                val optionBuilder =
                    if (isDownloaded){
                        CustomObjectDetectorOptions.Builder(remoteModel)
                    } else {
                        CustomObjectDetectorOptions.Builder(localModel)
                    }

                val customObjectDetectorOptions = optionBuilder
                    .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                    .enableClassification()
                    .setClassificationConfidenceThreshold(0.5f)
                    .setMaxPerObjectLabelCount(3)
                    .build()


                objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)

            }*/
        binding.backButton.setOnClickListener {
            startActivity(Intent(this@CameraActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CameraActivity, MainActivity::class.java))
        finish()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera(cameraProvider: ProcessCameraProvider){
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

                objectDetector.process(processImage)
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
            }
        }

        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelect, imageAnalysis, preview)
    }

}