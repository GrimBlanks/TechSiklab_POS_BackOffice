package classes;

import config.config;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import javax.swing.JOptionPane;

public class coreClass extends dbConnect {

    logging logs = new logging();
    ResultSet rs;
    private static String accountID = "";
    private Connection con = con();
    PreparedStatement pst;

    public void insertSuppName(String suppName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                if (isSupplierExisting(suppName)) {
                    JOptionPane.showMessageDialog(null, "Supplier existing. Try again.");
                } else {
                    String query = "INSERT INTO itemsupplier(supplierName, addedOn, addedBy) VALUES(?, NOW(), ?)";
                    pst = con.prepareStatement(query);
                    pst.setString(1, suppName);
                    pst.setString(2, accountID);
                    pst.executeUpdate();
                    pst.close();
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
                String query = "UPDATE itemsupplier SET deletedOn = NOW(), deletedBy = ? WHERE supplierName = ? AND deletedOn IS NULL";
                pst = con.prepareStatement(query);
                pst.setString(1, accountID);
                pst.setString(2, suppName);
                pst.executeUpdate();
                pst.close();
            } else {
                JOptionPane.showMessageDialog(null, "Please login before deleting a supplier", "Warning", 1);
            }
        } catch (Exception e) {
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
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
                    + "AND userName = ? AND password = SHA2(?, 256) ";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            rs = pst.executeQuery();
            if (rs.next()) {
                res = true;
                setAccountID(rs.getString("accountID"));
            }
            rs.close();
            pst.close();
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
                        + "SET deletedOn = NOW(), deletedBy = ? "
                        + "WHERE description = ? ";
                pst = con.prepareStatement(query);
                pst.setString(1, accountID);
                pst.setString(2, categoryName);
                pst.executeUpdate();

                pst.close();
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
                    pst = con.prepareStatement(query);
                    rs = pst.executeQuery(query);
                    if (rs.next()) {
                        int categoryCount = rs.getInt("Category_Count");
                        String insertQuery = "INSERT INTO itemcategory(categoryID, description, addedOn, addedBy) VALUES(?, ?, NOW(), ?)";
                        PreparedStatement pst1 = con.prepareStatement(insertQuery);
                        pst1.setInt(1, categoryCount + 1);
                        pst1.setString(2, categoryName);
                        pst1.setString(3, accountID);
                        pst1.executeUpdate();
                        pst1.close();
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
                    + "WHERE description = ? "
                    + "AND deletedOn IS NULL ";
            pst = con.prepareStatement(query);
            pst.setString(1, categoryName);
            rs = pst.executeQuery(query);
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
                    + "WHERE supplierName = ? "
                    + "AND deletedOn IS NULL ";
            pst = con.prepareStatement(query);
            pst.setString(1, supplierName);
            rs = pst.executeQuery();
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
            config conf = new config();
            con.setAutoCommit(false); // Begin transaction

            // Insert into employees
            if (middleName == null || middleName.isBlank()) {
                pst = con.prepareStatement("INSERT INTO employees(employeeID, firstName, lastName, addedOn, addedBy) VALUES (?, ?, ?, NOW(), ?)");
                pst.setString(1, empID);
                pst.setString(2, firstName);
                pst.setString(3, lastName);
                pst.setString(4, getAccountID());
            } else {
                pst = con.prepareStatement("INSERT INTO employees(employeeID, firstName, middleName, lastName, addedOn, addedBy) VALUES (?, ?, ?, ?, NOW(), ?)");
                pst.setString(1, empID);
                pst.setString(2, firstName);
                pst.setString(3, middleName);
                pst.setString(4, lastName);
                pst.setString(5, getAccountID());
            }
            pst.executeUpdate();
            pst.close();

            // Generate new accountID based on COUNT(*) + 1
            pst = con.prepareStatement("SELECT COUNT(*) + 1 AS newID FROM accountdetail");
            rs = pst.executeQuery();
            int newAccountID = 1;
            if (rs.next()) {
                newAccountID = rs.getInt("newID");
            }
            rs.close();
            pst.close();

            // Insert into accountheader
            pst = con.prepareStatement("INSERT INTO accountheader(accountID, employeeID, storeID, dateFrom, dateTo, addedBy, addedOn) VALUES (?, ?, ?, DATE(NOW()), ?, ?, DATE(NOW()))");
            pst.setInt(1, newAccountID);
            pst.setString(2, empID);
            pst.setInt(3, conf.getStoreID());
            pst.setString(4, accountUntil);
            pst.setString(5, getAccountID());
            pst.executeUpdate();
            pst.close();

            // Insert into accountdetail
            pst = con.prepareStatement("INSERT INTO accountdetail(accountID, userName, password, addedOn, addedBy) VALUES (?, ?, SHA2(?, 256), NOW(), ?)");
            pst.setInt(1, newAccountID);
            pst.setString(2, userName);
            pst.setString(3, passWord);
            pst.setString(4, getAccountID());
            pst.executeUpdate();
            pst.close();

            con.commit(); // Commit transaction
            JOptionPane.showMessageDialog(null, "Account added!", null, 1);

        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback(); // Rollback on error
                }
            } catch (SQLException ex) {
                logs.logger.log(Level.SEVERE, "Rollback failed", ex);
            }
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                logs.logger.log(Level.SEVERE, "Closing resources failed", ex);
            }
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
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
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
                    String query = "INSERT INTO profilegroup(description, addedOn, addedBy) VALUES(? , NOW(), ?)";
                    pst = con.prepareStatement(query);
                    pst.setString(1, groupName);
                    pst.setString(2, accountID);
                    pst.executeUpdate();
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
                        + "SET deletedOn = NOW(), deletedBy = ? "
                        + "WHERE description = ? "
                        + "AND deletedOn IS NULL";
                pst = con.prepareStatement(query);
                pst.setString(1, accountID);
                pst.setString(2, groupName);
                pst.executeUpdate();
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
            con.setAutoCommit(false);

            int groupID = 0;

            // 1. Get profileGroup ID
            String query = "SELECT Auto_ID FROM profilegroup WHERE description = ? AND deletedOn IS NULL";
            pst = con.prepareStatement(query);
            pst.setString(1, groupName);
            rs = pst.executeQuery();
            if (rs.next()) {
                groupID = rs.getInt("Auto_ID");
            }
            rs.close();
            pst.close();

            // 2. Insert into profileheader
            query = "INSERT INTO profileheader(accountID, profileGroupID, addedOn, addedBy) VALUES (?, ?, NOW(), ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, accountID);
            pst.setInt(2, groupID);
            pst.setString(3, addedBy);
            pst.executeUpdate();
            pst.close();

            // 3. Get inserted profileheader ID
            int profileID = 0;
            query = "SELECT Auto_ID FROM profileheader WHERE accountID = ? AND deletedOn IS NULL ORDER BY Auto_ID DESC LIMIT 1";
            pst = con.prepareStatement(query);
            pst.setString(1, accountID);
            rs = pst.executeQuery();
            if (rs.next()) {
                profileID = rs.getInt("Auto_ID");
            }
            rs.close();
            pst.close();

            // 4. Insert into profileprotocol
            query = "INSERT INTO profileprotocol(profileProtocolID, profileID, discountOverride, abortReceipt, totalDiscount, voidReceipt, reprintReceipt, lineVoid, priceOverride) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setInt(1, groupID);
            pst.setInt(2, profileID);
            pst.setInt(3, discountOverrideFlag);
            pst.setInt(4, abortReceiptFlag);
            pst.setInt(5, totalDiscountFlag);
            pst.setInt(6, voidReceiptFlag);
            pst.setInt(7, reprintReceiptFlag);
            pst.setInt(8, lineVoidFlag);
            pst.setInt(9, priceOverrideFlag);
            pst.executeUpdate();

            con.commit();
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                logs.logger.log(Level.SEVERE, "Rollback failed", ex);
            }
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                logs.logger.log(Level.SEVERE, "Error closing resources", ex);
            }
            logs.closeLogger();
        }
    }

    private boolean isProfileGroupExisting(String groupName) {
        boolean res = false;
        try {
            logs.setupLogger();
            String query = "SELECT * "
                    + "FROM profilegroup "
                    + "WHERE description = ? "
                    + "AND deletedOn IS NULL ";
            pst = con.prepareStatement(query);
            pst.setString(1, groupName);
            rs = pst.executeQuery();
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
