package com.sampa.com.sceneform1;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.sampa.com.sceneform1.dialog.ColorPickerDialogFragment;

import java.util.Objects;
import java.util.UUID;

import io.blushine.android.ui.showcase.MaterialShowcaseSequence;
import io.blushine.android.ui.showcase.MaterialShowcaseView;
import io.blushine.android.ui.showcase.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final String MODEL_NAME = "real_chair.sfb";
    private SceneView sceneView;
    private boolean modelPlaced = false;
    private TransformationSystem transformationSystem;
    private ModelRenderable mRender;
    private Toolbar toolbar;
    private boolean isRotating;
    private String modelTexture = "";
    private String scenceBackground = "";
    private int configBackground = 2;
    private int configTexture = 4;

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

        new Handler().post(() -> {
            if (TutorialSharedPreferences.mustShow(this))
                startShowcase();
        });

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
                ColorPickerDialogFragment colorPickerDialogFragment = ColorPickerDialogFragment.newInstance(configBackground, configTexture);
                colorPickerDialogFragment.setCallback((background, texture, posBackground, posTexture) -> {
                    if (background != null) {
                        configBackground = posBackground;
                        sceneView.setBackgroundColor(Color.parseColor(background));
                    }

                    if (texture != null) {
                        modelTexture = texture;
                        configTexture = posTexture;
                        Utils.setTextureToRender(this, mRender, texture);
                    }
                });
                colorPickerDialogFragment.show(getSupportFragmentManager(), TAG);
                break;
            case R.id.menu_help:
                new Handler().post(this::startShowcase);
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
    }

    private void startShowcase() {
        ShowcaseConfig config = new ShowcaseConfig(MainActivity.this);
        config.setDelay(500);

        MaterialShowcaseSequence mSequence = new MaterialShowcaseSequence(MainActivity.this, UUID.randomUUID().toString());
        mSequence.setConfig(config);

        mSequence.addSequenceItem(
                new MaterialShowcaseView.Builder(MainActivity.this)
                        .setTarget((View) findViewById(R.id.menu_animation))
                        .setTitleText(getString(R.string.rotacion_automatica))
                        .setContentText(getString(R.string.rotacion_automatica_content))
                        .setDismissText(getString(R.string.siguiente))
                        .show()
        );

        mSequence.addSequenceItem(
                new MaterialShowcaseView.Builder(MainActivity.this)
                        .setTarget((View) findViewById(R.id.menu_ar))
                        .setTitleText(getString(R.string.ver_en_ra))
                        .setContentText(getString(R.string.ver_en_ra_content))
                        .setDismissText(getString(R.string.siguiente))
                        .show()
        );

        mSequence.addSequenceItem(
                new MaterialShowcaseView.Builder(MainActivity.this)
                        .setTitleText("Cambiar aspecto")
                        .setContentText("Cambia el aspecto del artículo en base a tus preferencias")
                        .setDismissText(getString(R.string.siguiente))
                        .setTarget((View) findViewById(R.id.menu_texture))
                        .show()
        );

        mSequence.addSequenceItem(
                new MaterialShowcaseView.Builder(MainActivity.this)
                        .setTarget((View) findViewById(R.id.view_center))
                        .setTitleText(getString(R.string.interactua_con_el_modelo))
                        .setContentText(getString(R.string.interactua_con_el_modelo_content))
                        .setDismissText(getString(R.string.siguiente))
                        .show()
        );

        mSequence.addSequenceItem(
                new MaterialShowcaseView.Builder(MainActivity.this)
                        .setTitleText(getString(R.string.interaccion_protegida))
                        .setContentText(getString(R.string.interaccion_protegida_content))
                        .setDismissText(getString(R.string.finalizar))
                        .show()
        );
    }


    // region:: REFERENCE METHODS

    private void onFrameUpdate(@SuppressWarnings("unused") FrameTime frameTime) {
        if (!modelPlaced) {
            renderModel();
        }
    }

    // endregion


}
