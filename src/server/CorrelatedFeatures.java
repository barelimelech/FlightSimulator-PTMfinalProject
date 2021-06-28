package server;

public class CorrelatedFeatures {
    public final String feature1, feature2;
    public final float corrlation;
    public final Line lin_reg;
    public final float threshold;//farest distance

    public CorrelatedFeatures(String feature1, String feature2, float corrlation, Line lin_reg, float threshold) {
        this.feature1 = feature1;
        this.feature2 = feature2;
        this.corrlation = corrlation;
        this.lin_reg = lin_reg;
        this.threshold = threshold;
    }

    public void PRINT(){
        System.out.println(
                "feature1 = "+this.feature1 +"\n"+
                "feature2 = "+this.feature2 +"\n"+
                "corrlation = "+this.corrlation +"\n"+
                "lin_reg = "+this.lin_reg +"\n"+
                "threshold = "+this.threshold +"\n"
        );
    }
}