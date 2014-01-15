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
 * Classe que representa um tipo de manifestação cadastrada para a ouvidoria.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="tipo_manifestacao")
public class TipoManifestacao implements PersistDB {
    
    /** Representa uma crítica. */
    public static final int CRITICA = 1;
    /** Representa uma reclamação. */
    public static final int RECLAMACAO = 2;
    /** Representa uma denúncia. */
    public static final int DENUNCIA = 3;
    /** Representa um elogio. */
    public static final int ELOGIO = 4;
    /** Representa uma informação. */
    public static final int INFORMACAO = 5;
    /** Representa algum outro tipo de manifestação cadastrada. */
    public static final int OUTRO = 6;
    
    /**
     * Identificador único do {@link TipoManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_tipo_manifestacao")
    private int id;
    
    /**
     * Descrição textual do {@link TipoManifestacao}.
     */
    private String descricao;

    /**
     * Construtor padrão.
     */
    public TipoManifestacao() {
    }

    public TipoManifestacao(int id) {
		super();
		this.id = id;
    }

    /**
     * Construtor completo.
     * 
     * @param id
     * @param descricao
     */
    public TipoManifestacao(int id, String descricao) {
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
     * Retorna um tipo com o id passado.
     * 
     * @param tipo
     * @return
     */
    public static TipoManifestacao getTipoManifestacao(int tipo) {
    	return new TipoManifestacao(tipo);
    }
    
    /**
     * Retorna todos os tipos de manifestação.
     * 
     * @return
     */
    public static Collection<TipoManifestacao> getAllTipos() {
    	Collection<TipoManifestacao> tipos = new ArrayList<TipoManifestacao>();
    	
    	tipos.add(getTipoManifestacao(CRITICA));
    	tipos.add(getTipoManifestacao(RECLAMACAO));
    	tipos.add(getTipoManifestacao(DENUNCIA));
    	tipos.add(getTipoManifestacao(ELOGIO));
    	tipos.add(getTipoManifestacao(INFORMACAO));
    	tipos.add(getTipoManifestacao(OUTRO));
    	
    	return tipos;
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
		TipoManifestacao other = (TipoManifestacao) obj;
		if (id != other.id)
		    return false;
		return true;
    }

}
