/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import Models.Product;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import Models.Account;
import Models.Feedback;
import Models.OrderDetails;
import Models.OrderModel;
import Models.Orders;
import Models.ProductDescription;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

/**
 *
 * @author NTD
 */
public class DAO extends BaseDAO {

    @Override
    public Account getUser(String email, String password) {
        try {
            String query = "SELECT [userName]\n"
                    + "      ,[userPassword]\n"
                    + "      ,[phone]\n"
                    + "      ,[email]\n"
                    + "      ,[authority]\n"
                    + "  FROM [dbo].[HE141062_UserAccount]\n"
                    + "where email = ? and userPassword = ?\n"
                    + "\n";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (email.equalsIgnoreCase(rs.getString(4))
                        && password.equalsIgnoreCase(rs.getString(2))) {
                    Account user = new Account();
                    user.setUsername(rs.getString(1));
                    user.setPassword(rs.getString(2));
                    user.setPhone(rs.getString(3));
                    user.setEmail(rs.getString(4));
                    user.setAuthority(rs.getInt(5));
                    return user;
                } else {
                    return null;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String getPass(String email) {
        String pass = "";
        try {
            String query = "select userpassword from  HE141062_UserAccount\n"
                    + "WHERE EMAIL = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                 pass = rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pass;
    }

    @Override
    public ArrayList<Product> getAllProduct() {

        ArrayList<Product> lst = new ArrayList<>();
        try {
            String query = "SELECT [ProductID]\n"
                    + "      ,[ProductName]\n"
                    + "      ,[CategoryID]\n"
                    + "      ,[UnitPrice]\n"
                    + "      ,[UnitsInStock]\n"
                    + "      ,[UnitsOnOrder]\n"
                    + "      ,[Url]\n"
                    + "      "
                    + "  FROM [dbo].[HE141062_Product]";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pdID = rs.getInt(1);
                String pdName = rs.getString(2);
                int category = rs.getInt(3);
                BigDecimal unitPrice = rs.getBigDecimal(4);
                int unitStock = rs.getInt(5);
                int unitOnorder = rs.getInt(6);
                String url = rs.getString(7);
                lst.add(new Product(pdID, pdName, category, unitPrice, unitStock, unitOnorder, url));
            }
            return lst;

        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public List<Product> getAllPagging(int pageIndex, int pageSize) {
        try {
            String query = "SELECT * FROM HE141062_Product ORDER BY ProductID "
                    + "OFFSET (?*?-?) ROWS FETCH NEXT ? ROWS ONLY";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, pageIndex);
            ps.setInt(2, pageSize);
            ps.setInt(3, pageSize);
            ps.setInt(4, pageSize);
            ResultSet rs = ps.executeQuery();

            List<Product> lst = new ArrayList<>();
            while (rs.next()) {
                int pdID = rs.getInt(1);
                String pdName = rs.getString(2);
                int category = rs.getInt(3);
                BigDecimal unitPrice = rs.getBigDecimal(4);
                int unitStock = rs.getInt(5);
                int unitOnorder = rs.getInt(6);
                String url = rs.getString(7);
                lst.add(new Product(pdID, pdName, category, unitPrice, unitStock, unitOnorder, url));

            }
            return lst;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }

    public int countPage(int pageSize) {
        try {
            String query = "select Count(*) from HE141062_Product";

            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            int numOfPage = count / pageSize;
            if (count % pageSize != 0) {
                numOfPage++;
            }
            return numOfPage;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return 0;
    }

//    @Override
//    public ArrayList getNewProduct() {
//        ArrayList<Product> lst = new ArrayList<>();
//        try {
//            String query = "SELECT TOP 15 * FROM HE141062_Product";
//            PreparedStatement ps = connection.prepareStatement(query);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                int pdID = rs.getInt(1);
//                String pdName = rs.getString(2);
//                int category = rs.getInt(3);
//                BigDecimal unitPrice = rs.getBigDecimal(4);
//                int unitStock = rs.getInt(5);
//                int unitOnorder = rs.getInt(6);
//                String url = rs.getString(7);
//                lst.add(new Product(pdID, pdName, category, unitPrice, unitStock, unitOnorder, url));
//            }
//            return lst;
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    @Override
    public int register(String username, String email, String password) {
        try {
            String query = "INSERT INTO [dbo].[HE141062_UserAccount]\n"
                    + "           ([userName]\n"
                    + "           ,[userPassword]\n"
                    + "           ,[email]\n"
                    + "           ,[authority])\n"
                    + "     VALUES\n"
                    + "           (?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setInt(4, 2);
            return ps.executeUpdate();

        } catch (SQLException ex) {
            return 0;
        }
    }

    @Override
    public Product getoneProduct(int id) {
        Product p = null;
        try {
            String query = "SELECT [ProductID]\n"
                    + "      ,[ProductName]\n"
                    + "      ,[CategoryID]\n"
                    + "      ,[UnitPrice]\n"
                    + "      ,[UnitsInStock]\n"
                    + "      ,[UnitsOnOrder]\n"
                    + "      ,[Url]\n"
                    + "  FROM [dbo].[HE141062_Product]"
                    + " Where ProductID=?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pdID = rs.getInt(1);
                if (pdID == id) {
                    String pdName = rs.getString(2);
                    int category = rs.getInt(3);
                    BigDecimal unitPrice = rs.getBigDecimal(4);
                    int unitStock = rs.getInt(5);
                    int unitOnorder = rs.getInt(6);
                    String url = rs.getString(7);
                    p = new Product(pdID, pdName, category, unitPrice, unitStock, unitOnorder, url);
                }
            }

            return p;

        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ArrayList getEachTypeProduct(int type) {
        ArrayList<Product> lst = new ArrayList<>();
        try {
            String query = "Select * from HE141062_Product\n"
                    + "where HE141062_Product.CategoryID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pdID = rs.getInt(1);
                String pdName = rs.getString(2);
                int category = rs.getInt(3);
                BigDecimal unitPrice = rs.getBigDecimal(4);
                int unitStock = rs.getInt(5);
                int unitOnorder = rs.getInt(6);
                String url = rs.getString(7);
                lst.add(new Product(pdID, pdName, category, unitPrice, unitStock, unitOnorder, url));
            }
            return lst;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public ArrayList getEachTypeProduct2(int type, int pageIndex, int pageSize) {
        ArrayList<Product> lst = new ArrayList<>();
        try {
            String query = "with t as (select * from HE141062_Product where CategoryID = ?)\n"
                    + "SELECT * FROM t ORDER BY t.ProductID \n"
                    + "  OFFSET (?*?-?) ROWS FETCH NEXT ? ROWS ONLY";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, type);
            ps.setInt(2, pageIndex);
            ps.setInt(3, pageSize);
            ps.setInt(4, pageSize);
            ps.setInt(5, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pdID = rs.getInt(1);
                String pdName = rs.getString(2);
                int category = rs.getInt(3);
                BigDecimal unitPrice = rs.getBigDecimal(4);
                int unitStock = rs.getInt(5);
                int unitOnorder = rs.getInt(6);
                String url = rs.getString(7);
                lst.add(new Product(pdID, pdName, category, unitPrice, unitStock, unitOnorder, url));
            }
            return lst;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int countPageType(int pageSize, int type) {
        try {
            String query = "with t as (select * from HE141062_Product where CategoryID = 2)\n"
                    + "select Count(*) from t";

            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            int numOfPage = count / pageSize;
            if (count % pageSize != 0) {
                numOfPage++;
            }
            return numOfPage;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return 0;
    }

    @Override
    public ArrayList getAllAccount() {
        ArrayList<Account> lst = new ArrayList<>();
        try {
            String query = "Select * from HE141062_UserAccount";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String userName = rs.getString(1);
                String userPass = rs.getString(2);
                String userPhone = rs.getString(3);
                String userEmail = rs.getString(4);
                int type = rs.getInt(5);

                lst.add(new Account(userName, userPass, userPhone, userEmail, type));
            }
            return lst;

        } catch (SQLException ex) {
            return null;
        }
    }

    @Override
    public ArrayList latestProduct() {
        ArrayList<Product> lst = new ArrayList<>();
        try {
            String query = "select top 10 * from HE141062_Product";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pdID = rs.getInt(1);
                String pdName = rs.getString(2);
                int category = rs.getInt(3);
                BigDecimal unitPrice = rs.getBigDecimal(4);
                int unitStock = rs.getInt(5);
                int unitOnorder = rs.getInt(6);
                String url = rs.getString(7);
                lst.add(new Product(pdID, pdName, category, unitPrice, unitStock, unitOnorder, url));
            }
            return lst;

        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void EditInformationUser(String phone, String username, String email) {
        String query = "UPDATE [dbo].[HE141062_UserAccount]\n"
                + "   SET phone = ?, userName=?\n"
                + " WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, phone);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ProductDescription GetProductDesCrip(int id) {
        ProductDescription p = null;
        try {
            String query = "Select * from   HE141062_ProductDescription\n"
                    + "Where ProductID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String manhinh = rs.getString(1);
                String camerasau = rs.getString(2);
                String cameratruoc = rs.getString(3);
                String cpu = rs.getString(4);
                String ram = rs.getString(5);
                String bonhotrong = rs.getString(6);
                String sim = rs.getString(7);
                String sanxuattai = rs.getString(8);
                String heieuhanh = rs.getString(9);
                String chongnuoc = rs.getString(10);
                int ProductID = rs.getInt(11);

                p = new ProductDescription(ProductID, manhinh, camerasau, cameratruoc, cpu, ram, bonhotrong, sim, sanxuattai, heieuhanh, chongnuoc);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;

    }

    @Override
    public void InsertOrder(String Email, String receiverName, int phone, String address, Date orderdate, String status, String note, BigDecimal totalmoney) {
        String query = "INSERT INTO [dbo].[HE141062_Orders]\n"
                + "           ([userMail]\n"
                + "           ,[nameReceiver]\n"
                + "           ,[phone]\n"
                + "           ,[address]\n"
                + "           ,[orderDate]\n"
                + "           ,[status]\n"
                + "           ,[note]\n"
                + "           ,[totalMoney])\n"
                + "     VALUES\n"
                + "           (?,?,?,?,?,?,?,?)\n";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, Email);
            ps.setString(2, receiverName);
            ps.setInt(3, phone);
            ps.setString(4, address);
            ps.setDate(5, orderdate);
            ps.setString(6, status);
            ps.setString(7, note);
            ps.setBigDecimal(8, totalmoney);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void InsertOrderDetails(int ProductID, int quantity, BigDecimal amount, String urlPRD) {
        String query = "Select IDENT_CURRENT ('HE141062_Orders')";
        int OrderID = -1;
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderID = rs.getInt(1);
            }
        } catch (Exception e) {
        }
        query = "INSERT INTO [dbo].[HE141062_OrderDetails]\n"
                + "           ([orderID]\n"
                + "           ,[productID]\n"
                + "           ,[quantity]\n"
                + "           ,[amount]\n"
                + "           ,[urlProduct])\n"
                + "     VALUES\n"
                + "           (?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, OrderID);
            ps.setInt(2, ProductID);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, amount);
            ps.setString(5, urlPRD);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        DAO k = new DAO();
        System.out.println(k.getoneProduct(6));
    }

    @Override
    public void UpdateStatusOrder(int OrderID, String status) {
        String query = "UPDATE [dbo].[HE141062_Orders]\n"
                + "   SET status = ?\n"
                + " WHERE orderID = ?\n";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, OrderID);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<OrderModel> getOrderUser(String email) {
        ArrayList<OrderModel> lstOrderModel = new ArrayList<>();
        String query = "Select *\n"
                + "From HE141062_Orders join HE141062_OrderDetails on HE141062_Orders.orderID = HE141062_OrderDetails.orderID\n"
                + "where HE141062_Orders.userMail=?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt(1);
                String email1 = rs.getString(2);
                String nameReceiver = rs.getString(3);
                String phone = rs.getString(4);
                String address = rs.getString(5);
                Date orderdate = rs.getDate(6);
                String status = rs.getString(7);
                String note = rs.getString(8);
                BigDecimal totalmoney = rs.getBigDecimal(9);

                int productid = rs.getInt(11);
                int quantity = rs.getInt(12);
                BigDecimal amount = rs.getBigDecimal(13);
                String urlProduct = rs.getString(14);

                Orders order = new Orders(orderID, email1, nameReceiver, phone, address, orderdate, status, note, totalmoney);
                OrderDetails orderdetails = new OrderDetails(orderID, productid, quantity, amount, urlProduct);

                lstOrderModel.add(new OrderModel(order, orderdetails));

            }
            return lstOrderModel;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void InsertFeedback(String Email, String name, String phone, String title, String content) {
        DAO dao = new DAO();
        String query = "INSERT INTO [dbo].[HE141062_FeedBack]\n"
                + "           ([email]\n"
                + "           ,[name]\n"
                + "           ,[phone]\n"
                + "           ,[tile]\n"
                + "           ,[contentFB])\n"
                + "     VALUES\n"
                + "           (?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, Email);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, title);
            ps.setString(5, content);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void InsertFeedbackAProduct(String Email, String name, String phone, String title, String content, int ProductID) {
        DAO dao = new DAO();
        String query = "INSERT INTO [dbo].[HE141062_FeedBack_a_product]\n"
                + "           ([email]\n"
                + "           ,[name]\n"
                + "           ,[phone]\n"
                + "           ,[tile]\n"
                + "           ,[contentFB]\n"
                + "           ,[ProductID])\n"
                + "     VALUES\n"
                + "           (?,?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, Email);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, title);
            ps.setString(5, content);
            ps.setInt(6, ProductID);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Product> SortProduct(int categoryID, String typeSort) {
        ArrayList<Product> lst = new ArrayList<>();
        try {
            String query = "Select * from HE141062_Product\n";
            if (categoryID != 0 && categoryID != -1) {
                query += "where CategoryID = " + categoryID;
            }else if(categoryID == -1){
                query = "select top 10 * from HE141062_Product\n";
            }
            query += "order by UnitPrice " + typeSort;
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pdID = rs.getInt(1);
                String pdName = rs.getString(2);
                int category = rs.getInt(3);
                BigDecimal unitPrice = rs.getBigDecimal(4);
                int unitStock = rs.getInt(5);
                int unitOnorder = rs.getInt(6);
                String url = rs.getString(7);
                lst.add(new Product(pdID, pdName, category, unitPrice, unitStock, unitOnorder, url));
            }
            return lst;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ArrayList<Feedback> getFeedback(String typeFB) {
        DAO dao = new DAO();
        ArrayList<Feedback> lst = new ArrayList<>();
        String query = "Select * from HE141062_FeedBack";
        if (typeFB.equalsIgnoreCase("Product")) {
            query += "_a_product";
        }
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String email = rs.getString(1);
                String name = rs.getString(2);
                String phone = rs.getString(3);
                String title = rs.getString(4);
                String content = rs.getString(5);
                int ProductID = -1;
                if (typeFB.equalsIgnoreCase("Product")) {
                    ProductID = rs.getInt(6);
                }
                lst.add(new Feedback(email, name, phone, title, content, ProductID));
            }
            return lst;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ArrayList<OrderModel> getAllOrder() {
        ArrayList<OrderModel> lstOrderModel = new ArrayList<>();
        String query = "Select *\n"
                + "From HE141062_Orders join HE141062_OrderDetails on HE141062_Orders.orderID = HE141062_OrderDetails.orderID";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt(1);
                String email = rs.getString(2);
                String nameReceiver = rs.getString(3);
                String phone = rs.getString(4);
                String address = rs.getString(5);
                Date orderdate = rs.getDate(6);
                String status = rs.getString(7);
                String note = rs.getString(8);
                BigDecimal totalmoney = rs.getBigDecimal(9);

                int productid = rs.getInt(11);
                int quantity = rs.getInt(12);
                BigDecimal amount = rs.getBigDecimal(13);
                String urlProduct = rs.getString(14);

                Orders order = new Orders(orderID, email, nameReceiver, phone, address, orderdate, status, note, totalmoney);
                OrderDetails orderdetails = new OrderDetails(orderID, productid, quantity, amount, urlProduct);

                lstOrderModel.add(new OrderModel(order, orderdetails));

            }
            return lstOrderModel;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void DeleteUser(String Email) {
        try {
            String query = "Delete HE141062_UserAccount where email = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, Email);
            ps.executeUpdate();
        } catch (SQLException ex) {
        }

    }

    @Override
    public void ChangeTypeUser(String Email, int authority) {
        try {
            String query = "UPDATE [dbo].[HE141062_UserAccount]\n"
                    + "   SET \n"
                    + "      [authority] = ?\n"
                    + " WHERE email = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, authority);
            ps.setString(2, Email);
            ps.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public ArrayList<OrderDetails> getAllordersDetails() {
        ArrayList<OrderDetails> lstOrdersdetails = new ArrayList<>();
        String query = "Select *\n"
                + "from HE141062_OrderDetails";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt(1);
                int productID = rs.getInt(2);
                int quantity = rs.getInt(3);
                BigDecimal amount = rs.getBigDecimal(4);
                String urlPRD = rs.getString(5);
                OrderDetails o = new OrderDetails(orderID, productID, quantity, amount, urlPRD);
                lstOrdersdetails.add(o);
            }
            return lstOrdersdetails;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void UpdateProductOrder(int productID, int newUnitsOrder) {
        String query = "Update HE141062_Product\n"
                + "set UnitsOnOrder = ?\n"
                + "where ProductID = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, newUnitsOrder);
            ps.setInt(2, productID);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void UpdateProduct(Product p, ProductDescription pdes) {
        try {
            String query = "UPDATE [dbo].[HE141062_Product]\n"
                    + "   SET [ProductName] = ?\n"
                    + "      ,[UnitPrice] = ?\n"
                    + "      ,[UnitsInStock] = ?\n"
                    + "      ,[Url] = ?\n"
                    + " WHERE ProductID = ?\n"
                    + "UPDATE [dbo].[HE141062_ProductDescription]\n"
                    + "   SET [manhinh] = ?\n"
                    + "      ,[camerasau] = ?\n"
                    + "      ,[cameratruoc] = ?\n"
                    + "      ,[cpu] = ?\n"
                    + "      ,[ram] = ?\n"
                    + "      ,[bonhotrong] = ?\n"
                    + "      ,[sim] = ?\n"
                    + "      ,[sanxuattai] = ?\n"
                    + "      ,[heieuhanh] = ?\n"
                    + "      ,[chongnuoc] = ?\n"
                    + " WHERE [ProductID] = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps = connection.prepareStatement(query);
            ps.setString(1, p.getProductName());
            ps.setBigDecimal(2, p.getUnitPrice());
            ps.setInt(3, p.getUnitsinStock());
            ps.setString(4, p.getUrl());
            ps.setInt(5, p.getProductID());
            ps.setString(6, pdes.getManhinh());
            ps.setString(7, pdes.getCamerasau());
            ps.setString(8, pdes.getCameratruoc());
            ps.setString(9, pdes.getCpu());
            ps.setString(10, pdes.getRam());
            ps.setString(11, pdes.getBonhotrong());
            ps.setString(12, pdes.getSim());
            ps.setString(13, pdes.getSanxuattai());
            ps.setString(14, pdes.getHeieuhanh());
            ps.setString(15, pdes.getChongnuoc());
            ps.setInt(16, pdes.getProductID());
            ps.executeUpdate();

        } catch (SQLException ex) {
        }
    }

    @Override
    public void DeleteProduct(int id) {
        try {
            String query = "Delete HE141062_ProductDescription\n"
                    + "where ProductID = ?\n"
                    + "Delete HE141062_Product\n"
                    + "where ProductID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public void InsertProduct(Product p, ProductDescription pdes) {
        try {
            String query = "INSERT INTO [dbo].[HE141062_Product]\n"
                    + "           ([ProductName]\n"
                    + "           ,[CategoryID]\n"
                    + "           ,[UnitPrice]\n"
                    + "           ,[UnitsInStock]\n"
                    + "           ,[UnitsOnOrder]\n"
                    + "           ,[Url])\n"
                    + "     VALUES\n"
                    + "           (?,?,?,?,?,?)";
            int lastValue = 0;
            PreparedStatement ps = connection.prepareStatement(query);
            ps = connection.prepareStatement(query);
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryID());
            ps.setBigDecimal(3, p.getUnitPrice());
            ps.setInt(4, p.getUnitsinStock());
            ps.setInt(5, 0);
            ps.setString(6, "images/imgProduct/" + p.getUrl());
            ps.executeUpdate();

            query = "SELECT IDENT_CURRENT('HE141062_Product')";
            ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lastValue = rs.getInt(1);
            }
            query = "INSERT INTO [dbo].[HE141062_ProductDescription]\n"
                    + "           ([manhinh]\n"
                    + "           ,[camerasau]\n"
                    + "           ,[cameratruoc]\n"
                    + "           ,[cpu]\n"
                    + "           ,[ram]\n"
                    + "           ,[bonhotrong]\n"
                    + "           ,[sim]\n"
                    + "           ,[sanxuattai]\n"
                    + "           ,[heieuhanh]\n"
                    + "           ,[chongnuoc]\n"
                    + "           ,[ProductID])\n"
                    + "     VALUES\n"
                    + "           (?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ," + lastValue + ")";
            ps = connection.prepareStatement(query);
            ps.setString(1, pdes.getManhinh());
            ps.setString(2, pdes.getCamerasau());
            ps.setString(3, pdes.getCameratruoc());
            ps.setString(4, pdes.getCpu());
            ps.setString(5, pdes.getRam());
            ps.setString(6, pdes.getBonhotrong());
            ps.setString(7, pdes.getSim());
            ps.setString(8, pdes.getSanxuattai());
            ps.setString(9, pdes.getHeieuhanh());
            ps.setString(10, pdes.getChongnuoc());
            ps.executeUpdate();
        } catch (SQLException ex) {
        }
    }
}
