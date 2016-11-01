package br.com.speedy.ipapp_me.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.annimon.stream.Stream;

import java.math.BigDecimal;
import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.model.Produto;
import br.com.speedy.ipapp_me.model.Romaneio;

/**
 * Created by Henrique Araújo on 2015-03-06.
 */
public class ResumoAdapter extends BaseExpandableListAdapter {

    private Produto itemResumo;

    private List<Produto> itensResumo;

    private Context context;

    public ResumoAdapter(Context context, List<Produto> itensResumo){
        this.context = context;
        this.itensResumo = itensResumo;
    }

    @Override
    public int getGroupCount() {

        return itensResumo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itensResumo.get(groupPosition).getRomaneios().size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return itensResumo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itensResumo.get(groupPosition).getRomaneios().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private class ViewHolderGroup{
        TextView txtDescricao;
    }

    private class ViewHolderChild{
        TextView txtId;
        TextView txtLote;
        TextView txtQtdeEmbalagem;
        TextView txtPeso;
        TextView txtCamara;
        TextView txtPosicao;
        TextView txtTamanho;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderGroup holder = null;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
            holder = new ViewHolderGroup();
            holder.txtDescricao = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolderGroup) convertView.getTag();
        }

        itemResumo = (Produto) getGroup(groupPosition);
        int gp = groupPosition + 1;
        BigDecimal pesoPeixe = Stream.of(itemResumo.getRomaneios()).map(Romaneio::getPeso).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        holder.txtDescricao.setText(gp + " - " + itemResumo.getPeso() + " kg de " + itemResumo.getPeixe().getDescricao() + ", " + itemResumo.getTipoPeixe().getDescricao() + " (" + getChildrenCount(groupPosition) + " - " + pesoPeixe.toString() + " kg)");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolderChild holder = null;

        itemResumo = (Produto) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.list_item_resumo_filho, null);

            holder = new ViewHolderChild();
            holder.txtId = (TextView) convertView.findViewById(R.id.txtREId);
            holder.txtLote = (TextView) convertView.findViewById(R.id.txtRLote);
            holder.txtQtdeEmbalagem = (TextView) convertView.findViewById(R.id.txtRQtdeEmbalagem);
            holder.txtPeso = (TextView) convertView.findViewById(R.id.txtRPeso);
            holder.txtCamara = (TextView) convertView.findViewById(R.id.txtRCamara);
            holder.txtPosicao = (TextView) convertView.findViewById(R.id.txtRPosicao);
            //holder.txtTamanho = (TextView) convertView.findViewById(R.id.txtRTamanho);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolderChild) convertView.getTag();
        }

        int gp = groupPosition + 1;
        int cp = childPosition + 1;
        String id = gp + "." + cp;
        holder.txtId.setText(id);
        holder.txtLote.setText(itemResumo.getRomaneios().get(childPosition).getLote().toString());
        holder.txtQtdeEmbalagem.setText(itemResumo.getRomaneios().get(childPosition).getQtdeEmbalagens().toString());
        holder.txtPeso.setText(itemResumo.getRomaneios().get(childPosition).getPeso().toString() + " kg");
        holder.txtCamara.setText(itemResumo.getRomaneios().get(childPosition).getCamara().getDescricao().toString());
        holder.txtPosicao.setText(itemResumo.getRomaneios().get(childPosition).getPosicaoCamara().getDescricao().toString());

        /*if (itemResumo.getRomaneios().get(childPosition).getTamanhoPeixe() != null)
            holder.txtTamanho.setText(itemResumo.getRomaneios().get(childPosition).getTamanhoPeixe().getDescricao().toString());
        else
            holder.txtTamanho.setText("Não Especificado");*/

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}
