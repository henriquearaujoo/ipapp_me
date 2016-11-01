package br.com.speedy.ipapp_me.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.adapter.PedidoAdapter;
import br.com.speedy.ipapp_me.adapter.ProdutoAdapter;
import br.com.speedy.ipapp_me.dialog.DialogDadosRomaneio;
import br.com.speedy.ipapp_me.dialog.DialogDadosSaida;
import br.com.speedy.ipapp_me.model.Camara;
import br.com.speedy.ipapp_me.model.Pedido;
import br.com.speedy.ipapp_me.model.Peixe;
import br.com.speedy.ipapp_me.model.PosicaoCamara;
import br.com.speedy.ipapp_me.model.Produto;
import br.com.speedy.ipapp_me.model.TamanhoPeixe;
import br.com.speedy.ipapp_me.model.TipoPeixe;
import br.com.speedy.ipapp_me.util.DialogUtil;
import br.com.speedy.ipapp_me.util.HttpConnection;
import br.com.speedy.ipapp_me.util.SessionApp;
import br.com.speedy.ipapp_me.util.SharedPreferencesUtil;

public class ProdutosFragment extends ListFragment implements Runnable, SwipeRefreshLayout.OnRefreshListener {

    public static final int ATUALIZAR_LISTA = 1;
    public static final int ATUALIZAR_LISTA_SWIPE = 2;
    public static final int ABRIR_DIALOG = 3;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Produto> produtos;

    private Produto produto;

    private Pedido pedido;

    private View produtosStatus;

    private View produtosLista;

    private View msgItensNaoEncontrados;

    private ProdutoAdapter adapter;

    private Thread threadProdutos;

    private SwipeRefreshLayout refreshLayout;

    private ImageLoader il;

    private RequestQueue rq;

    private List<Camara> camaras;

    private List<TipoPeixe> tipos;

    private  List<TamanhoPeixe> tamanhos;

    private ProgressDialog progressDialog;

    // TODO: Rename and change types of parameters
    public static ProdutosFragment newInstance(int position) {
        ProdutosFragment fragment = new ProdutosFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProdutosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produto_list, container, false);

        pedido = SessionApp.getPedido();

        produtosLista = view.findViewById(R.id.produtos_lista);

        produtosStatus = view.findViewById(R.id.produtos_status);

        msgItensNaoEncontrados = view.findViewById(R.id.fp_msg_item_nao_encontrado);

        rq = Volley.newRequestQueue(getActivity());

        il = new ImageLoader(rq, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        initSwipeDownToRefresh(view);

        showProgress(true);
        threadProdutos = new Thread(ProdutosFragment.this);
        threadProdutos.start();

        return view;
    }

    public void getProdutos(){

        JSONObject object = new JSONObject();
        try {
            object.put("id", pedido.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callServer("post-json", object.toString());

        Message msg = new Message();
        msg.what = ATUALIZAR_LISTA;
        handler.sendMessage(msg);
    }

    public void getProdutosJSON(String data){
        produtos = new ArrayList<Produto>();

        try {
            JSONObject object = new JSONObject(data);
            JSONArray ja = object.getJSONArray("produtos");

            for (int i = 0; i < ja.length() ; i++) {
                Produto p = new Produto();
                p.setId(ja.getJSONObject(i).getLong("id"));
                p.setPeso(new BigDecimal(ja.getJSONObject(i).getDouble("peso")));

                JSONObject jPeixe = ja.getJSONObject(i).getJSONObject("peixe");
                Peixe peixe = new Peixe();
                peixe.setId(jPeixe.getLong("id"));
                peixe.setDescricao(jPeixe.getString("descricao"));
                peixe.setUrlFoto(jPeixe.getString("urlFoto"));

                JSONObject jTipo = ja.getJSONObject(i).getJSONObject("tipo");
                TipoPeixe tipoPeixe = new TipoPeixe();
                tipoPeixe.setId(jTipo.getLong("id"));
                tipoPeixe.setDescricao(jTipo.getString("descricao"));

                JSONObject jTamanho = ja.getJSONObject(i).getJSONObject("tamanho");
                TamanhoPeixe tamanho = new TamanhoPeixe();
                tamanho.setId(jTamanho.getLong("id"));
                tamanho.setDescricao(jTamanho.getString("descricao"));

                /*JSONObject jCamara = ja.getJSONObject(i).getJSONObject("camara");
                Camara camara = new Camara();
                camara.setId(jCamara.getLong("id"));
                camara.setDescricao(jCamara.getString("descricao"));

                JSONObject jPosicao = ja.getJSONObject(i).getJSONObject("posicao");
                PosicaoCamara posicaoCamara = new PosicaoCamara();
                posicaoCamara.setId(jPosicao.getLong("id"));
                posicaoCamara.setDescricao(jPosicao.getString("descricao"));*/

                p.setPeixe(peixe);
                p.setTipoPeixe(tipoPeixe);
                p.setTamanhoPeixe(tamanho);
                //p.setCamara(camara);
                //p.setPosicaoCamara(posicaoCamara);

                produtos.add(p);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCamarasETiposJSON(String dados){

        camaras = new ArrayList<Camara>();
        //tipos = new ArrayList<TipoPeixe>();

        try{
            JSONObject object = new JSONObject(dados);

            JSONArray jCamaras = object.getJSONArray("camaras");

            for (int i = 0; i < jCamaras.length() ; i++) {
                Camara camara = new Camara();
                camara.setId(jCamaras.getJSONObject(i).getLong("id"));
                camara.setDescricao(jCamaras.getJSONObject(i).getString("descricao"));

                camaras.add(camara);
            }

            /*JSONArray jTipos = object.getJSONArray("tipos");

            for (int i = 0; i < jTipos.length() ; i++) {
                TipoPeixe tipoPeixe = new TipoPeixe();
                tipoPeixe.setId(jTipos.getJSONObject(i).getLong("id"));
                tipoPeixe.setDescricao(jTipos.getJSONObject(i).getString("descricao"));

                tipos.add(tipoPeixe);
            }*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCamarasTiposETamanhosJSON(String dados){

        camaras = new ArrayList<Camara>();
        tipos = new ArrayList<TipoPeixe>();
        tamanhos = new ArrayList<TamanhoPeixe>();

        try{
            JSONObject object = new JSONObject(dados);

            JSONArray jCamaras = object.getJSONArray("camaras");

            for (int i = 0; i < jCamaras.length() ; i++) {
                Camara camara = new Camara();
                camara.setId(jCamaras.getJSONObject(i).getLong("id"));
                camara.setDescricao(jCamaras.getJSONObject(i).getString("descricao"));

                camaras.add(camara);
            }

            JSONArray jTipos = object.getJSONArray("tipos");

            for (int i = 0; i < jTipos.length() ; i++) {
                TipoPeixe tipoPeixe = new TipoPeixe();
                tipoPeixe.setId(jTipos.getJSONObject(i).getLong("id"));
                tipoPeixe.setDescricao(jTipos.getJSONObject(i).getString("descricao"));

                tipos.add(tipoPeixe);
            }

            JSONArray jTamanho = object.getJSONArray("tamanhos");

            TamanhoPeixe tp = new TamanhoPeixe();
            tp.setId(new Long(24));
            tp.setDescricao("Sem tamanho");

            tamanhos.add(tp);

            for (int i = 0; i < jTamanho.length() ; i++) {
                TamanhoPeixe tamanho = new TamanhoPeixe();
                tamanho.setId(jTamanho.getJSONObject(i).getLong("id"));
                tamanho.setDescricao(jTamanho.getJSONObject(i).getString("descricao"));

                tamanhos.add(tamanho);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callServer(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(getActivity(), "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(getActivity(), "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(getActivity(), "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getProdutosPorPedido", method, data);

        if (!resposta.isEmpty())
            getProdutosJSON(resposta);
    }

    private void callServerCamarasETipos(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(getActivity(), "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(getActivity(), "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(getActivity(), "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getCamaras", method, data);

        if (!resposta.isEmpty())
            getCamarasETiposJSON(resposta);
    }

    private void callServerCamarasTiposETamanhos(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(getActivity(), "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(getActivity(), "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(getActivity(), "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getCamarasTiposETamanhos", method, data);

        if (!resposta.isEmpty())
            getCamarasTiposETamanhosJSON(resposta);
    }

    @Override
    public void run() {
        getProdutos();
    }

    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            refreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            refreshLayout.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            refreshLayout.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });

            produtosStatus.setVisibility(show ? View.VISIBLE : View.GONE);
            produtosStatus.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            produtosStatus.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

        } else {
            refreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            produtosStatus.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case ATUALIZAR_LISTA:

                    adapter = new ProdutoAdapter(getActivity(), produtos, il, getFragmentManager());

                    setListAdapter(adapter);

                    if (isAdded())
                        showProgress(false);

                    if (produtos != null && produtos.size() > 0) {
                        refreshLayout.setVisibility(View.VISIBLE);
                        msgItensNaoEncontrados.setVisibility(View.GONE);
                    }else{
                        refreshLayout.setVisibility(View.GONE);
                        msgItensNaoEncontrados.setVisibility(View.VISIBLE);
                    }

                    break;

                case ATUALIZAR_LISTA_SWIPE:

                    adapter = new ProdutoAdapter(getActivity(), produtos, il, getFragmentManager());

                    setListAdapter(adapter);

                    stopSwipeRefresh();

                    break;

                case ABRIR_DIALOG:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DialogFragment dialogRomaneio = new DialogDadosRomaneio(getActivity(), camaras, tipos, tamanhos);
                    dialogRomaneio.show(getFragmentManager(), "dadosRomaneio");

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        produto = (Produto) this.getListAdapter().getItem(position);

        SessionApp.setProduto(produto);

        progressDialog = ProgressDialog.show(getActivity(), "", "Carregando, aguarde.", false, false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                callServerCamarasTiposETamanhos("get-json", "");

                Message msg = new Message();
                msg.what = ABRIR_DIALOG;
                handler.sendMessage(msg);
            }
        }).start();

        //showDialogOpcoes();

    }

    public void showDialogOpcoes(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Opções");

        String[] itens = {"Alterar produto", "Romaneio"};

        builder.setItems(itens, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showDialogDadosProduto();
                        break;
                    case 1:
                        //DialogFragment dialogRomaneio = new DialogDadosRomaneio(getActivity());
                        //dialogRomaneio.show(getFragmentManager(), "dadosRomaneio");
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogDadosProduto(){
        progressDialog = ProgressDialog.show(getActivity(), "", "Carregando, aguarde.", false, false);
        new Thread(new Runnable() {
            @Override
            public void run() {

                callServerCamarasETipos("get-json", "");

                Message msg = new Message();
                msg.what = ABRIR_DIALOG;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void initSwipeDownToRefresh(View view){
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorScheme(android.R.color.holo_blue_light,
                android.R.color.white, android.R.color.holo_blue_light,
                android.R.color.white);
    }

    @Override
    public void onRefresh() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject object = new JSONObject();
                try {
                    object.put("id", pedido.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callServer("post-json", object.toString());

                Message msg = new Message();
                msg.what = ATUALIZAR_LISTA_SWIPE;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void stopSwipeRefresh() {
        refreshLayout.setRefreshing(false);
    }
}
