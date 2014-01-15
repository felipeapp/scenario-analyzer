/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/03/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.dao.monitoria.InscricaoSelecaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.OrientacaoDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DadosAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoriaPorPrioridadeComparator;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.InscricaoSelecaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecaoComponenteCurricular;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.negocio.DiscenteMonitoriaMov;
import br.ufrn.sigaa.monitoria.negocio.DiscenteMonitoriaValidator;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.portal.jsf.PortalDiscenteMBean;
import br.ufrn.sigaa.portal.jsf.PortalDocenteMBean;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * MBean respons�vel por realiza as opera��es sobre os Discentes de Monitoria.
 * 
 * @author Victor Hugo
 * @author Ilueny
 * 
 */
@Component("discenteMonitoria") @Scope("session")
public class DiscenteMonitoriaMBean extends SigaaAbstractController<DiscenteMonitoria> {

	/** Conjunto de discentes removidos. Usado ao remover algum discente da sele��o.  */
	private Set<DiscenteMonitoria> discentesRemovidos = new HashSet<DiscenteMonitoria>();

	/** Usado Cadastro do discente no projeto atrav�s do resultado da sele��o  */
	private Set<Orientacao> orientacoesRemovidas = new HashSet<Orientacao>();

	/** Discentes inscritos na prova e n�o selecionados */
	private List<DiscenteMonitoria> discentesInscritosNaoCadastrados = new ArrayList<DiscenteMonitoria>();

	/** Usado ao Listar todos os projetos de monitoria cujo usu�rio logado � Coordenador  */
	private Collection<ProjetoEnsino> projetos = new HashSet<ProjetoEnsino>();

	/** Usado ao Selecionar o projeto para inscri��o na prova seletiva */
	private ProjetoEnsino projetoEnsino;

	/** Usados na cria��o da orienta��o */
	private Collection<EquipeDocente> docentes = new ArrayList<EquipeDocente>();

	/** ProvaSelecao usado em diversas opera��es  */
	private ProvaSelecao provaSelecao;

	/** Cole��o usada ao carregar lista de projetos que o discente fez a sele��o */
	private Collection<ProvaSelecao> provas = new HashSet<ProvaSelecao>();

	/** Total de bolsistas no projeto */
	private Object totalBolsistasProjeto;

	/** Total de volunt�rios no projeto */
	private Object totalVoluntariosProjeto;
	
	/** Dados do discente monitoria.   */
	private DadosAluno dados;
	
	/** orientadores do projeto de monitoria */
	private Collection<EquipeDocente> orientadores = new ArrayList<EquipeDocente>();
	
	/** orienta��o discente */
	private Orientacao orientacao = new Orientacao(); 
	
	/** Representa a inscri��o de um discente a um projeto de monitoria */
	private InscricaoSelecaoMonitoria inscricaoSelMonitoria = new InscricaoSelecaoMonitoria();
	
	/** Grupo de Discentes a serem notificados. */
	private Collection<InscricaoSelecaoMonitoria> grupoEmail;

	/** id da prova de inscri��o */
	private int idProva; 
	
	/**
	 * Definindo regra para execu��o de algumas opera��es
	 * onde s� o coordenador de projeto pode realizar.
	 * <br/>
	 * 
	 * JSP: N�o invocado por JSP.
	 * 
	 * @throws SegurancaException seguran�a.
	 */
	public  void checkChangeRole() throws SegurancaException {
		if ((getAcessoMenu() != null)
				&& (!getAcessoMenu().isCoordenadorMonitoria())) {
			throw new SegurancaException(
			"Somente Coordenadores de Projetos de Monitoria ativos podem realizar esta opera��o.");
		}
		super.checkChangeRole();
	}

	/**
	 * Construtor padr�o.
	 */
	public DiscenteMonitoriaMBean() {
		obj = new DiscenteMonitoria();
	}

	/**
	 * Lista todos os tipos de monitoria em combo.
	 * <br/><br/>
	 * JSP:
	 * <ul>
	 * 	<li>/sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 * 	<li>/sigaa.war/monitoria/CadastrarMonitor/form.jsp</li>
	 * 	<li>/sigaa.war/monitoria/ConsultarMonitor/lista_dados_bancarios.jsp</li>
	 * 	<li>/sigaa.war/monitoria/ConsultarMonitor/lista.jsp</li>
	 * 	<li>/sigaa.war/monitoria/Relatorios/dados_bancarios_monitores_form.jsp</li>
	 * 	<li>/sigaa.war/monitoria/Relatorios/quantitativo_monitores_form.jsp</li>
	 * 	<li>/sigaa.war/monitoria/ValidacaoProgradAtividadeMonitor/lista.jsp</li>
	 * 	<li>/sigaa.war/monitoria/ValidaSelecaoMonitor/form.jsp</li>
	 * </ul>
	 *  
	 * @return {@link Collection} de {@link SelectItem} para listar no combo.
	 */
	public  Collection<SelectItem> getTipoMonitoriaCombo() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(0, " -- SELECIONE --"));
		lista.add(new SelectItem(TipoVinculoDiscenteMonitoria.BOLSISTA, "BOLSISTA"));
		lista.add(new SelectItem(TipoVinculoDiscenteMonitoria.NAO_REMUNERADO, "N�O REMUNERADO"));
		lista.add(new SelectItem(TipoVinculoDiscenteMonitoria.EM_ESPERA, "EM ESPERA"));
		lista.add(new SelectItem(TipoVinculoDiscenteMonitoria.NAO_CLASSIFICADO, "N�O CLASSIFICADO"));
		return lista;
	}
	
	/**
	 * Lista todos os tipos de monitoria em combo.
	 * <br/><br/>
	 * JSP:
	 * <ul>
	 * 	<li>/sigaa.war/monitoria/ProvaSelecao/indicacao_discente.jsp</li>
	 * </ul>
	 *  
	 * @return {@link Collection} de {@link SelectItem} para listar no combo.
	 */
	public  Collection<SelectItem> getTipoVinculoClassificadoMonitoriaCombo() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(0, " -- SELECIONE --"));
		lista.add(new SelectItem(TipoVinculoDiscenteMonitoria.BOLSISTA, "BOLSISTA"));
		lista.add(new SelectItem(TipoVinculoDiscenteMonitoria.NAO_REMUNERADO, "N�O REMUNERADO"));
		lista.add(new SelectItem(TipoVinculoDiscenteMonitoria.EM_ESPERA, "EM ESPERA"));
		return lista;
	}

	/**
	 * Retorna somente os tipos de v�nculo (BOLSISTA e N�O REMUNERADO) que torna um monitor ativo.
	 * <br/><br/>
	 * JSP: /sigaa.war/monitoria/AlterarDiscenteMonitoria/alterar_vinculo_monitor.jsp
	 * JSP: /sigaa.war/monitoria/CadastrarMonitor/form.jsp
	 * 
	 * @return lista para exibi��o no combo.
	 */
	public  Collection<SelectItem> getTiposAtivosMonitoriaCombo() {
		Collection<SelectItem> tiposAtivosMonitoriaCombo = new ArrayList<SelectItem>();
		tiposAtivosMonitoriaCombo.add(new SelectItem(TipoVinculoDiscenteMonitoria.BOLSISTA, "BOLSISTA"));
		tiposAtivosMonitoriaCombo.add(new SelectItem(TipoVinculoDiscenteMonitoria.NAO_REMUNERADO, "N�O REMUNERADO"));
		return tiposAtivosMonitoriaCombo;
	}

	/**
	 * Lista todos os projetos de monitoria cujo usu�rio logado � Coordenador.
	 * <br/><br/>
	 * JSP: N�o invocado por JSP
	 * @return lista de provas para o coordenador. 
	 */
	public  String listarProvas() {
		try {
		    checkChangeRole();
		    ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
		    // Cada projeto tem v�rias provas, na view o usu�rio escolhe a prova
		    projetos = dao.findValidosByServidor(getUsuarioLogado().getServidor().getId(), true);
		    return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_LISTA);
		} catch (Exception e) {
		    addMensagemErro(e.getMessage());
		    return null;
		}
	}

	/**
	 * Carrega o projeto requisitado para o cadastro de novos resultados de
	 * sele��o e a lista de resultados de sele��es j� realizadas para aquele
	 * projeto.
	 * <br/><br/>
	 * JSP:
	 * <ul>
	 * 	<li>/sigaa.war/monitoria/CadastrarProvaSelecao/lista.jsp</li>
	 * 	<li>/sigaa.war/monitoria/CadastrarProvaSelecao/projetos.jsp</li>
	 * 	<li>/sigaa.war/monitoria/DiscenteMonitoria/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
    public  String iniciarCadatroResultadoProva() throws SegurancaException {

    	checkChangeRole();

    	try {

    		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
    		Integer idProva = getParameterInt("idProva");
    		provaSelecao = dao.findByPrimaryKey(idProva, ProvaSelecao.class);

    		// valida��es
    		ListaMensagens mensagens = new ListaMensagens();
    		DiscenteMonitoriaValidator.validaIniciarCadastroSelecao(provaSelecao, CalendarUtils.getAnoAtual(), mensagens);
    		if (!mensagens.isEmpty()) {
    			addMensagens(mensagens);
    			return null;
    		}

    		// discentes de monitoria com resultado j� cadastrado...
    		provaSelecao.setResultadoSelecao(dao.findByProvaSeletiva(idProva));
    		for (DiscenteMonitoria dis : provaSelecao.getResultadoSelecao()) {
    			dis.getOrientacoes().iterator();
    		}

    		// criando lista de discentes ainda n�o cadastrados...
    		// preparando discentes ativos inscritos para cadastrados resultados da prova seletiva
    		InscricaoSelecaoMonitoriaDao inscDao = getDAO(InscricaoSelecaoMonitoriaDao.class);	    
    		Collection<InscricaoSelecaoMonitoria> inscritos = inscDao.findByProvaSeletiva(provaSelecao.getId());
    		provaSelecao.setDiscentesInscritos(inscritos);

    		discentesInscritosNaoCadastrados = new ArrayList<DiscenteMonitoria>();
    		for (InscricaoSelecaoMonitoria insc : provaSelecao.getDiscentesInscritos()) {

    			DiscenteMonitoria dm = new DiscenteMonitoria();
    			dm.setDiscente(insc.getDiscente());
    			// s� inclui pra cadastrar se ainda n�o tiver nos resultados
    			if (!provaSelecao.getResultadoSelecao().contains(dm)) {

    				dm.setDataCadastro(new Date());
    				dm.setAtivo(true);
    				dm.setSelecionado(false);
    				dm.setClassificado(true);
    				dm.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO));
    				dm.setProjetoEnsino(provaSelecao.getProjetoEnsino());
    				dm.setProvaSelecao(provaSelecao);
    				dm.setTipoVinculo(getGenericDAO().findByPrimaryKey(TipoVinculoDiscenteMonitoria.EM_ESPERA, TipoVinculoDiscenteMonitoria.class));

    				// informando se o discente � priorit�rio segundo a resolu��o 169/2008
    				dm.setPrioritario(isPrioritario(dm.getDiscente()));

    				// atualizando dados banc�rios em discente monitoria		    
    				Pessoa p = getGenericDAO().findByPrimaryKey(insc.getDiscente().getPessoa().getId(), Pessoa.class);
    				if (p.getContaBancaria() != null) {
    					dm.setBanco(p.getContaBancaria().getBanco());
    					dm.setAgencia(p.getContaBancaria().getAgencia());
    					dm.setConta(p.getContaBancaria().getNumero());
    					dm.setOperacao(p.getContaBancaria().getOperacao());
    				}
    				discentesInscritosNaoCadastrados.add(dm);
    			}

    		}

    		// s� mostra bolsas que restaram....
    		totalBolsistasProjeto = dao.countMonitoresByProjeto(provaSelecao.getProjetoEnsino().getId(), TipoVinculoDiscenteMonitoria.BOLSISTA);
    		totalVoluntariosProjeto = dao.countMonitoresByProjeto(provaSelecao.getProjetoEnsino().getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);

    		prepareMovimento(SigaaListaComando.CADASTRAR_RESULTADO_SELECAO_MONITORIA);
    		
    		classificarDiscentes(provaSelecao);

    	} catch (Exception e) {
    		addMensagemErro(e.getMessage());
    		notifyError(e);
    	}

    	setConfirmButton("Confirmar");

    	return forward(getFormPage());

    }

	/**
	 * Adiciona o resultado de uma sele��o � lista de sele��o do projeto.
	 * <br/><br/>
	 * JSP: <ul><li> /sigaa.war/extensao/DiscenteExtensao/form.jsp</li>
	 * 			<li>/sigaa.war/monitoria/DiscenteMonitoria/form.jsp</li></ul>
	 * @return
	 * @throws ArqException 
	 */
    public  String adicionarResultadoSelecao() throws ArqException {
    	checkChangeRole();

    	DiscenteMonitoriaDao dao =  getDAO(DiscenteMonitoriaDao.class);
    	TipoVinculoDiscenteMonitoria vinculoNaoClassificado = dao.findByPrimaryKey(TipoVinculoDiscenteMonitoria.NAO_CLASSIFICADO, TipoVinculoDiscenteMonitoria.class);

    	//cria lista com id's dos discentes selecionados
    	Collection<Integer> idDiscentes = new ArrayList<Integer>();
    	for (Iterator<DiscenteMonitoria> it = discentesInscritosNaoCadastrados.iterator(); it.hasNext();) {
    		idDiscentes.add(it.next().getDiscente().getId());
    	}

    	//idDiscente e media ponderada dos componentes da prova de sele��o
    	Map<Integer, Object[]> discentesComMedias = dao.calcularMediaPonderadaDisciplinasDiscenteProva(provaSelecao, idDiscentes);

    	// Incluindo discentes na prova
    	for (Iterator<DiscenteMonitoria> it = discentesInscritosNaoCadastrados.iterator(); it.hasNext();) {
    		DiscenteMonitoria dm = it.next();
    		
    		//Valida��es
    		ListaMensagens mensagens = new ListaMensagens();
    		DiscenteMonitoriaValidator.validaDiscenteCadastradoSelecao(dm, provaSelecao, mensagens);
    		DiscenteMonitoriaValidator.validaAdicaoSelecao(dm, provaSelecao, mensagens, getCalendarioVigente());
    		if (!mensagens.isEmpty()) {
    			addMensagens(mensagens);
    			return null;
    		}
    		

    		// Dados para Classifica��o
    		if (dm.isClassificado()) {
    			// Definindo m�dia ponderada dos componentes do projeto cursados pelo discente para que seja definida a classifica��o.
    			Object[] dadosClassificacao =  discentesComMedias.get(dm.getDiscente().getId());
    			if (dadosClassificacao == null) {
    				mensagens.addErro(dm.getDiscente().getMatriculaNome() + " n�o foi aprovado(a) com rendimento satisfat�rio " +
    				"em algum(ns) do(s) Componentes Curriculares desta Prova Seletiva.");
    			}else {
    				dm.setMediaComponentesProjeto((Float)dadosClassificacao[0]);
    				IndiceAcademicoDiscente indiceDiscente = (IndiceAcademicoDiscente) dadosClassificacao[1];
    				indiceDiscente.setDiscente(dm.getDiscente().getDiscente());
    				dm.getDiscente().getDiscente().setIndices(new ArrayList<IndiceAcademicoDiscente>());
    				dm.getDiscente().getDiscente().getIndices().add(indiceDiscente);
    			}
    		} else {
    			dm.setTipoVinculo(vinculoNaoClassificado);
    			dm.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.NAO_APROVADO));
    		}

    		// adiciona o discente na lista de resultados da prova....
    		provaSelecao.getResultadoSelecao().add(dm);

    		// remove o adicionado da lista de pendentes de cadastro do resultado.
    		it.remove();
    	}

    	try {
    		// classificando...
			classificarDiscentes(provaSelecao);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		}

    	return null;
    }

	/**
	 * Remove um discente da sele��o.
	 * <br/><br/>
	 * JSP: <ul><li>/sigaa.war/extensao/DiscenteExtensao/form.jsp</li>
	 * 			<li>/sigaa.war/monitoria/DiscenteMonitoria/form.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
    public  String removeSelecao() throws DAOException, SegurancaException {
    	checkChangeRole();
    	int idDiscente = getParameterInt("idDiscente", 0);

    	if (idDiscente == 0) {
    		addMensagemErro("N�o foi poss�vel remover discente");
    		return null;
    	}

    	DiscenteMonitoria dm = new DiscenteMonitoria();
    	for (Iterator<DiscenteMonitoria> iterator = provaSelecao.getResultadoSelecao().iterator(); iterator.hasNext();) {
    		dm = iterator.next();

    		if (dm.getDiscente().getId() == idDiscente) {
    			// se o objeto for persistente remover do banco!
    			if (dm.getId() != 0) {
    				for (Orientacao ori : dm.getOrientacoes()) {
    					orientacoesRemovidas.add(ori);
    				}
    				discentesRemovidos.add(dm);
    			}
    			iterator.remove();
    			break;
    		}
    	}

    	// adicionando a lista dos sem resultados...
    	DiscenteMonitoria d = new DiscenteMonitoria();
    	getGenericDAO().initialize(dm.getDiscente());
    	d.setDiscente(dm.getDiscente());
		d.getDiscente().getDiscente().getIndices().iterator();
    	d.setDataCadastro(new Date());
    	d.setAtivo(true);
    	d.setSelecionado(false);
    	d.setClassificado(true);
    	d.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO));
    	d.setProjetoEnsino(provaSelecao.getProjetoEnsino());
    	d.setProvaSelecao(provaSelecao);
    	d.setNota(0.0);
    	d.setNotaProva(0.0);
    	d.setTipoVinculo(getGenericDAO().findByPrimaryKey(TipoVinculoDiscenteMonitoria.EM_ESPERA, TipoVinculoDiscenteMonitoria.class));

    	// informando se o discente � priorit�rio segundo a resolu��o 169/2008
    	d.setPrioritario(isPrioritario(dm.getDiscente()));

    	// atualizando dados banc�rios em discente monitoria
    	Pessoa p = getGenericDAO().findByPrimaryKey(
    			dm.getDiscente().getPessoa().getId(), Pessoa.class);
    	if (p.getContaBancaria() != null) {
    		d.setBanco(p.getContaBancaria().getBanco());
    		d.setAgencia(p.getContaBancaria().getAgencia());
    		d.setConta(p.getContaBancaria().getNumero());
    		d.setOperacao(p.getContaBancaria().getOperacao());
    	}

    	discentesInscritosNaoCadastrados.add(d);

    	try {
    		// classificando...
			classificarDiscentes(provaSelecao);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		}

    	return null;

    }

	/**<p>
	 * Este m�todo todo seta a classifica��o dos discentes participantes do projeto
	 * de monitoria de acordo com os crit�rios estabelecidos sendo o primeiro
	 * crit�rio a nota  do processo de sele��o. Em caso de empate o
	 * desempate obedecer� os seguintes crit�rios na seguinte ordem:</p>
	 * <li>1 - Maior nota na prova seletiva;
	 * <li>2 - Maior nota no(s) componente(s) curricular(es) de forma��o objeto da sele��o; (m�dia ponderada
	 * dos componentes curriculares do projeto cursadas pelo aluno);
	 * <li>3 - Maior �ndice de rendimento acad�mico (IRA) e ainda define quem ter� bolsa.
	 * 
	 * Pr�-classifica��o dos discentes. Ainda sujeito a aprova��o da prograd.
	 * 
	 * @param prova que ter� os discentes classificados
	 * @throws DAOException por busca dos dados do banco  
	 * 
	 */
    private void classificarDiscentes(ProvaSelecao prova) throws DAOException, NegocioException {

    	try {
	    	//zerando classifica��es antigas.
	    	for (DiscenteMonitoria dm : prova.getResultadoSelecao()) {
	    		dm.setClassificacao(0);
	    	}
	
	    	//Ordenando pelo compareTo de DiscenteMonitoria 
	    	Collections.sort((List<DiscenteMonitoria>) prova.getResultadoSelecao());
	
	    	//classificando.
	    	int count = 1;
	    	for (DiscenteMonitoria dm : prova.getResultadoSelecao()) {
	    		if (dm.isVinculoValido()) {
	    			dm.setClassificacao(count);
	    			count++;
	    		}
	    	}
	
	    	DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
	
	    	// Definindo o Tipo de V�nculo do discente, se � Bolsista ou Volunt�rio.
	    	int totalDiscentesRemuneradosNaProvaAgora = dao.countMonitoresByProva(prova.getId(), TipoVinculoDiscenteMonitoria.BOLSISTA);
	    	int totalDiscentesNaoRemuneradosNaProvaAgora = dao.countMonitoresByProva(prova.getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);
	
	    	for (DiscenteMonitoria dm : prova.getResultadoSelecao()) {
	
	    		if (dm.isAtivo() && (dm.getTipoVinculo() == null || dm.isVinculoValido())) {
	    			//ainda tem vaga para bolsista
	    			if (prova.getVagasRemuneradas() > totalDiscentesRemuneradosNaProvaAgora) {
	    				dm.setTipoVinculo(getGenericDAO().findByPrimaryKey(TipoVinculoDiscenteMonitoria.BOLSISTA, TipoVinculoDiscenteMonitoria.class));		    
	    				dm.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO));
	    				totalDiscentesRemuneradosNaProvaAgora++;
	    			} else {
	    				//ainda tem vaga para n�o remunerado
	    				if (prova.getVagasNaoRemuneradas() > totalDiscentesNaoRemuneradosNaProvaAgora) {
	    					dm.setTipoVinculo(getGenericDAO().findByPrimaryKey(TipoVinculoDiscenteMonitoria.NAO_REMUNERADO, TipoVinculoDiscenteMonitoria.class));
	    					dm.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO));
	    					totalDiscentesNaoRemuneradosNaProvaAgora++;
	    				} else {
	    					dm.setTipoVinculo(getGenericDAO().findByPrimaryKey(TipoVinculoDiscenteMonitoria.EM_ESPERA, TipoVinculoDiscenteMonitoria.class));
	    					dm.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO));
	    				}
	    			}
	    		}
	    	}
    	}catch (Exception e) {
    		e.printStackTrace();
    		throw new NegocioException("Erro ao classificar discentes.");    		
		}
    }

	/**
	 * Vai para a tela de sele��o dos orientadores dos discentes cadastrados.
	 * <br/><br/>
	 * JSP: /sigaa.war/monitoria/DiscenteMonitoria/form.jsp
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
    public  String selecionaOrientadores() throws DAOException, SegurancaException {
    	checkDocenteRole();
    	checkChangeRole();

    	if ((provaSelecao == null) || ValidatorUtil.isEmpty(provaSelecao.getResultadoSelecao())) {
    	    addMensagemErro("Lista de resultados cadastrados est� vazia.");
    	    return redirectMesmaPagina();
    	}

    	// carrega todos os docentes do projeto
    	docentes = getDAO(EquipeDocenteDao.class).findByProjeto(provaSelecao.getProjetoEnsino().getId(), true);

    	if (docentes != null && docentes.size() > 0) {
    		// setando os poss�veis orientadores para cada DiscenteMonitoria
    		for (DiscenteMonitoria dm : provaSelecao.getResultadoSelecao()) {
    			if (dm.isVinculoValido()) {
    				dm.setDocentes(new HashSet<EquipeDocente>());
    				dm.clonaDocentes(docentes);
    			}
    		}
    	}

    	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_ORIENTADORES);
    }

	/**
	 * Lista todas as orienta��es dos projetos do servidor logado.
	 * <br/><br/>
	 * JSP: N�o invocado por JSP
	 * 
	 * @return {@link Collection} de {@link Orientacao} do usu�rio logado.
	 */
    public  Collection<Orientacao> getOrientacoesUsuarioLogado() {
	try {
	    OrientacaoDao dao =  getDAO(OrientacaoDao.class);
	    return dao.findByServidor(getUsuarioLogado().getServidor(), null);
	} catch (DAOException e) {
	    addMensagemErro("Erro ao Listar projeto(s) do usu�rio atual");
	    return null;
	}
    }

	/**
	 * Retorna o diret�rio base
	 * <br/><br/>
	 * JSP: N�o invocado por JSP
	 */
	public  String getDirBase() {
		return "/monitoria/DiscenteMonitoria";
	}

	/**
	 * Cadastra o discente no projeto atrav�s do resultado da sele��o, 
	 * notas dos alunos na prova. Cria os v�nculos (orienta��es) do 
	 * discente com os seus professores
	 * <br/><br/>
	 * JSP: <ul><li>/sigaa.war/monitoria/DiscenteMonitoria/meus_projetos.jsp</li>
	 * 			<li>/sigaa.war/monitoria/DiscenteMonitoria/orientadores.jsp</li></ul>
	 * 
	 * @throws SegurancaException s� docentes coordenadores de projetos podem realizar esta opera��o.
	 * @throws DAOException por erro nas buscas de discentes no banco.
	 * @return se o cadastro foi bem sucedido, retorna para o portal do docente.
	 */
    @Override
    public String cadastrar() throws DAOException, SegurancaException {

	checkChangeRole();
	erros.getMensagens().clear();

	if ((provaSelecao == null) || ValidatorUtil.isEmpty(provaSelecao.getResultadoSelecao())) {
	    addMensagemErro("Lista de resultados cadastrados est� vazia.");
	    return redirectMesmaPagina();
	}

	ListaMensagens mensagens = new ListaMensagens();
	
	// Percorre a lista de discentes que tiveram o resultado da prova cadastrado
	for (DiscenteMonitoria discenteMonitoria : provaSelecao.getResultadoSelecao()) {
	    for (EquipeDocente orientador : discenteMonitoria.getDocentes()) {

		if (orientador.isSelecionado()) {

		    boolean jaOrientaDiscente = false;
		    for (Orientacao orientacao : discenteMonitoria.getOrientacoes()) {
			// testa se o orientador selecionado j� est� cadastrado
			if (orientador.getId() == orientacao.getEquipeDocente().getId()) {
			    jaOrientaDiscente = true;
			    break;
			}
		    }
		    // se sim, sai da intera��o
		    if (jaOrientaDiscente) {
			break;
		    
		    } else {

		    	// cria nova orienta��o para o discente atual
		        OrientacaoDao dao = getDAO(OrientacaoDao.class);	
		        try {
		          orientacao = new Orientacao();
	        	  dao.initialize(discenteMonitoria.getProjetoEnsino());
		    	  orientacao.setDiscenteMonitoria(discenteMonitoria);
		    	  orientacao.setDataInicio(orientador.getDataInicioOrientacao());
		    	  orientacao.setDataFim(orientador.getDataFimOrientacao());

		    	  dao.initialize(orientador);
		    	  orientacao.setEquipeDocente(orientador);
		    	  orientacao.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		    	  orientacao.setAtivo(true);
		    	  
		    	  mensagens.addAll(orientacao.validate());
		    	  if (!mensagens.isEmpty()) {
		    		  addMensagens(mensagens);
		    		  return null;
		    	  }
		        } finally {
		        	dao.close();
		        }
		        
		        discenteMonitoria.getOrientacoes().add(orientacao);
		    }

		    // orientador n�o selecionado
		    // efetuar a rem��o de um orientador j� cadastrado caso o
		    // usu�rio desmarque um checkbox marcado anteriormente.
		} else {
		    for (Orientacao ori : discenteMonitoria.getOrientacoes()) { 
			// testa se o orientador selecionado est� cadastrado
			if (orientador.getId() == ori.getEquipeDocente().getId()) {
			    discenteMonitoria.getOrientacoes().remove(ori);
			    orientacoesRemovidas.add(ori);
			    break;
			}
		    }
		}
	    }
	}

	for (DiscenteMonitoria dm : provaSelecao.getResultadoSelecao()) {
	    DiscenteMonitoriaValidator.validaCadaMonitorComUmOrientador(dm, mensagens);
	}

	// Testando se o docente selecionado j� tem mais 2 orientandos neste projeto (Regra da resolu��o atual)
	Set<Integer> naoRepeteDocente = new HashSet<Integer>();

	for (DiscenteMonitoria dm : provaSelecao.getResultadoSelecao()) {
	    for (Orientacao ori : dm.getOrientacoes()) {		
		if (ori.getEquipeDocente().isSelecionado() && naoRepeteDocente.add(ori.getEquipeDocente().getId())) {
		    DiscenteMonitoriaValidator.validaMaximoOrientacoesDocentes(ori.getEquipeDocente(), ori.getDataInicio(), ori.getDataFim(),  mensagens);
		}
	    }
	}

	if (!mensagens.isEmpty()) {
	    addMensagens(mensagens);
	    return null;
	}

	if (mensagens.isEmpty()) {

	    DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
	    mov.setSelecoesRemovidas(discentesRemovidos);
	    mov.setOrientacoesRemovidas(orientacoesRemovidas);
	    mov.setProvaSelecao(provaSelecao);
	    mov.setCodMovimento(SigaaListaComando.CADASTRAR_RESULTADO_SELECAO_MONITORIA);
	    mov.setCalendarioAcademico(getCalendarioVigente());
	    
	    try {

		execute(mov, getCurrentRequest());
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		addMensagemInformation("Uma solicita��o de confirma��o de participa��o no projeto foi enviada para " +
				"os discentes classificados com bolsas remuneradas e n�o remuneradas.");

		// limpa lista de sele��es marcadas para remo��o
		obj = new DiscenteMonitoria();

	    } catch (NegocioException e) {
		addMensagemErro(e.getMessage());
	    } catch (ArqException e) {
		addMensagemErroPadrao();
		notifyError(e);
	    }

	    resetBean();

	    // return redirectPage(getListPage());
	    return forward(PortalDocenteMBean.PORTAL_DOCENTE);

	} else {
	    return null;
	}
    }
    

    // *******************INICIO DAS INSCRI��ES DOS ALUNOS NA PROVA***********************

	/**
	 * Carrega dados da prova selecionada pelo discente e realiza valida��es de
	 * componentes cursados pelo discente e redireciona-o para tela de
	 * confirma��o da inscri��o caso ele preencha os requisitos m�nimos para
	 * sele��o.
	 * <br/><br/>
	 * Chamado pelas JSPs: 
	 * <ul>
	 * 	<li>/graduacao/agregador_bolsa/busca.jsp
	 *  <li>/sigaa.war/extensao/DiscenteExtensao/inscricao_discente.jsp
	 * 	<li>/sigaa.war/graduacao/agregador_bolsa/busca.jsp
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
    public String iniciarInscricaoDiscente() throws ArqException {

	// id da prova selecionada...
	int id = getParameterInt("id", 0);

	if (id != 0) {

	    GenericDAO dao = getGenericDAO();
	    provaSelecao = dao.findByPrimaryKey(id, ProvaSelecao.class);
	    provaSelecao.getProjetoEnsino().getId();

	    if ((provaSelecao != null) && (provaSelecao.getId() > 0)) {

		// evitar erro de lazy
		provaSelecao.setComponentesObrigatorios(dao.findByExactField(
			ProvaSelecaoComponenteCurricular.class,
			"provaSelecao.id", provaSelecao.getId()));
		for (ProvaSelecaoComponenteCurricular co : provaSelecao
			.getComponentesObrigatorios()) {
		    co.getComponenteCurricularMonitoria()
			    .getDocentesComponentes().iterator();
		}
		
		setDados(new DadosAluno());
		// valida��o: verificando se o discente j� se inscreveu para a
		// sele��o deste projeto e se cursou as disciplinas exigidas
		ListaMensagens mensagens = new ListaMensagens();
		DiscenteMonitoriaValidator.validaInscricaoSelecao(getDiscenteUsuario(), provaSelecao, mensagens);
		
		//Verificando se o discente est� inscrito no cadastro �nico.
		DiscenteMonitoriaValidator.validaDiscenteInCadastroUnico(getDiscenteUsuario(), mensagens, getCalendarioVigente());

		// Populando os dados banc�rios do discente.
		populaDadosBancarios();

		if (!mensagens.isEmpty()) {
		    addMensagens(mensagens);
		    return null;
		}
	    } else {
		addMensagemErro("Prova de sele��o n�o encontrada!");
		return null;
	    }

	} else {
	    addMensagemErro("Prova selecionada inv�lida!");
	    return null;
	}
	prepareMovimento(ArqListaComando.CADASTRAR);
	setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_INSCRICAO_DISCENTE);
    }

    /**
     * Busca os dados banc�rios na pessoa do usu�rio logado
     * 
     * @throws DAOException
     */
    private void populaDadosBancarios() throws DAOException {
	Pessoa p = getUsuarioLogado().getPessoa();
	p = getGenericDAO().findByPrimaryKey(p.getId(), Pessoa.class);
	if (p.getContaBancaria() != null) {
	    obj.setBanco(p.getContaBancaria().getBanco());
	    obj.setAgencia(p.getContaBancaria().getAgencia());
	    obj.setConta(p.getContaBancaria().getNumero());
	    obj.setOperacao(p.getContaBancaria().getOperacao());
	}
    }

    /**
     * Realiza a inscri��o do discente para a sele��o do projeto de monitoria
     * solicitado. Envia um e-mail com a confirma��o da inscri��o do discente e
     * com mais detalhes sobre a prova.
     * <br/>
     * Chamado pelas JSPs:
     * <ul>
     *  <li>sigaa.war/monitoria/DiscenteMonitoria/inscricao_discente.jsp</li>
     * </ul>
     * @return
     * @throws ArqException 
     */
    public String realizarInscricaoDiscente() throws ArqException {

	if (getDiscenteUsuario().getNivel() == NivelEnsino.GRADUACAO) {


	    if (obj == null || provaSelecao == null) {
		throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
	    }

	    //valida��es
	    ListaMensagens mensagens = new ListaMensagens();
	    DiscenteMonitoriaValidator.validaDiscenteInCadastroUnico(getDiscenteUsuario(), mensagens, getCalendarioVigente());
	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }
	    
	    InscricaoSelecaoMonitoria inscricao = new InscricaoSelecaoMonitoria();
	    inscricao.setDiscente(new DiscenteGraduacao(getDiscenteUsuario().getId()));
	    inscricao.setProjetoEnsino(provaSelecao.getProjetoEnsino());
	    inscricao.setProvaSelecao(provaSelecao);
	    inscricao.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
	    inscricao.setDataCadastro(new Date());
	    
	    //valida dados do aluno
	    ValidatorUtil.validateEmailRequired(getDados().getEmail(), "Email", erros);
		ValidatorUtil.validateRequired(getDados().getTelefone(), "Telefone", erros);
		ValidatorUtil.validateRequired(getDados().getQualificacoes(), "Qualifica��es", erros);
		
		if(!erros.isEmpty()){
			addMensagens(erros);
			return null;
		}
			
	    inscricao.setDados(getDados());
	    inscricao.setAtivo(true);

	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setObjMovimentado(inscricao);
	    mov.setCodMovimento(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA_MONITORIA);

	    try {
	    execute(mov);
					
	    getGenericDAO().initialize(provaSelecao.getProjetoEnsino().getProjeto());
		MembroProjeto coord = getGenericDAO().findByPrimaryKey(provaSelecao.getProjetoEnsino().getProjeto().getCoordenador().getId(), MembroProjeto.class);
		
		//Enviando email para o coordenador
		if (coord != null) {
			String cabecalho = new String();
			String mensagem = new String();
			String assunto = new String();
			String texto = new String();
			
			assunto = "Interesse de aluno em Projeto de Ensino";
			cabecalho = "<p> Caro(a) "+ coord.getPessoa().getNome().toUpperCase() + ",<br><br>";
			texto = "O discente "+ getUsuarioLogado().getNome() + " registrou-se como interessado(a) no projeto de Ensino " + provaSelecao.getProjetoEnsino().getTitulo() +".";
			mensagem = cabecalho+texto;

			MailBody body = new MailBody();
				
			body.setAssunto(assunto);
			body.setMensagem(mensagem);
			body.setEmail(coord.getPessoa().getEmail());
			body.setContentType(MailBody.HTML);
			Mail.send(body);
		}

		// Enviando um e-mail de confirma��o para o discente
		if (getUsuarioLogado().getEmail() != null) {

		    // �nica informa��o cadastrada sobre a prova seletiva do projeto
		    ProvaSelecao prova = provaSelecao;

		    // enviando e-mail para o candidato
		    MailBody email = new MailBody();
		    email.setAssunto("Inscri��o para sele��o em Projeto de Monitoria");
		    email.setContentType(MailBody.TEXT_PLAN);
		    email.setNome(getUsuarioLogado().getNome());
		    email.setEmail(getUsuarioLogado().getEmail());

		    StringBuffer msg = new StringBuffer();
		    msg.append("Sua inscri��o no processo seletivo do projeto '"
			    + provaSelecao.getProjetoEnsino()
			    .getTitulo()
			    + "' foi confirmada com sucesso!");
		    msg.append("\n\nInforma��es importantes sobre a Prova de Sele��o:\n");
		    msg.append("\nData da Prova: "
			    + new SimpleDateFormat("dd/MM/yyyy").format(prova
				    .getDataProva()));
		    msg.append("\nTotal de Bolsas Remuneradas do Projeto: "
			    + provaSelecao.getVagasRemuneradas());
		    msg.append("\nTotal de Bolsas N�O Remuneradas do Projeto: "
			    + provaSelecao.getVagasNaoRemuneradas());
		    msg.append("\n\nObserva��es:\n"
			    + prova.getInformacaoSelecao());

		    email.setMensagem(msg.toString());
		    Mail.send(email);
		    addMensagemInformation("Opera��o realizada com sucesso.<br/>"
			    + "E-mail de confirma��o enviado para: "
			    + getUsuarioLogado().getEmail());
		    removeOperacaoAtiva();
		}

	    } catch (NegocioException e) {
		addMensagens(e.getListaMensagens());
		notifyError(e);
	    } catch (ArqException e) {
		addMensagemErro(e.getMessage());
		notifyError(e);
	    }

	    return forward(PortalDiscenteMBean.PORTAL_DISCENTE);

	} else {
	    addMensagemErro("Discente deve ser de Gradua��o para se inscrever no programa de Monitoria");
	    return null;
	}

    }

	/**
	 * Carrega a lista de projetos que o discente fez a sele��o para que ele
	 * possa escolher um pra visualizar os resultados.
	 * <br/><br/>
	 * JSP: /sigaa.war/portais/discente/menu_discente.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
    public String popularVisualizarResultados() throws ArqException {

		if ((getDiscenteUsuario() == null) || getDiscenteUsuario().getNivel() != NivelEnsino.GRADUACAO) {
		    addMensagemErro("Somente discentes ativos de gradua��o tem acesso a esta opera��o.");
		    return null;
		}
	
		DiscenteAdapter d = getDiscenteUsuario();
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
	
		// retorna todas as provas onde o aluno se inscreveu
		provas = dao.findByInscricaoDiscente(d.getId());
		if (provas == null || provas.size() == 0) {
		    addMensagemErro("Voc� n�o participou de nenhum processo de sele��o de monitoria, portanto, " +
		    		"n�o pode visualizar os resultados das sele��es.");
		    return null;
		}
	
		return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_VISUALIZAR_RESULTADO_SELECAO_LISTA_PROJETOS);

    }

    public ProvaSelecao getProvaSelecao() {
	return provaSelecao;
    }

    public void setProvaSelecao(ProvaSelecao provaSelecao) {
	this.provaSelecao = provaSelecao;
    }

	/**
	 * Muda os projetos do MBean conforme id do centro
	 * <br/><br/>
	 * JSP: n�o invocado por JSP
	 * @param evt
	 *            idCentro
	 * @throws DAOException
	 */
    public void changeCentroListaEmAberto(ValueChangeEvent evt) throws DAOException {
	Integer idCentro = new Integer(evt.getNewValue().toString());
	atualizaProvas(idCentro, DateUtils.truncate(new Date(),	Calendar.DAY_OF_MONTH)); // lista s� as que est�o em aberto
    }

	/**
	 * Muda os projetos do MBean conforme id do centro
	 * <br/><br/>
	 * JSP: /sigaa.war/extensao/DetalhesSelecaoExtensao/lista_atividades.jsp
	 * @param evt
	 *            idCentro
	 * @throws DAOException
	 */
    public void changeCentroListaTodos(ValueChangeEvent evt)throws DAOException {
	Integer idCentro = new Integer(evt.getNewValue().toString());
	// lista todas as provas
	atualizaProvas(idCentro, null);
    }

	/**
	 * Lista todas as provas dos projeto com sele��o em aberto
	 * <br/><br/>
	 * JSP: /sigaa.war/monitoria/index.jsp
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
    public String listarInscricoesSelecaoAbertas() throws ArqException {
	// null = projetos de todos os centros,
	atualizaProvas(null, DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
	return forward(ConstantesNavegacaoMonitoria.DETALHESSELECAOMONITORIA_LISTA_PROJETOS);
    }

	/**
	 * Atualiza a lista de provas do MBean
	 * <br/><br/>
	 * JSP: N�o invocado por JSP
	 * @param idCentro
	 * @param data
	 * @throws DAOException
	 */
    private void atualizaProvas(Integer idCentro, Date data)throws DAOException {
	if ((idCentro != null) && (idCentro < 0)) {
	    idCentro = null;
	}
	ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
	provas = dao.findByCentroAndPrazoInscricao(idCentro, data);
    }

	/**
	 * Seleciona o projeto para inscri��o na prova seletiva
	 * <br/><br/>
	 * JSP: N�o invocado por JSP
	 * 
	 * @return
	 * @throws ArqException
	 */
    public void escolherProjetoMonitoria() throws ArqException {
	GenericDAO dao = getGenericDAO();
	int idProjeto = getParameterInt("id");
	projetoEnsino = dao.findByPrimaryKey(idProjeto, ProjetoEnsino.class);
	projetoEnsino.getDiscentesMonitoria().iterator();
    }

	/**
	 * Lista todas as orienta��es do discente monitoria cadastrado!
	 * <br/><br/>
	 * JSP: /sigaa.war/monitoria/DiscenteMonitoria/cadastro_dados_bancarios.jsp
	 * @return
	 * @throws DAOException 
	 */
    public Collection<Orientacao> getOrientacoesDiscenteMonitoriaLogado() throws DAOException {
	    return getGenericDAO().findByExactField(Orientacao.class, "discenteMonitoria.id", obj.getId());
    }

	/**
	 * Retorna todos os DiscenteMonitoria do usu�rio logado
	 * <br/><br/>
	 * JSP: /sigaa.war/monitoria/DiscenteMonitoria/meus_projetos.jsp
	 * @return
	 */
    public Collection<DiscenteMonitoria> getDiscentesMonitoriaUsuarioLogado() {
		try {
		    DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
		    DiscenteAdapter d = getDiscenteUsuario();
		    if ((d != null) && (d.getId() > 0)) {
			return dao.filter(null, null, d.getId(), null, null, null, null, null, null, null);
		    } else {
			return null;
		    }
		} catch (DAOException e) {
		    notifyError(e);
		    return null;
		}
    }

	/**
	 * Carrega o discente selecionado e vai pra tela de cadastro.
	 * <br/><br/>
	 * JSP: /sigaa.war/monitoria/DiscenteMonitoria/meus_projetos.jsp
	 * @return
	 */
    public String cadastrarDadosBancarios() throws ArqException {

	prepareMovimento(ArqListaComando.ALTERAR);
	loadDiscenteMonitoria();
	if (obj.getBanco() == null) {
	    obj.setBanco(new Banco());
	}

	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_CADASTRAR_DADOS_BANCARIOS);

    }

	/**
	 * Carrega um discente de monitoria do banco pra o contexto.
	 * <br/>
	 * JSP: N�o invocado por JSP
	 */
    public void loadDiscenteMonitoria() {
	try {
	    int id = getParameterInt("idDiscenteMonitoria");
	    GenericDAO dao = getGenericDAO();
	    obj = dao.findByPrimaryKey(id, DiscenteMonitoria.class);
	    obj.getOrientacoes().iterator();
	} catch (DAOException e) {
	    notifyError(e);
	}
    }

	/**
	 * Retorna lista de bancos cadastrados
	 * <br/><br/>
	 * JSP: /sigaa.war/extensao/PlanoTrabalho/form.jsp
	 * @return
	 */
    public Collection<SelectItem> getAllBancoCombo() {
	try {
	    GenericDAO dao = getGenericDAO();
	    return toSelectItems(dao.findByExactField(Banco.class, "publicado", true), "id", "denominacao");
	} catch (Exception e) {
	    notifyError(e);
	    addMensagemErro(e.getMessage());
	    return null;
	}
    }

	/**
	 * Atualiza dados banc�rios de discente de monitoria
	 * <br/><br/>
	 * JSP: sigaa.war/monitoria/DiscenteMonitoria/cadastro_dados_bancarios.jsp
	 * @return
	 * @throws ArqException 
	 */
    public String atualizarDadosBancarios() throws ArqException {

	if (!confirmaSenha()) {
	    return null;
	}

	MovimentoCadastro mov = new MovimentoCadastro();
	mov.setCodMovimento(ArqListaComando.ALTERAR);
	mov.setObjMovimentado(obj);
	try {

	    execute(mov, getCurrentRequest());
	    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

	    if (getUsuarioLogado().getEmail() != null) {

		// enviando e-mail para o discente
		MailBody email = new MailBody();
		email.setAssunto("Altera��o de Dados Banc�rios!");
		email.setContentType(MailBody.TEXT_PLAN);
		email.setNome(getUsuarioLogado().getNome());
		email.setEmail(getUsuarioLogado().getEmail());

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		StringBuffer msg = new StringBuffer();
		msg.append("Dados Banc�rios alterados com sucesso!");
		msg.append("\n\nSeus novos Dados Banc�rios cadastrados no SIGAA s�o:\n");
		msg.append("----------------------------------------------------------------");
		msg.append("\nBanco:   " + obj.getBanco().getDenominacao());
		msg.append("\nN� Ag�ncia:   " + obj.getAgencia());
		msg.append("\nN� Conta Corrente:   " + obj.getConta());
		msg.append("\nN� Opera��o:   " + obj.getOperacao());
		msg.append("\n--------------------------------------------------------------");
		msg.append("\nAs altera��es foram feitas em: "	+ df.format(new Date()));
		email.setMensagem(msg.toString());
		Mail.send(email);

	    }

	} catch (Exception e) {
	    addMensagemErro(e.getMessage());
	    e.printStackTrace();
	    return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_CADASTRAR_DADOS_BANCARIOS);
	}

	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_MEUS_PROJETOS);

    }
    
    /** 
     * Volta para a tela das provas cadastradas 
     *
     * <br/><br/>
	 * 	<ul>
	 * 		<li> JSP: /SIGAA/app/sigaa.ear/sigaa.war/monitoria/DiscenteMonitoria/form.jsp </li>
	 * 		<li> JSP: /SIGAA/app/sigaa.ear/sigaa.war/monitoria/ProvaSelecao/convocar_discentes.jsp </li>
	 * 		<li> JSP: /SIGAA/app/sigaa.ear/sigaa.war/monitoria/ProvaSelecao/indicacao_discente.jsp </li>
	 *  </ul>
     */
    public String voltaListaProvas(){
    	return forward("/monitoria/CadastrarProvaSelecao/projetos.jsp");
    }
    
	/**
	 * Iniciar a aceita��o do discente no projeto de monitoria.
	 * <br/><br/>
	 * JSP: sigaa.war/monitoria/DiscenteMonitoria/meus_projetos.jsp
	 * @return p�gina onde o discente ir� aceitar ou recusar participa��o.
	 */
    public String iniciarAceitarOuRecusarMonitoria() throws ArqException {

	prepareMovimento(SigaaListaComando.ACEITAR_OU_RECUSAR_MONITORIA);
	loadDiscenteMonitoria();
	//Valida��o pr�via dos dados banc�rios.
	ListaMensagens mensagens = new ListaMensagens();
	if (obj.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA) {	    
	    DiscenteMonitoriaValidator.validaDadosBancarios(obj, mensagens );
	}
	if (obj.getSituacaoDiscenteMonitoria().getId() != SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA) {
	    mensagens.addErro("Somente discentes convocados podem realizar esta opera��o.");
	}
	if (!mensagens.isEmpty()) {
	    addMensagens(mensagens);
	    return null;
	}
	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_ACEITAR_RECUSAR_MONITORIA);
	
    }
    
    
	/**
	 * Permite que discentes convocados (aprovados em prova seletiva)
	 * confirmem ou recusem sua participa��o no projeto de monitoria.
	 * <br/><br/>
	 * JSP: <ul><li>/sigaa.war/monitoria/DiscenteMonitoria/aceitar_recusar_monitoria.jsp</li>
	 * 			<li>/sigaa.war/monitoria/DiscenteMonitoria/meus_projetos.jsp</li></ul>
	 * 
	 * @return p�gina de projetos de monitoria do discente logado.
	 */
    public String aceitarOuRecusarMonitoria() {

	try {	    
	    
	    if ((!obj.isSelecionado()) && (ValidatorUtil.isEmpty(obj.getObservacao()))) {
		addMensagemErro("Justificativa: campo obrigat�rio n�o informado.");
		return null;
	    }
	    
	    DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
	    mov.setCodMovimento(SigaaListaComando.ACEITAR_OU_RECUSAR_MONITORIA);
	    mov.setDiscenteMonitoria(obj);
	    execute(mov, getCurrentRequest());
	    addMensagemInformation("Opera��o realizada com sucesso.");
	} catch (NegocioException e) {
	    addMensagemErro(e.getMessage());
	    return null;
	} catch (Exception e) {
	    addMensagemErro(e.getMessage());
	    return null;
	}

	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_MEUS_PROJETOS);

    }
    

	/**
	 * Retorna lista de situa��es do discente de monitoria cadastradas.
	 * <br/><br/>
	 * JSP: <ul><li>/sigaa.war/monitoria/CadastrarMonitor/form.jsp</li>
	 * 			<li>/sigaa.war/monitoria/Relatorios/monitores_mes_form.jsp</li></ul>
	 * @return
	 */
    public Collection<SelectItem> getAllSituacaoDiscenteMonitoriaCombo() {

	try {
	    GenericDAO dao = getGenericDAO();
	    return toSelectItems(dao.findAll(SituacaoDiscenteMonitoria.class),  "id", "descricao");
	} catch (Exception e) {
	    notifyError(e);
	    addMensagemErro(e.getMessage());
	    return null;
	}

    }
    
    /** 
     * Cadastrar apenas o resultado da prova seletiva pelo coordenador do projeto.
     *
	 * <br/><br/>
	 * JSP: 
	 * 		<ul>
	 * 			<li> /SIGAA/app/sigaa.ear/sigaa.war/monitoria/DiscenteMonitoria/form.jsp </li>
	 * 		</ul>
     * 
     * @return
     * @throws SegurancaException
     */
    public String cadastrarResultadoProvaSelecao() throws SegurancaException{
		//verifica pap�is
		checkDocenteRole();
    	checkChangeRole();
		//verifica prova sem resultado
    	if ((provaSelecao == null) || ValidatorUtil.isEmpty(provaSelecao.getResultadoSelecao())) {
    	    addMensagemErro("Lista de resultados cadastrados est� vazia.");
    	    return redirectMesmaPagina();
    	}
		
	    DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
	    mov.setProvaSelecao(provaSelecao);
	    mov.setCodMovimento(SigaaListaComando.CADASTRAR_RESULTADO_PROVA_SELECAO);
	    mov.setCalendarioAcademico(getCalendarioVigente());
	    
	    try {

		execute(mov, getCurrentRequest());
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		// limpa lista de sele��es marcadas para remo��o
		obj = new DiscenteMonitoria();

	    } catch (NegocioException e) {
		addMensagemErro(e.getMessage());
	    } catch (ArqException e) {
		addMensagemErroPadrao();
		notifyError(e);
	    }

	    resetBean();

	    // return redirectPage(getListPage());
	    return forward(PortalDocenteMBean.PORTAL_DOCENTE);	
	
	}
    
    /**
	 * Carrega sele��o e a lista de resultados de sele��es j� realizadas para aquele
	 * projeto.
	 * <br/><br/>
	 * JSP:
	 * <ul> 
	 * 	<li>/sigaa.war/monitoria/CadastrarProvaSelecao/projetos.jsp</li>	
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
    public  String iniciarConvocacaoProvaSelecao() throws SegurancaException {

    	checkChangeRole();

    	try {

    		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
    		Integer idProva = getParameterInt("idProva");
    		provaSelecao = dao.findByPrimaryKey(idProva, ProvaSelecao.class);

    		// valida��es
    		ListaMensagens mensagens = new ListaMensagens();
    		DiscenteMonitoriaValidator.validaIniciarCadastroSelecao(provaSelecao, CalendarUtils.getAnoAtual(), mensagens);
    		if (!mensagens.isEmpty()) {
    			addMensagens(mensagens);
    			return null;
    		}

    		// discentes de monitoria com resultado j� cadastrado...
    		provaSelecao.setResultadoSelecao(dao.findByProvaSeletiva(idProva));
    		for (DiscenteMonitoria dis : provaSelecao.getResultadoSelecao()) {
    			dis.getOrientacoes().iterator();
    		}

    		// s� mostra bolsas que restaram....
    		totalBolsistasProjeto = dao.countMonitoresByProjeto(provaSelecao.getProjetoEnsino().getId(), TipoVinculoDiscenteMonitoria.BOLSISTA);
    		totalVoluntariosProjeto = dao.countMonitoresByProjeto(provaSelecao.getProjetoEnsino().getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);
    		
    		

    	} catch (Exception e) {
    		addMensagemErro(e.getMessage());
    		notifyError(e);
    	}

    	return forward("/monitoria/ProvaSelecao/convocar_discentes.jsp");

    }
    
    /**
     * Iniciar a Orienta��o dos discentes convocados. 
     * 
	 * <br/><br/>
	 * JSP:
	 * <ul> 
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/monitoria/ProvaSelecao/convocar_discentes.jsp </li>	
	 * </ul>
     * 
     */
    public String iniciarConvocacaoOrientacaoDiscente() throws SegurancaException, DAOException{
    	
    	checkChangeRole();
    	int idDiscente = getParameterInt("idDiscente", 0);
    	DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
    	try {
    		orientadores = getDAO(EquipeDocenteDao.class).findByProjeto(provaSelecao.getProjetoEnsino().getId(), true);
    		
    		
    		if (idDiscente == 0) {
    			addMensagemErro("N�o foi poss�vel encontrar discente");
    			return null;
    		}
    		else{
    			this.obj = dao.findByPrimaryKey(idDiscente, DiscenteMonitoria.class);
    			this.obj.setOrientacoes(getDAO(OrientacaoDao.class).findByExactField(Orientacao.class, "discenteMonitoria.id", obj.getId()));
    		}
		} finally {
			dao.close();
		}
    	return forward("/monitoria/ProvaSelecao/indicacao_discente.jsp");
    }
    
    
    /**
     * Realiza a convoca��o de orienta��o por discente. 
     * 
	 * <br/><br/>
	 * JSP:
	 * <ul> 
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/monitoria/ProvaSelecao/indicacao_discente.jsp </li>	
	 * </ul>
     * 
     * @return
     * @throws ArqException 
     */
    public String convocacaoOrientacaoDiscente() throws ArqException{
    	checkChangeRole();
    	ListaMensagens lista = new ListaMensagens();
    	
    	if(obj.getTipoVinculo() == null || obj.getTipoVinculo().getId() <= 0){
    		lista.addErro("V�nculo: Campo obrigat�rio n�o informado.");
    	}
    	
    	if(obj.getSituacaoDiscenteMonitoria() == null || obj.getSituacaoDiscenteMonitoria().getId() <= 0){
    		lista.addErro("Situa��o: Campo obrigat�rio n�o informado.");
    	}
    	
    	
    	if(!lista.isEmpty()){
    		addMensagens(lista);
    		return null;
    	}
    	
    	DiscenteMonitoriaValidator.validaDiscenteComBolsa(obj, lista);
    	DiscenteMonitoriaValidator.validaPeriodoConfirmacaoMonitoria(obj, lista);
    	DiscenteMonitoriaValidator.validaDadosPrincipais(obj, lista);
    	
    	if ((obj.getDataInicio() != null) && (obj.getDataFim() != null)) {
			ValidatorUtil.validaInicioFim(obj.getDataInicio(), obj.getDataFim(), "Per�odo", lista);
			
			boolean inicioForaProjeto = CalendarUtils.isDentroPeriodo(obj.getProjetoEnsino().getProjeto().getDataInicio(), obj.getProjetoEnsino().getProjeto().getDataFim(), obj.getDataInicio());
			boolean fimForaProjeto = CalendarUtils.isDentroPeriodo(obj.getProjetoEnsino().getProjeto().getDataInicio(), obj.getProjetoEnsino().getProjeto().getDataFim(), obj.getDataFim());
			if (!inicioForaProjeto || !fimForaProjeto) {
				lista.addErro("Per�odo da monitoria do Discente devem estar dentro do per�odo de vig�ncia do Projeto. (" 
						+ Formatador.getInstance().formatarData(obj.getProjetoEnsino().getProjeto().getDataInicio()) + " at� " 
						+ Formatador.getInstance().formatarData(obj.getProjetoEnsino().getProjeto().getDataFim()) +")");
			}
		}
    	
    	if(!lista.isEmpty() || hasErrors()){
    		addMensagens(lista);
    		return null;
    	}
    	//validar orienta��es
    	for (EquipeDocente orient : orientadores) {
			if(orient.isSelecionado()){
				orientacao = new Orientacao();
				DiscenteMonitoriaValidator.validaMaximoOrientacoesDocentes(orient, orient.getDataInicioOrientacao(), orient.getDataFimOrientacao(), lista);
				getGenericDAO().initialize(orient);
				orientacao.setEquipeDocente(orient);
				orientacao.setDataInicio(orient.getDataInicioOrientacao());
				orientacao.setDataFim(orient.getDataFimOrientacao());
				orientacao.setDiscenteMonitoria(obj);
		    	orientacao.setAtivo(true);
		    	orientacao.setDiscenteMonitoria(obj);
		    	orientacao.setDiscenteNome(obj.getDiscente().getPessoa().getNome());
		    	obj.getOrientacoes().add(orientacao);
			}
		}
    	
    	if(!lista.isEmpty() || hasErrors()){
    		addMensagens(lista);
    		return null;
    	}
    	
    	DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
 	    mov.setProvaSelecao(provaSelecao);
 	    mov.setDiscenteMonitoria(obj);
 	    prepareMovimento(SigaaListaComando.CADASTRAR_ORIENTACOES_CONVOCAR);
 	    mov.setCodMovimento(SigaaListaComando.CADASTRAR_ORIENTACOES_CONVOCAR);
 	    mov.setCalendarioAcademico(getCalendarioVigente());
 	    
 	    try {

 		execute(mov, getCurrentRequest());
 		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

 		// limpa lista de sele��es marcadas para remo��o
 		obj = new DiscenteMonitoria();

 	    } catch (NegocioException e) {
 		addMensagemErro(e.getMessage());
 	    } catch (ArqException e) {
 		addMensagemErroPadrao();
 		notifyError(e);
 	    }

 	    resetBean();

 	    // return redirectPage(getListPage());
 	    return forward(PortalDocenteMBean.PORTAL_DOCENTE);	
 	
    }

    /** 
     * Respons�vel por iniciar o caso de uso de Envio de email
	 * <br/><br/>
	 * JSP:
	 * <ul> 
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/monitoria/CadastrarProvaSelecao/projetos.jsp </li>	
	 * </ul>
     */
    public String iniciarEnvioEmail() throws DAOException{
    	idProva = getParameterInt("idProva", 0);
    	return inicioEnvioEmail();
    }

    /**
     * Iniciar o envio de email, carregando os discentes inscritos para que seja poss�vel a sele��o de uma para a realiza��o 
     * do envio. 
     * 
	 * <br/><br/>
	 * JSP:
	 * <ul> 
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/monitoria/DetalhesSelecaoMonitoria/envio_email_candidato.jsp </li>	
	 * </ul>
     * 
     * @return
     * @throws DAOException
     */
	public String inicioEnvioEmail() throws DAOException {
		if ( idProva > 0 ) {

    		provaSelecao = getGenericDAO().findByPrimaryKey(idProva, ProvaSelecao.class);
    		
    		InscricaoSelecaoMonitoriaDao dao = getDAO(InscricaoSelecaoMonitoriaDao.class);
    		provaSelecao.setDiscentesInscritos(dao.findByProvaSeletiva(idProva));

    		return forward(ConstantesNavegacaoMonitoria.DETALHESSELECAOMONITORIA_NOTIFICAR_CANDIDATO);

    	} else {
    		addMensagemErro("Prova de sele��o n�o informada.");
    		return null;
    	}
	}
    
	/**
	 * Direciona o usu�rio para a tela onde o mesmo informar as informa��es do email. 
	 * 
	 * <br/><br/>
	 * JSP:
	 * <ul> 
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/monitoria/DetalhesSelecaoMonitoria/listar_candidatos_envio_email.jsp </li>	
	 * </ul>
     * 
	 * @return
	 * @throws DAOException
	 */
    public String preEnvioEmail() throws DAOException{
    	inscricaoSelMonitoria = new InscricaoSelecaoMonitoria();
    	inscricaoSelMonitoria.setNotificacao(new Notificacao());
    	grupoEmail = new LinkedList<InscricaoSelecaoMonitoria>();
    	Integer idInscricaoSelecao = getParameterInt("id", 0);
    	for (InscricaoSelecaoMonitoria inscricao : provaSelecao.getDiscentesInscritos()){
    		if (inscricao.getId() == idInscricaoSelecao)
    			grupoEmail.add(inscricao);
    	}
    	if (!isEmpty(grupoEmail)) {
    		carregarEmail(grupoEmail);
			return forward(ConstantesNavegacaoMonitoria.DETALHESSELECAOMONITORIA_FORMULARIO_GRUPO_CANDIDATO );
    	} else {
    		addMensagemErro("Selecione pelo menos um discente.");
    		return null;
    	}
    }
    
    /**
	 * Direciona o usu�rio para a tela onde o mesmo informar as informa��es do email para um grupo. 
	 * 
	 * <br/><br/>
	 * JSP:
	 * <ul> 
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/monitoria/DetalhesSelecaoMonitoria/listar_candidatos_envio_email.jsp </li>	
	 * </ul>
     * 
	 * @return
	 * @throws DAOException
	 */
    public String preEnvioEmailGrupo() throws DAOException{
    	inscricaoSelMonitoria = new InscricaoSelecaoMonitoria();
    	inscricaoSelMonitoria.setNotificacao(new Notificacao());
    	grupoEmail = new LinkedList<InscricaoSelecaoMonitoria>();
    	for (InscricaoSelecaoMonitoria inscricao : provaSelecao.getDiscentesInscritos()){
    		if (inscricao.getDiscente().isSelecionado())
    			grupoEmail.add(inscricao);
    	}
    	if (!isEmpty(grupoEmail)) {
    		carregarEmail(grupoEmail);
			return forward(ConstantesNavegacaoMonitoria.DETALHESSELECAOMONITORIA_FORMULARIO_GRUPO_CANDIDATO );
    	} else {
    		addMensagemErro("Selecione pelo menos um discente.");
    		return null;
    	}
    }
    
    /**
     * Carrega as informa��es necess�rios para o envio do email para o discente selecioando. 
     * @throws DAOException
     */
    private void carregarEmail() throws DAOException {
    	grupoEmail = new LinkedList<InscricaoSelecaoMonitoria>();
    	grupoEmail.add(inscricaoSelMonitoria);
    	carregarEmail(grupoEmail);
    }
    
    /**
     * Carrega as informa��es necess�rios para o envio do email para um grupo de discentes selecioandos. 
     * @throws DAOException
     */
    private void carregarEmail(Collection<InscricaoSelecaoMonitoria> grupoEmail) throws DAOException {
    	InscricaoSelecaoExtensaoDao dao = getDAO(InscricaoSelecaoExtensaoDao.class);
		try {
			Collection<Destinatario> destinatarios = new ArrayList<Destinatario>();
			for (InscricaoSelecaoMonitoria inscricao : grupoEmail){
				Destinatario destinatario = new Destinatario();
				destinatario.setNome(inscricao.getDiscente().getPessoa().getNome());
				if (inscricao.getDados() != null)
					destinatario.setEmail(inscricao.getDados().getEmail());
				else
					destinatario.setEmail(inscricao.getDiscente().getPessoa().getEmail());
				UsuarioGeral usuarioGeral = new UsuarioGeral(); 
				usuarioGeral.setEmail(destinatario.getEmail());
				usuarioGeral.setId(inscricao.getDiscente().getUsuario().getId());
				destinatario.setUsuario(usuarioGeral);
				destinatarios.add(destinatario);
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Respons�vel pelo envio do email e mensagem para o candidato a bolsa.
	 * 
	 * <br/><br/>
	 * JSP:
	 * <ul> 
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/monitoria/DetalhesSelecaoMonitoria/envio_email_candidato.jsp </li>	
	 * </ul>
     * 
	 * @return
	 */
	public String enviarEmail() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(inscricaoSelMonitoria.getNotificacao().getTitulo(), "Assunto", lista);
		ValidatorUtil.validateRequired(inscricaoSelMonitoria.getNotificacao().getMensagem(), "Mensagem", lista);
		
		if ( !inscricaoSelMonitoria.getNotificacao().isEnviarEmail() && !inscricaoSelMonitoria.getNotificacao().isEnviarMensagem() )
			lista.addErro("� necess�rio informar pelo menos uma forma de envio.");
		
		if ( lista.isErrorPresent() ) {
			addMensagens(lista);
			return null;
		}
		// seta a mesma notifica��o para o grupo de destinat�rios
		for (InscricaoSelecaoMonitoria inscSelMonitor : grupoEmail) {
			inscSelMonitor.setNotificacao(new Notificacao());
			inscSelMonitor.getNotificacao().setTitulo(inscricaoSelMonitoria.getNotificacao().getTitulo());
			inscSelMonitor.getNotificacao().setMensagem(inscricaoSelMonitoria.getNotificacao().getMensagem());
			inscSelMonitor.getNotificacao().setEnviarEmail(inscricaoSelMonitoria.getNotificacao().isEnviarEmail());
			inscSelMonitor.getNotificacao().setEnviarMensagem(inscricaoSelMonitoria.getNotificacao().isEnviarMensagem());
			Destinatario destinatario = new Destinatario();
			destinatario.setEmail(inscSelMonitor.getDiscente().getPessoa().getEmail());
			destinatario.setIdusuario(inscSelMonitor.getDiscente().getUsuario().getId());
			destinatario.setNome(inscSelMonitor.getDiscente().getNome());
			destinatario.setUsuario(inscSelMonitor.getDiscente().getUsuario());
			inscSelMonitor.getNotificacao().adicionarDestinatario(destinatario);
		}
		
		try {
			DiscenteMonitoriaMov mov = new DiscenteMonitoriaMov();
			mov.setProvaSelecao(new ProvaSelecao());
			
			prepareMovimento(SigaaListaComando.NOTIFICAR_INSCRITO_SELECAO);
			mov.setCodMovimento(SigaaListaComando.NOTIFICAR_INSCRITO_SELECAO);
			mov.setDiscenteMonitoria(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.getProvaSelecao().setDiscentesInscritos(grupoEmail);
			execute(mov, getCurrentRequest());
			
			StringBuilder dest = new StringBuilder();
			for (InscricaoSelecaoMonitoria inscSelMonitor : grupoEmail) {
				if (dest.length() > 0) dest.append(", ");
				dest.append(inscSelMonitor.getDiscente().getNome());
			}
			if (grupoEmail.size() > 2)
				addMensagemInformation("Sua mensagem foi enviada com sucesso para os seguintes discentes: " + dest);
			else
				addMensagemInformation("Sua mensagem foi enviada com sucesso para: " + dest);

			inscricaoSelMonitoria = new InscricaoSelecaoMonitoria();
			return forward("/monitoria/DetalhesSelecaoMonitoria/listar_candidatos_envio_email.jsp");
		}catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return null;
	}
    
    public Collection<ProjetoEnsino> getProjetos() {
    	return projetos;
    }

	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	public Collection<EquipeDocente> getDocentes() {
		return docentes;
	}

	public void setDocentes(Collection<EquipeDocente> docentes) {
		this.docentes = docentes;
	}

	public Set<DiscenteMonitoria> getDiscentesRemovidos() {
		return discentesRemovidos;
	}

	public void setDiscentesRemovidos(Set<DiscenteMonitoria> discentesRemovidos) {
		this.discentesRemovidos = discentesRemovidos;
	}

	public Collection<ProvaSelecao> getProvas() {
		return provas;
	}

	public void setProvas(Collection<ProvaSelecao> provas) {
		this.provas = provas;
	}

	/** retorna todos discentes inscritos na prova e ordenados */
	public List<DiscenteMonitoria> getDiscentesInscritosNaoCadastrados() {
	    Collections.sort(discentesInscritosNaoCadastrados, new DiscenteMonitoriaPorPrioridadeComparator());
	    return discentesInscritosNaoCadastrados;
	}

	public void setDiscentesInscritosNaoCadastrados(List<DiscenteMonitoria> discentesInscritosNaoCadastrados) {
		this.discentesInscritosNaoCadastrados = discentesInscritosNaoCadastrados;
	}

	public Set<Orientacao> getOrientacoesRemovidas() {
		return orientacoesRemovidas;
	}

	public void setOrientacoesRemovidas(Set<Orientacao> orientacoesRemovidas) {
		this.orientacoesRemovidas = orientacoesRemovidas;
	}

	public Object getTotalBolsistasProjeto() {
		return totalBolsistasProjeto;
	}

	public void setTotalBolsistasProjeto(Object totalBolsistasProjeto) {
		this.totalBolsistasProjeto = totalBolsistasProjeto;
	}

	public Object getTotalVoluntariosProjeto() {
		return totalVoluntariosProjeto;
	}

	public void setTotalVoluntariosProjeto(Object totalVoluntariosProjeto) {
		this.totalVoluntariosProjeto = totalVoluntariosProjeto;
	}

	public DadosAluno getDados() {
		return dados;
	}

	public void setDados(DadosAluno dados) {
		this.dados = dados;
	}

	public Collection<EquipeDocente> getOrientadores() {
		return orientadores;
	}

	public void setOrientadores(Collection<EquipeDocente> orientadores) {
		this.orientadores = orientadores;
	}

	public Orientacao getOrientacao() {
		return orientacao;
	}

	public void setOrientacao(Orientacao orientacao) {
		this.orientacao = orientacao;
	}

	public InscricaoSelecaoMonitoria getInscricaoSelMonitoria() {
		return inscricaoSelMonitoria;
	}

	public void setInscricaoSelMonitoria(
			InscricaoSelecaoMonitoria inscricaoSelMonitoria) {
		this.inscricaoSelMonitoria = inscricaoSelMonitoria;
	}

	public int getIdProva() {
		return idProva;
	}

	public void setIdProva(int idProva) {
		this.idProva = idProva;
	}

	public Collection<InscricaoSelecaoMonitoria> getGrupoEmail() {
		return grupoEmail;
	}
	
}