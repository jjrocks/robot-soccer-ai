package sebbot.strategy;

import java.util.HashSet;

import sebbot.Ball;
import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.SoccerParams;
import sebbot.Vector2D;
import sebbot.ballcapture.Action;
import sebbot.ballcapture.State;
import sebbot.PlayerAction;

/**
 * Created by Bret Black 11/3/2015
 * 
 * @author Bret Black
 */
public class DefensiveStrategy implements Strategy {
	private int range = 30;
	private int maxDistanceToEnd = 52;
	private Vector2D startPos = new Vector2D(0, 0); // home place
	private sebbot.ballcapture.Policy ballCaptureAlgorithm;

	/*
	 * =========================================================================
	 * 
	 * Constructors and destructors
	 * 
	 * =========================================================================
	 */

	/**
	 * @param ballCaptureAlgorithm
	 */
	public DefensiveStrategy(sebbot.ballcapture.Policy ballCaptureAlgorithm) {
		this.ballCaptureAlgorithm = ballCaptureAlgorithm;
	}

	/**
     * 
     */
	public DefensiveStrategy() {
		this(new sebbot.ballcapture.HandCodedPolicy());
	}

	/*
	 * =========================================================================
	 * 
	 * Main methods
	 * 
	 * =========================================================================
	 */

	@Override
	public void doAction(RobocupClient rcClient, FullstateInfo fsi,
			Player player) {
		// save goal position to track if we are too far
		//Vector2D endPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d,
		//		player.getPosition().getY());
		// startPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d, player
		// .getPosition().getY());

		// ball position
		//Vector2D ballPos = fsi.getBall().getPosition();

		// kick the ball if it is in range
		// ball is NOT in range, try different strategy (inspired by
		// GoToBallAndShoot)
		if (fsi.getBall().distanceTo(startPos) <= range) {
			// if ball is close to player, move towards it and kick (code taken
			// from GoToBallAndShoot)
			if (!simplePass(rcClient, fsi, player)) {
				if (!CommonStrategies.shootToGoal(rcClient, fsi, player)) {
					State state = new State(fsi, player);
					Action action = ballCaptureAlgorithm.chooseAction(state);

					rcClient.getBrain().getActionsQueue()
							.addLast(new PlayerAction(action, rcClient));
				}
			}
		} else {
			// normalize startPos x
			startPos.setX(player.isLeftSide() ? -Math.abs(startPos.getX())
					: Math.abs(startPos.getX()));

			// go to position
			CommonStrategies.simpleGoTo(startPos, rcClient, fsi, player);
		}
	}

	/**
	 * Pass to someone closer to the goal
	 * 
	 * @param c
	 *            The client
	 * @param fsi
	 *            The fullstateinfo
	 * @param p
	 *            The Player
	 * @return True if successful, false if fails or the player is already
	 *         closest to the goal
	 * */
	public boolean simplePass(RobocupClient c, FullstateInfo fsi, Player p) {
		Ball ball = fsi.getBall();
		Player[] team = p.isLeftSide() ? fsi.getLeftTeam() : fsi.getRightTeam();

		int numberOfPlayers = team.length;

		// goal coords
		Vector2D goalPos = new Vector2D(p.isLeftSide() ? 52.0d : -52.0d, 0);
		/* Find which player in the team is the closest to the goal */
		Player closestToTheGoal = p;
		for (int i = 0; i < numberOfPlayers; i++) {
			if ((team[i] != p)
					&& (team[i].distanceTo(ball) < closestToTheGoal
							.distanceTo(goalPos))) {
				closestToTheGoal = team[i];
			}
		}

		/* The kick to the player closest to the goal */
		if (closestToTheGoal != p) {
			return CommonStrategies.shootToPos(c, fsi, p,
					closestToTheGoal.getPosition());
		}
		return false;
	}

	/*
	 * =========================================================================
	 * 
	 * SETTERS AND GETTERS
	 * 
	 * =========================================================================
	 */

	/**
	 * 
	 * @param pos
	 *            The start position
	 */
	public void setStartPos(Vector2D pos) {
		startPos = pos;
	}
}
