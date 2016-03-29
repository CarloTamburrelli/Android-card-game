package com.example.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamburrelli on 05/08/14.
 */
public class CustomAdapter extends ArrayAdapter<carte> {

    public ArrayList<carte> countryList;
    int flag;


    public CustomAdapter(Context context, int textViewResourceId,List objects,int flag) {
        super(context, textViewResourceId, objects);
        this.flag=flag;
    }
    public CustomAdapter(Context context, int textViewResourceId,ArrayList<carte> countryList,int flag,char a) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<carte>();
        this.countryList.addAll(countryList);
        this.flag=flag;
    }


    private class ViewHolder {
        CheckBox name;
        ImageView im;
        TextView pw;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(flag==0){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.elenco, null);
        TextView nome = (TextView)convertView.findViewById(R.id.textViewz);
        ImageView im = (ImageView)convertView.findViewById(R.id.imageViewz);
        TextView pww = (TextView)convertView.findViewById(R.id.textpower);
        carte c = getItem(position);
        String nom= c.getNome() ;
        int conta = Integer.parseInt(nom.substring(0,1)); // 1_1_1_3
        conta =conta + Integer.parseInt(nom.substring(2,3));
        conta = conta + Integer.parseInt(nom.substring(4,5));
        conta = conta + Integer.parseInt(nom.substring(6));
        pww.setText(""+conta);
        nome.setText("Lati: "+c.getNome());
        im.setImageDrawable(c.getimg());
        }else if(flag==1){

            ViewHolder holder = null;

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.elenco2, null);

            holder = new ViewHolder();
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.im = (ImageView) convertView.findViewById(R.id.imageViewzss);
            holder.pw = (TextView) convertView.findViewById(R.id.textpowerZ);
            convertView.setTag(holder);
            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    carte country = (carte) cb.getTag();
                    country.setSelected(cb.isChecked());
                }
            });

            carte c = countryList.get(position);

            String nom= c.getNome() ;
            int conta = Integer.parseInt(nom.substring(0,1)); // 1_1_1_3
            conta =conta + Integer.parseInt(nom.substring(2,3));
            conta = conta + Integer.parseInt(nom.substring(4,5));
            conta = conta + Integer.parseInt(nom.substring(6));
            holder.pw.setText("" + conta);
            holder.im.setImageDrawable(c.getimg());
            holder.name.setText(c.getNome());
            holder.name.setChecked(c.isSelected());
            holder.name.setTag(c);

        }
        return convertView;
    }


}
