package classes;

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
                        + "WHERE supplierName = '" + suppName + "'";
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
            dbCore.setQuery(query);
            rs = dbCore.getResultSet();
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

    public void addItem(String itemID, String itemDescription, int supplierID, String accountID, double unitPrice, int categoryID, int PWDAllowed, int SeniorAllowed,
            double sellingPrice, String barcode, String color, String initialSize, int value) {

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
            dbCore.setQuery(query);
            rs = dbCore.getResultSet();
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
            dbCore.setQuery(query);
            rs = dbCore.getResultSet();
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
