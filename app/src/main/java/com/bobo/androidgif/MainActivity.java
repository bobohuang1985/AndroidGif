package com.bobo.androidgif;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AndroidGif";
    private static final int GIF_WIDTH = 530;
    private static final int GIF_HEIGHT = 405;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start).setOnClickListener(view -> {
            new Thread() {
                @Override
                public void run() {
                    List<String> files = getGifFrameFiles();
                    long costTime = 0;

                    List<Pair<GifEncoderInterface, String>> encoders = new ArrayList<>();
                    encoders.add(new Pair<>(new WaynejoEncoder(), "waynejo.gif"));
                    encoders.add(new Pair<>(new NbadalEncoder(), "nbadal.gif"));
                    for (Pair<GifEncoderInterface, String> encoder : encoders) {
                        costTime = 0;
                        File waynejoFile = new File(MainActivity.this.getFilesDir(), encoder.second);
                        waynejoFile.delete();
                        costTime += measureTimeMillis(object -> {
                            encoder.first.createGIF(waynejoFile.getAbsolutePath(), GIF_WIDTH, GIF_HEIGHT);
                        });
                        for (String file : files) {
                            Bitmap bitmap = decodeAssetImage(file);
                            costTime += measureTimeMillis(object -> {
                                encoder.first.addFrame(bitmap, 50);
                            });
                            bitmap.recycle();
                        }
                        costTime += measureTimeMillis(object -> {
                            encoder.first.finish();
                        });
                        Log.d(TAG, "encoder: " + encoder.first.getClass().getSimpleName()
                                + "; costTime: " + costTime);
                    }
                }
            }.start();
        });
    }

    private Bitmap decodeAssetImage(String name) {
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(name);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private List<String> getGifFrameFiles() {
        List<String> files = new ArrayList<>();
        for (int i = 0; i <= 297; i++) {
            files.add("image_" + i + ".png");
        }
        return files;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private long measureTimeMillis(Consumer consumer) {
        Long start = System.currentTimeMillis();
        consumer.accept(null);
        return System.currentTimeMillis() - start;
    }
}