package net.vindalarp.omni3d.api;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.world.phys.Vec3;
import net.vindalarp.omni3d.internal.Omni3dBasicRenderableBlueprint;
import net.vindalarp.omni3d.internal.CustomRenderPipelines;
import org.joml.Matrix4fc;

/**
 * <h5>BlockHighlight: Draws blocky block overlays. Basically comparable to a waypoint, just more flexible.</h5>
 */
public class BlockHighlight extends Omni3dBasicRenderableBlueprint {

    // Constructing part
    protected ColorRGBA color;
    protected Vec3 position;
    protected BlockScale scale;

    final public static Vec3 DEFAULT_POSITION = Vec3.ZERO;
    final public static ColorRGBA DEFAULT_COLOR = new ColorRGBA(1f, 1f, 1f, 1f);
    final public static BlockScale DEFAULT_SCALE = new BlockScale(1.005f,1.005f,1.005f);

    public BlockHighlight(Vec3 position, ColorRGBA color, BlockScale scale, boolean visibleThroughWalls) {
        super();
        this.position = position != null ? position : DEFAULT_POSITION;
        this.color = color != null ? color : DEFAULT_COLOR;
        this.scale = scale != null ? scale : DEFAULT_SCALE;
        this.visibleThroughWalls = visibleThroughWalls;
    }
    public BlockHighlight(Vec3 position) { this(position, DEFAULT_COLOR, DEFAULT_SCALE, false); }
    public BlockHighlight(Vec3 position, ColorRGBA color) { this(position, color, DEFAULT_SCALE, false); }
    public BlockHighlight(Vec3 position, BlockScale scale) { this(position, DEFAULT_COLOR, scale, false); }
    public BlockHighlight(Vec3 position, Boolean visibleThroughWalls) { this(position, DEFAULT_COLOR, DEFAULT_SCALE, visibleThroughWalls); }
    public BlockHighlight(Vec3 position, ColorRGBA color, BlockScale scale) { this(position, color, scale, false); }
    public BlockHighlight(Vec3 position, ColorRGBA color, Boolean visibleThroughWalls) { this(position, color, DEFAULT_SCALE, visibleThroughWalls); }
    public BlockHighlight(Vec3 position, BlockScale scale, Boolean visibleThroughWalls) { this(position, DEFAULT_COLOR, scale, visibleThroughWalls); }

    // API PART: Self explanatory functions, optionally add annotations later

    public void setPosition(Vec3 value) {this.position = value != null ? value : DEFAULT_POSITION;}
    public Vec3 getPosition() {return this.position;}

    public void setColor(ColorRGBA value) {this.color = value != null ? value : DEFAULT_COLOR;}
    public ColorRGBA getColor() {return this.color;}

    public void setScale(BlockScale value) {this.scale = value != null ? value : DEFAULT_SCALE;}
    public BlockScale getScale() {return this.scale;}

    // BACKEND PART

    @Override
    protected RenderPipeline getPipeline() { return (this.isVisibleThroughWalls() ? CustomRenderPipelines.BLOCK_HIGHLIGHT_SEE_THROUGH : CustomRenderPipelines.BLOCK_HIGHLIGHT_NORMAL).getPipeline(); }

    /**
     * Function for buffering and rendering the object.
     */
    @Override
    protected void bufferAndRender(LevelRenderContext context) {
        // NOTE: Copy pasted from the old version of this rendering utility.
        RenderPipeline pipeline = this.getPipeline();;
        VertexFormat formatBinding = pipeline.getVertexFormatBinding(0);
        if (formatBinding == null) {return;}
        PrimitiveTopology primitiveTopology = pipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = BUFFER.appendDraw(formatBinding, primitiveTopology, primitiveTopology == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

        setRenderDetails(pipeline, draw, null);

        // Adding Matrix to Matrices Stack based on camera position | TODO: This maths part is ai generated cus i cant calc for shit. testing required.
        PoseStack matrices = context.poseStack();
        Vec3 cameraPosition = context.levelState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        Vec3 pos = getPosition();
        BlockScale scale = getScale();

        matrices.translate(pos.x, pos.y, pos.z);
        matrices.scale(scale.x(), scale.y(), scale.z());
        matrices.translate(-pos.x, -pos.y, -pos.z);

        // Getting and filling the builder
        final var buffer = BUFFER.getVertexBuilder(draw);
        final Matrix4fc matrix = matrices.last().pose();

        // RENDERING  /  BUFFERING PART ----------------------

        ColorRGBA color = this.getColor();
        float minX = (float) pos.x();
        float maxX = minX + 1;
        float minY = (float) pos.y();
        float maxY = minY + 1;
        float minZ = (float) pos.z();
        float maxZ = minZ + 1;

        // Front Face
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        // Back face
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, minX, minY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        // Left face
        buffer.addVertex(matrix, minX, minY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        // Right face
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        // Top face
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        // Bottom face
        buffer.addVertex(matrix, minX, minY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(color.r(), color.g(), color.b(), color.a());

        matrices.popPose();
    }
}
