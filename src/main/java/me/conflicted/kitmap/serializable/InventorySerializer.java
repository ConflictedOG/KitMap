package me.conflicted.kitmap.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
@SuppressWarnings({"deprecation"})

public class InventorySerializer {

	
	
    public static String InventoryToString (Inventory invInventory, Player player)
    {
       
        String serialization = invInventory.getSize() + ";";
        for (int i = 0; i < invInventory.getSize(); i++)
        {
            ItemStack is = invInventory.getItem(i);
            if (is != null)
            {
                String serializedItemStack = new String();
             
				String isType = String.valueOf(is.getType().getId());
                serializedItemStack += "t@" + isType;
             
                if (is.getDurability() != 0)
                {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }
             
                if (is.getAmount() != 1)
                {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }
             
                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0)
                {
                    for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
                    {
                        serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                    }
                }
             
                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
        
    }
    
	    public static Inventory StringToInventory (String invString)
	    {
	        String[] serializedBlocks = invString.split(";");
	        String invInfo = serializedBlocks[0];
	        Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));
	       
	        for (int i = 1; i < serializedBlocks.length; i++)
	        {
	            String[] serializedBlock = serializedBlocks[i].split("#");
	            int stackPosition = Integer.valueOf(serializedBlock[0]);
	           
	            if (stackPosition >= deserializedInventory.getSize())
	            {
	                continue;
	            }
	           
	            ItemStack is = null;
	            Boolean createdItemStack = false;
	           
	            String[] serializedItemStack = serializedBlock[1].split(":");
	            for (String itemInfo : serializedItemStack)
	            {
	                String[] itemAttribute = itemInfo.split("@");
	                if (itemAttribute[0].equals("t"))
	                {
	                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
	                    createdItemStack = true;
	                }
	                else if (itemAttribute[0].equals("d") && createdItemStack)
	                {
	                    is.setDurability(Short.valueOf(itemAttribute[1]));
	                }
	                else if (itemAttribute[0].equals("a") && createdItemStack)
	                {
	                    is.setAmount(Integer.valueOf(itemAttribute[1]));
	                }
	                else if (itemAttribute[0].equals("e") && createdItemStack)
	                {
	                    is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
	                }
	            }
	            deserializedInventory.setItem(stackPosition, is);
	        }
	       
	        return deserializedInventory;
	    }
	
	    public static String toBase64(Inventory inventory) {
	        return toBase64(inventory.getContents());
	    }

	    public static String toBase64(ItemStack itemstack){
	        ItemStack[] arr = new ItemStack[1];
	        arr[0] = itemstack;
	        return toBase64(arr);
	    }

	    public static String toBase64(ItemStack[] contents) {
	        try {
	            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

	            dataOutput.writeInt(contents.length);

	            for (ItemStack stack : contents) {
	                dataOutput.writeObject(stack);
	            }
	            dataOutput.close();
	            return Base64Coder.encodeLines(outputStream.toByteArray());
	        } catch (Exception e) {
	            throw new IllegalStateException("Unable to save item stacks.", e);
	        }
	    }

	    public static Inventory inventoryFromBase64(String data) throws IOException {
	        ItemStack[] stacks = stacksFromBase64(data);
	        Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(stacks.length / 9D) * 9);

	        for (int i = 0; i < stacks.length; i++) {
	            inventory.setItem(i, stacks[i]);
	        }

	        return inventory;
	    }

	    public static ItemStack[] stacksFromBase64(String data) throws IOException {
	        try {
	            if(data == null || Base64Coder.decodeLines(data) == null) return new ItemStack[]{};
	            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
	            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
	            ItemStack[] stacks = new ItemStack[dataInput.readInt()];

	            for (int i = 0; i < stacks.length; i++) {
	                stacks[i] = (ItemStack) dataInput.readObject();
	            }
	            dataInput.close();
	            return stacks;
	        } catch (ClassNotFoundException e) {
	            throw new IOException("Unable to decode class type.", e);
	        }
	}
		
		
	    
}
