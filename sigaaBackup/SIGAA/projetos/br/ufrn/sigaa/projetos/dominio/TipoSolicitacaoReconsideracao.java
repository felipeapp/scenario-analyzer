/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/12/2009
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Representa os tipos de solicitações de reconsiderações
 * existentes.
 *
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "tipo_solicitacao_reconsideracao", schema = "projetos")
public class TipoSolicitacaoReconsideracao implements PersistDB {


    public static final int SOLICITACAO_ACAO_ACADEMICA_ASSOCIADA	= 1;
    
    public static final int SOLICITACAO_ACAO_EXTENSAO			= 2;
    
    public static final int SOLICITACAO_MONITORIA			= 3;


    @Id
    @GeneratedValue(generator="seqGenerator")
    @Column(name="id_tipo_solicitacao_reconsideracao")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    private int id;

    @Column(name = "descricao")
    private String descricao;


    public TipoSolicitacaoReconsideracao() {
    }

    public TipoSolicitacaoReconsideracao(int id) {
	this.id = id;
    }

    public TipoSolicitacaoReconsideracao(int id, String descricao) {
	this.id = id;
	this.descricao = descricao;
    }

    public int getId() {
	return id;
    }

    public void setId(int idTipoSituacaoProjeto) {
	id = idTipoSituacaoProjeto;
    }

    public String getDescricao() {
	return descricao;
    }

    public void setDescricao(String descricao) {
	this.descricao = descricao;
    }

    @Override
    public String toString() {
	return getDescricao();
    }

}
