package cat.xtec.ioc.dawm07eac2biblioteca;


/**
 *
 * @author Albert Figuerola
 */
public class Joc {
    private String name;
    private Double puntuacio;
    
    public Joc(String name, Double puntuacio) {
        this.name = name;
        this.puntuacio = puntuacio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPuntuacio() {
        return puntuacio;
    }

    public void setPuntuacio(Double puntuacio) {
        this.puntuacio = puntuacio;
    }
    
}
