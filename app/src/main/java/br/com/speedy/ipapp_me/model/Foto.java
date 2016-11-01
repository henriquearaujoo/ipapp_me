package br.com.speedy.ipapp_me.model;

import android.graphics.Bitmap;

/**
 * Created by Henrique on 11/08/2015.
 */
public class Foto {

    private int foto;

    private String path;

    private Bitmap bitmap;

    private String descricao;

    public Foto() {

    }

    public Foto(int foto) {
        this.foto = foto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
