/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 16/07/2008
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoRelatorioProjetoDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoRelatorioProjeto;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.RecomendacaoCorteBolsa;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;

/**
 * Managed-Bean para avaliação dos relatórios final de projetos de monitoria.
 *
 * @author Victor Hugo
 * @author ilueny santos
 *
 */
@Component("avalRelatorioProjetoMonitoria")
@Scope("session")
public class AvaliacaoRelatorioProjetoMBean extends SigaaAbstractController<AvaliacaoRelatorioProjeto> {

	/** Lista de todas as avaliações disponíveis para o usuário atual. */
	private Collection<AvaliacaoRelatorioProjeto> avaliacoes = new ArrayList<AvaliacaoRelatorioProjeto>();

	/** Avaliação realizada para um relatório selecionado. */
	private AvaliacaoRelatorioProjeto avaliacao = new AvaliacaoRelatorioProjeto();

	/** Monitores que poderão ser renovados pela comissão de avaliação. */
	private Collection<DiscenteMonitoria> monitoresAtivos = new ArrayList<DiscenteMonitoria>();


	/**
	 * Construtor padrão.
	 */
	public AvaliacaoRelatorioProjetoMBean() {
		super();
		obj = new AvaliacaoRelatorioProjeto();
	}


	/**
	 * Redireciona para uma página que lista as avaliações de um relatório.
	 * <br />
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\RelatorioProjetoMonitoria\lista.jsp</li>
	 * </ul>
	 *
	 * @return Página com a lista de relatórios.
	 * @throws DAOException Gerado por busca das avaliações do relatório.
	 */
	public String avaliacoesRelatorioForward() throws DAOException {
		final Integer idRelatorio = getParameterInt("idRelatorio", 0);
		avaliacoes = getGenericDAO().findByExactField(AvaliacaoRelatorioProjeto.class,	"relatorioProjetoMonitoria.id", idRelatorio);
		return forward(ConstantesNavegacaoMonitoria.AVALIACOES_RELATORIOS);
	}

	/**
	 * Avaliando o relatório do projeto.
	 * <br />
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
	 * </ul>
	 *
	 * @return Página com resultado da avaliação realizada.
	 * @throws DAOException Gerado por acesso as
	 * @throws SegurancaException Somente gestores de monitoria ou membros do comitê de monitoria pode acessar este método.
	 */
	public String avaliarProjeto() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_MONITORIA);
		final ListaMensagens mensagens = new ListaMensagens();

		String result;

		// Discentes selecionados para corte de bolsa são adicionados a avaliação do membro da comissão
		for (DiscenteMonitoria dm : getMonitoresAtivos()) {
			if (dm.isSelecionado()) {
				final RecomendacaoCorteBolsa rec = new RecomendacaoCorteBolsa();
				rec.setDiscenteMonitoria(dm);
				rec.setJustificativa(dm.getJustificativa());
				rec.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
				rec.setAvaliacaoRelatorioProjeto(obj);
				obj.getRecomendacoesCorteBolsa().add(rec);

				// Validando as solicitações de corte de bolsas
				if ((rec.getJustificativa() == null) || (rec.getJustificativa().trim().equals(""))) {
					mensagens.addErro("Justifique a seleção de '" + dm.getDiscente().getMatriculaNome() + "' para cancelamento da Bolsa.");
					obj.getRecomendacoesCorteBolsa().clear();
					break;
				}
			}
		}

		if (mensagens.isEmpty()) {
			try {
				final MovimentoCadastro mov = new MovimentoCadastro();
				obj.setDataAvaliacao(new Date());
				obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
				mov.setObjMovimentado(obj);
				mov.setCodMovimento(SigaaListaComando.AVALIAR_RELATORIO_PROJETO_MONITORIA);

				execute(mov, getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				result = listar();
			} catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
				obj.getRecomendacoesCorteBolsa().clear();
				result = null;
			}

		}else {
			addMensagens(mensagens);
			result = null;
		}

		return result;
	}


	/**
	 * Data de avaliação recebe a data atual.
	 * Não é chamado por JSPs.
	 */
	@Override
	public void beforeCadastrarAndValidate() {
		obj.setDataAvaliacao(new Date());
	}


	/**
	 * Usado em um SelectOneMenu para recomendar ou não a renovação de um projeto.
	 * <br />
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
	 * </ul>
	 *
	 * @param evt Indica se o avaliador recomenda a quantidade de bolsas.
	 * @return Mesma página
	 */
	public String changeRecomendaRenovacao(final ValueChangeEvent evt) {
		if (evt.getNewValue() != null) {
			final Boolean recomenda = Boolean.valueOf(evt.getNewValue().toString());
			obj.setMantemQuantidadeBolsas(recomenda);
		}
		return null;
	}

	/**
	 * Retorna a avaliação.
	 *
	 * @return {@link AvaliacaoRelatorioProjeto}
	 */
	public AvaliacaoRelatorioProjeto getAvaliacao() {
		return avaliacao;
	}


	/**
	 * Retorna lista de avaliações.
	 *
	 * @return Coleção de {@link AvaliacaoRelatorioProjeto}
	 */
	public Collection<AvaliacaoRelatorioProjeto> getAvaliacoes() {
		return avaliacoes;
	}

	/**
	 * Não é chamado por JSPs.
	 * @return String com o diretório principal das Avaliações de Relatórios.
	 */
	@Override
	public String getDirBase() {
		return "/monitoria/AvaliacaoRelatorioProjeto";
	}


	/**
	 * Lista os monitores ativos.
	 * Utilizado na lista de monitores que terão a bolsa renovada.
	 * <br />
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
	 * </ul>
	 *
	 * @return Lista de monitores Ativos.
	 * @throws DAOException Gerado através da busca de monitores.
	 */
	public Collection<DiscenteMonitoria> getListaMonitoresAtivos() throws DAOException {
		monitoresAtivos = getDAO(DiscenteMonitoriaDao.class).filter(obj.getRelatorioProjetoMonitoria().getProjetoEnsino().getTitulo(),
				null, null, SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, null, null, true, null, null, null);

		// Carregando relatórios dos monitores
		for (DiscenteMonitoria dm : monitoresAtivos) {
			dm.getRelatoriosMonitor().iterator();
		}
		return monitoresAtivos;
	}


	/**
	 * Lista de monitores Ativos.
	 *
	 * @return Coleção de {@link DiscenteMonitoria}
	 */
	public Collection<DiscenteMonitoria> getMonitoresAtivos() {
		return monitoresAtivos;
	}

	/**
	 * Relatório do projeto é selecionado para avaliação pelo membro da comissão ou por membro da PROGRAD.
	 * <br />
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/lista.jsp</li>
	 * </ul>
	 *
	 * @return Página com formulário de avaliação do relatório.
	 * @throws SegurancaException Somente gestores de monitoria ou membros do comitê de monitoria podem acessar este método.
	 */
	public String iniciarAvaliacao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_MONITORIA);

		try {

			prepareMovimento(SigaaListaComando.AVALIAR_RELATORIO_PROJETO_MONITORIA);
			setId();
			this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());

			// Monitores que enviaram relatórios
			if (obj.getRelatorioProjetoMonitoria() != null) {

				// Carregando relatórios dos monitores
				for (DiscenteMonitoria dm : obj.getRelatorioProjetoMonitoria().getProjetoEnsino().getDiscentesConsolidados()) {
					dm.getRelatoriosMonitor().iterator();
				}

				// Lista de monitores para renovação das bolsas
				monitoresAtivos = getDAO(DiscenteMonitoriaDao.class).findByProjetoMonitoria(obj.getRelatorioProjetoMonitoria().getProjetoEnsino());

				// Carregando relatórios dos monitores
				for (DiscenteMonitoria dm : monitoresAtivos) {
					dm.getRelatoriosMonitor().iterator();
				}
			}

			obj.getRecomendacoesCorteBolsa().iterator();

			// Definindo o padrão de renovar e manter a quantidade de bolsas.
			if (obj.isAvaliacaoEmAberto()) {
				obj.setRecomendaRenovacao(true);
				obj.setMantemQuantidadeBolsas(true);
			}

		} catch (Exception e) {
			notifyError(e);
		}

		setConfirmButton("Avaliar");
		return forward(getFormPage());
	}


	/**
	 * Carrega os projetos que possuem relatório final para que seja realizada a avaliação.
	 * <br />
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 *
	 * @return Lista de Avaliações.
	 * @throws SegurancaException Somente gestores de monitoria ou membros do comitê de monitoria pode realizar esta operação.
	 * @throws DAOException Gerada através da busca das avaliações do usuário.
	 */
	@Override
	public String listar() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_MONITORIA);
		avaliacoes = getDAO(AvaliacaoRelatorioProjetoDao.class).findByAvaliador(getUsuarioLogado().getServidor().getId());
		return forward(ConstantesNavegacaoMonitoria.AVALIACAORELATORIOPROJETO_LISTA);
	}

	/**
	 * Define a Avaliação selecionada.
	 * @param avaliacao Avaliação selecionada.
	 */
	public void setAvaliacao(final AvaliacaoRelatorioProjeto avaliacao) {
		this.avaliacao = avaliacao;
	}

	/**
	 * Define lista de Avaliações.
	 * @param avaliacoes Lista de avaliações encontradas.
	 */
	public void setAvaliacoes(final Collection<AvaliacaoRelatorioProjeto> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	/**
	 * Define lista de monitores ativos.
	 * @param monitoresAtivos Lista de monitores ativos encontrados.
	 */
	public void setMonitoresAtivos(final Collection<DiscenteMonitoria> monitoresAtivos) {
		this.monitoresAtivos = monitoresAtivos;
	}


	/**
	 * Carrega a avaliação selecionada para a exibição.
	 * <br />
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/lista.jsp</li>
	 * </ul>
	 *
	 * @return Página com visualização do relatório completo.
	 * @throws DAOException Gerado pela busca do relatório.
	 */
	public final String view() throws DAOException {
		obj = new AvaliacaoRelatorioProjeto();
		obj.setId(getParameterInt("idAvaliacao", 0));
		final GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(obj.getId(), AvaliacaoRelatorioProjeto.class);
		return forward(getViewPage());
	}

}
