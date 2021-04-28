package com.bobo.androidgif;

import android.graphics.Bitmap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NbadalEncoderPlus implements GifEncoderInterface {
    AnimatedGifEncoderPlus mAnimatedGifEncoder;
    FileOutputStream mFileOutputStream;

    public NbadalEncoderPlus() {
    }

    @Override
    public boolean createGIF(String fileName, int outputWidth, int outputHeight) {
        mAnimatedGifEncoder = new AnimatedGifEncoderPlus();
        mAnimatedGifEncoder.setRepeat(0);
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
