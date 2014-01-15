/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/07/2008 
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
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoMatriculaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean vinculado ao portal da coordenação de programas de pós-graduação
 *
 * @author Ricardo Wendell
 *
 */
@Component("portalCoordenacaoStrictoBean") @Scope("session")
public class PortalCoordenacaoStrictoMBean extends SigaaAbstractController<Object> {

	/** Listas de discentes que serão exibidas no portal do coordenador stricto sensu */
	private Collection<Discente> solicitacoesMatricula, solicitacoesMatriculaOutroPrograma, solicitacoesTrancamento;
	/** Totais referentes as listagens */
	private int totalPreMatriculas, totalPendentesOutrosProgramas, totalTrancamentos;

	/**
	 * Usado para determinar o tipo de ordenação na lista de orientações pendentes
	 */
	private Boolean orderByNome = Boolean.TRUE;

	public PortalCoordenacaoStrictoMBean() {
		if (getProgramaStricto() == null)
			addMensagemErro("Não foi possível detectar o programa no qual seu usuário está vinculado. Por favor reinicie os procedimentos.");
	}

	/**
	 * Este método recarrega as informações que são exibidas no portal do coordenador.
	 * <br>JSP: Não invocado por JSP
	 * 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void recarregarInformacoesPortal() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		getCurrentRequest().getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario( getProgramaStricto() ));
		setSolicitacoesMatricula(null);
		setSolicitacoesTrancamento(null);
		setSolicitacoesMatriculaOutroPrograma(null);
	}

	/**
	 * Popular as solicitações de matrícula pendentes de análise
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>JSP: /sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> getSolicitacoesMatricula() throws DAOException {
		if (solicitacoesMatricula == null && getCalendarioVigente() != null) {
			SolicitacaoMatriculaDao dao = getDAO( SolicitacaoMatriculaDao.class );
			int ano = getCalendarioVigente().getAno();
			int periodo = getCalendarioVigente().getPeriodo();
			solicitacoesMatricula = dao.findByUnidadeAnoPeriodo(getProgramaStricto().getId(), ano, periodo, 5, true, orderByNome);
			totalPreMatriculas = dao.getNumeroSolicitacoesMatricula(getProgramaStricto(), ano, periodo).intValue();
		}
		return this.solicitacoesMatricula;
	}


	public void setSolicitacoesMatricula(Collection<Discente> solicitacoesMatricula) {
		this.solicitacoesMatricula = solicitacoesMatricula;
	}
	
	/**
	 * Usado no portal para ordenar a lista de orientações pendentes
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>JSP: /sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void ordernarOrientacaoMatricula(ActionEvent evt) throws DAOException {
		orderByNome = !orderByNome;
		getSolicitacoesMatricula();
	}
	
	/**
	 * Popular as solicitações de trancamento pendentes de análise
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>JSP: /sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> getSolicitacoesTrancamento() throws DAOException {
		CalendarioAcademico calendario = getCalendarioVigente();
		if ( calendario != null && ValidatorUtil.isEmpty(solicitacoesTrancamento) ) {
			/** recarregando as solicitações de trancamento pendentes de avaliação */
			SolicitacaoTrancamentoMatriculaDao daoT = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
			solicitacoesTrancamento = daoT.findBySolicitacoesTrancamentoPendentes( null, getProgramaStricto(), calendario.getAno(), calendario.getPeriodo(), true );
			totalTrancamentos = (daoT.getNumeroSolicitacoesTrancamentoMatricula(getProgramaStricto(), calendario.getAno(), calendario.getPeriodo())).intValue();
		}
		return this.solicitacoesTrancamento;
	}


	public void setSolicitacoesTrancamento(
			Collection<Discente> solicitacoesTrancamento) {
		this.solicitacoesTrancamento = solicitacoesTrancamento;
	}


	public int getTotalPreMatriculas() {
		return this.totalPreMatriculas;
	}


	public void setTotalPreMatriculas(int totalPreMatriculas) {
		this.totalPreMatriculas = totalPreMatriculas;
	}


	public int getTotalTrancamentos() {
		return this.totalTrancamentos;
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
	public int getTotalPendentesOutrosProgramas() {
		return totalPendentesOutrosProgramas;
	}

	public void setTotalPendentesOutrosProgramas(int totalPendentesOutrosProgramas) {
		this.totalPendentesOutrosProgramas = totalPendentesOutrosProgramas;
	}
	/**
	 * Popular as solicitações de matrícula de outros programs pendentes de análise
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>JSP: /sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 */
	public Collection<Discente> getSolicitacoesMatriculaOutroPrograma() throws DAOException {
		if (solicitacoesMatriculaOutroPrograma == null && getCalendarioVigente() != null) {
			SolicitacaoMatriculaDao dao = getDAO( SolicitacaoMatriculaDao.class );
			solicitacoesMatriculaOutroPrograma = dao.findDiscentesOutrosProgramasByProgramaSituacao(getProgramaStricto().getId(), getCalendarioVigente(), SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);
			totalPendentesOutrosProgramas = 0;
			if (!ValidatorUtil.isEmpty(solicitacoesMatriculaOutroPrograma) && solicitacoesMatriculaOutroPrograma.size() > 0){
				Collection<Discente> outros = dao.findDiscentesOutrosProgramasByProgramaSituacao(getProgramaStricto().getId(), null, SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA);
				totalPendentesOutrosProgramas = (solicitacoesMatriculaOutroPrograma != null ? solicitacoesMatriculaOutroPrograma.size() : 0) +
				(  outros != null ? outros.size() : 0);				
			}
			dao.close();
		}
		return solicitacoesMatriculaOutroPrograma;
	}

	public void setSolicitacoesMatriculaOutroPrograma(
			Collection<Discente> solicitacoesMatriculaOutroPrograma) {
		this.solicitacoesMatriculaOutroPrograma = solicitacoesMatriculaOutroPrograma;
	}
	
	public boolean isProgramaCadastraDiscenteAntigo(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.PERMITE_PROGRAMA_POS_CADASTRAR_DISCENTE_ANTIGO);		
	}
	
	public boolean isProgramaImplantaHistorico(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.PERMITE_PROGRAMA_POS_IMPLANTAR_HISTORICO_DISCENTE_ANTIGO);		
	}
}
