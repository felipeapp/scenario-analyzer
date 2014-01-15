/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/09/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;

/**
 * Bean que controla a geração do relatório de total de títulos,
 * exemplares e fascículos por coleção.
 *
 * @author Bráulio
 */
@Component("relatorioTotalPorColecao")
@Scope("request")
public class RelatorioTotalPorColecao extends AbstractRelatorioBibliotecaMBean {

	/**
	 * A página do relatório
	 */
	public static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioTotalPorColecao.jsp";
	
	/**
	 * Lista de Resultados do relatório que será mostrada aos usuários.
	 */
	private Map<String, Integer[]> resultados;
	
	/**
	 * Guarda a totalização dos resultados.
	 */
	private Integer[] total;
	
	public RelatorioTotalPorColecao(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relatório de Total por Coleção";
		descricao = "Este relatório lista a quantidade total de títulos, exemplares e fascículos por coleção.";

		filtradoPorVariasBibliotecas = true;
		filtradoPorCtgMaterial = true;
	}

	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		RelatoriosBibliotecaDao dao = getDAO(RelatoriosBibliotecaDao.class);
		resultados = dao.findColecaoAcervoByBiblioteca(UFRNUtils.toInteger(variasBibliotecas),
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
		
		total = new Integer[]{0, 0, 0};
		for ( Integer[] a : resultados.values() ) {
			total[0] += a[0];
			total[1] += a[1];
			total[2] += a[2];
		}
		
		return forward(PAGINA_RELATORIO);
	}
	
	// GETs e SETs

	public Map<String, Integer[]> getResultados() { return resultados; }
	public Integer[] getTotal() { return total; }

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
