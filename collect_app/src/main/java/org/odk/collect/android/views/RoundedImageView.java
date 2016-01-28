package org.odk.collect.android.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

public class RoundedImageView extends ImageView {
    private  boolean isDefaultImage = true;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RoundedImageView(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);
    }

    public void setIsDefaultImage(boolean isDefaultImage) {
        this.isDefaultImage = isDefaultImage;
        this.invalidate();
    }

    public boolean isDefaultImage() {
        return isDefaultImage;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null)
            return;

        if (getWidth() == 0 || getHeight() == 0)
            return;

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (bitmap != null) {
            if (!isDefaultImage()) {
                Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap roundedBitmap = getCroppedBitmap(bitmapCopy, getWidth());

                canvas.drawBitmap(roundedBitmap, 0, 0, null);
            }
            else {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                canvas.drawBitmap(bitmap, getWidth() / 2 - width / 2 + 0.5f, getHeight() / 2 - height / 2 + 0.5f, null);
            }
        }
    }

    public static Bitmap getCroppedBitmap (Bitmap bitmap, int radius) {
        Bitmap scaledBitmap = bitmap;

        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);

        Bitmap output = Bitmap.createBitmap(scaledBitmap.getWidth(), scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        Context context = Collect.getContext();

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(context.getResources().getColor(R.color.colorRoundedImageBorder));
        canvas.drawCircle(radius / 2 + 0.5f, radius / 2 + 0.5f, radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, rect, paint);

        return output;
    }
}
