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
 * Classe que representa a origem de uma manifestação para a ouvidoria.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="origem_manifestacao")
public class OrigemManifestacao implements PersistDB {
    
    /** Representa uma manifestação cadastrada através do site da ouvidoria. */
    public static final int ONLINE = 1;
    /** Representa uma manifestação passada pessoalmente para a ouvidoria. */
    public static final int PRESENCIAL = 2;
    
    /**
     * Chave primária da {@link OrigemManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_origem_manifestacao")
    private int id;
    
    /**
     * Descrição textual da {@link OrigemManifestacao}.
     */
    private String descricao;

    /**
     * Construtor padrão.
     */
    public OrigemManifestacao() {
    }

    public OrigemManifestacao(int id) {
		super();
		this.id = id;
    }

    /**
     * Construtor completo.
     * 
     * @param id
     * @param descricao
     */
    public OrigemManifestacao(int id, String descricao) {
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
     * Retorna uma origem com o id passado.
     * 
     * @param origem
     * @return
     */
    public static OrigemManifestacao getOrigemManifestacao(int origem) {
    	return new OrigemManifestacao(origem);
    }
    
    /**
     * Retorna todas as origens existentes.
     * 
     * @return
     */
    public static Collection<OrigemManifestacao> getAllOrigensManifestacao() {
		Collection<OrigemManifestacao> origens = new ArrayList<OrigemManifestacao>();
		
		origens.add(getOrigemManifestacao(ONLINE));
		origens.add(getOrigemManifestacao(PRESENCIAL));
		
		return origens;
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
		OrigemManifestacao other = (OrigemManifestacao) obj;
		if (id != other.id)
		    return false;
		return true;
    }

}
