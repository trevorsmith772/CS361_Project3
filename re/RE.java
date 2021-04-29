package re;

import fa.State;
import fa.nfa.NFA;
import fa.nfa.NFAState;

/**
 * This class is used to create an NFA from a given
 * regular expression. This class uses recursive descent 
 * parsing and other recursive programming concepts.
 * 
 * @author Trevor Smith (trevorsmith772)
 * @author Roberto Cisneros (bertocisneros)
 * @author Brandon Mattaini (brandonmattaini)
 * Date: April 29, 2021
 */
public class RE implements REInterface {

    private String regEx; //Regular expression string
    private int numStates;

    /**
     * Constructor
     * @param regEx Regular expression string
     */
    public RE(String regEx) {
        this.regEx = regEx;
        numStates = 0;
    }

    @Override
    public NFA getNFA() {
        return regex();
    }


    /* Recursive Descent Methods */

	/**
	 * Returns the next item of input without consuming it
	 * @return char next item of input
	 */
    private char peek() {
        return regEx.charAt(0);
    }

	/**
	 * Consumes the next item of input, failing if not equal to item
	 * @param c next character getting compared in the regular expression
	 */
    private void eat(char c) {
        if (peek() == c) {
            regEx = regEx.substring(1);
        } else {    //wrong character expected/retrieved
            throw new RuntimeException("Expected: " + c + "; got: " + peek());
        }
    }

	/**
	 * Returns the next item of input and consumes it
	 * @return c next item of input being consumed
	 */
	private char next() {
        char c = peek();
        eat(c);
        return c;
    }

	/**
	 * Checks if there are any more characters in the expression
	 * @return boolean if the length is greater than 0
	 */
	private boolean more() {
        return regEx.length() > 0;
    }


    /* Regular Expression Parsing Methods */

	/**
	 * Parser for regular expression to form terms, using recursion
     * 
	 * @return NFA returns an NFA as a new term with or without a union
	 */
    private NFA regex() {
        NFA term = term();
        if (more() && peek() == '|') {
            eat('|');
            return union(term, regex());
        } else {
            return term;
        }
    }

	/**
	 * Checks if it has reached the boundary of a term or end of input
     * 
	 * @return NFA creates an NFA unioned from a single term
	 */
	private NFA term() {
        NFA factor = new NFA();
        factor.addStartState(Integer.toString(numStates));
        numStates++;
        String finalstate = Integer.toString(numStates);
        factor.addFinalState(Integer.toString(numStates));
        numStates++;
        factor.addTransition(factor.getStartState().getName(), 'e', finalstate);

        while (more() && peek() != ')' && peek() != '|') {
            NFA nextFactor = factor();
            factor = concatenate(factor, nextFactor);
        }

        return factor;
    }

	/**
	 * Parsing a base and Kleene stars
	 * @return NFA returns a base
	 */
	private NFA factor() {
        NFA base = base();
        while (more() && peek() == '*') {
            eat('*');
            base = repetition(base);
        }
        return base;
    }

	/**
	 * Check for parentheses, otherwise returns next primitive
	 * @return NFA from given symbol
	 */
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

    /* Additional Helper Methods */

    /**
     * Returns NFA if an or character is found
     * 
     * @param base - input NFA
     * @return the altered base NFA
     */
    public NFA repetition(NFA base) {
        NFAState nfaState = (NFAState) base.getStartState();
        for (State nfa : base.getFinalStates()) {
            base.addTransition(nfa.getName(), 'e', nfaState.getName());
        }

        String state = Integer.toString(numStates);
        numStates++;
        base.addStartState(state);
        base.addFinalState(state);
        base.addTransition(state, 'e', nfaState.getName());
        return base;
    }

    /**
     * Creates an NFA from a given symbol
     * 
     * @param c - the given symbol
     * @return the result NFA
     */
    public NFA primitive(char c) {
        NFA nfa = new NFA();

        String startState = Integer.toString(numStates);
        numStates++;
        nfa.addStartState(startState);

        String finalState = Integer.toString(numStates);
        numStates++;
        nfa.addFinalState(finalState);

        nfa.addTransition(startState, c, finalState);

        return nfa;
    }

    /**
     * Concatenates two NFAs
     * 
     * @param nfa - one NFA in operation
     * @param nfa2 - second NFA in operation
     * @return the concatenated result NFA
     */
    public NFA concatenate(NFA nfa, NFA nfa2) {
        nfa.addAbc(nfa2.getABC());
        for (State s : nfa.getFinalStates()) {
            NFAState state = (NFAState) s;
            state.setNonFinal();
            state.addTransition('e', (NFAState) nfa2.getStartState());
        }
        nfa.addNFAStates(nfa2.getStates());
        return nfa;
    }

    /**
     * Performs a union of two NFAs and returns the result
     * @param nfa - first NFA in union
     * @param nfa2 - second NFA in union
     * @return an NFA resulted from the union operation
     */
    public NFA union(NFA nfa, NFA nfa2) {
        NFAState nfaState = (NFAState) nfa.getStartState();
        NFAState nfaState2 = (NFAState) nfa2.getStartState();

        nfa.addNFAStates(nfa2.getStates());
        nfa.addAbc(nfa2.getABC());

        String startState = Integer.toString(numStates);
        numStates++;
        nfa.addStartState(startState);
        String finalState = Integer.toString(numStates);
        numStates++;
        nfa.addFinalState(finalState);

        nfa.addTransition(startState, 'e', nfaState.getName());
        nfa.addTransition(startState, 'e', nfaState2.getName());

        for (State s : nfa2.getFinalStates()) {
            NFAState state = (NFAState) s;
            state.setNonFinal();
            nfa.addTransition(state.getName(), 'e', finalState);
        }
        return nfa;
    }
}
