/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/05/2011
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dao.ExpressaoComponenteCurriculoDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ExpressaoComponenteCurriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;

/**
 * Managed Bean para o cadastro de expressões de co-requisitos e pre-requisitos específicas
 * para um componente curricular em um currículo.
 * 
 * @author Victor Hugo
 */
@Component("expressaoComponenteCurriculoBean") @Scope("request")
public class ExpressaoComponenteCurriculoMBean extends SigaaAbstractController<ExpressaoComponenteCurriculo> {

	/** Expressão que define o pré-requisito do componente curricular. */
	private String preRequisitoForm;
	/** Expressão que define o co-requisito do componente curricular. */
	private String coRequisitoForm;
	/** Lista contendo as expressões do componente curricular. */
	private List<ExpressaoComponenteCurriculo> expressoes;
	
	public ExpressaoComponenteCurriculoMBean() {
		obj = new ExpressaoComponenteCurriculo();
		obj.setCurriculo(new Curriculo());
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		int id = getParameterInt("id");
		GenericDAO dao = getGenericDAO();
		
		obj.setComponente( dao.findAndFetch(id, ComponenteCurricular.class, "detalhes") );
		expressoes = (List<ExpressaoComponenteCurriculo>) dao.findByExactField(ExpressaoComponenteCurriculo.class, "componente.id", id); 
		
		prepareMovimento(ArqListaComando.ALTERAR);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		setConfirmButton( "Cadastrar" );
		return super.preCadastrar();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		
		validateRequired(obj.getCurriculo(), "Curriculo", erros);
		if( isEmpty(preRequisitoForm) && isEmpty(coRequisitoForm) ){
			addMensagemErro("É necessário informar a expressão de co-requisito ou pre-requisito.");
		}
		
		/**
		 * validando expressões
		 */
		try {
			ExpressaoUtil.buildExpressaoToDB(preRequisitoForm, dao, obj.getComponente(), true);
		} catch (Exception e) {
			addMensagem(MensagensGraduacao.EXPRESSAO_PREREQUISITO_MAL_FORMADA);
		}
		try {
			ExpressaoUtil.buildExpressaoToDB(coRequisitoForm, dao, obj.getComponente(), true);
		} catch (Exception e) {
			addMensagem(MensagensGraduacao.EXPRESSAO_COREQUISITO_MAL_FORMADA);
		}
		
		
		if( hasErrors() )
			return null;
		
		Collection<ComponenteCurricular> componentesRefereciados = new ArrayList<ComponenteCurricular>();
		componentesRefereciados.addAll( ExpressaoUtil.expressaoCodigoToComponentes(preRequisitoForm) );
		componentesRefereciados.addAll( ExpressaoUtil.expressaoCodigoToComponentes(coRequisitoForm) );
		
		if (!isEmpty(componentesRefereciados)) {
			String[] codigos = new String[componentesRefereciados.size()];
			int index = 0;
			for (ComponenteCurricular cc : componentesRefereciados) {
				codigos[index++] = cc.getCodigo();
			}
			
			componentesRefereciados = dao.findByCodigos(codigos);
		}
		
		for (ComponenteCurricular componenteCurricular : componentesRefereciados) {
			if (!componenteCurricular.isAtivo() && 
					(componenteCurricular.getStatusInativo() == ComponenteCurricular.DESATIVADO || componenteCurricular.getStatusInativo() == ComponenteCurricular.CADASTRO_NEGADO) ){
				if (componenteCurricular.isGraduacao())
					erros.addWarning(componenteCurricular.getCodigo() + " pode ser usado como Co-Requisito ou Pré-Requisito, no entanto é um componente inativo e foi removido ou teve a solicitação negada pelo "+
						RepositorioDadosInstitucionais.get("siglaCDP")+".");
				else
					erros.addErro(componenteCurricular.getCodigo() + " não pode ser usado como Co-Requisito ou Pré-Requisito porque é um componente inativo e foi removido ou teve a solicitação negada pelo "+
							RepositorioDadosInstitucionais.get("siglaCDP")+"." );
			}	
		}
		
		if( hasErrors() )
			return null;
		
		obj.getComponente().setPreRequisito(preRequisitoForm);
		obj.getComponente().setCoRequisito(coRequisitoForm);
		
		ExpressaoUtil.buildExpressaoToDB(obj.getComponente(), dao, true);
		
		obj.setPrerequisito( obj.getComponente().getPreRequisito() );
		obj.setCorequisito( obj.getComponente().getCoRequisito() );
		
		super.cadastrar();
		return redirect( getSubSistema().getLink() );
	}

	/**
	 * Retorna uma lista de currículos de acordo com a matriz curricular selecionada.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getCurriculos() throws DAOException {
		List<SelectItem> curriculos = new ArrayList<SelectItem>();
		curriculos.add(new SelectItem("0", "--> SELECIONE <--"));
		
		Collection<Curriculo> col = getDAO(EstruturaCurricularDao.class).findByComponente( obj.getComponente().getId() );
		if (!isEmpty(col))
			curriculos.addAll(toSelectItems(col, "id", "descricaoCursoCurriculo"));
		
		return curriculos; 
	}
	
	/**
	 * Carrega a ExpressaoComponenteCurriculo por ajax de acordo com o curriculo selecionado na interface.
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/ensino/ExpressaoComponenteCurriculo/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws ArqException 
	 */
	public void carregarExpComponenteCurriculoSelecionado(ValueChangeEvent e) throws ArqException {
		
		int idCurriculo = (Integer) e.getNewValue();
		int idComponente =  obj.getComponente().getId();
		
		ExpressaoComponenteCurriculoDao dao = getDAO(ExpressaoComponenteCurriculoDao.class);
		ExpressaoComponenteCurriculo expressao = dao.findByComponenteCurriculo(idCurriculo, idComponente);
		
		if( expressao != null ){
			obj = expressao;
			obj.getComponente().getId();
			obj.getComponente().getDetalhes();
			
			obj.getComponente().setPreRequisito( obj.getPrerequisito() );
			obj.getComponente().setCoRequisito( obj.getCorequisito() );
			
			ComponenteCurricularDao daoComp = getDAO(ComponenteCurricularDao.class);
			ExpressaoUtil.buildExpressaoFromDB(obj.getComponente(), daoComp);
			
			preRequisitoForm = obj.getComponente().getPreRequisito();
			coRequisitoForm = obj.getComponente().getCoRequisito();
			
			setConfirmButton( "Alterar" );
			System.out.println( preRequisitoForm );
			System.out.println( coRequisitoForm );
			
		}else{
			ComponenteCurricular comp = obj.getComponente();
			obj = new ExpressaoComponenteCurriculo();
			obj.setComponente(comp);
			obj.setCurriculo( new Curriculo() );
			obj.getCurriculo().setId( idCurriculo );
			
			preRequisitoForm = "";
			coRequisitoForm = "";
			
			setConfirmButton( "Cadastrar" );
		}
		
	}
	
	@Override
	public String getDirBase() {
		return "/ensino/ExpressaoComponenteCurriculo";
	}

	public String getPreRequisitoForm() {
		return preRequisitoForm;
	}

	public void setPreRequisitoForm(String preRequisitoForm) {
		this.preRequisitoForm = preRequisitoForm;
	}

	public String getCoRequisitoForm() {
		return coRequisitoForm;
	}

	public void setCoRequisitoForm(String coRequisitoForm) {
		this.coRequisitoForm = coRequisitoForm;
	}


	public List<ExpressaoComponenteCurriculo> getExpressoes() {
		return expressoes;
	}


	public void setExpressoes(List<ExpressaoComponenteCurriculo> expressoes) {
		this.expressoes = expressoes;
	}

}