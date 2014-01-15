/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/02/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * Classe que representa o Tipo de avalia��o realizada por membros do comit� de
 * extens�o ou avaliadores Ad Hoc.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_avaliacao")
public class TipoAvaliacao implements Validatable {

	// avalia��o feita por convidados membros da UFRN
	public static final int AVALIACAO_ACAO_PARECERISTA = 1;

	// avalia��o do comit�
	public static final int AVALIACAO_ACAO_COMITE = 2;

	// avalia��o da PROEX para projetos que n�o solicitaram recursos FAEX
	public static final int AVALIACAO_ACAO_PROEX = 3;

	// relat�rio da a��o pelo departamento
	public static final int AVALIACAO_RELATORIO_DEPARTAMENTO = 4;

	// relat�rio da a��o pela PROEX
	public static final int AVALIACAO_RELATORIO_PROEX = 5;

	// avalia��o do comit�
	public static final int AVALIACAO_ACAO_PRESIDENTE_COMITE = 6;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_avaliacao", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	public TipoAvaliacao() {
	}

	public TipoAvaliacao(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String toString() {
		return descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		return lista;
	}

}
