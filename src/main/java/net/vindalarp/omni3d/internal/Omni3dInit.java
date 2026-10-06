package net.vindalarp.omni3d.internal;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StagedVertexBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import static net.vindalarp.omni3d.internal.Omni3dBasicRenderableBlueprint.BUFFER;

/**
 * Internal Init class for Omnia's 3D features. Has no features.
 */
public class Omni3dInit {

    private static void finalizeDraw(List<Omni3dBasicRenderableBlueprint> instances) {
        BUFFER.upload();

        // Actually rendering with gpu type shit. not explained in the docs at all so this is mostly copied/interpreted from the docs
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), new Vector4f(1f, 1f, 1f, 1f), new Vector3f(), new Matrix4f());
        RenderTarget mainTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = mainTarget.getColorTextureView();
        if (colorTexture == null) {return;}
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "render3dpass", colorTexture, Optional.empty(), mainTarget.getDepthTextureView(), OptionalDouble.empty())) {
            for (Omni3dBasicRenderableBlueprint i : instances) {
                if (i.isVisible() == false) {continue;}
                StagedVertexBuffer.ExecuteInfo info = BUFFER.getExecuteInfo(i.getDraw());
                if (info == null) {
                    continue;
                }
                renderPass.setPipeline((CompiledRenderPipeline) i.getPipeline());
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", dynamicTransforms);
                // Bind texture if applicable:
                // Sampler0 is used for texture inputs in vertices
                if (i.getTexture() != null) {
                    renderPass.setUniform("Sampler0", i.getTexture().texure0(), i.getTexture().sampler0());
                }
                // renderPass.bindTexture("Sampler0", textureSetup.texure0(), textureSetup.sampler0());
                renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
                renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());
                // The base vertex is the starting index when we copied the data into the vertex buffer divided by vertex scale
                renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
            }
        }

        BUFFER.endFrame();
    }

    public static void init() {
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(
                (LevelRenderContext context) -> {

                    List<Omni3dBasicRenderableBlueprint> instances = Omni3dBasicRenderableBlueprint.instanceList;

                    if (Omni3dBasicRenderableBlueprint.instanceList.isEmpty()) {return;}

                    for (Omni3dBasicRenderableBlueprint i : Omni3dBasicRenderableBlueprint.instanceList) {
                        if (i.isVisible() == false) { continue; }
                        i.bufferAndRender(context);
                    }

                    finalizeDraw(instances);

                    instances.forEach(Omni3dBasicRenderableBlueprint::clearRenderDetails);
                }
        );


    }
}
