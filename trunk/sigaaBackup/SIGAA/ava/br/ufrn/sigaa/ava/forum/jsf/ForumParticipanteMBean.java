package br.ufrn.sigaa.ava.forum.jsf;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.forum.dao.ForumGeralDao;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralParticipante;

@Component("forumParticipanteBean") 
@Scope("request")
public class ForumParticipanteMBean extends SigaaAbstractController<ForumGeralParticipante> {
	
	/** Utilizado na busca de f�runs. */
	private String titulo;
	
	private List<ForumGeral> foruns = new ArrayList<ForumGeral>(); 
	
	/** Construtor padr�o. */
	public ForumParticipanteMBean() {
		obj = new ForumGeralParticipante();
	}
	
	/** Diret�rio Base do F�rum. */
	@Override
	public String getDirBase() {
		return "/ava/Foruns/Participacao";
	}
	
	/** 
	 * Inicia a busca por f�runs cadastrados.
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/view.jsp</li>
     * </ul>
     * 
     */
	public String iniciarBusca() {
		foruns.clear();
		return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_PARTICIPACAO_LISTA);
	}
	
	/** 
	 * Realiza busca por f�runs cadastrados.
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Participacao/lista.jsp</li>
     * </ul>
     * 
     */
	public String buscar() throws HibernateException, DAOException {		
		foruns.clear();
		foruns = getDAO(ForumGeralDao.class).filter(titulo);
		
		if (ValidatorUtil.isEmpty(foruns)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);	
		}
		
		return null;
	}
	
	/** 
	 * Inicia a vincula��o do usu�rio com o f�rum selecionado.
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Participacao/lista.jsp</li>
     * </ul>
     * 
     */
	public String participar() throws HibernateException, ArqException {
		if (ValidatorUtil.isEmpty(getUsuarioLogado())){
			addMensagemErro("Usu�rio n�o informado.");
			return null;
		}			
		
		int id = getParameterInt("id", 0);
		if (id == 0){
			addMensagemErro("F�rum n�o selecionado!");
			return null;
		}
		
		if (!ValidatorUtil.isEmpty(getGenericDAO().findByExactField(ForumGeralParticipante.class,
				new String[]{"forum.id", "usuario.id", "ativo"}, 
				new Object[] {id, getUsuarioLogado().getId(), Boolean.TRUE})) ) {
			addMensagemErro("O Usu�rio j� possui acesso ao f�rum selecionado.");
			return null;			
		}
		
		ForumGeral forum = getGenericDAO().findByPrimaryKey(id, ForumGeral.class);
		if (ValidatorUtil.isEmpty(forum)){
			addMensagemErro("F�rum selecionado n�o � um f�rum v�lido.");
			return null;
		}
		
		obj = new ForumGeralParticipante();
		obj.setForum(forum);
		obj.setUsuario(getUsuarioLogado());
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_PARTICIPACAO_FORM);
	}

	@Override
	public String forwardCadastrar() {
		return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_PARTICIPACAO_LISTA);
	}
	
	/** 
	 * Realiza a desvincula��o do usu�rio no f�rum selecionado.
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Participacao/lista.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String inativar() throws ArqException, NegocioException {
		return super.inativar();
	}
	
	@Override
	protected String forwardInativar() {
		return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_PARTICIPACAO_LISTA);
	}
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public List<ForumGeral> getForuns() {
		return foruns;
	}

	public void setForuns(List<ForumGeral> foruns) {
		this.foruns = foruns;
	}

}
