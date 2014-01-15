/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 10/03/2010
 */

package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que representa as atividades de um Plano de Docência Assistida.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="atividade_docencia_assistida", schema="stricto_sensu")
public class AtividadeDocenciaAssistida implements Validatable {

	/**
	 * Chave primária da indicação.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_atividade_docencia_assistida")
	private int id;			
	
	/**
	 * Plano de Docência Assistida.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_plano_docencia_assistida")
	private PlanoDocenciaAssistida planoDocenciaAssistida;
	
	/**
	 * Atividades do plano de atuação.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_forma_atuacao_docencia_assistida")
	private FormaAtuacaoDocenciaAssistida formaAtuacao;
	
	/**
	 * Frequência das aulas.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_frequencia_atividade_docencia_assistida")
	private FrequenciaAtividadeDocenciaAssistida frequenciaAtividade;
	
	/**
	 * Descrição de como organizar a atividade.
	 */
	@Column(name="como_organizar")
	private String comoOrganizar;
	
	/**
	 * Data de início da atividade
	 */
	@Column(name="data_inicio")
	private Date dataInicio;
	
	/**
	 * Data final da atividade.
	 */
	@Column(name="data_fim")
	private Date dataFim;
	
	/**
	 * Carga Horária da atividade
	 */
	private Integer ch;
	
	/**
	 * Procedimentos da atividade
	 */
	private String procedimentos;
	
	/**
	 *  Caso Selecionar Outra Atividade, Descriminar a atividade
	 */
	@Column(name="outra_atividade")
	private String outraAtividade;
		
	/**
	 * Percentual de realização da atividade
	 */
	@Column(name="percentual_realizado")
	private int percentualRealizado;
	
	/**
	 * Resultados obtidos na atividade
	 */
	@Column(name="resultados_obtidos")
	private String resultadosObtidos;
	
	/**
	 * Dificuldades encontradas na atividade
	 */
	@Column(name="dificuldades_encontradas")
	private String dificuldades;
	
	/** Indica se a atividade foi prevista no relatório semestral */
	private boolean prevista;
	
	/** Indica que a tarefa está sendo alterada */
	@Transient
	private boolean alteracao = false;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PlanoDocenciaAssistida getPlanoDocenciaAssistida() {
		return planoDocenciaAssistida;
	}

	public void setPlanoDocenciaAssistida(
			PlanoDocenciaAssistida planoDocenciaAssistida) {
		this.planoDocenciaAssistida = planoDocenciaAssistida;
	}

	public FormaAtuacaoDocenciaAssistida getFormaAtuacao() {
		return formaAtuacao;
	}

	public void setFormaAtuacao(FormaAtuacaoDocenciaAssistida formaAtuacao) {
		this.formaAtuacao = formaAtuacao;
	}

	public String getComoOrganizar() {
		return comoOrganizar;
	}

	public void setComoOrganizar(String comoOrganizar) {
		this.comoOrganizar = comoOrganizar;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getCh() {
		return ch;
	}

	public void setCh(Integer ch) {
		this.ch = ch;
	}

	public String getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(String procedimentos) {
		this.procedimentos = procedimentos;
	}

	/**
	 * Valida os atributos da atividade. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate() 
	 */		
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(planoDocenciaAssistida, "Plano de Docência Assitida", lista);
		ValidatorUtil.validateRequired(formaAtuacao, "Atividade do Plano de Atuação", lista);
		
		ValidatorUtil.validateRequired(comoOrganizar, "Como Organizar a Atividade", lista);
		ValidatorUtil.validateRequired(dataInicio, "Data de Início da Atividade", lista);
		ValidatorUtil.validateRequired(dataFim, "Data de Fim da Atividade", lista);
		
		ValidatorUtil.validateRequired(frequenciaAtividade, "Frequência da Atividade", lista);
		
		ValidatorUtil.validateRequired(ch, "Carga Horária da Atividade", lista);
		
		return lista;
	}
	
	/**
	 * Compara o ID e do estágio com o passado por parâmetro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}


	/** 
	 * Calcula e retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}	

	public String getOutraAtividade() {
		return outraAtividade;
	}

	public void setOutraAtividade(String outraAtividade) {
		this.outraAtividade = outraAtividade;
	}

	public int getPercentualRealizado() {
		return percentualRealizado;
	}

	public void setPercentualRealizado(int percentualRealizado) {
		this.percentualRealizado = percentualRealizado;
	}

	public String getResultadosObtidos() {
		return resultadosObtidos;
	}

	public void setResultadosObtidos(String resultadosObtidos) {
		this.resultadosObtidos = resultadosObtidos;
	}

	public String getDificuldades() {
		return dificuldades;
	}

	public void setDificuldades(String dificuldades) {
		this.dificuldades = dificuldades;
	}

	public boolean isPrevista() {
		return prevista;
	}

	public void setPrevista(boolean prevista) {
		this.prevista = prevista;
	}

	public FrequenciaAtividadeDocenciaAssistida getFrequenciaAtividade() {
		return frequenciaAtividade;
	}

	public void setFrequenciaAtividade(
			FrequenciaAtividadeDocenciaAssistida frequenciaAtividade) {
		this.frequenciaAtividade = frequenciaAtividade;
	}

	public boolean isAlteracao() {
		return alteracao;
	}

	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
	}
}
