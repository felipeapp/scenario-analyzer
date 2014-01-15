/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
 * Entidade que implementa o calendário acadêmico dos diversos níveis, cursos e unidades
 * @author Gleydson
 */
@Entity
@Table(schema="comum", name = "calendario_academico", uniqueConstraints = {})
public class CalendarioAcademico implements Validatable {

	// Fields
	/** Chave primária. */
	private int id;

	/** Ano do calendário */
	private int ano;

	/** Período do calendário */
	private int periodo;

	/** Unidade do calendário */
	private Unidade unidade = new Unidade();

	/** Nível de ensino do calendário */
	private char nivel;

	/** Modalidade de educação do calendário */
	private ModalidadeEducacao modalidade;

	/** Convênio acadêmico do calendário */
	private ConvenioAcademico convenio;

	/** Curso do calendário */
	private Curso curso;

	/** Indica se o calendário está ativo ou não */
	private boolean ativo;

	/** Indica se o calendário é vigente */
	private boolean vigente;

	/** Registro entrada de quem cadastrou */
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data de cadastro */
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada da última atualização */
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data da última atualização */
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Ano de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado */
	private Integer anoFeriasVigente;

	/** Período de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado */
	private Integer periodoFeriasVigente;

	/** Ano que as solicitações de turma irão ter */
	private Integer anoNovasTurmas;

	/** Período que as solicitações de novas turmas irão ter */
	private Integer periodoNovasTurmas;
	
	/** Data de início do período letivo */
	private Date inicioPeriodoLetivo, fimPeriodoLetivo;

	 // PERÍODOS RELACIONADOS COM MATRÍCULA 
	/** Período para alunos solicitarem suas matrículas */
	private Date inicioMatriculaOnline, fimMatriculaOnline;

	/** Período para coordenações de curso matricularem alunos cadastrados (calouros) */
	private Date inicioMatriculaAlunoCadastrado, fimMatriculaAlunoCadastrado;
	
	/** Período DAE matricular alunos especiais */
	private Date inicioMatriculaAlunoEspecial, fimMatriculaAlunoEspecial;

	/** Período para os coordenadores de curso analisarem as solicitações de matrícula */
	private Date inicioCoordenacaoAnaliseMatricula, fimCoordenacaoAnaliseMatricula;

	/** Período para os alunos analisarem as análises dos coordenadores */
	private Date inicioDiscenteAnaliseMatricula, fimDiscenteAnaliseMatricula;

	/** Período do processamento de matrícula */
	private Date inicioProcessamentoMatricula, fimProcessamentoMatricula;

	/** Período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula */
	private Date inicioAjustesMatricula, fimAjustesMatricula;

	/** Período para alunos solicitarem a re-matricula nas disciplinas */
	private Date inicioReMatricula, fimReMatricula;

	/** Período para os coordenadores de curso analisarem as solicitações de rematrícula */
	private Date inicioCoordenacaoAnaliseReMatricula, fimCoordenacaoAnaliseReMatricula;

	/** Período para os alunos analisarem as análises dos coordenadores */
	private Date inicioDiscenteAnaliseReMatricula, fimDiscenteAnaliseReMatricula;

	/** Período de processamento da rematrícula */
	private Date inicioProcessamentoReMatricula, fimProcessamentoReMatricula;

	/** Período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula */
	private Date inicioAjustesReMatricula, fimAjustesReMatricula;
	
	/** Período para os alunos realizarem matrículas extraordinárias em turmas com vagas ainda remanescentes */
	private Date inicioMatriculaExtraordinaria, fimMatriculaExtraordinaria;

	private Date inicioValidacaoVinculoIngressante, fimValidacaoVinculoIngressante;
	
	// PERÍODOS RELACIONADOS COM TURMA DE ENSINO INDIVIDUAL 
	/** Período de requerimento de ensino individualizado pelos alunos */
	private Date inicioRequerimentoEnsinoIndiv, fimRequerimentoEnsinoIndiv;

	/** Período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos */
	private Date inicioSolicitacaoTurmaEnsinoIndiv, fimSolicitacaoTurmaEnsinoIndiv;

	/** Período de cadastro de turmas de ensino individualizado pelos departamentos */
	private Date inicioCadastroTurmaEnsinoIndiv, fimCadastroTurmaEnsinoIndiv;

	
	// PERÍODOS RELACIONADOS COM TURMA DE FÉRIAS 
	/** Período de férias */
	private Date inicioFerias, fimFerias;

	/** Período de requerimento de FÉRIAS pelos alunos */
	private Date inicioRequerimentoTurmaFerias, fimRequerimentoTurmaFerias;

	/** Período para solicitação de cadastro de turma de FERIAS aos chefes de departamento */
	private Date inicioSolicitacaoTurmaFerias, fimSolicitacaoTurmaFerias;

	/** Período de cadastro de turmas de ferias realizado pelo chefe de departamento */
	private Date inicioCadastroTurmaFerias, fimCadastroTurmaFerias;

	/** Período de matricula de aluno em turma de férias */
	private Date inicioMatriculaTurmaFerias, fimMatriculaTurmaFerias;

	/** Período para os alunos realizarem matrículas extraordinárias em turmas de férias com vagas ainda remanescentes */
	private Date inicioMatriculaExtraordinariaFerias, fimMatriculaExtraordinariaFerias;
	
	// OUTROS PERÍODOS 
	/** Período permitido para trancamento de matrículas de alunos em turmas */
	private Date inicioTrancamentoTurma, fimTrancamentoTurma;
	
	/**Período permitido para trancamento de programa feito pelos alunos. */
	private Date inicioTrancamentoPrograma, fimTrancamentoPrograma;
	
	/**Período permitido para trancamento de programa a posteriori feito pelos alunos. */
	private Date inicioTrancamentoProgramaPosteriori, fimTrancamentoProgramaPosteriori;
	
	/** Período para solicitação de cadastro de turma aos chefes de departamento */
	private Date inicioSolicitacaoTurma, fimSolicitacaoTurma;

	/** Período para os docentes consolidarem suas turmas */
	private Date inicioConsolidacaoTurma, fimConsolidacaoTurma;
	
	/** Período para os docentes consolidarem parcialmente suas turmas */
	private Date inicioConsolidacaoParcialTurma, fimConsolidacaoParcialTurma;

	/** Período de cadastro de turmas realizado pelo chefe de departamento */
	private Date inicioCadastroTurma, fimCadastroTurma;
	
	/** Período de cadastro de sugestão de criação de turmas realizado pelo chefe de departamento. */
	private Date inicioSugestaoTurmaChefe, fimSugestaoTurmaChefe;

	/** Período de cadastro de Plano de Mattrícula pela Coordenação */
	private Date inicioCadastroPlanoMatricula, fimCadastroPlanoMatricula;

	/** Período de cadastramento de novos alunos */
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
	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_calendario", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idCalendario) {
		this.id = idCalendario;
	}

	/** Retorna a unidade do calendário.
	 * @return unidade do calendário 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_unidade")
	public Unidade getUnidade() {
		return this.unidade;
	}

	/** Seta a unidade do calendário.
	 * @param unidade unidade do calendário 
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

	/** Retorna o registro entrada da última atualização 
	 * @return registro entrada da última atualização 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o registro entrada da última atualização 
	 * @param registroAtualizacao registro entrada da última atualização 
	 */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Retorna a data da última atualização 
	 * @return data da última atualização 
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	/** Seta a data da última atualização 
	 * @param dataAtualizacao data da última atualização 
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

	/** Retorna o código hash do objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Valida os atributos do calendário.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if(this.nivel != NivelEnsino.MEDIO){
			if (ano < 2000 ) {
				lista.addErro("Ano inválido");
				return lista;
			}
			
			if ( !PeriodoAcademicoHelper.getInstance().isPeriodoRegular(periodo) ) {
				lista.addErro("O período deve ser: " + PeriodoAcademicoHelper.getInstance().getPeriodosRegularesFormatado());
				return lista;
			}
			
		} else if (ano < 2000)
			lista.addErro("Ano inválido");
		
		validaInicioFim(inicioPeriodoLetivo, fimPeriodoLetivo, "Período Letivo", lista);
		
		if(this.nivel != NivelEnsino.MEDIO) {
			validaInicioFim(inicioConsolidacaoTurma, fimConsolidacaoTurma, "Consolidação de Turmas", lista);
			validaInicioFim(inicioConsolidacaoParcialTurma, fimConsolidacaoParcialTurma, "Consolidação Parcial de Turmas", lista);
			validaInicioFim(inicioTrancamentoTurma, fimTrancamentoTurma, "Trancamento de Turmas", lista);
			validaInicioFim(inicioMatriculaOnline, fimMatriculaOnline, "Matrícula Online", lista);
	
			if(this.nivel != NivelEnsino.TECNICO){
				validaInicioFim(inicioMatriculaAlunoCadastrado, fimMatriculaAlunoCadastrado, "Matrícula de Aluno Ingressante", lista);
				validaInicioFim(inicioFerias, fimFerias, "Férias", lista);
				validaInicioFim(inicioSolicitacaoTurma, fimSolicitacaoTurma, "Solicitação de Cadastro de Turmas", lista);
				validaInicioFim(inicioMatriculaAlunoEspecial, fimMatriculaAlunoEspecial, "Matrícula de aluno especial", lista);
				validaInicioFim(inicioCoordenacaoAnaliseMatricula, fimCoordenacaoAnaliseMatricula, "Análise dos Coordenadores", lista);
				validaInicioFim(inicioDiscenteAnaliseMatricula, fimDiscenteAnaliseMatricula, "Análise dos Discentes", lista);
				validaInicioFim(inicioProcessamentoMatricula, fimProcessamentoMatricula, "Processamento de Matrícula", lista);
				validaInicioFim(inicioAjustesMatricula, fimAjustesMatricula, "Ajustes das Matrículas/Turmas", lista);
				validaInicioFim(inicioReMatricula, fimReMatricula, "Re-Matrícula", lista);
				validaInicioFim(inicioCoordenacaoAnaliseReMatricula, fimCoordenacaoAnaliseReMatricula, "Análise dos Coordenadores das solicitações de re-matrícula", lista);
				validaInicioFim(inicioDiscenteAnaliseReMatricula, fimDiscenteAnaliseReMatricula, "Análise dos Discentes para re-matrícula", lista);
				validaInicioFim(inicioProcessamentoReMatricula, fimProcessamentoReMatricula, "Processamento de Re-Matrícula", lista);
				validaInicioFim(inicioAjustesReMatricula, fimAjustesReMatricula, "Ajustes das Re-Matrículas/Turmas", lista);
				validaInicioFim(inicioRequerimentoEnsinoIndiv, fimRequerimentoEnsinoIndiv, "Requerimento de Ensino Individualizado", lista);
				validaInicioFim(inicioMatriculaTurmaFerias, fimMatriculaTurmaFerias, "Matrícula em Turmas de Férias", lista);
				validaInicioFim(inicioMatriculaExtraordinaria, fimMatriculaExtraordinaria, "Matrícula Extraordinária", lista);
				validaInicioFim(inicioMatriculaExtraordinariaFerias, fimMatriculaExtraordinariaFerias, "Matrícula Extraordinária de Férias", lista);
				validaInicioFim(inicioSugestaoTurmaChefe, fimSugestaoTurmaChefe, "Sugestão de Turmas pelo Chefe do Departamento para o Próximo Período", lista);
			}
		} 
		
		if (this.nivel == NivelEnsino.GRADUACAO) {
			validaInicioFim(inicioCadastroPlanoMatricula, fimCadastroPlanoMatricula, "Plano de Matrículas", lista);
		}

		if(this.nivel == NivelEnsino.STRICTO) {
			validateRequired(inicioPeriodoLetivo, "Início do Período Letivo", lista);
			validateRequired(fimPeriodoLetivo, "Fim do Período Letivo", lista);
			validaInicioFim(inicioPeriodoLetivo, fimPeriodoLetivo, "Período Letivo", lista);
		}
		
		return lista;
	}

	/** Indica se o calendário está ativo ou não. 
	 * @return true, se o calendário está ativo ou não. 
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o calendário está ativo ou não 
	 * @param ativo true, se o calendário está ativo ou não 
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o nível de ensino do calendário.
	 * @return nível de ensino do calendário. 
	 */
	public char getNivel() {
		return nivel;
	}

	/** Seta o nível de ensino do calendário.
	 * @param nivel nível de ensino do calendário.
	 */
	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	/** Retorna o ano do calendário.
	 * @return ano do calendário.
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano do calendário.
	 * @param ano ano do calendário.
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o fim período para alunos solicitarem suas matrículas. 
	 * @return Fim do período para alunos solicitarem suas matrículas. 
	 */
	public Date getFimMatriculaOnline() {
		return fimMatriculaOnline;
	}

	/** Seta o fim do período para alunos solicitarem suas matrículas. 
	 * @param fimMatricula Fim do período para alunos solicitarem suas matrículas. 
	 */
	public void setFimMatriculaOnline(Date fimMatricula) {
		this.fimMatriculaOnline = fimMatricula;
	}

	/** Retorna data de fim do período letivo.
	 * @return data de fim do período letivo.
	 */
	public Date getFimPeriodoLetivo() {
		return fimPeriodoLetivo;
	}

	/** Seta a data de fim do período letivo.
	 * @param fimSemestre data de fim do período letivo.
	 */
	public void setFimPeriodoLetivo(Date fimSemestre) {
		this.fimPeriodoLetivo = fimSemestre;
	}

	/** Retorna o fim do período permitido para trancamento de matrículas de alunos em turmas 
	 * @return Fim do período permitido para trancamento de matrículas de alunos em turmas 
	 */
	public Date getFimTrancamentoTurma() {
		return fimTrancamentoTurma;
	}

	/** Seta o fim do período permitido para trancamento de matrículas de alunos em turmas 
	 * @param fimTrancamento fim do período permitido para trancamento de matrículas de alunos em turmas 
	 */
	public void setFimTrancamentoTurma(Date fimTrancamento) {
		this.fimTrancamentoTurma = fimTrancamento;
	}
	
	/** Retorna o fim do período permitido para trancamento de programa para os alunos 
	 * @return Fim do período permitido para trancamento de programas regulares. 
	 */
	public Date getFimTrancamentoPrograma() {
		return fimTrancamentoPrograma;
	}

	/** Seta o fim do período permitido para trancamento de programa para os alunos 
	 * @param fimTrancamentoPrograma fim do período permitido para trancamento de programas regulares. 
	 */
	public void setFimTrancamentoPrograma(Date fimTrancamentoPrograma) {
		this.fimTrancamentoPrograma = fimTrancamentoPrograma;
	}
	
	/** Retorna o fim do período permitido para trancamento de programa a posteriori para os alunos. 
	 * @return Fim do período permitido para trancamento de programa a posteriori. 
	 */
	public Date getFimTrancamentoProgramaPosteriori() {
		return fimTrancamentoProgramaPosteriori;
	}

	/** Seta o fim do período permitido para trancamento de programa  a posteriori para os alunos. 
	 * @param fimTrancamentoPrograma fim do período permitido para trancamento de programaa a posteriori. 
	 */
	public void setFimTrancamentoProgramaPosteriori(
			Date fimTrancamentoProgramaPosteriori) {
		this.fimTrancamentoProgramaPosteriori = fimTrancamentoProgramaPosteriori;
	}

	/** Retorna o início do período para alunos solicitarem suas matrículas. 
	 * @return início do período para alunos solicitarem suas matrículas. 
	 */
	public Date getInicioMatriculaOnline() {
		return inicioMatriculaOnline;
	}

	/** Seta o início do período para alunos solicitarem suas matrículas.
	 * @param inicioMatricula início do período para alunos solicitarem suas matrículas. 
	 */
	public void setInicioMatriculaOnline(Date inicioMatricula) {
		this.inicioMatriculaOnline = inicioMatricula;
	}

	/** Retorna a data de início do período letivo. 
	 * @return data de início do período letivo.
	 */
	public Date getInicioPeriodoLetivo() {
		return inicioPeriodoLetivo;
	}

	/** Seta a data de início do período letivo
	 * @param inicioSemestre data de início do período letivo
	 */
	public void setInicioPeriodoLetivo(Date inicioSemestre) {
		this.inicioPeriodoLetivo = inicioSemestre;
	}

	/** Retorna o início do período permitido para trancamento de matrículas de alunos em turmas. 
	 * @return início do período permitido para trancamento de matrículas de alunos em turmas.
	 */
	public Date getInicioTrancamentoTurma() {
		return inicioTrancamentoTurma;
	}

	/** Seta o início do período permitido para trancamento de matrículas de alunos em turmas.
	 * @param inicioTrancamento início do período permitido para trancamento de matrículas de alunos em turmas.
	 */
	public void setInicioTrancamentoTurma(Date inicioTrancamento) {
		this.inicioTrancamentoTurma = inicioTrancamento;
	}
	
	/**Retorna o início do período permitido para trancamento de programa para alunos.
	 * @return  início do período permitido para trancamento de programas regulares.
	 */
	public Date getInicioTrancamentoPrograma() {
		return inicioTrancamentoPrograma;
	}

	/**
	 * Seta o início do período permitido para trancamento de programa para alunos.
	 * @param inicioTrancamentoPrograma início do período permitido para trancamento regular de programa.
	 */
	public void setInicioTrancamentoPrograma(Date inicioTrancamentoPrograma) {
		this.inicioTrancamentoPrograma = inicioTrancamentoPrograma;
	}
	
	/**Retorna o início do período permitido para trancamento de programa a posteriori para alunos.
	 * @return  início do período permitido para trancamento de programa a posteriori.
	 */
	public Date getInicioTrancamentoProgramaPosteriori() {
		return inicioTrancamentoProgramaPosteriori;
	}

	/**
	 * Seta o início do período permitido para trancamento de programa a posteriori para alunos.
	 * @return inicioTrancamentoProgramaPosteriori início do período permitido para trancamento a posteriori de programa.
	 */
	public void setInicioTrancamentoProgramaPosteriori(
			Date inicioTrancamentoProgramaPosteriori) {
		this.inicioTrancamentoProgramaPosteriori = inicioTrancamentoProgramaPosteriori;
	}

	/** Retorna do Período do calendário. 
	 * @return Período do calendário. 
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o Período do calendário.
	 * @param semestre Período do calendário.
	 */
	public void setPeriodo(int semestre) {
		this.periodo = semestre;
	}

	/** Retorna uma string representando o ano-período do calendário.
	 * @return String no formato "ano.período"
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

	/** Retorna o convênio acadêmico do calendário 
	 * @return Convênio acadêmico do calendário 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_convenio")
	public ConvenioAcademico getConvenio() {
		return convenio;
	}

	/** Seta o convênio acadêmico do calendário 
	 * @param convenio Convênio acadêmico do calendário 
	 */
	public void setConvenio(ConvenioAcademico convenio) {
		this.convenio = convenio;
	}

	/** Retorna o curso do calendário 
	 * @return Curso do calendário 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso do calendário 
	 * @param curso Curso do calendário 
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna a modalidade de educação do calendário 
	 * @return Modalidade de educação do calendário 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_modalidade")
	public ModalidadeEducacao getModalidade() {
		return modalidade;
	}

	/** Seta a modalidade de educação do calendário 
	 * @param modalidade Modalidade de educação do calendário 
	 */
	public void setModalidade(ModalidadeEducacao modalidade) {
		this.modalidade = modalidade;
	}

	/** Retorna o fim do período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula 
	 * @return fim do período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula
	 */
	public Date getFimAjustesMatricula() {
		return fimAjustesMatricula;
	}

	/** Seta o fim do período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula
	 * @param fimAjustesMatricula fim do período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula
	 */
	public void setFimAjustesMatricula(Date fimAjustesMatricula) {
		this.fimAjustesMatricula = fimAjustesMatricula;
	}

	/** Retorna o fim do período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula 
	 * @return fim do período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula 
	 */
	public Date getFimAjustesReMatricula() {
		return fimAjustesReMatricula;
	}

	/** Seta o fim do período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula 
	 * @param fimAjustesReMatricula fim do período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula 
	 */
	public void setFimAjustesReMatricula(Date fimAjustesReMatricula) {
		this.fimAjustesReMatricula = fimAjustesReMatricula;
	}

	/** Retorna o fim do período para os docentes consolidarem suas turmas 
	 * @return fim do período para os docentes consolidarem suas turmas 
	 */
	public Date getFimConsolidacaoTurma() {
		return fimConsolidacaoTurma;
	}

	/** Seta o fim do período para os docentes consolidarem suas turmas 
	 * @param fimConsolidacaoTurma fim do período para os docentes consolidarem suas turmas 
	 */
	public void setFimConsolidacaoTurma(Date fimConsolidacaoTurma) {
		this.fimConsolidacaoTurma = fimConsolidacaoTurma;
	}

	/** Retorna o fim do período para os coordenadores de curso analisarem as solicitações de matrícula 
	 * @return fim do período para os coordenadores de curso analisarem as solicitações de matrícula 
	 */
	public Date getFimCoordenacaoAnaliseMatricula() {
		return fimCoordenacaoAnaliseMatricula;
	}

	/** Seta o fim do período para os coordenadores de curso analisarem as solicitações de matrícula 
	 * @param fimCoordenacaoAnaliseMatricula fim do período para os coordenadores de curso analisarem as solicitações de matrícula 
	 */
	public void setFimCoordenacaoAnaliseMatricula(Date fimCoordenacaoAnaliseMatricula) {
		this.fimCoordenacaoAnaliseMatricula = fimCoordenacaoAnaliseMatricula;
	}

	/** Retorna o fim do período para os alunos analisarem as análises dos coordenadores 
	 * @return fim do período para os alunos analisarem as análises dos coordenadores 
	 */
	public Date getFimDiscenteAnaliseMatricula() {
		return fimDiscenteAnaliseMatricula;
	}

	/** Seta o fim do período para os alunos analisarem as análises dos coordenadores 
	 * @param fimDiscenteAnaliseMatricula fim do período para os alunos analisarem as análises dos coordenadores 
	 */
	public void setFimDiscenteAnaliseMatricula(Date fimDiscenteAnaliseMatricula) {
		this.fimDiscenteAnaliseMatricula = fimDiscenteAnaliseMatricula;
	}

	/** Retorna o fim do período DAE matricular alunos especiais 
	 * @return fim do período DAE matricular alunos especiais 
	 */
	public Date getFimMatriculaAlunoEspecial() {
		return fimMatriculaAlunoEspecial;
	}

	/** Seta o fim do período DAE matricular alunos especiais 
	 * @param fimMatriculaAlunoEspecial fim do período DAE matricular alunos especiais 
	 */
	public void setFimMatriculaAlunoEspecial(Date fimMatriculaAlunoEspecial) {
		this.fimMatriculaAlunoEspecial = fimMatriculaAlunoEspecial;
	}

	/** Retorna o fim do período do processamento de matrícula 
	 * @return fim do período do processamento de matrícula 
	 */
	public Date getFimProcessamentoMatricula() {
		return fimProcessamentoMatricula;
	}

	/** Seta o fim do período do processamento de matrícula 
	 * @param fimProcessamentoMatricula fim do período do processamento de matrícula 
	 */
	public void setFimProcessamentoMatricula(Date fimProcessamentoMatricula) {
		this.fimProcessamentoMatricula = fimProcessamentoMatricula;
	}

	/** Retorna o fim do período de processamento da rematrícula 
	 * @return fim do período de processamento da rematrícula 
	 */
	public Date getFimProcessamentoReMatricula() {
		return fimProcessamentoReMatricula;
	}

	/** Seta o fim do período de processamento da rematrícula 
	 * @param fimProcessamentoReMatricula fim do período de processamento da rematrícula 
	 */
	public void setFimProcessamentoReMatricula(Date fimProcessamentoReMatricula) {
		this.fimProcessamentoReMatricula = fimProcessamentoReMatricula;
	}

	/** Retorna o fim do período para alunos solicitarem a re-matricula nas disciplinas 
	 * @return fim do período para alunos solicitarem a re-matricula nas disciplinas 
	 */
	public Date getFimReMatricula() {
		return fimReMatricula;
	}

	/** Seta o fim do período para alunos solicitarem a re-matricula nas disciplinas 
	 * @param fimReMatricula fim do período para alunos solicitarem a re-matricula nas disciplinas 
	 */
	public void setFimReMatricula(Date fimReMatricula) {
		this.fimReMatricula = fimReMatricula;
	}

	/** Retorna o fim do período de requerimento de ensino individualizado pelos alunos 
	 * @return fim do período de requerimento de ensino individualizado pelos alunos 
	 */
	public Date getFimRequerimentoEnsinoIndiv() {
		return fimRequerimentoEnsinoIndiv;
	}

	/** Seta o fim do período de requerimento de ensino individualizado pelos alunos 
	 * @param fimRequerimentoEnsinoIndiv fim do período de requerimento de ensino individualizado pelos alunos 
	 */
	public void setFimRequerimentoEnsinoIndiv(Date fimRequerimentoEnsinoIndiv) {
		this.fimRequerimentoEnsinoIndiv = fimRequerimentoEnsinoIndiv;
	}

	/** Retorna o fim do período para solicitação de cadastro de turma aos chefes de departamento 
	 * @return fim do período para solicitação de cadastro de turma aos chefes de departamento 
	 */
	public Date getFimSolicitacaoTurma() {
		return fimSolicitacaoTurma;
	}

	/** Seta o fim do período para solicitação de cadastro de turma aos chefes de departamento 
	 * @param fimSolicitacaoTurma fim do período para solicitação de cadastro de turma aos chefes de departamento 
	 */
	public void setFimSolicitacaoTurma(Date fimSolicitacaoTurma) {
		this.fimSolicitacaoTurma = fimSolicitacaoTurma;
	}

	/** Retorna o início do período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula 
	 * @return início do período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula 
	 */
	public Date getInicioAjustesMatricula() {
		return inicioAjustesMatricula;
	}

	/** Seta o início do período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula 
	 * @param inicioAjustesMatricula início do período para ajustes e remanejamentos de alunos em turmas depois do processamento de matrícula 
	 */
	public void setInicioAjustesMatricula(Date inicioAjustesMatricula) {
		this.inicioAjustesMatricula = inicioAjustesMatricula;
	}

	/** Retorna o início do período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula 
	 * @return início do período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula 
	 */
	public Date getInicioAjustesReMatricula() {
		return inicioAjustesReMatricula;
	}

	/** Seta o início do período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula 
	 * @param inicioAjustesReMatricula início do período para ajustes e remanejamentos de alunos em turmas depois do processamento de re-matrícula 
	 */
	public void setInicioAjustesReMatricula(Date inicioAjustesReMatricula) {
		this.inicioAjustesReMatricula = inicioAjustesReMatricula;
	}


	/** Retorna o início do período para os docentes consolidarem suas turmas 
	 * @return início do período para os docentes consolidarem suas turmas 
	 */
	public Date getInicioConsolidacaoTurma() {
		return inicioConsolidacaoTurma;
	}

	/** Seta o início do período para os docentes consolidarem suas turmas 
	 * @param inicioConsolidacaoTurma início do período para os docentes consolidarem suas turmas 
	 */
	public void setInicioConsolidacaoTurma(Date inicioConsolidacaoTurma) {
		this.inicioConsolidacaoTurma = inicioConsolidacaoTurma;
	}

	/** Retorna o início do período para os coordenadores de curso analisarem as solicitações de matrícula 
	 * @return início do período para os coordenadores de curso analisarem as solicitações de matrícula 
	 */
	public Date getInicioCoordenacaoAnaliseMatricula() {
		return inicioCoordenacaoAnaliseMatricula;
	}

	/** Seta o início do período para os coordenadores de curso analisarem as solicitações de matrícula  
	 * @param inicioCoordenacaoAnaliseMatricula início do período para os coordenadores de curso analisarem as solicitações de matrícula
	 */
	public void setInicioCoordenacaoAnaliseMatricula(Date inicioCoordenacaoAnaliseMatricula) {
		this.inicioCoordenacaoAnaliseMatricula = inicioCoordenacaoAnaliseMatricula;
	}

	/** Retorna o início do período para os alunos analisarem as análises dos coordenadores 
	 * @return início do período para os alunos analisarem as análises dos coordenadores 
	 */
	public Date getInicioDiscenteAnaliseMatricula() {
		return inicioDiscenteAnaliseMatricula;
	}

	/** Seta o início do período para os alunos analisarem as análises dos coordenadores 
	 * @param inicioDiscenteAnaliseMatricula início do período para os alunos analisarem as análises dos coordenadores 
	 */
	public void setInicioDiscenteAnaliseMatricula(Date inicioDiscenteAnaliseMatricula) {
		this.inicioDiscenteAnaliseMatricula = inicioDiscenteAnaliseMatricula;
	}

	/** Retorna o início do período DAE matricular alunos especiais 
	 * @return início do período DAE matricular alunos especiais 
	 */
	public Date getInicioMatriculaAlunoEspecial() {
		return inicioMatriculaAlunoEspecial;
	}

	/** Seta o início do período DAE matricular alunos especiais 
	 * @param inicioMatriculaAlunoEspecial início do período DAE matricular alunos especiais 
	 */
	public void setInicioMatriculaAlunoEspecial(Date inicioMatriculaAlunoEspecial) {
		this.inicioMatriculaAlunoEspecial = inicioMatriculaAlunoEspecial;
	}

	/** Retorna o início do período do processamento de matrícula 
	 * @return início do período do processamento de matrícula 
	 */
	public Date getInicioProcessamentoMatricula() {
		return inicioProcessamentoMatricula;
	}

	/** Seta o início do período do processamento de matrícula 
	 * @param inicioProcessamentoMatricula início do período do processamento de matrícula 
	 */
	public void setInicioProcessamentoMatricula(Date inicioProcessamentoMatricula) {
		this.inicioProcessamentoMatricula = inicioProcessamentoMatricula;
	}

	/** Retorna o início do período de processamento da rematrícula 
	 * @return início do período de processamento da rematrícula 
	 */
	public Date getInicioProcessamentoReMatricula() {
		return inicioProcessamentoReMatricula;
	}

	/** Seta o início do período de processamento da rematrícula  
	 * @param inicioProcessamentoReMatricula início do período de processamento da rematrícula 
	 */
	public void setInicioProcessamentoReMatricula(Date inicioProcessamentoReMatricula) {
		this.inicioProcessamentoReMatricula = inicioProcessamentoReMatricula;
	}

	/** Retorna o início do período para alunos solicitarem a re-matricula nas disciplinas 
	 * @return início do período para alunos solicitarem a re-matricula nas disciplinas 
	 */
	public Date getInicioReMatricula() {
		return inicioReMatricula;
	}

	/** Seta o início do período para alunos solicitarem a re-matricula nas disciplinas 
	 * @param inicioReMatricula início do período para alunos solicitarem a re-matricula nas disciplinas 
	 */
	public void setInicioReMatricula(Date inicioReMatricula) {
		this.inicioReMatricula = inicioReMatricula;
	}

	/** Retorna o início do período de requerimento de ensino individualizado pelos alunos 
	 * @return início do período de requerimento de ensino individualizado pelos alunos 
	 */
	public Date getInicioRequerimentoEnsinoIndiv() {
		return inicioRequerimentoEnsinoIndiv;
	}

	/** Seta o início do período de requerimento de ensino individualizado pelos alunos 
	 * @param inicioRequerimentoEnsinoIndiv início do período de requerimento de ensino individualizado pelos alunos 
	 */
	public void setInicioRequerimentoEnsinoIndiv(Date inicioRequerimentoEnsinoIndiv) {
		this.inicioRequerimentoEnsinoIndiv = inicioRequerimentoEnsinoIndiv;
	}

	/** Retorna o início do período para solicitação de cadastro de turma aos chefes de departamento 
	 * @return início do período para solicitação de cadastro de turma aos chefes de departamento 
	 */
	public Date getInicioSolicitacaoTurma() {
		return inicioSolicitacaoTurma;
	}

	/** Seta o início do período para solicitação de cadastro de turma aos chefes de departamento 
	 * @param inicioSolicitacaoTurma início do período para solicitação de cadastro de turma aos chefes de departamento 
	 */
	public void setInicioSolicitacaoTurma(Date inicioSolicitacaoTurma) {
		this.inicioSolicitacaoTurma = inicioSolicitacaoTurma;
	}

	/** Retorna o fim do período para os coordenadores de curso analisarem as solicitações de rematrícula 
	 * @return início do período para os coordenadores de curso analisarem as solicitações de rematrícula 
	 */
	public Date getFimCoordenacaoAnaliseReMatricula() {
		return fimCoordenacaoAnaliseReMatricula;
	}

	/** Seta o fim do período para os coordenadores de curso analisarem as solicitações de rematrícula 
	 * @param fimCoordenacaoAnaliseReMatricula início do período para os coordenadores de curso analisarem as solicitações de rematrícula 
	 */
	public void setFimCoordenacaoAnaliseReMatricula(Date fimCoordenacaoAnaliseReMatricula) {
		this.fimCoordenacaoAnaliseReMatricula = fimCoordenacaoAnaliseReMatricula;
	}

	/** Retorna o fim do período para os alunos analisarem as análises dos coordenadores 
	 * @return fim do período para os alunos analisarem as análises dos coordenadores 
	 */
	public Date getFimDiscenteAnaliseReMatricula() {
		return fimDiscenteAnaliseReMatricula;
	}

	/** Seta o fim do período para os alunos analisarem as análises dos coordenadores 
	 * @param fimDiscenteAnaliseReMatricula fim do período para os alunos analisarem as análises dos coordenadores 
	 */
	public void setFimDiscenteAnaliseReMatricula(Date fimDiscenteAnaliseReMatricula) {
		this.fimDiscenteAnaliseReMatricula = fimDiscenteAnaliseReMatricula;
	}

	/** Retorna o período para os coordenadores de curso analisarem as solicitações de rematrícula 
	 * @return Período para os coordenadores de curso analisarem as solicitações de rematrícula 
	 */
	public Date getInicioCoordenacaoAnaliseReMatricula() {
		return inicioCoordenacaoAnaliseReMatricula;
	}

	/** Seta o período para os coordenadores de curso analisarem as solicitações de rematrícula 
	 * @param inicioCoordenacaoAnaliseReMatricula Período para os coordenadores de curso analisarem as solicitações de rematrícula 
	 */
	public void setInicioCoordenacaoAnaliseReMatricula(Date inicioCoordenacaoAnaliseReMatricula) {
		this.inicioCoordenacaoAnaliseReMatricula = inicioCoordenacaoAnaliseReMatricula;
	}

	/** Retorna o início do período para os alunos analisarem as análises dos coordenadores 
	 * @return início do período para os alunos analisarem as análises dos coordenadores 
	 */
	public Date getInicioDiscenteAnaliseReMatricula() {
		return inicioDiscenteAnaliseReMatricula;
	}

	/** Seta o início do período para os alunos analisarem as análises dos coordenadores 
	 * @param inicioDiscenteAnaliseReMatricula início do período para os alunos analisarem as análises dos coordenadores 
	 */
	public void setInicioDiscenteAnaliseReMatricula(Date inicioDiscenteAnaliseReMatricula) {
		this.inicioDiscenteAnaliseReMatricula = inicioDiscenteAnaliseReMatricula;
	}

	/** Retorna o início do período de férias 
	 * @return início do período de férias 
	 */
	public Date getInicioFerias() {
		return inicioFerias;
	}

	/** Seta o início do período de férias 
	 * @param inicioFerias início do período de férias 
	 */
	public void setInicioFerias(Date inicioFerias) {
		this.inicioFerias = inicioFerias;
	}

	/** Retorna o fim do período de férias 
	 * @return fim do período de férias 
	 */
	public Date getFimFerias() {
		return fimFerias;
	}

 	/** Seta o fim do período de férias 
	 * @param fimFerias fim do período de férias 
	 */
	public void setFimFerias(Date fimFerias) {
		this.fimFerias = fimFerias;
	}

	/** Indica se a data atual está dentro do período informado.
	 * @param inicio
	 * @param fim
	 * @return true, caso a data atual esteja dentro do período informado. 
	 */
	private boolean isOperacaoDentroPeriodo(Date inicio, Date fim) {
		return CalendarUtils.isDentroPeriodo(inicio, fim);
	}

	/** Retorna o início do período para solicitação de cadastro de turma de FERIAS aos chefes de departamento 
	 * @return início do período para solicitação de cadastro de turma de FERIAS aos chefes de departamento 
	 */
	public Date getInicioSolicitacaoTurmaFerias() {
		return inicioSolicitacaoTurmaFerias;
	}

	/** Seta o início do período para solicitação de cadastro de turma de FERIAS aos chefes de departamento 
	 * @param inicioSolicitacaoTurmaFerias início do período para solicitação de cadastro de turma de FERIAS aos chefes de departamento 
	 */
	public void setInicioSolicitacaoTurmaFerias(Date inicioSolicitacaoTurmaFerias) {
		this.inicioSolicitacaoTurmaFerias = inicioSolicitacaoTurmaFerias;
	}

	/** Retorna o fim do período para solicitação de cadastro de turma de FERIAS aos chefes de departamento 
	 * @return fim do período para solicitação de cadastro de turma de FERIAS aos chefes de departamento 
	 */
	public Date getFimSolicitacaoTurmaFerias() {
		return fimSolicitacaoTurmaFerias;
	}

	/** Seta o fim do período para solicitação de cadastro de turma de FERIAS aos chefes de departamento 
	 * @param fimSolicitacaoTurmaFerias fim do período para solicitação de cadastro de turma de FERIAS aos chefes de departamento 
	 */
	public void setFimSolicitacaoTurmaFerias(Date fimSolicitacaoTurmaFerias) {
		this.fimSolicitacaoTurmaFerias = fimSolicitacaoTurmaFerias;
	}

	/** Verifica se hoje está no período de matricula de aluno em turma de ferias
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaTurmaFerias()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaTurmaFerias(), getFimMatriculaTurmaFerias());
	}
	
	/** Verifica se a data atual está dentro do período de matrícula extraordinária em turmas de férias.
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaExtraordinariaFerias()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaExtraordinariaFerias(), getFimMatriculaExtraordinariaFerias());
	}

	/** Verifica se hoje está no período de cadastramento de turmas realizado pelo chefe de departamento
	 * @return
	 */
	@Transient
	public boolean isPeriodoCadastroTurmas()  {
		return isOperacaoDentroPeriodo(getInicioCadastroTurma(), getFimCadastroTurma());
	}

	/** Verifica se hoje está no período de cadastramento de turmas de ferias realizado pelo chefe de departamento
	 * @return
	 */
	@Transient
	public boolean isPeriodoCadastroTurmasFerias()  {
		return isOperacaoDentroPeriodo(getInicioCadastroTurmaFerias(), getFimCadastroTurmaFerias());
	}

	/** Verifica se hoje está no período de matrículas de alunos regulares (seja matrícula normal ou re-matrícula) 
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaRegular()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaOnline(), getFimMatriculaOnline()) ||
				isOperacaoDentroPeriodo(getInicioReMatricula(), getFimReMatricula());
	}

	/**
	 * Verifica se hoje está no período de cadastro/alteração de de Plano de Matrículas
	 * 
	 * @return
	 */
	@Transient
	public boolean isPeriodoPlanoMatriculas()  {
		return isOperacaoDentroPeriodo(getInicioCadastroPlanoMatricula(), getFimCadastroPlanoMatricula());
	}
	
	/**
	 * Verifica se hoje está no período de cadastramento de discentes
	 * 
	 * @return
	 */
	@Transient
	public boolean isPeriodoCadastramentoDiscente()  {
		return isOperacaoDentroPeriodo(getInicioCadastramentoDiscente(), getFimCadastramentoDiscente());
	}
	
	/** Verifica se a data atual está dentro do período de rematrícula.
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaExtraordinaria()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaExtraordinaria(), getFimMatriculaExtraordinaria());
	}
	
	/** Verifica se a data atual está dentro do período de rematrícula.
	 * @return
	 */
	@Transient
	public boolean isPeriodoReMatricula()  {
		return isOperacaoDentroPeriodo(getInicioReMatricula(), getFimReMatricula());
	}

	/** Indica se a data atual está dentro do período de aluno cadastrado.
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaAlunoCadastrado()  {
		return isOperacaoDentroPeriodo(getInicioMatriculaAlunoCadastrado(), getFimMatriculaAlunoCadastrado()) ||
				isOperacaoDentroPeriodo(getInicioReMatricula(), getFimReMatricula());
	}


	/** Verifica se hoje está no período de análise dos coordenadores das solicitações de matrículas 
	 * @return
	 */
	@Transient
	public boolean isPeriodoAnaliseCoordenador()  {
		return (isOperacaoDentroPeriodo(getInicioCoordenacaoAnaliseMatricula(), getFimCoordenacaoAnaliseMatricula()) ||
				isOperacaoDentroPeriodo(getInicioCoordenacaoAnaliseReMatricula(), getFimCoordenacaoAnaliseReMatricula()));
	}

	/** Verifica se hoje está no período de revisão dos discentes das análises dos coordenadores
	 * @return
	 */
	@Transient
	public boolean isPeriodoAnaliseDiscente()  {
		return (isOperacaoDentroPeriodo(getInicioDiscenteAnaliseMatricula(), getFimDiscenteAnaliseMatricula()) ||
				isOperacaoDentroPeriodo(getInicioDiscenteAnaliseReMatricula(), getFimDiscenteAnaliseReMatricula()));
	}


	/** Verifica se hoje está no período de processamento de matrícula (seja na matrícula normal ou re-matrícula) 
	 * @return
	 */
	@Transient
	public boolean isPeriodoProcessamento() {
		return (isOperacaoDentroPeriodo(getInicioProcessamentoMatricula(), getFimProcessamentoMatricula()) ||
				isOperacaoDentroPeriodo(getInicioProcessamentoReMatricula(), getFimProcessamentoReMatricula()));
	}

	/** Verifica se hoje está no período de ajustes de turmas depois do processamento de matrícula 
	 * @return
	 */
	@Transient
	public boolean isPeriodoAjustesTurmas() {
		return (isOperacaoDentroPeriodo(getInicioAjustesMatricula(), getFimAjustesMatricula()) ||
				isOperacaoDentroPeriodo(getInicioAjustesReMatricula(), getFimAjustesReMatricula()));
	}

	/** Indica se a data atual está entre o período de trancamento de turmas.
	 * @return
	 */
	@Transient
	public boolean isPeriodoTrancamentoTurmas() {
		return (isOperacaoDentroPeriodo(getInicioTrancamentoTurma(), getFimTrancamentoTurma()) );
	}
	
	/** Indica se a data atual está entre o período de trancamento de programa.
	 * @return
	 */
	@Transient
	public boolean isPeriodoTrancamentoPrograma() {
		return (isOperacaoDentroPeriodo(getInicioTrancamentoPrograma(), getFimTrancamentoPrograma()) );
	}
	
	/** Indica se a data atual está entre o período de trancamento de programa.
	 * @return
	 */
	@Transient
	public boolean isPeriodoTrancamentoProgramaPosteriori() {
		return (isOperacaoDentroPeriodo(getInicioTrancamentoProgramaPosteriori(), getFimTrancamentoProgramaPosteriori()) );
	}
	

	/** Indica se a data atual está entre o período de matrícula de aluno especial.
	 * @return
	 */
	@Transient
	public boolean isPeriodoMatriculaAlunoEspecial() {
		return (isOperacaoDentroPeriodo(getInicioMatriculaAlunoEspecial(), getFimMatriculaAlunoEspecial()) );
	}

	/** Indica se a data atual está entre o período de solicitação de turma.
	 * @return
	 */
	@Transient
	public boolean isPeriodoSolicitacaoTurma() {
		return (isOperacaoDentroPeriodo(getInicioSolicitacaoTurma(), getFimSolicitacaoTurma()) );
	}

	/** Indica se a data atual está entre o período de requerimento de turma de férias.
	 * @return
	 */
	@Transient
	public boolean isPeriodoRequerimentoTurmaFerias() {
		return (isOperacaoDentroPeriodo(getInicioRequerimentoTurmaFerias(), getFimRequerimentoTurmaFerias()) );
	}

	/**
	 * Verifica se esta no período de solicitação de turma de férias
	 * @return
	 */
	@Transient
	public boolean isPeriodoSolicitacaoTurmaFerias() {
		return (isOperacaoDentroPeriodo(getInicioSolicitacaoTurmaFerias(), getFimSolicitacaoTurmaFerias()) );
	}


	/** Indica se a data atual está entre o período de férias.
	 * @return
	 */
	@Transient
	public boolean isPeriodoFerias() {
		if( getInicioFerias() == null || getFimFerias() == null )
			throw new ConfiguracaoAmbienteException("Erro de configuração no ambiente: o início e o fim do período de férias não estão definidos no calendário acadêmico vigente ( " + getAno() + "." + getPeriodo() + " )");
		return (isOperacaoDentroPeriodo(getInicioFerias(), getFimFerias()) );
	}

	/** Indica se a data atual está entre o período de requerimento de ensino individualizado pelos alunos.
	 * @return
	 */
	@Transient
	public boolean isPeriodoRequerimentoEnsinoIndiv() {
		return (isOperacaoDentroPeriodo(getInicioRequerimentoEnsinoIndiv(), getFimRequerimentoEnsinoIndiv()) );
	}

	/** Indica se a data atual está entre o período de solicitação de ensino individualizado pelos alunos
	 * @return
	 */
	@Transient
	public boolean isPeriodoSolicitacaoTurmaEnsinoIndiv() {
		return (isOperacaoDentroPeriodo(getInicioSolicitacaoTurmaEnsinoIndiv(), getFimSolicitacaoTurmaEnsinoIndiv()) );
	}

	/** Indica se a data atual está entre o período de cadastro de ensino individualizado pelos alunos
	 * @return
	 */
	@Transient
	public boolean isPeriodoCadastroTurmaEnsinoIndiv() {
		return (isOperacaoDentroPeriodo(getInicioCadastroTurmaEnsinoIndiv(), getFimCadastroTurmaEnsinoIndiv()) );
	}

	/**
	 * Retorna se está no período de análise de matrícula por parte da coordenação
	 */
	@Transient
	public boolean isPeriodoOrientacaoCoordenacao() {
		return (isOperacaoDentroPeriodo(getInicioCoordenacaoAnaliseMatricula(), getFimCoordenacaoAnaliseMatricula()) );
	}
	
	/**
	 * Retorna se está no período de validação dos vínculos de alunos ingressantes
	 */
	@Transient
	public boolean isPeriodoValidacaoVinculoIngressante() {
		return (isOperacaoDentroPeriodo(getInicioValidacaoVinculoIngressante(), getFimValidacaoVinculoIngressante()) );
	}
	
	/**
	 * Retorna uma instância do CalendarioAcademico com o ano e o período do próximo período regular setado!
	 * Ex:
	 * SE o ano período atual for 2007.1 retorna 2007.2
	 * SE o ano período atual for 2007.2 retorna 2008.1
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

	/** Retorna a descrição textual do nível de ensino.
	 * @return
	 */
	@Transient
	public String getNivelDescr() {
		return NivelEnsino.getDescricao(nivel);
	}

	/** Indica se o calendário é o vigente.
	 * @return true, se o calendário é o vigente.
	 */
	public boolean isVigente() {
		return vigente;
	}

	/** Seta se o calendário é o vigente.
	 * @param vigente true, se o calendário é o vigente.
	 */
	public void setVigente(boolean vigente) {
		this.vigente = vigente;
	}

	/** Retorna uma descrição textual do ano-período vigente.
	 * @return
	 */
	@Transient
	public String getAnoPeriodoVigente() {
		StringBuilder s = new StringBuilder(getAnoPeriodo());
		if (isVigente())
			s.append(" - Vigente");
		return  s.toString();
	}
	
	/** Retorna uma descrição textual do ano vigente (para ensino médio).
	 * @return
	 */
	@Transient
	public String getAnoVigente() {
		StringBuilder s = new StringBuilder(String.valueOf( getAno() ));
		if (isVigente())
			s.append(" - Vigente");
		return  s.toString();
	}	

	/** Retorna o início do período de cadastro de turmas realizado pelo chefe de departamento 
	 * @return início do período de cadastro de turmas realizado pelo chefe de departamento 
	 */
	public Date getInicioCadastroTurma() {
		return inicioCadastroTurma;
	}

	/** Seta o início do período de cadastro de turmas realizado pelo chefe de departamento 
	 * @param inicioCadastroTurma início do período de cadastro de turmas realizado pelo chefe de departamento 
	 */
	public void setInicioCadastroTurma(Date inicioCadastroTurma) {
		this.inicioCadastroTurma = inicioCadastroTurma;
	}

	/** Retorna o fim do período de cadastro de turmas realizado pelo chefe de departamento 
	 * @return fim do período de cadastro de turmas realizado pelo chefe de departamento 
	 */
	public Date getFimCadastroTurma() {
		return fimCadastroTurma;
	}

	/** Seta o fim do período de cadastro de turmas realizado pelo chefe de departamento 
	 * @param fimCadastroTurma fim do período de cadastro de turmas realizado pelo chefe de departamento 
	 */
	public void setFimCadastroTurma(Date fimCadastroTurma) {
		this.fimCadastroTurma = fimCadastroTurma;
	}

	/** Retorna o início do período de matricula de aluno em turma de férias 
	 * @return início do período de matricula de aluno em turma de férias 
	 */
	public Date getInicioMatriculaTurmaFerias() {
		return inicioMatriculaTurmaFerias;
	}

	/** Seta o início do período de matricula de aluno em turma de férias 
	 * @param inicioMatriculaTurmaFerias início do período de matricula de aluno em turma de férias 
	 */
	public void setInicioMatriculaTurmaFerias(Date inicioMatriculaTurmaFerias) {
		this.inicioMatriculaTurmaFerias = inicioMatriculaTurmaFerias;
	}

	/** Retorna o fim do período de matricula de aluno em turma de férias 
	 * @return fim do período de matricula de aluno em turma de férias 
	 */
	public Date getFimMatriculaTurmaFerias() {
		return fimMatriculaTurmaFerias;
	}

	/** Seta o fim do período de matricula de aluno em turma de férias 
	 * @param fimMatriculaTurmaFerias fim do período de matricula de aluno em turma de férias 
	 */
	public void setFimMatriculaTurmaFerias(Date fimMatriculaTurmaFerias) {
		this.fimMatriculaTurmaFerias = fimMatriculaTurmaFerias;
	}

	/** Retorna o início do período de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 * @return início do período de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 */
	public Date getInicioCadastroTurmaFerias() {
		return inicioCadastroTurmaFerias;
	}

	/** Seta o início do período de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 * @param inicioCadastroTurmaFerias início do período de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 */
	public void setInicioCadastroTurmaFerias(Date inicioCadastroTurmaFerias) {
		this.inicioCadastroTurmaFerias = inicioCadastroTurmaFerias;
	}

	/** Retorna o fim do período de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 * @return fim do período de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 */
	public Date getFimCadastroTurmaFerias() {
		return fimCadastroTurmaFerias;
	}

	/** Seta o fim do período de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 * @param fimCadastroTurmaFerias fim do período de cadastro de turmas de ferias realizado pelo chefe de departamento 
	 */
	public void setFimCadastroTurmaFerias(Date fimCadastroTurmaFerias) {
		this.fimCadastroTurmaFerias = fimCadastroTurmaFerias;
	}


	/** Retorna o início do período para coordenações de curso matricularem alunos cadastrados (calouros) 
	 * @return início do período para coordenações de curso matricularem alunos cadastrados (calouros) 
	 */
	public Date getInicioMatriculaAlunoCadastrado() {
		return inicioMatriculaAlunoCadastrado;
	}

	/** Seta o início do período para coordenações de curso matricularem alunos cadastrados (calouros) 
	 * @param inicioMatriculaAlunoCadastrado início do período para coordenações de curso matricularem alunos cadastrados (calouros) 
	 */
	public void setInicioMatriculaAlunoCadastrado(Date inicioMatriculaAlunoCadastrado) {
		this.inicioMatriculaAlunoCadastrado = inicioMatriculaAlunoCadastrado;
	}

	/** Retorna o fim do período para coordenações de curso matricularem alunos cadastrados (calouros) 
	 * @return fim do período para coordenações de curso matricularem alunos cadastrados (calouros) 
	 */
	public Date getFimMatriculaAlunoCadastrado() {
		return fimMatriculaAlunoCadastrado;
	}

	/** Seta o fim do período para coordenações de curso matricularem alunos cadastrados (calouros) 
	 * @param fimMatriculaAlunoCadastrado fim do período para coordenações de curso matricularem alunos cadastrados (calouros) 
	 */
	public void setFimMatriculaAlunoCadastrado(Date fimMatriculaAlunoCadastrado) {
		this.fimMatriculaAlunoCadastrado = fimMatriculaAlunoCadastrado;
	}

	/** Retorna o calendário acadêmico posterior a quantidade de semestre especificado.
	 * @param qtdPosterior Quantidade de semestres posterior ao calendário
	 * @return Calendário acadêmico posterior a quantidade de semestre especificado.
	 */
	public CalendarioAcademico incrCalendario(int qtdPosterior) {
		CalendarioAcademico cal = new CalendarioAcademico();
		int anoPeriodo = DiscenteHelper.somaSemestres(ano, periodo, qtdPosterior);
		cal.setAno( anoPeriodo / 10 );
		cal.setPeriodo( anoPeriodo - (cal.getAno() * 10) );
		return cal;
	}

	/** Retorna uma descrição textual informado o período da operação especificado.
	 * @param operacao Operação a informar o período
	 * @param inicio início do período informado.
	 * @param fim fim do período informado.
	 * @return Descrição textual informado o período da operação especificado.
	 */
	public static String getDescricaoPeriodo(String operacao, Date inicio, Date fim) {
		Formatador fmt = Formatador.getInstance();
		return "O período oficial para " + operacao + " é de " + fmt.formatarData(inicio) + " à " + fmt.formatarData(fim);
	}

	/** Retorna o ano de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado 
	 * @return ano de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado 
	 */
	public Integer getAnoFeriasVigente() {
		return anoFeriasVigente;
	}

	/** Seta o ano de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado 
	 * @param anoFeriasVigente o ano de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado 
	 */
	public void setAnoFeriasVigente(Integer anoFeriasVigente) {
		this.anoFeriasVigente = anoFeriasVigente;
	}

	/** Retorna o período de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado 
	 * @return período de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado 
	 */
	public Integer getPeriodoFeriasVigente() {
		return periodoFeriasVigente;
	}

	/** Seta o período de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado 
	 * @param periodoFeriasVigente período de férias que deve ser considerado nas operações do sistema quando este calendário estiver selecionado 
	 */
	public void setPeriodoFeriasVigente(Integer periodoFeriasVigente) {
		this.periodoFeriasVigente = periodoFeriasVigente;
	}

	/** Retorna o início do período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos 
	 * @return início do período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos 
	 */
	public Date getInicioSolicitacaoTurmaEnsinoIndiv() {
		return inicioSolicitacaoTurmaEnsinoIndiv;
	}

	/** Seta o início do período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos 
	 * @param inicioSolicitacaoTurmaEnsinoIndiv início do período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos 
	 */
	public void setInicioSolicitacaoTurmaEnsinoIndiv(
			Date inicioSolicitacaoTurmaEnsinoIndiv) {
		this.inicioSolicitacaoTurmaEnsinoIndiv = inicioSolicitacaoTurmaEnsinoIndiv;
	}

	/** Retorna o fim do período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos 
	 * @return fim do período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos 
	 */
	public Date getFimSolicitacaoTurmaEnsinoIndiv() {
		return fimSolicitacaoTurmaEnsinoIndiv;
	}

	/** Seta o fim do período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos 
	 * @param fimSolicitacaoTurmaEnsinoIndiv fim do período de solicitacão de turmas de ensino individualizado, pelas coordenações de cursos, aos departamentos 
	 */
	public void setFimSolicitacaoTurmaEnsinoIndiv(
			Date fimSolicitacaoTurmaEnsinoIndiv) {
		this.fimSolicitacaoTurmaEnsinoIndiv = fimSolicitacaoTurmaEnsinoIndiv;
	}

	/** Retorna o início do período de cadastro de turmas de ensino individualizado pelos departamentos 
	 * @return início do período de cadastro de turmas de ensino individualizado pelos departamentos 
	 */
	public Date getInicioCadastroTurmaEnsinoIndiv() {
		return inicioCadastroTurmaEnsinoIndiv;
	}

	/** Seta o início do período de cadastro de turmas de ensino individualizado pelos departamentos 
	 * @param inicioCadastroTurmaEnsinoIndiv início do período de cadastro de turmas de ensino individualizado pelos departamentos 
	 */
	public void setInicioCadastroTurmaEnsinoIndiv(
			Date inicioCadastroTurmaEnsinoIndiv) {
		this.inicioCadastroTurmaEnsinoIndiv = inicioCadastroTurmaEnsinoIndiv;
	}

	/** Retorna o fim do período de cadastro de turmas de ensino individualizado pelos departamentos 
	 * @return fim do período de cadastro de turmas de ensino individualizado pelos departamentos 
	 */
	public Date getFimCadastroTurmaEnsinoIndiv() {
		return fimCadastroTurmaEnsinoIndiv;
	}

	/** Seta o fim do período de cadastro de turmas de ensino individualizado pelos departamentos 
	 * @param fimCadastroTurmaEnsinoIndiv fim do período de cadastro de turmas de ensino individualizado pelos departamentos 
	 */
	public void setFimCadastroTurmaEnsinoIndiv(Date fimCadastroTurmaEnsinoIndiv) {
		this.fimCadastroTurmaEnsinoIndiv = fimCadastroTurmaEnsinoIndiv;
	}

	/** Retorna o ano que as solicitações de turma irão ter 
	 * @return Ano que as solicitações de turma irão ter 
	 */
	public Integer getAnoNovasTurmas() {
		return anoNovasTurmas;
	}

	/** Seta o ano que as solicitações de turma irão ter 
	 * @param anoNovasTurmas Ano que as solicitações de turma irão ter 
	 */
	public void setAnoNovasTurmas(Integer anoNovasTurmas) {
		this.anoNovasTurmas = anoNovasTurmas;
	}

	/** Retorna o período que as solicitações de novas turmas irão ter 
	 * @return Período que as solicitações de novas turmas irão ter 
	 */
	public Integer getPeriodoNovasTurmas() {
		return periodoNovasTurmas;
	}

	/** Seta o período que as solicitações de novas turmas irão ter 
	 * @param periodoNovasTurmas Período que as solicitações de novas turmas irão ter 
	 */
	public void setPeriodoNovasTurmas(Integer periodoNovasTurmas) {
		this.periodoNovasTurmas = periodoNovasTurmas;
	}

	/** Retorna o início do período de requerimento de FÉRIAS pelos alunos 
	 * @return início do período de requerimento de FÉRIAS pelos alunos 
	 */
	public Date getInicioRequerimentoTurmaFerias() {
		return inicioRequerimentoTurmaFerias;
	}

	/** Seta o início do período de requerimento de FÉRIAS pelos alunos 
	 * @param inicioRequerimentoTurmaFerias início do período de requerimento de FÉRIAS pelos alunos 
	 */
	public void setInicioRequerimentoTurmaFerias(Date inicioRequerimentoTurmaFerias) {
		this.inicioRequerimentoTurmaFerias = inicioRequerimentoTurmaFerias;
	}

	/** Retorna o fim do período de requerimento de FÉRIAS pelos alunos 
	 * @return fim do período de requerimento de FÉRIAS pelos alunos 
	 */
	public Date getFimRequerimentoTurmaFerias() {
		return fimRequerimentoTurmaFerias;
	}

	/** Seta o fim do período de requerimento de FÉRIAS pelos alunos 
	 * @param fimRequerimentoTurmaFerias fim do período de requerimento de FÉRIAS pelos alunos 
	 */
	public void setFimRequerimentoTurmaFerias(Date fimRequerimentoTurmaFerias) {
		this.fimRequerimentoTurmaFerias = fimRequerimentoTurmaFerias;
	}

	/** Indica se o nível de ensino do calendário é de graduação.
	 * @return
	 */
	@Transient
	public boolean isGraduacao(){
		return nivel == NivelEnsino.GRADUACAO;
	}

	/** Indica se o nível de ensino do calendário é de stricto sensu.
	 * @return
	 */
	@Transient
	public boolean isStricto(){
		return NivelEnsino.isAlgumNivelStricto(nivel);
	}

	/** Indica se o nível de ensino do calendário é de médio.
	 * @return
	 */
	@Transient
	public boolean isMedio(){
		return nivel == NivelEnsino.MEDIO;
	}
	
	/** Indica se a modalidade de ensino é a distância
	 * @return
	 */
	@Transient
	public boolean isADistancia(){
		return ( ( !isEmpty(modalidade) && getModalidade().isADistancia() )  || ( !isEmpty(curso) && getCurso().isADistancia() ) );
	}

	/** Retorna o ano anterior ao calendário.
	 * @return
	 */
	@Transient
	public int getAnoAnterior() {
		return (periodo == 1 ? ano - 1 : ano);
	}

	/** Retorna o período anterior ao calendário.
	 * @return
	 */
	@Transient
	public int getPeriodoAnterior() {
		return  (periodo == 1 ? periodo + 1 : periodo - 1);
	}

	/** Retorna uma string representado o ano/período anterior ao calendário.
	 * @return
	 */
	@Transient
	public String getAnoPeriodoAnterior() {
		return getAnoAnterior() + "." + getPeriodoAnterior();
	}

	/** Indica se a data atual está dentro do período de avaliação institucional.
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

