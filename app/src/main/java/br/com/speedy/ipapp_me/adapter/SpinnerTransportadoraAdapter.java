package br.com.speedy.ipapp_me.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.model.TipoPeixe;
import br.com.speedy.ipapp_me.model.Transportadora;

public class SpinnerTransportadoraAdapter extends BaseAdapter {

    private List<Transportadora> transportadoras;

    private Transportadora transportadora;

    private LayoutInflater mInflater;

    public SpinnerTransportadoraAdapter() {
        // TODO Auto-generated constructor stub
    }

    public SpinnerTransportadoraAdapter(Context context, List<Transportadora> transportadoras){
        this.transportadoras = transportadoras;

        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        TextView txtId;
        TextView txtDescricao;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return transportadoras.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return transportadoras.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return transportadoras.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_spinner, null);
            holder = new ViewHolder();
            //holder.txtId = (TextView) convertView.findViewById(R.id.txtIPId);
            holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtSpnDecricao);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        transportadora = (Transportadora) getItem(position);

        //String id = (position + 1) + "";

        //holder.txtId.setText(id);
        holder.txtDescricao.setText(transportadora.getNome());

        return convertView;
    }

}
