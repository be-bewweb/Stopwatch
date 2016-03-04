package be.bewweb.StopWatch.Modele;

/**
 * Created by Quentin on 17-02-16.
 */
public class Category {
    int adult;
    int child;
    int young;
    int senior;
    int veteranA;

    public Category() {
        this.adult = 16;
        this.child = 12;
        this.young = 21;
        this.senior = 40;
        this.veteranA = 50;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public int getYoung() {
        return young;
    }

    public void setYoung(int young) {
        this.young = young;
    }

    public int getSenior() {
        return senior;
    }

    public void setSenior(int senior) {
        this.senior = senior;
    }

    public int getVeteranA() {
        return veteranA;
    }

    public void setVeteranA(int veteranA) {
        this.veteranA = veteranA;
    }
}
