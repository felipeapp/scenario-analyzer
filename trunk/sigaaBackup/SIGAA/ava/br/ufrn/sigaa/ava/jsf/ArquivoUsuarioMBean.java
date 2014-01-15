/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.jsf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.PortaArquivosDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.administracao.dominio.ConfiguracaoPortaArquivos;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.negocio.MovimentoArquivoTurma;
import br.ufrn.sigaa.ava.negocio.RegistroAtividadeAvaHelper;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.dominio.TamanhoArquivo;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;

/**
 * Controlador para o porta arquivos do usuário.
 *
 */
@Component("arquivoUsuario")
@Scope("request")
public class ArquivoUsuarioMBean extends ControllerTurmaVirtual {

	/** Objeto movimentado */
	private ArquivoUsuario obj;
	
	/** Arquivo de Upload, o arquivo que foi enviado */
	private UploadedFile arquivo;
	
	/** Arquivo associado a uma turma */
	private ArquivoTurma arquivoTurma;

	/** Se o arquivo será adicionado na turma ou apenas no porta arquivos */
	private boolean associarTurma = false;
	/** Se o o bean foi acessado pelo portal do docente ou pela turma virtual */
	private boolean ehPortalDocente = false;
	
	/** Tamanho máximo permitido do arquivo */
	private int tamanhoMaximoArquivo = ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_ARQUIVO);

	/**Lista de arquivo da turma */
	private List<ArquivoTurma> arquivosTurma;
	
	/**
	 * Construtor padrão
	 * @throws Exception
	 */
	public ArquivoUsuarioMBean() throws Exception {
		init();
	}

	/**
	 * Inicia os dados utilizados pelo MBean.
	 */
	private void init() {
		obj = new ArquivoUsuario();
		obj.setPasta(new PastaArquivos());

		arquivoTurma = new ArquivoTurma();
		arquivoTurma.setAula(new TopicoAula());
	}

	/**
	 * Retorna as permissões que o usuário logado possui para a turma atual.
	 * @return
	 */
	private PermissaoAva getPermissaoUsuario() {
		try {
			TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
			PermissaoAva permissao = dao.findPermissaoByPessoaTurma(getUsuarioLogado().getPessoa(), turma());
			return permissao;
		} catch(RuntimeNegocioException e) {
			return null;
		}
	}

	/**
	 * Se o usuário atual for o docente da turma, retorna seu usuário.
	 * @return
	 */
	private Usuario getDonoPortaArquivos() {
		if (getServidorUsuario() != null && getServidorUsuario().getCategoria().getId() == Categoria.DOCENTE)
			return getUsuarioLogado();
		if (getUsuarioLogado().getDocenteExterno() != null)
			return getUsuarioLogado();
		
		PermissaoAva permissao = getPermissaoUsuario();
		if (permissao != null)
			return permissao.getUsuarioCadastro();

		return null;
	}

	/**
	 * Retorna todos os arquivos de um diretório passado como parâmetro de request. Usado
	 * no carregamento via AJAX dos arquivos de uma pasta.
	 * @return
	 * @throws ArqException
	 */
	public List<ArquivoUsuario> getArquivosDiretorio() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_ARQUIVO);
		PortaArquivosDao dao = getDAO(PortaArquivosDao.class);
		return dao.findArquivosByPasta(getParameterInt("idPasta"), getUsuarioLogado().getId());
	}

	/**
	 * Exibe o porta-arquivos no portal docente.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /portais/docente/menu_docente.jsp
	 * @return
	 */
	public String portaArquivos() {
		getCurrentSession().setAttribute("paComTurma", "false");
		return forward("/ava/PortaArquivos/view.jsp");
	}
	
	/**
	 * Direciona o usuário para a página de inserir arquivos
	 * no porta-arquivos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: Não é chamado por JSPs.
	 * @return
	 */
	public String viewCadastrar() {
		return forward("/ava/PortaArquivos/index.jsp");
	}

	/**
	 * Cadastra um arquivo no porta arquivos.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war/ava/PortaArquivos/associar.jsp</li>
	 * 	<li>sigaa.war/ava/PortaArquivos/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrar() throws ArqException {
		
		Boolean possuiTurma = new Boolean((String) getCurrentSession().getAttribute("paComTurma"));
		// A operação não está sendo feita na turma virtual.
		if (possuiTurma)
			registrarAcao(obj.getNome(), EntidadeRegistroAva.ARQUIVO, AcaoAva.INICIAR_INSERCAO, true, obj.getId());
		
		GenericDAO dao = null;
		
		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_ARQUIVO);
			if (obj.getId() == 0) { // Cadastrar um novo arquivo. Se não entrar é porque está associando um arquivo existente

				if (arquivo == null || arquivo.getSize() == 0)
					throw new NegocioException("Escolha um arquivo para enviar ao porta-arquivos.");

				if ((getTamanhoTotalOcupado() + arquivo.getSize()) > getTamanhoPortaArquivos())
					throw new NegocioException("Não é possível inserir o arquivo porque o porta-arquivos está cheio.");

				if (arquivo.getSize() > tamanhoMaximoArquivo * TamanhoArquivo.MEGA_BYTE)
					throw new NegocioException("Não é possível inserir o arquivo porque seu tamanho ultrapassa " + tamanhoMaximoArquivo + " MB.");
					
				String fileName = arquivo.getName();
				int pos = fileName.lastIndexOf("\\");
				fileName = fileName.substring(pos + 1);
				obj.setNome(fileName);
				obj.setTamanho(arquivo.getSize());
				obj.setData(new Date());
				obj.setUsuario(getDonoPortaArquivos());

				if (obj.getPasta().getId() == -1)
					obj.setPasta(null);

				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), fileName);
				obj.setIdArquivo(idArquivo);
				
			}

			TopicoAulaMBean topicoAula = (TopicoAulaMBean) getMBean("topicoAula");
			if (associarTurma && arquivoTurma.getAula().getId() == 0) {
				if (CollectionUtils.isEmpty(topicoAula.getComboIdentado()))
					throw new NegocioException("Não há nenhum tópico de aula cadastrado. Por favor, cadastre um tópico de aula.");
				else
					throw new NegocioException("Selecione um tópico de aula.");
			}

			MovimentoArquivoTurma mov = new MovimentoArquivoTurma();
			mov.setArquivo(obj);
			mov.setArquivoTurma(arquivoTurma);
			mov.setCadastrarEm(cadastrarEm);
			mov.setAssociarTurma(associarTurma);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_ARQUIVO);

			Notification n = (Notification) execute(mov, getCurrentRequest());
			if (n != null && n.hasMessages())
				return notifyView(n);
			
			if (possuiTurma && n != null)
				registrarAcao(obj.getNome(), EntidadeRegistroAva.ARQUIVO, AcaoAva.INSERIR, n.getTurmasSucesso(), obj.getId());
			
			dao = getGenericDAO();
			arquivoTurma.setAula(dao.refresh(arquivoTurma.getAula()));		
			Turma turma = dao.refresh(arquivoTurma.getAula().getTurma());
			
			String mensagemAtividade = "Novo Arquivo: " + obj.getNome();
			
			// Caso o arquivo não esteja sendo adicionado em uma turma.
			if ( turma != null ){
				if (n != null && n.getTurmasSucesso()!=null && !n.getTurmasSucesso().isEmpty()){
					for (Turma t : n.getTurmasSucesso())
						RegistroAtividadeAvaHelper.getInstance().registrarAtividade(t, mensagemAtividade);
				}
			}	
			
			if ( turma != null && arquivoTurma.isNotificarAlunos() ) {		
					
				String mensagem = "Um novo arquivo foi adicionado na turma virtual: " + turma.getDescricaoSemDocente() + "\n\nAcesse o SIGAA para proceder o download do arquivo.";
				
				notificarTurmas(cadastrarEm, "Novo arquivo Inserido: " + " - " +  obj.getNome() + " - " + turma.getDescricaoSemDocente()  , 
				mensagem, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);
			}
			
			// Se o docente iniciou o caso de uso em um tópico de aula, volta para a turma
			if (getCurrentRequest().getSession().getAttribute("idTopicoSelecionado") != null){
				// Remove o id do tópico selecionado da session.
				getCurrentRequest().getSession().removeAttribute("idTopicoSelecionado");
				addMensagemInformation("Arquivo associado com sucesso.");
				MenuTurmaMBean mBean = getMBean("menuTurma");
				return mBean.acessarPrincipal();
			}
			
			if (arquivoTurma.getAula().getId() == 0) {
				addMensagemInformation("Arquivo inserido com sucesso.");
				return redirectJSF("/ava/PortaArquivos/view.jsf");
			}
			else if (isAssociarTurma()) {
				addMensagemInformation("Arquivo associado com sucesso.");
				return redirectJSF("/ava/PortaArquivos/view.jsf");
			}
			else if ( ehPortalDocente ) {
				addMensagemInformation("Arquivo inserido com sucesso.");
				return portaArquivos();
			}
			else {
				addMensagemInformation("Arquivo inserido com sucesso.");
				return inserirArquivoTurma();
			}
			
			
		} catch (NegocioException e){
			addMensagens (e.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());

			if (getParameterInt("id") != null) { // Veio do associar a tópico!
				return redirectJSF(getContextPath() + "/ava/PortaArquivos/associar.jsf?id=" + getParameterInt("id"));
			}
			return null;
		}
		finally{
			if ( dao != null )
				dao.close();
		}
	}
	
	
	@Override
	public String cancelar() {
		if (isAssociarTurma() || arquivoTurma.getAula().getId() == 0)
			return redirectJSF("/ava/PortaArquivos/view.jsf");
		else	
			return super.cancelar();
	}
	
	
	/**
	 * Seleciona uma turma, entra na turma selecionada e redireciona para o porta arquivos.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war/ava/PortaArquivos/selecionaTurma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarTurma  () throws ArqException{
		
		GenericDAO dao = null;
		
		int idTurma = getParameterInt("idTurma"); 
		
		try{
			
			dao = getGenericDAO();
			Turma t = dao.findByPrimaryKey(idTurma, Turma.class);
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			tBean.setTurma(t);
			TopicoAulaMBean tABean = getMBean("topicoAula");
			tABean.setTopicosAulas(null);
			tABean.getAulas();
			
			return redirect("/sigaa/ava/PortaArquivos/associar.jsf");
		}finally{
			if ( dao != null )
				dao.close();
		}
	}

	/**
	 * Baixa Todos os arquivos de uma turma em um zip.
	 * Em que cada arquivo vai estar separado por partas de acordo com seu tópico de aula
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ui>
	 * <li>/ava/ArquivoTurma/listar_discente.jsp</li>
	 * </ui> 
	 */
	public void baixarAllArquivosTurma() {
		Collections.sort(arquivosTurma, new Comparator<ArquivoTurma>() {
			public int compare(ArquivoTurma a1, ArquivoTurma a2) {
				return a1.getAula().getId() - a2.getAula().getId();
			}
		});
		
		List<ArquivoTurma> mesmosNomes;
		for (ArquivoTurma arquivoAtual: arquivosTurma) {
			mesmosNomes = new ArrayList<ArquivoTurma>();
			//seleciona os arquivos com mesmo nome que o atual.
			for (ArquivoTurma arquivo: arquivosTurma) {
				if (arquivoAtual.getId() != arquivo.getId()) {
					if (arquivoAtual.getArquivo().getNome().equals(arquivo.getArquivo().getNome()))
						mesmosNomes.add(arquivo);
					
				}
			}
			int i = 1;
			//renomeia os arquivos que possuem nome em comum.
			for (ArquivoTurma arquivo: mesmosNomes) {
				ArquivoUsuario a = arquivo.getArquivo();
				a.setNome(a.getNome().replace("."+a.getExtensao(), "")+"("+i+")."+a.getExtensao());
				i++;
			}
			
		}
		
		criarZipArquivosTopicos(turma());
	}
	
	
	/**
	 * Baixa o arquivo pelo link do portal principal da Turma Virtual
	 * Se for um discente baixando o arquivo, registra o Log dessa ação.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ui>
	 * 	<li>/ava/aulas.jsp</li>
	 * 	<li>/ava/ArquivoTurma/listar_discente.jsp</li>
	 * 	<ul>					
	 */
	public void baixarArquivoPortalPrincipal() {
		Integer id = getParameterInt("id", 0);
		
		registrarAcao(EnvioArquivoHelper.recuperaNomeArquivo(id), EntidadeRegistroAva.ARQUIVO, AcaoAva.ACESSAR, id);
				
	    FacesContext faces = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

        //response.setHeader("Pragma", "No-cache");
        //response.setDateHeader("Expires", 0);
        //response.setHeader("Cache-Control", "no-cache");
        
		try {
            EnvioArquivoHelper.recuperaArquivo(response, id, true );
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        registrarLogAcessoDiscenteTurmaVirtual(Arquivo.class.getName(), id, turma().getId());
        
        faces.responseComplete();
	}

	/**
	 * Exibe a página após cadastrar.<br/><br/>
	 * 
	 * Não invocado por jsps. É publico por causa da arquitetura.
	 * @return
	 */
	public String forwardCadastrar() {
		return redirectJSF("/ava/PortaArquivos/view.jsf?expanded=true");
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/**
	 * Retorna todos os arquivos de um usuário.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ArquivoUsuario> getAllArquivos() throws DAOException {
		GenericDAO dao = getDAO(GenericSigaaDAO.class);
		return dao.findByExactField(ArquivoUsuario.class, "usuario", getUsuarioLogado().getId());

	}

	/**
	 * Retorna todos os arquivos de um usuário no formato de combobox.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAllCombo() throws DAOException {

		GenericDAO dao = getDAO(GenericSigaaDAO.class);
		Collection<ArquivoUsuario> arquivos =  dao.findByExactField(ArquivoUsuario.class, "usuario", getUsuarioLogado().getId());

		return toSelectItems(arquivos,"id", "nome");

	}

	/**
	 * Retorna todas as pastas do usuário em formato de combobox.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPastasUsuarioCombo() throws DAOException {
		PortaArquivosDao dao = getDAO(PortaArquivosDao.class);
		List<SelectItem> nos = new ArrayList<SelectItem>();

		List<PastaArquivos> pastas = null;

		if (getPermissaoUsuario() == null) {
			nos.add(new SelectItem(-1, "Meus Arquivos"));
			pastas = dao.findPastasByUsuario(getUsuarioLogado());
		} else {
			pastas = dao.findPastasByUsuario(getDonoPortaArquivos(), turma());
		}

		getNoCombo(pastas, nos, 0);

		return nos;
	}

	/**
	 * Retorna todas as pastas do usuário em formato de combobox sem a
	 * pasta Meus arquivos (raiz).
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPastasUsuarioSemRaizCombo() throws DAOException {
		PortaArquivosDao dao = getDAO(PortaArquivosDao.class);
		List<SelectItem> nos = new ArrayList<SelectItem>();
		List<PastaArquivos> pastas = dao.findPastasByUsuario(getUsuarioLogado());

		getNoCombo(pastas, nos, 0);

		return nos;
	}

	/**
	 * Retorna a sub-árvore de um nó.
	 * 
	 * @param no
	 * @return
	 */
	public void getNoCombo(List<PastaArquivos> pastas, List<SelectItem> nos, int nivel) {

		for (PastaArquivos pasta : pastas) {

			if (!hasNode(nos, pasta)) {
				StringBuilder sb = new StringBuilder("--");
				for (int i = 0; i < nivel; i++)
					sb.append("--");

				SelectItem item = new SelectItem(String.valueOf(pasta.getId()), sb.toString() + pasta.getNome());
				nos.add(item);

				if (pasta.getFilhos() != null && !pasta.getFilhos().isEmpty()) {
					getNoCombo(pasta.getFilhos(), nos, nivel+1);
				}
			}

		}

	}

	/**
	 * Verifica se uma pasta existe na árvore de pastas.
	 * 
	 * @param nos
	 * @param pasta
	 * @return
	 */
	private boolean hasNode(List<SelectItem> nos, PastaArquivos pasta) {
		for (SelectItem item : nos) {
			Integer id = Integer.valueOf(item.getValue().toString());
			if (id.equals(pasta.getId()))
				return true;
		}
		return false;
	}

	public String getFormPage() {
		return "/ava/PortaArquivos/index.jsp";
	}


	/**
	 * Retorna a pasta selecionada na árvore.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public PastaArquivos getPastaSelecionada() throws DAOException {
		GenericDAO dao = getDAO(GenericSigaaDAO.class);
		PastaArquivos pasta = new PastaArquivos();
		if (getParameter("idPasta") != null)
			pasta = dao.findByPrimaryKey(getParameterInt("idPasta"), PastaArquivos.class);
		return pasta;
	}

	/**
	 * Retorna o tamanho total ocupado pelo usuário no Porta arquivos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public long getTamanhoTotalOcupado() throws DAOException {
		PortaArquivosDao dao = getDAO(PortaArquivosDao.class);
		return dao.findTotalOcupadoByUsuario(getUsuarioLogado().getId());
	}

	/**
	 * Retorna o tamanho máximo definido para o Porta-Arquivos.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public long getTamanhoPortaArquivos() throws DAOException {
		long tamanho = 0;

		try {
			ConfiguracaoPortaArquivos config = null;
			config = getGenericDAO().findByExactField(ConfiguracaoPortaArquivos.class, "usuario.id", getUsuarioLogado().getId(), true);		

			if ( config == null )
				tamanho = ParametroHelper.getInstance().getParametroLong(ConstantesParametro.TAMANHO_MAXIMO_PORTA_ARQUIVOS);
			else
				tamanho = config.getTamanhoMaximoPortaArquivos();
		} catch(NullPointerException e) {
			addMensagemErro("O tamanho do porta-arquivos não foi configurado. Por favor, entre em contato com a administração do sistema.");
		}
		
		return tamanho;
	}

	/**
	 * Insere um arquivo em uma turma.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war/ava/aulas.jsp</li>
	 * 	<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * @return
	 */
	public String inserirArquivoTurma() {
		init();
		return inserirArquivoTurma(null);
	}
	
	/**
	 * Insere um arquivo na turma virtual.<br/><br/>
	 * 
	 * Não é chamado por JSPs.
	 * @param idTopico
	 * @return
	 */
	public String inserirArquivoTurma(Integer idTopico) {
		
		paginaOrigem = getParameter("paginaOrigem");
		
		associarTurma = true;

		if (idTopico != null && idTopico > 0) {
			arquivoTurma.setAula(new TopicoAula());
			arquivoTurma.getAula().setId(idTopico);
		}

		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_ARQUIVO);
			prepareMovimento(SigaaListaComando.CADASTRAR_PASTA_TURMA);

			MovimentoArquivoTurma mov = new MovimentoArquivoTurma();
			mov.setUsuario(getDonoPortaArquivos());
			mov.setTurma(turma());
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PASTA_TURMA);

			obj.setPasta((PastaArquivos) execute(mov, getCurrentRequest()));
			
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
		
		//Garantir o recarregamento dos dados na página principal
		TopicoAulaMBean bean = getMBean("topicoAula");
		bean.setTopicosAulas(null);

		return forward("/ava/PortaArquivos/index.jsp");
	}
		
	/**
	 * Insere vários arquivos em uma turma.<br/><br/>
	 * 
	 * Método não invocado por JSPs:
	 * 
	 * @return
	 */
	public String inserirVariosArquivosTurma() {
		init();
		return inserirVariosArquivosTurma(null);
	}
	
	 /**
	 * Insere varios Arquivos de um única vez.  
	 * Método não invocado por JSPs.
	 * @param idTopico
	 * @return
	 */
	public String inserirVariosArquivosTurma(Integer idTopico) {
		
		paginaOrigem = getParameter("paginaOrigem");
		
		associarTurma = true;
		
		getSelecionarTurmaPadrao();
		
		if (idTopico != null && idTopico > 0) {
			arquivoTurma.setAula(new TopicoAula());
			arquivoTurma.getAula().setId(idTopico);
		}
		
		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_ARQUIVOS);
			prepareMovimento(SigaaListaComando.CADASTRAR_PASTA_TURMA);

			MovimentoArquivoTurma mov = new MovimentoArquivoTurma();
			mov.setUsuario(getDonoPortaArquivos());
			mov.setTurma(turma());
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PASTA_TURMA);

			obj.setPasta((PastaArquivos) execute(mov, getCurrentRequest()));
			
			arquivosTurma = new ArrayList<ArquivoTurma>();
			
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	
		return forward("/ava/PortaArquivos/cadastrar_arquivos_lote.jsp");
	}	
	
	/**
	 * Adiciona uma arquivo
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war\ava\PortaArquivos\cadastrar_arquivos_lote.jsp</li>
	 * </ul>
	 * @param e
	 * @return
	 */
	public String adicionarArquivo() throws DAOException, NegocioException {
		
		if (arquivo == null )
			return null;
		
		long tamanhoAdicionados = 0;
		String fileName = (arquivoTurma.getNome() != null && !arquivoTurma.getNome().isEmpty()) ? arquivoTurma.getNome() :  arquivo.getName();
		for(ArquivoTurma a: arquivosTurma) {
			if (a.getNome().equals(fileName)) {
				addMensagemErro("Um arquivo de mesmo nome já foi adicionado.");
				return null;
			}
			tamanhoAdicionados += arquivo.getSize();
		}
		
		if (arquivo.getSize() > tamanhoMaximoArquivo * TamanhoArquivo.MEGA_BYTE) {
			addMensagemErro("Não é possível inserir o arquivo porque seu tamanho ultrapassa " + tamanhoMaximoArquivo + " MB.");
			return null;
		}
		
		if ((getTamanhoTotalOcupado() + arquivo.getSize()) + tamanhoAdicionados> getTamanhoPortaArquivos()) {
			addMensagemErro("Não é possível adicionar o arquivo porque o limite do porta arquivo será ultapassado.");
			return null;
		}
		
		ArquivoTurma at = new ArquivoTurma();
		
		at.setArquivoUpload(arquivo);
		at.setDescricao(arquivoTurma.getDescricao());
		at.setNome(fileName);
		
		arquivoTurma.setNome(null);
		arquivoTurma.setDescricao(null);
		
		arquivosTurma.add(at);
		
		return null;
	}
	
	/**
	 * Cadastrar varios arquivos no portaArquivos.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war\ava\PortaArquivos\cadastrar_arquivos_lote.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws IOException
	 */
	public String cadastrarVarios() throws ArqException, IOException, NegocioException {
			
		GenericDAO dao = null;
		
		if (arquivosTurma.isEmpty()) {
			addMensagemErro("Adicione pelo menos um arquivo para enviar ao porta-arquivos.");
			return null;
		}

		
		TopicoAulaMBean topicoAula = (TopicoAulaMBean) getMBean("topicoAula");
		if (associarTurma && arquivoTurma.getAula().getId() == 0) {
			if (CollectionUtils.isEmpty(topicoAula.getComboIdentado()))
				addMensagemErro("Não há nenhum tópico de aula cadastrado. Por favor, cadastre um tópico de aula.");
			else
				addMensagemErro("Selecione um tópico de aula.");
			return null;
		}	

		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_ARQUIVOS);
			
			MovimentoArquivoTurma mov = new MovimentoArquivoTurma();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_ARQUIVOS);
			mov.setCadastrarEm(cadastrarEm);
			mov.setAssociarTurma(associarTurma);
			mov.setArquivosTurma(new ArrayList<ArquivoTurma>());
			mov.setArquivoTurma(arquivoTurma);
			
			
			for(ArquivoTurma at: arquivosTurma) {
				UploadedFile a = at.getArquivoUpload();
				ArquivoUsuario arquivoUsuario = new ArquivoUsuario();
				
				String fileName = a.getName();
				int pos = fileName.lastIndexOf("\\");
				fileName = fileName.substring(pos + 1);
				
				arquivoUsuario.setNome(fileName);
				arquivoUsuario.setTamanho(a.getSize());
				arquivoUsuario.setData(new Date());
				arquivoUsuario.setUsuario(getDonoPortaArquivos());
				
				if (obj.getPasta().getId() == -1)
					arquivoUsuario.setPasta(null);
				else
					arquivoUsuario.setPasta(obj.getPasta());
				
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, a.getBytes(), a.getContentType(), fileName);
				arquivoUsuario.setIdArquivo(idArquivo);
				
				at.setArquivo(arquivoUsuario);
				
				mov.getArquivosTurma().add(at);
				
				registrarAcao(arquivoUsuario.getNome(), EntidadeRegistroAva.ARQUIVO, AcaoAva.INICIAR_INSERCAO, true, arquivoUsuario.getId());
			}
	
			Notification n = (Notification) execute(mov, getCurrentRequest());
			
			if (n != null && n.hasMessages())
				return notifyView(n);
			
			if (n != null) {
				for (ArquivoTurma a: mov.getArquivosTurma()) {
					registrarAcao(a.getArquivo().getNome(), EntidadeRegistroAva.ARQUIVO, AcaoAva.INSERIR, n.getTurmasSucesso(), a.getArquivo().getId());
				}
			}
			
			dao = getGenericDAO();
			arquivoTurma.setAula(dao.refresh(arquivoTurma.getAula()));		
			Turma turma = dao.refresh(arquivoTurma.getAula().getTurma());
			
			if ( arquivoTurma.isNotificarAlunos() ) {		
				
				String mensagem = "Novos arquivos foram adicionados na turma virtual: " + turma.getDescricaoSemDocente() + "\n\nAcesse o SIGAA para proceder o download dos arquivos.";
				
				notificarTurmas(cadastrarEm, "Arquivos inseridos na turma " + turma.getDescricaoSemDocente()  , 
				mensagem, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);
			}
		}	
		catch (NegocioException e) {
			e.printStackTrace();
		}
		
		addMensagemInformation("Arquivos inseridos com sucesso.");
		return inserirVariosArquivosTurma(arquivoTurma.getAula().getId());
	}
	
	/**
	 * Remove um arquivo da lista de arquivos adicionados em lote.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war\ava\PortaArquivos\cadastrar_arquivos_lote.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String removerArquivoItem() {
		int index = getParameterInt("indice");
		arquivosTurma.remove(index);
		return null;
	}
	
	/**
	 * Verifica se o aquivo deve ser associado a uma turma.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war/ava/PortaArquivos/index.jsp</li>
	 * </ul>
	 *  
	 * @return the arquivoTurma
	 */
	public boolean isAssociarTurma() {
		return associarTurma;
	}

	public void setAssociarTurma(boolean associarTurma) {
		this.associarTurma = associarTurma;
	}

	public ArquivoTurma getArquivoTurma() {
		return arquivoTurma;
	}

	public void setArquivoTurma(ArquivoTurma arquivoTurma) {
		this.arquivoTurma = arquivoTurma;
	}

	/**
	 * Prepara o arquivo para ser associado a uma turma.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getAssociarATopico() throws DAOException {
			
		int id; 
		
		if ( getParameterInt("id") != null )
		{	
			id = getParameterInt("id");
			ehPortalDocente = false;
		}else
		{	
			id = Integer.parseInt(getCurrentSession().getAttribute("idArquivo").toString());
			ehPortalDocente = true;
		}
		if ( id != 0 )
		{	
			GenericDAO dao = getDAO(GenericSigaaDAO.class);
			associarTurma = true;
			ArquivoUsuario arquivo = dao.findByPrimaryKey(id, ArquivoUsuario.class);
			this.obj = arquivo;				
		}

		return "";
	}
	
	/**
	 * Caso o docente tenha iniciado o caso de uso através de um tópico de aula na turma virtual,
	 * associa o o novo arquivo ao tópico selecionado.
	 */
	public String getSelecionarTopicoInicial () {
		Object aux = getCurrentRequest().getSession().getAttribute("idTopicoSelecionado");
		Integer idTopicoSelecionado = null;
		
		if (aux != null){
			idTopicoSelecionado = (Integer) aux;
			
			ArquivoUsuarioMBean aBean = getMBean("arquivoUsuario");
			aBean.getArquivoTurma().getAula().setId(idTopicoSelecionado);
		}
				
		return "";
	}

	/**
	 * Retira um arquivo da turma virtual, mas não do porta-arquivos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/aulas.jsp
	 * @return
	 */
	public String removerAssociacaoArquivo() {

		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			
			int id = getParameterInt("id");
			this.arquivoTurma = new ArquivoTurma();
			MaterialTurma m = dao.findByExactField(MaterialTurma.class, "idMaterial", id, true);
			
			if (m == null) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			
			this.arquivoTurma.setMaterial(m);
			this.arquivoTurma.setId(id);

			registrarAcao(arquivoTurma.getNome(), EntidadeRegistroAva.ARQUIVO, AcaoAva.INICIAR_REMOCAO, arquivoTurma.getId());
			
			prepareMovimento(ArqListaComando.REMOVER);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(this.arquivoTurma);
			mov.setCodMovimento(ArqListaComando.REMOVER);

			execute(mov, getCurrentRequest());
			
			registrarAcao(arquivoTurma.getNome(), EntidadeRegistroAva.ARQUIVO, AcaoAva.REMOVER, arquivoTurma.getId());
			addMensagemInformation("Arquivo removido com sucesso.");
			
			TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
			return tBean.retornarParaTurma();
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch(Exception e) {
			addMensagemErro(e.getMessage());
			return null;
		} finally {
			if (dao != null)
				dao.close();
		} 
	}
	

	
	/**
	 * Cria um arquivo zip com todos os arquivos da turma. 
	 * @param turma
	 * @param aulas
	 */
	private void criarZipArquivosTopicos(Turma turma) {
			
		
		
		
		if (!arquivosTurma.isEmpty()) {
			
				String nomeArquivo =  turma.getNome()+".zip";
				nomeArquivo = nomeArquivo.replace(" ", "_");
				getCurrentResponse().setContentType("application/zip");
				getCurrentResponse().setCharacterEncoding("iso-8859-15");
				getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\""+nomeArquivo+"\"");
				
				registrarAcao("Todos os arquivos", EntidadeRegistroAva.ARQUIVO, AcaoAva.ACESSAR,arquivosTurma.get(0).getId());
								
				try {
					OutputStream out = getCurrentResponse().getOutputStream();
					ZipOutputStream zip = new ZipOutputStream(out);
					int idAulaAtual = arquivosTurma.get(0).getAula().getId();
					String nomePasta = StringUtils.toAscii(arquivosTurma.get(0).getAula().getDescricao()) + "/";
					 
					
					for(ArquivoTurma arquivoTurma : arquivosTurma) {	
							if (idAulaAtual != arquivoTurma.getAula().getId()) {
								idAulaAtual = arquivoTurma.getAula().getId();
								nomePasta = StringUtils.toAscii(arquivoTurma.getAula().getDescricao()) + "/";		
							}								
							
							String nome = turma.getNome()+"/"+nomePasta+arquivoTurma.getArquivo().getNome();
							zip.putNextEntry(new ZipEntry(StringUtils.toAscii(nome)));
							EnvioArquivoHelper.recuperaArquivo(zip, arquivoTurma.getArquivo().getIdArquivo());
							zip.closeEntry();
							registrarLogAcessoDiscenteTurmaVirtual(Arquivo.class.getName(), arquivoTurma.getArquivo().getIdArquivo(), turma().getId());
					}
					
					zip.close();
					FacesContext.getCurrentInstance().responseComplete();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
						
		
	else {
			addMensagemErro("Não há arquivos relacionados com as aulas nesta turma.");
	}
		
		
	}
	
	public List<ArquivoTurma> getArquivosTurma() {
		return arquivosTurma;
	}

	public void setArquivosTurma(List<ArquivoTurma> arquivos) {
		this.arquivosTurma = arquivos;
	}
	
	/**
	 * Exibe todos os vídeos da turma para um discente.<br/>
	 * Método não invocado por JSP(s)
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarArquivosDiscente() throws DAOException {
		popularArquivos();
		return forward("/ava/ArquivoTurma/listar_discente.jsp");
	}
	
	/**
	 * Popula os arquivos a serem listados ao usuário.
	 * 
	 * @throws DAOException
	 */
	private void popularArquivos() throws DAOException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		if (arquivosTurma == null){
			TurmaVirtualDao dao = null;
			
			try {
				dao = new TurmaVirtualDao();
				arquivosTurma = dao.findArquivosByTurma(turma().getId(),tBean.isPermissaoDocente());
				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}
	
	public ArquivoUsuario getObj() {
		return obj;
	}

	public void setObj(ArquivoUsuario obj) {
		this.obj = obj;
	}

	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}

	/**
	 * Faz com que a turma padrão esteja selecionada.
	 * @return
	 */
	public String getSelecionarTurmaPadrao() {
		Turma turma = null;
		try {
			turma = turma();
		} catch(RuntimeNegocioException e) {
			
		}
		
		if (cadastrarEmVariasTurmas() && turma != null) {
			cadastrarEm = new ArrayList<String>();
			cadastrarEm.add(String.valueOf(turma.getId()));
		}
		return null;
	}

	public int getTamanhoMaximoArquivo() {
		return tamanhoMaximoArquivo;
	}

	public void setTamanhoMaximoArquivo(int tamanhoMaximoArquivo) {
		this.tamanhoMaximoArquivo = tamanhoMaximoArquivo;
	}

	public void setEhPortalDocente(boolean ehPortalDocente) {
		this.ehPortalDocente = ehPortalDocente;
	}

	public boolean isEhPortalDocente() {
		return ehPortalDocente;
	}

}