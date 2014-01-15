/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Representa a situação do aluno vinculado a uma ação de extensão.
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

	// situações 1, 2 e 3 são utilizadas em inscriçãoSelecaoExtensao associado
	// ao tipoVinculo. fase anterior à aceitação do aluno no projeto de extensão.
	public static final int INSCRITO_PROCESSO_SELETIVO = 1;

	public static final int NAO_SELECIONADO = 2;

	public static final int SELECIONADO = 3;

	// situações utilizadas durante a execução da ação de extensão, quando o
	// aluno já foi aceito
	public static final int ATIVO = 4;

	// finalizado pelo orientador ou pelo próprio monitor
	public static final int FINALIZADO = 5;

	// cancelada por descumprimento de alguma cláusula da resolução...
	public static final int CANCELADO = 6;

	// excluído, geralmente erro de migração ou cadastro, ativo=false em
	// discenteExtensao
	public static final int EXCLUIDO = 7;
	
	/** Utilizando quando o cadastro do plano de trabalho ainda não foi concluído e enviado para registro. */
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
