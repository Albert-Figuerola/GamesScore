package cat.xtec.ioc.dawm07eac2biblioteca;

import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.json.JSONObject;

/**
 *
 * @author Albert Figuerola
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/user"})
public class UserServlet extends HttpServlet {

    @Resource
    Validator validator;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("formUser")) {
            formUser(request, response);
        } else if (action.equals("newUser")) {
            newUser(request, response);
        }
    }

    private void formUser(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try (PrintWriter out = response.getWriter();) {
            UserLocal bean = (UserLocal) request.getSession().getAttribute("userbean");
            if (bean == null) {
                bean = (UserLocal) new InitialContext().lookup("java:global/dawm07eac2Biblioteca/User");
                request.getSession().setAttribute("userbean", bean);
            }
            JSONObject json = new JSONObject();
            if (bean.getUser() == null) {
                json.put("user", "");
                json.put("name", "");
                json.put("lastname", "");
            } else {
                json.put("user", bean.getUser());
                json.put("name", bean.getName());
                json.put("lastname", bean.getLastname());
            }
            out.print(json.toString());
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void newUser(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        JSONObject json = new JSONObject();
        try (PrintWriter out = response.getWriter();) {
            UserLocal bean = (UserLocal) request.getSession().getAttribute("userbean");
            bean.setUser(request.getParameter("user"));
            bean.setName(request.getParameter("name"));
            bean.setLastname(request.getParameter("lastname"));
            String message = "OK";
            for (ConstraintViolation c : validator.validate(bean)) {
                if ("user".equals(c.getPropertyPath().toString())) {
                    bean.setUser(request.getParameter(""));
                    bean.setName(request.getParameter(""));
                    bean.setLastname(request.getParameter(""));
                    message = c.getMessage();
                    message += "";
                }
            }
            json.put("resposta", message);
            out.print(json.toString());
            out.close();
            response.sendRedirect("index.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
