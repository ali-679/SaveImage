package ali.feyz_abadi.volley.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ali.feyz_abadi.volley.R;
import ali.feyz_abadi.volley.app.app;
import ali.feyz_abadi.volley.interfaces.IMultiAction;
import ali.feyz_abadi.volley.models.Images_models;
import ali.feyz_abadi.volley.views.ShowImage_activity;

public class Images_adapter extends RecyclerView.Adapter<Images_adapter.MyViewHolder> {

    List<Images_models> list;
    Context context;
    Activity activity;

    IMultiAction iMultiAction;

    public static Boolean multiAction =false;
    public static int count=0;

    public Images_adapter(Context context , List<Images_models> list,Activity activity,IMultiAction iMultiAction) {
        this.list = list;
        this.context=context;
        this.activity=activity;
        this.iMultiAction=iMultiAction;;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.item_images,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(app.BASE_URL+list.get(position).getImage()).into(holder.imageView);

        holder.checkBox.setVisibility(View.GONE);
        holder.checkBox.setChecked(false);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parent;
        ImageView imageView;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imageView);
            parent=itemView.findViewById(R.id.parent);
            checkBox=itemView.findViewById(R.id.checkBox);

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!multiAction) {
                        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, "image");
                        Intent intent = new Intent(context, ShowImage_activity.class);
                        intent.putExtra("link", list.get(getAdapterPosition()).getImage());
                        context.startActivity(intent, activityOptionsCompat.toBundle());
                    }
                   else if(multiAction)
                    {
                        if(count>0)
                        {
                            if(!list.get(getAdapterPosition()).getMultiAction())
                            {
                                list.get(getAdapterPosition()).setMultiAction(true);
                                iMultiAction.selected(++count,getAdapterPosition());
                                checkBox.setVisibility(View.VISIBLE);
                                checkBox.setChecked(true);
                            }
                            else
                            {
                                list.get(getAdapterPosition()).setMultiAction(false);
                                iMultiAction.deSelected(--count,getAdapterPosition());
                                checkBox.setVisibility(View.GONE);
                                checkBox.setChecked(false);
                            }
                        }
                        else
                        {
                            multiAction=false;
                        }
                    }
                }
            });

            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (count==0 || !multiAction)
                    {
                        iMultiAction.started();
                        iMultiAction.selected(++count,getAdapterPosition());

                        multiAction=true;

                        list.get(getAdapterPosition()).setMultiAction(true);
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setChecked(true);
                    }

                    return true;
                }
            });

        }
    }
}
