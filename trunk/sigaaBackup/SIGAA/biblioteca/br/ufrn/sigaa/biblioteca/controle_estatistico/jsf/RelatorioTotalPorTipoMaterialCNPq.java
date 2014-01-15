/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/09/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;

/**
 * Backing bean que controla a geração do relatório de total
 * de títulos, exemplares e fascículos por tipo de material e
 * área do CNPq.
 *
 * @author Bráulio
 */
@Component("relatorioTotalPorTipoMaterialCNPq")
@Scope("request")
public class RelatorioTotalPorTipoMaterialCNPq extends AbstractRelatorioBibliotecaMBean {

	/**
	 * A página do relatório
	 */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioTotalPorTipoMaterialCNPq.jsp";
	
	/**
	 * Lista de Resultados do relatório que será mostrada aos usuários.
	 */
	private List<Object[]> resultados;
	
	public RelatorioTotalPorTipoMaterialCNPq(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relatório de Total por Tipo de Material e Área CNPq";
		descricao = "Este relatório lista a quantidade total de títulos, exemplares e fascículos por área do CNPq e tipo de material.";

		filtradoPorVariasBibliotecas = true;
		filtradoPorCtgMaterial = true;
	}

	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		RelatoriosBibliotecaDao dao = getDAO(RelatoriosBibliotecaDao.class);
		resultados = dao.findMaterialEspecialCNPQAcervoByBiblioteca(
				UFRNUtils.toInteger(variasBibliotecas),
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS );
		List <Object []> auxArrayRelatorio = new ArrayList <Object []> ();
		
		String aux = "-1";
		Object [] total = null;

		for (Object [] r : resultados){
			if (r[0] == null) continue;
			
			if (!aux.equals(r[0]) && total != null){
				auxArrayRelatorio.add(total);
				total = null;
			}
			
			if (total == null){
				total = new Object [6];
				total[0] = aux = (String) r[0];
				total[1] = "Total";
				total[2] = total[3] = total[4] = total[5] = 0;
			}
			
			total[2] = (Integer) total[2] + ((BigInteger) r[2]).intValue();
			total[3] = (Integer) total[3] + ((BigInteger) r[3]).intValue();
			total[4] = (Integer) total[4] + ((BigInteger) r[4]).intValue();
			total[5] = (Integer) total[5] + ((BigInteger) r[5]).intValue();
			
			auxArrayRelatorio.add(r);
		}
		
		auxArrayRelatorio.add(total);
		
		resultados = auxArrayRelatorio;
		
		return forward(PAGINA_RELATORIO);
	}

	public List<Object[]> getResultados() { return resultados; }

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
