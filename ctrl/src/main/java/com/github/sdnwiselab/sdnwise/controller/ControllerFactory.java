/*
 * Copyright (C) 2015 SDN-WISE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.controller;

import com.github.sdnwiselab.sdnwise.adapter.AbstractAdapter;
import com.github.sdnwiselab.sdnwise.adapter.AdapterTcp;
import com.github.sdnwiselab.sdnwise.adapter.AdapterUdp;
import com.github.sdnwiselab.sdnwise.configuration.ConfigController;
import com.github.sdnwiselab.sdnwise.configuration.Configurator;
import com.github.sdnwiselab.sdnwise.topology.NetworkGraph;
import com.github.sdnwiselab.sdnwise.topology.VisualNetworkGraph;
import java.net.InetSocketAddress;

/**
 * Builder of AbstractController objects given the specifications contained in a
 * Configurator object. In the current version the only possible lower adapter
 * is an AdapterUdp while the only possible algorithm is Dijkstra
 *
 * @author Sebastiano Milardo
 */
public class ControllerFactory {

    private static InetSocketAddress newId = null;

    /**
     * Returns the corresponding AbstractController object given a Configurator
     * object.
     *
     * @param config a Configurator object.
     * @return an AbstractController object.
     */
    public final AbstractController getController(final Configurator config) {
        AbstractAdapter adapt = getLower(config.getController());
        NetworkGraph ng = getNetworkGraph(config.getController());
        return getControllerType(config.getController(), newId, adapt, ng);
    }

    public AbstractController getControllerType(ConfigController conf, InetSocketAddress newId, AbstractAdapter adapt, NetworkGraph ng) {
        String type = conf.getAlgorithm().get("TYPE");

        switch (type) {
            case "DIJKSTRA":
                return new ControllerDijkstra(newId, adapt, ng);
            default:
                throw new UnsupportedOperationException(
                        "Error in Configuration file");
        }
    }

    public AbstractAdapter getLower(ConfigController conf) {

        String type = conf.getLower().get("TYPE");
        switch (type) {
            case "TCP":
                newId = new InetSocketAddress(conf.getLower().get("IP"),
                        Integer.parseInt(conf.getLower().get("PORT")));
                return new AdapterTcp(conf.getLower());
            case "UDP":
                newId = new InetSocketAddress(conf.getLower().get("OUT_IP"),
                        Integer.parseInt(conf.getLower().get("IN_PORT")));
                return new AdapterUdp(conf.getLower());
            default:
                throw new UnsupportedOperationException("Error in config file");
        }
    }

    public NetworkGraph getNetworkGraph(ConfigController conf) {
        String graph = conf.getMap().get("GRAPH");
        int timeout = Integer.parseInt(conf.getMap().get("TIMEOUT"));
        int rssiResolution = Integer.parseInt(conf.getMap().get("RSSI_RESOLUTION"));

        switch (graph) {
            case "GUI":
                return new VisualNetworkGraph(timeout, rssiResolution);
            case "CLI":
                return new NetworkGraph(timeout, rssiResolution);
            default:
                throw new UnsupportedOperationException("Error in Configuration file");
        }
    }

}
