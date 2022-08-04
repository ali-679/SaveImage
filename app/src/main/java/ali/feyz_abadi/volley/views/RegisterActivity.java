package ali.feyz_abadi.volley.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    EditText editText_email,editText_password;
    TextView textView_err_email,textView_err_password,textView_signIn;
    Button button_signUp;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        onClick();
    }

    private void init()
    {
        editText_email=findViewById(R.id.editText_email);
        editText_password=findViewById(R.id.editText_password);
        textView_err_email=findViewById(R.id.textView_err_email);
        textView_err_password=findViewById(R.id.textView_err_password);
        textView_signIn=findViewById(R.id.textView_signIn);
        button_signUp=findViewById(R.id.button_signUp);

        requestQueue = Volley.newRequestQueue(this);
    }

    private void onClick()
    {
        textView_signIn.setOnClickListener(this);
        button_signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if( v ==textView_signIn)
        {
            finish();
        }
        else if(v==button_signUp)
        {

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
                signUp(email,password);
            }

            //startActivity(new Intent(this,EmailValidationActivity.class));
        }
    }

    private void signUp(String email , String password){

        double rand= Math.random();
        int codeRand =(int)(rand* 100000);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, app.BASE_URL + "check_email.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);

                    String message= jsonObject.getString("message");

                    if(message.equals("email_exists")){
                        app.failedToast(getString(R.string.toast_emailExists));
                    }
                    else if(message.equals("email_ok"))
                    {
                        Intent intent=new Intent(RegisterActivity.this,EmailValidationActivity.class);
                        intent.putExtra("email",email);
                        intent.putExtra("password",password);
                        intent.putExtra("code",codeRand+"");
                        startActivity(intent);
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
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();

                params.put("email",email);
                params.put("code",codeRand+"");

                return params;
            }
        };

        requestQueue.add(stringRequest);

    }
}