package com.learner.model.questions;

import java.util.UUID;

/**
 * Generates the info needed for question types:
 * - Fill the blank (FITB),
 * - Matching,
 * - Sequencing
 * Based upon info provided by textObject uuid
 */
public class QuestionFactory {

    public static Question createQuestion(QuestionType type, UUID textObjectUUID) {
        
        Question question;

        switch (type) {
            case FITB:
                question = new FITBQuestion(textObjectUUID);
                break;
            case MATCHING:
                question = new MatchingQuestion(textObjectUUID);
                break;
            case SEQUENCING:
                question = new SequencingQuestion(textObjectUUID);
                break;
            default:
                throw new IllegalArgumentException("Unsupported question type for creation/generation: " + type);
        }

        question.generateQuestion();

        return question;
    }
}
