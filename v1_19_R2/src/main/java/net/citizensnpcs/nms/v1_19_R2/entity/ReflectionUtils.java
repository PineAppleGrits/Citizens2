package net.citizensnpcs.nms.v1_19_R2.entity;

import net.minecraft.world.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static void setEntityAsFake(Entity entity) {
        try {
            Method method = entity.getClass().getMethod("setFake");
            method.invoke(entity);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("unable to set entity as fake", e);
        }
    }


}
