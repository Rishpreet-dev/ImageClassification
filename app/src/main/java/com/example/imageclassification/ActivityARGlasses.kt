package com.example.imageclassification

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.activity_ar_glasses.*

class ActivityARGlasses: AppCompatActivity() {

    private lateinit var arFragment: FaceArFragment
    private var glasses: ArrayList<ModelRenderable> = ArrayList()
    private var faceRegionsRenderable: ModelRenderable? = null

    private var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    private var changeModel: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!checkIsSupportedDeviceOrFinish()) {
            return
        }

        setContentView(R.layout.activity_ar_glasses)
        arFragment = face_fragment as FaceArFragment

        ModelRenderable.builder()
                .setSource(baseContext, Uri.parse("yellow_sunglasses.sfb"))
                .build()
                .thenAccept {
                    glasses.add(it)
                    faceRegionsRenderable = it
                    it.isShadowCaster = false
                    it.isShadowReceiver = false
                }

        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener {
            if(faceRegionsRenderable != null) {
                sceneView.session
                        ?.getAllTrackables(AugmentedFace::class.java)?.let {
                            for (f in it) {
                                if(!faceNodeMap.containsKey(f)) {
                                    val faceNode = AugmentedFaceNode(f)
                                    faceNode.setParent(scene)
                                    faceNode.faceRegionsRenderable = faceRegionsRenderable
                                    faceNodeMap[f] = faceNode
                                } else if (changeModel) {
                                    faceNodeMap.getValue(f).faceRegionsRenderable = faceRegionsRenderable
                                }
                            }

                            changeModel = false
                            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
                            val iter = faceNodeMap.entries.iterator()
                            while(iter.hasNext()) {
                                val entry = iter.next()
                                val face = entry.key
                                if(face.trackingState == TrackingState.STOPPED) {
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
        if (ArCoreApk.getInstance()
                        .checkAvailability(this) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE
        ) {
            Toast.makeText(baseContext, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        val openGlVersionString = (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
                ?.deviceConfigurationInfo
                ?.glEsVersion

        openGlVersionString?.let {
            if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
                Toast.makeText(
                        baseContext,
                        "Sceneform requires OpenGL ES 3.0 or later",
                        Toast.LENGTH_LONG
                )
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