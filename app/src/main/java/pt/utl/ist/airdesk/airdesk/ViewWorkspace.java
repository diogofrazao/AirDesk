package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class ViewWorkspace extends ActionBarActivity {


    ListView listView;
    ArrayList<String> filesList;
    ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workspace);
        listView = (ListView) findViewById(R.id.listView3);
        TextView editText = (TextView) findViewById(R.id.editText);
        filesList = new ArrayList<String>();
        Intent intent = getIntent();
        final String name = intent.getStringExtra("wsName");
        final String login = intent.getStringExtra("login");
        //listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, filesList);
       // listView.setAdapter(listAdapter);

       final String path = Environment.getExternalStorageDirectory().toString()+"/"+login+"/"+name;
        File f = new File(path);
        File file[] = f.listFiles();
//        String tamanho = Integer.toString(file.length);

 //       Log.v("tamanho",tamanho);
        if(!(file == null)) {
            for (int i = 0; i < file.length; i++) {
                filesList.add(file[i].getName());
                Log.v("file", file[i].getName());
            }
        }

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filesList);
        listView.setAdapter(listAdapter);
        Log.v("caminho",path);
        editText.setText(name, TextView.BufferType.EDITABLE);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewWorkspace.this,ViewFile.class);
                intent.putExtra("fileName",filesList.get(position));
                intent.putExtra("path",path);
                startActivity(intent);
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_workspace, menu);
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
}
