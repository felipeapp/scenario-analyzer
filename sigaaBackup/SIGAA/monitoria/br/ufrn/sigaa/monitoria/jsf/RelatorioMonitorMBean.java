/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/11/2006
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.RelatorioProjetoMonitorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.RelatorioMonitor;
import br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria;
import br.ufrn.sigaa.monitoria.negocio.RelatorioMonitoriaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para cadastrar o relatório final do monitor.
 * 
 * @author ilueny santos
 * @author Victor Hugo
 * 
 */
@Component("relatorioMonitor") @Scope("session")
public class RelatorioMonitorMBean extends
		SigaaAbstractController<RelatorioMonitor> {

	

	/** Atributo usado na busca do relatório */
	private Collection<RelatorioMonitor> relatorios = new HashSet<RelatorioMonitor>();
	/** Atributo usado na busca do relatório */
	private Collection<DiscenteMonitoria> discentesMonitoria;
	/** Atributo usado na busca do relatório */
	private String tituloProjeto = null;

	/** Atributo utilizado na listagem de relatórios dos projetos do coordenador atual */
	private Collection<RelatorioMonitor> relatoriosMonitoresUsuarioAtualCoordenador;
	/** Atributo utilizado na listagem de relatórios dos projetos do coordenador atual */
	private int ano = CalendarUtils.getAnoAtual();
	/** Atributo utilizado na listagem de relatórios dos projetos do coordenador atual */
	private Discente discente = new Discente();
	/** Atributo utilizado na listagem de relatórios dos projetos do coordenador atual */
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();
	/** Atributo utilizado na listagem de relatórios dos projetos do coordenador atual */
	private TipoRelatorioMonitoria buscaTipoRelatorio = new TipoRelatorioMonitoria();
	

	/** Filtro de Ano utilizado na busca dos relatórios */
	private boolean checkBuscaAno;
	/** Filtro de Discenet utilizado na busca dos relatórios */
	private boolean checkBuscaDiscente;
	/** Filtro de Projeto utilizado na busca dos relatórios */
	private boolean checkBuscaProjeto;
	/** Filtro do Tipo do Relatório utilizado na busca dos relatórios */
	private boolean checkBuscaTipoRelatorio;
	
	/** Atributo utilizado para informar se vai alterar ou não */
	private boolean altera = false;
	/** Atributo utilizado para informar se vai enviar ou não */
	private boolean enviar = false;
	
	public boolean isCheckBuscaTipoRelatorio() {
		return checkBuscaTipoRelatorio;
	}

	public void setCheckBuscaTipoRelatorio(boolean checkBuscaTipoRelatorio) {
		this.checkBuscaTipoRelatorio = checkBuscaTipoRelatorio;
	}

	public RelatorioMonitorMBean() {
		obj = new RelatorioMonitor();
	}
	
	
	/**
	 * Verifica se o usuário logado é discente de monitoria ativo
	 * 
	 * @throws SegurancaException
	 */
	private void checkDiscenteMonitoria() throws SegurancaException {
		try{
			
			if (getDiscenteUsuario() == null) {
				throw new SegurancaException("O usuário logado precisa ser um discente para executar esta operação");
			}			
			DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
			Collection<DiscenteMonitoria> discentes = dao.findDiscenteMonitoriaAtivoByDiscente( getDiscenteUsuario() );
			if(ValidatorUtil.isEmpty(discentes)){
				throw new SegurancaException("Você não tem permissão para executar esta operação pois não faz parte de nenhum projeto de monitoria.");
			}
			
		}catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}		
	}
	
	

	/**
	 * Inicia o cadastro do tipo de relatório de monitor informado.
	 * 
	 * @param idTipoRelatorio Refere-se ao tipo de relatório que se deseja cadastrar.
	 * @return
	 * @throws ArqException
	 */
	private String iniciarRelatorio(int idTipoRelatorio) throws ArqException {
		checkDiscenteMonitoria();		
		prepareMovimento(ArqListaComando.CADASTRAR);
		setReadOnly(false);

		int id = getParameterInt("id", 0);

		if (id == 0) {
			addMensagemErro("Selecione o projeto de monitoria para o qual quer enviar o relatório.");
			return null;
			
		} else {

			RelatorioProjetoMonitorDao dao = getDAO(RelatorioProjetoMonitorDao.class);
			DiscenteMonitoria dm = dao.findByPrimaryKey(id,	DiscenteMonitoria.class);
			if (dm == null || !dm.isAtivo()) {
				addMensagemErro("Você não tem permissão para executar esta operação pois não faz parte de nenhum projeto de monitoria.");
				return null;
			}
			
			//Validando envio do relatório parcial
			ListaMensagens listaMensagens = new ListaMensagens();
			
			if (idTipoRelatorio == TipoRelatorioMonitoria.RELATORIO_PARCIAL) {
				RelatorioMonitoriaValidator.validaEnvioRelatorioParcialMonitor(dm, listaMensagens);
			}
			if (idTipoRelatorio == TipoRelatorioMonitoria.RELATORIO_FINAL) {
				RelatorioMonitoriaValidator.validaEnvioRelatorioFinalMonitor(dm, listaMensagens);
			}
			
			if (!listaMensagens.isEmpty()){
				addMensagens(listaMensagens);
				return null;
			}
			
			//Busca relatório ativo já cadastrado.
			RelatorioMonitor relatorioNoBanco = dao.findByDiscenteMonitoriaTipoRelatorio(dm, idTipoRelatorio);
			TipoRelatorioMonitoria tipoRelatorio = dao.findByPrimaryKey(idTipoRelatorio,	TipoRelatorioMonitoria.class);
			
			if (relatorioNoBanco == null) {
				obj = new RelatorioMonitor();
				obj.setAtivo(true);
				obj.setTipoRelatorio(tipoRelatorio);
				obj.setDiscenteMonitoria(dm);
				
			}else if (relatorioNoBanco.isEnviado()) {				
				addMensagem(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD);
				return null;
				
			} else {
				addMensagemErro("'" + tipoRelatorio.getDescricao() + "' já foi cadastrado para esta monitoria.");
				return null;
			}
		}
		
		setOperacaoAtiva(SigaaListaComando.ENVIAR_RELATORIO_MONITOR.getId());
		return forward(ConstantesNavegacaoMonitoria.RELATORIOMONITOR_FORM);
	}
	
	

	/**
	 * Inicia o cadastro/alteração do relatório parcial do projeto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioParcial() throws ArqException {
		return iniciarRelatorio(TipoRelatorioMonitoria.RELATORIO_PARCIAL);
	}

	/**
	 * Inicia o cadastro/alteração do relatório final do projeto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioFinal() throws ArqException {
		return iniciarRelatorio(TipoRelatorioMonitoria.RELATORIO_FINAL);		
	}

	/**
	 * Inicia o cadastro/alteração do relatório de desligamento do monitor do projeto.
	 * Para que o monitor seja desligado do projeto é obrigatório o envio
	 * deste relatório que deve ser validado pelo coordenador e pró-reitoria de
	 * graduação. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *		<li>sigaa.war/monitoria/RelatorioMonitor/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioDesligamento() throws ArqException {
		return iniciarRelatorio(TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR);
	}

	/**
	 * Carrega a lista de relatórios do discente logado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String listar() throws ArqException {

		prepareMovimento(SigaaListaComando.CADASTRAR_RELATORIO_MONITOR);
		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_MONITOR);
		discentesMonitoria = getDiscentesMonitoriaUsuarioLogado();
		return forward(ConstantesNavegacaoMonitoria.RELATORIOMONITOR_LISTA);

	}

	/**
	 * Lista todos os DiscentesMonitoria ativos do DISCENTE logado
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\DiscenteMonitoria\meus_projetos.jsp</li>
	 * </ul>
	 */
	public Collection<DiscenteMonitoria> getDiscentesMonitoriaUsuarioLogado() {		
		try {
			if (getUsuarioLogado().getDiscenteAtivo() == null) {
				addMensagemErro("O usuário logado precisa ser um discente para executar esta operação");
				return null;
			}

			// discentes de monitoria ativos do discente logado
			DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
			return dao.filter(null, null, getUsuarioLogado().getDiscenteAtivo()
					.getId(), null, null, null, null, null, null, null);

		} catch (DAOException e) {
			addMensagemErro("Erro ao Listar projeto(s) do usuário atual.");
			notifyError(e);
			return null;
		}
	}

	/**
	 * Lista todos os relatórios do discente logado
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamando por nenhuma JSP</li>
	 * </ul>
	 */
	@Override
	public Collection<RelatorioMonitor> getAll() {
		RelatorioProjetoMonitorDao dao = getDAO(RelatorioProjetoMonitorDao.class);

		try {
			Collection<RelatorioMonitor> relatorios = dao.filterRelatorioMonitor(null, null, getDiscenteUsuario().getId(), null);
			return relatorios;
		} catch (Exception e) {
			notifyError(e);
			return new ArrayList<RelatorioMonitor>();
		}
	}

	@Override
	public String getDirBase() {
		return "/monitoria/RelatorioMonitor";
	}

	public Collection<DiscenteMonitoria> getDiscentesMonitoria() {
		return discentesMonitoria;
	}

	public void setDiscentesMonitoria(
			Collection<DiscenteMonitoria> discentesMonitoria) {
		this.discentesMonitoria = discentesMonitoria;
	}

	/**
	 * Carrega a avaliação selecionada para a exibição chamado por:
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {

		setId();
		GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(obj.getId(), RelatorioMonitor.class);

		return forward(getViewPage());

	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public boolean isCheckBuscaDiscente() {
		return checkBuscaDiscente;
	}

	public void setCheckBuscaDiscente(boolean checkBuscaDiscente) {
		this.checkBuscaDiscente = checkBuscaDiscente;
	}

	public boolean isCheckBuscaProjeto() {
		return checkBuscaProjeto;
	}

	public void setCheckBuscaProjeto(boolean checkBuscaProjeto) {
		this.checkBuscaProjeto = checkBuscaProjeto;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	public Collection<RelatorioMonitor> getRelatorios() {
		return relatorios;
	}

	public void setRelatorios(Collection<RelatorioMonitor> relatorios) {
		this.relatorios = relatorios;
	}

	/**
	 * Realiza busca por relatórios de monitores de acordo com os parâmetros
	 * passados usado na tela de busca... 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/RelatorioMonitor/busca.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	@Override
	public String buscar() {

		if (relatorios != null)
			relatorios.clear();

		Integer buscaAno = null;
		Integer idDiscente = null;
		String  tituloProj = null;
		Integer idTipoRelatorio = null;

		if (checkBuscaAno)
			buscaAno = ano;
		if (checkBuscaDiscente)
			idDiscente = discente.getId();
		if (checkBuscaProjeto)
			tituloProj = getTituloProjeto();
		if (checkBuscaTipoRelatorio)
			idTipoRelatorio = buscaTipoRelatorio.getId();

		/*
		 * Se não tiver critério força a consulta pelo ano atual
		 */
		if (!checkBuscaDiscente && !checkBuscaProjeto && !checkBuscaAno
				&& !checkBuscaTipoRelatorio) {
			checkBuscaAno = true;
			buscaAno = CalendarUtils.getAnoAtual();
		}

		RelatorioProjetoMonitorDao dao = null;
		dao = getDAO(RelatorioProjetoMonitorDao.class);
		try {

			relatorios = dao.filterRelatorioMonitor(tituloProj, buscaAno,
					idDiscente, idTipoRelatorio);

		} catch (DAOException e) {
			notifyError(e);
		}

		return null;

	}

	/**
	 * Cadastra o relatório permitindo que o monitor faça alterações posteriores
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/form.jsp</li>
	 * </ul>
	 * 
	 */
	public String cadastrar() throws ArqException {
		checkDiscenteMonitoria();
		
		if(!checkOperacaoAtiva(SigaaListaComando.ENVIAR_RELATORIO_MONITOR.getId()) && !isAltera() ){
		    clearMensagens();
		    addMensagemErro("Relatório já foi salvo.");
		    return listar();
		}
		clearMensagens();
		
		try {
		    	if(!obj.isEnviado()){
		    	    MovimentoCadastro mov = new MovimentoCadastro();
		    	    mov.setCodMovimento(SigaaListaComando.CADASTRAR_RELATORIO_MONITOR);
		    	    mov.setObjMovimentado(obj);
		    	    obj = (RelatorioMonitor) execute(mov, getCurrentRequest());
		    	    addMensagemInformation("Relatório salvo com sucesso.");
		    	}
		    	else{
		    	    addMensagem(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD);
		    	    return listar();
		    	}
		    		
		} catch (Exception e) {
			addMensagem(e.getMessage());
			notifyError(e);
		}
		obj = new RelatorioMonitor();
		afterCadastrar();
		return listar();

	}

	/**
	 * Cadastra e envia o relatório para prograd(Pró-Reitoria de Graduação) finalizando a edição do
	 * relatório
	 * <br /> 
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/form.jsp</li>
	 * </ul>
	 */
	public String enviar() throws ArqException {
		checkDiscenteMonitoria();
		
		if(!checkOperacaoAtiva(SigaaListaComando.ENVIAR_RELATORIO_MONITOR.getId())){
		    clearMensagens();
		    if(!isEnviar()){
			addMensagemWarning("Reinicie operação.");
			return listar();
		    }
		    if(isAltera()){
			setOperacaoAtiva(SigaaListaComando.ENVIAR_RELATORIO_MONITOR.getId());
		    }
		    if(!checkOperacaoAtiva(SigaaListaComando.ENVIAR_RELATORIO_MONITOR.getId())){
			addMensagem(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD);
			 return listar();
		    }
		   
		   
		}
		clearMensagens();
		
		ListaMensagens mensagens = new ListaMensagens();
		mensagens.addAll(obj.validate().getMensagens());
			
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		try {

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ENVIAR_RELATORIO_MONITOR);
			mov.setObjMovimentado(obj);
			obj = (RelatorioMonitor) execute(mov, getCurrentRequest());

			if (obj.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR) {
			    addMensagem(MensagensMonitoria.RELATORIO_ENVIADO_COORDENACAO_PROJETO);
			} else {
			    addMensagem(MensagensMonitoria.RELATORIO_ENVIADO_PROGRAD_SUCESSO);
			}
			removeOperacaoAtiva();
			return listar();

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}

		return null;

	}

	/**
	 * Devolve o relatório para que o monitor possa reeditá-lo procedimento
	 * permitido somente para os membros da prograd(Pró-Reitoria de Graduação) e quando o monitor alega que
	 * errou no preenchimento do relatório.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/busca.jsp</li>
	 * </ul>
	 */
	public String devolverRelatorioMonitor() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		int id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, RelatorioMonitor.class);

		try {

			prepareMovimento(SigaaListaComando.DEVOLVER_RELATORIO_MONITOR);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.DEVOLVER_RELATORIO_MONITOR);
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());

			addMensagemInformation("Relatório devolvido para o(a) Monitor(a) com sucesso!");
			buscar();

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}

		return null;

	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public TipoRelatorioMonitoria getBuscaTipoRelatorio() {
		return buscaTipoRelatorio;
	}

	public void setBuscaTipoRelatorio(TipoRelatorioMonitoria buscaTipoRelatorio) {
		this.buscaTipoRelatorio = buscaTipoRelatorio;
	}

	/**
	 * Validação do relatório realizada pela coordenação do projeto chamado por:
	 * <br />
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/validacao_coordenacao.jsp</li>
	 * </ul>
	 */
	public String coordenacaoValidarRelatorioDesligamento() throws ArqException {
		checkDocenteRole();
		
		try {

			if (obj.getCoordenacaoValidouDesligamento() == null) {
				addMensagemErro("Parecer da Coordenação do Projeto: Campo obrigatório não informado.");
				return null;
			}

			if ((obj.getCoordenacaoValidouDesligamento() != null) && (!obj.getCoordenacaoValidouDesligamento()) 
					&& (obj.getObservacaoCoordenacaoDesligamento() != null) && ("".equals(obj.getObservacaoCoordenacaoDesligamento().trim()))) {
				addMensagemErro("Em caso de não validação o preenchimento do campo de observações é obrigatório.");
				return null;
			}
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.COORDENACAO_VALIDAR_RELATORIO_MONITOR);
			mov.setObjMovimentado(obj);
			obj = (RelatorioMonitor) execute(mov, getCurrentRequest());

			addMensagemInformation("Relatório analisado e enviado para PROGRAD com sucesso.");

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}

		return listarRelatoriosDesligamentoUsuarioAtualCoordena();
	}

	/**
	 * Validação do relatório realizada pela prograd(Pró-Reitoria de Graduação) 
	 * <br />
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/validacao_prograd.jsp</li>
	 * </ul>
	 */
	public String progradValidarRelatorioDesligamento() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try {

			if (obj.getProgradValidouDesligamento() == null) {
				addMensagemErro("Parecer da PROGRAD: Campo obrigatório não informado.");
				return null;
			}
			
			if ((obj.getProgradValidouDesligamento() != null) && (!obj.getProgradValidouDesligamento()) 
					&& (obj.getObservacaoProgradDesligamento() != null) && ("".equals(obj.getObservacaoProgradDesligamento().trim()))) {
				addMensagemErro("Em caso de não validação o preenchimento do campo de observações é obrigatório.");
				return null;
			}

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.PROGRAD_VALIDAR_RELATORIO_MONITOR);
			mov.setObjMovimentado(obj);
			obj = (RelatorioMonitor) execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}
		
		return listarRelatoriosDesligamento();
	}

	/**
	 * Seleciona o relatório, prepara e envia para formulário de validação da
	 * prograd(Pró-Reitoria de Graduação)
	 * <br />
	 * <ul> 
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarProgradValidarRelatorioDesligamento()
			throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		prepareMovimento(SigaaListaComando.PROGRAD_VALIDAR_RELATORIO_MONITOR);

		int id = getParameterInt("id");

		if (id == 0) {
			addMensagemErro("Selecione o relatório que deseja validar.");
			return null;
		} else {

			RelatorioMonitor relatorio = getGenericDAO().findByPrimaryKey(id,
					RelatorioMonitor.class);

			// validando reenvio
			if (relatorio != null) {
				if ((relatorio.isEnviado())
						&& (relatorio.getCoordenacaoValidouDesligamento() != null))
					obj = relatorio;
				else {
					addMensagemErro("Relatório não pode ser validado porque ainda não foi concluído.");
					return null;
				}
			}

		}

		return forward(ConstantesNavegacaoMonitoria.RELATORIOMONITOR_VALIDACAO_PROGRAD);
	}

	/**
	 * Seleciona o relatório, prepara e envia para formulário de validação da
	 * coordenação do projeto
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/RelatorioMonitor/lista_coordenacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCoordenacaoValidarRelatorioDesligamento()
			throws ArqException {
		checkDocenteRole();
		
		prepareMovimento(SigaaListaComando.COORDENACAO_VALIDAR_RELATORIO_MONITOR);

		int id = getParameterInt("id");

		if (id == 0) {
			addMensagemErro("Selecione o relatório que deseja validar.");
			return null;
		} else {

			RelatorioMonitor relatorio = getGenericDAO().findByPrimaryKey(id,
					RelatorioMonitor.class);

			// validando reenvio
			if (relatorio != null) {
				if ((relatorio.isEnviado())
						&& (relatorio.getCoordenacaoValidouDesligamento() == null))
					obj = relatorio;
				else {
					addMensagemErro("Relatório não pode ser validado porque ainda não foi concluído.");
					return null;
				}
			}

		}

		return forward(ConstantesNavegacaoMonitoria.RELATORIOMONITOR_VALIDACAO_COORDENACAO);
	}

	public Collection<RelatorioMonitor> getRelatoriosMonitoresUsuarioAtualCoordenador() {
		return relatoriosMonitoresUsuarioAtualCoordenador;
	}

	public void setRelatoriosMonitoresUsuarioAtualCoordenador(
			Collection<RelatorioMonitor> relatoriosMonitoresUsuarioAtualCoordenador) {
		this.relatoriosMonitoresUsuarioAtualCoordenador = relatoriosMonitoresUsuarioAtualCoordenador;
	}

	/**
	 * Lista todos os relatórios de desligamento dos monitores de projetos onde
	 * o usuário atual é coordenador e redireciona para a página de listagem
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String listarRelatoriosDesligamentoUsuarioAtualCoordena()
			throws ArqException {
		checkDocenteRole();
		
		RelatorioProjetoMonitorDao dao = getDAO(RelatorioProjetoMonitorDao.class);
		relatoriosMonitoresUsuarioAtualCoordenador = dao.findByCoordenador(
				getUsuarioLogado().getServidor().getId(),
				TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR);

		return forward(ConstantesNavegacaoMonitoria.RELATORIOMONITOR_LISTA_COORDENACAO);
	}

	
	/**
	 * Lista todos os relatórios de desligamento cadastrados pelos discentes.	 * 
	 * Utilizado para validação de relatórios de desligamento pela prograd(Pró-Reitoria de Graduação).
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/RelatorioMonitor/lista_prograd.jsf</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String listarRelatoriosDesligamento() throws DAOException {		
			relatorios.clear();
			Integer anoProjeto = null;
			if (checkBuscaAno) {
				anoProjeto = ano;
			}			
			relatorios = getDAO(RelatorioProjetoMonitorDao.class).findRelatorioDesligamentoParaValidacao(anoProjeto);
			return forward(ConstantesNavegacaoMonitoria.RELATORIOMONITOR_LISTA_PROGRAD);
	}
	
	public String getTituloProjeto() {
		return tituloProjeto;
	}

	public void setTituloProjeto(String tituloProjeto) {
		this.tituloProjeto = tituloProjeto;
	}
	
	/**
	 * atualiza/altera relatório do monitor
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/RelatorioMonitor/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
	    try {

		beforeAtualizar();
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);

		RelatorioProjetoMonitorDao dao = getDAO(RelatorioProjetoMonitorDao.class);
		setId();
		
		setReadOnly(false);

		this.obj = dao.findByPrimaryKey(obj.getId(), RelatorioMonitor.class);
		if(!obj.isAtivo()){
		    addMensagemErro("Relatório já foi removido.");
		    afterAtualizar();
		    return listar();
		}

		setConfirmButton("Alterar");
		afterAtualizar();

	} catch (Exception e) {
		notifyError(e);
		addMensagemErroPadrao();
		e.printStackTrace();
	}

	return forward(getFormPage());
	}
	
	/** Método utilizado para realizar ações antes de atualizar */
	@Override
	protected void beforeAtualizar() throws ArqException {
	    altera = true;
	    enviar = true;
	}
	
	/** Método utilizado para realizar ações depois de cadastrar */
	@Override
	protected void afterCadastrar() throws ArqException {
	    altera = false;
	    enviar = false;
	}
	
	/**
	 * Inativa relatório do monitor
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/RelatorioMonitor/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
	    if(!checkOperacaoAtiva(ArqListaComando.DESATIVAR.getId())){
		clearMensagens();
		addMensagemErro("Relatório já foi removido.");
		return listar();
	    }else
		return super.inativar();
	}
	
	/**
	 * Inicia o procedimento de remoção do relatório que deverá ser confirmado 
	 * na próxima tela. Prepara para desativar o relatório do monitor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/RelatorioMonitor/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preRemoverRelatorio() throws ArqException {
		
		int id = getParameterInt("id", 0);
		
		if(obj == null) {
			obj = new RelatorioMonitor();
			((PersistDB) obj).setId(id);
		}else {
			((PersistDB) obj).setId(id);
		}
		
	        obj = getGenericDAO().findByPrimaryKey(obj.getId(), RelatorioMonitor.class);
	        
	        if (obj.getDataEnvio() != null) {
	            addMensagemErro("Este relatório já foi enviado e não pode ser removido.");
	            return listar();
	        }

	        prepareMovimento(ArqListaComando.DESATIVAR);
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		setReadOnly(true);
		setConfirmButton("Remover");
		return forward(ConstantesNavegacaoMonitoria.RELATORIOMONITOR_FORM);
	}
	
	/** Método utilizado para realizar ações depois de inativar */
	@Override
	protected void afterInativar() {
		obj = new RelatorioMonitor();
	}
	
	/** Método utilizado para realizar ações antes de inativar */
	@Override
	protected String forwardInativar() {	    
	    try {
		return listar();
	    } catch (ArqException e) {
		notifyError(e);
		return null;
	    }
	}

	public boolean isAltera() {
	    return altera;
	}

	public void setAltera(boolean altera) {
	    this.altera = altera;
	}

	public boolean isEnviar() {
	    return enviar;
	}

	public void setEnviar(boolean enviar) {
	    this.enviar = enviar;
	}
	
}