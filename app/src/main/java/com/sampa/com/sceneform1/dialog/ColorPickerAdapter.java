package com.sampa.com.sceneform1.dialog;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.sampa.com.sceneform1.R;
import com.sampa.com.sceneform1.Texture;

import java.util.List;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ColorPickerViewHolder> {
    private List<Texture> textureList;
    private Observer<Texture> listener;
    private ImageView lastColorPicked;
    private int position;

    public ColorPickerAdapter(List<Texture> textureList, Observer<Texture> listener, int position) {
        this.textureList = textureList;
        this.listener = listener;
        this.position = position;
    }

    @NonNull
    @Override
    public ColorPickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_texture, parent, false);
        return new ColorPickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorPickerViewHolder holder, int position) {
        if ((this.position - 1) == position) {
            this.lastColorPicked = holder.checked;
            holder.checked.setVisibility(View.VISIBLE);
        }

        holder.view.getBackground().setColorFilter(Color.parseColor(textureList.get(position).getColor()), PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public int getItemCount() {
        return textureList.size();
    }

    class ColorPickerViewHolder extends RecyclerView.ViewHolder {
        ImageView view;
        ImageView checked;

        ColorPickerViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.circle);
            checked = itemView.findViewById(R.id.checked);
            itemView.setOnClickListener(v -> {
                if (lastColorPicked != null) {
                    lastColorPicked.setVisibility(View.GONE);
                }
                (lastColorPicked = checked).setVisibility(View.VISIBLE);
                listener.onChanged(textureList.get(getAdapterPosition()));
            });
        }
    }
}
