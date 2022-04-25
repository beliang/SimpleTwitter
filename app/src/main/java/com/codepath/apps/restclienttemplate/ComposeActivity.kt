package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.w3c.dom.Text

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var buttonTweet: Button

    lateinit var client: TwitterClient

    lateinit var wordCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.composeTweet)
        buttonTweet = findViewById(R.id.buttonTweet)
        wordCount = findViewById(R.id.charLimit)

        client = TwitterApplication.getRestClient(this)

        // Handling click of tweet button
        buttonTweet.setOnClickListener {

            //Grab the content of editText (etCompose)
            val tweetContent = etCompose.text.toString()

            // 1. Make sure tweet is not empty
            if(tweetContent.isEmpty()) {
                Toast.makeText(this, "Tweet is empty!", Toast.LENGTH_SHORT).show()
                // SnackBar
            }
            // 2. Make sure tweet is under character count
            else if(tweetContent.length > 280) {
                Toast.makeText(this, "Tweet is too long! Limit is 280 characters",
                    Toast.LENGTH_SHORT).show()
            }

            else {
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet")
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully published Tweet!")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()

                    }

                })
            }
        }

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                wordCount.text = "Character count: " + etCompose.length()
            }

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                buttonTweet.isEnabled = etCompose.length() <= 280
                if(!buttonTweet.isEnabled)
                {
                    wordCount.setTextColor(Color.parseColor("#FF0000"))
                }
                else
                {
                    wordCount.setTextColor(Color.parseColor("#000000"))
                }
                wordCount.text = "Character count: " + etCompose.length()

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
    companion object {
        val TAG = "ComposeActivity"
    }
}

