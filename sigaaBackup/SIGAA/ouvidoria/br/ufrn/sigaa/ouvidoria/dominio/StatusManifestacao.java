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
 * Classe que representa o status atual de uma {@link Manifestacao}.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="status_manifestacao")
public class StatusManifestacao implements PersistDB {
    
    /** Representa o estado inicial de uma {@link Manifestacao} cadastrada. 
     * Fica aguardando um encaminhamento à unidade responsável ou uma resposta imediata. */
    public static final int SOLICITADA = 1;
    /** Representa o estado de uma {@link Manifestacao} que foi encaminhada à unidade responsável.
     * Fica aguardando resposta do responsável pela unidade ou designação de pessoa para resposta. */
    public static final int ENCAMINHADA_UNIDADE = 2;
    /** Representa o estado de uma {@link Manifestacao} que foi encaminhada à unidade responsável e 
     * repassada para uma pessoa responder. Fica aguardando resposta do designado. */
    public static final int DESIGNADA_RESPONSAVEL = 3;
    /** Representa o estado de uma {@link Manifestacao} que foi degignada para uma pessoa responder,
     *  essa resposta foi cadastrada e agora está aguardando o parecer da chefia da unidade responsável. */
    public static final int AGUARDANDO_PARECER = 4;
    /** Representa o estado de uma {@link Manifestacao} que foi respondida pelo responsável da unidade 
     * e se encontra na ouvidoria para montagem da resposta ao solicitante. 
     * Fica aguardando ser respondida ao solicitante. */
    public static final int PARECER_CADASTRADO = 5;
    /** Representa o estado de uma {@link Manifestacao} já respondida ao usuário. */
    public static final int RESPONDIDA = 6;
    /** Representa o estado de uma {@link Manifestacao} já respondida ao usuário. */
    public static final int FINALIZADA = 7;
    /** Representa o estado de uma {@link Manifestacao} removida pelo ouvidor. */
    public static final int CANCELADA = 8;
    /** Representa o estado de uma {@link Manifestacao} que espera esclarecimento do interessado. */
    public static final int ESPERANDO_ESCLARECIMENTO = 9;
    /** Representa o estado de uma {@link Manifestacao} que foi pedido esclarecimento e respondido pelo interessado para a ouvidoria. */
    public static final int ESCLARECIDO_OUVIDORIA = 10;
    /** Representa o estado de uma {@link Manifestacao} que foi pedido esclarecimento e respondido pelo interessado para o responsável. */
    public static final int ESCLARECIDO_RESPONSAVEL = 11;
    
    /**
     * Chave primária do {@link StatusManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_status_manifestacao")
    private int id;
    
    /**
     * Descrição textual do {@link StatusManifestacao}.
     */
    private String descricao;

    /**
     * Construtor padrão.
     */
    public StatusManifestacao() {
    }

    public StatusManifestacao(int id) {
		super();
		this.id = id;
    }

    public StatusManifestacao(int id, String descricao) {
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
    
    public boolean isParecerCadastrado() {
    	return this.id == PARECER_CADASTRADO; 
    }
    
    /**
     * Retorna um status com o id passado.
     * 
     * @param status
     * @return
     */
    public static StatusManifestacao getStatusManifestacao(int status) {
    	return new StatusManifestacao(status);
    }
    
    /**
     * Retorna todos os status de manifestação.
     * 
     * @return
     */
    public static Collection<StatusManifestacao> getAllStatusManifestacao() {
		Collection<StatusManifestacao> status = new ArrayList<StatusManifestacao>();
		
		status.add(getStatusManifestacao(SOLICITADA));
		status.add(getStatusManifestacao(ENCAMINHADA_UNIDADE));
		status.add(getStatusManifestacao(DESIGNADA_RESPONSAVEL));
		status.add(getStatusManifestacao(AGUARDANDO_PARECER));
		status.add(getStatusManifestacao(PARECER_CADASTRADO));
		status.add(getStatusManifestacao(RESPONDIDA));
		status.add(getStatusManifestacao(ESPERANDO_ESCLARECIMENTO));
		status.add(getStatusManifestacao(ESCLARECIDO_OUVIDORIA));
		status.add(getStatusManifestacao(ESCLARECIDO_RESPONSAVEL));
		return status;
    }
    
    /**
     * Retorna todos os status de manifestação que são acompanhados pela ouvidoria.
     * 
     * @return
     */
    public static Collection<StatusManifestacao> getAllStatusAcompanhados() {
		Collection<StatusManifestacao> status = new ArrayList<StatusManifestacao>();
		
		status.add(getStatusManifestacao(SOLICITADA));
		status.add(getStatusManifestacao(ENCAMINHADA_UNIDADE));
		status.add(getStatusManifestacao(DESIGNADA_RESPONSAVEL));
		status.add(getStatusManifestacao(AGUARDANDO_PARECER));
		status.add(getStatusManifestacao(PARECER_CADASTRADO));
		status.add(getStatusManifestacao(RESPONDIDA));
		status.add(getStatusManifestacao(FINALIZADA));
		
		
		return status;
    }
    
    /**
     * Retorna todos os status de manifestação que já foram encaminhados para resposta
     * mas ainda estão no aguardo.
     * 
     * @return
     */
    public static Collection<StatusManifestacao> getAllStatusSemResposta() {
		Collection<StatusManifestacao> status = new ArrayList<StatusManifestacao>();
		
		status.add(getStatusManifestacao(ENCAMINHADA_UNIDADE));
		status.add(getStatusManifestacao(DESIGNADA_RESPONSAVEL));
		status.add(getStatusManifestacao(AGUARDANDO_PARECER));
		status.add(getStatusManifestacao(ESCLARECIDO_RESPONSAVEL));
		
		return status;
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
		StatusManifestacao other = (StatusManifestacao) obj;
		if (id != other.id)
		    return false;
		return true;
    }

}
