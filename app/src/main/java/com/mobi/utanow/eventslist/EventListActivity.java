package com.mobi.utanow.eventslist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.mobi.utanow.R;
import com.mobi.utanow.UtaNow;
import com.mobi.utanow.bookmarks.BookMarksActivity;
import com.mobi.utanow.createevent.CreateEventActivity;
import com.mobi.utanow.login.LoginActivity;
import com.mobi.utanow.models.Event;
import com.mobi.utanow.organizations.OrganizationsActivity;
import com.mobi.utanow.settings.SettingsActivity;
import com.squareup.otto.Bus;

import javax.inject.Inject;


/**
 * Created by Anthony on 11/13/15.
 *
 */
public class EventListActivity extends AppCompatActivity
{
    @Inject
    Bus mEventBus;
    @Inject
    Firebase mFirebase;

    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle mToggle;
    RecyclerView mRecyclerView;
    EventsAdapter mAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        ((UtaNow) getApplication()).getAppComponent().inject(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initDrawer();
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mEventBus.unregister(this);
    }

    private void initRecyclerView()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.eventList);
        context = mRecyclerView.getContext();
        mAdapter = new EventsAdapter(context);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebase.child("events").keepSynced(true);

        mFirebase.child("events").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                mAdapter.addEvent(event, 0);
                mRecyclerView.scrollToPosition(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Event event= dataSnapshot.getValue(Event.class);
                mAdapter.removeEvent(event);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void initDrawer()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)

            {
                selectDrawerItem(menuItem);
                return true;
            }
        });

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
    }

    private void selectDrawerItem(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.bookmarks:
                Intent b = new Intent(this, BookMarksActivity.class);
                startActivity(b);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.createEvent:
                Intent c = new Intent(this, CreateEventActivity.class);
                startActivity(c);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.organizations:
                Intent o = new Intent(this, OrganizationsActivity.class);
                startActivity(o);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.logOut:
                LoginManager.getInstance().logOut();
                mFirebase.unauth();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }
}
