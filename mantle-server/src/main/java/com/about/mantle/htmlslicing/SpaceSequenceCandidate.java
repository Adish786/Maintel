package com.about.mantle.htmlslicing;

import java.util.Arrays;

/**
 * This class keeps track of the different types of spaces we can have and if they actually are space characters.
 * It allows up to keep track of how many characters the space is and split content accordingly.
 */
public class SpaceSequenceCandidate {
    private boolean isSpace;
    private char[] spaceCandidateChars;

    SpaceSequenceCandidate(Boolean isSpace, String spaceCandidateChars) {
        this.isSpace = isSpace;
        this.spaceCandidateChars = spaceCandidateChars.toCharArray();
    }

    public Boolean isSpace() {
        return isSpace;
    }

    public Integer getLength() {
        return spaceCandidateChars.length;
    }

    public boolean found(char [] chars, int index) {
        boolean found = false;

        if ((index + spaceCandidateChars.length) < chars.length) {
            found = true;
            for (int i = index; i < index + spaceCandidateChars.length; i++) {
                if (chars[i] != spaceCandidateChars[i - index]) {
                    found = false;
                    break;
                }
            }
        }
        return found;
    }

    @Override
    public String toString() {
        return "SpaceSequenceCandidate{" +
                "isSpace=" + isSpace +
                ", spaceCandidateChars=" + Arrays.toString(spaceCandidateChars) +
                '}';
    }
}
