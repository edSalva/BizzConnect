package com.example.edawg.bizconnect_poc;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Created by edawg on 11/6/16.
 */

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context ctx;
    private List<String> headers;
    private HashMap<String, List<BusinessCard>> children;

    public MyExpandableListAdapter(Context c, List<String> heads,
                                   HashMap<String, List<BusinessCard>> childs) {
        ctx = c;
        headers = heads;
        children = childs;
    }
    @Override
    public int getGroupCount() {
        return headers.size();
    }

    @Override
    public int getChildrenCount(int groupPos) {
        return children.get(headers.get(groupPos)).size();
    }

    @Override
    public Object getGroup(int groupPos) {
        return headers.get(groupPos);
    }

    @Override
    public Object getChild(int groupPos, int childPos) {
        return children.get(headers.get(groupPos)).get(childPos);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPos, boolean isExpanded, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            view = inflater.inflate(R.layout.list_group, parent, false);
        }

        TextView listHeader = (TextView) view.findViewById(R.id.list_header);
        listHeader.setTypeface(null, Typeface.BOLD);
        listHeader.setText(headers.get(groupPos));

        return view;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isExpanded,
                             View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            view = inflater.inflate(R.layout.list_item, parent, false);
        }

        BusinessCard bs = children.get(headers.get(groupPos)).get(childPos);
        ((TextView)view.findViewById(R.id.list_name)).setText(bs.getName());
        ((ImageButton)view.findViewById(R.id.list_number)).setTag(bs.getNumber());
        ((TextView)view.findViewById(R.id.list_linkedin)).setText(bs.getLinkedIn());
        ((ImageButton)view.findViewById(R.id.list_email)).setTag(bs.getEmail());
        ((TextView)view.findViewById(R.id.list_industry)).setText(bs.getIndustry());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
