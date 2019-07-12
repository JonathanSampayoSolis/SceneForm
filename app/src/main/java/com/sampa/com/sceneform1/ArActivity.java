package com.sampa.com.sceneform1;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArActivity extends AppCompatActivity {
	
	private static final double MIN_OPENGL_VERSION = 3.0;
	
	public static final String EXTRA_MODEL_NAME = "EXTRA_MODEL_NAME";
	
	public static final String EXTRA_MODEL_TEXTURE = "EXTRA_MODEL_TEXTURE";
	
	private ArFragment arFragment;
	private ModelRenderable mRender;
	
	@Override
	@SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar);
		
		if (!checkIsSupportedDeviceOrFinish(this)) {
			return;
		}
		
		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
		
		ModelRenderable.builder()
				.setSource(this, Uri.parse(getIntent().getStringExtra(EXTRA_MODEL_NAME)))
				.build()
				.thenAccept(renderable -> mRender = renderable)
				.exceptionally(
						throwable -> {
							Toast toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							return null;
						});
		
		arFragment.setOnTapArPlaneListener(
				(HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
					if (mRender == null) {
						return;
					}
					
					
					Anchor anchor = hitResult.createAnchor();
					AnchorNode anchorNode = new AnchorNode(anchor);
					anchorNode.setParent(arFragment.getArSceneView().getScene());
					
					
					TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
					andy.setParent(anchorNode);
					andy.setRenderable(mRender);
					andy.select();
				});
	}
	
	public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
		String openGlVersionString =
				((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
						.getDeviceConfigurationInfo()
						.getGlEsVersion();
		
		if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
			Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
					.show();
			activity.finish();
			return false;
		}
		return true;
	}
	
}
