package com.contacts.app.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.contacts.Contacts;
import com.contacts.helper.ContactsManger;
import com.contacts.app.helpers.ContactListAdapter;
import com.contacts.app.R;
import com.contacts.app.databinding.FragmentContactListBinding;
import com.contacts.utils.Util;

/***
 * Fragment to display a list of Contacts
 * */
public class ContactListFragment extends Fragment{
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = ContactListFragment.class.getName();

    private FragmentContactListBinding binding;

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
        adapter = new ContactListAdapter(Contacts.getInstance().getContactList());
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

        fetchContactData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /***
     * Fetch Contact Data to feed list.
     * Rules:
     * 1. If there is data in local contactList -> show them in UI right away
     * 2. At the same time, check for server updates based on timestamp.
     * 3. If there is new updates in server, fetch it, update DB and update UI
     * */
    private void fetchContactData() {
        if (mIsDebuggable) Log.v(TAG, "Updating data list.");
        if (Contacts.getInstance().getContactList().size() != 0 && adapter != null) {
            adapter.notifyDataChange(Contacts.getInstance().getContactList());
        }
        Contacts.getInstance().syncContactData(getActivity());
    }

    private void createContactEventListeners() {
        Contacts.getInstance().setEventListener(new ContactsManger.ContactEventListener() {
            @Override
            public void onContactListLoaded() {
                if (adapter != null) {
                    adapter.notifyDataChange(Contacts.getInstance().getContactList());
                }
            }

            @Override
            public void onNewContactAdded(int responseCode) {
                if (responseCode == Util.HTTP_RESPONSE_SUCCESS) {
                    if (mIsDebuggable)
                        Log.v(TAG, "Received callback from server. " +
                                "New contact successfully added in DB server. Updating local DB and UI");
                    Contacts.getInstance().onServerDataUpdated(getActivity());
                } else {
                    if (mIsDebuggable)
                        Log.v(TAG, "Received callback from server. " +
                                "New contact failed to be added in DB server. Error code: " + responseCode);
                }
            }

            @Override
            public void onContactUpdated(String newContactData) {
                if (mIsDebuggable)
                    Log.v(TAG, "Received Contact-Updated callback from server. " +
                            "New contact data: " + newContactData);
                Toast.makeText(getActivity(), getString(R.string.simulate_callback_response), Toast.LENGTH_LONG).show();
                Contacts.getInstance().onContactUpdated(newContactData, getActivity());
            }
        });
    }

}
