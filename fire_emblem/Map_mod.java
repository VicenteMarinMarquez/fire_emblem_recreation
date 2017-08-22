/* --- MAP MODERATOR ---
 * 
 *    Description: This is the Map_moderator class, used in Fire Emblem Recreation. The class keeps track of all
 * actions and objects in a map. The class keeps track of the array of units on the map. The class has two methods:
 * "check_inputs" and "draw". Both methods relay the method call to the individual classes.
 * 
 */

package fire_emblem;

import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;

public class Map_mod
{
    // *** *** Variables *** ***
    
    public static int map_num;
    public static String map_name = "";
    public static Image map_img;
    public static int x_dimension, y_dimension;
    public static int[][] terrain;
    public static final String[] terrain_names = { "- -", "Plain", "Wall", "Fort", "Forest", "Mntn", "Peak", "Cliff", "River" };
    public static final int[] terrain_mov_cost = { -1, 1, -1, 2, 2, 3, -1, -1, 3 };
    public static final int[] terrain_defence = { -1, 0, -1, 2, 1, 2, 3, -1, 0 };
    public static final int[] terrain_avoidance = { -1, 0, -1, 20, 20, 30, 30, -1, 0 };
    // The 2D ArrayList contains the ArrayLists for player, enemy, and allied
    // units, in order.
    public static ArrayList<ArrayList<Map_unit>> map_units = new ArrayList<ArrayList<Map_unit>>();
    
    public static Army_AI enemy_AI;
    
    public static Map_cursor map_cursor;
    public static final Image movement_tile = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\tile_movement.png");
    public static final Image attack_tile = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\tile_attack.png");
    public static final Image healing_tile = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + "Images\\tile_healing.png");
    // x and y offset are the number of pixles the camera is displayed by from the top left corner of the map.
    public static int x_offset, y_offset;
    
    public static boolean end_checks;
    
    public static boolean show = true;
    
    public static int phase; // 0 for the player phase, 1 for the enemy phase, 2 for the allied phase.
    public static final BufferedImage[] phase_image = new BufferedImage[4]; // The above phases and barlines at the end.
    public static Image phase_borders, phase_message;
    public static boolean switching_phases;
    //The time the phase started to play the animation for a phase switch and the time it ended to delay the start of the next phase.
    public static long phase_start_time;
    public static long phase_end_time;
    
    // *** *** Initialization Method *** ***
    
    public static void initialize() throws Exception
    {
        map_units.add(FE_R_Main.player_units);
        map_units.add(new ArrayList<Map_unit>());
        map_units.add(new ArrayList<Map_unit>());
        
        phase_image[0] = ImageIO.read(new File(FE_R_Main.resources_path + "Images\\Player_Phase_text.png"));
        phase_image[1] = ImageIO.read(new File(FE_R_Main.resources_path + "Images\\Enemy_Phase_text.png"));
        phase_image[2] = ImageIO.read(new File(FE_R_Main.resources_path + "Images\\Allied_Phase_text.png"));
        phase_image[3] = ImageIO.read(new File(FE_R_Main.resources_path + "Images\\Phase_Switch.png"));
        phase_borders = phase_image[0];
        phase_message = phase_image[3];
        
        phase = 0;
        switching_phases = false;
        phase_start_time = FE_R_Main.current_time;
        phase_end_time = -1;
        
        x_offset = 0;
        y_offset = 0;
    }
    
    public static void initialize(int given_map_num) throws Exception
    {
        map_units.add(FE_R_Main.player_units);
        map_units.add(new ArrayList<Map_unit>());
        map_units.add(new ArrayList<Map_unit>());
        
        phase_image[0] = ImageIO.read(new File(FE_R_Main.resources_path + "Images\\Player_Phase_text.png"));
        phase_image[1] = ImageIO.read(new File(FE_R_Main.resources_path + "Images\\Enemy_Phase_text.png"));
        phase_image[2] = ImageIO.read(new File(FE_R_Main.resources_path + "Images\\Allied_Phase_text.png"));
        phase_image[3] = ImageIO.read(new File(FE_R_Main.resources_path + "Images\\Phase_Switch.png"));
        phase_borders = phase_image[0];
        phase_message = phase_image[3];
        
        phase = 0;
        switching_phases = false;
        phase_start_time = FE_R_Main.current_time;
        phase_end_time = -1;
        
        x_offset = 0;
        y_offset = 0;
        map_num = given_map_num;
        
        get_map_info();
    }
    
    // *** *** Methods *** ***
    
    public static void start_map(int given_map_num)
    {
        x_offset = 0;
        y_offset = 0;
        map_num = given_map_num;
        
        map_units.set(1, new ArrayList<Map_unit>());
        map_units.set(2, new ArrayList<Map_unit>());
        
        phase = 0;
        switching_phases = false;
        phase_start_time = FE_R_Main.current_time;
        phase_end_time = -1;
        
        get_map_info();
        
        Map_ui.surveying_unit = null;
        Map_ui.update_ui();
    }
    
    public static void start_phase()
    {
        for (int i = 0; i < map_units.get(phase).size(); i++)
        {
            map_units.get(phase).get(i).start_turn();
        }
    }
    
    public static void get_map_info()
    {
        String line = "";
        Scanner line_scan;
        int n;
        try
        {
            ArrayList<ArrayList<Integer>> coords = new ArrayList<ArrayList<Integer>>();
            coords.add(new ArrayList<Integer>());
            coords.add(new ArrayList<Integer>());
            Scanner file_scan = new Scanner(FE_R_Main.file_map_data);
            /*file_read:*/ while (!line.equals("****FILE_END****"))
            {
                if (line.equals("***Map_Start"))
                {
                    line = file_scan.nextLine();
                    line_scan = new Scanner(line);
                    if (line_scan.nextInt() == map_num)
                    {
                        map_name = line_scan.nextLine().trim();
                        map_img = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + file_scan.nextLine());
                        line = file_scan.nextLine();
                        line_scan = new Scanner(line);
                        x_dimension = line_scan.nextInt();
                        y_dimension = line_scan.nextInt();
                        {
                            terrain = new int[x_dimension][y_dimension];
                            for (int i = 0; i < x_dimension; i++)
                            {
                                for (int j = 0; j < y_dimension; j++)
                                {
                                    terrain[i][j] = 1;
                                }
                            }
                        }
                        while (!line.equals("**Terrain_Start"))
                        {
                            line = file_scan.nextLine();
                        }
                        line = file_scan.nextLine();
                        while (!line.equals("**Terrain_End"))
                        {
                            line_scan = new Scanner(line);
                            int terrain_type = line_scan.nextInt();
                            /*
                             * while(line_scan.hasNextInt()) { int x, y; x =
                             * line_scan.nextInt(); y = line_scan.nextInt();
                             * terrain[x][y] = terrain_type; }
                             */
                            while (line_scan.hasNext())
                            {
                                String str_coords = line_scan.next(), int_substring;
                                if ((str_coords.indexOf('(') == -1) || (str_coords.indexOf(',') == -1) || (str_coords.indexOf(')') == -1))
                                {
                                    System.out.println("Error reading the terrain of the map " + map_name + " in the file " + FE_R_Main.file_map_data + " from Map_mod.");
                                    System.out.println("Improper terrain coordinate format. Last attempted coordinate: \"" + str_coords + "\"");
                                    System.out.println("\tfrom the line:\"" + line + "\"");
                                    break;
                                }
                                int x, y;
                                int_substring = str_coords.substring(str_coords.indexOf('(') + 1, str_coords.indexOf(',')).trim();
                                x = Integer.parseInt(int_substring);
                                int_substring = str_coords.substring(str_coords.indexOf(',') + 1, str_coords.indexOf(')')).trim();
                                y = Integer.parseInt(int_substring);
                                terrain[x][y] = terrain_type;
                            }
                            line = file_scan.nextLine();
                        }
                        while (!line.equals("**Units_Start"))
                        {
                            line = file_scan.nextLine();
                        }
                        line = file_scan.nextLine();
                        line_scan = new Scanner(line);
                        n = line_scan.nextInt();
                        for (int i = 0; i < n; i++)
                        {
                            coords.get(0).add(line_scan.nextInt());
                            coords.get(1).add(line_scan.nextInt());
                        }
                        while (!line.equals("*New_Player_Units"))
                        {
                            line = file_scan.nextLine();
                        }
                        line = file_scan.nextLine();
                        line_scan = new Scanner(line);
                        n = line_scan.nextInt();
                        for (int i = 0; i < n; i++)
                        {
                            int character_num = line_scan.nextInt(), k = line_scan.nextInt();
                            map_units.get(0).add(new Map_unit(character_num, 0, coords.get(0).get(k), coords.get(1).get(k)));
                        }
                        while (!line.equals("*Enemy_Units_Start"))
                        {
                            line = file_scan.nextLine();
                        }
                        line = file_scan.nextLine();
                        while (!line.equals("*Enemy_Units_End"))
                        {
                            line_scan = new Scanner(line);
                            
                            int unit_class = line_scan.nextInt();
                            int unit_x = line_scan.nextInt();
                            int unit_y = line_scan.nextInt();
                            
                            int[][] stats = new int[3][10];
                            int[] xp_stats = new int[2];
                            
                            {
                                int[] maxed = { 99, 99, 99, 99, 99, 99, 99, 99, 20, 10 };
                                stats[2] = maxed;
                            }
                            xp_stats[0] = line_scan.nextInt();
                            xp_stats[1] = line_scan.nextInt();
                            for (int i = 0; i < 10; i++)
                            {
                                stats[0][i] = line_scan.nextInt();
                            }
                            
                            line = file_scan.nextLine();
                            line_scan = new Scanner(line);
                            int num_of_items = line_scan.nextInt();
                            ArrayList<Item> inventory = new ArrayList<Item>();
                            for (int i = 0; i < num_of_items; i++)
                            {
                                inventory.add(new Item(line_scan.nextInt()));
                            }
                            
                            {
                                Map_unit unit = new Map_unit(0, 1, unit_x, unit_y);
                                map_units.get(1).add(unit);
                                unit.get_generic_unit_stats(unit_class, stats, xp_stats, inventory);
                                unit.get_generic_unit_info();
                            }
                            line = file_scan.nextLine();
                        }
                    }
                }
                line = file_scan.nextLine();
            }
            file_scan.close();
            System.out.println("Starting the map: " + map_name + ".\n");
        }
        catch (Exception ex)
        {
            System.out.println("Error reading the file " + FE_R_Main.file_map_data + " from Map_mod.");
            System.out.println("Last read line: " + line);
        }
        
        enemy_AI = new Army_AI(map_units, 1);
        
        map_cursor = new Map_cursor();
        
    }
    
    public static void check_inputs()
    {
        end_checks = false;
        
        /*
         * if(key_code == Console.VK_DOWN && last_key_code != Console.VK_DOWN)
         * y_offset += p_per_grid; if(key_code == Console.VK_UP && last_key_code
         * != Console.VK_UP) y_offset -= p_per_grid; if(key_code ==
         * Console.VK_RIGHT && last_key_code != Console.VK_RIGHT) x_offset +=
         * p_per_grid; if(key_code == Console.VK_LEFT && last_key_code !=
         * Console.VK_LEFT) x_offset -= p_per_grid;
         * 
         * if(key_char == 'q' && last_key_char != 'q') show = !show;
         * 
         * if(key_char == 'p' && last_key_char != 'p') { phase_start_time =
         * Fire_Emblem_Recreation.current_time; switching_phases = true; }
         */
        
        if (check_map_conditions()) return;
        
        if (phase_end_time != -1) check_phase();
        else if (!switching_phases)
        {
            if (!Combat_mod.combat_in_progress)
            {
                if (phase == 0) // Player Phase
                {
                    Map_ui.check_inputs();
                    if (end_checks) return;
                    map_cursor.check_inputs();
                    if (end_checks) return;
                }
                else if (phase == 1) // Enemy Phase
                {
                    enemy_AI.called_method();
                }
                else // Ally Phase
                {
                    //
                }
            }
            else
            {
                Combat_mod.called_method();
            }
        }
    }
    
    public static boolean check_map_conditions()
    {
        boolean conditions_met = false;
        
        // Victory Condition
        if (map_units.get(1).isEmpty())
        {
            FE_R_Main.game_ending = true;
            conditions_met = true;
        }
        
        // Defeat Condition
        if (map_units.get(0).isEmpty())
        {
            FE_R_Main.game_ending = true;
            conditions_met = true;
            
            FE_R_Main.game_over = true;
        }
        
        return conditions_met;
    }
    
    public static void check_phase()
    {
        boolean still_acting = false;
        if (phase_end_time == -1)
        {
            for (int i = 0; i < map_units.get(phase).size(); i++)
            {
                if (!map_units.get(phase).get(i).has_acted)
                {
                    still_acting = true;
                    break;
                }
            }
        }
        
        if (!still_acting && !Combat_mod.combat_in_progress)
        {
            if (phase_end_time == -1) phase_end_time = FE_R_Main.current_time;
            else if (FE_R_Main.current_time >= phase_end_time + 30)
            {
                phase_end_time = -1;
                for (int i = 0; i < map_units.get(phase).size(); i++)
                {
                    map_units.get(phase).get(i).default_anmtn_num = 0;
                    map_units.get(phase).get(i).anmtn_num = 0;
                }
                phase = (phase + 1) % 3;
                
                phase_start_time = FE_R_Main.current_time;
                switching_phases = true;
            }
        }
        
        if (phase == 2)
        {
            phase = 0;
        }
        
    }
    
    public static Map_unit find_unit(int x_val, int y_val)
    {
        Map_unit unit;
        for (int i = 0; i < map_units.size(); i++)
        {
            unit = find_unit(i, x_val, y_val);
            if (unit != null) return unit;
        }
        return null;
    }
    
    public static Map_unit find_unit(int n, int x_val, int y_val)
    {
        Map_unit unit;
        for (int i = 0; i < map_units.get(n).size(); i++)
        {
            unit = map_units.get(n).get(i);
            if (unit.x == x_val && unit.y == y_val) return unit;
        }
        return null;
    }
    
    public static void draw_map()
    {
        
        if (show)
        {
            FE_R_Main.c.drawImage(map_img, -x_offset, -y_offset);
            
            if (phase == 0)
            {
                if (map_cursor.selected_unit != null)
                {
                    if (!map_cursor.selected_unit.has_moved)
                    {
                        map_cursor.selected_unit.draw_tiles();
                        if (map_cursor.selected_unit.affiliation == 0) map_cursor.draw_arrow();
                    }
                }
                Map_ui.draw_attack_tiles();
            }
            
            for (int i = 0; i < map_units.size(); i++)
            {
                for (int j = 0; j < map_units.get(i).size(); j++)
                {
                    map_units.get(i).get(j).draw();
                }
            }
            if (phase == 0 && !Combat_mod.combat_in_progress) map_cursor.draw();
            
            switch_phase_animation: if (switching_phases)
            {
                int elapsed_time = (int) (FE_R_Main.current_time - phase_start_time);
                if (elapsed_time > 120)
                {
                    switching_phases = false;
                    start_phase();
                    break switch_phase_animation;
                }
                
                int[] alpha_value = new int[2];
                int x_val;
                if (elapsed_time < 40)
                {
                    alpha_value[0] = (int) ((191.0 * elapsed_time) / 40);
                    alpha_value[1] = (int) ((255.0 * elapsed_time) / 40);
                    x_val = (int) (3 * (Math.pow(elapsed_time - 40, 2) / 20.0));
                }
                else if (elapsed_time < 80)
                {
                    alpha_value[0] = 191;
                    alpha_value[1] = 255;
                    x_val = 0;
                }
                else
                {
                    alpha_value[0] = (int) ((191.0 * (120 - elapsed_time)) / 40);
                    alpha_value[1] = (int) ((255.0 * (120 - elapsed_time)) / 40);
                    x_val = (int) ((-3) * (Math.pow(elapsed_time - 80, 2) / 20.0));
                }
                
                if (elapsed_time % 5 == 0)
                {
                    phase_borders = new_Alpha((byte) (alpha_value[0]), phase_image[3]);
                    phase_message = new_Alpha((byte) (alpha_value[1]), phase_image[phase]);
                }
                FE_R_Main.c.drawImage(phase_borders, 0, 0);
                FE_R_Main.c.drawImage(phase_message, x_val, 0);
                
            }
            
            Map_ui.draw_ui();
            
        }
    }
    
    // - Utility Methods
    
    public static int get_relative_location(int x1, int y1, int x2, int y2)
    {
        if (x1 == x2 - 1) return 0;
        if (x1 == x2 + 1) return 1;
        if (y1 == y2 - 1) return 2;
        if (y1 == y2 + 1) return 3;
        return -1;
    }
    
    // - This method creates a path for a given set of tiles, where the path
    // goes from the center of the tiles to the tile
    // with the specified "current_index". The each tile in the array of tiles
    // has its x coordinate in index 0 of the 2D
    // ArrayList, its y coordinate in index 1, and the index of the tile before
    // it in index 2.
    public static ArrayList<Integer> create_path(ArrayList<ArrayList<Integer>> tiles, int current_index)
    {
        ArrayList<Integer> path = new ArrayList<Integer>();
        
        if (tiles.get(2).get(current_index) == -1) return path;
        
        int next_index = tiles.get(2).get(current_index), last_loc, next_loc;
        int[][] coord = new int[2][2];
        coord[0][0] = tiles.get(0).get(current_index);
        coord[0][1] = tiles.get(1).get(current_index);
        coord[1][0] = tiles.get(0).get(next_index);
        coord[1][1] = tiles.get(1).get(next_index);
        next_loc = Map_mod.get_relative_location(coord[0][0], coord[0][1], coord[1][0], coord[1][1]);
        
        last_loc = (2 * (1 + next_loc / 2)) - ((next_loc) % 2) - 1;
        path.add(last_loc);
        current_index = next_index;
        next_index = tiles.get(2).get(current_index);
        while (next_index != -1)
        {
            coord[0][0] = coord[1][0];
            coord[0][1] = coord[1][1];
            coord[1][0] = tiles.get(0).get(next_index);
            coord[1][1] = tiles.get(1).get(next_index);
            next_loc = Map_mod.get_relative_location(coord[0][0], coord[0][1], coord[1][0], coord[1][1]);
            
            {
                // int low_loc = Math.min(last_loc, next_loc), high_loc =
                // Math.max(last_loc, next_loc);
                // - The relative locations of the tiles before and after the
                // current one, stored as one with a lower
                // "location value" and one with a higher "location value".
                
            }
            
            last_loc = (2 * (1 + next_loc / 2)) - ((next_loc) % 2) - 1;
            path.add(0, last_loc);
            current_index = next_index;
            next_index = tiles.get(2).get(current_index);
        }
        return path;
    }
    
    // - The following method is not my own (although I did modify it), and so I
    // do not claim any ownership or credit for
    // it. The code can be found in this link
    // http://stackoverflow.com/questions/660580/change-the-alpha-value-of-a-bufferedimage
    public static BufferedImage new_Alpha(byte alpha, BufferedImage image)
    {
        alpha %= 0xff;
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage new_image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int cx = 0; cx < w; cx++)
        {
            for (int cy = 0; cy < h; cy++)
            {
                int color = image.getRGB(cx, cy);
                
                int mc = (alpha << 24) | 0x00ffffff;
                int newcolor = color & mc;
                new_image.setRGB(cx, cy, newcolor);
                
            }
            
        }
        return new_image;
    }
    
}
