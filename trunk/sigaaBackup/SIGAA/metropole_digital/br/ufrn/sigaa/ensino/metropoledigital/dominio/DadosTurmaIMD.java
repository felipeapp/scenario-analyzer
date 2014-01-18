package br.ufrn.sigaa.ensino.metropoledigital.dominio;

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

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que relaciona um vínculo de um tutor com uma Turma.
 * 
 * @author Gleydson, Rafael Silva, Rafael Barros
 *
 */
@Entity
@Table(name = "dados_turma_imd", schema = "metropole_digital")
public class DadosTurmaIMD implements PersistDB {
	/**ID da entidade*/
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.dados_turma_imd_id_dados_turma_imd_seq") })
	@Column(name = "id_dados_turma_imd", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/**Cronograma ao qual a turma está vinculada*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cronograma_execucao")
	private CronogramaExecucaoAulas cronograma;
	
	/**Código de Integração com o moodle*/
	@Column(name = "codigo_integracao")
	private String codigoIntegracao;
	
	/**Local onde será realizado as aulas*/
	private String local;
	
	/**Horário em que acontecerá as aulas*/
	private String horario;
	
	/**Identifica se a turma foi consolidada parcialmente ou não*/
	@Column(name="consolidado_parcialmente")
	private Boolean consolidadoParcialmente=false;
	
	/**Variável de controle que sinaliza se a turma foi selecionada ou não para o relatorio de frequencia do assistente social do IMD**/
	@Transient
	private boolean selecionada = false;
	
	//GETTERS AND SETTERS
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CronogramaExecucaoAulas getCronograma() {
		return cronograma;
	}

	public void setCronograma(CronogramaExecucaoAulas cronograma) {
		this.cronograma = cronograma;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public String getCodigoIntegracao() {
		return codigoIntegracao;
	}

	public void setCodigoIntegracao(String codigoIntegracao) {
		this.codigoIntegracao = codigoIntegracao;
	}

	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public Boolean isConsolidadoParcialmente() {
		return consolidadoParcialmente;
	}

	public void setConsolidadoParcialmente(Boolean consolidadoParcialmente) {
		this.consolidadoParcialmente = consolidadoParcialmente;
	}
	
	
}
