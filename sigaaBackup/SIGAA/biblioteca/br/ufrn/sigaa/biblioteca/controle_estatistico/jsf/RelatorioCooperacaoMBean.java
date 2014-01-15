/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/02/2011
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioCooperacaoDAO;

/**
 * Backing bean que controla a geração do relatório de Cooperação
 * de títulos e de autoridades autor e assunto.
 *
 * @author Bráulio
 */
@Component("relatorioCooperacaoMBean")
@Scope("request")
public class RelatorioCooperacaoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** JSP do relatório. */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioCooperacao.jsp";
	
	/** Total de títulos cooperados. */
	private List<Object[]> resultadoTitulos;
	
	/** Total de autoridades de autores cooperados. */
	private List<Object[]> resultadoAutoridadeAutores;
	
	/** Total de autoridades de assuntos cooperados. */
	private List<Object[]> resultadoAutoridadeAssuntos;
	
	public RelatorioCooperacaoMBean(){
		super.configuraMBeanRelatorio(this);
	}

	/** Guarda a totalização de titulos importados */
	private long qtdImportadosTitulos = 0L;
	/** Guarda a totalização de titulos exportados */
	private long qtdExportadosTitulos = 0L;
	/** Guarda a totalização de autoridades de autor importadas */
	private long qtdImportadosAutor = 0L;
	/** Guarda a totalização de autoridades de autor exportados */
	private long qtdExportadosAutor = 0L;
	/** Guarda a totalização de autoridades de assunto importadas */
	private long qtdImportadosAssunto = 0L;
	/** Guarda a totalização de autoridades de assunto exportados */
	private long qtdExportadosAssunto = 0L;
	
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por JSP.</>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		
		titulo = "Relatório de Cooperação";
		
		descricao = " <p>Neste relatório é possível verificar a quantidade de Títulos e de Autoridades que foram cooperados através do sistema. " +
				" Tanto importações de outros acervos, quanto exportações para outros acervos.</p>"
				+"<p> O filtro <i>\"Bibliotecas\"</i> indica a biblioteca em que o usuário estava trabalhando no momento em que realizou a cooperação ( importação ou exportação ) .</p>"
				+"<p> As cooperações de Autoridade são distinguidas no sistema entre <strong>Autoridades de Autores</strong> ou <strong>Autoridades de Assuntos</strong>. "
				+"<p> Autoridades de Autores são as autoridade que contém uma entrada principal de autor, campos MARC 100, 110 ou 111. </p>"
				+"<p> Autoridades de Assunto são as autoridade que contém uma entrada  principal de assunto, campos MARC 150, 151 ou 180. </p>";

		filtradoPorVariasBibliotecas = true;
		filtradoPorVariasPessoas = true;
		filtradoPorPeriodo = true;

		campoBibliotecaObrigatorio = false;
		campoPessoaObrigatorio = false;
	}

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por JSP.</>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		RelatorioCooperacaoDAO dao = getDAO(RelatorioCooperacaoDAO.class);

		Collection<Integer> idsBiblioteca = UFRNUtils.toInteger(variasBibliotecas);
		Collection<Integer> idsPessoa = UFRNUtils.toInteger(variasPessoas);
		
		qtdImportadosTitulos = 0L;
		qtdExportadosTitulos = 0L;
		qtdImportadosAutor = 0L;
		qtdExportadosAutor = 0L;
		qtdImportadosAssunto = 0L;
		qtdExportadosAssunto = 0L;
		
		
		resultadoTitulos = dao.findTitulosCooperacao(idsPessoa, idsBiblioteca, inicioPeriodo, fimPeriodo);
		
		for (Object[] object : resultadoTitulos) {
			qtdImportadosTitulos +=  ((BigDecimal)object[0]).longValue();
			qtdExportadosTitulos +=  ((BigDecimal)object[1]).longValue();
		}
		
		resultadoAutoridadeAutores = dao.findAutoridadesAutorCooperacao(idsPessoa, idsBiblioteca, inicioPeriodo, fimPeriodo);

		for (Object[] object : resultadoAutoridadeAutores) {
			qtdImportadosAutor +=  ((BigDecimal)object[0]).longValue();
			qtdExportadosAutor +=  ((BigDecimal)object[1]).longValue();
		}
		
		resultadoAutoridadeAssuntos = dao.findAutoridadesAssuntoCooperacao(idsPessoa, idsBiblioteca, inicioPeriodo, fimPeriodo);
		
		for (Object[] object : resultadoAutoridadeAssuntos) {
			qtdImportadosAssunto +=  ((BigDecimal)object[0]).longValue();
			qtdExportadosAssunto +=  ((BigDecimal)object[1]).longValue();
		}
		
		if (resultadoTitulos.size() == 0 && resultadoAutoridadeAutores.size() == 0 && resultadoAutoridadeAssuntos.size() == 0){
			addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			return null;
		}

		return forward(PAGINA);
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
	
	/////// GETs e SETs ///////

	
	
	public List<Object[]> getResultadoTitulos() {
		return resultadoTitulos;
	}

	public List<Object[]> getResultadoAutoridadeAutores() {
		return resultadoAutoridadeAutores;
	}

	public void setResultadoAutoridadeAutores(
			List<Object[]> resultadoAutoridadeAutores) {
		this.resultadoAutoridadeAutores = resultadoAutoridadeAutores;
	}

	public List<Object[]> getResultadoAutoridadeAssuntos() {
		return resultadoAutoridadeAssuntos;
	}

	public void setResultadoAutoridadeAssuntos(
			List<Object[]> resultadoAutoridadeAssuntos) {
		this.resultadoAutoridadeAssuntos = resultadoAutoridadeAssuntos;
	}

	public void setResultadoTitulos(List<Object[]> resultadoTitulos) {
		this.resultadoTitulos = resultadoTitulos;
	}
	
	public long getQtdImportadosTitulos() {
		return qtdImportadosTitulos;
	}

	public void setQtdImportadosTitulos(long qtdImportadosTitulos) {
		this.qtdImportadosTitulos = qtdImportadosTitulos;
	}

	public long getQtdExportadosTitulos() {
		return qtdExportadosTitulos;
	}

	public void setQtdExportadosTitulos(long qtdExportadosTitulos) {
		this.qtdExportadosTitulos = qtdExportadosTitulos;
	}

	public long getQtdImportadosAutor() {
		return qtdImportadosAutor;
	}

	public void setQtdImportadosAutor(long qtdImportadosAutor) {
		this.qtdImportadosAutor = qtdImportadosAutor;
	}

	public long getQtdExportadosAutor() {
		return qtdExportadosAutor;
	}

	public void setQtdExportadosAutor(long qtdExportadosAutor) {
		this.qtdExportadosAutor = qtdExportadosAutor;
	}

	public long getQtdImportadosAssunto() {
		return qtdImportadosAssunto;
	}

	public void setQtdImportadosAssunto(long qtdImportadosAssunto) {
		this.qtdImportadosAssunto = qtdImportadosAssunto;
	}

	public long getQtdExportadosAssunto() {
		return qtdExportadosAssunto;
	}

	public void setQtdExportadosAssunto(long qtdExportadosAssunto) {
		this.qtdExportadosAssunto = qtdExportadosAssunto;
	}

}
