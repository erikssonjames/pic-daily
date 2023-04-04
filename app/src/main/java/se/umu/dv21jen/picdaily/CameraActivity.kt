package se.umu.dv21jen.picdaily

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import se.umu.dv21jen.picdaily.models.NewCollection
import java.io.ByteArrayOutputStream
import java.util.Arrays
import kotlin.math.abs

class CameraActivity : AppCompatActivity() {
    private val TAG = "CameraActivity"
    private val firebaseStorage: FirebaseStorage = Firebase.storage
    private val REQUEST_PERMISSION_CAMERA = 200
    private val STATE_PREVIEW = 0
    private val STATE_WAIT_LOCK = 1
    private var newCollection: NewCollection? = null
    private lateinit var topAppBar: MaterialToolbar
    private var captureState = STATE_PREVIEW
    private lateinit var cameraView: TextureView
    private lateinit var takePictureButton: Button
    private var cameraViewListener = object: TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            setUpCamera(width, height)
            connectCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        }
    }
    private var cameraDevice: CameraDevice? = null
    private var cameraDeviceStateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startPreview()
        }
        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            cameraDevice = null
        }
        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            cameraDevice = null
        }
    }
    private var backgroundHandlerThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private lateinit var cameraId: String
    private lateinit var previewSize: Size
    private var totalRotation: Int = 0
    private lateinit var imageSize: Size
    private lateinit var imageReader: ImageReader
    private val onImageAvailableListener =
        ImageReader.OnImageAvailableListener { reader ->
            if (reader != null) {
                val byteBuffer = reader.acquireLatestImage().planes[0].buffer
                val byteArray = ByteArray(byteBuffer.remaining())
                byteBuffer.get(byteArray)

                newCollection?.imageRef = newCollection?.userId + newCollection?.numPictures

                val storageRef = firebaseStorage.reference
                val pictureRef = storageRef.child(newCollection?.imageRef!!)

                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).rotate(90F)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val correctByteArray = stream.toByteArray()

                pictureRef.putBytes(correctByteArray)
                    .addOnSuccessListener {
                        it.metadata!!.reference?.downloadUrl?.addOnSuccessListener { uri ->
                            newCollection!!.downloadUrl = uri.toString()

                            val intent = Intent(this, ImagePreviewActivity::class.java)
                            intent.putExtra("NEW_COLLECTION", newCollection)
                            startActivity(intent)
                            this.finish()
                        }
                    }
            }
        }
    private lateinit var previewCaptureSession: CameraCaptureSession
    private var previewCaptureCallback = object: CameraCaptureSession.CaptureCallback() {
        private fun process(captureResult: CaptureResult){
            when(captureState){
                STATE_PREVIEW -> {

                }
                STATE_WAIT_LOCK -> {
                    captureState = STATE_PREVIEW
                    val afState = captureResult.get(CaptureResult.CONTROL_AF_STATE)
                    Log.d(TAG, "afState = $afState")
                    startStillCaptureRequest()
                }
            }
        }

        @Override
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)

            process(result)
        }
    }
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    companion object {
        private val ORIENTATIONS = SparseIntArray()
        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        newCollection = intent.extras?.getSerializable("NEW_COLLECTION") as NewCollection

        cameraView = findViewById(R.id.camera_view)
        takePictureButton = findViewById(R.id.take_picture)

        topAppBar = findViewById(R.id.app_bar)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        takePictureButton.setOnClickListener {
            Log.d(TAG, "Pressed take picture")
            lockFocus()
        }
    }

    @Override
    override fun onPause() {
        closeCamera()

        stopBackgroundThread()

        super.onPause()
    }

    @Override
    override fun onResume() {
        super.onResume()

        startBackgroundThread()
        Log.d("CameraActivity", "Started thread in resume")

        if(cameraView.isAvailable) {
            setUpCamera(cameraView.width, cameraView.height)
            connectCamera()
        } else {
            cameraView.surfaceTextureListener = cameraViewListener
        }
    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_PERMISSION_CAMERA){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,
                    "You need to accept the usage of the camera to use this feature", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpCamera(width: Int, height: Int) {
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager

        try {
            for(id in cameraManager.cameraIdList){
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }
                val map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val deviceOrientation = if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
                    display?.rotation!!
                } else {
                    windowManager.defaultDisplay.rotation
                }
                totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation)
                Log.d(TAG, "totalRotation = $totalRotation")
                val swapRotation = totalRotation == 90 || totalRotation == 270
                var rotateWidth = width
                var rotateHeight = height
                if(swapRotation) {
                    rotateWidth = height
                    rotateHeight = width
                }
                if (map != null) {
                    val previewOptimalSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java), rotateWidth, rotateHeight)
                    val imageOptimalSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotateWidth, rotateHeight)

                    if(previewOptimalSize == null || imageOptimalSize == null){
                        throw Error("Something went wrong, couldn't find the optimal size")
                    } else {
                        previewSize = previewOptimalSize
                        imageSize = imageOptimalSize
                    }
                }
                imageReader = ImageReader.newInstance(imageSize.width, imageSize.height, ImageFormat.JPEG, 1)
                imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler)
                cameraId = id
                return
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun connectCamera() {
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED){
            cameraManager.openCamera(cameraId, cameraDeviceStateCallback, backgroundHandler)
        } else {
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
                Toast.makeText(this,
                            "Permission to use the camera is required", Toast.LENGTH_SHORT).show()
            }
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        }
    }

    private fun startPreview() {
        val surfaceTexture = cameraView.surfaceTexture
        surfaceTexture?.setDefaultBufferSize(previewSize.width, previewSize.height)
        val previewSurface = Surface(surfaceTexture)

        if(cameraDevice != null){
            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(previewSurface)

            cameraDevice!!.createCaptureSession(
                Arrays.asList(previewSurface, imageReader.surface),
                object: CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if(cameraDevice == null) return

                        previewCaptureSession = session

                        try {
                            previewCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler)
                        } catch (e: CameraAccessException){
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                        Toast.makeText(applicationContext,
                                "Could not set up the camera preview", Toast.LENGTH_SHORT).show()
                    }
                }, null)
        }
    }

    private fun startStillCaptureRequest() {
        captureRequestBuilder =
            cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(imageReader.surface)
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, totalRotation)
        previewCaptureSession.capture(captureRequestBuilder.build(), null, null)
    }

    private fun closeCamera(){
        if(cameraDevice != null) {
            cameraDevice!!.close()
            cameraDevice = null;
        }
    }

    private fun startBackgroundThread() {
        backgroundHandlerThread = HandlerThread("Camera2 background thread")
        backgroundHandlerThread!!.start()
        backgroundHandler = Handler(backgroundHandlerThread!!.looper)
    }

    private fun stopBackgroundThread() {
        backgroundHandlerThread?.quitSafely()
        backgroundHandlerThread?.join()
        backgroundHandlerThread = null
        backgroundHandler = null
    }

    private fun sensorToDeviceRotation(cameraCharacteristics: CameraCharacteristics, deviceOrientation: Int): Int {
        val sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
        val deviceOrientation = (deviceOrientation + 45) / 90 * 90
        if (sensorOrientation != null) {
            return (sensorOrientation + deviceOrientation + 360) % 360
        } else {
            throw Error("Something went wrong")
        }
    }

    private fun chooseOptimalSize(choices: Array<Size>, width: Int, height: Int): Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = (height.toDouble() / width.toDouble())


        var optimalSize: Size? = null
        var minDiff = Double.MAX_VALUE

        for(choice in choices){
            val ratio = choice.width.toDouble() / choice.height
            if(abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if(abs(choice.height - height) < minDiff) {
                optimalSize = choice
                minDiff = abs(choice.height.toDouble() - height)
            }
        }

        if(optimalSize == null){
            minDiff = Double.MAX_VALUE
            for(choice in choices){
                if(abs(choice.height.toDouble() - height) < minDiff) {
                    optimalSize = choice
                    minDiff = abs(choice.height.toDouble() - height)
                }
            }
        }

        return optimalSize
    }

    private fun lockFocus(){
        captureState = STATE_WAIT_LOCK
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START)
        previewCaptureSession.capture(captureRequestBuilder.build(), previewCaptureCallback, backgroundHandler)
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
}