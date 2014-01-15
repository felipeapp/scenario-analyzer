/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/07/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.RelatorioProjetoMonitorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;
import br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * MBean que controla opera��es nos relat�rios de projetos de monitoria.
 * 
 * @author Ilueny Santos
 * 
 */
@Scope("session")
@Component("relatorioProjetoMonitoria")
public class RelatorioProjetoMonitoriaMBean extends SigaaAbstractController<RelatorioProjetoMonitoria> {

	private Collection<ProjetoEnsino> projetos;

	private boolean checkBuscaProjeto =  false;
	private boolean checkBuscaAno =  false;
	private boolean checkBuscaServidor = false;
	private boolean checkBuscaTipoRelatorio =  false;
	private boolean comPaginacao;

	private String tituloProjeto = "";
	private ProjetoEnsino buscaProjetoEnsino = new ProjetoEnsino();
	private Integer buscaAnoProjeto = 0;
	private Servidor buscaServidor =  new Servidor();
	private TipoRelatorioMonitoria buscaTipoRelatorio = new TipoRelatorioMonitoria();
	private Collection<RelatorioProjetoMonitoria> relatoriosLocalizados;

	/**
	 * Construtor do MBean
	 */
	public RelatorioProjetoMonitoriaMBean() {
		obj = new RelatorioProjetoMonitoria();
		setBuscaAnoProjeto(CalendarUtils.getAnoAtual());
	}

	/**
	 * Lista os relat�rios do usu�rio atual.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarRelatorios() throws DAOException{
		if ((getAcessoMenu() != null) && (getAcessoMenu().isCoordenadorMonitoria())) {
			return forward( ConstantesNavegacaoMonitoria.RELATORIOPROJETO_LISTA );
		} else{
			addMensagemErro("Usu�rio atual n�o Coordenena projetos de monitoria ativos");
			return null;
		}
	}

	/**
	 * Inicia o cadastro/altera��o do relat�rio parcial do projeto.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioParcial() throws ArqException {
		checkDocenteRole();

		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_MONITORIA);
		getCurrentSession().setAttribute("id_projeto_enviado", null);

		int id = getParameterInt("id", 0);

		if (id == 0) {
			addMensagemErro("Selecione o projeto de monitoria para o qual quer enviar o relat�rio.");
			return null;
		} else {

			RelatorioProjetoMonitorDao dao = getDAO(RelatorioProjetoMonitorDao.class);
			ProjetoEnsino pm = dao.findByPrimaryKey(id, ProjetoEnsino.class);

			CalendarioMonitoria calendario = getDAO(ProjetoMonitoriaDao.class)
			.findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());

			Collection<RelatorioProjetoMonitoria> listaRelatorios = getDAO(
					RelatorioProjetoMonitorDao.class)
					.findByProjetoMonitoria(pm);
			for (RelatorioProjetoMonitoria rel : listaRelatorios) {
				if (rel.getTipoRelatorio().getId() == 1) {
					addMensagemErro("J� existe um relat�rio parcial salvo para o projeto atual.");
					return null;
				}
			}
			if ((calendario == null) || (!calendario.isAtivo())) {
				addMensagemErro("Per�odo de envio do relat�rio ainda n�o foi definido pela Pr�-Reitoria de Gradua��o!");
				return null;
			}
			if (calendario.getAnoProjetoRelatorioParcial() != pm.getAno()) {
				addMensagemErro("Atualmente, o recebimento de Relat�rio Parcial de Projeto est� autorizado somente para projetos submetidos em "
						+ calendario.getAnoProjetoRelatorioParcial());
				return null;
			} else {
				// verifica se t� no per�odo do recebimento do relat�rio
				if (calendario.isRelatorioParcialProjetoEmAberto()) {

					RelatorioProjetoMonitoria relatorio = dao
					.findByProjetoMonitoriaTipoRelatorio(pm,
							TipoRelatorioMonitoria.RELATORIO_PARCIAL);

					if ((relatorio != null) && (relatorio.isAtivo())) {
						//Carrega relat�rio que j� foi cadatrado, mas n�o foi enviado ainda.
						if (relatorio.getDataEnvio() == null) {
							obj = relatorio;
						} else {
							addMensagem(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD);
							return null;
						}
					} else {
						obj = new RelatorioProjetoMonitoria();
						obj.setRegistroEntrada(getUsuarioLogado()
								.getRegistroEntrada());
						obj.setTipoRelatorio(dao.findByPrimaryKey(
								TipoRelatorioMonitoria.RELATORIO_PARCIAL,
								TipoRelatorioMonitoria.class));
						obj.setStatus(dao.findByPrimaryKey(
								StatusRelatorio.CADASTRO_EM_ANDAMENTO,
								StatusRelatorio.class));
						obj.setProjetoEnsino(pm);
						obj.setDesejaRenovarProjeto(true);
					}
				} else {
					addMensagemErro("O prazo para recebimento do Relat�rio Parcial de Projeto terminou!");
					return null;
				}
			}
		}
		return forward(ConstantesNavegacaoMonitoria.RELATORIOPROJETO_FORM);
	}

	/**
	 * Chamado depois de uma atualiza��o
	 * 
	 * M�todo n�o invocado por JSP�s
	 * 
	 * @throws ArqException
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_MONITORIA);
		prepareMovimento(SigaaListaComando.CADASTRAR_RELATORIO_PROJETO_MONITORIA);
		setReadOnly(true);
	}

	/**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/lista.jsp</li>
	 * </ul>
	 * @throws ArqException
	 */
	@Override
	public String atualizar() throws ArqException {
		RelatorioProjetoMonitoria relatorio = getDAO(RelatorioProjetoMonitorDao.class).findByPrimaryKey(getParameterInt("id", 0), RelatorioProjetoMonitoria.class);
		if (relatorio != null && relatorio.getStatus().getId() == StatusRelatorio.AGUARDANDO_DISTRIBUICAO) {
			addMensagem(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD);
			return listarRelatorios();
		}
		getCurrentSession().removeAttribute("id_projeto_enviado");
		return super.atualizar();
	}

	/**
	 * Inicia o cadastro/altera��o do relat�rio final do projeto.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioFinal() throws ArqException {
		checkDocenteRole();

		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_MONITORIA);
		getCurrentSession().setAttribute("id_projeto_enviado", null);

		int id = getParameterInt("id", 0);

		if (id == 0) {
			addMensagemErro("Selecione o projeto de monitoria para o qual quer enviar o relat�rio.");
			return null;
		} else {

			RelatorioProjetoMonitorDao dao = getDAO(RelatorioProjetoMonitorDao.class);
			ProjetoEnsino pm = dao.findByPrimaryKey(id, ProjetoEnsino.class);

			// carrega o calend�rio de monitoria
			CalendarioMonitoria cm = getDAO(ProjetoMonitoriaDao.class)
			.findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());

			if ((cm == null) || (!cm.isAtivo())) {
				addMensagemErro("Per�odo de envio do relat�rio ainda n�o foi definido pela Pr�-Reitoria de Gradua��o!");
				return null;
			}

			if (cm.getAnoProjetoRelatorioFinal() != pm.getAno()) {
				addMensagemErro("Atualmente, o recebimento de Relat�rio Final de Projeto est� autorizado somente para projetos submetidos em "
						+ cm.getAnoProjetoRelatorioFinal());
				return null;
			} else {
				// verifica se t� no per�odo do recebimento do relat�rio
				if (cm.isRelatorioFinalProjetoEmAberto()) {

					RelatorioProjetoMonitoria relatorio = dao
					.findByProjetoMonitoriaTipoRelatorio(pm,
							TipoRelatorioMonitoria.RELATORIO_FINAL);

					if ((relatorio != null) && (relatorio.isAtivo())) {
						if (relatorio.getDataEnvio() == null) {
							obj = relatorio;
						} else {
							addMensagem(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD);
							return null;
						}
					} else {
						obj = new RelatorioProjetoMonitoria();
						obj.setRegistroEntrada(getUsuarioLogado()
								.getRegistroEntrada());
						obj.setTipoRelatorio(dao.findByPrimaryKey(
								TipoRelatorioMonitoria.RELATORIO_FINAL,
								TipoRelatorioMonitoria.class));
						obj.setStatus(dao.findByPrimaryKey(
								StatusRelatorio.CADASTRO_EM_ANDAMENTO,
								StatusRelatorio.class));
						obj.setProjetoEnsino(pm);
						obj.setDesejaRenovarProjeto(false);
					}
				} else {
					addMensagemErro("O prazo para recebimento do Relat�rio Final de Projeto terminou!");
					return null;
				}
			}
		}
		return forward(ConstantesNavegacaoMonitoria.RELATORIOPROJETO_FORM);
	}

	/**
	 * Cadastra o relat�rio permitindo que o docente fa�a altera��es posteriores
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String cadastrar() throws ArqException {
		checkDocenteRole();

		try {
			// Evitando erro de voltar por cima
			Integer idProjeto = (Integer) getCurrentSession().getAttribute("id_projeto_enviado");

			if((idProjeto != null) && (idProjeto == obj.getProjetoEnsino().getId())){
				addMensagem(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD);
				return null;
			}
			prepareMovimento(SigaaListaComando.CADASTRAR_RELATORIO_PROJETO_MONITORIA);

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_RELATORIO_PROJETO_MONITORIA);
			mov.setObjMovimentado(obj);
			obj = (RelatorioProjetoMonitoria) execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			//getCurrentSession().setAttribute("id_projeto_enviado", obj.getProjetoEnsino().getId());
		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}
		//obj = new RelatorioProjetoMonitoria();
		return listarRelatorios();
	}

	/**
	 * Cadastra e envia o relat�rio para PROGRAD
	 * finalizando a edi��o do relat�rio
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String enviar() throws ArqException {
		checkDocenteRole();

		// Evitando erro de voltar por cima
		Integer idProjeto = (Integer) getCurrentSession().getAttribute("id_projeto_enviado");

		if((idProjeto != null) && (idProjeto == obj.getProjetoEnsino().getId())){
			addMensagem(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD);
			return null;
		}

		ListaMensagens mensagens = new ListaMensagens();
		mensagens.addAll( obj.validate().getMensagens() );

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_MONITORIA);
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensMonitoria.RELATORIO_ENVIADO_PROGRAD_SUCESSO);
			//evitar erro de voltar por cima....
			getCurrentSession().setAttribute("id_projeto_enviado", obj.getProjetoEnsino().getId());
			return listarRelatorios();

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}
		return null;
	}

	/**
	 * Devolve o relat�rio para que o coordenador possa reedit�-lo
	 * procedimento permitido somente para os membros da PROGRAD e quando o coordenador
	 * alega que errou no preenchimento do relat�rio.
	 *
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String devolverRelatorioCoordenador() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		int id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, RelatorioProjetoMonitoria.class);

		try {

			prepareMovimento(SigaaListaComando.DEVOLVER_RELATORIO_COORDENADOR);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.DEVOLVER_RELATORIO_COORDENADOR);
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());

			addMensagemInformation("Relat�rio devolvido para o(a) Coordenador(a) com sucesso!");
			localizar(null);

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}
		return null;
	}

	/**
	 * Utilizado para habilitar/desabilitar o textarea (item C) para edi��o relacionado aos monitores
	 * que participaram do projeto de monitoria de acordo com a escolha dos radioButtons.
	 * 
	 * Valores utilizados para o evento:
	 * Sim - desabilita o textarea
	 * N�o ou Parcialmente - habilita o textarea
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * @param ActionEvent
	 * @return
	 */
	public void habilitarMotivosMonitores(ActionEvent event) {
		setReadOnly((obj.getMonitoresCumpriramExigencias() == 1) ? true : false);
	}

	/**
	 * Utilizado para habilitar/desabilitar o textarea (item C) para edi��o relacionado aos membros do
	 * projeto que participaram da monitoria de acordo com a escolha dos radioButtons.
	 * 
	 * Valores utilizados para o evento:
	 * Sim - desabilita o textarea
	 * Regular ou Ruim - habilita o textarea
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * @param ActionEvent
	 * @return
	 */
	public void habilitarMotivosMembros(ActionEvent event) {
		setReadOnly((obj.getParticipacaoMembrosSid() == 1) ? true : false);
	}

	/**
	 * M�todo n�o invocado por JSP�s
	 */
	@Override
	public String getDirBase() {
		return "/monitoria/RelatorioProjetoMonitoria";
	}

	public Collection<ProjetoEnsino> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}

	/**
	 *  M�todo respons�vel por chegar o localizar, com o intuito de saber se o projeto de monitoria possui ou n�o relat�rio final
	 * 
	 * 	M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/busca.jsp</li>
	 * </ul>
	 * @throws UnsupportedEncodingException
	 */
	public void localizarProjetoComRelatorio() throws UnsupportedEncodingException{
		localizar(null);
	}

	/**
	 *  M�todo respons�vel por chegar o localizar, com o intuito de saber se o projeto de monitoria possui ou n�o relat�rio final
	 * 
	 * 	M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/busca.jsp</li>
	 * </ul>
	 * @throws UnsupportedEncodingException
	 */
	public void localizarProjetoDistribuir() throws UnsupportedEncodingException{
		localizar(new Integer[] { TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO, TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO });
	}

	/**
	 * Localiza relat�rios
	 * 
	 * M�todo n�o invocado por JSP�s
	 * @throws UnsupportedEncodingException 
	 */
	private void localizar(Integer[] situacoes) throws UnsupportedEncodingException {

		erros.getMensagens().clear();
		if (relatoriosLocalizados != null) {
			relatoriosLocalizados.clear();
		}
		
		/* Analisando filtros selecionados */
		String tituloProj = null;
		Integer anoProjeto = null;
		Integer idServidorCoordenador = null;
		Integer idTipoRelatorio = null;

		ListaMensagens erros = new ListaMensagens();

		if (checkBuscaProjeto) {
			tituloProj = tituloProjeto; 
		}
		if (checkBuscaAno) {
			anoProjeto = buscaAnoProjeto;
		}
		if (checkBuscaServidor) {
			idServidorCoordenador = buscaServidor.getId();
		}
		if (checkBuscaTipoRelatorio) {
			idTipoRelatorio = buscaTipoRelatorio.getId();
		}

		RelatorioProjetoMonitorDao dao = getDAO( RelatorioProjetoMonitorDao.class );

		try {
			if( erros.isEmpty() ){
				relatoriosLocalizados = dao.filter(tituloProj, anoProjeto, idServidorCoordenador, idTipoRelatorio, situacoes);
			} else{
				addMensagens(erros);
			}
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao Buscar projetos!");
		}
	}

	public Integer getBuscaAnoProjeto() {
		return buscaAnoProjeto;
	}

	public void setBuscaAnoProjeto(Integer buscaAnoProjeto) {
		this.buscaAnoProjeto = buscaAnoProjeto;
	}

	public Servidor getBuscaServidor() {
		return buscaServidor;
	}

	public void setBuscaServidor(Servidor buscaServidor) {
		this.buscaServidor = buscaServidor;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}

	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}

	public boolean isCheckBuscaTipoRelatorio() {
		return checkBuscaTipoRelatorio;
	}

	public void setCheckBuscaTipoRelatorio(boolean checkBuscaTipoRelatorio) {
		this.checkBuscaTipoRelatorio = checkBuscaTipoRelatorio;
	}

	public boolean isComPaginacao() {
		return comPaginacao;
	}

	public void setComPaginacao(boolean comPaginacao) {
		this.comPaginacao = comPaginacao;
	}

	public TipoRelatorioMonitoria getBuscaTipoRelatorio() {
		return buscaTipoRelatorio;
	}

	public void setBuscaTipoRelatorio(TipoRelatorioMonitoria buscaTipoRelatorio) {
		this.buscaTipoRelatorio = buscaTipoRelatorio;
	}

	public Collection<RelatorioProjetoMonitoria> getRelatoriosLocalizados() {
		return relatoriosLocalizados;
	}

	public void setRelatoriosLocalizados(
			Collection<RelatorioProjetoMonitoria> relatoriosLocalizados) {
		this.relatoriosLocalizados = relatoriosLocalizados;
	}

	/**
	 * Permite listar os tipos de relat�rios no Select da View. Lista somente os
	 * tipos de relat�rios de projeto.
	 * 
	 * M�todo n�o invocado por JSP�s
	 *
	 * @return
	 */
	public Collection<TipoRelatorioMonitoria> getTiposRelatoriosProjetos() {

		GenericDAO dao = getGenericDAO();
		try {
			Collection<TipoRelatorioMonitoria> tiposRelatorios = dao.findByExactField(TipoRelatorioMonitoria.class, "contexto", "P");
			return tiposRelatorios;

		} catch (DAOException e) {
			notifyError(e);
			return null;
		}
	}
	
	/**
	 * Permite listar os todos os tipos de relat�rios cadastrados no Select da View
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/DistribuicaoRelatorioProjeto/lista.jsp</li>
	 *  <li>/monitoria/RelatorioMonitor/busca.jsp</li>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/busca.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getAllTiposRelatoriosProjetoCombo() {
		try {
			List<TipoRelatorioMonitoria> lista = new ArrayList<TipoRelatorioMonitoria>();
			GenericDAO dao = getGenericDAO();
			lista.addAll(dao.findAll(TipoRelatorioMonitoria.class));
			return toSelectItems(lista, "id", "descricao");

		} catch (DAOException e) {
			notifyError(e);
			return null;
		}
	}

	/**
	 * Carrega a avalia��o selecionada para a exibi��o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
	 *  <li>/monitoria/AvaliacaoRelatorioProjeto/lista.jsp</li>
	 *  <li>/projetos/MembroComissao/relatorios_avaliador.jsp</li>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/busca.jsp</li>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException{
		obj = new RelatorioProjetoMonitoria();
		obj.setId(getParameterInt("id",0));

		GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(obj.getId(), RelatorioProjetoMonitoria.class);
		return forward(getViewPage());
	}

	public ProjetoEnsino getBuscaProjetoEnsino() {
		return buscaProjetoEnsino;
	}

	public void setBuscaProjetoEnsino(ProjetoEnsino buscaProjetoEnsino) {
		this.buscaProjetoEnsino = buscaProjetoEnsino;
	}

	public boolean isCheckBuscaProjeto() {
		return checkBuscaProjeto;
	}

	public void setCheckBuscaProjeto(boolean checkBuscaProjeto) {
		this.checkBuscaProjeto = checkBuscaProjeto;
	}

	public String getTituloProjeto() {
		return tituloProjeto;
	}

	public void setTituloProjeto(String tituloProjeto) {
		this.tituloProjeto = tituloProjeto;
	}

}