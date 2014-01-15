/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 06/09/2010
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioRenovacoesPorModuloAcessoDao;

/**
 * MBean que controla a geração do relatório de quantidade de renovações por módulo de acesso.
 *
 * @author Bráulio
 */
@Component("relatorioRenovacoesPorModuloMBean")
@Scope("request")
public class RelatorioRenovacosPorModuloMBean extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * A página do relatório
	 */
	private static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioRenovacoesPorModulo.jsp";

	/** Lista dos módulos de acesso e a quantidade de renovações feitas. */
	private Map<String, Integer> resultados;
	
	public RelatorioRenovacosPorModuloMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Renovações por Módulo de Acesso");
		setDescricao(" <p>Neste relatório é possível visualizar o meio pelo qual os usuários estão renovando os empréstimos. </p>" +
				     " <p>As renovações de \"Dispositivos Móveis\" são renovações feitas de Celulares, Tablets e outros dispositivos.</p>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorPeriodo(true);
		campoBibliotecaObrigatorio = false;
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1);
		
	}

	/**
	 * Chamado, indiretamente, por <em>sigaa.war/biblioteca/controle_estatistico/formGeral.jsp</em>
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {

		RelatorioRenovacoesPorModuloAcessoDao dao = getDAO( RelatorioRenovacoesPorModuloAcessoDao.class );
		configuraDaoRelatorio(dao);
		
		List<Object[]> resultadosTemp = dao.findQtdRenovacoesPorModulo(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
		
		resultados = new TreeMap<String, Integer>();
		
		for (Object[] object : resultadosTemp) {
			if(RegistroEntrada.CANAL_DESKTOP.equalsIgnoreCase( ""+object[0]) ){
				resultados.put("Balcão",  ((BigInteger) object[1]).intValue() );
			}else{
				if(RegistroEntrada.CANAL_WAP.equalsIgnoreCase(""+object[0])
						|| RegistroEntrada.CANAL_WEB_MOBILE.equalsIgnoreCase(""+object[0])
						|| RegistroEntrada.CANAL_DEVICE.equalsIgnoreCase(""+object[0])){
					resultados.put("Dispositivos Móveis",  ((BigInteger) object[1]).intValue());
				}else{
					if(RegistroEntrada.CANAL_WEB.equalsIgnoreCase(""+object[0])){
						resultados.put("Web",  ((BigInteger) object[1]).intValue() );
					}else{
						resultados.put("Dispositivos Desconhecido",  ((BigInteger) object[1]).intValue() );
					}
				}
			}
		}
		
		return forward(PAGINA_DO_RELATORIO);
	}

	//// GETs e SETs ////
	
	public Map<String, Integer> getResultados() { return resultados; }

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
