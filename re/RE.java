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

	private NFA base() {
		switch (peek()) {
		case '(':
			eat('(');
			NFA nfa = regex();
			eat(')');
			return nfa;

		default:
			return primitive(next());
		}
	}

    private NFA factor() {
		NFA base = base() ;
		while (more() && peek() == '*') {
			eat('*') ;
			base =  repetition(base) ;
		}
		return base ;
	}

	private char next() {
		char c = peek();
		eat(c);
		return c;
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

	private NFA term() {
		NFA factor = new NFA(); 
		
		factor.addStartState(Integer.toString(stateCount++));
		String finalstate = Integer.toString(stateCount);
		factor.addFinalState(Integer.toString(stateCount++));
		factor.addTransition(factor.getStartState().getName(), 'e', finalstate);

		while (more() && peek() != ')' && peek() != '|') {
			NFA nextFactor = factor();
			factor = concatenate(factor, nextFactor); // factor = new Sequence(factor,nextFactor) ;
		}

		return factor;
	}

}
