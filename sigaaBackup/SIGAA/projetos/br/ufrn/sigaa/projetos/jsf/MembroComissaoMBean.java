/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.projetos.jsf; 

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoRelatorioProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoRelatorioProjeto;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoComissaoColegiado;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoMembroColegiado;
import br.ufrn.sigaa.prodocente.producao.jsf.ParticipacaoColegiadoComissaoMBean;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

/**
 * Controlador de cadastro de membros das diferentes comissões: pesquisa, monitoria e extensão.
 * 
 * @author Gleydson
 * @author Ilueny Santos
 * 
 */
@Component("membroComissao")
@Scope("session")
public class MembroComissaoMBean extends
		SigaaAbstractController<MembroComissao> {

	/**
	 * coleção do tipo AvaliacaoRelatorioProjeto
	 */
	private Collection<AvaliacaoRelatorioProjeto> avaliacoesRelatoriosMembroAtual;
	
	/**
	 * coleção do tipo AvaliacaoMonitoria
	 */
	private Collection<AvaliacaoMonitoria> avaliacoesResumoSidMembroAtual;
	
	/**
	 * Selectitem para combo tipo de comissão.
	 */
	private SelectItem[] tiposMembro = new SelectItem[]{
		new SelectItem(MembroComissao.MEMBRO_COMISSAO_PESQUISA,"COMISSÃO DE PESQUISA"),
		new SelectItem(MembroComissao.MEMBRO_COMISSAO_CIENTIFICA,"COMISSÃO CIENTÍFICA"),
		new SelectItem(MembroComissao.MEMBRO_COMISSAO_EXTENSAO,"COMISSÃO DE EXTENSÃO"),
		new SelectItem(MembroComissao.MEMBRO_COMISSAO_MONITORIA,"COMISSÃO DE MONITORIA"),
		new SelectItem(MembroComissao.MEMBRO_COMISSAO_INTEGRADA,"COMISSÃO INTEGRADA DE ENSINO, PESQUISA E EXTENSÃO")		
		};
	
	/**
	 * Coleção de membros de uma comissão.
	 */
	private Collection<MembroComissao> membros;
	
	/**
	 * Inteiro que identifica o tipo de avaliação.
	 */
	private Integer tipoAvaliacao;

	/**
	 * Inteiro auxiliar que identifica tipo do membro da comissão em algumas funcionalidades. 
	 */
	private Integer comissaoBusca = 0;
	
	/** Utilizado na view, durante a seleção de avaliações do membro da comissão*/
	private Integer idMembroSelecionado =  0;

	/**
	 * Coleção do tipo AvaliacaoMonitoria.
	 */
	private Collection<AvaliacaoMonitoria> avaliacoesDoMembroAtual;

	/**
	 * Atributo boolean que serve como flag para a decisão de exibição de próxima tela. 
	 */
	private boolean exibirProximaTela = false;
	

	/*
	 * Construtor
	 */
	public MembroComissaoMBean() {
		obj = new MembroComissao();
	}

	/**
	 * Inicializa os tipos de membro para que sejam mostrados na view.
	 */
	private void iniciarGerenciaMembroComissaoMonitoria() {
		tiposMembro = new SelectItem[2];
		tiposMembro[0] = new SelectItem(
				MembroComissao.MEMBRO_COMISSAO_MONITORIA,
				"COMISSÃO DE MONITORIA");
		tiposMembro[1] = new SelectItem(
				MembroComissao.MEMBRO_COMISSAO_CIENTIFICA,
				"COMISSÃO CIENTÍFICA");
		comissaoBusca = new Integer(MembroComissao.MEMBRO_COMISSAO_MONITORIA);
	}
	/**
	 * Inicializa os tipos de membro para que sejam mostrados na view.
	 */
	private void iniciarGerenciaMembroComissaoPesquisa() {
		tiposMembro = new SelectItem[1];
		tiposMembro[0] = new SelectItem(MembroComissao.MEMBRO_COMISSAO_PESQUISA, "COMISSÃO DE PESQUISA");
		comissaoBusca = new Integer(MembroComissao.MEMBRO_COMISSAO_PESQUISA);
	}
	/**
	 * Inicializa os tipos de membro para que sejam mostrados na view.
	 */
	private void iniciarGerenciaMembroComissaoExtensao() {
		tiposMembro = new SelectItem[1];
		tiposMembro[0] = new SelectItem(MembroComissao.MEMBRO_COMISSAO_EXTENSAO, "COMISSÃO DE EXTENSÃO");
		comissaoBusca = new Integer(MembroComissao.MEMBRO_COMISSAO_EXTENSAO);
	}

	/**
	 * Configura os comitês acessíveis na gerência de membros do CIEPE.
	 */
	private void iniciarGerenciaMembroCIEPE() {
		tiposMembro = new SelectItem[1];
		tiposMembro[0] = new SelectItem(MembroComissao.MEMBRO_COMISSAO_INTEGRADA, "COMISSÃO INTEGRADA DE ENSINO, PESQUISA E EXTENSÃO");
		comissaoBusca = new Integer(MembroComissao.MEMBRO_COMISSAO_INTEGRADA);
	}
	
	/**
	 * Redireciona para a página que permite consultar projetos de um avaliador.
	 * 
	 * Chamado por:
	 * sigaa.war/monitoria/index.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */ 
	public String iniciarConsultarProjetosAvaliador() throws SegurancaException {
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		iniciarGerenciaMembroComissaoMonitoria();
		return forward("/projetos/MembroComissao/projetos_avaliador.jsf");
	}
	
	/**
	 * Redireciona para a página que permite consultar relatórios
	 * de um avaliador.
	 * 
	 * Chamado por:
	 * sigaa.war/monitoria/index.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarConsultarRelatoriosAvaliador() throws SegurancaException {
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		iniciarGerenciaMembroComissaoMonitoria();
		return forward("/projetos/MembroComissao/relatorios_avaliador.jsf");
	}
	
	/**
	 * Redireciona para a página que permite consultar resumos
	 * de um avaliador.
	 * 
	 * Chamado por:
	 * sigaa.war/monitoria/index.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarConsultarResumoSidAvaliador() throws SegurancaException {
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		iniciarGerenciaMembroComissaoMonitoria();
		return forward("/projetos/MembroComissao/resumo_sid_avaliador.jsf");
	}
	
	/**
	 * Lista membros da comissão de monitoria.
	 * <br>
	 * Chamado por:
	 * sigaa.war/monitoria/index.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String listarMembroComissaoMonitoria() throws SegurancaException {
		if ( membros != null )
			membros.clear();
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		iniciarGerenciaMembroComissaoMonitoria();
		return forward(getListPage());
	}
	
	/**
	 * Lista membros da comissão de pesquisa.
	 * <br>
	 * Chamado por:
	 * sigaa.war/WEB-INF/jsp/pesquisa/menu/consultores.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listarMembroComissaoPesquisa() throws ArqException {
		if ( membros != null )
			membros.clear();
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		iniciarGerenciaMembroComissaoPesquisa();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getListPage());
	}
	
	/**
	 * Lista membros da comissão de extensão.
	 * <br>
	 * Chamado por:
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listarMembroComissaoExtensao() throws ArqException {
		if ( membros != null )
			membros.clear();
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		iniciarGerenciaMembroComissaoExtensao();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getListPage());
	} 
	
	/**
	 * Lista membros do CIEPE.
	 * <br>
	 * Chamado por:
	 * <ul><li>sigaa.war/extensao/menu.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/pesquisa/menu/consultores.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listarMembroCIEPE() throws ArqException {
		if ( membros != null )
			membros.clear();
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.GESTOR_MONITORIA);
		iniciarGerenciaMembroCIEPE();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getListPage());
	}
	
	/**
	 * Lista membros da comissão após o cadastro de um novo membro.
	 * <br>
	 * Não invocado por JSPs
	 * @return
	 * @throws ArqException
	 */
	public String listarMembrosComissaoAposCadastro() throws ArqException {
		
		String page = null;
		
		switch ( this.comissaoBusca ) {
			case MembroComissao.MEMBRO_COMISSAO_CIENTIFICA: 
			case MembroComissao.MEMBRO_COMISSAO_MONITORIA: 
				page = listarMembroComissaoMonitoria();
				break;
			case MembroComissao.MEMBRO_COMISSAO_PESQUISA: 
				page = listarMembroComissaoPesquisa();
				break;				
			case MembroComissao.MEMBRO_COMISSAO_EXTENSAO: 
				page = listarMembroComissaoExtensao();
				break;
			case MembroComissao.MEMBRO_COMISSAO_INTEGRADA: 
				page = listarMembroCIEPE();
		}
		
		buscar();
		return page;
	}
	
	/**
	 * Prepara o movimento de cadastrar membro
	 * da comissão de monitoria e redireciona para
	 * a página onde é possível o cadastro.
	 * <br>
	 * Chamado por:
	 * sigaa.war/monitoria/index.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrarMembroComissaoMonitoria() throws ArqException {
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		iniciarGerenciaMembroComissaoMonitoria();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	
	/**
	 * Prepara o movimento de cadastrar membro
	 * da comissão de pesquisa e redireciona para
	 * a página onde é possível o cadastro.
	 * <br>
	 * Chamado por:
	 * sigaa.war/WEB-INF/jsp/pesquisa/menu/consultores.jsp
	 *  
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrarMembroComissaoPesquisa() throws ArqException {
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		iniciarGerenciaMembroComissaoPesquisa();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	
	/**
	 * Prepara o movimento de cadastrar membro
	 * da comissão de extensão e redireciona para
	 * a página onde é possível o cadastro. 
	 * <br>
	 * Chamado por:
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrarMembroComissaoExtensao() throws ArqException {
		obj = new MembroComissao();
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		iniciarGerenciaMembroComissaoExtensao();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	/**
	 * Prepara o movimento de cadastrar membro do CIEPE e redireciona para
	 * a página onde é possível o cadastro.
	 * <br>
	 * Chamado por:<ul>
	 * <li>sigaa.war/extensao/menu.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/pesquisa/menu/consultores.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrarMembroCIEPE() throws ArqException {
		obj = new MembroComissao();
		checkRole( SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.GESTOR_MONITORIA);
		iniciarGerenciaMembroCIEPE();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
	
	@Override
	public String getDirBase() {
		return "/projetos/MembroComissao";
	}

	public SelectItem[] getTiposMembro() {
		return tiposMembro;
	}

	public void setTiposMembro(SelectItem[] tiposMembro) {
		this.tiposMembro = tiposMembro;
	}

	public Integer getComissaoBusca() {
		return comissaoBusca;
	}

	public void setComissaoBusca(Integer comissaoBusca) {
		this.comissaoBusca = comissaoBusca;
	}

	public Collection<MembroComissao> getMembros() {
		return membros;
	}

	public void setMembros(Collection<MembroComissao> membros) {
		this.membros = membros;
	}
	
	/**
	 * Ao cadastrar um MembroComissao caso o Servidor
	 * selecionado seja um Docente será redirecionado 
	 * para a tela de cadastro
	 * <br>
	 * Chamado por:
	 * sigaa.war/projetos/MembroComissao/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarMembro() throws ArqException, NegocioException {
			if(checkOperacaoAtiva(ArqListaComando.ALTERAR.getId())){
				
				if ( validarDados() ) {
						
						Servidor docente = getDAO(GenericDAOImpl.class).findByPrimaryKey(obj.getServidor().getId(), Servidor.class);
						boolean isDocente = docente.isDocente();
						
						if( "Cadastrar".equalsIgnoreCase(getConfirmButton()) && this.comissaoBusca == MembroComissao.MEMBRO_COMISSAO_EXTENSAO && getDAO(MembroComissaoDao.class).isMembroComissaoExtensao(docente) ){
							addMensagemErro(docente.getNome() + " já é membro da comissão.");
							return null;
						}
						if( "Cadastrar".equalsIgnoreCase(getConfirmButton()) && this.comissaoBusca == MembroComissao.MEMBRO_COMISSAO_INTEGRADA && getDAO(MembroComissaoDao.class).isMembroCadastrado(docente.getId(), MembroComissao.MEMBRO_COMISSAO_INTEGRADA ) ){
							addMensagemErro(docente.getNome() + " já é membro da comissão.");
							return null;
						}
						
						
						if ( !exibirProximaTela ) {
							cadastrar();
						}
						if ( exibirProximaTela && !isDocente ){
							addMensagemWarning(docente.getNome() + " não é docente, portanto não poderá participar de um colegiado.");
							cadastrar();
						}
						if ( exibirProximaTela && isDocente ) {
							ParticipacaoColegiadoComissaoMBean bean = getMBean("participacaoColegiadoComissao");
							
							//populando campos do formulário de cadastro do colegiado
							bean.getObj().setAnoReferencia(CalendarUtils.getAno(obj.getDataInicioMandato()));
							bean.getObj().setPeriodoInicio(obj.getDataInicioMandato());
							bean.getObj().setPeriodoFim(obj.getDataFimMandato());
							bean.getObj().setTipoComissaoColegiado(new TipoComissaoColegiado(TipoComissaoColegiado.COMISSAO_PERMANENTE));
							bean.getObj().setTipoMembroColegiado((TipoMembroColegiado) getGenericDAO().findByExactField(TipoMembroColegiado.class, "descricao", "Membro", true));
							bean.getObj().setInstituicao((InstituicoesEnsino) getGenericDAO().findByPrimaryKey(InstituicoesEnsino.UFRN, InstituicoesEnsino.class));
							
							return bean.entradaTelaMembroComisssao(docente, obj);
						}
						return listarMembrosComissaoAposCadastro();
				}
				return null;
			}
		
		return cancelar();
		
	}
	
	@Override
	public String getFormPage() {
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		return super.getFormPage();
	}
	
	@Override
	public String getListPage() {
		setOperacaoAtiva(ArqListaComando.REMOVER.getId());
		return super.getListPage();
	}
	
	/**
	 * Valida os dados do cadastro
	 * <br>
	 * Não invocado por JSPs
	 * @return
	 * @throws ArqException
	 */
	private boolean validarDados() {
		if ( !obj.validate().getMensagens().isEmpty() ) 
			addMensagens(obj.validate());

		if (obj.getServidor() != null && obj.getServidor().getId() > 0 && isEmpty(obj.getServidor().getNome()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Servidor");

		if (hasErrors())
			return false;
		
		return true;
	}
	

	/**
	 * Faz a busca de avaliações do membro atual.
	 * <br>
	 * Chamado por:<ul>
	 * <li>sigaa.war/extensao/AvaliadorAtividadeExtensao/projetos_avaliador.jsp</li>
	 * <li>sigaa.war/projetos/MembroComissao/lista.jsp</li>
	 * <li>sigaa.war/projetos/MembroComissao/projetos_avaliador.jsp</li>
	 * <li>sigaa.war/projetos/MembroComissao/relatorios_avaliador.jsp</li>
	 * </ul>
	 * @return
	 */
	public String buscar() {

	    	MembroComissaoDao dao = getDAO(MembroComissaoDao.class);
		try {
			membros = dao.findByComissao(comissaoBusca);
		} catch (DAOException e) {
			notifyError(e);
		}

		// limpa todas as avaliações do membro atual só preenche quando o
		// operador escolher outro
		if (avaliacoesDoMembroAtual != null)
			avaliacoesDoMembroAtual.clear();

		return null;
	}

	/**
	 * Lista de todos os membros localizados no combo
	 * <br>
	 * Chamado por:
	 * <ul>
	 * <li>sigaa.war/extensao/AvaliadorAtividadeExtensao/projetos_avaliador.jsp</li>
	 * <li>sigaa.war/projetos/MembroComissao/projetos_avaliador.jsp</li>
	 * <li>sigaa.war/projetos/MembroComissao/relatorios_avaliador.jsp</li>
	 * </ul>
	 * @return Coleção de SelectItem usado em selects html
	 */
	public Collection<SelectItem> getMembrosCombo() {
		List<MembroComissao> lista = new ArrayList<MembroComissao>();

		if (membros != null)
			lista = new ArrayList<MembroComissao>(membros);

		return toSelectItems(lista, "id", "unidadeServidor");
	}

	/**
	 * Muda o membro da Comissão do Mbean
	 * <br>
	 * Não é chamado por JSPs.
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void changeMembroComissao(ValueChangeEvent evt) throws DAOException {
	    Integer idMembro = 0;
	    try {
		if (evt.getNewValue() != null) { 
		    idMembro = new Integer(evt.getNewValue().toString());
		    if (idMembro != null && idMembro != 0) {
			// seleciona o membro da comissão de monitoria
			AvaliacaoMonitoriaDao daoAVA = getDAO(AvaliacaoMonitoriaDao.class);
			obj = daoAVA.findByPrimaryKey(idMembro, MembroComissao.class);
			// lista de todas as avaliações do membro da comissão
			avaliacoesDoMembroAtual = daoAVA.findByAvaliador(obj.getServidor().getId(), TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO);
		    }
		}
	    } catch (Exception e) {
		notifyError(e);
	    }
	}
	
	
	/**
	 * Trata evento de mudança de valor(Membro da Comissão).
	 * <br>
	 * Chamado por:
	 * sigaa.war/projetos/MembroComissao/relatorios_avaliador.jsp
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void changeMembroComissaoRelatorio(ValueChangeEvent evt) throws DAOException {
	    Integer idMembro = 0;
	    try {
		if (evt.getNewValue() != null) { 
		    idMembro = new Integer(evt.getNewValue().toString());
		    if (idMembro != null && idMembro != 0) {
			// seleciona o membro da comissão de monitoria
			AvaliacaoRelatorioProjetoDao daoREL = getDAO(AvaliacaoRelatorioProjetoDao.class);
			obj = daoREL.findByPrimaryKey(idMembro,	MembroComissao.class);
			// lista de todas as avaliações do membro da comissão
			avaliacoesRelatoriosMembroAtual = daoREL.findByAvaliador(obj.getServidor().getId());
		    }
		}
	    } catch (Exception e) {
		notifyError(e);
	    }
	}
	
	/**
	 * Na view, quando o usuário seleciona um novo membro da comissão, este método busca todas as avaliações do resumo SID
	 * do membro selecionado.
	 * <br>
	 * Chamado por:
	 * sigaa.war/projetos/MembroComissao/resumo_sid_avaliador.jsp
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void changeMembroComissaoResumoSid(ValueChangeEvent evt) throws DAOException {
	    Integer idMembro = 0;
	    try {
		if (evt.getNewValue() != null) { 
		    idMembro = new Integer(evt.getNewValue().toString());
		    if (idMembro != null && idMembro != 0) {
			// seleciona o membro da comissão de monitoria
			AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class);
			obj = dao.findByPrimaryKey(idMembro,	MembroComissao.class);
			// lista de todas as avaliações do membro da comissão
			avaliacoesResumoSidMembroAtual = dao.findByAvaliadorAvaliacaoResumiSid(obj.getId());
		    }
		}
	    } catch (Exception e) {
		notifyError(e);
	    }
	}
	
	/**
	 * Remove um membro da comissão.
	 * <br>
	 * Chamado por:
	 * sigaa.war/projetos/MembroComissao/lista.jsp
	 * 
	 * @return
	 */
	public String removeMembro() {
		if(checkOperacaoAtiva(ArqListaComando.REMOVER.getId())) {
			try {
				setId();
				prepareMovimento(ArqListaComando.ALTERAR);
				GenericDAO dao = getGenericDAO();
				this.obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
	
				// inclui data de desligamento do membro da monitoria
				obj.setDataDesligamento(new Date());
				obj.setAtivo(false);
	
				// verificando permissões
				if (obj.getPapel() == MembroComissao.MEMBRO_COMISSAO_PESQUISA)
					checkRole(SigaaPapeis.GESTOR_PESQUISA);
	
				if (obj.getPapel() == MembroComissao.MEMBRO_COMISSAO_MONITORIA)
					checkRole(SigaaPapeis.GESTOR_MONITORIA);
	
				if (obj.getPapel().equals(MembroComissao.MEMBRO_COMISSAO_EXTENSAO))
					checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	
				if (obj.getPapel().equals(MembroComissao.MEMBRO_COMISSAO_INTEGRADA))
					checkRole(SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.GESTOR_PESQUISA);
				
				ListaMensagens lista = new ListaMensagens(); 
				lista = obj.validate();
	
				if (lista.isEmpty()) {
	
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setObjMovimentado(obj);
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					try {
						execute(mov,
								(HttpServletRequest) FacesContext
										.getCurrentInstance().getExternalContext()
										.getRequest());
						addMessage("Operação realizada com sucesso!",
								TipoMensagemUFRN.INFORMATION);
	
						buscar();
						removeOperacaoAtiva();
						
					} catch (Exception e) {
						addMensagemErro("Erro Inesperado: " + e.getMessage());
						e.printStackTrace();
						
						return forward(getFormPage());
					}
				}

				return forward(getListPage());
				
			} catch (Exception e) {
				addMensagemErro(e.getMessage());
				notifyError(e);
			}
		}
		
		return redirect(getSubSistema().getLink());
	}

	/**
	 * Redireciona para uma página de listagem após o cadastro.
	 * <br>
	 * Não é chamado por JSPs.
	 * 
	 * @return
	 */
	@Override
	public String forwardCadastrar() {
		obj = new MembroComissao();
		buscar();
		return getDirBase() + "/lista.jsf";
	}

	/**
	 * Executa as ações necessárias antes da validação e do cadastro.
	 * <br>
	 * Não é chamado por JSPs.
	 * 
	 */
	@Override
	public void beforeCadastrarAndValidate() {

		try {
			// verificando permissões
			if (obj.getPapel() == MembroComissao.MEMBRO_COMISSAO_PESQUISA)
				checkRole(SigaaPapeis.GESTOR_PESQUISA);

			if (obj.getPapel() == MembroComissao.MEMBRO_COMISSAO_MONITORIA)
				checkRole(SigaaPapeis.GESTOR_MONITORIA);

			if (obj.getPapel().equals(MembroComissao.MEMBRO_COMISSAO_EXTENSAO))
				checkRole(SigaaPapeis.GESTOR_EXTENSAO);

			if (!"Alterar".equalsIgnoreCase(getConfirmButton())) {
				MembroComissaoDao dao = getDAO(MembroComissaoDao.class);
				if (dao.isMembroCadastrado(obj.getServidor().getId(), obj.getPapel())) {
					addMensagemErro("Servidor(a) já cadastrado(a) e ativo para a comissão informada.");
					exibirProximaTela = false;
				}

				obj.setDataCadastro(new Date());
				obj.setAtivo(true);
				
			}

			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			
		} catch (DAOException e) {
			notifyError(e);
		} catch (SegurancaException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

	}

	/**
	 * Atualiza um membro da comissão acadêmica.
	 * <br>
	 * Chamado por:
	 * sigaa.war/projetos/MembroComissao/lista.jsp
	 * 
	 */
	@Override
	public String atualizar() throws ArqException {
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			Integer id = getParameterInt("id");			
			MembroComissao membro = dao.findByPrimaryKey(id, MembroComissao.class);
			
			if ( !membro.isAtivo() ) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return cancelar();
			} else
				return super.atualizar();
			
			
		} finally {
			if ( dao != null )
				dao.close();
		}		
	}
	
	/**
	 * Retorna membros de monitoria.
	 * <br>
	 * Não é chamado por JSPs.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getMembrosMonitoria() {

	    	MembroComissaoDao dao = getDAO(MembroComissaoDao.class);
		try {
			return toSelectItems(dao.findByComissao(new Integer(
					MembroComissao.MEMBRO_COMISSAO_MONITORIA)), "id",
					"servidor.pessoa.nome");
		} catch (DAOException e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		}

	}
	

	public Collection<AvaliacaoMonitoria> getAvaliacoesDoMembroAtual() {
		return avaliacoesDoMembroAtual;
	}

	public void setAvaliacoesDoMembroAtual(
			Collection<AvaliacaoMonitoria> avaliacoesDoMembroAtual) {
		this.avaliacoesDoMembroAtual = avaliacoesDoMembroAtual;
	}

	public Integer getIdMembroSelecionado() {
		return idMembroSelecionado;
	}

	public void setIdMembroSelecionado(Integer idMembroSelecionado) {
		this.idMembroSelecionado = idMembroSelecionado;
	}
	
	public boolean isExibirProximaTela() {
		return exibirProximaTela;
	}

	public void setExibirProximaTela(boolean exibirProximaTela) {
		this.exibirProximaTela = exibirProximaTela;
	}

	public Integer getTipoAvaliacao() {
		return tipoAvaliacao;
	}

	public void setTipoAvaliacao(Integer tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}

	public Collection<AvaliacaoRelatorioProjeto> getAvaliacoesRelatoriosMembroAtual() {
		return avaliacoesRelatoriosMembroAtual;
	}

	public void setAvaliacoesRelatoriosMembroAtual(
			Collection<AvaliacaoRelatorioProjeto> relatoriosDoMembroAtual) {
		this.avaliacoesRelatoriosMembroAtual = relatoriosDoMembroAtual;
	}
	
	
	public Collection<AvaliacaoMonitoria> getAvaliacoesResumoSidMembroAtual() {
		return avaliacoesResumoSidMembroAtual;
	}

	public void setAvaliacoesResumoSidMembroAtual(
			Collection<AvaliacaoMonitoria> avaliacoesResumoSidMembroAtual) {
		this.avaliacoesResumoSidMembroAtual = avaliacoesResumoSidMembroAtual;
	}

	@Override
	protected void afterCadastrar() throws ArqException {
	}
	
}
