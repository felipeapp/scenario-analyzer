/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.CategoriaDiscenteEspecial;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;

/** 
 * Controller responsável pelas operações de cadastro de formas de ingresso de um discente na instituição, 
 * definido por nível de ensino.
 * 
 * @author Édipo Elder F. Melo
 */
public class FormaIngressoMBean extends SigaaAbstractController<FormaIngresso> {
	
	/** Indica se a forma de ingresso é ativa. */
	private boolean somenteAtivos = true;
	/** Nível de ensino específico da forma de ingresso. */
	private Character nivel;
	
	/** Construtor padrão. */
	public FormaIngressoMBean() {
		clear();
	}
	
	/** Inicializa os atributos do controller.
	 *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>metodo nao invocado por JSP</li>
	 * </ul>
	 */
	public void clear() {
		obj = new FormaIngresso();
		obj.setCategoriaDiscenteEspecial(new CategoriaDiscenteEspecial());
	}
	
	/**verifica se a forma de ingresso é de aluno especial*/
	public boolean isEspecial(){
		if(obj.getTipo() != null && obj.getTipo().equals(FormaIngresso.TIPO_ALUNO_ESPECIAL) )
			return true;
		return false;
	}
	/** Retorna uma coleção de selectItem com todas formas de ingresso.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(FormaIngresso.class, "id", "descricao");
	}
	
	/** Retorna todas formas de ingresso que possuem Processo Seletivo
	 * @throws DAOException 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getRealizamProcessoSeletivoCombo() throws DAOException {
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		return toSelectItems(dao.findByExactField(FormaIngresso.class, "realizaProcessoSeletivo", "true"), "id", "descricao");
	}
	
	/** Seta nulo para os dados que transientes não utilizados.
	 * Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAfterValidate()
	 * 
	 * Chamado por /administracao/cadastro/FormaIngresso/form.jsp
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
		if (obj.getTipo() == '0')
			obj.setTipo(null);
		if (obj.getNivel() == '0')
			obj.setNivel(null);
	}
	
	/** Inicia o cadastro de forma de ingresso
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/administracao/cadastro/FormaIngresso/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		obj.setAtivo(true);
		checkChangeRole();
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_FORMA_INGRESSO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_FORMA_INGRESSO);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
	
	/** Inicia a atualização da forma de ingresso
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/administracao/cadastro/FormaIngresso/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		try {
				setOperacaoAtiva(SigaaListaComando.CADASTRAR_FORMA_INGRESSO.getId());
				prepareMovimento(SigaaListaComando.CADASTRAR_FORMA_INGRESSO);
				populateObj(true);
				setReadOnly(false);
				setConfirmButton("Alterar");
			} 
		catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		if(obj == null){
			erros.addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			obj = new FormaIngresso();
			if(hasErrors()){
				return forward(getDirBase() + "/lista.jsf");
			}
		}	
		
		if(obj.getCategoriaDiscenteEspecial() == null)
			obj.setCategoriaDiscenteEspecial(new CategoriaDiscenteEspecial());
		return forward(getFormPage());
	}
	
	/** Cadastra / atualiza a forma de ingresso
	 * 
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/administracao/cadastro/FormaIngresso/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_FORMA_INGRESSO.getId()))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		erros = obj.validate();
		if (hasErrors())
			return null;
		if(obj.getTipo().equals(FormaIngresso.TIPO_ALUNO_REGULAR) || obj.getCategoriaDiscenteEspecial().getId() == 0)
			obj.setCategoriaDiscenteEspecial(null);
		beforeCadastrarAfterValidate();
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_FORMA_INGRESSO);
		execute(mov);
		removeOperacaoAtiva();
		String forward = forwardCadastrar();
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Forma de Ingresso");
		if (forward == null) {
			return redirectJSF(getCurrentURL());
		} else {
			return redirectJSF(forward);
		}
	}
	
	/** Cancela a operação corrente
	 * * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/administracao/cadastro/FormaIngresso/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		resetBean();
		removeOperacaoAtiva();
		return forward(getListPage());
	}
	
	/** Redireciona para a página de listagem de formas de ingresso
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return  getDirBase() + "/lista.jsf";
	}
	
	/** Remove (inativa) uma forma de ingresso.
	 * Verifica se o objeto já foi removido, para evitar o nullPointer
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/administracao/cadastro/FormaIngresso/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {

		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);		
		obj = dao.findByPrimaryKey(id, FormaIngresso.class);

		if (obj == null) {
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		
		return super.remover();
	}
	
	/** 
	 * Define o atributo de ordenação das formas de ingresso.
	 * Método não invocado por JSP.
	 */
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}
	
	/**
	 * Diretório base da lista
	 * 
	 * @return
	 */
	@Override
	public String getListPage() {
		getPaginacao().setPaginaAtual(0);
		return getDirBase() + "/lista.jsf";
	}
	

}