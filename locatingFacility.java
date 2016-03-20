import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;


public class locatingFacility 
{
	static int totalZones;
	static int totalFacilities;
	
	public static HashMap<Integer, Integer> mapWeights = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> mapXValues = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> mapYValues = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Double> mapDistances = new HashMap<Integer, Double>();
	public static HashMap<Integer, Integer> mapAllocation = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> mapAllocationOld = new HashMap<Integer, Integer>();
	public static ArrayList<HashMap<Integer, Double>> listDistanceToFacility = new ArrayList<HashMap<Integer, Double>>();
	
	public static void main(String[] args) throws NumberFormatException, IOException
	{
		
		String fileName="Instance2.txt";
		String ptsFileName="points.txt";
		readFile(fileName);
		String line ="";
		
        
        	for(int j=1;j<=totalZones;j++)
    	    {
        		FileReader pointsFile = new FileReader(ptsFileName);
                BufferedReader ptsBufferReader = new BufferedReader(pointsFile);
        		int i=1;
        		while((line = ptsBufferReader.readLine()) != null)   
                {
        		  mapDistances.put(i, calculateDistance(Integer.parseInt(line.split("\t")[1].toString()),Integer.parseInt(line.split("\t")[2].toString()),j));
    	    	  i++;
    		    }
        		 ptsBufferReader.close();
    	    	listDistanceToFacility.add(j-1,mapDistances);
    	    	mapDistances = new HashMap<Integer, Double>();
    	    	
    	    }
       
	     allocateZones();
	     boolean chkIterate = true;
	     int FF=0;
	     //goup zones with simillar allocation
	     HashMap<Integer, Double> xValuesMap = new HashMap<Integer, Double>();
	     HashMap<Integer, Double> yValuesMap = new HashMap<Integer, Double>();
	     while(chkIterate)
	     {
	    	 listDistanceToFacility = new ArrayList<HashMap<Integer,Double>>();
	    	 mapAllocationOld = new HashMap<Integer, Integer>();
	    	 mapAllocationOld.putAll(mapAllocation);
	    	 HashMap<Integer, List<Integer>> tempMap = new HashMap<Integer, List<Integer>>();
		     tempMap = groupZones();
		     xValuesMap = new HashMap<Integer, Double>();
		     yValuesMap = new HashMap<Integer, Double>();
		     for(int i=1;i<=totalFacilities;i++)
		     {
		    	 if(tempMap.containsKey(i))
		    	 {
		    		 xValuesMap.put(i,getXValue(tempMap.get(i)));
			    	 yValuesMap.put(i,getYValue(tempMap.get(i))); 
		    	 }
		    	 else
		    	 {
		    		 xValuesMap.put(i,50.00);
			    	 yValuesMap.put(i,50.00);
		    	 }
		    	 
		     }
		     for(int j=1;j<=totalZones;j++)
			 {
		    	 for(int i=1;i<=totalFacilities;i++)
				 {
		    		 mapDistances.put(i, calculateDistance(xValuesMap.get(i),yValuesMap.get(i),j));
			     }
			    listDistanceToFacility.add(mapDistances);
			    mapDistances = new HashMap<Integer, Double>();
			 }
			 allocateZones();
			 //xValuesMap = new HashMap<Integer, Double>();
			 //yValuesMap = new HashMap<Integer, Double>();
			 chkIterate = false;
			 for(int i=1;i<=mapAllocationOld.size();i++)
		     {
		    	 if(!mapAllocationOld.get(i).equals(mapAllocation.get(i)))
		    	 {
		    		 chkIterate = true;
		    		 break;
		    	 }
		     }
			 FF++;
	     }
	     System.out.println(FF + "  Final Allocation: \n");
	     System.out.println(mapAllocation);
	     
	     try
	     {
	    	File fileTwo=new File("finalAllocation2.txt");
	    	FileOutputStream fos=new FileOutputStream(fileTwo);
	        PrintWriter pw=new PrintWriter(fos);
	        int s=1;
	    	for(Entry<Integer, Integer> m :mapAllocation.entrySet())
	    	{
	    		if(xValuesMap.containsKey(s))
	    			pw.println(m.getKey()+"\t"+m.getValue()+"\t"+xValuesMap.get(s)+"\t"+yValuesMap.get(s));
	    		else
	    			pw.println(m.getKey()+"\t"+m.getValue());
	    	    s++;
	    	}
   	        pw.flush();
   	        pw.close();
   	        fos.close();
	      }
	     catch(Exception e)
	     {}

	     
	    	    //calculate x,y for simillar allocations
	    	    //re-iterate till allocation reamins same as prev (equalize hashmap)
	          
	}
	public static void readFile(String fileName)
	{
	       try
	       {
	          FileReader inputFile = new FileReader(fileName);
	          BufferedReader bufferReader = new BufferedReader(inputFile);
	          String firstLine, line;
	          firstLine = bufferReader.readLine();
	          totalZones = Integer.parseInt(firstLine.split(" ")[0].toString());
	          totalFacilities = Integer.parseInt(firstLine.split(" ")[1].toString());

	          while ((line = bufferReader.readLine()) != null)   
	          {
	        	  mapWeights.put(Integer.parseInt(line.split("\t")[0].toString()), Integer.parseInt(line.split("\t")[1].toString()));
	        	  mapXValues.put(Integer.parseInt(line.split("\t")[0].toString()), Integer.parseInt(line.split("\t")[2].toString()));
	        	  mapYValues.put(Integer.parseInt(line.split("\t")[0].toString()), Integer.parseInt(line.split("\t")[3].toString()));
	          }
	          bufferReader.close();
	       }catch(Exception e){
	          System.out.println("Error while reading file line by line:" + e.getMessage());                      
	       }
	}
	public static double calculateDistance(double x, double y, int zoneNum)
	{
		double distance;
		distance = Math.abs(x - mapXValues.get(zoneNum).doubleValue()) + Math.abs(y - mapYValues.get(zoneNum).doubleValue());
		return distance;
	}
	public static void allocateZones()
	{
		 double leastDistance = 0.00;
         int allotedZone;
         
         for(int i=0;i<listDistanceToFacility.size();i++)
         {
        	 
        	 leastDistance = listDistanceToFacility.get(i).get(1);
          	  allotedZone = 1;
          	  for(int j=2;j<=totalFacilities;j++)
   	          {
          		  if(leastDistance > listDistanceToFacility.get(i).get(j)) 
          			  {
          			    leastDistance = listDistanceToFacility.get(i).get(j);
          			    allotedZone = j;
          			  }
   	          }
          	  mapAllocation.put(i+1, allotedZone);
         }
         
    }
	public static HashMap<Integer, List<Integer>> groupZones()
	{
		HashMap<Integer, List<Integer>> tempMap = new HashMap<Integer, List<Integer>>();
		for(int i=1;i<=totalZones;i++)
	     {
	    	 addToMap(tempMap,mapAllocation.get(i),i);
	     }
		
		return tempMap;
		
	}
	public static void addToMap(HashMap<Integer, List<Integer>> map, Integer key, Integer value)
	{
		  if(!map.containsKey(key))
		  {
		    map.put(key, new ArrayList<Integer>());
		  }
		  map.get(key).add(value);
		  
	}
	public static double getXValue(List<Integer> equalZones)
	{
		int weightSum=0;
		int xSum = 0;
		double xVal = 0.00;
		for(int i=0;i<equalZones.size();i++)
		{
			weightSum += mapWeights.get(equalZones.get(i));
			xSum += (mapWeights.get(equalZones.get(i)) * mapXValues.get(equalZones.get(i)));
		}
		xVal = xSum/weightSum;
		return xVal;
	}
	public static double getYValue(List<Integer> equalZones)
	{
		int weightSum=0;
		int ySum = 0;
		double yVal = 0.00;
		for(int i=0;i<equalZones.size();i++)
		{
			weightSum += mapWeights.get(equalZones.get(i));
			ySum += (mapWeights.get(equalZones.get(i)) * mapYValues.get(equalZones.get(i)));
		}
		yVal = ySum/weightSum;
		return yVal;
	}
	
}
