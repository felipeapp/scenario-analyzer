/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Relat�rio que mostra a quantidade de usu�rios que t�m a possibilidade de fazerem
 * empr�stimos em alguma das bibliotecas do sistema. Inclui os usu�rios que ainda
 * n�o fizeram cadastro em nenhuma das bibliotecas.
 * 
 * @author Br�ulio Bezerra
 */
@Component("relatorioUsuariosComPotencialDeEmprestimoMBean")
@Scope("request")
public class RelatorioUsuariosComPotencialDeEmprestimoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** A p�gina que implementa o relat�rio. */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioUsuariosComPotencialDeEmprestimo.jsp";
	
	/** Os resultados do relat�rio. */
	private final SortedMap<String, Integer> resultados = new TreeMap<String, Integer>();
	
	/** A totaliza��o final do relat�rio. */
	private int total;

	public RelatorioUsuariosComPotencialDeEmprestimoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de Usu�rios com Potencial de Empr�stimo");
		
		setDescricao("Este relat�rio mostra a quantidade de usu�rios, agrupados por " +
				"categoria, que t�m a possibilidade de utilizar o servi�o de empr�stimo. " +
				"Mostra o universo total de usu�rios da institui��o, ou seja, " +
				"ele inclui os usu�rios que ainda n�o fizeram nenhum empr�stimo ou " +
				"mesmo que ainda n�o se cadastraram para utilizar os servi�os das bibliotecas.");
		
		setFiltradoPorCategoriaDeUsuario(true);
	}
	
	/**
	 * <p>Faz a consulta no banco, atrav�s do DAO e gera o relat�rio.
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
