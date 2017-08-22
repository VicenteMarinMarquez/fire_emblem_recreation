/* --- ARMY AI ---
 * 
 *    Description: This is the Army_AI class, used in Fire Emblem Recreation. The class represents an army and has its
 * very own simple AI. The enemy and allied armies are controlled by this class.
 * 
 */

package fire_emblem;

import java.util.ArrayList;

public class Army_AI
{
	//*** *** Variables *** ***

	public ArrayList<ArrayList<Map_unit>> map_units;
	public ArrayList<Map_unit> army_units;
	public ArrayList<Map_unit> units_left = new ArrayList<Map_unit>();
	public Map_unit current_unit = null, target_unit = null;
	public int affiliation;

	public ArrayList<Integer> unit_path = new ArrayList<Integer>();

	//*** *** Constuctors *** ***

	public Army_AI(ArrayList<ArrayList<Map_unit>> given_map_units, int given_affiliation)
	{
		map_units = given_map_units;
		affiliation = given_affiliation;
		army_units = map_units.get(affiliation);
		update_units_left();

	}

	//*** *** Methods *** ***

	//This is the method that is constantly called to ask the AI what it will do.
	public void called_method()
	{
		if (units_left.isEmpty())
		{
			update_units_left();
			return;
		}

		current_unit = units_left.get(0);
		if (!current_unit.has_moved)
		{
			target_unit = null;
			current_unit.update_tiles();
			boolean has_enemy_in_range = false;
			for (int i = 0; i < current_unit.attack_tiles.get(0).size(); i++)
			{
				int tile_x = current_unit.attack_tiles.get(0).get(i), tile_y = current_unit.attack_tiles.get(1).get(i);
				Map_unit unit_in_range = Map_mod.find_unit(0, tile_x, tile_y);
				if (unit_in_range != null)
				{
					has_enemy_in_range = true;
					target_unit = unit_in_range;
					unit_path = Map_mod.create_path(current_unit.movement_tiles, current_unit.attack_tiles.get(2).get(i));
					break;
				}
			}

			if (has_enemy_in_range)
			{
				current_unit.has_moved = true;
				current_unit.movement_path = unit_path;
				current_unit.movement_start_time = FE_R_Main.current_time;
				return;
			}
			else
			{
				current_unit.finish_turn();
				Map_ui.update_ui();
				update_units_left();
			}

		}
		if (current_unit.finished_moving)
		{
			Combat_mod.intiate_combat(current_unit, target_unit);
			Map_ui.update_ui();
			update_units_left();
		}
	}

	public void update_units_left()
	{
		units_left.clear();
		for (int i = 0; i < army_units.size(); i++)
		{
			if (!army_units.get(i).has_acted) units_left.add(army_units.get(i));
		}
		if (units_left.isEmpty())
		{
			Map_mod.check_phase();
			return;
		}
	}

}