package com.example.paddy.com594assignment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class SOFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    TextView question1;
    TextView question2;
    TextView question3;
    TextView question4;
    SearchView questionSearch;
    TableRow tableRowQuestionOne;
    TableRow tableRowQuestionTwo;
    TableRow tableRowQuestionThree;
    TableRow tableRowQuestionFour;
    HashMap<String, String> questionList = new HashMap<String, String>();


    public SOFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_so, container, false);
        question1 = view.findViewById(R.id.textView_question1);
        question2 = view.findViewById(R.id.textView_question2);
        question3 = view.findViewById(R.id.textView_question3);
        question4 = view.findViewById(R.id.textView_question4);
        questionSearch = view.findViewById(R.id.searchView_question);
        tableRowQuestionOne = view.findViewById(R.id.tableRow_questionOne);
        tableRowQuestionTwo = view.findViewById(R.id.tableRow_questionTwo);
        tableRowQuestionThree = view.findViewById(R.id.tableRow_questionThree);
        tableRowQuestionFour = view.findViewById(R.id.tableRow_questionFour);

        if(questionList.size() != 0){
            question1.setText(questionList.get("question 1"));
            question2.setText(questionList.get("question 2"));
            question3.setText(questionList.get("question 3"));
            question4.setText(questionList.get("question 4"));
        }
        else{
            getInitialQuestions();
        }

        tableRowQuestionOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTableRowClicked("one");
            }
        });
        tableRowQuestionTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTableRowClicked("two");
            }
        });
        tableRowQuestionThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTableRowClicked("three");
            }
        });
        tableRowQuestionFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTableRowClicked("four");
            }
        });

        questionSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getQuestions(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if(!isNetworkAvailable()){
            Toast.makeText(getActivity().getApplicationContext(), "NO ACTIVE INTERNET CONNECTION", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getQuestions(String question){
        String url = "http://api.stackexchange.com/2.2/search?order=desc&sort=activity&intitle="
                + question
                + "&site=stackoverflow";
        new GetStackOverflowQuestion().execute(url);
    }

    public void getInitialQuestions(){
        String url = "https://api.stackexchange.com/2.2/questions/featured?order=desc&sort=activity&site=stackoverflow";

        new GetStackOverflowQuestion().execute(url);
    }

    public void onTableRowClicked(String question){
        Fragment fragment = new SOQuestionFragment();
        Bundle args = new Bundle();
        if(question.equals("one")){
            args.putString("questionLink", questionList.get("questionLink 1"));
        }
        else if(question.equals("two")){
            args.putString("questionLink", questionList.get("questionLink 2"));
        }
        else if(question.equals("three")){
            args.putString("questionLink", questionList.get("questionLink 3"));
        }
        else if(question.equals("four")){
            args.putString("questionLink", questionList.get("questionLink 4"));
        }
        fragment.setArguments(args);

        if(fragment != null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.container_frame_SO, fragment);
            fragmentTransaction.replace(R.id.container_frame_SO, fragment);
            fragmentTransaction.addToBackStack("SOQuestions");
            fragmentTransaction.commit();
        }
    }

    private class GetStackOverflowQuestion extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {


            try{
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String inputString;
                while((inputString = bufferedReader.readLine()) != null){
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                JSONArray items = topLevel.getJSONArray("items");
                for(int i = 0; i < items.length(); i++){
                    JSONObject question = items.getJSONObject(i);
                    String questionString = question.getString("title");
                    String questionLink = question.getString("link");
                    questionList.put("question " + (i+1), questionString);
                    questionList.put("questionLink " + (i+1), questionLink);
                }
                urlConnection.disconnect();
            } catch (IOException | JSONException e){
                e.printStackTrace();
            }
            return questionList;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> questionList){
            question1.setText(questionList.get("question 1"));
            question2.setText(questionList.get("question 2"));
            question3.setText(questionList.get("question 3"));
            question4.setText(questionList.get("question 4"));
        }
    }


}
