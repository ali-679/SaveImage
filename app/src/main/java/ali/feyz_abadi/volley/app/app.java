package ali.feyz_abadi.volley.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ali.feyz_abadi.volley.R;

public class app {

    public static final String TAG = "volley";
    public static final String BASE_URL = "http://www.alifeyzabadi.ir/volley/";

    public static void log(String message) {
        Log.e(TAG,message);
    }
    public static void toast(String message) {
        Toast.makeText(application.getContext(),message, Toast.LENGTH_SHORT).show();
    }
    public static void successToast(String message)
    {
        Toast toast=new Toast(application.getContext());

        View view= LayoutInflater.from(application.getContext()).inflate(R.layout.success_toast,null);

        ImageView imageView=view.findViewById(R.id.imageView);
        TextView textView=view.findViewById(R.id.textView);

        imageView.setImageResource(R.drawable.ic_baseline_check_24);
        textView.setText(message);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,50);
        toast.show();

    }

    public static void failedToast(String message)
    {
        Toast toast=new Toast(application.getContext());

        View view= LayoutInflater.from(application.getContext()).inflate(R.layout.failed_toast,null);

        ImageView imageView=view.findViewById(R.id.imageView);
        TextView textView=view.findViewById(R.id.textView);

        imageView.setImageResource(R.drawable.ic_baseline_close_24);
        textView.setText(message);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,50);
        toast.show();
    }

    public static Boolean isConnected()
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) application.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
