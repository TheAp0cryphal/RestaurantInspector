//Inspiration for Custom Adapter at : https://www.youtube.com/watch?v=17NbUcEts9c"

package com.example.restaurantapp.SingletonSupport;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.MainActivity;
import com.example.restaurantapp.R;

import java.util.ArrayList;

import kotlin.reflect.KVisibility;

/*
    This is the adapter for the main list in the tabbed activity
    It is implimented as a recyclerview

 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private ArrayList<Item> mList;
    private OnItemClickListener mListener;
    private Context context;


    public interface OnItemClickListener {
        void OnClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView, img;
        public TextView textView1, tv3;
        public TextView textView2;

        @SuppressLint("ResourceAsColor")
        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imgView = itemView.findViewById(R.id.iv);
            img = itemView.findViewById(R.id.iv_dengji);
            textView1 = itemView.findViewById(R.id.tv1);
            textView2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnClick(position);
                        }
                    }
                }
            });

        }
    }

    public ItemAdapter(Context context, ArrayList<Item> List) {
        mList = List;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }



    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ResourceAsColor", "ObjectAnimatorBinding"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item current = mList.get(position);

        holder.imgView.setImageResource(current.getImageResource());
        holder.textView1.setText(current.getName());

        holder.textView2.setText(current.getDateDif());

        String issues=context.getResources().getString(R.string.issues);
        holder.tv3.setText("# " + current.getSumErrors() + " " + issues);

        ImageView imgV = holder.itemView.findViewById(R.id.imageView2);

        if (current.getFav()) {
            holder.imgView.animate().setStartDelay(0).rotationBy(360f).setDuration(1000).start();
            holder.itemView.setBackgroundResource(R.drawable.gradient);
            imgV = holder.itemView.findViewById(R.id.imageView2);
            imgV.setVisibility(View.VISIBLE);
            Log.i("R", current.getName());
        } else {
            imgV.setVisibility(View.INVISIBLE);
            holder.itemView.setBackgroundResource(R.drawable.whitegrad);
        }


        switch (current.getHazardLevel()) {
            case "High":

                holder.img.setBackground(context.getDrawable(R.drawable.ic_sentiment_very_dissatisfied_24px));
                break;
            case "Moderate":
                holder.img.setBackground(context.getDrawable(R.drawable.ic_sentiment_dissatisfied_24px));
                break;
            case "Low":
                holder.img.setBackground(context.getDrawable(R.drawable.ic_sentiment_very_satisfied_24px));
                break;
            default:
             /*   System.out.print(44444444);
                System.out.println(current.getHazardLevel());
*/
                holder.img.setBackground(context.getDrawable(R.drawable.ic_baseline_face_24));
                break;
        }

        String Last_Inspected_On=context.getResources().getString(R.string.Last_Inspected_On);
        holder.textView2.setText(Last_Inspected_On+ " " + current.getDateDif());
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
