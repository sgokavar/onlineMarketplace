import java.util.Scanner;
import java.util.ArrayList;

/**
 * SellerDashboard.java
 * 
 * This class provides the dashboard functionality for sellers, linked to a particular store that
 * the seller has selected. It first shows items purchased from the given store by customer, and
 * then sales by product. Both lists can also be sorted low to high or high to low, just like the
 * customer dashboard. In general, this class is structured extremely similarly to the customer
 * dashboard.
 * 
 * @author Sahithi Gokavarapu
 * @author Dhruv Jain
 * @author George O'Carroll
 * @author Nishitha Pelaprolu
 * @author Ateeq Ramlan
 * 
 * @version 11-14-2022
 * 
 */

public class SellerDashboard {
    private Store store;
    private Marketplace marketplace;

    /**
     * @param store       - store statistics are being pulled for
     * @param marketplace - used to obtain all customers, necessary for first part of dashboard
     */
    public SellerDashboard(Store store, Marketplace marketplace) {
        this.store = store;
        this.marketplace = marketplace;
    }

    /**
     * The dashboard is entered through this method, at which point the user decides how/if they
     * would like the dashboard to be sorted.
     * 
     * @param scan
     */
    public void goToSellerDashboard(Scanner scan) {
        int input = MenuHelper.getInput(scan, "1 - Unsorted\n2 - Highest Items Purchased/Sales First\n" +
                                        "3 - Lowest Items Purchased/Sales First", 3);

        dashboardSort(scan, input - 1);
    }

    /**
     * This method uses an insertion sorting algorithm to sort the lists being displayed to the
     * user, if they so choose. It also uses to methods to obtain the arrays used to sort the
     * customers/products in the correct order. The arrays also contain the values that end up
     * being displayed in the end.
     * 
     * @param scan
     * @param option - how/if the dashboard will be sorted
     */
    private void dashboardSort(Scanner scan, int option) {
        String name = store.getName();

        ArrayList<Customer> allCustomers = marketplace.getAllCustomers();
        ArrayList<Product> products = store.getProducts();

        int[] productsPerCustomer = productsPerCustomer();
        int[] productSales = salesPerProduct();

        ArrayList<String> customerList = new ArrayList<>();
        ArrayList<String> productList = new ArrayList<>();

        if (option == 0) {
            for (int i = 0; i < allCustomers.size(); i++) {
                customerList.add(String.format("- Customer: %s | Items Purchased From %s: %d",
                                 allCustomers.get(i).getEmail(), name, productsPerCustomer[i]));
            }

            for (int i = 0; i < products.size(); i++) {
                productList.add(String.format("- Product: %s | Sales: %d",
                                products.get(i).getProdName(), productSales[i]));
            }

            viewDashboard(scan, customerList, productList);

            return;
        }

        boolean lowToHigh = (option == 1 ? false : true);

        customerList.add(String.format("- Customer: %s | Items Purchased From %s: %d",
                                       allCustomers.get(0).getEmail(), name, productsPerCustomer[0]));
        ArrayList<Integer> numList = new ArrayList<>();
        numList.add(productsPerCustomer[0]);

        for (int i = 1; i < allCustomers.size(); i++) {
            Customer customer = allCustomers.get(i);

            for (int j = 0; j < i; j++) {
                if ((productsPerCustomer[i] > numList.get(j)) ^ lowToHigh) {
                    customerList.add(j, String.format("- Customer: %s | Items Purchased From %s: %d",
                                     customer.getEmail(), name, productsPerCustomer[i]));
                    numList.add(j, productsPerCustomer[i]);
                    break;
                } else if (j == i - 1) {
                    customerList.add(String.format("- Customer: %s | Items Purchased From %s: %d",
                                     customer.getEmail(), name, productsPerCustomer[i]));
                    numList.add(productsPerCustomer[i]);
                }
            }
        }

        productList.add(String.format("- Product: %s | Sales: %d",
                                products.get(0).getProdName(), productSales[0]));
        numList = new ArrayList<>();
        numList.add(productSales[0]);

        for (int i = 1; i < products.size(); i++) {
            Product product = products.get(i);

            for (int j = 0; j < i; j++) {
                if ((productSales[i] > numList.get(j)) ^ lowToHigh) {
                    productList.add(j, String.format("- Product: %s | Sales: %d",
                                    product.getProdName(), productSales[i]));
                    numList.add(j, productSales[i]);
                    break;
                } else if (j == i - 1) {
                    productList.add(String.format("- Product: %s | Sales: %d",
                                    product.getProdName(), productSales[i]));
                    numList.add(productSales[i]);
                }
            }
        }

        viewDashboard(scan, customerList, productList);
    }

    /**
     * This method displays the information as organized by the above method.
     * 
     * @param scan
     * @param customerList - List of information to display for customers, already sorted (if sorted wass selected)
     * @param productList  - "" for products, ""
     */
    private void viewDashboard(Scanner scan, ArrayList<String> customerList, ArrayList<String> productList) {
        System.out.println("----------");
        System.out.println("SELLER DASHBOARD");
        System.out.printf("For your store: %s\n", store.getName());

        System.out.println("Items Purchased by Customer");
        
        for (int i = 0; i < customerList.size(); i++) {
            System.out.println(customerList.get(i));
        }

        System.out.println("Sales by Product");
    
        for (int i = 0; i < productList.size(); i++) {
            System.out.println(productList.get(i));
        }

        MenuHelper.getInput(scan, "1 - Back", 1);
    }

    /**
     * Obtains array of purchases from this store by customer, with indexes matching indexes from
     * allCustomers.
     * 
     * @return - array described above
     */
    private int[] salesPerProduct() {
        ArrayList<Product> products = store.getProducts();
        ArrayList<Customer> allCustomers = marketplace.getAllCustomers();
        int[] productSales = new int[products.size()];

        for (int i = 0; i < products.size(); i++) {
            for (Customer c : allCustomers) {
                ArrayList<Product> purchases = c.getPastPurchases();
                ArrayList<Integer> quantities = c.getPastPurchaseQuantities();

                for (int j = 0; j < purchases.size(); j++) {
                    if (products.get(i).getProdName().equals(purchases.get(j).getProdName())) {
                        productSales[i] += quantities.get(j);
                    }
                }
            }
        }

        return productSales;
    }

    /**
     * Returns sales by product, with indexes matching indexes from the particular store's
     * "products" array.
     * 
     * @return - array described above
     */
    private int[] productsPerCustomer() {
        ArrayList<Customer> allCustomers = marketplace.getAllCustomers();
        int[] productsPerCustomer = new int[allCustomers.size()];
        
        for (int i = 0; i < allCustomers.size(); i++) {
            ArrayList<Product> purchases = allCustomers.get(i).getPastPurchases();
            ArrayList<Integer> quantities = allCustomers.get(i).getPastPurchaseQuantities();
            
            for (int j = 0; j < purchases.size(); j++) {
                if (store.getName().equals(purchases.get(j).getWhichStore())) {
                    productsPerCustomer[i] += quantities.get(j);
                }
            }
        }
        
        return productsPerCustomer;
    }    
}
