/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 14/11/2007
 *
 */
package br.ufrn.sigaa.questionario.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Representa o tipo de {@link Question�rio} que pode estar associado a um processo seletivo, 
 * vestibular, cadastro �nico de bolsa ou cadastro de ensino a dist�ncia. * 
 * Ex: FORMULARIO_ACOMPANHAMENTO_EAD
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "tipo_questionario", schema = "questionario" )
public class TipoQuestionario implements PersistDB {

	/** Constantes que representa o question�rio de acompanhamento de ensino a dist�ncia */
	public static final int FORMULARIO_ACOMPANHAMENTO_EAD = 1;
	
	/** Constante que representa os question�rios complementares do cadastro de inscritos para os processos
	 *  seletivos de n�vel strico-sensu, lato-sensu e t�cnico */
	public static final int PROCESSO_SELETIVO = 2;
	
	/** Constante que representa os question�rios s�cio-econ�micos do cadastro de bolsista no cadastro �nico */
	public static final int QUESTIONARIO_SOCIO_ECONOMICO = 3;
	
	/** Constante que representa os question�rios do cadastro de inscritos para o vestibular */
	public static final int QUESTIONARIO_VESTIBULAR = 4;
	
	/** Constante que representa os question�rios de Relat�rio de Est�gio que o Discente Responde */
	public static final int RELATORIO_DE_ESTAGIO_DISCENTE = 5;
	
	/** Constante que representa os question�rios de Relat�rio de Est�gio que o Supervisor do Est�gio Responde */
	public static final int RELATORIO_DE_ESTAGIO_SUPERVISOR = 6;		

	/** Constante que representa os question�rios de acompanhamento das a��es de extens�o */
	public static final int QUESTIONARIO_ACAO_EXTENSAO = 7;		
	
	/** Constante que representa os question�rios do cadastro de inscritos para a��es de exten��o(�rea P�blica) */
	public static final int QUESTIONARIO_INSCRICAO_ATIVIDADE = 8;
	
	/** Constante que representa os question�rios de Relat�rio do Orientador de Est�gio que o Orientador do Est�gio Responde */
	public static final int RELATORIO_DE_ORIENTADOR_DE_ESTAGIO = 9;
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
					parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_questionario", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descri��o do tipo de question�rio. */
	private String descricao;

	/** Indica se o tipo de question�rio aceita defini��o de um gabarito. */
	@Column(name = "aceita_definicao_gabarito")
	private boolean aceitaDefinicaoGabarito;
	
	/** Construtor padr�o. */
	public TipoQuestionario() {
		super();
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public TipoQuestionario(int id) {
		this.id = id;
	}

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a descri��o do tipo de question�rio.
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descri��o do tipo de question�rio.
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Indica se o tipo de question�rio aceita defini��o de um gabarito. 
	 * @return
	 */
	public boolean isAceitaDefinicaoGabarito() {
		return aceitaDefinicaoGabarito;
	}

	/** Seta se o tipo de question�rio aceita defini��o de um gabarito. 
	 * @param aceitaDefinicaoGabarito
	 */
	public void setAceitaDefinicaoGabarito(boolean aceitaDefinicaoGabarito) {
		this.aceitaDefinicaoGabarito = aceitaDefinicaoGabarito;
	}

	/** Indica se � do tipo PROCESSO_SELETIVO
	 * @return
	 */
	public boolean isProcessoSeletivo() {
		return this.id == PROCESSO_SELETIVO;
	}
	
	/** Indica se � do tipo QUESTIONARIO_SOCIO_ECONOMICO
	 * @return
	 */
	public boolean isSocioEconomico() {
		return this.id == QUESTIONARIO_SOCIO_ECONOMICO;
	}
	
	/** Indica se � do tipo QUESTIONARIO_VESTIBULAR
	 * @return
	 */
	public boolean isQuestionarioVestibular() {
		return this.id == QUESTIONARIO_VESTIBULAR;
	}
	
	/** Indica se � do tipo QUESTIONARIO_INSCRICAO_ATIVIDADE
	 * @return
	 */
	public boolean isQuestionarioInscricaoAtividade() {
		return this.id == QUESTIONARIO_INSCRICAO_ATIVIDADE;
	}
	
	/** Indica se � do tipo RELATORIO_DE_ESTAGIO
	 * @return
	 */
	public boolean isQuestionarioRelatorioEstagio() {
		return this.id == RELATORIO_DE_ESTAGIO_DISCENTE || this.id == RELATORIO_DE_ESTAGIO_SUPERVISOR || this.id == RELATORIO_DE_ORIENTADOR_DE_ESTAGIO;
	}	

	/** Indica se � do tipo Acompanhamento de uma a��o de Extens�o.
	 * @return
	 */
	public boolean isAcaoExtensao() {
		return this.id == QUESTIONARIO_ACAO_EXTENSAO;
	}
	
	/** Retorna uma descri��o textual do tipo de question�rio.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return descricao != null ? descricao : "";
	}

	/** Indica se as respostas ao question�rio s�o de preenchimento obrigat�rio.
	 * @return
	 */
	public boolean isRespostasObrigatorias() {
		return isSocioEconomico() || isQuestionarioVestibular();
	}

}
