/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 15/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Parecer dado pela Comissão à uma Auto Avaliação
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(schema="stricto_sensu", name="parecer_auto_avaliacao")
public class ParecerAutoAvaliacao implements Validatable {
	
	/** Chave primaria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_parecer_comissao_avaliacao", nullable = false)
	private int id;
	
	/** Auto Avaliação ao qual pertence este parecer. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao",nullable=false)
	private RespostasAutoAvaliacao respostasAutoAvaliacao;
	
	/** Parecer da Comissão de Auto Avaliação. */
	private String parecer;
	
	/** Registro de entrada do usuário que fez o parecer. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada",nullable=false)
	@CriadoPor
	private RegistroEntrada cadastradoPor;
	
	/** Situação ao qual foi colocada a Auto Avaliação no momento do parecer. */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="situacao", nullable=false)
	private SituacaoRespostasAutoAvaliacao situacao;
	
	/** Data de cadastro. */
	@Column(name="cadastrado_em")
	private Date cadastradoEm;

	/**
	 * Construtor padrão.
	 */
	public ParecerAutoAvaliacao() {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RespostasAutoAvaliacao getRespostasAutoAvaliacao() {
		return respostasAutoAvaliacao;
	}

	public void setRespostasAutoAvaliacao(
			RespostasAutoAvaliacao respostasAutoAvaliacao) {
		this.respostasAutoAvaliacao = respostasAutoAvaliacao;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(parecer, "Parecer", lista);
		return lista;
	}

	public RegistroEntrada getCadastradoPor() {
		return cadastradoPor;
	}

	public void setCadastradoPor(RegistroEntrada cadastradoPor) {
		this.cadastradoPor = cadastradoPor;
	}

	public SituacaoRespostasAutoAvaliacao getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoRespostasAutoAvaliacao situacao) {
		this.situacao = situacao;
	}

	public Date getCadastradoEm() {
		return cadastradoEm;
	}

	public void setCadastradoEm(Date cadastradoEm) {
		this.cadastradoEm = cadastradoEm;
	}
}
