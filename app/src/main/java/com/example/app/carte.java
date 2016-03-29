package com.example.app;

import android.graphics.drawable.Drawable;

/**
 * Created by tamburrelli on 05/08/14.
 */
public class carte {

    private String nome;
    private Drawable img;
    private int power;
    boolean selected = false;

    public carte(String nome, Drawable una, int pw) { //3 parametri
        this.nome = nome;
        this.img = una;
        this.power = pw;
    }

    public carte(String nome,Drawable una,int pw,boolean sele) { //4 parametri
        super();
        this.nome= nome;
        this.img= una;
        this.power = pw;
        this.selected = sele;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public int getpw() {
        return power;
    }

    public void setpw(int pw) {
        this.power = pw;
    }

    public Drawable getimg() {
        return img;
    }

    public void setimg(Drawable im) {
        this.img = im;
    }

    public boolean isSelected() {

        return selected;

    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
