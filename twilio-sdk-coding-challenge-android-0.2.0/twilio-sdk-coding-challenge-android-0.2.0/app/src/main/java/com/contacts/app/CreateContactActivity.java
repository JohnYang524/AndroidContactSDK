package com.contacts.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.contacts.Contacts;
import com.contacts.ContactsManger;
import com.contacts.models.Contact;
import com.contacts.utils.Util;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CreateContactActivity extends AppCompatActivity {
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = ContactsManger.class.getName();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);

        EditText et_firstName = findViewById(R.id.et_new_first_name);
        EditText et_lastName = findViewById(R.id.et_new_last_name);
        EditText et_phoneNum = findViewById(R.id.et_new_phone_num);

        Button btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAllFields(new EditText[]{et_firstName, et_lastName, et_phoneNum})) {
                    Contact newContact = new Contact(Util.getNextID(getApplicationContext()), getValue(et_firstName), getValue(et_lastName),getValue(et_phoneNum));
                    Contacts.getInstance().onNewContactAdded(newContact, getApplicationContext());// For testing purpose only
                    String jsonString = Util.contactToJSONString(newContact);
                    if (mIsDebuggable)
                        Log.v(TAG, "New contact data created: " + jsonString);
                    Intent intent=new Intent();
                    intent.putExtra(Util.KEY_CONTACT_DATA,jsonString);
                    setResult(ContactsActivity.CREATE_CONTACT_REQUEST_CODE,intent);
                    finish();
                }

            }
        });
    }

    // TODO: add better text validating for names/phone numbers.
    private boolean validateAllFields(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            if (editText == null)
                continue;
            if (editText.getText().toString().length() == 0) {
                Toast.makeText(this, "Please enter all required fields.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private String getValue(EditText editText) {
        if (editText != null)
            return editText.getText().toString();
        return "";
    }
}
