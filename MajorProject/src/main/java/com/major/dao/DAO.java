package com.major.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;

public class DAO {

	private Connection c;

	public DAO() throws Exception{
		Class.forName("com.mysql.cj.jdbc.Driver");
	    c=DriverManager.getConnection("jdbc:mysql://localhost:3306/medisolweb", "root", "#UDDeshay123");
	}
public void closeDBConnection() throws Exception{
	c.close();
}
public String checkAdminLogin(String id,String password) throws Exception {
	PreparedStatement p=c.prepareStatement("select * from adminlogin where id=? and password=?");
	p.setString(1, id);
	p.setString(2, password);
	ResultSet rs=p.executeQuery();
	if(rs.next()) {
		return rs.getString("name");
	}else {
		return null;
	}
	}
	public String additem(HashMap<String,Object>item) throws Exception {
		PreparedStatement p=c.prepareStatement("insert into items (name,cname,salt,price,qty,image) values(?,?,?,?,?,?)");
		p.setString(1, (String) item.get("name"));
		p.setString(2, (String) item.get("cname"));
		p.setString(3, (String) item.get("salt"));
		p.setDouble(4, (double) item.get("price"));
		p.setInt(5, (int) item.get("qty"));
		p.setBinaryStream(6, (InputStream) item.get("image"));
		try {
			p.executeUpdate();
			return "Item Inserstion success";
		}catch(SQLIntegrityConstraintViolationException e) {
			return"Item Inserstion failed";
		}
	}
	public String addToCart(String uemail,String name) throws Exception {
		PreparedStatement p=c.prepareStatement("insert into cart (email,item_name) values(?,?)");
		p.setString(1, uemail);
		p.setString(2, name);
		p.executeUpdate();
		return "Item Added success";
	}
	public String addAddress( String email,String address) throws Exception {
		PreparedStatement p=c.prepareStatement("insert into address (email,address) values(?,?)");
		p.setString(1, email);
		p.setString(2, address);
		p.executeUpdate();
		return "Address Added success";
	}
	public ArrayList<String> getAddress(String email) throws Exception {
		PreparedStatement p=c.prepareStatement("select * from address where email=?");
		p.setString(1, email);
		ResultSet rs=p.executeQuery();
		ArrayList<String> addresses=new ArrayList<>();
		while(rs.next()) {
			addresses.add(rs.getString("address"));
		}
		return addresses;
	}
	public String placeOrder(String email,String address,double total,String orderItems) throws Exception {
		PreparedStatement p=c.prepareStatement("insert into orders (email,address,ammount,items,status,odate) values(?,?,?,?,'Placed',CURRENT_DATE)");
		p.setString(1, email);
		p.setString(2, address);
		p.setDouble(3, total);
		p.setString(4, orderItems);
		p.executeUpdate();
		p=c.prepareStatement("delete from cart where email=?");
		p.setString(1, email);
		p.executeUpdate();
		return "Order placed success";
	}
	public ArrayList<HashMap> getPlacedOrdersByEmail(String email) throws Exception {
		ArrayList<HashMap> orders=new ArrayList<>();
		PreparedStatement p=c.prepareStatement("select * from orders where email=? and status='placed'");
		p.setString(1, email);
		ResultSet rs=p.executeQuery();
		while(rs.next()) {
			HashMap order=new HashMap();
			order.put("id", rs.getInt("id"));
			order.put("address", rs.getString("address"));
			order.put("amount", rs.getDouble("ammount"));
			order.put("items", rs.getString("items"));
			order.put("odate", rs.getString("odate"));
			order.put("status", rs.getString("status"));
			orders.add(order);
		}
		return orders;
	}
	public String changeOrderStatus(int id,String status) throws Exception {
		PreparedStatement p=c.prepareStatement("update orders set status=? where id=?");
		p.setString(1, status);
		p.setInt(2, id);
		p.executeUpdate();
		return "Order Status Updation success";
	}
	public void itemQtyIncDesc(String name, int a) throws Exception {
		PreparedStatement p=c.prepareStatement("update items set qty=qty-? where name=?");
		p.setInt(1, a);
		p.setString(2, name);
		p.executeUpdate();
	}
	public ArrayList<HashMap> getOrdersByEmail(String email) throws Exception {
		ArrayList<HashMap> orders=new ArrayList<>();
		PreparedStatement p=c.prepareStatement("select * from orders where email=?");
		p.setString(1, email);
		ResultSet rs=p.executeQuery();
		while(rs.next()) {
			HashMap order=new HashMap();
			order.put("id", rs.getInt("id"));
			order.put("address", rs.getString("address"));
			order.put("amount", rs.getDouble("ammount"));
			order.put("items", rs.getString("items"));
			order.put("odate", rs.getString("odate"));
			order.put("status", rs.getString("status"));
			orders.add(order);
		}
		return orders;
	}
		public ArrayList<HashMap> getAllItems() throws Exception {
			ArrayList<HashMap> items=new ArrayList<>();
			PreparedStatement p=c.prepareStatement("select * from items");
			ResultSet rs=p.executeQuery();
			while(rs.next()) {
				HashMap item=new HashMap();
				item.put("name", rs.getString("name"));
				item.put("cname", rs.getString("cname"));
				item.put("qty", rs.getInt("qty"));
				item.put("price", rs.getDouble("price"));
				item.put("salt", rs.getString("salt"));
				items.add(item);
			}
			return items;
		}
		public ArrayList<String> getCartItemByEmail(String email) throws Exception {
			ArrayList<String> items=new ArrayList<>();
			PreparedStatement p=c.prepareStatement("select * from cart where email=?");
			ResultSet rs=p.executeQuery();
			while(rs.next()) {
				
				items.add(rs.getString("item_name"));
				
			}
			return items;
		}
		public byte[] getImage(String name) throws SQLException {
			PreparedStatement p=c.prepareStatement("select image from items where name=?");
		p.setString(1, name);
		ResultSet rs=p.executeQuery();
		if(rs.next()) {
			byte image[]=rs.getBytes("image");
			return image;
		}else {
			return null;
		}
}
		public HashMap getItemByName(String name) throws Exception {
			
			PreparedStatement p=c.prepareStatement("select * from items where name=?");
			p.setString(1, name);
			ResultSet rs=p.executeQuery();
			if(rs.next()) {
				HashMap item=new HashMap();
				item.put("name", rs.getString("name"));
				item.put("cname", rs.getString("cname"));
				item.put("qty", rs.getInt("qty"));
				item.put("price", rs.getDouble("price"));
				item.put("salt", rs.getString("salt"));
				return item;
			}else {
				return null;
			}
			}
			public String ChangeItemImage(String name,InputStream image) throws SQLException {
				PreparedStatement p=c.prepareStatement("update items set image=? where name=?");
			p.setBinaryStream(1, image);
			p.setString(2, name);
			int r=p.executeUpdate();
			if(r!=0) {
				return "Item image updation success";	
			}else {
				return "Item image updation failed";
			}
				
			
			
		}
			public String checkUserLogin(String email,String password) throws Exception {
				PreparedStatement p=c.prepareStatement("select * from users where email=? and password=?");
				p.setString(1, email);
				p.setString(2, password);
				ResultSet rs=p.executeQuery();
				if(rs.next()) {
					return "success";
				}else {
					return "failed";
				}
			}
			public HashMap getuserbyemail(String email) throws Exception {
				
				PreparedStatement p=c.prepareStatement("select * from users where email=?");
				p.setString(1, email);
				ResultSet rs=p.executeQuery();
				if(rs.next()) {
					HashMap users=new HashMap();
					users.put("email", rs.getString("email"));
					users.put("name", rs.getString("name"));
					
					users.put("phone", rs.getString("phone"));
					
					users.put("status", rs.getString("status"));
					return users;
				}else {
					return null;
				}
				}
			public String UpdateItemQty(String name, int qty) throws SQLException {
				
				PreparedStatement p=c.prepareStatement("update items set qty=? where name=?");
				p.setInt(1, qty);
				p.setString(2, name);
				int r=p.executeUpdate();
				if(r!=0) {
					return "Item Quantity updation success";	
				}else {
					return "Item Quantity  updation failed";
				}
			}
				public ArrayList<HashMap> getItemsLike(String name) throws SQLException {
					ArrayList<HashMap> items=new ArrayList<>();
					PreparedStatement p=c.prepareStatement("select * from items where name like ?");
					p.setString(1, "%"+name+"%");
					ResultSet rs=p.executeQuery();
					while(rs.next()) {
						HashMap item=new HashMap();
						item.put("name", rs.getString("name"));
						item.put("cname", rs.getString("cname"));
						item.put("qty", rs.getInt("qty"));
						item.put("price", rs.getDouble("price"));
						item.put("salt", rs.getString("salt"));
						items.add(item);
					}
					return items;
				}
				public String register(String email,String name,String password,String phone) throws Exception {
					PreparedStatement p=c.prepareStatement("insert into users (email,name,password,phone,status) values(?,?,?,?,'Active')");
					p.setString(1, email);
					p.setString(2, name);
					p.setString(3, password);
					p.setString(4, phone);
					
					try {
						p.executeUpdate();
						return "User Registration success";
					}catch(SQLIntegrityConstraintViolationException e) {
						return"User allready exsit";
					}
				}

			public String UpdateItemDetails(String name,String newname, String cname, String salt, double price) throws SQLException {
				PreparedStatement p=c.prepareStatement("update items set name=?,cname=?,salt=?,price=? where name=?");
				
				p.setString(1, newname);
				p.setString(2, cname);
				p.setString(3, salt);
				p.setDouble(4,  price);
				p.setString(5, name);
				int r=p.executeUpdate();
				if(r!=0) {
					return "Item Details updation success";	
				}else {
					return "Item Details  updation failed";
				}
				
			}
			public void removeFromCart(int id) throws Exception {
				PreparedStatement p=c.prepareStatement("delete from cart where id=?");
				p.setInt(1,id);
				p.executeUpdate();
			}

			public int getCartQty(String email) throws Exception {
				PreparedStatement p=c.prepareStatement("select * from cart where email=?");
				p.setString(1, email);
				ResultSet rs=p.executeQuery();
				int qty=0;
				while(rs.next()) {
					qty++;
				}
				return qty;
			}
			public HashMap<Integer,String> getCartItemsByEmail(String email) throws Exception {
				HashMap<Integer,String> items=new HashMap<>();
				PreparedStatement p=c.prepareStatement("select * from cart where email=?");
				p.setString(1, email);
				ResultSet rs=p.executeQuery();
				while(rs.next()) {
					items.put(rs.getInt("id"),rs.getString("item_name"));
				}
				return items;
			}
			
}
