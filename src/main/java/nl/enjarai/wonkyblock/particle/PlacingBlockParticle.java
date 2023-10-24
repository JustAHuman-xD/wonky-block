package nl.enjarai.wonkyblock.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import nl.enjarai.wonkyblock.WonkyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.*;
import nl.enjarai.wonkyblock.util.RendererImplementation;

import java.util.Random;

public class PlacingBlockParticle extends Particle {
    private static final Random JAVA_RANDOM = new Random();

    private final MinecraftClient client;

    private final BlockPos pos;
    private final Block block;
    private final BlockState blockState;
    private final BlockEntity tileEntity;

    private final RendererImplementation renderer;
    private final BakedModel model;
    private final Direction facing;

    private Vec3d prevRot;
    private Vec3d rot;

    private final boolean lookingUp;
    private final float startingHeight;
    private final float startingAngle;

    private float height;
    private float prevHeight;
    private float smoothHeight;
    private boolean inPosition = false;

    private long tick = -1;
    private float step = 0.00275f;

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
            return new PlacingBlockParticle(
                    world, x, y, z,
                    world.getBlockState(new BlockPos((int) x, (int) y, (int) z))
            );
        }
    }

    public PlacingBlockParticle(ClientWorld world, double x, double y, double z, BlockState state) {
        super(world, x, y, z);

        pos = new BlockPos((int) x, (int) y, (int) z);

        client = MinecraftClient.getInstance();

        assert client.player != null;
        facing = client.player.getHorizontalFacing();

        lookingUp = MathHelper.wrapDegrees(client.player.getPitch()) <= 0;

        prevHeight = height = startingHeight = (float) JAVA_RANDOM.nextDouble(0.065, 0.115);
        startingAngle = (float) JAVA_RANDOM.nextDouble(0.03125, 0.0635);

        prevRot = new Vec3d(0, 0, 0);

        rot = switch (facing) {
            case EAST -> new Vec3d(-startingAngle, 0, -startingAngle);
            case NORTH -> new Vec3d(-startingAngle, 0, startingAngle);
            case SOUTH -> new Vec3d(startingAngle, 0, -startingAngle);
            case WEST -> new Vec3d(startingAngle, 0, startingAngle);
            default -> new Vec3d(0, 0, 0);
        };

        blockState = state;
        block = blockState.getBlock();

        renderer = WonkyBlock.getRenderer();

        collidesWithWorld = false;

        model = client.getBlockRenderManager().getModels().getModel(state);

        if (model == null) {
            collidesWithWorld = true;
            dead = true;
        }

        tileEntity = world.getBlockEntity(pos);
    }

    @Override
    public void tick() {
        if (age >= 10) {
            killParticle();
        }
        if (++age >= 10 || inPosition) {
            WonkyBlock.getInvisibleBlocks().remove(pos);
            age = 11;
        }

        if (dead || client.isPaused())
            return;

        prevHeight = height;

        prevRot = rot;

        rot = switch (facing) {
            case EAST -> rot.add(step, 0, step);
            case NORTH -> rot.add(step, 0, -step);
            case SOUTH -> rot.add(-step, 0, step);
            case WEST -> rot.add(-step, 0, -step);
            default -> new Vec3d(0, 0, 0);
        };

        height -= step * 5f;

        step *= 1.5678982f;
    }

    @SuppressWarnings({"SuspiciousNameCombination"})
    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float delta) {
        if (dead)
            return;

        if (collidesWithWorld) {
            if (tick >= 1) {
                killParticle();
                return;
            }

            tick++;
        }

        float deltaX = (float) (prevPosX + (x - prevPosX) * delta - velocityX) - 0.5f;
        float deltaY = (float) (prevPosY + (y - prevPosY) * delta - velocityY) - 0.5f;
        float deltaZ = (float) (prevPosZ + (z - prevPosZ) * delta - velocityZ) - 0.5f;

        smoothHeight = ((float) (prevHeight + (height - prevHeight) * (double) delta));

        if (smoothHeight <= 0) {
            smoothHeight = 0;
        }

        var tRot = switch (facing) {
            case EAST -> new Vec3d(1, 0, -1);
            case NORTH -> new Vec3d(-1, 0, -1);
            case SOUTH -> new Vec3d(1, 0, 1);
            case WEST -> new Vec3d(-1, 0, 1);
            default -> new Vec3d(0, 0, 0);
        };

        var t = switch (facing) {
            case EAST -> new Vec3d(-smoothHeight, smoothHeight, smoothHeight);
            case NORTH -> new Vec3d(smoothHeight, smoothHeight, smoothHeight);
            case SOUTH -> new Vec3d(-smoothHeight, smoothHeight, -smoothHeight);
            case WEST -> new Vec3d(smoothHeight, smoothHeight, -smoothHeight);
            default -> new Vec3d(0, 0, 0);
        };

        var smoothRot = prevRot.lerp(rot, delta);
        switch (facing) {
            case NORTH, WEST -> {
                if (smoothRot.z < 0) {
                    inPosition = true;
                    smoothRot = new Vec3d(0, smoothRot.getY(), 0);
                }
            }
            case EAST -> {
                if (smoothRot.z > 0) {
                    inPosition = true;
                    smoothRot = new Vec3d(0, smoothRot.getY(), 0);
                }
            }
            case SOUTH -> {
                if (smoothRot.x < 0) {
                    inPosition = true;
                    smoothRot = new Vec3d(0, smoothRot.getY(), 0);
                }
            }
            default -> {}
        }

        RenderLayer renderLayer = RenderLayers.getMovingBlockLayer(blockState);
        VertexConsumer blockVertexConsumer = client.getBufferBuilders().getEntityVertexConsumers().getBuffer(renderLayer);
        MatrixStack matrices = new MatrixStack();

        Vec3d cameraPos = camera.getPos();
        matrices.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());

        matrices.push();

        RenderSystem.enableCull();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.translate(deltaX, deltaY, deltaZ);

        matrices.translate(tRot.x, tRot.y, tRot.z);
//
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) smoothRot.x));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) smoothRot.z));
//
        matrices.translate(-tRot.x, -tRot.y, -tRot.z);
        matrices.translate(t.x, t.y, t.z);

        renderer.renderBlock(world, model, blockState, pos, matrices, blockVertexConsumer, false, random, blockState.getRenderingSeed(pos));

        matrices.pop();

        client.getBufferBuilders().getEntityVertexConsumers().draw();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    public void killParticle() {
        dead = true;
    }

    @Override
    public void markDead() {
        WonkyBlock.getInvisibleBlocks().remove(pos);
    }
}
