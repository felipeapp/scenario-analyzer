/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 09/04/2010
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dao.EquivalenciaEspecificaDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Managed Bean para o cadastro de equivalências específicas
 * para um componente curricular em um currículo.
 * 
 * @author David Pereira
 */
@Component @Scope("session")
public class EquivalenciaEspecificaMBean extends SigaaAbstractController<EquivalenciaEspecifica> {

	/**  Curso selecionado para cadastro da equivalência. */
	private Curso curso;
	
	/** Matriz selecionada para cadastro da equivalência. */
	private MatrizCurricular matriz;
	
	/** Indica o filtro para a busca por curso utilizado na busca. */
	private boolean chkCurso;
	/** Indica o filtro para a busca por matriz curricular utilizado na busca. */
	private boolean chkMatriz;
	/** Indica o filtro para a busca por currículo utilizado na busca. */
	private boolean chkCurriculo;
	/** Indica o filtro para a busca por componente curricular. */
	private boolean chkComponente;
	
	/** Atributo responsável por armazenar o curso utilizada como filtro na busca. */
	private Integer idCurso = null;
	/** Atributo responsável por armazenar a matriz curricular utilizada como filtro na busca. */
	private Integer idMatriz = null;
	/** Atributo responsável por armazenar o currículo utilizados como filtro na busca. */
	private Integer idCurriculo = null;
	/** Atributo responsável por armazenar a disciplina utilizada como filtro na busca. */
	private Integer idDisciplina = null;
	
	
	public EquivalenciaEspecificaMBean() {
		reset();
	}
	
	/**
	 * Limpa os dados do Managed Bean.
	 */
	private void reset() {
		obj = new EquivalenciaEspecifica();
		obj.setCurriculo(new Curriculo());
		obj.setComponente(new ComponenteCurricular());
		
		curso = new Curso();
		matriz = new MatrizCurricular();
	}
	
	@Override
	protected void doValidate() throws ArqException {
		if (obj.getFimVigencia() != null && obj.getFimVigencia().before(obj.getInicioVigencia())) {
			addMensagemErro("O fim da vigência deve ser posterior ao início.");
		}
		
		if (!hasErrors()) {
			ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
			ComponenteCurricular cc = dao.findByPrimaryKey(obj.getComponente().getId(), ComponenteCurricular.class);
			
			if (cc.getEquivalencia() == null && obj.getExpressao().contains(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL)) {
				addMensagemErro("Não é possível utilizar a equivalência global porque ela não está definida no componente curricular.");
				return;
			}

			String expressao = obj.getExpressao();
			if (cc.getEquivalencia() != null && obj.getExpressao().contains(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL)) {
				expressao = expressao.replaceAll(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL, ExpressaoUtil.buildExpressaoFromDB(cc.getEquivalencia(), dao));
			}
			
			try {
				String expressaoCompilada = ExpressaoUtil.buildExpressaoToDB(expressao, dao, cc, null);
				ArvoreExpressao.fromExpressao(expressaoCompilada);
				
				if (cc.getEquivalencia() != null && obj.getExpressao().contains(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL)) {
					obj.setExpressao(expressaoCompilada.replace(cc.getEquivalencia(), " " + EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL + " ").trim());
				} else {
					obj.setExpressao(expressaoCompilada);
				}
			} catch(ArqException e) {
				e.printStackTrace();
				addMensagemErro(e.getMessage());
			} catch(Exception e) {
				e.printStackTrace();
				addMensagemErro("Expressão de equivalência mal-formada: " + StringUtils.converteJavaToHtml(obj.getExpressao()));
			}
		}
	}
	
	@Override
	public void afterAtualizar() throws ArqException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		String expressaoGeral = null;
		if (obj.getComponente().getEquivalencia() != null)
			expressaoGeral = ExpressaoUtil.buildExpressaoFromDB(obj.getComponente().getEquivalencia(), dao);
		String expressao = obj.getExpressao();
		if (obj.getComponente().getEquivalencia() != null)
			expressao = expressao.replaceAll(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL, obj.getComponente().getEquivalencia());
		
		expressao = ExpressaoUtil.buildExpressaoFromDB(expressao, dao);
		
		if (expressaoGeral != null)
			obj.setExpressao(expressao.replace(expressaoGeral, " " + EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL + " ").trim());
		else
			obj.setExpressao(expressao);
		matriz = obj.getCurriculo().getMatriz();
		curso = matriz.getCurso();
	}

	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	@Override
	public String getFormPage() {
		return "/ensino/EquivalenciaEspecifica/form.jsf";
	}

	@Override
	public String getListPage() {
		return "/ensino/EquivalenciaEspecifica/lista.jsf";
	}
	
	/**
	 * Busca reconhecimento(s) de acordo com o parâmetro 'param'
	 * <br/ >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li>JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/EquivalenciaEspecifica/lista.jsp</li>
	 * 	</ul>
	 * 
	 * @throws Exception
	 */
	@Override
	public String buscar() throws Exception {

		validacaoFiltros();
		idCurso = null;
		idMatriz = null;
		idCurriculo = null;
		idDisciplina = null;
		setResultadosBusca(null);
		
		if( hasErrors() )
			return null;
		
		if ( isChkCurso() )
			idCurso = curso.getId();	
		if ( isChkMatriz() )
			idMatriz = matriz.getId();
		if ( isChkCurriculo() )
			idCurriculo = obj.getCurriculo().getId();
		if ( isChkComponente() ) {
			idDisciplina = obj.getComponente().getId();
		} 
	
		EquivalenciaEspecificaDao dao = getDAO(EquivalenciaEspecificaDao.class);
		setResultadosBusca(dao.findByCursoMatrizAndDisciplina(idCurso, idMatriz, idCurriculo, idDisciplina));

		if (resultadosBusca == null || resultadosBusca.isEmpty()){
			addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
		}
		
		return forward(getListPage());
	}
	
	/**
	 * Método responsável por realizar a validação dos filtros de buscas utilizados no caso de uso.
	 */
	private void validacaoFiltros() {
		
		if ( !isChkCurso() && !isChkMatriz() && !isChkCurriculo() && !isChkComponente() ) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}else{
			if ( isChkCurso()  && isEmpty( curso ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
						"Curso");
			}
			if ( isChkMatriz()  && isEmpty( matriz ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
						"Matriz Curricular");
			}
			if ( isChkCurriculo() && isEmpty( obj.getCurriculo() ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
						"Currículo");
			} 
			if ( isChkComponente() && isEmpty( obj.getComponente() ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
						"Componente Curricular");
			}
		}
		
	}
	
	@Override
	public String listar() throws ArqException {
		if (obj != null && obj.getId() > 0){
			if (!isChkCurso())
				curso = new Curso();
			if (!isChkMatriz())
				matriz = new MatrizCurricular();
			if (!isChkCurriculo())
				obj.setCurriculo(new Curriculo());
			if (!isChkComponente())
				obj.setComponente(new ComponenteCurricular());
		}
		return super.listar();
	}
	
	/**
	 * Método responsável por inativar ou ativar equivalências especificas de especificas de graduação.
	 * <br/ >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li>JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/EquivalenciaEspecifica/lista.jsp</li>
	 * 	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String inativarOuAtivar() throws ArqException, NegocioException {
		setId();
		EquivalenciaEspecificaDao dao = getDAO(EquivalenciaEspecificaDao.class);
		setObj( dao.findByPrimaryKey(obj.getId(), EquivalenciaEspecifica.class) );
		if ( obj.isAtivo() ){
			dao.updateField(EquivalenciaEspecifica.class, obj.getId(), "ativo", false);
		} else {
			dao.updateField(EquivalenciaEspecifica.class, obj.getId(), "ativo", true);
		}
		setResultadosBusca(dao.findByCursoMatrizAndDisciplina(idCurso, idMatriz, idCurriculo, idDisciplina));
		
		return listar();
	}
	
	/**
	 * Retorna uma lista de {@link SelectItem} representando as matrizes curriculares de um curso.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getMatrizesCurriculares() throws DAOException {
		List<SelectItem> matrizesCurriculares = new ArrayList<SelectItem>();
		matrizesCurriculares.add(new SelectItem("0", "-- SELECIONE --"));
		if (curso.getId() > 0) {
			matrizesCurriculares.addAll(toSelectItems(getDAO(MatrizCurricularDao.class).findAtivasByCurso(curso.getId()), "id", "descricaoMin"));
		}
		
		return matrizesCurriculares;
	}
	
	/**
	 * Retorna uma lista de currículos de acordo com a matriz curricular selecionada.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getCurriculos() throws DAOException {
		List<SelectItem> curriculos = new ArrayList<SelectItem>();
		curriculos.add(new SelectItem("0", "-- SELECIONE --"));
		if (matriz.getId() > 0) {
			Collection<Curriculo> col = getDAO(EstruturaCurricularDao.class).findByMatriz(matriz.getId());
			if (!isEmpty(col))
				curriculos.addAll(toSelectItems(col, "id", "descricao"));
		}	
		
		return curriculos;
	}

	public Curso getCurso() {
		return curso;
	}
	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	public MatrizCurricular getMatriz() {
		return matriz;
	}
	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}
	public boolean isChkCurso() {
		return chkCurso;
	}
	public void setChkCurso(boolean chkCurso) {
		this.chkCurso = chkCurso;
	}
	public boolean isChkMatriz() {
		return chkMatriz;
	}
	public void setChkMatriz(boolean chkMatriz) {
		this.chkMatriz = chkMatriz;
	}
	public boolean isChkCurriculo() {
		return chkCurriculo;
	}
	public void setChkCurriculo(boolean chkCurriculo) {
		this.chkCurriculo = chkCurriculo;
	}
	public boolean isChkComponente() {
		return chkComponente;
	}
	public void setChkComponente(boolean chkComponente) {
		this.chkComponente = chkComponente;
	}

	@Override
	protected void afterInativar() {
		all = null;
		super.afterInativar();
	}
	
	@Override
	protected void beforeInativar() {
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			tratamentoErroPadrao(e, "Não foi possível preparar a operação de inativação de equivalência específica");
		}
		super.beforeInativar();
	}
}
