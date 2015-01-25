package com.badlogicgames.superjumper;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by gg_shily on 1/25/15.
 */
public class Voicebar extends DynamicGameObject {
    public float stateTime;
    public float totalTime;
    public Voicebar(float x, float y, float width, float height, float totalTime) {
        super(x, y, width, height);
        this.totalTime = totalTime;
    }

    @Override
    public boolean update(float deltaTime) {
         super.update(deltaTime);
        stateTime += deltaTime;

        return false;
    }

    public void draw(SpriteBatch batch) {

        if(stateTime < totalTime)
            batch.draw(Assets.voicebarInside, position.x - 1.2f, position.y - 0.4f, (totalTime - stateTime) * 3f / totalTime, .8f);

        batch.draw(Assets.voicebarFrame, position.x - 1.8f, position.y - 0.5f, 3.6f, 1f);

    }
}
