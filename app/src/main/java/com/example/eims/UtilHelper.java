package com.example.eims;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.eims.Leave;

public class UtilHelper {
    Context context;
    public UtilHelper (Context context){
        this.context = context;
    }

    public void createPopUpDialog(String title,String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void createPopUpDialogCloseActivity(String title,String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)context).finish();
                    }
                }).show();
    }

    public LinearLayout createLinearLayout(boolean isVerticalLayout,boolean isUsingSquareBorder){//create linear Layout with match parent,wrap content
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        if(isVerticalLayout){
            container.setOrientation(LinearLayout.VERTICAL);
        }
        if(isUsingSquareBorder){
            container.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangle));
        }
        return container;
    }

    public LinearLayout createLinearLayout(boolean isVerticalLayout,boolean isUsingSquareBorder,float weightSum){//create linear Layout with match parent,wrap content
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        if(isVerticalLayout){
            container.setOrientation(LinearLayout.VERTICAL);
        }
        if(isUsingSquareBorder){
            container.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangle));
        }
        container.setWeightSum(weightSum);
        return container;
    }

    public LinearLayout createLinearLayout(boolean isVerticalLayout,boolean isUsingSquareBorder,float weightSum,float layout_weight, boolean isLayoutWeightVertical){
        LinearLayout container = new LinearLayout(context);
        if(isVerticalLayout){
            container.setOrientation(LinearLayout.VERTICAL);
        }
        if(isLayoutWeightVertical){
            container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,layout_weight));
        }
        else{
            container.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,layout_weight));
        }

        if(isUsingSquareBorder){
            container.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangle));
        }
        container.setWeightSum(weightSum);
        return container;
    }

    public LinearLayout createLinearLayout(boolean isVerticalLayout,boolean isUsingSquareBorder,float layout_weight, boolean isLayoutWeightVertical){
        LinearLayout container = new LinearLayout(context);

        if(isVerticalLayout){
            container.setOrientation(LinearLayout.VERTICAL);
        }

        if(isLayoutWeightVertical){
            container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,layout_weight));
        }
        else{
            container.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,layout_weight));
        }

        if(isUsingSquareBorder){
            container.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangle));
        }
        return container;
    }

    public RelativeLayout createRelativeLayout(boolean isUsingSquareBorder){//create linear Layout with match parent,match parent content
        RelativeLayout container = new RelativeLayout(context);
        container.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        if(isUsingSquareBorder){
            container.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangle));
        }
        return container;
    }

    public RelativeLayout createRelativeLayout(boolean isUsingSquareBorder,float layout_weight, boolean isLayoutWeightVertical){
        RelativeLayout container = new RelativeLayout(context);
        if(isLayoutWeightVertical){
            container.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,0,layout_weight));
        }else{
            container.setLayoutParams(new LinearLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT,layout_weight));
        }
        if(isUsingSquareBorder){
            container.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangle));
        }
        return container;
    }

    public TextView createTextView(String textContent){
        TextView textView = new TextView(context);
        textView.setText(textContent);
        return textView;
    }

    public TextView createTextView(String textContent,float layout_weight){
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,layout_weight));
        textView.setText(textContent);
        return textView;
    }

    public ImageView createImageViewOnRelative(int drawableID, int width, int height){
        ImageView imageView = new ImageView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,height);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(drawableID);
        return imageView;
    }

}
