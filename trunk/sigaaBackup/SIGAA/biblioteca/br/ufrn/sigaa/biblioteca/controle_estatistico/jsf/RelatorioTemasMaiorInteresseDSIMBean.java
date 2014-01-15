/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Relat�rio por onde os funcion�rios das bibliotecas poder�o verificar os temas (autores ou assuntos) de maior interesse 
 * dos usu�rios da biblioteca. Tamb�m verificar a quantidade de usu�rios que cadastraram interesse em receber o 
 * "Informativo de novas Aquisi��es" e de qual �rea eles est�o recebendo.
 * </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioTemasMaiorInteresseDSIMBean")
@Scope("request")
public class RelatorioTemasMaiorInteresseDSIMBean extends AbstractRelatorioBibliotecaMBean{

	/**
	 * A p�gina do relat�rio propriamente dito, onde os dados s�o mostrados
	 */
	public static final String PAGINA_RELATORIO  = "/biblioteca/controle_estatistico/outros/relatorioTemasMaiorInteresseDSI.jsp";
	
	/** Guarda a quantidade de usu�rios que est�o cadastrados para receber o informativo de novas aquisi��es */
	private long qtdUsuariosRecebendoInformativoNovasAquisicoes = 0l;
	
	/** Guarda as grandes �reas de maitor interesse dos usu�rios de acordo com o escolhido no informativo de novas aquisi��es */
	private  List<Object[]> grandesAreasDeMaiorInteresseUsuario;
	
	/** Guarda a listagem dos 50 assuntos de maitor interesse para os usu�rios do sistema */
	private  List<Object[]> assuntosDeMaiorInteresseUsuario;
	
	/** Guarda a listagem dos 50 autores de maitor interesse para os usu�rios do sistema */
	private  List<Object[]> autoresDeMaiorInteresseUsuario;
	
	/** O limite de busca para os temas de maior interesse do usu�rios*/
	private final static int LIMITE_BUSCA_TEMAS_MAIOR_INTERESSE = 50;
	
	/**
	 * Contrutor padr�o os relat�rios da biblioteca
	 */
	public RelatorioTemasMaiorInteresseDSIMBean(){
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
		setTitulo("Relat�rio dos Temas de Maior Interesse da DSI");
		setDescricao("<p> Neste relat�rio � poss�vel consultar os temas (autores e assuntos) de maior interesse dos usu�rios do sistema da Dissemina��o Selecitiva da Informa��o. </p>" +
					 "<p> " +
					 " Tamb�m � poss�vel consultar quantos usu�rios marcaram interesse em receber o \"Informativo Mensal de Novas Aquisi��es\" e de quais �reas do conhecimento."+
				    " </p>"+
				    " <br/>"+
				    " <p> Escolhendo-ser uma biblioteca, ser�o retornados os temas de maior interesse dos usu�rios quando ele informaram o interesse de receber informa��es de uma biblioteca espec�fica. </p> ");
		
		setFiltradoPorVariasBibliotecas(true);
		setCampoBibliotecaObrigatorio(false);
		setPossuiFiltrosObrigatorios(false);
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
		
		RelatorioTemasMaiorInteresseDSIDao dao = getDAO( RelatorioTemasMaiorInteresseDSIDao.class);
		configuraDaoRelatorio(dao);
		
		qtdUsuariosRecebendoInformativoNovasAquisicoes = dao.countTotalUsuariosRecebendoInformativoNovasAquisicoes();
		
		grandesAreasDeMaiorInteresseUsuario = dao.listaGrandesAreasDeMaiorInteresseDosUsuarios();
		
		assuntosDeMaiorInteresseUsuario = dao.listaAssuntosDeMaiorInteresseDosUsuarios(UFRNUtils.toInteger(variasBibliotecas), LIMITE_BUSCA_TEMAS_MAIOR_INTERESSE);
		
		autoresDeMaiorInteresseUsuario = dao.listaAutoresDeMaiorInteresseDosUsuarios(UFRNUtils.toInteger(variasBibliotecas), LIMITE_BUSCA_TEMAS_MAIOR_INTERESSE);
		
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
