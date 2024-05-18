package com.apps.kunalfarmah.realtimetictactoe.model;

import androidx.annotation.Keep;

public class ImagesBox {

    @Keep
    public ImagesBox(){

    }

    public int imgvw,value;

   public ImagesBox(int no, int val){
        this.imgvw=no;
        this.value =val;
    }
}
