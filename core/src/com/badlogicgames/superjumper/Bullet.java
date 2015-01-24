package com.badlogicgames.superjumper;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by gg_shily on 1/24/15.
 */
public class Bullet extends DynamicGameObject {
    private final float lifecycle;

    public float stateTime;
    public float damage;
    public int type;


    public Bullet(float x, float y, float width, float height, float lifecycle, float damage, int type) {
        super(x, y, width, height);

        System.out.println("new bullet " + x + " " + y);

        this.lifecycle = lifecycle;
        this.damage = damage;
        this.type = type;
    }

    public boolean update(float deltaTime) {

        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        bounds.x = position.x - bounds.width / 2;
        bounds.y = position.y - bounds.height / 2;

        stateTime += deltaTime;
        if(stateTime >= lifecycle)
            return true;
        return false;
    }

    public void draw(SpriteBatch batch) {

        TextureRegion keyFrame = Assets.coinAnim.getKeyFrame(stateTime, Animation.ANIMATION_LOOPING);
        batch.draw(keyFrame, position.x - 0.5f, position.y - 0.5f, 1, 1);
    }

}
