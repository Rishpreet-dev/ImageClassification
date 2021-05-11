package com.example.imageclassification.facenodes

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.AugmentedFaceNode

class JewelleryFaceNode(augmentedFace: AugmentedFace?, private val context: Context): AugmentedFaceNode(augmentedFace) {

    private var earNodeLeft: Node? = null
    private var earNodeRight: Node? = null
    private var noseNode: Node? = null
    private var neckNode:Node? = null

    companion object {
        enum class FaceRegion {
            LEFT_EAR,
            RIGHT_EAR,
            NOSE,
            NECK
        }
    }

    override fun onActivate() {
        super.onActivate()
        earNodeLeft = Node()
        earNodeLeft?.setParent(this)

        earNodeRight = Node()
        earNodeRight?.setParent(this)

        noseNode = Node()
        noseNode?.setParent(this)

        neckNode = Node()
        neckNode?.setParent(this)

        ModelRenderable.builder()
                .setSource(context, Uri.parse("earring v2.sfb"))
                .build()
                .thenAccept { uiRenderable: ModelRenderable ->
                    uiRenderable.isShadowCaster = false
                    uiRenderable.isShadowReceiver = false
                    earNodeLeft?.renderable = uiRenderable
                }
                .exceptionally { throwable: Throwable? ->
                    throw AssertionError(
                            "Could not create ui element",
                            throwable
                    )
                }

        ModelRenderable.builder()
                .setSource(context, Uri.parse("earring v2.sfb"))
                .build()
                .thenAccept { uiRenderable: ModelRenderable ->
                    uiRenderable.isShadowCaster = false
                    uiRenderable.isShadowReceiver = false
                    earNodeRight?.renderable = uiRenderable
                }
                .exceptionally { throwable: Throwable? ->
                    throw AssertionError(
                            "Could not create ui element",
                            throwable
                    )
                }

        ModelRenderable.builder()
                .setSource(context, Uri.parse("ringobj.sfb"))
                .build()
                .thenAccept { uiRenderable: ModelRenderable ->
                    uiRenderable.isShadowCaster = false
                    uiRenderable.isShadowReceiver = false
                    noseNode?.renderable = uiRenderable
                }
                .exceptionally { throwable: Throwable? ->
                    throw AssertionError(
                            "Could not create ui element",
                            throwable
                    )
                }

        ModelRenderable.builder()
                .setSource(context, Uri.parse("necklace.sfb"))
                .build()
                .thenAccept { uiRenderable: ModelRenderable ->
                    uiRenderable.isShadowCaster = false
                    uiRenderable.isShadowReceiver = false
                    neckNode?.renderable = uiRenderable
                }
                .exceptionally { throwable: Throwable? ->
                    throw AssertionError(
                            "Could not create ui element",
                            throwable
                    )
                }
    }

    private fun getRegionPose(region: FaceRegion) : Vector3? {
        val buffer = augmentedFace?.meshVertices
        if (buffer != null) {
            Log.i("VAL 000", buffer.get(0).toBigDecimal().toPlainString() + " " + buffer.get(1).toBigDecimal().toPlainString() + " " + buffer.get(2).toBigDecimal().toPlainString())
            return when (region) {
                FaceRegion.LEFT_EAR ->
                    /*Vector3(1 * buffer.get(323 * 3) + 0.005f//- buffer.get(345 * 3)
                           ,2 * buffer.get(323 * 3 + 1) - buffer.get(345 * 3 + 1),
                            2 * buffer.get(323 * 3 + 2) - buffer.get(345 * 3 + 2) - 0.015f)*/
                    Vector3(buffer.get(323 * 3) + 0.005f,
                            buffer.get(323 * 3 + 1)- 0.01f,
                            buffer.get(323 * 3 + 2) - 0.045f)
                FaceRegion.RIGHT_EAR ->
                    Vector3(buffer.get(93 * 3) - 0.005f,
                            buffer.get(93 * 3 + 1) - 0.01f,
                            buffer.get(93 * 3 + 2) - 0.045f)
                FaceRegion.NOSE ->
                    Vector3(buffer.get(220 * 3),
                            buffer.get(220 * 3 + 1),
                            buffer.get(220 * 3 + 2))
                FaceRegion.NECK ->
                    Vector3(buffer.get(0 * 3),
                            buffer.get(0 * 3 + 1),
                            buffer.get(0 * 3 + 2))
            }
        }
        return null
    }

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)
        augmentedFace?.let {
            getRegionPose(FaceRegion.LEFT_EAR)?.let {
                //eyeNodeLeft?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                earNodeLeft?.localPosition = Vector3(it.x + 0.003f, it.y - 0.05f, it.z + 0.017f)
                earNodeLeft?.localScale = Vector3(1.2f, 1.2f, 1.2f)
                //eyeNodeLeft?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), -10f)
            }

            getRegionPose(FaceRegion.RIGHT_EAR)?.let {
                //eyeNodeRight?.localPosition = Vector3(it.x, it.y - 0.01f, it.z + 0.015f)
                earNodeRight?.localPosition = Vector3(it.x - 0.003f, it.y - 0.05f, it.z + 0.017f)
                ////////eyeNodeRight?.localScale = Vector3(0.055f, 0.055f, 0.055f)
                earNodeRight?.localScale = Vector3(1.2f, 1.2f, 1.2f)
                //eyeNodeRight?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 10f)
            }

            getRegionPose(FaceRegion.NOSE)?.let {
                noseNode?.localPosition = Vector3(it.x - 0.001f, it.y - 0.004f , it.z - 0.01f)
                noseNode?.localScale = Vector3(0.007f, 0.004f, 0.007f)
                noseNode?.localRotation = Quaternion.axisAngle(Vector3(2.0f, 3.0f, 1.0f), 70f)
            }

            getRegionPose(FaceRegion.NECK)?.let {
                neckNode?.worldRotation = Quaternion.axisAngle(Vector3(1.0f, 0.0f, 0.0f), -30f)
                //neckNode?.localPosition = Vector3(it.x + 0.005f, it.y - 0.22f, it.z - 0.145f)
                neckNode?.localPosition = Vector3(it.x + 0.005f, it.y - 0.22f, it.z - 0.13f)
                neckNode?.localScale = Vector3(1.4f, 1f, 1.4f)
                //neckNode?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 0f)
            }
        }
    }
}