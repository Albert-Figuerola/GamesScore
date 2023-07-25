/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package cat.xtec.ioc.dawm07eac2biblioteca;

import javax.ejb.Stateless;

/**
 *
 * @author AlbertFiguerola
 */
@Stateless
public class ValidateJocBean implements ValidateJocBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private String articleName;

    private String fileImageName;

    @Override
    public String getArticleName() {
        return articleName;
    }

    @Override
    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    @Override
    public String getFileImageName() {
        return fileImageName;
    }

    @Override
    public void setFileImageName(String fileImageName) {
        this.fileImageName = fileImageName;
    }

    @Override
    public Boolean isValidFileImageName(String articleName, String fileImageName) {
        int index = fileImageName.lastIndexOf(".");
        String nomImatge = fileImageName.substring(0, index);
        return articleName.equals(nomImatge);
    }
}
