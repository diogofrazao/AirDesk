package pt.utl.ist.airdesk.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class ViewWorkspace extends ActionBarActivity {


    ListView listView;
    ArrayList<String> filesList;
    ArrayAdapter<String> listAdapter;
    String path;
    File f;

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

       path = Environment.getExternalStorageDirectory().toString()+"/"+login+"/"+name;
        f = new File(path);
        File file[] = f.listFiles();
//        String tamanho = Integer.toString(file.length);

 //       Log.v("tamanho",tamanho);
        if(!(file == null)) {
            for (int i = 0; i < file.length; i++) {
                filesList.add(file[i].getName());
                Log.v("file", file[i].getName());
            }
        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.mylistfile ,R.id.Itemname, filesList);
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


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(ViewWorkspace.this);

                alert.setTitle("Delete file");
                alert.setMessage("Do you want to delete file?");

// Set an EditText view to get user input


                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    File fileToDelete = new File(path,filesList.get(position).toString());
                        fileToDelete.delete();
                        filesList.clear();
                        File file[] = f.listFiles();
                        if(!(file == null)) {
                            for (int i = 0; i < file.length; i++) {
                                filesList.add(file[i].getName());
                                Log.v("file", file[i].getName());
                            }
                        }
                        listAdapter.notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                return true;
            }
        });


    }

    public void onClickCreateFile(View view){
        Intent createFile = new Intent(ViewWorkspace.this,CreateFile.class);
        createFile.putExtra("path", path);
        startActivityForResult(createFile,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_workspace, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
               filesList.clear();
                File file[] = f.listFiles();
                if(!(file == null)) {
                    for (int i = 0; i < file.length; i++) {
                        filesList.add(file[i].getName());
                        Log.v("file", file[i].getName());
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        }
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
