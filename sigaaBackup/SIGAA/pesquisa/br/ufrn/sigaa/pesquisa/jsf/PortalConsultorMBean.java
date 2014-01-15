package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioBolsaFinalDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;

/**
 * MBean responsável pelo gerenciamento das abas do portal do Consultor.
 * 
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class PortalConsultorMBean extends SigaaAbstractController<Consultor> {

	/** Responsável por armazenar todos os projetos pendentes */
	private Collection<AvaliacaoProjeto> avaliacoesProjetoPendetes;
	/** Responsável por armazenar todos os projetos pendentes e especiais */
	private Collection<ProjetoPesquisa> avaliacoesProjetoEspeciaisPendetes;
	/** Responsável por armazenar todos os planos de trabalho pendentes */
	private Collection<PlanoTrabalho> planoTrabalhoPendentes;
	/** Responsável por armazenar todos os planos de trabalho pendentes e especiais */
	private Collection<PlanoTrabalho> planoTrabalhoPendentesEspecial;
	/** Responsável por armazenar todos os relatórios de projeto */
	private Collection<RelatorioProjeto> relatoriosProjeto;
	/** Responsável por armazenar todos os relatórios de projeto especiais */
	private Collection<RelatorioBolsaFinal> relatoriosEspecial;
	/** Responsável por armazenar todas as avaliações dos projetos avaliados */
	private Collection<AvaliacaoProjeto> avaliacoesProjetoAvaliado;
	
	private Collection<ProjetoPesquisa> projetos;
	private ProjetoPesquisa projetoPesquisa;
	
	private Collection<PlanoTrabalho> planos;
	private PlanoTrabalho planoTrabalho;
	
	public PortalConsultorMBean() {
		clear();
	}
	
	/** Responsável pela inicialização dos atributos utilizados no caso de uso. */
	public void clear(){
		obj = new Consultor();
	}
	
	/**
	 * Responsável por carregar todos os projetos de pesquisa.
	 * @param pendente
	 * @throws DAOException
	 */
	private void carregarProjetoPesquisa(boolean pendente, boolean avaliacaoProjeto) throws DAOException {
		AvaliacaoProjetoDao avaliacaoProjetoDao = getDAO(AvaliacaoProjetoDao.class);
		try {
			//Projetos Internos
			avaliacoesProjetoPendetes = avaliacaoProjetoDao.findByPortalConsultor(obj.getId(), pendente);
			if ( !avaliacaoProjeto )
				avaliacoesProjetoPendetes.addAll( avaliacaoProjetoDao.findByConsultor(obj.getId(), pendente) );
		} finally {
			avaliacaoProjetoDao.close();
		}
	}

	/**
	 * Responsável por carregar todos os plano de trabalho.
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>Método não invocado por JSP.</li>
	 * </ul>
	 */
	public void carregarPlanos() throws DAOException {
		carregarPlanoTrabalhoPendentes();
		carregarPlanoTrabalhoPendentesEspeciais();
	}

	/**
	 * Responsável por carregar todos os projetos de pesquisa.
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>Método não invocado por JSP.</li>
	 * </ul>
	 */
	public void carregarProjetos() throws DAOException {
		carregarProjetoPesquisa(true, false);
		carregarProjetoPesquisaConsultoriaEspecial();
	}
	
	/**
	 * Serve para verificar se há projetos pendentes de avaliação.
	 */
	public boolean hasProjetosPendetes() throws DAOException {
		carregarProjetos();
		if ( isEmpty( avaliacoesProjetoPendetes ) && isEmpty( avaliacoesProjetoEspeciaisPendetes ) )
			return false;
		return true;
	}

	/**
	 * Serve para verificar se há planos pendentes de avaliação.
	 */
	public boolean hasPlanoPendete() throws DAOException {
		carregarPlanos();
		if ( isEmpty( planoTrabalhoPendentes ) && isEmpty( planoTrabalhoPendentesEspecial ) )
			return false;
		return true;
	}
	
	/**
	 * Responsável por carregar os relatórios de Projeto. 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/portal_consultor.jsp</li>
	 * </ul>
	 */
	public void carregarRelatorioProjeto() throws DAOException{
		RelatorioProjetoDao relatorioDao = getDAO(RelatorioProjetoDao.class);
		try {
			relatoriosProjeto = relatorioDao.findPendentesAvaliacao( obj.getAreaConhecimentoCnpq().getGrandeArea().getId() );
		} finally {
			relatorioDao.close();
		}
	}
	
	/**
	 * Responsável por carregar o relatório final de projeto.
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/portal_consultor.jsp</li>
	 * </ul>
	 */
	public void carregarRelatorioFinal() throws DAOException {
		RelatorioBolsaFinalDao relatorioFinalDao = getDAO(RelatorioBolsaFinalDao.class);
		try {
			if (obj.isConsultorEspecial())
				relatoriosEspecial = relatorioFinalDao.findPortalConsultor( obj.getAreaConhecimentoCnpq().getGrandeArea().getId() );
		} finally {
			relatorioFinalDao.close();
		}
	}

	/**
	 * Responsável por carregar os projetos já avaliados.
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/portal_consultor.jsp</li>
	 * </ul>
	 */
	public void carregarProjetoAvaliado() throws DAOException {
		AvaliacaoProjetoDao avaliacaoProjetoDao = getDAO(AvaliacaoProjetoDao.class);
		try {
			avaliacoesProjetoAvaliado = avaliacaoProjetoDao.findByPortalConsultor(obj.getId(), false);
		} finally {
			avaliacaoProjetoDao.close();
		}
	}

	/**
	 * Responsável por carregar os projetos de consultoria especial.
	 * @throws DAOException
	 */
	private void carregarProjetoPesquisaConsultoriaEspecial() throws DAOException {
		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class);
		try {
			if (obj.isConsultorEspecial())
				avaliacoesProjetoEspeciaisPendetes = projetoDao.findPendentesAvaliacao( obj.getAreaConhecimentoCnpq() );
		} finally {
			projetoDao.close();
		}
	}
	
	/**
	 * Responsável por carregar os planos de trabalho pendentes e de consultoria especial.
	 * @throws DAOException
	 */
	private void carregarPlanoTrabalhoPendentesEspeciais() throws DAOException {
		PlanoTrabalhoDao planoTrabalhoDao = getDAO(PlanoTrabalhoDao.class);
		try {
			if (obj.isConsultorEspecial())
				planoTrabalhoPendentesEspecial = planoTrabalhoDao.findPendentesAvaliacao();
		} finally {
			planoTrabalhoDao.close();
		}
	}

	/**
	 * Responsável por carregar os planos de trabalho pendentes.
	 * @throws DAOException
	 */
	private void carregarPlanoTrabalhoPendentes() throws DAOException {
		PlanoTrabalhoDao planoTrabalhoDao = getDAO(PlanoTrabalhoDao.class);
		try {
			carregarProjetoPesquisa(true, false);
			planoTrabalhoPendentes = planoTrabalhoDao.findByConsultor(obj.getId(), true, getAvaliacoesProjetoPendetes());
		} finally {
			planoTrabalhoDao.close();
		}
	}
	
	public String telaProjetosPendentes() throws DAOException{
		carregarProjetoPesquisa(true, true);
		if ( isEmpty( avaliacoesProjetoPendetes ) ) {
			addMensagemErro("Não há Projeto de Pesquisa pendente de Avaliação.");
			return null;
		}
		return forward("/pesquisa/include_portal_consultor/_projetos_pendentes.jsf");
	}
	
	public String telaProjetosConsultoriaEspecial() throws DAOException{
		carregarProjetoPesquisaConsultoriaEspecial();
		if ( isEmpty( avaliacoesProjetoEspeciaisPendetes ) ) {
			addMensagemErro("Não há Projeto de Pesquisa pendente de Avaliação.");
			return null;
		}
		return forward("/pesquisa/include_portal_consultor/_projetos_consultoria_especial.jsf");
	}

	public String telaPlanoPendentes() throws DAOException{
		carregarPlanoTrabalhoPendentes();
		if ( isEmpty( planoTrabalhoPendentes ) ) {
			addMensagemErro("Não há planos pendentes de Avaliação.");
			return null;
		}
		return forward("/pesquisa/include_portal_consultor/_planos_pendentes.jsf");
	}
	
	public String telaPlanosConsultoriaEspecial() throws DAOException{
		carregarPlanoTrabalhoPendentesEspeciais();
		if ( isEmpty( planoTrabalhoPendentesEspecial ) ) {
			addMensagemErro("Não há planos pendentes de Avaliação.");
			return null;
		}
		return forward("/pesquisa/include_portal_consultor/_planos_consultoria_especial.jsf");
	}
	
	public String telaRelatorios() throws DAOException{
		carregarRelatorioProjeto();
		if ( isEmpty( relatoriosProjeto ) ) {
			addMensagemErro("Não há relatório de Projeto pendente de Avaliação.");
			return null;
		}
		return forward("/pesquisa/include_portal_consultor/_relatorios_pendentes.jsf");
	}
	
	public String telaRelatoriosFinais() throws DAOException{
		carregarRelatorioFinal();
		if ( isEmpty( relatoriosEspecial ) ) {
			addMensagemErro("Nenhum Relatório final de IC encontrado.");
			return null;
		}
		return forward("/pesquisa/include_portal_consultor/_relatorios_finais.jsf");
	}

	public String telaProjetosAvaliados() throws DAOException{
		carregarProjetoAvaliado();
		if ( isEmpty( avaliacoesProjetoAvaliado ) ) {
			addMensagemErro("Não foi encontrado nenhum Projeto de Pesquisa Avaliado.");
			return null;
		}
		return forward("/pesquisa/include_portal_consultor/_projetos_avaliados.jsf");
	}
	
	public Collection<AvaliacaoProjeto> getAvaliacoesProjetoPendetes() {
		return avaliacoesProjetoPendetes;
	}
	
	public Collection<ProjetoPesquisa> getAvaliacoesProjetoEspeciaisPendetes() {
		return avaliacoesProjetoEspeciaisPendetes;
	}
	
	public Collection<PlanoTrabalho> getPlanoTrabalhoPendentes() {
		return planoTrabalhoPendentes;
	}

	public Collection<PlanoTrabalho> getPlanoTrabalhoPendentesEspecial() {
		return planoTrabalhoPendentesEspecial;
	}
	
	public void setAvaliacoesProjetoPendetes(Collection<AvaliacaoProjeto> avaliacoesProjetoPendetes) {
		this.avaliacoesProjetoPendetes = avaliacoesProjetoPendetes;
	}
	
	public void setAvaliacoesProjetoEspeciaisPendetes(
			Collection<ProjetoPesquisa> avaliacoesProjetoEspeciaisPendetes) {
		this.avaliacoesProjetoEspeciaisPendetes = avaliacoesProjetoEspeciaisPendetes;
	}

	public void setPlanoTrabalhoPendentes(
			Collection<PlanoTrabalho> planoTrabalhoPendentes) {
		this.planoTrabalhoPendentes = planoTrabalhoPendentes;
	}

	public void setPlanoTrabalhoPendentesEspecial(
			Collection<PlanoTrabalho> planoTrabalhoPendentesEspecial) {
		this.planoTrabalhoPendentesEspecial = planoTrabalhoPendentesEspecial;
	}

	public Collection<RelatorioProjeto> getRelatoriosProjeto() {
		return relatoriosProjeto;
	}

	public void setRelatoriosProjeto(Collection<RelatorioProjeto> relatoriosProjeto) {
		this.relatoriosProjeto = relatoriosProjeto;
	}

	public Collection<RelatorioBolsaFinal> getRelatoriosEspecial() {
		return relatoriosEspecial;
	}

	public void setRelatoriosEspecial(
			Collection<RelatorioBolsaFinal> relatoriosEspecial) {
		this.relatoriosEspecial = relatoriosEspecial;
	}

	public Collection<AvaliacaoProjeto> getAvaliacoesProjetoAvaliado() {
		return avaliacoesProjetoAvaliado;
	}

	public void setAvaliacoesProjetoAvaliado(
			Collection<AvaliacaoProjeto> avaliacoesProjetoAvaliado) {
		this.avaliacoesProjetoAvaliado = avaliacoesProjetoAvaliado;
	}

	public String listarProjetos() throws LimiteResultadosException, DAOException {
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class);
		
		projetos = dao.findPortalConsultor();
		
		return telaProjetos();
	}
	
	public String telaProjetos() {
		return forward("/pesquisa/include_portal_consultor/_projetos.jsp");
	}
	
	public String verProjeto() throws DAOException {
		projetoPesquisa = getGenericDAO().findByPrimaryKey(getParameterInt("id"), ProjetoPesquisa.class);		
		return forward("/pesquisa/include_portal_consultor/_projeto_pesquisa.jsp");
	}
	
	public String verRelatorioProjeto() {
		return forward("/pesquisa/include_portal_consultor/_relatorio_projeto.jsp");
	}
	
	public String listarPlanos() throws DAOException {
		PlanoTrabalhoDao dao = getDAO(PlanoTrabalhoDao.class);
		planos = dao.findByProjeto(projetoPesquisa);
		return telaPlanos();
	}
	
	public String telaPlanos() {
		return forward("/pesquisa/include_portal_consultor/_planos.jsp");
	}
	
	public String verPlano() throws DAOException {
		planoTrabalho = getGenericDAO().findByPrimaryKey(getParameterInt("id"), PlanoTrabalho.class);
		return forward("/pesquisa/include_portal_consultor/_plano_trabalho.jsp");
	}
	
	public String verRelatorioPlano() {
		return forward("/pesquisa/include_portal_consultor/_relatorio_plano.jsp");
	}

	public Collection<ProjetoPesquisa> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoPesquisa> projetos) {
		this.projetos = projetos;
	}

	public ProjetoPesquisa getProjetoPesquisa() {
		return projetoPesquisa;
	}

	public void setProjetoPesquisa(ProjetoPesquisa projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	public Collection<PlanoTrabalho> getPlanos() {
		return planos;
	}

	public void setPlanos(Collection<PlanoTrabalho> planos) {
		this.planos = planos;
	}

	public PlanoTrabalho getPlanoTrabalho() {
		return planoTrabalho;
	}

	public void setPlanoTrabalho(PlanoTrabalho planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}
}