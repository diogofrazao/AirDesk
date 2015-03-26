package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CreateWorkSpace extends ActionBarActivity {

    Button workspaceButtonCreate;
    EditText workspaceNameEntry;
    EditText workspaceDimensionEntry;

    //dsfsdf

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_space);


        workspaceNameEntry = (EditText) findViewById(R.id.workspaceNameEntry);

        workspaceDimensionEntry = (EditText) findViewById(R.id.workspaceDimensionEntry);

        workspaceButtonCreate = (Button) findViewById(R.id.workspaceButtonCreate);


        workspaceButtonCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                String str = workspaceNameEntry .getText().toString();
                String str2 = workspaceDimensionEntry.getText().toString();

                Intent intent = new Intent(CreateWorkSpace.this, MainAirDesk.class);
                intent.putExtra("titles",str);
                intent.putExtra("contents",str2);

                setResult(RESULT_OK,intent);
                finish();




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
