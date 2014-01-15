/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/03/2010
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * Representa o tipo de parecer dado em uma avaliação de propostas de 'Ação de Extensão'.
 * Usado também na Avaliação de Relatórios de Ações de Extensão
 * 
 * @author Igor Linnik
 * 
 ******************************************************************************/
@Entity
@Table(name = "tipo_parecer_avaliacao", schema = "extensao", uniqueConstraints = {})
public class TipoParecerAvaliacaoExtensao implements Validatable {
	
	/** Tipo de parecer para uma avaliação */
	/** Tipo parecer não definido */ 
	public static final int NAO_DEFINIDO = -1;
	/** Tipo parecer aprovado */
	public static final int APROVADO = 1;
	/** Tipo parecer aprovado com recomendações */
	public static final int APROVADO_COM_RECOMENDACAO = 2;
	/** Tipo parecer reprovado */
	public static final int REPROVADO = 3;
	/** tipo parecer ação não realizada */
	public static final int ACAO_NAO_REALIZADA = 4;
	/** Tipo parecer devolver para ajustes do coordenador */
	public static final int DEVOLVER_PARA_AJUSTES_COORDENADOR = 5;
	
	/** Identificador */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_parecer_avaliacao", unique = true, nullable = false)
	private int id;

	/** Descrição do tipo de parecer */
	@Column(name = "descricao", length = 50)
	private String descricao;

	/** Indica se o tipo de parecer esta ativo no sistema */
	@Column(name = "ativo")
	private boolean ativo = true;	

	/** Construtor Padrão. */
	public TipoParecerAvaliacaoExtensao() {
	}
	
	/** Construtor simples. */
	public TipoParecerAvaliacaoExtensao(int id) {
		this.id = id;
	}

	/** Valida tipo parecer */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}	
}
