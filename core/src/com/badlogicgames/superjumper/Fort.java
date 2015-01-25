package com.badlogicgames.superjumper;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

/**
 * Created by gg_shily on 1/25/15.
 */
public class Fort extends DynamicGameObject {

    public static final int FORT_TYPE_CROSS = 1;
    public static final int FORT_TYPE_FRONT = 2;

    private final float lifecycle;

    public float stateTime;
    public float nextBulletTime;
    public float bulletInterval;
    public int type;

    public Fort(float x, float y, float width, float height, float lifecycle, int type, float bulletInterval) {
        super(x, y, width, height);

        this.lifecycle = lifecycle;
        this.type = type;
        this.bulletInterval = bulletInterval;
    }

    @Override
    public boolean update(float deltaTime) {
        super.update(deltaTime);

        stateTime += deltaTime;

        return stateTime > lifecycle;
    }

    ArrayList<Bullet> getBullets()
    {
        if(stateTime > nextBulletTime)
        {
            nextBulletTime += bulletInterval;

            ArrayList<Bullet> bullets = new ArrayList<Bullet>();
            if(type == FORT_TYPE_CROSS)
            {
                Bullet bullet = new Bullet(position.x, position.y, 0.2f, 0.2f, 2f, 1f, 0);
                bullet.velocity.add(3.0f, 0.0f);
                bullets.add(bullet);

                bullet = new Bullet(position.x, position.y, 0.2f, 0.2f, 2f, 1f, 0);
                bullet.velocity.add(-3.0f, 0.0f);
                bullets.add(bullet);

                bullet = new Bullet(position.x, position.y, 0.2f, 0.2f, 2f, 1f, 1);
                bullet.velocity.add(.0f, 3.0f);
                bullets.add(bullet);

                bullet = new Bullet(position.x, position.y, 0.2f, 0.2f, 2f, 1f, 0);
                bullet.velocity.add(.0f, -3.0f);
                bullets.add(bullet);

            }
            else if(type == FORT_TYPE_FRONT)
            {
                Bullet bullet = new Bullet(position.x, position.y, 0.2f, 0.2f, 2f, 1f, 1);
                bullet.velocity.add(.0f, 3.0f);
                bullets.add(bullet);

                bullet = new Bullet(position.x, position.y, 0.2f, 0.2f, 2f, 1f, 0);
                bullet.velocity.add(1.5f, 1.5f);
                bullets.add(bullet);

                bullet = new Bullet(position.x, position.y, 0.2f, 0.2f, 2f, 1f, 0);
                bullet.velocity.add(-1.5f, 1.5f);
                bullets.add(bullet);

            }

            return bullets;
        }
        return null;
    }

    public void draw(SpriteBatch batch) {

        TextureRegion keyFrame = Assets.cowboy.getKeyFrame(stateTime, Animation.ANIMATION_LOOPING);
        batch.draw(keyFrame, position.x - 0.5f, position.y - 0.5f, 1, 1);
    }
}
