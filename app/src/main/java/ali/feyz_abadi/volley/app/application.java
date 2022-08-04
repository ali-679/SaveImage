package ali.feyz_abadi.volley.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;
import java.util.Locale;

public class application extends Application {

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        getLanguage();
    }

    public static Context getContext() {
        return context;
    }

    private void getLanguage() {
        String language = Locale.getDefault().getDisplayLanguage();

        String font = "";
        switch (language) {
            case "English":
                font = "fonts/pop.ttf";
                break;
            case "فارسی":
                font = "fonts/ir.ttf";
                break;
        }
        setFont(font);
    }

    private void setFont(String font) {
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), font);
        try {
            Field field = Typeface.class.getDeclaredField("MONOSPACE");
            field.setAccessible(true);
            field.set(null, typeface);
        } catch (Exception ignored) {
        }
    }

}
