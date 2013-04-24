package hawkshaw.drivers;

import hawkshaw.NThreadedManagedCache;
import hawkshaw.throttles.BurstyThrottle;
import hawkshaw.throttles.GammaDistThrottle;
import hawkshaw.throttles.NeverThrottle;
import hawkshaw.throttles.Throttle;
import hawkshaw.throttles.WhiteThrottle;

import java.util.ArrayList;
import java.util.List;

public class HighGcOccupancy {

	private static final int TENURED_SIZE = 1 * 1024;

	private static final byte[] leakedData = new byte[TENURED_SIZE];
	private static final List<NThreadedManagedCache> managers = new ArrayList<NThreadedManagedCache>();

	private static void run() throws InterruptedException {

		// Remove at a constant rate
		Throttle deleteAt = new WhiteThrottle(12344, 1, 10);
		Throttle produceAt = GammaDistThrottle.of(12567, 2.0, 2.0);

		/*
		NThreadedManagedCache manager = new NThreadedManagedCache(		deleteAt,
																		produceAt, 
																		new WhiteThrottle(12567, 500, 1500, 1),
			
																		4);
																		*/
		//manager.startAllocation();
		managers.add( new NThreadedManagedCache( new BurstyThrottle(213, 100, 1000), 
												 new WhiteThrottle(123,100, 1000), 
												 new WhiteThrottle(333, 512 * 1024, 2024 * 1024, 1),
												 2));



		managers.add( new NThreadedManagedCache( new BurstyThrottle(12344, 100, 1000), 
																	new WhiteThrottle(12567,100, 1000), 
																	new WhiteThrottle(12567,  1024 * 1024, 2 * 1024 * 1024, 1),
																	2));
		
		

		managers.add( new NThreadedManagedCache( new WhiteThrottle(32113, 10000, 50000),  
												 new WhiteThrottle(213, 10000, 15000), 
												 new WhiteThrottle(333, 30.0 * 1024.0 * 1024.0, 40.0 * 1024.0 * 1024.0, 1),
												 1));
		
		for(NThreadedManagedCache manager : managers) {
			manager.startAllocation();
		}


		Thread.sleep(Long.MAX_VALUE);
		int x = leakedData.length;

	}

	public static void main(String[] args) throws InterruptedException {
		run();
	}

	private HighGcOccupancy() {
	}

}
