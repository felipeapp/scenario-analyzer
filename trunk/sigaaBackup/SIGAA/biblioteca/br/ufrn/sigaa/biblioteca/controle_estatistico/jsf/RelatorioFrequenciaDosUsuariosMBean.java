/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em 14/07/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.FrequenciaUsuariosBibliotecaDao;

/**
 * <p> Gera o relat�rio de frequ�ncia de usu�rio por per�odo.
 * 
 * <p> O relat�rio antigo, que dividia os resultados por meses, est� em
 *  RegistroFrequenciaUsuariosBibliotecaMBean.iniciarRelatorioAnual.
 */
@Component("relatorioFrequenciaDosUsuariosMBean")
@Scope("request")
public class RelatorioFrequenciaDosUsuariosMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** O caminho para o relat�rio. */
	public static final String CAMINHO = "/biblioteca/controle_estatistico/relatorioFrequenciaDosUsuarios.jsp";

	/** Guarda os resultados do relat�rio. */
	private int[] resultados;

	public RelatorioFrequenciaDosUsuariosMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de Frequ�ncia dos Usu�rios");
		setDescricao("<p>Neste relat�rio � poss�vel recuperar as informa��es sobre a quantidade de usu�rio que frequentaram a biblioteca por  per�odo e turno.</p>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorPeriodo(true);
		
		setInicioPeriodo( CalendarUtils.createDate(1, 0, CalendarUtils.getAnoAtual() ) );
		setFimPeriodo( CalendarUtils.createDate(31, 11, CalendarUtils.getAnoAtual() ) );
	}
	/**
	 * Gera o relat�rio. � chamado, indiretamente, pela JSP: <br/>
	 * <tt>sigaa.war/biblioteca/controle_estatistico/formGeral.jsp</tt>
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		FrequenciaUsuariosBibliotecaDao dao = null;
		try {
			dao = getDAO(FrequenciaUsuariosBibliotecaDao.class);
			
			resultados = dao.findFrequenciaPorPeriodo(
					UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
			
		} finally {
			if ( dao != null ) dao.close();
		}
		
		return forward(CAMINHO);
	}
	
	////// gets e sets
	
	public int[] getResultados() { return resultados; }
	public int getTotal() { return resultados[0] + resultados[1] + resultados[2]; }
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}
	
}
