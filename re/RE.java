package re;

import fa.State;
import fa.dfa.DFA;
import fa.nfa.NFA;
import fa.nfa.NFAState;

public class RE implements REInterface {

    private String regEx;
    private int stateCount;

    public RE(String regEx) {
        this.regEx = regEx;
        stateCount = 0;
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

    public NFA repetition(NFA base){
		NFAState nfaState = (NFAState) base.getStartState();
		for(State nfa : base.getFinalStates()){
			base.addTransition(nfa.getName(), 'e', nfaState.getName());
		}

		String state = Integer.toString(stateCount++);
		base.addStartState(state);
		base.addFinalState(state);
		base.addTransition(state, 'e', nfaState.getName());
		return base;
	}

    public NFA primitive(char c){
		NFA nfa = new NFA();

		String startState = Integer.toString(stateCount++);
		nfa.addStartState(startState);

		String finalState = Integer.toString(stateCount++);
		nfa.addFinalState(finalState);

		nfa.addTransition(startState, c, finalState);

		return nfa;
	}

    public NFA concatenate(NFA nfa,NFA nfa2){
		//System.out.println("concat");
		nfa.addAbc(nfa2.getABC());
		for(State s:nfa.getFinalStates()){
			NFAState state = (NFAState)s;
			state.setNonFinal();
			state.addTransition( 'e', (NFAState)nfa2.getStartState());
		}
		nfa.addNFAStates(nfa2.getStates());
		return nfa;
	}

    public NFA union(NFA nfa, NFA nfa2){
		//System.out.println("union");
		NFAState nfaState = (NFAState) nfa.getStartState();
		NFAState nfaState2 = (NFAState) nfa2.getStartState();

		nfa.addNFAStates(nfa2.getStates());
		nfa.addAbc(nfa2.getABC());

		String startState = Integer.toString(stateCount++);
		nfa.addStartState(startState);
		String finalState = Integer.toString(stateCount++);
		nfa.addFinalState(finalState);

		nfa.addTransition(startState, 'e', nfaState.getName());
		nfa.addTransition(startState, 'e', nfaState2.getName());  

		for(State s:nfa2.getFinalStates()){
			NFAState state = (NFAState)s;
			state.setNonFinal();
			nfa.addTransition(state.getName(), 'e', finalState);
		}
		return nfa; 
	}
}
