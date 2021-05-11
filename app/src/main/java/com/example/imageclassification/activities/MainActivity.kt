package com.example.imageclassification.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.imageclassification.R
import com.example.imageclassification.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var selectImageBtn : Button
    private lateinit var makePredictionBtn : Button
    private lateinit var imgView : ImageView
    private lateinit var predictionVal : TextView
    private lateinit var bitmap: Bitmap
    private lateinit var tryOnBtn: Button
    private var imageSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        selectImageBtn = findViewById(R.id.select_img_btn)
        makePredictionBtn = findViewById(R.id.make_prediction_btn)
        imgView = findViewById(R.id.img_view)
        predictionVal = findViewById(R.id.prediction_val)
        tryOnBtn = findViewById(R.id.ar_btn)


        setActionBar(toolbar)
        actionBar?.title = "Image Prediction"

        tryOnBtn.setOnClickListener {
            if(imageSelected)
                startActivity(Intent(this, ActivityARGlasses::class.java))
            else
                Toast.makeText(this, "Please select image first",Toast.LENGTH_LONG).show()
        }

        tryOnBtn.setOnLongClickListener{
            if(imageSelected) {
                startActivity(Intent(this, ActivityARJewellery::class.java))
                true
            } else {
                Toast.makeText(this, "Please select image first",Toast.LENGTH_LONG).show()
                false
            }
        }

        val labels = application.assets.open("labels.txt").bufferedReader().use { it.readText() }.split("\n")

        selectImageBtn.setOnClickListener {
            Log.d("mssg", "button pressed")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 100)
        }

        makePredictionBtn.setOnClickListener {
            if (imageSelected) {
                val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
                val model = MobilenetV110224Quant.newInstance(this)
                val tBuffer = TensorImage.fromBitmap(resized)
                val byteBuffer = tBuffer.buffer
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
                inputFeature0.loadBuffer(byteBuffer)
                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                val max = getMax(outputFeature0.floatArray)
                predictionVal.text = labels[max]
                model.close()
            }
            else
                Toast.makeText(this, "Please select image first",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== RESULT_OK && requestCode==100) {
            imgView.setImageURI(data?.data)
            val uri: Uri? = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            imageSelected=true
        }
    }

    private fun getMax(arr:FloatArray) : Int{
        var ind = 0
        var min = 0.0f

        for(i in 0..1000)
        {
            if(arr[i] > min)
            {
                min = arr[i]
                ind = i
            }
        }
        return ind
    }
}