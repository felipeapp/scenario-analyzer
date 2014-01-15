/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 20/07/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioDeUsuariosComPotencialEmprestimo;
import br.ufrn.sigaa.biblioteca.controle_estatistico.negocio.CosultasRelatoriosBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * Relatório que mostra a quantidade de usuários que têm a possibilidade de fazerem
 * empréstimos em alguma das bibliotecas do sistema. Inclui os usuários que ainda
 * não fizeram cadastro em nenhuma das bibliotecas.
 * 
 * @author Bráulio Bezerra
 */
@Component("relatorioUsuariosComPotencialDeEmprestimoMBean")
@Scope("request")
public class RelatorioUsuariosComPotencialDeEmprestimoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** A página que implementa o relatório. */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioUsuariosComPotencialDeEmprestimo.jsp";
	
	/** Os resultados do relatório. */
	private final SortedMap<String, Integer> resultados = new TreeMap<String, Integer>();
	
	/** A totalização final do relatório. */
	private int total;

	public RelatorioUsuariosComPotencialDeEmprestimoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Usuários com Potencial de Empréstimo");
		
		setDescricao("Este relatório mostra a quantidade de usuários, agrupados por " +
				"categoria, que têm a possibilidade de utilizar o serviço de empréstimo. " +
				"Mostra o universo total de usuários da instituição, ou seja, " +
				"ele inclui os usuários que ainda não fizeram nenhum empréstimo ou " +
				"mesmo que ainda não se cadastraram para utilizar os serviços das bibliotecas.");
		
		setFiltradoPorCategoriaDeUsuario(true);
	}
	
	/**
	 * <p>Faz a consulta no banco, através do DAO e gera o relatório.
	 * <p>Chamado, indiretamente, pela JSP <tt>/sigaa.war/biblioteca/controle_estatistico/formGeral.jsp</tt>.
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioDeUsuariosComPotencialEmprestimo dao = null; 
		
		try{
			
			dao = getDAO( new CosultasRelatoriosBibliotecaFactory().getClasseGeraConsultasUsuariosComPontenciaEmprestimo());
			
			resultados.clear();
			VinculoUsuarioBiblioteca vinculo = VinculoUsuarioBiblioteca.getVinculo(this.valorVinculoDoUsuarioSelecionado);
			
			resultados.putAll( dao.findQtdUsuariosComPotencialDeEmprestimo(vinculo == VinculoUsuarioBiblioteca.INATIVO ? null : vinculo) );
			
			total = 0;
			for ( int k : resultados.values() )
				total += k;
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward( PAGINA );
	}

	public SortedMap<String, Integer> getResultados() { return resultados; }
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
