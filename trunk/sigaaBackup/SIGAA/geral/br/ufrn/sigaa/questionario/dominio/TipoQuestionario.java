/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Representa o tipo de {@link Questionário} que pode estar associado a um processo seletivo, 
 * vestibular, cadastro único de bolsa ou cadastro de ensino a distância. * 
 * Ex: FORMULARIO_ACOMPANHAMENTO_EAD
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "tipo_questionario", schema = "questionario" )
public class TipoQuestionario implements PersistDB {

	/** Constantes que representa o questionário de acompanhamento de ensino a distância */
	public static final int FORMULARIO_ACOMPANHAMENTO_EAD = 1;
	
	/** Constante que representa os questionários complementares do cadastro de inscritos para os processos
	 *  seletivos de nível strico-sensu, lato-sensu e técnico */
	public static final int PROCESSO_SELETIVO = 2;
	
	/** Constante que representa os questionários sócio-econômicos do cadastro de bolsista no cadastro único */
	public static final int QUESTIONARIO_SOCIO_ECONOMICO = 3;
	
	/** Constante que representa os questionários do cadastro de inscritos para o vestibular */
	public static final int QUESTIONARIO_VESTIBULAR = 4;
	
	/** Constante que representa os questionários de Relatório de Estágio que o Discente Responde */
	public static final int RELATORIO_DE_ESTAGIO_DISCENTE = 5;
	
	/** Constante que representa os questionários de Relatório de Estágio que o Supervisor do Estágio Responde */
	public static final int RELATORIO_DE_ESTAGIO_SUPERVISOR = 6;		

	/** Constante que representa os questionários de acompanhamento das ações de extensão */
	public static final int QUESTIONARIO_ACAO_EXTENSAO = 7;		
	
	/** Constante que representa os questionários do cadastro de inscritos para ações de extenção(Área Pública) */
	public static final int QUESTIONARIO_INSCRICAO_ATIVIDADE = 8;
	
	/** Constante que representa os questionários de Relatório do Orientador de Estágio que o Orientador do Estágio Responde */
	public static final int RELATORIO_DE_ORIENTADOR_DE_ESTAGIO = 9;
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
					parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_questionario", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descrição do tipo de questionário. */
	private String descricao;

	/** Indica se o tipo de questionário aceita definição de um gabarito. */
	@Column(name = "aceita_definicao_gabarito")
	private boolean aceitaDefinicaoGabarito;
	
	/** Construtor padrão. */
	public TipoQuestionario() {
		super();
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public TipoQuestionario(int id) {
		this.id = id;
	}

	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a descrição do tipo de questionário.
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descrição do tipo de questionário.
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Indica se o tipo de questionário aceita definição de um gabarito. 
	 * @return
	 */
	public boolean isAceitaDefinicaoGabarito() {
		return aceitaDefinicaoGabarito;
	}

	/** Seta se o tipo de questionário aceita definição de um gabarito. 
	 * @param aceitaDefinicaoGabarito
	 */
	public void setAceitaDefinicaoGabarito(boolean aceitaDefinicaoGabarito) {
		this.aceitaDefinicaoGabarito = aceitaDefinicaoGabarito;
	}

	/** Indica se é do tipo PROCESSO_SELETIVO
	 * @return
	 */
	public boolean isProcessoSeletivo() {
		return this.id == PROCESSO_SELETIVO;
	}
	
	/** Indica se é do tipo QUESTIONARIO_SOCIO_ECONOMICO
	 * @return
	 */
	public boolean isSocioEconomico() {
		return this.id == QUESTIONARIO_SOCIO_ECONOMICO;
	}
	
	/** Indica se é do tipo QUESTIONARIO_VESTIBULAR
	 * @return
	 */
	public boolean isQuestionarioVestibular() {
		return this.id == QUESTIONARIO_VESTIBULAR;
	}
	
	/** Indica se é do tipo QUESTIONARIO_INSCRICAO_ATIVIDADE
	 * @return
	 */
	public boolean isQuestionarioInscricaoAtividade() {
		return this.id == QUESTIONARIO_INSCRICAO_ATIVIDADE;
	}
	
	/** Indica se é do tipo RELATORIO_DE_ESTAGIO
	 * @return
	 */
	public boolean isQuestionarioRelatorioEstagio() {
		return this.id == RELATORIO_DE_ESTAGIO_DISCENTE || this.id == RELATORIO_DE_ESTAGIO_SUPERVISOR || this.id == RELATORIO_DE_ORIENTADOR_DE_ESTAGIO;
	}	

	/** Indica se é do tipo Acompanhamento de uma ação de Extensão.
	 * @return
	 */
	public boolean isAcaoExtensao() {
		return this.id == QUESTIONARIO_ACAO_EXTENSAO;
	}
	
	/** Retorna uma descrição textual do tipo de questionário.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return descricao != null ? descricao : "";
	}

	/** Indica se as respostas ao questionário são de preenchimento obrigatório.
	 * @return
	 */
	public boolean isRespostasObrigatorias() {
		return isSocioEconomico() || isQuestionarioVestibular();
	}

}
