package br.com.speedy.ipapp_me.model;

import java.util.Date;

/**
 * Created by henrique on 2015-04-08.
 */
public class UltimoLogin {

    private Long id;

    private Date data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
