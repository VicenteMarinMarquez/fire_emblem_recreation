/* --- --- FIRE EMBLEM RECREATION --- ---
 * 
 *    Description: This program is a recreation of the gameboy style Fire Emblem, originally made by Intelligent
 * Systems, Nintendo.
 * 
 * *** Version 1.05 *** Pre-Alpha (October 10th to , 2016)
 *    Version Description:
 * 
 *    Version Changes:
 * - Changed Combat_mod so that its methods now use static values, since only one combat takes place at a time.
 * This also allows it to keep track of the progress of combat over multiple frames.
 * - Combat now takes place in rounds at different frames. Print statements have been added to display the events of each round.
 * 
 * - Added forts and passive healing due to them.
 *    Bugs:
 * - 
 */

package fire_emblem;

import java.util.ArrayList;
import java.io.File;
import java.awt.*;
import hsa_ufa.*;

public class FE_R_Main
{
 public static ArrayList<Map_unit> player_units = new ArrayList<Map_unit>();
 public static boolean running = true, game_over = false, game_ending = false;
 public static int game_state = 0; // 0 is title screen, 1 is a map, 2 is a game
        // ending.
 public static int fps = 60;
 public static final int nano_p_sec = 1000000000;

 public static long start_time, clock_time, current_time;
 public static int p_per_grid = 32; //The number of pixels per each square grid on the map.

 // -- Resources --
 
 public final static String resources_path = "Resources\\";

 public final static Image start_screen = Toolkit.getDefaultToolkit().getImage(resources_path + "Images\\start_screen.png");
 public final static Image game_over_screen = Toolkit.getDefaultToolkit().getImage(resources_path + "Images\\game_over_screen.png");
 public final static Image continue_screen = Toolkit.getDefaultToolkit().getImage(resources_path + "Images\\continue_screen.png");

 public final static File file_unit_data = new File(resources_path + "Data Files\\unit_data.txt");
 public final static File file_map_data = new File(resources_path + "Data Files\\map_data.txt");
 public final static File file_font_data = new File(resources_path + "Data Files\\font_data.txt");
 public final static File file_item_data = new File(resources_path + "Data Files\\item_data.txt");

 public static int key_code, last_key_code, time_code = 0, time_char = 0;
 public static char key_char, last_key_char;
 
 public static Console c;
 public static Text_constructor text_struct = new Text_constructor();
 
 public static void main(String[] args) throws Exception
 {

  c = new Console(576, 416, "Fire Emblem");

  start_time = (System.nanoTime() * fps) / nano_p_sec;
  clock_time = start_time;
  current_time = clock_time - start_time;

  key_code = c.getKeyCode();
  key_char = c.getKeyChar();
  last_key_code = key_code;
  last_key_char = key_char;

  System.out.println("\nRunning at " + fps + " fps.\n");

  // The appropriate (temporary) offset for the start, continue, and game over screens.
  int screen_x_offset = (c.getDrawWidth() - 480) / 2, screen_y_offset = (c.getDrawHeight() - 360) / 2;
  
  //--- Initialize all static classes ---
  Map_mod.initialize();
  Map_ui.initialize();

  while (running)
  {
   // --- Time Based ---
   if (clock_time != (System.nanoTime() * fps) / nano_p_sec)
   {
    // --- Inputs ---
    key_code = c.getKeyCode();
    key_char = c.getKeyChar();

    if (key_code == last_key_code) time_code++;
    else time_code = 0;
    if (key_char == last_key_char) time_char++;
    else time_char = 0;

    // - Termination -
    if (key_code == Console.VK_ESCAPE) break;

    clock_time = (System.nanoTime() * fps) / nano_p_sec;
    current_time = clock_time - start_time;

    if (game_ending)
    {
     game_state = 2;
     if (game_over)
     {
     }
     else
     {
     }
     game_ending = false;
    }

    // --- Title Screen Actions ---
    if (game_state == 0)
    {
     // - Check to start a map -
     if (key_code == Console.VK_ENTER)
     {
      game_state = 1;
      // Temporarily delete all units
      player_units.clear();
      Map_mod.start_map(2);
     }
    }

    // --- Map Actions ---
    if (game_state == 1)
    {
     Map_mod.check_inputs();
    }

    // --- Endings ---
    if (game_state == 2)
    {
     if (key_code == Console.VK_ENTER)
     {
      game_state = 1;
      game_over = false;
      // Temporarily delete all units
      player_units.clear();
      Map_mod.start_map(2);
     }
    }

    // Update the previous inputs.
    last_key_code = key_code;
    last_key_char = key_char;

    // - Display -
    synchronized (c)
    {
     // - Clear Screen -
     c.clear();

     if (game_state == 0)
     {
      c.drawImage(start_screen, screen_x_offset, screen_y_offset);
     }
     if (game_state == 1)
     {
      // - Draw everything on the map -
      Map_mod.draw_map();
     }
     if (game_state == 2)
     {
      if (game_over)
      {
       c.drawImage(game_over_screen, screen_x_offset, screen_y_offset);
      }
      else
      {
       c.drawImage(continue_screen, screen_x_offset, screen_y_offset);
      }
     }
    }

   }

  }
  c.close();

 }

 public static boolean was_char_pressed(char c)
 {
  return (key_char == c && last_key_char != c);
 }
 
 public static boolean was_key_pressed(int i)
 {
  return (key_code == i && last_key_code != i);
 }
}
