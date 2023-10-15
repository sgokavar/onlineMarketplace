import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

/**
 * LoginMenu.java
 * 
 * This menu is used to login the user or create a new account for the user. That user is then
 * returned to RunMarket, where it can be used to enter the CustomerMenu or SellerMenu. However, if
 * the user opts to exit instead of logging in/creating an account, null will be returned and the
 * program will exit appropriately.
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

public class LoginMenu {
    Marketplace marketplace;

    public static final String LOGIN_MENU_OPTIONS = "1 - Login\n2 - Create New Account\n3 - Exit";
    public static final String ACCOUNT_TYPE_OPTIONS = "1 - Customer\n2 - Seller";

    public LoginMenu(Marketplace marketplace) {
        this.marketplace = marketplace;
    }
    
    /**
     * Menu used to select if user is (1) an existing user logging in or (2) a new user.
     * 
     * @param scan
     */
    public User login(Scanner scan) {
        User user = null;
        
        do {
            System.out.println("----------");
            System.out.println("LOGIN OR CREATE NEW ACCOUNT BELOW");

            int input = MenuHelper.getInput(scan, LOGIN_MENU_OPTIONS, 3);

            switch (input) {
                case 1:
                    user = existingUserLogin(scan); // 1 - Login
                    break;
                case 2:
                    user = createNewAccount(scan);  // 2 - Create New Account
                    break;
                case 3:
                    return null;                    // User selected exit option, main method will end in RunMarket
            }
        } while (user == null); // If user backs out of logging in/creating account, loop will repeat

        return user;
    }  

    /**
     * Menu used to create a new account.
     * The user specifies their email, password, and whether they are (1) a customer or (2) a seller.
     * 
     * @param scan
     */
    private User createNewAccount(Scanner scan) {
        System.out.println("----------");
        System.out.println("NEW ACCOUNT CREATION");
        System.out.println("Enter 'X' instead of an email to return to the previous menu.");
        
        User newUser = null;

        while (true) {
            System.out.print("Email: ");
            String email = scan.nextLine();

            if (email.trim().equals("X")) {
                break;
            }

            if (!isNewEmail(email)) {
                System.out.println("An account under that email already exists.");
                continue;
            } else if (!isValidEmail(email)) {
                System.out.println("Ensure that the email you entered is formatted correctly.");
                continue;
            }

            String password;

            while (true) {
                System.out.print("Password: ");
                password = scan.nextLine();

                if (password.equals("X")) {
                    System.out.println("'X' is not an accepted password due to its use in navigating certain menus.");
                    System.out.println("We apologize for this inconvenience.");
                } else {
                    break;
                }
            }

            int input = MenuHelper.getInput(scan, ACCOUNT_TYPE_OPTIONS, 2);

            switch (input) {
                case 1:
                    newUser = new Customer(email, password, true);
                    break;
                case 2:
                    newUser = new Seller(email, password, true);
            }
            break;

        }

        return newUser; // newUser will be null if user entered X, otherwise it will be the account type they selected
    }

    /**
     * Determines whether or not the user has entered a new email by checking it against the emails
     * of all of the customers and then all of the sellers.
     * 
     * @param email - email to be checked
     * @return
     */
    private boolean isNewEmail(String email) {
        ArrayList<Customer> allCustomers = marketplace.getAllCustomers();
        ArrayList<Seller> allSellers = marketplace.getAllSellers();

        for (Customer c : allCustomers) {
            if (email.equals(c.getEmail())) {
                return false;
            }
        }
        for (Seller s : allSellers) {
            if (email.equals(s.getEmail())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if email is formatted similar to a@a.a (which would be the minimum length)
     * 
     * @param email - email to be checked
     * @return
     */
    private boolean isValidEmail(String email) {
        if (email.contains(" ")) {
            return false;
        } else if (email.length() < 5) {
            return false;
        }

        int index = email.indexOf("@");

        if (index < 1 || index >= email.length() - 3) {
            return false;
        }

        String searchStr = email.substring(index + 1);
        int indexTwo = searchStr.indexOf(".");

        return (!(indexTwo < 1 || indexTwo >= searchStr.length() - 1));
    }
    
    /**
     * Menu used to log in an existing user. 'X' can be entered instead of your email to return
     * to the menu where you select between logging in as an existing user or creating a new
     * account. It can also be entered instead of a password to return to entering an email.
     * 
     * @param scan
     */
    private User existingUserLogin(Scanner scan) {
        System.out.println("----------");
        System.out.println("LOGIN BELOW");
        System.out.println("Enter 'X' instead of an email to return to the previous menu.");
        System.out.println("When entering a password, you can enter 'X' instead to return to entering an email.");

        User user = null;
        
        String[] userInfo = findUser(scan);

        while (!(userInfo == null)) {
            if (getPassword(scan, userInfo)) {
                if (userInfo[2].equals("customer")) {
                    user = new Customer(userInfo[0], userInfo[1], false);
                    break;
                } else {
                    user = new Seller(userInfo[0], userInfo[1], false);
                    break;
                }
            }

            userInfo = findUser(scan);
        }

        return user;
    }

    // METHODS BELOW ARE USED FOR LOGGING IN AN EXISTING USER

    /**
     * This method finds the info in user_list.csv that corresponds to the email that it also
     * obtains from the user. X can be used to back out of entering the email, returning the user
     * to selecting between logging in as an existing user or creating a new account.
     * 
     * @param scan
     * @return     - info in user_list.csv corresponding to received email
     */
    private String[] findUser(Scanner scan) {
        File f = new File("user_list.csv");

        while (true) {
            System.out.print("Email: ");
            String email = scan.nextLine();

            if (email.trim().equals("X")) {
                return null;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line = br.readLine();

                while (line != null) {
                    String[] userInfo = line.split(",");

                    if (email.equals(userInfo[0])) {
                        return userInfo;
                    }

                    line = br.readLine();
                }
            } catch (IOException e) {
                System.out.println("Error! Issue while reading user_list.csv to find matching email.");
                return null;
            }

            System.out.println("The email you entered does not match an existing account.");
        }
    }

    /**
     * This method is used to get the password corresponding to the inputted userInfo from the
     * user. Additionally, X can be entered to return to entering an email in the instance where
     * a user has, for example, entered an email corresponding to someone else's account. At this
     * time, there is no "Forgot password?" functionality.
     * 
     * @param scan
     * @param userInfo - info corresponding to user that password must be entered for
     * @return
     */
    private boolean getPassword(Scanner scan, String[] userInfo) {
        while (true) {
            System.out.print("Password: ");
            String password = scan.nextLine();

            if (password.trim().equals("X")) {
                return false;
            }

            if (password.equals(userInfo[1])) {
                return true;
            }

            System.out.println("Incorrect Password");
        }
    }

}
