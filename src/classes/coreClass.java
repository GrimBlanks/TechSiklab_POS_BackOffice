package classes;

import java.util.logging.Level;

public class coreClass {

    logging logs = new logging();
    databaseCore dbCore = new databaseCore();

    public void insertSuppName(String suppName) {
        try {
            logs.setupLogger();
            String query = "INSERT INTO supplier(supplierName) VALUES('" + suppName + "')";
            dbCore.execute(query);
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
}
