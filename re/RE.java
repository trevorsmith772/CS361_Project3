package re;

import fa.State;
import fa.dfa.DFA;
import fa.nfa.NFA;
import fa.nfa.NFAState;

public class RE implements REInterface {

    private String regEx;
    private int count;

    public RE(String regEx) {
        this.regEx = regEx;
    }

    @Override
    public NFA getNFA() {
        return regex();
    }




    private NFA regex() {
        NFA term = term();

        if (more() && peek() == '|') {
            eat('|');
            return union(term, regex());
        }else{
            return term;
        }
    }

    private boolean more() {
        return regEx.length() > 0;
    }

    private char peek() {
        return regEx.charAt(0);
    }

    private void eat(char c) {
        if (peek() == c){
            regEx = regEx.substring(1);
        }else{
            throw new RuntimeException("Expected: " + c + "; got: " + peek());
        }
    }





}
