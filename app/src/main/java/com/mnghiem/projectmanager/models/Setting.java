package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class Setting {

    @SerializedName("ma_nd")
    private int maNguoiDung;

    @SerializedName("che_do_toi")
    private boolean cheDoToi;

    @SerializedName("ngon_ngu")
    private String ngonNgu;

    public Setting(int maNguoiDung, boolean cheDoToi, String ngonNgu) {
        this.maNguoiDung = maNguoiDung;
        this.cheDoToi = cheDoToi;
        this.ngonNgu = ngonNgu;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public boolean isCheDoToi() {
        return cheDoToi;
    }

    public String getNgonNgu() {
        return ngonNgu;
    }

    public void setCheDoToi(boolean cheDoToi) {
        this.cheDoToi = cheDoToi;
    }

    public void setNgonNgu(String ngonNgu) {
        this.ngonNgu = ngonNgu;
    }
}
