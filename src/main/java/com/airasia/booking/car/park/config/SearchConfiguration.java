package com.airasia.booking.car.park.config;

import com.airasia.booking.car.park.utils.Constant;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



import java.net.InetSocketAddress;


@Configuration
public class SearchConfiguration {

    @Value("${elasticsearch.recreate.index}")
    private boolean isRecreate;

    @Value("${airasia.carslot.booking.template}")
    private String carParkBookingTemplate;

    @Value("${airasia.carslot.booking.index.mapping}")
    public String carParkBookingIndexMapping;

    @Value("${elasticsearch.host}")
    public String esHost;

    @Value("${elasticsearch.port}")
    public int esPort;

    @Bean
    public TransportClient securedClient() throws Exception {
        Settings settings = Settings.builder().build();
        TransportClient transportClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(esHost, esPort)));
        IndicesAdminClient indices = transportClient.admin().indices();
        boolean exists = indices.prepareExists(Constant.CAR_SLOT_INDEX).execute().actionGet().isExists();
        if (exists) {
            indices.prepareDelete(Constant.CAR_SLOT_INDEX).execute().actionGet();
            indices.prepareCreate(Constant.CAR_SLOT_INDEX).execute().actionGet();
        }
        return transportClient;
    }
}
