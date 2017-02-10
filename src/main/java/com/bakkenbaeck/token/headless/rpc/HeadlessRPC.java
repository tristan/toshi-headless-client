package com.bakkenbaeck.token.headless.rpc;

import com.bakkenbaeck.token.headless.EthService;
import com.bakkenbaeck.token.headless.rpc.entities.*;
import com.bakkenbaeck.token.model.network.SentTransaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

public class HeadlessRPC {
    private JedisPool jedisPool;
    private String address;
    private EthService ethService;

    public HeadlessRPC(JedisPool jedisPool, String address, EthService ethService) {
        this.jedisPool = jedisPool;
        this.address = address;
        this.ethService = ethService;
    }

    public void handleRequest(HeadlessRPCRequest request) {
        if (request != null) {
            try {
                switch (request.getMethod()) {
                    case "ping":
                        ping(request);
                        break;
                    case "sendTransaction":
                        sendTransaction(request);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleResult(HeadlessRPCRequest request, HeadlessRPCResult result) throws IOException {
        HeadlessRPCResponse response = new HeadlessRPCResponse();
        response.setId(request.getId());
        response.setResult(result);
        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(response);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(address + "_rpc_response", message);
        }
    }

    private void ping(HeadlessRPCRequest request) throws IOException {
        handleResult(request, new PingResult("Java client pong!"));
    }

    private void sendTransaction(HeadlessRPCRequest request) throws IOException {
        SentTransaction tx = ethService.sendEth(request.getParams().get("to"), request.getParams().get("value"));
        if (tx != null) {
            handleResult(request, new SendTransactionResult((tx.getTxHash())));
        } else {

        }

    }

}