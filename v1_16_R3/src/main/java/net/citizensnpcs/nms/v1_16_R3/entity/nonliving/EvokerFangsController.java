package net.citizensnpcs.nms.v1_16_R3.entity.nonliving;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEvokerFangs;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.nms.v1_16_R3.entity.MobEntityController;
import net.citizensnpcs.nms.v1_16_R3.util.ForwardingNPCHolder;
import net.citizensnpcs.nms.v1_16_R3.util.NMSImpl;
import net.citizensnpcs.npc.CitizensNPC;
import net.citizensnpcs.npc.ai.NPCHolder;
import net.citizensnpcs.util.Util;
import net.minecraft.server.v1_16_R3.EntityEvokerFangs;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumHand;
import net.minecraft.server.v1_16_R3.EnumInteractionResult;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.Vec3D;
import net.minecraft.server.v1_16_R3.World;

public class EvokerFangsController extends MobEntityController {
    public EvokerFangsController() {
        super(EntityEvokerFangsNPC.class);
    }

    @Override
    public EvokerFangs getBukkitEntity() {
        return (EvokerFangs) super.getBukkitEntity();
    }

    public static class EntityEvokerFangsNPC extends EntityEvokerFangs implements NPCHolder {
        private final CitizensNPC npc;

        public EntityEvokerFangsNPC(EntityTypes<? extends EntityEvokerFangs> types, World world) {
            this(types, world, null);
        }

        public EntityEvokerFangsNPC(EntityTypes<? extends EntityEvokerFangs> types, World world, NPC npc) {
            super(types, world);
            this.npc = (CitizensNPC) npc;
        }

        @Override
        public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
            if (npc == null) {
                return super.a(entityhuman, vec3d, enumhand);
            }
            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent((Player) entityhuman.getBukkitEntity(),
                    getBukkitEntity());
            Bukkit.getPluginManager().callEvent(event);
            return event.isCancelled() ? EnumInteractionResult.FAIL : EnumInteractionResult.SUCCESS;
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
                NMSImpl.setBukkitEntity(this, new EvokerFangsNPC(this));
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
            super.tick();
            if (npc != null) {
                npc.update();
            }
        }
    }

    public static class EvokerFangsNPC extends CraftEvokerFangs implements ForwardingNPCHolder {
        public EvokerFangsNPC(EntityEvokerFangsNPC entity) {
            super((CraftServer) Bukkit.getServer(), entity);
        }
    }
}
