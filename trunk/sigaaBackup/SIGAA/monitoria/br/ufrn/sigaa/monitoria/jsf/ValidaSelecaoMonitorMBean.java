/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/01/2008
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.negocio.DiscenteMonitoriaValidator;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaMov;

/**
 * MBean responsável por realizar a validação pela prograd(Pró-Reitoria de Graduação) do resultado da
 * seleção de projetos de monitoria cadastrados pelos coordenadores dos
 * respectivos projetos
 * 
 * @author ilueny santos
 * @author david
 * 
 */
@Component("validaSelecaoMonitor") 
@Scope("session")
public class ValidaSelecaoMonitorMBean extends SigaaAbstractController<ProjetoEnsino> {

	/** Lista de projetos utilizada na busca de projetos com provas cadastradas.*/
	private Collection<ProjetoEnsino> projetos;
	
	/** Prova seletiva selecionada para validação*/
	private ProvaSelecao prova = new ProvaSelecao();	
	
	/** Centro selecionado para busca. */
	private Integer idCentro;
	
	/** Ano selecionado para busca. */
	private Integer ano;
	
	/** Título selecionado para busca. */
	private String titulo;
	
	/** monitor */
	private DiscenteMonitoria discenteMonitor;
	
	/** vínculo monitior */
	private Integer idVinculo;
	
	/** situacção do monitor */
	private Integer idSituacao;
	
	/** observações */
	private String observacoes;
	
	/** Construtor */
	public ValidaSelecaoMonitorMBean() {
		obj = new ProjetoEnsino();
		ano = CalendarUtils.getAnoAtual();
	}

	/**
	 * Lista situações possíveis para uma seleção
	 */
	public List<SelectItem> getAllStatusSelecaoMonitoriaCombo() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(0, " -- SELECIONE --"));
		lista.add(new SelectItem(SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO, 			"AGUARDANDO CONVOCAÇÃO"));
		lista.add(new SelectItem(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, 			"ASSUMIU MONITORIA"));
		lista.add(new SelectItem(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA, 		"CONVOCADO"));
		lista.add(new SelectItem(SituacaoDiscenteMonitoria.CONVOCADO_MAS_REJEITOU_MONITORIA, 	"REJEITOU MONITORIA"));
		lista.add(new SelectItem(SituacaoDiscenteMonitoria.INVALIDADO_PROGRAD, 			"MONITORIA INVÁLIDA"));
		lista.add(new SelectItem(SituacaoDiscenteMonitoria.NAO_APROVADO, 			"NÃO APROVADO"));
		return lista;
	}

	
	/**
	 * Inicia a busca por provas seletivas cadastradas.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\index.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws ArqException
	 */
	public String iniciarBuscaSelecoes() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		projetos = new ArrayList<ProjetoEnsino>();
		return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_LISTA);				
	}
	
	
	/**
	 * Carrega os discentes da prova selecionada e redireciona para a tela de
	 * validar discentes inscritos no resultado da seleção;
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\provas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarProva() throws ArqException {		
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		int idProva = getParameterInt("id", 0);
		prova = getGenericDAO().findByPrimaryKey(idProva, ProvaSelecao.class);
		prova.setResultadoSelecao(getDAO(DiscenteMonitoriaDao.class).findByProvaSeletiva(idProva));
		prepareMovimento(SigaaListaComando.VALIDA_CADASTRO_SELECAO);
		return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_FORM);
	}
	
	/**
	 * Carrega os discentes da prova selecionada e redireciona para a tela de
	 * validar discentes inscritos no resultado da seleção;
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\provas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String voltaTelaProva() throws ArqException{
		prova.setResultadoSelecao(getDAO(DiscenteMonitoriaDao.class).findByProvaSeletiva(prova.getId()));
		prepareMovimento(SigaaListaComando.VALIDA_CADASTRO_SELECAO);
		return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_FORM);

	}
	
	/**
	 * redireciona para a tela de listagem de provas;
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String voltaTelaListagem() throws ArqException{
		return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_PROVAS);

	}
	
	/**
	 * redireciona para a tela da prova selecionada;
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\alterar_resultado.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String voltaProva() throws ArqException{
		return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_FORM);

	}

	
	/**
	 * Seleciona o projeto para validação das provas
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarProjeto() throws ArqException {		
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		int idProjeto = getParameterInt("id", 0);
		if (idProjeto > 0 ) {
			obj = getGenericDAO().findByPrimaryKey(idProjeto, ProjetoEnsino.class);
			return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_PROVAS);
		}else {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Projeto");
			return null;
		}		
	}


	/**
	 * Recupera os docentes do projeto de monitoria e redireciona para página de validação de monitores. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\lista.jsp</li>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\provas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String filtrar() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		atualizarProjetosComSelecao(idCentro, ano, titulo);
		return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_LISTA);
	}

	
	/**
	 * Recupera os docentes participantes de todos os projetos de monitoria com provas seletivas cadastradas
	 * de um determinado centro ou de todos os centros.
	 * <br />
	 * Não invocado por JSP(s).
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * 
	 */
	public void atualizarProjetosComSelecao(Integer idCentro, Integer ano, String titulo) throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		projetos = getDAO(ProjetoMonitoriaDao.class).findByCadastroSelecao(idCentro, ano, titulo);
	}

	/**
	 * 
	 * Permiti que a prograd(Pró-Reitoria de Graduação), através de procedimento formal, desfaça a
	 * operação de validação do cadastro de seleção feito pelo professor. Que já
	 * tinha sido validado por ele mas que por algum motivo, deve ser desfeito.
	 * 
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\form.jsp</li>
	 * </ul>
	 * 
	 */
	public String desvalidarResultadoMonitor() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		int id = getParameterInt("id", 0);
		DiscenteMonitoria monitor = getGenericDAO().findByPrimaryKey(id, DiscenteMonitoria.class);
		monitor.getOrientacoes().iterator();
		
		if (!ValidatorUtil.isEmpty(monitor)) {

			ListaMensagens lista = new ListaMensagens();
			DiscenteMonitoriaValidator.validaDiscenteComBolsa(monitor, lista);
			if (!lista.isEmpty()) {
				addMensagens(lista);
				return null;
			}
			
			prepareMovimento(SigaaListaComando.DESVALIDA_SELECAO_MONITORIA);
			monitor.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.DESVALIDADO_PELA_PROGRAD));
		}

		try {
			ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
			mov.setCodMovimento(SigaaListaComando.DESVALIDA_SELECAO_MONITORIA);
			mov.setAcao(ProjetoMonitoriaMov.ACAO_DESVALIDAR_SELECAO);
			mov.setObjMovimentado(monitor);

			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
			prova = getGenericDAO().findByPrimaryKey(prova.getId(), ProvaSelecao.class);
			prova.setResultadoSelecao(getDAO(DiscenteMonitoriaDao.class).findByProvaSeletiva(prova.getId()));
			prepareMovimento(SigaaListaComando.VALIDA_CADASTRO_SELECAO);
			return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_FORM);
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		return redirectMesmaPagina();
	}

	/**
	 * Cadastra monitor selecionado para monitoria.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 *  
	 */
	@Override
	public String cadastrar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);		
		ListaMensagens lista = new ListaMensagens();
		
		boolean selecionouAlguem = false;
		// só valida ou grava os selecionados.
		for (DiscenteMonitoria dm : prova.getResultadoSelecao()) {
			if (dm.isSelecionado()) {
				selecionouAlguem = true;
				dm.setDataInicio(hoje);
				dm.setDataFim(dm.getProjetoEnsino().getProjeto().getDataFim());
				
				//Validando dados básicos do discente. verificando se a situação foi escolhida...
				lista.addAll(dm.validate());					
				DiscenteMonitoriaValidator.validaDiscenteConcluinteOuComAfastamento(dm.getDiscente(),	lista);
				DiscenteMonitoriaValidator.validaDiscenteComBolsa(dm, lista);
			}
		}
		
		if (!selecionouAlguem) {
			lista.addErro("Selecione ao menos um resultado para validação.");
		}
		
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return voltaTelaProva();
		}
		
		
		try {			
			ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
			mov.setCodMovimento(SigaaListaComando.VALIDA_CADASTRO_SELECAO);
			mov.setAcao(ProjetoMonitoriaMov.ACAO_VALIDAR_CADASTRO_SELECAO);
			mov.setObjMovimentado(prova);

			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			atualizarProjetosComSelecao(idCentro, ano, titulo);
			return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_PROVAS);			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_PROVAS);
		}
		
	}
	
	/**
	 * Inicia operação para alterar resultado da prova
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\form.jsp</li>
	 * </ul>
	 *  */
	public String iniciarAlterarResultadoMonitor() throws ArqException{
		int id = getParameterInt("id", 0);
		discenteMonitor = (getGenericDAO().findByPrimaryKey(id, DiscenteMonitoria.class));
		discenteMonitor.getOrientacoes().iterator();
		
		return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_ALTERA); 
	}
	
	/**
	 * operação para alterar resultado da prova
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ValidaSelecaoMonitor\altera_resultado.jsp</li>
	 * </ul>
	 *  */
	public String alterarResultadoMonitor() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);	
		ListaMensagens lista = new ListaMensagens();
		
		if(idSituacao != null && idSituacao.intValue() > 0){
			discenteMonitor.setSituacaoDiscenteMonitoria(getGenericDAO().findByPrimaryKey(idSituacao, SituacaoDiscenteMonitoria.class));
		}else{
			lista.addErro("Situação Validada: Campo obrigatório não informado.");
		}
		if(idVinculo != null && idVinculo.intValue() > 0){
			discenteMonitor.setTipoVinculo(getGenericDAO().findByPrimaryKey(idVinculo, TipoVinculoDiscenteMonitoria.class));
		}else{
			lista.addErro("Vínculo Validado: Campo obrigatório não informado.");
		}
		
		if(idSituacao != null && idVinculo != null ){
			if(idSituacao == SituacaoDiscenteMonitoria.NAO_APROVADO 
				|| idSituacao == SituacaoDiscenteMonitoria.CONVOCADO_MAS_REJEITOU_MONITORIA
				|| idSituacao == SituacaoDiscenteMonitoria.INVALIDADO_PROGRAD)
				if (idVinculo != TipoVinculoDiscenteMonitoria.NAO_CLASSIFICADO) {
				lista.addErro("Para discentes com situação NÃO APROVADO, MONITORIA INVÁLIDA e REJEITOU MONITORIA é necessário selecionar o vínculo NÃO CLASSIFICADO.");
			}
		}
		
		if (!ValidatorUtil.isEmpty(discenteMonitor)) {
			discenteMonitor.setDataInicio(hoje);
			discenteMonitor.setDataFim(discenteMonitor.getProjetoEnsino().getProjeto().getDataFim());
			discenteMonitor.setObservacao(observacoes);

			//Validando dados básicos do discente. verificando se a situação foi escolhida...
			lista.addAll(discenteMonitor.validate());					
			DiscenteMonitoriaValidator.validaDiscenteConcluinteOuComAfastamento(discenteMonitor.getDiscente(),	lista);
			DiscenteMonitoriaValidator.validaDiscenteComBolsa(discenteMonitor, lista);
		}
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		
		try {	
			
			for (DiscenteMonitoria dm : prova.getResultadoSelecao()) {
				if(dm.getId() == discenteMonitor.getId()){
					dm.setSituacaoDiscenteMonitoria(discenteMonitor.getSituacaoDiscenteMonitoria());
					dm.setTipoVinculo(discenteMonitor.getTipoVinculo());
					dm.setDataInicio(discenteMonitor.getDataInicio());
					dm.setDataFim(discenteMonitor.getDataFim());
					dm.setObservacao(discenteMonitor.getObservacao());
					dm.setSelecionado(true);
				}
			}
			
			ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
			prepareMovimento(SigaaListaComando.VALIDA_CADASTRO_SELECAO);
			mov.setCodMovimento(SigaaListaComando.VALIDA_CADASTRO_SELECAO);
			mov.setAcao(ProjetoMonitoriaMov.ACAO_VALIDAR_CADASTRO_SELECAO);
			mov.setObjMovimentado(prova);

			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			atualizarProjetosComSelecao(idCentro, ano, titulo);
			idSituacao = new Integer(0);
			idVinculo = new Integer(0);
			return voltaProva();			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	}
	
	@Override
	public String getDirBase() {
		return "/monitoria/ValidaSelecaoMonitor";
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	public Collection<ProjetoEnsino> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}

	public ProvaSelecao getProva() {
		return prova;
	}

	public void setProva(ProvaSelecao prova) {
		this.prova = prova;
	}

	public String getTitulo() {
	    return titulo;
	}

	public void setTitulo(String titulo) {
	    this.titulo = titulo;
	}

	public Integer getIdCentro() {
		return idCentro;
	}

	public void setIdCentro(Integer idCentro) {
		this.idCentro = idCentro;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public DiscenteMonitoria getDiscenteMonitor() {
		return discenteMonitor;
	}

	public void setDiscenteMonitor(DiscenteMonitoria discenteMonitor) {
		this.discenteMonitor = discenteMonitor;
	}

	public Integer getIdVinculo() {
		return idVinculo;
	}

	public void setIdVinculo(Integer idVinculo) {
		this.idVinculo = idVinculo;
	}

	public Integer getIdSituacao() {
		return idSituacao;
	}

	public void setIdSituacao(Integer idSituacao) {
		this.idSituacao = idSituacao;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

}
