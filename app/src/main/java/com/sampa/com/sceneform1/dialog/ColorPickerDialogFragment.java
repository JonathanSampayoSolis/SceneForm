package com.sampa.com.sceneform1.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sampa.com.sceneform1.R;
import com.sampa.com.sceneform1.Texture;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerDialogFragment extends DialogFragment {
    private static final String ARG_BACKGROUND_COLOR = "ARG_BACKGROUND_COLOR";
    private static final String ARG_RENDER_TEXTURE = "ARG_RENDER_TEXTURE";
    private ColorPickerDialogFragment.Callback callback;
    private String TEXTURE_COLOR;
    private String BACKGROUND_COLOR;
    private int arg_background;
    private int arg_texture;

    public static ColorPickerDialogFragment newInstance(int backgroundColor, int renderTexture) {

        Bundle args = new Bundle();
        args.putInt(ARG_BACKGROUND_COLOR, backgroundColor);
        args.putInt(ARG_RENDER_TEXTURE, renderTexture);
        ColorPickerDialogFragment fragment = new ColorPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_color_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            arg_background = getArguments().getInt(ARG_BACKGROUND_COLOR, 2);
            arg_texture = getArguments().getInt(ARG_RENDER_TEXTURE, 4);
        }

        List<Texture> textureList = new ArrayList<>();
        textureList.add(new Texture("#353b84", "Chair_BaseColor_blue.png", 1));
        textureList.add(new Texture("#46763b", "Chair_BaseColor_green.png", 2));
        textureList.add(new Texture("#906096", "Chair_BaseColor_pink.png", 3));
        textureList.add(new Texture("#7e302d", "Chair_BaseColor_red.png", 4));
        textureList.add(new Texture("#291809", "Chair_BaseColor.png", 5));
        textureList.add(new Texture("#1c1c1c", "Chair_BaseColor_gray.png", 6));

        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(textureList, this::onTextureClick, arg_texture);

        RecyclerView recyclerViewTextures = view.findViewById(R.id.recyclerTextures);
        recyclerViewTextures.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewTextures.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTextures.setHasFixedSize(true);
        recyclerViewTextures.setAdapter(colorPickerAdapter);

        //BACKGROUND
        List<Texture> textureBackgroundList = new ArrayList<>();
        textureBackgroundList.add(new Texture("#FFFFFF", null, 1));
        textureBackgroundList.add(new Texture("#aaaaaa", null, 2));
        textureBackgroundList.add(new Texture("#000000", null, 3));

        ColorPickerAdapter colorPickerBackgroundAdapter = new ColorPickerAdapter(textureBackgroundList, this::onBackgroundClick, arg_background);

        RecyclerView recyclerViewBackground = view.findViewById(R.id.recyclerBackground);
        recyclerViewBackground.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewBackground.setItemAnimator(new DefaultItemAnimator());
        recyclerViewBackground.setHasFixedSize(true);
        recyclerViewBackground.setAdapter(colorPickerBackgroundAdapter);
        if (callback != null) {
            view.findViewById(R.id.btn_accept).setOnClickListener(v -> {
                callback.changeColors(BACKGROUND_COLOR, TEXTURE_COLOR, arg_background, arg_texture);
                dismiss();
            });
        }
        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());


    }

    private void onTextureClick(Texture texture) {
        TEXTURE_COLOR = texture.getTexture();
        arg_texture = texture.getPosition();
    }

    private void onBackgroundClick(Texture texture) {
        BACKGROUND_COLOR = texture.getColor();
        arg_background = texture.getPosition();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().setCancelable(false);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().setCancelable(false);
        }
    }

    public interface Callback {
        void changeColors(String background, String texture, int posBackground, int posTexture);
    }

}
