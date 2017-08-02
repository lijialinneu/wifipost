package lijialin.wifipost.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "wifipost.db"; //数据库名称
    private static final int version = 1; //数据库版本

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "DROP TABLE IF EXISTS user";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS user(username varchar(50) not null, " +
                "password varchar(50) not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}