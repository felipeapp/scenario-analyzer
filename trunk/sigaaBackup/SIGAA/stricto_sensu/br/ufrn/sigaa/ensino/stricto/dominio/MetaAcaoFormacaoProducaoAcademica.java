/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 08/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;

/**
 * Meta de formação acadêmica e de produção científica para o ano avaliado de
 * um docente permanente e colaborador do Programa.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(schema="stricto_sensu", name="meta_acao_formacao_producao_academica")
public class MetaAcaoFormacaoProducaoAcademica implements Validatable {

	/** Chave primaria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_meta_acao_formacao_producao_academica", nullable = false)
	private int id;
	
	/** Docente do programa de pós avaliado. */ 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_equipe_programa",nullable=true)
	private EquipePrograma equipePrograma;
	
	/** Docente do curso lato sensu avaliado. */ 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso_servidor",nullable=true)
	private CorpoDocenteCursoLato corpoDocente;
	
	/** Núm. Mestres a serem Formados. */
	@Column(name="num_formacao_mestres")
	private int numFormacaoMestres;
	
	/** Núm. Doutores a serem Formados. */
	@Column(name="num_formacao_doutores")
	private int numFormacaoDoutores;
	
	/** Quantidade de Artigos (A1 + A2) produzidos. */
	@Column(name="qtd_artigos_a1a2")
	private int qtdArtigosQualisA1A2;
	
	/** Quantidade de Artigos (B1 + B2) produzidos. */
	@Column(name="qtd_artigos_b1b2")
	private int qtdArtigosQualisB1B2;
	
	/** Quantidade de Demais Estratos produzidos. */
	@Column(name="qtd_demais_artigos")
	private int qtdDemaisArtigos;
	
	/** Quantidade de Pedidos de Patente. */
	@Column(name="qtd_pedidos_patente")
	private int qtdPedidosPatente;
	
	/** Auto Avaliação ao qual pertence estas metas. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao",nullable=false)
	private RespostasAutoAvaliacao respostasAutoAvaliacao;
	
	/**
	 * Construtor padrão
	 */
	public MetaAcaoFormacaoProducaoAcademica() {
		this.equipePrograma = new EquipePrograma();
		this.corpoDocente = new CorpoDocenteCursoLato();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumFormacaoMestres() {
		return numFormacaoMestres;
	}
	public void setNumFormacaoMestres(int numFormacaoMestres) {
		this.numFormacaoMestres = numFormacaoMestres;
	}
	public int getNumFormacaoDoutores() {
		return numFormacaoDoutores;
	}
	public void setNumFormacaoDoutores(int numFormacaoDoutores) {
		this.numFormacaoDoutores = numFormacaoDoutores;
	}
	public int getQtdArtigosQualisA1A2() {
		return qtdArtigosQualisA1A2;
	}
	public void setQtdArtigosQualisA1A2(int qtdArtigosQualisA1A2) {
		this.qtdArtigosQualisA1A2 = qtdArtigosQualisA1A2;
	}
	public int getQtdArtigosQualisB1B2() {
		return qtdArtigosQualisB1B2;
	}
	public void setQtdArtigosQualisB1B2(int qtdArtigosQualisB1B2) {
		this.qtdArtigosQualisB1B2 = qtdArtigosQualisB1B2;
	}
	public int getQtdDemaisArtigos() {
		return qtdDemaisArtigos;
	}
	public void setQtdDemaisArtigos(int qtdDemaisArtigos) {
		this.qtdDemaisArtigos = qtdDemaisArtigos;
	}
	public int getQtdPedidosPatente() {
		return qtdPedidosPatente;
	}
	public void setQtdPedidosPatente(int qtdPedidosPatente) {
		this.qtdPedidosPatente = qtdPedidosPatente;
	}
	public EquipePrograma getEquipePrograma() {
		return equipePrograma;
	}
	public void setEquipePrograma(EquipePrograma equipePrograma) {
		this.equipePrograma = equipePrograma;
	}
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (isEmpty(equipePrograma) && isEmpty(corpoDocente))
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docente");
		if (numFormacaoDoutores == 0 && numFormacaoMestres == 0
				&& qtdArtigosQualisA1A2 == 0 && qtdArtigosQualisB1B2 == 0
				&& qtdDemaisArtigos == 0 && qtdPedidosPatente == 0)
			lista.addErro("Informe um valor maior que zero em qualquer item das Metas de Formação Acadêmica e de Produção Científica do Docente.");
		return lista;
	}

	public RespostasAutoAvaliacao getRespostasAutoAvaliacao() {
		return respostasAutoAvaliacao;
	}

	public void setRespostasAutoAvaliacao(
			RespostasAutoAvaliacao respostasAutoAvaliacao) {
		this.respostasAutoAvaliacao = respostasAutoAvaliacao;
	}

	public CorpoDocenteCursoLato getCorpoDocente() {
		return corpoDocente;
	}

	public void setCorpoDocente(CorpoDocenteCursoLato corpoDocente) {
		this.corpoDocente = corpoDocente;
	}

	/** Retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, corpoDocente, equipePrograma);
	}
	
	/** Compara se este objeto é igual a outro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MetaAcaoFormacaoRH) {
			MetaAcaoFormacaoProducaoAcademica other = (MetaAcaoFormacaoProducaoAcademica) obj;
			return this.id != 0 && this.id == other.id || EqualsUtil.testEquals(this, other, "corpoDocente", "equipePrograma"); 
		} else
			return false;
	}
	
	/** Encapsulamento para retornar o código hash do objeto usando JSF.
	 * @return
	 */
	public int getHash() {
		return hashCode();
	}
}
