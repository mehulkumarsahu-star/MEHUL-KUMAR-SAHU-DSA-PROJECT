import java.util.ArrayList;
import java.util.Scanner;

class Item {
    String name;
    int weight;
    int value;

    Item(String name, int weight, int value) {
        this.name = name;
        this.weight = weight;
        this.value = value;
    }
}

public class WarehousePackingOptimizer {

    // Validation: weight and value must be > 0. Returns error message or null if valid.
    static String validate(Item it) {
        if (it.weight <= 0) return "Invalid weight. It must be greater than 0.";
        if (it.value <= 0) return "Invalid value. It must be greater than 0.";
        return null;
    }

    // Own sorting method (bubble sort) by value/weight ratio, highest first.
    // Ratio compared using cross-multiplication: v1/w1 < v2/w2  <=>  v1*w2 < v2*w1
    static void sortByRatio(Item[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Item a = arr[j], b = arr[j + 1];
                if ((long) a.value * b.weight < (long) b.value * a.weight) {
                    arr[j] = b;
                    arr[j + 1] = a;
                }
            }
        }
    }

    // Greedy plan
    static void greedy(Item[] items, int capacity) {
        Item[] sorted = items.clone();
        sortByRatio(sorted);

        int totalW = 0, totalV = 0;
        StringBuilder names = new StringBuilder();
        for (Item it : sorted) {
            if (totalW + it.weight <= capacity) {
                totalW += it.weight;
                totalV += it.value;
                if (names.length() > 0) names.append(", ");
                names.append(it.name);
            }
        }
        System.out.println("Greedy: " + names + " | Weight " + totalW + " | Value " + totalV);
    }

    // 0/1 Knapsack using 2D DP table + backtracking
    static void knapsack(Item[] items, int capacity) {
        int n = items.length;
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w];
                if (items[i - 1].weight <= w) {
                    int take = dp[i - 1][w - items[i - 1].weight] + items[i - 1].value;
                    if (take > dp[i][w]) dp[i][w] = take;
                }
            }
        }

        // Backtrack to find chosen items
        ArrayList<Item> chosen = new ArrayList<>();
        int w = capacity;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                chosen.add(0, items[i - 1]);
                w -= items[i - 1].weight;
            }
        }

        int totalW = 0;
        StringBuilder names = new StringBuilder();
        for (Item it : chosen) {
            totalW += it.weight;
            if (names.length() > 0) names.append(", ");
            names.append(it.name);
        }
        System.out.println("Knapsack: " + names + " | Weight " + totalW + " | Value " + dp[n][capacity]);
    }

    // Validate all items, then show both plans side by side
    static void runPlans(Item[] items, int capacity) {
        ArrayList<Item> valid = new ArrayList<>();
        for (Item it : items) {
            String err = validate(it);
            if (err != null) {
                System.out.println(err);
            } else {
                valid.add(it);
            }
        }
        if (valid.isEmpty()) return;
        Item[] arr = valid.toArray(new Item[0]);
        greedy(arr, capacity);
        knapsack(arr, capacity);
    }

    static void runTestCases() {
        System.out.println("--- Test Case 1: Normal Input ---");
        System.out.println("Capacity: 50 kg | Items: A (10, 60), B (20, 100), C (30, 120)");
        runPlans(new Item[]{
                new Item("A", 10, 60), new Item("B", 20, 100), new Item("C", 30, 120)}, 50);

        System.out.println("\n--- Test Case 2: Boundary / Minimum Input ---");
        System.out.println("Capacity: 10 kg | Items: A (10, 60)");
        runPlans(new Item[]{new Item("A", 10, 60)}, 10);

        System.out.println("\n--- Test Case 3: Invalid Input ---");
        System.out.println("Item: D (0, 50)");
        runPlans(new Item[]{new Item("D", 0, 50)}, 10);

        System.out.println("\n--- Test Case 4: Different Valid Scenario ---");
        System.out.println("Capacity: 8 kg | Items: P (5, 10), Q (4, 7), R (4, 7)");
        runPlans(new Item[]{
                new Item("P", 5, 10), new Item("Q", 4, 7), new Item("R", 4, 7)}, 8);
    }

    static void customInput(Scanner sc) {
        System.out.print("Enter capacity (kg): ");
        int capacity = sc.nextInt();
        System.out.print("Enter number of items: ");
        int n = sc.nextInt();

        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Item " + (i + 1) + " (name weight value): ");
            String name = sc.next();
            int wt = sc.nextInt();
            int val = sc.nextInt();
            items[i] = new Item(name, wt, val);
        }
        System.out.println();
        runPlans(items, capacity);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== Warehouse Packing Optimizer =====");
            System.out.println("1. Run all test cases");
            System.out.println("2. Enter custom input");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt();
            if (choice == 1) {
                runTestCases();
            } else if (choice == 2) {
                customInput(sc);
            } else if (choice == 3) {
                System.out.println("Bye!");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }
}
