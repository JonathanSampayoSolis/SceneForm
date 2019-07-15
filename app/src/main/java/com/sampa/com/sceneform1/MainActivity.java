package com.sampa.com.sceneform1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	private SceneView sceneView;

	private boolean modelPlaced = false;

	private TransformationSystem transformationSystem;
	private ModelRenderable mRender;

	private Toolbar toolbar;

	private boolean isRotating;

	private final String MODEL_NAME = "real_chair.sfb";
	
	private String modelTexture = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sceneView = findViewById(R.id.scene_view);

		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle("SKU12345");

		findViewById(R.id.btn_show_menu).setOnClickListener(v -> toolbar.setVisibility(View.VISIBLE));

		loadModel(MODEL_NAME);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_animation:
				if (!isRotating) {
					item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pause_white));
					sceneView.getScene().removeChild(sceneView.getScene().getChildren().get(2));
					isRotating = true;
					addRotateNode();
					Settings.ROTATIONSPEEDMULTIPLIER = 8.0f;
				} else {
					item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_3d_rotation_white));
					sceneView.getScene().removeChild(sceneView.getScene().getChildren().get(2));
					renderModel();
					Settings.ROTATIONSPEEDMULTIPLIER = 0f;
					isRotating = false;
				}
				break;
			case R.id.menu_ar:
				Intent intent = new Intent(this, ArActivity.class);
				intent.putExtra(ArActivity.EXTRA_MODEL_NAME, MODEL_NAME);
				intent.putExtra(ArActivity.EXTRA_MODEL_TEXTURE, modelTexture);

				startActivity(intent);
				break;
			case R.id.menu_hide:
				toolbar.setVisibility(View.GONE);
				break;
			case R.id.menu_texture:
				modelTexture = "Chair_BaseColor_2.png";
				Utils.setTextureToRender(this, mRender, modelTexture);
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	// region:: PRIVATE METHODS

	private void renderModel() {
		if (mRender == null) {
			return;
		}
		
		TransformableNode modelNode = new TransformableNode(transformationSystem);

		modelNode.getRotationController().setEnabled(true);
		modelNode.getScaleController().setEnabled(true);
		modelNode.getTranslationController().setEnabled(false);
		modelNode.setRenderable(mRender);

		sceneView.getScene().addChild(modelNode);
		modelNode.setLocalPosition(new Vector3(0, -.3f, -1));

		modelPlaced = true;
	}

	private void loadModel(@SuppressWarnings("SameParameterValue") String model) {
		ModelRenderable.builder()
				.setSource(this, Uri.parse(model))
				.build()
				.thenApply(renderable -> mRender = renderable)
				.exceptionally(throwable -> this.onException(model, throwable));

		applyGesture();
	}

	private void applyGesture() {
		transformationSystem = new TransformationSystem(getResources().getDisplayMetrics(), new FootprintSelectionVisualizer());
		sceneView.getScene().addOnUpdateListener(this::onFrameUpdate);
		sceneView.getScene().addOnPeekTouchListener(transformationSystem::onTouch);

		sceneView.getScene().getCamera().setLocalPosition(new Vector3(0, 0.2f, 0));
	}

	ModelRenderable onException(String id, Throwable throwable) {
		Log.d(TAG, "Unable to load renderable: " + id, throwable);
		return null;
	}

	private void addRotateNode() {

		RotatingNode rotatingNode = new RotatingNode(false, false, 0f);
		rotatingNode.setParent(sceneView.getScene());
		rotatingNode.setLocalPosition(new Vector3(0f, -.3f, -1));
		rotatingNode.setName("Chair");
		rotatingNode.setRenderable(mRender);

		rotatingNode.setDegreesPerSecond(Settings.ORBITDEGREESPERSECOND);
//        rotatingNode.setOnTapListener(((hitTestResult, motionEvent) -> {
//            if (isRotating) {
//                isRotating = false;
//                Settings.ROTATIONSPEEDMULTIPLIER = 0f;
//            } else {
//                isRotating = true;
//                Settings.ROTATIONSPEEDMULTIPLIER = 8.0f;
//            }
//        }));
	}

	// endregion

	// region:: REFERENCE METHODS

	private void onFrameUpdate(@SuppressWarnings("unused") FrameTime frameTime) {
		if (!modelPlaced) {
			renderModel();
		}
	}

	// endregion


}
