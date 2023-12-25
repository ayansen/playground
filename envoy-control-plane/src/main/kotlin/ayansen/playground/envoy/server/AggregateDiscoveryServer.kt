


class AggregateDiscoveryServer {
    companion object {
        private const val GROUP = "key"
    }



    fun start() {
        val cache: SimpleCache<String> = SimpleCache<Any, Any>(Int({ node -> GROUP }))
        cache.setSnapshot(
            GROUP,
            Snapshot.create(
                ImmutableList.of(
                    TestResources.createCluster(
                        "cluster0", "127.0.0.1", 1234, Cluster.DiscoveryType.STATIC
                    )
                ),
                .of(),
                ImmutableList.of(),
                ImmutableList.of(),
                ImmutableList.of(),
                "1"
            )
        )
        val v3DiscoveryServer = V3DiscoveryServer(cache)
        val builder: ServerBuilder = NettyServerBuilder.forPort(12345)
            .addService(v3DiscoveryServer.getAggregatedDiscoveryServiceImpl())
            .addService(v3DiscoveryServer.getClusterDiscoveryServiceImpl())
            .addService(v3DiscoveryServer.getEndpointDiscoveryServiceImpl())
            .addService(v3DiscoveryServer.getListenerDiscoveryServiceImpl())
            .addService(v3DiscoveryServer.getRouteDiscoveryServiceImpl())
        val server: Server = builder.build()
        server.start()
        System.out.println("Server has started on port " + server.getPort())
        Runtime.getRuntime().addShutdownHook(Thread(server::shutdown))
        Thread.sleep(10000)
        cache.setSnapshot(
            GROUP,
            Snapshot.create(
                ImmutableList.of(
                    TestResources.createCluster(
                        "cluster1", "127.0.0.1", 1235, Cluster.DiscoveryType.STATIC
                    )
                ),
                ImmutableList.of(),
                ImmutableList.of(),
                ImmutableList.of(),
                ImmutableList.of(),
                "1"
            )
        )
        server.awaitTermination()
    }
}