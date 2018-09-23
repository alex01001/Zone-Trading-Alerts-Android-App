package com.stocksbuyalerts.alexey.zonetradingalerts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter <AlertAdapter.MyViewHolder>{

    final private AlertItemClickListener onClickListener;
    private LayoutInflater inflater;
    private List<Alert> data = Collections.emptyList();
    private Context context;

    private int wideScreen; // 1 = narrow screen; 2= regular; 3 = wide

    public interface AlertItemClickListener {
        void onAlertItemClick(int ClickedItemIndex, TextView symbol);
    }

    public AlertAdapter (Context tContext, AlertItemClickListener listener, int tWideScreen){
        context = tContext;
        inflater = LayoutInflater.from(tContext);
        onClickListener = listener;
        wideScreen=tWideScreen;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.alert_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Alert current = data.get(position);
        holder.alertDate.setText(current.getTimeStr());
        if(wideScreen==3) {
            String commpanyName = current.getName();
            if (!commpanyName.equals("")){
                holder.alertSymbol.setText(Html.fromHtml("<b>"+ current.getSymbol() + "</b> - " + commpanyName));
            }
            else {
                holder.alertSymbol.setText(Html.fromHtml("<b>"+ current.getSymbol() + "</b>"));
            }
            holder.alertPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP,34);
            double newWidth = (holder.tumbnail.getLayoutParams().width*1.2);
            holder.tumbnail.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.large_thumbnail_width);
            holder.tumbnail.requestLayout();
            holder.tumbnail.setVisibility(View.VISIBLE);
        }
        else {
            if(wideScreen==1)
                holder.tumbnail.setVisibility(View.GONE);
            else
                holder.tumbnail.setVisibility(View.VISIBLE);

            holder.alertSymbol.setText(current.getSymbol());

        }
        holder.alertPrice.setText("$"+current.getPrice());


        String thumbnailURL = current.getChartURL().replace(".html", ".png");

        Picasso.get()
                .load(thumbnailURL)
                .placeholder(R.drawable.thumbnail)
                .error(R.drawable.thumbnail)
                .into(holder.tumbnail);

    }
    public void setAlertData (List<Alert> mData){
        data = mData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(data==null) return 0;
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private TextView alertDate;
        private TextView alertSymbol;
        private TextView alertPrice;
        private ImageView tumbnail;
        private LinearLayout card;

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onAlertItemClick(clickedPosition,alertSymbol);
        }


        public MyViewHolder(View itemView) {
            super(itemView);
            alertDate = (TextView) itemView.findViewById(R.id.tv_alertDateTime);
            alertSymbol = (TextView) itemView.findViewById(R.id.tv_alertSymbol);
            alertPrice = (TextView) itemView.findViewById(R.id.tv_alertPrice);
            tumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            card = (LinearLayout) itemView.findViewById(R.id.linear_layout);
            itemView.setOnClickListener(this);
        }
    }
}