package cat.xtec.ioc.dawm07eac2biblioteca;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Albert Figuerola
 */
@Local
public interface PuntuacionsLocal {
    public List<Joc> getJocsPuntuats();
    public void setJocsPuntuats(List<Joc> jocsPuntuats);
}
