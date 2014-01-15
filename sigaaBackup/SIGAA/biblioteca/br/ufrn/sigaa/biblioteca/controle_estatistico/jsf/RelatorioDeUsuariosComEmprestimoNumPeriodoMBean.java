/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 30/04/2010
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioDeUsuariosComEmprestimosPorPeriodoDao;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * Relat�rio que mostra a quantidade de usu�rios que fizeram empr�stimos num determinado
 * per�odo de tempo. Os dados s�o agrupados pela biblioteca.
 *
 * @author Br�ulio
 * @since 30/04/2010
 * @version 1.0 Cria��o
 */
@Component("relatorioDeUsuariosComEmprestimoNumPeriodo")
@Scope("request")
public class RelatorioDeUsuariosComEmprestimoNumPeriodoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** A p�gina do relat�rio. */
	private static final String PAGINA_DO_RELATORIO =
			"/biblioteca/controle_estatistico/relatorioUsuariosComEmprestimoNumPeriodo.jsp";
	
	/**
	 * <dl>
	 *   <dt>Chave</dt><dd>Tipo de usu�rio</dd></dl>
	 *   <dt>Elemento</dt><dd>N� de usu�rios com empr�stimos</dd>
	 * </dl>
	 */
	private Map<VinculoUsuarioBiblioteca, Integer> resultados = new TreeMap<VinculoUsuarioBiblioteca, Integer>();
	
	/** O total geral. */
	private int total;

	public RelatorioDeUsuariosComEmprestimoNumPeriodoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Quantitativo de Usu�rios que Fizeram Empr�stimo por Per�odo");
		
		setDescricao("Este relat�rio informa a quantidade de usu�rios que fizeram empr�stimo em um determinado per�odo. " +
				"Ou seja, os usu�rios que efetivamente utilizaram a biblioteca. ");
		
		setFiltradoPorVariasBibliotecas(true);
		campoBibliotecaObrigatorio = false;
		setFiltradoPorPeriodo(true);
		setFiltradoPorCategoriaDeUsuario(true);
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1);
		
	}
	
	
	@Override
	public String gerarRelatorio() throws DAOException {
		
		resultados = new TreeMap<VinculoUsuarioBiblioteca, Integer>();
		
		RelatorioDeUsuariosComEmprestimosPorPeriodoDao dao = null; 
		
		try{
			dao = getDAO(RelatorioDeUsuariosComEmprestimosPorPeriodoDao.class);
			
			VinculoUsuarioBiblioteca vinculo = VinculoUsuarioBiblioteca.getVinculo( valorVinculoDoUsuarioSelecionado );
			
			if(vinculo == VinculoUsuarioBiblioteca.INATIVO)
				vinculo = null; // Recupera empr�timos de todos os v�nculos  
			
			resultados = dao.findQtdUsuariosComEmprestimos(UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, vinculo);
			
			this.total = 0;
			for ( Map.Entry<VinculoUsuarioBiblioteca, Integer> linha : resultados.entrySet() )
				this.total += linha.getValue();
			
			if ( total == 0 ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
		}finally{
			if( dao != null) dao.close(); 
		}
		return forward(PAGINA_DO_RELATORIO);
	}
	
	public Map<VinculoUsuarioBiblioteca, Integer> getResultados() { return resultados; }
	public int                  getTotal()      { return total; }


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
