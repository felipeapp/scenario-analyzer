/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '09/01/2007'
 *
 */
package br.ufrn.sigaa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaInicioFim;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.negocio.PeriodoAcademicoHelper;

/**
 * Entidade que implementa o calend�rio acad�mico dos diversos n�veis, cursos e unidades
 * @author Gleydson
 */
@Entity
@Table(schema="comum", name = "calendario_academico", uniqueConstraints = {})
public class CalendarioAcademico implements Validatable {

	// Fields
	/** Chave prim�ria. */
	private int id;

	/** Ano do calend�rio */
	private int ano;

	/** Per�odo do calend�rio */
	private int periodo;

	/** Unidade do calend�rio */
	private Unidade unidade = new Unidade();

	/** N�vel de ensino do calend�rio */
	private char nivel;

	/** Modalidade de educa��o do calend�rio */
	private ModalidadeEducacao modalidade;

	/** Conv�nio acad�mico do calend�rio */
	private ConvenioAcademico convenio;

	/** Curso do calend�rio */
	private Curso curso;

	/** Indica se o calend�rio est� ativo ou n�o */
	private boolean ativo;

	/** Indica se o calend�rio � vigente */
	private boolean vigente;

	/** Registro entrada de quem cadastrou */
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data de cadastro */
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada da �ltima atualiza��o */
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data da �ltima atualiza��o */
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Ano de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado */
	private Integer anoFeriasVigente;

	/** Per�odo de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado */
	private Integer periodoFeriasVigente;

	/** Ano que as solicita��es de turma ir�o ter */
	private Integer anoNovasTurmas;

	/** Per�odo que as solicita��es de novas turmas ir�o ter */
	private Integer periodoNovasTurmas;
	
	/** Data de in�cio do per�odo letivo */
	private Date inicioPeriodoLetivo, fimPeriodoLetivo;

	 // PER�ODOS RELACIONADOS COM MATR�CULA 
	/** Per�odo para alunos solicitarem suas matr�culas */
	private Date inicioMatriculaOnline, fimMatriculaOnline;

	/** Per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) */
	private Date inicioMatriculaAlunoCadastrado, fimMatriculaAlunoCadastrado;
	
	/** Per�odo DAE matricular alunos especiais */
	private Date inicioMatriculaAlunoEspecial, fimMatriculaAlunoEspecial;

	/** Per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula */
	private Date inicioCoordenacaoAnaliseMatricula, fimCoordenacaoAnaliseMatricula;

	/** Per�odo para os alunos analisarem as an�lises dos coordenadores */
	private Date inicioDiscenteAnaliseMatricula, fimDiscenteAnaliseMatricula;

	/** Per�odo do processamento de matr�cula */
	private Date inicioProcessamentoMatricula, fimProcessamentoMatricula;

	/** Per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula */
	private Date inicioAjustesMatricula, fimAjustesMatricula;

	/** Per�odo para alunos solicitarem a re-matricula nas disciplinas */
	private Date inicioReMatricula, fimReMatricula;

	/** Per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula */
	private Date inicioCoordenacaoAnaliseReMatricula, fimCoordenacaoAnaliseReMatricula;

	/** Per�odo para os alunos analisarem as an�lises dos coordenadores */
	private Date inicioDiscenteAnaliseReMatricula, fimDiscenteAnaliseReMatricula;

	/** Per�odo de processamento da rematr�cula */
	private Date inicioProcessamentoReMatricula, fimProcessamentoReMatricula;

	/** Per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula */
	private Date inicioAjustesReMatricula, fimAjustesReMatricula;
	
	/** Per�odo para os alunos realizarem matr�culas extraordin�rias em turmas com vagas ainda remanescentes */
	private Date inicioMatriculaExtraordinaria, fimMatriculaExtraordinaria;

	private Date inicioValidacaoVinculoIngressante, fimValidacaoVinculoIngressante;
	
	// PER�ODOS RELACIONADOS COM TURMA DE ENSINO INDIVIDUAL 
	/** Per�odo de requerimento de ensino individualizado pelos alunos */
	private Date inicioRequerimentoEnsinoIndiv, fimRequerimentoEnsinoIndiv;

	/** Per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos */
	private Date inicioSolicitacaoTurmaEnsinoIndiv, fimSolicitacaoTurmaEnsinoIndiv;

	/** Per�odo de cadastro de turmas de ensino individualizado pelos departamentos */
	private Date inicioCadastroTurmaEnsinoIndiv, fimCadastroTurmaEnsinoIndiv;

	
	// PER�ODOS RELACIONADOS COM TURMA DE F�RIAS 
	/** Per�odo de f�rias */
	private Date inicioFerias, fimFerias;

	/** Per�odo de requerimento de F�RIAS pelos alunos */
	private Date inicioRequerimentoTurmaFerias, fimRequerimentoTurmaFerias;

	/** Per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento */
	private Date inicioSolicitacaoTurmaFerias, fimSolicitacaoTurmaFerias;

	/** Per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento */
	private Date inicioCadastroTurmaFerias, fimCadastroTurmaFerias;

	/** Per�odo de matricula de aluno em turma de f�rias */
	private Date inicioMatriculaTurmaFerias, fimMatriculaTurmaFerias;

	/** Per�odo para os alunos realizarem matr�culas extraordin�rias em turmas de f�rias com vagas ainda remanescentes */
	private Date inicioMatriculaExtraordinariaFerias, fimMatriculaExtraordinariaFerias;
	
	// OUTROS PER�ODOS 
	/** Per�odo permitido para trancamento de matr�culas de alunos em turmas */
	private Date inicioTrancamentoTurma, fimTrancamentoTurma;
	
	/**Per�odo permitido para trancamento de programa feito pelos alunos. */
	private Date inicioTrancamentoPrograma, fimTrancamentoPrograma;
	
	/**Per�odo permitido para trancamento de programa a posteriori feito pelos alunos. */
	private Date inicioTrancamentoProgramaPosteriori, fimTrancamentoProgramaPosteriori;
	
	/** Per�odo para solicita��o de cadastro de turma aos chefes de departamento */
	private Date inicioSolicitacaoTurma, fimSolicitacaoTurma;

	/** Per�odo para os docentes consolidarem suas turmas */
	private Date inicioConsolidacaoTurma, fimConsolidacaoTurma;
	
	/** Per�odo para os docentes consolidarem parcialmente suas turmas */
	private Date inicioConsolidacaoParcialTurma, fimConsolidacaoParcialTurma;

	/** Per�odo de cadastro de turmas realizado pelo chefe de departamento */
	private Date inicioCadastroTurma, fimCadastroTurma;
	
	/** Per�odo de cadastro de sugest�o de cria��o de turmas realizado pelo chefe de departamento. */
	private Date inicioSugestaoTurmaChefe, fimSugestaoTurmaChefe;

	/** Per�odo de cadastro de Plano de Mattr�cula pela Coordena��o */
	private Date inicioCadastroPlanoMatricula, fimCadastroPlanoMatricula;

	/** Per�odo de cadastramento de novos alunos */
	private Date inicioCadastramentoDiscente, fimCadastramentoDiscente;
	
	/** Conjunto de eventos extras. */
	private Set<EventoExtraSistema> eventosExtra = new HashSet<EventoExtraSistema>();
	
	// Constructors
	/** Default constructor */
	public CalendarioAcademico() {
	}

	/** Minimal constructor */
	public CalendarioAcademico(int idCalendario) {
		this.id = idCalendario;
	}

	/** Construtor parametrizado. */
	public CalendarioAcademico(Unidade unidade2, char nivel2) {
		unidade = unidade2;
		nivel = nivel2;
	}

	// Property accessors
	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_calendario", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idCalendario) {
		this.id = idCalendario;
	}

	/** Retorna a unidade do calend�rio.
	 * @return unidade do calend�rio 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_unidade")
	public Unidade getUnidade() {
		return this.unidade;
	}

	/** Seta a unidade do calend�rio.
	 * @param unidade unidade do calend�rio 
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/** Retorna o registro entrada de quem cadastrou 
	 * @return registro entrada de quem cadastrou 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	/** Seta o registro entrada de quem cadastrou 
	 * @param registroCadastro registro entrada de quem cadastrou 
	 */
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	/** Retorna a data de cadastro 
	 * @return data de cadastro 
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data de cadastro 
	 * @param dataCadastro data de cadastro 
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna o registro entrada da �ltima atualiza��o 
	 * @return registro entrada da �ltima atualiza��o 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o registro entrada da �ltima atualiza��o 
	 * @param registroAtualizacao registro entrada da �ltima atualiza��o 
	 */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Retorna a data da �ltima atualiza��o 
	 * @return data da �ltima atualiza��o 
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	/** Seta a data da �ltima atualiza��o 
	 * @param dataAtualizacao data da �ltima atualiza��o 
	 */
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	/** Compara os dois objetos.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna o c�digo hash do objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Valida os atributos do calend�rio.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if(this.nivel != NivelEnsino.MEDIO){
			if (ano < 2000 ) {
				lista.addErro("Ano inv�lido");
				return lista;
			}
			
			if ( !PeriodoAcademicoHelper.getInstance().isPeriodoRegular(periodo) ) {
				lista.addErro("O per�odo deve ser: " + PeriodoAcademicoHelper.getInstance().getPeriodosRegularesFormatado());
				return lista;
			}
			
		} else if (ano < 2000)
			lista.addErro("Ano inv�lido");
		
		validaInicioFim(inicioPeriodoLetivo, fimPeriodoLetivo, "Per�odo Letivo", lista);
		
		if(this.nivel != NivelEnsino.MEDIO) {
			validaInicioFim(inicioConsolidacaoTurma, fimConsolidacaoTurma, "Consolida��o de Turmas", lista);
			validaInicioFim(inicioConsolidacaoParcialTurma, fimConsolidacaoParcialTurma, "Consolida��o Parcial de Turmas", lista);
			validaInicioFim(inicioTrancamentoTurma, fimTrancamentoTurma, "Trancamento de Turmas", lista);
			validaInicioFim(inicioMatriculaOnline, fimMatriculaOnline, "Matr�cula Online", lista);
	
			if(this.nivel != NivelEnsino.TECNICO){
				validaInicioFim(inicioMatriculaAlunoCadastrado, fimMatriculaAlunoCadastrado, "Matr�cula de Aluno Ingressante", lista);
				validaInicioFim(inicioFerias, fimFerias, "F�rias", lista);
				validaInicioFim(inicioSolicitacaoTurma, fimSolicitacaoTurma, "Solicita��o de Cadastro de Turmas", lista);
				validaInicioFim(inicioMatriculaAlunoEspecial, fimMatriculaAlunoEspecial, "Matr�cula de aluno especial", lista);
				validaInicioFim(inicioCoordenacaoAnaliseMatricula, fimCoordenacaoAnaliseMatricula, "An�lise dos Coordenadores", lista);
				validaInicioFim(inicioDiscenteAnaliseMatricula, fimDiscenteAnaliseMatricula, "An�lise dos Discentes", lista);
				validaInicioFim(inicioProcessamentoMatricula, fimProcessamentoMatricula, "Processamento de Matr�cula", lista);
				validaInicioFim(inicioAjustesMatricula, fimAjustesMatricula, "Ajustes das Matr�culas/Turmas", lista);
				validaInicioFim(inicioReMatricula, fimReMatricula, "Re-Matr�cula", lista);
				validaInicioFim(inicioCoordenacaoAnaliseReMatricula, fimCoordenacaoAnaliseReMatricula, "An�lise dos Coordenadores das solicita��es de re-matr�cula", lista);
				validaInicioFim(inicioDiscenteAnaliseReMatricula, fimDiscenteAnaliseReMatricula, "An�lise dos Discentes para re-matr�cula", lista);
				validaInicioFim(inicioProcessamentoReMatricula, fimProcessamentoReMatricula, "Processamento de Re-Matr�cula", lista);
				validaInicioFim(inicioAjustesReMatricula, fimAjustesReMatricula, "Ajustes das Re-Matr�culas/Turmas", lista);
				validaInicioFim(inicioRequerimentoEnsinoIndiv, fimRequerimentoEnsinoIndiv, "Requerimento de Ensino Individualizado", lista);
				validaInicioFim(inicioMatriculaTurmaFerias, fimMatriculaTurmaFerias, "Matr�cula em Turmas de F�rias", lista);
				validaInicioFim(inicioMatriculaExtraordinaria, fimMatriculaExtraordinaria, "Matr�cula Extraordin�ria", lista);
				validaInicioFim(inicioMatriculaExtraordinariaFerias, fimMatriculaExtraordinariaFerias, "Matr�cula Extraordin�ria de F�rias", lista);
				validaInicioFim(inicioSugestaoTurmaChefe, fimSugestaoTurmaChefe, "Sugest�o de Turmas pelo Chefe do Departamento para o Pr�ximo Per�odo", lista);
			}
		} 
		
		if (this.nivel == NivelEnsino.GRADUACAO) {
			validaInicioFim(inicioCadastroPlanoMatricula, fimCadastroPlanoMatricula, "Plano de Matr�culas", lista);
		}

		if(this.nivel == NivelEnsino.STRICTO) {
			validateRequired(inicioPeriodoLetivo, "In�cio do Per�odo Letivo", lista);
			validateRequired(fimPeriodoLetivo, "Fim do Per�odo Letivo", lista);
			validaInicioFim(inicioPeriodoLetivo, fimPeriodoLetivo, "Per�odo Letivo", lista);
		}
		
		return lista;
	}

	/** Indica se o calend�rio est� ativo ou n�o. 
	 * @return true, se o calend�rio est� ativo ou n�o. 
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o calend�rio est� ativo ou n�o 
	 * @param ativo true, se o calend�rio est� ativo ou n�o 
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o n�vel de ensino do calend�rio.
	 * @return n�vel de ensino do calend�rio. 
	 */
	public char getNivel() {
		return nivel;
	}

	/** Seta o n�vel de ensino do calend�rio.
	 * @param nivel n�vel de ensino do calend�rio.
	 */
	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	/** Retorna o ano do calend�rio.
	 * @return ano do calend�rio.
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano do calend�rio.
	 * @param ano ano do calend�rio.
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o fim per�odo para alunos solicitarem suas matr�culas. 
	 * @return Fim do per�odo para alunos solicitarem suas matr�culas. 
	 */
	public Date getFimMatriculaOnline() {
		return fimMatriculaOnline;
	}

	/** Seta o fim do per�odo para alunos solicitarem suas matr�culas. 
	 * @param fimMatricula Fim do per�odo para alunos solicitarem suas matr�culas. 
	 */
	public void setFimMatriculaOnline(Date fimMatricula) {
		this.fimMatriculaOnline = fimMatricula;
	}

	/** Retorna data de fim do per�odo letivo.
	 * @return data de fim do per�odo letivo.
	 */
	public Date getFimPeriodoLetivo() {
		return fimPeriodoLetivo;
	}

	/** Seta a data de fim do per�odo letivo.
	 * @param fimSemestre data de fim do per�odo letivo.
	 */
	public void setFimPeriodoLetivo(Date fimSemestre) {
		this.fimPeriodoLetivo = fimSemestre;
	}

	/** Retorna o fim do per�odo permitido para trancamento de matr�culas de alunos em turmas 
	 * @return Fim do per�odo permitido para trancamento de matr�culas de alunos em turmas 
	 */
	public Date getFimTrancamentoTurma() {
		return fimTrancamentoTurma;
	}

	/** Seta o fim do per�odo permitido para trancamento de matr�culas de alunos em turmas 
	 * @param fimTrancamento fim do per�odo permitido para trancamento de matr�culas de alunos em turmas 
	 */
	public void setFimTrancamentoTurma(Date fimTrancamento) {
		this.fimTrancamentoTurma = fimTrancamento;
	}
	
	/** Retorna o fim do per�odo permitido para trancamento de programa para os alunos 
	 * @return Fim do per�odo permitido para trancamento de programas regulares. 
	 */
	public Date getFimTrancamentoPrograma() {
		return fimTrancamentoPrograma;
	}

	/** Seta o fim do per�odo permitido para trancamento de programa para os alunos 
	 * @param fimTrancamentoPrograma fim do per�odo permitido para trancamento de programas regulares. 
	 */
	public void setFimTrancamentoPrograma(Date fimTrancamentoPrograma) {
		this.fimTrancamentoPrograma = fimTrancamentoPrograma;
	}
	
	/** Retorna o fim do per�odo permitido para trancamento de programa a posteriori para os alunos. 
	 * @return Fim do per�odo permitido para trancamento de programa a posteriori. 
	 */
	public Date getFimTrancamentoProgramaPosteriori() {
		return fimTrancamentoProgramaPosteriori;
	}

	/** Seta o fim do per�odo permitido para trancamento de programa  a posteriori para os alunos. 
	 * @param fimTrancamentoPrograma fim do per�odo permitido para trancamento de programaa a posteriori. 
	 */
	public void setFimTrancamentoProgramaPosteriori(
			Date fimTrancamentoProgramaPosteriori) {
		this.fimTrancamentoProgramaPosteriori = fimTrancamentoProgramaPosteriori;
	}

	/** Retorna o in�cio do per�odo para alunos solicitarem suas matr�culas. 
	 * @return in�cio do per�odo para alunos solicitarem suas matr�culas. 
	 */
	public Date getInicioMatriculaOnline() {
		return inicioMatriculaOnline;
	}

	/** Seta o in�cio do per�odo para alunos solicitarem suas matr�culas.
	 * @param inicioMatricula in�cio do per�odo para alunos solicitarem suas matr�culas. 
	 */
	public void setInicioMatriculaOnline(Date inicioMatricula) {
		this.inicioMatriculaOnline = inicioMatricula;
	}

	/** Retorna a data de in�cio do per�odo letivo. 
	 * @return data de in�cio do per�odo letivo.
	 */
	public Date getInicioPeriodoLetivo() {
		return inicioPeriodoLetivo;
	}

	/** Seta a data de in�cio do per�odo letivo
	 * @param inicioSemestre data de in�cio do per�odo letivo
	 */
	public void setInicioPeriodoLetivo(Date inicioSemestre) {
		this.inicioPeriodoLetivo = inicioSemestre;
	}

	/** Retorna o in�cio do per�odo permitido para trancamento de matr�culas de alunos em turmas. 
	 * @return in�cio do per�odo permitido para trancamento de matr�culas de alunos em turmas.
	 */
	public Date getInicioTrancamentoTurma() {
		return inicioTrancamentoTurma;
	}

	/** Seta o in�cio do per�odo permitido para trancamento de matr�culas de alunos em turmas.
	 * @param inicioTrancamento in�cio do per�odo permitido para trancamento de matr�culas de alunos em turmas.
	 */
	public void setInicioTrancamentoTurma(Date inicioTrancamento) {
		this.inicioTrancamentoTurma = inicioTrancamento;
	}
	
	/**Retorna o in�cio do per�odo permitido para trancamento de programa para alunos.
	 * @return  in�cio do per�odo permitido para trancamento de programas regulares.
	 */
	public Date getInicioTrancamentoPrograma() {
		return inicioTrancamentoPrograma;
	}

	/**
	 * Seta o in�cio do per�odo permitido para trancamento de programa para alunos.
	 * @param inicioTrancamentoPrograma in�cio do per�odo permitido para trancamento regular de programa.
	 */
	public void setInicioTrancamentoPrograma(Date inicioTrancamentoPrograma) {
		this.inicioTrancamentoPrograma = inicioTrancamentoPrograma;
	}
	
	/**Retorna o in�cio do per�odo permitido para trancamento de programa a posteriori para alunos.
	 * @return  in�cio do per�odo permitido para trancamento de programa a posteriori.
	 */
	public Date getInicioTrancamentoProgramaPosteriori() {
		return inicioTrancamentoProgramaPosteriori;
	}

	/**
	 * Seta o in�cio do per�odo permitido para trancamento de programa a posteriori para alunos.
	 * @return inicioTrancamentoProgramaPosteriori in�cio do per�odo permitido para trancamento a posteriori de programa.
	 */
	public void setInicioTrancamentoProgramaPosteriori(
			Date inicioTrancamentoProgramaPosteriori) {
		this.inicioTrancamentoProgramaPosteriori = inicioTrancamentoProgramaPosteriori;
	}

	/** Retorna do Per�odo do calend�rio. 
	 * @return Per�odo do calend�rio. 
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o Per�odo do calend�rio.
	 * @param semestre Per�odo do calend�rio.
	 */
	public void setPeriodo(int semestre) {
		this.periodo = semestre;
	}

	/** Retorna uma string representando o ano-per�odo do calend�rio.
	 * @return String no formato "ano.per�odo"
	 */
	@Transient
	public String getAnoPeriodo() {
		return getAno() + "." + getPeriodo();
	}
	
	@Transient
	public String getAnoPeriodoFeriasVigente() {
		return getAnoFeriasVigente() + "." + getPeriodoFeriasVigente();
	}

	@Transient
	public String getDescricaoCalendario(){
		return !isMedio() ? "Semestre atual: <strong>" + getAnoPeriodo() + "</strong>": "Ano Atual: <strong>" + getAno() + "</strong>";
	}
	
	/** Retorna um conjunto de eventos extras. 
	 * @return Conjunto de eventos extras. 
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "calendario")
	public Set<EventoExtraSistema> getEventosExtra() {
		return eventosExtra;
	}

	/** Seta um conjunto de eventos extras. 
	 * @param eventosExtra Conjunto de eventos extras. 
	 */
	public void setEventosExtra(Set<EventoExtraSistema> eventosExtra) {
		this.eventosExtra = eventosExtra;
	}

	/** Retorna o conv�nio acad�mico do calend�rio 
	 * @return Conv�nio acad�mico do calend�rio 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_convenio")
	public ConvenioAcademico getConvenio() {
		return convenio;
	}

	/** Seta o conv�nio acad�mico do calend�rio 
	 * @param convenio Conv�nio acad�mico do calend�rio 
	 */
	public void setConvenio(ConvenioAcademico convenio) {
		this.convenio = convenio;
	}

	/** Retorna o curso do calend�rio 
	 * @return Curso do calend�rio 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso do calend�rio 
	 * @param curso Curso do calend�rio 
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna a modalidade de educa��o do calend�rio 
	 * @return Modalidade de educa��o do calend�rio 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_modalidade")
	public ModalidadeEducacao getModalidade() {
		return modalidade;
	}

	/** Seta a modalidade de educa��o do calend�rio 
	 * @param modalidade Modalidade de educa��o do calend�rio 
	 */
	public void setModalidade(ModalidadeEducacao modalidade) {
		this.modalidade = modalidade;
	}

	/** Retorna o fim do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula 
	 * @return fim do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula
	 */
	public Date getFimAjustesMatricula() {
		return fimAjustesMatricula;
	}

	/** Seta o fim do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula
	 * @param fimAjustesMatricula fim do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula
	 */
	public void setFimAjustesMatricula(Date fimAjustesMatricula) {
		this.fimAjustesMatricula = fimAjustesMatricula;
	}

	/** Retorna o fim do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula 
	 * @return fim do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula 
	 */
	public Date getFimAjustesReMatricula() {
		return fimAjustesReMatricula;
	}

	/** Seta o fim do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula 
	 * @param fimAjustesReMatricula fim do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula 
	 */
	public void setFimAjustesReMatricula(Date fimAjustesReMatricula) {
		this.fimAjustesReMatricula = fimAjustesReMatricula;
	}

	/** Retorna o fim do per�odo para os docentes consolidarem suas turmas 
	 * @return fim do per�odo para os docentes consolidarem suas turmas 
	 */
	public Date getFimConsolidacaoTurma() {
		return fimConsolidacaoTurma;
	}

	/** Seta o fim do per�odo para os docentes consolidarem suas turmas 
	 * @param fimConsolidacaoTurma fim do per�odo para os docentes consolidarem suas turmas 
	 */
	public void setFimConsolidacaoTurma(Date fimConsolidacaoTurma) {
		this.fimConsolidacaoTurma = fimConsolidacaoTurma;
	}

	/** Retorna o fim do per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula 
	 * @return fim do per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula 
	 */
	public Date getFimCoordenacaoAnaliseMatricula() {
		return fimCoordenacaoAnaliseMatricula;
	}

	/** Seta o fim do per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula 
	 * @param fimCoordenacaoAnaliseMatricula fim do per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula 
	 */
	public void setFimCoordenacaoAnaliseMatricula(Date fimCoordenacaoAnaliseMatricula) {
		this.fimCoordenacaoAnaliseMatricula = fimCoordenacaoAnaliseMatricula;
	}

	/** Retorna o fim do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 * @return fim do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 */
	public Date getFimDiscenteAnaliseMatricula() {
		return fimDiscenteAnaliseMatricula;
	}

	/** Seta o fim do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 * @param fimDiscenteAnaliseMatricula fim do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 */
	public void setFimDiscenteAnaliseMatricula(Date fimDiscenteAnaliseMatricula) {
		this.fimDiscenteAnaliseMatricula = fimDiscenteAnaliseMatricula;
	}

	/** Retorna o fim do per�odo DAE matricular alunos especiais 
	 * @return fim do per�odo DAE matricular alunos especiais 
	 */
	public Date getFimMatriculaAlunoEspecial() {
		return fimMatriculaAlunoEspecial;
	}

	/** Seta o fim do per�odo DAE matricular alunos especiais 
	 * @param fimMatriculaAlunoEspecial fim do per�odo DAE matricular alunos especiais 
	 */
	public void setFimMatriculaAlunoEspecial(Date fimMatriculaAlunoEspecial) {
		this.fimMatriculaAlunoEspecial = fimMatriculaAlunoEspecial;
	}

	/** Retorna o fim do per�odo do processamento de matr�cula 
	 * @return fim do per�odo do processamento de matr�cula 
	 */
	public Date getFimProcessamentoMatricula() {
		return fimProcessamentoMatricula;
	}

	/** Seta o fim do per�odo do processamento de matr�cula 
	 * @param fimProcessamentoMatricula fim do per�odo do processamento de matr�cula 
	 */
	public void setFimProcessamentoMatricula(Date fimProcessamentoMatricula) {
		this.fimProcessamentoMatricula = fimProcessamentoMatricula;
	}

	/** Retorna o fim do per�odo de processamento da rematr�cula 
	 * @return fim do per�odo de processamento da rematr�cula 
	 */
	public Date getFimProcessamentoReMatricula() {
		return fimProcessamentoReMatricula;
	}

	/** Seta o fim do per�odo de processamento da rematr�cula 
	 * @param fimProcessamentoReMatricula fim do per�odo de processamento da rematr�cula 
	 */
	public void setFimProcessamentoReMatricula(Date fimProcessamentoReMatricula) {
		this.fimProcessamentoReMatricula = fimProcessamentoReMatricula;
	}

	/** Retorna o fim do per�odo para alunos solicitarem a re-matricula nas disciplinas 
	 * @return fim do per�odo para alunos solicitarem a re-matricula nas disciplinas 
	 */
	public Date getFimReMatricula() {
		return fimReMatricula;
	}

	/** Seta o fim do per�odo para alunos solicitarem a re-matricula nas disciplinas 
	 * @param fimReMatricula fim do per�odo para alunos solicitarem a re-matricula nas disciplinas 
	 */
	public void setFimReMatricula(Date fimReMatricula) {
		this.fimReMatricula = fimReMatricula;
	}

	/** Retorna o fim do per�odo de requerimento de ensino individualizado pelos alunos 
	 * @return fim do per�odo de requerimento de ensino individualizado pelos alunos 
	 */
	public Date getFimRequerimentoEnsinoIndiv() {
		return fimRequerimentoEnsinoIndiv;
	}

	/** Seta o fim do per�odo de requerimento de ensino individualizado pelos alunos 
	 * @param fimRequerimentoEnsinoIndiv fim do per�odo de requerimento de ensino individualizado pelos alunos 
	 */
	public void setFimRequerimentoEnsinoIndiv(Date fimRequerimentoEnsinoIndiv) {
		this.fimRequerimentoEnsinoIndiv = fimRequerimentoEnsinoIndiv;
	}

	/** Retorna o fim do per�odo para solicita��o de cadastro de turma aos chefes de departamento 
	 * @return fim do per�odo para solicita��o de cadastro de turma aos chefes de departamento 
	 */
	public Date getFimSolicitacaoTurma() {
		return fimSolicitacaoTurma;
	}

	/** Seta o fim do per�odo para solicita��o de cadastro de turma aos chefes de departamento 
	 * @param fimSolicitacaoTurma fim do per�odo para solicita��o de cadastro de turma aos chefes de departamento 
	 */
	public void setFimSolicitacaoTurma(Date fimSolicitacaoTurma) {
		this.fimSolicitacaoTurma = fimSolicitacaoTurma;
	}

	/** Retorna o in�cio do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula 
	 * @return in�cio do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula 
	 */
	public Date getInicioAjustesMatricula() {
		return inicioAjustesMatricula;
	}

	/** Seta o in�cio do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula 
	 * @param inicioAjustesMatricula in�cio do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de matr�cula 
	 */
	public void setInicioAjustesMatricula(Date inicioAjustesMatricula) {
		this.inicioAjustesMatricula = inicioAjustesMatricula;
	}

	/** Retorna o in�cio do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula 
	 * @return in�cio do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula 
	 */
	public Date getInicioAjustesReMatricula() {
		return inicioAjustesReMatricula;
	}

	/** Seta o in�cio do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula 
	 * @param inicioAjustesReMatricula in�cio do per�odo para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matr�cula 
	 */
	public void setInicioAjustesReMatricula(Date inicioAjustesReMatricula) {
		this.inicioAjustesReMatricula = inicioAjustesReMatricula;
	}


	/** Retorna o in�cio do per�odo para os docentes consolidarem suas turmas 
	 * @return in�cio do per�odo para os docentes consolidarem suas turmas 
	 */
	public Date getInicioConsolidacaoTurma() {
		return inicioConsolidacaoTurma;
	}

	/** Seta o in�cio do per�odo para os docentes consolidarem suas turmas 
	 * @param inicioConsolidacaoTurma in�cio do per�odo para os docentes consolidarem suas turmas 
	 */
	public void setInicioConsolidacaoTurma(Date inicioConsolidacaoTurma) {
		this.inicioConsolidacaoTurma = inicioConsolidacaoTurma;
	}

	/** Retorna o in�cio do per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula 
	 * @return in�cio do per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula 
	 */
	public Date getInicioCoordenacaoAnaliseMatricula() {
		return inicioCoordenacaoAnaliseMatricula;
	}

	/** Seta o in�cio do per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula  
	 * @param inicioCoordenacaoAnaliseMatricula in�cio do per�odo para os coordenadores de curso analisarem as solicita��es de matr�cula
	 */
	public void setInicioCoordenacaoAnaliseMatricula(Date inicioCoordenacaoAnaliseMatricula) {
		this.inicioCoordenacaoAnaliseMatricula = inicioCoordenacaoAnaliseMatricula;
	}

	/** Retorna o in�cio do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 * @return in�cio do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 */
	public Date getInicioDiscenteAnaliseMatricula() {
		return inicioDiscenteAnaliseMatricula;
	}

	/** Seta o in�cio do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 * @param inicioDiscenteAnaliseMatricula in�cio do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 */
	public void setInicioDiscenteAnaliseMatricula(Date inicioDiscenteAnaliseMatricula) {
		this.inicioDiscenteAnaliseMatricula = inicioDiscenteAnaliseMatricula;
	}

	/** Retorna o in�cio do per�odo DAE matricular alunos especiais 
	 * @return in�cio do per�odo DAE matricular alunos especiais 
	 */
	public Date getInicioMatriculaAlunoEspecial() {
		return inicioMatriculaAlunoEspecial;
	}

	/** Seta o in�cio do per�odo DAE matricular alunos especiais 
	 * @param inicioMatriculaAlunoEspecial in�cio do per�odo DAE matricular alunos especiais 
	 */
	public void setInicioMatriculaAlunoEspecial(Date inicioMatriculaAlunoEspecial) {
		this.inicioMatriculaAlunoEspecial = inicioMatriculaAlunoEspecial;
	}

	/** Retorna o in�cio do per�odo do processamento de matr�cula 
	 * @return in�cio do per�odo do processamento de matr�cula 
	 */
	public Date getInicioProcessamentoMatricula() {
		return inicioProcessamentoMatricula;
	}

	/** Seta o in�cio do per�odo do processamento de matr�cula 
	 * @param inicioProcessamentoMatricula in�cio do per�odo do processamento de matr�cula 
	 */
	public void setInicioProcessamentoMatricula(Date inicioProcessamentoMatricula) {
		this.inicioProcessamentoMatricula = inicioProcessamentoMatricula;
	}

	/** Retorna o in�cio do per�odo de processamento da rematr�cula 
	 * @return in�cio do per�odo de processamento da rematr�cula 
	 */
	public Date getInicioProcessamentoReMatricula() {
		return inicioProcessamentoReMatricula;
	}

	/** Seta o in�cio do per�odo de processamento da rematr�cula  
	 * @param inicioProcessamentoReMatricula in�cio do per�odo de processamento da rematr�cula 
	 */
	public void setInicioProcessamentoReMatricula(Date inicioProcessamentoReMatricula) {
		this.inicioProcessamentoReMatricula = inicioProcessamentoReMatricula;
	}

	/** Retorna o in�cio do per�odo para alunos solicitarem a re-matricula nas disciplinas 
	 * @return in�cio do per�odo para alunos solicitarem a re-matricula nas disciplinas 
	 */
	public Date getInicioReMatricula() {
		return inicioReMatricula;
	}

	/** Seta o in�cio do per�odo para alunos solicitarem a re-matricula nas disciplinas 
	 * @param inicioReMatricula in�cio do per�odo para alunos solicitarem a re-matricula nas disciplinas 
	 */
	public void setInicioReMatricula(Date inicioReMatricula) {
		this.inicioReMatricula = inicioReMatricula;
	}

	/** Retorna o in�cio do per�odo de requerimento de ensino individualizado pelos alunos 
	 * @return in�cio do per�odo de requerimento de ensino individualizado pelos alunos 
	 */
	public Date getInicioRequerimentoEnsinoIndiv() {
		return inicioRequerimentoEnsinoIndiv;
	}

	/** Seta o in�cio do per�odo de requerimento de ensino individualizado pelos alunos 
	 * @param inicioRequerimentoEnsinoIndiv in�cio do per�odo de requerimento de ensino individualizado pelos alunos 
	 */
	public void setInicioRequerimentoEnsinoIndiv(Date inicioRequerimentoEnsinoIndiv) {
		this.inicioRequerimentoEnsinoIndiv = inicioRequerimentoEnsinoIndiv;
	}

	/** Retorna o in�cio do per�odo para solicita��o de cadastro de turma aos chefes de departamento 
	 * @return in�cio do per�odo para solicita��o de cadastro de turma aos chefes de departamento 
	 */
	public Date getInicioSolicitacaoTurma() {
		return inicioSolicitacaoTurma;
	}

	/** Seta o in�cio do per�odo para solicita��o de cadastro de turma aos chefes de departamento 
	 * @param inicioSolicitacaoTurma in�cio do per�odo para solicita��o de cadastro de turma aos chefes de departamento 
	 */
	public void setInicioSolicitacaoTurma(Date inicioSolicitacaoTurma) {
		this.inicioSolicitacaoTurma = inicioSolicitacaoTurma;
	}

	/** Retorna o fim do per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula 
	 * @return in�cio do per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula 
	 */
	public Date getFimCoordenacaoAnaliseReMatricula() {
		return fimCoordenacaoAnaliseReMatricula;
	}

	/** Seta o fim do per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula 
	 * @param fimCoordenacaoAnaliseReMatricula in�cio do per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula 
	 */
	public void setFimCoordenacaoAnaliseReMatricula(Date fimCoordenacaoAnaliseReMatricula) {
		this.fimCoordenacaoAnaliseReMatricula = fimCoordenacaoAnaliseReMatricula;
	}

	/** Retorna o fim do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 * @return fim do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 */
	public Date getFimDiscenteAnaliseReMatricula() {
		return fimDiscenteAnaliseReMatricula;
	}

	/** Seta o fim do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 * @param fimDiscenteAnaliseReMatricula fim do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 */
	public void setFimDiscenteAnaliseReMatricula(Date fimDiscenteAnaliseReMatricula) {
		this.fimDiscenteAnaliseReMatricula = fimDiscenteAnaliseReMatricula;
	}

	/** Retorna o per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula 
	 * @return Per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula 
	 */
	public Date getInicioCoordenacaoAnaliseReMatricula() {
		return inicioCoordenacaoAnaliseReMatricula;
	}

	/** Seta o per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula 
	 * @param inicioCoordenacaoAnaliseReMatricula Per�odo para os coordenadores de curso analisarem as solicita��es de rematr�cula 
	 */
	public void setInicioCoordenacaoAnaliseReMatricula(Date inicioCoordenacaoAnaliseReMatricula) {
		this.inicioCoordenacaoAnaliseReMatricula = inicioCoordenacaoAnaliseReMatricula;
	}

	/** Retorna o in�cio do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 * @return in�cio do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 */
	public Date getInicioDiscenteAnaliseReMatricula() {
		return inicioDiscenteAnaliseReMatricula;
	}

	/** Seta o in�cio do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 * @param inicioDiscenteAnaliseReMatricula in�cio do per�odo para os alunos analisarem as an�lises dos coordenadores 
	 */
	public void setInicioDiscenteAnaliseReMatricula(Date inicioDiscenteAnaliseReMatricula) {
		this.inicioDiscenteAnaliseReMatricula = inicioDiscenteAnaliseReMatricula;
	}

	/** Retorna o in�cio do per�odo de f�rias 
	 * @return in�cio do per�odo de f�rias 
	 */
	public Date getInicioFerias() {
		return inicioFerias;
	}

	/** Seta o in�cio do per�odo de f�rias 
	 * @param inicioFerias in�cio do per�odo de f�rias 
	 */
	public void setInicioFerias(Date inicioFerias) {
		this.inicioFerias = inicioFerias;
	}

	/** Retorna o fim do per�odo de f�rias 
	 * @return fim do per�odo de f�rias 
	 */
	public Date getFimFerias() {
		return fimFerias;
	}

 	/** Seta o fim do per�odo de f�rias 
	 * @param fimFerias fim do per�odo de f�rias 
	 */
	public void setFimFerias(Date fimFerias) {
		this.fimFerias = fimFerias;
	}

	/** Indica se a data atual est� dentro do per�odo informado.
	 * @param inicio
	 * @param fim
	 * @return true, caso a data atual esteja dentro do per�odo informado. 
	 */
	private boolean isOperacaoDentroPeriodo(Date inicio, Date fim) {
		return CalendarUtils.isDentroPeriodo(inicio, fim);
	}

	/** Retorna o in�cio do per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento 
	 * @return in�cio do per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento 
	 */
	public Date getInicioSolicitacaoTurmaFerias() {
		return inicioSolicitacaoTurmaFerias;
	}

	/** Seta o in�cio do per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento 
	 * @param inicioSolicitacaoTurmaFerias in�cio do per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento 
	 */
	public void setInicioSolicitacaoTurmaFerias(Date inicioSolicitacaoTurmaFerias) {
		this.inicioSolicitacaoTurmaFerias = inicioSolicitacaoTurmaFerias;
	}

	/** Retorna o fim do per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento 
	 * @return fim do per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento 
	 */
	public Date getFimSolicitacaoTurmaFerias() {
		return fimSolicitacaoTurmaFerias;
	}

	/** Seta o fim do per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento 
	 * @param fimSolicitacaoTurmaFerias fim do per�odo para solicita��o de cadastro de turma de FERIAS aos chefes de departamento 
	 */
	public void setFimSolicitacaoTurmaFerias(Date fimSolicitacaoTurmaFerias) {
		this.fimSolicitacaoTurmaFerias = fimSolicitacaoTurmaFerias;
	}

	/** Verifica se hoje est� no per�odo de matricula de aluno em turma de ferias
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaTurmaFerias()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaTurmaFerias(), getFimMatriculaTurmaFerias());
	}
	
	/** Verifica se a data atual est� dentro do per�odo de matr�cula extraordin�ria em turmas de f�rias.
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaExtraordinariaFerias()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaExtraordinariaFerias(), getFimMatriculaExtraordinariaFerias());
	}

	/** Verifica se hoje est� no per�odo de cadastramento de turmas realizado pelo chefe de departamento
	 * @return
	 */
	@Transient
	public boolean isPeriodoCadastroTurmas()  {
		return isOperacaoDentroPeriodo(getInicioCadastroTurma(), getFimCadastroTurma());
	}

	/** Verifica se hoje est� no per�odo de cadastramento de turmas de ferias realizado pelo chefe de departamento
	 * @return
	 */
	@Transient
	public boolean isPeriodoCadastroTurmasFerias()  {
		return isOperacaoDentroPeriodo(getInicioCadastroTurmaFerias(), getFimCadastroTurmaFerias());
	}

	/** Verifica se hoje est� no per�odo de matr�culas de alunos regulares (seja matr�cula normal ou re-matr�cula) 
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaRegular()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaOnline(), getFimMatriculaOnline()) ||
				isOperacaoDentroPeriodo(getInicioReMatricula(), getFimReMatricula());
	}

	/**
	 * Verifica se hoje est� no per�odo de cadastro/altera��o de de Plano de Matr�culas
	 * 
	 * @return
	 */
	@Transient
	public boolean isPeriodoPlanoMatriculas()  {
		return isOperacaoDentroPeriodo(getInicioCadastroPlanoMatricula(), getFimCadastroPlanoMatricula());
	}
	
	/**
	 * Verifica se hoje est� no per�odo de cadastramento de discentes
	 * 
	 * @return
	 */
	@Transient
	public boolean isPeriodoCadastramentoDiscente()  {
		return isOperacaoDentroPeriodo(getInicioCadastramentoDiscente(), getFimCadastramentoDiscente());
	}
	
	/** Verifica se a data atual est� dentro do per�odo de rematr�cula.
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaExtraordinaria()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaExtraordinaria(), getFimMatriculaExtraordinaria());
	}
	
	/** Verifica se a data atual est� dentro do per�odo de rematr�cula.
	 * @return
	 */
	@Transient
	public boolean isPeriodoReMatricula()  {
		return isOperacaoDentroPeriodo(getInicioReMatricula(), getFimReMatricula());
	}

	/** Indica se a data atual est� dentro do per�odo de aluno cadastrado.
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaAlunoCadastrado()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaAlunoCadastrado(), getFimMatriculaAlunoCadastrado()) ||
				isOperacaoDentroPeriodo(getInicioReMatricula(), getFimReMatricula());
	}


	/** Verifica se hoje est� no per�odo de an�lise dos coordenadores das solicita��es de matr�culas 
	 * @return
	 */
	@Transient
	public boolean isPeriodoAnaliseCoordenador()  {
		return (isOperacaoDentroPeriodo(getInicioCoordenacaoAnaliseMatricula(), getFimCoordenacaoAnaliseMatricula()) ||
				isOperacaoDentroPeriodo(getInicioCoordenacaoAnaliseReMatricula(), getFimCoordenacaoAnaliseReMatricula()));
	}

	/** Verifica se hoje est� no per�odo de revis�o dos discentes das an�lises dos coordenadores
	 * @return
	 */
	@Transient
	public boolean isPeriodoAnaliseDiscente()  {
		return (isOperacaoDentroPeriodo(getInicioDiscenteAnaliseMatricula(), getFimDiscenteAnaliseMatricula()) ||
				isOperacaoDentroPeriodo(getInicioDiscenteAnaliseReMatricula(), getFimDiscenteAnaliseReMatricula()));
	}


	/** Verifica se hoje est� no per�odo de processamento de matr�cula (seja na matr�cula normal ou re-matr�cula) 
	 * @return
	 */
	@Transient
	public boolean isPeriodoProcessamento() {
		return (isOperacaoDentroPeriodo(getInicioProcessamentoMatricula(), getFimProcessamentoMatricula()) ||
				isOperacaoDentroPeriodo(getInicioProcessamentoReMatricula(), getFimProcessamentoReMatricula()));
	}

	/** Verifica se hoje est� no per�odo de ajustes de turmas depois do processamento de matr�cula 
	 * @return
	 */
	@Transient
	public boolean isPeriodoAjustesTurmas() {
		return (isOperacaoDentroPeriodo(getInicioAjustesMatricula(), getFimAjustesMatricula()) ||
				isOperacaoDentroPeriodo(getInicioAjustesReMatricula(), getFimAjustesReMatricula()));
	}

	/** Indica se a data atual est� entre o per�odo de trancamento de turmas.
	 * @return
	 */
	@Transient
	public boolean isPeriodoTrancamentoTurmas() {
		return (isOperacaoDentroPeriodo(getInicioTrancamentoTurma(), getFimTrancamentoTurma()) );
	}
	
	/** Indica se a data atual est� entre o per�odo de trancamento de programa.
	 * @return
	 */
	@Transient
	public boolean isPeriodoTrancamentoPrograma() {
		return (isOperacaoDentroPeriodo(getInicioTrancamentoPrograma(), getFimTrancamentoPrograma()) );
	}
	
	/** Indica se a data atual est� entre o per�odo de trancamento de programa.
	 * @return
	 */
	@Transient
	public boolean isPeriodoTrancamentoProgramaPosteriori() {
		return (isOperacaoDentroPeriodo(getInicioTrancamentoProgramaPosteriori(), getFimTrancamentoProgramaPosteriori()) );
	}
	

	/** Indica se a data atual est� entre o per�odo de matr�cula de aluno especial.
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaAlunoEspecial() {
		return (isOperacaoDentroPeriodo(getInicioMatriculaAlunoEspecial(), getFimMatriculaAlunoEspecial()) );
	}

	/** Indica se a data atual est� entre o per�odo de solicita��o de turma.
	 * @return
	 */
	@Transient
	public boolean isPeriodoSolicitacaoTurma() {
		return (isOperacaoDentroPeriodo(getInicioSolicitacaoTurma(), getFimSolicitacaoTurma()) );
	}

	/** Indica se a data atual est� entre o per�odo de requerimento de turma de f�rias.
	 * @return
	 */
	@Transient
	public boolean isPeriodoRequerimentoTurmaFerias() {
		return (isOperacaoDentroPeriodo(getInicioRequerimentoTurmaFerias(), getFimRequerimentoTurmaFerias()) );
	}

	/**
	 * Verifica se esta no per�odo de solicita��o de turma de f�rias
	 * @return
	 */
	@Transient
	public boolean isPeriodoSolicitacaoTurmaFerias() {
		return (isOperacaoDentroPeriodo(getInicioSolicitacaoTurmaFerias(), getFimSolicitacaoTurmaFerias()) );
	}


	/** Indica se a data atual est� entre o per�odo de f�rias.
	 * @return
	 */
	@Transient
	public boolean isPeriodoFerias() {
		if( getInicioFerias() == null || getFimFerias() == null )
			throw new ConfiguracaoAmbienteException("Erro de configura��o no ambiente: o in�cio e o fim do per�odo de f�rias n�o est�o definidos no calend�rio acad�mico vigente ( " + getAno() + "." + getPeriodo() + " )");
		return (isOperacaoDentroPeriodo(getInicioFerias(), getFimFerias()) );
	}

	/** Indica se a data atual est� entre o per�odo de requerimento de ensino individualizado pelos alunos.
	 * @return
	 */
	@Transient
	public boolean isPeriodoRequerimentoEnsinoIndiv() {
		return (isOperacaoDentroPeriodo(getInicioRequerimentoEnsinoIndiv(), getFimRequerimentoEnsinoIndiv()) );
	}

	/** Indica se a data atual est� entre o per�odo de solicita��o de ensino individualizado pelos alunos
	 * @return
	 */
	@Transient
	public boolean isPeriodoSolicitacaoTurmaEnsinoIndiv() {
		return (isOperacaoDentroPeriodo(getInicioSolicitacaoTurmaEnsinoIndiv(), getFimSolicitacaoTurmaEnsinoIndiv()) );
	}

	/** Indica se a data atual est� entre o per�odo de cadastro de ensino individualizado pelos alunos
	 * @return
	 */
	@Transient
	public boolean isPeriodoCadastroTurmaEnsinoIndiv() {
		return (isOperacaoDentroPeriodo(getInicioCadastroTurmaEnsinoIndiv(), getFimCadastroTurmaEnsinoIndiv()) );
	}

	/**
	 * Retorna se est� no per�odo de an�lise de matr�cula por parte da coordena��o
	 */
	@Transient
	public boolean isPeriodoOrientacaoCoordenacao() {
		return (isOperacaoDentroPeriodo(getInicioCoordenacaoAnaliseMatricula(), getFimCoordenacaoAnaliseMatricula()) );
	}
	
	/**
	 * Retorna se est� no per�odo de valida��o dos v�nculos de alunos ingressantes
	 */
	@Transient
	public boolean isPeriodoValidacaoVinculoIngressante() {
		return (isOperacaoDentroPeriodo(getInicioValidacaoVinculoIngressante(), getFimValidacaoVinculoIngressante()) );
	}
	
	/**
	 * Retorna uma inst�ncia do CalendarioAcademico com o ano e o per�odo do pr�ximo per�odo regular setado!
	 * Ex:
	 * SE o ano per�odo atual for 2007.1 retorna 2007.2
	 * SE o ano per�odo atual for 2007.2 retorna 2008.1
	 * @return
	 */
	@Transient
	public CalendarioAcademico getProximoAnoPeriodoRegular(){
		
		List<PeriodoAcademico> regulares = PeriodoAcademicoHelper.getInstance().getPeriodosRegulares();
		PeriodoAcademico ultimoPeriodoRegular = regulares.get(regulares.size() - 1);
		PeriodoAcademico primeiroPeriodoRegular = regulares.get(0);
		
		CalendarioAcademico ca = new CalendarioAcademico();
		if( this.getPeriodo() == ultimoPeriodoRegular.getPeriodo() ){
			ca.setAno( this.getAno() + 1 );
			ca.setPeriodo(primeiroPeriodoRegular.getPeriodo());
		} else{
			ca.setAno(this.getAno());
			ca.setPeriodo(ultimoPeriodoRegular.getPeriodo());
		}
		return ca;
	}

	/** Retorna a descri��o textual do n�vel de ensino.
	 * @return
	 */
	@Transient
	public String getNivelDescr() {
		return NivelEnsino.getDescricao(nivel);
	}

	/** Indica se o calend�rio � o vigente.
	 * @return true, se o calend�rio � o vigente.
	 */
	public boolean isVigente() {
		return vigente;
	}

	/** Seta se o calend�rio � o vigente.
	 * @param vigente true, se o calend�rio � o vigente.
	 */
	public void setVigente(boolean vigente) {
		this.vigente = vigente;
	}

	/** Retorna uma descri��o textual do ano-per�odo vigente.
	 * @return
	 */
	@Transient
	public String getAnoPeriodoVigente() {
		StringBuilder s = new StringBuilder(getAnoPeriodo());
		if (isVigente())
			s.append(" - Vigente");
		return  s.toString();
	}
	
	/** Retorna uma descri��o textual do ano vigente (para ensino m�dio).
	 * @return
	 */
	@Transient
	public String getAnoVigente() {
		StringBuilder s = new StringBuilder(String.valueOf( getAno() ));
		if (isVigente())
			s.append(" - Vigente");
		return  s.toString();
	}	

	/** Retorna o in�cio do per�odo de cadastro de turmas realizado pelo chefe de departamento 
	 * @return in�cio do per�odo de cadastro de turmas realizado pelo chefe de departamento 
	 */
	public Date getInicioCadastroTurma() {
		return inicioCadastroTurma;
	}

	/** Seta o in�cio do per�odo de cadastro de turmas realizado pelo chefe de departamento 
	 * @param inicioCadastroTurma in�cio do per�odo de cadastro de turmas realizado pelo chefe de departamento 
	 */
	public void setInicioCadastroTurma(Date inicioCadastroTurma) {
		this.inicioCadastroTurma = inicioCadastroTurma;
	}

	/** Retorna o fim do per�odo de cadastro de turmas realizado pelo chefe de departamento 
	 * @return fim do per�odo de cadastro de turmas realizado pelo chefe de departamento 
	 */
	public Date getFimCadastroTurma() {
		return fimCadastroTurma;
	}

	/** Seta o fim do per�odo de cadastro de turmas realizado pelo chefe de departamento 
	 * @param fimCadastroTurma fim do per�odo de cadastro de turmas realizado pelo chefe de departamento 
	 */
	public void setFimCadastroTurma(Date fimCadastroTurma) {
		this.fimCadastroTurma = fimCadastroTurma;
	}

	/** Retorna o in�cio do per�odo de matricula de aluno em turma de f�rias 
	 * @return in�cio do per�odo de matricula de aluno em turma de f�rias 
	 */
	public Date getInicioMatriculaTurmaFerias() {
		return inicioMatriculaTurmaFerias;
	}

	/** Seta o in�cio do per�odo de matricula de aluno em turma de f�rias 
	 * @param inicioMatriculaTurmaFerias in�cio do per�odo de matricula de aluno em turma de f�rias 
	 */
	public void setInicioMatriculaTurmaFerias(Date inicioMatriculaTurmaFerias) {
		this.inicioMatriculaTurmaFerias = inicioMatriculaTurmaFerias;
	}

	/** Retorna o fim do per�odo de matricula de aluno em turma de f�rias 
	 * @return fim do per�odo de matricula de aluno em turma de f�rias 
	 */
	public Date getFimMatriculaTurmaFerias() {
		return fimMatriculaTurmaFerias;
	}

	/** Seta o fim do per�odo de matricula de aluno em turma de f�rias 
	 * @param fimMatriculaTurmaFerias fim do per�odo de matricula de aluno em turma de f�rias 
	 */
	public void setFimMatriculaTurmaFerias(Date fimMatriculaTurmaFerias) {
		this.fimMatriculaTurmaFerias = fimMatriculaTurmaFerias;
	}

	/** Retorna o in�cio do per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 * @return in�cio do per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 */
	public Date getInicioCadastroTurmaFerias() {
		return inicioCadastroTurmaFerias;
	}

	/** Seta o in�cio do per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 * @param inicioCadastroTurmaFerias in�cio do per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 */
	public void setInicioCadastroTurmaFerias(Date inicioCadastroTurmaFerias) {
		this.inicioCadastroTurmaFerias = inicioCadastroTurmaFerias;
	}

	/** Retorna o fim do per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 * @return fim do per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 */
	public Date getFimCadastroTurmaFerias() {
		return fimCadastroTurmaFerias;
	}

	/** Seta o fim do per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 * @param fimCadastroTurmaFerias fim do per�odo de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 */
	public void setFimCadastroTurmaFerias(Date fimCadastroTurmaFerias) {
		this.fimCadastroTurmaFerias = fimCadastroTurmaFerias;
	}


	/** Retorna o in�cio do per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) 
	 * @return in�cio do per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) 
	 */
	public Date getInicioMatriculaAlunoCadastrado() {
		return inicioMatriculaAlunoCadastrado;
	}

	/** Seta o in�cio do per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) 
	 * @param inicioMatriculaAlunoCadastrado in�cio do per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) 
	 */
	public void setInicioMatriculaAlunoCadastrado(Date inicioMatriculaAlunoCadastrado) {
		this.inicioMatriculaAlunoCadastrado = inicioMatriculaAlunoCadastrado;
	}

	/** Retorna o fim do per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) 
	 * @return fim do per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) 
	 */
	public Date getFimMatriculaAlunoCadastrado() {
		return fimMatriculaAlunoCadastrado;
	}

	/** Seta o fim do per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) 
	 * @param fimMatriculaAlunoCadastrado fim do per�odo para coordena��es de curso matricularem alunos cadastrados (calouros) 
	 */
	public void setFimMatriculaAlunoCadastrado(Date fimMatriculaAlunoCadastrado) {
		this.fimMatriculaAlunoCadastrado = fimMatriculaAlunoCadastrado;
	}

	/** Retorna o calend�rio acad�mico posterior a quantidade de semestre especificado.
	 * @param qtdPosterior Quantidade de semestres posterior ao calend�rio
	 * @return Calend�rio acad�mico posterior a quantidade de semestre especificado.
	 */
	public CalendarioAcademico incrCalendario(int qtdPosterior) {
		CalendarioAcademico cal = new CalendarioAcademico();
		int anoPeriodo = DiscenteHelper.somaSemestres(ano, periodo, qtdPosterior);
		cal.setAno( anoPeriodo / 10 );
		cal.setPeriodo( anoPeriodo - (cal.getAno() * 10) );
		return cal;
	}

	/** Retorna uma descri��o textual informado o per�odo da opera��o especificado.
	 * @param operacao Opera��o a informar o per�odo
	 * @param inicio in�cio do per�odo informado.
	 * @param fim fim do per�odo informado.
	 * @return Descri��o textual informado o per�odo da opera��o especificado.
	 */
	public static String getDescricaoPeriodo(String operacao, Date inicio, Date fim) {
		Formatador fmt = Formatador.getInstance();
		return "O per�odo oficial para " + operacao + " � de " + fmt.formatarData(inicio) + " � " + fmt.formatarData(fim);
	}

	/** Retorna o ano de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado 
	 * @return ano de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado 
	 */
	public Integer getAnoFeriasVigente() {
		return anoFeriasVigente;
	}

	/** Seta o ano de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado 
	 * @param anoFeriasVigente o ano de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado 
	 */
	public void setAnoFeriasVigente(Integer anoFeriasVigente) {
		this.anoFeriasVigente = anoFeriasVigente;
	}

	/** Retorna o per�odo de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado 
	 * @return per�odo de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado 
	 */
	public Integer getPeriodoFeriasVigente() {
		return periodoFeriasVigente;
	}

	/** Seta o per�odo de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado 
	 * @param periodoFeriasVigente per�odo de f�rias que deve ser considerado nas opera��es do sistema quando este calend�rio estiver selecionado 
	 */
	public void setPeriodoFeriasVigente(Integer periodoFeriasVigente) {
		this.periodoFeriasVigente = periodoFeriasVigente;
	}

	/** Retorna o in�cio do per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos 
	 * @return in�cio do per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos 
	 */
	public Date getInicioSolicitacaoTurmaEnsinoIndiv() {
		return inicioSolicitacaoTurmaEnsinoIndiv;
	}

	/** Seta o in�cio do per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos 
	 * @param inicioSolicitacaoTurmaEnsinoIndiv in�cio do per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos 
	 */
	public void setInicioSolicitacaoTurmaEnsinoIndiv(
			Date inicioSolicitacaoTurmaEnsinoIndiv) {
		this.inicioSolicitacaoTurmaEnsinoIndiv = inicioSolicitacaoTurmaEnsinoIndiv;
	}

	/** Retorna o fim do per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos 
	 * @return fim do per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos 
	 */
	public Date getFimSolicitacaoTurmaEnsinoIndiv() {
		return fimSolicitacaoTurmaEnsinoIndiv;
	}

	/** Seta o fim do per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos 
	 * @param fimSolicitacaoTurmaEnsinoIndiv fim do per�odo de solicitac�o de turmas de ensino individualizado, pelas coordena��es de cursos, aos departamentos 
	 */
	public void setFimSolicitacaoTurmaEnsinoIndiv(
			Date fimSolicitacaoTurmaEnsinoIndiv) {
		this.fimSolicitacaoTurmaEnsinoIndiv = fimSolicitacaoTurmaEnsinoIndiv;
	}

	/** Retorna o in�cio do per�odo de cadastro de turmas de ensino individualizado pelos departamentos 
	 * @return in�cio do per�odo de cadastro de turmas de ensino individualizado pelos departamentos 
	 */
	public Date getInicioCadastroTurmaEnsinoIndiv() {
		return inicioCadastroTurmaEnsinoIndiv;
	}

	/** Seta o in�cio do per�odo de cadastro de turmas de ensino individualizado pelos departamentos 
	 * @param inicioCadastroTurmaEnsinoIndiv in�cio do per�odo de cadastro de turmas de ensino individualizado pelos departamentos 
	 */
	public void setInicioCadastroTurmaEnsinoIndiv(
			Date inicioCadastroTurmaEnsinoIndiv) {
		this.inicioCadastroTurmaEnsinoIndiv = inicioCadastroTurmaEnsinoIndiv;
	}

	/** Retorna o fim do per�odo de cadastro de turmas de ensino individualizado pelos departamentos 
	 * @return fim do per�odo de cadastro de turmas de ensino individualizado pelos departamentos 
	 */
	public Date getFimCadastroTurmaEnsinoIndiv() {
		return fimCadastroTurmaEnsinoIndiv;
	}

	/** Seta o fim do per�odo de cadastro de turmas de ensino individualizado pelos departamentos 
	 * @param fimCadastroTurmaEnsinoIndiv fim do per�odo de cadastro de turmas de ensino individualizado pelos departamentos 
	 */
	public void setFimCadastroTurmaEnsinoIndiv(Date fimCadastroTurmaEnsinoIndiv) {
		this.fimCadastroTurmaEnsinoIndiv = fimCadastroTurmaEnsinoIndiv;
	}

	/** Retorna o ano que as solicita��es de turma ir�o ter 
	 * @return Ano que as solicita��es de turma ir�o ter 
	 */
	public Integer getAnoNovasTurmas() {
		return anoNovasTurmas;
	}

	/** Seta o ano que as solicita��es de turma ir�o ter 
	 * @param anoNovasTurmas Ano que as solicita��es de turma ir�o ter 
	 */
	public void setAnoNovasTurmas(Integer anoNovasTurmas) {
		this.anoNovasTurmas = anoNovasTurmas;
	}

	/** Retorna o per�odo que as solicita��es de novas turmas ir�o ter 
	 * @return Per�odo que as solicita��es de novas turmas ir�o ter 
	 */
	public Integer getPeriodoNovasTurmas() {
		return periodoNovasTurmas;
	}

	/** Seta o per�odo que as solicita��es de novas turmas ir�o ter 
	 * @param periodoNovasTurmas Per�odo que as solicita��es de novas turmas ir�o ter 
	 */
	public void setPeriodoNovasTurmas(Integer periodoNovasTurmas) {
		this.periodoNovasTurmas = periodoNovasTurmas;
	}

	/** Retorna o in�cio do per�odo de requerimento de F�RIAS pelos alunos 
	 * @return in�cio do per�odo de requerimento de F�RIAS pelos alunos 
	 */
	public Date getInicioRequerimentoTurmaFerias() {
		return inicioRequerimentoTurmaFerias;
	}

	/** Seta o in�cio do per�odo de requerimento de F�RIAS pelos alunos 
	 * @param inicioRequerimentoTurmaFerias in�cio do per�odo de requerimento de F�RIAS pelos alunos 
	 */
	public void setInicioRequerimentoTurmaFerias(Date inicioRequerimentoTurmaFerias) {
		this.inicioRequerimentoTurmaFerias = inicioRequerimentoTurmaFerias;
	}

	/** Retorna o fim do per�odo de requerimento de F�RIAS pelos alunos 
	 * @return fim do per�odo de requerimento de F�RIAS pelos alunos 
	 */
	public Date getFimRequerimentoTurmaFerias() {
		return fimRequerimentoTurmaFerias;
	}

	/** Seta o fim do per�odo de requerimento de F�RIAS pelos alunos 
	 * @param fimRequerimentoTurmaFerias fim do per�odo de requerimento de F�RIAS pelos alunos 
	 */
	public void setFimRequerimentoTurmaFerias(Date fimRequerimentoTurmaFerias) {
		this.fimRequerimentoTurmaFerias = fimRequerimentoTurmaFerias;
	}

	/** Indica se o n�vel de ensino do calend�rio � de gradua��o.
	 * @return
	 */
	@Transient
	public boolean isGraduacao(){
		return nivel == NivelEnsino.GRADUACAO;
	}

	/** Indica se o n�vel de ensino do calend�rio � de stricto sensu.
	 * @return
	 */
	@Transient
	public boolean isStricto(){
		return NivelEnsino.isAlgumNivelStricto(nivel);
	}

	/** Indica se o n�vel de ensino do calend�rio � de m�dio.
	 * @return
	 */
	@Transient
	public boolean isMedio(){
		return nivel == NivelEnsino.MEDIO;
	}
	
	/** Indica se a modalidade de ensino � a dist�ncia
	 * @return
	 */
	@Transient
	public boolean isADistancia(){
		return ( ( !isEmpty(modalidade) && getModalidade().isADistancia() )  || ( !isEmpty(curso) && getCurso().isADistancia() ) );
	}

	/** Retorna o ano anterior ao calend�rio.
	 * @return
	 */
	@Transient
	public int getAnoAnterior() {
		return (periodo == 1 ? ano - 1 : ano);
	}

	/** Retorna o per�odo anterior ao calend�rio.
	 * @return
	 */
	@Transient
	public int getPeriodoAnterior() {
		return  (periodo == 1 ? periodo + 1 : periodo - 1);
	}

	/** Retorna uma string representado o ano/per�odo anterior ao calend�rio.
	 * @return
	 */
	@Transient
	public String getAnoPeriodoAnterior() {
		return getAnoAnterior() + "." + getPeriodoAnterior();
	}

	/** Indica se a data atual est� dentro do per�odo de avalia��o institucional.
	 * @return
	 */
	@Transient
	public boolean isPeriodoAvaliacaoInstitucional(){
		return isOperacaoDentroPeriodo(getInicioMatriculaOnline(), getFimMatriculaOnline())
			|| isOperacaoDentroPeriodo(getInicioReMatricula(), getFimReMatricula())
			|| isOperacaoDentroPeriodo(getInicioMatriculaExtraordinaria(), getFimMatriculaExtraordinaria())
			|| isOperacaoDentroPeriodo(getInicioMatriculaOnline(), getFimMatriculaExtraordinaria());
	}

	public Date getInicioConsolidacaoParcialTurma() {
		return inicioConsolidacaoParcialTurma;
	}

	public void setInicioConsolidacaoParcialTurma(Date inicioConsolidacaoParcialTurma) {
		this.inicioConsolidacaoParcialTurma = inicioConsolidacaoParcialTurma;
	}

	public Date getFimConsolidacaoParcialTurma() {
		return fimConsolidacaoParcialTurma;
	}

	public void setFimConsolidacaoParcialTurma(Date fimConsolidacaoParcialTurma) {
		this.fimConsolidacaoParcialTurma = fimConsolidacaoParcialTurma;
	}

	public Date getInicioMatriculaExtraordinaria() {
		return inicioMatriculaExtraordinaria;
	}

	public void setInicioMatriculaExtraordinaria(Date inicioMatriculaExtraordinaria) {
		this.inicioMatriculaExtraordinaria = inicioMatriculaExtraordinaria;
	}

	public Date getFimMatriculaExtraordinaria() {
		return fimMatriculaExtraordinaria;
	}

	public void setFimMatriculaExtraordinaria(Date fimMatriculaExtraordinaria) {
		this.fimMatriculaExtraordinaria = fimMatriculaExtraordinaria;
	}

	public Date getInicioSugestaoTurmaChefe() {
		return inicioSugestaoTurmaChefe;
	}

	public void setInicioSugestaoTurmaChefe(Date inicioSugestaoTurmaChefe) {
		this.inicioSugestaoTurmaChefe = inicioSugestaoTurmaChefe;
	}

	public Date getFimSugestaoTurmaChefe() {
		return fimSugestaoTurmaChefe;
	}

	public void setFimSugestaoTurmaChefe(Date fimSugestaoTurmaChefe) {
		this.fimSugestaoTurmaChefe = fimSugestaoTurmaChefe;
	}

	@Transient
	public boolean isPeriodoSugestaoTurmaChefe() {
		return isOperacaoDentroPeriodo(getInicioSugestaoTurmaChefe(), getFimSugestaoTurmaChefe());
	}

	@Transient
	public boolean isPeriodoConsolidacao() {
		return isOperacaoDentroPeriodo(getInicioConsolidacaoTurma(), getFimConsolidacaoTurma());
	}
	
	public Date getInicioMatriculaExtraordinariaFerias() {
		return inicioMatriculaExtraordinariaFerias;
	}

	public void setInicioMatriculaExtraordinariaFerias(
			Date inicioMatriculaExtraordinariaFerias) {
		this.inicioMatriculaExtraordinariaFerias = inicioMatriculaExtraordinariaFerias;
	}

	public Date getFimMatriculaExtraordinariaFerias() {
		return fimMatriculaExtraordinariaFerias;
	}

	public void setFimMatriculaExtraordinariaFerias(
			Date fimMatriculaExtraordinariaFerias) {
		this.fimMatriculaExtraordinariaFerias = fimMatriculaExtraordinariaFerias;
	}

	public void setInicioValidacaoVinculoIngressante(
			Date inicioValidacaoVinculoIngressante) {
		this.inicioValidacaoVinculoIngressante = inicioValidacaoVinculoIngressante;
	}

	public Date getInicioValidacaoVinculoIngressante() {
		return inicioValidacaoVinculoIngressante;
	}

	public void setFimValidacaoVinculoIngressante(
			Date fimValidacaoVinculoIngressante) {
		this.fimValidacaoVinculoIngressante = fimValidacaoVinculoIngressante;
	}

	public Date getFimValidacaoVinculoIngressante() {
		return fimValidacaoVinculoIngressante;
	}

	public Date getInicioCadastroPlanoMatricula() {
		return inicioCadastroPlanoMatricula;
	}

	public void setInicioCadastroPlanoMatricula(Date inicioCadastroPlanoMatricula) {
		this.inicioCadastroPlanoMatricula = inicioCadastroPlanoMatricula;
	}

	public Date getFimCadastroPlanoMatricula() {
		return fimCadastroPlanoMatricula;
	}

	public void setFimCadastroPlanoMatricula(Date fimCadastroPlanoMatricula) {
		this.fimCadastroPlanoMatricula = fimCadastroPlanoMatricula;
	}

	public Date getInicioCadastramentoDiscente() {
		return inicioCadastramentoDiscente;
	}

	public void setInicioCadastramentoDiscente(Date inicioCadastramentoDiscente) {
		this.inicioCadastramentoDiscente = inicioCadastramentoDiscente;
	}

	public Date getFimCadastramentoDiscente() {
		return fimCadastramentoDiscente;
	}

	public void setFimCadastramentoDiscente(Date fimCadastramentoDiscente) {
		this.fimCadastramentoDiscente = fimCadastramentoDiscente;
	}

}

