package com.example.imageclassification

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.imageclassification.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ActivityImageClassification : AppCompatActivity() {

    private lateinit var selectImageButton : Button
    private lateinit var makePrediction : Button
    private lateinit var imgView : ImageView
    private lateinit var textView : TextView
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_classification)

        selectImageButton = findViewById(R.id.button)
        makePrediction = findViewById(R.id.button2)
        imgView = findViewById(R.id.imageView2)
        textView = findViewById(R.id.textView)

        val labels = application.assets.open("labels.txt").bufferedReader().use { it.readText() }.split("\n")

        selectImageButton.setOnClickListener {
            Log.d("mssg", "button pressed")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 100)
        }

        makePrediction.setOnClickListener {
            val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val model = MobilenetV110224Quant.newInstance(this)

            val tBuffer = TensorImage.fromBitmap(resized)
            val byteBuffer = tBuffer.buffer

// Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
            inputFeature0.loadBuffer(byteBuffer)

// Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val max = getMax(outputFeature0.floatArray)

            textView.text = labels[max]

// Releases model resources if no longer used.
            model.close()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imgView.setImageURI(data?.data)

        val uri : Uri? = data?.data
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    }

    private fun getMax(arr: FloatArray) : Int {
        var ind = 0
        var min = 0.0f

        for(i in 0..1000) {
            if(arr[i] > min) {
                min = arr[i]
                ind = i
            }
        }
        return ind
    }
}