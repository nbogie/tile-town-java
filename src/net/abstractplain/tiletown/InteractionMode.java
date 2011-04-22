package net.abstractplain.tiletown;

/**
 * An InteractionMode handles user interaction with the game during that mode's employment. Different modes are switched in and out
 * according to the state of the game. For example, first we might have ModePlaceTile, then change to ModePlaceMeeple. A mode will probably
 * register mouse and keyboard listeners with the gui and remove them when it is finished but does not have to do so.
 * 
 */
public interface InteractionMode {

	void leave();

	void enter();

	String getShortAdvice();

}
