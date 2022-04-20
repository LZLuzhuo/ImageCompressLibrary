package me.luzhuo.image_compress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import me.luzhuo.lib_image_compress.InputStreamProvider;
import me.luzhuo.lib_image_compress.LuBanEngine;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission(null);
    }

    public void onClick(View view) throws IOException {
        Uri file = Uri.parse("content://media/external/file/623930");
        LuBanEngine luBanEngine = new LuBanEngine(new InputStreamProvider() {

            @NonNull
            @Override
            public InputStream open() throws IOException {
                return getContentResolver().openInputStream(file);
            }

            @Override
            public int getWidth() {
                return 3456;
            }

            @Override
            public int getHeight() {
                return 4608;
            }

            @NonNull
            @Override
            public String getMimeType() {
                return "image/jpeg";
            }
        });
        Log.e("TAG", "" + luBanEngine.compress()); // /storage/emulated/0/Android/data/me.luzhuo.image_compress/cache/compress/a1c7de9734c0490891471b73b91c8670
    }

    public void permission(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x01);
    }
}