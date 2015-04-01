package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pt.utl.ist.airdesk.airdesk.Sqlite.WSDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WorkspaceRepresentation;


public class CreateWorkSpace extends ActionBarActivity {

    Button workspaceButtonCreate;
    EditText workspaceNameEntry;
    EditText workspaceDimensionEntry;
    EditText workspaceUsers;
    String login;
    private WSDataSource datasource;

    //dsfsdf

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_space);

        datasource = new WSDataSource(this);
        datasource.open();


        workspaceNameEntry = (EditText) findViewById(R.id.workspaceNameEntry);

        workspaceDimensionEntry = (EditText) findViewById(R.id.workspaceDimensionEntry);

        workspaceUsers = (EditText) findViewById(R.id.workspaceUsers);

        workspaceButtonCreate = (Button) findViewById(R.id.workspaceButtonCreate);


        workspaceButtonCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                String str = workspaceNameEntry .getText().toString();
                String str2 = workspaceDimensionEntry.getText().toString();
                WorkspaceRepresentation workspaceRepresentation = null;

                Intent intent = getIntent();

                login = intent.getStringExtra("login");

                Intent intent2 = new Intent(CreateWorkSpace.this, MainAirDesk.class);



                String user = workspaceUsers.getText().toString();
                Log.v("USERRRRRRR", user);
                String path = Environment.getExternalStorageDirectory()+"/"+login+"/"+str;
                Log.v("PATHHHHHHH", path);

                File root = new File(Environment.getExternalStorageDirectory() + "/"+login, str);
                Long sdcard = Environment.getExternalStorageDirectory().length();
                Log.v("sdcard size",sdcard.toString());
                if (!root.exists()) {
                    root.mkdirs();
                    intent2.putExtra("titles",str);
                    intent2.putExtra("contents",str2);
                    if(!workspaceUsers.getText().toString().equals(null)){
                        Log.v("teste", "PASSSOU");
                        workspaceRepresentation = datasource.createWorkspaceRepresentation(str, user, path);
                    }


                       String sFileName="teste3.txt";
                       String sBody="lolol";

        try
        {
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            //importError = e.getMessage();
           // iError();
        }

                    String sFileName1="teste2.txt";
                    String sBody1="lolol";

                    try
                    {
                        File gpxfile = new File(root, sFileName1);
                        FileWriter writer = new FileWriter(gpxfile);
                        writer.append(sBody1);
                        writer.flush();
                        writer.close();
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                        //importError = e.getMessage();
                        // iError();
                    }

                    setResult(RESULT_OK,intent2);
                    finish();
                    }
                else{
                    Log.v("directorio", "directorio");
                }










           }

        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_work_space, menu);
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
