package com.stocksbuyalerts.alexey.zonetradingalerts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter <AlertAdapter.MyViewHolder>{

    final private AlertItemClickListener onClickListener;
    private LayoutInflater inflater;
    List<Alert> data = Collections.emptyList();
    private Context context;

    public interface AlertItemClickListener {
        void onAlertItemClick(int ClickedItemIndex);
    }

    public AlertAdapter (Context tContext, AlertItemClickListener listener){
        context = tContext;
        inflater = LayoutInflater.from(tContext);
        onClickListener = listener;
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
        holder.alertSymbol.setText(current.getSymbol());
        holder.alertPrice.setText("at $"+current.getPrice());

//        URL posterURL = NetworkTools.buildPosterUrl(current.getPosterPath());
//        Picasso.with(context).load(posterURL.toString()).resize(185,277).centerCrop().into(holder.posterImg);
//
//        ViewGroup.LayoutParams lp;
//        lp = holder.posterImg.getLayoutParams();
//        lp.height = 270*Resources.getSystem().getDisplayMetrics().widthPixels/(2*185)-16;
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

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onAlertItemClick(clickedPosition);
        }


        public MyViewHolder(View itemView) {
            super(itemView);
            alertDate = (TextView) itemView.findViewById(R.id.tv_alertDateTime);
            alertSymbol = (TextView) itemView.findViewById(R.id.tv_alertSymbol);
            alertPrice = (TextView) itemView.findViewById(R.id.tv_alertPrice);
            itemView.setOnClickListener(this);
        }
    }
}