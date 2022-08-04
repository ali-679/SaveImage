package ali.feyz_abadi.volley.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ali.feyz_abadi.volley.R;
import ali.feyz_abadi.volley.app.app;

public class ForgetPassActivitty extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layout_email, layout_password, layout_pinView;
    EditText editText_email, editText_password;
    TextView textView_err_email, textView_err_password;

    PinView pinView;

    Button button_continue, button_checkCode, button_goBack;

    String email;

    int codeRand;

    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        init();
        onClick();

    }

    private void init() {
        layout_email = findViewById(R.id.layout_email);
        layout_password = findViewById(R.id.layout_password);
        layout_pinView = findViewById(R.id.layout_pinView);
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        textView_err_email = findViewById(R.id.textView_err_email);
        textView_err_password = findViewById(R.id.textView_err_password);
        pinView = findViewById(R.id.pinView);
        button_continue = findViewById(R.id.button_continue);
        button_checkCode = findViewById(R.id.button_checkCode);
        button_goBack = findViewById(R.id.button_goBack);

        requestQueue = Volley.newRequestQueue(this);
    }

    private void onClick() {
        button_continue.setOnClickListener(this);
        button_checkCode.setOnClickListener(this);
        button_goBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == button_continue) {
            email = editText_email.getText().toString().trim();
            if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                textView_err_email.setVisibility(View.VISIBLE);
            } else {
                checkEmail();
            }
        } else if (v == button_checkCode) {
            String checkCode = pinView.getText().toString().trim();
            if (checkCode.equals(codeRand + "")) {// code ok
                layout_pinView.setVisibility(View.GONE);
                button_checkCode.setVisibility(View.GONE);

                layout_password.setVisibility(View.VISIBLE);
                button_goBack.setVisibility(View.VISIBLE);
            } else {
                pinView.setLineColor(getResources().getColor(R.color.colorRed));
            }
        } else if (v == button_goBack) {
            String password = editText_password.getText().toString().trim();
            if (password.length() < 8) {
                textView_err_password.setVisibility(View.VISIBLE);
            } else {
                textView_err_password.setVisibility(View.GONE);
                changePassword(password);
            }
        }
    }

        private void checkEmail ()
        {
            double rand = Math.random();
            codeRand = (int) (rand * 100000);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, app.BASE_URL + "forget_pass.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("message");

                        if (message.equals("email_notExists")) {
                            app.failedToast(getString(R.string.toast_notExists));
                        } else if (message.equals("email_ok")) {
                            layout_email.setVisibility(View.GONE);
                            button_continue.setVisibility(View.GONE);

                            layout_pinView.setVisibility(View.VISIBLE);
                            button_checkCode.setVisibility(View.VISIBLE);
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
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();

                    params.put("email", email);
                    params.put("code", codeRand + "");

                    return params;
                }
            };

            requestQueue.add(stringRequest);

        }

        private void changePassword(String password)
        {
            StringRequest stringRequest=new StringRequest(Request.Method.POST, app.BASE_URL + "change_pass.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String message = jsonObject.getString("message");
                        if(message.equals("OK"))
                        {
                            app.successToast(getString(R.string.toast_password_changed));
                            finish();
                        }
                        else if(message.equals("FAILED"))
                        {
                            app.failedToast(getString(R.string.toast_errNet));
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

                  Map<String , String> params= new HashMap<>();
                  params.put("email",email);
                  params.put("password",password);

                  return params;
                }
            };

            requestQueue.add(stringRequest);

        }

    }