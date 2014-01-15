/* 
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
 *
 * Created on 20/03/2007
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dao.RecomendacaoDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.Recomendacao;

/**
 * Managed bean para cadastro de Recomendações de Cursos Stricto Sensu
 *
 * @author Leonardo
 *
 */
@Component("recomendacao") @Scope("session")
public class RecomendacaoMBean extends SigaaAbstractController<Recomendacao> {
	/** Programa Stricto */
	private Unidade programa;

	/** Lista de cursos stricto */
	private Collection<SelectItem> cursosStricto;

	/** Construtor padrão */
	public RecomendacaoMBean(){
		initObj();
	}

	/** Inicializa os objetos */
	private void initObj() {
		obj = new Recomendacao();
		obj.setCurso( new Curso());
		obj.setDataAvaliacao( new Date());
		programa = new Unidade();
		cursosStricto = new HashSet<SelectItem>();
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.PPG);
	}
	
	@Override
	public String getDirBase() {
		return "/stricto/recomendacao";
	}

	/**
	 * Método que recebe um evento JSF e atualiza a lista de cursos stricto
	 * de acordo com o selectOneMenu da JSP
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/stricto/recomendacao/form.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaCursosStricto(ValueChangeEvent evt) throws DAOException{
		carregaCursosStricto( (Integer) evt.getNewValue() );
	}
	
	/**
	 * Carrega o combo dos curso de acordo copmmo programa selecionado.
	 * Método não chamado por JSP's.
	 * @param idUnidade
	 * @throws DAOException
	 */
	private void carregaCursosStricto(Integer idUnidade) throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		cursosStricto = toSelectItems(dao.findByUnidade(idUnidade, ' '), "id", "nomeCursoStricto");
	}
	
	/**
	 * Verifica a existência de uma recomendação para o curso selecionado.
	 * Método não chamado por JSP's.
	 * @see AbstractControllerCadastro#doValidate()
	 */
	@Override
	protected void doValidate() throws ArqException {
		if( isEmpty( programa ) ){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Programa");	
		}
		if( !isEmpty( obj.getCurso() ) ){
			Recomendacao chkRecomendacaoCursoExistente = getGenericDAO().
					findByExactField(Recomendacao.class, "curso.id", obj.getCurso().getId(), true);
			if( !isEmpty(chkRecomendacaoCursoExistente) &&
					( (!isEmpty(obj) && obj.getId() != chkRecomendacaoCursoExistente.getId()) || isEmpty(obj) ) )
				addMensagemErro("Já existe uma recomendação cadastrada para o curso selecionado.");
		}
	}
	
	/**
	 * Método não chamado por JSP's.
	 * @see AbstractControllerCadastro#afterAtualizar()
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if( !isEmpty(obj) ){
			programa = obj.getCurso().getUnidade();
			carregaCursosStricto( programa.getId() );
		}	
	}

	/**
	 * Inicializa o cadastro.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:	  
	 * <ul>
	 * <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul> 
	 */
	public String preCadastrar() throws ArqException {
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getFormPage());
	}
	
	/**
	 * Retorna todas as recomendações dos cursos para
	 * listagem e alteração.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:	  
	 * <ul>
	 * <li>/sigaa.war/stricto/recomendacao/lista.jsp</li>
	 * </ul> 
	 */
	@Override
	public Collection<Recomendacao> getAll() throws ArqException {
		if( isAllEmpty(all) ){
			all = getDAO(RecomendacaoDAO.class).findAllProjection();
		}
		return all;
	}
	
	/**
	 * Reinicializa o objeto no managed bean e redirecina para
	 * listagem das recomendações  de curso.
	 * <ul>
	 * <li>/sigaa.war/stricto/recomendacao/form.jsp</li>
	 * </ul> 
	 */
	@Override
	public String cancelar() {
		if( !isEmpty(getUltimoComando()) && getUltimoComando().equals(ArqListaComando.ALTERAR) ){
			resetBean();
			initObj();
			all = null;
			return forward(getListPage());
		} 
		return super.cancelar();
	}
	
	/**
	 * Redireciona para tela do subsistema
	 * <ul>
	 * <li>/sigaa.war/stricto/recomendacao/l.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String subSistema() {
		return redirectJSF(getSubSistema().getLink());
	}

	/**
	 * Reinicializa o objeto após o cadastro.
	 * Método não chamado por JSP's.
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		initObj();
		all = null;
	}
	
	/**
	 * Remove a recomendação cadastrada.
	 * @return
	 * @throws ArqException
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:	  
	 * <ul>
	 * <li>/sigaa.war/stricto/recomendacao/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		prepareMovimento(ArqListaComando.REMOVER);
		populateObj(true);
		return super.remover();
	}

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	public Collection<SelectItem> getCursosStricto() {
		return cursosStricto;
	}

	public void setCursosStricto(Collection<SelectItem> cursosStricto) {
		this.cursosStricto = cursosStricto;
	}
}
