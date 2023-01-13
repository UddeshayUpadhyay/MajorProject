package com.major.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.major.dao.DAO;

/**
 * Servlet implementation class removeFromCart
 */
@WebServlet("/removeFromCart")
public class removeFromCart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			int id=Integer.parseInt(request.getParameter("id"));
			
			HttpSession session=request.getSession();
			String uemail=(String)session.getAttribute("uemail");
			if(uemail==null) {
				session.setAttribute("msg", "Please Login first!");
				response.sendRedirect("UserLogin.jsp");
			}else {
				DAO db=new DAO();
				db.removeFromCart(id);
				db.closeDBConnection();
				response.sendRedirect("cart.jsp");
			}
		}catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("ExceptionPage.jsp");
		}
	}

}
