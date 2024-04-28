import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class conversorDeMonedas {

        private static final String API_KEY = "a23ab25e79b8579d64e34d0a";
        private static final String BASE_CURRENCY = "COP";

        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            boolean salir = false;

            System.out.println("""
                    Bienvenidos
                                        
                    Si tiene alguna duda frente a la definición de la moneda siga el siguiente mensaje:
                                        
                    USD: Dólar estadounidense
                    EUR: Euro
                    JPY: Yen japones
                    ARS: Peso argentino
                    BOB: Boliviano boliviano
                    BRL: Real brasileño
                    CLP: Peso chileno
                    COP: Peso colombiano
                                        
                    """);

            while (!salir) {
                boolean validInput = false;
                double amount = 0.0;
                String sourceCurrency = "";
                String targetCurrency = "";

                while (!validInput) {
                    try {
                        System.out.print("Ingresa la cantidad: ");
                        amount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline character

                        System.out.print("Introduzca la moneda de origen (ej., USD, EUR, JPY, ARS, BOB, BRL, CLP, COP): ");
                        sourceCurrency = scanner.nextLine().toUpperCase();



                        System.out.print("Introduzca la moneda de salida (ej., USD, EUR, JPY, ARS, BOB, BRL, CLP, COP): ");
                        targetCurrency = scanner.nextLine().toUpperCase();

                        validInput = true;
                    } catch (Exception e) {
                        System.out.println("Valor invalido, ingreselo nuevamente.");
                        scanner.nextLine(); // Consume the invalid input
                    }
                }


                    double convertedAmount = convertCurrency(amount, sourceCurrency, targetCurrency);
                    System.out.printf("%.2f %s es igual a %.2f %s%n", amount, sourceCurrency, convertedAmount, targetCurrency);

                    System.out.print("Quiere convertir otro valor? (Si/No): ");
                    String choice = scanner.nextLine().toLowerCase();
                    if (choice.equals("no")) {
                        salir = true;
                        System.out.println("Saliendo...");
                }
            }
        }

        private static double convertCurrency(double amount, String sourceCurrency, String targetCurrency) {
            try {
                String url_str = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + BASE_CURRENCY;
                URL url = new URL(url_str);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject jsonobj = root.getAsJsonObject();

                String req_result = jsonobj.get("result").getAsString();
                if (!req_result.equals("success")) {
                    throw new RuntimeException("API request failed: " + req_result);
                }

                JsonObject conversion_rates = jsonobj.getAsJsonObject("conversion_rates");
                double sourceCurrencyRate = conversion_rates.get(sourceCurrency).getAsDouble();
                double targetCurrencyRate = conversion_rates.get(targetCurrency).getAsDouble();

                return (amount / sourceCurrencyRate) * targetCurrencyRate;
            } catch (IOException e) {
                throw new RuntimeException("Error fetching exchange rates: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Invalid currency code: " + e.getMessage());
            }
        }
}

