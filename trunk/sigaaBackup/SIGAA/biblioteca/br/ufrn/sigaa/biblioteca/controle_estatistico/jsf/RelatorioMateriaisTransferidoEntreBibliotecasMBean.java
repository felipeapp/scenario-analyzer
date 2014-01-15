/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 20/07/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioMateriaisTransferidoEntreBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;

/**
 *
 * <p> MBean do relat�rio de materiais transferidos entre as bibliotecas no sistema. </p>
 *
 * 
 * <p>
 *  <ul>
 *  	<li>O relat�rio dever� apresentar as seguintes informa��es: N�mero do Sistema / C�digo / Referencia completa no Formato ABNT 6023; </li>
 *  	<li>Ao final do relat�rios, dever� aparecer o quantitativo de t�tulos e volumes transferidos. </li>
 *  </ul>
 * </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioMateriaisTransferidoEntreBibliotecasMBean")
@Scope("request")
public class RelatorioMateriaisTransferidoEntreBibliotecasMBean extends AbstractRelatorioBibliotecaMBean{

	
	
	/** O caminho para o relat�rio. */
	public static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioMateriaisTranferidosEntreBiblioteca.jsp";
	
	/** Guarda o resultado da consulta */
	private List<Object[]> resultado;
	
	/** Guarda o resultado da consulta para fasc�culos transferidos, tem mais informa��es do que exemplares, tem que mostra o usu�rio que autorizou.*/
	private List<Object[]> resultadoFasciculos;
	
	/**
	 * Dado auxiliar utilizado para contar a quantidade de titulos de exemplares transferidos
	 */
	private Set<Integer> contaTitulosDestindoExemplares;
	
	/**
	 * Dado auxiliar utilizado para contar a quantidade de titulos de fasc�culos transferidos
	 */
	private Set<Integer> contaTitulosDestindoFasciculos;
	
	/**
	 * Construtor padr�o
	 */
	public RelatorioMateriaisTransferidoEntreBibliotecasMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <br/><br/>
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		setTitulo( "Relat�rio de Materiais Transferidos Entre Bibliotecas " );
		setDescricao("<p> Relat�rio do tipo anal�tico pelo qual � poss�vel verificar as tranfer�ncias de materiais ocorridas entre as bibliotecas. </p>" +
				     "<p> <strong> Observa��o 1: </strong> O filtro de \"Modalidade de Aquisi��o\" as modalidade de aquisi��o \"Compra\" e \"Doa��o\" s� se aplicam aos fasc�culos, haja visto que para exemplare ainda n�o � guardado essa informa��o. </p>");
		
		
		filtradoPorVariasBibliotecas = true;
		descricaoFiltro1Bibliotecas ="Bibliotecas Origem";
	
		filtradoPorSegundaOpcaoVariasBiblioteca = true;
		descricaoFiltro2Bibliotecas ="Bibliotecas Destino";
		
		campoBibliotecaObrigatorio = false;
		campo2BibliotecaObrigatorio = false;
		
		filtradoPorPeriodo = true;
		filtradoPorTiposDeMaterial = true;
		filtradoPorColecoes = true;
		
		filtradoPorTipoClassificacao = true; // se CDU ou BACK
		
		filtradoPorIntervaloClassificacao = true; // Classe inicial e final
		campoIntervaloClassificacaoObrigatorio = false;
		
		
		filtradoPorModalidadeAquisicao = true; // compra, doa��o e substitui��o
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1); // por padr�o, recupera as transfer�ncias do �ltimo m�s
		
	}

	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * <p>M�todo chamado a partir da p�gina: /sigaa.war/biblioteca/controle_estatistico/relatorioMateriaisTranferidosEntreBiblioteca.jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		
		contaTitulosDestindoExemplares = new HashSet<Integer>();
		contaTitulosDestindoFasciculos = new HashSet<Integer>();
		
		RelatorioMateriaisTransferidoEntreBibliotecaDao dao = getDAO( RelatorioMateriaisTransferidoEntreBibliotecaDao.class );
		configuraDaoRelatorio(dao); // A classe Pai fecha o dao automaticamente
		
		
		String sqlModalidadeAquisicaoExemplares = "";
		String sqlModalidadeAquisicaoFasciculos = "";
		
		switch(tipoModalidadeEscolhida){
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_TODAS:
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_INDEFINIDA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 1 ";
				sqlModalidadeAquisicaoFasciculos = " AND assinatura.modalidade_aquisicao IS NULL ";
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_COMPRA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND assinatura.modalidade_aquisicao = "+Assinatura.MODALIDADE_COMPRA;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_DOACAO:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND assinatura.modalidade_aquisicao = "+Assinatura.MODALIDADE_DOACAO;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_SUBSITITUICAO:
				sqlModalidadeAquisicaoExemplares = " AND exemplar.id_exemplar_que_eu_substituo IS NOT NULL ";
				sqlModalidadeAquisicaoFasciculos = " AND fasciculo.id_fasciculo_que_eu_substituo IS NOT NULL ";
				break;
		}
		
		resultado = dao.findExemplaresTranferidosEntreBiblioteca(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasBibliotecas2)
				, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial)
				, inicioPeriodo, fimPeriodo, classificacaoEscolhida, classeInicial, classeFinal, sqlModalidadeAquisicaoExemplares);
		
		resultadoFasciculos = dao.findFasciculosTranferidosEntreBiblioteca(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasBibliotecas2)
				, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial)
				, inicioPeriodo, fimPeriodo, classificacaoEscolhida, classeInicial, classeFinal, sqlModalidadeAquisicaoFasciculos);
		
		
		if ( (resultado == null || resultado.size() == 0) && (resultadoFasciculos == null || resultadoFasciculos.size() == 0) ) {
			addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			return null;
		}
		
			
		// Para cada resultado, substitui o id do T�tulo pelo seu formato de refer�ncia.
		for (Object[] object : resultado) {
			int idTitulo = (Integer)  object[9];
			
			contaTitulosDestindoExemplares.add(idTitulo); // elimina os repetidos
			
			String temp = new FormatosBibliograficosUtil().gerarFormatoReferencia(new TituloCatalografico(idTitulo), false);
		
			// Trata a exibi��o de negrito //
			if(StringUtils.notEmpty(temp) ){
				temp.replaceAll(FormatosBibliograficosUtil.INICIO_NEGRITO, "");
				temp.replaceAll(FormatosBibliograficosUtil.FINAL_NEGRITO, "");
			}
			
			object[9] = temp;
		}
		
		// Para cada resultado, substitui o id do T�tulo pelo seu formato de refer�ncia.
		for (Object[] object : resultadoFasciculos) {
			int idTitulo = (Integer)  object[12];
			
			contaTitulosDestindoFasciculos.add(idTitulo); // elimina os repetidos
			
			String temp = new FormatosBibliograficosUtil().gerarFormatoReferencia(new TituloCatalografico(idTitulo), false);
			
			// Trata a exibi��o de negrito //
			if(StringUtils.notEmpty(temp) ){
				temp.replaceAll(FormatosBibliograficosUtil.INICIO_NEGRITO, "");
				temp.replaceAll(FormatosBibliograficosUtil.FINAL_NEGRITO, "");
			}
			
			object[12] = temp;
		}
			
		
		
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

	public List<Object[]> getResultado() {
		return resultado;
	}

	public void setResultado(List<Object[]> resultado) {
		this.resultado = resultado;
	}

	public List<Object[]> getResultadoFasciculos() {
		return resultadoFasciculos;
	}

	public void setResultadoFasciculos(List<Object[]> resultadoFasciculos) {
		this.resultadoFasciculos = resultadoFasciculos;
	}

	/**
	 *  Retorna a quantidade de t�tulos cujos exemplare foram movimentados no per�odo
	 *
	 * @return
	 */
	public int getQuantidadeTitulosExemplares() {
		
		if(contaTitulosDestindoExemplares == null)
			return 0;
		else
			return contaTitulosDestindoExemplares.size();
	}

	/**
	 *  Retorna a quantidade de t�tulos cujos fasciculos foram movimentados no per�odo
	 *
	 * @return
	 */
	public int getQuantidadeTitulosFasciculos() {
		
		if(contaTitulosDestindoFasciculos == null)
			return 0;
		else
			return contaTitulosDestindoFasciculos.size();
	}
	
	
	

}
