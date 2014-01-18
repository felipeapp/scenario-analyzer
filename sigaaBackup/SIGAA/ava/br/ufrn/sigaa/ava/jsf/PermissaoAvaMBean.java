/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/06/21 - 18:07:52
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaPessoaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoPessoa;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed Bean para cadastro de permiss�es em uma turma virtual.
 *
 * @author David Pereira
 *
 */
@Component("permissaoAva") @Scope("request")
public class PermissaoAvaMBean extends CadastroTurmaVirtual<PermissaoAva> implements OperadorPessoa {

	private Pessoa pessoa;

	/** Permiss�o do usu�rio */
	private PermissaoAva permissaoUsuario;
	/** Indica se a permiss�o do usu�rio j� foi buscada, para n�o ficar buscando indefinidamente quando for null. */
	private boolean jaBuscado;
	
	public PermissaoAvaMBean() {
		object = new PermissaoAva();
	}
	
	/**
	 * Retorna a lista contendo todas as permiss�es de uma turma.<br/><br/>
	 * 
	 * M�todo n�o invocado por JSPs. � public por causa da arquitetura.
	 */
	@Override
	public List<PermissaoAva> lista() {
		return getDAO(TurmaVirtualDao.class).findPermissoesByTurma(turma());
	}
	
	/**
	 * Exibe o formul�rio para se cadastrar uma nova permiss�o.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /ava/PermissaoAva/listar.jsp
	 * @return
	 */
	public String preCadastrar() {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		if (tBean.isDocente()) {
		
			try {
				prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
				BuscaPessoaMBean mbean = (BuscaPessoaMBean) getMBean("buscaPessoa");
				mbean.setCodigoOperacao(OperacaoPessoa.PERMISSAO_AVA);
				return mbean.popular();
			} catch (ArqException e) {
				notifyError(e);
				addMensagemErroPadrao();
				return null;
			}
		}
		return null;
	}

	/**
	 * Ap�s selecionada a pessoa a receber a permiss�o, exibe a tela de configurar a permiss�o.<br/><br/>
	 * 
	 * M�todo n�o invocado por JSPs. � public por causa da arquitetura.
	 */
	public String selecionaPessoa() {
		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
		object = dao.findPermissaoByPessoaTurma(pessoa, turma());
		
		if( object != null && object.getTurma().getIdPolo() != null && object.getTurma().getIdPolo() > 0 ){
			try {
				object.getTurma().setPolo( dao.findByPrimaryKey(object.getTurma().getIdPolo(), Polo.class) );
			} catch (DAOException e) {
				return tratamentoErroPadrao(e);
			}
		}
		
		if (object == null) {
			object = new PermissaoAva();
			object.setTurma(turma());
			object.setPessoa(pessoa);
			return forward("/ava/PermissaoAva/novo.jsp");
		}

		return forward("/ava/PermissaoAva/editar.jsp");
		
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public PermissaoAva getPermissaoUsuario() {
		if (permissaoUsuario == null && getUsuarioLogado() != null){
			if (!jaBuscado){
				permissaoUsuario = getDAO(TurmaVirtualDao.class).findPermissaoByPessoaTurma(getUsuarioLogado().getPessoa(), turma());
				jaBuscado = true;
			}
		}
		return permissaoUsuario;
	}
	
	/**
	 * Se o usu�rio possui algum n�vel de permiss�o na turma virtual.<br/><br/>
	 * 
  	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/ava/menu_horizontal.jsp</li>
	 * </ul>	 * @return
	 */
	public boolean getPermissaoAcesso () {
		if (getPermissaoUsuario() != null)
			return true;
		else
			return false;
	}
	
	@Override
	public String forwardCadastrar () {
		return "/ava/PermissaoAva/listar.jsf";
	}
	
	/**
	 * Cadastra a permiss�o e retorna para a lista.
	 * @return
	 * @throws ArqException 
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");	
		object.setPessoa(getGenericDAO().findByPrimaryKey(object.getPessoa().getId(), Pessoa.class));
		
		if (tBean.isDocente())
				return super.cadastrar();
		
		return null;
		
	}
	
	/**
	 * Remove a permiss�o e retorna para a lista.
	 * @return
	 * @throws ArqException 
	 */
	@Override
	public String remover() {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");	
		if (tBean.isDocente())
				return super.remover();
		return null;
		
	}
	
	/**
	 * Prepara a permiss�o para ser atualizada.
	 * @return
	 * @throws ArqException 
	 */
	@Override
	public String editar() {
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");	
		if (tBean.isDocente())
				return super.editar();
		return null;
		
	}
	
	/**
	 * Atualiza a permiss�o e retorna para a lista.
	 * @return
	 */
	@Override
	public String atualizar() {
		
		GenericDAO dao = null;
		
		try {
			
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			dao = getGenericDAO();
			
			if (tBean.isDocente() ) {
				
				object.setPessoa(dao.findByPrimaryKey(object.getPessoa().getId(), Pessoa.class));
				object.setTurma(turma());
				antesPersistir();
				
				Specification specification = getEspecificacaoAtualizacao();
				if (specification == null || specification instanceof NullSpecification ) 
					specification = getEspecificacaoCadastro();
				
				registrarAcao(AcaoAva.INICIAR_ALTERACAO);
				Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, object, specification);
				
				if (notification.hasMessages()) {
					prepare(SigaaListaComando.ATUALIZAR_AVA);
					return notifyView(notification);
				}
		
				registrarAcao(AcaoAva.ALTERAR);
				listagem = null;
				flash(" Permiss�o atualizada com sucesso.");
				aposPersistir();
				return forward("/ava/PermissaoAva/listar.jsp");
			}
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}
	
	public boolean isJaBuscado() {
		return jaBuscado;
	}

	public void setJaBuscado(boolean jaBuscado) {
		this.jaBuscado = jaBuscado;
	}

}
