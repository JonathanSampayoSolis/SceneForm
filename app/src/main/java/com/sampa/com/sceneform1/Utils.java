package com.sampa.com.sceneform1;

import android.content.Context;
import android.net.Uri;

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Texture;

public final class Utils {
	
	public static void setTextureToRender(Context context, ModelRenderable render, String mTexture) {
		Texture.builder()
				.setSource(context, Uri.parse(mTexture))
				.setUsage(Texture.Usage.COLOR)
				.setSampler(
						Texture.Sampler.builder()
								.setMagFilter(Texture.Sampler.MagFilter.LINEAR)
								.setMinFilter(Texture.Sampler.MinFilter.LINEAR_MIPMAP_LINEAR)
								.build()
				).build()
				.thenAccept(texture -> render.getMaterial().setTexture("baseColor", texture))
				.exceptionally(t -> {
					t.printStackTrace();
					return null;
				});
	}
	
}
