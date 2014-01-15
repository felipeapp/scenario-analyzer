package br.ufrn.sigaa.complexohospitalar.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.complexohospitalar.dao.MatriculaResidenciaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.dominio.MatriculaMov;

/**
 * Controller responsável por operações relacionadas à matrícula de alunos da residência.
 * 
 * @author bernardo
 *
 */
@Component("matriculaResidenciaMedica") @Scope("session")
public class MatriculaResidenciaMedicaMBean extends SigaaAbstractController {
	
	/** Atributo que Auxilia na busca */
	private Boolean[] boolDadosBusca = {false, false, false, false, false, false};

	/** Dados da turma para filtrar busca de turmas abertas */
	private Turma dadosBuscaTurma;

	/** Calendário utilizado para matrícula. */
	private CalendarioAcademico calendarioParaMatricula;
	
	/** Turma escolhida no fluxo de matrícula. */
	private Turma turmaEscolhida;

	/** Coleção de discentes carregados. Ao final do processo contém apenas discentes selecionados. */
	private Collection<? extends DiscenteAdapter> discentes;
	
	/** Coleção auxiliar de discentes carregados. */
	private Collection<? extends DiscenteAdapter> discentesCarregados;

	/** Coleção de turmas encontradas na busca. */
	private Collection<Turma> resultadoTurmasBuscadas;
	
	public MatriculaResidenciaMedicaMBean() {
		init();
	}

	/**
	 * Inicia os dados necessários para o correto funcionamento do bean. 
	 */
	private void init() {
		dadosBuscaTurma = new Turma();
		dadosBuscaTurma.setDisciplina(new ComponenteCurricular());
	}

	/**
	 * Inicia o processo de marícula em lote.
	 * 
	 * Invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String iniciarMatriculaEmLote() throws Exception {
		resetBean();
		init();
		
		return forwardSelecionarComponente();
	}
	
	/**
	 * Busca as turmas para posterior seleção.
	 * 
	 * Invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/matricula_lote/selecionar_componente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String buscarTurmas() throws ArqException {
		validarDadosBusca();
		
		if(hasErrors())
			return null;
		
		MatriculaResidenciaDao dao = getDAO(MatriculaResidenciaDao.class);
		ComponenteCurricular cc = dadosBuscaTurma.getDisciplina();
		String codigo = null;
		String nomeComponente = null;
		String nomeDocente = null;
		String horario = null;
		TipoComponenteCurricular tipo = new TipoComponenteCurricular();
		Unidade unidade = new Unidade();
		Integer ano = null;
		Integer periodo = null;
		
		if (boolDadosBusca[0])
			codigo = cc.getCodigo().trim();
		if (boolDadosBusca[1])
			nomeComponente = cc.getNome().trim();
		if (boolDadosBusca[2])
			horario = dadosBuscaTurma.getDescricaoHorario().trim();
		if (boolDadosBusca[3])
			nomeDocente = dadosBuscaTurma.getNomesDocentes().trim();
		if (boolDadosBusca[4])
			unidade = cc.getUnidade();
		if (boolDadosBusca[5]){
			ano = dadosBuscaTurma.getAno();
			periodo = dadosBuscaTurma.getPeriodo();
		} else {
			ano = getCalendarioParaMatricula().getAno();
			periodo = getCalendarioParaMatricula().getPeriodo();
		}
		
		Curso curso = null;
		Polo polo = null;

		if (codigo == null && nomeComponente == null && unidade == null && (ano == null || periodo == null)) {
			addMensagemErro("Por favor, escolha algum critério de busca");
		} else {
			try {
				// Definir tipos de turma a serem buscados, dependendo do tipo de matrícula
				Collection<Integer> tiposTurma = new ArrayList<Integer>(3);
				tiposTurma.add(Turma.REGULAR);
				
				if (unidade == null) 
					unidade = new Unidade();
				
				tipo = new TipoComponenteCurricular();
				
				resultadoTurmasBuscadas = dao.findAbertasByComponenteCurricular(null, null, nomeComponente,
						codigo, unidade.getId(), tipo.getId(), nomeDocente,  horario, curso, polo,
						ano , periodo, null, getNivelEnsino(), false, tiposTurma.toArray(new Integer[]{}) );
				
				if (isEmpty(resultadoTurmasBuscadas)) {
					addMensagemWarning("Não foram encontradas turmas abertas para os parâmetros de busca especificados.");
				}
				else {
					for (Turma t : resultadoTurmasBuscadas) {
						t.getDisciplina().getDetalhes().getId();
					}
				}
				
				getCurrentRequest().setAttribute("resultadoTurmasBuscadas", resultadoTurmasBuscadas);
			} catch (LimiteResultadosException lre) {
				addMensagemErro("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
			}
		}
		
		return null;
	}
	
	/**
	 * Valida os dados utilizados para realizar a busca de turmas. 
	 */
	private void validarDadosBusca() {
		if (boolDadosBusca[0] && isEmpty(dadosBuscaTurma.getDisciplina().getCodigo()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Código do Componente");
		if (boolDadosBusca[1] && isEmpty(dadosBuscaTurma.getDisciplina().getNome()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome do Componente");;
		if (boolDadosBusca[2] && isEmpty(dadosBuscaTurma.getDescricaoHorario()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Horário");
		if (boolDadosBusca[3] && isEmpty(dadosBuscaTurma.getNomesDocentes()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome do Docente");
		if (boolDadosBusca[4] && isEmpty(dadosBuscaTurma.getDisciplina().getUnidade()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Responsável");
	}

	/**
	 * Seleciona a turma para realizar a matrícula.
	 * 
	 * Invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/matricula_lote/selecionar_componente.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String selecionarTurma() throws DAOException {
		Integer turma = getParameterInt("turmaSelecionada");
		
		if(isEmpty(turma)) {
			addMensagemErro("Não foi possível recuperar a turma escolhida. Favor reinicie o processo.");
			return cancelar();
		}
		
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		CursoDao cursoDao = getDAO(CursoDao.class);
		
		try {
			Collection<Curso> cursos = new ArrayList<Curso>();
			int[] status = StatusDiscente.getValidos();
			
			turmaEscolhida = recuperarTurma(turma.intValue());
			turmaEscolhida.setMatricular(true);
			
			if(isUserInRole(SigaaPapeis.SECRETARIA_RESIDENCIA) && isPortalComplexoHospitalar())
				cursos.addAll( cursoDao.findAllCursosResidenciaByUnidadeResponsavel(
						getUsuarioLogado().getPermissao(SigaaPapeis.SECRETARIA_RESIDENCIA).iterator().next().getUnidadePapel().getId()));
			else
				cursos.addAll( cursoDao.findAllCursosResidenciaMedica() );
			
			discentes = discenteDao.findOtimizado(null, null, null, null, null, cursos, status, null, 0, getNivelEnsino(), false);
			
			discentesCarregados = new ArrayList<DiscenteAdapter>(discentes);
		} finally {
			discenteDao.close();
			cursoDao.close();
		}
		
		return forwardSelecionarDiscentes();
	}
	
	/**
	 * Recupera o objeto {@link Turma} selecionado dentro da coleção de turmas buscadas.
	 * 
	 * @param idTurma
	 * @return
	 */
	private Turma recuperarTurma(int idTurma) {
		Turma t = null;
		
		if(isNotEmpty(resultadoTurmasBuscadas)) {
			for (Turma turma : resultadoTurmasBuscadas) {
				if(turma.getId() == idTurma) {
					t = turma;
					break;
				}
			}
		}
		
		return t;
	}

	/**
	 * Seleciona os discentes escolhidos para realizar matrículas.
	 * 
	 * Invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/matricula_lote/selecionar_discentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionarDiscentes() throws ArqException {
		boolean hasDiscenteSelecionado = false; 

		for (DiscenteAdapter d : discentes) {
			if (d.getDiscente().getDiscente().isSelecionado()){
				hasDiscenteSelecionado = true;
				break;
			}
		}
		
		if (!hasDiscenteSelecionado)
			erros.addErro("Nenhum discente selecionado. É necessário ao menos um discente para concluir a operação.");
		
		if (hasOnlyErrors())
			return null;
		
		prepareMovimento(SigaaListaComando.MATRICULAR_ALUNOS_RESIDENCIA_EM_LOTE);

		for (Iterator<? extends DiscenteAdapter> it = discentes.iterator(); it.hasNext(); ) {
			DiscenteAdapter d = it.next();
			if ( !d.isSelecionado() )
				it.remove();
		}

		return forward("/complexo_hospitalar/matricula_lote/resumo.jsp");
	}
	
	/**
	 * Volta para a tela de escolha dos discentes, repopulando a lista de acordo com os dados encontrados.
	 * 
	 * Invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/matricula_lote/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarSelecaoDiscentes() {
		if(isEmpty(discentes))
			return cancelar();
		
		for (Iterator<? extends DiscenteAdapter> it = discentes.iterator(); it.hasNext(); ) {
			DiscenteAdapter d = it.next();
			
			for (Iterator<? extends DiscenteAdapter> itAux = discentesCarregados.iterator(); it.hasNext(); ) {
				DiscenteAdapter dAux = itAux.next();
				
				if(d.getId() == dAux.getId()) {
					dAux.setSelecionado(d.isSelecionado());
					break;
				}
			}
		}
		
		/** Repopulando lista de discentes. */
		discentes = new ArrayList<DiscenteAdapter>(discentesCarregados);
		
		return forwardSelecionarDiscentes();
	}
	
	/**
	 * Confirma o processo de matrícula, cadastrando as matrículas para os alunos escolhidos.
	 * 
	 * Invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/matricula_lote/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmarMatriculas() throws ArqException {
		try {
			MatriculaMov mov = new MatriculaMov();
			
			mov.setCodMovimento(SigaaListaComando.MATRICULAR_ALUNOS_RESIDENCIA_EM_LOTE);
			mov.setTurmas(Collections.singletonList(turmaEscolhida));
			mov.setDiscentes(discentes);

			execute(mov, getCurrentRequest());
			addMessage("Matrícula(s) cadastrada(s) com sucesso", TipoMensagemUFRN.INFORMATION);
		} catch(NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return cancelar();
	}

	/**
	 * Direciona o fluxo para a tela de seleção de componente.
	 * 
	 * Invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/matricula_lote/selecionar_discente.jsp</li>
	 * <li>/sigaa.war/complexo_hospitalar/matricula_lote/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String forwardSelecionarComponente() {
		return forward("/complexo_hospitalar/matricula_lote/selecionar_componente.jsp");
	}

	/**
	 * Direciona o fluxo para a tela de seleção de discentes.
	 * 
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
	public String forwardSelecionarDiscentes() {
		return forward("/complexo_hospitalar/matricula_lote/selecionar_discentes.jsp");
	}
	
	public CalendarioAcademico getCalendarioParaMatricula() {
		return calendarioParaMatricula == null ? getCalendarioVigente() : calendarioParaMatricula;
	}

	public Boolean[] getBoolDadosBusca() {
		return boolDadosBusca;
	}

	public void setBoolDadosBusca(Boolean[] boolDadosBusca) {
		this.boolDadosBusca = boolDadosBusca;
	}

	public Turma getDadosBuscaTurma() {
		return dadosBuscaTurma;
	}

	public void setDadosBuscaTurma(Turma dadosBuscaTurma) {
		this.dadosBuscaTurma = dadosBuscaTurma;
	}

	public Turma getTurmaEscolhida() {
		return turmaEscolhida;
	}

	public void setTurmaEscolhida(Turma turmaEscolhida) {
		this.turmaEscolhida = turmaEscolhida;
	}

	public Collection<? extends DiscenteAdapter> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<? extends DiscenteAdapter> discentes) {
		this.discentes = discentes;
	}

	public Collection<Turma> getResultadoTurmasBuscadas() {
		return resultadoTurmasBuscadas;
	}

	public void setResultadoTurmasBuscadas(Collection<Turma> resultadoTurmasBuscadas) {
		this.resultadoTurmasBuscadas = resultadoTurmasBuscadas;
	}
	
}
