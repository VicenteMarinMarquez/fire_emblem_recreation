/* --- ITEM ---
 * 
 *    Description: This is the Item class, used in Fire Emblem Recreation. The class represents an item in a unit's
 * inventory. These are the types of Items:
 * - Weapons
 * - Staves
 * - Consumables
 * 
 */

package fire_emblem;

//import java.io.File;
import java.util.Scanner;
//import hsa_ufa.*;
import java.awt.*;

public class Item
{
	//*** *** Variables *** ***

	public int item_num, item_type, max_durability;
	public String item_name;
	public Image item_image;
	public int durability, weapon_type;
	public boolean is_physical;
	public int[] weapon_stats; //In this order: Range, Might, Hit, Crit, Weight
	public int[] staff_stats; //In this order: Range, Base_Healing
	public int[] consumable_stats; //In this order: HP_restore

	//*** *** Constructors *** ***

	public Item(int given_item_num)
	{
		item_num = given_item_num;
		get_item_info();
	}

	//*** *** Methods *** ***

	public void get_item_info()
	{
		String line = "";
		Scanner file_scan, line_scan;
		try
		{
			file_scan = new Scanner(FE_R_Main.file_item_data);
			file_read: while (!line.equals("****FILE_END****"))
			{
				if (line.equals("**Item_Start"))
				{
					line = file_scan.nextLine();
					line_scan = new Scanner(line);
					if (line_scan.nextInt() == item_num)
					{
						item_name = line_scan.nextLine().trim();
						line = file_scan.nextLine();
						line_scan = new Scanner(line);
						item_type = line_scan.nextInt();
						item_image = Toolkit.getDefaultToolkit().getImage(FE_R_Main.resources_path + file_scan.nextLine());
						while (!line.equals("*Stats_Start"))
						{
							line = file_scan.nextLine();
						}
						line = file_scan.nextLine();
						line_scan = new Scanner(line);
						max_durability = line_scan.nextInt();
						durability = max_durability;
						if (item_type == 0)
						{
							line = file_scan.nextLine();
							line_scan = new Scanner(line);
							weapon_type = line_scan.nextInt();
							if (weapon_type == 0 || weapon_type == 1 || weapon_type == 2 || weapon_type == 3) is_physical = true;
							else is_physical = false;

							weapon_stats = new int[5];
							line = file_scan.nextLine();
							line_scan = new Scanner(line);
							for (int i = 0; i < weapon_stats.length; i++)
							{
								weapon_stats[i] = line_scan.nextInt();
							}
						}
						else if (item_type == 1)
						{
							staff_stats = new int[2];
							line = file_scan.nextLine();
							line_scan = new Scanner(line);
							for (int i = 0; i < staff_stats.length; i++)
							{
								staff_stats[i] = line_scan.nextInt();
							}
						}
						else if (item_type == 2)
						{
							consumable_stats = new int[1];
							line = file_scan.nextLine();
							line_scan = new Scanner(line);
							for (int i = 0; i < consumable_stats.length; i++)
							{
								consumable_stats[i] = line_scan.nextInt();
							}
						}
						break file_read;
					}
				}
				line = file_scan.nextLine();
			}
		}
		catch (Exception ex)
		{
			System.out.println("Error reading the file " + FE_R_Main.file_item_data + " from Item.");
		}

	}

	/*     This method tries to use this item on the unit with this item in their inventory.
	 *     Note: this method uses the method "try_use_item" with the same arguments to find the first two return values.
	 * - The arguments represent the unit with this item in their inventory and the index of this item in
	 * their inventory.
	 * - Returns an array of booleans of size three representing the following in this order:
	 * - index 0: Whether or not the item was in the given units inventory in the correct index (the method terminates 
	 * if this is false, and so the item is not used).
	 * - index 1: Whether or not the item can be used by the given unit (if this is false, then the item is not used).
	 * - index 2: Whether or not the item was used up by the given unit.
	 */
	public boolean[] use_item(Map_unit unit, int item_num)
	{
		boolean[] return_values = { false, false, false };
		{
			boolean[] item_usability = try_use_item(unit, item_num);
			return_values[0] = item_usability[0];
			return_values[1] = item_usability[1];
		}
		if(!(return_values[0] && return_values[1])) return return_values;
		
		if (item_type == 2)
		{
			unit.combat_stats[0] = Math.min(unit.stats[0][0], unit.combat_stats[0] + consumable_stats[0]);
			durability--;

			if (durability <= 0)
			{
				unit.inventory.remove(item_num);
				return_values[2] = true;
				return return_values;
			}
		}
		return return_values;
	}

	/*     This method checks to see if this item can be used on the unit with this item in their inventory.
	 *     Note: the item is never used or used up.
	 * - The arguments represent the unit with this item in their inventory and the index of this item in
	 * their inventory.
	 * - Returns an array of booleans of size two representing the following in this order:
	 * - index 0: Whether or not the item was in the given units inventory in the correct index (the method terminates 
	 * if this is false, and so the item cannot be used).
	 * - index 1: Whether or not the item can be used by the given unit.
	 */
	public boolean[] try_use_item(Map_unit unit, int item_num)
	{
		boolean[] return_values = { false, false };

		//Check if this item is the item in the given index of the inventory of the given unit.  
		{
			Item item = unit.inventory.get(item_num);
			return_values[0] = (item == this);
		}
		if (!return_values[0]) return return_values;

		//Check if this item can be used by the given unit.
		if (item_type == 2)
		{
			if(unit.combat_stats[0] < unit.stats[0][0]) return_values[1] = true;
		}
		return return_values;
	}

}