package micdoodle8.mods.galacticraft.mars;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.src.Block;
import net.minecraft.src.EntityCow;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumAction;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import cpw.mods.fml.common.FMLLog;

public class GCMarsItemReinforcedBucket extends GCMarsItem
{
	public int isFull;
	
    public GCMarsItemReinforcedBucket(int par1, int par2)
    {
        super(par1);
        this.maxStackSize = 1;
        this.isFull = par2;
        this.setCreativeTab(GalacticraftCore.galacticraftTab);
    }

    @Override
    public ItemStack onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	
    	if (this.shiftedIndex != GCMarsItems.reinforcedBucketMilk.shiftedIndex)
    	{
    		super.onFoodEaten(par1ItemStack, par2World, par3EntityPlayer);
    	}
    	else
    	{
            if (!par3EntityPlayer.capabilities.isCreativeMode && par1ItemStack.getTagCompound().getInteger("bucketUses") > 1)
            {
            	FMLLog.info("" + par1ItemStack.getTagCompound().getInteger("bucketUses"));
            	par1ItemStack.getTagCompound().setInteger("bucketUses", par1ItemStack.getTagCompound().getInteger("bucketUses") - 1);
            	return par1ItemStack;
            }
            else if (!par3EntityPlayer.capabilities.isCreativeMode && par1ItemStack.getTagCompound().getInteger("bucketUses") == 1)
            {
                --par1ItemStack.stackSize;
            }

            if (!par2World.isRemote)
            {
                par3EntityPlayer.curePotionEffects(par1ItemStack);
            }
    	}

        return par1ItemStack.stackSize <= 0 ? new ItemStack(GCMarsItems.reinforcedBucketEmpty) : par1ItemStack;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
    	if (this.shiftedIndex != GCMarsItems.reinforcedBucketMilk.shiftedIndex)
    	{
    		return 0;
    	}
    	else
    	{
            return 32;
    	}
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
    	if (this.shiftedIndex != GCMarsItems.reinforcedBucketMilk.shiftedIndex)
    	{
    		return EnumAction.none;
    	}
    	else
    	{
            return EnumAction.drink;
    	}
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
		if (par1ItemStack.getTagCompound() == null)
		{
			par1ItemStack.setTagCompound(new NBTTagCompound());
		}
		
		if (par1ItemStack != null && par1ItemStack.getTagCompound().getInteger("bucketUses") != 1 && par1ItemStack.getTagCompound().getInteger("bucketUses") != 2 && par1ItemStack.getTagCompound().getInteger("bucketUses") != 3)
		{
			par1ItemStack.getTagCompound().setInteger("bucketUses", 2);
		}
		
    	if (this.shiftedIndex != GCMarsItems.reinforcedBucketMilk.shiftedIndex)
    	{
    		float var4 = 1.0F;
            double var5 = par3EntityPlayer.prevPosX + (par3EntityPlayer.posX - par3EntityPlayer.prevPosX) * (double)var4;
            double var7 = par3EntityPlayer.prevPosY + (par3EntityPlayer.posY - par3EntityPlayer.prevPosY) * (double)var4 + 1.62D - (double)par3EntityPlayer.yOffset;
            double var9 = par3EntityPlayer.prevPosZ + (par3EntityPlayer.posZ - par3EntityPlayer.prevPosZ) * (double)var4;
            boolean var11 = this.isFull == 0;
            MovingObjectPosition var12 = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, var11);

            if (var12 == null)
            {
            	if (par1ItemStack != null)
                	return par1ItemStack;
            }
            else
            {
                if (var12.typeOfHit == EnumMovingObjectType.TILE)
                {
                    int var13 = var12.blockX;
                    int var14 = var12.blockY;
                    int var15 = var12.blockZ;

                    if (!par2World.canMineBlock(par3EntityPlayer, var13, var14, var15))
                    {
                    	if (par1ItemStack != null)
                        	return par1ItemStack;
                    }

                    if (this.isFull == 0)
                    {
                        if (!par3EntityPlayer.func_82247_a(var13, var14, var15, var12.sideHit, par1ItemStack))
                        {
                        	if (par1ItemStack != null)
                            	return par1ItemStack;
                        }

                        if (par2World.getBlockMaterial(var13, var14, var15) == Material.water && par2World.getBlockMetadata(var13, var14, var15) == 0)
                        {
                            par2World.setBlockWithNotify(var13, var14, var15, 0);

                            if (par3EntityPlayer.capabilities.isCreativeMode)
                            {
                            	if (par1ItemStack != null)
                                	return par1ItemStack;
                            }

                            if (--par1ItemStack.stackSize <= 0)
                            {
                            	ItemStack bucket = new ItemStack(GCMarsItems.reinforcedBucketWater);
                            	bucket.setTagCompound(new NBTTagCompound());
                            	bucket.getTagCompound().setInteger("bucketUses", 2);
                                return bucket;
                            }

                            if (!par3EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.bucketWater)))
                            {
                            	ItemStack bucket = new ItemStack(GCMarsItems.reinforcedBucketWater);
                            	bucket.setTagCompound(new NBTTagCompound());
                            	bucket.getTagCompound().setInteger("bucketUses", 2);
                                par3EntityPlayer.dropPlayerItem(bucket);
                            }

                            if (par1ItemStack != null)
                            	return par1ItemStack;
                        }

                        if (par2World.getBlockMaterial(var13, var14, var15) == Material.lava && par2World.getBlockMetadata(var13, var14, var15) == 0)
                        {
                            par2World.setBlockWithNotify(var13, var14, var15, 0);

                            if (par3EntityPlayer.capabilities.isCreativeMode)
                            {
                            	if (par1ItemStack != null)
                                	return par1ItemStack;
                            }

                            if (--par1ItemStack.stackSize <= 0)
                            {
                            	ItemStack bucket = new ItemStack(GCMarsItems.reinforcedBucketLava);
                            	bucket.setTagCompound(new NBTTagCompound());
                            	bucket.getTagCompound().setInteger("bucketUses", 2);
                                return bucket;
                            }

                            if (!par3EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.bucketLava)))
                            {
                            	ItemStack bucket = new ItemStack(GCMarsItems.reinforcedBucketLava);
                            	bucket.setTagCompound(new NBTTagCompound());
                            	bucket.getTagCompound().setInteger("bucketUses", 2);
                                par3EntityPlayer.dropPlayerItem(bucket);
                            }

                            if (par1ItemStack != null)
                            	return par1ItemStack;
                        }

                        if (par2World.getBlockMaterial(var13, var14, var15) == GCMarsBlocks.bacterialSludge && par2World.getBlockMetadata(var13, var14, var15) == 0)
                        {
                            par2World.setBlockWithNotify(var13, var14, var15, 0);

                            if (par3EntityPlayer.capabilities.isCreativeMode)
                            {
                            	if (par1ItemStack != null)
                                	return par1ItemStack;
                            }

                            if (--par1ItemStack.stackSize <= 0)
                            {
                            	ItemStack bucket = new ItemStack(GCMarsItems.reinforcedBucketBacteria);
                            	bucket.setTagCompound(new NBTTagCompound());
                            	bucket.getTagCompound().setInteger("bucketUses", 2);
                                return bucket;
                            }

                            if (!par3EntityPlayer.inventory.addItemStackToInventory(new ItemStack(GCMarsItems.reinforcedBucketBacteria)))
                            {
                            	ItemStack bucket = new ItemStack(GCMarsItems.reinforcedBucketBacteria);
                            	bucket.setTagCompound(new NBTTagCompound());
                            	bucket.getTagCompound().setInteger("bucketUses", 2);
                                par3EntityPlayer.dropPlayerItem(bucket);
                            }

                            if (par1ItemStack != null)
                            	return par1ItemStack;
                        }
                        
                        if (par1ItemStack != null)
                        	if (par1ItemStack != null)
                            	return par1ItemStack;
                    }
                    else
                    {
                        if (this.isFull < 0)
                        {
                            return new ItemStack(GCMarsItems.reinforcedBucketEmpty);
                        }

                        if (var12.sideHit == 0)
                        {
                            --var14;
                        }

                        if (var12.sideHit == 1)
                        {
                            ++var14;
                        }

                        if (var12.sideHit == 2)
                        {
                            --var15;
                        }

                        if (var12.sideHit == 3)
                        {
                            ++var15;
                        }

                        if (var12.sideHit == 4)
                        {
                            --var13;
                        }

                        if (var12.sideHit == 5)
                        {
                            ++var13;
                        }

                        if (!par3EntityPlayer.func_82247_a(var13, var14, var15, var12.sideHit, par1ItemStack))
                        {
                            return par1ItemStack;
                        }

                        if (this.tryPlaceContainedLiquid(par2World, var5, var7, var9, var13, var14, var15) && !par3EntityPlayer.capabilities.isCreativeMode && par1ItemStack.getTagCompound() != null && par1ItemStack.getTagCompound().getInteger("bucketUses") == 1)
                        {
                            return new ItemStack(GCMarsItems.reinforcedBucketEmpty);
                        }
                        else if (this.tryPlaceContainedLiquid(par2World, var5, var7, var9, var13, var14, var15) && !par3EntityPlayer.capabilities.isCreativeMode && par1ItemStack.getTagCompound().getInteger("bucketUses") > 0)
                        {
                        	par1ItemStack.getTagCompound().setInteger("bucketUses", par1ItemStack.getTagCompound().getInteger("bucketUses") - 1);
                        }
                    }
                }
                else if (this.isFull == 0 && var12.entityHit instanceof EntityCow)
                {
                    return new ItemStack(GCMarsItems.reinforcedBucketMilk);
                }

                if (par1ItemStack != null)
                	return par1ItemStack;
            }
    	}
    	else
    	{
            par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
    	}
    	
		return par1ItemStack;
    }
    
    public boolean tryPlaceContainedLiquid(World par1World, double par2, double par4, double par6, int par8, int par9, int par10)
    {
    	if (this.isFull <= 0)
        {
            return false;
        }
        else if (!par1World.isAirBlock(par8, par9, par10) && par1World.getBlockMaterial(par8, par9, par10).isSolid())
        {
            return false;
        }
        else
        {
            if (par1World.provider.isHellWorld && this.isFull == GCMarsItems.reinforcedBucketWater.shiftedIndex)
            {
                par1World.playSoundEffect(par2 + 0.5D, par4 + 0.5D, par6 + 0.5D, "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

                for (int var11 = 0; var11 < 8; ++var11)
                {
                    par1World.spawnParticle("largesmoke", (double)par8 + Math.random(), (double)par9 + Math.random(), (double)par10 + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            }
            else
            {
                par1World.setBlockAndMetadataWithNotify(par8, par9, par10, this.isFull, 0);
            }

            return true;
        }
    }
}
