package com.contacts.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.contacts.Contacts;
import com.contacts.helper.ContactsManager;
import com.contacts.app.R;
import com.contacts.app.databinding.FragmentCreateNewBinding;
import com.contacts.models.Contact;
import com.contacts.utils.Util;
/***
 * Fragment to create a new contact record.
 * */
public class CreateNewContactFragment extends Fragment {
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = ContactsManager.class.getName();

    private FragmentCreateNewBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentCreateNewBinding.inflate(inflater, container, false);

        // Fixing a keyboard issue
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAllFields(new EditText[]{binding.etNewFirstName, binding.etNewLastName, binding.etNewPhoneNum})) {

                    Contact newContact = new Contact(Util.getNextID(getActivity()),
                                                    getValue(binding.etNewFirstName),
                                                    getValue(binding.etNewLastName),
                                                    getValue(binding.etNewPhoneNum));

                    Contacts.getInstance().addNewContact(newContact, getActivity());

                    hideKeyboard(view);
                    // Navigate back to list
                    NavHostFragment.findNavController(CreateNewContactFragment.this)
                            .navigate(R.id.action_createnew_to_list);

                    Toast.makeText(getActivity(), getString(R.string.new_contact_created_message), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // TODO: add better text validating for names/phone numbers.
    private boolean validateAllFields(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            if (editText == null)
                continue;
            if (editText.getText().toString().length() == 0) {
                Toast.makeText(getActivity(), getString(R.string.invalid_entry_message), Toast.LENGTH_LONG).show();
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

    private void hideKeyboard(View view) {
        // Hide keyboard
        InputMethodManager imm =(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
