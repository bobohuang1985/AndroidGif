package com.bobo.androidgif;

import android.graphics.Bitmap;

import com.waynejo.androidndkgif.GifEncoder;

import java.io.FileNotFoundException;

public class WaynejoEncoder implements GifEncoderInterface{
    GifEncoder mGifEncoder;
    public WaynejoEncoder() {
    }

    @Override
    public boolean createGIF(String file, int width, int height) {
        mGifEncoder = new GifEncoder();
        try {
            mGifEncoder.init(width, height, file, GifEncoder.EncodingType.ENCODING_TYPE_SIMPLE_FAST);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean addFrame(Bitmap bitmap, int intervalMs) {
        mGifEncoder.encodeFrame(bitmap, intervalMs);
        return true;
    }

    @Override
    public void finish() {
        mGifEncoder.close();
    }
}
