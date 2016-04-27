package se.anderssjobom.weathertracker;

/**
 * Created by Rickard on 2016-04-26.
 */
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/*

NonSwipeViewPager är en ViewPager som inte tillåter swipes

*/

public class NonSwipeableViewPager extends ViewPager {

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Tillåt inte swipes mellan fragment
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Tillåt inte swipes mellan fragment
        return false;
    }
}