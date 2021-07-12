package com.example.popmoviesapp.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.popmoviesapp.API.APIService;
import com.example.popmoviesapp.DBDatabase;
import com.example.popmoviesapp.R;
import com.example.popmoviesapp.Activity.DetailMovieActivity;
import com.example.popmoviesapp.Adapter.MovieAdapter;
import com.example.popmoviesapp.Model.ModelMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;


public class FragmentMovie extends Fragment implements  MovieAdapter.onSelectData, Runnable {

    private static final String TAG ="" ;
    private RecyclerView rvFilmRecommend;
    private MovieAdapter movieAdapter;
    private ProgressDialog progressDialog;
    private List<ModelMovie> moviePopular = new ArrayList<>();
    DBDatabase db;
    TextView txtNoData;

    public FragmentMovie() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_film, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        txtNoData = rootView.findViewById(R.id.tvNotFound);

        rvFilmRecommend = rootView.findViewById(R.id.rvFilmRecommend);
        rvFilmRecommend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFilmRecommend.setHasFixedSize(true);

        Intent intent = getActivity().getIntent();
        String id_movie = intent.getStringExtra("id");
        db = new DBDatabase(getContext());
        db.open();

        if (isNetworkAvailable()) {     //if true
            getMovie();
        }
        else {
            //if false
            ArrayList<ModelMovie> arrayList = new ArrayList<ModelMovie>(db.getNotes());
            Log.d(TAG, "setData: "+arrayList.size());
            if (arrayList.size() >0)
            {
            setData(arrayList);}
            else
            {
                ArrayList<ModelMovie> arrayList1 = new ArrayList<ModelMovie>(db.getNotes());
                Log.d(TAG, "setData: arrayList1 "+arrayList1.size());
                setData(arrayList1);
            }
        }

        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void run() {
       /* // todo: background tasks*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                db.getAllMoviews();
                /*Cursor cursor=new  DBDatabase(getActivity()).getAllMoviews();
                while (cursor.moveToFirst()) {
                    ModelMovie movie = new ModelMovie(
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getDouble(5),
                            cursor.getString(6));
                      cursor.moveToNext();
                    moviePopular.add(movie);
                    Log.d(TAG, " data");
                  *//*  // todo: update your ui / view in activity*//*
                }*/
                }
        });
    }

    private void runOnUiThread(Runnable runnable) {
    }

/*
    public class Backgroundtask extends AsyncTask<ModelMovie,Void,Void> {
        private RecyclerView rvFilmRecommend;
        private MovieAdapter movieAdapter;
        private ProgressDialog progressDialog;
        private List<ModelMovie> moviePopular = new ArrayList<>();
        DBDatabase db;
        private Context Context;

        public Backgroundtask(RecyclerView rvFilmRecommend, ProgressDialog progressDialog, android.content.Context context) {
            this.rvFilmRecommend = rvFilmRecommend;
            this.progressDialog = progressDialog;
            Context = context;
        }


        @Override
        protected Void doInBackground(ModelMovie... voids) {
            db = new DBDatabase(Context);
            db.getAllMoviews();
            Cursor cursor=new  DBDatabase(getActivity()).getAllMoviews();
            while (cursor.moveToFirst()){
                ModelMovie movie=new ModelMovie(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getString(6));
                cursor.moveToNext();
                moviePopular.add(movie);
                //Log.d(TAG," data");
            }
            return null;
        }

    }
*/

    private void getMovie() {
        progressDialog.show();
        AndroidNetworking.get(APIService.BASEURL + APIService.MOVIE_TOP_RATED + APIService.APIKEY + APIService.LANGUAGE)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            moviePopular = new ArrayList<>();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ModelMovie dataApi = new ModelMovie();
                                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMMM yyyy");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                                String datePost = jsonObject.getString("release_date");

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setTitle(jsonObject.getString("title"));
                                dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setReleaseDate(formatter.format(dateFormat.parse(datePost)));
                                dataApi.setPosterPath(jsonObject.getString("poster_path"));
                                dataApi.setBackdropPath(jsonObject.getString("backdrop_path"));
                                dataApi.setPopularity(jsonObject.getString("popularity"));
                                saveData(  dataApi);
                                moviePopular.add(dataApi);
                                showMovie();
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            db.getAllMoviews();
                            ArrayList<ModelMovie> arrayList = new ArrayList<ModelMovie>(db.getNotes());
                            Log.d(TAG, "setData: "+arrayList.size());
                            if (arrayList.size() >0)
                            {
                                setData(arrayList);}
                           // setData(arrayList);
                          //  Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                       db.getAllMoviews();
                        progressDialog.dismiss();
                      //  Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveData(ModelMovie dataApi) {
        if (dataApi!=null){
            int id_movie = dataApi.getId();
            String original_title = dataApi.getTitle();
            String posterPath = dataApi.getPosterPath();
            String overview = dataApi.getOverview();
            Double voteAverage = dataApi.getVoteAverage();
            String releaseDate = dataApi.getReleaseDate();
            db.add(id_movie, original_title, posterPath, overview, voteAverage, releaseDate);
            Toast.makeText(getContext(), "Added to db", Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(getContext(), "not Added to db", Toast.LENGTH_SHORT).show();

        }
    }


    private void setData(ArrayList<ModelMovie> arrayList) {

        if (arrayList.size() == 0) {
            txtNoData.setVisibility(View.VISIBLE);
            rvFilmRecommend.setVisibility(View.GONE);
        }
        else {
            txtNoData.setVisibility(View.GONE);
            rvFilmRecommend.setVisibility(View.VISIBLE);


            Runnable someRunnable = new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           /* Cursor cursor=new  DBDatabase(getActivity()).getAllMoviews();
                            while (cursor.moveToFirst()) {
                                ModelMovie movie = new ModelMovie(
                                        cursor.getInt(1),
                                        cursor.getString(2),
                                        cursor.getString(3),
                                        cursor.getString(4),
                                        cursor.getDouble(5),
                                        cursor.getString(6));
                                cursor.moveToNext();
                              //
                                Log.d(TAG, " data");

                            }*/
                            //moviePopular.add((ModelMovie) arrayList.clone());
                            Collections.copy(moviePopular,arrayList);
                        }
                    });

                }
            };

            Executors.newSingleThreadExecutor().execute(someRunnable);
            //  Executors.newSingleThreadExecutor().execute(this::run);
            // new Backgroundtask(rvFilmRecommend,progressDialog,getContext()).execute();
            /*Cursor cursor=new  DBDatabase(getActivity()).getAllMoviews();
            while (cursor.moveToFirst()){
                ModelMovie movie=new ModelMovie(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getString(6));
              //  cursor.moveToNext();

                moviePopular.add(movie);
                Log.d(TAG," data");
            }*/

            movieAdapter = new MovieAdapter(getActivity(), arrayList, this);
            rvFilmRecommend.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
        }



        /* if (db.getAllMoviews() == null) {
            txtNoData.setVisibility(View.VISIBLE);
            rvFilmRecommend.setVisibility(View.GONE);
        }
        else {
            txtNoData.setVisibility(View.GONE);
            rvFilmRecommend.setVisibility(View.VISIBLE);

            Runnable someRunnable = new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor=new  DBDatabase(getActivity()).getAllMoviews();
                            while (cursor.moveToFirst()) {
                                ModelMovie movie = new ModelMovie(
                                        cursor.getInt(1),
                                        cursor.getString(2),
                                        cursor.getString(3),
                                        cursor.getString(4),
                                        cursor.getDouble(5),
                                        cursor.getString(6));
                                cursor.moveToNext();
                                moviePopular.add(movie);
                                Log.d(TAG, " data");

                            }
                        }
                    });

                }
            };

           // Executors.newSingleThreadExecutor().execute(someRunnable);
            //  Executors.newSingleThreadExecutor().execute(this::run);
            // new Backgroundtask(rvFilmRecommend,progressDialog,getContext()).execute();
            *//*Cursor cursor=new  DBDatabase(getActivity()).getAllMoviews();
            while (cursor.moveToFirst()){
                ModelMovie movie=new ModelMovie(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getString(6));
              //  cursor.moveToNext();

                moviePopular.add(movie);
                Log.d(TAG," data");
            }*//*

            movieAdapter = new MovieAdapter(getActivity(), moviePopular, this);
            rvFilmRecommend.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
        }*/
    }
    private void showMovie() {
        movieAdapter = new MovieAdapter(getActivity(), moviePopular, this);
        rvFilmRecommend.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelected(ModelMovie modelMovie) {
        Intent intent = new Intent(getActivity(), DetailMovieActivity.class);
        intent.putExtra("detailMovie", modelMovie);
        startActivity(intent);
    }
}