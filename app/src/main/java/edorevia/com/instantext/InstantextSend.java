package edorevia.com.instantext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class InstantextSend extends Activity {

    String name;
    String phoneNo;
    String message;
    SmsManager smsManager;
    ArrayList<String> parts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_instantext_send);

        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        phoneNo = intent.getStringExtra("phoneNumber");
        message = intent.getStringExtra("messageText");

        TextView messageText = (TextView) findViewById(R.id.instantext_preview);
        messageText.setText(message);
        TextView recipientText = (TextView) findViewById(R.id.recipient_text);
        if (name.equals(phoneNo)) {
            recipientText.setText(name);
        } else {
            recipientText.setText(name + " (" + phoneNo + ")");
        }

        Button sendBtn = (Button) findViewById(R.id.send_button);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                smsManager = SmsManager.getDefault();
               parts = smsManager.divideMessage(message);
                if (parts.size() > 1) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    InstantextHelper.sendSMSMessage(phoneNo, parts, smsManager);
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder delBuilder = new AlertDialog.Builder(InstantextSend.this);
                    delBuilder.setMessage("This message will be broken into " + parts.size() + " parts. Send anyway?").setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
                } else {
                    InstantextHelper.sendSMSMessage(phoneNo, parts, smsManager);
                    finish();
                }
            }
        });

        Button cancelBtn = (Button) findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }


}
