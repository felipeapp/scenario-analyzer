/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Relatório que mostra a quantidade de usuários que fizeram empréstimos num determinado
 * período de tempo. Os dados são agrupados pela biblioteca.
 *
 * @author Bráulio
 * @since 30/04/2010
 * @version 1.0 Criação
 */
@Component("relatorioDeUsuariosComEmprestimoNumPeriodo")
@Scope("request")
public class RelatorioDeUsuariosComEmprestimoNumPeriodoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** A página do relatório. */
	private static final String PAGINA_DO_RELATORIO =
			"/biblioteca/controle_estatistico/relatorioUsuariosComEmprestimoNumPeriodo.jsp";
	
	/**
	 * <dl>
	 *   <dt>Chave</dt><dd>Tipo de usuário</dd></dl>
	 *   <dt>Elemento</dt><dd>Nº de usuários com empréstimos</dd>
	 * </dl>
	 */
	private Map<VinculoUsuarioBiblioteca, Integer> resultados = new TreeMap<VinculoUsuarioBiblioteca, Integer>();
	
	/** O total geral. */
	private int total;

	public RelatorioDeUsuariosComEmprestimoNumPeriodoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Quantitativo de Usuários que Fizeram Empréstimo por Período");
		
		setDescricao("Este relatório informa a quantidade de usuários que fizeram empréstimo em um determinado período. " +
				"Ou seja, os usuários que efetivamente utilizaram a biblioteca. ");
		
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
				vinculo = null; // Recupera emprétimos de todos os vínculos  
			
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
