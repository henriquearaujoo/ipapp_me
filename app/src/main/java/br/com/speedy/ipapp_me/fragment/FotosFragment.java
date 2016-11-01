package br.com.speedy.ipapp_me.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.speedy.ipapp_me.FotosActivity;
import br.com.speedy.ipapp_me.R;
import br.com.speedy.ipapp_me.adapter.FotoAdapter;
import br.com.speedy.ipapp_me.interfaces.RecyclerViewOnCLickListenerHack;
import br.com.speedy.ipapp_me.model.Foto;
import br.com.speedy.ipapp_me.util.SessionApp;

public class FotosFragment extends Fragment implements RecyclerViewOnCLickListenerHack, View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PromocaoFragment.
     */

    private RecyclerView rv;

    private List<Foto> fotos;

    private Button btAdicionarFoto;

    private Uri mCapturedImageURI;

    private String filePath;

    // TODO: Rename and change types and number of parameters
    public static FotosFragment newInstance(String param1, String param2) {
        FotosFragment fragment = new FotosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_foto, container, false);

        btAdicionarFoto = (Button) view.findViewById(R.id.btFAdicionar);
        btAdicionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamera();
            }
        });

        rv = (RecyclerView) view.findViewById(R.id.rv_list);

        rv.setHasFixedSize(true);

        /*LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        //llm.setReverseLayout(true);
        rv.setLayoutManager(llm);*/

        GridLayoutManager llm = new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);

        /*StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        llm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rv.setLayoutManager(llm);*/

        fotos = SessionApp.getFotosPedido() != null ? SessionApp.getFotosPedido() : new ArrayList<Foto>();
        //fotos = ((FotosActivity) getActivity()).getListaFotos();
        FotoAdapter fotoAdapter = new FotoAdapter(getActivity(), fotos);
        fotoAdapter.setRecyclerViewOnCLickListenerHack(this);
        rv.setAdapter(fotoAdapter);

        return view;
    }

    public void abrirCamera(){

        try{
            String fileName = "foto_pedido_" + SessionApp.getPedido().getCodigo() + "_" + new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()) +"_";
            /*ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);*/

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            File image = File.createTempFile(fileName, ".jpg", storageDir);

            filePath = image.getAbsolutePath();

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            adicionarFoto(data);
        }
    }

    public void adicionarFoto(Intent data){

        //Bundle extras = data.getExtras();
        //Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);
        Bitmap imageBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filePath), 350, 250);

        if (SessionApp.getFotosPedido() == null)
            SessionApp.setFotosPedido(new ArrayList<Foto>());

        Foto foto = new Foto();
        foto.setPath(filePath);
        foto.setBitmap(imageBitmap);
        SessionApp.getFotosPedido().add(foto);

        int size = SessionApp.getFotosPedido().size();

        FotoAdapter fotoAdapter = (FotoAdapter) rv.getAdapter();
        fotoAdapter.addItem(SessionApp.getFotosPedido().get(size - 1), size);
    }

    @Override
    public void onClickListener(View view, int position) {
        Toast.makeText(getActivity(), "Item clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }
}
