// ------------------------------------ DBADapter.java ---------------------------------------------

// TODO: Change the package to match your project.
package com.example.restaurantapp.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

// BIG THANKS TO BRIAN FRASER FOR THE SQL DB CODE!!!!
// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.

public class SQLDatabase {

	/////////////////////////////////////////////////////////////////////
	//	Constants & Data
	/////////////////////////////////////////////////////////////////////
	// For logging:
	private static final String TAG = "DBAdapter";
	
	// DB Fields
	public static final String KEY_RESTAURANT_ROWID = "_idRestaurant";
	public static final int COL_RESTAURANT_ROWID = 0;

	public static final String KEY_INSPECTION_ROWID = "_idInspection";
	public static final int COL_INSPECTION_ROWID = 0;
	/*
	 * CHANGE 1:
	 */
	// TODO: Setup your fields here:
	public static final String KEY_TRACKINGNUMBER = "trackingNumber";
	public static final String KEY_NAME = "name";
	public static final String KEY_PHYSICALADDRESS = "address";
	public static final String KEY_PHYSICALCITY = "city";
	public static final String KEY_FACTYPE = "facilityType";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_FAVOURITE = "favourite";

	// TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
	public static final int COL_TRACKINGNUMBER = 1;
	public static final int COL_NAME = 2;
	public static final int COL_PHYSICALADDRESS = 3;
	public static final int COL_PHYSICALCITY = 4;
	public static final int COL_FACTYPE = 5;
	public static final int COL_LATITUDE = 6;
	public static final int COL_LONGITUDE = 7;
	public static final int COL_FAVOURITE = 8;

	public static final String KEY_INSPECTION_TRACKINGNUMBER = "trackingNumber";
	public static final String KEY_INSPECTIONDATE = "date";
	public static final String KEY_INSPTYPE = "inspType";
	public static final String KEY_NUMCRITICAL = "numCritical";
	public static final String KEY_NUMNONCRITICAL = "numNonCritical";
	public static final String KEY_VIOLLUMP = "violLump";
	public static final String KEY_HAZARDRATING = "hazardRating";

	// TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
	public static final int COL_INSPECTION_TRACKINGNUMBER = 1;
	public static final int COL_INSPECTIONDATE = 2;
	public static final int COL_INSPTYPE = 3;
	public static final int COL_NUMCRITICAL = 4;
	public static final int COL_NUMNONCRITICAL = 5;
	public static final int COL_VIOLLUMP = 6;
	public static final int COL_HAZARDRATING = 7;

	
	public static final String[] ALL_RESTAURANT_KEYS = new String[] {KEY_RESTAURANT_ROWID, KEY_TRACKINGNUMBER ,KEY_NAME, KEY_PHYSICALADDRESS, KEY_PHYSICALCITY, KEY_FACTYPE, KEY_LATITUDE, KEY_LONGITUDE, KEY_FAVOURITE};
	public static final String[] ALL_INSPECTION_KEYS = new String[] {KEY_INSPECTION_ROWID, KEY_INSPECTION_TRACKINGNUMBER ,KEY_INSPECTIONDATE, KEY_INSPTYPE, KEY_NUMCRITICAL, KEY_NUMNONCRITICAL, KEY_VIOLLUMP, KEY_HAZARDRATING};

	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "RestaurantDB";
	public static final String DATABASE_RESTAURANT_TABLE = "restaurantTable";
	public static final String DATABASE_INSPECTION_TABLE = "inspectionTable";
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 16;
	
	private static final String DATABASE_CREATE_RESTAURANT_SQL =
			"create table " + DATABASE_RESTAURANT_TABLE
			+ " (" + KEY_RESTAURANT_ROWID + " integer primary key autoincrement, "
			
			/*
			 * CHANGE 2:
			 */
			// TODO: Place your fields here!
			// + KEY_{...} + " {type} not null"
			//	- Key is the column name you created above.
			//	- {type} is one of: text, integer, real, blob
			//		(http://www.sqlite.org/datatype3.html)
			//  - "not null" means it is a required field (must be given a value).
			// NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
					+ KEY_TRACKINGNUMBER + " string not null, "
					+ KEY_NAME + " string not null, "
					+ KEY_PHYSICALADDRESS + " string not null, "
					+ KEY_PHYSICALCITY + " string not null, "
					+ KEY_FACTYPE + " string not null, "
					+ KEY_LATITUDE + " string not null, "
					+ KEY_LONGITUDE + " string not null, "
					+ KEY_FAVOURITE + " integer default 0"


			// Rest  of creation:
			+ ");";

	private static final String DATABASE_CREATE_INSPECTION_SQL =
			"create table " + DATABASE_INSPECTION_TABLE
					+ " (" + KEY_INSPECTION_ROWID + " integer primary key autoincrement, "

					/*
					 * CHANGE 2:
					 */
					// TODO: Place your fields here!
					// + KEY_{...} + " {type} not null"
					//	- Key is the column name you created above.
					//	- {type} is one of: text, integer, real, blob
					//		(http://www.sqlite.org/datatype3.html)
					//  - "not null" means it is a required field (must be given a value).
					// NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
					+ KEY_INSPECTION_TRACKINGNUMBER + " string not null, "
					+ KEY_INSPECTIONDATE + " string not null, "
					+ KEY_INSPTYPE + " string not null, "
					+ KEY_NUMCRITICAL + " string not null, "
					+ KEY_NUMNONCRITICAL + " string not null, "
					+ KEY_VIOLLUMP + " string not null, "
					+ KEY_HAZARDRATING + " string not null "

					// Rest  of creation:
			+ ");";
	
	// Context of application who uses us.
	private final Context context;
	
	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/////////////////////////////////////////////////////////////////////
	//	Public methods:
	/////////////////////////////////////////////////////////////////////
	
	public SQLDatabase(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);

	}
	
	// Open the database connection.
	public SQLDatabase open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	public void StartTransaction(){
		db.beginTransaction();
	}

	public void SetAndEnd(){
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}

	public long getDatabaseSize(){
		return new File(db.getPath()).length();
	}

	/*
		public static final String KEY_TRACKINGNUMBER = "trackingNumber";
	public static final String KEY_NAME = "name";
	public static final String KEY_PHYSICALCITY = "city";
	public static final String KEY_FACTYPE = "facilityType";
	public static final String KEY_LATITUDE = "lat";
	public static final String KEY_LONGITUDE = "long";
	 */
	
	// Add a new set of values to the database.
	public long insertRestaurantRow(String trackingNumber, String name, String address, String city, String facilityType, String latitude, String longitude) {
		/*
		 * CHANGE 3:
		 */		
		// TODO: Update data in the row with new fields.
		// TODO: Also change the function's arguments to be what you need!
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TRACKINGNUMBER, trackingNumber);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_PHYSICALADDRESS, address);
		initialValues.put(KEY_PHYSICALCITY, city);
		initialValues.put(KEY_FACTYPE, facilityType);
		initialValues.put(KEY_LATITUDE, latitude);
		initialValues.put(KEY_LONGITUDE, longitude);
		
		// Insert it into the database.
		return db.insert(DATABASE_RESTAURANT_TABLE, null, initialValues);
	}
	
	// Delete a row from the database, by rowId (primary key)
	public boolean deleteRestaurantRow(long rowId) {
		String where = KEY_RESTAURANT_ROWID + "=" + rowId;
		return db.delete(DATABASE_RESTAURANT_TABLE, where, null) != 0;
	}
	
	public void deleteAllRestaurants() {
		Cursor c = getAllRestaurantRows();
		long rowId = c.getColumnIndexOrThrow(KEY_RESTAURANT_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRestaurantRow(c.getLong((int) rowId));
			} while (c.moveToNext());
		}
		c.close();
	}
	
	// Return all data in the database.
	public Cursor getAllRestaurantRows() {
		String where = null;
		Cursor c = 	db.query(true, DATABASE_RESTAURANT_TABLE, ALL_RESTAURANT_KEYS,
							where, null, null, null, KEY_NAME, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Get a specific row (by rowId)
	public Cursor getRestaurantRow(long rowId) {
		String where = KEY_RESTAURANT_ROWID + "=" + rowId;
		Cursor c = 	db.query(true, DATABASE_RESTAURANT_TABLE, ALL_RESTAURANT_KEYS,
						where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public Cursor getRestaurantFromTrackingNumber(String trackingNumber){
		String where = "SELECT All * FROM " + DATABASE_RESTAURANT_TABLE + " WHERE trackingNumber = '" + trackingNumber + "';";
		Cursor c = db.rawQuery(where, null);
		if(c != null){
			c.moveToFirst();
		}
		return c;
	}

	// Change an existing row to be equal to new data.
	public boolean updateRestaurantRow(long rowId, String trackingNumber, String name, String address, String city, String facilityType, String latitude, String longitude, int favourite) {
		String where = KEY_RESTAURANT_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
		// TODO: Update data in the row with new fields.
		// TODO: Also change the function's arguments to be what you need!
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_TRACKINGNUMBER, trackingNumber);
		newValues.put(KEY_NAME, name);
		newValues.put(KEY_PHYSICALADDRESS, address);
		newValues.put(KEY_PHYSICALCITY, city);
		newValues.put(KEY_FACTYPE, facilityType);
		newValues.put(KEY_LATITUDE, latitude);
		newValues.put(KEY_LONGITUDE, longitude);
		newValues.put(KEY_FAVOURITE, favourite);
		
		// Insert it into the database.
		return db.update(DATABASE_RESTAURANT_TABLE, newValues, where, null) != 0;
	}


	public boolean updateFavourite(long rowId) {
		String where = KEY_RESTAURANT_ROWID + "=" + rowId;
		Cursor c = 	db.query(true, DATABASE_RESTAURANT_TABLE, ALL_RESTAURANT_KEYS,
				where, null, null, null, null, null);
		if(c != null){
			c.moveToFirst();
		}
		int favourite =  Integer.parseInt(c.getString(COL_FAVOURITE));
		if(favourite == 0){
			favourite = 1;
		}
		else{
			favourite = 0;
		}
		/*
		 * CHANGE 4:
		 */
		// TODO: Update data in the row with new fields.
		// TODO: Also change the function's arguments to be what you need!
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_TRACKINGNUMBER, c.getString(COL_TRACKINGNUMBER));
		newValues.put(KEY_NAME, c.getString(COL_NAME));
		newValues.put(KEY_PHYSICALADDRESS, c.getString(COL_PHYSICALADDRESS));
		newValues.put(KEY_PHYSICALCITY, c.getString(COL_PHYSICALCITY));
		newValues.put(KEY_FACTYPE, c.getString(COL_FACTYPE));
		newValues.put(KEY_LATITUDE, c.getString(COL_LATITUDE));
		newValues.put(KEY_LONGITUDE, c.getString(COL_LONGITUDE));
		newValues.put(KEY_FAVOURITE, favourite);
		// Insert it into the database.
		return db.update(DATABASE_RESTAURANT_TABLE, newValues, where, null) != 0;
	}

	public Cursor searchRestaurantName(String name){
		//String where = "SELECT ALL * FROM " + DATABASE_RESTAURANT_TABLE + " WHERE name LIKE '" + name + "';";
		Cursor cursor = db.query(DATABASE_RESTAURANT_TABLE, null, KEY_NAME+ " LIKE '%"+name+"%'", null, null, null, KEY_NAME);
		//Cursor c = db.rawQuery(where, null);
		if(cursor != null){
			cursor.moveToFirst();

		}
		return cursor;
	}
	public Cursor getFavourites(){
		// DOES THIS HAVE TO HAVE 'Distinct' set to true??

		String where = KEY_FAVOURITE + "=" + 1;
		Cursor c = 	db.query(false, DATABASE_RESTAURANT_TABLE, ALL_RESTAURANT_KEYS,
				where, null, null, null, KEY_NAME, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}



	public Cursor getHazard(String hazard){

		String where = "SELECT * FROM " + DATABASE_RESTAURANT_TABLE + " WHERE trackingNumber IN " +
				"(SELECT DISTINCT trackingNumber AS tnumber FROM " + DATABASE_INSPECTION_TABLE + " WHERE  " + KEY_HAZARDRATING + " LIKE '" + hazard + "' AND date = (SELECT max(date) FROM " + DATABASE_INSPECTION_TABLE + " WHERE trackingNumber = tnumber)) ORDER BY name ;";
		Cursor c = db.rawQuery(where, null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public Cursor getCriticalLessOrEqual(int inputNumCritical){

		String where = "SELECT * FROM " + DATABASE_RESTAURANT_TABLE + " WHERE trackingNumber in (SELECT trackingNumber FROM " + DATABASE_INSPECTION_TABLE + " GROUP BY trackingNumber HAVING SUM(numCritical) <= "+ inputNumCritical +" ) ORDER BY name;";
		Cursor c = db.rawQuery(where, null);
		if(c != null){
			c.moveToFirst();
		}
		return c;
	}

	public Cursor getCriticalGreaterOrEqual(int inputNumCritical){
		String where = "SELECT * FROM " + DATABASE_RESTAURANT_TABLE + " WHERE trackingNumber in (SELECT trackingNumber FROM " + DATABASE_INSPECTION_TABLE + " GROUP BY trackingNumber HAVING SUM(numCritical) >= "+ inputNumCritical +" ) ORDER BY name ;";
		Cursor c = db.rawQuery(where, null);
		if(c != null){
			c.moveToFirst();
		}
		return c;
	}


	/////////////////////////////////////////////////////////////////////
	//	Inspection database functions:
	/////////////////////////////////////////////////////////////////////


	/*
		public static final String KEY_TRACKINGNUMBER = "trackingNumber";
	public static final String KEY_NAME = "name";
	public static final String KEY_PHYSICALCITY = "city";
	public static final String KEY_FACTYPE = "facilityType";
	public static final String KEY_LATITUDE = "lat";
	public static final String KEY_LONGITUDE = "long";

	KEY_ROWID, KEY_TRACKINGNUMBER ,KEY_INSPECTIONDATE, KEY_INSPTYPE, KEY_NUMCRITICAL, KEY_NUMNONCRITICAL, KEY_NUMNONCRITICAL, KEY_VIOLLUMP, KEY_HAZARDRATING}
	 */

	// Add a new set of values to the database.
	public long insertInspectionRow(String trackingNumber, String date, String inspType, String numCritical, String numNonCritical, String ViolLump, String hazardRating) {
		/*
		 * CHANGE 3:
		 */
		// TODO: Update data in the row with new fields.
		// TODO: Also change the function's arguments to be what you need!
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TRACKINGNUMBER, trackingNumber);
		initialValues.put(KEY_INSPECTIONDATE, date);
		initialValues.put(KEY_INSPTYPE, inspType);
		initialValues.put(KEY_NUMCRITICAL, numCritical);
		initialValues.put(KEY_NUMNONCRITICAL, numNonCritical);
		initialValues.put(KEY_VIOLLUMP, ViolLump);
		initialValues.put(KEY_HAZARDRATING, hazardRating);

		// Insert it into the database.
		return db.insert(DATABASE_INSPECTION_TABLE, null, initialValues);
	}


	// Delete a row from the database, by rowId (primary key)
	public boolean deleteInspectionRow(long rowId) {
		String where = KEY_INSPECTION_ROWID + "=" + rowId;
		return db.delete(DATABASE_INSPECTION_TABLE, where, null) != 0;
	}

	public void deleteAllInspections() {
		Cursor c = getAllInspectionRows();
		long rowId = c.getColumnIndexOrThrow(KEY_INSPECTION_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteInspectionRow(c.getLong((int) rowId));
			} while (c.moveToNext());
		}
		c.close();
	}

	// Return all data in the database.
	public Cursor getAllInspectionRows() {
		String where = null;
		Cursor c = 	db.query(true, DATABASE_INSPECTION_TABLE, ALL_INSPECTION_KEYS,
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Get a specific row (by rowId)
	public Cursor getInspectionRow(long rowId) {
		String where = KEY_INSPECTION_ROWID + "=" + rowId;
		Cursor c = 	db.query(true, DATABASE_INSPECTION_TABLE, ALL_INSPECTION_KEYS,
				where, null, null, null, null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public Cursor getMostRecentInspection(String trackingNumber){
		String where = KEY_INSPECTION_TRACKINGNUMBER + "='"+trackingNumber+"';";
		Cursor c = 	db.query(true, DATABASE_INSPECTION_TABLE, ALL_INSPECTION_KEYS,
				where, null, null, null, KEY_INSPECTIONDATE, String.valueOf(1));

		if (c != null) {
			c.moveToFirst();
		}
		return c;

	}

	public Cursor getAllInspections(String trackingNumber){
		String where = "SELECT All * FROM " + DATABASE_INSPECTION_TABLE + " WHERE trackingNumber = '" + trackingNumber + "';";
		Cursor c = db.rawQuery(where, null);
		if(c != null){
			c.moveToFirst();
		}
		return c;
	}

	// Change an existing row to be equal to new data.
	public boolean updateInspectionRow(long rowId, String trackingNumber, String date, String inspType, String numCritical, String numNonCritical, String ViolLump, String hazardRating) {
		String where = KEY_INSPECTION_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
		// TODO: Update data in the row with new fields.
		// TODO: Also change the function's arguments to be what you need!
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_TRACKINGNUMBER, trackingNumber);
		newValues.put(KEY_INSPECTIONDATE, date);
		newValues.put(KEY_INSPTYPE, inspType);
		newValues.put(KEY_NUMCRITICAL, numCritical);
		newValues.put(KEY_NUMNONCRITICAL, numNonCritical);
		newValues.put(KEY_VIOLLUMP, ViolLump);
		newValues.put(KEY_HAZARDRATING, hazardRating);
		// Insert it into the database.
		return db.update(DATABASE_INSPECTION_TABLE, newValues, where, null) != 0;
	}

	/////////////////////////////////////////////////////////////////////
	//	Private Helper Classes:
	/////////////////////////////////////////////////////////////////////


	public int countTables() {
		int count = 0;
		String SQL_GET_ALL_TABLES = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'";
		Cursor cursor = db
				.rawQuery(SQL_GET_ALL_TABLES, null);
		cursor.moveToFirst();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			count++;
			db.close();
		}
		cursor.close();
		return count;
	}
	
	/**
	 * Private class which handles database creation and upgrading.
	 * Used to handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_RESTAURANT_SQL);
			_db.execSQL(DATABASE_CREATE_INSPECTION_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data!");
			
			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_RESTAURANT_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_INSPECTION_TABLE);
			
			// Recreate new database:
			onCreate(_db);
		}
	}
}
