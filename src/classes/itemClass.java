package classes;

import forms.addItemForm;
import java.sql.ResultSet;
import java.util.logging.Level;
import javax.swing.JOptionPane;

public class itemClass {

    logging logs = new logging();
    databaseCore dbCore = new databaseCore();
    coreClass core = new coreClass();

    public void addItem(String itemID,
            String itemDescription,
            int supplierID,
            String accountID,
            double unitPrice,
            int categoryID,
            int PWDAllowed,
            int SeniorAllowed,
            double sellingPrice,
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

    public void deleteItem(String itemID, String accountID) {
        try {
            logs.setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "UPDATE itemheader "
                        + "SET deletedOn = NOW(), deletedBy = '" + accountID + "' "
                        + "WHERE itemID = '" + itemID + "'";
                dbCore.execute(query);
            } else {
                JOptionPane.showMessageDialog(null, "Please login before deleteing an item", "Warning", 1);
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public void updateItemDetails(String ItemID, String ItemDesc, String supplier, String itemCategory,
            String UOM, int allowPWD, int allowSC, double sellingPrice) {
        try {
            logs.setupLogger();
            String query = "UPDATE itemheader SET itemDescription = '" + ItemDesc + "', supplierID = '" + getSuppID(supplier) + "', "
                    + "updatedOn = NOW(), updatedBy = '" + core.getAccountID() + "' "
                    + "WHERE itemID = '" + ItemID + "'";
            dbCore.execute(query);
            query = "UPDATE itemdetail SET description = '" + ItemDesc + "', categoryID = '" + getCategoryID(itemCategory) + "', "
                    + "discountPWDAllowed = " + allowPWD + ", discountSCAllowed = " + allowSC + ", "
                    + "updatedOn = NOW(), updatedBy = '" + core.getAccountID() + "'";
            if (!UOM.equalsIgnoreCase("none")) {
                query += ", unitOfMeasure = '" + UOM + "' ";
            }else{
                query += ", unitOfMeasure = ' ' ";
            }
            query += "WHERE itemID = '" + ItemID + "'";
            dbCore.execute(query);
            query = "INSERT INTO itemprice(itemID, value, addedOn, addedBy) "
                    + "VALUES ('" + ItemID + "', " + sellingPrice + ", NOW(), '" + core.getAccountID() + "')";
            dbCore.execute(query);
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }

    public int getSuppID(String description) {
        int suppID = 0;
        try {
            ResultSet rs;
            logs.setupLogger();
            String query = "SELECT Auto_ID "
                    + "FROM itemsupplier "
                    + "WHERE supplierName = '" + description + "' "
                    + "AND deletedOn IS NULL";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                suppID = Integer.parseInt(rs.getString("Auto_ID"));
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
        return suppID;
    }

    public int getCategoryID(String description) {
        int catID = 0;
        try {
            ResultSet rs;
            logs.setupLogger();
            String query = "SELECT * "
                    + "FROM itemcategory "
                    + "WHERE description = '" + description + "' "
                    + "AND deletedOn IS NULL";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                catID = Integer.parseInt(rs.getString("categoryID"));
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
        return catID;
    }

    public void getAllItemDetails(String itemID) {
        try {
            ResultSet rs;
            logs.setupLogger();
            String query = "SELECT "
                    + "    ih.itemID AS `ItemID`, "
                    + "    ih.itemDescription AS `itemDescription`, "
                    + "    s.supplierName AS `supplierName`, "
                    + "    id.unitPrice AS `unitPrice`, "
                    + "    ic.description AS `categoryName`, "
                    + "    id.discountPWDAllowed AS `discountPWDAllowed`, "
                    + "    id.discountSCAllowed AS `discountSCAllowed`, "
                    + "    (SELECT `value` FROM itemprice WHERE itemID =  ih.itemID ORDER BY Auto_ID DESC LIMIT 1) AS `ItemPrice`, "
                    + "    IFNULL(id.unitOfMeasure, '') AS `UOM` "
                    + "FROM itemheader ih "
                    + "JOIN itemdetail id ON ih.itemID = id.itemID "
                    + "JOIN itemcategory ic ON id.categoryID = ic.categoryID "
                    + "JOIN itemsupplier s ON s.Auto_ID = ih.supplierID "
                    + "LEFT JOIN itemprice ip ON ih.itemID = ip.itemID "
                    + "WHERE ih.deletedOn IS NULL "
                    + "AND ih.itemID = '" + itemID + "'";
            rs = dbCore.getResultSet(query);
            if (rs.next()) {
                String itemDescription = rs.getString("itemDescription");
                String supplierName = rs.getString("supplierName");
                String unitPrices = rs.getString("unitPrice");
                String categoryName = rs.getString("categoryName");
                String PWDAllowed = rs.getString("discountPWDAllowed");
                String SeniorAllowed = rs.getString("discountSCAllowed");
                String sellingPrices = rs.getString("ItemPrice");
                String UnitOfMeasure = rs.getString("UOM");
                addItemForm.setItemDetails(itemID, itemDescription, supplierName, unitPrices, categoryName,
                        PWDAllowed, SeniorAllowed, sellingPrices, UnitOfMeasure);
            }
        } catch (Exception e) {
            logs.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            logs.closeLogger();
        }
    }
}
