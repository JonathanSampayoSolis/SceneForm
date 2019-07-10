package com.sampa.com.sceneform1;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class MainActivity extends AppCompatActivity {
	
	SceneView sceneView;
	
	private Scene scene;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sceneView = findViewById(R.id.scene_view);
		scene = sceneView.getScene();
		
		renderObject("Chair.sfb");
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
	
	
	// region:: PRIVATE METHODS
	
	private void renderObject(@SuppressWarnings("SameParameterValue") String sfb) {
		ModelRenderable.builder()
				.setSource(this, Uri.parse(sfb))
				.build()
				.thenAccept(this::addNodeToScene)
				.exceptionally(throwable -> {
					Log.e("MainActivity", throwable.getMessage());
					throwable.printStackTrace();
					return null;
				});
	}
	
	// endregion
	
	// region:: REFERENCE METHODS
	
	private void addNodeToScene(ModelRenderable render) {
		Node node = new Node();
		
		node.setParent(scene);
		node.setLocalPosition(new Vector3(0f, -1f, -2f));
		node.setName("Chair");
		node.setRenderable(render);
		
		scene.addChild(node);
	}
	
	// endregion
	
}
