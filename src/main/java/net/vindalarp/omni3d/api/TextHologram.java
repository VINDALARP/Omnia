package net.vindalarp.omni3d.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.Vec3;
import net.vindalarp.omni3d.internal.CustomRenderPipelines;
import net.vindalarp.omni3d.internal.Omni3dBasicRenderableBlueprint;
import org.joml.Matrix4f;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;

/**
 * <h5>TextHologram: Text in the 3D world that always faces the camera.</h5>
 */
public class TextHologram extends Omni3dBasicRenderableBlueprint {

    // Construction part
    protected String text;
    protected Integer fontSize;
    protected Vec3 position;
    protected ColorRGBA color;

    static public final String DEFAULT_TEXT = "Hello, world :)";
    static public final Integer DEFAULT_FONT_SIZE = 12;
    static public final Vec3 DEFAULT_POSITION = Vec3.ZERO;
    static public final ColorRGBA DEFAULT_COLOR = new ColorRGBA(1, 1, 1, 1);

    public TextHologram(String text, Integer fontSize, Vec3 position, ColorRGBA color, boolean visibleThroughWalls) {
        super();
        this.text = text != null ? text : DEFAULT_TEXT;
        this.fontSize = fontSize != null ? fontSize : DEFAULT_FONT_SIZE;
        this.position = position != null ? position : DEFAULT_POSITION;
        this.color = color != null ? color : DEFAULT_COLOR;
        this.visibleThroughWalls = visibleThroughWalls;
    }
    public TextHologram(String text, Vec3 position) { this(text, DEFAULT_FONT_SIZE, position, DEFAULT_COLOR, false); }
    public TextHologram(String text, Vec3 position, Integer fontSize) { this(text, fontSize, position, DEFAULT_COLOR, false); }
    public TextHologram(String text, Vec3 position, ColorRGBA color) { this(text, DEFAULT_FONT_SIZE, position, color, false); }
    public TextHologram(String text, Vec3 position, Boolean visibleThroughWalls) { this(text, DEFAULT_FONT_SIZE, position, DEFAULT_COLOR, visibleThroughWalls); }
    public TextHologram(String text, Vec3 position, ColorRGBA color, Integer fontSize) { this(text, fontSize, position, color, false); }
    public TextHologram(String text, Vec3 position, Integer fontSize, Boolean visibleThroughWalls) { this(text, fontSize, position, DEFAULT_COLOR, visibleThroughWalls); }
    public TextHologram(String text, Vec3 position, ColorRGBA color, Boolean visibleThroughWalls) { this(text, DEFAULT_FONT_SIZE, position, color, visibleThroughWalls); }
    public TextHologram(String text, Vec3 position, ColorRGBA color, Integer fontSize, Boolean visibleThroughWalls) { this(text, fontSize, position, color, visibleThroughWalls); }

    // API PART: Stuff n stuff

    public void setText(String value) { this.text = value != null ? value : DEFAULT_TEXT; }
    public String getText() { return this.text; }

    public void setFontSize(Integer value) { this.fontSize = value != null ? value : DEFAULT_FONT_SIZE; }
    public Integer getFontSize() { return this.fontSize; }

    public void setPosition(Vec3 value) { this.position = value != null ? value : DEFAULT_POSITION; }
    public Vec3 getPosition() { return this.position; }

    public void setColor(ColorRGBA value) { this.color = value != null ? value : DEFAULT_COLOR; }
    public ColorRGBA getColor() { return this.color; }

    // BACKEND PART

    @Override
    protected RenderPipeline getPipeline() { return (this.isVisibleThroughWalls() ? CustomRenderPipelines.TEXT_HOLOGRAM_SEE_THROUGH : CustomRenderPipelines.TEXT_HOLOGRAM_NORMAL).getPipeline(); }

    @Override
    protected void bufferAndRender(LevelRenderContext context) {
        RenderPipeline pipeline = this.getPipeline();
        VertexFormat formatBinding = pipeline.getVertexFormatBinding(0);
        if (formatBinding == null) { return; }
        PrimitiveTopology primitiveTopology = pipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = getDraw();
        if (draw == null) {draw = BUFFER.appendDraw(formatBinding, primitiveTopology);}

        // Adding Matrix to Matrices Stack based on camera position
        CameraRenderState cameraRenderState = context.levelState().cameraRenderState;
        Vec3 cameraPosition = cameraRenderState.pos;
        float s = this.getFontSize()/256f;
        Matrix4f matrix = new Matrix4f()
                .translate((float) (this.getPosition().x - cameraPosition.x), (float) (this.getPosition().y - cameraPosition.y), (float) (this.getPosition().z - cameraPosition.z))
                .rotate(cameraRenderState.orientation)
                .scale(s, -s, s);

        // Getting and filling the builder
        final var builder = BUFFER.getVertexBuilder(draw);
        Component textComponent = Component.literal(this.getText());
        Style textStyle = textComponent.getStyle();
        Font.PreparedText preparedText = Minecraft.getInstance().font.prepareText(textComponent.getVisualOrderText(), (float) -Minecraft.getInstance().font.width(textComponent)/2, 0f, this.color.toHexIntRGBA(), false, false, 0);
        StagedVertexBuffer.Draw finalDraw = draw;
        preparedText.visit(new Font.GlyphVisitor() { // no clue how this actually works, i mostly copied this from the Skyblocker github
            @Override
            public void acceptGlyph(TextRenderable.Styled glyph) {
                TextureSetup textureSetup = TextureSetup.singleTextureWithLightmap(glyph.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                glyph.render(matrix, builder, 0xF000F0, isVisibleThroughWalls());
                setRenderDetails(pipeline, finalDraw, textureSetup);
            }

            @Override
            public void acceptEffect(TextRenderable glyph) {
                TextureSetup textureSetup = TextureSetup.singleTextureWithLightmap(glyph.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                glyph.render(matrix, builder, 0xF000F0, isVisibleThroughWalls());
                setRenderDetails(pipeline, finalDraw, textureSetup);
            }
        });
    }
}
