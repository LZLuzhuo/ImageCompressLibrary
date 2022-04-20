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

import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.luzhuo.lib_core.app.base.CoreBaseApplication;
import me.luzhuo.lib_core.data.hashcode.HashManager;
import me.luzhuo.lib_file.FileManager;
import me.luzhuo.lib_file.bean.ImageFileBean;

/**
 * 图片压缩
 * JPG 0.98M -> 135KB (√)
 * JPG 79.75KB -> <100K过滤 (√)
 * 长图 3.24MB -> 1.61MB (√)
 * PNG 1.46MB -> 467KB (√)
 * webp 385KB -> (忽略) (√)
 * Gif 668KB -> (忽略) (√)
 */
public class ImageCompress {
    /**
     * 最小压缩大小
     * 小于该大小的文件, 不会参与压缩
     * 单位: KB
     */
    private static final int LeastCompressSize = 100 << 10;
    private final FileManager fileManager = new FileManager(CoreBaseApplication.appContext);

    @Nullable
    public String compress(ImageFileBean fileBean) {
        if (fileBean == null) return null;
        if (needCopy(fileBean)) return checkCopyFile(fileBean);

        try {
            return new LuBanEngine(new InputStreamProvider() {
                @NonNull
                @Override
                public InputStream open() throws IOException {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return CoreBaseApplication.appContext.getContentResolver().openInputStream(fileBean.uriPath);
                    else return new FileInputStream(fileBean.urlPath);
                }

                @NonNull
                @Override
                public String getOutPath() {
                    String uuidName;
                    if (fileManager.needUri()) uuidName = fileBean.uriPath.toString();
                    else uuidName = fileBean.urlPath;
                    super.outPath = new FileManager(CoreBaseApplication.appContext).getCacheDirectory().getAbsolutePath() + File.separator + "compress" + File.separator + HashManager.getInstance().getUuid(uuidName);
                    return super.getOutPath();
                }

                @Override
                public int getWidth() {
                    return fileBean.width;
                }

                @Override
                public int getHeight() {
                    return fileBean.height;
                }

                @NonNull
                @Override
                public String getMimeType() {
                    return fileBean.mimeType;
                }
            }).compress();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    private boolean needCopy(ImageFileBean fileBean) {
        if (fileBean.size < LeastCompressSize) return true;
        if (fileBean.mimeType.equalsIgnoreCase("image/webp") || fileBean.mimeType.equalsIgnoreCase("image/gif")) return true;
        return false;
    }

    private String checkCopyFile(ImageFileBean fileBean) {
        if (fileManager.needUri()) {
            try {
                String outPath = new FileManager(CoreBaseApplication.appContext).getCacheDirectory().getAbsolutePath() + File.separator + HashManager.getInstance().getUuid(fileBean.uriPath.toString());
                fileManager.Stream2File(CoreBaseApplication.appContext.getContentResolver().openInputStream(fileBean.uriPath), outPath);
                return outPath;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return fileBean.urlPath;
        }
    }
}
