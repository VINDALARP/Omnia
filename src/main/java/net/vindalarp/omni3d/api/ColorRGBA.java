package net.vindalarp.omni3d.api;

/**
 * Simple color helper class
 * @param r Float of 0-1 for the red channel
 * @param g Float of 0-1 for the green channel
 * @param b Float of 0-1 for the blue channel
 * @param a Float of 0-1 for the alpha channel
 */
public record ColorRGBA(float r, float g, float b, float a) {
    /**
     * Returns the color in a Hex-Int format
     */
    public int toHexIntRGBA() {
        int rn = (int) (r * 255);
        int gn = (int) (g * 255);
        int bn = (int) (b * 255);
        int an = (int) (a * 255);

        // ai made me this, i dont do maths
        return (an << 24) | (rn << 16) | (gn << 8) | bn;
    }
}