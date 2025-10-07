package com.example.myquickhsk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class FilterSearchAdapter extends RecyclerView.Adapter<FilterSearchAdapter.ReviewViewHolder> {

    private List<FilterSearchResultsCard> cardList;
    private View.OnClickListener onItemClickListener;

    public FilterSearchAdapter(List<FilterSearchResultsCard> cardList) {

        this.cardList = cardList;

    }

    public void setOnItemClickListener(View.OnClickListener listener) {

        this.onItemClickListener = listener;

    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        Button scriptButton;
        TextView pinyinText;
        TextView definitionText;

        public ReviewViewHolder(View itemView) {

            super(itemView);

            scriptButton = itemView.findViewById(R.id.review_list_entry_script);
            pinyinText = itemView.findViewById(R.id.review_list_entry_subscript);
            definitionText = itemView.findViewById(R.id.review_list_entry_definition_subscript);

        }

    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_search_results_list_item, parent, false);

        if (onItemClickListener != null) {

            view.setOnClickListener(onItemClickListener);

        }

        return new ReviewViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        FilterSearchResultsCard card = cardList.get(position);

        holder.scriptButton.setText(card.getScript());
        holder.pinyinText.setText(card.getPinyin());
        holder.definitionText.setText(card.getPreviewDefinition());

        holder.itemView.setTag(position);
        holder.scriptButton.setTag(position);

        if (onItemClickListener != null) {

            holder.itemView.setOnClickListener(onItemClickListener);
            holder.scriptButton.setOnClickListener(onItemClickListener);

        }

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public FilterSearchResultsCard getItem(int position) {
        return cardList.get(position);
    }

}
