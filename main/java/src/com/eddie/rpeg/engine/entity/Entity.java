/**
 * Programmer: Eddie Penta
 * Date: Nov 15, 2012
 * Purpose: <INSERT PURPOSE>
 * File name: Object.java
 */
package com.eddie.rpeg.engine.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.eddie.rpeg.engine.entity.mover.Mover;
import com.eddie.rpeg.engine.entity.types.Pickupable;
import com.eddie.rpeg.engine.level.Level;
import com.eddie.rpeg.engine.render.ObjectDrawer;
import com.eddie.rpeg.engine.render.animation.Animation;
import com.eddie.rpeg.engine.render.animation.AnimationStyle;
import com.eddie.rpeg.engine.system.RPEG;
import com.eddie.rpeg.engine.system.Tick;

/**
 * An entity is any object that is drawn on the screen. This object implements a tick
 * for animations, but can be overriden to provide other functionality, just be sure to call
 * the super.
 * 
 * The draw method is called whenever the render system requests that this object be drawn to the screen.
 * This method should only draw and not do any calculations. Any calculation should be done inside a seperate thread
 * or in a tick.
 */
public abstract class Entity implements Tick, Serializable{
	private static final long serialVersionUID = 3148455923653939525L;
	protected transient ArrayList<Mover> move = new ArrayList<Mover>();
	protected String name;
	protected transient Animation animation;
	protected transient BufferedImage image;
	protected double x;
	protected double y;
	protected transient ArrayList<Pickupable> items = new ArrayList<Pickupable>();
	protected transient boolean dispose;
	protected transient Level level;
	protected boolean visible;
	protected int aniwait = 50;
	protected long ID = -1;
	protected transient RPEG system;
	protected transient HashMap<String, BufferedImage> cache = new HashMap<String, BufferedImage>();
	protected transient ObjectDrawer obj;
	protected boolean hasAnimation;
	public boolean flipped;
	
	public Entity(String name, RPEG system, Level level, boolean load) {
		this.name = name;
		this.system = system;
		this.hasAnimation = load;
		loadAnimation();
		this.system.getTicker().addTick(this);
		this.visible = false;
		this.level = level;
	}
	
	public void setHasAnimation(boolean has) {
		this.hasAnimation = has;
	}
	
	public boolean hasAnimation() {
		return hasAnimation;
	}
	
	public void loadAnimation() {
		if (hasAnimation) {
			animation = new Animation(this);
			try {
				animation.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			animation.setAnimation(AnimationStyle.IDLE);
		}
	}
	
	
	/**
	 * Clear all the movers in the mover list
	 */
	public void clearMoverList() {
		for (Mover m : move) {
			system.getTicker().removeTick(m);
		}
		move.clear();
	}
	
	public boolean isDisposed() {
		return dispose;
	}
	
	public void setID(long ID) {
	    if (ID == -1)
	        throw new InvalidParameterException("You cant set an Entities ID to -1!");
		this.ID = ID;
	}
	
	public long getID() {
		return ID;
	}

	public Entity(String name, RPEG system, Level level) {
		this(name, system, level, true);
	}
	
	public void pickUp(Pickupable p) {
		if (items.contains(p))
			return;
		items.add(p);
		p.attach(this);
	}
	
	public void throwNext(double d, double e) {
		if (items.size() == 0)
			return;
		Pickupable p = items.get(0);
		items.remove(p);
		p.deattach();
		p.throwObject(this, d, e);
	}

	/**
	 * 
	 */
	public Entity() { }

	public int getHeight() {
		if (getImage() == null)
			return 0;
		return getImage().getHeight();
	}
	
	public Level getLevel() {
		return level;
	}
	
	public int getWidth() {
		if (getImage() == null)
			return 0;
		return getImage().getWidth();
	}
	
	public void changeLevel(Level level) {
		//TODO DO IT
	}

	public boolean hasMover(String name) {
		return getMover(name) != null;
	}

	public void addMover(Mover move) {
		if (move.getParent() == null)
			move.setParent(this);
		this.move.add(move);
	}

	public Mover getMover(int i) {
		return getMoverList().get(i);
	}

	public Mover getMover(String name) {
		for (Mover m : move) {
			if (m.getName().equals(name))
				return m;
		}
		return null;
	}

	public ArrayList<Mover> getMoverList() {
		return move;
	}


	public final void setDrawerParent(ObjectDrawer obj) {
		this.obj = obj;
		onLoad();
	}

	public ObjectDrawer getDrawerParent() {
		return obj;
	}


	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visable) {
		this.visible = visable;
	}

	public void setAnimationSpeed(int timeout) {
		this.aniwait = timeout;
	}

	public void resetAnimationSpeed() {
		setAnimationSpeed(50);
	}

	public String getName() {
		return name;
	}

	public void setImage(String file) {
		if (cache.containsKey(file)) {
			setImage(cache.get(file));
			return;
		}
		try {
			image = ImageIO.read(new File(file));
			cache.put(file, image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getDefaultImage() {
		String file = "entities/" + getName() + "/image.png";
		if (cache.containsKey(file))
			return cache.get(file);
		else {
			if (!new File(file).exists())
				return null;
			try {
				BufferedImage image = ImageIO.read(new File(file));
				cache.put(file, image);
				return image;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public double getDrawX() {
		if (!RPEG.center_player)
			return getX();
		return (getX() + system.getCenterX()) - getWidth();
	}
	
	public double getDrawY() {
		if (!RPEG.center_player)
			return getY();
		return (getY() + system.getCenterY()) + getHeight();
	}
	
	/**
	 * This is method is called when this entity is added to a window
	 */
	public void onLoad() { }

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	public boolean isHighest(double minx, double miny, double maxx, double maxy) {
		for (Entity e : getDrawerParent()) {
			if (e.getX() > minx && e.getX() < maxx && e.getY() > miny && e.getY() < maxy) {
				if (getDrawerParent().indexOf(e) > getDrawerParent().indexOf(this))
					return false;
			}
		}
		return true;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	public void update() {
		setImage(flipped ? animation.getFliped() : animation.next());
	}

	/* (non-Javadoc)
	 * @see com.eddie.rpeg.system.Tick#tick()
	 */
	@Override
	public void tick() {
		if (animation == null)
			return;
		if (animation.isStopped())
			return;
		if (animation.hasNext()) {
			setImage(flipped ? animation.next() : animation.nextFlipped());
		}
		else
			animation.reset();
	}

	/* (non-Javadoc)
	 * @see com.eddie.rpeg.system.Tick#inSeperateThread()
	 */
	@Override
	public boolean inSeperateThread() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.eddie.rpeg.system.Tick#getTimeout()
	 */
	@Override
	public int getTimeout() {
		return aniwait;
	}

	/**
	 * 
	 */
	public Animation getAnimation() {
		return animation;
	}

	public void dispose() {
		if (dispose)
			return;
		dispose = true;
		if (animation != null) {
			animation.stop();
			animation.dispose();
			animation = null;
		}
		if (getDrawerParent() != null)
		    getDrawerParent().removeObject(this);
		system.getTicker().removeTick(this);
		clearMoverList();
	}

	public abstract void draw(Graphics g, BufferedImage screen);

	/**
	 * @param level2
	 */
	public void setLevel(Level level2) {
		this.level = level2;
	}

	/**
	 * @param system2
	 */
	public void setSystem(RPEG system2) {
		this.system = system2;
	}
	
	@Override
	public int hashCode() {
		return (int)ID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Entity) {
			if (obj.getClass().equals(this.getClass())) {
				Entity e = (Entity)obj;
				return e.getName().equals(getName()) && e.ID == ID;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public int getAnimationSpeed() {
		return aniwait;
	}
	
	public boolean willVisibleNextDraw() {
		if (!RPEG.center_player) {
			return getX() > 0 && getX() < system.getMaxScreenX() && getY() > 0 && getY() < system.getMaxScreenY();
		} else {
			return getDrawX() > 0 && getDrawX() < system.getMaxScreenX() && getDrawY() > 0 && getDrawY() < system.getMaxScreenY();
		}
	}

}
