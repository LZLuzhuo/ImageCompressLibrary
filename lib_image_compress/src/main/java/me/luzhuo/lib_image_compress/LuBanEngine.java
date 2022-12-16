/* Copyright 2022 Luzhuo. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.luzhuo.lib_image_compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import me.luzhuo.lib_core.media.ImageManager;

/**
 * 鲁班压缩引擎
 */
public class LuBanEngine {
    // 图片压缩质量
    private static final int DEFAULT_QUALITY = 80;
    private InputStreamProvider srcStream;
    private ImageManager image = new ImageManager();

    public LuBanEngine(@NonNull InputStreamProvider srcStream) {
        this.srcStream = srcStream;
    }

    /**
     * 算法来源: https://github.com/Curzibn/Luban
     * 开源协议: Apache-2.0 License
     */
    private int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    @NonNull
    public String compress() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize(srcStream.getWidth(), srcStream.getHeight());

        Bitmap tagBitmap = BitmapFactory.decodeStream(srcStream.openInputStream(), null, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // 矫正角度
        Bitmap bitmap = image.rotateIfRequired(tagBitmap, srcStream.openInputStream());
        bitmap.compress(haveAlpha(srcStream.getMimeType()) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, DEFAULT_QUALITY, stream);
        bitmap.recycle();

        FileOutputStream fos = new FileOutputStream(srcStream.getOutPath());
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();
        srcStream.closeInputStream();

        return srcStream.getOutPath();
    }

    private boolean haveAlpha(String mimeType) {
        return mimeType.toLowerCase().equals("image/gif") || mimeType.toLowerCase().equals("image/webp") || mimeType.toLowerCase().equals("image/png");
    }

}
