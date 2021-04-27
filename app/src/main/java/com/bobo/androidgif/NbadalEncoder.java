package com.bobo.androidgif;

import android.graphics.Bitmap;

import com.nbadal.gif_encoder.AnimatedGifEncoder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NbadalEncoder implements GifEncoderInterface{
    AnimatedGifEncoder mAnimatedGifEncoder;
    FileOutputStream mFileOutputStream;
    public NbadalEncoder() {}
    @Override
    public boolean createGIF(String fileName, int outputWidth, int outputHeight) {
        mAnimatedGifEncoder = new AnimatedGifEncoder();
        try {
            mFileOutputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mAnimatedGifEncoder.start(mFileOutputStream);
        return true;
    }

    @Override
    public boolean addFrame(Bitmap bitmap, int intervalMs) {
        mAnimatedGifEncoder.setDelay(intervalMs);
        mAnimatedGifEncoder.addFrame(bitmap);
        return true;
    }

    @Override
    public void finish() {
        try {
            mFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
