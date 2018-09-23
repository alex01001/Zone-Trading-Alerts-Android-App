package com.stocksbuyalerts.alexey.zonetradingalerts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter <NewsAdapter.MyViewHolder>{

    final private NewsItemClickListener onClickListener;
    private LayoutInflater inflater;
    List<News> data = Collections.emptyList();
    private Context context;

    private boolean wideScreen;

    public interface NewsItemClickListener {
        void onNewsItemClick(int ClickedItemIndex);
    }

    public NewsAdapter (Context tContext, NewsItemClickListener listener, boolean tWideScreen){
        context = tContext;
        inflater = LayoutInflater.from(tContext);
        onClickListener = listener;
        wideScreen=tWideScreen;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.news_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        News current = data.get(position);
        String pd = current.getPubDate();
        pd = pd.substring(0,pd.length()-6);
        holder.newsDate.setText(pd);
        holder.newsTitle.setText(current.getTitle());
        holder.newsDescription.setText(current.getDescription());
    }
    public void setNewsData (List<News> mData){
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
        private TextView newsDate;
        private TextView newsTitle;
        private TextView newsDescription;

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onNewsItemClick(clickedPosition);
        }

        public MyViewHolder(View itemView) {
            super(itemView);
            newsDate = (TextView) itemView.findViewById(R.id.tv_newsDate);
            newsTitle = (TextView) itemView.findViewById(R.id.tv_newsTitle);
            newsDescription = (TextView) itemView.findViewById(R.id.tv_newsDescription);
            itemView.setOnClickListener(this);
        }
    }
}