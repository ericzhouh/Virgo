package com.winterfarmer.virgo.vehicle;

import com.winterfarmer.virgo.vehicle.dao.VehicleDao;
import com.winterfarmer.virgo.vehicle.dao.VehicleMysqlDaoImpl;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;

/**
 * Created by yangtianhang on 15/4/19.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/virgo-test-vehicle-context.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class DaoInit {
    @Resource(name = "vehicleMysqlDao")
    VehicleDao vehicleDao;

    @Test
    public void init() {
        ((VehicleMysqlDaoImpl) vehicleDao).initTable(true);
    }
}
