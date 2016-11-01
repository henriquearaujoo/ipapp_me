package br.com.speedy.ipapp_me;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import br.com.speedy.ipapp_me.fragment.FotosFragment;
import br.com.speedy.ipapp_me.model.Foto;


public class FotosActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos);

        FotosFragment pFragment = (FotosFragment) getSupportFragmentManager().findFragmentByTag("fotosFrag");

        if (pFragment == null) {
            pFragment = new FotosFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.rl_fragment_container, pFragment, "fotosFrag");
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<Foto> getListaFotos(){
        String[] descricoes = new String[]{"BMW 720", "Camaro", "Corvette"};
        int[] fotos = new int[]{R.drawable.bmw_720, R.drawable.camaro, R.drawable.corvette};
        List<Foto> list = new ArrayList<Foto>();

        for (int i = 0; i < fotos.length; i++) {
            list.add(new Foto(fotos[i]));
        }

        return list;
    }
}
