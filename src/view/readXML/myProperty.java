package view.readXML;

public class myProperty {

    private String Name;
    private String valMin;
    private String valMax;


    public myProperty(String name) {
        this.Name = name;
        this.valMin = "-1";
        this.valMax = "1";
   }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getValMin() {
        return valMin;
    }

    public void setValMin(String valMin) {
        this.valMin = valMin;
    }

    public String getValMax() {
        return valMax;
    }

    public void setValMax(String valMax) {
        this.valMax = valMax;
    }
}
