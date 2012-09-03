package basicpipes.pipes;

import basicpipes.TradeHelper;
import basicpipes.pipes.api.ILiquidProducer;
import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.electricity.TileEntityElectricUnit;
import universalelectricity.extend.IElectricUnit;

public class TileEntityPump extends TileEntityElectricUnit implements ILiquidProducer,IElectricUnit {
 int dCount = 0;
 float eStored = 0;
 float eMax = 2000;
 int wStored = 0;
 int wMax = 10;
 public TileEntity[] sList = {null,null,null,null,null,null};
 private int count = 0;
	@Override
	public void onDisable(int duration) {
		dCount = duration;
	}

	@Override
	public boolean isDisabled() {
		if(dCount <= 0)
		{
			return false;
		}
		return true;
	}

	@Override
	public void onUpdate(float watts, float voltage, ForgeDirection side) {
		super.onUpdate(watts, voltage, side);
		sList = TradeHelper.getSourounding(this);
		if(!worldObj.isRemote)
		{
			count++;
			if (electricityRequest() > 0 && canConnect(side))
	        {
	            float rejectedElectricity = (float) Math.max((this.eStored + watts) - this.eMax, 0.0);
	            this.eStored = (float) Math.max(this.eStored + watts - rejectedElectricity, 0.0);
	        }
			int bBlock = worldObj.getBlockId(xCoord, yCoord -1, zCoord);
			if(bBlock == Block.waterStill.blockID && this.eStored > 500 && this.wStored < this.wMax && count>=2)
			{
				eStored -= 500;
				wStored += 1;
				worldObj.setBlockAndMetadataWithNotify(xCoord, yCoord-1, zCoord, 0, 0);
				count = 0;
			}
		}
		 
	}

	@Override
	public float electricityRequest() {
		return Math.max(eMax - eStored,0);
	}
	@Override
	public boolean canReceiveFromSide(ForgeDirection side) {
		if(side != ForgeDirection.DOWN)
		{
		return true;
		}
		return false;
	}

	@Override
	public float getVoltage() {
		return 240;
	}

	@Override
	public int getTickInterval() {
		return 10;
	}

	@Override
	public int onProduceLiquid(int type, int maxVol, ForgeDirection side) {
		if(type == 1 && wStored > 0)
		{
			int tradeW = Math.min(maxVol, wStored);
			wStored -= tradeW;
	        return tradeW;
		}
		return 0;
	}

	@Override
	public boolean canProduceLiquid(int type, ForgeDirection side) {
		if(type == 1 && side != ForgeDirection.DOWN)
		{
			return true;
		}
		return false;
	}
}