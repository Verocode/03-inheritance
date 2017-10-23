package de.fhro.inf.prg3.a03;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static de.fhro.inf.prg3.a03.Animal.State.*;

/**
 * @author Peter Kurfer
 * Created on 10/7/17.
 */
public class Animal {

	private static final Logger logger = LogManager.getLogger();

	//public enum State {SLEEPING, HUNGRY, DIGESTING, PLAYFUL, DEAD}
	public abstract class State{

		public final State tick(){
			logger.info("tick()"+state.getClass());
			logger.info("Time:"+time+ " , awake:"+awake+ " timeSinceFeed"+timeSinceFeed+" sleep:"+sleep+ "digest:"+digest);
			time++;
			return successor();
		}
		public State feed(){
			logger.info("Cant feed cat atm!");
			throw new IllegalStateException("You cant feed it when its not hungry!");

		}
		abstract State successor();

	}
	public class DigestionState extends State{
		@Override
		State successor() {
			if (++timeSinceFeed == digest) {
				logger.info("Getting in a playful mood!");

				timeSinceFeed = 0;
				return new PlayfulState();
			}
			return this;
		}
	}
	public class HungryState extends State{
		@Override
		State successor() {
			if(time == awake){
				logger.info("Cat starved :(");
				return new DeathState();
			}
			return this;
		}
		@Override
		public State feed(){
			logger.info("Fed the cat");
			return new DigestionState();
		}
	}
	public class PlayfulState extends State{
		@Override
		State successor() {

			if (time == awake) {
				logger.info("Yoan... getting tired!");

				time = 0;

				return new SleepingState();

			}
			return this;

	}
	}
	public class SleepingState extends State{
		@Override
		State successor() {
			if (time == sleep) {
				logger.info("Yawn am hungry hungry!");
				time=0;
			return new HungryState();
			}
			return this;
		}
	}
	public class DeathState extends State{
		@Override
		State successor() {
			logger.info("ju dead cant success");
			return this;
		}
	}
	private State state = new SleepingState();

	// state durations (set via constructor), ie. the number of ticks in each state
	private final int sleep;
	private final int awake;
	private final int digest;

	private final String name;

	// money you make, when people watch your animal
	private final int collectionAmount;
	private final GenusSpecies genusSpecies;

	// those species this animal likes to eat
	private final GenusSpecies[] devours;

	private int time = 0;
	private int timeSinceFeed;

	public Animal(GenusSpecies genusSpecies, String name, GenusSpecies[] devours, int sleep, int awake, int digest, int collectionAmount) {
		this.name = name;
		this.genusSpecies = genusSpecies;
		this.devours = devours;
		this.sleep = sleep;
		this.awake = awake;
		this.digest = digest;
		this.collectionAmount = collectionAmount;

		Arrays.sort(this.devours);
	}

	public void tick(){
		logger.info("tick()");
		//time++;
		state = state.tick();


	}

	public void feed(){
		if(!state.getClass().equals( HungryState.class)){
			throw new IllegalStateException("Wrong state to feed!");
		}
		else{
			logger.info("You fed the cat!");
			state=state.feed();
		}

	}

	public boolean devours(Animal other){
		return Arrays.binarySearch(this.devours, other.genusSpecies) >= 0;
	}

	public String getName() {
		return name;
	}

	public int collect() {
		if(!isPlayful()){
			throw new IllegalStateException("One does not simply collect if the animal is not playful!");
		}
		return collectionAmount;
	}

	public boolean isAsleep() {
		return state.getClass().equals(SleepingState.class);
	}

	public boolean isPlayful() {
		return state.getClass().equals(PlayfulState.class);
	}

	public boolean isHungry() {
		return state.getClass().equals(HungryState.class);
	}

	public boolean isDigesting() {
		return state.getClass().equals(DigestionState.class);
	}

	public boolean isDead() {
		return state.getClass().equals(DeathState.class);
	}
}
