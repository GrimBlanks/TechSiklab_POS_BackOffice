package classes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.sql.ResultSet;

public class coreClass {

    logging logs = new logging();
    databaseCore dbCore = new databaseCore();
    ResultSet rs;
    private static String employeeID = "";
    private static String accountID = "";

    public void insertSuppName(String suppName, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "INSERT INTO supplier(supplierName,  addedOn, addedBy) VALUES('" + suppName + "', NOW(), '" + accountID + "'";
                dbCore.execute(query);
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public void deleteSupplier(String suppName, String accountID) {
        try {
            logs.setupLogger();
            String query = "UPDATE supplier "
                    + "SET deletedOn = NOW(), deletedBy = '" + accountID + "' "
                    + "WHERE supplierName = '" + suppName + "'";
            dbCore.executeUpdate(query);
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
                setEmployeeID(rs.getString("employeeID"));
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

    private void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getAccountID() {
        return this.accountID;
    }
    
    public void addItem(String itemID, String itemDescription, int supplierID, String accountID, double unitPrice, int categoryID, int PWDAllowed, int SeniorAllowed, 
            double sellingPrice, String barcode, String color, String initialSize, int value){
        
    }
}
