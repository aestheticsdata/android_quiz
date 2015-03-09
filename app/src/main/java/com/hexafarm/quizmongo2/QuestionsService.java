package com.hexafarm.quizmongo2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class QuestionsService implements Serializable {

    public JSONArray jsonQuestions = null;
    public String json = null;
    public List<QuestionVO> questionsVO = new ArrayList<>();

    QuestionsService(String json) {
        this.json = json;
        try {
            jsonQuestions = new JSONArray(json);

            for(int i=0; i<jsonQuestions.length(); i++) {

                JSONObject jsonObject = (JSONObject) jsonQuestions.get(i);

                String question = jsonObject.get("question").toString();

                int correctAnswer = -1;
                //Log.i(MainActivity.TAG, "correctAnswer type" + jsonObject.get("correctAnswer").getClass().getName());

                //////////////////////////////////////////////////////////////////////////////
                // A bug in the backoffice : when creating a question the correct answer is an
                // integer, but when the question is edited (even if not modified) the correct
                // answer become a string

                if (jsonObject.get("correctAnswer") instanceof Integer) {
                    //Log.i(MainActivity.TAG, "correct answer is an integer");
                    correctAnswer = (Integer) jsonObject.get("correctAnswer");
                } else {
                    //Log.i(MainActivity.TAG, "correct answer is a String");
                    correctAnswer = Integer.parseInt((String) jsonObject.get("correctAnswer"));
                }

                //////////////////////////////////////////////////////////////////////////////


                JSONArray jsonArray = (JSONArray)jsonObject.get("choices");
                ArrayList<String> choices = new ArrayList<>();
                for (int j=0; j<jsonArray.length(); j++) {
                    choices.add(jsonArray.get(j).toString());
                }

                questionsVO.add(new QuestionVO(question, choices, correctAnswer));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
