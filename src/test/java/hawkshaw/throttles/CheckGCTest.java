package hawkshaw.throttles;

import hawkshaw.drivers.GcChurner;
import hawkshaw.drivers.SimpleMain;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CheckGCTest {

    @SuppressWarnings("static-method")
    @Test
    public void checkManagedCacheAllocationCausesGC() {
        GarbageCollectorMXBean bean = getGCMBean();
        long beforeCount = bean.getCollectionCount();
        SimpleMain.run(1000000);
        long afterCount = bean.getCollectionCount();
        Assert.assertTrue(afterCount > beforeCount);
    }

    @SuppressWarnings("static-method")
    @Test
    public void checkDualThreadedManagedCacheAllocationCausesGC() {
        GarbageCollectorMXBean bean = getGCMBean();
        long beforeCount = bean.getCollectionCount();
        GcChurner.run(40);
        long afterCount = bean.getCollectionCount();
        Assert.assertTrue(afterCount > beforeCount);
    }

    private static GarbageCollectorMXBean getGCMBean() {
        try {
            List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
            for (GarbageCollectorMXBean bean:beans) {
                if (bean.getName().toLowerCase().contains("marksweep")) {
                    return bean;
                }
            }
            throw new Exception();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }
}
