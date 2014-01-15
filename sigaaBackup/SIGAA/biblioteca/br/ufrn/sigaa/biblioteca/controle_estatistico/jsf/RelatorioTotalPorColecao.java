/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Bean que controla a gera��o do relat�rio de total de t�tulos,
 * exemplares e fasc�culos por cole��o.
 *
 * @author Br�ulio
 */
@Component("relatorioTotalPorColecao")
@Scope("request")
public class RelatorioTotalPorColecao extends AbstractRelatorioBibliotecaMBean {

	/**
	 * A p�gina do relat�rio
	 */
	public static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioTotalPorColecao.jsp";
	
	/**
	 * Lista de Resultados do relat�rio que ser� mostrada aos usu�rios.
	 */
	private Map<String, Integer[]> resultados;
	
	/**
	 * Guarda a totaliza��o dos resultados.
	 */
	private Integer[] total;
	
	public RelatorioTotalPorColecao(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relat�rio de Total por Cole��o";
		descricao = "Este relat�rio lista a quantidade total de t�tulos, exemplares e fasc�culos por cole��o.";

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
