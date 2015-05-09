package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pt.utl.ist.airdesk.airdesk.Sqlite.WSDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WSPermissionSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WSUsersPermission;
import pt.utl.ist.airdesk.airdesk.Sqlite.WorkspaceRepresentation;


public class CreateWorkSpace extends ActionBarActivity {

    private Button workspaceButtonCreate;
    private EditText workspaceNameEntry;
    private EditText workspaceDimensionEntry;
    private EditText workspaceUsers;
    private CheckBox checkbox;
    private String login;
    private WSDataSource datasourceWorkspace;
    String permission = "r";
    private SeekBar seekBar;
    private Long sdcard;
    private int lastProgress;
    private WSPermissionSource datasourcePermissions;

    //dsfsdf

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_space);

        datasourceWorkspace = new WSDataSource(this);
        datasourceWorkspace.open();
        datasourcePermissions = new WSPermissionSource(this);
        datasourcePermissions.open();

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        workspaceNameEntry = (EditText) findViewById(R.id.workspaceNameEntry);

        workspaceDimensionEntry = (EditText) findViewById(R.id.workspaceDimensionEntry);

        workspaceUsers = (EditText) findViewById(R.id.workspaceUsers);

        workspaceButtonCreate = (Button) findViewById(R.id.workspaceButtonCreate);

        checkbox = (CheckBox) findViewById(R.id.checkBox);

        sdcard = Environment.getExternalStorageDirectory().getFreeSpace();

        workspaceButtonCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (checkbox.isChecked()) {
                    permission = "rw";
                }


                    String str = workspaceNameEntry .getText().toString();
                String str2 = workspaceDimensionEntry.getText().toString();
                WorkspaceRepresentation workspaceRepresentation = null;
                WSUsersPermission wsUsersPermission = null;

                Intent intent = getIntent();

                login = intent.getStringExtra("login");

                Intent intent2 = new Intent(CreateWorkSpace.this, MainAirDesk.class);



                String user = workspaceUsers.getText().toString();
                String path = Environment.getExternalStorageDirectory()+"/"+login+"/"+str;

                File root = new File(Environment.getExternalStorageDirectory() + "/"+login, str);

                seekBar.setMax(sdcard.intValue());


                if(lastProgress > 0 ) {
                    if (!root.exists()) {
                        root.mkdirs();
                        intent2.putExtra("titles", str);
                        intent2.putExtra("contents", str2);

                        workspaceRepresentation = datasourceWorkspace.createWorkspaceRepresentation(str, lastProgress, path, login);

                        if(!user.equals("")) {
                            wsUsersPermission = datasourcePermissions.createPermissionsRepresentation(str, user, permission);
                        }
                        setResult(RESULT_OK, intent2);
                        finish();
                    }

                }
                else{
                    Toast.makeText(CreateWorkSpace.this, "Minimal quota is 0",Toast.LENGTH_SHORT).show();
                }

           }

        });

        seekBar.setMax(sdcard.intValue() / (1024 * 1024));
        //seekBar.setProgress(50);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(CreateWorkSpace.this, "seek bar progress:" + progressChanged,
                        Toast.LENGTH_SHORT).show();
                workspaceDimensionEntry.setText(progressChanged+"");
                lastProgress = progressChanged;
            }
        });



        workspaceDimensionEntry.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    int i = Integer.parseInt(s.toString());
                    if (i >= 0 && i <= sdcard / (1024 * 1024)) {
                        seekBar.setProgress(i); // This ensures 0-120 value for seekbar
                    } else setProgress(seekBar.getMax());
                } catch (NumberFormatException e) {
                    seekBar.setProgress(0);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
