package cat.xtec.ioc.dawm07eac2biblioteca;

import java.util.List;
import javax.ejb.Stateful;

/**
 *
 * @author Albert Figuerola
 */
@Stateful
public class Puntuacions implements PuntuacionsLocal {
    private List<Joc> jocsPuntuats;

    @Override
    public List<Joc> getJocsPuntuats() {
        return jocsPuntuats;
    }

    @Override
    public void setJocsPuntuats(List<Joc> jocsPuntuats) {
        this.jocsPuntuats = jocsPuntuats;
    }

}
