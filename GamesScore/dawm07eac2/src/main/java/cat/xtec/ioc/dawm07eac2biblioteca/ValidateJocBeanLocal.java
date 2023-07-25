/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package cat.xtec.ioc.dawm07eac2biblioteca;

import javax.ejb.Local;
import javax.validation.constraints.AssertFalse;

/**
 *
 * @author AlbertFiguerola
 */
@Local
public interface ValidateJocBeanLocal {
    
    public String getArticleName();
    
    public String getFileImageName();
    
    public void setArticleName(String articleName);
    
    public void setFileImageName(String fileImageName);
    
    @AssertFalse(message = "El nom del joc no coincideix amb el de l'imatge.")
    public Boolean isValidFileImageName(String articleName, String fileImageName);
    
}
