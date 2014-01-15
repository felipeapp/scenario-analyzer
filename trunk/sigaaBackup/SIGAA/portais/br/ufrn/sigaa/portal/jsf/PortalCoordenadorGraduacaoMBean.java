/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 04/06/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import java.util.Collection;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoMatriculaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * Managed Bean do portal do coordenador de gradua��o
 * @author leonardo
 *
 */
@Component(value="portalCoordenadorGrad")
@Scope(value="request")
public class PortalCoordenadorGraduacaoMBean extends SigaaAbstractController<Object> {

	private Collection<Discente> solicitacoesMatricula, solicitacoesTrancamento;

	private Curso curso;

	private int totalPreMatriculas, totalTrancamentos;

	/**
	 * Usada pra determinar o tipo de ordena��o da lista de orienta��es pendentes
	 */
	private Boolean orderByNome = Boolean.TRUE;
	
	/**
	 * Construtor Padr�o
	 * @throws DAOException
	 */
	public PortalCoordenadorGraduacaoMBean() throws DAOException{
		curso = getCursoAtualCoordenacao();
	}


	/**
	 * Este m�todo recarrega as informa��es que s�o exibidas no portal do coordenador.
	 * <br>JSP: N�o invocado por JSP
	 * 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void recarregarInformacoesPortal() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);

		if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) ){

			getCurrentRequest().getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario( getProgramaStricto() ));

		} else{
			getCurrentRequest().getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametros(curso));
			getCurrentRequest().getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(curso));

			if ( getCalendarioVigente().isPeriodoAnaliseCoordenador()) {
				/** recarregando as matr�culas pendentes de an�lise exibidas no portal */
				SolicitacaoMatriculaDao dao = getDAO( SolicitacaoMatriculaDao.class );
				int ano = getCalendarioVigente().getAno();
				int periodo = getCalendarioVigente().getPeriodo();
				solicitacoesMatricula = dao.findByCursoAnoPeriodo(curso, ano, periodo, true);
				totalPreMatriculas = (dao.getNumeroSolicitacoesMatricula(curso, ano, periodo, !getCursoAtualCoordenacao().isADistancia())).intValue();
			}

			if ( getCalendarioVigente().isPeriodoTrancamentoTurmas()) {
				/** recarregando as solicita��es de trancamento pendentes de avalia��o */
				DiscenteDao daoDiscente = getDAO( DiscenteDao.class );
				SolicitacaoTrancamentoMatriculaDao daoT = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
				CalendarioAcademico cal = getCalendarioVigente();
				solicitacoesTrancamento = daoDiscente.findBySolicitacoesTrancamentoPendentes( curso, null, cal.getAno(), cal.getPeriodo(), true );
				totalTrancamentos = (daoT.getNumeroSolicitacoesTrancamentoMatricula(curso, cal.getAno(), cal.getPeriodo())).intValue();
			}
		}

	}
	
	/**
	 * Chamado no portal para ordenar a lista de solicita��es de orienta��es
	 * <br>JSP: /NewSIGAA/app/sigaa.ear/sigaa.war/graduacao/coordenador.jsp
	 * @param evt
	 * @throws DAOException
	 */
	public void ordernarOrientacaoMatricula(ActionEvent evt) throws DAOException {
		orderByNome = !orderByNome;
		getSolicitacoesMatricula();
	}
	
	/**
	 * Retorna todos as solicita��es de matr�cula.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>N�o invocado por jsp.</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> getSolicitacoesMatricula() throws DAOException {

		if(solicitacoesMatricula == null && isUserInRole(SigaaPapeis.COORDENADOR_CURSO) && getCalendarioVigente().isPeriodoAnaliseCoordenador()){
			SolicitacaoMatriculaDao dao = getDAO( SolicitacaoMatriculaDao.class );
			int ano = getCalendarioVigente().getAno();
			int periodo = getCalendarioVigente().getPeriodo();

			Curso curso = getCursoAtualCoordenacao();
			
			if (curso != null) {
				solicitacoesMatricula = dao.findByCursoAnoPeriodo(curso, ano, periodo, true, true, orderByNome);
				totalPreMatriculas = (dao.getNumeroSolicitacoesMatricula(curso, ano, periodo, true)).intValue();
			}
			
		}

		return solicitacoesMatricula;
	}

	/**
	 * Popular as solicita��es de trancamento pendentes de an�lise
	 * <br>JSP: sigaa.war/graduacao/coordenador.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> getSolicitacoesTrancamento() throws DAOException {
		if ( solicitacoesTrancamento == null && isUserInRole(SigaaPapeis.COORDENADOR_CURSO) && getCalendarioVigente().isPeriodoTrancamentoTurmas()) {
			DiscenteDao dao = getDAO( DiscenteDao.class );
			SolicitacaoTrancamentoMatriculaDao daoT = getDAO(SolicitacaoTrancamentoMatriculaDao.class);

			CalendarioAcademico cal = getCalendarioVigente();
			if (cal != null) {
				solicitacoesTrancamento = dao.findBySolicitacoesTrancamentoPendentes( curso, null, cal.getAno(), cal.getPeriodo(), true );
				totalTrancamentos = (daoT.getNumeroSolicitacoesTrancamentoMatricula(curso, cal.getAno(), cal.getPeriodo())).intValue();
			}
		}
		return solicitacoesTrancamento;
	}

	public boolean getAcessoMatriculaTurmaFerias(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.ACESSO_COORDENACAO_MATRICULA_TURMA_DE_FERIAS);
	}

	public Curso getCurso() {
		return curso;
	}


	public void setCurso(Curso curso) {
		this.curso = curso;
	}


	public int getTotalPreMatriculas() {
		return totalPreMatriculas;
	}


	public void setTotalPreMatriculas(int totalPreMatriculas) {
		this.totalPreMatriculas = totalPreMatriculas;
	}


	public int getTotalTrancamentos() {
		return totalTrancamentos;
	}


	public void setTotalTrancamentos(int totalTrancamentos) {
		this.totalTrancamentos = totalTrancamentos;
	}

	public Boolean getOrderByNome() {
		return orderByNome;
	}

	public void setOrderByNome(Boolean orderByNome) {
		this.orderByNome = orderByNome;
	}
}
