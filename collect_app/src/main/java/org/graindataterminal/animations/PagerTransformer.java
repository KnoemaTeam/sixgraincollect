package org.graindataterminal.animations;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by semyon on 07/31/15.
 */
public class PagerTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.65f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if (position < - 1) {
            page.setAlpha(0);
        }
        else if (position <= 1) {
            float scale = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scale) / 2;
            float horzMargin = pageWidth * (1 - scale) / 2;

            if (position < 0) {
                page.setTranslationX(horzMargin - vertMargin / 2);
            }
            else {
                page.setTranslationX(-horzMargin + vertMargin / 2);
            }

            page.setScaleX(scale);
            page.setScaleY(scale);

            page.setAlpha(MIN_ALPHA + (scale - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }
        else {
            page.setAlpha(0);
        }
    }
}
