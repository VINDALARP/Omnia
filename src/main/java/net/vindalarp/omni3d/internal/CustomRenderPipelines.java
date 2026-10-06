package net.vindalarp.omni3d.internal;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public enum CustomRenderPipelines {
    LINE_NORMAL(RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET).withLocation(Identifier.fromNamespaceAndPath("omni3d", "render_pipelines/lines_pipeline")).build())),
    LINE_SEE_THROUGH(RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET).withLocation(Identifier.fromNamespaceAndPath("omni3d", "render_pipelines/lines_pipeline_see_through")).withDepthStencilState(Optional.empty()).build())),

    BLOCK_HIGHLIGHT_NORMAL(RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET).withLocation(Identifier.fromNamespaceAndPath("omni3d", "render_pipelines/overlay_block_pipeline")).build())),
    BLOCK_HIGHLIGHT_SEE_THROUGH(RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET).withLocation(Identifier.fromNamespaceAndPath("omni3d", "render_pipelines/overlay_block_pipeline_seethrough")).withDepthStencilState(Optional.empty()).build())),

    TEXT_HOLOGRAM_NORMAL(RenderPipelines.register(RenderPipeline.builder(new RenderPipeline.Snippet[]{RenderPipelines.TEXT_SNIPPET}).withLocation("omni3d_pipeline/text_not_see_through").withVertexShader("core/text").withFragmentShader("core/text").withShaderDefine("IS_SEE_THROUGH").withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))/*.withDepthStencilState(Optional.empty())*/.build())),
    TEXT_HOLOGRAM_SEE_THROUGH(RenderPipelines.register(RenderPipeline.builder(new RenderPipeline.Snippet[]{RenderPipelines.TEXT_SNIPPET}).withLocation("pipeline/text_see_through").withVertexShader("core/text").withFragmentShader("core/text").withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withShaderDefine("IS_SEE_THROUGH").withDepthStencilState(Optional.empty()).build()));

    private final RenderPipeline pipeline;
    public RenderPipeline getPipeline() {
        return this.pipeline;
    }

    CustomRenderPipelines(RenderPipeline pipeline) {
        this.pipeline = pipeline;
    }

}
