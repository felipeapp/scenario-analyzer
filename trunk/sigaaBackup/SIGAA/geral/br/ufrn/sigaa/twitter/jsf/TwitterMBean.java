/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/07/2011
 *
 */
package br.ufrn.sigaa.twitter.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaTwitterDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.ava.dominio.TurmaTwitter;
import br.ufrn.sigaa.ava.jsf.CadastroTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.twitter.OAuthTwitter;

/**Classe que realiza as operacoes sobre o twitter
 * 
 * @author Eric Moura
 */
@Component
@Scope(value="session")
public class TwitterMBean extends CadastroTurmaVirtual <TurmaTwitter> {
	
	/** Link para a página de configuração do twitter. */
	public static final String PAGINA_CONFIGURACAO = "/ava/NoticiaTurma/novo_twitter.jsp";
	/** Link para a página de visualização do twitter. */
	public static final String PAGINA_VISUALIZAR_TWITTER = "/ava/NoticiaTurma/visualizar_twitter.jsp";
	
	/** Número de caracteres que a mensagem será abreviada - dando espaço para adicionar novas palavaras. */
	public static final int NUMERO_CARACTERES_TRUNCAMENTO_TWITTER = 129;
	/** Código da mensagem de erro de mensagem duplicada enviada pelo Twitter */
	public static final int ERRO_MSG_DUPLICADA = 187;
	
	/** Protocolo de autorização do twitter. */
	private OAuthTwitter oAuthTwitter;
	
	/** Mensagem que vai ser postada no twitter. */
	private String novoStatus;

	/** Token de acesso ao twitter. */
	private AccessToken accessToken;

	/** Twitter no qual as mensagens serão postadas. */
	private Twitter twitter;
	
	/** Notificar os alunos por e-mail sobre a criação do twitter. */
	private boolean notificar;
	
	/** Turma que está postando no twitter. */
	private Turma turma;
	
	/** Ids das turmas que terão mensagens cadastradas. */
	private List<String> alterarStatusPara;
	
	/** Armazena os dados do twitter da turma. */
	private TurmaTwitter turmaTwitterSelecionada;
	
	/** Nome do usuário do twitter. */
	private String usuario;
	
	/** Se o usuário está postando uma mensagem. */
	private boolean postando = false;
	
	/**
	 * Se configura para utilizar um twitter da turma passada por parâmetro.
	 * <br />
	 * Método não invocado por JSPs:
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public String novoTwitter (Integer idTurma) throws DAOException {
		if (idTurma == null)
			idTurma = getParameterInt("idTurma");
		else {
			List <String> turmas = new ArrayList <String> ();
			turmas.add("" + idTurma);
			setAlterarStatusPara(turmas);
		}
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			Turma turma = dao.findByPrimaryKey(idTurma, Turma.class);
			turmaTwitterSelecionada = dao.findByExactField(TurmaTwitter.class, "turma.id", idTurma, true);
			if(ValidatorUtil.isEmpty(turmaTwitterSelecionada))
				getCurrentRequest().getSession().setAttribute("turmaTwitter", turma);
		
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Envia uma mensagem pelo twitter da turma
	 * <br />
	 * Método invocado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/noticiaturma/novo_twitter.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String postar(){
		String retorno = null;
		try {
			
			if (StringUtils.isEmpty(novoStatus)){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Texto do Twitter");
				return null;
			}
			
			postando = true;
			registrarAcao(null, EntidadeRegistroAva.TWITTER, AcaoAva.INICIAR_ENVIO, turmaTwitterSelecionada.getId());			
			retorno = enviarNovoStatus();
			registrarAcao(null, EntidadeRegistroAva.TWITTER, AcaoAva.ENVIAR, turmaTwitterSelecionada.getId());
			postando = false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retorno;
	}
	

	/**
	 * Avalia se a turma atual possui twitter ou não
	 * 
	 */
	public boolean isPossuiTwitter() throws DAOException{
		if(ValidatorUtil.isEmpty(turmaTwitterSelecionada))
			return false;
		else
			return true;
	}
	
	/**
	 * Carrega os dados do twitter.
	 * <br />
	 * Método não invocado por JSPs:
	 */
	private Twitter carregarDadosTwitter (TurmaTwitter turmaTwitter){
		if(!ValidatorUtil.isEmpty(turmaTwitter)){
			accessToken = new AccessToken(turmaTwitter.getAccessToken(), turmaTwitter.getAccessSecret());
			return OAuthTwitter.carregarConfiguracao(accessToken);
		} else {
			return null;
		}
	}
	
	/**
	 * Reinicia o twitter com a turma atual.
	 * <br />
	 * Método invocado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/noticiaturma/novo_twitter.jsp</li>
	 *  <li>/ava/noticiaturma/visualizar_twitter.jsp</li>
	 * </ul>
	 */
	public String getReiniciarTwitter() throws DAOException{
		init();
		novoTwitter(turma().getId());
		return null;
	}
	
	/**
	 * Redireciona para página de congigurações do twitter.
	 * <br />
	 * Método não invocado por JSPs:
	 */
	public String acessarConfiguracoes () throws DAOException {
		return forward(PAGINA_CONFIGURACAO);
	}
	
	/**
	 * Redireciona para página de visualização do twitter.
	 * <br />
	 * Método não invocado por JSPs:
	 */
	public String visualizarDiscente () {
		return forward (PAGINA_VISUALIZAR_TWITTER);
	}
	
	/**
	 * Envia o docente à página de autorização do twitter para associar sua conta à turma virtual.
	 * Método não invocado por JSPs
	 * @return
	 * @throws TwitterException 
	 */
	public String associarTwitter () throws TwitterException {
		return redirectSemContexto(getEnderecoAutenticacao());
	}
	
	/**
	 * Verifica se a turma virtual possui Twitter
	 * @return
	 * @throws TwitterException 
	 */
	public boolean isVerificaTurmaTemTwitter(){
		boolean retorno = false;
		TurmaVirtualMBean turmaVirtualMBean = getMBean("turmaVirtual");
		Turma turma = turmaVirtualMBean.getTurma();
		GenericDAOImpl dao = getDAO(GenericDAOImpl.class);
		
		try{
			TurmaTwitter turmaTwitter = dao.findByExactField(TurmaTwitter.class,"turma.id",turma.getId(), true);
			if(!ValidatorUtil.isEmpty(turmaTwitter)){
				turmaTwitterSelecionada = turmaTwitter;
				retorno = true;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			dao.close();
		}
		
		return retorno;
	}
	
	/**
	 * Retorna o nome do usuário do Twitter
	 * Método invocado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/noticiaturma/novo_twitter.jsp</li>
	 * </ul>
	 * @return
	 * @throws TwitterException 
	 */
	public String getUsuarioTwitter() throws IllegalStateException, TwitterException{
		if (usuario == null){
			Twitter t = null;
			if (accessToken != null)
				t = OAuthTwitter.carregarConfiguracao(accessToken);
			else if (turmaTwitterSelecionada !=null)
				t = carregarDadosTwitter(turmaTwitterSelecionada);
			else 
				return null;
			User u = t.showUser(t.getId());
			usuario = u.getScreenName();
		}
		return usuario;
	}

	public TwitterMBean(){
		init();
	}
	
	/** 
	 * Inicializa o bean.
	 * Método não invodado por JSPs
	 */
	public void init(){
		oAuthTwitter = new OAuthTwitter();
		alterarStatusPara = new ArrayList<String>();
		notificar = false;
		novoStatus ="";
	}
	
	/**
	 * Remove o twitter da turma virtual.
	 * <br />
	 * Método invocado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/noticiaturma/novo_twitter.jsp</li>
	 * </ul>
	 */
	public String removerTurmaTwitter(){
		TurmaVirtualMBean turmaVirtualMBean = getMBean("turmaVirtual");
		Turma turma = turmaVirtualMBean.getTurma();
		GenericDAOImpl dao = getDAO(GenericDAOImpl.class);
		
		try{
			TurmaTwitter turmaTwitter = dao.findByExactField(TurmaTwitter.class,"turma.id",turma.getId(), true);
			if(!ValidatorUtil.isEmpty(turmaTwitter)){
				accessToken = null;
				turmaTwitterSelecionada = null;
				prepareMovimento(ArqListaComando.REMOVER);
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setCodMovimento(ArqListaComando.REMOVER);
				mov.setObjMovimentado(turmaTwitter);
				registrarAcao(null, EntidadeRegistroAva.TWITTER, AcaoAva.INICIAR_REMOCAO, turmaTwitter.getId());			
				execute(mov);
				registrarAcao(null, EntidadeRegistroAva.TWITTER, AcaoAva.REMOVER, turmaTwitter.getId());
				addMensagemInformation("O Twitter foi descadastrado com sucesso");
			}
			
			usuario = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return forward("/ava/index.jsf");
	}
	
	/**
	 * Retorna o endereço da página de autenticação do twitter para que o usuário permita
	 * a utilização de sua conta pelo aplicativo do Sigaa.
	 * 
	 * @return
	 * @throws TwitterException
	 */
	public String getEnderecoAutenticacao() throws TwitterException{
		TurmaVirtualMBean turmaVirtualMBean = getMBean("turmaVirtual");
		Turma turma = turmaVirtualMBean.getTurma();
		RequestToken requestToken = oAuthTwitter.getTwitter().getOAuthRequestToken();
		getCurrentRequest().getSession().setAttribute("twitter", oAuthTwitter.getTwitter());
		getCurrentRequest().getSession().setAttribute("turmaTwitter", turma);
		getCurrentRequest().getSession().setAttribute("requestToken", requestToken);
		getCurrentRequest().getSession().setAttribute("registroEntrada", getRegistroEntrada());
		return requestToken.getAuthorizationURL();
	}
	
	/**
	 * Envia o usuário à tela de gerenciar as permissões do twitter.
	 * <br />
	 * Método invocado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/noticiaturma/novo_twitter.jsp</li>
	 * </ul>
	 * @return
	 * @throws IOException
	 * @throws TwitterException
	 */
	public String acessarEnderecoAutenticacao () throws IOException, TwitterException {
		getCurrentResponse().sendRedirect(getEnderecoAutenticacao());
		return null;
	}

	/**
	 * Redireciona para página de testes do twitter.
	 * <br />
	 * Método não invocado por JSPs:
	 */
	public String paginaTesteTwitter(){
		return forward("/geral/twitter/twitter_test.jsp");
	}
	
	/**
	 * Posta uma nova mensagem da turma no twitter.
	 * <br />
	 * Método não invocado por JSPs:
	 */
	public String enviarNovoStatus() throws DAOException {
		if(!ValidatorUtil.isEmpty(novoStatus)){
			try {

				abreviarStatus();	
				
				ArrayList<Integer> idsTurma = new ArrayList<Integer>(); 
				for(String tid: alterarStatusPara){
					Integer idTurma = Integer.valueOf(tid);
					idsTurma.add(idTurma);
				}
				
				TurmaTwitterDao dao = getDAO(TurmaTwitterDao.class);
				ArrayList<TurmaTwitter> turmasTwitter = (ArrayList<TurmaTwitter>) dao.findTurmasTwitterByIds(idsTurma);

				if (turmasTwitter != null && !turmasTwitter.isEmpty()){
					for(TurmaTwitter turmaTwitter: turmasTwitter){
						Twitter twitterTurma = carregarDadosTwitter(turmaTwitter);
						if (twitterTurma != null)
							twitterTurma.updateStatus(novoStatus);
						novoStatus = "";
					}
					
					addMensagemInformation("Mensagem enviada para o Twitter com sucesso.");
				}
			} catch (TwitterException e) {
				boolean hasError = false;
				if (e.getErrorCode() == ERRO_MSG_DUPLICADA){
					addMensagemErro("A mensagem está duplicada.");
					hasError = true;
				}
				if (!hasError)
					addMensagemErro("Twitter indisponível no momento.");
				postando = false;
			}
		}
		return null;
	}
	
	/**
	 * Truca a mensagem do Twitter.
	 * <br />
	 * Método não invocado por JSPs:
	 */
	private void abreviarStatus() {
		if ( novoStatus.charAt(novoStatus.length()-1) == '.' ){				
			novoStatus = StringUtils.abbreviate(novoStatus,TwitterMBean.NUMERO_CARACTERES_TRUNCAMENTO_TWITTER);
			novoStatus += " Via #SIGAA";				
		}	
		else {
			novoStatus = StringUtils.abbreviate(novoStatus,TwitterMBean.NUMERO_CARACTERES_TRUNCAMENTO_TWITTER-1);
			novoStatus += ". Via #SIGAA";
		}
	}
	
	/**
	 * Método de teste que lista os status do twitter.
	 * <br />
	 * Método invocado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/geral/twitter/twitter_test.jsp</li>
	 * </ul>
	 */
	public List<Status> getListaStatus() {
		List<Status> statuses = null;
		if(accessToken!=null){
			try {
				if(twitter!=null){
					statuses = twitter.getHomeTimeline();
				}
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			System.out.println("timeline:");
			if (statuses != null)
				for (Status status : statuses) {
					System.out.println(status.getUser().getName() + ":"
							+ status.getText());
				}
		}
		return statuses;
	}
	
	/**
	 * Lista as turma com permissão no twitter.
	 * <br />
	 * Método invocado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/Noticia/_form.jsp</li>
	 * </ul>
	 */
	public List<Turma> getTurmasPermissaoTwitter() throws ArqException {
		
		TurmaVirtualDao dao = null;
		
		try {
			dao = getDAO(TurmaVirtualDao.class);
			List<Turma> turmas = new ArrayList<Turma>();
			// Usuario pode ter permissão sendo um discente
			if ( getUsuarioLogado().getServidor() != null )
				 turmas = (List<Turma>) getDAO(TurmaDao.class).findByDocente(getUsuarioLogado().getServidor(), null, null, null, null, SituacaoTurma.ABERTA);
			
			// Turmas que o usuário possue permissão
			List<Turma> turmasSemelhantes = dao.findTurmasPermitidasByPessoa (getUsuarioLogado().getPessoa(), PermissaoAva.DOCENTE);
			
			if ( turmasSemelhantes != null )
				for ( Turma t : turmasSemelhantes )
					if ( !turmas.contains(t) )
						turmas.add(t);
			
			// Caso a turma não esteja aberta
			if ( !turmas.contains(turma) && !ValidatorUtil.isEmpty(turma))
				turmas.add(turma);
			
			List<Turma> turmasSemTwitter = new ArrayList<Turma>();
			for(Turma turmaAtual : turmas){
				if(!ValidatorUtil.isEmpty(turmaAtual)){
					TurmaTwitter turmaTwitter = dao.findByExactField(TurmaTwitter.class,"turma.id",turmaAtual.getId(), true);
					if(ValidatorUtil.isEmpty(turmaTwitter)){
						turmasSemTwitter.add(turmaAtual);
					}
				}
			}
			
			turmas.removeAll(turmasSemTwitter);
			turmas.remove(null);
			
			Collections.sort(turmas, new Comparator<Turma>(){
				public int compare(Turma t1, Turma t2) {
					int retorno = 0;
					retorno = t2.getAno() - t1.getAno();
					if( retorno == 0 ) {
						retorno = t2.getPeriodo() - t1.getPeriodo();
						if ( retorno == 0 ){
							String nome = StringUtils.toAscii(t1.getDescricaoSemDocente());
							return nome.compareToIgnoreCase(StringUtils.toAscii( t2.getDescricaoSemDocente() ));
						}
					}	
					return retorno;
				}
			});
			
			return turmas;
		}finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	public String getNovoStatus() {
		return novoStatus;
	}
	
	public void setNovoStatus(String novoStatus) {
		this.novoStatus = novoStatus;
	}


	public TurmaTwitter getTurmaTwitterSelecionada() {
		return turmaTwitterSelecionada;
	}


	public void setTurmaTwitterSelecionada(TurmaTwitter turmaTwitterSelecionada) {
		this.turmaTwitterSelecionada = turmaTwitterSelecionada;
	}

	public boolean isNotificar() {
		return notificar;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}

	public List<String> getAlterarStatusPara() {
		return alterarStatusPara;
	}

	public void setAlterarStatusPara(List<String> alterarStatusPara) {
		this.alterarStatusPara = alterarStatusPara;
	}

	@Override
	public List<TurmaTwitter> lista() {
		return null;
	}
	
	/**
	 * Envia erro caso o twitter estiver fora do ar.
	 * <br />
	 * Método invocado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/noticiaturma/novo_twitter.jsp</li>
	 * </ul>
	 * @return
	  */
	public String getVerificaErro () throws IOException, TwitterException {
		Boolean erro = (Boolean) getCurrentSession().getAttribute("erro");
		if ( erro != null && erro ) {
			getCurrentSession().setAttribute("erro",false);
			addMensagemErro("Twitter indisponível no momento.");
			return redirectJSF(getSubSistema().getLink());
		}
		return null;
	}

	public void setPostando(boolean postando) {
		this.postando = postando;
	}

	public boolean isPostando() {
		return postando;
	}
}
