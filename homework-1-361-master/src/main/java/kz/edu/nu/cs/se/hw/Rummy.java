package kz.edu.nu.cs.se.hw;

import java.util.*;

/**
 * Starter code for a class that implements the <code>PlayableRummy</code>
 * interface. A constructor signature has been added, and method stubs have been
 * generated automatically in eclipse.
 * 
 * Before coding you should verify that you are able to run the accompanying
 * JUnit test suite <code>TestRummyCode</code>. Most of the unit tests will fail
 * initially.
 * 
 * @see PlayableRummy* @see TestRummyCode

 *
 */
public class Rummy implements PlayableRummy {
	
	private String[] playersList;
	private int currentPlayer;

	private ArrayList<String> deck = new ArrayList<String>();

	private Map<String, ArrayList<String>> playersHands = new HashMap<>();
	private ArrayList<ArrayList<String>> meld = new ArrayList<ArrayList<String>>();
	private ArrayList<String> discard = new ArrayList<String>();

	private Steps step = Steps.WAITING;
	private String discardOn;
	
    public Rummy(String... players) throws RummyException {

		String[] ranks = new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };
		String[] suits = new String[] { "C", "D", "H", "S", "M" };

		for (String suit : suits) {

			for (String rank : ranks) {
				rearrange(rank + suit);
			}

		}

    	if(players.length > 6) {
    		throw new RummyException("Need less players!", 8);
    	} 
    	else if(players.length < 2) {
    		throw new RummyException("Need more players!", 2);
    	}

    	playersList = players;
    	for(String player: playersList) {
			playersHands.put(player, new ArrayList<String>());
    	}

    }

    @Override
    public String[] getPlayers() {
    	return playersList;
	}

    @Override
    public int getNumPlayers() {
    	return playersList.length;
    }

    @Override
    public int getCurrentPlayer() {
    	return currentPlayer;
    }

    @Override
    public int getNumCardsInDeck() {
    	return deck.size();
    }

    @Override
    public int getNumCardsInDiscardPile() {
    	return discard.size();
    }

    @Override
    public String getTopCardOfDiscardPile() {
    	return discard.get(discard.size() - 1);
    }

    @Override
    public String[] getHandOfPlayer(int player) {
    	
    	if(player <= getNumPlayers() - 1 && player >= 0) {
			return playersHands.get(playersList[player]).toArray(new String[playersHands.get(playersList[player]).size()]);

    	}
    	else{
			throw new RummyException("No such player!", 10);
		}

    }

    @Override
    public int getNumMelds() {
    	return meld.size();
    }

    @Override
    public String[] getMeld(int i) {

		if(i <= getNumMelds() - 1 && i >= 0) {
			return meld.get(i).toArray(new String[meld.get(i).size()]);

		}
		else{
			throw new RummyException("Not valid index!", 11);
		}

    }

    @Override
    public void rearrange(String card) {
    	
        if(getCurrentStep() == Steps.WAITING) {
        	
        	if(deck.contains(card)) {
             	deck.remove(card);
        	}

        	deck.add(card);
             
        }

        else {
             throw new RummyException("Not WAITING state!", 3); 
        }
              
    }

    @Override
    public void shuffle(Long l) {
       
    	if(getCurrentStep() == Steps.WAITING) {
    		
    		Random random = new Random();
        	random.setSeed(l);
        	
        	for(int i = 0; i < getNumCardsInDeck(); i ++) {

        		int rand = i + random.nextInt(getNumCardsInDeck() - i);
        		Collections.swap(deck, rand, i);

        	}
        	
    	}
        
    	else {
    		throw new RummyException("Not WAITING state!", 3);
    	}
    	
    }

    @Override
    public Steps getCurrentStep() {
        return step;
    }

    @Override
    public int isFinished() {
    	
    	if(getCurrentStep() == Steps.FINISHED) {
    		return getCurrentPlayer();
    	}
    	else {
    		return -1;   	        
    	}
      
    }

    @Override
    public void initialDeal() {
        
    	if(getCurrentStep() == Steps.WAITING) {
    		
    		int numCards;

        	if(getNumPlayers() > 2) {
				numCards = 7;
			}
        	else if(getNumPlayers() > 4) {
        		numCards = 6;
        	}
        	else {
        		numCards = 10;
        	}
        	
        	for(int i = 0; i < numCards; i ++) {

        		for(int j = 0; j < getNumPlayers(); j ++) {

					step = Steps.DRAW;

					playersHands.get(playersList[getCurrentPlayer()]).add(deck.get(getNumCardsInDeck() - 1));

            		deck.remove(getNumCardsInDeck() - 1);

            		if(getCurrentPlayer() < getNumPlayers() - 1) {
            			currentPlayer ++;
            		}
            		else {
            			currentPlayer = 0;
            		}

        		}

        	}
        	
        	discard.add(deck.get(getNumCardsInDeck() - 1));
    		deck.remove(getNumCardsInDeck() - 1);
    		
    	}
    	
    	else {
    		throw new RummyException("Not WAITING state!", 3);
    	}
    	  	
    }

    @Override
    public void drawFromDiscard() {
        
    	if(getCurrentStep() == Steps.DRAW) {

			step = Steps.MELD;

			playersHands.get(playersList[getCurrentPlayer()]).add(getTopCardOfDiscardPile());

			discardOn = discard.get(getNumCardsInDiscardPile() - 1);
        	discard.remove(getNumCardsInDiscardPile()-1);
    		
    	}
    	
    	else {
    		throw new RummyException("Not DRAW state!", 4);
    	}
    	    	
    }

    @Override
    public void drawFromDeck() {
        
    	if(getCurrentStep() == Steps.DRAW) {
    		
    		Collections.reverse(discard);
            
        	if(getNumCardsInDeck() == 0) {
        		
        		deck = new ArrayList<String>(discard);

        		discard.clear();
        		discard.add(deck.get(getNumCardsInDeck()-1));

        		deck.remove(getNumCardsInDeck()-1);
        		
        	}

			step = Steps.MELD;

			playersHands.get(playersList[getCurrentPlayer()]).add(deck.get(getNumCardsInDeck() - 1));
        	deck.remove(getNumCardsInDeck() - 1);

    	}
    	
    	else {
    		throw new RummyException("Not DRAW state!", 4);
    	}
    	
    }

    @Override
    public void meld(String... cards) {

		if(getCurrentStep() == Steps.MELD || step == Steps.RUMMY) {

			boolean checker = false;

			for(String card : cards) {

				if(playersHands.get(playersList[getCurrentPlayer()]).contains(card) != true){
					throw new RummyException("Expected cards!", 7);
				}

			}

			String[] list = new String[]{"A", "2", "3", "4", "5", "6", "7", "8", "9", "1", "J", "Q", "K"};
			List<String> ranks = Arrays.asList(list);

			char suit = cards[0].charAt(cards[0].length() - 1);
			char rank = cards[0].charAt(0);

			boolean checker1 = false;
			boolean checker2 = false;

			for(String card : cards) {

				if(suit != card.charAt(card.length() - 1)) {

					checker2 = false;
					break;

				}
				else {
					checker2 = true;
				}

			}

			for(String card : cards) {

				if(rank != card.charAt(0)) {

					checker1 = false;
					break;

				}
				else {
					checker1 = true;
				}

			}

			for(int i = 1; i < cards.length; i ++) {

				if (checker2 == false) {
					break;
				}

				String str1 = Character.toString(cards[i - 1].charAt(0));
				String str2 = Character.toString(cards[i].charAt(0));

				if ((ranks.indexOf(str2) - ranks.indexOf(str1) != - 1) && (ranks.indexOf(str2) - ranks.indexOf(str1) != 1)) {

					checker = false;
					break;

				}
				else {
					checker = true;
				}

			}

			meld.add(new ArrayList<String>(Arrays.asList(cards)));

			if((checker == false || checker2 == false) && (checker1 == false)) {
				throw new RummyException("Not valid meld!", 1);
			}

			for(String card: cards) {
				playersHands.get(playersList[getCurrentPlayer()]).remove(card);
			}

			if(playersHands.get(playersList[getCurrentPlayer()]).size() <= 1 && step == Steps.RUMMY) {
				step = Steps.FINISHED;
			}

		}

		else{
			throw new RummyException("Not MELD or RUMMY states!", 15);
		}

	}

    @Override
    public void addToMeld(int meldIndex, String... cards) {

		if(getCurrentStep() == Steps.MELD || getCurrentStep() == Steps.RUMMY) {

			String[] list = new String[]{"A", "2", "3", "4", "5", "6", "7", "8", "9", "1", "J", "Q", "K"};
			List<String> ranks = Arrays.asList(list);

			ArrayList<String> temp = new ArrayList<String>(meld.get(meldIndex));

			for(String card : cards) {

				if(playersHands.get(playersList[getCurrentPlayer()]).contains(card) != true) {
					throw new RummyException("Expected cards!", 7);
				}

			}

			for(String card : cards) {

				boolean checker = false;

				char rank = card.charAt(0);
				char suit = card.charAt(card.length() - 1);

				boolean checker1 = false;
				boolean checker2 = false;


				for(String cardsMeld: temp) {

					if (cardsMeld.charAt(cardsMeld.length() - 1) != suit) {

						checker2 = false;
						break;

					}
					else {
						checker2 = true;
					}

				}

				for(String cardsMeld : temp) {

					if (cardsMeld.charAt(0) != rank) {

						checker1 = false;
						break;

					}
					else {
						checker1 = true;
					}

				}

				for(String cardsMeld : temp) {

					String str1 = Character.toString(card.charAt(0));
					String str2 = Character.toString(cardsMeld.charAt(0));

					if ((checker2 == true) && (ranks.indexOf(str2) - ranks.indexOf(str1) == 1 || ranks.indexOf(str2) - ranks.indexOf(str1) == -1)) {
						checker = true;
						break;
					}
					else {
						checker = false;
					}

				}

				if (checker == false && checker1 == false) {
					throw new RummyException("Not valid index of meld!", 11);
				}

				temp.add(card);

			}

			for (String card: cards) {

				meld.get(meldIndex).add(card);
				playersHands.get(playersList[getCurrentPlayer()]).remove(card);

			}
			if (getHandOfPlayer(getCurrentPlayer()).length == 0) {
				step = Steps.FINISHED;
			}
		}

		else{
			throw new RummyException("Not MELD or RUMMY states!", 15);
		}

    }

    @Override
    public void declareRummy() {

		if (getCurrentStep() == Steps.MELD) {
			step = Steps.RUMMY;
		}
		else{
			throw new RummyException("Not MELD state!", 5);
		}

    }

    @Override
    public void finishMeld() {

		if (getCurrentStep() != Steps.MELD || getCurrentStep() != Steps.RUMMY) {

			if (step == Steps.RUMMY && getHandOfPlayer(getCurrentPlayer()).length != 0) {

				step = Steps.DISCARD;
				throw new RummyException("Not demonstrated Rummy!", 16);

			}

			step = Steps.DISCARD;

		}
		else{
			throw new RummyException("Not MELD or RUMMY states!", 15);
		}

	}

    @Override
    public void discard(String card) {

		if (getCurrentStep() == Steps.DISCARD) {

			if (playersHands.get(playersList[getCurrentPlayer()]).contains(card) == false){
				throw new RummyException("No such cards!", 7);
			}

			if (discardOn != card) {

				playersHands.get(playersList[getCurrentPlayer()]).remove(card);
				discard.add(card);

			}
			else{
				throw new RummyException("Not valid discard!", 13);
			}

			if (getHandOfPlayer(getCurrentPlayer()).length == 0) {

				isFinished();
				step = Steps.FINISHED;

			}
			else {

				step = Steps.DRAW;

				if (getNumPlayers() - 1 > getCurrentPlayer()) {
					currentPlayer ++;
				}
				else {
					currentPlayer = 0;
				}

			}

		}

		else{
			throw new RummyException("Not DISCARD state!", 6);
		}

	}
        
}
