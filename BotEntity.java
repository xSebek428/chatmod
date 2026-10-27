package pl.example.botmod;

import java.util.UUID;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class BotEntity extends PathAwareEntity {
    private UUID ownerUuid;
    private boolean following;

    public BotEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.30)
                .add(EntityAttributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(6, new WanderAroundFarGoal(this, 0.8));
        goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        goalSelector.add(8, new LookAroundGoal(this));
    }

    public void setOwner(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setFollowing(boolean following) {
        this.following = following;
        if (!following) getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (!(getWorld() instanceof ServerWorld world) || !following || ownerUuid == null) return;

        ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(ownerUuid);
        if (owner == null || owner.getWorld() != world) {
            getNavigation().stop();
            return;
        }

        if (squaredDistanceTo(owner) > 9.0) {
            getNavigation().startMovingTo(owner, 1.1);
        } else {
            getNavigation().stop();
        }
    }
}
