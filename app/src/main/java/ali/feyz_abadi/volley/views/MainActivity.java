package ali.feyz_abadi.volley.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ali.feyz_abadi.volley.R;
import ali.feyz_abadi.volley.adapters.Images_adapter;
import ali.feyz_abadi.volley.app.app;
import ali.feyz_abadi.volley.app.spref;
import ali.feyz_abadi.volley.interfaces.IMultiAction;
import ali.feyz_abadi.volley.models.Images_models;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IMultiAction {

    ImageView imageView_more;

    final int CAMERA_PERMISSION=110;
    final int CAMERA_CAPTURE=111;
    final int GALLERY_PERMISSION=112;
    final int GALLERY_PICK=113;

    RequestQueue requestQueue;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    List<Images_models> list;
    Images_adapter adapter;

    AlertDialog alertDialog;

    RelativeLayout layout;
    TextView textView_delete,textView_cancel;

    String ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        getImages();
        onClick();
    }

    private void getImages() {

        list.clear();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, app.BASE_URL + "get_images.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    int len = jsonArray.length();

                    for (int i=0; i < len ;i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        Images_models model=new Images_models();

                        model.setId(jsonObject.getString("id"));
                        model.setEmail(jsonObject.getString("email"));
                        model.setImage(jsonObject.getString("image"));
                        model.setUploaded_at(jsonObject.getString("uploaded_at"));

                        list.add(model);
                    }

                    adapter.notifyDataSetChanged();

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

                Map<String, String> params=new HashMap<>();
                params.put("email",spref.getSharedPreferences().getString(spref.EMAIL,""));
                return params;
            }
        };

        swipeRefreshLayout.setRefreshing(false);
        requestQueue.add(stringRequest);

    }

    private void init()
    {
        imageView_more=findViewById(R.id.imageView_more);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        recyclerView=findViewById(R.id.recyclerView);

        layout=findViewById(R.id.layout);
        textView_delete=findViewById(R.id.textView_delete);
        textView_cancel=findViewById(R.id.textView_cancel);

        requestQueue= Volley.newRequestQueue(this);

        list = new ArrayList<>();
        adapter =new Images_adapter(this,list,this,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImages();
            }
        });

    }

    private void onClick() {
        imageView_more.setOnClickListener(this);
        textView_delete.setOnClickListener(this);
        textView_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==imageView_more)
        {
            openBottomSheet();
        }
        else if(v==textView_delete)
        {
            deleteImg();
        }
        else if(v==textView_cancel)
        {
            cancelMultiAction();
        }
    }

    private void deleteImg() {

        ids ="-1";
        for (int i=0 ; i< list.size() ; i++)
        {
            if(list.get(i).getMultiAction()) {
                ids += "," + list.get(i).getId();
            }
        }

        StringRequest stringRequest=new StringRequest(Request.Method.POST, app.BASE_URL + "delete_images.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    String message= jsonObject.getString("message");

                    if(message.equals("delete_ok"))
                    {
                        cancelMultiAction();
                        getImages();
                    }
                    else if (message.equals("delete_failed"))
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

                Map<String,String> params = new HashMap<>();
                params.put("ids",ids);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void openBottomSheet() {
        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(this);
        View view= LayoutInflater.from(this).inflate(R.layout.bottom_sheet,findViewById(R.id.layout_bottomSheet));

        LinearLayout layout_fromCamera = view.findViewById(R.id.layout_fromCamera);
        LinearLayout layout_fromGallery = view.findViewById(R.id.layout_fromGallery);
        LinearLayout layout_logout = view.findViewById(R.id.layout_logout);
        LinearLayout layout_deleteAccount = view.findViewById(R.id.layout_deleteAccount);

        layout_fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    int res= ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)+ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(res== PackageManager.PERMISSION_GRANTED)
                    {
                        fromCamera();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},CAMERA_PERMISSION );
                    }
                }
                else
                {
                    fromCamera();
                }

                bottomSheetDialog.dismiss();
            }
        });

        layout_fromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    int res=ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(res==PackageManager.PERMISSION_GRANTED)
                    {
                        fromGallery();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY_PERMISSION);

                    }

                    bottomSheetDialog.dismiss();
                }
                else
                {
                    fromGallery();
                }
            }
        });

        layout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spref.getSharedPreferences().edit().putString("email","").apply();
                spref.getSharedPreferences().edit().putBoolean(spref.REMEMBER_ME,false).apply();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });

        layout_deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlert();

            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    private void showAlert() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.alert_delete_acc,null);

        builder.setView(view);

        TextView textView_cancel=view.findViewById(R.id.textView_cancel);
        TextView textView_deleteAccount=view.findViewById(R.id.textView_deleteAccount);

        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        textView_deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest=new StringRequest(Request.Method.POST, app.BASE_URL + "delete_acc.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if(message.equals("Delete_Ok"))
                            {

                                spref.getSharedPreferences().edit().putString(spref.EMAIL,"").apply();
                                spref.getSharedPreferences().edit().putBoolean(spref.REMEMBER_ME,false).apply();

                                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                app.successToast(getString(R.string.toast_accoun_deleted));
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

                        Map<String , String> params = new HashMap<>();
                        params.put("email",spref.getSharedPreferences().getString(spref.EMAIL,""));
                        
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

                alertDialog.dismiss();
            }
        });

        alertDialog=builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();


    }

    private void fromGallery()
    {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_PICK);
    }
    private void fromCamera()
    {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == CAMERA_PERMISSION)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
            {
                fromCamera();
            }
        }
        if (requestCode==GALLERY_PERMISSION)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                fromGallery();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode==RESULT_OK)
        {
            if(requestCode==CAMERA_CAPTURE)
            {
                Bitmap bitmap= (Bitmap) data.getExtras().get("data");
                upload(bitmap);
            }
            if(requestCode==GALLERY_PICK)
            {
                try {
                    Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                    upload(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upload(Bitmap bitmap) {
        int size= (int) ((bitmap.getHeight())*(812.0/bitmap.getWidth()));
        Bitmap decoded=Bitmap.createScaledBitmap(bitmap,812,size,true);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        decoded.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        byte[] bytes= byteArrayOutputStream.toByteArray();

        String image= Base64.encodeToString(bytes,Base64.DEFAULT);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, app.BASE_URL + "upload.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String message=jsonObject.getString("message");
                    if(message.equals("OK"))// uploaded
                    {
                        app.successToast(getString(R.string.toast_imageUpload));
                        getImages();
                    }
                    else if(message.equals("FAILED"))// NOT uploaded
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
                Map<String , String> params=new HashMap<>();
                params.put("email", spref.getSharedPreferences().getString(spref.EMAIL,""));
                params.put("image",image);
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    @Override
    public void started() {
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void selected(int count, int position) {
        list.get(position).setMultiAction(true);
    }

    @Override
    public void deSelected(int count, int position) {
        list.get(position).setMultiAction(false);

        if(count == 0)
        {
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        if(layout.getVisibility()==View.VISIBLE)
        {
            cancelMultiAction();
        }
        else
        {
            super.onBackPressed();
        }


    }

    private void cancelMultiAction()
    {
        layout.setVisibility(View.GONE);
        Images_adapter.count=0;
        Images_adapter.multiAction=false;
        adapter.notifyDataSetChanged();
    }

}