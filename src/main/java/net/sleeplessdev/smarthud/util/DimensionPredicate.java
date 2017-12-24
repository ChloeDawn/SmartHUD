package net.sleeplessdev.smarthud.util;

import net.minecraft.world.DimensionType;

import java.util.function.Predicate;

public interface DimensionPredicate extends Predicate<DimensionType> {

    DimensionPredicate ANY = dim -> true;

}