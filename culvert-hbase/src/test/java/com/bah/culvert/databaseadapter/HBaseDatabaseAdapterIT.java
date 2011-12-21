/**
 * Copyright 2011 Booz Allen Hamilton.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Booz Allen Hamilton licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bah.culvert.databaseadapter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.coprocessor.CoprocessorHost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.bah.culvert.DatabaseAdapterTestingUtility;
import com.bah.culvert.adapter.DatabaseAdapter;
import com.bah.culvert.databaseadapter.HBaseDatabaseAdapter;
import com.bah.culvert.tableadapters.HBaseCulvertCoprocessorEndpoint;
import com.bah.culvert.utils.HbaseTestProperties;

/**
 * Integration tests for the HBase table adapter.
 */
@RunWith(JUnit4.class)
public class HBaseDatabaseAdapterIT {

  private final static HBaseTestingUtility HBASE_TEST_UTIL = new HBaseTestingUtility();
  private final static Configuration CONF = HBASE_TEST_UTIL.getConfiguration();

  /**
   * Creates a utility and adapter for the test class
   * 
   * @throws Throwable
   */
  @BeforeClass
  public static void setup() throws Throwable { 
    HbaseTestProperties.addStandardHBaseProperties(CONF);
    CONF.set(CoprocessorHost.REGION_COPROCESSOR_CONF_KEY,
    HBaseCulvertCoprocessorEndpoint.class.getName());
    HBASE_TEST_UTIL.startMiniCluster(2);
    HBASE_TEST_UTIL.getMiniHBaseCluster();
  }

  @Test
  public void testDatabase() throws Throwable {
    DatabaseAdapter db = new HBaseDatabaseAdapter();
    db.setConf(CONF);
    DatabaseAdapterTestingUtility.testDatabaseAdapter(db);
  }
  
  /**
   * Tear down the cluster after the test
   * 
   * @throws Throwable
   */
  @AfterClass
  public static void tearDown() throws Throwable {
    HBASE_TEST_UTIL.shutdownMiniCluster();
  }
}
