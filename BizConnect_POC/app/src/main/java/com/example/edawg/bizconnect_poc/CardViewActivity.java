package com.example.edawg.bizconnect_poc;

import android.Manifest;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardViewActivity extends AppCompatActivity {

    MyExpandableListAdapter mAdapter;
    ExpandableListView mListView;
    List<BusinessCard> mCards;
    private ListView mListView2;

    private Uri test;
    private String name;
    private String mobile;
    private String email;
    private String company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_card_view);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setTitle(R.string.collection);
        }

        mCards = getIntent().getParcelableArrayListExtra(TradeCardsActivity.CARDS_LIST_EXTRA);

        mListView= (ExpandableListView) findViewById(R.id.explist);

        mAdapter = createAdapter();

        mListView.setAdapter(mAdapter);
        mListView2=(ListView) findViewById(R.id.listView);
        final TextView mUserName1=(TextView)findViewById(R.id.contact_name1);
        final TextView mUserNumber1=(TextView)findViewById(R.id.contact_number1);
        final TextView mUserEmail1=(TextView)findViewById(R.id.contact_email1);
        final TextView mUserCompany1=(TextView)findViewById(R.id.contact_company1);
        mUserName1.setText(mCards.get(0).getName());
        mUserNumber1.setText(mCards.get(0).getNumber());
        mUserEmail1.setText(mCards.get(0).getEmail());
        mUserCompany1.setText(mCards.get(0).getIndustry());
        Button badgeMedium = (Button) findViewById(R.id.bconnect);
        badgeMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewContact newContact= new CreateNewContact();
                newContact.createContact(mUserName1.getText().toString(), mUserNumber1.getText().toString(),
                        mUserEmail1.getText().toString(), mUserCompany1.getText().toString(), getApplicationContext());
                Toast.makeText(CardViewActivity.this, "Contact Added", Toast.LENGTH_LONG).show();
            }
        });
        Button link= (Button) findViewById(R.id.blink);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.linkedin.com/in/jack-sturtevant-1161a0128?authType=NAME_SEARCH&authToken=ObRG&locale=en_US&trk=tyah&trkInfo=clickedVertical%3Amynetwork%2CclickedEntityId%3A525989538%2CauthType%3ANAME_SEARCH%2Cidx%3A2-1-2%2CtarId%3A1478408016680%2Ctas%3AJack"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }



    private MyExpandableListAdapter createAdapter() {
        List<String> headers = new ArrayList<>();
        HashMap<String, List<BusinessCard>> children = new HashMap<>();

        for (BusinessCard bs : mCards) {
            String event = bs.getEvent();
            if (!headers.contains(event)) {
                headers.add(event);
                children.put(event, new ArrayList<BusinessCard>());
            }
            children.get(event).add(bs);
        }

        return new MyExpandableListAdapter(CardViewActivity.this, headers, children);
    }

    public void makeCall(View view) {
        String phoneURI = "tel:" + view.getTag();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneURI));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    public void makeMail(View view) {
        String email = (String) view.getTag();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        startActivity(Intent.createChooser(intent, "Send an email"));
    }
}
