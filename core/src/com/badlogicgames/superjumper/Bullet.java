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

//        System.out.println("new bullet " + x + " " + y);

        this.lifecycle = lifecycle;
        this.damage = damage;
        this.type = type;
    }

    @Override
    public boolean update(float deltaTime) {
        super.update(deltaTime);

        stateTime += deltaTime;
        if(stateTime >= lifecycle)
            return true;
        return false;
    }

    public void draw(SpriteBatch batch) {

        if(this.type == 1) {
            TextureRegion keyFrame = Assets.bullet1.getKeyFrame(stateTime, Animation.ANIMATION_LOOPING);
            batch.draw(keyFrame, position.x - 0.5f, position.y - 0.5f, 1, 2);
        }
        else
        {
            TextureRegion keyFrame = Assets.bullet2.getKeyFrame(stateTime, Animation.ANIMATION_LOOPING);
            batch.draw(keyFrame, position.x - 0.5f, position.y - 0.5f, 1, 1);
        }
    }

}
