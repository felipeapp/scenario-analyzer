package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatoriosCoordenadorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.RelatoriosCoordenadorMBean;


/**
 * MBean respons�vel controlar a gera��o do relat�rio de reprova��es por curso/disciplina
 * @author Victor Hugo
 */
@Component("relatorioDisciplinasReprovacoesBean") @Scope("request")
public class RelatorioDisciplinasReprovacoesMBean extends SigaaAbstractController {

	/**
	 * campos utilizados como filtro no relat�rio
	 */
	private int ano, periodo;
	
	private Curso curso;
	private ComponenteCurricular componente;
	
	/**
	 * Construtor, inicializando objetos
	 */
	public RelatorioDisciplinasReprovacoesMBean() {
		curso = new Curso();
		componente = new ComponenteCurricular();
		ano = CalendarUtils.getAnoAtual();
		System.out.println( ano );
		periodo = getPeriodoAtual();		
	}
	
	/**
	 * Inicia o caso de uso, leva o usu�rio a p�gina de configura��o dos filtros do relat�rio
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>	
	 * @return
	 */
	public String iniciar(){
		return forward("/ead/relatorios/DisciplinasReprovacoes/relatorio_disciplinas_reprovacoes_form.jsf");
	}
	
	/**
	 * Gera o relat�rio e exibe ao usu�rio
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/ead/relatorios/DisciplinasReprovacoes/relatorio_disciplinas_reprovacoes_form.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException{
		
		validateRequired(ano, "Ano", erros);
		validateRequired(periodo, "Per�odo", erros);
		validateRange(ano, 1900, 2050, "Ano", erros);
		validateRange(periodo, 1, 4, "Per�odo", erros);
		
		if( (curso == null || curso.getId() == 0 ) && (componente == null || componente.getId() == 0) ){
			addMensagemErro("� necess�rio selecionar pelo menos pelo menos o curso ou o componente.");
			return null;
		}
		
		if( hasErrors() )
			return null;
		
		RelatoriosCoordenadorDao dao = getDAO(RelatoriosCoordenadorDao.class);
		dao.initialize(curso);
		
		RelatoriosCoordenadorMBean relBean = getMBean("relatoriosCoordenador");
		relBean.setTipoRelatorio( RelatoriosCoordenadorMBean.REL_REPROVACOES_DISCIPLINAS );
		relBean.setAno(ano);
		relBean.setPeriodo(periodo);
		relBean.setCurso(curso);
		relBean.setEad(true);
		relBean.setExibeCurso(false);
		
		Curso cursoParam = null;
		if( curso != null && curso.getId() > 0 ){
			cursoParam = curso;
			relBean.setExibeCurso(true);
		}
		ComponenteCurricular componenteParam = null;
		if( componente != null && componente.getId() > 0 )
			componenteParam = componente;
		
		relBean.setComponente(componenteParam);
		
		Map<?, ?> relatorio = dao.findReprovacoesDisciplinas(ano, periodo, cursoParam, componenteParam, null, true);
		
		if( isEmpty(relatorio) ){
			addMensagemWarning("N�o foram encontradas reprova��es para os crit�rios informados.");
			return null;
		}
		
		relBean.setRelatorio( relatorio );
		
		return forward(RelatoriosCoordenadorMBean.JSP_REL_REPROVACOES_DISCIPLINAS);
	}
	
	/**
	 * Carrega os componentes do formul�rio de acordo com o curso selecionado
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/ead/relatorios/DisciplinasReprovacoes/relatorio_disciplinas_reprovacoes_form.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAllComponentesADistanciaCombo() throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		
		List<ComponenteCurricular> allComponentes = new ArrayList<ComponenteCurricular>();
		if( curso != null && curso.getId() > 0 ) 
			allComponentes = dao.findComponentesEnsinoADistancia(curso);
		else
			allComponentes = dao.findComponentesEnsinoADistancia(null);
		
		return toSelectItems(allComponentes, "id", "descricao");
	}
	
	@Override
	public String getDirBase() {
		return super.getDirBase();
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

}