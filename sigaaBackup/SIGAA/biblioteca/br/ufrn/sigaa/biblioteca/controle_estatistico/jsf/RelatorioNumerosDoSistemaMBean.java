/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean para gerenciar o relatório de números gerais do sistema. </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioNumerosDoSistemaMBean")
@Scope("request")
public class RelatorioNumerosDoSistemaMBean extends AbstractRelatorioBibliotecaMBean{

	/**
	 * A página do relatório propriamente dito, onde os dados são mostrados
	 */
	public static final String PAGINA_RELATORIO  = "/biblioteca/controle_estatistico/outros/relatorioNumerosDoSistema.jsp";

	/** A quantidade de Títulos no Sistema */
	private long qtdTitulos;
	
	/** A quantidade de Autoridades no Sistema */
	private long qtdAutoridades;
	
	
	/** A quantidade de Artigos no Sistema */
	private long qtdArtigos;
	
	/** A quantidade de Exemplares no Sistema */
	private long qtdExemplares;
	
	/** A quantidade de Fascículos no Sistema */
	private long qtdFasciculos;
	
	/** A quantidade de Assinaturas no Sistema */
	private long qtdAssinaturas;
	
	/** A quantidade de Empréstimos no Sistema */
	private long qtdEmprestimos;

	/** A quantidade de Usuários no Sistema */
	private long qtdUsuarios;
	
	
	/**
	 * Contrutor padrão os relatórios da biblioteca
	 */
	public RelatorioNumerosDoSistemaMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório dos Números Gerais do Sistema");
		setDescricao("<p> Neste relatório é possível consultar os números gerais do sistema. </p>" +
					 "<p> " +
					 " Nele é possível consultar, por exemplo, a quantidade total de materiais no acervo, a quantidade total de usuários do sistema, entre outros números."+
				    "  Utilizado para se ter uma dimensão geral do sistema de biblioteca. " +
				    " </p>"+
				    " <br/>"+
					" <p> <strong>Observação:</strong>  Esse relatório não apresenta filtros, por tanto é preciso apenas confirmar a sua geração. </p> ");
		
		possuiFiltrosObrigatorios = false;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
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
