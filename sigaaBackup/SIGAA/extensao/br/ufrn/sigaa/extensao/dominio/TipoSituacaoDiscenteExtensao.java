/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/*******************************************************************************
 * <p>
 * Representa a situa��o do aluno vinculado a uma a��o de extens�o.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_situacao_discente_extensao")
public class TipoSituacaoDiscenteExtensao implements PersistDB {

	/**
	 * Estas constantes devem estar sincronizadas com os valores do banco.
	 */

	// situa��es 1, 2 e 3 s�o utilizadas em inscri��oSelecaoExtensao associado
	// ao tipoVinculo. fase anterior � aceita��o do aluno no projeto de extens�o.
	public static final int INSCRITO_PROCESSO_SELETIVO = 1;

	public static final int NAO_SELECIONADO = 2;

	public static final int SELECIONADO = 3;

	// situa��es utilizadas durante a execu��o da a��o de extens�o, quando o
	// aluno j� foi aceito
	public static final int ATIVO = 4;

	// finalizado pelo orientador ou pelo pr�prio monitor
	public static final int FINALIZADO = 5;

	// cancelada por descumprimento de alguma cl�usula da resolu��o...
	public static final int CANCELADO = 6;

	// exclu�do, geralmente erro de migra��o ou cadastro, ativo=false em
	// discenteExtensao
	public static final int EXCLUIDO = 7;
	
	/** Utilizando quando o cadastro do plano de trabalho ainda n�o foi conclu�do e enviado para registro. */
	public static final int CADASTRO_EM_ANDAMENTO = 8;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_situacao_discente_extensao")
	private int id;

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 50)
	private String descricao;

	/** default constructor */
	public TipoSituacaoDiscenteExtensao() {
	}

	/** minimal constructor */
	public TipoSituacaoDiscenteExtensao(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoSituacaoDiscenteExtensao(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
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

}
