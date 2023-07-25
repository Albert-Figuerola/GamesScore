package cat.xtec.ioc.dawm07eac2biblioteca;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Albert Figuerola
 */
@WebServlet(name = "Puntuacions", urlPatterns = {"/puntuacions"})
public class PuntuacionsServlet extends HttpServlet {

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
        if (action.equals("jocsPuntuatsList")) {
            jocsList(request, response);
        } else if (action.equals("deleteJoc")) {
            deleteJoc(request, response);
        }
    }

    private void jocsList(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            PuntuacionsLocal puntuacionsBean = (PuntuacionsLocal) request.getSession().getAttribute("puntuacionsbean");
            if (puntuacionsBean == null) {
                puntuacionsBean = (PuntuacionsLocal) new InitialContext().lookup("java:global/dawm07eac2Biblioteca/Puntuacions");
                puntuacionsBean.setJocsPuntuats(new ArrayList<Joc>());
                request.getSession().setAttribute("puntuacionsbean", puntuacionsBean);
            }

            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();

            for (Joc joc : puntuacionsBean.getJocsPuntuats()) {
                LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<>();
                jsonOrderedMap.put("name", joc.getName());
                jsonOrderedMap.put("puntuacio", joc.getPuntuacio().toString());
                JSONObject jsonObj = new JSONObject(jsonOrderedMap);
                array.put(jsonObj);
            }

            json.put("jsonArray", array);
            out.print(json.toString());
            out.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void deleteJoc(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter();) {
            String delJoc = request.getParameter("joc");
            PuntuacionsLocal puntuacionsBean = (PuntuacionsLocal) request.getSession().getAttribute("puntuacionsbean");
            for (Joc joc : puntuacionsBean.getJocsPuntuats()) {
                if (joc.getName().equals(delJoc)) {
                    joc.setPuntuacio(0.0);
                    puntuacionsBean.getJocsPuntuats().remove(joc);
                    break;
                }
            }
            JSONObject json = new JSONObject();
            json.put("resposta", "OK");
            out.print(json.toString());
            out.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

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
