package edu.feup.stockportfolio.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;

import edu.feup.stockportfolio.R;

public class LabelAdapter extends BaseAdapter {
    private LayoutInflater inflater_;

    private ArrayList<PieSlice> slices_;

    public LabelAdapter(Context context, ArrayList<PieSlice> slices) {
        slices_ = slices;
        inflater_ = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return slices_.size();
    }

    @Override
    public Object getItem(int position) {
        return slices_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        ViewHolder holder = null;

        if (convert_view == null) {
            holder = new ViewHolder();
            convert_view = inflater_.inflate(R.layout.label_list_item, null);
            holder.color = convert_view.findViewById(R.id.color);
            holder.name = (TextView) convert_view.findViewById(R.id.name);
            convert_view.setTag(holder);
        } else {
            holder = (ViewHolder) convert_view.getTag();
        }

        PieSlice slice = slices_.get(position);

        holder.color.setBackgroundColor(slice.getColor());
        holder.name.setText(slice.getTitle());

        return convert_view;
    }

    public static class ViewHolder {
        public View color;
        public TextView name;
    }
}