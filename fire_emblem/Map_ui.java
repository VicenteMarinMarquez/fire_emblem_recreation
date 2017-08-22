/* --- MAP UI ---
 * 
 *    Description: This is the Map_ui class, used in Fire Emblem Recreation. The class keeps track of and displays all
 * of the UI elements while on a map.
 * 
 */

package fire_emblem;

import java.awt.*;
import java.util.ArrayList;

public class Map_ui
{
    // *** *** Variables *** ***
    
    public static final Image ui_image = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\map_ui.png");
    
    public static Map_unit surveying_unit;
    
    public static final Image highlight_actions = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_actions_highlight.png"),
        highlight_items = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_items_highlight.png"),
        right_pointer = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_pointer_right.png"),
        left_pointer = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_pointer_left.png"),
        simple_stats_ui = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_simple_stats.png");
    
    public static Image[] simple_stats_text = new Image[5]; // The unit's name, their level, xp, hp, and max_hp
    
    public static ArrayList<Map_unit> enemies_in_range = new ArrayList<Map_unit>();
    public static int enemy_num = 0, last_enemy_num = 0;
    public static final Image forecast_ui = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_forecast.png");
    public static int[] forecast_stats = new int[9]; // Player's might, hit, and crit; Enemy's might, hit, crit, level, hp, and max_hp.
    public static Image[] forecast_text = new Image[10]; // The above with the enemy's name at the end.
    public static Image[] multiple_attacks_text = new Image[2]; // The image showing the number of times the player and enemy attack.
    
    public static final Image double_attack = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_double_strike.png");
    
    public static boolean are_actions_present = false;
    
    public static boolean selecting_actions = false;
    
    public static boolean items_present = false;
    
    public static boolean forecast_present = false;
    public static boolean[] available_actions;
    public static int action_num = 0; // "action_num" the selected action, "item_num" the selected item, "num_of_items" the number of items in the inventory.
    
    public static int item_num = 0;
    
    public static int num_of_items;
    
    public static final Image[] items_menu = new Image[3];
    public static ArrayList<Image> items_image = new ArrayList<Image>();
    public static ArrayList<Image> item_names_text = new ArrayList<Image>();
    public static ArrayList<Image> item_uses_text = new ArrayList<Image>();
    
    public static final Image[] actions_menu = new Image[3];
    public static final String[] actions_names = { "Attack", "Item", "Wait" };
    public static final Image[] actions_text = new Image[actions_names.length];
    public static ArrayList<Integer> action_codes = new ArrayList<Integer>();
    
    public static final Image[] terrain_text = new Image[Map_mod.terrain_names.length];
    public static final Integer[] terrain_text_length = new Integer[Map_mod.terrain_names.length];
    public static int current_terrain = 0;
    public static Image terrain_def, terrain_avo;
    
    // *** *** Initialization Method *** ***
    public static void initialize()
    {
        
        for (int i = 0; i < actions_names.length; i++)
        {
            actions_text[i] = FE_R_Main.text_struct.create_text_img(actions_names[i], 0);
        }
        
        for (int i = 0; i < Map_mod.terrain_names.length; i++)
        {
            int[] temp_array = new int[Map_mod.terrain_names[i].length()];
            terrain_text[i] = FE_R_Main.text_struct.create_text_img(Map_mod.terrain_names[i], 0, temp_array);
            terrain_text_length[i] = temp_array[temp_array.length - 1];
        }
        
        terrain_def = FE_R_Main.text_struct.create_text_img("- -", 1);
        terrain_avo = FE_R_Main.text_struct.create_text_img("- -", 1);
        
        actions_menu[0] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_actions_top.png");
        actions_menu[1] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_actions_connecting.png");
        actions_menu[2] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_actions_bottom.png");
        items_menu[0] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_items_top.png");
        items_menu[1] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_items_connecting.png");
        items_menu[2] = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\ui_items_bottom.png");
    }
    
    // *** *** Methods *** ***
    
    public static void check_inputs()
    {
        if (are_actions_present)
        {
            Map_unit selected_unit = Map_mod.map_cursor.selected_unit;
            if (selecting_actions)
            {
                if (FE_R_Main.was_char_pressed('s')) action_num++;
                if (FE_R_Main.was_char_pressed('w')) action_num--;
                
                //If the action number is negative (the player is selecting something above the top action), cycle back to the bottom.
                while (action_num < 0)
                    action_num += action_codes.size();
                //If the action number is more than the number of actions (the player is selecting something below the bottom action), cycle back to the top.
                action_num = action_num % action_codes.size();
                
                if (FE_R_Main.was_char_pressed('j'))
                {
                    selecting_actions = false;
                    
                    if (action_codes.get(action_num) == 0) items_present = true;
                    if (action_codes.get(action_num) == 1) items_present = true;
                    
                    if (action_codes.get(action_num) == 2)
                    {
                        selected_unit.finish_turn();
                        Map_mod.end_checks = true;
                    }
                    update_ui();
                }
                
            }
            else
            {
                if (items_present)
                {
                    if (action_codes.get(action_num) == 0)
                    {
                        if (forecast_present)
                        {
                            if (FE_R_Main.was_char_pressed('w')) enemy_num++;
                            if (FE_R_Main.was_char_pressed('a')) enemy_num++;
                            if (FE_R_Main.was_char_pressed('s')) enemy_num--;
                            if (FE_R_Main.was_char_pressed('d')) enemy_num--;
                            
                            //If the enemy number is negative (the player has cycled through all the enemies going backwards), cycle back to the last enemy.
                            while (enemy_num < 0)
                                enemy_num += enemies_in_range.size();
                            //If the enemy number is more than the number of enemies (the player has cycled through all the enemies going forwards), cycle back to the first enemy.
                            enemy_num = enemy_num % enemies_in_range.size();
                            
                            if (enemy_num != last_enemy_num) update_ui();
                            last_enemy_num = enemy_num;
                            
                            if (FE_R_Main.was_char_pressed('j'))
                            {
                                are_actions_present = false;
                                selecting_actions = false;
                                items_present = false;
                                forecast_present = false;
                                
                                Map_unit enemy = enemies_in_range.get(enemy_num);
                                Combat_mod.intiate_combat(selected_unit, enemy);
                                
                                Map_mod.end_checks = true;
                            }
                            
                            if (FE_R_Main.was_char_pressed('k'))
                            {
                                forecast_present = false;
                                Map_mod.end_checks = true;
                                update_ui();
                            }
                            
                        }
                        else
                        {
                            if (FE_R_Main.was_char_pressed('s')) item_num++;
                            if (FE_R_Main.was_char_pressed('w')) item_num--;
                            
                            while (item_num < 0) item_num += num_of_items;
                            item_num = item_num % num_of_items;
                            
                            if (FE_R_Main.was_char_pressed('j'))
                            {
                                if (selected_unit.inventory.get(item_num).item_type == 0)
                                {
                                    Item equiping_weapon = selected_unit.inventory.remove(item_num);
                                    selected_unit.equiped_weapon = equiping_weapon;
                                    item_num = 0;
                                    selected_unit.inventory.add(0, equiping_weapon);
                                    
                                    forecast_present = true;
                                    enemy_num = 0;
                                    last_enemy_num = 0;
                                    update_ui();
                                }
                                
                            }
                            
                            if (FE_R_Main.was_char_pressed('k'))
                            {
                                selecting_actions = true;
                                items_present = false;
                                Map_mod.end_checks = true;
                            }
                        }
                    }
                    if (action_codes.get(action_num) == 1)
                    {
                        if (FE_R_Main.was_char_pressed('s')) item_num++;
                        if (FE_R_Main.was_char_pressed('w')) item_num--;
                        
                        while (item_num < 0)
                            item_num += num_of_items;
                        item_num = item_num % num_of_items;
                        
                        Item selected_item = selected_unit.inventory.get(item_num);
                        
                        if (FE_R_Main.was_char_pressed('j'))
                        {
                            if (selected_item.item_type == 0)
                            {
                                selected_unit.inventory.remove(item_num);
                                selected_unit.equiped_weapon = selected_item;
                                item_num = 0;
                                selected_unit.inventory.add(0, selected_item);
                            }
                            else if(selected_unit.inventory.get(item_num).item_type == 1)
                            {
                                //
                            }
                            else if(selected_unit.inventory.get(item_num).item_type == 2)
                            {
                                boolean[] item_usability = selected_item.try_use_item(selected_unit, item_num);
                                if(item_usability[1] == true)
                                {
                                    selected_item.use_item(selected_unit, item_num);
                                    
                                    are_actions_present = false;
                                    selecting_actions = false;
                                    items_present = false;
                                    forecast_present = false;
                                    
                                    selected_unit.finish_turn();
                                    Map_mod.end_checks = true;
                                }
                                
                            }
                            update_ui();
                        }
                        
                        if (FE_R_Main.was_char_pressed('k'))
                        {
                            selecting_actions = true;
                            items_present = false;
                            Map_mod.end_checks = true;
                        }
                    }
                    
                }
            }
        }
        else
        {
            enemies_in_range.clear();
        }
    }
    
    public static void update_ui()
    {
        Map_cursor map_cursor = Map_mod.map_cursor;
        
        if (!forecast_present)
        {
            if (surveying_unit == null) surveying_unit = map_cursor.surveying_unit;
            else
            {
                if (surveying_unit != map_cursor.surveying_unit && map_cursor.surveying_unit != null) surveying_unit = map_cursor.surveying_unit;
                else if (surveying_unit.affiliation == -1) surveying_unit = map_cursor.surveying_unit;
            }
        }
        
        if (surveying_unit != null)
        {
            simple_stats_text[0] = FE_R_Main.text_struct.create_text_img(surveying_unit.unit_name, 0);
            String level = String.valueOf(surveying_unit.xp_stats[0]), xp = String.valueOf(surveying_unit.xp_stats[1]);
            String hp = String.valueOf(surveying_unit.combat_stats[0]),
                max_hp = String.valueOf(surveying_unit.stats[0][0]);
            simple_stats_text[1] = FE_R_Main.text_struct.create_text_img(level, 1);
            simple_stats_text[2] = FE_R_Main.text_struct.create_text_img(xp, 1);
            simple_stats_text[3] = FE_R_Main.text_struct.create_text_img(hp, 1);
            simple_stats_text[4] = FE_R_Main.text_struct.create_text_img(max_hp, 1);
        }
        else
        {
            Image font_0_null = FE_R_Main.text_struct.create_text_img("- -", 0),
                font_1_null = FE_R_Main.text_struct.create_text_img("- -", 1);
            simple_stats_text[0] = font_0_null;
            simple_stats_text[1] = font_1_null;
            simple_stats_text[2] = font_1_null;
            simple_stats_text[3] = font_1_null;
            simple_stats_text[4] = font_1_null;
        }
        
        {
            int x_val = map_cursor.x, y_val = map_cursor.y;
            current_terrain = Map_mod.terrain[x_val][y_val];
        }
        
        String def = String.valueOf(Map_mod.terrain_defence[current_terrain]);
        String avo = String.valueOf(Map_mod.terrain_avoidance[current_terrain]);
        if (def.equals("-1")) def = "- -";
        if (avo.equals("-1")) avo = "- -";
        terrain_def = FE_R_Main.text_struct.create_text_img(def, 1);
        terrain_avo = FE_R_Main.text_struct.create_text_img(avo, 1);
        
        // Update variables
        Map_unit selected_unit = map_cursor.selected_unit;
        if (selected_unit != null)
        {
            num_of_items = selected_unit.inventory.size();
            if (selected_unit.finished_moving)
            {
                if (selecting_actions)
                {
                    are_actions_present = true;
                    available_actions = new boolean[actions_names.length];
                    action_codes.clear();
                    
                    //num_of_items = selected_unit.inventory.size();
                    available_actions[1] = (num_of_items != 0);
                    available_actions[2] = true;
                    
                    enemies_in_range = selected_unit.find_units_in_range(1);
                    if (!enemies_in_range.isEmpty()) available_actions[0] = true;
                    
                    for (int i = 0; i < available_actions.length; i++)
                    {
                        if (available_actions[i])
                        {
                            action_codes.add(i);
                        }
                    }
                }
            }
            else
            {
                // Reset some variables
                are_actions_present = false;
                action_num = 0;
                item_num = 0;
            }
            
            if (items_present)
            {
                items_image.clear();
                item_names_text.clear();
                item_uses_text.clear();
                
                for (int i = 0; i < num_of_items; i++)
                {
                    Item current_item = selected_unit.inventory.get(i);
                    items_image.add(current_item.item_image);
                    Image name_text = FE_R_Main.text_struct.create_text_img(current_item.item_name, 0);
                    item_names_text.add(name_text);
                    Image uses_text = FE_R_Main.text_struct.create_text_img(String.valueOf(current_item.durability), 1);
                    item_uses_text.add(uses_text);
                }
            }
            
            if (forecast_present)
            {
                Map_unit enemy = enemies_in_range.get(enemy_num);
                int facing_location = Map_mod.get_relative_location(selected_unit.temporary_x, selected_unit.temporary_y, enemy.x, enemy.y);
                selected_unit.anmtn_num = Map_unit.find_facing_animation(facing_location);
                
                forecast_stats = new int[9];
                forecast_text = new Image[10];
                multiple_attacks_text = new Image[2];
                
                forecast_stats[6] = enemy.xp_stats[0];
                forecast_stats[7] = enemy.combat_stats[0];
                forecast_stats[8] = enemy.stats[0][0];
                
                {
                    int[] battle_stats = Combat_mod.get_battle_stats(selected_unit, enemy);
                    forecast_stats[0] = battle_stats[0];
                    forecast_stats[1] = battle_stats[1];
                    forecast_stats[2] = battle_stats[2];
                    forecast_stats[3] = battle_stats[4];
                    forecast_stats[4] = battle_stats[5];
                    forecast_stats[5] = battle_stats[6];
                    
                    if (battle_stats[3] == 2) multiple_attacks_text[0] = double_attack;
                    if (battle_stats[7] == 2) multiple_attacks_text[1] = double_attack;
                }
                for (int i = 0; i < forecast_stats.length; i++)
                {
                    String str = String.valueOf(forecast_stats[i]);
                    forecast_text[i] = FE_R_Main.text_struct.create_text_img(str, 1);
                }
                forecast_text[9] = FE_R_Main.text_struct.create_text_img(enemy.unit_name, 0);
            }
        }
        else
        {
            // Reset some variables
            are_actions_present = false;
            action_num = 0;
            item_num = 0;
        }
        
    }
    
    public static void create_battle_forecast(Map_unit unit_1, Map_unit unit_2)
    {
    }
    
    public static void draw_ui()
    {
        long current_time = FE_R_Main.current_time;
        
        FE_R_Main.c.drawImage(ui_image, 0, 0); // The UI displayed on the map
        
        FE_R_Main.c.drawImage(simple_stats_ui, 0, 320);
        FE_R_Main.c.drawImage(simple_stats_text[0], 0 + 24, 320 + 14);
        FE_R_Main.c.drawImage(simple_stats_text[1], 0 + 88 - simple_stats_text[1].getWidth(null), 320 + 42);
        FE_R_Main.c.drawImage(simple_stats_text[2], 0 + 136 - simple_stats_text[2].getWidth(null), 320 + 42);
        FE_R_Main.c.drawImage(simple_stats_text[3], 0 + 88 - simple_stats_text[3].getWidth(null), 320 + 70);
        FE_R_Main.c.drawImage(simple_stats_text[4], 0 + 136 - simple_stats_text[4].getWidth(null), 320 + 70);
        
        FE_R_Main.c.drawImage(terrain_text[current_terrain], 492 + 36 - (terrain_text_length[current_terrain] / 2), 332);
        FE_R_Main.c.drawImage(terrain_def, 492 + 70 - terrain_def.getWidth(null), 358);
        FE_R_Main.c.drawImage(terrain_avo, 492 + 70 - terrain_avo.getWidth(null), 380);
        
        if (are_actions_present)
        {
            FE_R_Main.c.drawImage(actions_menu[0], 480, 0);
            for (int i = 0; i < action_codes.size(); i++)
            {
                if (i != action_codes.size() - 1) FE_R_Main.c.drawImage(actions_menu[1], 480, 14 + i * 32);
                else FE_R_Main.c.drawImage(actions_menu[2], 480, 14 + i * 32);
                
                if (i == action_num)
                {
                    int pointer_offset = (int) (4 * ((current_time % 30) / 5));
                    if (pointer_offset >= 12) pointer_offset = 24 - pointer_offset;
                    FE_R_Main.c.drawImage(highlight_actions, 480, 24 + i * 32);
                    if (selecting_actions) FE_R_Main.c.drawImage(right_pointer, 480 - 20 - pointer_offset, 16 + i * 32);
                }
                FE_R_Main.c.drawImage(actions_text[action_codes.get(i)], 480 + 8, 14 + i * 32);
            }
            
        }
        if (forecast_present)
        {
            FE_R_Main.c.drawImage(forecast_ui, 160, 320);
            FE_R_Main.c.drawImage(forecast_text[0], 196 - forecast_text[0].getWidth(null), 320 + 12);
            FE_R_Main.c.drawImage(forecast_text[1], 196 - forecast_text[1].getWidth(null), 320 + 36);
            FE_R_Main.c.drawImage(forecast_text[2], 196 - forecast_text[2].getWidth(null), 320 + 60);
            FE_R_Main.c.drawImage(forecast_text[3], 328 - forecast_text[3].getWidth(null), 320 + 12);
            FE_R_Main.c.drawImage(forecast_text[4], 328 - forecast_text[4].getWidth(null), 320 + 36);
            FE_R_Main.c.drawImage(forecast_text[5], 328 - forecast_text[5].getWidth(null), 320 + 60);
            
            if (multiple_attacks_text[0] != null) FE_R_Main.c.drawImage(multiple_attacks_text[0], 224 - multiple_attacks_text[0].getWidth(null), 320 + 12);
            if (multiple_attacks_text[1] != null) FE_R_Main.c.drawImage(multiple_attacks_text[1], 356 - multiple_attacks_text[1].getWidth(null), 320 + 12);
            
            FE_R_Main.c.drawImage(forecast_text[6], 424 - forecast_text[6].getWidth(null), 320 + 42);
            FE_R_Main.c.drawImage(forecast_text[7], 424 - forecast_text[7].getWidth(null), 320 + 70);
            FE_R_Main.c.drawImage(forecast_text[8], 472 - forecast_text[8].getWidth(null), 320 + 70);
            FE_R_Main.c.drawImage(forecast_text[9], 358, 320 + 14);
            
            {
                Map_unit enemy = enemies_in_range.get(enemy_num);
                int x_value = enemy.x * FE_R_Main.p_per_grid - Map_mod.x_offset;
                int y_value = enemy.y * FE_R_Main.p_per_grid - Map_mod.y_offset;
                FE_R_Main.c.drawImage(Map_cursor.cursor_img[0], x_value, y_value);
            }
        }
        else
        {
            if (items_present)
            {
                int y_start = 416 - (8 + (32 * num_of_items));
                FE_R_Main.c.drawImage(items_menu[0], 178, y_start);
                for (int i = 0; i < num_of_items; i++)
                {
                    FE_R_Main.c.drawImage(items_menu[1], 178, y_start + 8 + (32 * i));
                    
                    if (i == item_num)
                    {
                        int pointer_offset = (int) (4 * ((current_time % 30) / 5));
                        if (pointer_offset >= 12) pointer_offset = 24 - pointer_offset;
                        FE_R_Main.c.drawImage(highlight_items, 178, y_start + 8 + (32 * i));
                        FE_R_Main.c.drawImage(left_pointer, 388 - 8 + pointer_offset, y_start + 8 + (32 * i) + 6);
                    }
                    
                    FE_R_Main.c.drawImage(items_image.get(i), 178 + 8, y_start + 8 + (32 * i));
                    FE_R_Main.c.drawImage(item_names_text.get(i), 178 + 40, y_start + 8 + (32 * i) + 6);
                    FE_R_Main.c.drawImage(item_uses_text.get(i), 178 + 200 - item_uses_text.get(i).getWidth(null), y_start + 8 + (32 * i) + 6);
                }
            }
        }
        
    }
    
    public static void draw_attack_tiles()
    {
        if (forecast_present)
        {
            Image attack_tile_img = Map_mod.attack_tile;
            for (int i = 0; i < enemies_in_range.size(); i++)
            {
                Map_unit unit = enemies_in_range.get(i);
                int tile_x = unit.x * FE_R_Main.p_per_grid - Map_mod.x_offset;
                int tile_y = unit.y * FE_R_Main.p_per_grid - Map_mod.y_offset;
                FE_R_Main.c.drawImage(attack_tile_img, tile_x, tile_y);
            }
        }
    }
}
