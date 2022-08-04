package ali.feyz_abadi.volley.app;

import android.content.Context;
import android.content.SharedPreferences;

public class spref {

    static SharedPreferences sharedPreferences;

    public static final String EMAIL="email";
    public static final String REMEMBER_ME="remember";

    public static SharedPreferences getSharedPreferences()
    {
        if(sharedPreferences == null)
        {
            sharedPreferences = application.getContext().getSharedPreferences(app.TAG, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }
}
