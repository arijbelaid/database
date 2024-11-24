package com.example.testdb

import android.content.ContentValues
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.BaseColumns
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testdb.FeedReaderDbHelper.FeedReaderContract
import com.example.testdb.ui.theme.TestDBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val dbHelper= FeedReaderDbHelper(this)

        enableEdgeToEdge()
        setContent {
            TestDBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)

                    )

                    ButtonComposable(
                        text = "insert",
                        onClick = { insertDB(dbHelper)

                        }
                    )
                    ButtonComposable(text = "read"){ readDB(dbHelper) }
                    ButtonComposable(text = "delete"){ deleteDB(dbHelper) }
                    ButtonComposable(text = "update"){ updateDB(dbHelper) }


                }
            }
        }
    }
}
fun insertDB(dbHelper: FeedReaderDbHelper){
    // Gets the data repository in write mode
    val db = dbHelper.writableDatabase

// Create a new map of values, where column names are the keys
    val values = ContentValues().apply {
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, "title")
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "subtitle")
    }

// Insert the new row, returning the primary key value of the new row
    val newRowId = db?.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)

    println("Inserted row id: $newRowId")

}
//Lire les informations d'une base de données



fun readDB(dbHelper: FeedReaderDbHelper) {
    val db = dbHelper.readableDatabase

// Define a projection that specifies which columns from the database
// you will actually use after this query.
    val projection = arrayOf(
        BaseColumns._ID,
        FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
        FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE
    )

// Filter results WHERE "title" = 'My Title'
    val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
    val selectionArgs = arrayOf("My Title")

// How you want the results sorted in the resulting Cursor
    val sortOrder = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

    val cursor = db.query(
        FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
        projection ,           // The array of columns to return (pass null to get all)
        selection,            // The columns for the WHERE clause
        selectionArgs,          // The values for the WHERE clause
        null,                   // don't group the rows
        null,                   // don't filter by row groups
        sortOrder               // The sort order
    )
    val itemIds = mutableListOf<Long>()
    val itemTitles= mutableListOf<String>()
    with(cursor) {
        while (moveToNext()) {
            val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
            val itemTitle =getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE))
            itemIds.add(itemId)
            itemTitles.add(itemTitle)
        }
    }
    cursor.close()
    println("Item IDs:$itemIds")
    println("Item Titles: $itemTitles")
}

//Supprimer des informations d'une base de données
fun deleteDB(dbHelper: FeedReaderDbHelper) {
    val db = dbHelper.writableDatabase

    // Define 'where' part of query.
    val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
    // Specify arguments in placeholder order.
    val selectionArgs = arrayOf("MyTitle")
    // Issue SQL statement.
    val deletedRows = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs)

    println("Deleted rows count: $deletedRows")
}


//Mettre à jour une base de données
fun updateDB(dbHelper: FeedReaderDbHelper){
    val db = dbHelper.writableDatabase

// New value for one column
    val title = "MyNewTitle"
    val values = ContentValues().apply {
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title)
    }

// Which row to update, based on the title
    val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
    val selectionArgs = arrayOf("MyOldTitle")
    val count = db.update(
        FeedReaderContract.FeedEntry.TABLE_NAME,
        values,
        selection,
        selectionArgs)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Composable
fun ButtonComposable(modifier: Modifier = Modifier,text:String,onClick:() -> Unit){
    Button(onClick = onClick) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestDBTheme {
        Greeting("Android")
    }
}