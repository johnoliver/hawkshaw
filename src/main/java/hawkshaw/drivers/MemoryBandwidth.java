package hawkshaw.drivers;


/**
 * 
 * -XX:+PrintGCDetails -Xloggc:gc.log -Xmx6G
 */
public class MemoryBandwidth {

	@SuppressWarnings("unused")
    private static volatile byte[] foo;

	private static void run() throws InterruptedException {

		while (true) {
			foo = new byte[1024];
		}
	}

	public static void main(String[] args) throws InterruptedException {
		run();
	}

	private MemoryBandwidth() {
	}

}
