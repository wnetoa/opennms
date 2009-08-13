/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2009 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 * OpenNMS Licensing       <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 */
package org.opennms.sms.gateways.internal;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.opennms.sms.reflector.smsservice.GatewayGroup;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator
 *
 * @author brozow
 */
public class Activator implements BundleActivator {
    
    private static Logger log = LoggerFactory.getLogger(Activator.class);
    
    private final List<ServiceRegistration> m_registrations = new ArrayList<ServiceRegistration>();

    public void start(BundleContext context) throws Exception {
        
        String configURLString = context.getProperty("org.opennms.sms.gateway.configURL");
        
        URL configURL = configURLString != null ? new URL(configURLString) : context.getBundle().getEntry("/config/modemConfig.properties");
        
        log.info("Using modem configuration from " + configURL);
        
        log.info("Modem System property is [" + System.getProperty("org.opennms.sms.gateways.modems") + "]");
        log.info("Modem Bundle property is [" + context.getProperty("org.opennms.sms.gateways.modems") + "]");

        GatewayGroupFactory factory = new GatewayGroupFactory(configURL);
        
        GatewayGroup[] groups = factory.getGatewayGroups();
        
        for(GatewayGroup group : groups) {
            m_registrations.add(context.registerService(GatewayGroup.class.getName(), group, null));
        }
        
        
    }

    public void stop(BundleContext context) throws Exception {
        for(ServiceRegistration registration : m_registrations) {
            registration.unregister();
        }
    }

}
