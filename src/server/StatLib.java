package server;

public class StatLib {

    // simple average
    public static float avg(float[] x) {
        float sum = 0;
        for (int i = 0; i < x.length; i++)
            sum += x[i];

        return sum / x.length;
    }

    // returns the variance of X and Y
    public static float var(float[] x) {
        float sum = 0;

        for (int i = 0; i < x.length; i++)
            sum += Math.pow(x[i] - avg(x), 2);
        return sum / x.length;
    }

    // returns the covariance of X and Y
    public static float cov(float[] x, float[] y) {
        float sum = 0;

        for (int i = 0; i < x.length; i++)
            sum += (x[i] - avg(x)) * (y[i] - avg(y));

        return sum / x.length;
    }

    // returns the Pearson correlation coefficient of X and Y
    public static float pearson(float[] x, float[] y) {
        return cov(x, y) / (float) (Math.sqrt(var(x)) * Math.sqrt(var(y)));
    }

    // performs a linear regression and returns the line equation
    public static Line linear_reg(Point[] points) {
        float a, b, xAvg, yAvg;

        float xArray[] = new float[points.length];
        float yArray[] = new float[points.length];
        for (int i = 0; i < points.length; i++) {
            xArray[i] = points[i].x;
            yArray[i] = points[i].y;
        }

        a = cov(xArray, yArray) / var(xArray);
        xAvg = avg(xArray);
        yAvg = avg(yArray);
        b = yAvg - (a * xAvg);

        Line line = new Line(a, b);
        for (int i = 0; i < xArray.length; i++)
            line.f(xArray[i]);

        return line;
    }

    // returns the deviation between point p and the line equation of the points
    public static float dev(Point p, Point[] points) {
        for (int i = 0; i < points.length; i++) {
            if (p.x == points[i].x)
                return Math.abs(points[i].y - p.y);
        }
        return 0;
    }

    // returns the deviation between point p and the line
    public static float dev(Point p, Line l) {
        float fx = l.f(p.x);
        return Math.abs(fx - p.y);
    }
}