/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */

package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.URL_INVALIDA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.validator.UrlValidator;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.VideoTurmaDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.dominio.VideoTurma;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroVideoTurma;
import br.ufrn.sigaa.ava.negocio.RegistroAtividadeAvaHelper;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * MBean que gerencia os questionários da turma virtual.
 * 
 * @author Fred_Castro
 *
 */

@Component("videoTurma")
@Scope("request")
public class VideoTurmaMBean extends ControllerTurmaVirtual {
	
	/** Define o valor para enviar um vídeo do computador. */
	public static final char VIDEO_INTERNO = 'I';
	/** Define o valor para enviar um vídeo externo. */
	public static final char VIDEO_EXTERNO = 'E';
	/** Define o valor para enviar um vídeo do porta-arquivos. */
	public static final char VIDEO_PORTA_ARQUIVOS = 'P';
	
	/** O objeto que está sendo gerenciado atualmente. */
	private VideoTurma video;
	
	/** A lista contendo todos o vídeos da turma. */
	private List <VideoTurma> videos;
	
	/** O identificador do tópico de aula no qual o vídeo está sendo cadastrado. */
	private Integer idTopicoAula;
	
	/** Indica se, após realizar a operação selecionada, é para voltar à turma virtual. */
	private boolean voltarATurma;
	
	/** Armazena o arquivo do vídeo enviado. */
	private UploadedFile arquivoEnviado;
		
	/** ID de um arquivo do porta-arquivo. */
	private Integer idArquivoPA;
	
	/** Indica se o docente optou por enviar um vídeo de seu computador. */
	private Character enviar;
	
	/** Se deve ser enviado um e-mail para os alunos ao cadastrar um novo vídeo. */
	private boolean notificarAlunos = true;
	
	/** Indica o nome do arquivo do porta-arquivos */
	private String nomeArquivo;
	
	/**
	 * Monta uma lista de SelectItems representando as opções de resoluções de vídeo disponíveis para exibição.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/VideoTurma/form.jsp</li>
	 * </ul> 
	 */
	public List <SelectItem> getResolucoesCombo () {
		
		List <SelectItem> rs = new ArrayList <SelectItem> ();
		
		for (Integer i : getResolucoes().keySet())
			rs.add(new SelectItem(""+i, ""+getResolucoes().get(i) + " x " + i));
		
		return rs;
	}
	
	/**
	 * Inicia o caso de uso de cadastrar um novo vídeo em um tópico de aula.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/topico_aula.jsp</li>
	 * </ul> 
	 * 
	 * @param idTopicoAula
	 * @return
	 * @throws ArqException
	 */
	public String novoVideo (int idTopicoAula) throws ArqException {
		this.idTopicoAula = idTopicoAula;
		voltarATurma = true;
		return novoVideo();
	}
	
	/**
	 * Inicia o caso de uso de cadastrar um novo vídeo.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/TopicoAula/listar.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String novoVideo () throws ArqException {
		
		video = new VideoTurma();
		// Configura a altura Padrão
		video.setAltura(280);
		if (idTopicoAula != null)
			video.setTopicoAula(new TopicoAula(idTopicoAula));
		
		if (cadastrarEmVariasTurmas()) {
			cadastrarEm = new ArrayList<String>();
			cadastrarEm.add(String.valueOf(turma().getId()));
		}
		
		clean();		
		getCurrentRequest().getSession().setAttribute("cadastrarVideoTurma",true);		

		prepareMovimento(SigaaListaComando.CADASTRAR_VIDEO_TURMA);
		
		// Por padrão, solicita que o usuário envie o vídeo.
		enviar = VIDEO_INTERNO;
		return formularioNovoVideo();
	}
	
	/**
	 * Inicia o caso de uso de alterar um vídeo existente.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/VideoTurma/listar.jsp</li>
	 * 		<li>sigaa.war/ava/topico_aula.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String alterarVideo () throws ArqException {
		GenericDAO dao = null;

		try {
			dao = getGenericDAO();
			int idVideo = getParameterInt("idVideo", 0);
			voltarATurma = getParameterBoolean("voltarATurma");
			
			if (idVideo > 0){
				video = dao.findByPrimaryKey(idVideo, VideoTurma.class);
			} 
			
			if(video == null || !video.isAtivo()) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return forward("/ava/VideoTurma/listar.jsp");
			}
			
			clean();
			getCurrentRequest().getSession().setAttribute("cadastrarVideoTurma",false);	
			
			prepareMovimento(SigaaListaComando.CADASTRAR_VIDEO_TURMA);
			
			if (video.getTopicoAula() == null)
				video.setTopicoAula(new TopicoAula());
			if (video.getMaterial() != null)
				video.setMaterial(dao.refresh(video.getMaterial()));
				
			boolean interno = video.getIdArquivo() != null || video.getIdArquivoConvertido() != null;
			if (interno)
				enviar = VIDEO_INTERNO;
			else
				enviar = VIDEO_EXTERNO;
				
			return formularioEditarVideo();
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	/**
	 * Limpa as variáveis de sessão.
	 * JSP: Não é chamado por JSPs.
	 * 
	 * @return
	 * @throws ArqException
	 */
	private void clean() {
		getCurrentRequest().getSession().setAttribute("videoTurmaSelecionado",null);
		getCurrentRequest().getSession().setAttribute("idArquivo",null);
		idArquivoPA = null;
		nomeArquivo = null;
	}
	
	/**
	 * Salva o vídeo que está sendo gerenciado atualmente, seja ele novo ou já existente.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/VideoTurma/form.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String salvarVideo () throws ArqException {
		
		boolean erro = false;
		boolean alterando = false;
		
		if (StringUtils.isEmpty(video.getTitulo())){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");
			erro = true;
		}
		
		if (video.getId() == 0 && (enviar == VIDEO_INTERNO) && (arquivoEnviado == null || arquivoEnviado.getSize() == 0)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Arquivo com o vídeo");
			 erro = true;
		}
		
		if (video.getId() == 0 && (enviar == VIDEO_PORTA_ARQUIVOS) && (idArquivoPA == null || idArquivoPA == 0)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Arquivo com o vídeo");
			 erro = true;
		}
		
		if (enviar == VIDEO_EXTERNO && StringUtils.isEmpty(video.getLink())){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Endereço do Vídeo");
			erro = true;
		}
		
		if (enviar == VIDEO_EXTERNO && StringUtils.isNotEmpty(video.getLink())){
			if (!new UrlValidator().isValid(video.getLink())){
				addMensagem(URL_INVALIDA, "Link");
				erro = true;			
			}
		}
		
		if (video.getTopicoAula().getId() <= 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tópico de Aula");
			erro = true;
		}
		
		if (!erro){
			GenericDAO dao = null;
			
			try {
				dao = getGenericDAO();
				
				if (video.getTopicoAula() != null && video.getTopicoAula().getId() == -1)
					video.setTopicoAula(null);
				
				video.setMaterial(dao.refresh(video.getMaterial()));
				video.setTurma(turma());
				
				MovimentoCadastroVideoTurma mov = new MovimentoCadastroVideoTurma (video, arquivoEnviado);
				// Quando o vídeo é enviado através do porta-arquivos.
				mov.setIdArquivoPA(idArquivoPA);
				mov.setEnviar(enviar);
				
				if (video.getId() > 0){
					registrarAcao(video.getTitulo(), EntidadeRegistroAva.VIDEO, AcaoAva.INICIAR_ALTERACAO, video.getId());
					alterando = true;
				} else {
					registrarAcao(video.getTitulo(), EntidadeRegistroAva.VIDEO, AcaoAva.INICIAR_INSERCAO);
					mov.setCadastrarEm(cadastrarEm);
					mov.setCadastrarEmVariasTurmas(cadastrarEmVariasTurmas());
				}
				execute(mov);
				
				if (alterando)
					registrarAcao(video.getTitulo(), EntidadeRegistroAva.VIDEO, AcaoAva.ALTERAR, video.getId());
				else{
					registrarAcao(video.getTitulo(), EntidadeRegistroAva.VIDEO, AcaoAva.INSERIR, video.getId());
					RegistroAtividadeAvaHelper.getInstance().registrarAtividade(video.getTurma(), video.getMensagemAtividade());
				}
					
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				
				if (notificarAlunos){
					
					String conteudo = null;
					String assunto = null;
					
					if (alterando){
						conteudo = "Um vídeo foi alterado na turma <b>" + turma() + "</b> do SIGAA"; 
						assunto = "Vídeo Alterado: " + video.getTitulo() + " - " + turma();
					}else{
						conteudo = "Um novo vídeo foi cadastrado na turma <b>" + turma() + "</b> do SIGAA"; 
						assunto = "Novo Vídeo Cadastrado: " + video.getTitulo() + " - " + turma();
					}
					
					// String mensagem = "Uma nova tarefa foi adicionada à turma.\nAcesse o SIGAA para visualizá-la.";
					notificarTurmas(cadastrarEm, assunto , conteudo, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA );
				}
				
				videos = null;
				clean();
				
				if (voltarATurma){
					TurmaVirtualMBean tBean = getMBean("turmaVirtual");
					return tBean.entrar();
				}
					
				
				return listarVideos();
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		if ( alterando )
			return formularioEditarVideo();
		else
			return formularioNovoVideo();

	}
	
	/**
	 * Remove o vídeo selecionado
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/VideoTurma/listar.jsp</li>
	 *	 	<li>sigaa.war/ava/topico_aula.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String remover () throws ArqException {
		int idVideo = getParameterInt("id", 0);
		if (idVideo ==  0) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;			
		}else {
			try {
				prepareMovimento(SigaaListaComando.INATIVAR_VIDEO_TURMA);
				video = getGenericDAO().findByPrimaryKey(idVideo, VideoTurma.class);

				MovimentoCadastroVideoTurma mov = new MovimentoCadastroVideoTurma();
				mov.setCodMovimento(SigaaListaComando.INATIVAR_VIDEO_TURMA);
				mov.setVideo(video);
				registrarAcao(video.getTitulo(), EntidadeRegistroAva.VIDEO, AcaoAva.INICIAR_REMOCAO, video.getId());
				execute(mov);
				registrarAcao(video.getTitulo(), EntidadeRegistroAva.VIDEO, AcaoAva.REMOVER, video.getId());
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Vídeo");
				videos = null;
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			}
			
			if (getParameterBoolean("voltarATurma")){
				TurmaVirtualMBean tBean = getMBean("turmaVirtual");
				return tBean.entrar();
			}

			return listarVideos();
		}
	}
	
	/**
	 * Exibe um vídeo.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 *	 	<li>sigaa.war/ava/topico_aula.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String exibir () throws DAOException {
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			video = dao.findByPrimaryKey(getParameterInt("id", 0), VideoTurma.class);
			
			if(!video.isAtivo()) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return forward("/ava/VideoTurma/listar.jsp");
			}
			
			registrarAcao(video.getTitulo(), EntidadeRegistroAva.VIDEO, AcaoAva.ACESSAR, video.getId());
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/ava/VideoTurma/exibir.jsp");
	}
	
	/**
	 * Exibe todos os vídeos da turma.
	 * <br/><br/>Método chamado invocado por JSP(s):
	 * É public porque é acessado no método acessarCadastrarVideoAula da classe MenuTurmaMBean 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarVideos () throws DAOException {
		popularVideos();
		
		return forward("/ava/VideoTurma/listar.jsp");
	}
	
	/**
	 * Exibe todos os vídeos da turma para um discente.<br/>
	 * Método não invocado por JSP(s)
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarVideosDiscente() throws DAOException {
		popularVideos();
		
		return forward("/ava/VideoTurma/listar_discente.jsp");
	}
	
	/**
	 * Popula os vídeos a serem listados ao usuário.
	 * 
	 * @throws DAOException
	 */
	private void popularVideos() throws DAOException {
		if (videos == null){
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			VideoTurmaDao dao = null;
			
			try {
				dao = new VideoTurmaDao();
				videos = dao.findVideosByTurma(turma(), tBean.isPermissaoDocente());				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}
	
	/**
	 * Se for um discente vendo o vídeo, registra o Log dessa ação.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ui>
	 * 	<li>/ava/aulas.jsp</li>
	 * 	<li>/ava/VideoTurma/exibir.jsp</li>
	 * 	<ul>			 		
	 * @throws DAOException 
	 */
	public void verVideoPortalPrincipal() throws DAOException {
		Integer id = getParameterInt("id", 0);
        registrarLogAcessoDiscenteTurmaVirtual(VideoTurma.class.getName(), id, turma().getId());
	}
	
	/**
	 * Encaminha o usuário para o formulário de cadastrar um vídeo.
	 * @return
	 */
	private String formularioNovoVideo () {
		return forward("/ava/VideoTurma/novo.jsp");
	}
	
	/**
	 * Encaminha o usuário para o formulário de alterar um vídeo.
	 * @return
	 */
	private String formularioEditarVideo () {
		return forward("/ava/VideoTurma/editar.jsp");
	}

	public VideoTurma getVideo() {
		return video;
	}

	public void setVideo(VideoTurma video) {
		this.video = video;
	}

	public List<VideoTurma> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoTurma> videos) {
		this.videos = videos;
	}

	public UploadedFile getArquivoEnviado() {
		return arquivoEnviado;
	}

	public void setArquivoEnviado(UploadedFile arquivoEnviado) {
		this.arquivoEnviado = arquivoEnviado;
	}

	public Character getEnviar() {
		return enviar;
	}

	public void setEnviar(Character enviar) {
		this.enviar = enviar;
	}

	public Map<Integer, Integer> getResolucoes() {
		return VideoTurma.getResolucoes();
	}
	
	/**
	 * Indica se o material a ser cadastrado pode ser cadastrado em várias
	 * turmas do mesmo componente curricular.
	 * JSP: Não é chamado por JSPs.
	 * @return
	 */
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}

	public void setNotificarAlunos(boolean notificarAlunos) {
		this.notificarAlunos = notificarAlunos;
	}

	public boolean isNotificarAlunos() {
		return notificarAlunos;
	}
	
	/**
	 * Retorna as possíveis formas de lançar notas para um questionário que foi respondido mais de uma vez.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/ava/VideoTurma/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getComboEnviar() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(Character.valueOf(VIDEO_INTERNO), "Enviar do seu Computador"));
		result.add(new SelectItem(Character.valueOf(VIDEO_EXTERNO), "Link externo (Ex: YouTube)"));
		result.add(new SelectItem(Character.valueOf(VIDEO_PORTA_ARQUIVOS), "Enviar do Porta-Arquivos"));
		return result;
	}
	
	/**
	 * Prepara o plano de curso para ser importado para turma.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/ava/VideoTurma/form.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public void entrarPortaArquivos (ActionEvent e) throws DAOException {
		
		if (enviar == VIDEO_PORTA_ARQUIVOS){
			getCurrentRequest().getSession().setAttribute("videoTurmaSelecionado",video);
			redirectJSF("/ava/PortaArquivos/view.jsp");
		}
	}
	
	/**
	 * Seleciona uma turma, entra na turma selecionada e redireciona para o porta arquivos.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war/ava/PortaArquivos/view.jsp</li>
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
			
			video = new VideoTurma();
			// Configura a altura Padrão
			video.setAltura(280);
			if (idTopicoAula != null)
				video.setTopicoAula(new TopicoAula(idTopicoAula));		
			if (cadastrarEmVariasTurmas()) {
				cadastrarEm = new ArrayList<String>();
				cadastrarEm.add(String.valueOf(turma().getId()));
			}
			
			getCurrentRequest().getSession().setAttribute("cadastrarVideoTurma",true);		
			prepareMovimento(SigaaListaComando.CADASTRAR_VIDEO_TURMA);
			
			// Por padrão, solicita que o usuário envie o vídeo.
			enviar = VIDEO_PORTA_ARQUIVOS;
			return formularioNovoVideo();

		}finally{
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Prepara o arquivo para ser associado a um vídeo.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/ava/VideoTurma/form.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public String getAssociarAVideo() throws ArqException {
			
		String idArquivoAux = (String) getCurrentRequest().getSession().getAttribute("idArquivo");
		Integer idArquivo = null;
		if (idArquivoAux!=null)
			idArquivo = Integer.parseInt(idArquivoAux);
		
		VideoTurma videoAntigo = (VideoTurma) getCurrentRequest().getSession().getAttribute("videoTurmaSelecionado");
		
		if (videoAntigo!=null)
			video = videoAntigo;
		
		//Direto pelo porta-arquivos
		if (video == null){
			video = new VideoTurma();
			video.setAltura(280);
			prepareMovimento(SigaaListaComando.CADASTRAR_VIDEO_TURMA);
		}
		
		if ( idArquivo != null && idArquivo != 0 ){	
			GenericDAO dao = getDAO(GenericSigaaDAO.class);
			ArquivoUsuario arquivo = dao.findByPrimaryKey(idArquivo, ArquivoUsuario.class);
			enviar = VIDEO_PORTA_ARQUIVOS;
			cadastrarEm = new ArrayList<String>();
			cadastrarEm.add(String.valueOf(turma().getId())); 
			nomeArquivo = arquivo.getNome();
			idArquivoPA = arquivo.getIdArquivo();
			prepareMovimento(SigaaListaComando.CADASTRAR_VIDEO_TURMA);
		}

		return "";
	}
	
	public boolean isEnviarPortaArquivos () {
		return enviar != null && enviar == VIDEO_PORTA_ARQUIVOS;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setIdArquivoPA(Integer idArquivoPA) {
		this.idArquivoPA = idArquivoPA;
	}

	public Integer getIdArquivoPA() {
		return idArquivoPA;
	}
	
}