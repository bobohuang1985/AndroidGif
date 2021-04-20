package com.bobo.androidgif;

import android.graphics.Bitmap;

public interface GifEncoderInterface {
    /**
     * Initialize gif encoder with parameters
     *
     * @param fileName - path to GIF file
     * @param outputWidth - width of GIF file
     * @param outputHeight - height of GIF file
     * @return true if successful, false otherwise
     */
    boolean createGIF(String fileName, int outputWidth, int outputHeight);
    /**
     * Encode frame to GIF file
     *
     * @param bitmap - the frame to be encoded
     * @param intervalMs - duration of frame. The interval between this frame and next one.
     * @return true if successful, false otherwise
     */
    boolean addFrame(Bitmap bitmap, int intervalMs);
    /**
     * Finalize GIF encoder
     */
    void finish();
}
