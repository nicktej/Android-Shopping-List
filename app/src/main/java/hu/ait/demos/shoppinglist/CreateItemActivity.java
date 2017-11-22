package hu.ait.demos.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;
import java.util.UUID;

import hu.ait.demos.shoppinglist.data.Item;
import io.realm.Realm;


public class CreateItemActivity extends AppCompatActivity {
    public static final String KEY_ITEM = "KEY_ITEM";
    public static final String TOTAL = "TOTAL";
    private Spinner spinnerItemType;
    private EditText etName;
    private EditText etPrice;
    private CheckBox checkBox;
    private EditText etItemDesc;
    private Item itemToEdit = null;
    public boolean flag;
    public int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        setupUI();

        if (getIntent().getSerializableExtra(MainActivity.KEY_EDIT) != null) {
            initEdit();
        } else {
            initCreate();
        }
    }

    private void initCreate() {
        getRealm().beginTransaction();
        itemToEdit = getRealm().createObject(Item.class, UUID.randomUUID().toString());
        getRealm().commitTransaction();
    }

    private void initEdit() {
        String itemID = getIntent().getStringExtra(MainActivity.KEY_EDIT);
        itemToEdit = getRealm().where(Item.class)
                .equalTo("itemID", itemID)
                .findFirst();

        etName.setText(itemToEdit.getItemName());
        etPrice.setText(itemToEdit.getPrice());
        etItemDesc.setText(itemToEdit.getDescription());
        checkBox.setChecked(itemToEdit.getFlag());

        spinnerItemType.setSelection(itemToEdit.getItemType().getValue());
    }

    private void setupUI() {
        spinnerItemType = (Spinner) findViewById(R.id.spinnerItemType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.itemtypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemType.setAdapter(adapter);

        etName = (EditText) findViewById(R.id.etName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etItemDesc = (EditText) findViewById(R.id.etItemDesc);
        checkBox = (CheckBox) findViewById(R.id.cbPurchased);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

    private void saveItem() {

        if (!TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etPrice.getText())) {
            Intent intentResult = new Intent();
            getRealm().beginTransaction();
            itemToEdit.setItemName(etName.getText().toString());
            itemToEdit.setPrice(etPrice.getText().toString());
            itemToEdit.setDescription(etItemDesc.getText().toString());
            itemToEdit.setItemType(spinnerItemType.getSelectedItemPosition());
            if (checkBox.isChecked()) {
                flag = true;
                checkBox.setChecked(flag);
                itemToEdit.setFlag(flag);
            }
            else {
                flag = false;
                checkBox.setChecked(flag);
                itemToEdit.setFlag(flag);
            }
            getRealm().commitTransaction();

            intentResult.putExtra(KEY_ITEM, itemToEdit.getItemID());
            setResult(RESULT_OK, intentResult);
            finish();
        }

        else {
            if (TextUtils.isEmpty(etPrice.getText())) {
                etPrice.setError("Error! Try again");
            }

            if (TextUtils.isEmpty(etName.getText())) {
                etName.setError("Error! Try again");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TOTAL, total);
    }
}
