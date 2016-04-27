/*
 * Copyright (C) 2015 Lyft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.lijunguan.imgselector.cropimage.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkArgument;
import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;

/**

 */
public class CropView extends ImageView {

    public static final String TAG = "CropView";

    private static final int MAX_TOUCH_POINTS = 2;
    private TouchManager touchManager;

    private Paint viewportPaint = new Paint();
    private Paint bitmapPaint = new Paint();

    private Bitmap bitmap;
    private Matrix transform = new Matrix();


    public CropView(Context context) {
        super(context);
        initCropView(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initCropView(context, attrs);
    }

    void initCropView(Context context, AttributeSet attrs) {
        CropViewConfig config = CropViewConfig.from(context, attrs);

        touchManager = new TouchManager(MAX_TOUCH_POINTS, config);

        bitmapPaint.setFilterBitmap(true);
        viewportPaint.setColor(config.getViewportOverlayColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap == null) {
            return;
        }

        drawBitmap(canvas);
        drawOverlay(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        transform.reset();
        touchManager.applyPositioningAndScale(transform);

        canvas.drawBitmap(bitmap, transform, bitmapPaint);
    }

    private void drawOverlay(Canvas canvas) {
        final int viewportWidth = touchManager.getViewportWidth();
        final int viewportHeight = touchManager.getViewportHeight();
        final int left = (getWidth() - viewportWidth) / 2;
        final int top = (getHeight() - viewportHeight) / 2;

        canvas.drawRect(0, top, left, getHeight() - top, viewportPaint);
        canvas.drawRect(0, 0, getWidth(), top, viewportPaint);
        canvas.drawRect(getWidth() - left, top, getWidth(), getHeight() - top, viewportPaint);
        canvas.drawRect(0, getHeight() - top, getWidth(), getHeight(), viewportPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetTouchManager();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        final Bitmap bitmap = resId > 0
                ? BitmapFactory.decodeResource(getResources(), resId)
                : null;
        setImageBitmap(bitmap);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        final Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmap = bitmapDrawable.getBitmap();
        } else if (drawable != null) {
            bitmap = asBitmap(drawable, getWidth(), getHeight());
        } else {
            bitmap = null;
        }

        setImageBitmap(bitmap);
    }

    public Bitmap asBitmap(Drawable drawable, int minWidth, int minHeight) {
        final Rect tmpRect = new Rect();
        drawable.copyBounds(tmpRect);
        if (tmpRect.isEmpty()) {
            tmpRect.set(0, 0, Math.max(minWidth, drawable.getIntrinsicWidth()), Math.max(minHeight, drawable.getIntrinsicHeight()));
            drawable.setBounds(tmpRect);
        }
        Bitmap bitmap = Bitmap.createBitmap(tmpRect.width(), tmpRect.height(), Bitmap.Config.ARGB_8888);
        drawable.draw(new Canvas(bitmap));
        return bitmap;
    }

    @Override
    public void setImageBitmap(@Nullable Bitmap bitmap) {
        this.bitmap = bitmap;
        resetTouchManager();
        invalidate();
    }

    private void resetTouchManager() {
        final boolean invalidBitmap = bitmap == null;
        final int bitmapWidth = invalidBitmap ? 0 : bitmap.getWidth();
        final int bitmapHeight = invalidBitmap ? 0 : bitmap.getHeight();
        touchManager.resetFor(bitmapWidth, bitmapHeight, getWidth(), getHeight());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);

        touchManager.onEvent(event);
        invalidate();
        return true;
    }

    /**
     * Performs synchronous image cropping based on configuration.
     *
     * @return A {@link Bitmap} cropped based on viewport and user panning and zooming or <code>null</code> if no {@link Bitmap} has been
     * provided.
     */
    @Nullable
    public Bitmap crop() {
        if (bitmap == null) {
            return null;
        }

        final Bitmap src = bitmap;
        final Bitmap.Config srcConfig = src.getConfig();
        final Bitmap.Config config = srcConfig == null ? Bitmap.Config.ARGB_8888 : srcConfig;
        final int viewportHeight = touchManager.getViewportHeight();
        final int viewportWidth = touchManager.getViewportWidth();

        final Bitmap dst = Bitmap.createBitmap(viewportWidth, viewportHeight, config);

        Canvas canvas = new Canvas(dst);
        final int left = (getRight() - viewportWidth) / 2;
        final int top = (getBottom() - viewportHeight) / 2;
        canvas.translate(-left, -top);

        drawBitmap(canvas);

        return dst;
    }

    /**
     * Obtain current viewport width.
     *
     * @return Current viewport width.
     * <p>Note: It might be 0 if layout pass has not been completed.</p>
     */
    public int getViewportWidth() {
        return touchManager.getViewportWidth();
    }

    /**
     * Obtain current viewport height.
     *
     * @return Current viewport height.
     * <p>Note: It might be 0 if layout pass has not been completed.</p>
     */
    public int getViewportHeight() {
        return touchManager.getViewportHeight();
    }


    public static class CropRequest {

        private final CropView cropView;
        private Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        private int quality = CropViewConfig.DEFAULT_IMAGE_QUALITY;

        public CropRequest(@NonNull CropView cropView) {
            checkNotNull(cropView, "cropView == null");
            this.cropView = cropView;
        }

        /**
         * Compression format to use, defaults to {@link Bitmap.CompressFormat#JPEG}.
         *
         * @return current request for chaining.
         */
        public CropRequest format(@NonNull Bitmap.CompressFormat format) {
            checkNotNull(format, "format == null");
            this.format = format;
            return this;
        }

        /**
         * Compression quality to use (must be 0..100), defaults to {@value CropViewConfig#DEFAULT_IMAGE_QUALITY}.
         *
         * @return current request for chaining.
         */
        public CropRequest quality(int quality) {
            checkArgument(quality >= 0 && quality <= 100, "quality must be 0..100");
            this.quality = quality;
            return this;
        }


        //TODO 有必要的话 采用Callable + Future (将此方法在非主线程中执行)

        /**
         * 同步地将裁剪后的bitmap 写入到提供的file中，  如果需要会创建父目录
         *
         * @param file Must have permissions to write, will be created if doesn't exist or overwrite if it does.
         */
        public void into(@NonNull File file) throws IOException {
            final Bitmap croppedBitmap = cropView.crop();
            flushToFile(croppedBitmap, format, quality, file);
        }

        private void flushToFile(final Bitmap bitmap,
                                 final Bitmap.CompressFormat format,
                                 final int quality,
                                 final File file) throws IOException {

            OutputStream outputStream = null;
            try {
                file.getParentFile().mkdirs();
                outputStream = new FileOutputStream(file);
                bitmap.compress(format, quality, outputStream);
                outputStream.flush();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
