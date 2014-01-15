/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>MBean EXCLUSIVO para os relat�rios de suspens�es estornadas </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioSuspensoesEstornadasBibliotecaMBean")
@Scope("request")
public class RelatorioSuspensoesEstornadasBibliotecaMBean extends AbstractRelatorioBibliotecaMBean{

	/** P�gina que implementa o relat�rio de suspens�es estronadas. */
	private static final String PAGINA_RELATORIO_SUSPENSOES_ESTORNADAS = "/biblioteca/controle_estatistico/suspensoes/paginaRelatorioSuspensoesEstornadas.jsp";
	

	/** Guarda os resultados do relat�rio */
	private List<Object> resultado;
	
	public RelatorioSuspensoesEstornadasBibliotecaMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		titulo = "Relat�rio de Suspens�es Estornadas por Per�odo";
		descricao = " <p> Neste relat�rio � poss�vel consultar as suspens�es que foram estornadas no per�odo informado. </p> "
				+"<p> Para o per�odo do relat�rio � considerada a data em que a suspens�o foi estornada </p>"
				+"<br/> "
				+"<p> Observa��o: Ao se selecionar uma biblioteca s�o recuperadas todas as suspens�es estornadas cujos materiais " +
				"dos empr�stimos s�o da biblioteca selecionada junto com as suspens�es manuais, que n�o possuem biblioteca associada.</p>";
			
			filtradoPorVariasBibliotecas = true;
			setCampoBibliotecaObrigatorio(false); // se n�o selecionar a biblioteca traz todas por padr�o
			filtradoPorPeriodo = true;
			
			campoPeriodoObrigatorio = true;
			
			possuiFiltrosObrigatorios = true;

			fimPeriodo = CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0);
			inicioPeriodo = CalendarUtils.subtrairMeses(fimPeriodo, 1);
		
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		RelatoriosSuspensoesEstornadasBibliotecaDao dao = getDAO( RelatoriosSuspensoesEstornadasBibliotecaDao.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois do relat�rio automaticamente.
		
		resultado = dao.findSuspensoesEstornadasPorPeriodo(UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
		
		if(resultado.size() == 0){
			addMensagemWarning("N�o foram encontrados resultados para o per�odo informado. ");
			return null;
		}
		
		//Collections.sort(resultado);
		
		return forward(PAGINA_RELATORIO_SUSPENSOES_ESTORNADAS);
	}

	
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

	public List<Object> getResultado() {
		return resultado;
	}

	public void setResultado(List<Object> resultado) {
		this.resultado = resultado;
	}
	
}
