package modelo;

public class Gasolinera {
    private String provincia;
    private String municipio;
    private String localidad;
    private String cp;
    private String direccion;
    private double gasolina95;
    private double gasolina98;
    private double diesel;
    private double dieselPlus;

    public Gasolinera(String provincia, String municipio, String localidad, String cp, String direccion,
                      double gasolina95, double gasolina98, double diesel, double dieselPlus) {
        this.provincia = provincia;
        this.municipio = municipio;
        this.localidad = localidad;
        this.cp = cp;
        this.direccion = direccion;
        this.gasolina95 = gasolina95;
        this.gasolina98 = gasolina98;
        this.diesel = diesel;
        this.dieselPlus = dieselPlus;
    }

    public String getProvincia() { return provincia; }
    public String getMunicipio() { return municipio; }
    public String getLocalidad() { return localidad; }
    public String getCp() { return cp; }
    public String getDireccion() { return direccion; }
    public double getGasolina95() { return gasolina95; }
    public double getGasolina98() { return gasolina98; }
    public double getDiesel() { return diesel; }
    public double getDieselPlus() { return dieselPlus; }

    public double getPrecioCombustible(String tipo) {
        switch (tipo.toLowerCase()) {
            case "gasolina95": return gasolina95;
            case "gasolina98": return gasolina98;
            case "diesel": return diesel;
            case "dieselplus": return dieselPlus;
            default: return Double.NaN;
        }
    }

    @Override
    public String toString() {
        return direccion + ", " + localidad + " (" + provincia + ")";
    }
}
