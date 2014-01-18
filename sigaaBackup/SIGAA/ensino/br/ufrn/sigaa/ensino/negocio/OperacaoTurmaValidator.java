package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.graduacao.ReservaCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaHelper;
import br.ufrn.sigaa.ava.dao.PermissaoAvaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;

/**
 * Classe auxiliar utilizada para verificar se pode ser realizada uma operação
 * em uma turma.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class OperacaoTurmaValidator {

	/**
	 * Indica se o usuário tem permissão para exibir o processamento da
	 * matrícula da turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isExibeProcessamentoMatricula(Turma turma) {
		return !turma.isEad() && turma.isGraduacao()
				&& !getAcesso().isCoordenadorPolo()
				&& turma.getProcessada() != null
				&& turma.getProcessada().booleanValue();
	}

	/**
	 * Indica se o usuário tem permissão para exibir o processamento da
	 * rematrícula da turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isExibeProcessamentoRematricula(Turma turma) {
		return !turma.isEad() && turma.isGraduacao()
				&& !getAcesso().isCoordenadorPolo()
				&& turma.getProcessadaRematricula() != null
				&& turma.getProcessadaRematricula().booleanValue();
	}

	/**
	 * Indica se o usuário tem permissão para adicionar uma reserva sem
	 * solicitação.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteAdicionarReservaSemSolicitacao(Turma turma) {
		return turma.isGraduacao() && 
			((getAcesso().isAdministradorDAE() || getAcesso().isDae()) && (turma.isAberta()));
	}

	/**
	 * Indica se o usuário tem permissão para alterar as informações da turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteAlterar(Turma turma) {
		switch (turma.getDisciplina().getNivel()) {
		case NivelEnsino.TECNICO:
			return SigaaSubsistemas.TECNICO.equals(getSubSistema()) && turma.isAberta() && turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
		case NivelEnsino.FORMACAO_COMPLEMENTAR:
			return SigaaSubsistemas.FORMACAO_COMPLEMENTAR.equals(getSubSistema()) && turma.isAberta() && turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
		case NivelEnsino.STRICTO:
			if (isAcessoCoordenacaoStricto() && isPortalCoordenadorStricto()) {
				return isMesmaUnidadeStrictoTurma(turma);
			}
			if (getAcesso().isPpg())
				return true;
			break;
		case NivelEnsino.LATO:
			
			// Valida nível do componente selecionado
			if (turma.getDisciplina().getNivel() != NivelEnsino.LATO)
				return false;
			
			// se for usuário da pro reitoria de pós-graduação
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO))
				return true;
			
			// se a turma estiver aberta e (se for coordenador de curso stricto sensu ou secretário) e o curso for do mesmo programa
			if (turma.isAberta() && ( getAcesso().isCoordenadorCursoLato() || getAcesso().isSecretarioLato()) ) 
				return getCursoAtualCoordenacao() != null && turma.getCurso().getId() == getCursoAtualCoordenacao().getId();
		
			break;
		case NivelEnsino.GRADUACAO:
				// ser for administrador do DAE
				if (isModuloGraduacao() && getAcesso().isAdministradorDAE())
					return true;
				
				// se for usuário do DAE ou da SAE e a turma estiver aberta
				if (getAcesso().isDae() || getAcesso().isEad())
					return turma.isAberta();
				
				// se for chefe de departamento ou secretário e a turma for da mesma unidade e a turma estiver aberta
				if (getAcesso().isChefeDepartamento() || getAcesso().isSecretarioDepartamento())
					return turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId() && turma.isAberta();
				
				// se for Coordenador de Curso ou secretário e a turma for da mesma unidade e a turma estiver aberta
				if (isAcessoCoordenacaoGraduacao())
					return getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().getUnidadeCoordenacao() != null 
						&& turma.getDisciplina().getUnidade().getId() == getCursoAtualCoordenacao().getUnidadeCoordenacao().getId() && turma.isAberta();				
				
				// se for coordenador de curso de probásica e o curso for o que coordena e a turma estiver aberta
				if (getAcesso().isCoordenadorCursoProbasica()) 
				    return getCursoAtualCoordenacao() != null && turma.getCurso().getId() == getCursoAtualCoordenacao().getId() && turma.isAberta();
			break;
		case NivelEnsino.RESIDENCIA:
			if (turma.getDisciplina().getNivel() != NivelEnsino.RESIDENCIA)
				return false;
			
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA))
				return true;
			
			break;
		}
		return false;
	}

	/**
	 * Indica se o usuário tem permissão para realizar algumas ajustes simples na turma,
	 * como aumento do número de vagas e reservas para matrícula extraordinária. 
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteAjustarTurma(Turma turma) {
		
		if (turma.getDisciplina().getNivel() == NivelEnsino.GRADUACAO) {
				// ser for administrador do DAE
				if (isModuloGraduacao() && getAcesso().isAdministradorDAE())
					return true;
				
				// se for usuário do DAE e a turma estiver aberta
				if (getAcesso().isDae())
					return turma.isAberta();
				
				// se for chefe de departamento ou secretário e a turma for da mesma unidade e a turma estiver aberta
				if (getAcesso().isChefeDepartamento() || getAcesso().isSecretarioDepartamento())
					return turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId() && turma.isAberta();
				
				// se for Coordenador de Curso ou secretário e a turma for da mesma unidade e a turma estiver aberta
				if (isAcessoCoordenacaoGraduacao())
					return getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().getUnidadeCoordenacao() != null 
						&& turma.getDisciplina().getUnidade().getId() == getCursoAtualCoordenacao().getUnidadeCoordenacao().getId() && turma.isAberta();				
				
				// se for coordenador de curso de probásica e o curso for o que coordena e a turma estiver aberta
				if (getAcesso().isCoordenadorCursoProbasica()) 
				    return getCursoAtualCoordenacao() != null && turma.getCurso().getId() == getCursoAtualCoordenacao().getId() && turma.isAberta();
			
		}
		return false;
	}
	
	/**
	 * Indica se o usuário tem permissão para cadastrar uma notícia para a
	 * turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteCadastrarNoticia(Turma turma) {
		
		if (	((getAcesso().isChefeDepartamento() || getAcesso().isSecretarioDepartamento()) 
				&& turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId()) 
				|| verificarNivelTecnico(turma) 
				|| verificarNivelFormacaoComplementar(turma))
			return true;
		
		switch (turma.getDisciplina().getNivel()) {
		case NivelEnsino.GRADUACAO:
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE)) {
				return true;
			}
						
			if (isAcessoCoordenacaoGraduacao()) {
				if (getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().getUnidadeCoordenacao() != null 
						&& getCursoAtualCoordenacao().getUnidadeCoordenacao().getId() == turma.getDisciplina().getUnidade().getId())
					return true;
			}
			break;
		case NivelEnsino.STRICTO:
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO))
				return true;
			
			if (isAcessoCoordenacaoStricto() && isPortalCoordenadorStricto())
				return isMesmaUnidadeStrictoTurma(turma);
			break;
		}
		return false;
	}
	
	/**
	 * Verifica se o usuário tem permissão para Alterar o Status de Matrícula dos discentes da Turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteAlterarStatusMatriculaTurma(Turma turma){
		return (getAcesso().isDae() || getAcesso().isAdministradorDAE()) && turma.isGraduacao();		
	}

	/**
	 * Verifica se o usuário é chefe de departamento.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isChefeDepartamento(Turma turma){
		return getAcesso().isChefeDepartamento();		
	}
	
	/**
	 * Indica se o usuário tem permissão para consolidar a turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteConsolidar(Turma turma) {
		switch (turma.getDisciplina().getNivel()) {
		case NivelEnsino.GRADUACAO:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && turma.isAberta() && turma.getQtdMatriculados() > 0;
			
		case NivelEnsino.STRICTO:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO) && turma.isAberta() && turma.getQtdMatriculados() > 0;
		
		case NivelEnsino.TECNICO:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_TECNICO) && turma.isAberta() && turma.getQtdMatriculados() > 0;
			
		case NivelEnsino.FORMACAO_COMPLEMENTAR:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR) && turma.isAberta() && turma.getQtdMatriculados() > 0;
			
		case NivelEnsino.LATO:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_LATO) && turma.isAberta() && turma.getQtdMatriculados() > 0;
		}
		
		return false;
	}

	/**
	 * Indica se o usuário tem permissão para fechar a turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteFecharTurma(Turma turma) {
		getUsuarioLogado().getUnidade();
		turma.getDisciplina().getUnidade();
		return (getAcesso().isAdministradorDAE() || getAcesso().isDae() || isTurmaDeDepartamentoDoChefeLogado(turma))
			&& turma.isAberta() && turma.isGraduacao();
		
	}

	/**
	 * Verifica se o usuário logado é um chefe de departamento e se a turma
	 * passada como argumento pertence ao departamento ao sob sua chefia.
	 * 
	 * @param t
	 * @return
	 */
	private static boolean isTurmaDeDepartamentoDoChefeLogado(Turma t) {
		try {
			Unidade undChefe = getUsuarioLogado().getUnidade();
			Unidade undDaTurma = t.getDisciplina().getUnidade();		
			return getAcesso().isChefeDepartamento() && undChefe.getId() == undDaTurma.getId();
		} catch (NullPointerException e) {
			return false;
		}
	}

	/**
	 * Indica se o usuário tem permissão para gerar o diário de classe da turma.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isPermiteGerarDiarioClasse(Turma turma) throws DAOException {
		switch (turma.getDisciplina().getNivel()) {
		case NivelEnsino.GRADUACAO:
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE))
				return true;
		
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE))
				return getUnidadeResponsabilidade() != null && turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
				
		case NivelEnsino.STRICTO:
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO))
				return true;
			
			if (isAcessoCoordenacaoStricto() && isPortalCoordenadorStricto())
				return isMesmaUnidadeStrictoTurma(turma);
			
		case NivelEnsino.TECNICO :
			return turma.isTecnico() && getAcesso().isTecnico() && turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
			
		case NivelEnsino.FORMACAO_COMPLEMENTAR :
			return turma.isFormacaoComplementar() && getAcesso().isFormacaoComplementar() && turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
			
		case NivelEnsino.LATO:
			
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_STRICTO, SigaaPapeis.GESTOR_LATO))
				return true;
			
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO)) 
				return getCursoAtualCoordenacao() != null && turma.getCurso().getId() == getCursoAtualCoordenacao().getId();
		}
		
		return false;
	}

	/**
	 * Indica se o usuário tem permissão para gerar o diário da turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteGerarDiarioTurma(Turma turma) {
		return (turma.isConsolidada() && getAcesso().isEad() && turma.isEad());
	}

	/**
	 * Indica se o usuário tem permissão para gerar a lista de frequência da
	 * turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteGerarListaFrequencia(Turma turma) {
		switch (turma.getDisciplina().getNivel()) {
		case NivelEnsino.GRADUACAO:
			return getAcesso().isDae() || SigaaSubsistemas.SEDIS.equals(getSubSistema())	|| getAcesso().isCoordenadorPolo();
		case NivelEnsino.STRICTO:
			return getAcesso().isCoordenadorCursoStricto() || getAcesso().isSecretariaPosGraduacao();
		case NivelEnsino.LATO:
			return (getAcesso().isCoordenadorCursoLato() || getAcesso().isSecretarioLato()) && turma.isLato();
		}
		return false;
	}

	/**
	 * Indica se o usuário tem permissão para gerar o relatório de notas dos
	 * tutores de ensino à distância.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteGerarNotasTutores(Turma turma) {
		return (getAcesso().isEad() || getAcesso().isCoordenadorCursoEad()) && turma.isEad();
	}

	/**
	 * Indica se o usuário tem permissão para gerar a planilha de notas do
	 * discente.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteGerarPlanilhaNotas(Turma turma) {
		if (isAcessoCoordenacaoGraduacao() || getAcesso().isSecretarioDepartamento())
			return getCursoAtualCoordenacao() != null 
				&& (getCursoAtualCoordenacao().getUnidadeCoordenacao() != null && turma.getDisciplina().getUnidade().getId() == getCursoAtualCoordenacao().getUnidadeCoordenacao().getId() && turma.isConsolidada()
				|| turma.contemReservaCurso(getCursoAtualCoordenacao()));
		return (isModuloGraduacao()
				&& getAcesso().isDae() && turma.isConsolidada()
				&& turma.isGraduacao() || ((getAcesso().isTutorEad()
				|| getAcesso().isDae() || getAcesso().isChefeDepartamento()
				|| getAcesso()
				.isEad())
				&& turma.isConsolidada() && turma.isGraduacao()));
	}

	/**
	 * Indica se o usuário tem permissão para listar os discentes da turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteListarAlunos(Turma turma) {
		return getAcesso().isPpg()
				|| getAcesso().isDae()
				|| ((getAcesso().isChefeDepartamento() || getAcesso()
						.isSecretarioDepartamento()) && turma.getDisciplina()
						.getUnidade().getId() == getAcesso().getUsuario()
						.getUnidade().getId())
				|| ((getAcesso().isCoordenadorCursoStricto() || getAcesso()
						.isSecretariaPosGraduacao()) && turma.getDisciplina()
						.getUnidade().getId() == getUsuarioLogado()
						.getUnidade().getId()) 
				|| getAcesso().isTecnico() 
				|| getAcesso().isFormacaoComplementar()
				|| getAcesso().isDocente()
				|| getAcesso().isSecretarioGraduacao();
	}

	/**
	 * Indica se o usuário tem permissão para imprimir o relatório de lista de
	 * discentes da turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteListarAlunosImpressao(Turma turma) {
		return (getAcesso().isTecnico() && isModuloTecnico()) || getAcesso().isFormacaoComplementar();
	}

	/**
	 * Indica se o usuário tem permissão para reabrir uma turma consolidada.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteReabrirTurma(Turma turma) {
		switch (turma.getDisciplina().getNivel()) {
		case NivelEnsino.GRADUACAO:
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && turma.isConsolidada())
				return true;
			else if (getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO) && turma.isConsolidada() && getCalendarioVigente().isPeriodoConsolidacao())
				return true;
			
		case NivelEnsino.LATO:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_LATO) && turma.isConsolidada();
			
		case NivelEnsino.STRICTO:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.PPG) && turma.isConsolidada();

		case NivelEnsino.TECNICO:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_TECNICO) && turma.isConsolidada();
			
		case NivelEnsino.FORMACAO_COMPLEMENTAR:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR) && turma.isConsolidada();
			
		case NivelEnsino.MEDIO:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO) && turma.isConsolidada();	
			
		default:
			return false;
		}
	}

	/**
	 * Indica se o usuário tem permissão para remover uma turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteRemoverTurma(Turma turma) {
		switch (turma.getDisciplina().getNivel()) {
		case NivelEnsino.GRADUACAO:
			
			if (isAcessoDepartamento()) {
				if (turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId())
					return true;
			}
			
			if (isAcessoCoordenacaoGraduacao()) {
				if (getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao() != null 
						&& getCursoAtualCoordenacao().getUnidadeCoordenacao() != null && turma.getDisciplina().getUnidade().getId() == getCursoAtualCoordenacao().getUnidadeCoordenacao().getId())
					return true;
			}
			              
			if (getAcesso().isCoordenadorCursoProbasica() && isPortalCoordenadorGraduacao()) {
				if (getCursoAtualCoordenacao() != null && turma.getCurso()!= null && turma.getCurso().getId() == getCursoAtualCoordenacao().getId())
					return true;
			}
			
			if (getAcesso().isCoordenacaoProbasica())
				return true;
			
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE))
				return true;
			
			if (getAcesso().isEad())
				return true;
			
			return false;
		case NivelEnsino.LATO:
			
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO))
				return true;
			
			if (!turma.isAberta() || turma.getQtdMatriculados() > 0)
				return false;
			
			if (getAcesso().isCoordenadorCursoLato() && ( getCursoAtualCoordenacao() != null && (getCursoAtualCoordenacao().getId() == turma.getCurso().getId() 
							|| turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId())))
				return true;
			
			if (turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId()) {
				return true;
			}
			
			return false;
		case NivelEnsino.STRICTO:
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.PPG))
				return true;
			
			if (isAcessoCoordenacaoStricto() && isPortalCoordenadorStricto())
				return isMesmaUnidadeStrictoTurma(turma);
			
		case NivelEnsino.TECNICO:
			return SigaaSubsistemas.TECNICO.equals(getSubSistema())
			&& turma.isAberta()
			&& turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
		case NivelEnsino.FORMACAO_COMPLEMENTAR:
			return SigaaSubsistemas.FORMACAO_COMPLEMENTAR.equals(getSubSistema())
					&& turma.isAberta()
					&& turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
		case NivelEnsino.RESIDENCIA:
			return getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA)
					&& turma.isAberta();
		default:
			return false;
		}
	}

	/**
	 * Indica se o usuário tem permissão para visualizar turma.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteVisualizarTurma(Turma turma) {
		return true;
	}

	/**
	 * Indica se o usuário tem permissão para visualizar turma virtual.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isPermiteVisualizarTurmaVirtual(Turma turma) throws DAOException {
		if(verificarCoordenadores(turma))
			return true;
		
		PermissaoAvaDao dao = null;
		try {
			dao = DAOFactory.getInstance().getDAO(PermissaoAvaDao.class);
			boolean possuiPermissao = dao.possuiPermissaoTurma(getUsuarioLogado().getPessoa(), turma);
			
			return (getAcesso().isChefeDepartamento() && turma.getDisciplina()
					.getUnidade().getId() == getAcesso().getUsuario().getUnidade()
					.getId())
					|| possuiPermissao
					|| getAcesso().isPpg()
					|| getAcesso().isDae()
					|| verificarNivelTecnico(turma)
					|| verificarNivelFormacaoComplementar(turma)
					|| getUsuarioLogado().getDiscenteAtivo() != null;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Indica se o usuário tem permissão para visualizar a agenda da turma.
	 */
	public static boolean isPermiteVerAgenda(Turma turma){
		return true;
	}
	
	/**
	 * Verifica se o usuário logado tem acesso ao módulo técnico e se a turma passada pertence a sua escola.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean verificarNivelTecnico(Turma turma) {
		return (getAcesso().isTecnico() || getAcesso().isCoordenadorCursoTecnico()) && turma.isTecnico() && turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
	}
	
	/**
	 * Verifica se o usuário logado tem acesso ao módulo escolas especializadas e se a turma passada pertence a sua escola.
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean verificarNivelFormacaoComplementar(Turma turma) {
		return getAcesso().isFormacaoComplementar() && turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
	}

	/**
	 * Verifica se o usuário logado é coordenador, se está no seu respectivo portal de coordenação e tem permissão de acesso à turma virtual. <br /><br />
	 * Para turmas de <b>Graduação</b>, será retornado <code>true</code> se existir reservas para o curso coordenado.<br />
	 * Para os casos <b>Lato Sensu</b>, será retornado <code>true</code> se a turma for do curso coordenado.<br />
	 * Para os casos <b>Stricto Sensu</b>, será retornado <code>true</code> se o programa relacionado a disciplina da turma for igual ao programa coordenado.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public static boolean verificarCoordenadores(Turma turma) throws DAOException {
		if (getAcesso().isCoordenadorCursoGrad() && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) {
			ReservaCursoDao dao = DAOFactory.getInstance().getDAO(ReservaCursoDao.class);
			try {
				Collection<ReservaCurso> reservas = dao.findByExactField(ReservaCurso.class, "turma", turma.getId());
				if(reservas != null && reservas.size() > 0) {
					for (ReservaCurso reserva : reservas) {
						if(!isEmpty(reserva.getMatrizCurricular()) && reserva.getMatrizCurricular().getCurso().getId() == getCursoAtualCoordenacao().getId())
							return true;
					}
				}
			} finally {
				dao.close();
			}
		}
		if (getAcesso().isCoordenadorCursoLato() && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
			if ( turma.getDisciplina().isResidencia() )
				return false;
			else
				return getCursoAtualCoordenacao().getId() == turma.getCurso().getId();
		}
		if ((getAcesso().isCoordenadorCursoStricto() || getAcesso().isSecretariaPosGraduacao()) && (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)) || getSubSistema().equals(SigaaSubsistemas.PORTAL_TURMA)) {
			return isMesmaUnidadeStrictoTurma(turma);
		}
		
		if (verificarNivelTecnico(turma)){
			return true;
		}
		
		
		return false;
	}
	

	/**
	 * Verifica se o usuário logado é coordenador ou secretário, se está no seu respectivo portal de coordenação e tem permissão de acesso à turma virtual. <br /><br />
	 * Para turmas de <b>Graduação</b>, será retornado <code>true</code> se existir reservas para o curso coordenado.<br />
	 * Para os casos <b>Lato Sensu</b>, será retornado <code>true</code> se a turma for do curso coordenado.<br />
	 * Para os casos <b>Stricto Sensu</b>, será retornado <code>true</code> se o programa relacionado a disciplina da turma for igual ao programa coordenado.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public static boolean verificarCoordenacao(Turma turma) throws DAOException {
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) {
			ReservaCursoDao dao = DAOFactory.getInstance().getDAO(ReservaCursoDao.class);
			try {
				Collection<ReservaCurso> reservas = dao.findByExactField(ReservaCurso.class, "turma", turma.getId());
				if(reservas != null && reservas.size() > 0) {
					for (ReservaCurso reserva : reservas) {
						if(!isEmpty(reserva.getMatrizCurricular()) && reserva.getMatrizCurricular().getCurso().getId() == getCursoAtualCoordenacao().getId())
							return true;
					}
				}
			} finally {
				dao.close();
			}
		}
		if (getAcesso().isCoordenadorCursoLato() && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
			if ( turma.getDisciplina().isResidencia() )
				return false;
			else
				return getCursoAtualCoordenacao().getId() == turma.getCurso().getId();
		}
		if ((getAcesso().isCoordenadorCursoStricto() || getAcesso().isSecretariaPosGraduacao()) && (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)) || getSubSistema().equals(SigaaSubsistemas.PORTAL_TURMA)) {
			return isMesmaUnidadeStrictoTurma(turma);
		}
		
		return false;
	}
	
	/**
	 * Verifica se a unidade do coordenador ou secretário de coordenação de um programa Stricto é igual a da turma passada.
	 * 
	 * @param turma
	 * @return
	 */
	private static boolean isMesmaUnidadeStrictoTurma(Turma turma) {
		Unidade unidade = getProgramaStricto() != null ? getProgramaStricto() : getUsuarioLogado().getVinculoAtivo().getUnidade();
		return turma.getDisciplina().getUnidade() == null ? false : turma.getDisciplina().getUnidade().getId() == unidade.getId();
	}
	
	/**
	 * Indica se o usuário tem permissão para enviar e-mail para os participantes da turma.
	 * @throws DAOException 
	 */
	public static boolean isPermiteEnviarEmail(Turma turma) throws DAOException{
		if (getAcesso().isChefeDepartamento()  && turma.getDisciplina().getUnidade().getId() == getAcesso().getUsuario().getUnidade().getId()
				|| verificarCoordenacao(turma)
				|| verificarNivelTecnico(turma) 
				|| verificarNivelFormacaoComplementar(turma))
				return true;
		
		return false;
	}

	/**
	 * Indica se o usuário tem permissão para visualizar Notas de Alunos de uma Turma.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isPermiteListarNotasAlunos(Turma turma)  {
		switch (turma.getDisciplina().getNivel()) {
		case NivelEnsino.GRADUACAO:
			if (getAcesso().isCoordenadorCursoEad() || getAcesso().isTutorEad()  )
				return turma.isEad();
			else
				return getAcesso().isCdp() || getAcesso().isAdministradorDAE() || getAcesso().isDae();
		case NivelEnsino.LATO:
			if(getAcesso().isCoordenadorCursoLato() || getAcesso().isSecretarioLato()) {
				if(turma.getCurso() != null && getCursoAtualCoordenacao() != null)
					return turma.getCurso().getId() == getCursoAtualCoordenacao().getId();
			}
			return false;
		default:
			return false;
		}
	}
	
	/**
	 * Retorna os responsáveis por uma unidade. Método auxiliar.
	 * 
	 * @return
	 */
	public static Unidade getUnidadeResponsabilidade() {
		Unidade unidade = null;
		if ( isPortalCoordenadorGraduacao() && isAcessoCoordenacaoGraduacao() )
			unidade = getCursoAtualCoordenacao().getUnidadeCoordenacao();
		else
			unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
		
		return unidade;
	}

	/**
	 * Acesso ao módulo de graduação
	 * @return
	 */
	private static boolean isModuloGraduacao() {
		return SigaaSubsistemas.GRADUACAO.equals(getSubSistema());
	}
	
	/**
	 * Verifica se esta no módulo técnico
	 * @return
	 */
	private static boolean isModuloTecnico() {
		return SigaaSubsistemas.TECNICO.equals(getSubSistema());
	}

	/**
	 * Acesso ao Departamento
	 * 
	 * @return
	 */
	private static boolean isAcessoDepartamento() {
		return getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO);
	}

	/**
	 * Subsistema atual é o Portal Docente
	 * 
	 * @return
	 */
	private static boolean isPortalDocente() {
		return SigaaSubsistemas.PORTAL_DOCENTE.equals(getSubSistema());
	}

	/**
	 * Possui acesso as opereçãoes do portal da coordenação de graduação
	 * 
	 * @return
	 */
	private static boolean isAcessoCoordenacaoGraduacao() {
		return getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
	}

	/**
	 * Possui acesso as opereçãoes do portal da coordenação de stricto
	 * 
	 * @return
	 */
	private static boolean isAcessoCoordenacaoStricto() {
		return getUsuarioLogado().isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
	}		
	
	/**
	 * Subsistema atual é o Portal da Coordenação Graduação
	 * 
	 * @return
	 */
	private static boolean isPortalCoordenadorGraduacao() {
		return SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema());
	}	
	
	/**
	 * 
	 * Subsistema atual é o Portal da Coordenação Stricto
	 * 
	 * @return
	 */
	private static boolean isPortalCoordenadorStricto() {
		return SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema());
	}

	/**
	 * Retorna a entidade DadosAcesso, que contém as informações da sessão do
	 * usuário, com informações de contexto
	 * 
	 * @return
	 */
	private static DadosAcesso getAcesso() {
		return (DadosAcesso) getCurrentSession().getAttribute("acesso");
	}

	/**
	 * Retorna Curso atual manipulado pelo coordenador logado
	 * 
	 * @return
	 */
	private static Curso getCursoAtualCoordenacao() {
		return SigaaHelper.getCursoAtualCoordenacao();
	}

	/**
	 * Retorna o subsistema que está sendo utilizado pelo usuário.
	 */
	private static SubSistema getSubSistema() {
		SubSistema subsistema = null;
		if (getCurrentSession().getAttribute("subsistema") instanceof String) {
			GenericSharedDBDao genDAO = null;
			String idSubsistema = (String) getCurrentSession().getAttribute(
					"subsistema");
			try {
				genDAO = new GenericSharedDBDao();
				subsistema = genDAO.findByPrimaryKey(Integer
						.parseInt(idSubsistema), SubSistema.class);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (genDAO != null)
					genDAO.close();
			}
		} else {
			subsistema = (SubSistema) getCurrentSession().getAttribute(
					"subsistema");
		}
		return subsistema;
	}

	/**
	 * Retorna o Programa de Pós que o Usuário logado coordena
	 * 
	 * @return
	 */
	private static Unidade getProgramaStricto() {
		if (getCurrentSession().getAttribute(
				SigaaAbstractController.PROGRAMA_ATUAL) != null)
			return (Unidade) getCurrentSession().getAttribute(
					SigaaAbstractController.PROGRAMA_ATUAL);
		else
			return null;
	}

	/**
	 * Retorna o calendário vigente para as operações
	 * 
	 * @return
	 * @throws DAOException
	 */
	private static CalendarioAcademico getCalendarioVigente() {
		CalendarioAcademico cal = (CalendarioAcademico) getCurrentSession()
				.getAttribute("calendarioAcademico");
		if (cal == null)
			try {
				cal = CalendarioAcademicoHelper
						.getCalendario(getUsuarioLogado());
			} catch (Exception e) {
				e.printStackTrace();
			}

		if (cal == null) {
			throw new RuntimeNegocioException(
					"É necessário que o calendário acadêmico esteja definido para realizar esta operação.");
		}
		return cal;
	}

	/**
	 * Retorna o usuário logado.
	 * 
	 * @param <V>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <V extends Usuario> V getUsuarioLogado() {
		return (V) getCurrentRequest().getSession().getAttribute("usuario");
	}

	/**
	 * Possibilita o acesso ao HttpSession.
	 */
	private static HttpSession getCurrentSession() {
		return getCurrentRequest().getSession(true);
	}

	/**
	 * Possibilita o acesso ao HttpServletRequest.
	 */
	private static HttpServletRequest getCurrentRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}

	/**
	 * Acessa o external context do JavaServer Faces
	 **/
	private static ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}
	
	/**
	 * Indica se o usuário tem permissão para duplicar a turma, isto é, cadastrar uma nova turma
	 * copiando todas informações de outra turma. 
	 * 
	 * @param turma
	 * @return
	 */
	public static boolean isPermiteDuplicarTurma(Turma turma) {
		if (!turma.isAberta()) return false;
		if (turma.getDisciplina().getNivel() == NivelEnsino.GRADUACAO) {
			// ser for administrador do DAE
			if (isModuloGraduacao() && (getAcesso().isAdministradorDAE() || getAcesso().isDae()))
				return true;
			// se for chefe de departamento ou secretário e a turma for da mesma unidade e estiver no período de ajustes
			if ((getAcesso().isChefeDepartamento() || getAcesso().isSecretarioDepartamento()) && getCalendarioVigente().isPeriodoAjustesTurmas())
				return turma.getDisciplina().getUnidade().getId() == getUnidadeResponsabilidade().getId();
		}
		return false;
	}
}