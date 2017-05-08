
    }

    /**
     * Gets the next sample perception
     * @return SamplePercept A SamplePercept object containing the percept
     */

    public SamplePercept getPercept() {
	String line;

	try {
	    line=myFile.readLine();
	    if (myFile==null) {
		return null;
	    } else if (line==null) {
		try {
		    myFile.close();
		} catch (Exception e) {
		}

		myFile=null;
		return null;
	    } else {
		return new SamplePercept(line);
	    }
	} catch (Exception e) {
	    System.err.println("Ooops!  I seem to have gotten an i/o error reading the file.");
	    System.exit(1);
	}
	return null;
    }
    
    public static char simpleReflexAgent (int val){
    	char action;
    	if (val % 5 == 0){
			action = 'G';
		} else {
			action = 'N';
		}
    	return action;
    }

    /**
     * Run a test of the reading routines, prints out all percepts of the file
     *
     * Usage: java RoverSampleSensor -file <filename>
     */
    
    public static void main(String args[]) {
    	if (args.length!= 0 && 
    			(args.length != 2 ||   (! args[0].equals("-file")))) {
    		System.err.println("Usage: RoverSampleSensor -file <filename>");
    		System.exit(1);
    	}
    	RoverSampleSensor rss=null;
    	SamplePercept sp;
    	if (args.length==0) {
    	    rss=new RoverSampleSensor();
    	} else {
    	    rss=new RoverSampleSensor(args[1]);
    	}

    	//rss=new RoverSampleSensor("data/hw1-data1.txt");
    	while((sp=rss.getPercept())!=null) {
    		System.out.println("Perceived: "+sp.value() + " Action: "  + simpleReflexAgent(sp.value()));
    		
    	}
    }

}
