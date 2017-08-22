/* --- TEXT CONSTRUCTOR ---
 * 
 *    Description: This is the Text_constructor class, used in Fire Emblem Recreation. The class has different methods
 * for dealing with any text in game.
 * 
 */

package fire_emblem;

import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
//import hsa_ufa.*;

public class Text_constructor
{
    //*** *** Variables *** ***
    
    public String image_path = FE_R_Main.resources_path + "Fonts";
    public ArrayList<ArrayList<BufferedImage>> char_img = new ArrayList<ArrayList<BufferedImage>>();
    public ArrayList<ArrayList<Integer>> char_length = new ArrayList<ArrayList<Integer>>();
    public ArrayList<ArrayList<Boolean>> supported_chars = new ArrayList<ArrayList<Boolean>>();
    public final ArrayList<Integer> default_Integer_ArrayList;
    public final ArrayList<Boolean> default_Boolean_ArrayList;
    
    //*** *** Constuctors *** ***
    
    public Text_constructor()
    {
        
        default_Integer_ArrayList = new ArrayList<Integer>();
        default_Boolean_ArrayList = new ArrayList<Boolean>();
        for (int i = 0; i < 128; i++)
        {
            default_Integer_ArrayList.add(0);
            default_Boolean_ArrayList.add(false);
        }
        
        get_fonts();
    }
    
    //*** *** Methods *** ***
    
    public void get_fonts()
    {
        String line = "";
        Scanner line_scan;
        try
        {
            Scanner file_scan = new Scanner(FE_R_Main.file_font_data);
            /*file_read:*/ while (!line.equals("****FILE_END****"))
            {
                if (line.equals("**Font_Start"))
                {
                    line = file_scan.nextLine();
                    line_scan = new Scanner(line);
                    int n = line_scan.nextInt();
                    {
                        ArrayList<Integer> array_Integer = new ArrayList<Integer>(default_Integer_ArrayList);
                        ArrayList<Boolean> array_Boolean = new ArrayList<Boolean>(default_Boolean_ArrayList);
                        char_length.add(array_Integer);
                        supported_chars.add(array_Boolean);
                    }
                    if (char_length.size() - 1 != n)
                    {
                        line_scan.close();
                        throw new Exception("Fonts Missing");
                    }
                    
                    while (!line.equals("*Character_Lengths_Start"))
                    {
                        line = file_scan.nextLine();
                    }
                    line = file_scan.nextLine();
                    while (!line.equals("*Character_Lengths_End"))
                    {
                        line_scan = new Scanner(line);
                        int num_of_chars = line_scan.nextInt(), initial_ascii = line_scan.nextInt();
                        for (int i = 0; i < num_of_chars; i++)
                        {
                            char_length.get(n).set(initial_ascii + i, line_scan.nextInt());
                            supported_chars.get(n).set(initial_ascii + i, true);
                        }
                        line = file_scan.nextLine();
                    }
                    
                    {
                        ArrayList<BufferedImage> empty_images_array = new ArrayList<BufferedImage>();
                        for (int i = 0; i < 128; i++)
                            empty_images_array.add(null);
                        char_img.add(empty_images_array);
                    }
                    
                    for (int i = 0; i < supported_chars.get(n).size(); i++)
                    {
                        if (supported_chars.get(n).get(i))
                        {
                            File image_file = new File(image_path + "\\font_" + n + "_char_" + i + ".png");
                            BufferedImage buffered_image = ImageIO.read(image_file);
                            char_img.get(n).set(i, buffered_image);
                        }
                    }
                }
                line = file_scan.nextLine();
            }
            
        }
        catch (Exception ex)
        {
            System.out.println("Error reading the file " + FE_R_Main.file_font_data + " from Text_constructor.");
            System.out.println("The error was: \"" + ex.getMessage() + "\"");
        }
    }
    
    public Image create_text_img(String str, int font_num)
    {
        if (str.length() == 0) return null;
        while (str.charAt(0) == ' ')
        {
            if (str.length() > 1) str = str.substring(1);
            else return null;
        }
        
        BufferedImage buffered_text = null;
        int text_length = 0, text_height = 0;
        for (int i = 0; i < str.length(); i++)
        {
            int current_ascii = (int) (str.charAt(i));
            BufferedImage buffered_1 = buffered_text;
            if (current_ascii == 32)
            {
                buffered_text = new BufferedImage(text_length + 6, text_height, BufferedImage.TYPE_INT_ARGB);
                Graphics g = buffered_text.getGraphics();
                g.drawImage(buffered_1, 0, 0, null);
                
                text_length += 4;
            }
            else
            {
                BufferedImage buffered_2 = char_img.get(font_num).get(current_ascii);
                int current_width = char_length.get(font_num).get(current_ascii);
                text_height = Math.max(text_height, buffered_2.getHeight());
                
                buffered_text = new BufferedImage(text_length + current_width, text_height, BufferedImage.TYPE_INT_ARGB);
                Graphics g = buffered_text.getGraphics();
                g.drawImage(buffered_1, 0, 0, null);
                g.drawImage(buffered_2, text_length, 0, null);
                
                text_length += current_width - 2;
            }
        }
        Image img = buffered_text;
        return img;
    }
    
    public Image create_text_img(String str, int font_num, String file_name)
    {
        if (str.length() == 0) return null;
        while (str.charAt(0) == ' ')
        {
            if (str.length() > 1) str = str.substring(1);
            else return null;
        }
        
        BufferedImage buffered_text = null;
        int text_length = 0, text_height = 0;
        for (int i = 0; i < str.length(); i++)
        {
            int current_ascii = (int) (str.charAt(i));
            BufferedImage buffered_1 = buffered_text;
            if (current_ascii == 32)
            {
                buffered_text = new BufferedImage(text_length + 6, text_height, BufferedImage.TYPE_INT_ARGB);
                Graphics g = buffered_text.getGraphics();
                g.drawImage(buffered_1, 0, 0, null);
                
                text_length += 4;
            }
            else
            {
                BufferedImage buffered_2 = char_img.get(font_num).get(current_ascii);
                int current_width = char_length.get(font_num).get(current_ascii);
                text_height = Math.max(text_height, buffered_2.getHeight());
                
                buffered_text = new BufferedImage(text_length + current_width, text_height, BufferedImage.TYPE_INT_ARGB);
                Graphics g = buffered_text.getGraphics();
                g.drawImage(buffered_1, 0, 0, null);
                g.drawImage(buffered_2, text_length, 0, null);
                
                text_length += current_width - 2;
            }
        }
        try
        {
            ImageIO.write(buffered_text, "PNG", new File(file_name));
        }
        catch (Exception ex)
        {
            System.out.println("Could not write file.");
        }
        
        Image img = buffered_text;
        return img;
    }
    
    public Image create_text_img(String str, int font_num, int[] returned_lengths)
    {
        BufferedImage buffered_text = null;
        int text_length = 0, text_height = 1;
        for (int i = 0; i < str.length(); i++)
        {
            int current_ascii = (int) (str.charAt(i));
            BufferedImage buffered_1 = buffered_text;
            if (current_ascii == 32)
            {
                buffered_text = new BufferedImage(text_length + 6, text_height, BufferedImage.TYPE_INT_ARGB);
                Graphics g = buffered_text.getGraphics();
                g.drawImage(buffered_1, 0, 0, null);
                
                text_length += 4;
                returned_lengths[i] = text_length;
            }
            else
            {
                BufferedImage buffered_2 = char_img.get(font_num).get(current_ascii);
                int current_width = char_length.get(font_num).get(current_ascii);
                text_height = Math.max(text_height, buffered_2.getHeight());
                
                buffered_text = new BufferedImage(text_length + current_width, text_height, BufferedImage.TYPE_INT_ARGB);
                Graphics g = buffered_text.getGraphics();
                g.drawImage(buffered_1, 0, 0, null);
                g.drawImage(buffered_2, text_length, 0, null);
                
                text_length += current_width - 2;
                returned_lengths[i] = text_length;
            }
        }
        Image img = buffered_text;
        return img;
    }
}
