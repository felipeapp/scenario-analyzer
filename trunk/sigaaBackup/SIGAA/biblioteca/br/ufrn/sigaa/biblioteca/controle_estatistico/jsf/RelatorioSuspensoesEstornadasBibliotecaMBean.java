/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/02/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatoriosSuspensoesEstornadasBibliotecaDao;

/**
 * <p>MBean EXCLUSIVO para os relatórios de suspensões estornadas </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioSuspensoesEstornadasBibliotecaMBean")
@Scope("request")
public class RelatorioSuspensoesEstornadasBibliotecaMBean extends AbstractRelatorioBibliotecaMBean{

	/** Página que implementa o relatório de suspensões estronadas. */
	private static final String PAGINA_RELATORIO_SUSPENSOES_ESTORNADAS = "/biblioteca/controle_estatistico/suspensoes/paginaRelatorioSuspensoesEstornadas.jsp";
	

	/** Guarda os resultados do relatório */
	private List<Object> resultado;
	
	public RelatorioSuspensoesEstornadasBibliotecaMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		titulo = "Relatório de Suspensões Estornadas por Período";
		descricao = " <p> Neste relatório é possível consultar as suspensões que foram estornadas no período informado. </p> "
				+"<p> Para o período do relatório é considerada a data em que a suspensão foi estornada </p>"
				+"<br/> "
				+"<p> Observação: Ao se selecionar uma biblioteca são recuperadas todas as suspensões estornadas cujos materiais " +
				"dos empréstimos são da biblioteca selecionada junto com as suspensões manuais, que não possuem biblioteca associada.</p>";
			
			filtradoPorVariasBibliotecas = true;
			setCampoBibliotecaObrigatorio(false); // se não selecionar a biblioteca traz todas por padrão
			filtradoPorPeriodo = true;
			
			campoPeriodoObrigatorio = true;
			
			possuiFiltrosObrigatorios = true;

			fimPeriodo = CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0);
			inicioPeriodo = CalendarUtils.subtrairMeses(fimPeriodo, 1);
		
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		RelatoriosSuspensoesEstornadasBibliotecaDao dao = getDAO( RelatoriosSuspensoesEstornadasBibliotecaDao.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois do relatório automaticamente.
		
		resultado = dao.findSuspensoesEstornadasPorPeriodo(UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
		
		if(resultado.size() == 0){
			addMensagemWarning("Não foram encontrados resultados para o período informado. ");
			return null;
		}
		
		//Collections.sort(resultado);
		
		return forward(PAGINA_RELATORIO_SUSPENSOES_ESTORNADAS);
	}

	
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

	public List<Object> getResultado() {
		return resultado;
	}

	public void setResultado(List<Object> resultado) {
		this.resultado = resultado;
	}
	
}
