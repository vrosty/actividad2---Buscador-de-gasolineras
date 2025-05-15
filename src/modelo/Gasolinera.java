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

        // Validación de parámetros del constructor
        if (provincia == null || provincia.trim().isEmpty()) {
            throw new IllegalArgumentException("La provincia no puede ser nula o vacía.");
        }
        if (municipio == null || municipio.trim().isEmpty()) {
            throw new IllegalArgumentException("El municipio no puede ser nulo o vacío.");
        }
        if (localidad == null || localidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La localidad no puede ser nula o vacía.");
        }
        // Considera si cp puede ser nulo o vacío según tus requerimientos. Por ahora, lo permitimos si no es esencial.
        // if (cp == null || cp.trim().isEmpty()) {
        //     throw new IllegalArgumentException("El código postal no puede ser nulo o vacío.");
        // }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede ser nula o vacía.");
        }

        // Validar que los precios no sean negativos (NaN es aceptable si no hay precio)
        if (gasolina95 < 0 && !Double.isNaN(gasolina95)) {
            throw new IllegalArgumentException("El precio de la Gasolina 95 no puede ser negativo.");
        }
        if (gasolina98 < 0 && !Double.isNaN(gasolina98)) {
            throw new IllegalArgumentException("El precio de la Gasolina 98 no puede ser negativo.");
        }
        if (diesel < 0 && !Double.isNaN(diesel)) {
            throw new IllegalArgumentException("El precio del Diésel no puede ser negativo.");
        }
        if (dieselPlus < 0 && !Double.isNaN(dieselPlus)) {
            throw new IllegalArgumentException("El precio del Diésel Plus no puede ser negativo.");
        }

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
        if (tipo == null || tipo.trim().isEmpty()){
            // Devolver NaN si el tipo es nulo/vacío o desconocido, consistente con el switch.
            return Double.NaN;
        }
        switch (tipo.toLowerCase()) { // Mantenemos toLowerCase como estaba
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