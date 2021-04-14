package com.ciprian.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.ciprian.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODECTS = "Products";

    public abstract static class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODECTS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODECTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODECTS;
        public static final String TABLE_NAME = "Products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_Quantity = "quantity";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PHOTO = "photo";
        public static final int INCREMENT_ONE=1;
        public static final int INCREMENT_TWO=2;
        public static final int INCREMENT_FIVE=5;
        public static final int INCREMENT_TEN=10;
        public static boolean isValidIncrement(int increment) {
            if (increment == INCREMENT_ONE || increment == INCREMENT_TWO || increment == INCREMENT_FIVE || increment==INCREMENT_TEN) {
                return true;
            }
            return false;
        }

    }

}

