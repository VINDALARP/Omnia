package net.vindalarp.omni3d.internal;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;

import java.util.ArrayList;
import java.util.List;

/**
 * Intentionally inconvenient name for a function that the average user is not meant to call.
 * <h3>Holds the following information about a renderable object: </h3>
 * <br/>
 * <b>Basic Instance API, Basic Instance Rendering Backend Functions, Constructor</b>
 */
public abstract class Omni3dBasicRenderableBlueprint {

    // Usable attributes
    protected boolean visible;
    protected boolean visibleThroughWalls;

    // Technical attributes
    protected RenderPipeline renderPipeline;
    protected StagedVertexBuffer.Draw draw;
    protected TextureSetup texture;

    /**
     * Make sure you destroy this correctly to save memory once you don't need it anymore!!
     * Override, call super and add renderable-specific things depending on implementation.
     */
    public Omni3dBasicRenderableBlueprint() {
        // Set all the default values and initiates the object
        this.visible = true;
        instanceList.add(this);
    }

    // TODO: Add more API features

    /**
     * Makes this object visible.
     */
    public final void show() {visible = true;}

    /**
     * Makes this object invisible / hides it.
     */
    public final void hide() {visible = false;}

    /**
     * Decide whether or not the object should be visible through walls.
     */
    public final void setVisibleThroughWalls(boolean value) {this.visibleThroughWalls = value;}

    /**
     * @return Returns whether or not this renderable is visible through walls.
     */
    public final boolean isVisibleThroughWalls() {return this.visibleThroughWalls;}

    /**
     * Sets this item's visibility to whatever you desire it to be.
     */
    public final void setVisible(boolean value) {visible = value;}

    /**
     * Gets this item's visibility.
     */
    public final boolean isVisible() {return this.visible;}

    /**
     * Destroys this object. Make sure to also set all references to null in order to actually clean it up.
     */
    public final void destroy() {instanceList.remove(this);}

    /** This function MUST be overriden and implemented. Otherwise, the "engine" wont know how to show what its supposed to show.*/
    protected abstract void bufferAndRender(LevelRenderContext context);

    /**
     *  <h3>Mandatory init method.</h3><br />
     *  <h4>Use as: static { init(); }</h4><br />
     *  This must have: Pipeline initialisation
     */
    private static void init() {
        // EXAMPLE ONLY: Implement all of this differently depending on class extension

        // Initialise pipelines, options: PIPELINE_NORMAL, PIPELINE_SEE_THROUGH | TODO (low priority, only if more pipelines get added): Add enum helper

        /*
        renderPipelines.put("PIPELINE_NORMAL", null);
        renderPipelines.put("PIPELINE_SEE_THROUGH", null);
         */
    }

    // Meant to be used as support for the function above
    protected final void setRenderDetails(RenderPipeline pipeline, StagedVertexBuffer.Draw draw, TextureSetup texture) {
        this.renderPipeline = pipeline;
        this.draw = draw;
        this.texture = texture;
    }
    protected final void clearRenderDetails() {
        this.renderPipeline = null;
        this.draw = null;
        this.texture = null;
    }

    /**
     * Automatic pipeline getter. Decides which to return based on visibility through walls.
     */
    protected abstract RenderPipeline getPipeline();

    protected StagedVertexBuffer.Draw getDraw() {return this.draw;}
    protected TextureSetup getTexture() {return this.texture;}


    // -------------- STAAAAAATICSSSSS ------------------------

    // The "Engines" saved given things. Initialise with static {...}. Unsure if it works, review once tested
    static public final StagedVertexBuffer BUFFER = new StagedVertexBuffer(() -> "render3d_buffer", RenderType.BIG_BUFFER_SIZE);
    static protected final List<Omni3dBasicRenderableBlueprint> instanceList = new ArrayList<>();
}