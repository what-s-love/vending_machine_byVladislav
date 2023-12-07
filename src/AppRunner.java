import acceptor.CardAcceptor;
import acceptor.CashAcceptor;
import acceptor.CoinAcceptor;
import enums.ActionLetter;
import model.*;
import util.UniversalArray;
import util.UniversalArrayImpl;

import java.util.Scanner;

public class AppRunner {

    private static boolean isExit = false;
    private final UniversalArray<Product> products = new UniversalArrayImpl<>();
    private CashAcceptor cashAcceptor;
    private boolean cashPay;

    private AppRunner() {
        products.addAll(new Product[]{
                new Water(ActionLetter.B, 20),
                new CocaCola(ActionLetter.C, 50),
                new Soda(ActionLetter.D, 30),
                new Snickers(ActionLetter.E, 80),
                new Mars(ActionLetter.F, 80),
                new Pistachios(ActionLetter.G, 130)
        });
        cashAcceptor = null;
        cashPay = false;
    }

    public static void run() {
        AppRunner app = new AppRunner();

        app.startSimulation();
    }

    private void startSimulation() {
        print("В автомате доступны:");
        showProducts(products);

        print("Выберите способ оплаты:");
        print("1 - Cash\n2 - Card");
        choosePaymentMethod(chooseNum());

        while (!isExit) {
            print("Доступная сумма: " + cashAcceptor.getAmount());

            UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
            allowProducts.addAll(getAllowedProducts().toArray());
            chooseAction(allowProducts);
        }

    }

    private UniversalArray<Product> getAllowedProducts() {
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        for (int i = 0; i < products.size(); i++) {
            if (cashAcceptor.getAmount() >= products.get(i).getPrice()) {
                allowProducts.add(products.get(i));
            }
        }
        return allowProducts;
    }

    private void chooseAction(UniversalArray<Product> products) {
        if (cashPay) {
            print(" a - Пополнить баланс");
        }
        showActions(products);
        print(" h - Выйти");
        String action = fromConsole().substring(0, 1);
        if ("a".equalsIgnoreCase(action) && cashPay) {
            cashAcceptor.setAmount(cashAcceptor.getAmount() + 10);
            print("Вы пополнили баланс на 10");
            return;
        }
        try {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getActionLetter().equals(ActionLetter.valueOf(action.toUpperCase()))) {
                    cashAcceptor.setAmount(cashAcceptor.getAmount() - products.get(i).getPrice());
                    print("Вы купили " + products.get(i).getName());
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            if ("h".equalsIgnoreCase(action)) {
                isExit = true;
            } else {
                print("Недопустимая буква. Попробуйте еще раз.");
                chooseAction(products);
            }
        }
    }

    private void showActions(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(String.format(" %s - %s", products.get(i).getActionLetter().getValue(), products.get(i).getName()));
        }
    }

    private void choosePaymentMethod(int num) {
        switch (num) {
            case 1:
                cashAcceptor = new CoinAcceptor(100);
                cashPay = true;
                break;
            case 2:
                cashAcceptor = new CardAcceptor(1000);
                print("Прислоните карту к считывающему аппарату");
                break;
            default:
                print("Нет такого варианта. Попробуйте еще раз.");
                choosePaymentMethod(chooseNum());
        }
    }

    public int chooseNum() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Введите цифру: ");
        String str = sc.nextLine().trim();
        try {
            char[] strToChars = str.toCharArray();
            if (str.isEmpty()) {
                throw new NumberFormatException("Вы не ввели данные.");
            }
            for (char a : strToChars) {
                if (!Character.isDigit(a)) {
                    throw new NumberFormatException("Вы ввели недопустимый символ.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return chooseNum();
        }
        return Integer.parseInt(str);
    }

    private String fromConsole() {
        return new Scanner(System.in).nextLine();
    }

    private void showProducts(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(products.get(i).toString());
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
