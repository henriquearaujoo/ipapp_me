package br.com.speedy.ipapp_me.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.dialog.DialogFotoPeixe;
import br.com.speedy.ipapp_me.model.Produto;


public class ProdutoAdapter extends BaseAdapter {

    private List<Produto> produtos;

    private Produto produto;

    private LayoutInflater mInflater;

    private ImageLoader imageLoader;

    private FragmentManager fragmentManager;

    public ProdutoAdapter() {
    }

    public ProdutoAdapter(Context context, List<Produto> produtos, ImageLoader imageLoader, FragmentManager fragmentManager){
        this.produtos = produtos;
        this.imageLoader = imageLoader;
        this.fragmentManager = fragmentManager;

        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        TextView txtId;
        TextView txtDescricao;
        ImageView ivFotoPeixe;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return produtos.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return produtos.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return produtos.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_produto, null);
            holder = new ViewHolder();
            holder.txtId = (TextView) convertView.findViewById(R.id.txtIPId);
            holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtIPDescricao);
            holder.ivFotoPeixe = (ImageView) convertView.findViewById(R.id.ivIPFotoPeixe);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Produto produto = (Produto) getItem(position);

        String id = (position + 1) + "";

        holder.txtId.setText(id);
        holder.txtDescricao.setText(produto.getPeso().toString() + " kg de " + produto.getPeixe().getDescricao() + ", " + produto.getTamanhoPeixe().getDescricao() + ", " + produto.getTipoPeixe().getDescricao());
        if (produto.getPeixe().getUrlFoto() != null)
            imageLoader.get(produto.getPeixe().getUrlFoto(), imageLoader.getImageListener(holder.ivFotoPeixe, R.drawable.ic_fish_grey600_48dp, R.drawable.ic_fish_grey600_48dp));

        holder.ivFotoPeixe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogFotoPeixe(produto.getPeixe());
                dialogFragment.show(fragmentManager, "fotoPeixe");
            }
        });

        return convertView;
    }

}
