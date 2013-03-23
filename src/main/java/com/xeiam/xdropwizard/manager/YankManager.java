/**
 * Copyright (C) 2012 Xeiam  http://xeiam.com
 *
 * ***IMPORTANT*** THIS CODE IS PROPRIETARY!!! 
 */
package com.xeiam.xdropwizard.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xeiam.xdropwizard.HelloServiceConfiguration.YankConfiguration;
import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;
import com.yammer.dropwizard.lifecycle.Managed;

/**
 * <p>
 * This is where Yank is bound to the main DropWizard thread.
 * </p>
 * 
 * @author timmolter
 */
public class YankManager implements Managed {

  private final Logger logger = LoggerFactory.getLogger(YankManager.class);

  private YankConfiguration yankConfiguration;

  /**
   * Constructor
   */
  public YankManager(YankConfiguration yankConfiguration) {

    this.yankConfiguration = yankConfiguration;
  }

  @Override
  public void start() throws Exception {

    logger.info("initializing Yank...");

    if (yankConfiguration.getSqlPropsFileName() == null || yankConfiguration.getSqlPropsFileName().trim().length() < 1) {
      DBConnectionManager.INSTANCE.init(PropertiesUtils.getPropertiesFromClasspath(yankConfiguration.getDbPropsFileName()));
    } else {
      DBConnectionManager.INSTANCE.init(PropertiesUtils.getPropertiesFromClasspath(yankConfiguration.getDbPropsFileName()), PropertiesUtils.getPropertiesFromClasspath(yankConfiguration
          .getSqlPropsFileName()));
    }

    logger.info("Yank started successfully.");
  }

  @Override
  public void stop() throws Exception {

    logger.info("shutting down Yank...");

    DBConnectionManager.INSTANCE.release();

    logger.info("Yank shutdown successfully.");
  }

}