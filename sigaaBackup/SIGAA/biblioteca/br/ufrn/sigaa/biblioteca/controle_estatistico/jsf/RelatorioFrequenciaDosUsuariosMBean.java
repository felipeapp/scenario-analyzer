/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Gera o relatório de frequência de usuário por período.
 * 
 * <p> O relatório antigo, que dividia os resultados por meses, está em
 *  RegistroFrequenciaUsuariosBibliotecaMBean.iniciarRelatorioAnual.
 */
@Component("relatorioFrequenciaDosUsuariosMBean")
@Scope("request")
public class RelatorioFrequenciaDosUsuariosMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** O caminho para o relatório. */
	public static final String CAMINHO = "/biblioteca/controle_estatistico/relatorioFrequenciaDosUsuarios.jsp";

	/** Guarda os resultados do relatório. */
	private int[] resultados;

	public RelatorioFrequenciaDosUsuariosMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Frequência dos Usuários");
		setDescricao("<p>Neste relatório é possível recuperar as informações sobre a quantidade de usuário que frequentaram a biblioteca por  período e turno.</p>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorPeriodo(true);
		
		setInicioPeriodo( CalendarUtils.createDate(1, 0, CalendarUtils.getAnoAtual() ) );
		setFimPeriodo( CalendarUtils.createDate(31, 11, CalendarUtils.getAnoAtual() ) );
	}
	/**
	 * Gera o relatório. É chamado, indiretamente, pela JSP: <br/>
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}
	
}
