/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.cv.dao.MembroComunidadeDao;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;
import br.ufrn.sigaa.cv.dominio.SolicitacaoParticipacaoComunidade;
import br.ufrn.sigaa.cv.dominio.TipoComunidadeVirtual;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean respons�vel por buscar comunidades virtuais de 
 * acordo com o nome da comunidade e seu tipo.
 * 
 * @author Agostinho
 *
 */
@Component("buscarComunidadeVirtualMBean")
@Scope(value="session")
public class BuscarComunidadeVirtualMBean extends SigaaAbstractController<ComunidadeVirtual> {

	/** Possui a lista das comunidades virtuais. */
	private Collection<ComunidadeVirtual> listaComunidades;
	
	/** Possui a lista dos tipos das comunidades virtuais. */
	private Collection<SelectItem> allTiposComunidades;
	
	/** Define o tipo da comunidade a ser buscada. **/
	private Integer idTipoComunidadeVirtual;
	
	/** Define o nome ou palavra-chave da comunidade a ser buscada. **/
	private String nomeComunidade;
	
	/** Define a comunidade selecionada na busca. */
	private ComunidadeVirtual comunidade;
	
	/** Define o id da comunidade selecionada na busca. **/
	private String idComunidade;

	/** Lista de comunidades que uma pessoa est� associada */
	private Collection<ComunidadeVirtual> comunidadesPorPessoa;
	
	/** C�digo digitado pelo usu�rio para checar se este pode ingressar na comunidade. */
	private String codigoAcesso;
	/**
	 * Inst�ncia o dom�nio e os tipos de comunidade dispon�veis.
	 * @throws DAOException 
	 * @throws DAOException 
	 */
	public BuscarComunidadeVirtualMBean() throws DAOException {
		clear();
	}
	
	/**
	 * Redireciona para p�gina de busca de comunidades.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * 	<li>/sigaa.war/portais/docente/docente.jsp</li>
	 * </ul>
	 *  
	 * @param e
	 * @throws DAOException 
	 */
	public void criar(ActionEvent e) throws DAOException {
		clear();
		forward("/cv/buscar_comunidade.jsp");
	}
	
	/**
	 * Inst�ncia o dom�nio e os tipos de comunidade dispon�veis.
	 * @throws DAOException 
	 */
	private void clear() throws DAOException {
		obj = new ComunidadeVirtual();
		listaComunidades = new ArrayList<ComunidadeVirtual>();
		comunidade = new ComunidadeVirtual();
		List<TipoComunidadeVirtual> listaTemp = getDAO(ComunidadeVirtualDao.class).findTiposComunidadeVirtual(false);
		allTiposComunidades = toSelectItems(listaTemp, "id", "descricao");
	}
	
	/**
	 * Realiza as buscas das comunidades virtuais.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/buscar_comunidade.jsp
	 * @throws Exception 
	 */
	@Override
	public String buscar() throws Exception {
		
		// Reinicia os dados da busca.
		PagingInformation paging = getMBean("paginacao");
		paging.setPaginaAtual(0);
		
		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class);
		if (nomeComunidade == null) 
			nomeComunidade = "";
		
		listaComunidades = dao.findComunidadesByDescricaoTipo(nomeComunidade, obj.getTipoComunidadeVirtual().getId(), getPaginacao(), null);
		return null;
	}
	
	@Override
	public Collection<ComunidadeVirtual> getAllPaginado() throws ArqException {
		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class);
		try {	
				listaComunidades = dao.findComunidadesByDescricaoTipo(nomeComunidade, obj.getTipoComunidadeVirtual().getId(), getPaginacao(), null);
		} catch (Exception e) {
			notifyError(e);
		}
		return listaComunidades;
	}
	
	
	
	private void notificarUsuarios(String assunto, String texto, List<Pessoa> pessoas) {
		
		String cabecalho = new String();
		String mensagem = new String();
		//Para cada pessoa da lista, envia um email.
		for ( Pessoa p : pessoas ) {
			if ( p != null ){
				cabecalho = "<p> Caro(a) "+ p.getNome().toUpperCase() + ",<br><br>";
				mensagem = cabecalho+texto;
				
				MailBody body = new MailBody();
					
				body.setAssunto(assunto);
				body.setMensagem(mensagem);
				body.setFromName("SIGAA - Comunidade Virtual");
				body.setEmail(p.getEmail());
				body.setContentType(MailBody.HTML);
				Mail.send(body);
			}	
		}
	}
	
	
	/**
	 * Permite um usu�rio solicitar sua participa��o ao moderador 
	 * de uma comunidade do tipo Moderada.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/buscar_comunidade.jsp
	 * 
	 * @param evt
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String solicitarParticipacaoComunidadeVirtual(ActionEvent evt) throws SegurancaException, ArqException, NegocioException {
	
		MembroComunidadeDao mcDao = getDAO(MembroComunidadeDao.class);
		
		try {
			prepareMovimento(ArqListaComando.CADASTRAR);
			
			SolicitacaoParticipacaoComunidade particCV = new SolicitacaoParticipacaoComunidade();
		
			ComunidadeVirtual comunidadeVirtual = comunidade;
			
			if (evt != null)
				comunidadeVirtual = getComunidadeVirtualByID(getIDComunidadeVirtualFromView(evt));
			
			particCV.setComunidadeVirtual(comunidadeVirtual);
			particCV.setUsuario(getUsuarioLogado());
			
			// Checa se o usu�rio j� � participante da comunidade
			boolean usuarioExistente =  mcDao.findPartipanteComunidade(getUsuarioLogado().getPessoa().getId(), comunidadeVirtual.getId());
			
			// Se n�o for, checa se j� solicitou cadastro.
			if (!usuarioExistente)
				usuarioExistente = mcDao.findSolicitacoesByUsuario(getUsuarioLogado().getId(), comunidadeVirtual.getId());
	
			if (!usuarioExistente) {
				MovimentoCadastro movCad = new MovimentoCadastro();
				movCad.setObjMovimentado(particCV);
				movCad.setCodMovimento(ArqListaComando.CADASTRAR);
				execute(movCad);
				
				addMessage("Participa��o solicitada com sucesso! Aguarde a libera��o pelo moderador dessa comunidade.", TipoMensagemUFRN.INFORMATION);
								
				//Cria o assunto e o titulo do email
				String assunto = "Solicita��o de Participa��o em Comunidade Virtual";
				String texto = " <p>Uma nova solicita��o para participar da comunidade " +comunidadeVirtual.getNome().toUpperCase()+ " foi feita.<br> Para visualiz�-la acesse a op��o <b>Configura��es</b> na Comunidade Virtual no SIGAA.</p><br><br><i>Esta mensagem � autom�tica e n�o deve ser respondida</i><br><br>Comunidade virtual - SIGAA-UFRN";
				
				//busca o admistrador da comunidade
				Set<MembroComunidade> lista = comunidadeVirtual.getParticipantesComunidade();
				List<Pessoa> adm = new ArrayList<Pessoa>();

				for (MembroComunidade membro : lista) {
					if (membro.isAdministrador()) {
						adm.add(membro.getPessoa());
					}
				}
								
				//Envia email para o admistrador
				notificarUsuarios(assunto, texto, adm);
								
			} else 
				addMessage("Voc� j� solicitou participa��o nessa comunidade ou j� � participante da mesma.", TipoMensagemUFRN.WARNING);
			
			return null;
		} finally {
			if (mcDao != null)
				mcDao.close();
		}
	}
	
	public void prepararIngressoComCodigo (ActionEvent e) throws DAOException{
		comunidade = getComunidadeVirtualByID(getIDComunidadeVirtualFromView(e));
	}
	
	public String tentarIngressarComCodigo () throws SegurancaException, ArqException {
		boolean ok = false;
		
		try {
			// Se o usu�rio n�o digitou um c�digo de acesso, ser� enviada uma solicita��o de cadastro para o moderador da comunidade.
			if (codigoAcesso == null || codigoAcesso.trim().equals("")){
				solicitarParticipacaoComunidadeVirtual(null);
				
			// Se o usu�rio digitou o c�digo igual ao c�digo da comunidade, ent�o pode ingressar.
			} else if (codigoAcesso.trim().equals(comunidade.getCodigoAcesso())) {
				participarComunidadeVirtualPublica((ActionEvent) null);
				
			// Se o usu�rio digitou o c�digo errado
			} else
				addMessage("O c�digo de acesso digitado est� inv�lido", TipoMensagemUFRN.ERROR);
			
			if (!erros.isErrorPresent())
				ok = true;
			
			RequestContext.getCurrentInstance().addCallbackParam("sucesso", ok);
			//FacesContext.getCurrentInstance().addMessage(null, mensagemGrowl);
			//addMensagemInfoAjax(mensagemGrowl.getDetail());
		
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		return forward("/cv/buscar_comunidade.jsp");
	}
	
	/**
	 * Verifica se usu�rio j� solicitou participa��o na comunidade
	 * N�o invocado por JSP.
	 * 
	 * @param idComunidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean verificarParticipacaoUsuarioComunidade(int idComunidade) throws HibernateException, DAOException {
		return getDAO(MembroComunidadeDao.class).findSolicitacoesByUsuario(getUsuarioLogado().getId(), idComunidade);
	}

	/**
	 * Permite usu�rios participarem de uma comunidade do tipo P�blica.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/buscar_comunidade.jsp
	 * 
	 * @param evt
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String participarComunidadeVirtualPublica(ActionEvent evt) throws SegurancaException, ArqException, NegocioException {
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		Integer idComunidade = comunidade != null ? comunidade.getId() : 0;
		if (evt != null)
			idComunidade = getIDComunidadeVirtualFromView(evt);

		comunidade = getComunidadeVirtualByID(idComunidade);

		boolean usuarioExistente =  getDAO(MembroComunidadeDao.class).findPartipanteComunidade(getUsuarioLogado().getPessoa().getId(), comunidade.getId());
		
		if (!usuarioExistente) {
			MembroComunidade membroCV = new MembroComunidade();
			membroCV.setComunidade(comunidade);
			membroCV.setPessoa(getUsuarioLogado().getPessoa());
			membroCV.setAtivo(true);
			membroCV.setPermissao(MembroComunidade.MEMBRO);
		
			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setObjMovimentado(membroCV);
			movCad.setCodMovimento(ArqListaComando.CADASTRAR);
			
			addMessage ("Inscri��o na comunidade realizada com sucesso! Voc� pode acessar a comunidade atrav�s do seu Portal.", TipoMensagemUFRN.INFORMATION);
				
			execute(movCad);
			
			ComunidadeVirtualMBean comunidadeMBean = (ComunidadeVirtualMBean) getMBean("comunidadeVirtualMBean");
			comunidadeMBean.setComunidade(comunidade);
		}
		else {
			addMessage("Voc� j� � participante dessa comunidade.", TipoMensagemUFRN.WARNING);
			
			return null;
		}
		return null;
	}
	

	/**
	 * Usu�rio solicita a participa��o em um Grupo pr�-definido de acordo com a comunidade selecionada na view.
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/buscar_comunidade.jsp
	 * 
	 * @param evt
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	
	public String participarComunidadeVirtualRestritoGrupo(ActionEvent evt) throws SegurancaException, ArqException, NegocioException {
	
		Integer idComunidade = getIDComunidadeVirtualFromView(evt);
		return participarComunidadeVirtualRestritoGrupo(idComunidade);
		
	}
	
	/**
	 * Usu�rio solicita a participa��o em um Grupo pr�-definido de acordo com a comunidade selecionada na view.
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/buscar_comunidade.jsp
	 * 
	 * @param evt
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	
	public String acessoPublicoCVRestritoGrupo() throws SegurancaException, ArqException, NegocioException {
		
		//Redireciona para tela de autentica��o, caso usu�rio n�o esteja logado, passando como par�metro o caminho de acesso a comunidade
		if((isEmpty(getUsuarioLogado()) && !isEmpty(idComunidade)))
			return redirect("/verTelaLogin.do?urlRedirect=/sigaa/public/cv/" + idComunidade + ".jsf");
		
		setSubSistemaAtual(SigaaSubsistemas.SIGAA);
		MembroComunidadeDao membroComunidadeDao = getDAO(MembroComunidadeDao.class);

		//Verifica se o usu�rio � membro do grupo restrito da comunidade virtual
		if( idComunidade != null )
			comunidade = getComunidadeVirtualByID( Integer.valueOf( idComunidade ) );
		
		if( comunidade == null)
			addMessage("Comunidade inexistente.", TipoMensagemUFRN.WARNING);
			
		String sqlGrupo = "";
		if(!isEmpty(comunidade) && !isEmpty(comunidade.getIdGrupoAssociado()))
			sqlGrupo = membroComunidadeDao.findSQLByGrupoDestinatarios(comunidade.getIdGrupoAssociado());
		
		ComunidadeVirtualMBean comunidadeMBean = (ComunidadeVirtualMBean) getMBean("comunidadeVirtualMBean");
		
		if( verificarExistenciaUsuarioGrupo(membroComunidadeDao, sqlGrupo, false)){
		
			//Cadastra o membro caso ainda n�o esteja como participante
			boolean usuarioExistente =  membroComunidadeDao.findPartipanteComunidade(getUsuarioLogado().getPessoa().getId(), comunidade.getId());
			if (!usuarioExistente)
				cadastrarMembroRestritoGrupo();
			
			//Seta a comunidade e carrega os dados necess�rios para exibi��o da comunidade virtual
			comunidadeMBean.setComunidade(comunidade);
			
			return comunidadeMBean.entrar();
			
		}else
			addMessage("Essa comunidade s� aceita usu�rios que estejam pr�-definidos em um Grupo de pessoas autorizadas. No momento voc� n�o faz parte desse Grupo.", TipoMensagemUFRN.WARNING);
		
		if(!isEmpty(getUsuarioLogado().getVinculoAtivo()) && !isEmpty(getUsuarioLogado().getVinculoAtivo().getDiscente()) )
			getUsuarioLogado().setDiscente(getUsuarioLogado().getVinculoAtivo().getDiscente().getDiscente());
		
		return comunidadeMBean.redirecionarDeAcordoUsuarioLogado();
		
	}
	
	/**
	 * Usu�rio solicita a participa��o em um Grupo pr�-definido de acordo com a comunidade setada no par�metro.
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>/sigaa.war/cv/buscar_comunidade.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private String participarComunidadeVirtualRestritoGrupo(Integer idComunidade) throws SegurancaException, ArqException, NegocioException {
		
		MembroComunidadeDao membroComunidadeDao = getDAO(MembroComunidadeDao.class);

		comunidade = getComunidadeVirtualByID(idComunidade);
		
		boolean usuarioFazParteGrupo = false;
		
		String sqlGrupo = membroComunidadeDao.findSQLByGrupoDestinatarios(comunidade.getIdGrupoAssociado());
		usuarioFazParteGrupo = verificarExistenciaUsuarioGrupo(membroComunidadeDao, sqlGrupo, usuarioFazParteGrupo);
		
		if (usuarioFazParteGrupo) {

			boolean usuarioExistente =  membroComunidadeDao.findPartipanteComunidade(getUsuarioLogado().getPessoa().getId(), comunidade.getId());
			
			if (!usuarioExistente) {
				cadastrarMembroRestritoGrupo();
				
				addMessage("Inscri��o na comunidade realizada com sucesso! Voc� pode acessar a comunidade atrav�s do seu Portal.", TipoMensagemUFRN.INFORMATION);
				ComunidadeVirtualMBean comunidadeMBean = (ComunidadeVirtualMBean) getMBean("comunidadeVirtualMBean");
				comunidadeMBean.setComunidade(comunidade);
			}
			else {
				addMessage("Voc� j� � participante dessa comunidade.", TipoMensagemUFRN.WARNING);
			}
		}
		else {
			addMessage("Essa comunidade s� aceita usu�rios que estejam pr�-definidos em um Grupo de pessoas autorizadas. No momento voc� n�o faz parte desse Grupo.", TipoMensagemUFRN.WARNING);
		}
		return null;
	}
	
	/**
	 * Cadastra o membro quando a comunidade acessada possuir o TipoComunidadeVirtual.RESTRITO_GRUPO.
 	 * M�todo n�o invocado por JSP.
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void cadastrarMembroRestritoGrupo() throws NegocioException, ArqException{
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		MembroComunidade membroCV = new MembroComunidade();
		membroCV.setComunidade(comunidade);
		membroCV.setPessoa(getUsuarioLogado().getPessoa());
		membroCV.setAtivo(true);
		membroCV.setPermissao(MembroComunidade.MEMBRO);
		
		MovimentoCadastro movCad = new MovimentoCadastro();
		movCad.setObjMovimentado(membroCV);
		movCad.setCodMovimento(ArqListaComando.CADASTRAR);
		
		execute(movCad);
	}

	/**
	 * Verifica se o usu�rio faz parte do grupo.
 	 * M�todo n�o invocado por JSP.
	 * @param comunidadeVirtDao
	 * @param listaGruposCV
	 * @param usuarioFazParteGrupo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private boolean verificarExistenciaUsuarioGrupo(MembroComunidadeDao comunidadeVirtDao, String sqlGrupo, boolean usuarioFazParteGrupo) throws DAOException {
		
		if (!sqlGrupo.isEmpty()) {
			List<Usuario> usuariosInGrupo = comunidadeVirtDao.findUsuariosFromGrupoDestinatarios(sqlGrupo);
			for (Usuario usuario : usuariosInGrupo) {
				if ( usuario.getId() == getUsuarioLogado().getId() )
					usuarioFazParteGrupo = true;
			}
		
			//Se for o administrador da comunidade
			Boolean usuarioAdministrador = comunidadeVirtDao.findAdministradorComunidade(getUsuarioLogado().getPessoa().getId(), comunidade.getId());
			if(usuarioAdministrador)
					usuarioFazParteGrupo = true;
		}
 		
		return usuarioFazParteGrupo;
	}

	/**
	 * Exibe todas as comunidades que o docente faz parte.<br/>	 
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 * 	<li>/sigaa.war/portais/docente/docente.jsp</li>
	 *  <li>/sigaa.war/ambientes_virtuais/menu.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String exibirTodasComunidadesDocente() {
		return redirect("/cv/visualizar_comunidades.jsf");
	}
	
	/**
	 * Lista todas as comunidades que uma pessoa esteja associada.
	 * 
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 * 	<li>/sigaa.war/portais/docente/docente.jsp</li>
	 *  <li>/sigaa.war/portais/docente/discente.jsp</li>
	 *  <li>/sigaa.war/cv/visualizar_comunidades.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComunidadeVirtual> getComunidadesPorPessoa() throws DAOException {
		if (isEmpty(comunidadesPorPessoa)) {
			ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class);
			comunidadesPorPessoa = dao.findComunidadesComProjecaoByPessoa(getUsuarioLogado());
		}
		return comunidadesPorPessoa;
	}
	
	/**
	 * Seta a lista de comunidades que uma pessoa est� associada.
	 * 
	 * M�todo n�o invocado por JSPs
	 * @return
	 * @throws DAOException
	 */
	public void setComunidadesPorPessoa( Collection<ComunidadeVirtual> comunidades) throws DAOException {
			comunidadesPorPessoa = comunidades;
	}
	
	/**
	 * Limpa os as comunidades virtuais do portal discente ap�s a renderiza��o do mesmo.
	 * Utilizado para evitar m�ltiplas chamadas ao m�todo getComunidadesPorPessoa(), o que
	 * causava a realiza��o de diversas consultas desnecess�rias.
	 * M�todo chamado pela seguinte JSP: /sigaa.war/portais/discente/discente.jsp
	 * @return
	 */
	public String getLimparComunidadesPessoa() {
		return null;
	}
	
	
	/**
	 * Localiza o ID do ActionEvent.
 	 * M�todo n�o invocado por JSP.
	 * @param evt
	 * @return
	 */
	private Integer getIDComunidadeVirtualFromView(ActionEvent evt) {
		Integer idComunidade = (Integer) evt.getComponent().getAttributes().get("idComunidade");
		return idComunidade;
	}

	/**
	 * Localiza uma comunidade virtual de acordo com o ID.
 	 * M�todo n�o invocado por JSP.
	 * @param idComunidade
	 * @return
	 * @throws DAOException
	 */
	private ComunidadeVirtual getComunidadeVirtualByID(Integer idComunidade) throws DAOException {
		ComunidadeVirtual cv = getDAO(ComunidadeVirtualDao.class).findByPrimaryKey(idComunidade, ComunidadeVirtual.class);
		return cv;
	}
	
	/**
	 * Define a comunidade seleciona no resultado da busca.
	 * 
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 * 	<li>/sigaa.war/portais/docente/docente.jsp</li>
	 *  <li>/sigaa.war/portais/docente/discente.jsp</li>
	 *  <li>/sigaa.war/cv/visualizar_comunidades.jsp</li>
	 * </ul> 
	 * @return
	 */
	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	/**
 	 * M�todo n�o invocado por JSP.
	 * @param comunidade
	 */
	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	/**
 	 * M�todo n�o invocado por JSP.
	 * @return
	 */
	public Collection<ComunidadeVirtual> getListaComunidades() {
		return listaComunidades;
	}

	/**
 	 * M�todo n�o invocado por JSP.
	 * @param listaComunidades
	 */
	public void setListaComunidades(Collection<ComunidadeVirtual> listaComunidades) {
		this.listaComunidades = listaComunidades;
	}

	/**
 	 * M�todo n�o invocado por JSP.
	 * @return
	 */
	public Integer getIdTipoComunidadeVirtual() {
		return idTipoComunidadeVirtual;
	}

	/**
 	 * M�todo n�o invocado por JSP.
	 * @param idTipoComunidadeVirtual
	 */
	public void setIdTipoComunidadeVirtual(Integer idTipoComunidadeVirtual) {
		this.idTipoComunidadeVirtual = idTipoComunidadeVirtual;
	}

	/**
	 * Retorna uma cole��o de Selectitem contendo todos os tipos de comunidades virtuais.
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *  <li>/sigaa.war/cv/buscar_comunidades.jsp</li>
	 * </ul> 
	 * @return
	 */
	public Collection<SelectItem> getAllTiposComunidades() {
		return allTiposComunidades;
	}

	/**
	 * M�todo n�o invocado por JSP.
	 * @param allTiposComunidades
	 */
	public void setAllTiposComunidades(Collection<SelectItem> allTiposComunidades) {
		this.allTiposComunidades = allTiposComunidades;
	}
	
	/**
	 * Retorna o nome ou palavra-chave da comunidade virtual na busca.
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *  <li>/sigaa.war/cv/buscar_comunidades.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getNomeComunidade() {
		return nomeComunidade;
	}

	/**
	 * M�todo n�o invocado por JSP. 
	 * @param nomeComunidade
	 */
	public void setNomeComunidade(String nomeComunidade) {
		this.nomeComunidade = nomeComunidade;
	}

	/**
	 * Retorna o id da comunidade virtual selecionada.
	 * M�todo n�o invocado por JSP. (Utilizado pelo pretty-faces)
	 * @return
	 */
	public String getIdComunidade() {
		return idComunidade;
	}

	/**
	 * M�todo n�o invocado por JSP. 
	 * @param idComunidade
	 */
	public void setIdComunidade(String idComunidade) {
		this.idComunidade = idComunidade;
	}

	public String getCodigoAcesso() {
		return codigoAcesso;
	}

	public void setCodigoAcesso(String codigoAcesso) {
		this.codigoAcesso = codigoAcesso;
	}
}