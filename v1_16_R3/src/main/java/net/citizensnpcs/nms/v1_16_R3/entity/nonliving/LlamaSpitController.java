package net.citizensnpcs.nms.v1_16_R3.entity.nonliving;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLlamaSpit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.nms.v1_16_R3.util.ForwardingNPCHolder;
import net.citizensnpcs.nms.v1_16_R3.util.NMSImpl;
import net.citizensnpcs.npc.AbstractEntityController;
import net.citizensnpcs.npc.CitizensNPC;
import net.citizensnpcs.npc.ai.NPCHolder;
import net.citizensnpcs.util.Util;
import net.minecraft.server.v1_16_R3.EntityLlama;
import net.minecraft.server.v1_16_R3.EntityLlamaSpit;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.World;
import net.minecraft.server.v1_16_R3.WorldServer;

public class LlamaSpitController extends AbstractEntityController {
    public LlamaSpitController() {
        super(EntityLlamaSpitNPC.class);
    }

    @Override
    protected Entity createEntity(Location at, NPC npc) {
        WorldServer ws = ((CraftWorld) at.getWorld()).getHandle();
        final EntityLlamaSpitNPC handle = new EntityLlamaSpitNPC(
                NMSImpl.<EntityLlamaSpit> getEntityType(EntityLlamaSpitNPC.class), ws, npc);
        handle.setPositionRotation(at.getX(), at.getY(), at.getZ(), at.getPitch(), at.getYaw());
        return handle.getBukkitEntity();
    }

    @Override
    public LlamaSpit getBukkitEntity() {
        return (LlamaSpit) super.getBukkitEntity();
    }

    public static class EntityLlamaSpitNPC extends EntityLlamaSpit implements NPCHolder {
        private final CitizensNPC npc;

        public EntityLlamaSpitNPC(EntityTypes<? extends EntityLlamaSpit> types, World world) {
            this(types, world, null);
        }

        public EntityLlamaSpitNPC(EntityTypes<? extends EntityLlamaSpit> types, World world, NPC npc) {
            super(types, world);
            this.npc = (CitizensNPC) npc;
        }

        public EntityLlamaSpitNPC(World world, NPC npc, EntityLlama entity) {
            super(world, entity);
            this.npc = (CitizensNPC) npc;
        }

        @Override
        public void collide(net.minecraft.server.v1_16_R3.Entity entity) {
            // this method is called by both the entities involved - cancelling
            // it will not stop the NPC from moving.
            super.collide(entity);
            if (npc != null) {
                Util.callCollisionEvent(npc, entity.getBukkitEntity());
            }
        }

        @Override
        public boolean d(NBTTagCompound save) {
            return npc == null ? super.d(save) : false;
        }

        @Override
        public CraftEntity getBukkitEntity() {
            if (npc != null && !(super.getBukkitEntity() instanceof NPCHolder)) {
                NMSImpl.setBukkitEntity(this, new LlamaSpitNPC(this));
            }
            return super.getBukkitEntity();
        }

        @Override
        public NPC getNPC() {
            return npc;
        }

        @Override
        public void i(double x, double y, double z) {
            Vector vector = Util.callPushEvent(npc, x, y, z);
            if (vector != null) {
                super.i(vector.getX(), vector.getY(), vector.getZ());
            }
        }

        @Override
        public void tick() {
            if (npc != null) {
                npc.update();
                if (!npc.data().get(NPC.DEFAULT_PROTECTED_METADATA, true)) {
                    super.tick();
                }
            } else {
                super.tick();
            }
        }
    }

    public static class LlamaSpitNPC extends CraftLlamaSpit implements ForwardingNPCHolder {
        public LlamaSpitNPC(EntityLlamaSpitNPC entity) {
            super((CraftServer) Bukkit.getServer(), entity);
        }
    }
}
