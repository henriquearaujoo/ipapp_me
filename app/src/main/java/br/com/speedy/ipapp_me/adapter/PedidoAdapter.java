package br.com.speedy.ipapp_me.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.model.Pedido;

public class PedidoAdapter extends BaseAdapter {

	private List<Pedido> pedidos;

	private Pedido peixe;

    private LayoutInflater mInflater;

	public PedidoAdapter() {
		// TODO Auto-generated constructor stub
	}

    public PedidoAdapter(Context context, List<Pedido> pedidos){
		this.pedidos = pedidos;

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
		return pedidos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return pedidos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return pedidos.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_pedido, null);
			holder = new ViewHolder();
            holder.txtId = (TextView) convertView.findViewById(R.id.txtAPId);
			holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtAPDescricao);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

        final Pedido pedido = (Pedido) getItem(position);

        String id = (position + 1) + "";

        holder.txtId.setText(id);
		holder.txtDescricao.setText(pedido.getCodigo() + " de " + new SimpleDateFormat("dd/MM/yyyy").format(pedido.getData()) + " entrega em " + new SimpleDateFormat("dd/MM/yyyy").format(pedido.getDataEntrega()));

		return convertView;
	}

}
