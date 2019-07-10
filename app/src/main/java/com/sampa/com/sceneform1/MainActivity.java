package com.sampa.com.sceneform1;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "ModelRenderingActivity";
	
	private SceneView sceneView;
	
	private boolean modelPlaced = false;
	
	private TransformationSystem transformationSystem;
	private ModelRenderable mRender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadModel("Chair.sfb");
		setContentView(R.layout.activity_main);
		sceneView = findViewById(R.id.scene_view);
		
		transformationSystem = new TransformationSystem(getResources().getDisplayMetrics(), new FootprintSelectionVisualizer());
		sceneView.getScene().addOnUpdateListener(this::onFrameUpdate);
		sceneView.getScene().addOnPeekTouchListener((hitTestResult, motionEvent) -> transformationSystem.onTouch(hitTestResult, motionEvent));
		
		sceneView.getScene().getCamera().setLocalPosition(new Vector3(0, 0.2f, 0));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			sceneView.resume();
		} catch (CameraNotAvailableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		sceneView.pause();
	}
	
	
	private void onFrameUpdate(FrameTime frameTime) {
		Log.d(TAG, "onFrameUpdate");
		if (!modelPlaced) {
			placeModel();
		}
	}
	
	
	private void placeModel() {
		if (mRender == null) {
			Log.d(TAG, "Renderable not yet ready");
			return;
		}
		
		//Node modelNode = new Node();
		TransformableNode modelNode = new TransformableNode(transformationSystem);
		modelNode.getRotationController().setEnabled(true);
		modelNode.getScaleController().setEnabled(true);
		modelNode.getTranslationController().setEnabled(false);
		modelNode.setRenderable(mRender);
		sceneView.getScene().addChild(modelNode);
		modelNode.setLocalPosition(new Vector3(0, -1f, -2f));
		Log.d(TAG, "Placed renderable");
		modelPlaced = true;
	}
	
	
	private void loadModel(String model) {
		CompletableFuture<ModelRenderable> future =
				ModelRenderable.builder()
						.setSource(this, Uri.parse(model))
						.build()
						.thenApply(renderable -> mRender = renderable)
						.exceptionally(throwable -> this.onException(model, throwable));
	}
	
	
	ModelRenderable onException(String id, Throwable throwable) {
		Log.d(TAG, "Unable to load renderable: " + id, throwable);
		return null;
	}
}
