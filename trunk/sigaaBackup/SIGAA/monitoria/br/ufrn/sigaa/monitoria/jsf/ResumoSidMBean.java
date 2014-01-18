/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
* 
* Created on 07/12/2009
*/
package br.ufrn.sigaa.monitoria.jsf; 

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ParticipacaoSidDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ResumoSidDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ParticipacaoSid;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ResumoSid;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.negocio.CalendarioMonitoriaHelper;
import br.ufrn.sigaa.monitoria.negocio.ParticipacaoSidMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
* 
* Controla as páginas que manipulam o Resumo de seminário de iniciação à docência.
*
* @author UFRN
*/
@Component("resumoSid") @Scope("session")
public class ResumoSidMBean extends SigaaAbstractController<ResumoSid> {
	
	/**Resumo das atividades do projeto para o seminário de iniciação à docência*/
	private Collection<ResumoSid> resumos;
	/** Título do projeto */
	private String  tituloProjeto = null; 
	/** Indica se filtra a busca por ano do projeto. */
	private boolean checkBuscaAno =  false;
	/** Indica se filtra a busca por ano do seminário de iniciação à docência. */
	private boolean checkBuscaAnoSid =  false;
	/** Indica se filtra a busca pelo titulo do projeto. */
	private boolean checkBuscaProjeto =  false;
	/** Indica se filtra a busca pelo id do servidor. */
	private boolean checkBuscaServidor = false;
	/**Guarda o projeto de ensino selecionado na busca*/
	private ProjetoEnsino buscaProjetoEnsino = new ProjetoEnsino();
	/**Parametro: ano do projeto usado na busca do resumo*/
	private Integer buscaAnoProjeto = CalendarUtils.getAnoAtual();
	/**Parametro: ano do seminario usado na busca do resumo*/
	private Integer buscaAnoSid = CalendarUtils.getAnoAtual();
	/**Parametro:id do servidor usado na busca do resumo*/
	private Servidor buscaServidor =  new Servidor();
	/**classe é utilizada para avaliar um projeto ou um resumo SID */
	private AvaliacaoMonitoria avaliacaoMonitoria  = new AvaliacaoMonitoria();
	/**lista de participações de discente de monitoria em resumo do SID */
	private Collection<ParticipacaoSid> participacoes;
	/**Define o caminho da proxima jsp*/
	private String urlAfterSearch = null;
	/**Quantidade maxima de resumo sid por ano de um determinao projeto*/
	private int qntMaximaResumoSid;
	
	public ResumoSidMBean() {
		obj = new ResumoSid();		
	}
	

	/**
	 * Retorna todos os Resumos passíveis de serem avaliados
	 * pelo usuário logado (membro da comissão de monitoria)
	 *
	 * Não foi encontrado jsp que chame o método
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public Collection<AvaliacaoMonitoria> getResumosParaAvaliar() throws SegurancaException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA, SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA);
	    try {
		if (getUsuarioLogado().getServidor() == null){
		    return new ArrayList<AvaliacaoMonitoria>();
		}				
		AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class);
		Collection<AvaliacaoMonitoria> avaliacoes = dao.findByAvaliador(getUsuarioLogado().getServidor().getId(), 
			TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID );
		for(AvaliacaoMonitoria av : avaliacoes) {
			av.getProjetoEnsino().getProjeto().getId();
			av.getProjetoEnsino().getProjeto().getTitulo();
		}
		return avaliacoes;
	    } catch(Exception e) {
		notifyError(e);
		return new ArrayList<AvaliacaoMonitoria>();
	    }
	}

	/**
	 * Lista resumo de projetos
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Chamado pela jsp: sigaa.war/monitoria/ResumoSid/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String listarResumosProjeto() throws ArqException{
	    checkDocenteRole();
	    prepareMovimento(SigaaListaComando.CADASTRAR_RESUMO_SID);

	    ResumoSidDao dao =  getDAO(ResumoSidDao.class);	
	    Integer idProjeto = getParameterInt("id", 0);
	    buscaProjetoEnsino = dao.findByPrimaryKey(idProjeto, ProjetoEnsino.class);		
	    resumos = dao.findByProjeto(buscaProjetoEnsino);

	    //é possivel enviar a quantidade maxima resumo de seminário de iniciação à docência por projeto por ano para atender todos bolsistas	    
	    
	    int maximoBolsistasSid = ParametroHelper.getInstance().getParametroInt(ParametrosMonitoria.NUMERO_MAXIMO_BOLSISTAS_MONITORIA);
	    qntMaximaResumoSid = (int) Math.round(((double)( buscaProjetoEnsino.getBolsasSolicitadas() ) / maximoBolsistasSid)+0.5d);
	    
	    return forward(ConstantesNavegacaoMonitoria.CADASTRARRESUMO_RESUMOS);		
	}

	/**
	 * Checa se o usuário é docente
	 * 
	 * @throws SegurancaException
	 */
	@Override
	public void checkDocenteRole() throws SegurancaException {
		if ( !isUserInRole(SigaaPapeis.GESTOR_MONITORIA) )
			super.checkDocenteRole();
	}

	/**
	 * Carrega o resumo seminário de iniciação à docência para a exibição
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/DistribuicaoResumoSid/lista.jsp</li>
	 * 		<li>sigaa.war/monitoria/ResumoSid/busca.jsp</li>
	 * 		<li>sigaa.war/monitoria/ResumoSid/resumos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException{		
	    setId();		
	    GenericDAO dao = getGenericDAO();
	    obj = dao.findByPrimaryKey(obj.getId(), ResumoSid.class);
	    obj.getParticipacoesSid().iterator();
	    return forward(getViewPage());		
	}
	
	/**
	 * visualiza resumo seminário de iniciação à docência selecionado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\ResumoSid\lista_avaliar.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String viewSid() throws DAOException{		
		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(id, ResumoSid.class);
		obj.getParticipacoesSid().iterator();
		return forward(getViewPage());		
	}


	/**
	 * Redireciona para tela com todas as avaliações do projeto
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ResumoSid/resumos.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String viewAvaliacoesProjeto() throws DAOException{		
	    int id = getParameterInt("idProjeto", 0);		
	    ProjetoMonitoriaMBean pm = (ProjetoMonitoriaMBean) getMBean("projetoMonitoria");
	    pm.setObj(getGenericDAO().findByPrimaryKey(id, ProjetoEnsino.class));		
	    return forward(ConstantesNavegacaoMonitoria.VISUALIZARAVALIACOES_LISTA);		
	}


	/**
	 * Lista todos os discentes do projeto que participaram do resumo seminário de iniciação à docência.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Chamado pela jsp: sigaa.war/monitoria/ResumoSid/busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarParticipacoesSid() throws ArqException{		
	    int id = getParameterInt("idProjeto", 0);		
	    ParticipacaoSidDao dao = getDAO(ParticipacaoSidDao.class);		
	    buscaProjetoEnsino = dao.findByPrimaryKey(id, ProjetoEnsino.class);		
	    participacoes = dao.findByProjeto(id, null, null);		
	    boolean registrarFrequencia = getParameterBoolean("registrarFrequencia");

	    if(registrarFrequencia){			
		prepareMovimento(SigaaListaComando.REGISTRAR_FREQUENCIA_RESUMO_SID);
		return forward(ConstantesNavegacaoMonitoria.REGISTRO_FREQUENCIA_DISCENTE_RESUMO_SID);			
	    }else{			
		return forward(ConstantesNavegacaoMonitoria.RESUMO_SID_LISTA_PARTICIPACOES);			
	    }		
	}

	
	
	
	/**
	 * Registra participação de um discente no resumo seminário de iniciação à docência.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ResumoSid/registro_frequencia_discente_sid.jsp<li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String registrarFrequencia() throws DAOException, SegurancaException{
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);		
	    try {
		ParticipacaoSidMov mov = new ParticipacaoSidMov();
		mov.setParticipacoes(participacoes);			
		mov.setCodMovimento(SigaaListaComando.REGISTRAR_FREQUENCIA_RESUMO_SID);
		execute(mov, getCurrentRequest());
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	    } catch (Exception e) {
		tratamentoErroPadrao(e);
	    }
	    return forward(ConstantesNavegacaoMonitoria.BUSCAR_RESUMO_SID);

	}

	
	
	/**
	 * 
	 * Cadastra o resumo de seminário de iniciação à docência caso seja possível tal cadastro.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Chamado pela jsp: sigaa.war/monitoria/ResumoSid/resumos.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarResumoSid() throws ArqException {
	    checkDocenteRole();

	    try {

		if (buscaProjetoEnsino != null){
		    CalendarioMonitoria calendario = getDAO(ProjetoMonitoriaDao.class).findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual()); 

		    if ((calendario == null) || (!calendario.isAtivo())){
		    	addMensagem(MensagensMonitoria.PERIODO_ENVIO_RESUMOS_NAO_DEFINIDO);
		    	return null;				
		    }

		    if ( !CalendarioMonitoriaHelper.isPeriodoSubmissaoSID(buscaProjetoEnsino) ) {									
		    	addMensagem(MensagensMonitoria.SUBMISSAO_RESUMO_FORA_DO_PRAZO);
		    	return null;									
		    
		    } else {			

				//verifica se tá no período do recebimento do resumo
				if (calendario.isEnvioResumoSidEmAberto()){
	
					ResumoSidDao dao = getDAO(ResumoSidDao.class);														
					Collection<ResumoSid> resumosCadastrados = dao.findByProjetoAnoSid(buscaProjetoEnsino.getId(), CalendarUtils.getAnoAtual());
					buscaProjetoEnsino = dao.findByPrimaryKey(buscaProjetoEnsino.getId(), ProjetoEnsino.class);
					
				    if ( resumosCadastrados.size() < qntMaximaResumoSid ) {
					
						//criando novo resumo para o projeto
						obj = new ResumoSid();
						obj.setAnoSid(CalendarUtils.getAnoAtual());
						prepareMovimento(SigaaListaComando.CADASTRAR_RESUMO_SID);
						obj.setDataEnvio(new Date());
		
						for (DiscenteMonitoria dm : buscaProjetoEnsino.getDiscentesMonitoria()) {
						    //lista somente os discentes que realmente estão/foram na monitores...
						    if ((dm.isAssumiuMonitoria() || dm.isFinalizado()) 
							    && (dm.isVinculoBolsista() || dm.isVinculoNaoRemunerado())) {
		
							ParticipacaoSid ps = new ParticipacaoSid();
							ps.setResumoSid(obj);
							ps.setParticipou(true);
							ps.setDiscenteMonitoria(dm);
							obj.getParticipacoesSid().add(ps);
						    }
						}
		
						// seta a escolha do projeto da lista no obj (resumoSid)
						obj.setProjetoEnsino(buscaProjetoEnsino);
	
				    } else {
				    	addMensagemErro("O número máximo de submissões do resumo SID já foi atingido.");
				    	return null;
				    }
				    
				    setConfirmButton("Confirmar");
				    return forward(getFormPage());
	
				} else {
					
				    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");											
				    addMensagem(MensagensMonitoria.PERIODO_SUBMISSAO_RESUMOS_DE_ACORDO_COM_O_ANO, 
					    calendario.getAnoProjetoResumoSid(),sdf.format(calendario.getInicioEnvioResumoSid()),
					    sdf.format(calendario.getFimEnvioResumoSid()));
				    return null;
				}
		    }

		}else{
		    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		    return null;
		}

	    } catch (DAOException e) {
		addMensagemErro(e.getMessage());
		return null;
	    }

	}
	
	/**
	 * 
	 * Atualiza o Resumo de seminário de iniciação à docência.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ResumoSid/resumos.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws SegurancaException {
	    checkDocenteRole();
	    try {
		CalendarioMonitoria calendario = getDAO(ProjetoMonitoriaDao.class).findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());
		
		if (ValidatorUtil.isEmpty(calendario)){
		    addMensagem(MensagensMonitoria.PERIODO_ENVIO_RESUMOS_NAO_DEFINIDO);
		    return null;				
		}

		//testa se esse projeto é do ano q tá autorizado! 
		if (calendario.getAnoProjetoResumoSid() != buscaProjetoEnsino.getAno()){
		    addMensagem(MensagensMonitoria.SUBMISSAO_RESUMO_FORA_DO_PRAZO);
		    return null;
		}else{		
		    //verifica se tá no período do recebimento(edição) dos resumos
		    if (calendario.isEditarResumoSid()){								
			return super.atualizar();								
		    }else{								
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");							
			addMensagem(MensagensMonitoria.PERIODO_ALTERAR_RESUMOS_DE_ACORDO_COM_O_ANO,
				calendario.getAnoProjetoResumoSid(),sdf.format(calendario.getFimEdicaoResumoSid()));
			return null;								
		    }
		}		

	    } catch (Exception e) {
		tratamentoErroPadrao(e);
	    }
	    return null;

	}

	/**
	 * Valida e persiste o resumo no banco.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Chamado pela jsp: sigaa.war/monitoria/ResumoSid/form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 *
	 */
	public String cadastrar() throws ArqException {
	    checkDocenteRole();
	    if (getConfirmButton().equalsIgnoreCase("remover")) {
		if (obj.getId() == 0) {
		    addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
		    return null;
		} else {
		    try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);					
			mov.setCodMovimento(SigaaListaComando.REMOVER_RESUMO_SID);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			resetBean();
		    } catch (Exception e) {
			tratamentoErroPadrao(e);
		    }
		    return forward(getListPage());
		}
	    } else {
		ListaMensagens lista = obj.validate();
		if (lista != null) {
		    erros.addAll(lista.getMensagens());
		}
		
		if (!hasErrors()) {
		    try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_RESUMO_SID);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		    } catch (Exception e) {
			tratamentoErroPadrao(e);
		    }
		    resetBean();
		    return forward(getListPage());
		}
		return null;
	    }
	}


	/**
	 * Lista todos os projetos do usuário logado que possuem resumos de seminário de iniciação à docência
	 * pendentes
	 * 
	 * Não foi encontrado jsp que chame o método.
	 *
	 * @return
	 */
	public Collection<ProjetoEnsino> getProjetosSemResumoUsuarioLogado() {
	    try {
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);			
		return dao.findSemResumoSidByServidor( getUsuarioLogado().getServidor().getId() );	

	    } catch(Exception e) {
		notifyError(e);
		return null;
	    }
	}


	@Override
	public String getDirBase() {
		return "/monitoria/ResumoSid";
	}


	/**
	 *  Busca a avaliação selecionada no banco e exibe no form
	 *  para o avaliador dá o parecer.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 *  <li>Chamado pela jsp: sigaa.war/monitoria/ResumoSid/lista_avaliar.jsp</li>
	 * </ul>
	 * @return
	 */
	public String selecionarAvaliacao() {
	    ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
	    try {
		int idAvaliacao = getParameterInt("id");
		avaliacaoMonitoria = dao.findByPrimaryKey(idAvaliacao, AvaliacaoMonitoria.class);			
		prepareMovimento(SigaaListaComando.AVALIAR_RESUMO_SID);
	    } catch(Exception e) {
		notifyError(e);
	    }
	    return forward(ConstantesNavegacaoMonitoria.CADASTRARRESUMO_FORM_AVALIAR);
	}
	
	/**
	 * 
	 * Não foi encontrada jsp que chame o método.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getStatusAvaliacao() {
	    List<StatusAvaliacao> listaStatus = new ArrayList<StatusAvaliacao>();
	    try {
		GenericDAO dao = getGenericDAO();
		listaStatus.add(dao.findByPrimaryKey(StatusAvaliacao.AVALIADO_SEM_RESSALVAS, StatusAvaliacao.class));
		listaStatus.add(dao.findByPrimaryKey(StatusAvaliacao.AVALIADO_COM_RESSALVAS, StatusAvaliacao.class));
	    } catch(Exception e) {
		tratamentoErroPadrao(e);
	    }
	    return toSelectItems(listaStatus, "id", "descricao");
	}


	/**
	 * Conclui a avaliação do resumo
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>jsp: sigaa.war/monitoria/ResumoSid/form_avaliar.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String avaliar() throws SegurancaException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA, SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA);
	    try {
		//avaliando definitivamente
		avaliacaoMonitoria.setDataAvaliacao(new Date());
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(avaliacaoMonitoria);
		mov.setCodMovimento(SigaaListaComando.AVALIAR_RESUMO_SID);
		execute(mov, getCurrentRequest());			
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return forward(ConstantesNavegacaoMonitoria.CADASTRARRESUMO_LISTA_AVALIAR);
	    } catch(Exception e) {
		addMensagemErro(e.getMessage());
		notifyError(e);
	    }
	    return null;
	}

	
	
	/**
	 * Grava a avaliação e permite concluir a avaliação em outro momento.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):]
	 * <ul>
	 *  <li>Chamado pela jsp: sigaa.war/monitoria/ResumoSid/form_avaliar.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String gravar() throws NegocioException, ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA, SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA);
	    avaliacaoMonitoria.setDataAvaliacao(null);
	    avaliacaoMonitoria.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIACAO_EM_ANDAMENTO));
	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setObjMovimentado(avaliacaoMonitoria);
	    mov.setCodMovimento(SigaaListaComando.AVALIAR_RESUMO_SID);
	    execute(mov, getCurrentRequest());
	    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);			
	    return forward(ConstantesNavegacaoMonitoria.CADASTRARRESUMO_LISTA_AVALIAR);
	}

	
	/**
	 * 
	 * Redireciona para página de consulta de freqüências do resumo de seminário de iniciação à docência.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/ResumoSid/registro_frequencia_discente_sid.jsp</li>
	 * 		<li>sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String consultarFrequenciaSID() throws SegurancaException {
	    urlAfterSearch = null; 
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);
	    return forward("/monitoria/ResumoSid/busca.jsf");
	}
	
	/**
	 * Realiza busca por resumos de projetos de acordo com os parâmetros passados
	 * usado na tela de busca...
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/monitoria/DistribuicaoResumoSid/lista.jsp</li>
	 * 	<li>sigaa.war/monitoria/ResumoSid/busca.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String buscar() {
	    if (resumos != null) {
		resumos.clear();
	    }

	    String tituloProj = null;
	    Integer ano = null;
	    Integer anoSid = null;
	    Integer idServidor = null;

	    if (checkBuscaProjeto) {
		tituloProj = getTituloProjeto();
	    }
	    if (checkBuscaAno) {
		ano = this.buscaAnoProjeto;
	    }
	    if (checkBuscaAnoSid) {
		anoSid = this.buscaAnoSid;
	    }
	    if (checkBuscaServidor) {
		idServidor = buscaServidor.getId();
	    }

	    /*
	     * se não  tiver critério, força a consulta pelo ano atual
	     */
	    if(!checkBuscaServidor && !checkBuscaProjeto && !checkBuscaAno && !checkBuscaAnoSid){ 
		checkBuscaAno = true;
		ano = CalendarUtils.getAnoAtual();
	    }

	    try {
		resumos = getDAO(ResumoSidDao.class).filter(tituloProj, ano, idServidor, true, anoSid);
	    } catch (DAOException e) {
		tratamentoErroPadrao(e);
	    } 

	    if (urlAfterSearch != null) {
		String temp = urlAfterSearch;
		//urlAfterSearch = null;
		return forward(temp);
	    }
	    return null;
	}

	/**
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * @return
	 */
	public String relatorioSubmissao() {
	    urlAfterSearch = "/monitoria/ResumoSid/relatorio_submissao.jsp";		
	    return forward("/monitoria/ResumoSid/busca.jsf");
	}
	
	public Integer getBuscaAnoProjeto() {
		return buscaAnoProjeto;
	}

	public void setBuscaAnoProjeto(Integer buscaAnoProjeto) {
		this.buscaAnoProjeto = buscaAnoProjeto;
	}

	public ProjetoEnsino getBuscaProjetoEnsino() {
		return buscaProjetoEnsino;
	}

	public void setBuscaProjetoEnsino(ProjetoEnsino buscaProjetoEnsino) {
		this.buscaProjetoEnsino = buscaProjetoEnsino;
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

	public boolean isCheckBuscaProjeto() {
		return checkBuscaProjeto;
	}

	public void setCheckBuscaProjeto(boolean checkBuscaProjeto) {
		this.checkBuscaProjeto = checkBuscaProjeto;
	}

	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}

	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}

	public Collection<ResumoSid> getResumos() {
		return resumos;
	}

	public void setResumos(Collection<ResumoSid> resumos) {
		this.resumos = resumos;
	}

	public AvaliacaoMonitoria getAvaliacaoMonitoria() {
		return avaliacaoMonitoria;
	}

	public void setAvaliacaoMonitoria(AvaliacaoMonitoria avaliacaoMonitoria) {
		this.avaliacaoMonitoria = avaliacaoMonitoria;
	}
	

	/**
	 * Método para emitir o certificado do SID
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li> sigaa.war/monitoria/ResumoSid/lista_participacoes.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws Exception
	 */
	public String emitirCertificado() throws Exception {
	    ParticipacaoSid participante = null;

	    try {
		participante = getGenericDAO().findByPrimaryKey(getParameterInt("id"), ParticipacaoSid.class);
		if(participante == null){
		    addMensagemErro("Certificado não encontrado.");
		    return null;
		}
		
		participante.getDiscenteMonitoria().getOrientacoes().iterator();

		if ((!participante.isApresentou()) || (!participante.isParticipou())){
		    addMensagem(MensagensMonitoria.DISCENTE_SEM_DIREITO_A_CERTIFICADO_DE_PARTICIPACAO_RESUMO_SID);
		    return null;
		}

		if (participante.getDiscenteMonitoria() == null){
		    addMensagem(MensagensMonitoria.NAO_HA_PARTICIPANTES_COM_DIREITO_CERTIFICADO_RESUMI_SID);
		    return null;
		}

		ArrayList<ParticipacaoSid> lista = new ArrayList<ParticipacaoSid>();
		lista.add(participante);

		HashMap<Object, Object> parametros = new HashMap<Object, Object>();
		parametros.put("ANO", CalendarUtils.getAnoAtual());
		parametros.put("DATA_CERTIFICADO", Formatador.getInstance().formatarDataDiaMesAno( new Date() ) );
		parametros.put("COORDENADOR_DIDATICO_PEDAGOGICO", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.NOME_COORDENADOR_DIDATICO_PEDAGOGICO));
		parametros.put("PRO_REITOR_GRADUACAO", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.NOME_PRO_REITOR_GRADUACAO));
		parametros.put("COORDENADOR_PROJETO",participante.getDiscenteMonitoria().getProjetoEnsino().getCoordenacao().getPessoa().getNome());
		parametros.put("DEPARTAMENTO",participante.getDiscenteMonitoria().getProjetoEnsino().getCoordenacao().getUnidade().getNome());

		// Gerar certificado
		JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("certificado_sid.jasper"), parametros, new JRBeanCollectionDataSource(lista));

		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=CERTIFICADO_SID_" +participante.getDiscenteMonitoria().getDiscente().getMatricula()+ ".pdf");
		JasperExportManager.exportReportToPdfStream(prt,getCurrentResponse().getOutputStream());
		FacesContext.getCurrentInstance().responseComplete();
		return null;
		
	    } catch (Exception e) {
		tratamentoErroPadrao(e);
		return null;
	    }

	}
	
	/**
	 * Método que retorna para a página de busca do relatório
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/monitoria/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String relatorioResumoSid(){
		return forward("/monitoria/ResumoSid/form_busca_relatorio_resumo_sid.jsf");
	}
	
	/**
	 * Método que retorna o relatório dos Resumos SID em formato PDF.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/monitoria/ResumoSid/form_busca_relatorio_resumo_sid.jsp</li>
	 * </ul>
	 * 
	 * @param ano
	 * @return
	 */
	public String emitirRelatorioResumoSid() {
		try{
			Connection con = null;
			if(buscaAnoSid == null){
				addMensagemErro("Ano: Campo obrigatório não informado.");
				return null;
			}
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("ano", buscaAnoSid);
			parametros.put("pro_reitoria", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.NOME_PRO_REITORIA_GRADUACAO));
			parametros.put("universidade", RepositorioDadosInstitucionais.getNomeInstituicao());
			parametros.put("pathSubReport", JasperReportsUtil.getReportSIGAA("resumos_sid_subreport.jasper"));
			
			// Gerar Relatorio
			con = Database.getInstance().getSigaaConnection();
			JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("resumos_sid.jasper"), parametros, con);
			
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=RELATORIO_RESUMO_SID_"+buscaAnoSid.toString()+".pdf");
			JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());
			FacesContext.getCurrentInstance().responseComplete();
			con.close();
			
			return null;
		}catch(Exception e){
			tratamentoErroPadrao(e);
			return null;
		}
		
	}
	
	/**
	 * Discente imprime o próprio certificado dos Resumos de seminários de iniciação à docência que o mesmo participou.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Chamado pela jsp: sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarParticipacoesDiscente() throws DAOException{
	    participacoes = getGenericDAO().findByExactField(ParticipacaoSid.class, "discenteMonitoria.discente.id", getUsuarioLogado().getDiscenteAtivo().getId());
	    return forward(ConstantesNavegacaoMonitoria.RESUMO_SID_LISTA_PARTICIPACOES);
	}

	public Collection<ParticipacaoSid> getParticipacoes() {
		return participacoes;
	}

	public void setParticipacoes(Collection<ParticipacaoSid> participacoes) {
		this.participacoes = participacoes; 
	}

	public String getUrlAfterSearch() {
		return urlAfterSearch;
	}

	public void setUrlAfterSearch(String urlAfterSearch) {
		this.urlAfterSearch = urlAfterSearch;
	}


	public boolean isCheckBuscaAnoSid() {
		return checkBuscaAnoSid;
	}


	public void setCheckBuscaAnoSid(boolean checkBuscaAnoSid) {
		this.checkBuscaAnoSid = checkBuscaAnoSid;
	}


	public Integer getBuscaAnoSid() {
		return buscaAnoSid;
	}


	public void setBuscaAnoSid(Integer buscaAnoSid) {
		this.buscaAnoSid = buscaAnoSid;
	}
	
	public String getTituloProjeto() {
		return tituloProjeto;
	}

	public void setTituloProjeto(String tituloProjeto) {
		this.tituloProjeto = tituloProjeto;
	}

	public int getQntMaximaResumoSid() {
		return qntMaximaResumoSid;
	}

	public void setQntMaximaResumoSid(int qntMaximaResumoSid) {
		this.qntMaximaResumoSid = qntMaximaResumoSid;
	}
}