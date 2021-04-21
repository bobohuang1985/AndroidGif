package com.bobo.androidgif;

import android.graphics.Bitmap;

import com.bilibili.burstlinker.BurstLinker;
import com.bilibili.burstlinker.GifEncodeException;

public class BurstLinkerEncoder implements GifEncoderInterface{
    BurstLinker mBurstLinker;
    int mQuantizerType;
    public BurstLinkerEncoder(int quantizerType) {
        mBurstLinker = new BurstLinker();
        mQuantizerType = quantizerType;
    }
    @Override
    public boolean createGIF(String fileName, int outputWidth, int outputHeight) {
        try {
            mBurstLinker.init(outputWidth, outputHeight, fileName);
        } catch (GifEncodeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean addFrame(Bitmap bitmap, int intervalMs) {
        try {
            mBurstLinker.connect(bitmap, /*BurstLinker.MEDIAN_CUT_QUANTIZER*/mQuantizerType,
                    BurstLinker.NO_DITHER, 0, 0, intervalMs);
        } catch (GifEncodeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void finish() {
        mBurstLinker.release();
    }
}
