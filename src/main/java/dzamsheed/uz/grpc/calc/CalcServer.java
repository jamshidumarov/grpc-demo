package dzamsheed.uz.grpc.calc;

import dzamsheed.uz.grpc.CalcRequest;
import dzamsheed.uz.grpc.CalcResponse;
import dzamsheed.uz.grpc.CalculatorGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class CalcServer {
    private Server server;

    private void start() throws IOException {
        int port = 50055;
        server = ServerBuilder
                .forPort(port)
                .addService(new CalculatorImpl())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(CalcServer.this::stop));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class CalculatorImpl extends CalculatorGrpc.CalculatorImplBase {
        @Override
        public void calculate(CalcRequest request, StreamObserver<CalcResponse> responseObserver) {
            double result = switch (request.getOperator()) {
                case "+":
                    yield request.getFirstNumber() + request.getSecondNumber();
                case "-":
                    yield request.getFirstNumber() - request.getSecondNumber();
                case "*":
                    yield (double) (request.getFirstNumber() * request.getSecondNumber());
                case "/":
                    yield (double) (request.getFirstNumber() / request.getSecondNumber());
                case "%":
                    yield (double) (request.getFirstNumber() % request.getSecondNumber());
                default:
                    yield 0;
            };
            responseObserver.onNext(CalcResponse.newBuilder().setResult(result).build());
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        CalcServer calcServer = new CalcServer();
        calcServer.start();
        calcServer.blockUntilShutdown();

    }
}
