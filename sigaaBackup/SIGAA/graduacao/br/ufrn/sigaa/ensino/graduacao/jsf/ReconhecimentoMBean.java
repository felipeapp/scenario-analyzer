/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 13/02/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.ReconhecimentoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Reconhecimento;


/**
 * MBean para cadastro de reconhecimentos
 *
 * @author André
 *
 */
@Component("reconhecimento") @Scope("session")
public class ReconhecimentoMBean extends SigaaAbstractController<Reconhecimento> {

	/** Define o link para o menu principal. */
	public static final String JSP_MENU_GRADUACAO = "menuGraduacao";

	/**
	 * Coleção de selectItens com as possíveis matrizes para popular
	 * o select no formulário a partir do reconhecimento encontrado
	 */
	private List<SelectItem> possiveisMatrizes = new ArrayList<SelectItem>(0);

	
	/** Indica o filtro para a busca por portaria/decreto utilizado na busca. */
	private boolean chkPortaria;
	/** Indica o filtro para a busca por matriz curricular utilizado na busca. */
	private boolean chkMatriz;
	/** Indica o filtro para a busca por curso utilizado na busca. */
	private boolean chkCurso;
	/** Indica o filtro para retornar todos os reconhecimentos de curso. */
	private boolean chkTodos;
	/** Atributo responsável por armazenar a portaria/decreto utilizados como filtro na busca por reconhecimentos */
	private String portaria = null;
	/** Atributo responsável por armazenar a matriz curricular utilizada como filtro na busca por reconhecimentos */
	private Integer idMatriz = null;
	/** Atributo responsável por armazenar o curso utilizada como filtro na busca por reconhecimentos */
	private Integer idCurso = null;
	
	/**
	 * Construtor
	 */
	public ReconhecimentoMBean() {
		initObj();
	}

	/** 
	 * Inicializa os atributos do controller. 
	 */
	private void initObj() {
		obj = new Reconhecimento();
		obj.setMatriz(new MatrizCurricular());
		obj.getMatriz().setCurso(new Curso());
		this.chkPortaria = false;
		this.chkMatriz = false;
		this.chkCurso = false;
		this.chkTodos = false;
		resultadosBusca = null;
	}

	/**
	 * Inicializa os atributos do controller após um cadastro
	 * 
	 * @throws ArqException
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		if ( getResultadosBusca() != null ){
			for (Iterator<Reconhecimento> it = getResultadosBusca().iterator(); it.hasNext();) {
				Reconhecimento reconhecimento = it.next();
				if( reconhecimento.getId() == obj.getId() ){
					it.remove();
					getResultadosBusca().add(obj);
					break;
				}
			}
		}
		obj = new Reconhecimento();
		obj.setMatriz(new MatrizCurricular(idMatriz!=null?idMatriz:0));
		obj.getMatriz().setCurso(new Curso(idCurso!=null?idCurso:0));
		obj.setPortariaDecreto(portaria);
	}
	
	@Override
	public void afterRemover() {
		initObj();
	}
	
	@Override
	public String getDirBase() {
		return "/graduacao/reconhecimento";
	}

	/**
	 * Cancela a operação. Caso Id do objeto seja zero, então redireciona para a página
	 * principal de graduação. Caso contrário, redireciona para a listagem de reconhecimentos.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/reconhecimento/form.jsp</li>
	 * 	</ul>
	 * 
	 * @return
	 */
	public String cancelarCadastro() {
		if (obj.getId() > 0){
			obj = new Reconhecimento();
			obj.setMatriz(new MatrizCurricular(idMatriz!=null?idMatriz:0));
			obj.getMatriz().setCurso(new Curso(idCurso!=null?idCurso:0));
			obj.setPortariaDecreto(portaria);
			return forward( getListPage() );
		} else {
			return cancelar();
		}
	}

	/**
	 * Cadastra um reconhecimento
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 	  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/reconhecimento/form.jsp</li>
	 * 	</ul>
	 * 
	 * @throws SegurancaException
	 * @return
	 */
	@Override
	public String preCadastrar() throws SegurancaException {
		initObj();
		setReadOnly(false);
		checkRole(SigaaPapeis.CDP);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	/**
	 * Busca reconhecimento(s) de acordo com o parâmetro 'param'
	 * <br/ >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li>JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/reconhecimento/lista.jsp</li>
	 * 	</ul>
	 * 
	 * @throws Exception
	 */
	@Override
	public String buscar() throws Exception {

		validacaoFiltros();
		portaria = null;
		idMatriz = null;
		idCurso = null;
		
		if( hasErrors() )
			return null;
		
		if ( isChkPortaria() )
			portaria = obj.getPortariaDecreto();
		if ( isChkMatriz() )
			idMatriz = obj.getMatriz().getId();
		if ( isChkCurso() )
			idCurso = obj.getMatriz().getCurso().getId();	
		if ( isChkTodos() ) {
			portaria = null;
		  	idMatriz = null;
		} 
	
		ReconhecimentoDao dao = getDAO(ReconhecimentoDao.class);
		setResultadosBusca(dao.findByMatrizAndPortaria(idMatriz, portaria, idCurso));

		if (resultadosBusca == null || resultadosBusca.isEmpty()){
			addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
		}
		
		return forward(getListPage());
	}

	/**
	 * Prepara para a remoção do reconhecimento preenchendo o formulário da jsp
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/reconhecimento/lista.jsp</li>
	 * 	</ul>
	 */
	@Override
	public String preRemover() {
		try {
			checkRole(SigaaPapeis.CDP);
			setReadOnly(true);
			obj = getGenericDAO().findByPrimaryKey(getParameterInt("id"), Reconhecimento.class);
			carregarMatrizes(null);
		} catch (ArqException e) {
			return tratamentoErroPadrao(e);
		}
		return super.preRemover();
	}

	/**
	 * Prepara para a atualização do reconhecimento preenchendo o formulário da jsp
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/reconhecimento/lista.jsp</li>
	 * 	</ul>
	 * 
	 * @throws ArqException
	 */
	@Override
	public String atualizar() throws ArqException {
		try {
			checkRole(SigaaPapeis.CDP);
		} catch (SegurancaException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return forward(getListPage());
		}
		return super.atualizar();
	}
	
	/**
	 * Atualiza a lista de possíveis matrizes
	 * 
	 * JSP: Não invocado por JSP.
	 * @throws ArqException
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		ArrayList<MatrizCurricular> matrizes = new ArrayList<MatrizCurricular>();
		matrizes.add(obj.getMatriz());
		possiveisMatrizes = toSelectItems(matrizes, "id", "descricao") ;
	}

	/**
	 * Carrega as possíveis matrizes
	 * <br />	
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * 	<ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/reconhecimento/form.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMatrizes(ValueChangeEvent e) throws DAOException {
		Integer idCurso;
		possiveisMatrizes = null;
		if (e != null)
			idCurso = new Integer( e.getNewValue().toString());
		else
			idCurso = obj.getMatriz().getCurso().getId();
		
		if (idCurso > 0) {
			// remove as matrizes curriculares que possuem ênfase
			MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
			Collection<MatrizCurricular> matrizes = dao.findAtivasByCurso(idCurso);
			filtraMatrizesComEnfase(matrizes);
			possiveisMatrizes = toSelectItems(matrizes, "id", "descricao") ;
		} else {
			possiveisMatrizes = new ArrayList<SelectItem>(0);
		}
	}
	
	/** Remove da coleção as matrizes curriculares que possuem ênfase 
	 * @param matrizes
	 */
	private void filtraMatrizesComEnfase(Collection<MatrizCurricular> matrizes) {
		if (!isEmpty(matrizes)) {
			Iterator<MatrizCurricular> iterator = matrizes.iterator();
			while (iterator.hasNext())
				if (!isEmpty(iterator.next().getEnfase()))
						iterator.remove();
		}
	}
	
	/**
	 * Retorna uma coleção de selectItens com as possíveis matrizes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/reconhecimento/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public List<SelectItem> getPossiveisMatrizes() throws ArqException {
		if(obj.getMatriz().getCurso().getId() > 0)
			return possiveisMatrizes;
		else {
			// filtra as matrizes curriculares com ênfase
			MatrizCurricularMBean mcMBean = new MatrizCurricularMBean();
			Collection<MatrizCurricular> matrizes = mcMBean.getAll();
			filtraMatrizesComEnfase(matrizes);
			return toSelectItems(matrizes, "id", "descricao") ;
		}	
	}
	
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	protected String forwardRemover() {
		return getListPage();	
	}
	
	@Override
	public boolean isReadOnly() {
		return obj.getId() > 0;
	}
	
	/** Método responsável por verificar ser a operação é de remoção de objetos.
	 *	<br/>
	 * Método chamado pela(s) seguintes(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/reconhecimento/form.jsp</li>
	 * </ul>
	 */
	public boolean isRemover(){
		return getConfirmButton().equalsIgnoreCase("remover");
	}
	
	/**
	 * Redireciona para tela de busca do de reconhecimento
	 * <br/>
	 * Método chamado pela(s) seguintes(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		initObj();
		return super.listar();
	}

	public boolean isChkPortaria() {
		return chkPortaria;
	}

	public void setChkPortaria(boolean chkPortaria) {
		this.chkPortaria = chkPortaria;
	}

	public boolean isChkMatriz() {
		return chkMatriz;
	}

	public void setChkMatriz(boolean chkMatriz) {
		this.chkMatriz = chkMatriz;
	}

	public boolean isChkTodos() {
		return chkTodos;
	}

	public void setChkTodos(boolean chkTodos) {
		this.chkTodos = chkTodos;
	}
	
	public boolean isChkCurso() {
		return chkCurso;
	}
	
	public void setChkCurso(boolean chkCurso) {
		this.chkCurso = chkCurso;
	}

	
	/**
	 * Método responsável por realizar a validação dos filtros de buscas utilizados no caso de uso.
	 */
	private void validacaoFiltros() {
		
		if ( !isChkPortaria() && !isChkMatriz() && !isChkTodos() && !isChkCurso() ) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}else{
			if ( isChkPortaria() && isEmpty( obj.getPortariaDecreto() ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
						"Portaria/Decreto");
			} 
			if ( isChkMatriz()  && isEmpty( obj.getMatriz() ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
				"Matriz Curricular");
			}
			if ( isChkCurso()  && isEmpty( obj.getMatriz().getCurso() ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
				"Curso");
			}
		}
		
	}


}
