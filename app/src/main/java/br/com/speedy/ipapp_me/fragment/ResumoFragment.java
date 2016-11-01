package br.com.speedy.ipapp_me.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.adapter.ResumoAdapter;
import br.com.speedy.ipapp_me.model.Produto;
import br.com.speedy.ipapp_me.model.Romaneio;
import br.com.speedy.ipapp_me.util.SessionApp;

public class ResumoFragment extends Fragment implements Runnable {

    public static final int ATUALIZAR_LISTA = 1;
    public static final int FINALIZAR_ENVIO = 2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Produto> itensResumo;

    private ExpandableListView eListView;

    private View itemStatus;

    private View itemLista;

    private ResumoAdapter adapter;

    private Thread threadResumo;

    private TextView txtTotalEmbalagens;

    private TextView txtTotalPeso;

    private View view;

    private Integer totalEmbalagens = 0;

    private BigDecimal totalPeso = BigDecimal.ZERO;

    // TODO: Rename and change types of parameters
    public static ResumoFragment newInstance(int position) {
        ResumoFragment fragment = new ResumoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ResumoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_resumo_list, container, false);

        eListView = (ExpandableListView) view.findViewById(R.id.eList);

        eListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Produto p = itensResumo.get(groupPosition);
                showDialogRemoverPai(p, groupPosition);
                return true;
            }
        });

        eListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Produto p = itensResumo.get(groupPosition);
                Romaneio r = itensResumo.get(groupPosition).getRomaneios().get(childPosition);
                showDialogRemoverFilho(p, r, groupPosition, childPosition);
                return true;
            }
        });

        txtTotalEmbalagens = (TextView) view.findViewById(R.id.txtRTotalEmbalagens);

        txtTotalEmbalagens.setText("0");

        txtTotalPeso = (TextView) view.findViewById(R.id.txtRTotalPeso);

        txtTotalPeso.setText("0");

        return view;
    }

    public void runThread(){
        threadResumo = new Thread(this);
        threadResumo.start();
    }

    public void getItens(){

        itensResumo = SessionApp.getItens() != null ? SessionApp.getItens() : new ArrayList<Produto>();

        if (itensResumo.size() > 0)
            Collections.sort(itensResumo, new SortByPeixe());

        Message msg = new Message();
        msg.what = ATUALIZAR_LISTA;
        handler.sendMessage(msg);
    }

    public class SortByPeixe implements Comparator<Produto>{

        @Override
        public int compare(Produto p1, Produto p2) {
            return p1.getPeixe().getDescricao().compareToIgnoreCase(p2.getPeixe().getDescricao());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //runThread();

    }

    @Override
    public void run() {

        getItens();
    }

    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            itemStatus.setVisibility(View.VISIBLE);
            itemStatus.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            itemStatus.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            itemLista.setVisibility(View.VISIBLE);
            itemLista.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            itemLista.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            itemStatus.setVisibility(show ? View.VISIBLE : View.GONE);
            itemLista.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case ATUALIZAR_LISTA:

                    if (itensResumo != null) {

                        totalEmbalagens = 0;

                        totalPeso = BigDecimal.ZERO;

                        for (Produto i : itensResumo){

                            for(Romaneio r : i.getRomaneios()){
                                totalEmbalagens = totalEmbalagens + r.getQtdeEmbalagens();
                                totalPeso = totalPeso.add(r.getPeso());
                            }


                        }

                        txtTotalEmbalagens.setText(totalEmbalagens.toString());

                        txtTotalPeso.setText(totalPeso.toString() + " kg");

                        adapter = new ResumoAdapter(getActivity(), itensResumo);

                        setListAdapter(adapter);

                        for (int i = 0; i < adapter.getGroupCount(); i++) {
                            eListView.expandGroup(i);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

    public void setListAdapter (ExpandableListAdapter adapter) {

        eListView.setAdapter(adapter);
    }

    public void showDialogRemoverPai(final Produto itemResumo, final int groupPosition){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirmação");

        int position = groupPosition + 1;

        builder.setMessage("Deseja remover o item " + position + " e todos seus subitens?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removerPai(groupPosition);
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogRemoverFilho(final Produto p, final Romaneio r, final int itemPosition, final int childPosition){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirmação");

        int item = itemPosition + 1;
        int subItem = childPosition + 1;
        String sub = item + "." + subItem;

        builder.setMessage("Deseja remover o subitem " + sub + "?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removerFilho(itemPosition, childPosition);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Informação");

        builder.setIcon(R.drawable.ic_information_grey600_48dp);

        builder.setMessage(msg);

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void removerPai(int groupPosition){

        SessionApp.getItens().remove(groupPosition);

        runThread();
    }

    public void removerFilho(int groupPosition, int childPosition){

        SessionApp.getItens().get(groupPosition).getRomaneios().remove(childPosition);

        if (SessionApp.getItens().get(groupPosition).getRomaneios().size() == 0)
            SessionApp.getItens().remove(groupPosition);

        runThread();
    }

}
