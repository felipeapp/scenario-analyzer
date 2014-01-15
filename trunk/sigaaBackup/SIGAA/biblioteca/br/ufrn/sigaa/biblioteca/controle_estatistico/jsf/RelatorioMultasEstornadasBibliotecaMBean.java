/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 11/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.relatorios.RelatoriosMultasBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategyFactory;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.ResultadoRelatorioMultas;

/**
 * <p>MBean EXCLUSIVO para os relatórios de multa </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioMultasEstornadasBibliotecaMBean")
@Scope("request")
public class RelatorioMultasEstornadasBibliotecaMBean extends AbstractRelatorioBibliotecaMBean{

	/** Página que implementa o relatório de multas estronadas. */
	private static final String PAGINA_RELATORIO_MULTAS_ESTORNADAS = "/biblioteca/controle_estatistico/multas/paginaRelatorioMultasEstornadas.jsp";
	

	/** Guarda os resultados do relatório */
	private List<ResultadoRelatorioMultas> resultado;
	
	/** O valor total do relatório */
	protected BigDecimal valorTotalRelatorio;
	
	public RelatorioMultasEstornadasBibliotecaMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *  
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		
		titulo = "Relatório de Multas Estornadas por Período";
		descricao = " <p> Neste relatório é possível consultar as multas que foram estornadas no período informado. </p> "
			+"<p> Para o período do relatório é considerada a data em que a multa foi estornada </p>";
		
		
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false); // se não selecionar a biblioteca traz todas por padrão
		filtradoPorPeriodo = true;
		
		campoPeriodoObrigatorio = true;
		
		possuiFiltrosObrigatorios = true;

		fimPeriodo = CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0);
		inicioPeriodo = CalendarUtils.subtrairMeses(fimPeriodo, 1);
	}
	
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
			RelatoriosMultasBibliotecaDao multasDao = getDAO( RelatoriosMultasBibliotecaDao.class);
			super.configuraDaoRelatorio(multasDao); // fecha o dao depois do relatório automaticamente.
			
			resultado = multasDao.findMultasEstornadasPorPeriodo(UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
			
			if(resultado.size() == 0){
				addMensagemWarning("Não foram encontrados resultados para o período informado. ");
				return null;
			}
			
			Collections.sort(resultado);
			
			valorTotalRelatorio = new BigDecimal(0);
			
			for (ResultadoRelatorioMultas resul : resultado) {
				valorTotalRelatorio = valorTotalRelatorio.add(resul.getValorTotalUsuario());
			}
			
			return forward(PAGINA_RELATORIO_MULTAS_ESTORNADAS);
		
	}
	
	public String getValorTotalRelatorioFormatado(){
		return new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiaMulta().getValorFormatado(valorTotalRelatorio);
	}
	

	public List<ResultadoRelatorioMultas> getResultado() {
		return resultado;
	}

	public void setResultado(List<ResultadoRelatorioMultas> resultado) {
		this.resultado = resultado;
	}


	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}


	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}	
	
}
