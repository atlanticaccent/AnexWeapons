package data.scripts.weapons.everyFrame;

import com.fs.starfarer.api.combat.*;
import data.scripts.weapons.ai.anexweapons_mtb_dhwij_turret_projectile_AI;
import org.lazywizard.lazylib.combat.CombatUtils;

import java.util.ArrayList;
import java.util.List;

public class anexweapons_mtb_dhwij_turret_everyFrame implements EveryFrameWeaponEffectPlugin {

	private List<DamagingProjectileAPI> alreadyRegisteredProjectiles = new ArrayList<DamagingProjectileAPI>();

	@Override
	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

		ShipAPI source = weapon.getShip();
		ShipAPI target = null;

		if(source.getWeaponGroupFor(weapon)!=null ){
			//WEAPON IN AUTOFIRE
			if(source.getWeaponGroupFor(weapon).isAutofiring()  //weapon group is autofiring
					&& source.getSelectedGroupAPI()!=source.getWeaponGroupFor(weapon)){ //weapon group is not the selected group
				target = source.getWeaponGroupFor(weapon).getAutofirePlugin(weapon).getTargetShip();
			}
			else {
				target = source.getShipTarget();
			}
		}

		for (DamagingProjectileAPI proj : CombatUtils.getProjectilesWithinRange(weapon.getLocation(), 200f)) {
			if (proj.getWeapon() == weapon && !alreadyRegisteredProjectiles.contains(proj) && engine.isEntityInPlay(proj) && !proj.didDamage()) {
				engine.addPlugin(new anexweapons_mtb_dhwij_turret_projectile_AI(proj, target));
				alreadyRegisteredProjectiles.add(proj);
			}
		}

		//And clean up our registered projectile list
		List<DamagingProjectileAPI> cloneList = new ArrayList<>(alreadyRegisteredProjectiles);
		for (DamagingProjectileAPI proj : cloneList) {
			if (!engine.isEntityInPlay(proj) || proj.didDamage()) {
				alreadyRegisteredProjectiles.remove(proj);
			}
		}
	}
}
