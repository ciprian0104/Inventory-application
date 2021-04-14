package com.ciprian.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ciprian.inventoryapp.data.DbBitmapUtility;
import com.ciprian.inventoryapp.data.InventoryContract.InventoryEntry;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int EXISTING_Inventory_LOADER = 0;
    private static final int SELECT_PICTURE = 100;
    Spinner spinner;
    Snackbar snackbar;
    ImageView imageView;
    Button incrementButton, decrementButton, orderButton;
    ConstraintLayout constraintLayout;
    TextView quantityTextView;
    EditText nameEditText, priceEditText;
    Uri mUri, selectedImageUri;
    String name, price;
    byte[] image;
    int quantity = 1, incrementBy = 1;
    private boolean productHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };
    private Object ConstraintLayout;

    private void setupSpinner() {
        ArrayAdapter incrementSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.increment_options, android.R.layout.simple_spinner_item);
        incrementSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(incrementSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.one))) {
                        incrementBy = InventoryEntry.INCREMENT_ONE;
                    } else if (selection.equals(getString(R.string.two))) {
                        incrementBy = InventoryEntry.INCREMENT_TWO;
                    } else if (selection.equals(getString(R.string.five))) {
                        incrementBy = InventoryEntry.INCREMENT_FIVE;
                    } else {
                        incrementBy = InventoryEntry.INCREMENT_TEN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                incrementBy = InventoryEntry.INCREMENT_ONE;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    try {
                        InputStream stream = getContentResolver().openInputStream(selectedImageUri);
                        image = DbBitmapUtility.getBytes(stream);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setImageBitmap(DbBitmapUtility.getImage(image));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        orderButton = findViewById(R.id.order_button);
        constraintLayout = findViewById(R.id.ConstraintLayout);
        imageView = (ImageView) findViewById(R.id.get_image);
        nameEditText = findViewById(R.id.nameEditText);
        quantityTextView = (TextView) findViewById(R.id.QuantityTextView);
        priceEditText = findViewById(R.id.PriceEditText);
        spinner = findViewById(R.id.spinner);
        incrementButton = findViewById(R.id.Increment_button);
        decrementButton = findViewById(R.id.decrement_button);

        Intent intent = getIntent();
        mUri = intent.getData();
        if (mUri == null) {
            setTitle(R.string.Editor_activity_label_insert_mode);
            orderButton.setVisibility(View.INVISIBLE);

        } else {
            setTitle(R.string.Editor_activity_label_edit_mode);
            getLoaderManager().initLoader(EXISTING_Inventory_LOADER, null, this);
        }

        incrementButton.setOnTouchListener(mTouchListener);
        decrementButton.setOnTouchListener(mTouchListener);
        imageView.setOnTouchListener(mTouchListener);
        nameEditText.setOnTouchListener(mTouchListener);
        quantityTextView.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + "1234567890"));
                startActivity(callIntent);
            }
        });
        setupSpinner();
    }

    private void saveProduct() {
        name = nameEditText.getText().toString().trim();
        price = priceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || image==null) {
            return;
        }
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(price) && image == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, name);
        values.put(InventoryEntry.COLUMN_PRICE, price);
        values.put(InventoryEntry.COLUMN_Quantity, quantity);
        values.put(InventoryEntry.COLUMN_PHOTO, image);
        if (mUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mUri, values, null, null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveProduct();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || image==null) {
                    snackbar = Snackbar.make(constraintLayout, "Product Requires Image, Name And Price", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    finish();
                }
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardClickListener);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {InventoryEntry._ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_Quantity,
                InventoryEntry.COLUMN_PHOTO};
        return new CursorLoader(this,
                mUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_Quantity);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PHOTO);
            name = cursor.getString(nameColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            price = cursor.getString(priceColumnIndex);
            image = cursor.getBlob(imageColumnIndex);
            nameEditText.setText(name);
            priceEditText.setText(price);
            quantityTextView.setText("" + quantity);
            if (image != null) {
                imageView.setImageBitmap(DbBitmapUtility.getImage(image));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            switch (incrementBy) {
                case InventoryEntry.INCREMENT_ONE:
                    spinner.setSelection(0);
                    break;
                case InventoryEntry.INCREMENT_TWO:
                    spinner.setSelection(1);
                    break;
                case InventoryEntry.INCREMENT_FIVE:
                    spinner.setSelection(2);
                    break;
                default:
                    spinner.setSelection(3);
                    break;
            }
        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        nameEditText.setText(null);
        priceEditText.setText(null);
        quantityTextView.setText("" + 1);
        imageView.setImageBitmap(null);
    }

    public void incrementQuantity(View view) {
        quantity += incrementBy;
        quantityTextView.setText("" + quantity);
    }

    public void decrementQuantity(View view) {
        if (quantity <= 1 || quantity - incrementBy < 1) {
            Toast.makeText(this, "You Cannot Have Less Than 1", Toast.LENGTH_SHORT).show();
            return;
        }
        quantity -= incrementBy;
        quantityTextView.setText("" + quantity);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mUri == null) {
            menu.findItem(R.id.delete).setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                delete();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the productt.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardClickListener);
    }

    private void delete() {
        if (mUri != null) {
            int rowsDeleted = getContentResolver().delete(mUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
