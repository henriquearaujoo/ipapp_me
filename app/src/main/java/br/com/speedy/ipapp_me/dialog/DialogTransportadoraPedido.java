package br.com.speedy.ipapp_me.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.adapter.SpinnerTipoPeixeAdapter;
import br.com.speedy.ipapp_me.adapter.SpinnerTransportadoraAdapter;
import br.com.speedy.ipapp_me.model.TipoPeixe;
import br.com.speedy.ipapp_me.model.Transportadora;
import br.com.speedy.ipapp_me.util.SessionApp;

/**
 * TODO: document your custom view class.
 */
public class DialogTransportadoraPedido extends DialogFragment {

    public static final int ATUALIZAR_TIPOS = 1;

    private EditText edtPlaca;

    private Spinner spnTransportadoras;

    private List<Transportadora> transportadoras;

    public DialogTransportadoraPedido(){

    }

    public DialogTransportadoraPedido(List<Transportadora> transportadoras){
        this.transportadoras = transportadoras;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sample_dialog_transportadora_pedido, null);

        edtPlaca = (EditText) view.findViewById(R.id.edtPPlaca);

        edtPlaca.setText(SessionApp.getPlacaCarro() != null && !SessionApp.getPlacaCarro().isEmpty() ? SessionApp.getPlacaCarro() : null);

        spnTransportadoras = (Spinner) view.findViewById(R.id.spnPTransportadora);

        SpinnerTransportadoraAdapter adapter = new SpinnerTransportadoraAdapter(getActivity(), transportadoras);

        spnTransportadoras.setAdapter(adapter);

        if(SessionApp.getTransportadora() != null && SessionApp.getTransportadora().getId() != null){
            for (int i = 0; i < transportadoras.size(); i++) {
                if (transportadoras.get(i).getId() != null && SessionApp.getTransportadora().getId().longValue() == transportadoras.get(i).getId().longValue()){
                    spnTransportadoras.setSelection(i);
                    break;
                }
            }
        }

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SessionApp.setPlacaCarro(edtPlaca.getText() != null && !edtPlaca.getText().toString().isEmpty() ? edtPlaca.getText().toString() : null);
                        SessionApp.setTransportadora(spnTransportadoras.getSelectedItem() != null && ((Transportadora) spnTransportadoras.getSelectedItem()).getId() != null ? (Transportadora) spnTransportadoras.getSelectedItem() : null);
                    }
                })
                .setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogTransportadoraPedido.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Transportadora");

        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
