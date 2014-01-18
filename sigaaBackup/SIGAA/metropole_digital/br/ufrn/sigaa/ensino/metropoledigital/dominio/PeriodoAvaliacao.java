package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Esta entidade representa os per�odo de avalia��o que comp�em
 * um cronograma de execu��o das aulas do curso tecnico do IMD. 
 * 
 * @author Rafael A Silva
 * @author Rafael Barros
 * 
 */

@Entity
@Table(name = "periodo_avaliacao", schema = "metropole_digital")
@SuppressWarnings("serial")
public class PeriodoAvaliacao implements Validatable, PersistDB, Comparable<PeriodoAvaliacao>{
	
	/** Chave prim�ria da tabela periodo_avaliacao **/
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.periodo_avaliacao_id_periodo_avaliacao_seq") })
	@Column(name = "id_periodo_avaliacao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** N�mero de ordena��o do per�odo dentro do cronograma de execu��o **/
	@Column(name = "numero_periodo")
	private Integer numeroPeriodo;

	/** Data inicio do periodo **/
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data fim do periodo **/
	@Column(name = "data_fim")
	private Date datafim;
	
	/** C�digo de integra��o do periodo que ser� utilizado para sincronizar informa��es com o Moodle **/
	@Column(name = "codigo_integracao")
	private String codigoIntegracao;

	/** Campo que ir� armazenar o dia e m�s da data inicio do periodo em forma textual. Este atributo ser� utilizado em casos especificos na planilha de frequencia e notas. **/
	@Transient 
	private String diaMesInicioTexto;
	
	/** Campo que ir� armazenar o dia e m�s da data fim do periodo em forma textual. Este atributo ser� utilizado em casos especificos na planilha de frequencia e notas. **/
	@Transient
	private String diaMesFimTexto;
	
	/**Campo calculado com base nas cargas hor�rias do CargaHorariaSemanalDisciplina **/
	@Column(name = "ch_total_periodo")
	private int chTotalPeriodo;

	/** Cronograma de execu��o no qual o periodo pertence **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cronograma_execucao")
	private CronogramaExecucaoAulas cronogramaExecucaoAulas;

	/** Cole��o de cargas hor�rias semanais das disciplinas dentro do periodo **/
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "periodoAvaliacao")
	private List<CargaHorariaSemanalDisciplina> chsdList;

	public PeriodoAvaliacao() {
		chsdList = new ArrayList<CargaHorariaSemanalDisciplina>();
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	// GETTERS AND SETTERS
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getNumeroPeriodo() {
		return numeroPeriodo;
	}

	public void setNumeroPeriodo(Integer numeroPeriodo) {
		this.numeroPeriodo = numeroPeriodo;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDatafim() {
		return datafim;
	}

	public void setDatafim(Date datafim) {
		this.datafim = datafim;
	}

	public CronogramaExecucaoAulas getCronogramaExecucaoAulas() {
		return cronogramaExecucaoAulas;
	}

	public void setCronogramaExecucaoAulas(
			CronogramaExecucaoAulas cronogramaExecucaoAulas) {
		this.cronogramaExecucaoAulas = cronogramaExecucaoAulas;
	}

	public List<CargaHorariaSemanalDisciplina> getChsdList() {
		return chsdList;
	}

	public void setChsdList(List<CargaHorariaSemanalDisciplina> chsdList) {
		this.chsdList = chsdList;
	}

	public int getChTotalPeriodo() {
		return chTotalPeriodo;
	}

	public void setChTotalPeriodo(int chTotalPeriodo) {
		this.chTotalPeriodo = chTotalPeriodo;
	}
	
	/**Fun��o get do atributo diaMesInicioTexto que tamb�m efetua o preenchimento desse atributo**/
	public String getDiaMesInicioTexto() {
		retonarDataInicioTexto();
		return diaMesInicioTexto;
	}

	public void setDiaMesInicioTexto(String diaMesInicioTexto) {
		this.diaMesInicioTexto = diaMesInicioTexto;
	}

	/**Fun��o get do atributo diaMesFimTexto que tamb�m efetua o preenchimento desse atributo**/
	public String getDiaMesFimTexto() {
		retonarDataInicioTexto();
		return diaMesFimTexto;
	}

	public void setDiaMesFimTexto(String diaMesFimTexto) {
		this.diaMesFimTexto = diaMesFimTexto;
	}

	public String getCodigoIntegracao() {
		return codigoIntegracao;
	}

	public void setCodigoIntegracao(String codigoIntegracao) {
		this.codigoIntegracao = codigoIntegracao;
	}

	
	/**M�todo respons�vel pelo preenchimento das vari�veis diaMesInicioTexto e diaMesFimTexto **/
	private void retonarDataInicioTexto(){
		String retorno = "";
		String dataInicioTexto = dataInicio.toString();
		
		retorno += dataInicioTexto.substring(8, 10);
		retorno += "/";
		retorno += dataInicioTexto.substring(5, 7);
		
		this.diaMesInicioTexto = retorno;
		
		retorno = "";
		String dataFimTexto = datafim.toString();
		
		retorno += dataFimTexto.substring(8, 10);
		retorno += "/";
		retorno += dataFimTexto.substring(5, 7);
		
		this.diaMesFimTexto = retorno;
		
		
	}

	@Override
	public int compareTo(PeriodoAvaliacao o) {
		return this.getNumeroPeriodo()-o.getNumeroPeriodo();
	}

}
