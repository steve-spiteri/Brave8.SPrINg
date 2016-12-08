package brave8.spring;

/**
 * Created by Salvatore on 2016-11-14.
 */

import android.app.Activity;
import android.content.Intent;

public class ThemeUtils {
    private static int cTheme;
    public final static int LIGHT = 0;
    public final static int DARK = 1;

    public static void changeToTheme(Activity activity, int theme)
    {
        cTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (cTheme)
        {
            default:
            case LIGHT:
                activity.setTheme(R.style.AppTheme);
                break;
            case DARK:
                activity.setTheme(R.style.AppTheme_alternate);
                break;
        }
    }
}
