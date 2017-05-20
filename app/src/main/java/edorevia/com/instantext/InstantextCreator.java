package edorevia.com.instantext;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class InstantextCreator extends AppCompatActivity {

    Button saveButton;
    ImageButton addContactButton;
    InstantextDBHelper dbHelper;
    boolean isEditMode;
    EditText nameEdit;
    EditText phoneEdit;
    EditText textEdit;
    int dbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instantext_creator);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#007AA3")));
        bar.setDisplayHomeAsUpEnabled(true);

        dbHelper = new InstantextDBHelper(getApplicationContext());
        isEditMode = false;

        nameEdit = (EditText) findViewById(R.id.name_edit);
        phoneEdit = (EditText) findViewById(R.id.number_edit);
        textEdit = (EditText) findViewById(R.id.text_edit);

        Intent intent = this.getIntent();
        dbId = -1;
        try {
            dbId = intent.getIntExtra("dbId", -1);
            if (dbId != -1) {
                Cursor dbCursor = dbHelper.getText(dbId);
                dbCursor.moveToFirst();
                isEditMode = true;
                nameEdit.setText(dbCursor.getString(dbCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_NAME)));
                phoneEdit.setText(dbCursor.getString(dbCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_NUMBER)));
                textEdit.setText(dbCursor.getString(dbCursor.getColumnIndex(InstantextDBHelper.TEXT_COLUMN_TEXT)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameEdit = (EditText) findViewById(R.id.name_edit);
                phoneEdit = (EditText) findViewById(R.id.number_edit);
                textEdit = (EditText) findViewById(R.id.text_edit);
                if (phoneEdit.getText().toString().equals("") || textEdit.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InstantextCreator.this);
                    builder.setMessage(R.string.blank_fields_alert).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // just close the dialog
                        }
                    }).show();
                } else {
                    saveButton.setEnabled(false);
                    if (nameEdit.getText().toString().equals("")) {
                        nameEdit.setText(phoneEdit.getText().toString());
                    }
                    if (isEditMode && dbId != -1) {
                        dbHelper.updatePerson(dbId, nameEdit.getText().toString(), phoneEdit.getText().toString(), textEdit.getText().toString());
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                        InstantextWidget.updateWidgetByDbId(getApplicationContext(), appWidgetManager, dbId);
                    } else {
                        dbHelper.insertText(nameEdit.getText().toString(), phoneEdit.getText().toString(), textEdit.getText().toString());
                    }
                    finish();
                }
            }
        });

        addContactButton = (ImageButton) findViewById(R.id.add_contact_button);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor contactCursor =  getContentResolver().query(contactData, null, null, null, null);
            if (contactCursor.moveToFirst()) {
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNumber = getPhoneNumberById(contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.Contacts._ID)));
                nameEdit.setText(name);
                phoneEdit.setText(phoneNumber);
            }
        }
    }

    private String getPhoneNumberById(int contactId) {
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.DATA
        };
        Cursor phoneCursor;
        String phoneNumber;

        phoneCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projection,"CONTACT_ID = ?",
                new String[]{Integer.toString(contactId)},null);
        if (phoneCursor.moveToFirst()) {
            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
        } else {
            phoneNumber = "";
        }

        return(phoneNumber);
    }
}
