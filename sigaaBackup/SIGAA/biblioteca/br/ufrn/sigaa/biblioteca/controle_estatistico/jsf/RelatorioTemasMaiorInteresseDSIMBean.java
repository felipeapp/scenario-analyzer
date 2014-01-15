/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/12/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioTemasMaiorInteresseDSIDao;

/**
 *
 * <p>Relatório por onde os funcionários das bibliotecas poderão verificar os temas (autores ou assuntos) de maior interesse 
 * dos usuários da biblioteca. Também verificar a quantidade de usuários que cadastraram interesse em receber o 
 * "Informativo de novas Aquisições" e de qual área eles estão recebendo.
 * </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioTemasMaiorInteresseDSIMBean")
@Scope("request")
public class RelatorioTemasMaiorInteresseDSIMBean extends AbstractRelatorioBibliotecaMBean{

	/**
	 * A página do relatório propriamente dito, onde os dados são mostrados
	 */
	public static final String PAGINA_RELATORIO  = "/biblioteca/controle_estatistico/outros/relatorioTemasMaiorInteresseDSI.jsp";
	
	/** Guarda a quantidade de usuários que estão cadastrados para receber o informativo de novas aquisições */
	private long qtdUsuariosRecebendoInformativoNovasAquisicoes = 0l;
	
	/** Guarda as grandes áreas de maitor interesse dos usuários de acordo com o escolhido no informativo de novas aquisições */
	private  List<Object[]> grandesAreasDeMaiorInteresseUsuario;
	
	/** Guarda a listagem dos 50 assuntos de maitor interesse para os usuários do sistema */
	private  List<Object[]> assuntosDeMaiorInteresseUsuario;
	
	/** Guarda a listagem dos 50 autores de maitor interesse para os usuários do sistema */
	private  List<Object[]> autoresDeMaiorInteresseUsuario;
	
	/** O limite de busca para os temas de maior interesse do usuários*/
	private final static int LIMITE_BUSCA_TEMAS_MAIOR_INTERESSE = 50;
	
	/**
	 * Contrutor padrão os relatórios da biblioteca
	 */
	public RelatorioTemasMaiorInteresseDSIMBean(){
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
		setTitulo("Relatório dos Temas de Maior Interesse da DSI");
		setDescricao("<p> Neste relatório é possível consultar os temas (autores e assuntos) de maior interesse dos usuários do sistema da Disseminação Selecitiva da Informação. </p>" +
					 "<p> " +
					 " Também é possível consultar quantos usuários marcaram interesse em receber o \"Informativo Mensal de Novas Aquisições\" e de quais áreas do conhecimento."+
				    " </p>"+
				    " <br/>"+
				    " <p> Escolhendo-ser uma biblioteca, serão retornados os temas de maior interesse dos usuários quando ele informaram o interesse de receber informações de uma biblioteca específica. </p> ");
		
		setFiltradoPorVariasBibliotecas(true);
		setCampoBibliotecaObrigatorio(false);
		setPossuiFiltrosObrigatorios(false);
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
		
		RelatorioTemasMaiorInteresseDSIDao dao = getDAO( RelatorioTemasMaiorInteresseDSIDao.class);
		configuraDaoRelatorio(dao);
		
		qtdUsuariosRecebendoInformativoNovasAquisicoes = dao.countTotalUsuariosRecebendoInformativoNovasAquisicoes();
		
		grandesAreasDeMaiorInteresseUsuario = dao.listaGrandesAreasDeMaiorInteresseDosUsuarios();
		
		assuntosDeMaiorInteresseUsuario = dao.listaAssuntosDeMaiorInteresseDosUsuarios(UFRNUtils.toInteger(variasBibliotecas), LIMITE_BUSCA_TEMAS_MAIOR_INTERESSE);
		
		autoresDeMaiorInteresseUsuario = dao.listaAutoresDeMaiorInteresseDosUsuarios(UFRNUtils.toInteger(variasBibliotecas), LIMITE_BUSCA_TEMAS_MAIOR_INTERESSE);
		
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

	
	
	////////// sets e gets //////////
	
	public long getQtdUsuariosRecebendoInformativoNovasAquisicoes() {
		return qtdUsuariosRecebendoInformativoNovasAquisicoes;
	}

	public void setQtdUsuariosRecebendoInformativoNovasAquisicoes(long qtdUsuariosRecebendoInformativoNovasAquisicoes) {
		this.qtdUsuariosRecebendoInformativoNovasAquisicoes = qtdUsuariosRecebendoInformativoNovasAquisicoes;
	}

	public List<Object[]> getGrandesAreasDeMaiorInteresseUsuario() {
		return grandesAreasDeMaiorInteresseUsuario;
	}

	public void setGrandesAreasDeMaiorInteresseUsuario(List<Object[]> grandesAreasDeMaiorInteresseUsuario) {
		this.grandesAreasDeMaiorInteresseUsuario = grandesAreasDeMaiorInteresseUsuario;
	}

	public List<Object[]> getAssuntosDeMaiorInteresseUsuario() {
		return assuntosDeMaiorInteresseUsuario;
	}

	public void setAssuntosDeMaiorInteresseUsuario(List<Object[]> assuntosDeMaiorInteresseUsuario) {
		this.assuntosDeMaiorInteresseUsuario = assuntosDeMaiorInteresseUsuario;
	}

	public List<Object[]> getAutoresDeMaiorInteresseUsuario() {
		return autoresDeMaiorInteresseUsuario;
	}

	public void setAutoresDeMaiorInteresseUsuario(List<Object[]> autoresDeMaiorInteresseUsuario) {
		this.autoresDeMaiorInteresseUsuario = autoresDeMaiorInteresseUsuario;
	}
	
	public int getLimiteBuscaTemasMaiorInteresse() {
		return LIMITE_BUSCA_TEMAS_MAIOR_INTERESSE;
	}
	
	
	
}
