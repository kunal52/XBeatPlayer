package com.techweblearn.musicbeat.Models;

/**
 * Created by kunal on 17/1/18.
 */

public class FeedbackModel {

    String name;
    String email;
    String feedback;

    public FeedbackModel() {
    }

    public FeedbackModel(String name, String email, String feedback) {
        this.name = name;
        this.email = email;
        this.feedback = feedback;
    }


}
