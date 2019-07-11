package com.sampa.com.sceneform1;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sceneView = findViewById(R.id.scene_view);
		
		toolbar = findViewById(R.id.toolbar);
		
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle("SKU12345");
		
		findViewById(R.id.btn_show_menu).setOnClickListener(v -> toolbar.setVisibility(View.VISIBLE));
		
		loadModel("Chair.sfb");
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
				
				break;
			case R.id.menu_ar:
				
				break;
			case R.id.menu_hide:
				toolbar.setVisibility(View.GONE);
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
		modelNode.setLocalPosition(new Vector3(0, -1f, -2f));
		
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
	
	// endregion
	
	// region:: REFERENCE METHODS
	
	private void onFrameUpdate(@SuppressWarnings("unused") FrameTime frameTime) {
		if (!modelPlaced) {
			renderModel();
		}
	}
	
	// endregion
	
	
}
