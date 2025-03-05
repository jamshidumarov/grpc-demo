package dzamsheed.uz.grpc.calc;

import dzamsheed.uz.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;

public class CalcClient {
    private final ManagedChannel channel;
    private final CalculatorGrpc.CalculatorBlockingStub blockingStub;

    public CalcClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = CalculatorGrpc.newBlockingStub(channel);
    }

    public void calc(int first, String operator, int second) {
        CalcRequest request = CalcRequest.newBuilder()
                .setFirstNumber(first)
                .setSecondNumber(second)
                .setOperator(operator)
                .build();
        CalcResponse response;
        try {
            response = blockingStub.calculate(request);
            System.out.println("Response: " + response.getResult());
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        CalcClient client = new CalcClient("localhost", 50055);
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        while (!name.equals("stop")) {
            String[] split = name.split(" ");
            int first = Integer.parseInt(split[0]);
            int second = Integer.parseInt(split[2]);
            try {
                client.calc(first, split[1], second);
            } finally {
                client.shutdown();
            }
            name = scanner.nextLine();
        }
    }
}
