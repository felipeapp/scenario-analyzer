/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/02/2011
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioImplantacaoDAO;

/**
 * Backing bean que controla a geração do relatório de Implantação
 * de títulos e de autoridades autor e assunto.
 *
 * @author Bráulio
 */
@Component("relatorioImplantacaoMBean")
@Scope("request")
public class RelatorioImplantacaoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** JSP do relatório. */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioImplantacao.jsp";
	
	/** Total de títulos implantados. */
	private List<Object[]> resultadoTitulos;
	
	/** Total de autoridades de autores implantados. */
	private List<Object[]> resultadoAutoridadeAutores;
	
	/** Total de autoridades de assuntos implantados. */
	private List<Object[]> resultadoAutoridadeAssuntos;
	
	public RelatorioImplantacaoMBean(){
		super.configuraMBeanRelatorio(this);
	}

	/** Guarda a totalização de titulos implantados */
	private long qtdImplantadosTitulos = 0L;
	/** Guarda a totalização de autoridades de autor implantadas */
	private long qtdImplantadosAutor = 0L;
	/** Guarda a totalização de autoridades de assunto implantados */
	private long qtdImplantadosAssunto = 0L;
	
	
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
		
		titulo = "Relatório de Implantação";
		
		descricao = " <p>Neste relatório é possível verificar a quantidade de Títulos e de Autoridades que foram implantados no sistema. Ou seja, tudo que foi criado no sistema " +
				"com exceção daquelas informações que foram importadas de outras bases.</p>"
				+"<p> As implantações de Autoridade são distinguidas no sistema entre <strong>Autoridades de Autores</strong> ou <strong>Autoridades de Assuntos</strong>. "
				+"<p> Autoridades de Autores são as autoridade que contém uma entrada principal de autor, campos MARC 100, 110 ou 111. </p>"
				+"<p> Autoridades de Assunto são as autoridade que contém uma entrada  principal de assunto, campos MARC 150, 151 ou 180. </p>";

		filtradoPorVariasBibliotecas = false;
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
		RelatorioImplantacaoDAO dao = getDAO(RelatorioImplantacaoDAO.class);

		Collection<Integer> idsPessoa = UFRNUtils.toInteger(variasPessoas);
		
		qtdImplantadosTitulos = 0L;
		qtdImplantadosAutor = 0L;
		qtdImplantadosAssunto = 0L;
				
		resultadoTitulos = dao.findTitulosImplantacao(idsPessoa, inicioPeriodo, fimPeriodo);
		
		for (Object[] object : resultadoTitulos) {
			qtdImplantadosTitulos +=  ((BigInteger)object[0]).longValue();
		}
		
		resultadoAutoridadeAutores = dao.findAutoridadesAutorImplantacao(idsPessoa, inicioPeriodo, fimPeriodo);

		for (Object[] object : resultadoAutoridadeAutores) {
			qtdImplantadosAutor +=  ((BigInteger)object[0]).longValue();
		}
		
		resultadoAutoridadeAssuntos = dao.findAutoridadesAssuntoImplantacao(idsPessoa, inicioPeriodo, fimPeriodo);
		
		for (Object[] object : resultadoAutoridadeAssuntos) {
			qtdImplantadosAssunto +=  ((BigInteger)object[0]).longValue();
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

	public long getQtdImplantadosTitulos() {
		return qtdImplantadosTitulos;
	}

	public void setQtdImplantadosTitulos(long qtdImplantadosTitulos) {
		this.qtdImplantadosTitulos = qtdImplantadosTitulos;
	}

	public long getQtdImplantadosAutor() {
		return qtdImplantadosAutor;
	}

	public void setQtdImplantadosAutor(long qtdImplantadosAutor) {
		this.qtdImplantadosAutor = qtdImplantadosAutor;
	}

	public long getQtdImplantadosAssunto() {
		return qtdImplantadosAssunto;
	}

	public void setQtdImplantadosAssunto(long qtdImplantadosAssunto) {
		this.qtdImplantadosAssunto = qtdImplantadosAssunto;
	}

}
