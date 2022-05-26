/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atividade.Aula11Web2.models.repository;

import com.atividade.Aula11Web2.models.entity.ClientePF;
import com.atividade.Aula11Web2.models.entity.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
/**
 *
 * @author Pedro Paulo
 */
@Repository
public class UsuarioRepository {
    
    
    @PersistenceContext
    private EntityManager em;
    
    public Usuario usuario(String login){
        String hql = "from Usuario where login=:login";
        Query query = em.createQuery(hql, Usuario.class);
        query.setParameter("login", login);
        return (Usuario) query.getSingleResult();
    }
    
    public void save(Usuario usuario) {
        em.persist(usuario);
    }
    
    
}
