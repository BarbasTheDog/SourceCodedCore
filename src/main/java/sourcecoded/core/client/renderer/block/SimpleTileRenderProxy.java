package sourcecoded.core.client.renderer.block;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

/**
 * Allows blocks with TileEntities to render their TESR
 * in the inventory of a player, or as an EntityItem.
 *
 * in the getRenderType of the Block Object,
 * return renderID of this object
 */
public class SimpleTileRenderProxy implements ISimpleBlockRenderingHandler {

    public static final int renderID = RenderingRegistry.getNextAvailableRenderId();

    TileEntity te;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (block instanceof ITileEntityProvider) {
            ITileEntityProvider prov = (ITileEntityProvider) block;
            te = prov.createNewTileEntity(null, metadata);
        } else return;

        if (block instanceof IBlockRenderHook) {
            IBlockRenderHook hook = (IBlockRenderHook) block;
            hook.callbackInventory(te);
        }

        glRotatef(90F, 0F, 1F, 0F);
        glTranslatef(-0.5F, -0.5F, -0.5F);
        float scale = 1F;
        glScalef(scale, scale, scale);

        TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

}
