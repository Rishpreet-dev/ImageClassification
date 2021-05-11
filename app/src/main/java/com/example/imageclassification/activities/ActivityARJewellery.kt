package com.example.imageclassification.activities

import android.app.ActivityManager
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.imageclassification.R
import com.example.imageclassification.facenodes.JewelleryFaceNode
import com.example.imageclassification.fragments.FaceArFragment
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.activity_ar_glasses.*

class ActivityARJewellery: AppCompatActivity() {

    private lateinit var arFragment: FaceArFragment
    private var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    private lateinit var searchBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish()) {
            return
        }

        setContentView(R.layout.activity_ar_jewellery)
        searchBtn = findViewById(R.id.search_btn)

        searchBtn.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(SearchManager.QUERY, "jewellery")
                startActivity(intent)
            } catch (e: Exception) {
                throw e
            }
        }

        arFragment = face_fragment as FaceArFragment

        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener {
            sceneView.session
                    ?.getAllTrackables(AugmentedFace::class.java).let {
                        if (it != null) {
                            for (f in it) {
                                if(!faceNodeMap.containsKey(f)) {
                                    val faceNode = JewelleryFaceNode(f, this)
                                    faceNode.setParent(scene)
                                    faceNodeMap[f] = faceNode
                                }
                            }

                            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
                            val iter = faceNodeMap.entries.iterator()
                            while (iter.hasNext()) {
                                val entry = iter.next()
                                val face = entry.key
                                if (face.trackingState == TrackingState.STOPPED) {
                                    val faceNode = entry.value
                                    faceNode.setParent(null)
                                    iter.remove()
                                }
                            }
                        }
                    }
        }
    }

    private fun checkIsSupportedDeviceOrFinish() : Boolean {
        if (ArCoreApk.getInstance().checkAvailability(this) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Toast.makeText(this, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        val openGlVersionString =  (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
                ?.deviceConfigurationInfo
                ?.glEsVersion

        openGlVersionString?.let { _ ->
            if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
                Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                        .show()
                finish()
                return false
            }
        }
        return true
    }

    companion object {
        const val MIN_OPENGL_VERSION = 3.0
    }
}