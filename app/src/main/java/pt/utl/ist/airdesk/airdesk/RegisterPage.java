package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import pt.utl.ist.airdesk.airdesk.Sqlite.UsersDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.UsersRepresentation;
import pt.utl.ist.airdesk.airdesk.Sqlite.WSDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WorkspaceRepresentation;


public class RegisterPage extends ActionBarActivity {

    Button RegisterButton;
    private UsersDataSource datasource;
    EditText registerBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        registerBox = (EditText) findViewById(R.id.registerBox);
        RegisterButton = (Button) findViewById(R.id.RegisterButton);
        datasource = new UsersDataSource(this);
        datasource.open();

        RegisterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                List<UsersRepresentation> comments = new ArrayList<>();

                UsersRepresentation usersRepresentation = null;

                String str = registerBox.getText().toString();

                usersRepresentation = datasource.createUsersRepresentation(str);

                comments = datasource.getAllComments();
                String debug = comments.get(0).getName();

                Log.d("USERS:", debug);






                Intent intent = new Intent(RegisterPage.this, LoginPage.class);


                startActivity(intent);

            }

        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_page, menu);
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
