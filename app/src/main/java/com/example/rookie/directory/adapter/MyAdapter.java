package com.example.rookie.directory.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rookie.directory.R;
import com.example.rookie.directory.vo.Person;
import java.util.List;


/**
 * Created by Rookie on 2015/12/13.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<Person> contacts;

    public MyAdapter(List<Person> contacts,Context context){
        this.contacts = contacts;
        this.context = context;
    }
    /**
     * @return 总的大小
     */
    @Override
    public int getCount() {
        return contacts.size();
    }
    @Override
    public Person getItem(int position) {
        return contacts.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /**
         * 将内容加载到里边，即每一条要显示的内容
         */
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView =View.inflate(context,R.layout.call, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        Person p = (Person) getItem(position);
        holder.tv_number.setText(p.getNumber());
        holder.tv_name.setText(p.getDisPlayName());
        return convertView;
    }
    private static class ViewHolder{
        TextView tv_name ;
        TextView tv_number ;
    }
}
