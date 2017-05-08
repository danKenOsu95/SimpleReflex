import java.io.*;
import java.lang.*;
import java.util.*;


/**
 * MovingRoverSensors: A class for reading visual and sample
 * perceptions from a file
 *
 * @author Dan Kennon
 * MovingRoverSensors provided by Eric Fosler-Lussier
 *
 * allowed stdin version of constructor
 */

public class MovingRoverSensors {
    private SamplePercept[][] samps;
    private VisionPercept[][] vis;
    
    enum direction{NORTH, SOUTH, EAST, WEST}
    enum tiles{CLEAR, BOULDER, NULL}
    enum action{GRAB, GONORTH, GOSOUTH, GOEAST, GOWEST, LOOKNORTH, LOOKSOUTH, LOOKEAST, LOOKWEST, STOP}
    public static int Xval = 1, Yval = 1;
	public static LinkedList<Integer> Samples = new LinkedList<Integer>();
	public static LinkedList<Integer> Xlist = new LinkedList<Integer>();
	public static int TotalMoves = 0;
	public static boolean Stuck = false;
	public static int StuckCount = 0;
	private static class State {
		public int x;				
		public int y;				
		public direction next;	
		public int seen;

		public void initState(int xval, int yval, direction next){
			x = xval;
			y = yval;
			direction direction = next;
			seen = 0;
		}
	}
    

    /**
     * Creates Sensors object from file
     * @param filename The file that data is read from
     */
    public MovingRoverSensors(String filename) {
	BufferedReader myFile=null;

	try {
	    myFile=new BufferedReader(new FileReader(filename));
	} catch (Exception e) {
	    System.err.println("Ooops!  I can't seem to load the file \""+filename+"\", do you have the file in the correct place?");
	    System.exit(1);
	}
	initialize(myFile);
    }

    /**
     * Creates Sensors object from standard input
     */
    public MovingRoverSensors() {
	BufferedReader myFile=null;

	try {
	    myFile=new BufferedReader(new InputStreamReader(System.in));
	} catch (Exception e) {
	    System.err.println("Ooops!  I can't seem to read the file from the standard input!");
	    System.exit(1);
	}
	initialize(myFile);
    }

    private void initialize(BufferedReader myFile) {

	int counter=0;
	LinkedList sp1=new LinkedList();
	LinkedList sp2=new LinkedList();
	LinkedList vp1=new LinkedList();
	LinkedList vp2=new LinkedList();
	String line;

	try {
	    while((line=myFile.readLine())!=null) {
		counter++;
		StringTokenizer st=new StringTokenizer(line,",");
		int row=Integer.parseInt(st.nextToken());
		
		if (row!=counter) {
		    throw new Exception("Malformatted file");
		}
		while(st.hasMoreTokens()) {
		    if (counter==1) {
			// next token: vision
			vp1.add(new VisionPercept(st.nextToken()));
			// next token: sample
			sp1.add(new SamplePercept(st.nextToken()));
		    } else if (counter==2) {
			// next token: vision
			vp2.add(new VisionPercept(st.nextToken()));
			// next token: sample
			sp2.add(new SamplePercept(st.nextToken()));
		    }			
		}
	    }
	} catch (Exception e) {
	    System.err.println("Ooops!  I had some problems reading in the file.");
	    System.exit(1);
	}

	try {
	    // now allocate array space for fast lookup
	    // this isn't really necessary, but it makes the lookup code
	    // very clean

	    Object[] x1=sp1.toArray();
	    Object[] x2=sp2.toArray();
	    Object[] x3=vp1.toArray();
	    Object[] x4=vp2.toArray();
	    samps=new SamplePercept[x1.length][2];
	    vis=new VisionPercept[x1.length][2];
	    
	    int i;
	    for(i=0;i<x1.length;i++) {
		samps[i][0]=(SamplePercept) x1[i];
		samps[i][1]=(SamplePercept) x2[i];
		vis[i][0]=(VisionPercept) x3[i];
		vis[i][1]=(VisionPercept) x4[i];
	    }
	    
	} catch (Exception e) {
	    System.err.println("Ooops!  I had some problems reading in the file.");
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    /**
     * Gets a sample perception at <x,y>
     * @param x The x coordinate
     * @param y The y coordinate
     * @return SamplePercept A SamplePercept object containing the percept
     */
    public SamplePercept getSamplePercept(int x,int y) {
	try {
	    return samps[x-1][y-1];
	    
	} catch (Exception e) {
	}
	return null;
    }
    
    /**
     * Gets a vision perception at <x,y>
     * @param x The x coordinate
     * @param y The y coordinate
     * @return VisionPercept A VisionPercept object containing the percept
     */
    public VisionPercept getVisionPercept(int x,int y) {
	try {
	    return vis[x-1][y-1];
	    
	} catch (Exception e) {
	}
	return null;
    }
    
    public static VisionPercept getNextPercept(MovingRoverSensors mrs, VisionPercept vp){
    	VisionPercept toReturn;
    	if(vp.direction == direction.NORTH){
    		toReturn = mrs.getVisionPercept(Xval, Yval + 1);
    	}else if(vp.direction == direction.SOUTH){
    		toReturn = mrs.getVisionPercept(Xval, Yval - 1);
    	}else if(vp.direction == direction.EAST){
    		toReturn = mrs.getVisionPercept(Xval + 1, Yval);
    	}else{
    		toReturn = mrs.getVisionPercept(Xval -1, Yval);
    	}

    	return toReturn;
    }
    
    static private tiles getTileState(VisionPercept vp){
        if(vp == null){
            return tiles.NULL;
        }else if(vp.isClear()){
            return tiles.CLEAR;
        }else{
            return tiles.BOULDER;
        }
    }
    
    public static action reflexAgentWithState(VisionPercept cVP, VisionPercept nVP, SamplePercept sp){
        action actionTaken;
    	if (!Samples.contains(sp.value())){
    		actionTaken = action.GRAB;
    	} else {
    		tiles state = getTileState(nVP);
    		if (state.equals(tiles.CLEAR)){
    			if (Xlist.getLast() - 1 == Xlist.getLast() || Xlist.size() == 1){
    				if (cVP.direction == direction.EAST){
    					actionTaken = action.GOEAST;
    				} else {
    					actionTaken = action.LOOKEAST;
    				}
    			} else {
    				if (cVP.row == 2){
    					if (cVP.direction == direction.SOUTH){
    						actionTaken = action.GOSOUTH;
    						cVP.seen++;
    					} else {
    						actionTaken = action.LOOKSOUTH;
    					}
    				} else if (cVP.row == 1) {
    					if (cVP.direction == direction.NORTH){
    						actionTaken = action.GONORTH;
    						cVP.seen++;
    					} else {
    						actionTaken = action.LOOKNORTH;
    					}
    				} else {
    					actionTaken = action.LOOKEAST;
    				}
    			}
    		} else if (getTileState(nVP).equals(tiles.BOULDER)) {
    			if (cVP.row == 2){
    				if(cVP.direction == direction.EAST){
    					actionTaken = action.LOOKSOUTH;
    				} else {
    					actionTaken = action.LOOKEAST;
    					StuckCount++;
    				}
    				
    			} else {
    				if(cVP.direction == direction.EAST){
    					actionTaken = action.LOOKNORTH;
    				} else {
    					actionTaken = action.LOOKEAST;
    					StuckCount++;
    				}
    			}
    		} else {
    			if (cVP.row == 2){
    				if(cVP.direction == direction.EAST){
    					actionTaken = action.LOOKSOUTH;
    				} else {
    					actionTaken = action.LOOKEAST;
    				}
    			} else {
    				if(cVP.direction == direction.EAST){
    					actionTaken = action.LOOKNORTH;
    				} else {
    					actionTaken = action.LOOKEAST;
    				}
    			}
    		}
    	}
    	if (StuckCount > 2){
    		actionTaken = action.STOP;
    	}
    	return actionTaken;
    }
	
    /**
     * Run a test of the reading routines, prints out all percepts of the file
     *
     * Usage: java MovingRoverSensors -file <filename>
     */
    public static void main(String args[]) {
	if (args.length!=0 && 
	    (args.length != 2 || (! args[0].equals("-file")))) {
	    System.err.println("Usage: MovingRoverSensors -file <filename>");
	    System.exit(1);
	}	
	MovingRoverSensors mrs=null;
	SamplePercept sp; 
	VisionPercept currentVP;
	VisionPercept nextVP;
	action act = action.GRAB;
	boolean finished = false;
	if (args.length==0) {
	    mrs=new MovingRoverSensors();
	} else {
	    mrs=new MovingRoverSensors(args[1]);
	}
	sp = mrs.getSamplePercept(Xval, Yval);
	currentVP = mrs.getVisionPercept(Xval, Yval);
	currentVP.column = 1;
	currentVP.row = 1;
	currentVP.direction = direction.NORTH;
	currentVP.seen = 0;
	nextVP = getNextPercept(mrs, currentVP);
	Xlist.add(Xval);
	while(!finished){
		act = reflexAgentWithState(currentVP, nextVP, sp);
		nextVP = getNextPercept(mrs, currentVP);
		sp = mrs.getSamplePercept(Xval, Yval);
		switch (act){
		case GONORTH: {
			currentVP.row++;
			TotalMoves++;
			Yval++;
			if (nextVP != null){
			nextVP.column = Xval;
			nextVP.row = Yval;
			nextVP.direction = direction.EAST;
			}
			Xlist.add(Xval);
			break;
		}
		
		case GOSOUTH: {
			TotalMoves++;
			currentVP.row--;
			Yval--;
			if (nextVP != null){
			nextVP.column = Xval;
			nextVP.row = Yval;
			nextVP.direction = direction.EAST;
			}
			Xlist.add(Xval);
			break;
		}
		
		case GOEAST: {
			TotalMoves++;
			currentVP.column++;
			Xval++;
			if (nextVP != null){
			nextVP.column = Xval;
			nextVP.row = Yval;
			nextVP.direction = direction.EAST;
			}
			Xlist.add(Xval);
			break;
		}
		
		case GOWEST: {
			TotalMoves++;
			currentVP.column--;
			Xval--;
			if (nextVP != null){
			nextVP.column = Xval;
			nextVP.row = Yval;
			nextVP.direction = direction.EAST;
			}
			Xlist.add(Xval);
			break;
		}
		case LOOKNORTH: {
			if (nextVP != null){
			nextVP.direction = direction.NORTH;
			nextVP.column = Xval;
			nextVP.row = Yval+1;
			}
			currentVP.direction = direction.NORTH;
			TotalMoves++;
			break;
		}
		
		case LOOKSOUTH: {
			if (nextVP != null){
			nextVP.direction = direction.SOUTH;
			nextVP.column = Xval;
			nextVP.row = Yval - 1;
			}
			currentVP.direction = direction.SOUTH;
			break;
		}
		
		case LOOKEAST: {
			if (nextVP != null){
			nextVP.direction = direction.EAST;
			nextVP.column = Xval + 1;
			nextVP.row = Yval;
			}
			currentVP.direction = direction.EAST;
			TotalMoves++;
			break;
		}
		
		case LOOKWEST: {
			if (nextVP != null){
			nextVP.direction = direction.WEST;
			nextVP.column = Xval - 1;
			nextVP.row = Yval;
			}
			currentVP.direction = direction.WEST;
			break;
		}
		
		case GRAB: {
			Samples.add(mrs.getSamplePercept(Xval, Yval).value());
			break;
		}
		
		default: {
			finished = true;
			break;
		}
		}
		
		nextVP = getNextPercept(mrs, currentVP);
		sp = mrs.getSamplePercept(Xval, Yval);
		System.out.println("Position: <" + Xval + "," + Yval +"> Looking: " + currentVP.direction + " Perceived: <" +  sp.value() + "," + getTileState(nextVP) + "> Action: " + act);
	}
	System.out.println("Total Compounds Collected: " + Samples.size() + " Total Moves: " + TotalMoves);
	
	
	}

}
