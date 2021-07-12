package com.example.popmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.popmoviesapp.Fragment.FragmentMovie;
import com.example.popmoviesapp.Model.ModelMovie;

import java.util.ArrayList;
import java.util.List;

import static com.example.popmoviesapp.DBHelper.TABLE_NAME;
import static com.example.popmoviesapp.DBHelper.TABLE_NAME1;

public class DBDatabase {

    Context c;
    SQLiteDatabase database;
    DBHelper dbHelper;
    private List<ModelMovie> movielist = new ArrayList<>();


    public DBDatabase(Context c, SQLiteDatabase database, DBHelper dbHelper) {
        this.c = c;
        this.database = database;
        this.dbHelper = dbHelper;
    }

public DBDatabase(Context context){ dbHelper=new DBHelper(context); }

    public void open() {
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            dbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long  add(int id_movie, String original_title, String posterPath, String overview,
                    Double voteAverage, String releaseDate)
    {

        database=dbHelper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put(DBHelper.COLUMN_ID_MOVIE,id_movie);
            cv.put(DBHelper.COLUMN_ORIGINAL_TITLE,original_title);
            cv.put(DBHelper.COLUMN_POSTER_PATH,posterPath);
            cv.put(DBHelper.COLUMN_OVERVIEW,overview);
            cv.put(DBHelper.COLUMN_VOTE_AVERAGE,voteAverage);
            cv.put(DBHelper.COLUMN_RELEASE_DATE,releaseDate);

          return database.insertWithOnConflict(TABLE_NAME,DBHelper.COLUMN_ID,cv,SQLiteDatabase.CONFLICT_REPLACE);

       /* if(checkIfMyTitleExists(id_movie))
        {
            return  true;
        }
        // return database.insert(TABLE_NAME1,DBHelper.COLUMN_ID,cv);
        return false;*/

    }

    public long add1(int id_movie, String original_title, String posterPath, String overview,
                    Double voteAverage, String releaseDate)
    {
        database=dbHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBHelper.COLUMN_ID_MOVIE,id_movie);
        cv.put(DBHelper.COLUMN_ORIGINAL_TITLE,original_title);
        cv.put(DBHelper.COLUMN_POSTER_PATH,posterPath);
        cv.put(DBHelper.COLUMN_OVERVIEW,overview);
        cv.put(DBHelper.COLUMN_VOTE_AVERAGE,voteAverage);
        cv.put(DBHelper.COLUMN_RELEASE_DATE,releaseDate);
        return database.insertWithOnConflict(TABLE_NAME1,DBHelper.COLUMN_ID,cv,SQLiteDatabase.CONFLICT_REPLACE);

    }
    public boolean checkIfMyTitleExists(int id_movie) {
        String Query = "Select * from " + TABLE_NAME + " where " + id_movie;
        Cursor cursor = database.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public Cursor  getAllMoviews()
    {
        //database=dbHelper.getReadableDatabase();
       /* String qry="SELECT * FROM "+TABLE_NAME;
        Cursor cursor=database.rawQuery(qry,null);
        */
        ArrayList<ModelMovie> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT *FROM " + TABLE_NAME;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);
        return cursor;


    }
    public Cursor getAllMoviews1()
    {/*
        database=dbHelper.getReadableDatabase();
        String qry="SELECT   * FROM "+TABLE_NAME1;
        Cursor cursor=database.rawQuery(qry,null);
        return cursor;*/
        ArrayList<ModelMovie> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT *FROM " + TABLE_NAME1;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);
        return cursor;

    }

    //get the all notes
    public ArrayList<ModelMovie>getNotes() {
        ArrayList<ModelMovie> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT *FROM " + TABLE_NAME;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModelMovie noteModel = new ModelMovie();
                noteModel.setId(cursor.getInt(1));
                noteModel.setTitle(cursor.getString(2));
                noteModel.setVoteAverage(cursor.getDouble(3));
                noteModel.setOverview(cursor.getString(4));
                noteModel.setReleaseDate(cursor.getString(5));
                noteModel.setPosterPath(cursor.getString(6));
                arrayList.add(noteModel);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }

    public ArrayList<ModelMovie>getpopularmovie() {
        ArrayList<ModelMovie> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT *FROM " + TABLE_NAME1;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModelMovie noteModel = new ModelMovie();
                noteModel.setId(cursor.getInt(1));
                noteModel.setTitle(cursor.getString(2));
                noteModel.setVoteAverage(cursor.getDouble(3));
                noteModel.setOverview(cursor.getString(4));
                noteModel.setReleaseDate(cursor.getString(5));
                noteModel.setPosterPath(cursor.getString(6));
                arrayList.add(noteModel);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }

    public long delete()
    {
        if(database!=null){
            database=dbHelper.getReadableDatabase();
            String qry="DELETE  FROM "+TABLE_NAME;
            database.rawQuery(qry,null);

        }
        return 0;
    }
    /*public long delete(String id_movie)

    {
        try
        {
            return database.delete(TABLE_NAME,DBHelper.COLUMN_ID_MOVIE+" =?",new String[]{id_movie});

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
*/
/*
    public boolean ifExists(String id_movie) {
        SQLiteDatabase res = getAllMoviews();
        int flag = 0;
        while (res.moveToNext()) {
            String id_movies = res.getString(1);
            if (id_movies.equals(id_movie)) {
                flag++;
            }
        }
        if (flag==0) {
            return false;
        } else {
            return true;
        }
    }
*/
}
