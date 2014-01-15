/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Managed-Bean para avalia��o dos relat�rios final de projetos de monitoria.
 *
 * @author Victor Hugo
 * @author ilueny santos
 *
 */
@Component("avalRelatorioProjetoMonitoria")
@Scope("session")
public class AvaliacaoRelatorioProjetoMBean extends SigaaAbstractController<AvaliacaoRelatorioProjeto> {

	/** Lista de todas as avalia��es dispon�veis para o usu�rio atual. */
	private Collection<AvaliacaoRelatorioProjeto> avaliacoes = new ArrayList<AvaliacaoRelatorioProjeto>();

	/** Avalia��o realizada para um relat�rio selecionado. */
	private AvaliacaoRelatorioProjeto avaliacao = new AvaliacaoRelatorioProjeto();

	/** Monitores que poder�o ser renovados pela comiss�o de avalia��o. */
	private Collection<DiscenteMonitoria> monitoresAtivos = new ArrayList<DiscenteMonitoria>();


	/**
	 * Construtor padr�o.
	 */
	public AvaliacaoRelatorioProjetoMBean() {
		super();
		obj = new AvaliacaoRelatorioProjeto();
	}


	/**
	 * Redireciona para uma p�gina que lista as avalia��es de um relat�rio.
	 * <br />
	 *
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\RelatorioProjetoMonitoria\lista.jsp</li>
	 * </ul>
	 *
	 * @return P�gina com a lista de relat�rios.
	 * @throws DAOException Gerado por busca das avalia��es do relat�rio.
	 */
	public String avaliacoesRelatorioForward() throws DAOException {
		final Integer idRelatorio = getParameterInt("idRelatorio", 0);
		avaliacoes = getGenericDAO().findByExactField(AvaliacaoRelatorioProjeto.class,	"relatorioProjetoMonitoria.id", idRelatorio);
		return forward(ConstantesNavegacaoMonitoria.AVALIACOES_RELATORIOS);
	}

	/**
	 * Avaliando o relat�rio do projeto.
	 * <br />
	 *
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
	 * </ul>
	 *
	 * @return P�gina com resultado da avalia��o realizada.
	 * @throws DAOException Gerado por acesso as
	 * @throws SegurancaException Somente gestores de monitoria ou membros do comit� de monitoria pode acessar este m�todo.
	 */
	public String avaliarProjeto() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_MONITORIA);
		final ListaMensagens mensagens = new ListaMensagens();

		String result;

		// Discentes selecionados para corte de bolsa s�o adicionados a avalia��o do membro da comiss�o
		for (DiscenteMonitoria dm : getMonitoresAtivos()) {
			if (dm.isSelecionado()) {
				final RecomendacaoCorteBolsa rec = new RecomendacaoCorteBolsa();
				rec.setDiscenteMonitoria(dm);
				rec.setJustificativa(dm.getJustificativa());
				rec.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
				rec.setAvaliacaoRelatorioProjeto(obj);
				obj.getRecomendacoesCorteBolsa().add(rec);

				// Validando as solicita��es de corte de bolsas
				if ((rec.getJustificativa() == null) || (rec.getJustificativa().trim().equals(""))) {
					mensagens.addErro("Justifique a sele��o de '" + dm.getDiscente().getMatriculaNome() + "' para cancelamento da Bolsa.");
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
	 * Data de avalia��o recebe a data atual.
	 * N�o � chamado por JSPs.
	 */
	@Override
	public void beforeCadastrarAndValidate() {
		obj.setDataAvaliacao(new Date());
	}


	/**
	 * Usado em um SelectOneMenu para recomendar ou n�o a renova��o de um projeto.
	 * <br />
	 *
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
	 * </ul>
	 *
	 * @param evt Indica se o avaliador recomenda a quantidade de bolsas.
	 * @return Mesma p�gina
	 */
	public String changeRecomendaRenovacao(final ValueChangeEvent evt) {
		if (evt.getNewValue() != null) {
			final Boolean recomenda = Boolean.valueOf(evt.getNewValue().toString());
			obj.setMantemQuantidadeBolsas(recomenda);
		}
		return null;
	}

	/**
	 * Retorna a avalia��o.
	 *
	 * @return {@link AvaliacaoRelatorioProjeto}
	 */
	public AvaliacaoRelatorioProjeto getAvaliacao() {
		return avaliacao;
	}


	/**
	 * Retorna lista de avalia��es.
	 *
	 * @return Cole��o de {@link AvaliacaoRelatorioProjeto}
	 */
	public Collection<AvaliacaoRelatorioProjeto> getAvaliacoes() {
		return avaliacoes;
	}

	/**
	 * N�o � chamado por JSPs.
	 * @return String com o diret�rio principal das Avalia��es de Relat�rios.
	 */
	@Override
	public String getDirBase() {
		return "/monitoria/AvaliacaoRelatorioProjeto";
	}


	/**
	 * Lista os monitores ativos.
	 * Utilizado na lista de monitores que ter�o a bolsa renovada.
	 * <br />
	 *
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
	 * </ul>
	 *
	 * @return Lista de monitores Ativos.
	 * @throws DAOException Gerado atrav�s da busca de monitores.
	 */
	public Collection<DiscenteMonitoria> getListaMonitoresAtivos() throws DAOException {
		monitoresAtivos = getDAO(DiscenteMonitoriaDao.class).filter(obj.getRelatorioProjetoMonitoria().getProjetoEnsino().getTitulo(),
				null, null, SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, null, null, true, null, null, null);

		// Carregando relat�rios dos monitores
		for (DiscenteMonitoria dm : monitoresAtivos) {
			dm.getRelatoriosMonitor().iterator();
		}
		return monitoresAtivos;
	}


	/**
	 * Lista de monitores Ativos.
	 *
	 * @return Cole��o de {@link DiscenteMonitoria}
	 */
	public Collection<DiscenteMonitoria> getMonitoresAtivos() {
		return monitoresAtivos;
	}

	/**
	 * Relat�rio do projeto � selecionado para avalia��o pelo membro da comiss�o ou por membro da PROGRAD.
	 * <br />
	 *
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/lista.jsp</li>
	 * </ul>
	 *
	 * @return P�gina com formul�rio de avalia��o do relat�rio.
	 * @throws SegurancaException Somente gestores de monitoria ou membros do comit� de monitoria podem acessar este m�todo.
	 */
	public String iniciarAvaliacao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_MONITORIA);

		try {

			prepareMovimento(SigaaListaComando.AVALIAR_RELATORIO_PROJETO_MONITORIA);
			setId();
			this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());

			// Monitores que enviaram relat�rios
			if (obj.getRelatorioProjetoMonitoria() != null) {

				// Carregando relat�rios dos monitores
				for (DiscenteMonitoria dm : obj.getRelatorioProjetoMonitoria().getProjetoEnsino().getDiscentesConsolidados()) {
					dm.getRelatoriosMonitor().iterator();
				}

				// Lista de monitores para renova��o das bolsas
				monitoresAtivos = getDAO(DiscenteMonitoriaDao.class).findByProjetoMonitoria(obj.getRelatorioProjetoMonitoria().getProjetoEnsino());

				// Carregando relat�rios dos monitores
				for (DiscenteMonitoria dm : monitoresAtivos) {
					dm.getRelatoriosMonitor().iterator();
				}
			}

			obj.getRecomendacoesCorteBolsa().iterator();

			// Definindo o padr�o de renovar e manter a quantidade de bolsas.
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
	 * Carrega os projetos que possuem relat�rio final para que seja realizada a avalia��o.
	 * <br />
	 *
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 *
	 * @return Lista de Avalia��es.
	 * @throws SegurancaException Somente gestores de monitoria ou membros do comit� de monitoria pode realizar esta opera��o.
	 * @throws DAOException Gerada atrav�s da busca das avalia��es do usu�rio.
	 */
	@Override
	public String listar() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_MONITORIA);
		avaliacoes = getDAO(AvaliacaoRelatorioProjetoDao.class).findByAvaliador(getUsuarioLogado().getServidor().getId());
		return forward(ConstantesNavegacaoMonitoria.AVALIACAORELATORIOPROJETO_LISTA);
	}

	/**
	 * Define a Avalia��o selecionada.
	 * @param avaliacao Avalia��o selecionada.
	 */
	public void setAvaliacao(final AvaliacaoRelatorioProjeto avaliacao) {
		this.avaliacao = avaliacao;
	}

	/**
	 * Define lista de Avalia��es.
	 * @param avaliacoes Lista de avalia��es encontradas.
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
	 * Carrega a avalia��o selecionada para a exibi��o.
	 * <br />
	 *
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoRelatorioProjeto/lista.jsp</li>
	 * </ul>
	 *
	 * @return P�gina com visualiza��o do relat�rio completo.
	 * @throws DAOException Gerado pela busca do relat�rio.
	 */
	public final String view() throws DAOException {
		obj = new AvaliacaoRelatorioProjeto();
		obj.setId(getParameterInt("idAvaliacao", 0));
		final GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(obj.getId(), AvaliacaoRelatorioProjeto.class);
		return forward(getViewPage());
	}

}
