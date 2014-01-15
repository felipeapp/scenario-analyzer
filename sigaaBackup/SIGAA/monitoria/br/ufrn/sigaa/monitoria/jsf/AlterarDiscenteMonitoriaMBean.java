/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/03/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_INICIO_MENOR_FIM;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.negocio.AlterarOrientacaoMov;
import br.ufrn.sigaa.monitoria.negocio.DiscenteMonitoriaMov;
import br.ufrn.sigaa.monitoria.negocio.DiscenteMonitoriaValidator;

/**
 * MBean responsável pelo caso de uso de alterar orientação de monitores
 * 
 * @author Victor Hugo
 * @author ilueny santos
 * 
 */
@Component("alterarDiscenteMonitoria")
@Scope("session")
public class AlterarDiscenteMonitoriaMBean extends SigaaAbstractController<DiscenteMonitoria> {
	
    	/** Representa a página de origem a partir da qual foi dado o comando para finalizar o discente. */
	private String paginaOrigemFinalizar = "";

	/** Representa o projeto do qual o discente faz parte. */
	private ProjetoEnsino projeto = new ProjetoEnsino();

	/** Lista de possíveis orientadores para o discente selecionado. */
	private Collection<EquipeDocente> docentes = new HashSet<EquipeDocente>();

	/** Utilizado na alteração do tipo de vínculo do discente como o projeto.*/
	private int novoTipoMonitoria;

	/** Permite que o discente seja finalizado sem que tenha emitido os relatórios obrigatórios. */
	private boolean validarRelatorios = true;
	
	
	
	public AlterarDiscenteMonitoriaMBean() {
		obj = new DiscenteMonitoria();
	}

	/**
	 * Confirma a alteração do monitor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/form.jsp</li>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/reativar_form.jsp</li>
	 * </ul>
	 * 
	 * @throws ParseException  
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String alterar() throws ArqException, ParseException {
	    	String result = null;
		if (getConfirmButton().equalsIgnoreCase("Finalizar Monitoria")) {
		    result = finalizarMonitoria();		
		}

		if (getConfirmButton().equalsIgnoreCase("Salvar")) {
		    result = alterarOrientadores();
		}

		if (getConfirmButton().equalsIgnoreCase("Excluir Monitoria")) {
		    result = excluirMonitoria();
		}

		if (getConfirmButton().equalsIgnoreCase("Reativar Monitoria")) {
		    result = reativaMonitoria();
		}
		return result;
	}
	
	/**
	 * Método não invocado por JSP.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
	    if (!getAcessoMenu().isCoordenadorMonitoria() && !isUserInRole(SigaaPapeis.GESTOR_MONITORIA)) {
	    	MensagemAviso msgAviso = UFRNUtils.getMensagem(MensagensMonitoria.PERMISSAO_FINALIZAR_MONITOR);		
	    	throw new SegurancaException(msgAviso.getMensagem());
	    }
	}
	

	/** 
	 * Preparando movimento para excluir monitor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws ArqException 
	 * @throws ArqException 
	 */
	public String preExcluirMonitoria() throws ArqException {	    
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
		int idMonitor = getParameterInt("id");
		obj = dao.findByPrimaryKey(idMonitor, DiscenteMonitoria.class);
		obj.getOrientacoes().iterator();
		prepareMovimento(SigaaListaComando.EXCLUIR_DISCENTEMONITORIA);
		setConfirmButton("Excluir Monitoria");
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_FORM);
	}
	/**
	 * Método usado para fazer a exclusão de projetos de monitoria. 
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws DAOException
	 */
	public String excluirMonitoria() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
		mov.setDiscenteMonitoria(obj);
		mov.setCodMovimento(SigaaListaComando.EXCLUIR_DISCENTEMONITORIA);
		try {
		    execute(mov, getCurrentRequest());
		    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		}
		obj = new DiscenteMonitoria();
		((ConsultarMonitorMBean) getMBean("consultarMonitor")).localizar();
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_LISTA);

	}

	/**
	 * Preparando movimento para finalizar monitor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * 	<li>sigaa.war/monitoria/CancelarBolsas/lista.jsp</li>
	 * 	<li>sigaa.war/monitoria/EnvioFrequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @throws SegurancaException 
	 */
	public String preFinalizarMonitoria() throws SegurancaException {
	    checkChangeRole();		
	    paginaOrigemFinalizar = getParameter("paginaOrigem") != null ? getParameter("paginaOrigem") : "";

	    try {
		setValidarRelatorios(true);
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
		Integer idMonitor = getParameterInt("id", 0);
		if ((idMonitor == null) || (idMonitor.equals(0))) {
		    addMensagemErro("Monitor não selecionado.");
		    return redirectMesmaPagina();
		}

		obj = dao.findByPrimaryKey(idMonitor, DiscenteMonitoria.class);
		obj.getOrientacoes().iterator();
		prepareMovimento(SigaaListaComando.FINALIZAR_DISCENTEMONITORIA);
		setConfirmButton("Finalizar Monitoria");
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_FORM);

	    } catch (Exception e) {
		notifyError(e);
		return null;
	    }

	}

	/**
	 * Remove (DESATIVA) o discente selecionado
	 * 
	 * Não é chamado por JSPs diretamente.
	 * 
	 * @return
	 */
	public String finalizarMonitoria() throws SegurancaException {
		checkChangeRole();

		if (obj.getId() == 0) {
			addMensagemErro("Não há Monitor selecionado.");
			return null;
		} else if (obj.getDataFim() == null) {
			addMensagemErro("Data de finalização da monitoria deve ser informada.");
			return null;
		} else if (obj.getDataInicio() == null) {
			addMensagemErro("Data de início da monitoria deve ser informada.");
			return null;
		} else {

			DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
			mov.setDiscenteMonitoria(obj);
			mov.setCodMovimento(SigaaListaComando.FINALIZAR_DISCENTEMONITORIA);
			// permite que a prograd execute procedimentos sem realizar validação..
			mov.setValidar(true);
			mov.setValidarRelatorios(validarRelatorios);

			try {

				DiscenteMonitoria d = (DiscenteMonitoria) execute(mov, getCurrentRequest());
				addMensagemInformation("Monitoria de '" + d.getDiscente().getNome()
						+ "'  finalizada com sucesso.");

			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			} catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
			}

			if (paginaOrigemFinalizar.equalsIgnoreCase("CANCELAR_BOLSAS")) {				
				((CancelarBolsaMonitoriaMBean)getMBean("cancelarBolsaMonitoria")).buscar();
				return forward("/monitoria/CancelarBolsas/form.jsp");
				
			} else if (getConfirmButton().equalsIgnoreCase("Finalizar Monitoria")){
				if(getUsuarioLogado().getServidor() == null){
					return forward(getListPage());
				}
				return ((ConsultarMonitorMBean)getMBean("consultarMonitor")).coordenadorAlterarMonitor();
				
			} else {
				((ConsultarMonitorMBean)getMBean("consultarMonitor")).localizar();
				return forward(getListPage());
			}
		}

	}

	/**
	 * Preparando movimento para re-ativar monitoria.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * </ul>
	 *
	 * @return
	 */
	public String preReativarMonitoria() {
		try {

			checkRole(SigaaPapeis.GESTOR_MONITORIA);
			// não valida envio de relatórios do discente
			setValidarRelatorios(false);
			EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
			int idMonitor = getParameterInt("id");

			obj = dao.findByPrimaryKey(idMonitor, DiscenteMonitoria.class);
			obj.getOrientacoes().iterator();
			prepareMovimento(SigaaListaComando.REATIVAR_MONITORIA);
			setConfirmButton("Reativar Monitoria");
			return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_REATIVAR_FORM);

		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}

		return null;
	}

	/**
	 * Reativa a monitoria selecionada
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @throws SegurancaException
	 */
	private String reativaMonitoria() throws SegurancaException {

		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		if (obj.getId() == 0) {
			addMensagemErro("Não há Monitor selecionado!");
			return null;
		} else {

			DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
			mov.setDiscenteMonitoria(obj);
			mov.setCodMovimento(SigaaListaComando.REATIVAR_MONITORIA);
			mov.setValidar(true);
			mov.setValidarRelatorios(false);
			try {
				DiscenteMonitoria d = (DiscenteMonitoria) execute(mov, getCurrentRequest());
				addMensagemInformation("Monitoria de '" + d.getDiscente().getNome() + "'  foi reativada com sucesso.");
				if ( Sistema.isSipacAtivo() && d.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA) {
					addMensagemWarning("Lembre-se de reativar a Bolsa do(a) discente no SIPAC.");
				}
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			} catch (Exception e) {
				tratamentoErroPadrao(e);
			}

			((ConsultarMonitorMBean) getMBean("consultarMonitor")).localizar();
			return forward(getListPage());
		}
	}

	@Override
	public String getDirBase() {
		return "/monitoria/AlterarDiscenteMonitoria";
	}
	/**
	 * 
	 * Método usado para redirecionar para uma página 
	 * que possibilita algumas operações sobre um 
	 * monitor, como por exemplo, alterar o seu vínculo.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarVinculoMonitor() throws ArqException {

		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		setValidarRelatorios(true);
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
		int idMonitor = getParameterInt("id");
		obj = dao.findByPrimaryKey(idMonitor, DiscenteMonitoria.class);
		obj.getOrientacoes().iterator();
		prepareMovimento(SigaaListaComando.ALTERAR_VINCULO_DISCENTEMONITORIA);
		setConfirmButton("Alterar Vínculo do Monitor");
		setNovoTipoMonitoria(obj.getTipoVinculo().getId());
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_ALTERAR_VINCULO);

	}

	/**
	 * Este método carrega as orientações dos monitores e altera o tipo de
	 * vínculo do monitor de Não remunerado para Bolsista Remunerado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/alterar_vinculo_monitor.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String alterarVinculoMonitor() throws ArqException {

		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		if (obj.getTipoVinculo().getId() != getNovoTipoMonitoria()) {

			try {

				DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
				mov.setCodMovimento(SigaaListaComando.ALTERAR_VINCULO_DISCENTEMONITORIA);
				mov.setDiscenteMonitoria(obj);
				mov.setNovoTipoMonitoria(novoTipoMonitoria);
	
				//@negocio: Permite que a PROGRAD execute procedimentos sem realizar validação..
				mov.setValidar(true);
				mov.setValidarRelatorios(validarRelatorios);
				execute(mov, getCurrentRequest());
				addMensagemInformation("Tipo de vínculo alterado com sucesso!");

			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			} catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
			}

		} else {
			addMensagemErro("Alteração do vínculo não realizada!");
		}

		obj = new DiscenteMonitoria();
		// atualiza tela com dados novo da mudança....
		((ConsultarMonitorMBean) getMBean("consultarMonitor")).localizar();
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_LISTA);

	}

	/**
	 * Este método carrega as orientações dos monitores e leva para a tela de
	 * alteração de orientadores
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarOrientadores() throws ArqException {
	    
		checkChangeRole();
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
		int idMonitor = getParameterInt("id");
		obj = dao.findByPrimaryKey(idMonitor, DiscenteMonitoria.class);
		obj.getOrientacoes().iterator();

		// Carregando todos os orientadores do projeto
		docentes.clear();
		docentes = dao.findByProjeto(obj.getProjetoEnsino().getId(), true);

		// Removendo os docentes que já são orientadores do monitor
		for (Iterator<EquipeDocente> it = docentes.iterator(); it.hasNext();) {
			EquipeDocente membro = it.next();
			for (Orientacao orientacaoObj : obj.getOrientacoes()) {
				if (orientacaoObj.getEquipeDocente().getId() == membro.getId() && !orientacaoObj.isFinalizada()) {
					it.remove();
					break;
				}
			}
		}

		setConfirmButton("Salvar");
		prepareMovimento(SigaaListaComando.ALTERAR_ORIENTACOES);
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_FORM);

	}
	
	
	/**
	 * Este método carrega as orientações dos monitores e leva para a tela de
	 * alteração de orientadores
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarOrientadoresErros() throws ArqException {
	    
		checkChangeRole();
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
		obj = dao.findByPrimaryKey(obj.getId(), DiscenteMonitoria.class);
		obj.getOrientacoes().iterator();

		// Carregando todos os orientadores do projeto
		docentes.clear();
		docentes = dao.findByProjeto(obj.getProjetoEnsino().getId(), true);

		// Removendo os docentes que já são orientadores do monitor
		for (Iterator<EquipeDocente> it = docentes.iterator(); it.hasNext();) {
			EquipeDocente membro = it.next();
			for (Orientacao orientacaoObj : obj.getOrientacoes()) {
				if (orientacaoObj.getEquipeDocente().getId() == membro.getId() && !orientacaoObj.isFinalizada()) {
					it.remove();
					break;
				}
			}
		}

		setConfirmButton("Salvar");
		prepareMovimento(SigaaListaComando.ALTERAR_ORIENTACOES);
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_FORM);

	}

	/**
	 * Este método valida os orientadores selecionados e invoca o processador
	 * para persistir as novas orientações.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws ParseException 
	 */
	public String alterarOrientadores() throws ArqException, ParseException {
		checkChangeRole();

		// Permite alteração das orientação de monitores convocados.
		// Define a data início e data fim temporariamente.
		if (obj.isConvocado()) {
			obj.setDataInicio(obj.getProjetoEnsino().getProjeto().getDataInicio());
			obj.setDataFim(obj.getProjetoEnsino().getProjeto().getDataFim());
		}

		ListaMensagens mensagem = new ListaMensagens();
		DiscenteMonitoriaValidator.validaDadosPrincipais(obj, mensagem);
		
		
		if (!mensagem.isEmpty()) {
		    addMensagens(mensagem);
		    return null;
		}

		
		Collection<Orientacao> orientacoesAtualizar = new ArrayList<Orientacao>();
		Collection<Orientacao> orientacoesAdicionar = new ArrayList<Orientacao>();

		forGeral: for (EquipeDocente docente : docentes) {

			// orientador selecionado, adicionar orientação caso não exista
			if (docente.isSelecionado()) {

				for (Orientacao orientacaoObj : obj.getOrientacoes()) {
					if ((orientacaoObj.getEquipeDocente().getId() == docente.getId())
						&& (!orientacaoObj.isFinalizada())) {
						continue forGeral;
					}
				}

				ListaMensagens lista = new ListaMensagens();
				
				// adicionando novo orientador
				Orientacao orientacao = new Orientacao();
				orientacao.setDiscenteMonitoria(obj);
				orientacao.setDataInicio(docente.getDataInicioOrientacao());
				orientacao.setDataFim(docente.getDataFimOrientacao());
				orientacao.setEquipeDocente(docente);
				orientacao.setAtivo(true);
				orientacao.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
				
				// testando se o docente selecionado já não tem mais 2
				// orientandos neste projeto
				//
				// MOSTRA SOMENTE UM AVISO SE PASSAR DE 2 ALUNOS POR ORIENTADOR,
				// NÃO BLOQUEIA (prograd solicitou que fosse assim!)...
				DiscenteMonitoriaValidator.validaMaximoOrientacoesDocentes(docente, orientacao.getDataInicio(), orientacao.getDataFim(), lista);

				lista.addAll(orientacao.validate());
				
				if (!lista.isEmpty()) {
				    addMensagens(lista);
				    return null;
				}
				
				orientacoesAdicionar.add(orientacao);

			}

		}

		// verifica se houve alterações nas orientações
		for (Orientacao ori : obj.getOrientacoes()) {

			if ((ori.getDataFim() == null) && (ori.isFinalizar())) {
				addMensagemErro("Para finalizar a orientação informe a Data Fim.");
				return null;
			}

			if ((ori.getDataInicio() == null) && (ori.isFinalizar())) {
				addMensagemErro("Para finalizar a orientação informe a Data Início.");
				return null;
			}
			if (ori.getDataInicio() != null && ori.getDataFim() != null && ori.getDataInicio().getTime() > ori.getDataFim().getTime()) {
				addMensagem(DATA_INICIO_MENOR_FIM, "Período de Orientações do Discente");
				return preAlterarOrientadoresErros();
			}
			if (ori.isFinalizar() && ori.getDataInicio() != null && ori.getDataFim() != null && ori.getDataInicio().getTime() > (new Date()).getTime()) {
				addMensagem(DATA_INICIO_MENOR_FIM, "Período de Orientações do Discente");
				return preAlterarOrientadoresErros();
			}
			
			if(!ori.isFinalizada()){
				if(!CalendarUtils.isDentroPeriodo(obj.getDataInicio(), obj.getDataFim(), ori.getDataInicio()) ){
					addMensagemErro("Data Início fora do período da vigência da monitoria.");
					return null;
				}
				if(!CalendarUtils.isDentroPeriodo(obj.getDataInicio(), obj.getDataFim(), ori.getDataFim())){
					addMensagemErro("Data Fim fora do período da vigência da monitoria.");
					return null;
				}
			}

			// finalizando orientação
			if ((ori.getDataFim() != null) && (ori.getDataInicio() != null) && (ori.isFinalizar())) {
				// valida datas da orientação...
			    	ListaMensagens lista = ori.validate();
				if ((lista != null) && (!lista.isEmpty())) {
				    	addMensagens(lista);
					return null;
				}
			}

			orientacoesAtualizar.add(ori);
			ori.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());

		}

		try {

			AlterarOrientacaoMov mov = new AlterarOrientacaoMov();
			mov.setOrientacoesAdicionar(orientacoesAdicionar);
			mov.setOrientacoesAtualizar(orientacoesAtualizar);
			mov.setObjMovimentado(obj);

			mov.setCodMovimento(SigaaListaComando.ALTERAR_ORIENTACOES);
			execute(mov, getCurrentRequest());

			addMensagemInformation("Orientações alteradas com sucesso!");

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		obj = new DiscenteMonitoria();
		
		if ( isPortalDocente() )
			return ((ConsultarMonitorMBean) getMBean("consultarMonitor")).coordenadorAlterarMonitor();
		else {
			((ConsultarMonitorMBean) getMBean("consultarMonitor")).localizar();
			return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_LISTA);
		}
	}
	/**
	 * Método usado para redirecionar para uma página 
	 * que possibilita algumas operações sobre um 
	 * monitor, como por exemplo, alterar a nota do monitor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarNotas() throws ArqException {
		prepareMovimento(SigaaListaComando.ALTERAR_NOTA_SELECAO_MONITORIA);
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
		int idMonitor = getParameterInt("id");
		obj = dao.findByPrimaryKey(idMonitor, DiscenteMonitoria.class);
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_NOTA_FORM);
	}

	/**
	 * Altera notas de monitores.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/AlterarDiscenteMonitoria/alterar_nota_monitor.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String alterarNotas() throws ArqException, RemoteException {

		if (obj.getNota() == null || obj.getNotaProva() == null) {
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nota");
		    return null;
		}

		if ((obj.getNotaProva() > 10 || obj.getNotaProva() < 0)
				|| (obj.getNota() > 10 || obj.getNota() < 0)) {
		    	addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Nota");
			return null;
		}

		try {
			DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
			mov.setCodMovimento(SigaaListaComando.ALTERAR_NOTA_SELECAO_MONITORIA);
			mov.setDiscenteMonitoria(obj);
			mov.setValidar(true);
			mov.setValidarRelatorios(false);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return forward(getFormPage());
		}
		String result = null;
		
		if (getAcessoMenu().isCoordenadorMonitoria()) {
		    result = ((ConsultarMonitorMBean) getMBean("consultarMonitor")).coordenadorAlterarMonitor();
		}
		
		return result; 
	}
	
	/** Retorna para listagem
	 *  <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *  <li>sigaa.war/monitoria/AlterarDiscenteMonitoria/form.jsp</li>
	 *  </ul>
	 *  
	 *  
	 *  */
	public String voltarListagem(){
		return forward(ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_LISTA);
	}

	public Collection<EquipeDocente> getDocentes() {
		return docentes;
	}

	public void setDocentes(Collection<EquipeDocente> docentes) {
		this.docentes = docentes;
	}

	public ProjetoEnsino getProjeto() {
		return projeto;
	}

	public void setProjeto(ProjetoEnsino projeto) {
		this.projeto = projeto;
	}

	/**
	 * Informa o novo tipo de vínculo do monitor. usando na operação de mudança
	 * de vínculo do monitor quando vaga uma bolsa e um não remunerado assume a
	 * bolsa.
	 * 
	 * @return
	 */
	public int getNovoTipoMonitoria() {
		return novoTipoMonitoria;
	}

	public void setNovoTipoMonitoria(int novoTipoMonitoria) {
		this.novoTipoMonitoria = novoTipoMonitoria;
	}

	public boolean isValidarRelatorios() {
		return validarRelatorios;
	}

	public void setValidarRelatorios(boolean validar) {
		this.validarRelatorios = validar;
	}
	
	public String getPaginaOrigemFinalizar() {
		return paginaOrigemFinalizar;
	}

	public void setPaginaOrigemFinalizar(String paginaOrigemFinalizar) {
		this.paginaOrigemFinalizar = paginaOrigemFinalizar;
	}

}