/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogicgames.superjumper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class World {
	public static final float WORLD_WIDTH = 10;
	public static final float WORLD_HEIGHT = 15 * 20;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;
	public static final Vector2 gravity = new Vector2(0, -12);

	public final Bob bob;
	public final List<Platform> platforms;
	public final List<Spring> springs;
	public final List<Squirrel> squirrels;
	public final List<Coin> coins;
    public final List<Bullet> bullets;
    public final List<Bob> bobs;
    public final List<Fort> forts;
    public final List<Coin> explors;
	public Castle castle;
	public final Random rand;

    Voicebar voicebar;


    public float heightSoFar;
	public int score;
	public int state;

    public int hp;

    public float genBobTime;

	public World () {
        this.hp = 10;
		this.bob = new Bob(5, 10);
		this.platforms = new ArrayList<Platform>();
		this.springs = new ArrayList<Spring>();
		this.squirrels = new ArrayList<Squirrel>();
		this.coins = new ArrayList<Coin>();
        this.bullets = new ArrayList<Bullet>();
        this.bobs = new ArrayList<Bob>();
        this.forts = new ArrayList<Fort>();
        this.explors = new ArrayList<Coin>();
        bobs.add(bob);

		rand = new Random();

		this.heightSoFar = 0;
		this.score = 0;
		this.state = WORLD_STATE_RUNNING;

        this.genBobTime = 2.0f;
	}

	private void generateLevel () {
		float y = Platform.PLATFORM_HEIGHT / 2;
		float maxJumpHeight = Bob.BOB_JUMP_VELOCITY * Bob.BOB_JUMP_VELOCITY / (2 * -gravity.y);
		while (y < WORLD_HEIGHT - WORLD_WIDTH / 2) {
			int type = rand.nextFloat() > 0.8f ? Platform.PLATFORM_TYPE_MOVING : Platform.PLATFORM_TYPE_STATIC;
			float x = rand.nextFloat() * (WORLD_WIDTH - Platform.PLATFORM_WIDTH) + Platform.PLATFORM_WIDTH / 2;

			Platform platform = new Platform(type, x, y);
			platforms.add(platform);

			if (rand.nextFloat() > 0.9f && type != Platform.PLATFORM_TYPE_MOVING) {
				Spring spring = new Spring(platform.position.x, platform.position.y + Platform.PLATFORM_HEIGHT / 2
					+ Spring.SPRING_HEIGHT / 2);
				springs.add(spring);
			}

			if (y > WORLD_HEIGHT / 3 && rand.nextFloat() > 0.8f) {
				Squirrel squirrel = new Squirrel(platform.position.x + rand.nextFloat(), platform.position.y
					+ Squirrel.SQUIRREL_HEIGHT + rand.nextFloat() * 2);
				squirrels.add(squirrel);
			}

			if (rand.nextFloat() > 0.6f) {
				Coin coin = new Coin(platform.position.x + rand.nextFloat(), platform.position.y + Coin.COIN_HEIGHT
					+ rand.nextFloat() * 3);
				coins.add(coin);
			}

			y += (maxJumpHeight - 0.5f);
			y -= rand.nextFloat() * (maxJumpHeight / 3);
		}

		castle = new Castle(WORLD_WIDTH / 2, y);
	}

	public void update (float deltaTime, float accelX) {
        if((genBobTime -= deltaTime) <= 0)
        {
            genBobTime = 2 + rand.nextFloat();
            float x = rand.nextFloat() * 8 + 1;
            Bob bob = new Bob(x, 15);
            bobs.add(bob);
        }

//		updateBob(deltaTime, accelX);
		updatePlatforms(deltaTime);
		updateSquirrels(deltaTime);
		updateCoins(deltaTime);
        updateBobs(deltaTime);
        updateBullets(deltaTime);
        updateFort(deltaTime);
        updateExplors(deltaTime);

        if(voicebar != null)
        {
            voicebar.update(deltaTime);
        }

		if (bob.state != Bob.BOB_STATE_HIT) checkCollisions();
		checkGameOver();
	}

    private void updateExplors(float deltaTime) {
        for(int i = explors.size() - 1; i >= 0; i--)
        {
            explors.get(i).update(deltaTime);

            if(explors.get(i).stateTime > .6)
            {
                explors.remove(i);
            }
        }
    }

    private void updateFort(float deltaTime) {
        for(int i = forts.size() - 1; i >= 0; i--)
        {
            if(forts.get(i).update(deltaTime))
            {
                forts.remove(i);
            }
            else
            {
                ArrayList<Bullet> bullets = forts.get(i).getBullets();
                if(bullets != null)
                {
                    this.bullets.addAll(bullets);
                }
            }
        }
    }

    private void updateBobs(float deltaTime) {
        for(int i = bobs.size() - 1; i >= 0; i--)
        {
            bobs.get(i).update(deltaTime);
            if(heightSoFar  > bobs.get(i).position.y)
            {
                bobs.remove(i);
                hp--;
            }
        }
    }

    private void updateBullets(float deltaTime) {
        int len = bullets.size();
        for(int i = len - 1; i >= 0; i--)
        {
            if(bullets.get(i).update(deltaTime))
            {
                bullets.remove(i);
                continue;
            }
            
            Bob bob = getIntersectedBob(bullets.get(i));
            if(bob != null)
            {
                Coin coin = new Coin(bob.position.x, bob.position.y);
                explors.add(coin);

                bobs.remove(bob);
                bullets.remove(i);

                score += 10;

                Assets.playSound(Assets.hitEnemySound);
            }
        }
    }

    private Bob getIntersectedBob(Bullet bullet) {
        for(Bob bob : bobs)
        {
            if(bob.bounds.overlaps(bullet.bounds))
            {
                return bob;
            }
        }
        return null;
    }

    private void updateBob (float deltaTime, float accelX) {
		if (bob.state != Bob.BOB_STATE_HIT && bob.position.y <= 0.5f) bob.hitPlatform();
//		if (bob.state != Bob.BOB_STATE_HIT) bob.velocity.x = -accelX / 10 * Bob.BOB_MOVE_VELOCITY;
		bob.update(deltaTime);
		heightSoFar = Math.max(bob.position.y, heightSoFar);
	}

	private void updatePlatforms (float deltaTime) {
		int len = platforms.size();
		for (int i = 0; i < len; i++) {
			Platform platform = platforms.get(i);
			platform.update(deltaTime);
			if (platform.state == Platform.PLATFORM_STATE_PULVERIZING && platform.stateTime > Platform.PLATFORM_PULVERIZE_TIME) {
				platforms.remove(platform);
				len = platforms.size();
			}
		}
	}

	private void updateSquirrels (float deltaTime) {
		int len = squirrels.size();
		for (int i = 0; i < len; i++) {
			Squirrel squirrel = squirrels.get(i);
			squirrel.update(deltaTime);
		}
	}

	private void updateCoins (float deltaTime) {
		int len = coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = coins.get(i);
			coin.update(deltaTime);
		}
	}

	private void checkCollisions () {
		checkPlatformCollisions();
		checkSquirrelCollisions();
		checkItemCollisions();
		checkCastleCollisions();
	}

	private void checkPlatformCollisions () {
		if (bob.velocity.y > 0) return;

		int len = platforms.size();
		for (int i = 0; i < len; i++) {
			Platform platform = platforms.get(i);
			if (bob.position.y > platform.position.y) {
				if (bob.bounds.overlaps(platform.bounds)) {
					bob.hitPlatform();
					if (rand.nextFloat() > 0.5f) {
						platform.pulverize();
					}
					break;
				}
			}
		}
	}

	private void checkSquirrelCollisions () {
		int len = squirrels.size();
		for (int i = 0; i < len; i++) {
			Squirrel squirrel = squirrels.get(i);
			if (squirrel.bounds.overlaps(bob.bounds)) {
				bob.hitSquirrel();
			}
		}
	}

	private void checkItemCollisions () {
		int len = coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = coins.get(i);
			if (bob.bounds.overlaps(coin.bounds)) {
				coins.remove(coin);
				len = coins.size();
				score += Coin.COIN_SCORE;
			}

		}

		if (bob.velocity.y > 0) return;

		len = springs.size();
		for (int i = 0; i < len; i++) {
			Spring spring = springs.get(i);
			if (bob.position.y > spring.position.y) {
				if (bob.bounds.overlaps(spring.bounds)) {
					bob.hitSpring();
				}
			}
		}
	}

	private void checkCastleCollisions () {
//		if (castle.bounds.overlaps(bob.bounds)) {
//			state = WORLD_STATE_NEXT_LEVEL;
//		}
	}

	private void checkGameOver () {
		if (hp <= 0) {
			state = WORLD_STATE_GAME_OVER;
		}
	}
}
