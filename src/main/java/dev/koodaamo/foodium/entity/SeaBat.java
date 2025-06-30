package dev.koodaamo.foodium.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import dev.koodaamo.foodium.FoodiumMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;	
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class SeaBat extends FlyingMob {
	Vec3 moveTargetPoint = Vec3.ZERO;

	BlockPos anchorPoint;
	
	public static final SoundEvent SOUND_SEABAT_DEATH = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "entity.seabat.death"));
	public static final SoundEvent SOUND_SEABAT_HURT = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "entity.seabat.hurt"));
	public static final SoundEvent SOUND_SEABAT_AMBIENT = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "entity.seabat.ambient"));

	public SeaBat(EntityType<? extends SeaBat> entityType, Level level) {
		super(entityType, level);
		this.xpReward = 5;
        this.moveControl = new SeaBat.PhantomMoveControl(this);
        this.lookControl = new SeaBat.PhantomLookControl(this);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1);
	}

	public AttackPhase attackPhase;

	static enum AttackPhase {
		CIRCLE, SWOOP;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return this.random.nextInt(4) != 0 ? null : SOUND_SEABAT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_27451_) {
		return SOUND_SEABAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SOUND_SEABAT_DEATH;
	}

	abstract class PhantomMoveTargetGoal extends Goal {
		public PhantomMoveTargetGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		protected boolean touchingTarget() {
			return SeaBat.this.moveTargetPoint.distanceToSqr(SeaBat.this.getX(), SeaBat.this.getY(), SeaBat.this.getZ()) < 4.0;
		}
	}

	class PhantomCircleAroundAnchorGoal extends SeaBat.PhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        @Override
        public boolean canUse() {
            return SeaBat.this.getTarget() == null || SeaBat.this.attackPhase == SeaBat.AttackPhase.CIRCLE;
        }

        @Override
        public void start() {
            this.distance = 5.0F + SeaBat.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + SeaBat.this.random.nextFloat() * 9.0F;
            this.clockwise = SeaBat.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        @Override
        public void tick() {
            if (SeaBat.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + SeaBat.this.random.nextFloat() * 9.0F;
            }

            if (SeaBat.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                this.distance++;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (SeaBat.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = SeaBat.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (SeaBat.this.moveTargetPoint.y < SeaBat.this.getY() && !SeaBat.this.level().isEmptyBlock(SeaBat.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (SeaBat.this.moveTargetPoint.y > SeaBat.this.getY() && !SeaBat.this.level().isEmptyBlock(SeaBat.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }
        }

        private void selectNext() {
            if (SeaBat.this.anchorPoint == null) {
                SeaBat.this.anchorPoint = SeaBat.this.blockPosition();
            }

            this.angle = this.angle + this.clockwise * 15.0F * (float) (Math.PI / 180.0);
            SeaBat.this.moveTargetPoint = Vec3.atLowerCornerOf(SeaBat.this.anchorPoint)
                .add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
        }
    }
	
	static class PhantomLookControl extends LookControl {
        public PhantomLookControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
        }
    }
	
	class PhantomMoveControl extends MoveControl {
        private float speed = 0.1F;

        public PhantomMoveControl(final Mob p_33241_) {
            super(p_33241_);
        }

        @Override
        public void tick() {
            if (SeaBat.this.horizontalCollision) {
            	SeaBat.this.setYRot(SeaBat.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double dx = SeaBat.this.moveTargetPoint.x - SeaBat.this.getX();
            double dy = SeaBat.this.moveTargetPoint.y - SeaBat.this.getY();
            double dz = SeaBat.this.moveTargetPoint.z - SeaBat.this.getZ();
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            if (Math.abs(horizontalDistance) > 0.00001F) {
                double horizontalMultiplier = 1.0 - Math.abs(dy * 0.7F) / horizontalDistance;
                dx *= horizontalMultiplier;
                dz *= horizontalMultiplier;
                horizontalDistance = Math.sqrt(dx * dx + dz * dz);
                double distance = Math.sqrt(dx * dx + dz * dz + dy * dy);
                float currentYawDeg = SeaBat.this.getYRot();
                float targetYawRad = (float)Mth.atan2(dz, dx);
                float rightYawDeg = Mth.wrapDegrees(SeaBat.this.getYRot() + 90.0F);
                float targetYawDeg = Mth.wrapDegrees(targetYawRad * (180.0F / (float)Math.PI));
                SeaBat.this.setYRot(Mth.approachDegrees(rightYawDeg, targetYawDeg, 4.0F) - 90.0F);
                SeaBat.this.yBodyRot = SeaBat.this.getYRot();
                if (Mth.degreesDifferenceAbs(currentYawDeg, SeaBat.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float newPitchDeg = (float)(-(Mth.atan2(-dy, horizontalDistance) * 180.0F / (float)Math.PI));
                SeaBat.this.setXRot(newPitchDeg);
                float newYawDeg = SeaBat.this.getYRot() + 90.0F;
                double speedIncreaseX = this.speed * Mth.cos(newYawDeg * (float) (Math.PI / 180.0)) * Math.abs(dx / distance);
                double speedIncreaseZ = this.speed * Mth.sin(newYawDeg * (float) (Math.PI / 180.0)) * Math.abs(dz / distance);
                double speedIncreaseY = this.speed * Mth.sin(newPitchDeg * (float) (Math.PI / 180.0)) * Math.abs(dy / distance);
                Vec3 oldMovement = SeaBat.this.getDeltaMovement();
                SeaBat.this.setDeltaMovement(oldMovement.add(new Vec3(speedIncreaseX, speedIncreaseY, speedIncreaseZ).subtract(oldMovement).scale(0.2)));
            }
        }
    }
	
	class PhantomSweepAttackGoal extends SeaBat.PhantomMoveTargetGoal {
		@Override
		public boolean canUse() {
			return SeaBat.this.getTarget() != null && SeaBat.this.attackPhase == SeaBat.AttackPhase.SWOOP;
		}

		@Override
		public boolean canContinueToUse() {
			LivingEntity livingentity = SeaBat.this.getTarget();
			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
				return false;
			} else if (livingentity instanceof Player player && (livingentity.isSpectator() || player.isCreative())) {
				return false;
			} else if (!this.canUse()) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
			SeaBat.this.setTarget(null);
			SeaBat.this.attackPhase = SeaBat.AttackPhase.CIRCLE;
		}

		@Override
		public void tick() {
			LivingEntity livingentity = SeaBat.this.getTarget();
			if (livingentity != null) {
				SeaBat.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5), livingentity.getZ());
				if (SeaBat.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
					SeaBat.this.doHurtTarget(getServerLevel(SeaBat.this.level()), livingentity);
					SeaBat.this.attackPhase = SeaBat.AttackPhase.CIRCLE;
					if (!SeaBat.this.isSilent()) {
						SeaBat.this.level().levelEvent(1039, SeaBat.this.blockPosition(), 0);
					}
				} else if (SeaBat.this.horizontalCollision || SeaBat.this.hurtTime > 0) {
					SeaBat.this.attackPhase = SeaBat.AttackPhase.CIRCLE;
				}
			}
		}
	}

	boolean canAttack(ServerLevel p_365188_, LivingEntity p_367013_, TargetingConditions p_364315_) {
        return p_364315_.test(p_365188_, this, p_367013_);
    }
	
	class PhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);
        private int nextScanTick = reducedTickDelay(20);

        @Override
        public boolean canUse() {
            if (this.nextScanTick > 0) {
                this.nextScanTick--;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                ServerLevel serverlevel = getServerLevel(SeaBat.this.level());
                List<Player> list = serverlevel.getNearbyPlayers(this.attackTargeting, SeaBat.this, SeaBat.this.getBoundingBox().inflate(16.0, 64.0, 16.0));
                if (!list.isEmpty()) {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for (Player player : list) {
                        if (SeaBat.this.canAttack(serverlevel, player, TargetingConditions.DEFAULT)) {
                        	SeaBat.this.setTarget(player);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = SeaBat.this.getTarget();
            return livingentity != null ? SeaBat.this.canAttack(getServerLevel(SeaBat.this.level()), livingentity, TargetingConditions.DEFAULT) : false;
        }
    }
	
	class PhantomAttackStrategyGoal extends Goal {
		private int nextSweepTick;

		@Override
		public boolean canUse() {
			LivingEntity livingentity = SeaBat.this.getTarget();
			return livingentity != null ? SeaBat.this.canAttack(getServerLevel(SeaBat.this.level()), livingentity, TargetingConditions.DEFAULT) : false;
		}

		@Override
		public void start() {
			this.nextSweepTick = this.adjustedTickDelay(10);
			SeaBat.this.attackPhase = SeaBat.AttackPhase.CIRCLE;
			this.setAnchorAboveTarget();
		}

		@Override
		public void stop() {
			if (SeaBat.this.anchorPoint != null) {
				SeaBat.this.anchorPoint = SeaBat.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, SeaBat.this.anchorPoint).above(10 + SeaBat.this.random.nextInt(20));
			}
		}

		@Override
		public void tick() {
			if (SeaBat.this.attackPhase == SeaBat.AttackPhase.CIRCLE) {
				this.nextSweepTick--;
				if (this.nextSweepTick <= 0) {
					SeaBat.this.attackPhase = SeaBat.AttackPhase.SWOOP;
					this.setAnchorAboveTarget();
					this.nextSweepTick = this.adjustedTickDelay((8 + SeaBat.this.random.nextInt(4)) * 20);
					SeaBat.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + SeaBat.this.random.nextFloat() * 0.1F);
				}
			}
		}

		private void setAnchorAboveTarget() {
			if (SeaBat.this.anchorPoint != null) {
				SeaBat.this.anchorPoint = SeaBat.this.getTarget().blockPosition().above(20 + SeaBat.this.random.nextInt(20));
				if (SeaBat.this.anchorPoint.getY() < SeaBat.this.level().getSeaLevel()) {
					SeaBat.this.anchorPoint = new BlockPos(SeaBat.this.anchorPoint.getX(), SeaBat.this.level().getSeaLevel() + 1, SeaBat.this.anchorPoint.getZ());
				}
			}
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new SeaBat.PhantomAttackStrategyGoal());
		this.goalSelector.addGoal(2, new SeaBat.PhantomSweepAttackGoal());
		this.goalSelector.addGoal(3, new SeaBat.PhantomCircleAroundAnchorGoal());
		this.targetSelector.addGoal(1, new SeaBat.PhantomAttackPlayerTargetGoal());
	}

	@Override
	public void tick() {
		super.tick();
	}
}