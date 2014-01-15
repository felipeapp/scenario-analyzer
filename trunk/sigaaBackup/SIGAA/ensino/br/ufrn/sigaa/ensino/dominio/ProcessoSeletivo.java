/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/12/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Calendar;
import java.util.Collection;
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

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * Representa um processo seletivo onde os alunos podem realizar pr�-inscri��es
 * pela parte p�blica do SIGAA
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name="processo_seletivo", schema="ensino")
public class ProcessoSeletivo implements Validatable {

	/**
	 * Atributo que define a unicidade da classe.
	 */
	@Id
	@Column(name="id_processo_seletivo")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="ensino.processo_seletivo_id_seq") })
	private int id;
	
	/**
	 * Atributo utilizado somente quando processos seletivo de n�vel de ensino (P�s-Gradua��o e T�cnico).
	 * Os processos seletivos de stricto, lato e t�cnico n�o possuem matriz curricular, se relacionam diretamente com curso.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_curso", nullable = true)
	private Curso curso;
	
	/**
	 * Atributo utilizado para agrupar os processos seletivos quando necess�rio.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_edital_processo_seletivo")
	private EditalProcessoSeletivo editalProcessoSeletivo;
	
	/**
	 * Atributo utilizado somente quando processos seletivo de n�vel de ensino (Gradua��o) 
	 * Os processos seletivos de gradua��o se relaciona com curso indiretamente, atrav�s da matriz curricular.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_matriz_curricular", nullable = true)
	private MatrizCurricular matrizCurricular;
	
	/**
	 * Atributo que define o numero sequencial do processo seletivo.
	 */
	@Column(name="sequencia_inscricoes")
	private int sequenciaInscricoes = 0;
	
	/**
	 * Atributo que define as opera��es executadas.	  
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada criadoPor;

	/**
	 * Atributo que define a data de cadastro do processo seletivo.
	 */
	@Column(name="data_cadastro")
	@CriadoEm
	private Date criadoEm;
	
	/**
	 * Indica a quantidade de vagas dispon�veis no processos seletivo
	 */
	@Column(name="vagas")
	private Integer vaga = 0;
	
	/**
	 * Atributo que indica as vagas restantes para um processo seletivo
	 * Somente � setado algum valor se {@link EditalProcessoSeletivo#isVerificaExisteVaga() for TRUE.
	 */
	@Transient
	private Integer vagaRestante = 0;

	/**
	 * Atributo que indica se o processo est� ativo.
	 */
	private boolean ativo;
	
	/**
	 * Quando o processo seletivo for stricto, o orientador escolhido pelo aluno ser� notificado.
	 */
	@Column(name = "notificar_orientador")
	private Boolean notificarOrientador = Boolean.FALSE;
	
	/** Atributo que define o question�rio par o processo seletivo.  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_questionario")
	private Questionario questionario;
	
	/** 
	 * Atrbuto que define a lista de inscritos para o processo seletivo. 
	 */
	@Transient
	private Collection<InscricaoSelecao> inscritos;
	
	public ProcessoSeletivo(){
		curso = new Curso();
		editalProcessoSeletivo = new EditalProcessoSeletivo();
		criadoEm = editalProcessoSeletivo.getCriadoEm();
		criadoPor = editalProcessoSeletivo.getCriadoPor();
		ativo = true;
		sequenciaInscricoes = 0;
	}

	public ProcessoSeletivo(int id){
		this.id = id;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna a data de cria��o do edital do processo seletivo.
	 * @return
	 */
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/**
	 * Retorna o resigtro de entrada contendo entre outras informa��es
	 * o usu�rio que cadastrou o edital do processo seletivo.
	 * @return
	 */
	public RegistroEntrada getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public int getSequenciaInscricoes() {
		return this.sequenciaInscricoes;
	}

	public void setSequenciaInscricoes(int sequenciaInscricoes) {
		this.sequenciaInscricoes = sequenciaInscricoes;
	}

	public String getNome() {
		return getEditalProcessoSeletivo().getNome();
	}
	
	public String getNomeCompleto(){
		return getCurso().getDescricao() + " - "+ getCurso().getNivelDescricao(); 
	}
	
	public String getDescricaoCompleta(){
		return getEditalProcessoSeletivo().getNome() + " - " + getCurso().getNivelDescricao(); 
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public boolean getAtivo() {
		return this.ativo;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public Collection<InscricaoSelecao> getInscritos() {
		return inscritos;
	}

	public void setInscritos(Collection<InscricaoSelecao> inscritos) {
		this.inscritos = inscritos;
	}

	/**
	 * retorna o n�mero de aprovados no processo seletivo
	 * @return
	 */
	public int getNumeroAprovados(){
		int count = 0;
		if( inscritos != null ){
			for( InscricaoSelecao inscricao : inscritos ){
				if( inscricao.isAprovada() )
					count++;
			}
		}
		return count;
	}
	
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
	
		ValidatorUtil.validateRequired(editalProcessoSeletivo.getNome(), "T�tulo do Edital", erros);
		
		ValidatorUtil.validateRequired(editalProcessoSeletivo.getInicioInscricoes(), "Data In�cio das Inscri��es", erros);
		if(  getId()==0 && !ValidatorUtil.isEmpty(editalProcessoSeletivo.getInicioInscricoes()) )
			ValidatorUtil.validateFuture( CalendarUtils.adicionaDias(editalProcessoSeletivo.getInicioInscricoes(),1) , "Data In�cio das Inscri��es", erros);
		
		ValidatorUtil.validateRequired(editalProcessoSeletivo.getFimInscricoes(), "Data Fim das Inscri��es", erros);
		if( getId()==0 && !ValidatorUtil.isEmpty(editalProcessoSeletivo.getFimInscricoes()) )
			ValidatorUtil.validaInicioFim(editalProcessoSeletivo.getInicioInscricoes(), editalProcessoSeletivo.getFimInscricoes(), "Per�odo das Inscri��es", erros);
						
		if (editalProcessoSeletivo.getIdConfiguracaoGRU() != null) {
			if( !ValidatorUtil.isEmpty(editalProcessoSeletivo.getDataVencimentoBoleto()) )
				ValidatorUtil.validateFuture(CalendarUtils.adicionaDias(editalProcessoSeletivo.getDataVencimentoBoleto(),1), "Data de Vencimento do Boleto", erros);
			else
				validateRequired(editalProcessoSeletivo.getDataVencimentoBoleto(), "Data de Vencimento do Boleto", erros);
		} else {
			editalProcessoSeletivo.setTaxaInscricao(0);
		}
		
		return erros;
		
	}

	/**
	 * M�todo que incrementa a sequ�ncia no cadastro do processos seletivo.
	 */
	public void incrementarSequencia() {
		sequenciaInscricoes += 1;
	}
	
	/**
	 * Verifica se o processo seletivo est� agrupado por um edital.
	 * @return
	 */
	public boolean isAgrupado(){
		
		return (getEditalProcessoSeletivo()!=null);
		
	}

	/**
	 * Retorna se o processo seletivo est� aberto para inscri��es
	 *
	 * @return
	 */
	public boolean isInscricoesAbertas() {
		
		Date hoje = new Date();
		if( getEditalProcessoSeletivo().getInicioInscricoes() != null 
			&& getEditalProcessoSeletivo().getFimInscricoes() != null
			&& (hoje.equals(getEditalProcessoSeletivo().getInicioInscricoes()) || hoje.equals(getEditalProcessoSeletivo().getFimInscricoes())
					|| ( hoje.after(getEditalProcessoSeletivo().getInicioInscricoes()) && hoje.before(getEditalProcessoSeletivo().getFimInscricoes()))) ){
			
			//Se o o n�vel de ensino do curso do processo seletivo for stricto-sensu e n�o estiver publicado
			if(curso!=null && curso.isStricto() && !isPublicado()) 
				return false;
			
			return true;
		}
		return false;
		
	}
	
	/**
	 * Retorna se o processo seletivo finalizou o per�odo de inscri��o.
	 * @return
	 */
	public boolean isInscricoesFinalizadas(){
		
		Date hoje = new Date();
		if( getEditalProcessoSeletivo().getInicioInscricoes() != null 
				&& getEditalProcessoSeletivo().getFimInscricoes() != null
				&&  hoje.after(getEditalProcessoSeletivo().getFimInscricoes()) )
			return true;
		
		return false;
		
	}
	
	/**
	 * Retorna se o processo seletivo finalizou o per�odo de agendamento.
	 * @return
	 */
	public boolean isAgendaFinalizada(){
		
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		if( getEditalProcessoSeletivo().getInicioPeriodoAgenda() != null 
				&& getEditalProcessoSeletivo().getFimPeriodoAgenda() != null
				&&  hoje.after(getEditalProcessoSeletivo().getFimPeriodoAgenda()) )
			return true;
		
		return false;
		
	}

	/**
	 * Retorna o n�vel de ensino do curso ao qual o processo seletivo se refere
	 * 
	 * @return
	 */
	public char getNivelEnsino() {
		return getCurso()!=null?getCurso().getNivel(): getMatrizCurricular().getCurso().getNivel();
	}

	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	public Integer getVaga() {
		return vaga;
	}

	public void setVaga(Integer vagas) {
		this.vaga = vagas;
	}

	public EditalProcessoSeletivo getEditalProcessoSeletivo() {
		return editalProcessoSeletivo;
	}

	public void setEditalProcessoSeletivo(
			EditalProcessoSeletivo editalProcessoSeletivo) {
		this.editalProcessoSeletivo = editalProcessoSeletivo;
	}

	public Boolean getNotificarOrientador() {
		return notificarOrientador;
	}

	public void setNotificarOrientador(Boolean notificarOrientador) {
		this.notificarOrientador = notificarOrientador;
	}

	/**
	 * Retorna o status do processo seletivo.
	 * @return
	 */
	public Integer getStatus() {
		if(!ValidatorUtil.isEmpty(editalProcessoSeletivo))
			return getEditalProcessoSeletivo().getStatus();
		return null;
	}

	/**
	 * Retorna o motivo da altera��o do processo seletivo.
	 * Somente utilizado para o n�vel Strico Sensu.
	 * @return
	 */
	public String getMotivoAlteracao() {
		if(!ValidatorUtil.isEmpty(editalProcessoSeletivo))
			return getEditalProcessoSeletivo().getMotivoAlteracao();
		
		return null;
	}


	/**
	 * Faz a verifica��o do Status para pode alterar o processo seletivo ou n�o. (para P�s-Gradua��o).
	 * @return
	 */
	@Transient
	public boolean isPermiteAlterar() {
		boolean permiteAlterar = false;
		for (Integer aux : StatusProcessoSeletivo.getAtivos()){
			if (aux.equals( this.getStatus() )){
				permiteAlterar = true;
				break;
			}
		}
		return permiteAlterar;
	}

	/**
	 * Retorna a descri��o do Status do processo seletivo
	 * @return
	 */
	@Transient
	public String getDescricaoStatus() {
		if (this.getStatus() != null)
			return StatusProcessoSeletivo.getDescricao(this.getStatus());
		else
			return "N�O DEFINIDO";
	}
	
	/**
	 * Verifica se o Processo Seletivo est� pendente para ser publicado (Apenas para P�s-Gradua��o)
	 * @return
	 */
	@Transient
	public boolean isPendentePublicacao(){
		if (this.getStatus() != null)
			return this.getStatus().equals( StatusProcessoSeletivo.PENDENTE_APROVACAO );
		else
			return false;
	}
	
	/**
	 * Verifica se o Processo Seletivo foi solicitado altera��o pela PPG (Apenas para P�s-Gradua��o)
	 * @return
	 */
	@Transient
	public boolean isSolicitadoAlteracao(){
		if (this.getStatus() != null)
			return this.getStatus().equals( StatusProcessoSeletivo.SOLICITADO_ALTERACAO );
		else
			return false;
	}	
	
	/**
	 * Verifica se o Processo Seletivo est� publicado (Apenas para P�s-Gradua��o)
	 * @return
	 */
	@Transient
	public boolean isPublicado(){
		if (this.getStatus() != null)
			return this.getStatus().equals( StatusProcessoSeletivo.PUBLICADO );
		else
			return false;
	}
	
	/**
	 * Retorna o n�mero de inscritos de um processo seletivo
	 * @return
	 */
	@Transient
	public Integer getQtdInscritos(){
		return !ValidatorUtil.isEmpty(getInscritos())?getInscritos().size():0;
	}
	
	/**
	 * Retorna se o processo seletivo pertence a um edital que n�o possui agendamento.
	 * @return
	 */
	@Transient
	public boolean isPossuiAgendamento(){
		return this.editalProcessoSeletivo != null && this.editalProcessoSeletivo.isPossuiAgendamento(); 
	}
	
	/**
	 * Popula o horario na data de inscri��o.
	 */
	public void populaHoraPeriodoInscricao(){
		
		//Adiciona a hora inicial a data do in�cio das inscri��es
		Calendar cHoraInicio = Calendar.getInstance();
		cHoraInicio.setTime( getEditalProcessoSeletivo().getHoraInicioInscricoes() );
		getEditalProcessoSeletivo().setInicioInscricoes( CalendarUtils.configuraTempoDaData(getEditalProcessoSeletivo().getInicioInscricoes(),
				cHoraInicio.get(Calendar.HOUR_OF_DAY), cHoraInicio.get(Calendar.MINUTE), cHoraInicio.get(Calendar.SECOND), 0) );
		
		//Adiciona a hora final a data do fim das inscri��es
		Calendar cHoraFim = Calendar.getInstance();
		cHoraFim.setTime( getEditalProcessoSeletivo().getHoraFimInscricoes() );
		getEditalProcessoSeletivo().setFimInscricoes( CalendarUtils.configuraTempoDaData(getEditalProcessoSeletivo().getFimInscricoes(), 
				cHoraFim.get(Calendar.HOUR_OF_DAY), cHoraFim.get(Calendar.MINUTE), cHoraFim.get(cHoraInicio.get(Calendar.SECOND)), 
				cHoraFim.get(cHoraInicio.get(Calendar.MILLISECOND))) );
		
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "curso", "matrizCurricular");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, curso, matrizCurricular);
	}

	/**
	 * Retorna a quantidade que ainda restam no processo seletivo.
	 * Somente retorna algum valor se {@link EditalProcessoSeletivo#isVerificaExisteVaga() for TRUE.
	 * @return
	 */
	public Integer getVagaRestante() {
		return vagaRestante;
	}

	/**
	 * Somente � setado algum valor se {@link EditalProcessoSeletivo#isVerificaExisteVaga() for TRUE.
	 * @param vagaRestante
	 */
	public void setVagaRestante(Integer vagaRestante) {
		this.vagaRestante = vagaRestante;
	}
	
}
