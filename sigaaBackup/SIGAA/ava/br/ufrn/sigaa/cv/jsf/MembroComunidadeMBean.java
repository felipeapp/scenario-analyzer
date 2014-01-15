/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 31/10/2008 
 */
package br.ufrn.sigaa.cv.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.cv.dao.MembroComunidadeDao;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaPessoaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean respons�vel por gerenciar os participantes de uma Comunidade Virtual.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("request")
public class MembroComunidadeMBean extends PaginacaoComunidadeController <MembroComunidade> {

	/** Lista de participantes da comunidade. */
	private List<MembroComunidade> participantes = new ArrayList<MembroComunidade>();
	/** Participante selecionado para ser modificado. */
	private Usuario usuarioSelecionado;
	/** Lista de usu�rios buscados. */
	private List<Usuario> listaUsers = new ArrayList<Usuario>();
	/** Lista de pessoas buscadas. */
	private List<Pessoa> listaPessoas = new ArrayList<Pessoa>();
	/** Nome da Pessoa que ser� buscada. */
	private String nomePessoaBuscar = "";
	
	/**
	 * Construtor
	 */
	public MembroComunidadeMBean() {
		object = new MembroComunidade();
	}
	
	/**
	 * Faz a busca de pessoas.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/novo.jsp
	 * @return
	 * @throws DAOException
	 */
	public String buscarPessoas() throws DAOException {
		if (!ValidatorUtil.isEmpty(nomePessoaBuscar) ) {
			listaUsers = getDAO(MembroComunidadeDao.class).findUsuarios(nomePessoaBuscar, Pessoa.PESSOA_FISICA);
			if (listaUsers.size() == 0)
				addMensagemWarning("Nenhum usu�rio localizado. Por favor informe outro nome.");
		}
		else
			addMensagemWarning("Voc� precisa informar o nome de algum usu�rio para realizar a busca.");
		
		return forward("/cv/" + object.getClass().getSimpleName() + "/novo.jsp");
	}
	
	/**
	 * Quando inicia, seta o c�digo da opera��o para CADASTRA_MEMBRO_CV.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/participantes.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		BuscaPessoaMBean bp = (BuscaPessoaMBean) getMBean("buscaPessoa");
		bp.setCodigoOperacao(OperacaoPessoa.CADASTRA_MEMBRO_CV);
		return bp.popular();
	}
	
	/**
	 * Lista os participantes da comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/participantes.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<MembroComunidade> getParticipantes() throws DAOException {
		return lista();
	}
	
	/**
	 * Retorna a lista dos participantes paginados.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/participantes.jsp
	 */
	public List <MembroComunidade> getParticipantesPaginado () throws DAOException {
		
		if (listagem == null){
			ComunidadeVirtualDao dao = null;
			try {
				ComunidadeVirtualMBean cvBean = getMBean("comunidadeVirtualMBean");
				
				dao = getDAO(ComunidadeVirtualDao.class);
				listagem = dao.findParticipantes(cvBean.getComunidade(), getPaginacao(), crescente);
				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return listagem;
	}

	/**
	 * Ap�s cadastrar redireciona usu�rio a p�gina de participantes.<br/><br/>
	 * 
	 * N�o invocado por JSP.
	 */
	@Override
	public String forwardCadastrar() {
		return "/cv/MembroComunidade/participantes.jsf";
	}
	
	/**
	 * Atualizar as permiss�es de um participante.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/editar.jsp
	 */
	@Override
	public String atualizar() throws DAOException {
		
		prepare(SigaaListaComando.ATUALIZAR_CV);
		
		boolean usuarioEraAdministrador = false;
		
		List<MembroComunidade> listaParticipantes = lista();
		int quantAdmisExistentes = 0;
		for (MembroComunidade membro : listaParticipantes) {
			if ( membro.isAdministrador() ){
				if (membro.getId() == object.getId())
					usuarioEraAdministrador = true;
				quantAdmisExistentes++;
			}
		}
		
		// Se o usu�rio era o �nico administrador da comunidade virtual, est� deixando de ser e a comunidade virtual s� tem um administrador, exibe a mensagem.
		if (usuarioEraAdministrador && !object.isAdministrador() && quantAdmisExistentes == 1)
			addMensagemWarning("Esta comunidade s� tem um administrador. Tirar a permiss�o de administrador deste usu�rio deixaria a comunidade sem administradores. Para isso, por favor, antes adicione outro administrador.");
		else
			super.atualizar();
		
		return forward("/cv/MembroComunidade/participantes.jsp");  
	}
	
	/**
	 * Edita os dados de um participante.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/participantes.jsp
	 */
	@Override
	public String editar() throws DAOException {
		object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), object.getClass());
		if (object != null)
			return super.editar();
		else
			addMensagemWarning("Participante n�o localizado como membro dessa comunidade.");
		
		return forward("/cv/MembroComunidade/participantes.jsf");
	}
	
	/**
	 * Remove um participante da comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/participantes.jsp
	 * @throws DAOException 
	 */
	@Override
	public String remover()  {
		try {
			prepare(SigaaListaComando.ATUALIZAR_CV);
			
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id", 0), object.getClass());
			
			MembroComunidade membro = null;
			if (object == null) {  // pr�prio participante saindo da comunidade.
				 membro = getDAO(ComunidadeVirtualDao.class ).findMembroByPessoa( comunidade(), new Pessoa(getUsuarioLogado().getPessoa().getId()) );
					if (membro != null)
						object = getGenericDAO().findByPrimaryKey(membro.getId(), MembroComunidade.class);
					else
						addMensagemWarning("Membro n�o localizado na comunidade.");
			}
				
			if (object != null) {
			
				if (object.getPermissao() == MembroComunidade.ADMINISTRADOR) {
					addMensagemWarning("Para remover um usu�rio do tipo 'Administrador' primeiro � necess�rio alterar sua permiss�o para Membro ou Moderador, permitindo dessa forma sua remo��o.");
					return null;
				}
				
				object.setAtivo(false);
				execute(SigaaListaComando.ATUALIZAR_CV, object , getEspecificacaoRemocao());
				
				ComunidadeVirtualMBean cBean = getMBean("comunidadeVirtualMBean");
				cBean.atualizarListaParticipantesComunidade();
				
				addMensagemInformation("Participante da comunidade removido com sucesso.");
				return forward("/cv/MembroComunidade/participantes.jsf");
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Envia uma convite para o usu�rio selecionado. Caso o usu�rio aceite o convite, ir�
	 * imediatamente fazer parte da Comunidade Virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/novo.jsp
	 */
	@Override
	public String cadastrar() throws ArqException {

		usuarioSelecionado = getGenericDAO().findByPrimaryKey(getParameterInt("idUsuario",0), Usuario.class);
		
		if ( !(object.getPermissao() == 0) && usuarioSelecionado != null) {
		
				String hashMD5 = UFRNUtils.toMD5( (comunidade().getNome() + System.currentTimeMillis()));

				String linkSistemaAtual = RepositorioDadosInstitucionais.getLinkSigaa();
				
				MailBody body = new MailBody();
				body.setAssunto("SIGAA - Convite para Comunidade Virtual");
				
				String link = linkSistemaAtual.trim() + "/sigaa/validarConvite?user="+ usuarioSelecionado.getId() + "&autenticacao=" + hashMD5; 
				
				body.setMensagem("<html>Ol� " + usuarioSelecionado.getNome() + 
						"!<br><br> Voc� foi convidado(a) para ser " + (MembroComunidade.getDescricaoPermissaoByID(object.getPermissao())) + " da Comunidade " + comunidade().getNome() + 
						".<br><br> Caso deseje participar dessa comunidade por favor <a href=\"" + link + "\">clique aqui</a> para confirmar sua inscri��o. <br> " +
						"<br> Descri��o da comunidade: " +
						"<br> " + comunidade().getDescricao() +
						"<br><br> Caso n�o deseje participar, por favor ignore esse e-mail. " +
						"<br><br> " + getUsuarioLogado().getNome() +
						"<br>Comunidade virtual - SIGAA-"+RepositorioDadosInstitucionais.getSiglaInstituicao()+"</html>");
				
 				body.setFromName("SIGAA - " + comunidade().getNome());
				body.setEmail( usuarioSelecionado.getEmail() );
				Mail.send(body);
		
				if ( getDAO(MembroComunidadeDao.class).registrarConviteEnviado(
						new Date(), usuarioSelecionado.getId(), comunidade().getId(), usuarioSelecionado.getPessoa().getId(), object.getPermissao(), hashMD5) )
					addMensagemInformation("Convite enviado para o usu�rio com sucesso! Aguardando confirma��o pelo usu�rio.");
			
				clear();
				return forward("/cv/MembroComunidade/novo.jsf");
				
		} else if (usuarioSelecionado == null){
			addMensagemWarning("Voc� n�o selecionou um usu�rio.");
		} else if (object.getPermissao() == 0){
			addMensagemWarning("Selecione um tipo de permiss�o para esse usu�rio");
		}
		return null;
	}
	
	/**
	 * Reseta dom�nio e o nome da �ltima pessoa buscada.
	 */
	private void clear() {
		object = new MembroComunidade();
		nomePessoaBuscar = "";
	}

	/**
	 * Localiza os participantes de determinada comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/MembroComunidade/participantes.jsp
	 */
	@Override
	public List<MembroComunidade> lista() throws DAOException {
		participantes = CollectionUtils.toArrayList( comunidade().getParticipantesComunidade() );
		return participantes;
	}
	
	/**
	 * Redireciona usu�rio para p�gina de participantes da comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * @return
	 * @throws DAOException
	 */
	public String exibirAllParticipantes() throws DAOException {
		iniciarListagem();
		return forward("/cv/MembroComunidade/participantes.jsp");
	}
	
	/**
	 * Redireciona para a p�gina de participantes.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/MembroComunidade/editar.jsp</li>
	 * 	<li>/sigaa.war/cv/MembroComunidade/participantes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String participantes() {
		return redirect("/cv/MembroComunidade/participantes.jsf");
	}

	public List<Usuario> getListaUsers() {
		return listaUsers;
	}

	public void setListaUsers(List<Usuario> listaUsers) {
		this.listaUsers = listaUsers;
	}

	public String getNomePessoaBuscar() {
		return nomePessoaBuscar;
	}

	public void setNomePessoaBuscar(String nomePessoaBuscar) {
		this.nomePessoaBuscar = nomePessoaBuscar;
	}

	public List<Pessoa> getListaPessoas() {
		return listaPessoas;
	}

	public void setListaPessoas(List<Pessoa> listaPessoas) {
		this.listaPessoas = listaPessoas;
	}
}
