/* --- MAP CURSOR ---
 * 
 *    Description: This is the Map_cursor class, used in Fire Emblem Recreation. The class keeps track of its position
 * on the map with coordinates and a variable "moderator.p_per_grid" to find the location of its image on the console. The class has two
 * methods: "check_inputs" and "draw". "check_inputs" receives the current and last keys pressed and changes the
 * position of the cursor on the map. "draw" simply draws the image of the cursor in its required position depending on
 * the current frame of animation.
 * 
 */

package fire_emblem;

import java.util.ArrayList;
//import java.io.File;
//import java.util.Scanner;
import java.awt.*;

public class Map_cursor
{
	//*** *** Variables *** ***
	
	public static int max_x, max_y;

	public final static Image[] cursor_img = new Image[4];
	public Image[] arrow_sprites = new Image[14];
	public ArrayList<Image> arrow_img = new ArrayList<Image>();
	public ArrayList<Integer> arrow_path = new ArrayList<Integer>();
	public final static int[] frames_p_img = { 10, 10, 10, 10 };
	public final static int n_of_frames = 40;
	public int anmtn_num = 0; //anmtn_num is the animation number.

	public int x = 0, y = 0, tile_index = -1;
	public boolean is_roaming = true; //If the cursor is roaming across the map.
	public Map_unit surveying_unit = null; //The unit the cursor is currently hovering over.
	public Map_unit selected_unit = null;

	//*** *** Constructors *** ***

	public Map_cursor()
	{
		
		max_x = Map_mod.x_dimension - 1;
		max_y = Map_mod.y_dimension - 1;

		cursor_img[0] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\spr_cursor_0.png");
		cursor_img[1] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\spr_cursor_1.png");
		cursor_img[2] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\spr_cursor_0.png");
		cursor_img[3] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\spr_cursor_2.png");
		{
			String img_root = FE_R_Main.resources_path + "Images\\spr_arrow_";
			for (int i = 0; i < 14; i++)
			{
				String img_location = img_root + i + ".png";
				arrow_sprites[i] = Toolkit.getDefaultToolkit().getImage(img_location);
			}
		}
	}

	//*** *** Methods *** ***
	public void check_inputs()
	{
		if (is_roaming)
		{
			int last_x = x, last_y = y;

			if (FE_R_Main.key_char == 's')
			{
				if (FE_R_Main.last_key_char != 's') y++;
				else if (FE_R_Main.time_char > 20 && FE_R_Main.time_char % 4 == 0) y++;
			}
			if (FE_R_Main.key_char == 'w')
			{
				if (FE_R_Main.last_key_char != 'w') y--;
				else if (FE_R_Main.time_char > 20 && FE_R_Main.time_char % 4 == 0) y--;
			}
			if (FE_R_Main.key_char == 'd')
			{
				if (FE_R_Main.last_key_char != 'd') x++;
				else if (FE_R_Main.time_char > 20 && FE_R_Main.time_char % 4 == 0) x++;
			}
			if (FE_R_Main.key_char == 'a')
			{
				if (FE_R_Main.last_key_char != 'a') x--;
				else if (FE_R_Main.time_char > 20 && FE_R_Main.time_char % 4 == 0) x--;
			}

			x = Math.min(max_x, x);
			x = Math.max(0, x);
			y = Math.min(max_y, y);
			y = Math.max(0, y);

			if (x != last_x || y != last_y) cursor_has_moved();

			if (selected_unit != null)
			{
				if (selected_unit.affiliation == 0 && !selected_unit.has_acted)
				{
					if (tile_index != -1)
					{
						if (FE_R_Main.was_char_pressed('j'))
						{
							if (surveying_unit == null || surveying_unit == selected_unit)
							{
								is_roaming = false;
								selected_unit.has_moved = true;
								selected_unit.movement_path = arrow_path;
								if (surveying_unit == selected_unit)
								{
									selected_unit.finished_moving = true;
									performimg_actions();
								}
								else selected_unit.movement_start_time = FE_R_Main.current_time;
								return;
							}
						}
					}
				}
				if (FE_R_Main.was_char_pressed('k'))
				{
					selected_unit.deselect();
					selected_unit = null;
					Map_ui.update_ui();
					arrow_img.clear();
				}
				if (selected_unit != surveying_unit)
				{
					if (FE_R_Main.was_char_pressed('j'))
					{
						if (selected_unit != surveying_unit)
						{
							selected_unit.deselect();
							selected_unit = null;
							Map_ui.update_ui();
							arrow_img.clear();
						}
					}
				}
			}
			else
			{
				if (surveying_unit != null)
				{
					if (FE_R_Main.was_char_pressed('j'))
					{
						selected_unit = surveying_unit;
						selected_unit.update_tiles();

						tile_index = selected_unit.find_movement_tile(x, y);
						//- Create Arrow
						if (tile_index != -1) create_arrow_image(tile_index);
						else arrow_img.clear();

						selected_unit.is_selected = true;
						if (selected_unit.affiliation == 0 && !selected_unit.has_acted) selected_unit.anmtn_num = 2;
					}
				}
			}

		}
		else
		{
			if (selected_unit == null)
			{
				is_roaming = true;
				return;
			}
			if (selected_unit.finished_moving && Map_ui.selecting_actions)
			{
				if (FE_R_Main.was_char_pressed('k'))
				{
					selected_unit.has_moved = false;
					selected_unit.finished_moving = false;
					selected_unit.anmtn_num = 2;
					is_roaming = true;

					cursor_has_moved();
					Map_ui.update_ui();
				}
			}
		}

	}

	public void cursor_has_moved()
	{
		is_roaming = true;

		//- Hovering over Units -
		if (surveying_unit != null)
		{
			surveying_unit.is_surveyed = false;
			if (surveying_unit.affiliation == 0 && selected_unit == null) surveying_unit.anmtn_num = surveying_unit.default_anmtn_num;
			surveying_unit = null;
		}

		surveying_unit = Map_mod.find_unit(x, y);

		if (surveying_unit != null)
		{
			surveying_unit.is_surveyed = true;
			if (surveying_unit.affiliation == 0 && selected_unit == null && !surveying_unit.has_acted) surveying_unit.anmtn_num = 1;
		}
		//- Map UI
		Map_ui.update_ui();

		if (selected_unit != null && selected_unit.affiliation == 0 && !selected_unit.has_acted)
		{
			tile_index = selected_unit.find_movement_tile(x, y);
			//- Create Arrow
			if (tile_index != -1) create_arrow_image(tile_index);
			else arrow_img.clear();
		}
	}

	//- This is the method that the selected unit calls when it has finished moving and the player will select an action
	//from the UI.
	public void performimg_actions()
	{
		surveying_unit = selected_unit;
		Map_ui.selecting_actions = true;
		Map_ui.update_ui();
	}

	public void create_arrow_image(int current_index)
	{
		arrow_img.clear();
		arrow_path = Map_mod.create_path(selected_unit.movement_tiles, current_index);
		if (arrow_path.isEmpty()) return;

		arrow_img.add(arrow_sprites[arrow_path.get(0)]);
		for (int i = 1; i < arrow_path.size(); i++)
		{
			int last_loc = arrow_path.get(i - 1), next_loc = arrow_path.get(i);
			if (last_loc == 0)
			{
				if (next_loc == 0) arrow_img.add(arrow_sprites[4]);
				else if (next_loc == 2) arrow_img.add(arrow_sprites[5]);
				else if (next_loc == 3) arrow_img.add(arrow_sprites[6]);
			}
			else if (last_loc == 1)
			{
				if (next_loc == 1) arrow_img.add(arrow_sprites[4]);
				else if (next_loc == 2) arrow_img.add(arrow_sprites[7]);
				else if (next_loc == 3) arrow_img.add(arrow_sprites[8]);
			}
			else if (last_loc == 2)
			{
				if (next_loc == 0) arrow_img.add(arrow_sprites[8]);
				else if (next_loc == 1) arrow_img.add(arrow_sprites[6]);
				else if (next_loc == 2) arrow_img.add(arrow_sprites[9]);
			}
			else
			{
				if (next_loc == 0) arrow_img.add(arrow_sprites[7]);
				else if (next_loc == 1) arrow_img.add(arrow_sprites[5]);
				else if (next_loc == 3) arrow_img.add(arrow_sprites[9]);
			}
		}
		arrow_img.add(arrow_sprites[10 + arrow_path.get(arrow_path.size() - 1)]);
	}

	public void draw()
	{
		long current_time = FE_R_Main.current_time;

		if (is_roaming == false) return;
		int draw_x, draw_y;

		int i, animation_frame = (int) (current_time % n_of_frames);
		for (i = 0; i < frames_p_img.length && animation_frame > frames_p_img[i]; i++)
		{
			animation_frame -= frames_p_img[i];
		}
		draw_x = x * FE_R_Main.p_per_grid - 2 - Map_mod.x_offset;
		draw_y = y * FE_R_Main.p_per_grid - 2 - Map_mod.x_offset;
		FE_R_Main.c.drawImage(cursor_img[i], draw_x, draw_y);
	}

	public void draw_arrow()
	{
		int tile_x = selected_unit.x, tile_y = selected_unit.y;
		for (int i = 0; i < arrow_img.size(); i++)
		{
			int draw_x = tile_x * FE_R_Main.p_per_grid - Map_mod.x_offset,
					draw_y = tile_y * FE_R_Main.p_per_grid - Map_mod.x_offset;
			FE_R_Main.c.drawImage(arrow_img.get(i), draw_x, draw_y);
			if (i + 1 != arrow_img.size())
			{
				int next_loc = arrow_path.get(i);
				if (next_loc == 0) tile_x++;
				if (next_loc == 1) tile_x--;
				if (next_loc == 2) tile_y++;
				if (next_loc == 3) tile_y--;
			}
		}

	}
}
