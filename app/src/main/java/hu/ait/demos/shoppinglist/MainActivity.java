package hu.ait.demos.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.ait.demos.shoppinglist.adapter.ItemsAdapter;
import hu.ait.demos.shoppinglist.CreateItemActivity;
import hu.ait.demos.shoppinglist.data.Item;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_NEW_ITEM = 101;
    public static final int REQUEST_EDIT_ITEM = 102;
    public static final String KEY_EDIT = "KEY_EDIT";
    private ItemsAdapter itemsAdapter;
    private CoordinatorLayout layoutContent;
    private DrawerLayout drawerLayout;
    private int itemToEditPosition = -1;
    public CheckBox checkBox;
    private String string;
    public int total;
    public int add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MainApplication)getApplication()).openRealm();

        RealmResults<Item> allItems = getRealm().where(Item.class).findAll();
        final Item itemsArray[] = new Item[allItems.size()];
        List<Item> itemsResult = new ArrayList<Item>(Arrays.asList(allItems.toArray(itemsArray)));

        itemsAdapter = new ItemsAdapter(itemsResult, this);
        RecyclerView recyclerViewItems = (RecyclerView) findViewById(
                R.id.recyclerViewItems);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(itemsAdapter);

        ItemsListTouchHelperCallback touchHelperCallback = new ItemsListTouchHelperCallback(
                itemsAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                touchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerViewItems);

        layoutContent = (CoordinatorLayout) findViewById(
                R.id.layoutContent);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateItemActivity();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.action_add:
                                showCreateItemActivity();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_total:

                                total = itemsAdapter.calculate();
                                showSnackBarMessage(getString(R.string.total) + total);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_about:
                                showSnackBarMessage(getString(R.string.txt_about));
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_help:
                                showSnackBarMessage(getString(R.string.txt_help));
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                        }

                        return false;
                    }
                });

        setUpToolBar();
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void showCreateItemActivity() {
        Intent intentStart = new Intent(MainActivity.this,
                CreateItemActivity.class);
        startActivityForResult(intentStart, REQUEST_NEW_ITEM);
    }

    public void showEditItemActivity(String itemID, int position) {
        Intent intentStart = new Intent(MainActivity.this,
                CreateItemActivity.class);
        itemToEditPosition = position;

        intentStart.putExtra(KEY_EDIT, itemID);
        startActivityForResult(intentStart, REQUEST_EDIT_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String itemID  = data.getStringExtra(
                        CreateItemActivity.KEY_ITEM);

                Item item = getRealm().where(Item.class)
                        .equalTo("itemID", itemID)
                        .findFirst();

                if (requestCode == REQUEST_NEW_ITEM) {
                    itemsAdapter.addItem(item);
                    showSnackBarMessage(getString(R.string.txt_item_added));
                } else if (requestCode == REQUEST_EDIT_ITEM) {


                    itemsAdapter.updateItem(itemToEditPosition, item);
                    showSnackBarMessage(getString(R.string.txt_item_edited));
                }
                break;
            case RESULT_CANCELED:
                showSnackBarMessage(getString(R.string.txt_add_cancel));
                break;
        }
    }


    public void deleteItem(Item item) {
        getRealm().beginTransaction();
        item.deleteFromRealm();
        getRealm().commitTransaction();
    }

    public void deleteAllItem() {
        getRealm().beginTransaction();
        getRealm().deleteAll();
        getRealm().commitTransaction();
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
            }
        }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                showCreateItemActivity();
                return true;
            case R.id.action_deleteall_from_toolbar:
                itemsAdapter.removeAllItem();
                return true;
            default:
                showCreateItemActivity();
                return true;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MainApplication)getApplication()).closeRealm();
    }

}

