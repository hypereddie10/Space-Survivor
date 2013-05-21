/**
 * Programmer: Eddie Penta
 * Date: May 20, 2013
 * Purpose: <INSERT PURPOSE>
 * File name: Bullet.java
 */
package com.eddie.space.entities;

import com.eddie.rpeg.engine.entity.Entity;
import com.eddie.rpeg.engine.entity.types.Damager;
import com.eddie.rpeg.engine.level.Level;
import com.eddie.rpeg.engine.render.animation.AnimationCallback.OnAnimationCompleted;
import com.eddie.rpeg.engine.render.animation.AnimationStyle;
import com.eddie.rpeg.engine.system.RPEG;
import com.eddie.space.entities.bullets.BulletManager;

public abstract class Bullet extends RotatableEntity implements Damager {
	private static final long serialVersionUID = -3436725886675567328L;
	
	public Bullet(String name, Level l, RPEG core) {
		super(name, core, l);
		getAnimation().setOnAnimationCompletedCallback(ANIFINISH);
	}

	/* (non-Javadoc)
	 * @see com.eddie.rpeg.engine.entity.types.Damager#onHit(com.eddie.rpeg.engine.entity.Entity, double, double)
	 */
	@Override
	public void onHit(Entity hit, double cx, double cy) {
		super.getAnimation().setAnimation(AnimationStyle.DEATH);
	}
	
	
	@Override
	public void tick() {
		super.tick();
		if (isVisable() && getAnimation().getStyle() != AnimationStyle.DEATH) {
			setY(getY() - BulletManager.getSpeed());
			if (getY() <= 0)
				dispose();
		}
	}
	
	private final OnAnimationCompleted ANIFINISH = new OnAnimationCompleted() {

		@Override
		public void onAnimationCompleted(AnimationStyle style) {
			if (style == AnimationStyle.DEATH)
				dispose();
		}
		
	};

}
