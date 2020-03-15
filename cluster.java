import java.io.*;
import java.lang.Object;
import java.util.*;
import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class cluster {
	int No_nodes,No_Cluster,k_factor;
	int[] index_Seq, anon_target_degree,update_Degree;
	 
	Map<String, Integer> agent_Nodes = new LinkedHashMap<String, Integer>();
	Map<String, Integer>[] cluster; 
	
	HashMap<String,Integer> nodes = new HashMap<>();
	HashMap<String,Integer> anon_nodes = new HashMap<>();
	HashMap<String,Integer> Sorted_nodes = new LinkedHashMap<>();
	
	//TreeMap<String,Integer> visited = new TreeMap<String,Integer>();
	TreeMap<String,String> node_C = new TreeMap<String,String>();
	Map<String, List<String>> edges = new HashMap<>();
	Map<String, List<String>> anon_edges = new HashMap<>();

	static int index_Count;

	
	cluster()
	{
	}
	
	void setClusterCount(int k,int n)
	{
		k_factor = k;
		No_Cluster = n;
	}
	
	// Insert Nodes
	void inNodes(String n)
	{
		nodes.put(n,0);
		anon_nodes.put(n,0);
	}
	
	void setNodeCount(int no)
	{
		No_nodes = no ;
	}
	
	// Insert Edges
	void inEdge(String v1,String v2)
	{
		if(edges.get(v1)==null)
		{
			edges.put(v1, new ArrayList<String>());
			anon_edges.put(v1, new ArrayList<String>());
			//node_C.put(v1,"false");
		}
		if(edges.get(v2)==null)
		{
			edges.put(v2, new ArrayList<String>());
			anon_edges.put(v2, new ArrayList<String>());
			//node_C.put(v2,"false");
		}
		
		edges.get(v1).add(new String(v2));
		edges.get(v2).add(new String(v1));
		anon_edges.get(v1).add(new String(v2));
		anon_edges.get(v2).add(new String(v1));
		
		//edges.put(v1, v2);
		//edges.put(arg0, arg1)
		
		nodes.put(v1, nodes.get(v1) + 1);
		nodes.put(v2, nodes.get(v2) + 1);
		
		anon_nodes.put(v1, anon_nodes.get(v1) + 1);
		anon_nodes.put(v2, anon_nodes.get(v2) + 1);
	}
	
	
	// function to find out common nodes
	int common_nodes(String node1, String node2)
	{
		int i,count = 0,j;
		List<String> setN1 = new ArrayList<String>();
		setN1 = edges.get(node1);
		 
		List<String> setN2 = new ArrayList<String>();
		setN2 = edges.get(node2);
		
		for(i=0;i<setN1.size();i++)
		{
			for(j=0;j<setN2.size();j++)
			{
				//System.out.println("Comapre ==== " + setN1.get(i) + " :::: " + setN2.get(j));
				if(setN1.get(i).toString().equalsIgnoreCase(setN2.get(j).toString()) == true)
				{
					count++;
					j=setN2.size();
				}
			}
		}
		//System.out.println("Score ==== " + count);
	
		return count;
	}
	
	
	
	void sortByValues() { 
	       List<Entry<String, Integer>> list = new LinkedList<>(nodes.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator<Object>() {
	            public int compare(Object o2, Object o1) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              Sorted_nodes.put(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()));
	       } 
		
	}
	
	
	// Print Sorted Degree
	void printSortedDegree()
	{
		System.out.println("### Degrees of Nodes ###");
		for (Map.Entry<String, Integer> e : Sorted_nodes.entrySet()) {
		    //to get key
		    System.out.println(e.getKey()+ " --> " + e.getValue());
		}
	}
	
	// Define k Cluster Agent
	void setClusterAgent()
	{
		int c = No_Cluster;
		System.out.println("No. of Cluster : " + No_Cluster);
		
		int i,k=0,count=0,max=0,tempV,j,l,score=0,maxScore;
		String maxKey,flag="";
		int[] tempValue = new int[c+1];
		String[] tempKey = new String[c+1];
		String[] multiK = new String[c+1];
		
		for (Map.Entry<String, Integer> e : nodes.entrySet())
		{	tempV = e.getValue();
			if(count<c)
			{
				j=count-1;
				while(j>=0 && tempValue[j]<tempV)
				{
					tempValue[j+1] = tempValue[j];
					tempKey[j+1] = tempKey[j];
					j--;
				}
				
				tempValue[j+1] = tempV;
				tempKey[j+1] = e.getKey();
											
				count++;
			}
			else
			{
				for(i=0;i<c;i++)
				{
					if(tempV >= tempValue[i])
						break;
				}
	    	
				// Insert node in ClusterAgent List
				if(i!=c)
				{
					j=c-1;
					k=0;
															
					while(j>=0 && tempValue[j]==tempValue[c-1])
					{
						multiK[k] = tempKey[j];
						k++; j--;
					}
					
					if(tempV == tempValue[c-1])
						multiK[k++] = e.getKey();
					
					/*System.out.println(" Candidate Nodes : ");
					for(j=0;j<k;j++)
						System.out.println("," + multiK[j]);*/
					
					// 	for multiple key value
					if(k!=1)
					{
						maxScore=0; 
						for(j=0;j<k;j++)
						{
							// count score for each node
							score = 0;
							// Exclude if new node is also candidate node
							if(tempV != tempValue[c-1])
								score =  common_nodes(e.getKey(),multiK[j]);
							
							l=0;
							while(tempValue[l] > tempValue[c-1])
							{
								//System.out.println("Key : "+ e.getKey() + "Score Called : " + tempKey[l] + " :: " + multiK[j]);
								score += common_nodes(tempKey[l],multiK[j]);
								l++;
							}
							//System.out.println("Total Score : " + score);
							
							if(score>maxScore)
							{
								maxScore = score;
								flag = multiK[j];
							}
						}
						
					}
					else
					{
						flag = tempKey[c-1];
					}
					
					if(flag.equalsIgnoreCase(e.getKey().toString()) == false)
					{	
						k=c-1;
						while(tempKey[k]!=flag && k>0)
							k--;
						
						for(j=k;j>i;j--)
						{
							tempValue[j] = tempValue[j-1];
							tempKey[j] = tempKey[j-1];
						}
						
						tempKey[i] = e.getKey();
						tempValue[i] = tempV;
					}
				}
			}
		}
		
		for(i=0;i<c;i++)
		{
			agent_Nodes.put(tempKey[i], tempValue[i]);
		}

		/*System.out.println("### Cluster Agents ###");
		for(Map.Entry<String, Integer> e : agent_Nodes.entrySet()) {
		    //to get key
		    System.out.println(e.getKey()+ " --> " + e.getValue());
		}*/
	}
	
	
	// Function to create cluster from cluster_Agent Nodes
	void createCluster()
	{
		int i,j,k,flag=1,count,term;
		String agent;
		Set<String> agentSet = agent_Nodes.keySet();
		//cluster = new HashMap<String, Integer> [k];
		cluster = (Map<String, Integer>[]) new Map[No_Cluster];
		
		// for temporary storage
		Map<String, Integer> tempMap = new LinkedHashMap<String, Integer>();

		// Initialize each cluster with cluster_Agent
		// As well as attach nodes with 1 degree and 2 degree.
		String nod;
		int degree;
		List<String> candidateSet;
		
		for(i=0;i<No_Cluster;i++)
		{
			cluster[i] = new HashMap<String,Integer>();
			agent = agentSet.toArray()[i].toString();
			cluster[i].put(agent,nodes.get(agent));
			node_C.put(agent, agent);
			//visited.put(agent,1);
			
			// Attach nodes with 1/2/3 Degree
			candidateSet = edges.get(agent);
			for(j=0;j<candidateSet.size();j++)
			{
				nod = candidateSet.toArray()[j].toString();
				degree = nodes.get(nod);
								
				if(node_C.containsKey(nod)==false)
				{
					// direct include for degree 1 or 2.
					if(degree==1 || degree==2 || degree==3)
					{
						//tempMap.put(nod, degree);
						cluster[i].put(nod, degree);
						node_C.put(nod, agentSet.toArray()[i].toString());
						//System.out.println(" Node included : " + nod + " @@@@@ Cluster : " + i);
					}
				}
			}
		}
		
		
		
		while(flag==1)
		{ i=0;
		for(i=0;i<No_Cluster;i++)
		{
			flag=1; term=0;
			while(flag==1 && term<10)
			{
				flag=0;
				for(Map.Entry<String, Integer> e : cluster[i].entrySet()) {
				    //get all neighbors
					candidateSet = edges.get(e.getKey());
					
					for(j=0;j<candidateSet.size() && term<10;j++)
					{
						nod = candidateSet.toArray()[j].toString();
						degree = nodes.get(nod);
						//System.out.println(" Node : " + nod + " Degree : " + degree);
						
						//if(visited.get(nod)==0)
						if(node_C.containsKey(nod)==false)
						{
						// direct include for degree 1 or 2.
						if(degree==1 || degree==2 || degree==3)
						{
							tempMap.put(nod, degree);
							node_C.put(nod, agentSet.toArray()[i].toString());
							//visited.put(nod,1);
							flag = 1; term++;
						}
						else
						{
							// Neighbors
							List<String> nodes1 = edges.get(nod);
							count = 0;
							
							// count connectivity
							for(Map.Entry<String, Integer> e1 : cluster[i].entrySet()) {
								for(k=0;k<nodes1.size();k++)
								{
									if((e.getKey().compareToIgnoreCase(nodes1.toArray()[k].toString())) == 0)
										count++; 
								}
							}
							
							// add if connectivity is at-least half degree
							if(count > (degree/2))
							{
								tempMap.put(nod, degree);
								node_C.put(nod, agentSet.toArray()[i].toString());
								//visited.put(nod,1);
								//cluster[i].put(nod, degree);
								flag = 1; term++;
							}
						}
						}
					}
					
				    //System.out.println(e.getKey()+ " --> " + e.getValue());
				}
				
				if(flag==1)
				{
					for(Map.Entry<String, Integer> tempe : tempMap.entrySet()) {
						cluster[i].put(tempe.getKey(), tempe.getValue());
					}
					
					tempMap.clear();
				}
				
			}
		}
		}
		
		
		// Set Cluster for  unvisited nodes
		for (Map.Entry<String, Integer> e : nodes.entrySet()){
			if(node_C.containsKey(e.getKey())==false)
			{
				//System.out.println("  ############### Unvisited :::::::::::::        " + e.getKey());
				candidateSet = edges.get(e.getKey());
				degree=0; flag=0; term=0;
				
				for(i=0;i<No_Cluster && flag==0;i++)
				{

						count=0;
						for(Map.Entry<String, Integer> Ce : cluster[i].entrySet())
						{
						    if(candidateSet.contains(Ce.getKey())==true)
						    	count++;
						}
						
						if(count>degree)
						{
							degree = count;
							term = i;
						}
						
						if(degree >= (e.getValue()/2))
							flag = 1;
				}
				
				node_C.put(e.getKey(), agentSet.toArray()[term].toString());
				cluster[term].put(e.getKey(), e.getValue());				
			}
		}
		
		// Set Cluster for  unvisited nodes
			for (Map.Entry<String, Integer> e : nodes.entrySet()){
					if(node_C.containsKey(e.getKey())==false)
					{
						System.out.println("  ############### Unvisited :::::::::::::        " + e.getKey());
					}
			}
	}
	
	
	// Print all Nodes from each cluster
	void printCluster()
	{
		int i;
		for(i=0;i<No_Cluster;i++)
		{
			System.out.println("\n\n Cluster ["+ (i+1) + "] : Node Set " );
			for (Map.Entry<String, Integer> e : cluster[i].entrySet()) {
			    //to get key
			    System.out.println(e.getKey()+ " --> " + e.getValue());
			}
		}
	}
	
	
	// Print Node_Degree set and Edges set
	void printDegree()
	{
		System.out.println("### Degrees of Nodes ###");
		for (Map.Entry<String, Integer> e : nodes.entrySet()) {
		    //to get key
		    System.out.println(e.getKey()+ " --> " + e.getValue());
		}
		
		System.out.println("### Edges of Graph ###");
		for (Map.Entry<String, List<String>> e : edges.entrySet()) {
		    //to get key
		    System.out.println(e.getKey()+ " --> " + e.getValue());
		}
	}
	
	
	// Print Anonymized Degree and Edge Set
	void printAnonymzation()
	{
		System.out.println("### Degrees of Nodes ###");
		for (Map.Entry<String, Integer> e : anon_nodes.entrySet()) {
		    //to get key
		    System.out.println(e.getKey()+ " --> " + e.getValue());
		}
		
		System.out.println("### Edges of Graph ###");
		for (Map.Entry<String, List<String>> e : anon_edges.entrySet()) {
		    //to get key
		    System.out.println(e.getKey()+ " --> " + e.getValue());
		}
	}
	
	// Function to choose Anonymizations sequence
	void degree_Cluster(int start, int end)
	{
		//List<Integer> DSeq = new ArrayList<Integer>(Sorted_nodes.values());
		
		if((end-start) >= (2*k_factor))
		{
			int i,n,n_next,max = 0,selectedI=0;
			
			i = start + k_factor;
			selectedI = i-1;
						
			while(i<(end-k_factor))
			{
				n =  Integer.parseInt(Sorted_nodes.values().toArray()[i-1].toString());
				n_next =  Integer.parseInt(Sorted_nodes.values().toArray()[i].toString());
								
				if( (n - n_next) > max)
				{
					max = n - n_next;
					selectedI = i-1;
				}
				i++;
			}
			
			i = index_Count;
			
			while(i>=0 && (index_Seq[i]>selectedI) )
			{
				index_Seq[i+1] = index_Seq[i];
				i--;
			}
				
			index_Seq[i+1] = selectedI;
			index_Count++;
				
			degree_Cluster(start, selectedI);
			degree_Cluster(selectedI+1, end);
		}
	}
	
	
	/// Anonymization function
	void anonymizFunction()
	{
		// Call for degree partition
		index_Count = 0;
		index_Seq = new int[1000];
		anon_target_degree = new int[1000];
		index_Seq[0]=-1; 
		
		// make DS Partition
		degree_Cluster(0,No_nodes);
		//index_Seq[++index_Count] = No_nodes-1;
		
		// Calculating target degree for each DS partition
		int i=0,count=0,sum=0,node_index=0,diff,j;
		
	 	for (Map.Entry<String, Integer> e : Sorted_nodes.entrySet()) {				
			if(node_index == (index_Seq[i]+1))
			{
				count = e.getValue();
				anon_target_degree[i] = e.getValue();
				//System.out.println("\n\n\n Anonymized Degree is : " + anon_target_degree[i]);
				i++;
			}
			//System.out.print("    Node : " + e.getKey() + " --->>  Value :  " + e.getValue());
			node_index++;
		}
				
		// find-out vertex for DS+ and DS-
		update_Degree = new int[No_nodes];
		node_index=i=j=count=0;
		String key=null;
		
		//System.out.println("Update Degree Sequence :: ");
		for(Map.Entry<String, Integer> e : Sorted_nodes.entrySet())
		{
			if(node_index==index_Seq[j+1]+1)
				j++;
			update_Degree[node_index] = anon_target_degree[j] - Integer.parseInt(e.getValue().toString());					
			
			//System.out.print(""e.getKey() + " ---> " + update_Degree[node_index]);
			
			node_index++;
		}
		
		
		int node,cluster_no,setInc,pos,flag,k,undone_count=0;
		String[] agent = new String[No_nodes];
		node_index=0;

		// temporary storage for node
		Map<String, Integer> tempMap = new LinkedHashMap<String, Integer>();
		int[] undone = new int[No_nodes];
		List<String> setN1 = new ArrayList<String>();
		String node1,node2;

		
		for(Map.Entry<String, Integer> ef : Sorted_nodes.entrySet())
		{
			flag=0;
			if(update_Degree[node_index]!=0)
			{
				//Find out nodes in same cluster
				//agent = node_C.get(e.getKey());
				node1 = ef.getKey();
				diff = update_Degree[node_index];
				cluster_no = new ArrayList<String>(agent_Nodes.keySet()).indexOf(node_C.get(ef.getKey()));
				setN1 = anon_edges.get(ef.getKey());
				
				j=0;
				
				for(Map.Entry<String, Integer> neighbours  : cluster[cluster_no].entrySet())
				{
					agent[j++] = neighbours.getKey();
				}
				
				// Find out neighbor present in DS+s
				if(diff > 0)
				{
					for(i=0;i<j && diff>0 ;i++)
					{
						pos = new ArrayList<String>(Sorted_nodes.keySet()).indexOf(agent[i]);
						
						if(update_Degree[pos] > 0 && (setN1.contains(agent[i])==false) && (agent[i].equalsIgnoreCase(ef.getKey())==false))
						{						
							node2 = Sorted_nodes.keySet().toArray()[pos].toString();
							
							update_Degree[node_index]--;
							update_Degree[pos]--;
							
							// 	Update Anonymized Node Set													
							anon_nodes.put(node1, anon_nodes.get(node1)+1);
							anon_nodes.put(node2, anon_nodes.get(node2)+1);
							
							// Update Anpnymized Edge Set
							anon_edges.get(node1).add(node2);
							anon_edges.get(node2).add(node1);
							
							diff--;
						}
					}
				}
				
				
				if(diff!=0)
					undone[undone_count++] = node_index;
			}
			node_index++;
		}
		
		
		System.out.println("Remaining Nodes : ");
		for(i=0;i<undone_count;i++)
		{
			if(update_Degree[undone[i]]!=0)
			{
				System.out.println("### Before ::: Remainings Node :  " + Sorted_nodes.keySet().toArray()[undone[i]].toString() + "  -->>> Degree : " + update_Degree[undone[i]] + "  $$$$$ Cluster : " + node_C.get(Sorted_nodes.keySet().toArray()[undone[i]].toString()));
			}
		}

				
		int flag1;
		/// Inter_Cluster Node Connection operations
		for(i=0;i<undone_count;i++)
		{
			if(update_Degree[undone[i]]!=0)
			{
				node1 = Sorted_nodes.keySet().toArray()[undone[i]].toString();
				diff = update_Degree[undone[i]];
				setN1 = anon_edges.get(node1);
				
				for(j=i+1; j<undone_count && diff>0;j++)
				{
					if(update_Degree[undone[j]]!=0)
					{
						node2 = Sorted_nodes.keySet().toArray()[undone[j]].toString();
						
						if(setN1.contains(node2)==false)
						{
							update_Degree[undone[i]]--;
							update_Degree[undone[j]]--;
													
							anon_edges.get(node1).add(node2);
							anon_edges.get(node2).add(node1);
													
							// 	Update Anonymized Node Set
							anon_nodes.put(node1, anon_nodes.get(node1)+1);
							anon_nodes.put(node2, anon_nodes.get(node2)+1);	 diff--;		
							
							//System.out.print("\n\n Updated Node1 : " + node1 + " ***** Degree : " + anon_nodes.get(node1));
							//System.out.print("\nUpdated Node2 : " + node2 + " ***** Degree : " + anon_nodes.get(node2));
						}
					}
				}
			}
		}
		
		int[] remain = new int[No_nodes];	
		int remain_count=0,sp1,sp2;
		flag1=0;
		
		System.out.println("Remaining Nodes : ");
		for(i=0;i<undone_count;i++)
		{
			if(update_Degree[undone[i]]!=0)
			{
				System.out.println("Remainings Node :  " + Sorted_nodes.keySet().toArray()[undone[i]].toString() + "  -->>> Degree : " + update_Degree[undone[i]] + "  $$$$$ Cluster : " + node_C.get(Sorted_nodes.keySet().toArray()[undone[i]].toString()));
				
				/*j=remain_count;
				while(j>0 && remain[j]<update_Degree[undone[i]])
				{	
					remain[j+1] = remain[j];
					j--;
				}
				
				remain[j+1] = undone[i]; flag1=1;
				remain_count++;*/
				remain[remain_count++] = undone[i];
			}
		}
		
		/*
		****************************
		* Nodes only +
		* Remaining Nodes
		****************************
		*/
		
		// Do it man,,,,,,,,, ;)
		node_index = No_nodes-1;
		int[] is_Edge = new int[remain_count];
		//while(flag1==1)
		while(node_index>=0)
		{
			flag1=0;
			node1 = Sorted_nodes.keySet().toArray()[node_index].toString();
			sp1 = Collections.frequency(new ArrayList<Integer>(anon_nodes.values()),anon_nodes.get(node1));
			
			if(sp1 > k_factor)
			{
				setN1 = anon_edges.get(node1);
				diff = 0;
				
				// count possible No. of nodes 
				for(i=0;i<remain_count;i++)
				{
					is_Edge[i] = 0;
					if(update_Degree[remain[i]]!=0 && remain[i]!=node_index)
					{
						flag1=1;
						node2 = Sorted_nodes.keySet().toArray()[remain[i]].toString();
						
						if(setN1.contains(node2)==false)
						{
							diff++;
							is_Edge[i] = 1;
						}
					}
				}
				
				// node found
				if(diff!=0)
				{
					// check for k-anony valid value
					count = anon_nodes.get(node1) + diff ;
					i = index_Count;
					while(i>=0 && count>=anon_target_degree[i])
					{
						i--;
					}
					sum = anon_target_degree[i+2];
										
					sp2 = Collections.frequency(new ArrayList<Integer>(anon_nodes.values()),sum);
					if(sp2 >= k_factor)
					{
						count = anon_nodes.get(node1);
												
						if(node_index%2 == 0)
							i=0;
						else
							i=remain_count-1;
						
						//for(i=0;i<remain_count && count<sum;i++)
						//{
						while(true)
						{						
							if(node_index%2 == 0 && (i>=remain_count || count>=sum))
								break;
							
							if(node_index%2==1 && (i<0 || count>=sum))
								break;
							
							if(is_Edge[i]!=0)
							{
								// add edge
								node2 = Sorted_nodes.keySet().toArray()[remain[i]].toString();
								anon_edges.get(node1).add(node2);
								anon_edges.get(node2).add(node1);
								
								//Update Anonymized Node Set
								anon_nodes.put(node1, anon_nodes.get(node1)+1);
								anon_nodes.put(node2, anon_nodes.get(node2)+1);
								
								update_Degree[remain[i]]--; count++;
								
								//System.out.println("### Relax ###  Node1 : " + node1 + "    Node2 :  " + node2);
							}
							
							if(node_index%2 == 0)
								i++;
							else
								i--;
						}			
					}
				}
			}
			node_index--;
		}
		
		
		
		/*
		 ********************************
		 * Graph refinemnet for un_anonymized nodes.
		 * 
		 ******************************** 
		 
		
		// Make a list of un_anonymized nodes;
		String[] un_nodes = new String[remain_count];
		i=0; count=0;
		for (Map.Entry<String, Integer> e : anon_nodes.entrySet()) {
			sp1 = e.getValue();
			sp2 = Collections.frequency(new ArrayList<Integer>(anon_nodes.values()),sp1);
			
			if(sp2 < (k_factor/2))
			{
				node1 = e.getKey();
				cluster_no = new ArrayList<String>(Sorted_nodes.keySet()).indexOf(node1);
								
				for(j=0;j<index_Count;j++)
				{
					if(index_Seq[j] >= cluster_no)
						break;
				}
				j--;
				
				System.out.println("\n\n Difference : " + (anon_target_degree[j]-sp1));
				System.out.println("Anon_target_Degree : " + anon_target_degree[j]);
				System.out.println("Node Index : " + cluster_no);
				count = count + anon_target_degree[j] - sp1;
				
				un_nodes[i++] = node1;
				System.out.println("??????????   Not k-anonymized ( " + sp2 + ")  : Node : " + e.getKey() + "  -->> " + sp1);
			}
		}
		
		System.out.println("Total Count is : " + count);*/
	}
	
	// check for anonymization
	void anonymizCheck()
	{
		System.out.println("Anonymization Check : ");
		int value,flag;
		for (Map.Entry<String, Integer> e : anon_nodes.entrySet()) {
			value = e.getValue();
			flag = Collections.frequency(new ArrayList<Integer>(anon_nodes.values()),value);
			
			if(flag < k_factor)
			{
				System.out.println("??????????   Not k-anonymized ( " + flag + ")  : Node : " + e.getKey() + "  -->> " + e.getValue());
			}
		}
	}
	
	
	/*
	 *********************
	 * Utility and Privacy Measurement
	 ******************** 
	 */
	// Data utility loss
	void nodeDegreeDiff()
	{
		int nodeDegree,count=0;
		for (Map.Entry<String, Integer> e : anon_nodes.entrySet()) {
			
			nodeDegree = e.getValue();
			count = count + (nodeDegree  - nodes.get(e.getKey()) );
		}
		System.out.println("\n Total Degree Difference : " + count);
	}
	
	void utilityEdges()
	{
		int i,j,cl1,cl2,count,count1;
		String node1,node2;
		List<String> nodeSet = new ArrayList<String>();
		float[][] util1 = new float[No_Cluster][No_Cluster];
		float[][] util2 = new float[No_Cluster][No_Cluster];
		float ans,diff;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(4);
		//System.out.println(df.format(decimalNumber));
		
		for(i=0;i<No_Cluster;i++)
		{
			for(j=0;j<No_Cluster;j++)
			{
				util1[i][j] = util2[i][j] = 0.0F;
			}
		}
		
		
		// count for original edges
		count = 0;
		for (Map.Entry<String, List<String>> e : edges.entrySet()) {
			node1 = e.getKey();
			nodeSet = e.getValue();
			
			cl1 = new ArrayList<String>(agent_Nodes.keySet()).indexOf(node_C.get(node1));
		
			for(i=0;i<nodeSet.size();i++)
			{
				node2 = nodeSet.get(i);
				cl2 = new ArrayList<String>(agent_Nodes.keySet()).indexOf(node_C.get(node2));
				
				if(cl1<=cl2)
					util1[cl1][cl2]++;
				else
					util1[cl2][cl1]++;
			}
			count++;
		}
		

		// count for anonymized edges
		count1 = 0;
		for (Map.Entry<String, List<String>> e : anon_edges.entrySet()) {
			node1 = e.getKey();
			nodeSet = e.getValue();
			
			cl1 = new ArrayList<String>(agent_Nodes.keySet()).indexOf(node_C.get(node1));
		
			for(i=0;i<nodeSet.size();i++)
			{
				node2 = nodeSet.get(i);
				cl2 = new ArrayList<String>(agent_Nodes.keySet()).indexOf(node_C.get(node2));
				
				if(cl1<=cl2)
					util2[cl1][cl2]++;
				else
					util2[cl2][cl1]++;
			}
			count1++;
		}

		
		for(i=0;i<No_Cluster;i++)
		{
			for(j=0;j<No_Cluster;j++)
			{
				util1[i][j] = ((float)util1[i][j]/count);
				util2[i][j] = ((float)util2[i][j]/count);
			}
		}
		
		
		System.out.println("\n\n Original Edge Distribution : ");
		for(i=0;i<No_Cluster;i++)
		{	System.out.println("\n");
			for(j=0;j<No_Cluster;j++)
			{
				System.out.print("  " + util1[i][j]);
			}
		}
		
		
		System.out.println("\n\n Anonymized Edge Distribution : ");
		for(i=0;i<No_Cluster;i++)
		{	System.out.println("\n");
			for(j=0;j<No_Cluster;j++)
			{
				System.out.print("  " + util2[i][j]);
			}
		}
		
		ans = 0.0F;
		// count total utility loss
		for(i=0;i<No_Cluster;i++)
		{
			for(j=i;j<No_Cluster;j++)
			{
				diff  = util1[i][j] - util2[i][j];
				if(diff < 0)
					diff = (-diff);
				
				ans = ans + diff;
			}
		}
		System.out.println("\n\n\n Utility Loss :::::::  " + ans);

	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("nodes.data");
		BufferedReader fr,in;
		String line;
		int node_count=0,k=0;
		// make a cluster object
		// Input value of k
		in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the value of k : ");
		try {
			k = Integer.parseInt(in.readLine());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
				
		//k=2;
		
		cluster C = new cluster();
				
		// Read Nodes
		try
		{
			fr = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while(true)
			{
					line = fr.readLine();
					if(line==null)
		                break;
					C.inNodes(line);
					node_count++;
			}
			fr.close();			
	    }catch(FileNotFoundException e) {
	        e.printStackTrace();
	    }catch(IOException e)
	    {
	        e.printStackTrace();
	    }
		C.setNodeCount(node_count);
		
		if((node_count%k != 0) || (node_count/(2*k)==1))
			C.setClusterCount(k,(int)Math.ceil((float)node_count/(2*k)));
		else
			C.setClusterCount(k,(int)Math.ceil(((float)node_count/(2*k))));
		//C.setClusterCount(k,5);
		
		
		//C.setClusterCount(k,2);
		
		// create edge set
		file = new File("edges.data");
		try {
	        fr = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
	        while(true)
	        {     
	            line = fr.readLine();
	            if(line==null)
	                break;
	            String[] words = line.split(" ");//those are your words
	            C.inEdge(words[0], words[1]);
	        }
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		//C.printDegree();
				
		// select cluster-Agent
		//k = node_count / (2*k-1);
		C.setClusterAgent();
		
		// Create cluster 
		C.createCluster();
		
		//Print Cluster
		//C.printCluster();
	
		// Sort the nodes by values(Degree)
		C.sortByValues();
		//System.out.println("After Sorting :::: ");
		//C.printSortedDegree();
		
		// Call anonymization function and print the updated value
		C.anonymizFunction();
		
		System.out.println("printAnonymzation ");
		//C.printAnonymzation();
		
		C.anonymizCheck();
		
		
		// Utility measurement
		C.nodeDegreeDiff();
		C.utilityEdges();
		
	}
}
