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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import me.luzhuo.lib_core.app.base.CoreBaseApplication;
import me.luzhuo.lib_core.data.hashcode.HashManager;
import me.luzhuo.lib_file.FileManager;

/**
 * 输入流适配器
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class InputStreamProvider {
    private InputStream inputStream;
    protected String outPath = new FileManager(CoreBaseApplication.appContext).getCacheDirectory().getAbsolutePath() + File.separator + "compress" + File.separator + HashManager.getInstance().getUuid();

    /**
     * 打开文件流
     */
    @NonNull
    public InputStream openInputStream() throws IOException {
        inputStream = open();
        return inputStream;
    };

    @NonNull
    public abstract InputStream open() throws IOException;

    /**
     * 关闭文件流
     */
    public void closeInputStream() {
        if (inputStream == null) return;
        try {
            inputStream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            inputStream = null;
        }
    }

    /**
     * 输出文件的位置
     */
    @NonNull
    public String getOutPath() {

        try {
            final File localFile = new File(outPath);
            if (!localFile.getParentFile().exists()) localFile.getParentFile().mkdirs();
            if (!localFile.exists()) localFile.createNewFile();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return outPath;
    }

    /**
     * 图片的原始宽度
     */
    public abstract int getWidth();

    /**
     * 图片的原始高度
     */
    public abstract int getHeight();

    /**
     * 图片的 MimeType
     */
    @NonNull
    public abstract String getMimeType();
}
