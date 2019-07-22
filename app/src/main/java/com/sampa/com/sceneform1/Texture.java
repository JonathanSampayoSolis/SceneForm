package com.sampa.com.sceneform1;

public class Texture {
    private String color;
    private String texture;
    private int position;

    public Texture(String color, String texture, int position) {
        this.color = color;
        this.texture = texture;
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
