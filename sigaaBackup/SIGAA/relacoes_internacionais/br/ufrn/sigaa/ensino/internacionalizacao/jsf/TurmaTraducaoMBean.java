/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/08/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * MBean para a manutenção das traduções dos atributos da {@link Turma Turma}.
 * 
 * @author Rafael Gomes
 *
 */
@Component
@Scope("request")
public class TurmaTraducaoMBean extends AbstractTraducaoElementoMBean<Turma>{

	/** Define o link para a busca geral de componente curricular. */
	private static final String JSP_BUSCA = "/lista_turma.jsf";
	
	/** Informa se o campo de Disciplina será considerado para montar a lista de objetos.*/
	private boolean filtroComponente;
	/** Informa se o campo de ano e período será considerado para montar a lista de objetos.*/
	private boolean filtroAnoPeriodo;
	/** Informa se o campo de código será considerado para montar a lista de objetos.*/
	private boolean filtroCodigo;
	

	/** 
	 * Construtor padrão. 
	 */
	public TurmaTraducaoMBean() {
		initObj();
	}
	
	/**
	 * Inicializa os campos do objeto que representa um componente para ser
	 * manipulado durante as operações.
	 * @throws NegocioException 
	 */
	private void initObj() {
		obj = new Turma();
		obj.setDisciplina(new ComponenteCurricular());
		filtroComponente = false;
		filtroAnoPeriodo = false;
		if (resultadosBusca != null)
			resultadosBusca.clear();
	}
	
	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/elemento";
	}
	
	@Override
	public String getListPage() {
		return getDirBase() + JSP_BUSCA;
	}
	
	/**
	 * Inicia o caso de uso de atualização.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkChangeRole();
		initObj();
		prepareMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		setConfirmButton("Cadastrar");
		return forward(getListPage());
	}

	@Override
	public String buscar() throws Exception {
		TurmaDao dao = getDAO(TurmaDao.class);
		
		ValidatorUtil.validateRequiredId(obj.getDisciplina().getId(), "Disciplina", erros);
		ValidatorUtil.validateRequired(obj.getDisciplina().getNome(), "Disciplina", erros);
		
		if (isFiltroAnoPeriodo()){
			ValidatorUtil.validateRequiredId(obj.getAno(), "Ano", erros);
			ValidatorUtil.validateRequiredId(obj.getPeriodo(), "Período", erros);
		} else {
			obj.setAno(0);
			obj.setPeriodo(0);
		}
		
		if (isFiltroCodigo())
			ValidatorUtil.validateRequired(obj.getCodigo(), "Código", erros);
		
		if (hasErrors()) return null;
		
		setResultadosBusca(dao.findByDisciplinaAnoPeriodoSituacao(obj.getDisciplina(), obj.getAno(), obj.getPeriodo(), (SituacaoTurma[]) null));
		
		if(resultadosBusca.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return null;
	}
	
	/**
	 * Método responsável por instanciar o objeto selecionado na listagem 
	 * e prepará-lo para a tradução de seus atributos.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/elemento/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionar() throws ArqException {
		setOperacaoAtiva(SigaaListaComando.TRADUZIR_ELEMENTO.getId());
		prepareMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		
		int id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(id, obj.getClass());
		
		carregarElementosTraducao(obj.getClass(), obj.getId(), Order.asc("nome"));
		return forward(getDirBase() + "/internacionalizar_turma.jsp");
	}

	public boolean isFiltroComponente() {
		return filtroComponente;
	}

	public void setFiltroComponente(boolean filtroComponente) {
		this.filtroComponente = filtroComponente;
	}

	public boolean isFiltroAnoPeriodo() {
		return filtroAnoPeriodo;
	}

	public void setFiltroAnoPeriodo(boolean filtroAnoPeriodo) {
		this.filtroAnoPeriodo = filtroAnoPeriodo;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}
	
}