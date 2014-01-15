/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/08/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.ServidorDAO;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.InvencaoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.mensagens.MensagensPesquisa;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.pesquisa.dominio.ArquivoInvencao;
import br.ufrn.sigaa.pesquisa.dominio.FinanciamentoInvencao;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.Invencao;
import br.ufrn.sigaa.pesquisa.dominio.Inventor;
import br.ufrn.sigaa.pesquisa.dominio.ParecerInvencao;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoInvencao;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusNotificacaoInvencao;
import br.ufrn.sigaa.pesquisa.dominio.VinculoInvencao;
import br.ufrn.sigaa.pesquisa.negocio.InvencaoValidator;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoPesquisaValidator;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Controlador para efetuar o registro de notificações de invenção.
 * 
 * @author Leonardo Campos
 * 
 */
@Component("invencao")
@Scope("session")
public class InvencaoMBean extends SigaaAbstractController<Invencao> {

	@Autowired
	private TokenGenerator generator;
	
	/** Constantes das Views */
	public final String JSP_DADOS_GERAIS = "/pesquisa/invencao/dados_gerais.jsp";
	public final String JSP_REVELACOES = "/pesquisa/invencao/revelacoes.jsp";
	public final String JSP_PROPRIEDADES_TERCEIROS = "/pesquisa/invencao/propriedade_terceiros.jsp";
	public final String JSP_FINANCIAMENTOS = "/pesquisa/invencao/financiamentos.jsp";
	public final String JSP_INVENTORES = "/pesquisa/invencao/inventores.jsp";
	public final String JSP_RESUMO = "/pesquisa/invencao/resumo.jsp";
	public final String JSP_LISTA_INVENCOES_PENDENTES = "/pesquisa/invencao/lista_invencoes_pendentes.jsp";
	public final String JSP_REMOCAO = "/pesquisa/invencao/remocao.jsp";
	public final String JSP_LISTA = "/pesquisa/invencao/lista.jsp";
	public final String JSP_COMPROVANTE = "/pesquisa/invencao/comprovante.jsp";
	public final String JSP_VIEW = "/pesquisa/invencao/view.jsp";
	
	private boolean buscaUsuario;
	private boolean buscaSituacao;
	private boolean buscaData;
	private String loginUsuario;

	private ArquivoInvencao arquivo;
	private UploadedFile file;

	private FinanciamentoInvencao financiamento;

	private VinculoInvencao vinculo;

	private String codigoProjetoPesquisa;

	private Inventor inventor = new Inventor();
	
	private Servidor docente = new Servidor();
	private Discente discente = new Discente();
	private Servidor servidor = new Servidor();
	private DocenteExterno docenteExterno = new DocenteExterno();

	private List<MensagemAviso> avisosAjax = new ArrayList<MensagemAviso>();
	private String cpf = new String("");

	private Collection<Invencao> invencoesGravadas;
	
	private AreaConhecimentoCnpq grandeArea;

	private AreaConhecimentoCnpq area;

	private AreaConhecimentoCnpq subarea;

	private AreaConhecimentoCnpq especialidade;
	
	private Collection<SelectItem> areas = new ArrayList<SelectItem>();
	private Collection<SelectItem> subareas = new ArrayList<SelectItem>();
	private Collection<SelectItem> especialidades = new ArrayList<SelectItem>();

	/**
	 * Retorna uma coleção de itens dos Centros
	 * <br><br>
	 * JSP: sigaa.war/pesquisa/invencao/dados_gerais.jsp
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getCentrosCombo() throws ArqException {
		return toSelectItems(getGenericDAO().findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc"), "unidade.id", "unidade.codigoNome");
	}
	
	/**
	 * Retorna uma coleção de itens de tipos de situação de uma Notificação de Invenção
	 * <br><br>
	 * JSP: sigaa.war/pesquisa/invencao/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getSituacaoCombo() throws DAOException, ArqException{
		return toSelectItems(TipoStatusNotificacaoInvencao.getTipos());
	}
	
	public InvencaoMBean() {
		clear();
	}

	/**
	 * Inicializa os objetos
	 */
	private void clear() {
		obj = new Invencao();
		obj.setCodigo(null);
		obj.setCentro(new Unidade());
		obj.setTipo(new TipoInvencao());
		arquivo = new ArquivoInvencao();
		financiamento = new FinanciamentoInvencao();

		vinculo = new VinculoInvencao();
		vinculo.setProjetoPesquisa(new ProjetoPesquisa());
		vinculo.setGrupoPesquisa(new GrupoPesquisa());

		inventor.setDiscente(new Discente());
		inventor.setDocente(new Servidor());
		inventor.setServidor(new Servidor());
		inventor.setDocenteExterno(new DocenteExterno());
		inventor.setPessoa(new Pessoa());
		
		docente.getPessoa().setPais(new Pais());
		discente.getPessoa().setPais(new Pais());
		servidor.getPessoa().setPais(new Pais());
		inventor.getPessoa().setPais(new Pais());
		
		grandeArea = new AreaConhecimentoCnpq();
		area = new AreaConhecimentoCnpq();
		subarea = new AreaConhecimentoCnpq();
		especialidade = new AreaConhecimentoCnpq();
	}

	/**
	 * Tela de listar
	 * <br><br>
	 * JSP: sigaa.war/WEB-INF/jsp/pesquisa/menu/inovacao_empreendedorismo.jsp
	 */
	public String listar() throws DAOException {
		setResultadosBusca(getGenericDAO().findAll(Invencao.class));
		
		return forward(JSP_LISTA);
	}
	
	/**
	 * Método que verifica se já existem invenções com cadastro em andamento pelo usuário
	 * e redireciona para a tela de continuação de cadastro, ou inicia um novo cadastro,
	 * caso contrário.
	 * <br><br>
	 * JSP: sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		if(getSubSistema().equals(SigaaSubsistemas.PORTAL_DOCENTE))
			checkDocenteRole();
	
		invencoesGravadas = getDAO(InvencaoDao.class).findByUsuarioCadastro(getUsuarioLogado().getId());
		
		if(invencoesGravadas != null && !invencoesGravadas.isEmpty()){
			return forward(JSP_LISTA_INVENCOES_PENDENTES);
		}else{
			return iniciarNova();
		}
	}

	/**
	 * Método que inicia um novo cadastro de invenção.
	 * <br><br>
	 * JSP: sigaa.war/pesquisa/invencao/lista_invencoes_pendentes.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarNova() throws ArqException {
		clear();
		setOperacaoAtiva(SigaaListaComando.GRAVAR_INVENCAO.getId());
		prepareMovimento(SigaaListaComando.GRAVAR_INVENCAO);
		return telaDadosGerais();
	}

	/**
	 * Método que carrega uma invenção e redireciona para a tela do início do cadastro
	 * <br><br>
	 * JSP: <ul><li>sigaa.war/pesquisa/invencao/lista_invencoes_pendentes.jsp</li>
	 * 			<li>sigaa.war/pesquisa/invencao/lista.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String continuarCadastro() throws ArqException {
		clear();
		setReadOnly(false);
		int id = getParameterInt("idInvencao");
		obj = getGenericDAO().findByPrimaryKey(id, Invencao.class);
		obj.getArquivos().iterator();
		obj.getFinanciamentos().iterator();
		obj.getVinculos().iterator();
		obj.getInventores().iterator();
		if(obj.getCentro() == null)
			obj.setCentro(new Unidade());
		
		GenericDAO dao = getGenericDAO();
		
		// Setando as áreas nos selects
		if (obj.getAreaConhecimentoCnpq() != null) {
			obj.setAreaConhecimentoCnpq( dao.findByPrimaryKey( obj.getAreaConhecimentoCnpq().getId(), AreaConhecimentoCnpq.class) );
			grandeArea = obj.getAreaConhecimentoCnpq().getGrandeArea();
			carregaAreas();
		}

		AreaConhecimentoCnpq a = obj.getAreaConhecimentoCnpq();
		if (a != null) {
			area = a.getArea();
			carregaSubAreas();
			if( a.getSubarea() != null ){
				subarea = obj.getAreaConhecimentoCnpq().getSubarea();
				carregaEspecialidades();
			}
			if( a.getEspecialidade() != null ){
				especialidade = obj.getAreaConhecimentoCnpq().getEspecialidade();
			}
		}
		
		setOperacaoAtiva(SigaaListaComando.GRAVAR_INVENCAO.getId());
		prepareMovimento(SigaaListaComando.GRAVAR_INVENCAO);
		return telaDadosGerais();
	}
	
	/**
	 * Prepara tela de remoção
	 * <br><br>
	 * JSP: <ul><li>sigaa.war/pesquisa/invencao/lista_invencoes_pendentes.jsp</li>
	 *      	<li>sigaa.war/pesquisa/invencao/lista.jsp</li></ul>
	 */
	@Override
	public String preRemover() {
		try {
			prepareMovimento(SigaaListaComando.REMOVER_INVENCAO);

			GenericDAO dao = getGenericDAO();
			int id = getParameterInt("idInvencao");
			obj.setId(id);
			PersistDB obj = this.obj;

			this.obj = (Invencao) dao.findByPrimaryKey(obj.getId(),	obj.getClass());
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		return forward(JSP_REMOCAO);
	}

	/**
	 * Remove a invenção do sistema para o usuário
	 * Status da invenção como removida.
	 * Ativo = false
	 * <br><br>
	 * JSP: sigaa.war/pesquisa/invencao/remocao.jsp
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#remover()
	 */
	@Override
	public String remover() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		if (obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			mov.setCodMovimento(getUltimoComando());
			try {
				removeParecer();
				execute(mov, (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Notificação de Invenção");
			} catch (Exception e) {
				return tratamentoErroPadrao(e);
			}

			// funcionário do NIT removendo invenção a partir da tela de gerenciamento...
			if(getUsuarioLogado().isUserInRole(SigaaPapeis.NIT) && getSubSistema().equals(SigaaSubsistemas.PESQUISA))
				return listar();
			else
				// usuário que cadastrou a invenção removendo...
				return getSubSistema().getForward();
		}
	}
	
	/**
	 * Exibe um resumo da invenção
	 * <br><br>
	 * JSP: /sigaa.war/pesquisa/invencao/lista_invencoes_pendentes.jsp
	 * 		/sigaa.war/pesquisa/invencao/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String view() throws ArqException {
		boolean view = getParameterBoolean("idView");
		setReadOnly(true);
		try {
			GenericDAO dao = getGenericDAO();
			int id = getParameterInt("idInvencao");
			obj.setId(id);
			PersistDB obj = this.obj;

			this.obj = (Invencao) dao.findByPrimaryKey(obj.getId(),	obj.getClass());
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
		if(view)
			return forward(JSP_VIEW);
		else
			return forward(JSP_RESUMO);
	}

	/**
	 * Visualiza o arquivo anexo a invenção
	 * <br><br>
	 * JSP: <ul><li>/sigaa.war/pesquisa/invencao/include/dados_invencao.jsp</li>
	 *      	<li>/sigaa.war/pesquisa/invencao/parecer.jsp</li></ul>
	 * 
	 * @param event
	 */
	public void viewArquivo() {
		try {
			int idArquivo = getParameterInt("idArquivo");
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo,
					false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(" Arquivo não encontrado!");
		}
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	/**
	 * Método para anexar um arquivo à invenção.
	 * <br><br>
	 * JSP: <ul><li>sigaa.war/pesquisa/invencao/dados_gerais.jsp</li>
	 *      	<li>sigaa.war/pesquisa/invencao/revelacoes.jsp</li></ul>
	 * 
	 * @param evt
	 */
	public String anexarArquivo() {

		arquivo.setCategoria( Integer.parseInt(getParameter("categoriaArquivo")) );
		
		try {

			if ((arquivo.getDescricao() == null)
					|| ("".equals(arquivo.getDescricao().trim()))) {
				addMensagemErro("Informe uma descrição para a revelação.");
				return null;
			}

			obj.addArquivo(arquivo);

			// grava no banco...
			gravar();

			addMessage("Revelação adicionada com sucesso!", TipoMensagemUFRN.INFORMATION);
			arquivo = new ArquivoInvencao();

		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		return null;
	}

	/**
	 * Método para remover um anexo da invenção
	 * <br><br>
	 * JSP: <ul><li>sigaa.war/pesquisa/invencao/dados_gerais.jsp</li>
	 *      	<li>sigaa.war/pesquisa/invencao/revelacoes.jsp</li></ul>
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public String removeAnexo() throws ArqException {
		ArquivoInvencao arquivo = new ArquivoInvencao();

		arquivo.setIdArquivo(Integer.parseInt(getParameter("idArquivo")));
		arquivo.setId(Integer.parseInt(getParameter("idArquivoInvencao")));

		// remove do banco de pesquisa
		remover(arquivo);

		// remove do banco de arquivos
		EnvioArquivoHelper.removeArquivo(arquivo.getIdArquivo());

		// remove da view
		obj.getArquivos().remove(arquivo);

		return null;
	}

	/**
	 * Método utilitário, remove objetos diretamente do banco de dados
	 * <br><br>
	 * JSP: sigaa.war/pesquisa/invencao/remocao.jsp
	 * 
	 * @param persistDB
	 * @return
	 * @throws ArqException
	 */
	public void remover(PersistDB persistDB) {

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(persistDB);

		if (persistDB == null || persistDB.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return;
		}

		Comando ultimoComando = getUltimoComando();

		try {
			prepareMovimento(ArqListaComando.REMOVER);
			mov.setCodMovimento(ArqListaComando.REMOVER);

			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Invenção");
			prepareMovimento(ultimoComando);

		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}

		return;

	}
	
	/**
	 * Adiciona financiamentos obtidos pelo projeto
	 * <br><br>
	 * JSP: sigaa.war/pesquisa/invencao/financiamentos.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String adicionarFinanciamento() throws DAOException {
		try {
			// validação...
			ListaMensagens lista = new ListaMensagens();
			InvencaoValidator.validaAdicionaFinanciamento(financiamento, lista);
			if (!lista.isEmpty()) {
				addMensagens(lista);
				return null;
			}
			
			financiamento.setEntidadeFinanciadora( getGenericDAO().findByPrimaryKey(financiamento.getEntidadeFinanciadora().getId(), EntidadeFinanciadora.class) );
			obj.addFinanciamento(financiamento);
			
			// grava no banco...
			gravar();
			
			addMensagemInformation("Financiamento adicionado com sucesso.");
			financiamento = new FinanciamentoInvencao();
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		} 
		return null;
	}
	
	/**
	 * Remove financiamento
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>sigaa.war/pesquisa/invencao/financiamentos.jsp</li>
	 *      </ul>
	 * @return
	 * @throws ArqException
	 */
	public String removerFinanciamento() throws ArqException {
		
		FinanciamentoInvencao fin = new FinanciamentoInvencao();
		fin.setId(Integer.parseInt(getParameter("idFinanciamentoInvencao")));
		fin.setEntidadeFinanciadora(null);
		
		// remove do banco de pesquisa
		remover(fin);
		
		// remove da view
		obj.getFinanciamentos().remove(fin);
		
		return null;
	}

	/**
	 * Serve para modificar a Grande Área
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/invencao/dados_gerais.jsp</li>
	 *      </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void changeGrandeArea(ValueChangeEvent evt) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
			AreaConhecimentoCnpq grandeArea = new AreaConhecimentoCnpq( (Integer) evt.getNewValue()) ;
			areas = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
		}
	}
	
	/**
	 * Atribui a variável "areas" uma coleção de itens das Áreas
	 * <br><br>
	 * JSP: Não invocado por JSP.
	 * @throws DAOException
	 */
	private void carregaAreas() throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		areas = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
	}

	/**
	 * Serve para modificar uma área
   	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/invencao/dados_gerais.jsp</li>
	 *      </ul>
	 */
	public void changeArea(ValueChangeEvent evt) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
			Integer codigo = (Integer) evt.getNewValue() ;
			subareas = toSelectItems(dao.findSubAreas(codigo), "id", "nome");
		}
	}
	
	/**
	 * Atribui a variável "subareas" uma coleção de itens das Sub-áreas
	 * <br><br>
	 * JSP: Não invocado por JSP.
	 * @throws DAOException
	 */
	private void carregaSubAreas() throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		subareas = toSelectItems(dao.findSubAreas(area.getId()), "id", "nome");
	}
	
	/**
	 * Serve para modificar uma Sub-área.
   	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/invencao/dados_gerais.jsp</li>
	 *      </ul>
	 */
	public void changeSubArea(ValueChangeEvent evt) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
			AreaConhecimentoCnpq subArea = new AreaConhecimentoCnpq( (Integer) evt.getNewValue()) ;
			especialidades = toSelectItems(dao.findEspecialidade(subArea), "id", "nome");
		}
	}
	
	/**
	 * Atribui a variável "especialidades" uma coleção de itens das
	 * Especialidades de uma Sub-Área do CNPq (Conselho Nacional de Desenvolvimento Científico e Tecnológico)
	 * <br><br>
	 * JSP: Não invocado por JSP.
	 * @throws DAOException
	 */
	private void carregaEspecialidades() throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		especialidades = toSelectItems(dao.findEspecialidade(subarea), "id", "nome");
	}
	
	/**
	 * Valida os dados submetidos no formulário de dados gerais 
	 * e encaminha para a página com o formulário de revelações
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/sigaa.war/pesquisa/invencao/dados_gerais.jsp</li>
	 *      </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterDadosGerais() throws SegurancaException,
			ArqException, NegocioException {
		
		GenericDAO dao = getGenericDAO();
		
		// Setando a última área de conhecimento selecionada na view
		if( especialidade.getId() != 0 ){
		    obj.setAreaConhecimentoCnpq( dao.findByPrimaryKey( especialidade.getId(), AreaConhecimentoCnpq.class ) );
		} else if ( subarea.getId() != 0 ){
		    obj.setAreaConhecimentoCnpq( dao.findByPrimaryKey( subarea.getId(), AreaConhecimentoCnpq.class ) );
		} else if ( area.getId() != 0 ){
		    obj.setAreaConhecimentoCnpq( dao.findByPrimaryKey( area.getId(), AreaConhecimentoCnpq.class ) );
		    if(obj.getAreaConhecimentoCnpq().getGrandeArea().getId() != grandeArea.getId())
		        obj.setAreaConhecimentoCnpq( null );
		} else {
		    obj.setAreaConhecimentoCnpq( null );
		}
		
		ListaMensagens lista = new ListaMensagens();
		InvencaoValidator.validaDadosGerais(obj, lista);
		if(!lista.isEmpty()){
			addMensagens(lista);
			return telaDadosGerais();
		}
		gravar();
		return telaRevelacoes();
	}

	/**
	 * Grava a invenção
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>Não Invocado por JSP.</li>
	 *      </ul>
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void gravar() throws SegurancaException, ArqException,
			NegocioException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		obj.setStatus(TipoStatusNotificacaoInvencao.GRAVADA);

		mov.setCodMovimento(SigaaListaComando.GRAVAR_INVENCAO);

		execute(mov, getCurrentRequest());
		prepareMovimento(SigaaListaComando.GRAVAR_INVENCAO);
	}

	/**
	 * Submete os dados da revelação
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>sigaa.war/pesquisa/invencao/revelacoes.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String submeterRevelacoes() {
		return telaPropriedadeTerceiros();
	}
	
	/**
	 * Submete os dados que declarou como sendo propriedade de terceiros
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>sigaa.war/pesquisa/invencao/propriedade_terceiros.jsp</li>
	 *      </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterPropriedadesTerceiros() throws SegurancaException, ArqException, NegocioException{
		gravar();
		return telaFinanciamentos();
	}
	
	/**
	 * Submete os financiamentos que declarou
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>sigaa.war/pesquisa/invencao/financiamentos.jsp</li>
	 *      </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterFinanciamentos() throws SegurancaException, ArqException, NegocioException{
		docente = new Servidor();
		discente = new Discente();
		servidor = new Servidor();
		docenteExterno = new DocenteExterno();
		gravar();
		return telaInventores();
	}
	
	/**
	 * Valida os critérios de busca informados e consulta por invenções.
	 * <br /><br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: sigaa.war/pesquisa/invensao/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		if(!buscaUsuario && !buscaSituacao && !buscaData){
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			listar();
			return forward(JSP_LISTA);
		}
		
		String login = null;
		Integer situacao = null;
		Date data = null;
		
		if(buscaUsuario){
			ValidatorUtil.validateRequired(loginUsuario, "Usuario", erros);
			login = loginUsuario;
		}
		if(buscaSituacao){
			ValidatorUtil.validateRequiredId(obj.getStatus(), "Situação", erros);
			situacao = obj.getStatus();
		}
		if(buscaData){
			ValidatorUtil.validateRequired(obj.getCriadoEm(), "Data", erros);
			data = obj.getCriadoEm();
		}
		
		if (hasErrors())
			return null;
	
		resultadosBusca = getDAO(InvencaoDao.class).findByStatusLoginData(situacao, login, data);
		
		if(resultadosBusca == null || resultadosBusca.isEmpty()){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(JSP_LISTA);
	}
	
	/**
	 * Informou o cpf da pessoa externa
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/sigaa.war/pesquisa/invencao/inventores.jsp</li>
	 *      </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void buscarParticipanteExternoByCPF() throws DAOException {

		PessoaDao pessoaDao = getDAO(PessoaDao.class);

		try {

			// limpa os dados
			docente = new Servidor();
			discente = new Discente();
			servidor = new Servidor();

			inventor = new Inventor();
			inventor.setInvencao(this.obj);
			inventor.setDiscente(new Discente());
			inventor.setDocente(new Servidor());
			inventor.setServidor(new Servidor());
			inventor.setDocenteExterno(new DocenteExterno());

			if (docenteExterno == null)
				docenteExterno = new DocenteExterno();			
			
			//pessoa internacional
			if (docenteExterno.getPessoa().isInternacional()){
				docenteExterno.setPessoa(new Pessoa());
				inventor.setDocenteExterno(docenteExterno);
				// permite a edição do nome da pessoa pelo usuário
				inventor.setSelecionado(true);
				cpf = "";
				
			}else{
				// permite a edição do nome da pessoa pelo usuário
				inventor.setSelecionado(false);
			}
			
			if ((cpf != null) && (!cpf.trim().equals(""))) {
				avisosAjax.clear();

				if (!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf)) {
					getAvisosAjax().add(
							new MensagemAviso("CPF inválido!",
									TipoMensagemUFRN.ERROR));

				} else {

					Pessoa p = pessoaDao.findByCpf(Long.parseLong(cpf));
					docenteExterno.setPessoa(p); // seta a pessoa
					// encontrada

					if (docenteExterno.getPessoa() == null || docenteExterno.getPessoa().getPais() == null) {
						p = new Pessoa();
						p.setCpf_cnpjString(cpf);
						docenteExterno.setPessoa(p);
						inventor.setDocenteExterno(docenteExterno);
						// permite a edição do nome da pessoa pelo usuário
						inventor.setSelecionado(true);
					} else {
						// não permite inclusão do nome da pessoa
						inventor.setSelecionado(false);
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
		} finally {
			pessoaDao.close();
		}

		getCurrentSession().setAttribute("aba", "autor-externo");
		getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.EXTERNO);

	}
	
	public boolean isBuscaUsuario() {
		return buscaUsuario;
	}

	public void setBuscaUsuario(boolean buscaUsuario) {
		this.buscaUsuario = buscaUsuario;
	}

	public boolean isBuscaSituacao() {
		return buscaSituacao;
	}

	public void setBuscaSituacao(boolean buscaSituacao) {
		this.buscaSituacao = buscaSituacao;
	}

	public boolean isBuscaData() {
		return buscaData;
	}

	public void setBuscaData(boolean buscaData) {
		this.buscaData = buscaData;
	}
	
	/**
	 * Busca dados do Docente
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>sigaa.war/pesquisa/invencao/inventores.jsp</li>
	 *      </ul>
	 * @return
	 * @throws DAOException 
	 */
	public void buscarDadosDocente()throws DAOException{
		if (docente.getId() != 0) {
			docente = getGenericDAO().findByPrimaryKey(docente.getId(), Servidor.class);
			if (docente.getPessoa().getPais() == null){
				docente.getPessoa().setPais(new Pais());
			}
			if (docente.getPessoa().getPais().getId() > 0){
				docente.getPessoa().setPais(getGenericDAO().findByPrimaryKey(docente.getPessoa().getPais().getId(), Pais.class));
			}
			if(docente.getPessoa().getEnderecoContato() == null){
				docente.getPessoa().setEnderecoContato(new Endereco());
			}
			if (docente.getPessoa().getEnderecoContato().getId() > 0){
				docente.getPessoa().setEnderecoContato(getGenericDAO().findByPrimaryKey(docente.getPessoa().getEnderecoContato().getId(), Endereco.class));
			}
			if (docente.getPessoa().getNomeMae() == null){
				docente.getPessoa().setNomeMae("Não informado");
			}
		}
	}
	
	/**
	 * Busca dados do Discente
	 * <br>
	 * JSP: <ul>
	 * 		  <li>sigaa.war/pesquisa/invencao/inventores.jsp</li>
	 *      </ul>
	 * @return
	 * @throws DAOException 
	 */
	public void buscaDadosDiscente() throws DAOException{
		if (discente.getId() != 0) {
			discente = getGenericDAO().findByPrimaryKey(discente.getId(), Discente.class);
			if (discente.getPessoa().getPais() == null){
				discente.getPessoa().setPais(new Pais());
			}
			if (discente.getPessoa().getPais().getId() > 0){
				discente.getPessoa().setPais(getGenericDAO().findByPrimaryKey(discente.getPessoa().getPais().getId(), Pais.class));
			}
			if(discente.getPessoa().getEnderecoContato() == null){
				discente.getPessoa().setEnderecoContato(new Endereco());
			}
			if (discente.getPessoa().getEnderecoContato().getId() > 0){
				discente.getPessoa().setEnderecoContato(getGenericDAO().findByPrimaryKey(discente.getPessoa().getEnderecoContato().getId(), Endereco.class));
			}
			if (discente.getPessoa().getContaBancaria() == null){
				discente.getPessoa().setContaBancaria(new ContaBancaria());
			}
			if (discente.getPessoa().getContaBancaria().getId() > 0){
				discente.getPessoa().setContaBancaria(getGenericDAO().findByPrimaryKey(discente.getPessoa().getContaBancaria().getId(), ContaBancaria.class));
			}
			if (discente.getPessoa().getNomeMae() == null){
				discente.getPessoa().setNomeMae("Não informado");
			}
		}
	}
	
	/**
	 * Busca dados do Servidor
	 * <br>
	 * JSP: <ul>
	 * 		  <li>sigaa.war/pesquisa/invencao/inventores.jsp</li>
	 *      </ul>
	 * @return
	 * @throws DAOException 
	 */
	public void buscaDadosServidor() throws DAOException{
		if (servidor.getId() != 0) {
			servidor = getGenericDAO().findByPrimaryKey(servidor.getId(), Servidor.class);
			if (servidor.getPessoa().getPais() == null){
				servidor.getPessoa().setPais(new Pais());
			}
			if (servidor.getPessoa().getPais().getId() > 0){
				servidor.getPessoa().setPais(getGenericDAO().findByPrimaryKey(servidor.getPessoa().getPais().getId(), Pais.class));
			}
			if(servidor.getPessoa().getEnderecoContato() == null){
				servidor.getPessoa().setEnderecoContato(new Endereco());
			}
			if (servidor.getPessoa().getEnderecoContato().getId() > 0){
				servidor.getPessoa().setEnderecoContato(getGenericDAO().findByPrimaryKey(servidor.getPessoa().getEnderecoContato().getId(), Endereco.class));
			}
			if (servidor.getPessoa().getNomeMae() == null){
				servidor.getPessoa().setNomeMae("Não informado");
			}
		}
	}
	
	/**
	 * Verifica dados do Docente Externo
	 * <br>
	 * JSP: Não invocado por JSP.
	 * @return
	 * @throws DAOException 
	 */
	public void verificaDadosDocenteExterno() throws DAOException{
		if (docenteExterno.getPessoa().getPais() == null){
			docenteExterno.getPessoa().setPais(new Pais());
		}
		if (docenteExterno.getPessoa().getPais().getId() > 0){
			docenteExterno.getPessoa().setPais(getGenericDAO().findByPrimaryKey(docenteExterno.getPessoa().getPais().getId(), Pais.class));
		}
		if(docenteExterno.getPessoa().getEnderecoContato() == null){
			docenteExterno.getPessoa().setEnderecoContato(new Endereco());
		}
		if (docenteExterno.getPessoa().getEnderecoContato().getId() > 0){
			docenteExterno.getPessoa().setEnderecoContato(getGenericDAO().findByPrimaryKey(docenteExterno.getPessoa().getEnderecoContato().getId(), Endereco.class));
		}
		if (docenteExterno.getPessoa().getNomeMae() == null){
			docenteExterno.getPessoa().setNomeMae("Não informado");
		}
	}

	/**
	 * Adiciona inventor
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>sigaa.war/pesquisa/invencao/inventores.jsp</li>
	 *      </ul>
	 * @return
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String adicionarInventor() throws ArqException, RemoteException, NegocioException {

		GenericDAO dao = getGenericDAO();
		ListaMensagens mensagens = new ListaMensagens();

		// define qual o tipo de inventor será adicionado (default = docente)
		int categoriaMembro = getParameterInt("categoriaMembro", CategoriaMembro.DOCENTE);

		inventor.setCategoriaMembro(dao.findByPrimaryKey(categoriaMembro, CategoriaMembro.class));			

		InvencaoValidator.validaAdicionaInventorInterno(inventor, docente, discente, servidor, mensagens);			
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return telaInventores();
		}
		
		if (CategoriaMembro.EXTERNO == categoriaMembro) {

			if ((docenteExterno == null) || (docenteExterno.getPessoa() == null)) {
				docenteExterno = new DocenteExterno();
				cpf = "";
				return telaInventores();
			}

			if ((docenteExterno.getPessoa().isInternacional()) || (ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf)))					
				InvencaoValidator.validaInventorDocenteExterno(inventor, docenteExterno, mensagens);
			else
				mensagens.addErro("CPF digitado para o docente externo é ínválido!");

			verificaDadosDocenteExterno();
			
			docenteExterno.getPessoa().setNome(	docenteExterno.getPessoa().getNome().trim().toUpperCase() );
			docenteExterno.setTipoDocenteExterno(null);
			if(!ValidatorUtil.isEmpty(docenteExterno.getInstituicao()))
				docenteExterno.setInstituicao(getGenericDAO().findByPrimaryKey(docenteExterno.getInstituicao().getId(), InstituicoesEnsino.class));

			inventor.setDocenteExterno(docenteExterno);
			inventor.setPessoa(docenteExterno.getPessoa());
			getCurrentSession().setAttribute("aba", "autor-externo");
			
		//não é pessoa externa...	
		} else{
			
			if (CategoriaMembro.DOCENTE == categoriaMembro) {
				inventor.setDocente(getGenericDAO().findByPrimaryKey(docente.getId(), Servidor.class));
				inventor.setPessoa(docente.getPessoa());
				inventor.getPessoa().setNome(inventor.getDocente().getPessoa().getNome());
				InvencaoValidator.validaInventor(inventor, mensagens);
				getCurrentSession().setAttribute("aba", "autor-docente");
			}

			if (CategoriaMembro.DISCENTE == categoriaMembro) {
				inventor.setDiscente(getGenericDAO().findByPrimaryKey(discente.getId(), Discente.class));
				inventor.setPessoa(discente.getPessoa());
				inventor.getPessoa().setNome(inventor.getDiscente().getPessoa().getNome());
				InvencaoValidator.validaInventor(inventor, mensagens);
				getCurrentSession().setAttribute("aba", "autor-discente");
			}
			
			if (CategoriaMembro.SERVIDOR == categoriaMembro) {
				ServidorDao servDao = getDAO(ServidorDao.class);
				try {
					Servidor s = servDao.findAndFetch(servidor.getId(), Servidor.class, "id", "pessoa", 
							"pessoa.municipio", "pessoa.contaBancaria", "pessoa.enderecoContato");
					inventor.setServidor(servidor);
					inventor.getServidor().getPessoa().setContaBancaria( s.getPessoa().getContaBancaria() );
					inventor.getServidor().getPessoa().setEnderecoContato( s.getPessoa().getEnderecoContato() );
					inventor.getServidor().getPessoa().setMunicipio( s.getPessoa().getMunicipio() );
					inventor.getServidor().getPessoa().setId( servidor.getPessoa().getId() );
					inventor.getServidor().getPessoa().setNome( s.getPessoa().getNome() );
					inventor.setPessoa(inventor.getServidor().getPessoa());
					InvencaoValidator.validaInventor(inventor, mensagens);
					getCurrentSession().setAttribute("aba", "autor-servidor");
				} finally {
					dao.close();
				}
			}
		}
		
		// informa qual o tipo de membro de equipe será cadastrado
		getCurrentSession().setAttribute("categoriaAtual", categoriaMembro);
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		// /FIM DAS VALIDAÇÕES, AGORA VAI GRAVAR NO BANCO MESMO....
		if (CategoriaMembro.EXTERNO == categoriaMembro) {

			// verifica se a pessoa externa já está na base de dados (pelo
			// cpf_cnpj) se não estiver, inclui...
			// se tiver, retorna a pessoa encontrada no banco
			// se for estrangeiro inclui sempre
			Pessoa pessoa = adicionaPessoaExterna(inventor.getDocenteExterno().getPessoa());

			inventor.setPessoa(pessoa);
			
			alteraDadosInventor(inventor.getPessoa());
			
			// seta a pessoa encontrada ou que acabou de ser incluída...
			inventor.getDocenteExterno().setPessoa(pessoa);

		}
		// Caso seja Docente, Discente ou Servidor
		else
			alteraDadosInventor(inventor.getPessoa());
		
		if (!obj.getInventores().contains(inventor)) {

			if (CategoriaMembro.DOCENTE == categoriaMembro){
				inventor.setDiscente(null);
				inventor.setServidor(null);
				inventor.setDocenteExterno(null);
			}else if(CategoriaMembro.DISCENTE == categoriaMembro){
				inventor.setDocente(null);
				inventor.setServidor(null);
				inventor.setDocenteExterno(null);
			}else if(CategoriaMembro.SERVIDOR == categoriaMembro){
				inventor.setDocente(null);
				inventor.setDiscente(null);
				inventor.setDocenteExterno(null);
			}else if (CategoriaMembro.EXTERNO == categoriaMembro){
				inventor.setDocente(null);
				inventor.setDiscente(null);
				inventor.setServidor(null);
			}

			obj.addInventor(inventor);

			gravar();

		} else
			addMensagemErro("Autor já associado à invenção");
		
		// limpa os dados
		popularDadosInventor();

		return telaInventores();
	}
	
	/**
	 * Persiste pessoa seja docente, discente ou servidor
	 * 
	 * @param pessoa
	 *            Objeto do tipo pessoa
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void alteraDadosInventor(Pessoa pessoa) throws ArqException, RemoteException{
		try {
			PessoaMov pessoaMov = new PessoaMov();
			pessoaMov.setPessoa(pessoa);
			pessoaMov.setCodMovimento(SigaaListaComando.ALTERAR_PESSOA);
			prepareMovimento(SigaaListaComando.ALTERAR_PESSOA);
			execute(pessoaMov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} finally {
			// Re-preparar o comando anterior
			prepareMovimento(ArqListaComando.ALTERAR);
		}
	}
	
	/**
	 * Inicializa o objeto do inventor
	 */
	private void popularDadosInventor() {
		cpf = "";
		docente = new Servidor();
		discente = new Discente();
		servidor = new Servidor();
		docenteExterno = new DocenteExterno();

		inventor = new Inventor();
		inventor.setInvencao(this.obj);
		inventor.setDocente(new Servidor());
		inventor.setDiscente(new Discente());
		inventor.setServidor(new Servidor());
		inventor.setDocenteExterno(new DocenteExterno());
		inventor.setPessoa(new Pessoa());
		
		docente.getPessoa().setPais(new Pais());
		discente.getPessoa().setPais(new Pais());
		servidor.getPessoa().setPais(new Pais());
		inventor.getPessoa().setPais(new Pais());
	}
	
	/**
	 * Persiste pessoa externa
	 * 
	 * @param pessoa
	 *            Objeto do tipo pessoa
	 * @return pessoa Objeto do tipo pessoa se cadastrou com sucesso, Retorna
	 *         Null se falhar.
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws DAOException
	 */
	private Pessoa adicionaPessoaExterna(Pessoa pessoa) throws ArqException,
			RemoteException {

		PessoaDao pessoaDao = getDAO(PessoaDao.class);

		if ((pessoa.isInternacional()) || ((pessoaDao.findByCpf(pessoa.getCpf_cnpj()) == null))) {

			pessoa.setTipo('F');

			try {

				ProjetoPesquisaValidator.limparDadosPessoa(pessoa);

				// Cadastrar a pessoa
				PessoaMov pessoaMov = new PessoaMov();
				pessoaMov.setPessoa(pessoa);
				pessoaMov.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
				pessoaMov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);

				prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA);
				pessoa = (Pessoa) execute(pessoaMov, getCurrentRequest());

			} catch (NegocioException e) {

				addMensagens(e.getListaMensagens());
				return null;

			} finally {

				// Re-preparar o comando anterior
				prepareMovimento(ArqListaComando.ALTERAR);
				pessoaDao.close();

			}

		} else { // a pessoa já está no banco...

			pessoa = docenteExterno.getPessoa();

		}
		return pessoa;

	}
	
	/**
	 * Remove Parecer
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>Não invocado por JSP</li>
	 *      </ul>
	 * @return
	 */
	public void removeParecer() throws DAOException{
		List<ParecerInvencao> parecer = (List<ParecerInvencao>)getGenericDAO().findByExactField(ParecerInvencao.class, "invencao", obj.getId());
		for (ParecerInvencao p : parecer){
			try {
				if (p.getId() > 0){
					remover(p);
				}
			} catch (Exception e) {
				addMensagemErro("Erro ao Remover Parecer da Invenção.");
				notifyError(e);
			}
		}
	}
	
	/**
	 * Remover inventor
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/sigaa.war/pesquisa/invencao/inventores.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String removerInventor() {

		int id = getParameterInt("idInventor", 0);
		Inventor inv = new Inventor(id);

		// Verifica se o objeto já existe no banco de dados.
		if (id > 0) {
			try {
				inv = getGenericDAO().findByPrimaryKey(inv.getId(), Inventor.class, "id", "pessoa.id");
				remover(inv); // remove do banco de dados

			} catch (Exception e) {
				addMensagemErro("Erro ao Remover Membro da Equipe da Atividade.");
				notifyError(e);
			}
		} else { // transient
			int idPessoa = getParameterInt("idPessoa", 0);
			inv.setPessoa(new Pessoa(idPessoa));
		}

		// remove da view
		removerInventorLista(inv);
		
		popularDadosInventor();

		return telaInventores();
	}

	/**
	 * O remover do hashset não funcionava mesmo com equals e hashcode implementado,
	 * fiz isso pra forçar
	 * @param inv
	 */
	private void removerInventorLista(Inventor inv) {
		List<Inventor> list = new ArrayList<Inventor>();
		for (Inventor i : obj.getInventores()) {
			list.add(i);
		}
		
		list.remove(inv);
		
		obj.setInventores(new HashSet<Inventor>());
		
		if (list != null) {
			for (Inventor inventor : list) {
				obj.getInventores().add(inventor);
			}
		}
	}
	
	/**
	 * Grava a invenção
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/sigaa.war/pesquisa/invencao/inventores.jsp</li>
	 *      </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterInventores() throws SegurancaException, ArqException, NegocioException{
		if(obj.getInventores() != null && obj.getInventores().isEmpty()){
			addMensagem(MensagensPesquisa.INFORME_PELO_MENOS_UM_INVENTOR);
			return null;
		}
	    GenericDAO dao = getGenericDAO();
	    for(Inventor i: obj.getInventores()) {
	        i.setPessoa( dao.refresh(i.getPessoa()) );
	    }
	    prepareMovimento(SigaaListaComando.NOTIFICAR_INVENCAO);
		return forward(JSP_RESUMO);
	}

	/**
	 * Serve para submeter uma notificação de uma nova invenção
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/invencao/resumo.jsp</li>
	 *      </ul>
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String submeterNotificacaoInvencao() throws NegocioException, ArqException{
		try {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		obj.setStatus(TipoStatusNotificacaoInvencao.SUBMETIDA);

		mov.setCodMovimento(SigaaListaComando.NOTIFICAR_INVENCAO);
		obj = execute(mov, getCurrentRequest());
		
		notificarGestor();
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Notificação de Invenção");
		}
		catch (NegocioException e) {
			addMensagemWarning(e.getMessage());
			return null;
		}
		return verComprovante();
	}
	
	/**
	 * Notifica o gestor do Núcleo de Inovação Tecnológica por email
	 */
	private void notificarGestor() throws ArqException {
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setNome("Gestor do NIT");
		mail.setEmail(ParametroHelper.getInstance().getParametro(ConstantesParametro.EMAIL_NOTIFICACAO_INVENCAO));
		mail.setAssunto("SIGAA - Notificação de Invenção Submetida");
		mail.setMensagem("Caro Gestor do NIT, <br/><br/>" +
				"Uma nova notificação de invenção de código " +obj.getCodigo()+
				" foi submetida através do SIGAA.<br/>" +
				"Os dados completos da invenção notificada estão disponíveis no SIGAA, " +
				"através do seguinte caminho a partir do menu principal: 'Pesquisa -> Inovação -> Gerenciar Notificações'.<br><br>");
		Mail.send(mail);
	}
	
	/**
	 * Esse método serve para visualizar o comprovante da Invenção
	 * <br/>
	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/invencao/lista_invencoes_pendentes.jsp</li>
	 *        <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/invencao/lista.jsp</li>
	 *      </ul>
	 * 
	 * @return
	 */
	public String verComprovante(){
		if(obj.getId() == 0){
			try {
				GenericDAO dao = getGenericDAO();
				int id = getParameterInt("idInvencao");
				obj.setId(id);
				PersistDB obj = this.obj;
	
				this.obj = (Invencao) dao.findByPrimaryKey(obj.getId(),	obj.getClass());
			} catch (Exception e) {
				return tratamentoErroPadrao(e);
			}
		}
		
		return forward(JSP_COMPROVANTE);
	}
	
	/**
	 * Esse método tem com finalidade a realização de uma consulta em uma capa de processo.
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/invencao/comprovante.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String consultaCapaProcesso() {
		if(obj.getNumeroProtocolo() != null && obj.getAnoProtocolo() != null){
			TokenAutenticacao token = generator.generateToken(getUsuarioLogado(), getSistema(), String.valueOf(obj.getNumeroProtocolo()), String.valueOf(obj.getAnoProtocolo()));
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSipac() + "/sipac/visualizaCapaProcesso?numero=" + obj.getNumeroProtocolo() + "&ano=" + 
					obj.getAnoProtocolo() + "&key=" + token.getKey() + "&id=" + token.getId());
		} else {
			addMensagem(MensagensPesquisa.NAO_EXISTE_PROCESSO_INVENCAO);
			return null;
		}
	}
	
	/* Métodos de navegação entre as telas do caso de uso */
	
	/**
	 * Redireciona para a tela de dados gerais
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/pesquisa/invencao/revelacoes.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String telaDadosGerais() {
		return forward(JSP_DADOS_GERAIS);
	}

	/**
	 * Vai para a tela de revelações
	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/pesquisa/invencao/propriedade_terceiros.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String telaRevelacoes() {
		return forward(JSP_REVELACOES);
	}

	/**
	 * Redireciona para a tela de propriedades de terceiros
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/pesquisa/invencao/vinculos.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String telaPropriedadeTerceiros() {
		return forward(JSP_PROPRIEDADES_TERCEIROS);
	}

	/**
	 * Encaminha para a tela onde são especificados os financiamentos da invenção
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/pesquisa/invencao/dados_adicionais.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String telaFinanciamentos() {
		return forward(JSP_FINANCIAMENTOS);
	}

	/**
	 * Encaminha para a tela onde são definidos os inventores
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/pesquisa/invencao/resumo.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String telaInventores() {
		return forward(JSP_INVENTORES);
	}

	/* Métodos getters e setters */
	public ArquivoInvencao getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoInvencao arquivo) {
		this.arquivo = arquivo;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public FinanciamentoInvencao getFinanciamento() {
		return financiamento;
	}

	public void setFinanciamento(FinanciamentoInvencao financiamento) {
		this.financiamento = financiamento;
	}

	public VinculoInvencao getVinculo() {
		return vinculo;
	}

	public void setVinculo(VinculoInvencao vinculo) {
		this.vinculo = vinculo;
	}

	public String getCodigoProjetoPesquisa() {
		return codigoProjetoPesquisa;
	}

	public void setCodigoProjetoPesquisa(String codigoProjetoPesquisa) {
		this.codigoProjetoPesquisa = codigoProjetoPesquisa;
	}

	public Inventor getInventor() {
		return inventor;
	}

	public void setInventor(Inventor inventor) {
		this.inventor = inventor;
	}

	public List<MensagemAviso> getAvisosAjax() {
		return avisosAjax;
	}

	public void setAvisosAjax(List<MensagemAviso> avisosAjax) {
		this.avisosAjax = avisosAjax;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Collection<Invencao> getInvencoesGravadas() {
		return invencoesGravadas;
	}

	public void setInvencoesGravadas(Collection<Invencao> invencoesGravadas) {
		this.invencoesGravadas = invencoesGravadas;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	/**
	 * Retornar um boleano indicando se o autor é da ufrn ou não 
	 * @return
	 */
	public boolean isAutorUfrn(){
		if(obj.getInventores() != null && !obj.getInventores().isEmpty()){
			for(Inventor inv: obj.getInventores()){
				if(inv.getCategoriaMembro().getId() != CategoriaMembro.EXTERNO)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Retorna um boleano indicando se o autor é externo 
	 * @return
	 */
	public boolean isAutorExterno(){
		if(obj.getInventores() != null && !obj.getInventores().isEmpty()){
			for(Inventor inv: obj.getInventores()){
				if(inv.getCategoriaMembro().getId() == CategoriaMembro.EXTERNO)
					return true;
			}
		}
		return false;
	}

	public AreaConhecimentoCnpq getGrandeArea() {
		return grandeArea;
	}

	public void setGrandeArea(AreaConhecimentoCnpq grandeArea) {
		this.grandeArea = grandeArea;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public AreaConhecimentoCnpq getSubarea() {
		return subarea;
	}

	public void setSubarea(AreaConhecimentoCnpq subarea) {
		this.subarea = subarea;
	}

	public AreaConhecimentoCnpq getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AreaConhecimentoCnpq especialidade) {
		this.especialidade = especialidade;
	}

	public Collection<SelectItem> getAreas() {
		return areas;
	}

	public void setAreas(Collection<SelectItem> areas) {
		this.areas = areas;
	}

	public Collection<SelectItem> getSubareas() {
		return subareas;
	}

	public void setSubareas(Collection<SelectItem> subareas) {
		this.subareas = subareas;
	}

	public Collection<SelectItem> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Collection<SelectItem> especialidades) {
		this.especialidades = especialidades;
	}
	
	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}

}