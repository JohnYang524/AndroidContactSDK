package com.contacts.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.contacts.Contacts;
import com.contacts.ContactsManger;
import com.contacts.app.databinding.FragmentContactListBinding;
import com.contacts.models.Contact;
import com.contacts.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends Fragment{
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = ContactListFragment.class.getName();

    private FragmentContactListBinding binding;

    List<Contact> contacts = new ArrayList<>();
    ContactListAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentContactListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Event listener for contact events
        createContactEventListeners();
        // Create adapter passing in the contact data
        adapter = new ContactListAdapter(contacts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.contactList.setLayoutManager(layoutManager);
        // Attach the adapter to the recyclerview to populate items
        binding.contactList.setAdapter(adapter);
        // Divider
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(getActivity(),
                layoutManager.getOrientation());
        binding.contactList.addItemDecoration(mDividerItemDecoration);

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ContactListFragment.this)
                        .navigate(R.id.action_list_to_createnew);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createContactEventListeners() {
        Contacts.getInstance().setEventListener(new ContactsManger.ContactEventListener() {
            @Override
            public void onContactListLoaded() {
                contacts = Contacts.getInstance().getContactList();
                if (adapter != null) {
                    adapter.notifyDataChange(contacts);
                }
            }

            @Override
            public void onNewContactAdded(int responseCode) {
                if (responseCode == Util.HTTP_RESPONSE_SUCCESS) {
                    if (mIsDebuggable)
                        Log.v(TAG, "New contact successfully added in DB server. Updating local DB and UI");
                    Contacts.getInstance().onServerDataUpdated(getActivity());
                } else {
                    if (mIsDebuggable)
                        Log.v(TAG, "New contact failed to be added in DB server. Error code: " + responseCode);
                }
            }

            @Override
            public void onContactUpdated(Contact oldContact, Contact newContact) {

            }
        });
    }
}
