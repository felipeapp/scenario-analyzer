/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 19/05/2011
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
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que representa um tipo de {@link HistoricoManifestacao}.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="tipo_historico_manifestacao")
public class TipoHistoricoManifestacao implements PersistDB {
    
    /** Representa o tipo de {@link HistoricoManifestacao} cadastrado da Ouvidoria para a {@link Unidade} 
     * ou {@link Pessoa} designada pela resposta da {@link Manifestacao}. */
    public static final int OUVIDORIA_RESPONSAVEL = 1;
    
    /** Representa o tipo de {@link HistoricoManifestacao} cadastrado pela Ouvidoria como resposta à
     * {@link Manifestacao} do {@link InteressadoManifestacao}. */
    public static final int OUVIDORIA_INTERESSADO = 2;
    
    /** Representa o tipo de {@link HistoricoManifestacao} cadastrado pela Ouvidoria como pedido de esclarecimento à
     * {@link Manifestacao} do {@link InteressadoManifestacao}. */
    public static final int ESCLARECIMENTO_OUVIDORIA_INTERESSADO = 3;
    
    /** Representa o tipo de {@link HistoricoManifestacao} cadastrado pelo {@link InteressadoManifestacao} 
     * como resposta do pedido de esclarecimento da ouvidoria. */
    public static final int INTERESSADO_OUVIDORIA = 4;
    
    /** Representa o tipo de {@link HistoricoManifestacao} cadastrado pelo {@link InteressadoManifestacao} 
     * como resposta do pedido de esclarecimento do responsável. */
    public static final int INTERESSADO_RESPONSAVEL = 5;
    
    /**
     * Identificador único do {@link TipoHistoricoManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_tipo_historico_manifestacao")
    private int id;
    
    /**
     * Descrição textual do {@link TipoHistoricoManifestacao}.
     */
    private String descricao;

    /**
     * Construtor padrão
     */
    public TipoHistoricoManifestacao() {
    }

    public TipoHistoricoManifestacao(int id) {
		super();
		this.id = id;
    }

    /**
     * Construtor completo
     * 
     * @param id
     * @param descricao
     */
    public TipoHistoricoManifestacao(int id, String descricao) {
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
     * Retorna um tipo de histórico com o id passado.
     * 
     * @param tipo
     * @return
     */
    public static TipoHistoricoManifestacao getTipoHistoricoManifestacao(int tipo) {
    	return new TipoHistoricoManifestacao(tipo);
    }
    
    /**
     * Retorna todos os tipo de histórico que podem ser visto pelo manifestante.
     * 
     * @return
     */
    public static Collection<TipoHistoricoManifestacao> getAllTiposVisiveisInteressado() {
		Collection<TipoHistoricoManifestacao> visiveis = new ArrayList<TipoHistoricoManifestacao>();
		
		visiveis.add(getTipoHistoricoManifestacao(OUVIDORIA_INTERESSADO));
		visiveis.add(getTipoHistoricoManifestacao(ESCLARECIMENTO_OUVIDORIA_INTERESSADO));
		visiveis.add(getTipoHistoricoManifestacao(INTERESSADO_OUVIDORIA));
		visiveis.add(getTipoHistoricoManifestacao(INTERESSADO_RESPONSAVEL));
		
		return visiveis;
    }
    
    /**
     * Retorna todos os tipo de histórico que podem ser visto pelo manifestante.
     * 
     * @return
     */
    public static Collection<TipoHistoricoManifestacao> getAllTiposEsclarecimento() {
		Collection<TipoHistoricoManifestacao> visiveis = new ArrayList<TipoHistoricoManifestacao>();
		
		visiveis.add(getTipoHistoricoManifestacao(ESCLARECIMENTO_OUVIDORIA_INTERESSADO));
		visiveis.add(getTipoHistoricoManifestacao(INTERESSADO_OUVIDORIA));
		visiveis.add(getTipoHistoricoManifestacao(INTERESSADO_RESPONSAVEL));
		
		return visiveis;
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
		TipoHistoricoManifestacao other = (TipoHistoricoManifestacao) obj;
		if (id != other.id)
		    return false;
		return true;
    }

}
