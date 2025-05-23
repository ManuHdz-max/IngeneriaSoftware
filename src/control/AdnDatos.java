/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;



/**
 *
 * @author ericg
 */
public class AdnDatos {
    private EntityManagerFactory enf;
    public EntityManagerFactory getEnf(){
        if(enf==null){
            enf=Persistence.createEntityManagerFactory("MiChingonPU");}
        return enf;
    }
}
