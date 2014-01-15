/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 08/01/2013
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;

/**
 * MBean para controle do acompanhamento e indicadores de solicitação de ensino individual de aluno
 *
 * @author Rafael Gomes
 *
 */
@Component() @Scope("request")
public class IndicadorSolicitacaoEnsinoIndividualMBean extends SigaaAbstractController<SolicitacaoEnsinoIndividual> {

	/** JSP utilizada para acompanhamento dos indicadores de solicitação de ensino individual. */
	public static final String JSP_INDICADORES = "/indicadores.jsp";
	
	/** Map de solicitações de ensino individual agrupados por componentes curriculares. */
	private Map<ComponenteCurricular, Collection<SolicitacaoEnsinoIndividual>> mapResultado = new HashMap<ComponenteCurricular, Collection<SolicitacaoEnsinoIndividual>>();
	
	/**
	 * Inicia o caso de uso de indicadores de solicitação de ensino individual realizado para os componentes da unidade do chefe de departamento..
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciar() throws DAOException, SegurancaException {
		
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO);
		
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);
		Collection<SolicitacaoEnsinoIndividual> solicitacoes = new ArrayList<SolicitacaoEnsinoIndividual>();
		mapResultado = new HashMap<ComponenteCurricular, Collection<SolicitacaoEnsinoIndividual>>();
		
		solicitacoes = dao.findByUnidadeAnoPeriodoSituacao(getUnidadeResponsabilidade().getId(), getCalendarioVigente().getAno(), 
				getCalendarioVigente().getPeriodo(), SolicitacaoEnsinoIndividual.ATENDIDA, SolicitacaoEnsinoIndividual.SOLICITADA);
		
		for (SolicitacaoEnsinoIndividual sei : solicitacoes) {
			if (mapResultado.containsKey(sei.getComponente())) {
				mapResultado.get(sei.getComponente()).add(sei);
			} else {
				Collection<SolicitacaoEnsinoIndividual> solicitacoesComponente = new ArrayList<SolicitacaoEnsinoIndividual>();
				solicitacoesComponente.add(sei);
				mapResultado.put(sei.getComponente(), solicitacoesComponente);
			}
		}
		
		return forward(getListPage());
	}

	public String iniciar2(){
		return forward(getDirBase() + "/testRich.jsp");
	}

	@Override
	public String getDirBase() {
		return "/graduacao/solicitacao_ensino_individual";
	}
	
	@Override
	public String getListPage() {
		return getDirBase() + JSP_INDICADORES;
	}

	public Map<ComponenteCurricular, Collection<SolicitacaoEnsinoIndividual>> getMapResultado() {
		return mapResultado;
	}
	
	public void setMapResultado(
			Map<ComponenteCurricular, Collection<SolicitacaoEnsinoIndividual>> mapResultado) {
		this.mapResultado = mapResultado;
	}
}
