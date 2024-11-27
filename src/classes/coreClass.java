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
                    String query = "INSERT INTO supplier(supplierName,  addedOn, addedBy) VALUES('" + suppName + "', NOW(), '" + accountID + "')";
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
                String query = "UPDATE supplier "
                        + "SET deletedOn = NOW(), deletedBy = '" + accountID + "' "
                        + "WHERE supplierName = '" + suppName + "' "
                        + "AND deletedOn IS NULL";
                dbCore.executeUpdate(query);
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

    public void addItem(String itemID,
            String itemDescription,
            int supplierID,
            String accountID,
            double unitPrice,
            int categoryID,
            int PWDAllowed,
            int SeniorAllowed,
            double sellingPrice,
            String barcode,
            String UOMText) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "INSERT INTO itemheader(itemID, itemDescription, supplierID, addedOn, addedBy) "
                        + "VALUES('" + itemID + "', '" + itemDescription + "', " + supplierID + ", NOW(), '" + accountID + "'); ";
                dbCore.execute(query);
                query = "INSERT INTO itemdetail(itemID, unitPrice, categoryID, description, discountPWDAllowed, discountSCAllowed, unitOfMeasure) "
                        + "VALUES('" + itemID + "', " + unitPrice + ", " + categoryID + ", '" + itemDescription + "', '" + PWDAllowed + "', '" + SeniorAllowed + "', '" + UOMText + "'); ";
                dbCore.execute(query);
                query = "INSERT INTO itembarcode(itemID, barcode, addedOn, addedBy) VALUES('" + itemID + "', '" + barcode + "', NOW(), '" + accountID + "'); ";
                dbCore.execute(query);
                query = "INSERT INTO itemprice(itemID, value, addedOn, addedBy) VALUES('" + itemID + "', '" + sellingPrice + "', NOW(), '" + accountID + "'); ";
                dbCore.execute(query);
            } else {
                JOptionPane.showMessageDialog(null, "Please login before adding an item.", "Warning", 1);
            }
        } catch (Exception e) {
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public void deleteCategory(String categoryName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "UPDATE itemcategory "
                        + "SET deletedOn = NOW(), deletedBy = '" + accountID + "' "
                        + "WHERE description = '" + categoryName + "'";
                dbCore.executeUpdate(query);
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
                    String query = "INSERT INTO itemcategory(description,  addedOn, addedBy) VALUES('" + categoryName + "', NOW(), '" + accountID + "')";
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
                    + "FROM supplier "
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

    public void deleteItem(String itemID, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "UPDATE itemheader "
                        + "SET deletedOn = NOW(), deletedBy = '" + accountID + "' "
                        + "WHERE itemID = '" + itemID + "'";
                dbCore.executeUpdate(query);
            } else {
                JOptionPane.showMessageDialog(null, "Please login before deleteing an item", "Warning", 1);
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public void addAccount(String empID, String firstName, String middleName, String lastName,
            String userName, String passWord, String accountUntil) {
        try {
            logs.setupLogger();
            String query = "INSERT INTO employees(employeeID, firstName, middleName, lastName) "
                    + "VALUES ('" + empID + "', '" + firstName + "', '" + middleName + "', '" + lastName + "')";
            if (middleName.isBlank() || middleName.isEmpty()) {
                query = "INSERT INTO employees(employeeID, firstName, lastName) "
                        + "VALUES ('" + empID + "', '" + firstName + "', '" + lastName + "')";
            }
            dbCore.execute(query);

            config conf = new config();
            coreClass core = new coreClass();
            query = "INSERT INTO accountheader(accountID, employeeID, storeID, dateFrom, dateTo, addedBy, addedOn) "
                    + "VALUES((SELECT COUNT(*) + 1 FROM accountdetail),'" + empID + "', " + conf.getStoreID() + ", DATE(NOW()), '" + accountUntil + "', '" + core.getAccountID() + "', DATE(NOW()))";
            dbCore.execute(query);

            query = "INSERT INTO accountdetail(accountID, userName, password) "
                    + "VALUES((SELECT accountID FROM accountheader WHERE employeeID = '" + empID + "'), '" + userName + "', SHA2('" + passWord + "', 256))";
            dbCore.execute(query);
            JOptionPane.showMessageDialog(null, "Account added!", null, 1);
        } catch (Exception e) {
//            logs.logger.log(Level.SEVERE, "An exception occurred", e);
            e.printStackTrace();
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

    public void insertGroupName(String suppName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                if (isSupplierExisting(suppName)) {
                    JOptionPane.showMessageDialog(null, "Supplier existing. Try again.");
                } else {
                    String query = "INSERT INTO profilegroup(description, addedOn, addedBy) VALUES('" + suppName + "', NOW(), '" + accountID + "')";
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
                dbCore.executeUpdate(query);
            } else {
                JOptionPane.showMessageDialog(null, "Please login before deleteing a group", "Warning", 1);
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }
}
