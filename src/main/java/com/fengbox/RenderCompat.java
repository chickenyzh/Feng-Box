package com.fengbox;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

/**
 * Compatibility layer for 1.21.2+ rendering API changes.
 * Replaces the removed BufferRenderer.drawWithGlobalProgram() and RenderSystem.setShader() pattern.
 */
public class RenderCompat {

    private static final Supplier<String> NAME = () -> "feng_box";

    /**
     * Draw a BuiltBuffer using POSITION_COLOR pipeline (for quads and triangles).
     * Replaces: RenderSystem.setShader(GameRenderer::getPositionColorProgram) + BufferRenderer.drawWithGlobalProgram(buf.end())
     */
    public static void drawPositionColor(BuiltBuffer built) {
        BuiltBuffer.DrawParameters params = built.getDrawParameters();
        RenderPipeline pipeline;
        if (params.mode() == VertexFormat.DrawMode.LINES || params.mode() == VertexFormat.DrawMode.DEBUG_LINES || params.mode() == VertexFormat.DrawMode.DEBUG_LINE_STRIP) {
            pipeline = RenderPipelines.LINES;
        } else if (params.mode() == VertexFormat.DrawMode.TRIANGLES) {
            pipeline = RenderPipelines.RENDERTYPE_LIGHTNING_DRAGON_RAYS;
        } else if (params.mode() == VertexFormat.DrawMode.TRIANGLE_FAN) {
            pipeline = RenderPipelines.DEBUG_TRIANGLE_FAN;
        } else {
            pipeline = RenderPipelines.DEBUG_QUADS;
        }
        drawWithPipeline(built, pipeline);
    }

    /**
     * Draw a BuiltBuffer using POSITION_TEX_COLOR pipeline.
     * Replaces: RenderSystem.setShader(GameRenderer::getPositionTexColorProgram) + BufferRenderer.drawWithGlobalProgram(buf.end())
     */
    public static void drawPositionTexColor(BuiltBuffer built) {
        drawWithPipeline(built, RenderPipelines.GUI_TEXTURED);
    }

    /**
     * Draw a BuiltBuffer using POSITION_TEX pipeline (no color).
     * Replaces: RenderSystem.setShader(GameRenderer::getPositionTexProgram) + BufferRenderer.drawWithGlobalProgram(buf.end())
     */
    public static void drawPositionTex(BuiltBuffer built) {
        drawWithPipeline(built, RenderPipelines.GUI_TEXTURED);
    }

    /**
     * Draw a BuiltBuffer using POSITION_COLOR pipeline for ESP rendering.
     * Skips depth buffer so ESP shows through walls.
     */
    public static void drawPositionColorEsp(BuiltBuffer built) {
        if (built == null) return;
        try {
            BuiltBuffer.DrawParameters params = built.getDrawParameters();
            ByteBuffer vertexData = built.getBuffer();
            if (vertexData == null || params.vertexCount() == 0) {
                built.close();
                return;
            }
            GpuDevice device = RenderSystem.getDevice();
            if (device == null) {
                built.close();
                return;
            }
            GpuBuffer vertexBuffer = device.createBuffer(NAME, GpuBuffer.USAGE_VERTEX, vertexData);
            Framebuffer fbo = MinecraftClient.getInstance().getFramebuffer();
            GpuTextureView colorView = fbo.getColorAttachmentView();
            CommandEncoder cmd = device.createCommandEncoder();
            // NO depthView -> disables depth test so ESP shows through walls
            RenderPass pass = cmd.createRenderPass(NAME, colorView, OptionalInt.empty());
            RenderPipeline pipeline;
            if (params.mode() == VertexFormat.DrawMode.LINES || params.mode() == VertexFormat.DrawMode.DEBUG_LINES || params.mode() == VertexFormat.DrawMode.DEBUG_LINE_STRIP) {
                pipeline = RenderPipelines.LINES;
            } else if (params.mode() == VertexFormat.DrawMode.TRIANGLES) {
                pipeline = RenderPipelines.RENDERTYPE_LIGHTNING_DRAGON_RAYS;
            } else if (params.mode() == VertexFormat.DrawMode.TRIANGLE_FAN) {
                pipeline = RenderPipelines.DEBUG_TRIANGLE_FAN;
            } else {
                pipeline = RenderPipelines.DEBUG_QUADS;
            }
            pass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setVertexBuffer(0, vertexBuffer);
            if (params.indexCount() > 0) {
                RenderSystem.ShapeIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(params.mode());
                if (indexBuffer != null && indexBuffer.isLargeEnough(params.indexCount())) {
                    GpuBuffer gpuIndexBuffer = indexBuffer.getIndexBuffer(params.indexCount());
                    pass.setIndexBuffer(gpuIndexBuffer, indexBuffer.getIndexType());
                    pass.drawIndexed(0, 0, params.indexCount(), 1);
                } else {
                    pass.draw(params.vertexCount(), 1);
                }
            } else {
                pass.draw(params.vertexCount(), 1);
            }
            pass.close();
            vertexBuffer.close();
            built.close();
        } catch (Exception e) {
            built.close();
        }
    }

    /**
     * Core draw method - creates a RenderPass and draws the BuiltBuffer with the given pipeline.
     */
    private static void drawWithPipeline(BuiltBuffer built, RenderPipeline pipeline) {
        if (built == null) return;
        
        BuiltBuffer.DrawParameters params = built.getDrawParameters();
        ByteBuffer vertexData = built.getBuffer();
        if (vertexData == null || params.vertexCount() == 0) {
            built.close();
            return;
        }

        GpuDevice device = RenderSystem.getDevice();
        if (device == null) {
            built.close();
            return;
        }

        // Create vertex buffer
        GpuBuffer vertexBuffer = device.createBuffer(NAME, GpuBuffer.USAGE_VERTEX, vertexData);

        // Get framebuffer targets
        Framebuffer fbo = MinecraftClient.getInstance().getFramebuffer();
        GpuTextureView colorView = fbo.getColorAttachmentView();
        GpuTextureView depthView = fbo.getDepthAttachmentView();

        // Create command encoder and render pass
        CommandEncoder cmd = device.createCommandEncoder();
        RenderPass pass;
        if (depthView != null) {
            pass = cmd.createRenderPass(NAME, colorView, OptionalInt.empty(), depthView, OptionalDouble.empty());
        } else {
            pass = cmd.createRenderPass(NAME, colorView, OptionalInt.empty());
        }

        // Set pipeline and bind default uniforms (projection, model-view, etc.)
        pass.setPipeline(pipeline);
        RenderSystem.bindDefaultUniforms(pass);

        // Set vertex buffer
        pass.setVertexBuffer(0, vertexBuffer);

        // Draw
        if (params.indexCount() > 0) {
            // For indexed draw modes (QUADS, TRIANGLES with indices, etc.)
            RenderSystem.ShapeIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(params.mode());
            if (indexBuffer != null && indexBuffer.isLargeEnough(params.indexCount())) {
                GpuBuffer gpuIndexBuffer = indexBuffer.getIndexBuffer(params.indexCount());
                pass.setIndexBuffer(gpuIndexBuffer, indexBuffer.getIndexType());
                pass.drawIndexed(0, 0, params.indexCount(), 1);
            } else {
                // Fallback to non-indexed draw
                pass.draw(params.vertexCount(), 1);
            }
        } else {
            // Non-indexed draw
            pass.draw(params.vertexCount(), 1);
        }

        // Cleanup
        pass.close();
        vertexBuffer.close();
        built.close();
    }
}
