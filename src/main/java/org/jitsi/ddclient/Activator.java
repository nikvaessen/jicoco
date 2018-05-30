/*
 * Copyright @ 2018 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.ddclient;

import net.java.sip.communicator.util.*;
import org.jitsi.service.configuration.*;
import org.jitsi.util.*;
import org.osgi.framework.*;

/**
 * Actives a {@link DataDogStatsClient} as an OSGi service
 *
 * @author Nik Vaessen
 */
public class Activator
    implements BundleActivator
{

    /**
     * The property for the prefix of for the {@link DataDogStatsClient}
     * instance managed by this {@link Activator}.
     */
    public static final String DDCLIENT_PREFIX_PNAME
        = "org.jitsi.ddclient.prefix";

    /**
     * The property for the host of for the {@link DataDogStatsClient}
     * instance managed by this {@link Activator}.
     */
    public static final String DDCLIENT_HOST_PNAME = "org.jitsi.ddclient.host";

    /**
     * The property for the port of for the {@link DataDogStatsClient}
     * instance managed by this {@link Activator}.
     */
    public static final String DDCLIENT_PORT_PNAME = "org.jitsi.ddclient.port";

    /**
     * The default prefix. When this prefix is used, this {@link Activator}
     * will NOT register the client and instead abort
     */
    private static final String DEFAULT_PREFIX = "";

    /**
     * The default hostname of the DataDog server to connect to
     */
    private static final String DEFAULT_HOST = "localhost";

    /**
     * The default port of the DataDog server to connect to
     */
    private static final int DEFAULT_PORT = 8125;

    /**
     * The {@link DataDogStatsClient} managed by this {@link BundleActivator}
     */
    private DataDogStatsClient client;

    /**
     * Registers the DataDogStatsClient
     */
    private ServiceRegistration<DataDogStatsClient> serviceRegistration;

    /**
     * The {@code ConfigurationService} which looks up values of configuration
     * properties.
     */
    protected ConfigurationService cfg;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext context)
        throws Exception
    {
        cfg = ServiceUtils.getService(context, ConfigurationService.class);

        String prefix = ConfigUtils.getString(cfg,
            DDCLIENT_PREFIX_PNAME, DEFAULT_PREFIX);

        if(prefix.isEmpty())
        {
            return;
        }

        String host = ConfigUtils.getString(cfg,
            DDCLIENT_HOST_PNAME, DEFAULT_HOST);
        int port = ConfigUtils.getInt(cfg,
            DDCLIENT_PORT_PNAME, DEFAULT_PORT);

        client = new DataDogStatsClient(prefix, host, port);

        serviceRegistration
            = context.registerService(DataDogStatsClient.class, client, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext context)
        throws Exception
    {
        if (serviceRegistration != null)
        {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }

        if (client != null)
        {
            client.stop();
            client = null;
        }
    }
}
