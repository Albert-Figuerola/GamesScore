package cat.xtec.ioc.dawm07eac2biblioteca;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Albert Figuerola
 */
@MultipartConfig(location = "/")
public class Biblioteca extends HttpServlet {

    @EJB
    private ValidateJocBeanLocal validation;
    private List<Joc> jocs = new ArrayList<Joc>();

    //Directori on es guarden les imatges
    private static final String UPLOAD_DIR = "img";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        Enumeration initData = config.getInitParameterNames();
        while (initData.hasMoreElements()) {
            String name = (String) initData.nextElement();
            String values = config.getInitParameter(name);
            // Double puntuacio = Double.parseDouble(values);
            Double puntuacio = Double.valueOf(values);
            jocs.add(new Joc(name, puntuacio));
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws org.json.JSONException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, JSONException {
        String action = request.getParameter("action");
        if (action.equals("listJocs")) {
            listJocs(request, response);
        } else if (action.equals("addJocPuntuacions")) {
            addJocPuntuacions(request, response);
        } else if (action.equals("createJoc")) {
            createJoc(request, response);
        }
    }

    protected void listJocs(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try (PrintWriter out = response.getWriter()) {
            for (Joc joc : jocs) {
                LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<>();
                jsonOrderedMap.put("name", joc.getName());
                jsonOrderedMap.put("puntuacio", String.valueOf(joc.getPuntuacio()));

                // part 3 - check dels jocs puntuats
                if (checkJocPuntuatSession(request, joc)) {
                    jsonOrderedMap.put("afegit", "SI");
                } else {
                    jsonOrderedMap.put("afegit", "NO");
                }

                JSONObject jsonObj = new JSONObject(jsonOrderedMap);
                array.put(jsonObj);
            }
            json.put("jsonArray", array);
            out.print(json.toString());
        } catch (JSONException ex) {
            Logger.getLogger(Biblioteca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void addJocPuntuacions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String jocPuntuat = request.getParameter("joc");
            String puntuacioJoc = request.getParameter("puntuacio");
            JSONObject json = new JSONObject();

            // busquem el joc passat per paràmetre, si coincideix amb algún joc de l'array, fem un set de la puntuació
            for (Joc nomJoc : jocs) {
                if (nomJoc.getName().equals(jocPuntuat)) {
                    nomJoc.setPuntuacio(Double.valueOf(puntuacioJoc));
                    // part 3 - llista jocs puntuats
                    addJocToSession(request, nomJoc);
                    break;
                }
            }
            json.put("jocPuntuat", jocPuntuat);
            json.put("puntuacioJoc", Double.valueOf(puntuacioJoc));
            out.print(json.toString());
            out.close();
        } catch (JSONException ex) {
            Logger.getLogger(Biblioteca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void createJoc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // obtenir la part de la sol·licitud de conté l'imatge
        Part filePart = request.getPart("fileImageName");
        // obtenir el nom de la part de l'imatge
        String fileName = filePart.getSubmittedFileName();
        // obtenir nom i puntuació del joc dels inputs
        String nomJoc = request.getParameter("fpname");
        String puntuacio = request.getParameter("fprate");
        // ruta del projecte GlassFish on crearem tmp
        String rutaProjecte = System.getProperty("user.dir");
        // carpeta img de target
        String rutaCarpetaImg = request.getServletContext().getRealPath(File.separator + UPLOAD_DIR);

        try {
            if (isValidFileName(nomJoc, fileName)) {
                // carpeta temporal on ubicarem la imatge, si no existeix la creem
                File tmpDir = new File(rutaProjecte + "\\tmp");
                if (!tmpDir.exists()) {
                    tmpDir.mkdir();
                }
                // creem  un objecte File amb la ruta i el nom de l'imatge
                File file = new File(tmpDir, fileName);
                // copiem el contingut de la part de l'arxiu pujat a l'arxiu creat al pas anterior
                try (InputStream inputStream = filePart.getInputStream()) {
                    Files.copy(inputStream, file.toPath());
                }
                // creem un objecte File amb la ruta de destí anteriorment declarada, sino existeix la creem
                File targetDirFile = new File(rutaCarpetaImg);
                if (!targetDirFile.exists()) {
                    targetDirFile.mkdir();
                }
                // creem un objecte File amb la ruta destí i creem un arxiu amb el nom de l'imatge
                File targetFile = new File(targetDirFile, fileName);
                // copiem el contingut de l'arxiu temporal a l'arxiu final
                Path sourcePath = file.toPath();
                Path targetPath = targetFile.toPath();
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                // eliminem arxius temporals
                file.delete();
                // creem un nou objecte Joc i l'introduïm a l'array list
                Joc nouJoc = new Joc(nomJoc, Double.valueOf(puntuacio));
                jocs.add(nouJoc);
            }
            response.sendRedirect("index.html");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean isValidFileName(String paramName, String fileName) {
        return validation.isValidFileImageName(paramName, fileName);
    }

    private void addJocToSession(HttpServletRequest request, Joc joc) {
        try {
            PuntuacionsLocal puntuacionsBean = (PuntuacionsLocal) request.getSession().getAttribute("puntuacionsbean");
            if (puntuacionsBean == null) {
                puntuacionsBean = (PuntuacionsLocal) new InitialContext().lookup("java:global/dawm07eac2Biblioteca/Puntuacions");
                puntuacionsBean.setJocsPuntuats(new ArrayList<Joc>());
                request.getSession().setAttribute("puntuacionsbean", puntuacionsBean);
            }
            puntuacionsBean.getJocsPuntuats().add(joc);
        } catch (NamingException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private Boolean checkJocPuntuatSession(HttpServletRequest request, Joc joc) {
        boolean valor = false;
        try {
            PuntuacionsLocal puntuacionsBean = (PuntuacionsLocal) request.getSession().getAttribute("puntuacionsbean");
            if (puntuacionsBean == null) {
                puntuacionsBean = (PuntuacionsLocal) new InitialContext().lookup("java:global/dawm07eac2Biblioteca/Puntuacions");
                puntuacionsBean.setJocsPuntuats(new ArrayList<Joc>());
                request.getSession().setAttribute("puntuacionsbean", puntuacionsBean);
            }
            for (Joc nomJoc : puntuacionsBean.getJocsPuntuats()) {
                if (nomJoc.getName().equals(joc.getName())) {
                    valor = true;
                    break;
                }
            }
        } catch (NamingException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return valor;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(Biblioteca.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(Biblioteca.class.getName()).log(Level.SEVERE, null, ex);
        }
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
