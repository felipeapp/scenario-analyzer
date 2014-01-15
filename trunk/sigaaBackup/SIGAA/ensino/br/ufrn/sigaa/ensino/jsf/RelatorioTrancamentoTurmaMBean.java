/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 28/05/2009
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/** Controller responsável pela geração de relatórios de trancamento de componentes curriculares
 * @author Édipo Elder F. Melo
 *
 */
@Component("relatorioTrancamentoTurma") 
@Scope("request")
public class RelatorioTrancamentoTurmaMBean extends	SigaaAbstractController {

	private int idComponente;
	private int ano;
	private int periodo;
	
	/**
	 * Método que popula a página de início do caso de uso
	 * 
	 * Chamado por:
	 * <ul>
	 * <li>/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorio() throws DAOException{
		initFields();
		return forward("/ensino/trancamento_matricula/relatorio_motivo.jsp");
	}
	
	private void initFields() throws DAOException{
		this.ano = getAnoAtual();
		this.periodo = getPeriodoAtual();
		this.idComponente = 0;
	}
	
	/**
	 * Método que gera o relatórios de trancamento de componentes curriculares com
	 * base nos dados informados pelo usuário.
	 * 
	 * Chamado por:
	 * <ul>
	 * <li>/ensino/trancamento_matricula/relatorio_motivo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException {
		ComponenteCurricular componente = null;
		SolicitacaoTrancamentoMatriculaDao dao = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
		if (ano <= 1900 || (periodo < 1 || periodo > 4)) {
			addMensagemErro("Informe um ano-período válido");
			return null;
		}
		if (idComponente != 0) {
			componente = dao.findByPrimaryKey(idComponente, ComponenteCurricular.class);
		}
		List dados = new ArrayList();
		
		if( getAcessoMenu().isCoordenadorCursoGrad() )
			dados = dao.relatorioTrancamentoTurmaPorMotivo(ano, periodo, componente, null, getCursoAtualCoordenacao());
		else
			dados = dao.relatorioTrancamentoTurmaPorMotivo(ano, periodo, componente, getUsuarioLogado().getVinculoServidor().getUnidade(), null);
		getCurrentRequest().setAttribute("dados", dados);
		getCurrentRequest().setAttribute("componente", componente);
		return forward("/ensino/relatorios/trancamento_componente.jsp");
	}

	public int getIdComponente() {
		return idComponente;
	}

	public void setIdComponente(int idComponente) {
		this.idComponente = idComponente;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public Collection<SelectItem> getComponenteCombo() throws DAOException{
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		for (ComponenteCurricular componente : dao.findByUnidadeOtimizado(getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), NivelEnsino.GRADUACAO, null)) {
			SelectItem item = new SelectItem(componente.getId(), componente.getDescricaoResumida());
			combo.add(item);
		}
		return combo;
	}
}
