package classes;

import forms.addItemForm;
import java.sql.ResultSet;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import java.sql.*;

public class itemClass extends dbConnect {

    Connection con = con();
    coreClass core = new coreClass();
    PreparedStatement pst;
    ResultSet rs;

    public void addItem(String itemID,
            String itemDescription,
            int supplierID,
            String accountID,
            double unitPrice,
            int categoryID,
            int PWDAllowed,
            int SeniorAllowed,
            double sellingPrice,
            String UOMText,
            int isVatable,
            int totalDisc) {
        try {
            setupLogger();

            if (accountID != null && !accountID.isBlank()) {
                // INSERT INTO itemheader
                String query1 = "INSERT INTO itemheader(itemID, itemDescription, supplierID, addedOn, addedBy) "
                        + "VALUES (?, ?, ?, NOW(), ?)";
                PreparedStatement pst1 = con.prepareStatement(query1);
                pst1.setString(1, itemID);
                pst1.setString(2, itemDescription.toUpperCase());
                pst1.setInt(3, supplierID);
                pst1.setString(4, accountID);
                pst1.executeUpdate();

                // INSERT INTO itemdetail
                String query2;
                PreparedStatement pst2;
                if (UOMText != null) {
                    query2 = "INSERT INTO itemdetail(itemID, unitPrice, categoryID, description, discountPWDAllowed, discountSCAllowed, unitOfMeasure, addedOn, addedBy, isVatable, totalDiscountAllowed) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?)";
                    pst2 = con.prepareStatement(query2);
                    pst2.setString(1, itemID);
                    pst2.setDouble(2, unitPrice);
                    pst2.setInt(3, categoryID);
                    pst2.setString(4, itemDescription.toUpperCase());
                    pst2.setInt(5, PWDAllowed);
                    pst2.setInt(6, SeniorAllowed);
                    pst2.setString(7, UOMText);
                    pst2.setString(8, accountID);
                    pst2.setInt(9, isVatable);
                    pst2.setInt(10, totalDisc);
                } else {
                    query2 = "INSERT INTO itemdetail(itemID, unitPrice, categoryID, description, discountPWDAllowed, discountSCAllowed, addedOn, addedBy, isVatable, totalDiscountAllowed) "
                            + "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?)";
                    pst2 = con.prepareStatement(query2);
                    pst2.setString(1, itemID);
                    pst2.setDouble(2, unitPrice);
                    pst2.setInt(3, categoryID);
                    pst2.setString(4, itemDescription.toUpperCase());
                    pst2.setInt(5, PWDAllowed);
                    pst2.setInt(6, SeniorAllowed);
                    pst2.setString(7, accountID);
                    pst2.setInt(8, isVatable);
                    pst2.setInt(9, totalDisc);
                }
                pst2.executeUpdate();

                // INSERT INTO itemprice
                String query3 = "INSERT INTO itemprice(itemID, value, addedOn, addedBy) VALUES (?, ?, NOW(), ?)";
                PreparedStatement pst3 = con.prepareStatement(query3);
                pst3.setString(1, itemID);
                pst3.setDouble(2, sellingPrice);
                pst3.setString(3, accountID);
                pst3.executeUpdate();

                // Optional: Clean up
                pst1.close();
                pst2.close();
                pst3.close();
            } else {
                JOptionPane.showMessageDialog(null, "Please login before adding an item.", "Warning", 1);
            }
        } catch (Exception e) {
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            closeLogger();
        }
    }

    public boolean isItemExist(String ItemID) {
        boolean isExist = false;
        try {
            setupLogger();
            String checkQuery = "SELECT * FROM itemheader WHERE itemID = ? AND deletedOn IS NULL";
            pst = con.prepareStatement(checkQuery);
            pst.setString(1, ItemID);
            rs = pst.executeQuery();

            if (rs.next()) {
                isExist = true;
            }

            pst.close();
        } catch (Exception e) {
            logging.logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            closeLogger();
        }

        return isExist;
    }

    public void deleteItem(String itemID, String accountID) {
        try {
            setupLogger();
            if (!accountID.isBlank() || !accountID.isEmpty()) {
                String query = "UPDATE itemheader "
                        + "SET deletedOn = NOW(), deletedBy = ? "
                        + "WHERE itemID = ? AND deletedOn IS NULL";
                pst = con.prepareStatement(query);
                pst.setString(1, accountID);
                pst.setString(2, itemID);
                pst.executeUpdate();

                String detailQuery = "UPDATE itemdetail "
                        + "SET deletedOn = NOW(), deletedBy = ? "
                        + "WHERE itemID = ? AND deletedOn IS NULL";
                pst = con.prepareStatement(detailQuery);
                pst.setString(1, accountID);
                pst.setString(2, itemID);
                pst.executeUpdate();

                String itemBarcodeQuery = "UPDATE itembarcode "
                        + "SET deletedOn = NOW(), deletedBy = ? "
                        + "WHERE itemID = ? AND deletedOn IS NULL";
                pst = con.prepareStatement(itemBarcodeQuery);
                pst.setString(1, accountID);
                pst.setString(2, itemID);
                pst.executeUpdate();
                
                String itemPriceQuery = "UPDATE itemprice "
                        + "SET deletedOn = NOW(), deletedBy = ? "
                        + "WHERE itemID = ? AND deletedOn IS NULL";
                pst = con.prepareStatement(itemPriceQuery);
                pst.setString(1, accountID);
                pst.setString(2, itemID);
                pst.executeUpdate();
            } else {
                JOptionPane.showMessageDialog(null, "Please login before deleteing an item", "Warning", 1);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            closeLogger();
        }
    }

    public void updateItemDetails(String ItemID, String ItemDesc, String supplier, String itemCategory,
            String UOM, int allowPWD, int allowSC, double sellingPrice,
            int isVatable, int totalDisc) {
        try {
            setupLogger();

            // UPDATE itemheader
            String query1 = "UPDATE itemheader SET itemDescription = ?, supplierID = ?, "
                    + "updatedOn = NOW(), updatedBy = ? WHERE itemID = ? AND deletedOn IS NULL";
            PreparedStatement pst1 = con.prepareStatement(query1);
            pst1.setString(1, ItemDesc.toUpperCase());
            pst1.setInt(2, getSuppID(supplier));
            pst1.setString(3, core.getAccountID());
            pst1.setString(4, ItemID);
            pst1.executeUpdate();

            // UPDATE itemdetail
            String query2 = "UPDATE itemdetail SET description = ?, categoryID = ?, discountPWDAllowed = ?, "
                    + "discountSCAllowed = ?, updatedOn = NOW(), updatedBy = ?, isVatable = ?, "
                    + "totalDiscountAllowed = ?, unitOfMeasure = ? WHERE itemID = ? AND deletedOn IS NULL";

            PreparedStatement pst2 = con.prepareStatement(query2);
            pst2.setString(1, ItemDesc.toUpperCase());
            pst2.setInt(2, getCategoryID(itemCategory));
            pst2.setInt(3, allowPWD);
            pst2.setInt(4, allowSC);
            pst2.setString(5, core.getAccountID());
            pst2.setInt(6, isVatable);
            pst2.setInt(7, totalDisc);
            if (UOM != null) {
                pst2.setString(8, UOM);
            } else {
                pst2.setNull(8, java.sql.Types.VARCHAR);
            }
            pst2.setString(9, ItemID);
            pst2.executeUpdate();

            // INSERT INTO itemprice
            String query3 = "INSERT INTO itemprice(itemID, value, addedOn, addedBy) VALUES (?, ?, NOW(), ?)";
            PreparedStatement pst3 = con.prepareStatement(query3);
            pst3.setString(1, ItemID);
            pst3.setDouble(2, sellingPrice);
            pst3.setString(3, core.getAccountID());
            pst3.executeUpdate();

            // Clean up
            pst1.close();
            pst2.close();
            pst3.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            closeLogger();
        }
    }

    public int getSuppID(String description) {
        int suppID = 0;
        try {
            setupLogger();
            String query = "SELECT Auto_ID "
                    + "FROM itemsupplier "
                    + "WHERE supplierName = ? "
                    + "AND deletedOn IS NULL";
            pst = con.prepareStatement(query);
            pst.setString(1, description);
            rs = pst.executeQuery();
            if (rs.next()) {
                suppID = rs.getInt("Auto_ID");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            closeLogger();
        }
        return suppID;
    }

    public int getCategoryID(String description) {
        int catID = 0;
        try {
            setupLogger();
            String query = "SELECT * "
                    + "FROM itemcategory "
                    + "WHERE description = ? "
                    + "AND deletedOn IS NULL";
            pst = con.prepareStatement(query);
            pst.setString(1, description);
            rs = pst.executeQuery();
            if (rs.next()) {
                catID = rs.getInt("categoryID");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            closeLogger();
        }
        return catID;
    }

    public void getAllItemDetails(String itemID) {
        try {
            setupLogger();
            String query = "SELECT "
                    + "    ih.itemID AS `ItemID`, "
                    + "    ih.itemDescription AS `itemDescription`, "
                    + "    s.supplierName AS `supplierName`, "
                    + "    id.unitPrice AS `unitPrice`, "
                    + "    ic.description AS `categoryName`, "
                    + "    id.discountPWDAllowed AS `discountPWDAllowed`, "
                    + "    id.discountSCAllowed AS `discountSCAllowed`, "
                    + "    (SELECT `value` FROM itemprice WHERE itemID =  ih.itemID ORDER BY Auto_ID DESC LIMIT 1) AS `ItemPrice`, "
                    + "    IFNULL(id.unitOfMeasure, '') AS `UOM`,"
                    + "    isVatable, totalDiscountAllowed "
                    + "FROM itemheader ih "
                    + "JOIN itemdetail id ON ih.itemID = id.itemID "
                    + "LEFT JOIN itemcategory ic ON id.categoryID = ic.categoryID "
                    + "JOIN itemsupplier s ON s.Auto_ID = ih.supplierID "
                    + "LEFT JOIN itemprice ip ON ih.itemID = ip.itemID "
                    + "WHERE ih.deletedOn IS NULL "
                    + "AND ih.itemID = ? "
                    + "AND id.deletedOn IS NULL ";
            pst = con.prepareStatement(query);
            pst.setString(1, itemID);
            rs = pst.executeQuery();
            if (rs.next()) {
                String itemDescription = rs.getString("itemDescription");
                String supplierName = rs.getString("supplierName");
                String unitPrices = rs.getString("unitPrice");
                String categoryName = rs.getString("categoryName");
                String PWDAllowed = rs.getString("discountPWDAllowed");
                String SeniorAllowed = rs.getString("discountSCAllowed");
                String sellingPrices = rs.getString("ItemPrice");
                String UnitOfMeasure = rs.getString("UOM");
                String isVat = rs.getString("isVatable");
                String totalDisc = rs.getString("totalDiscountAllowed");
                addItemForm.setItemDetails(itemID, itemDescription, supplierName, unitPrices, categoryName,
                        PWDAllowed, SeniorAllowed, sellingPrices, UnitOfMeasure, isVat, totalDisc);
            }
            pst.close();
            rs.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            closeLogger();
        }
    }

    public boolean isBarcodeUsed(String barcode) {
        boolean result = false;
        try {
            String query = "SELECT * "
                    + "FROM itembarcode "
                    + "WHERE barcode = ? "
                    + "AND deletedOn IS NULL ";
            pst = con.prepareStatement(query);
            pst.setString(1, barcode);
            rs = pst.executeQuery();
            if (rs.next()) {
                result = true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception occurred", e);
        } finally {
            closeLogger();
        }

        return result;
    }
}
