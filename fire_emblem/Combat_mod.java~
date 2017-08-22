/* --- COMBAT MODERATOR ---
 * 
 *    Description: This is the Combat_mod class, used in Fire Emblem Recreation. The class keeps track of all aspects
 * relating to combat between units.
 * 
 */

package fire_emblem;

public class Combat_mod
{
 //*** *** Variables *** ***

 public static boolean combat_in_progress = false, hit_target = false;
 public static long combat_start_time = -1, round_start_time = -1; //round start time only applies when animations are off.

 public static Map_unit player_combatant, enemy_combatant;
 public static int[] battle_stats;

 /*    The value of "round" represents the following:
  * - Round == 0: The initiating unit attacks (always).
  * - Round == 1: The defending unit retaliates (unless unharmed).
  * - Round == 2: The defending unit retaliates again if fast enough (this round is mutually exclusive with round 3).
  * - Round == 3: The initiating unit attacks again if fast enough (this round is mutually exclusive with round 2).
  */
 public static int round;

 //The units taking part in combat
 public static Map_unit initiating_unit, defending_unit;
 //*** *** Methods *** ***

 public static void called_method()
 {
  if (combat_in_progress)
  {
   if (FE_R_Main.current_time >= round_start_time + 30)
   {
    if (round == 2 && battle_stats[7] != 2) round++;
    if (round == 3 && battle_stats[3] != 2) round++;
    
    boolean continue_combat = false;
    if(round < 4) continue_combat = peform_combat();
    
    if(!continue_combat)
    {
     round_start_time = -1;
     
     //If the player combatant does not have affiliation -1 (dead) calculate their experience.
     if (player_combatant.affiliation == 0) 
     {
       calc_experience();
     }
     end_combat();
    }
    else
    {
     round_start_time = FE_R_Main.current_time;
    }
   }
  }
 }

 //This method returns whether or not the combat was initiated.
 public static boolean intiate_combat(Map_unit i_unit, Map_unit d_unit)
 {
  if (combat_in_progress) return false;
  combat_in_progress = true;
  combat_start_time = FE_R_Main.current_time;
  round_start_time = FE_R_Main.current_time;
  initiating_unit = i_unit;
  defending_unit = d_unit;

  initiating_unit.finish_turn();

  //Change the directions the units are facing (in their animations).
  {
   int facing_direction_i = Map_mod.get_relative_location(initiating_unit.temporary_x, initiating_unit.temporary_y, defending_unit.x, defending_unit.y);
   initiating_unit.anmtn_num = Map_unit.find_facing_animation(facing_direction_i);
   int facing_direction_d = Map_mod.get_relative_location(defending_unit.x, defending_unit.y, initiating_unit.temporary_x, initiating_unit.temporary_y);
   defending_unit.anmtn_num = Map_unit.find_facing_animation(facing_direction_d);
  }

  System.out.println("Initiating Combat.");

  get_battle_stats();

  hit_target = false;
  player_combatant = null;
  enemy_combatant = null;
  if (initiating_unit.affiliation == 0)
  {
   player_combatant = initiating_unit;
   enemy_combatant = defending_unit;
  }
  else if (defending_unit.affiliation == 0)
  {
   player_combatant = defending_unit;
   enemy_combatant = initiating_unit;
  }
  round = 0;

  return true;
 }

 // - Calculates the initiating unit's might, hit, crit, number of strikes;
 // defending unit's might, hit, crit, and number of strikes in a battle 
 // and updates the values in an the global int array in the above order.

 //This method is for use inside of the class  to update the battle stats.
 private static void get_battle_stats()
 {
  battle_stats = new int[8];

  initiating_unit.update_inventory();
  defending_unit.update_inventory();
  {
   Map_unit unit_1 = initiating_unit, unit_2 = defending_unit;
   for (int i = 0; i < 2; i++)
   {
    if (i == 1)
    {
     unit_1 = defending_unit;
     unit_2 = initiating_unit;
    }

    int total_defence, accuracy_bonus = 0;
    if (unit_1.equiped_weapon.is_physical)
    {
     total_defence = unit_2.stats[0][6] + Map_mod.terrain_defence[Map_mod.terrain[unit_2.temporary_x][unit_2.temporary_y]];
    }
    else
    {
     total_defence = unit_2.stats[0][7] + Map_mod.terrain_defence[Map_mod.terrain[unit_2.temporary_x][unit_2.temporary_y]];
    }

    {
     int weapon_type_1 = unit_1.equiped_weapon.weapon_type,
       weapon_type_2 = unit_2.equiped_weapon.weapon_type;
     if (unit_1.equiped_weapon.is_physical == unit_2.equiped_weapon.is_physical)
     {
      if (weapon_type_1 != 3 && weapon_type_2 != 3)
      {
       weapon_type_1 = weapon_type_1 % 4;
       weapon_type_2 = weapon_type_2 % 4;
       if (weapon_type_1 == ((weapon_type_2 + 1) % 3)) accuracy_bonus = 15;
       else accuracy_bonus = -15;
      }
     }
    }

    battle_stats[(4 * i) + 0] = unit_1.combat_stats[2] - total_defence;
    battle_stats[(4 * i) + 0] = Math.max(battle_stats[(4 * i) + 0], 0);

    battle_stats[(4 * i) + 1] = unit_1.combat_stats[3] + accuracy_bonus - unit_2.combat_stats[4];
    battle_stats[(4 * i) + 1] = Math.max(battle_stats[(4 * i) + 1], 0);
    battle_stats[(4 * i) + 1] = Math.min(battle_stats[(4 * i) + 1], 100);

    battle_stats[(4 * i) + 2] = unit_1.combat_stats[5] - unit_2.stats[0][5];
    battle_stats[(4 * i) + 2] = Math.max(battle_stats[(4 * i) + 2], 0);
    battle_stats[(4 * i) + 2] = Math.min(battle_stats[(4 * i) + 2], 100);

    if (unit_1.combat_stats[6] >= unit_2.combat_stats[6] + 4) battle_stats[(4 * i) + 3] = 2;
    else battle_stats[(4 * i) + 3] = 1;
   }
  }
 }

 //For external method calls that don't necessarily involve actual combat (like the combat forecast).
 public static int[] get_battle_stats(Map_unit i_unit, Map_unit d_unit)
 {
  battle_stats = new int[8];

  i_unit.update_inventory();
  d_unit.update_inventory();
  {
   Map_unit unit_1 = i_unit, unit_2 = d_unit;
   for (int i = 0; i < 2; i++)
   {
    if (i == 1)
    {
     unit_1 = d_unit;
     unit_2 = i_unit;
    }

    int total_defence, accuracy_bonus = 0;
    if (unit_1.equiped_weapon.is_physical)
    {
     total_defence = unit_2.stats[0][6] + Map_mod.terrain_defence[Map_mod.terrain[unit_2.temporary_x][unit_2.temporary_y]];
    }
    else
    {
     total_defence = unit_2.stats[0][7] + Map_mod.terrain_defence[Map_mod.terrain[unit_2.temporary_x][unit_2.temporary_y]];
    }

    {
     int weapon_type_1 = unit_1.equiped_weapon.weapon_type,
       weapon_type_2 = unit_2.equiped_weapon.weapon_type;
     if (unit_1.equiped_weapon.is_physical == unit_2.equiped_weapon.is_physical)
     {
      if (weapon_type_1 != 3 && weapon_type_2 != 3)
      {
       weapon_type_1 = weapon_type_1 % 4;
       weapon_type_2 = weapon_type_2 % 4;
       if (weapon_type_1 == ((weapon_type_2 + 1) % 3)) accuracy_bonus = 15;
       else accuracy_bonus = -15;
      }
     }
    }

    battle_stats[(4 * i) + 0] = unit_1.combat_stats[2] - total_defence;
    battle_stats[(4 * i) + 0] = Math.max(battle_stats[(4 * i) + 0], 0);

    battle_stats[(4 * i) + 1] = unit_1.combat_stats[3] + accuracy_bonus - unit_2.combat_stats[4];
    battle_stats[(4 * i) + 1] = Math.max(battle_stats[(4 * i) + 1], 0);
    battle_stats[(4 * i) + 1] = Math.min(battle_stats[(4 * i) + 1], 100);

    battle_stats[(4 * i) + 2] = unit_1.combat_stats[5] - unit_2.stats[0][5];
    battle_stats[(4 * i) + 2] = Math.max(battle_stats[(4 * i) + 2], 0);
    battle_stats[(4 * i) + 2] = Math.min(battle_stats[(4 * i) + 2], 100);

    if (unit_1.combat_stats[6] >= unit_2.combat_stats[6] + 4) battle_stats[(4 * i) + 3] = 2;
    else battle_stats[(4 * i) + 3] = 1;
   }
  }
  return battle_stats;
 }

 //returns whether or not the combat should continue (sometimes it shouldn't because a unit 
 //died or a unit attacked twice and so no one else will attack).
 public static boolean peform_combat()
 {
  System.out.println("Statring round " + round + ".");

  boolean hit_in_round = false;
  if (round == 0 || round == 3)
  {
   int[] round_stats = { battle_stats[0], battle_stats[1], battle_stats[2] };
   hit_in_round = perform_round_of_combat(round_stats, initiating_unit, defending_unit, true);
  }
  else
  {
   int[] round_stats = { battle_stats[4], battle_stats[5], battle_stats[6] };
   hit_in_round = perform_round_of_combat(round_stats, defending_unit, initiating_unit, true);
  }
  if (hit_in_round) hit_target = true;

  if (initiating_unit.combat_stats[0] <= 0)
  {
   initiating_unit.died();
   System.out.print(initiating_unit.unit_name + " has died! ");
   return false;
  }
  if (defending_unit.combat_stats[0] <= 0)
  {
   defending_unit.died();
   System.out.print(defending_unit.unit_name + " has died! ");
   return false;
  }

  round++;
  
  //If it was round 2 then round 3 will not happen (since those rounds are mutually exclusive),
  //so the combat should not end unless round 2 just ended (making it round 3).
  return (round != 3);
 }

 public static void calc_experience()
 {
  if (player_combatant != null)
  {
   int xp_gained = 0;
   if (hit_target)
   {
    xp_gained += (31 + enemy_combatant.xp_stats[0] - player_combatant.xp_stats[0]) / 3;
    if (enemy_combatant.affiliation == -1)
    {
     xp_gained += ((enemy_combatant.xp_stats[0] - player_combatant.xp_stats[0]) * 3) + 20;
    }
   }
   else
   {
    xp_gained += 1;
   }
   xp_gained = Math.max(xp_gained, 0);

   player_combatant.xp_stats[1] += xp_gained;
   if (player_combatant.xp_stats[1] > 99) player_combatant.level_up();
  }
 }

 public static void end_combat()
 {
  System.out.println("Combat has ended.\n");
  
  Map_ui.update_ui();
  
  round = -1;
  
  initiating_unit.anmtn_num = initiating_unit.default_anmtn_num;
  defending_unit.anmtn_num = defending_unit.default_anmtn_num;

  combat_in_progress = false;
  combat_start_time = -1;
  initiating_unit = null;
  defending_unit = null;

  Map_mod.check_phase();
 }
 
 // - Returns if the player unit hit the enemy unit.
 // The round stats represents unit_1's damage, hit rate, and critical rate, in that order, when attacking unit_2.
 public static boolean perform_round_of_combat(int[] round_stats, Map_unit unit_1, Map_unit unit_2, boolean print_round_events)
 {
  boolean hit_target = false;
  
  if(print_round_events) System.out.print("\t" + unit_1.unit_name + " attaks");
  
  int num_RNG = (int) (Math.random() * 100);
  if (num_RNG < round_stats[1])
  {
   if (player_combatant == unit_1) hit_target = true;
   int crit_multiplier = 1;
   num_RNG = (int) (Math.random() * 100);
   if (num_RNG < round_stats[2])
   {
    crit_multiplier = 3;
    if(print_round_events) System.out.print(" with a critical hit!");
   }
   else
   {
    if(print_round_events) System.out.print(" and connects.");
   }
   
   int damage = round_stats[0] * crit_multiplier;
   unit_2.combat_stats[0] -= damage;
   if(print_round_events) System.out.print(" " + unit_2.unit_name + " took " + damage + " damage.");
  }
  else
  {
   if(print_round_events) System.out.print(" and misses!");
  }
  if(print_round_events) System.out.print("\n");
  return hit_target;
 }

}
