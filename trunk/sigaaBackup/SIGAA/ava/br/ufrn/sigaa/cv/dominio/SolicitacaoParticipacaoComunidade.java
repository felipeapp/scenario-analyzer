/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Entidade que representa as solicitações de um usuário
 * para participar de uma Comunidade Virtual quando a comunidade
 * é do tipo Moderada.
 * 
 * @author Agostinho
 *
 */

@Entity 
@Table(name="solicitacao_participacao_comunidade", schema="cv")
public class SolicitacaoParticipacaoComunidade implements Validatable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id_solicitacao_participacao_comunidade")
	private int id;
	
	@CriadoEm
	@Column(name = "data_solicitacao")
	private Date dataSolicitacao;

	@CriadoPor
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="id")
	private ComunidadeVirtual comunidadeVirtual;

	@Column(name="aceito_comunidade")
	private boolean aceitoComunidade;

	@Column(name="pendente_aprovacao")
	private boolean pendenteDecisao = true;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public ComunidadeVirtual getComunidadeVirtual() {
		return comunidadeVirtual;
	}

	public void setComunidadeVirtual(ComunidadeVirtual comunidadeVirtual) {
		this.comunidadeVirtual = comunidadeVirtual;
	}
	
	public ListaMensagens validate() {
		return null;
	}

	public boolean isAceitoComunidade() {
		return aceitoComunidade;
	}

	public void setAceitoComunidade(boolean aceitoComunidade) {
		this.aceitoComunidade = aceitoComunidade;
	}

	public boolean isPendenteDecisao() {
		return pendenteDecisao;
	}

	public void setPendenteDecisao(boolean pendenteDecisao) {
		this.pendenteDecisao = pendenteDecisao;
	}
	
}
