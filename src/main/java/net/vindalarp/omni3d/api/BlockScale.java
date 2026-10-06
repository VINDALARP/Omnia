package net.vindalarp.omni3d.api;

/**
 * Simple class for block scales.
 * Remember that a block is 1.0, and if you dont want it to overlap weirdly in the world, you'd need slightly more than that.
 * @param x X-Scale value
 * @param y Y-Scale value
 * @param z Z-Scale value
 */
public record BlockScale(float x, float y, float z) {}