package br.com.speedy.ipapp_me.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.adapter.SpinnerCamaraAdapter;
import br.com.speedy.ipapp_me.adapter.SpinnerPosicoesAdapter;
import br.com.speedy.ipapp_me.adapter.SpinnerTamanhoAdapter;
import br.com.speedy.ipapp_me.adapter.SpinnerTipoPeixeAdapter;
import br.com.speedy.ipapp_me.model.Camara;
import br.com.speedy.ipapp_me.model.PosicaoCamara;
import br.com.speedy.ipapp_me.model.Produto;
import br.com.speedy.ipapp_me.model.Romaneio;
import br.com.speedy.ipapp_me.model.TamanhoPeixe;
import br.com.speedy.ipapp_me.model.TipoPeixe;
import br.com.speedy.ipapp_me.util.HttpConnection;
import br.com.speedy.ipapp_me.util.SessionApp;
import br.com.speedy.ipapp_me.util.SharedPreferencesUtil;

/**
 * TODO: document your custom view class.
 */
public class DialogDadosRomaneio extends DialogFragment {

    public static final int ATUALIZAR_DADOS = 1;
    public static final int ATUALIZAR_POSICOES = 2;

    private EditText editTextLote;

    private EditText editTextQtdeEmbalagem;

    private EditText editTextPeso;

    private Context context;

    private AlertDialog alertDialog;

    private Romaneio romaneio;

    private Produto produto;

    private List<Camara> camaras;

    private List<PosicaoCamara> posicaoCamaras;

    private List<TipoPeixe> tipos;

    private List<TamanhoPeixe> tamanhos;

    private Spinner spnCamaras;

    private Spinner spnPosicoes;

    //private Spinner spnTipos;

    //private Spinner spnTamanhos;

    private Camara camara;

    public DialogDadosRomaneio(){

    }

    public DialogDadosRomaneio(Context context, List<Camara> camaras, List<TipoPeixe> tipos, List<TamanhoPeixe> tamanhos){
        this.context = context;
        this.camaras = camaras;
        this.tipos = tipos;
        this.tamanhos = tamanhos;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sample_dialog_dados_romaneio, null);

        produto = SessionApp.getProduto();

        romaneio = SessionApp.getRomaneio();

        editTextLote = (EditText) view.findViewById(R.id.edtDRLote);

        editTextQtdeEmbalagem = (EditText) view.findViewById(R.id.edtDRQtdeEmbalagem);

        editTextPeso = (EditText) view.findViewById(R.id.edtDRPeso);

        //editTextObservacoes =  (EditText) view.findViewById(R.id.edtDRObservacoes);

        spnCamaras = (Spinner) view.findViewById(R.id.spnRCamara);

        spnCamaras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                camara = (Camara) spnCamaras.getSelectedItem();
                getPosicoesCamara();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnPosicoes = (Spinner) view.findViewById(R.id.spnRPosicao);

        //spnTipos = (Spinner) view.findViewById(R.id.spnRTipo);

        //spnTamanhos = (Spinner) view.findViewById(R.id.spnRTamanho);

        Message msg = new Message();
        msg.what = ATUALIZAR_DADOS;
        handler.sendMessage(msg);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            salvarRomaneio();
                        }
                    }

                    ).

                    setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    alertDialog.cancel();
                                }
                            }

                    );

        builder.setTitle("Romaneio - " + produto.getPeixe().getDescricao());

        alertDialog = builder.create();

        return alertDialog;
    }

    public void preencherDados(){

        editTextLote.setText(romaneio.getLote().toString());
        editTextQtdeEmbalagem.setText(romaneio.getQtdeEmbalagens().toString());
    }

    public void callServerPosicoesCamara(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(getActivity(), "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(getActivity(), "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(getActivity(), "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getPosicoesCamara", method, data);

        if (!resposta.isEmpty())
            getPosicoesJSON(resposta);
    }

    public void getPosicoesCamara(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                JSONObject object = new JSONObject();
                try {
                    object.put("id", camara.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callServerPosicoesCamara("post-json", object.toString());

                Message msg = new Message();
                msg.what = ATUALIZAR_POSICOES;
                handler.sendMessage(msg);

            }
        }).start();
    }

    public void getPosicoesJSON(String dados){
        posicaoCamaras = new ArrayList<PosicaoCamara>();

        try {
            JSONArray jPosicoes = new JSONArray(dados);

            for (int i = 0; i < jPosicoes.length() ; i++) {
                PosicaoCamara posicaoCamara = new PosicaoCamara();
                posicaoCamara.setId(jPosicoes.getJSONObject(i).getLong("id"));
                posicaoCamara.setDescricao(jPosicoes.getJSONObject(i).getString("descricao"));

                posicaoCamaras.add(posicaoCamara);
            }
        }catch (Exception e){
            Log.e("IPAPP_MA", e.getMessage());
        }

    }

    public void salvarRomaneio(){

        if (romaneio == null) {
            if (editTextLote.getText() != null && !editTextLote.getText().toString().isEmpty()
                    && editTextQtdeEmbalagem.getText() != null && !editTextQtdeEmbalagem.getText().toString().isEmpty()
                    && editTextPeso.getText() != null && !editTextPeso.getText().toString().isEmpty()) {
                try {
                    Produto itemResumo = null;

                    if (SessionApp.getItens() == null || SessionApp.getItens().size() == 0) {
                        SessionApp.setItens(new ArrayList<Produto>());
                    } else {
                        for (Produto i : SessionApp.getItens()) {
                            if (i.getId().longValue() == produto.getId().longValue()) {
                                itemResumo = i;
                                break;
                            }
                        }
                    }

                    if (itemResumo == null) {
                        itemResumo = new Produto();
                        itemResumo.setTipoPeixe(produto.getTipoPeixe());
                        itemResumo.setPeso(produto.getPeso());
                        itemResumo.setPeixe(produto.getPeixe());
                        itemResumo.setId(produto.getId());
                        itemResumo.setPedido(produto.getPedido());
                        SessionApp.getItens().add(itemResumo);
                    }

                    romaneio = new Romaneio();
                    romaneio.setLote(editTextLote.getText().toString());
                    romaneio.setQtdeEmbalagens(Integer.parseInt(editTextQtdeEmbalagem.getText().toString()));
                    romaneio.setPeso(new BigDecimal(editTextPeso.getText().toString()));
                    romaneio.setProduto(produto);
                    romaneio.setTipoPeixe(produto.getTipoPeixe());
                    romaneio.setTamanhoPeixe(produto.getTamanhoPeixe());
                    romaneio.setCamara((Camara) spnCamaras.getSelectedItem());
                    romaneio.setPosicaoCamara((PosicaoCamara) spnPosicoes.getSelectedItem());
                    //romaneio.setTipoPeixe((TipoPeixe) spnTipos.getSelectedItem());
                    //romaneio.setTamanhoPeixe(spnTamanhos.getSelectedItem() != null ? (TamanhoPeixe) spnTamanhos.getSelectedItem() : null);
                    //romaneio.setObservacoes(editTextObservacoes.getText() != null && !editTextObservacoes.getText().toString().isEmpty() ? editTextObservacoes.getText().toString() : null);

                    if (itemResumo.getRomaneios() == null || itemResumo.getRomaneios().size() == 0) {
                        itemResumo.setRomaneios(new ArrayList<Romaneio>());
                        itemResumo.getRomaneios().add(romaneio);
                    } else
                        itemResumo.getRomaneios().add(romaneio);

                    showDialogInformacao("Romaneio adicionado com sucesso.");
                } catch (Exception e) {
                    showDialogAdvertencia("Não foi possivel adicionar o romaneio.");
                }
            } else {
                showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
            }
        }else{
            if (editTextLote.getText() != null && !editTextLote.getText().toString().isEmpty()
                    && editTextQtdeEmbalagem.getText() != null && !editTextQtdeEmbalagem.getText().toString().isEmpty()) {

                try{

                    for (Produto itemResumo : SessionApp.getItens()) {

                        for (Romaneio aux1 : itemResumo.getRomaneios()) {
                            if (aux1.getId().longValue() == romaneio.getId().longValue()) {
                                aux1.setLote(editTextLote.getText().toString());
                                aux1.setQtdeEmbalagens(Integer.parseInt(editTextQtdeEmbalagem.getText().toString()));
                                //aux1.setObservacoes(editTextObservacoes.getText() != null && !editTextObservacoes.getText().toString().isEmpty() ? editTextObservacoes.getText().toString() : null);
                            }
                        }

                    }

                    showDialogInformacao("Romaneio editado com sucesso.");
                }catch (Exception e){
                    showDialogAdvertencia("Não foi possivel editar o romaneio.");
                }

            }else {
                showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
            }
        }
    }

    public void showDialogAdvertencia(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Advertência");

        builder.setIcon(R.drawable.ic_alert_grey600_48dp);

        builder.setMessage(msg);

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                alertDialog.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case ATUALIZAR_DADOS:

                    if(camaras != null){
                        SpinnerCamaraAdapter adapter = new SpinnerCamaraAdapter(getActivity(), camaras);
                        spnCamaras.setAdapter(adapter);
                    }

                    /*if (tipos != null){
                        SpinnerTipoPeixeAdapter adapter = new SpinnerTipoPeixeAdapter(getActivity(), tipos);
                        spnTipos.setAdapter(adapter);
                    }*/

                    /*if (tamanhos != null){
                        SpinnerTamanhoAdapter adapter = new SpinnerTamanhoAdapter(getActivity(), tamanhos);
                        spnTamanhos.setAdapter(adapter);
                    }*/

                    if(romaneio != null)
                        preencherDados();

                    break;
                case ATUALIZAR_POSICOES:
                    SpinnerPosicoesAdapter posicoesAdapter = new SpinnerPosicoesAdapter(getActivity(), posicaoCamaras);
                    spnPosicoes.setAdapter(posicoesAdapter);

                    /*if (aux != null){
                        for (int i = 0; i < posicaoCamaras.size(); i++) {
                            if(aux.getPosicaoCamara().getId().longValue() == posicaoCamaras.get(i).getId().longValue()){
                                spnPosicoes.setSelection(i);
                                break;
                            }
                        }
                    }*/

                    break;
                default:
                    break;
            }
        }
    };

}
