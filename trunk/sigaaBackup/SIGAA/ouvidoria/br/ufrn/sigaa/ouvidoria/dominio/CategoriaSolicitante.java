/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 17/05/2011
 *
 */
package br.ufrn.sigaa.ouvidoria.dominio;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe que representa a categoria do solicitante que cadastrou uma manifestação para a ouvidoria.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="categoria_solicitante")
public class CategoriaSolicitante implements PersistDB {
    
    /** Representa um discente da instituição. */
    public static final int DISCENTE = 1;
    /** Representa um docente da instituição. */
    public static final int DOCENTE = 2;
    /** Representa um servidor técnico administrativo da instituição. */
    public static final int TECNICO_ADMINISTRATIVO = 3;
    /** Representa um solicitante da comunidade externa à instituição.
     * Categoria utilizada quando o solicitante não se encaixa em nenhuma das outras categorias. */
    public static final int COMUNIDADE_EXTERNA = 4;
    
    /**
     * Identificador único da {@link CategoriaSolicitante}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_categoria_solicitante")
    private int id;
    
    /**
     * Descrição textual da {@link CategoriaSolicitante}.
     */
    private String descricao;

    /**
     * Construtor padrão.
     */
    public CategoriaSolicitante() {
    }

    public CategoriaSolicitante(int id) {
		super();
		this.id = id;
    }

    /**
     * Construtor completo.
     * 
     * @param id
     * @param descricao
     */
    public CategoriaSolicitante(int id, String descricao) {
		super();
		this.id = id;
		this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    /**
     * Retorna uma categoria com o id passado.
     * 
     * @param categoria
     * @return
     */
    public static CategoriaSolicitante getCategoriaSolicitante(int categoria) {
    	return new CategoriaSolicitante(categoria);
    }
    
    /**
     * Retorna todas as categorias.
     * 
     * @return
     */
    public static Collection<CategoriaSolicitante> getAllCategorias() {
		Collection<CategoriaSolicitante> categorias = new ArrayList<CategoriaSolicitante>();
		
		categorias.add(getCategoriaSolicitante(DISCENTE));
		categorias.add(getCategoriaSolicitante(DOCENTE));
		categorias.add(getCategoriaSolicitante(TECNICO_ADMINISTRATIVO));
		categorias.add(getCategoriaSolicitante(COMUNIDADE_EXTERNA));
		
		return categorias;
    }
    
    /**
     * Retorna todas as categorias da comunidade interna. 
     * 
     * @return
     */
    public static Collection<CategoriaSolicitante> getAllCategoriasComunidadeInterna() {
		Collection<CategoriaSolicitante> categorias = new ArrayList<CategoriaSolicitante>();
		
		categorias.add(getCategoriaSolicitante(DISCENTE));
		categorias.add(getCategoriaSolicitante(DOCENTE));
		categorias.add(getCategoriaSolicitante(TECNICO_ADMINISTRATIVO));
		
		return categorias;
    }

    @Override
    public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
    }

    @Override
    public boolean equals(Object obj) {
		if (this == obj)
		    return true;
		if (obj == null)
		    return false;
		if (getClass() != obj.getClass())
		    return false;
		CategoriaSolicitante other = (CategoriaSolicitante) obj;
		if (id != other.id)
		    return false;
		return true;
    }

}
