package qnopy.com.qnopyandroid.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class ServerConnectActivity extends ProgressDialogActivity {

    EditText ip_text, port_text, app_version_txt;
    Button connect_button,connect_default_button;
    TextView defaultserver;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connect);
        context = this;
        connect_button = findViewById(R.id.connect_btn);
        connect_default_button = findViewById(R.id.default_server_btn);
        ip_text = findViewById(R.id.ip_ev);
        defaultserver = findViewById(R.id.default_server);
//        GlobalStrings.Local_Base_URL= getResources().getString(R.string.prod_base_uri);
//        defaultserver.setText("Default Server :\n"+GlobalStrings.Local_Base_URL);
        port_text = findViewById(R.id.port_ev);
        app_version_txt = findViewById(R.id.app_version_ev);

        connect_default_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                GlobalStrings.Local_Base_URL= "http://"+ip+port+"/FetchForms/api/"+version+"/";
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Default Server")
//                        .setMessage("You are connecting to:\n"+ GlobalStrings.Local_Base_URL )
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(context, SplashScreenActivity.class));

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ip_text.getText().toString();
                String version = app_version_txt.getText().toString();

                if (ip.length() < 1) {
                    ip_text.setError("Enter IP address of server which you want to connect");
                } else if (version.length() < 1) {
                    app_version_txt.setError("Enter version code of server api");
                } else {
                    String port = port_text.getText().toString();
                    if (port.length() > 1) {
                        port = ":" + port;
                    }

                    GlobalStrings.API_VERSION = version;
                    GlobalStrings.IP = ip;
                    GlobalStrings.PORT = port;

//                    GlobalStrings.Local_Base_URL= "http://"+ip+port+"/FetchForms/api/"+version+"/";
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Server")
//                            .setMessage("You are connecting to:\n"+ GlobalStrings.Local_Base_URL )
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(context, SplashScreenActivity.class));

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    //  Toast.makeText(context,"You are connecting to:"+GlobalStrings.Local_Base_URL,Toast.LENGTH_LONG).show();
                }


            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
