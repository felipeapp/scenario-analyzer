/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>MBean EXCLUSIVO para os relat�rios de multa </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioMultasNaoPagasBibliotecaMBean")
@Scope("request")
public class RelatorioMultasNaoPagasBibliotecaMBean extends AbstractRelatorioBibliotecaMBean{

	/** P�gina que implementa o relat�rio de n�o em aberto. */
	private static final String PAGINA_RELATORIO_MULTAS_NAO_PAGAS = "/biblioteca/controle_estatistico/multas/paginaRelatorioMultasAbertas.jsp";


	/** Guarda os resultados do relat�rio */
	private List<ResultadoRelatorioMultas> resultado;
	
	/** O valor total do relat�rio */
	protected BigDecimal valorTotalRelatorio;
	
	public RelatorioMultasNaoPagasBibliotecaMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *  
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		
		titulo = "Relat�rio de Multas a rerecer por Per�odo";
		descricao = " <p> Neste relat�rio � poss�vel consultar as multas que foram criadas mas ainda n�o foram pagas no per�odo informado. </p> "
			+"<p> Para o per�odo do relat�rio � considerada a data em que a multa foi criada </p>";
		
		
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false); // se n�o selecionar a biblioteca traz todas por padr�o
		filtradoPorPeriodo = true;
		
		campoPeriodoObrigatorio = true;
		
		possuiFiltrosObrigatorios = true;

		fimPeriodo = CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0);
		inicioPeriodo = CalendarUtils.subtrairMeses(fimPeriodo, 1);
	}
	
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		
	
			RelatoriosMultasBibliotecaDao multasDao = getDAO( RelatoriosMultasBibliotecaDao.class);
			super.configuraDaoRelatorio(multasDao); // fecha o dao depois do relat�rio automaticamente.
			
			resultado = multasDao.findMultasNaoPagasPorPeriodo(UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
			
			if(resultado.size() == 0){
				addMensagemWarning("N�o foram encontrados resultados para o per�odo informado. ");
				return null;
			}
			
			Collections.sort(resultado);
			
			valorTotalRelatorio = new BigDecimal(0);
			
			for (ResultadoRelatorioMultas resul : resultado) {
				valorTotalRelatorio = valorTotalRelatorio.add(resul.getValorTotalUsuario());
			}
			
			return forward(PAGINA_RELATORIO_MULTAS_NAO_PAGAS);
			
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
