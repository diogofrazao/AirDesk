package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.utl.ist.airdesk.airdesk.Sqlite.UsersDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.UsersRepresentation;


public class LoginPage extends ActionBarActivity {

    EditText loginText;
    Button go;
    Button SignupButton;
    private UsersDataSource datasource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        datasource = new UsersDataSource(this);
        datasource.open();

        loginText = (EditText) findViewById(R.id.loginText);
        go = (Button) findViewById(R.id.go);
        SignupButton = (Button) findViewById(R.id.SignupButton);

        go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String estaNaBd;
                String login = loginText.getText().toString();
                estaNaBd = datasource.userOnTable(login);



                    if(estaNaBd.equals("sim")){
                        File root = new File(Environment.getExternalStorageDirectory(), login);
                        if (!root.exists())
                            root.mkdirs();
                        Intent intent = new Intent(LoginPage.this, MainAirDesk.class);
                        intent.putExtra("login",login);
                        startActivity(intent);
                        }


                if(estaNaBd.equals("nao")){
                        Toast.makeText(getApplicationContext(), "Utilizador inválido!", Toast.LENGTH_SHORT).show();

                }



            }

        });


        SignupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                Intent intent = new Intent(LoginPage.this, RegisterPage.class);


                startActivity(intent);

            }

        });






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_page, menu);
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
