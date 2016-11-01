package br.com.speedy.ipapp_me;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.speedy.ipapp_me.dialog.DialogTransportadoraPedido;
import br.com.speedy.ipapp_me.fragment.PedidosFragment;
import br.com.speedy.ipapp_me.fragment.ProdutosFragment;
import br.com.speedy.ipapp_me.fragment.ResumoFragment;
import br.com.speedy.ipapp_me.model.Pedido;
import br.com.speedy.ipapp_me.model.Produto;
import br.com.speedy.ipapp_me.model.Romaneio;
import br.com.speedy.ipapp_me.model.Transportadora;
import br.com.speedy.ipapp_me.util.DialogUtil;
import br.com.speedy.ipapp_me.util.HttpConnection;
import br.com.speedy.ipapp_me.util.SessionApp;
import br.com.speedy.ipapp_me.util.SharedPreferencesUtil;


public class PedidoActivity extends ActionBarActivity implements ActionBar.TabListener{

    public static final int FINALIZAR_ENVIO = 1;
    public static final int ABRIR_DADOS_TRANSPORTADORA = 2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private ProgressDialog progressDialog;

    private Fragment fragmentResumo;

    private Button btEnviarDados;

    private Button btEstoque;

    private Button btTransportadora;

    private Button btFotos;

    private List<Produto> itemResumos;

    private List<Transportadora> transportadoras;

    private Boolean valido;

    private PedidosFragment pedidosFragment;

    public PedidoActivity(){

    }

    public PedidoActivity(PedidosFragment pedidosFragment){
        this.pedidosFragment = pedidosFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setSubtitle("Pedido: " + SessionApp.getPedido().getCodigo());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.

            ActionBar.Tab tab = actionBar.newTab();
            //tab.setText(mSectionsPagerAdapter.getPageTitle(i));
            tab.setTabListener(this);

            if (i == 0) {
                //tab.setIcon(R.drawable.ic_action_peixe);
                tab.setText("Produtos");
            }else if(i == 1){
                tab.setText("Resumo");
            }

            actionBar.addTab(tab);
        }

        btEnviarDados = (Button) findViewById(R.id.btPEnviarDados);
        btEstoque = (Button) findViewById(R.id.btPEstoque);
        btTransportadora = (Button) findViewById(R.id.btPTransportadora);
        btFotos =  (Button) findViewById(R.id.btPFotos);
        btEstoque.setText("  Estoque");
        btEnviarDados.setText("  Enviar dados");
        btTransportadora.setText("  Transportadora");
        btFotos.setText("  Fotos");
        btEnviarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararEnviarDados();
            }
        });

        btEstoque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PedidoActivity.this, EstoqueActivity.class);
                startActivity(i);
            }
        });

        btTransportadora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDadosTransportadora();
            }
        });

        btFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PedidoActivity.this, FotosActivity.class);
                startActivity(i);
            }
        });

    }

    public void prepararEnviarDados(){
        itemResumos = SessionApp.getItens();

        if (itemResumos != null && itemResumos.size() > 0){
            /*if (SessionApp.getPlacaCarro() != null && SessionApp.getTransportadora() != null)
                showDialogConfirmacaoEnvio("Confirma o envio dos dados?");
            else
                DialogUtil.showDialogAdvertencia(PedidoActivity.this, "Selecione a transportadora e a placa do carro antes de enviar os dados.");*/
            showDialogConfirmacaoEnvio("Confirma o envio dos dados?");
        }else{
            DialogUtil.showDialogAdvertencia(PedidoActivity.this, "Adicione pelo menos um romaneio antes de enviar os dados.");
        }
    }

    public void abrirDadosTransportadora(){

        progressDialog = ProgressDialog.show(PedidoActivity.this, "", "Carregando, aguarde.", false, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                callServerTransportadoras("get-json", "");

                Message msg = new Message();
                msg.what = ABRIR_DADOS_TRANSPORTADORA;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void showDialogConfirmacaoEnvio(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PedidoActivity.this);

        builder.setTitle("Confirmação");

        builder.setMessage(msg);

        builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                enviarDados();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void enviarDados(){

        progressDialog = ProgressDialog.show(PedidoActivity.this, "", "Enviando, aguarde.", false, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jPedido = new JSONObject();

                try{
                    jPedido.put("id", SessionApp.getPedido().getId());
                    jPedido.put("idUsuario", SessionApp.getUsuario().getId());
                    //jPedido.put("placaCarro", SessionApp.getPlacaCarro());

                    //JSONObject jTransportadora = new JSONObject();
                    //jTransportadora.put("id", SessionApp.getTransportadora().getId());

                    //jPedido.put("transportadora", jTransportadora);

                    JSONArray jProdutos = new JSONArray();

                    for (Produto itemResumo : itemResumos){
                        JSONObject jProduto = new JSONObject();
                        jProduto.put("id", itemResumo.getId());

                        JSONArray jRomaneios = new JSONArray();

                        for (Romaneio aux : itemResumo.getRomaneios()) {

                            JSONObject jRomaneio = new JSONObject();
                            jRomaneio.put("id", aux.getId());
                            jRomaneio.put("lote", aux.getLote());
                            jRomaneio.put("qtdeEmbalagens", aux.getQtdeEmbalagens());
                            jRomaneio.put("observacao", aux.getObservacoes());
                            jRomaneio.put("peso", aux.getPeso());

                            JSONObject jCamara = new JSONObject();
                            jCamara.put("id", aux.getCamara().getId());

                            JSONObject jPosicao = new JSONObject();
                            jPosicao.put("id", aux.getPosicaoCamara().getId());

                            JSONObject jTipo = new JSONObject();
                            jTipo.put("id", aux.getTipoPeixe().getId());

                            JSONObject jTamanho = new JSONObject();
                            if (aux.getTamanhoPeixe() != null)
                                jTamanho.put("id", aux.getTamanhoPeixe().getId());

                            jRomaneio.put("camara", jCamara);
                            jRomaneio.put("posicao", jPosicao);
                            jRomaneio.put("tipo", jTipo);
                            jRomaneio.put("tamanhoPeixe", jTamanho);

                            jRomaneios.put(jRomaneio);
                        }

                        jProduto.put("romaneios", jRomaneios);

                        jProdutos.put(jProduto);

                    }

                    jPedido.put("produtos", jProdutos);

                    String resposta = callServer("post-json", jPedido.toString());

                    JSONObject jResposta = new JSONObject(resposta);

                    valido = jResposta.getBoolean("valido");

                    Message msg = new Message();
                    msg.what = FINALIZAR_ENVIO;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    Log.e("IPAPP_ME - Resumo", e.getMessage());
                    DialogUtil.showDialogAdvertencia(PedidoActivity.this, "Não foi possivel enviar os dados.");
                }
            }
        }).start();
    }

    private String callServer(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(PedidoActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(PedidoActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(PedidoActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "salvarPedido", method, data);

        return resposta;

    }

    public void getTransportadorasJSON(String dados){

        transportadoras = new ArrayList<Transportadora>();

        Transportadora tp = new Transportadora();
        tp.setId(null);
        tp.setNome("Selecione a transportadora");

        transportadoras.add(tp);

        try{
            JSONObject object = new JSONObject(dados);

            JSONArray jTipos = object.getJSONArray("transportadoras");

            for (int i = 0; i < jTipos.length() ; i++) {
                Transportadora transportadora = new Transportadora();
                transportadora.setId(jTipos.getJSONObject(i).getLong("id"));
                transportadora.setNome(jTipos.getJSONObject(i).getString("nome"));

                transportadoras.add(transportadora);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callServerTransportadoras(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(PedidoActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(PedidoActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(PedidoActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getTransportadoras", method, data);

        if (!resposta.isEmpty())
            getTransportadorasJSON(resposta);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       return super.onOptionsItemSelected(item);
    }

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PedidoActivity.this);

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

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case FINALIZAR_ENVIO:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    if(valido) {
                        limparSessionApp();
                        //runThread();
                        showDialogInformacao("Dados enviados com sucesso");
                    }else
                        DialogUtil.showDialogAdvertencia(PedidoActivity.this, "Não foi possivel enviar os dados.");

                    break;
                case ABRIR_DADOS_TRANSPORTADORA:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DialogFragment df = new DialogTransportadoraPedido(transportadoras);
                    df.show(getSupportFragmentManager(), "transportadoras");

                    break;
                default:
                    break;
            }
        }
    };

    public void limparSessionApp(){
        SessionApp.setPedido(null);
        SessionApp.setProduto(null);
        SessionApp.setItens(null);
        SessionApp.setRomaneio(null);

        if (pedidosFragment != null)
            pedidosFragment.runThread();

        finish();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

        if(tab.getPosition() == 1){
            ((ResumoFragment) fragmentResumo).runThread();
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            Fragment fragment = null;

            switch (position){
                case 0:
                    fragment = ProdutosFragment.newInstance(position + 1);
                    break;
                case 1:
                    fragmentResumo = new ResumoFragment().newInstance(position + 1);
                    fragment = fragmentResumo;
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section11).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section12).toUpperCase(l);
            }
            return null;
        }
    }

}
