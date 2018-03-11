package com.rr.nn;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Iris {
    @Getter @Setter
    double sepalLength;
    @Getter @Setter
    double sepalWidth;
    @Getter @Setter
    double petalLength;
    @Getter @Setter
    double petalWidth;
    @Getter @Setter
    IrisType irisType;

    public double getXi(int i) {
        switch (i) {
            case 0:
                return getSepalLength();
            case 1:
                return getSepalWidth();
            case 2:
                return getPetalLength();
            case 3:
                return getPetalWidth();
            default:
                throw new IllegalArgumentException("Smth gonna wrong!!!");
        }
    }

    public Iris(double sepalLength, double sepalWidth, double petalLength, double petalWidth) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
    }
}