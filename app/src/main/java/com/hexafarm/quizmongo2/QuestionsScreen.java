package com.hexafarm.quizmongo2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class QuestionsScreen extends ActionBarActivity {


    private ListView listView            = null;
    private QuestionsService qs          = null;
    private int currentQuestion          = 0;
    private Button previousBtn           = null;
    private Button nextBtn               = null;
    private int score                    = 0;
    private QuestionVO question          = null;
    private int selectedAnswer           = 0;
    private ArrayAdapter<String> adapter = null;
    private TextView question_tf         = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_screen);

        this.postInit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_questions_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postInit() {

        Intent intent = getIntent();

        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        qs = new QuestionsService(message);

        question = qs.questionsVO.get(currentQuestion); // get the first questions

        adapter = new ArrayAdapter<String>(QuestionsScreen.this, android.R.layout.simple_list_item_single_choice, question.getChoices());

        listView = (ListView) findViewById(R.id.lvitems);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setItemChecked(0, true);
        //listView.getChildAt(0).setBackgroundResource(R.color.selected_list_item_color);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedAnswer = position;
                        listView.setItemChecked(position, true);
                        //parent.getChildAt(position).setBackgroundResource(R.color.selected_list_item_color);
                        selectedAnswer = position;
                        Log.i(MainActivity.TAG, "position : " + position);
                    }
                }
        );

        question_tf = (TextView) findViewById(R.id.question);
        question_tf.setText(question.getQuestion());

        //if (currentQuestion == 0) {
        if (true) {
            previousBtn = (Button) findViewById(R.id.previousBtn);
            previousBtn.setEnabled(false);
        }

        nextBtn = (Button) findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(
                new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (selectedAnswer == question.getCorrectAnswer()) {
                            Log.i(MainActivity.TAG, "score++");
                            score += 1;
                        }

                        processNextQuestion();
                    }
                }
        );

    }

    private void processNextQuestion() {

        currentQuestion++;

        adapter.clear();

        selectedAnswer = 0; // re-init selectedAnswer because if the correct answer is 0 and nothing is clicked, the value of selectedAnswer will be the previous one

        if (currentQuestion < qs.questionsVO.size()) {

            listView.setItemChecked(0, true);

            question = qs.questionsVO.get(currentQuestion);

            adapter.addAll(question.getChoices());

            question_tf.setText(question.getQuestion());

            Log.i(MainActivity.TAG, "selectedAnswer : " + selectedAnswer);
            Log.i(MainActivity.TAG, "correct answer : " + question.getCorrectAnswer());

            /*if (selectedAnswer == question.getCorrectAnswer() ) {
                Log.i(MainActivity.TAG, "score++");
                score += 1;
            }*/
        } else {

            Log.i(MainActivity.TAG, "no more questions");
        }

        Log.i(MainActivity.TAG, "score : " + score);
    }
}