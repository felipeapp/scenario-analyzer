/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '12/05/2011'
 *
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.RotuloTurmaDao;
import br.ufrn.sigaa.ava.dominio.RotuloTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator;

/**
 * Representa um r�tulo que auxilia na organiza��o dos t�picos de aula
 * na p�gina principal da turma virtual.
 * 
 * @author Ilueny Santos
 *
 */
@Component("rotuloTurmaBean") 
@Scope("request")
public class RotuloTurmaMBean extends CadastroTurmaVirtual<RotuloTurma> {

	/** Lista de r�tulos apresentados na view. */
	List<RotuloTurma> rotulos = new ArrayList<RotuloTurma>();

	/**
	 * Construtor Padr�o.
	 */
	public RotuloTurmaMBean (){
		object = new RotuloTurma();
	}
	
	/**
	 * Retorna a lista de r�tulos da turma.<br/>
	 * Chamado pelo m�todo "getListagem()" do bean CadastroTurmaVirtual.<br/>
	 * 
  	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/ava/RotuloTurma/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public List<RotuloTurma> lista() {
		try {
			return	getDAO(RotuloTurmaDao.class).findRotulosByTurma(turma());
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	/**
	 * Retorna uma especifica��o para a turma.
	 * N�o � usado em jsps.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {				
				RotuloTurma rotulo = (RotuloTurma) objeto;
				
				if (isEmpty(rotulo.getDescricao())) {
					notification.addError("Descri��o: Campo obrigat�rio n�o informado.");
				}
				if (isEmpty(rotulo.getTurma())) {
					notification.addError("Turma: Campo obrigat�rio n�o informado.");
				}
				if (isEmpty(rotulo.getAula())) {
					notification.addError("T�pico de Aula: Campo obrigat�rio n�o informado.");
				}
				
				return !notification.hasMessages();				
			}
		};
	}

	/**
	 * Indica ao ControllerTurmaVirtual que � para cadastrar em todas as turmas selecionadas.
	 */
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return false;
	}

	
	/**
	 * Verifica se o usu�rio logado pode cadastrar um r�tulo para a turma trabalhada.
	 * <br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/ChatTurma/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isPermiteCadastrarRotulo() {
		TurmaVirtualMBean tv = getMBean("turmaVirtual");
		PermissaoAvaMBean perm = getMBean("permissaoAva");
		
		return tv.isDocente() 
				|| perm.getPermissaoUsuario() != null && perm.getPermissaoUsuario().isDocente()
				|| OperacaoTurmaValidator.verificarNivelTecnico(turma());
	}

	
	
	/** 
	 * Inicia o cadastro de um R�tulo para o t�pico de aula informado. 
  	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/ava/RotuloTurma/lista.jsp</li>
	 *   <li>/sigaa.war/ava/topico_aula.jsp</li>   
	 * </ul>
	 */
	public String preCadastrar(TopicoAula topicoAula) throws ArqException {
		try {
			topicoAula = getGenericDAO().findByPrimaryKey(topicoAula.getId(), TopicoAula.class, "id", "descricao", "turma.id");			
			object = new RotuloTurma();
			object.setAula(topicoAula);
			object.setTurma(topicoAula.getTurma());
			object.setUsuarioCadastro(getUsuarioLogado());
			
			List <String> cadastrarEm = new ArrayList <String> ();
			cadastrarEm.add(""+turma().getId());
			setCadastrarEm(cadastrarEm);			
			
			prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
			return forward("/ava/RotuloTurma/novo.jsp");
		}catch (Exception e) {
			tratamentoErroPadrao(e);			
			return null;
		}
	}

	@Override
	public void antesPersistir() {
		if (object != null && ValidatorUtil.isNotEmpty(object.getDescricao())){
			object.setDescricao(object.getDescricao().replace("<p>", ""));
			object.setDescricao(object.getDescricao().replace("</p>", ""));
		}

		super.antesPersistir();
	}
	
	
	/** 
	 * Inicia o cadastro de um R�tulo para a turma atual.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 * 		<li>/sigaa.war/ava/RotuloTurma/listar.jsp</li>
	 *  </ul>
	 *  
	 */
	public String novoRotuloTurma() throws ArqException {
		try {
			object = new RotuloTurma();
			object.setAula(new TopicoAula());
			object.setTurma(turma());
			object.setUsuarioCadastro(getUsuarioLogado());
			
			List <String> cadastrarEm = new ArrayList <String> ();
			cadastrarEm.add(""+turma().getId());
			setCadastrarEm(cadastrarEm);

			prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
			return forward("/ava/RotuloTurma/novo.jsp");

		}catch (Exception e) {
			tratamentoErroPadrao(e);			
			return null;
		}
	}

	/**
	 * Remove o rotulo do selecionado.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 * 		<li>/sigaa.war/ava/RotuloTurma/listar.jsp</li>
	 *  </ul>
	 * 
	 */
	@Override
	public String inativar() {
		setPaginaOrigem(getParameter("paginaOrigem"));
		return super.inativar();
	}
	
	/**
	 * Atualiza o r�tulo selecionado.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 * 		<li>/sigaa.war/ava/topico_aula.jsp</li>
	 *  </ul>
	 * 
	 */
	@Override
	public String atualizar() {
		setPaginaOrigem(getParameter("paginaOrigem"));
		return super.atualizar();
	}

	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
	}

	public List<RotuloTurma> getRotulos() {
		return rotulos;
	}

	public void setRotulos(List<RotuloTurma> rotulos) {
		this.rotulos = rotulos;
	}
	
}
