package starpocalypse.salvage;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldSource;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BaseSalvageSpecial;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BaseSalvageSpecial.ExtraSalvage;
import java.util.List;

public class DerelictModifyingScript implements EveryFrameScript {

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
            LocationAPI loc = Global.getSector().getCurrentLocation();
            for (CampaignTerrainAPI terrain : loc.getTerrainCopy()) {
                if (!terrain.getType().equals(Terrain.DEBRIS_FIELD  )) continue;
                  DebrisFieldTerrainPlugin plugin = (DebrisFieldTerrainPlugin)terrain.getPlugin();
		    	if (plugin.isScavenged() || plugin.isFadingOut()) continue;                
	    		DebrisFieldParams params = ((DebrisFieldTerrainPlugin)terrain.getPlugin()).getParams();
	    		if (params.lastsDays > 60 || params.source != DebrisFieldSource.BATTLE) continue;
			
                Global.getSector().getPlayerFleet().getContainingLocation().removeEntity(terrain);
             }
            
        for (SectorEntityToken entity : getEntities(Tags.SALVAGEABLE)) {
            if (!entity.hasTag(Tags.EXPIRES)) continue;
			if (!Entities.WRECK.equals(entity.getCustomEntityType()))
				continue;
			if (!(entity.getCustomPlugin() instanceof DerelictShipEntityPlugin))
				continue;

           Global.getSector().getPlayerFleet().getContainingLocation().removeEntity(entity);        
        }
    }

    private List<SectorEntityToken> getEntities(String tag) {
        return Global.getSector().getPlayerFleet().getContainingLocation().getEntitiesWithTag(tag);
    }
}
