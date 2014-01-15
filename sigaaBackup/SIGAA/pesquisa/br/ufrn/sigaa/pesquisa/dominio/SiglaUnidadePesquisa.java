/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Armazena as defini��es de siglas para os centros ou unidades acad�micas especializadas 
 * para serem usadas na gera��o de c�digos de projetos
 * 
 * @author Leonardo Campos
 *
 */	
@Entity
@Table(name = "sigla_unidade_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class SiglaUnidadePesquisa implements Validatable {

	/** chave prim�ria da classe */
	private int id;
	/** Unidade para qual se deve partir para chegar na unidade da sigla ou para a unidade do CIC */
	private Unidade unidade;
	/** Sigla para ser usada nos projetos da unidade em quest�o */
	private Character sigla;
	/** Unidade de classifica��o da unidade */
	private Unidade unidadeClassificacao;
	/** Unidade do CIC a qual a unidade est� v�nculada */
	private Unidade unidadeCic;

	/** default constructor */
	public SiglaUnidadePesquisa() {
		unidade = new Unidade();
		unidadeClassificacao = new Unidade();
		unidadeCic = new Unidade();
	}

	/** Retorna a chave prim�ria e serve para a gera��o da chave prim�ria */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	public int getId() {
		return id;
	}

	/** Retorna a sigla da Unidade de Pesquisa */
	@Column(name = "sigla")
	public Character getSigla() {
		return sigla;
	}

	/** Retorna a Unidade */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Unidade getUnidade() {
		return unidade;
	}

	public void setSigla(Character sigla) {
		this.sigla = sigla;
	}

	/** Seta a chave prim�ria */
	public void setId(int id) {
		this.id = id;
	}
	
	/** Seta a sigla */
	public void setSigla(char sigla) {
		this.sigla = sigla;
	}

	/** Seta a unidade */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade_classificacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Unidade getUnidadeClassificacao() {
		return unidadeClassificacao;
	}

	public void setUnidadeClassificacao(Unidade unidadeClassificacao) {
		this.unidadeClassificacao = unidadeClassificacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade_cic", unique = false, nullable = true, insertable = true, updatable = true)
	public Unidade getUnidadeCic() {
		return unidadeCic;
	}

	public void setUnidadeCic(Unidade unidadeCic) {
		this.unidadeCic = unidadeCic;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(getUnidade().getId(), "Unidade", lista);
		return lista;
	}

}