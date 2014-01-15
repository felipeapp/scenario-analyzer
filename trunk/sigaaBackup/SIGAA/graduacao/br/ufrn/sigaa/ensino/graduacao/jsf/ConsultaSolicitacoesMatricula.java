/*
c * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/01/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoSolicitacaoMatricula;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

/**
 *
 * Realiza consulta sobre as solicitações de matrículas dos alunos
 * @author Andre Dantas
 *
 */
@Component("consultaSolicitacoes")
@Scope("session")
public class ConsultaSolicitacoesMatricula extends SigaaAbstractController<Object> implements OperadorDiscente {

	/** Armazena o discente utilizado */
	private DiscenteAdapter discente;
	/** Armazena uma coleção de solicitações de matrícula */
	private Collection<SolicitacaoMatricula> solicitacoes;
	/** Armazena o número de solicitações */
	private Integer numeroSolicitacao;

	/**
	 * Redireciona para a tela de solicitações de matrículas.
	 * JSP: Não invocado por JSP.
	 * @return
	 */
	public String telaSolicitacoes() {
		return forward("/graduacao/discente/solicitacoes_matricula.jsp");
	}

	/**
	 * Inicia a operação de consulta sobre solicitações de matrículas.
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_CENTRO,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CONSULTAR_SOLICITACOES_MATRICULAS);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Seleciona o discente e redireciona para a tela de solicitações de matrícula.
	 * 
	 */
	public String selecionaDiscente() throws ArqException {
		try {
			SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);
			CalendarioAcademico cal = getCalendarioVigente();
			if (isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.GESTOR_TECNICO)) {
				solicitacoes = dao.findTodasByDiscenteAnoPeriodo(discente, cal.getAno(), cal.getPeriodo(),
					SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO,SolicitacaoMatricula.VISTA, SolicitacaoMatricula.ATENDIDA);
			} else {
				solicitacoes = dao.findByDiscenteAnoPeriodo(discente, cal.getAno(), cal.getPeriodo(), null,
						SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO,SolicitacaoMatricula.VISTA,SolicitacaoMatricula.NEGADA);
			}

			if (isEmpty(solicitacoes)) {
				addMensagemErro("O discente não tentou se matricular em nenhuma turma no período atual");
				return null;
			}
			if(getSubSistema().equals(SigaaSubsistemas.TECNICO))
				discente = getGenericDAO().findByPrimaryKey(discente.getId(), DiscenteTecnico.class);
			else
				discente = getDAO(DiscenteDao.class).findByPK(discente.getId());
			numeroSolicitacao = solicitacoes.iterator().next().getNumeroSolicitacao();
			prepareMovimento(SigaaListaComando.ANULAR_SOLICITACAO_MATRICULA);
			return telaSolicitacoes();
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
	}

	/**
	 * Anula as solicitações de matrícula informadas pelo usuário.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/discente/solicitacao_matricula.jsp</li>
	 *	</ul> 
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String anularMarcadas() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO);
		String[] anuladosString = getCurrentRequest().getParameterValues("anulados");
		ArrayList<Integer> anuladosIds = new ArrayList<Integer>(0);
		if (!isEmpty(anuladosString)) {
			for (String s : anuladosString) {
				anuladosIds.add(new Integer(s));
			}
			for (SolicitacaoMatricula sol : solicitacoes) {
				if (anuladosIds.contains(sol.getId())) {
					sol.setAnulado(true);
					sol.setObservacaoAnulacao(getParameter("motivo_"+sol.getId()));
				} else {
					sol.setAnulado(false);
				}

			}
		} else {
			addMensagemErro("É necessário selecionar pelo menos uma solicitação.");
			return null;
		}
		
		validarAnulacoes();
		
		if (hasErrors())
			return null;

		return efetuarAnulacao(true);
	}

	/**
	 * Anula as solicitações de matrícula de acordo com os parametros da classe.
	 * <br />
	 * Método não chamado por JSPs
	 * 
	 * @param redirect
	 * @return
	 * @throws SegurancaException
	 */
	public String efetuarAnulacao(boolean redirect) throws SegurancaException {
		MovimentoSolicitacaoMatricula mov = new MovimentoSolicitacaoMatricula();
		mov.setCodMovimento(SigaaListaComando.ANULAR_SOLICITACAO_MATRICULA);
		mov.setSolicitacoes(solicitacoes);
		mov.setDiscente(getDiscente());
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			if(redirect)
				addMessage("As solicitações marcadas foram anuladas e o discente foi notificado por e-mail", TipoMensagemUFRN.INFORMATION);
			else
				addMessage("As solicitações da turma foram anuladas e os discentes foram notificados por e-mail", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException ne) {
			addMensagemErro(ne.getMessage());
			return null;
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
		if(redirect)
			return iniciar();
		else
			return null;
	}

	/**
	 * Realiza a validação das solicitações anuladas.
	 * JSP: Não invocado por JSP.
	 */
	private void validarAnulacoes() {
		String[] anulados= getCurrentRequest().getParameterValues("anulados");
		if (!isEmpty(anulados)) {
			for (String s : anulados) {
				if (isEmpty(getParameter("motivo_"+s))) {
					addMensagemErro("Todas as solicitações anuladas devem ter um motivo cadastrado");
					return ;
				}
			}
		} else {
			addMensagemWarning("É necessário selecionar pelo menos uma solicitação.");
		}
	}

	/**
	 * Carrega todas as solicitações de matrículas do aluno a partir do período e ano corrente.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/solicitacao_matricula/painel_solicitacoes.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregarSolicitacoes() throws DAOException {
		try {
			discente = getDAO(DiscenteDao.class).findByPK(getParameterInt("id"));
			SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);
			CalendarioAcademico cal = getCalendarioVigente();
			solicitacoes = dao.findTodasByDiscenteAnoPeriodo(discente, cal.getAno(), cal.getPeriodo(),
					SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO,SolicitacaoMatricula.VISTA);
			
			if (isEmpty(solicitacoes)) {
				return null;
			}
			numeroSolicitacao = solicitacoes.iterator().next().getNumeroSolicitacao();
			prepareMovimento(SigaaListaComando.ANULAR_SOLICITACAO_MATRICULA);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
		}
		return null;
	}

	/** 
	 * Seta o discente 
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = discente;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Collection<SolicitacaoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public boolean isPassivelAnulacao() {
		return isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO);
	}
}
