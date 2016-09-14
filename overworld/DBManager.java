package overworld;

import java.sql.*;

public class DBManager {

    /*
        Manages all operations related to SQLite databases
        Can only access one database at a time

        Notes:
            For efficiency, delete and rewrite all tables when saving
            Autocommit MUST be off or save will never end
            Batching insert operations will speed operations up massively
     */

    private Connection c;
    private Statement statement;

    private int batchNum = 0, mapSize;
    private PreparedStatement ps;

    DBManager(String name) {

        String URL = "jdbc:sqlite:src/data/" + name + ".db";

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(URL);
            statement = c.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables() throws SQLException {

        /*
            Creates needed tables in the database
            ID is needed in ALL tables to be able to update records
         */

        String sql = "CREATE TABLE IF NOT EXISTS WORLD_DATA (TYPE varchar(255), SUBTYPE varchar(255), BRANCH varchar(1), NAME varchar(255), RELATIONSHIP int)";
        statement.execute(sql);
    }

    public ResultSet selectFromDatabase(String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        return statement.executeQuery(sql);
    }

    public void insertIntoTable_WORLD_DATA(String type, String subType, String branch, String name, int relationship) throws SQLException {

        /*
            Makes batches of mapSize queries to process at once (speeds up save time A LOT)
         */

        if (batchNum == 0)
            ps = c.prepareStatement("INSERT INTO WORLD_DATA VALUES(?, ?, ?, ?, ?)");

        if (batchNum >= mapSize) {
            ps.executeBatch();
            batchNum = 0;
        } else {
            ps.setString(1, type);
            ps.setString(2, subType);
            ps.setString(3, branch);
            ps.setString(4, name);
            ps.setInt(5, relationship);
            ps.addBatch();
            batchNum++;
        }
    }

    public void deleteTable(String tableName) throws SQLException {
        String sql = "DROP TABLE " + tableName;
        statement.execute(sql);
    }

    public void setAutoCommit(boolean state) throws SQLException {
        c.setAutoCommit(state);
    }

    public void commit() throws SQLException {
        c.commit();
    }

    public void closeDatabase() {
        try {
            c.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }
}
