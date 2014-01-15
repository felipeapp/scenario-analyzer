/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 13/06/2011
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioEmprestimosPorTurnoDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.ConstantesRelatorioBiblioteca;

/**
 * MBean que controla a geração do relatório de quantidade de empréstimos por turno.
 *
 * @author Bráulio
 */
@Component("relatorioEmprestimosPorTurnoMBean")
@Scope("request")
public class RelatorioEmprestimosPorTurnoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** Página de exibição dos resultados. */
	private static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioEmprestimosPorTurno.jsp";

	/** Lista dos resultados. */
	private Map<String, Map<Integer, Integer>> resultados;
	
	/** Total de empréstimos/renovações no turno da manhã. */
	private int totalManha;
	
	/** Total de empréstimos/renovações no turno da tarde. */
	private int totalTarde;
	
	/** Total de empréstimos/renovações no turno da noite. */
	private int totalNoite;
	
	/** Total de empréstimos/renovações em todos os turnos. */
	private int total;
	
	/**
	 * Construtor
	 */
	public RelatorioEmprestimosPorTurnoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Empréstimos por Turno");
		setDescricao("Neste relatório, o usuário pode visualizar e comparar em que período do dia os empréstimos estão sendo realizados.");
		setCampoBibliotecaObrigatorio(false);
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorTurno(true);
		setFiltradoPorPeriodo(true);
		
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);
		
	}

	/**
	 * Chamado, indiretamente, por <em>sigaa.war/biblioteca/controle_estatistico/formGeral.jsp</em>
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {

		RelatorioEmprestimosPorTurnoDAO dao = getDAO(RelatorioEmprestimosPorTurnoDAO.class);

		
		
		/**
		 * Retorna:
		 * 
		 * Map<String, Map<Integer, Integer>>
		 * 
		 * Onde:
		 * 
		 * Chave: Biblioteca 1
		 *              Chave: Turno
		 *              Valor: Quantidade
		 *              
		 * Chave: Biblioteca 2
		 *              Chave: Turno
		 *              Valor: Quantidade
		 *  
		 * Chave: Biblioteca 3
		 *              Chave: Turno
		 *              Valor: Quantidade                          
		 * 
		 */
		resultados = dao.findQtdEmprestimosPorTurno(UFRNUtils.toInteger(variasBibliotecas), turno, inicioPeriodo, fimPeriodo);

		totalManha = 0;
		totalTarde = 0;
		totalNoite = 0;
		
		for (String biblioteca : resultados.keySet()) {
			for (Integer turno : resultados.get(biblioteca).keySet()) {
				int totalParcial = resultados.get(biblioteca).get(turno);
				
				if (turno == ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor()) {
					totalManha += totalParcial;
				} else if (turno == ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor()) {
					totalTarde += totalParcial;
				} else if (turno == ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor()) {
					totalNoite += totalParcial;
				}
				else {
					throw new IllegalArgumentException("Turno inválido.");
				}
			}
		}
		
		total = totalManha + totalTarde + totalNoite;
		
		if(total == 0 ){
			addMensagemWarning("Não foram encontrados resultados para o período informado.");
			return null;
		}
		
		return forward(PAGINA_DO_RELATORIO);
	}

	//// GETs e SETs ////
	
	public Map<String, Map<Integer, Integer>> getResultados() { return resultados; }
	public int getTotalManha() { return totalManha; }
	public int getTotalTarde() { return totalTarde; }
	public int getTotalNoite() { return totalNoite; }
	public int getTotal() { return total; }
	

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
