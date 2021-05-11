package com.example.imageclassification.activities

import android.app.ActivityManager
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.imageclassification.fragments.FaceArFragment
import com.example.imageclassification.R
import com.example.imageclassification.adapters.AdapterGlasses
import com.example.imageclassification.models.MGlass
import com.example.imageclassification.navigators.DiscreteScrollViewListener
import com.example.imageclassification.util.Status.CHANGE_MODEL
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.AugmentedFaceNode
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_ar_glasses.*

class ActivityARGlasses: AppCompatActivity(), DiscreteScrollViewListener {

    private lateinit var arFragment: FaceArFragment
    private var glasses: ArrayList<ModelRenderable> = ArrayList()
    private var faceRegionsRenderable: ModelRenderable? = null

    private var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    private var changeModel: Boolean = false

    private var adapterGlasses: AdapterGlasses? = null
    private var glassesData = ArrayList<MGlass>()
    private lateinit var searchBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!checkIsSupportedDeviceOrFinish()) {
            return
        }

        setContentView(R.layout.activity_ar_glasses)

        searchBtn = findViewById(R.id.search_btn)

        searchBtn.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(SearchManager.QUERY, "sunglasses")
                startActivity(intent)
            } catch (e: Exception) {
                throw e
            }
        }

        arFragment = face_fragment as FaceArFragment

        adapterGlasses = glasses_view?.let { AdapterGlasses(baseContext, ArrayList(), glasses, this, it) }
        glasses_view?.adapter = adapterGlasses
        glasses_view?.setHasFixedSize(true)
        glasses_view?.setItemTransformer(
            ScaleTransformer.Builder()
                .setMaxScale(1f)
                .setMinScale(0.7f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.BOTTOM)
                .build()
        )

        ModelRenderable.builder()
                .setSource(baseContext, Uri.parse("yellow_sunglasses.sfb"))
                .build()
                .thenAccept {
                    glasses.add(it)
                    faceRegionsRenderable = it
                    it.isShadowCaster = false
                    it.isShadowReceiver = false
                }

        ModelRenderable.builder()
            .setSource(baseContext, Uri.parse("sunglasses.sfb"))
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = true
                modelRenderable.isShadowReceiver = true
            }

        ModelRenderable.builder()
            .setSource(baseContext, Uri.parse("aviator.sfb"))
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = true
                modelRenderable.isShadowReceiver = true
            }

        getGlassesData()
        glassesData.let {
            adapterGlasses?.setDataSet(it)
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

    override fun onClick(requestType: String, data: Any?) {
        when(requestType) {
            CHANGE_MODEL -> {
                changeModel = !changeModel
                faceRegionsRenderable = glasses[data as Int]
            }
        }
    }

    private fun getGlassesData() {
        //adding dummy data
        glassesData.add(
            MGlass(
                "0",
                R.drawable.icon_catalogue_0.toString()
            )
        )
        glassesData.add(
            MGlass(
                "1",
                R.drawable.icon_catalogue_1.toString()
            )
        )
        glassesData.add(
            MGlass(
                "2",
                R.drawable.icon_catalogue_2.toString()
            )
        )
        glassesData.add(
            MGlass(
                "3",
                R.drawable.icon_catalogue_3.toString()
            )
        )
        glassesData.add(
            MGlass(
                "4",
                R.drawable.icon_catalogue_4.toString()
            )
        )
        glassesData.add(
            MGlass(
                "5",
                R.drawable.icon_catalogue_5.toString()
            )
        )
        glassesData.add(
            MGlass(
                "6",
                R.drawable.icon_catalogue_6.toString()
            )
        )
        glassesData.add(
            MGlass(
                "7",
                R.drawable.icon_catalogue_7.toString()
            )
        )
        glassesData.add(
            MGlass(
                "8",
                R.drawable.icon_catalogue_8.toString()
            )
        )
        glassesData.add(
            MGlass(
                "9",
                R.drawable.icon_catalogue_9.toString()
            )
        )
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