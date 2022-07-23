package com.playgrond.lambdas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

// article: https://medium.com/pragmatic-programmers/refactoring-to-functional-style-in-java-8-from-legacy-to-lambdas-3e36f2911190

class YahooFinance {

    private static final String YAHOO_FINANCE_URL = "http://ichart.finance.yahoo.com/table.csv?s=";
    private static final String REGEX = ",";

    public static double getPrice(final String ticker) {
        try {
            final URL url = new URL(YAHOO_FINANCE_URL + ticker);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                final String[] dataItems = reader.lines()
                    .collect(toList())
                    .get(1)
                    .split(REGEX);
                return Double.parseDouble(dataItems[dataItems.length - 1]);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}

public class Ex1 {
    public static String getPriceFor(String ticker) {
        return ticker + " : " + YahooFinance.getPrice(ticker);
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                System.out.println("Hello from another thread");
            }
        });
        thread1.start();
        System.out.println("Hello from main");

        Thread thread2 = new Thread(() -> System.out.println("Hello from another thread"));
        thread2.start();
        System.out.println("Hello from main");

        try {
            List<String> tickers = Arrays.asList("GOOG", "AMZN", "AAPL", "MSFT", "INTC", "ORCL");
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            List<Future<String>> pricesFutures = new ArrayList<>();
            for (String ticker : tickers) {
                pricesFutures.add(executorService.submit(new Callable<String>() {
                    public String call() {
                        return getPriceFor(ticker);
                    }
                }));
            }
            for (Future<String> priceFuture : pricesFutures) {
                System.out.println(priceFuture.get());
            }
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            List<String> tickers = Arrays.asList("GOOG", "AMZN", "AAPL", "MSFT", "INTC", "ORCL");
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            List<Future<String>> pricesFutures = new ArrayList<>();
            for (String ticker : tickers) {
                pricesFutures.add(executorService.submit(() -> getPriceFor(ticker)));
            }
            for (Future<String> priceFuture : pricesFutures) {
                System.out.println(priceFuture.get());
            }
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            List<String> tickers = Arrays.asList("GOOG", "AMZN", "AAPL", "MSFT", "INTC", "ORCL");
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            List<Future<String>> pricesFutures = new ArrayList<>();
            tickers.stream()
                .map(ticker -> executorService.submit(() -> getPriceFor(ticker)))
                .collect(toList());
            for (Future<String> priceFuture : pricesFutures) {
                System.out.println(priceFuture.get());
            }
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


    }
}
