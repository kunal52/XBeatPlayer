package com.techweblearn.musicbeat.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Util;

/**
 * Created by Kunal on 03-12-2017.
 */

public class DrawerLayout extends RecyclerView.Adapter<DrawerLayout.ViewHolder> {


    Context context;
    Drawable[]item_drawable;
    String[]item_name;
    OnCallBack callBack;

    public DrawerLayout(Context context) {
        this.context=context;
        item_name=new String[4];
        item_drawable=new Drawable[4];

        Log.d("Theme", String.valueOf(Util.getThemePrimaryColor(context)));
        item_name[0]="Home";
        item_name[1]="Library";
        item_name[2]="Setting";
        item_name[3]="About";

        int color=Util.getThemeAccentColor(context);

        item_drawable[0] = context.getResources().getDrawable(R.drawable.ic_home_black_24dp);
        item_drawable[0].setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        item_drawable[1] = context.getResources().getDrawable(R.drawable.ic_library_music_black_24dp);
        item_drawable[1].setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        item_drawable[2] = context.getResources().getDrawable(R.drawable.ic_settings_black_24dp);
        item_drawable[2].setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        item_drawable[3] = context.getResources().getDrawable(R.drawable.ic_help_black_24dp);
        item_drawable[3].setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.navigation_menu_item_recyclerview,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText(item_name[position]);
        holder.icon.setBackground(item_drawable[position]);

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setOnItemClicked(OnCallBack callBack)
    {
        this.callBack=callBack;
    }
    public interface OnCallBack
    {
         void onItemClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        public ViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            icon=itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onItemClicked(getAdapterPosition());
                }
            });
        }
    }
}
