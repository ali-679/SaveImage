package ali.feyz_abadi.volley.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ali.feyz_abadi.volley.R;
import ali.feyz_abadi.volley.app.app;
import ali.feyz_abadi.volley.app.spref;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button button_signIn;
    EditText editText_email,editText_password;
    TextView textView_forgetPass,textView_signUp,textView_err_email,textView_err_password;
    CheckBox checkBox_remember;

    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        onClick();
    }

    private void init(){
        button_signIn = findViewById(R.id.button_signIn);
        editText_email = findViewById(R.id.editText_email);
        textView_err_email = findViewById(R.id.textView_err_email);
        editText_password = findViewById(R.id.editText_password);
        textView_err_password = findViewById(R.id.textView_err_password);
        textView_forgetPass = findViewById(R.id.textView_forgetPass);
        textView_signUp = findViewById(R.id.textView_signUp);
        checkBox_remember = findViewById(R.id.checkBox_remember);

        String email = spref.getSharedPreferences().getString(spref.EMAIL,"");
        editText_email.setText(email);


        requestQueue= Volley.newRequestQueue(this);
    }

    private void onClick()
    {
        button_signIn.setOnClickListener(this);
        textView_forgetPass.setOnClickListener(this);
        textView_signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == button_signIn) {

           // spref.getSharedPreferences().edit().putBoolean(spref.REMEMBER_ME,checkBox_remember.isChecked()).apply();

            String email = editText_email.getText().toString().trim();
            String password = editText_password.getText().toString().trim();
            if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))
            {
                textView_err_email.setVisibility(View.VISIBLE);
                textView_err_password.setVisibility(View.GONE);
            }
            else if(password.length()<8)
            {
                textView_err_email.setVisibility(View.GONE);
                textView_err_password.setVisibility(View.VISIBLE);
            }
            else
            {
                textView_err_email.setVisibility(View.GONE);
                textView_err_password.setVisibility(View.GONE);
                login(email,password);
            }
        }
        else if(v == textView_forgetPass)
        {
            startActivity(new Intent(this,ForgetPassActivitty.class));
            finish();
        }
        else if(v == textView_signUp)
        {
            startActivity(new Intent(this,RegisterActivity.class));
            finish();
        }
    }

    private void login(String email,String password)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, app.BASE_URL + "login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    switch (message)
                    {
                        case "login_ok":
                            spref.getSharedPreferences().edit().putBoolean(spref.REMEMBER_ME,checkBox_remember.isChecked()).apply();
                            spref.getSharedPreferences().edit().putString(spref.EMAIL,email).apply();
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case "login_failed":
                            app.failedToast(getString(R.string.toast_loginFailed));
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                app.failedToast(getString(R.string.toast_errNet));
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

}