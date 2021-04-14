package com.ciprian.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ciprian.inventoryapp.data.InventoryContract.InventoryEntry;


public class InventoryCursorAdapter extends CursorAdapter {
    Uri currentUri;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.NameTxtView);
        TextView quantityTextView = view.findViewById(R.id.quantityTxtView);
        TextView priceTextView = view.findViewById(R.id.priceTextView);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_Quantity);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        String name = cursor.getString(nameColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        nameTextView.setText(name);
        quantityTextView.setText(String.valueOf(quantity));
        priceTextView.setText(price + "$");
        final int quantityViewInt = quantity;

        final int itemId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));

        ImageView imageView = view.findViewById(R.id.buy_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityViewInt == 0) {

                    Toast.makeText(context, "Database has no longer this product", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.order_dialog_msg);
                    builder.setPositiveButton(R.string.order, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Delete" button, so delete the product.
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + "01009064947"));
                            context.startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }
                if (quantityViewInt > 0) {
                    int newQuantity = quantityViewInt - 1;
                    ContentValues newQuantityValue = new ContentValues();
                    newQuantityValue.put(InventoryEntry.COLUMN_Quantity, newQuantity);
                    currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, itemId);
                    context.getContentResolver().update(currentUri, newQuantityValue, null, null);
                }

            }
        });
    }
}
