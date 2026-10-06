package net.vindalarp.omni3d.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.world.phys.Vec3;
import net.vindalarp.omni3d.internal.CustomRenderPipelines;
import net.vindalarp.omni3d.internal.Omni3dBasicRenderableBlueprint;
import org.joml.Matrix4f;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;

/**
 * <h5>Line: Draws a line between two Vec3 positions / "attachments".</h5>
 */
public class Line extends Omni3dBasicRenderableBlueprint {

    // Construction part
    protected ColorRGBA color;
    protected Vec3 attachment0;
    protected Vec3 attachment1;
    protected Float width;

    final public static Vec3 DEFAULT_ATTACHMENT0 = Vec3.ZERO;
    final public static Vec3 DEFAULT_ATTACHMENT1 = Vec3.ZERO;
    final public static ColorRGBA DEFAULT_COLOR = new ColorRGBA(1f, 1f, 1f, 1f);
    final public static Float DEFAULT_WIDTH = 3f;

    public Line(Vec3 attachment0, Vec3 attachment1, ColorRGBA color, Float width, boolean visibleThroughWalls) {
        super();
        this.attachment0 = attachment0 != null ? attachment0 : DEFAULT_ATTACHMENT0;
        this.attachment1 = attachment1 != null ? attachment1 : DEFAULT_ATTACHMENT1;
        this.color = color != null ? color : DEFAULT_COLOR;
        this.width = width != null ? width : DEFAULT_WIDTH;
        this.visibleThroughWalls = visibleThroughWalls;;
    }
    public Line(Vec3 attachment0, Vec3 attachment1) { this(attachment0, attachment1, DEFAULT_COLOR, DEFAULT_WIDTH, false); }
    public Line(Vec3 attachment0, Vec3 attachment1, ColorRGBA color) { this(attachment0, attachment1, color, DEFAULT_WIDTH, false); }
    public Line(Vec3 attachment0, Vec3 attachment1, Float width) { this(attachment0, attachment1, DEFAULT_COLOR, width, false); }
    public Line(Vec3 attachment0, Vec3 attachment1, Boolean visibleThroughWalls) { this(attachment0, attachment1, DEFAULT_COLOR, DEFAULT_WIDTH, visibleThroughWalls); }
    public Line(Vec3 attachment0, Vec3 attachment1, ColorRGBA color, Float width) { this(attachment0, attachment1, color, width, false); }
    public Line(Vec3 attachment0, Vec3 attachment1, ColorRGBA color, Boolean visibleThroughWalls) { this(attachment0, attachment1, color, DEFAULT_WIDTH, visibleThroughWalls); }
    public Line(Vec3 attachment0, Vec3 attachment1, Float width, Boolean visibleThroughWalls) { this(attachment0, attachment1, DEFAULT_COLOR, width, visibleThroughWalls); }

    // API PART: Self explanatory functions, optionally add annotations later

    public void setAttachment0(Vec3 value) {this.attachment0 = value != null ? value : DEFAULT_ATTACHMENT0;}
    public Vec3 getAttachment0() {return this.attachment0;}

    public void setAttachment1(Vec3 value) {this.attachment1 = value != null ? value : DEFAULT_ATTACHMENT1;}
    public Vec3 getAttachment1() {return this.attachment1;}

    public void setColor(ColorRGBA value) {this.color = value != null ? value : DEFAULT_COLOR;}
    public ColorRGBA getColor() {return this.color;}

    public void setWidth(Float value) {this.width = value != null ? value : DEFAULT_WIDTH;}
    public Float getWidth() {return this.width;}


    // BACKEND PART

    @Override
    protected RenderPipeline getPipeline() { return (this.isVisibleThroughWalls() ? CustomRenderPipelines.LINE_SEE_THROUGH : CustomRenderPipelines.LINE_NORMAL).getPipeline(); }

    @Override
    protected void bufferAndRender(LevelRenderContext context) {
        RenderPipeline pipeline = this.getPipeline();
        VertexFormat formatBinding = pipeline.getVertexFormatBinding(0);
        if (formatBinding == null) { return; }
        PrimitiveTopology primitiveTopology = pipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = BUFFER.appendDraw(formatBinding, primitiveTopology);

        setRenderDetails(pipeline, draw, null);

        // Adding Matrix to Matrices Stack based on camera position
        PoseStack matrices = context.poseStack();
        Vec3 cameraPosition = context.levelState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        // Getting and filling the builder
        final var buffer = BUFFER.getVertexBuilder(draw);

        Vec3 posA = this.getAttachment0();
        Vec3 posB = this.getAttachment1();
        float lineWidth = this.getWidth();
        ColorRGBA c = this.getColor();
        Matrix4f matrix = matrices.last().pose();


        buffer.addVertex(matrix, (float) posA.x(), (float) posA.y(), (float) posA.z()).setColor(c.r(), c.g(), c.b(), c.a()).setNormal(0f, 1f, 0f).setLineWidth(lineWidth);
        buffer.addVertex(matrix, (float) posB.x(), (float) posB.y(), (float) posB.z()).setColor(c.r(), c.g(), c.b(), c.a()).setNormal(0f, 1f, 0f).setLineWidth(lineWidth);

        matrices.popPose(); // Cleaning up matrices
    }
}
