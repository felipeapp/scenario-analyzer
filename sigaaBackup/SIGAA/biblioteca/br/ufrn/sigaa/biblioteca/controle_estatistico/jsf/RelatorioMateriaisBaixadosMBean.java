/*
 * RelatorioMateriaisBaixadosMBean.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/12/2009
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioMateriaisBaixadosDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioMateriaisBaixados;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 *    MBean que gerencia o relat�rio de materiais baixados do acervo.
 *
 * @author Jadson
 * @author Br�ulio
 * @version 2.0 - Mudando para utilizar o AbstractRelatorioBibliotecaMBean, adicionando novos filtro, como classifica��o CDU
 */
@Component("relatorioMateriaisBaixadosMBean")
@Scope("request")
public class RelatorioMateriaisBaixadosMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** Os resultados do relat�rio anal�tico . */
	private List<DadosRelatorioMateriaisBaixados> dadosRelatorio;
	
	/** Os resultados do relat�rio sint�tico . */
	private List<Object[]> resultadoSintetico;
	
	
	/** O total geral de t�tulos para o relat�rio anal�tico. */
	private int totalTitulos;
	
	
	
	/** P�gina que gera o relat�rio anal�tico que lista os materiais baixados em detalhe. */
	private static final String PAG_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioMateriaisBaixadosAnalitico.jsp";
	
	/** P�gina que gera o relat�rio sint�tico dos outros tipos de agrupamento que n�o sejam por m�s. */
	private static final String  PAG_RELATORIO_SINTETICO =
			"/biblioteca/controle_estatistico/relatorioMateriaisBaixadosSintetico.jsp";
	
	/**
	 * Construtor padr�o
	 */
	public RelatorioMateriaisBaixadosMBean(){
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
		
		setTitulo( "Relat�rio de Materiais Baixados do Acervo" );
		setDescricao("<p> Neste relat�rio � poss�vel consultar os materiais que foram baixados do acervo do sistema. </p>" +
					 "<p> O filtro \"Tipo de Classifica��o\" faz refer�ncia a classifica��o digitada nos campos \"Classe Inicial\" e \"Classe Final\" e tamb�m ser� a classifica��o mostrada no relat�rio anal�tico. </p>" +
				     "<p> <strong> Observa��o 1: </strong> O filtro de \"Modalidade de Aquisi��o\" as modalidade de aquisi��o \"Compra\" e \"Doa��o\" s� se aplicam aos fasc�culos, haja visto que para exemplare ainda n�o � guardado essa informa��o. </p>" + 
				  	 "<p> <strong> Observa��o 2: </strong> O agrupamento s� � considerado para o relat�rio do tipo sint�tico. </p>");
		
		
		filtradoPorVariasBibliotecas = true;
		campoBibliotecaObrigatorio = false;
		
		
		filtradoPorPeriodo = true;
		
		
		filtradoPorTipoClassificacao = true; // se CDU ou BACK ou outra
		
		filtradoPorIntervaloClassificacao = true; // Classe inicial e final
		campoIntervaloClassificacaoObrigatorio = false;
		
		filtradoPorModalidadeAquisicao = true; // compra, doa��o
		
		filtradoPorFormaRelatorio = true; // (analitico ou sintetico)
		filtradoPorCtgMaterial = true;
		
		filtradoPorAgrupamento1 = true;
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1); // por padr�o, recupera as transfer�ncias do �ltimo m�s
		
	}
	
	
	/**
	 * Gera o relat�rio ap�s o usu�rio preencher os filtros.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/formRelatorioMateriaisBaixados.jsp</li></ul>
	 */
	public String gerarRelatorio() throws DAOException {
		
		RelatorioMateriaisBaixadosDao dao = getDAO(RelatorioMateriaisBaixadosDao.class);
		configuraDaoRelatorio(dao);
		
		if ( formatoRelatorio == SINTETICO )
			return gerarRelatorioSintetico(dao);
		else
			return gerarRelatorioAnalitico(dao);
	}
	
	
	/**
	 * Gera o relat�rio anal�tico: um relat�rio com todos os materiais que
	 * foram baixados no per�odo especificado.
	 */
	private String gerarRelatorioAnalitico(RelatorioMateriaisBaixadosDao dao) throws DAOException {
		
		dadosRelatorio = new ArrayList<DadosRelatorioMateriaisBaixados>();
		
		/* Utilizando para contar os t�tulos dos materiais baixados de forma n�o repetida */
		Set<Integer> titulosDosMateriaisBaixados = new HashSet<Integer>();
		
		String sqlModalidadeAquisicaoExemplares = "";
		String sqlModalidadeAquisicaoFasciculos = "";
		
		switch(tipoModalidadeEscolhida){
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_TODAS:
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_INDEFINIDA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 1 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao IS NULL ";
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_COMPRA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao = "+Assinatura.MODALIDADE_COMPRA;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_DOACAO:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao = "+Assinatura.MODALIDADE_DOACAO;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_SUBSITITUICAO:
				sqlModalidadeAquisicaoExemplares = " AND e.id_exemplar_que_eu_substituo IS NOT NULL ";
				sqlModalidadeAquisicaoFasciculos = " AND f.id_fasciculo_que_eu_substituo IS NOT NULL ";
				break;
		}
		
		List<Object[]> lista = dao.findMateriaisBaixadosPorPeriodoAnalitico(
					UFRNUtils.toInteger(variasBibliotecas) , inicioPeriodo, fimPeriodo,
					( ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES ),
					( ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS ),
					classificacaoEscolhida, classeInicial, classeFinal, sqlModalidadeAquisicaoExemplares, sqlModalidadeAquisicaoFasciculos);
		
		if ( lista.isEmpty() ) {
			erros.addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			validacaoDados(erros);
			return null;
		}
		
			
		for (Object[] array : lista) {
			dadosRelatorio.add(
					new DadosRelatorioMateriaisBaixados(
					(Integer)array[0],  // id material
					(String)array[1],   // c�digo barras
					(array[8] != null ? array[8].toString() : null), //numero patrimonio
					(String)array[2],   // motivo baixa
					(Integer) array[3], // id biblioteca 
					(String) array[4], (String) array[5], (Date)array[6], // descri��o biblioteca, nome usu�rio e data baixa
					(String)array[7], // informa��es do T�tulo
					classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1 ? (String) array[9] : null, // as classifica��es bibliogr�ficas
					classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2 ? (String) array[10] : null, // as classifica��es bibliogr�ficas
					classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3 ? (String) array[11] : null, // as classifica��es bibliogr�ficas
					(String) array[12], (String) array[13], // tipo de material e cole��o
					(Integer) array[14]) );  // o id do t�tulo
			
			titulosDosMateriaisBaixados.add(   (Integer) array[14]  );
		}
		
		totalTitulos = titulosDosMateriaisBaixados.size();
		
		Collections.sort(dadosRelatorio);
		
		return forward(PAG_RELATORIO_ANALITICO);
	}
	
	
	/**
	 * Gera o relat�rio sint�tico: um relat�rio com o total de materiais baixados
	 * num determinado ano, com os dados agrupados por m�s, classe CDU, classe
	 * Black, tipo de material ou cole��o.
	 */
	private String gerarRelatorioSintetico(RelatorioMateriaisBaixadosDao dao) throws DAOException {
		
		String sqlModalidadeAquisicaoExemplares = "";
		String sqlModalidadeAquisicaoFasciculos = "";
		
		switch(tipoModalidadeEscolhida){
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_TODAS:
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_INDEFINIDA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 1 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao IS NULL ";
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_COMPRA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao = "+Assinatura.MODALIDADE_COMPRA;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_DOACAO:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao = "+Assinatura.MODALIDADE_DOACAO;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_SUBSITITUICAO:
				sqlModalidadeAquisicaoExemplares = " AND e.id_exemplar_que_eu_substituo IS NOT NULL ";
				sqlModalidadeAquisicaoFasciculos = " AND f.id_fasciculo_que_eu_substituo IS NOT NULL ";
				break;
		}
		
		resultadoSintetico = dao.findQtdMateriaisBaixadosSintetico(UFRNUtils.toInteger(variasBibliotecas) , inicioPeriodo, fimPeriodo,
				( ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES ),
				( ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS ),
				classificacaoEscolhida, classeInicial, classeFinal, tipoModalidadeEscolhida, agrupamento1
				, sqlModalidadeAquisicaoExemplares, sqlModalidadeAquisicaoFasciculos);		
		
		if ( resultadoSintetico == null || resultadoSintetico.size() == 0 ) {
			addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			return null;
		}
		
		AgrupamentoRelatoriosBiblioteca agrupamentoUtilizado = agrupamento1;
		
		if( agrupamentoUtilizado == AgrupamentoRelatoriosBiblioteca.MES){
			
			for (Object[] objects : resultadoSintetico) {
				objects[0] = CalendarUtils.getNomeMes( ((Double)objects[0]).intValue() );   // troca o n�mero pele nome do m�s
			}
		} 
			
		return forward(PAG_RELATORIO_SINTETICO);
		
	}
	

	
	
	// GETs e SETs
	
	public List<DadosRelatorioMateriaisBaixados> getDadosRelatorio() {
		return dadosRelatorio;
	}
	
	public int getTotalTitulos() {
		return totalTitulos;
	}
	
	public String getClasseInicial() {
		return classeInicial;
	}

	public void setClasseInicial(String classeInicial) {
		this.classeInicial = classeInicial;
	}

	public String getClasseFinal() {
		return classeFinal;
	}

	public void setClasseFinal(String classeFinal) {
		this.classeFinal = classeFinal;
	}
	
	public List<Object[]> getResultadoSintetico() {
		return resultadoSintetico;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		
		if(agrupamentos1 == null ){
			agrupamentos1 = new ArrayList<SelectItem>();
			
			agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.MES.valor, AgrupamentoRelatoriosBiblioteca.MES.alias));
			
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao1() )
				agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_1.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao1()));
			
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao2() )
				agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_2.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao2()));
			
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao3() )
				agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_3.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao3()));
			
			agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.COLECAO.valor, AgrupamentoRelatoriosBiblioteca.COLECAO.alias));
			agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.valor, AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.alias));
		}
		
		return agrupamentos1;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		// N�o usado nesse relat�rio //
		return null;
	}


}
