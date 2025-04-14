package classes;

import config.config;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import javax.swing.JOptionPane;

public class coreClass {

    logging logs = new logging();
    databaseCore dbCore = new databaseCore();
    ResultSet rs;
    private static String accountID = "";

    public void insertSuppName(String suppName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                if (isSupplierExisting(suppName)) {
                    JOptionPane.showMessageDialog(null, "Supplier existing. Try again.");
                } else {
                    String query = "INSERT INTO itemsupplier(supplierName,  addedOn, addedBy) VALUES('" + suppName + "', NOW(), '" + accountID + "')";
                    dbCore.execute(query);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please login before adding a supplier", "Warning", 1);
            }
        } catch (Exception e) {
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public void deleteSupplier(String suppName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "UPDATE itemsupplier "
                        + "SET deletedOn = NOW(), deletedBy = '" + accountID + "' "
                        + "WHERE supplierName = '" + suppName + "' "
                        + "AND deletedOn IS NULL";
                dbCore.execute(query);
            } else {
                JOptionPane.showMessageDialog(null, "Please login before deleteing a supplier", "Warning", 1);
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public boolean login(String username, String password) {
        boolean res = false;
        try {
            logs.setupLogger();
            String query = "SELECT  ah.* "
                    + "FROM accountheader ah "
                    + "JOIN accountdetail ad "
                    + "ON ah.accountID = ad.accountID "
                    + "WHERE ah.deletedOn IS NULL "
                    + "AND userName = '" + username + "' AND password = SHA2('" + password + "', 256) ";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                res = true;
                setAccountID(rs.getString("accountID"));
            }
            rs.close();
        } catch (IOException | SQLException e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
        return res;
    }

    public void setAccountID(String accID) {
        accountID = accID;
    }

    public String getAccountID() {
        return this.accountID;
    }

    public void deleteCategory(String categoryName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "UPDATE itemcategory "
                        + "SET deletedOn = NOW(), deletedBy = '" + accountID + "' "
                        + "WHERE description = '" + categoryName + "'";
                dbCore.execute(query);
            } else {
                JOptionPane.showMessageDialog(null, "Please login before deleteing a supplier", "Warning", 1);
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public void insertCategory(String categoryName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                if (isCategoryExisting(categoryName)) {
                    JOptionPane.showMessageDialog(null, "Category existing. Try again.");
                } else {
                    String query = "SELECT COUNT(*) AS 'Category_Count' FROM itemcategory";
                    rs = dbCore.getResultSet(query);
                    if (rs.next()) {
                        String insertQuery = "INSERT INTO itemcategory(categoryID, description,  addedOn, addedBy) VALUES(" + Integer.parseInt(rs.getString("Category_Count")) + ",'" + categoryName + "', NOW(), '" + accountID + "')";
                        dbCore.execute(insertQuery);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please login before adding a supplier", "Warning", 1);
            }
        } catch (Exception e) {
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    private boolean isCategoryExisting(String categoryName) {
        boolean res = false;
        try {
            logs.setupLogger();
            String query = "SELECT * "
                    + "FROM itemcategory "
                    + "WHERE description = '" + categoryName + "' "
                    + "AND deletedOn IS NULL ";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                res = true;
            }
            rs.close();
        } catch (IOException | SQLException e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
        return res;
    }

    private boolean isSupplierExisting(String supplierName) {
        boolean res = false;
        try {
            logs.setupLogger();
            String query = "SELECT * "
                    + "FROM itemsupplier "
                    + "WHERE supplierName = '" + supplierName + "' "
                    + "AND deletedOn IS NULL ";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                res = true;
            }
            rs.close();
        } catch (IOException | SQLException e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
        return res;
    }

    public void addAccount(String empID, String firstName, String middleName, String lastName,
            String userName, String passWord, String accountUntil) {
        try {
            logs.setupLogger();
            String query = "INSERT INTO employees(employeeID, firstName, middleName, lastName, addedOn, addedBy) "
                    + "VALUES ('" + empID + "', '" + firstName + "', '" + middleName + "', '" + lastName + "', NOW(), '" + getAccountID() + "')";
            if (middleName.isBlank() || middleName.isEmpty()) {
                query = "INSERT INTO employees(employeeID, firstName, lastName) "
                        + "VALUES ('" + empID + "', '" + firstName + "', '" + lastName + "')";
            }
            dbCore.execute(query);

            config conf = new config();
            coreClass core = new coreClass();
            query = "INSERT INTO accountheader(accountID, employeeID, storeID, dateFrom, dateTo, addedBy, addedOn) "
                    + "VALUES((SELECT COUNT(*) + 1 FROM accountdetail),'" + empID + "', " + conf.getStoreID() + ", DATE(NOW()), '" + accountUntil + "', '" + getAccountID() + "', DATE(NOW()))";
            dbCore.execute(query);

            query = "INSERT INTO accountdetail(accountID, userName, password, addedOn, addedBy) "
                    + "VALUES((SELECT accountID FROM accountheader WHERE employeeID = '" + empID + "'), '" + userName + "', SHA2('" + passWord + "', 256), NOW(), '" + getAccountID() + "')";
            dbCore.execute(query);
            JOptionPane.showMessageDialog(null, "Account added!", null, 1);
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public int getOperatorCount() {
        int res = 0;
        try {
            logs.setupLogger();
            String query = "SELECT COUNT(*) AS 'Counts' "
                    + "FROM accountheader "
                    + "WHERE deletedOn IS NULL ";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                res = Integer.parseInt(rs.getString("Counts"));
            }
            rs.close();
        } catch (IOException | SQLException e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
        return res;
    }

    public void insertGroupName(String groupName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                if (isProfileGroupExisting(groupName)) {
                    JOptionPane.showMessageDialog(null, "Profile Group existing. Try again.");
                } else {
                    String query = "INSERT INTO profilegroup(description, addedOn, addedBy) VALUES('" + groupName + "', NOW(), '" + accountID + "')";
                    dbCore.execute(query);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please login before adding a profile group.", "Warning", 1);
            }
        } catch (Exception e) {
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public void deleteProfileGroup(String groupName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "UPDATE profilegroup "
                        + "SET deletedOn = NOW(), deletedBy = '" + accountID + "' "
                        + "WHERE description = '" + groupName + "' "
                        + "AND deletedOn IS NULL";
                dbCore.execute(query);
            } else {
                JOptionPane.showMessageDialog(null, "Please login before deleteing a group", "Warning", 1);
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public void insertProfileHeader(String accountID, String addedBy, String groupName, int discountOverrideFlag, int abortReceiptFlag,
            int voidReceiptFlag, int totalDiscountFlag, int reprintReceiptFlag, int lineVoidFlag, int priceOverrideFlag) {
        try {
            logs.setupLogger();

            ResultSet rs;
            int groupID = 0;
            String query = "SELECT Auto_ID FROM profilegroup WHERE description = '" + groupName + "' AND deletedOn IS NULL ";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                groupID = Integer.parseInt(rs.getString("Auto_ID"));
            }

            query = "INSERT INTO profileheader(accountID, profileGroupID, addedOn, addedBy) "
                    + "VALUES ('" + accountID + "'," + groupID + ",NOW(), '" + addedBy + "')";
            dbCore.execute(query);

            query = "SELECT Auto_ID FROM profileheader WHERE accountID = '" + accountID + "' AND deletedOn IS NULL ORDER BY Auto_ID DESC LIMIT 1 ";
            rs = dbCore.getResultSet(query);
            int profileID = 0;
            if (rs.next()) {
                profileID = Integer.parseInt(rs.getString("Auto_ID"));
            }
            query = "INSERT INTO profileprotocol(profileProtocolID ,profileID, discountOverride, abortReceipt, totalDiscount, voidReceipt, reprintReceipt, lineVoid, priceOverride) "
                    + "VALUES (" + groupID + "," + profileID + ", " + discountOverrideFlag + "," + abortReceiptFlag + "," + totalDiscountFlag + "," + voidReceiptFlag + "," + reprintReceiptFlag + "," + lineVoidFlag + "," + priceOverrideFlag + ")";
            dbCore.execute(query);

        } catch (Exception e) {
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    private boolean isProfileGroupExisting(String groupName) {
        boolean res = false;
        try {
            logs.setupLogger();
            String query = "SELECT * "
                    + "FROM profilegroup "
                    + "WHERE description = '" + groupName + "' "
                    + "AND deletedOn IS NULL ";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                res = true;
            }
            rs.close();
        } catch (IOException | SQLException e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
        return res;
    }
}
