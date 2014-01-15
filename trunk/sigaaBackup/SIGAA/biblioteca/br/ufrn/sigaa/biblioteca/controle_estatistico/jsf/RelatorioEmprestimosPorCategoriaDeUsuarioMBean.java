/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 11/11/2009
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioEmprestimosPorCategoriaUsuarioDao;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * Relatório de quantidade de empréstimos + renovações por tipo de usuário.
 * @author Bráulio
 */
@Component("relatorioEmprestimosPorCategoriaDeUsuarioMBean")
@Scope("request")
public class RelatorioEmprestimosPorCategoriaDeUsuarioMBean extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * A página de exibição dos dados do relatório
	 */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioQuantitativoDeEmprestimosPorCategoriaDeUsuario.jsp";
	
	/**
	 * Conteúdo do array interno:
	 * [0] = quantidade de empréstimos + renovações de Janeiro
	 * [1] = quantidade deempréstimos + renovações de Fevereiro
	 * ...
	 * [11] = quantidade de empréstimos + renovações de Dezembro
	 * [12] = total de empréstimos + renovações
	 */
	private Map<VinculoUsuarioBiblioteca, Integer[]> resultados;
	
	/**
	 * Guarda os totais de empréstimos de cada mês do ano escolhido.
	 */
	private Integer[] totaisPorMes;
	
	public RelatorioEmprestimosPorCategoriaDeUsuarioMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		titulo = "Relatório de Empréstimos por Categoria de Usuário";
		descricao = " <p>Neste relatório é possível consultar a quantidade de empréstimos realizados por ano nas bibliotecas. " +
				" Os dados do relatório são organizados pela <strong><i>Categoria do Usuário</i></strong> e <strong>Mês</strong> dentro do ano escolhido. </p>"
			+ "<p>Observação: Para fins de contagem do relatório são considerados os empréstimos  e renovações feitos dentro do ano selecionado.</p>";

		setFiltradoPorVariasBibliotecas(true);          // pode filtrar por biblioteca
		setCampoBibliotecaObrigatorio(false);           //  se não escolher a biblioteca, busca por todas
		setFiltradoPorVariasCategoriasDeUsuario(true);  // pode escolher categorias específicas de um usuário
		
		setFiltradoPorAno(true); // mostras os meses dentro do ano escolhido.
		
	}
	
	/**
	 * Chamado indiretamente por /sigaa.war/biblioteca/controle_estatistico/formGeral.jsp
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioEmprestimosPorCategoriaUsuarioDao dao = null; 
		
		try{
			dao = getDAO(RelatorioEmprestimosPorCategoriaUsuarioDao.class);

			resultados = new TreeMap<VinculoUsuarioBiblioteca, Integer[]>(
					new Comparator<VinculoUsuarioBiblioteca>() {
						@Override
						public int compare(VinculoUsuarioBiblioteca o1, VinculoUsuarioBiblioteca o2) {
							return new Integer(o1.getValor()).compareTo(new Integer(o2.getValor()));
						}
					});
			
			Collection<Integer> idsBibliotecasEscolhidas = UFRNUtils.toInteger(variasBibliotecas);
			Collection<Integer> idsCatagoriaUsuarioEscolhidas = UFRNUtils.toInteger(variasCategoriasDeUsuario);
			
			resultados.putAll( dao.countEmprestimosERenovacoesPorCategoriaUsuario( idsBibliotecasEscolhidas, idsCatagoriaUsuarioEscolhidas, this.getAno() ) );
			
			totaisPorMes = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0};
			
			for ( int i = 0; i <= 12; i++ ) {
				totaisPorMes[i] = 0;
				for ( VinculoUsuarioBiblioteca vinculo : resultados.keySet() ) {
					totaisPorMes[i] += resultados.get(vinculo)[i];
				}
			}
			
			// se não houve resultados, mostra uma mensagem de aviso
			if ( totaisPorMes[12] == 0 ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(PAGINA);
	}

	public Map<VinculoUsuarioBiblioteca, Integer[]> getResultados() { return resultados; }
	public void setResultados(Map<VinculoUsuarioBiblioteca, Integer[]> resultados) { this.resultados = resultados; }
	public Integer[] getTotaisPorMes() { return totaisPorMes; }
	public void setTotaisPorMes(Integer[] totaisPorMes) { this.totaisPorMes = totaisPorMes; }

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
