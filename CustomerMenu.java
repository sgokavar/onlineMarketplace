import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.*;

/**
 * CustomerMenu.java
 * 
 * This is the menu for customers, and with it they can view the marketplace, purchase products,
 * add products to their cart, and checkout. It is not divided like the Seller class based on what
 * you are "in" (the Seller, a Store, or a Product) at the time so this class is large. Their is
 * the outside menu and then a number of paths you can take to access different aspects of it.
 * However, although the code is lengthy, the menu itself isn't complicated when accessed and
 * doesn't realistically have a huge range of capabilities. Shopping-cart-related things (besides
 * adding to cart) take place in another menu to make this a bit less congested,
 * the ShoppingCartMenu.
 * 
 * @author Sahithi Gokavarapu
 * @author Dhruv Jain
 * @author George O'Carroll
 * @author Nishitha Pelaprolu
 * @author Ateeq Ramlan
 * 
 * @version 11-12-2022
 * 
 */

public class CustomerMenu {
    private Customer customer;
    private Marketplace marketplace;

    public static final String CUSTOMER_MENU_TEXT = "1 - View Marketplace\n2 - Shopping Cart\n" +
                                                    "3 - View Purchase History\n" +
                                                    "4 - View Dashboard\nOther Options\n5 - Logout\n6 - Exit";
    public static final String MARKETPLACE_MENU_TEXT = "1 - Unsorted\n2 - By Price\n" +
                                                       "3 - By Quantity\n4 - Search Marketplace\n" +
                                                       "Other Options\n5 - Back";

    /**
     * @param customer    - Currently logged in customer
     * @param marketplace - Contains all of the data on Users, Stores, and Products
     */
    public CustomerMenu(Customer customer, Marketplace marketplace) {
        this.customer = customer;
        this.marketplace = marketplace;
    }

    /**
     * This method is how the CustomerMenu is accessed from the outside, and allows the user to
     * choose between:
     *  - Viewing the marketplace (options can be subsequently selected like sorting the
     *    marketplace or searching the marketplace with a word or phrase)
     *  - Viewing the customer who is logged in's shopping cart, they can also check out
     *  - Viewing the customer's purchase history
     * 
     * @param scan - Scanner used throughout program
     * @return     - Determines whether or not the user is logged out or the program is exited entirely
     */
    public boolean goToCustomerMenu(Scanner scan) {
        while (true) {
            System.out.println("----------");
            System.out.println("CUSTOMER MENU");

            int input = MenuHelper.getInput(scan, CUSTOMER_MENU_TEXT, 6);

            switch (input) {
                case 1:
                    marketplaceOptions(scan);
                    break;
                case 2:
                    ShoppingCartMenu shoppingCartMenu = new ShoppingCartMenu(customer);
                    shoppingCartMenu.goToShoppingCartMenu(scan);
                    break;
                case 3:
                    viewPurchaseHistory(scan);
                    break;
                case 4:
                    CustomerDashboard customerDashboard = new CustomerDashboard(customer, marketplace);
                    customerDashboard.goToCustomerDashboard(scan);
                    break;
                case 5:
                    return false;
                case 6:
                    return true;
            }
        }
    }

    /**
     * Options for how to view the marketplace.
     * 
     * 1 - Unsorted
     * 2 - By Price
     * 3 - By Quantity
     * 4 - Search By...
     * 
     * @param scan
     */
    public void marketplaceOptions(Scanner scan) {
        while (true) {
            System.out.println("----------");
            System.out.println("MARKETPLACE OPTIONS");

            int input = MenuHelper.getInput(scan, MARKETPLACE_MENU_TEXT, 5);

            switch(input) {
                case 1:
                    viewUnsorted(scan);
                    break;
                case 2:                  
                    viewByX(scan, true);
                    break;
                case 3:
                    viewByX(scan, false);
                    break;
                case 4:
                    searchMarketplace(scan);
                    break;
                case 5:
                    return;
            }
        }
    }

    /**
     * Here the user can view all of the products they have ever purchased on this marketplace in
     * chronological order, with the newer ones at the top.
     * 
     * @param scan
     */
    private void viewPurchaseHistory(Scanner scan) {
        System.out.println("----------");
        System.out.println("PURCHASE HISTORY");

        ArrayList<Product> pastPurchases = customer.getPastPurchases();
        ArrayList<Integer> pastPurchaseQuantities = customer.getPastPurchaseQuantities();

        if (pastPurchases.size() != 0) {
            System.out.println("Your most recent purchases are at the top of the list.");
            for (int i = pastPurchases.size() - 1; i >= 0; i--) {
                Product product = pastPurchases.get(i);
                int quantity = pastPurchaseQuantities.get(i);
                double price = product.getPrice() * quantity;

                System.out.printf(" - $%.2f [%d] %s | $%.2f each | Purchased from: %s\n",
                                  price, quantity, product.getProdName(), product.getPrice(),
                                  product.getWhichStore());
            }
        } else {
            System.out.println("You have not made any purchases.");
        }

        System.out.println("*****");
        int input = MenuHelper.getInput(scan, "1 - Export Purchase history to a CSV file\n2 - Back", 2);
        switch (input){
            case 1:
                exportPurchasesToCSV(scan, customer);
                break;
        }
    }

    // MARKETPLACE VIEWING OPTIONS

    private void viewUnsorted(Scanner scan) {
        viewMarketplace(scan, marketplace.getAllProducts());
    }

    /**
     * SORTS MARKETPLACE
     * 
     * This method is a bit confusing visually because it can sort by two things two different
     * ways. Here, the order in which the products are listed to the user can be sorted based on
     * quantity or price, from low to high or high to low. In all cases, it is accomplished via an
     * insertion sort into a new, separate ArrayList which is then handed off to the method that
     * actually displays the marketplace to the user.
     * 
     * @param scan
     * @param byPrice
     */
    private void viewByX(Scanner scan, boolean byPrice) {
        ArrayList<Product> allProducts = marketplace.getAllProducts();
        ArrayList<Product> marketList = new ArrayList<>();

        int input = MenuHelper.getInput(scan, "1 - Low to High\n2 - High to Low", 2);
        boolean lowToHigh = (input == 1 ? true : false);
        
        marketList.add(allProducts.get(0));

        if (byPrice) {
            for (int i = 1; i < allProducts.size(); i++) {
                Product product = allProducts.get(i);

                for (int j = 0; j < i; j++) {
                    if ((product.getPrice() > marketList.get(j).getPrice()) ^ lowToHigh) {
                        marketList.add(j, product);
                        break;
                    } else if (j == i - 1) {
                        marketList.add(product);
                    }
                }
            }
        } else {
            for (int i = 1; i < allProducts.size(); i++) {
                Product product = allProducts.get(i);

                for (int j = 0; j < i; j++) {
                    if ((product.getStockRemaining() > marketList.get(j).getStockRemaining()) ^ lowToHigh) {
                        marketList.add(j, product);
                        break;
                    } else if (j == i - 1) {
                        marketList.add(product);
                    }
                }
            }
        }

        viewMarketplace(scan, marketList);
    }

    /**
     * Marketplace search function here
     * 
     * @param scan
     */
    private void searchMarketplace(Scanner scan) {
        ArrayList<Product> allProducts = marketplace.getAllProducts();

        while (true) {
            ArrayList<Product> marketList = new ArrayList<>();

            System.out.println("Searches will be referenced against clothing items' names, descriptions,\n" +
                               "materials, colors, and the store they are listed in.");
            System.out.print("Enter your search: ");
            String search = scan.nextLine();

            for (Product p : allProducts) {
                if (p.getProdName().toLowerCase().contains(search.toLowerCase().trim()) ||
                    p.getDescription().toLowerCase().contains(search.toLowerCase().trim()) ||
                    p.getWhichStore().toLowerCase().contains(search.toLowerCase().trim()) ||
                    p.getMaterial().toLowerCase().contains(search.toLowerCase().trim()) ||
                    p.getColour().toLowerCase().contains(search.toLowerCase().trim())) {

                    marketList.add(p);
                }
            }

            if (marketList.size() == 0) {
                System.out.println("Your search returned no results.");
                int input = MenuHelper.getInput(scan, "1 - Try a Different Search\n2 - Back", 2);

                switch (input) {
                    case 1:
                        continue;
                    case 2:
                        return;
                }
            }

            viewMarketplace(scan, marketList);
            return;
        }
    }

    // SELECTING PRODUCTS AND BUYING THEM OR ADDING THEM TO CART

    /**
     * This method displays the marketplace to the user, listing all of the products on it.
     * It shows the name of a product, its price, and the store selling it. To see more details or
     * immediately buy a product/add a product to cart, the user must enter a number corresponding
     * to the product.
     * 
     * @param scan
     * @param marketProducts - ArrayList of products on the market, already in the order in which
     *                         they will be displayed
     */
    private void viewMarketplace(Scanner scan, ArrayList<Product> marketProducts) {
        String menuText = "";

        int i;
        
        for (i = 1; i < marketProducts.size() + 1; i++) {
            Product product = marketProducts.get(i - 1);

            menuText += String.format("%d - %s | $%.2f | Offered by: %s\n",
                                      i, product.getProdName(), product.getPrice(), product.getWhichStore());
        }

        menuText += i + " - Back";

        while (true) {
            System.out.println("----------");
            System.out.println("MARKETPLACE");

            int input = MenuHelper.getInput(scan, menuText, i);

            if (input < i) {
                productPage(scan, marketProducts.get(input - 1));
            } else if (input == i) {
                return;
            }
        }
    }

    /**
     * This is the page for a particular product where a user sees all of the details associated
     * with the product and can choose if they want to buy it/add it to cart.
     * 
     * @param scan
     * @param product - product being shown in greater detail that the user can decide to purchase
     *                  or add to cart
     */
    private void productPage(Scanner scan, Product product) {
        while (true) {
            System.out.println("----------");
            System.out.println("LISTING");

            System.out.println("Store: " + product.getWhichStore());
            System.out.println("Clothing Item Name: " + product.getProdName());
            System.out.println("Description: " + product.getDescription());
            System.out.printf("Price: $%.2f\n", product.getPrice());
            System.out.println("Quantity Remaining: " + product.getStockRemaining());

            int input = MenuHelper.getInput(scan, "1 - Buy Now\n2 - Add to Shopping Cart\n3 - Add Review\n" +
                                            "4 - View Reviews\n5 - Back", 5);

            boolean exit = false;

            switch(input) {
                case 1:
                    buyNowOrAddToCart(scan, product, true);
                    exit = true;
                    break;
                case 2:
                    buyNowOrAddToCart(scan, product, false);
                    exit = true;
                    break;
                case 3:
                    product.leaveReview(scan);
                    break;
                case 4:
                    product.viewReviews(scan);
                    break;
                case 5:
                    return;
                    
            }

            if (exit) {
                break;
            }
        }
    }

    /**
     * Here, the user enters the quantity they would like to purchase or add to cart. They cannot
     * buy more than are on the market if they are buying immediately. Of course, this method
     * behaves differenty based on whether or not buyNow is true.
     * 
     * @param scan
     * @param product - Product being bought or added to cart
     * @param buyNow  - True if the user is buying now, otherwise false
     */
    private void buyNowOrAddToCart(Scanner scan, Product product, boolean buyNow) {
        while (true) {
            System.out.println("Enter 'X' to cancel.");
            if (buyNow) {
                System.out.println("How many would you like to purchase?: ");
            } else {
                System.out.println("How many would you like to add to your shopping cart?");
            }

            String numStr = scan.nextLine();

            if (numStr.equals("X")) {
                return;
            }

            int num;

            try {
                num = Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                System.out.println("You must enter a number!");
                continue;
            }

            if (num <= 0) {
                System.out.println("You must enter a number greater than 0.");
                continue;
            }

            int stockRemaining = product.getStockRemaining();

            if (buyNow && num > stockRemaining) {
                System.out.printf("There are only %d remaining.\n", stockRemaining);
            } else if (buyNow) {
                product.buyAmount(num);
                System.out.printf("You purchased %d of the item %s.\n",
                                 num, product.getProdName());
                product.addToPurchaseHistory(num, customer.getEmail());
                customer.addPurchasedProduct(product, num);
                break;
            } else {
                System.out.printf("You added %d of the item %s to your cart.\n",
                                  num, product.getProdName());
                customer.addShoppingCartProduct(product, num);
                break;
            }
        }
    }

    /**
     * Here, the user enters the filename they want to export to.
     * They must export to a csv and cannot export to a protected file 
     * protected files are files used for data persistence by the program
     * 
     * @param scan
     * @param customer1 - Customer that wants to export to a CSV
     */
    public void exportPurchasesToCSV(Scanner scan, Customer customer1) {
        System.out.println("----------");
        System.out.println("EXPORT TO CSV");
        System.out.println("Enter a file name you would like to export to");
        while (true) {
            System.out.println("'X' can be entered to go back to the previous menu.");
            System.out.print("Enter the name of the file to export the products to: ");

            String fileName = scan.nextLine();

            if (fileName.equals("X")) {
                return;
            }

            // Necessary for protecting .java files
            if (!(fileName.substring(fileName.length() - 4)).equals(".csv")) {
                System.out.println("Ensure that you are exporting to a file with the extension '.csv'");
                continue;
            }

            // Must protect internal .csv files as well
            String[] protectedFiles = { "product_file_output.csv", "purchased_products.csv",
                "seller_products.csv", "shopping_cart_products.csv",
                "stores_list.csv", "user_list.csv", "product_reviews.csv" };
            boolean illegalFileName = false;

            for (String s : protectedFiles) {
                if (fileName.equals(s)) {
                    System.out.printf("%s is also the name of an internal file. Thus, it cannot be exported to.\n",
                            fileName);
                    illegalFileName = true;
                    break;
                }
            }

            if (illegalFileName) {
                continue;
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(new File(fileName), false))) {
                pw.println("Quantity, Price, Total Spend, Product name, Store");
                for (Product product : customer1.getPastPurchases()) {
                    int index = customer1.getPastPurchases().indexOf(product);
                    int quantity = customer1.getPastPurchaseQuantities().get(index);
                    pw.printf("%d,$%.2f, $%.2f, %s,%s\n", quantity, product.getPrice(),
                              quantity * product.getPrice(), product.getProdName(), product.getWhichStore());
                }
                
                System.out.println("Purchase history exported succesfully");
                break;
            } catch (IOException e) {
                System.out.println("Enter a valid file name!");
            }
        }
    }
}
