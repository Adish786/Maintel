package com.about.mantle.infocat.property;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.about.mantle.infocat.model.QuestionAnswer;

public class QuestionListProperty extends Property<List<QuestionAnswer>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private QuestionListPropertyValue value;

    @Override
    public PropertyValue<List<QuestionAnswer>> getValue() {
        return value;
    }

    public static class QuestionListPropertyValue extends PropertyValue<List<QuestionAnswer>> {

        private List<QuestionAnswer> questionList = Collections.emptyList();

        public List<QuestionAnswer> getQuestionList() {return questionList;}

        public void setQuestionList(List<QuestionAnswer> questionList) {
            this.questionList = questionList;
        }

        @Override
        public List<QuestionAnswer> getPrimaryValue() {
            return questionList;
        }
    }
}
