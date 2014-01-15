/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 08/09/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioNumerosDoSistemaDao;

/**
 *
 * <p>MBean para gerenciar o relat�rio de n�meros gerais do sistema. </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioNumerosDoSistemaMBean")
@Scope("request")
public class RelatorioNumerosDoSistemaMBean extends AbstractRelatorioBibliotecaMBean{

	/**
	 * A p�gina do relat�rio propriamente dito, onde os dados s�o mostrados
	 */
	public static final String PAGINA_RELATORIO  = "/biblioteca/controle_estatistico/outros/relatorioNumerosDoSistema.jsp";

	/** A quantidade de T�tulos no Sistema */
	private long qtdTitulos;
	
	/** A quantidade de Autoridades no Sistema */
	private long qtdAutoridades;
	
	
	/** A quantidade de Artigos no Sistema */
	private long qtdArtigos;
	
	/** A quantidade de Exemplares no Sistema */
	private long qtdExemplares;
	
	/** A quantidade de Fasc�culos no Sistema */
	private long qtdFasciculos;
	
	/** A quantidade de Assinaturas no Sistema */
	private long qtdAssinaturas;
	
	/** A quantidade de Empr�stimos no Sistema */
	private long qtdEmprestimos;

	/** A quantidade de Usu�rios no Sistema */
	private long qtdUsuarios;
	
	
	/**
	 * Contrutor padr�o os relat�rios da biblioteca
	 */
	public RelatorioNumerosDoSistemaMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio dos N�meros Gerais do Sistema");
		setDescricao("<p> Neste relat�rio � poss�vel consultar os n�meros gerais do sistema. </p>" +
					 "<p> " +
					 " Nele � poss�vel consultar, por exemplo, a quantidade total de materiais no acervo, a quantidade total de usu�rios do sistema, entre outros n�meros."+
				    "  Utilizado para se ter uma dimens�o geral do sistema de biblioteca. " +
				    " </p>"+
				    " <br/>"+
					" <p> <strong>Observa��o:</strong>  Esse relat�rio n�o apresenta filtros, por tanto � preciso apenas confirmar a sua gera��o. </p> ");
		
		possuiFiltrosObrigatorios = false;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		
		RelatorioNumerosDoSistemaDao dao = getDAO( RelatorioNumerosDoSistemaDao.class);
		configuraDaoRelatorio(dao);
		
		qtdTitulos = dao.countTotalTitulos();
		qtdAutoridades = dao.countTotalAutoridades();
		qtdArtigos = dao.countTotalArtigos();
		qtdExemplares = dao.countTotalExemplares();
		qtdFasciculos = dao.countTotalFasciculos();
		qtdAssinaturas = dao.countTotalAssinaturas();
		qtdEmprestimos = dao.countTotalEmprestimos();
		qtdUsuarios = dao.countTotalUsuarios();
		
		return forward(PAGINA_RELATORIO);
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

	// sets e gets //
	
	public long getQtdTitulos() {
		return qtdTitulos;
	}

	public void setQtdTitulos(long qtdTitulos) {
		this.qtdTitulos = qtdTitulos;
	}

	public long getQtdAutoridades() {
		return qtdAutoridades;
	}

	public void setQtdAutoridades(long qtdAutoridades) {
		this.qtdAutoridades = qtdAutoridades;
	}

	public long getQtdArtigos() {
		return qtdArtigos;
	}

	public void setQtdArtigos(long qtdArtigos) {
		this.qtdArtigos = qtdArtigos;
	}

	public long getQtdExemplares() {
		return qtdExemplares;
	}

	public void setQtdExemplares(long qtdExemplares) {
		this.qtdExemplares = qtdExemplares;
	}

	public long getQtdFasciculos() {
		return qtdFasciculos;
	}

	public void setQtdFasciculos(long qtdFasciculos) {
		this.qtdFasciculos = qtdFasciculos;
	}

	public long getQtdAssinaturas() {
		return qtdAssinaturas;
	}

	public void setQtdAssinaturas(long qtdAssinaturas) {
		this.qtdAssinaturas = qtdAssinaturas;
	}

	public long getQtdEmprestimos() {
		return qtdEmprestimos;
	}

	public void setQtdEmprestimos(long qtdEmprestimos) {
		this.qtdEmprestimos = qtdEmprestimos;
	}


	public long getQtdUsuarios() {
		return qtdUsuarios;
	}


	public void setQtdUsuarios(long qtdUsuarios) {
		this.qtdUsuarios = qtdUsuarios;
	}

	
	
}
