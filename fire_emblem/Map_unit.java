/* --- MAP UNIT ---
 * 
 *    Description: This is the Map_unit class, used in Fire Emblem Recreation. The class keeps track of the variables
 * for a unit on a battle map or the unit corresponding to a character in the player's party. The class has two methods:
 * "get_unit_info" and "draw". "get_unit_info" finds information such as the stats and animations for a unit or
 * character from the file "map_data.txt". "draw" draws the image of the unit in its required position while taking
 * into account what animation is playing and the current image of the animation.
 * 
 */

package fire_emblem;

import java.util.ArrayList;
//import java.io.File;
import java.util.Scanner;
import java.awt.*;

public class Map_unit
{
	//*** *** Variables *** ***

	public int character_num; //Corresponds to a character, otherwise 0 for generic.
	public int affiliation; //Corresponds to an affiliation, 0 is player, 1 is enemy, 2 is allied.
	public int unit_class; //Corresponds to a class, otherwise 0 for undetermined.
	public String unit_name; //Corresponds to a character, otherwise "" for generic.

	public ArrayList<Item> inventory = new ArrayList<Item>();
	public Item equiped_weapon;

	public static final String[] stat_names = { "MAX HP", "Str", "Mag", "Skl", "Spd", "Lck", "Def", "Res", "Con", "Mov" };
	public int[][] stats = new int[3][10];
	//The order for the stat arrays is Stats, Stat Growths, and Stat Caps. The stats in the array are in this order:
	//max_hp str mag skl spd lck def res con mov

	public static final String[] combat_stats_names = { "HP", "Rng", "Atk", "Hit", "Avoid", "Crit", "Combat_Spd" };
	public int[] combat_stats = new int[7];

	public int[] xp_stats = new int[2]; //First is the unit's level, and the second their current experience. 

	public ArrayList<ArrayList<Image>> unit_img;
	public ArrayList<ArrayList<Integer>> frames_p_img;
	public ArrayList<Integer> n_of_frames;
	public int anmtn_num = 0, last_anmtn_num = 0, default_anmtn_num = 0; //The current animation number and of the animation playing the frame before.
	public long movement_start_time;
	//"has_moved" whether or not the unit is moving to a new location, "finished_moving" if it has finished, and "has_acted" if it has acted.
	public boolean has_moved = false, finished_moving = false, has_acted = false;

	public boolean is_surveyed = false, is_selected = false; //If the unit is being hovered over, or has been selected.
	public boolean buffering_animation = false;
	public ArrayList<ArrayList<Integer>> movement_tiles = new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<Integer>> attack_tiles = new ArrayList<ArrayList<Integer>>();
	public ArrayList<Integer> movement_path;
	public int x, y, temporary_x, temporary_y, x_offset = 0, y_offset = 0;

	//*** *** Constuctors *** ***

	public Map_unit(int given_character_num, int given_affiliation, int given_x, int given_y)
	{
		character_num = given_character_num;
		affiliation = given_affiliation;
		movement_tiles.add(new ArrayList<Integer>());
		movement_tiles.add(new ArrayList<Integer>());
		movement_tiles.add(new ArrayList<Integer>());
		movement_tiles.add(new ArrayList<Integer>());
		attack_tiles.add(new ArrayList<Integer>());
		attack_tiles.add(new ArrayList<Integer>());
		attack_tiles.add(new ArrayList<Integer>());
		x = given_x;
		y = given_y;

		unit_img = new ArrayList<ArrayList<Image>>();
		frames_p_img = new ArrayList<ArrayList<Integer>>();
		n_of_frames = new ArrayList<Integer>();
		if (affiliation == 0)
		{
			get_player_unit_info();
			update_inventory();
		}
	}

	//*** *** Methods *** ***
	public void get_player_unit_info()
	{
		String line = "";
		Scanner line_scan;
		try
		{
			Scanner file_scan = new Scanner(FE_R_Main.file_unit_data);
			line = file_scan.nextLine();
			file_read: while (!line.equals("****FILE_END****"))
			{
				if (line.equals("***Player_Units_Start"))
				{
					while (!line.equals("***Player_Units_End"))
					{
						if (line.equals("**Unit_Start"))
						{
							line = file_scan.nextLine();
							line_scan = new Scanner(line);
							if (line_scan.nextInt() == character_num)
							{
								unit_name = line_scan.nextLine().trim();
								while (!line.equals("*Unit_Stats_Start"))
								{
									line = file_scan.nextLine();
								}
								for (int k = 0; k < 3; k++)
								{
									line = file_scan.nextLine();
									line_scan = new Scanner(line);
									for (int h = 0; h < 10; h++)
									{
										stats[k][h] = line_scan.nextInt();
									}
								}
								combat_stats[0] = stats[0][0];

								line = file_scan.nextLine();
								line_scan = new Scanner(line);
								xp_stats[0] = line_scan.nextInt();
								xp_stats[1] = line_scan.nextInt();
								while (!line.equals("*Unit_Animations_Start"))
								{
									line = file_scan.nextLine();
								}
								line = file_scan.nextLine();
								for (int k = 0; !line.equals("*Unit_Animations_End"); k++)
								{
									unit_img.add(new ArrayList<Image>());
									frames_p_img.add(new ArrayList<Integer>());
									line_scan = new Scanner(line);
									n_of_frames.add(line_scan.nextInt());
									int n = line_scan.nextInt();
									for (int i = 0; i < n; i++)
									{
										frames_p_img.get(k).add(line_scan.nextInt());
									}
									line = file_scan.nextLine();
									line_scan = new Scanner(line);
									for (int i = 0; i < n; i++)
									{
										unit_img.get(k).add(Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + line_scan.next()));
									}
									line = file_scan.nextLine();
								}
								while (!line.equals("*Unit_Items_Start"))
								{
									line = file_scan.nextLine();
								}
								line = file_scan.nextLine();
								while (!line.equals("*Unit_Items_End"))
								{
									line_scan = new Scanner(line);
									int num_of_items = line_scan.nextInt();
									for (int i = 0; i < num_of_items; i++)
									{
										inventory.add(new Item(line_scan.nextInt()));
									}
									line = file_scan.nextLine();
								}
								break file_read;
							}
						}
						line = file_scan.nextLine();
					}
					break file_read;
				}
				line = file_scan.nextLine();
			}
			file_scan.close();
		}
		catch (Exception ex)
		{
			System.out.println("Error Reading File.");
		}
	}

	public void get_generic_unit_info()
	{
		String line = "";
		Scanner line_scan;
		try
		{
			Scanner file_scan = new Scanner(FE_R_Main.file_unit_data);
			line = file_scan.nextLine();
			file_read: while (!line.equals("****FILE_END****"))
			{
				if (line.equals("***Generic_Units_Start"))
				{
					while (!line.equals("***Generic_Units_End"))
					{
						if (line.equals("**Unit_Start"))
						{
							line = file_scan.nextLine();
							line_scan = new Scanner(line);
							if (line_scan.nextInt() == unit_class)
							{
								unit_name = line_scan.nextLine().trim();
								while (!line.equals("*Unit_Animations_Start"))
								{
									line = file_scan.nextLine();
								}
								line = file_scan.nextLine();
								for (int k = 0; !line.equals("*Unit_Animations_End"); k++)
								{
									unit_img.add(new ArrayList<Image>());
									frames_p_img.add(new ArrayList<Integer>());
									line_scan = new Scanner(line);
									n_of_frames.add(line_scan.nextInt());
									int n = line_scan.nextInt();
									for (int i = 0; i < n; i++)
									{
										frames_p_img.get(k).add(line_scan.nextInt());
									}
									line = file_scan.nextLine();
									line_scan = new Scanner(line);
									for (int i = 0; i < n; i++)
									{
										unit_img.get(k).add(Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + line_scan.next()));
									}
									line = file_scan.nextLine();
								}
								break file_read;
							}
						}
						line = file_scan.nextLine();
					}
					break file_read;
				}
				line = file_scan.nextLine();
			}
			file_scan.close();
		}
		catch (Exception ex)
		{
			System.out.println("Error reading the file " + FE_R_Main.file_unit_data + " from Map_unit.");
		}
	}

	public void get_generic_unit_stats(int given_class, int[][] given_stats, int[] given_xp_stats, ArrayList<Item> given_inventory)
	{
		unit_class = given_class;
		stats = given_stats;
		combat_stats[0] = stats[0][0];
		xp_stats = given_xp_stats;
		inventory = given_inventory;

		update_inventory();
	}

	public void deselect()
	{
		is_selected = false;

		if (Map_mod.map_cursor.selected_unit == this)
		{
			Map_mod.map_cursor.selected_unit = null;
			Map_mod.map_cursor.cursor_has_moved();
		}

		if (affiliation == 0) anmtn_num = default_anmtn_num;
		update_tiles();

		has_moved = false;
		finished_moving = false;
	}

	public void died()
	{
		is_selected = false;

		for (int i = 0; i < Map_mod.map_units.get(affiliation).size(); i++)
		{
			if (Map_mod.map_units.get(affiliation).get(i) == this) Map_mod.map_units.get(affiliation).remove(i);
		}
		affiliation = -1;
		has_moved = false;
		finished_moving = false;

		if (Map_mod.map_cursor.selected_unit == this)
		{
			Map_mod.map_cursor.selected_unit = null;
		}
		Map_mod.map_cursor.cursor_has_moved();

	}

	public void start_turn()
	{
		has_acted = false;
		update_tiles();
		do_passive_effects();

		default_anmtn_num = 0;
		anmtn_num = default_anmtn_num;
	}

	public void finish_turn()
	{
		has_acted = true;
		deselect();
		x = temporary_x;
		y = temporary_y;

		default_anmtn_num = 6;
		anmtn_num = default_anmtn_num;

		if (Map_mod.phase == 0) Map_mod.map_cursor.cursor_has_moved();

		Map_mod.check_phase();
	}

	public void update_inventory()
	{
		if (equiped_weapon == null)
		{
			for (int i = 0; i < inventory.size(); i++)
			{
				if (inventory.get(i).item_type == 0)
				{
					equiped_weapon = inventory.get(i);
					break;
				}
			}
		}

		if (equiped_weapon != null)
		{
			combat_stats[6] = stats[0][4] - Math.max(equiped_weapon.weapon_stats[4] - stats[0][8], 0);

			combat_stats[1] = equiped_weapon.weapon_stats[0];

			if (equiped_weapon.is_physical) combat_stats[2] = stats[0][1] + equiped_weapon.weapon_stats[1];
			else combat_stats[2] = stats[0][2] + equiped_weapon.weapon_stats[1];

			combat_stats[3] = equiped_weapon.weapon_stats[2] + (stats[0][3] * 2) + (stats[0][5] / 2);
			int terrain_avoidance_bonus = Map_mod.terrain_avoidance[Map_mod.terrain[temporary_x][temporary_y]];
			combat_stats[4] = (combat_stats[6] * 2) + stats[0][5] + terrain_avoidance_bonus;
			combat_stats[5] = equiped_weapon.weapon_stats[3] + (stats[0][3] / 2);
		}
		else
		{
			//Reset Combat Stats
			combat_stats[6] = stats[0][4];

			combat_stats[1] = -1;
			combat_stats[2] = -1;
			combat_stats[3] = -1;
			int terrain_avoidance_bonus = Map_mod.terrain_avoidance[Map_mod.terrain[temporary_x][temporary_y]];
			combat_stats[4] = (combat_stats[6] * 2) + stats[0][5] + terrain_avoidance_bonus;
			combat_stats[5] = -1;
		}
	}

	public void level_up()
	{
		System.out.println("\n(" + unit_name + ")");
		System.out.println("LEVEL UP!");
		xp_stats[0]++;
		xp_stats[1] = xp_stats[1] % 100;

		for (int i = 0; i < 8; i++)
		{
			int num_RNG = (int) (Math.random() * 100);
			if (num_RNG < stats[1][i])
			{
				stats[0][i]++;
				System.out.println(stat_names[i] + " +1");
			}
		}
		System.out.println();
	}

	public void do_passive_effects()
	{
		if (Map_mod.terrain[x][y] == 3)
		{
			combat_stats[0] = Math.min(stats[0][0], combat_stats[0] + (stats[0][0] / 10));
		}
	}

	public void update_tiles()
	{
		movement_tiles.get(0).clear();
		movement_tiles.get(1).clear();
		movement_tiles.get(2).clear();
		movement_tiles.get(3).clear();

		if (stats[0][9] != 0)
		{
			add_movement_tile(x, y, -1, stats[0][9] + Map_mod.terrain_mov_cost[Map_mod.terrain[x][y]]);
		}

		attack_tiles.get(0).clear();
		attack_tiles.get(1).clear();
		attack_tiles.get(2).clear();
		if (combat_stats[1] != 0)
		{
			int range = combat_stats[1];
			for (int i = 0; i < movement_tiles.get(0).size(); i++)
			{
				int tile_x = movement_tiles.get(0).get(i), tile_y = movement_tiles.get(1).get(i);
				if (Map_mod.find_unit(tile_x, tile_y) == null || Map_mod.find_unit(tile_x, tile_y) == this)
				{
					for (int j = 1; j <= range; j++)
					{
						for (int k = 0; k <= j; k++)
						{
							add_attack_tile(tile_x - (j - k), tile_y - k, i);
							add_attack_tile(tile_x + (j - k), tile_y + k, i);
						}
					}
				}
			}

		}
	}

	public void add_movement_tile(int tile_x, int tile_y, int from_index, int mov)
	{
		if (tile_x < 0 || !(tile_x < Map_mod.x_dimension)) return;
		if (tile_y < 0 || !(tile_y < Map_mod.y_dimension)) return;

		{
			int current_terrain_cost = Map_mod.terrain_mov_cost[Map_mod.terrain[tile_x][tile_y]];
			mov -= current_terrain_cost;
			if (mov < 0 || current_terrain_cost == -1) return;

			if (affiliation == 0 || affiliation == 2)
			{
				if (Map_mod.find_unit(1, tile_x, tile_y) != null) return;
			}
			else
			{
				if (Map_mod.find_unit(0, tile_x, tile_y) != null || Map_mod.find_unit(2, tile_x, tile_y) != null) return;
			}

		}

		int this_index = find_movement_tile(tile_x, tile_y);
		if (this_index != -1)
		{
			if (movement_tiles.get(3).get(this_index) > mov)
			{
				return;
			}
			else
			{
				movement_tiles.get(2).set(this_index, from_index);
				movement_tiles.get(3).set(this_index, mov);
			}
		}
		else
		{
			this_index = movement_tiles.get(0).size();
			movement_tiles.get(0).add(tile_x);
			movement_tiles.get(1).add(tile_y);
			movement_tiles.get(2).add(from_index);
			movement_tiles.get(3).add(mov);
		}
		if (mov == 0) return;
		add_movement_tile(tile_x, tile_y - 1, this_index, mov);
		add_movement_tile(tile_x, tile_y + 1, this_index, mov);
		add_movement_tile(tile_x - 1, tile_y, this_index, mov);
		add_movement_tile(tile_x + 1, tile_y, this_index, mov);
	}

	public int find_movement_tile(int x_val, int y_val)
	{
		for (int i = 0; i < movement_tiles.get(0).size(); i++)
		{
			if (movement_tiles.get(0).get(i) == x_val && movement_tiles.get(1).get(i) == y_val)
			{
				return i;
			}
		}
		return -1;
	}

	public void add_attack_tile(int x_val, int y_val, int movement_tile_index)
	{
		if (find_movement_tile(x_val, y_val) != -1 || find_attack_tile(x_val, y_val) != -1) return;
		else
		{
			attack_tiles.get(0).add(x_val);
			attack_tiles.get(1).add(y_val);
			attack_tiles.get(2).add(movement_tile_index);
		}
	}

	public int find_attack_tile(int x_val, int y_val)
	{
		for (int i = 0; i < attack_tiles.get(0).size(); i++)
		{
			if (attack_tiles.get(0).get(i) == x_val && attack_tiles.get(1).get(i) == y_val)
			{
				return i;
			}
		}
		return -1;
	}

	public ArrayList<Map_unit> find_units_in_range(int given_affiliation)
	{
		ArrayList<Map_unit> in_range = new ArrayList<Map_unit>();
		for (int j = 1; j <= combat_stats[1]; j++)
		{
			for (int k = 0; k <= j; k++)
			{
				Map_unit unit_1 = Map_mod.find_unit(given_affiliation, temporary_x - (j - k), temporary_y - k);
				Map_unit unit_2 = Map_mod.find_unit(given_affiliation, temporary_x + (j - k), temporary_y + k);
				if (unit_1 != null) in_range.add(unit_1);
				if (unit_2 != null) in_range.add(unit_2);
			}
		}
		return in_range;
	}

	public static int find_facing_animation(int location_code)
	{
		if (location_code == 0) return 4;
		else if (location_code == 1) return 5;
		else if (location_code == 2) return 2;
		else if (location_code == 3) return 3;
		else return -1;
	}

	public void draw()
	{
		long current_time = FE_R_Main.current_time;

		if (!has_moved)
		{
			x_offset = 0;
			y_offset = 0;
			temporary_x = x;
			temporary_y = y;
		}
		else
		{
			is_moving: if (!finished_moving)
			{
				Map_cursor map_cursor = Map_mod.map_cursor;

				int current_tile = (int) (current_time - movement_start_time) / 4;
				int time_on_tile = (int) ((current_time - movement_start_time) % 4);
				temporary_x = x;
				temporary_y = y;
				x_offset = 0;
				y_offset = 0;
				for (int i = 0; i < current_tile; i++)
				{
					int next_loc = movement_path.get(i);
					if (next_loc == 0) temporary_x++;
					if (next_loc == 1) temporary_x--;
					if (next_loc == 2) temporary_y++;
					if (next_loc == 3) temporary_y--;
				}
				if (current_tile == movement_path.size())
				{
					finished_moving = true;
					if (affiliation == 0) map_cursor.performimg_actions();
					break is_moving;
				}

				int next_loc = movement_path.get(current_tile);
				anmtn_num = find_facing_animation(next_loc);
				if (next_loc == 0) x_offset = time_on_tile * 8;
				if (next_loc == 1) x_offset = (-1) * time_on_tile * 8;
				if (next_loc == 2) y_offset = time_on_tile * 8;
				if (next_loc == 3) y_offset = (-1) * time_on_tile * 8;

			}
			if (finished_moving)
			{
			}
		}

		int draw_x, draw_y;
		if (anmtn_num != last_anmtn_num) buffering_animation = true;
		int n, frames_left = (int) (current_time % n_of_frames.get(anmtn_num));
		for (n = 0; n < frames_p_img.get(anmtn_num).size() && frames_left > frames_p_img.get(anmtn_num).get(n); n++)
		{
			frames_left -= frames_p_img.get(anmtn_num).get(n);
		}
		draw_x = temporary_x * FE_R_Main.p_per_grid - 14 + x_offset - Map_mod.x_offset;
		draw_y = temporary_y * FE_R_Main.p_per_grid - 26 + y_offset - Map_mod.y_offset;
		if (is_surveyed && affiliation == 0) draw_y -= 2;
		if (buffering_animation)
		{
			if (n == 0) buffering_animation = false;
			FE_R_Main.c.drawImage(unit_img.get(anmtn_num).get(0), draw_x, draw_y);
		}
		else
		{
			FE_R_Main.c.drawImage(unit_img.get(anmtn_num).get(n), draw_x, draw_y);
		}

		last_anmtn_num = anmtn_num;
	}

	public void draw_tiles()
	{
		Image mov_tile_img = Map_mod.movement_tile, attack_tile_img = Map_mod.attack_tile;
		for (int i = 0; i < movement_tiles.get(0).size(); i++)
		{
			int tile_x = movement_tiles.get(0).get(i), tile_y = movement_tiles.get(1).get(i);
			tile_x = tile_x * FE_R_Main.p_per_grid - Map_mod.x_offset;
			tile_y = tile_y * FE_R_Main.p_per_grid - Map_mod.y_offset;
			FE_R_Main.c.drawImage(mov_tile_img, tile_x, tile_y);
		}
		for (int i = 0; i < attack_tiles.get(0).size(); i++)
		{
			int tile_x = attack_tiles.get(0).get(i), tile_y = attack_tiles.get(1).get(i);
			tile_x = tile_x * FE_R_Main.p_per_grid - Map_mod.x_offset;
			tile_y = tile_y * FE_R_Main.p_per_grid - Map_mod.y_offset;
			FE_R_Main.c.drawImage(attack_tile_img, tile_x, tile_y);
		}
	}
}
