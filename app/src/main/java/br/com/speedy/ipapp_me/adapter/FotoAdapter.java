package br.com.speedy.ipapp_me.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.interfaces.RecyclerViewOnCLickListenerHack;
import br.com.speedy.ipapp_me.model.Foto;

/**
 * Created by henrique on 23/05/2015.
 */
public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.MyViewHolder>{

    private Context context;
    private List<Foto> fotos;
    private LayoutInflater layoutInflater;
    private RecyclerViewOnCLickListenerHack recyclerViewOnCLickListenerHack;

    public FotoAdapter(Context context, List<Foto> fotos){
        this.context = context;
        this.fotos = fotos;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.inflate(R.layout.list_item_fotos, parent, false);

        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.ivFoto.setImageBitmap(fotos.get(position).getBitmap());
        //holder.ivFoto.setImageResource(fotos.get(position).getFoto());
        //holder.tvDescricao.setText(fotos.get(position).getDescricao());

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            holder.ivPromocao.setImageResource(promocoes.get(position).getFoto());
        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), promocoes.get(position).getFoto());
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            bitmap = ImageHelper.getRoundedCornerBitmap(context, bitmap, 10, width, height, false, false, true, true);
            holder.ivPromocao.setImageBitmap(bitmap);
        }*/
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    public void setRecyclerViewOnCLickListenerHack(RecyclerViewOnCLickListenerHack recyclerViewOnCLickListenerHack) {
        this.recyclerViewOnCLickListenerHack = recyclerViewOnCLickListenerHack;
    }

    public void addItem(Foto foto, int position){
        fotos.add(foto);
        notifyItemInserted(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView ivFoto;
        public TextView tvCategoria;
        public TextView tvDescricao;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivFoto = (ImageView) itemView.findViewById(R.id.iv_foto);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyclerViewOnCLickListenerHack != null){
                recyclerViewOnCLickListenerHack.onClickListener(v, getPosition());
            }

        }
    }
}
