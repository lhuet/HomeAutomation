package fr.lhuet.home.hardware.pojo;

import java.time.Instant;

/**
 * Created by lhuet on 26/01/16.
 */
public class TeleinfoData {

    private Instant datetime;
    private int indexcpt;
    private float pmoy;
    private float imoy;
    private int pmax;
    private int imax;

    private int papp;
    private int sumpapp;
    private int nbdata;
    private int iinst;
    private int sumiinst;


    public TeleinfoData() {
        raz();
    }

    public Instant getDatetime() {
        return datetime;
    }

    public void raz() {
        this.imax = 0;
        this.pmax = 0;
        this.setSumiinst(0);
        this.setSumpapp(0);
        this.setNbdata(0);
    }

    public void setDatetime(Instant datetime) {
        this.datetime = datetime;
    }

    public int getIndexcpt() {
        return indexcpt;
    }

    public void setIndexcpt(int indexcpt) {
        this.indexcpt = indexcpt;
    }

    public float getPmoy() {
        return pmoy;
    }

    public void setPmoy(float pmoy) {
        this.pmoy = pmoy;
    }

    public float getImoy() {
        return imoy;
    }

    public void setImoy(float imoy) {
        this.imoy = imoy;
    }

    public int getPmax() {
        return pmax;
    }

    public void setPmax(int pmax) {
        this.pmax = pmax;
    }

    public int getImax() {
        return imax;
    }

    public void setImax(int imax) {
        this.imax = imax;
    }

    public int getPapp() {
        return papp;
    }

    public void setPapp(int papp) {
        this.papp = papp;
    }

    public int getSumpapp() {
        return sumpapp;
    }

    public void setSumpapp(int sumpapp) {
        this.sumpapp = sumpapp;
    }

    public int getNbdata() {
        return nbdata;
    }

    public void setNbdata(int nbdata) {
        this.nbdata = nbdata;
    }

    public int getIinst() {
        return iinst;
    }

    public void setIinst(int iinst) {
        this.iinst = iinst;
    }

    public int getSumiinst() {
        return sumiinst;
    }

    public void setSumiinst(int sumiinst) {
        this.sumiinst = sumiinst;
    }
}
