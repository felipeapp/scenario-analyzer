/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 05/09/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioOcorrenciasPerdaMaterialDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioOcorrenciaPerdaMaterial;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioOcorrenciaPerdaMaterial.SituacaoDevolucao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;

/**
 *
 * <p>MBean que gerencia as ocorrências de perda de material registradas no sistema. </p>
 *
 * <p> <i> Perda de material é um tipo de prorrogação que o empréstimos pode sofrer. Esse prorrogação ocorre no momento que em 
 * o usuário comunica a perda de um material</i> </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioOcorrenciasPerdaMaterialMBean")
@Scope("request")
public class RelatorioOcorrenciasPerdaMaterialMBean extends AbstractRelatorioBibliotecaMBean{

	
	/**
	 * A página onde é visualizado os dados do relatório
	 */
	private static final String PAGINA_DO_RELATORIO_SINTETICO = "/biblioteca/controle_estatistico/relatorioOcorrenciasPerdaMaterialSintetico.jsp";

	
	/**
	 * A página onde é visualizado os dados do relatório
	 */
	private static final String PAGINA_DO_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioOcorrenciasPerdaMaterialAnalitico.jsp";
	
	
	/** O resultado da consulta do relatório. Guarda o sintético*/
	List<Object[]> resultado; 
	
	/** O resultado da consulta do relatório. Guarda o analítico */
	private List<DadosRelatorioOcorrenciaPerdaMaterial> resultadoAnalitico;

	/**Guarda a quantidade de comunicações ainda em aberto. Usado no relatório analítico. */
	private int quantidadeComunicacoesEmAberto;
	/**Guarda a quantidade de comunicações cujo material foi reposto por um similar. Usado no relatório analítico. */
	private int quantidadeComunicacoesRepostosSimilar;
	/**Guarda a quantidade de comunicações cujo material foi reposto por um equivalente. Usado no relatório analítico. */
	private int quantidadeComunicacoesRepostosEquivalente;
	/**Guarda a quantidade de comunicações cujo material foi reposto e substituído. Usado no relatório analítico.*/
	private int quantidadeComunicacoesRepostosESubstituidos;
	/**Guarda a quantidade de comunicações cujo material não  foi reposto. Usado no relatório analítico. */
	private int quantidadeComunicacoesNaoRepostos;
	
	/**Guarda a quantidade materiais perdiso mas que ainda continuam no acervo. Usado no relatório analítico.*/
	private int quantidadeMateriasNaoBaixados;
	
	/**Guarda a quantidade total. Usado no relatório analítico. */
	private int quantidadeTotalPerdas;
	
	public RelatorioOcorrenciasPerdaMaterialMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	/**
	 * Configura o relatório.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 */
	@Override
	public void configurar() {
		
		setTitulo("Relatório de Ocorrências de Perda de Material ");		
		setDescricao(" <p> Neste relatório é possível consultar as perdas de materiais comunicadas no sistema. </p> " +
				     " <p> No relatório <strong>sintético</strong> é possível visualizar a quantidade de perdas de materiais no período selecionado agrupadas por biblioteca. </p>"+
					 " <p> No relatório <strong>analítico</strong> é apresentada uma listagem dos Títulos cujas perdas de materiais foram comunicadas, bem como ver o motivo da comunicação, quem realizou a comunicação e para qual período o empréstimo foi prorrogado. </p>" +
					 " <p> O Filtro \"Mostrar Informações das Prorrogações\" só é considerado para o relatório Analítico. </p> ");
		
		setFiltradoPorVariasBibliotecas(true);
		campoBibliotecaObrigatorio = false;
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);  // perdas do último mês por padrão.
		setFiltradoPorPeriodo(true);
		
		setFiltradoPorBooleano(true);
		dadoBooleano = false;
		descricaoDadoBooleano = "Mostrar Informações das Prorrogações? ";
		
		filtradoPorFormaRelatorio = true;  // Sintético ou Analítico
		
	}
	
	
	/**
	 * Realiza a consulta e organiza os resultados.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioOcorrenciasPerdaMaterialDao dao = getDAO(RelatorioOcorrenciasPerdaMaterialDao.class);
		configuraDaoRelatorio(dao); // fecha o dao automaticamente após a consulta
		
		if ( formatoRelatorio == SINTETICO ){
			
			/**
			 * Object[0] = id da Biblioteca
			 * Object[1] = Descrição da Biblioteca 
			 * Object[2] = Quantidade em Aberto
			 * Object[3] = Quantidade Reposto Similar
			 * Object[4] = Quantidade Reposto Equivalente
			 * Object[5] = Quantidade Substituído
			 * Object[6] = Quantidade em Não Reposto
			 * Object[6] = Quantidade em Não Baixado
			 */
			resultado = dao.countOcorrenciasPerdaMaterial(UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
		
			if( resultado.size() == 0){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			
			return forward( PAGINA_DO_RELATORIO_SINTETICO );
		
		}else{
			
			
			/*
			 * Object[0] = id do emprétimo
			 * Object[1] = código de barras
			 * Object[2] = descrição da situação
			 * Object[3] = se o material está baixado
			 * Object[4] = o id do Título do material
			 * Object[5] = informação material baixado
			 * Object[6] = biblioteca
			 * Object[7] = tipo de devolução, se ocorreu
			 * Object[8] = motivo da não entrega do material, se ele não foi reposto pelo usuário
			 * Object[9] =  o usuário que perdeu o material
			 * Object[10] = o código de barras do material que substituíu o mateiral baixado
			 * Object[11] = o usuário que devolveu
			 * Object[12] = a data que foi devolvido
			 * Object[13] = a data inicial da prorrogação
			 * Object[14] =   a data final da prorrogação
			 * Object[15] =   quem fez a comunicação de perda
			 * Object[16] =   a data que a comunicação de perda foi realizada
			 */
			resultado =  dao.listaOcorrenciasPerdaMaterial(UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
		
			if( resultado.size() == 0){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			resultadoAnalitico = new ArrayList<DadosRelatorioOcorrenciaPerdaMaterial>();
			
			
			List<TituloCatalografico> listaTitulosTemp = new ArrayList<TituloCatalografico>();
			
			for (Object[] linha : resultado) {
				listaTitulosTemp.add(new TituloCatalografico(  (Integer)linha[4] ) ); 
			}
			
			// Gera o conjunto de formatos de referência de uma vez para não gerar várias consulas ao banco. ///
			List<Object[]> formatosReferenciaTemp = new FormatosBibliograficosUtil().gerarFormatosReferenciaSeparados( listaTitulosTemp , false);
			
			for (Object[] linha : resultado) {
				
				
				DadosRelatorioOcorrenciaPerdaMaterial dados = new DadosRelatorioOcorrenciaPerdaMaterial((Integer)linha[0], (String)linha[1], (String)linha[2], (Boolean)linha[3], (Integer)linha[4]
				   , (String)linha[5], (String)linha[6], (Short)linha[7], (String)linha[8], (String)linha[9], (String)linha[10], (String)linha[11], (Date)linha[12]);
				
				
				dados.setDescricaoTitulo(  objtemFormatoReferenciaTituloMaterial(formatosReferenciaTemp, linha));
				
				// Vem repetido para cada prorrogação sofrida pelo empréstimo //
				if(resultadoAnalitico.contains(dados)){
					dados = resultadoAnalitico.get(resultadoAnalitico.indexOf(dados));
				}else{
					resultadoAnalitico.add(dados);
				}
				
				// Se o usuário escolheu mostrar as informação de cadas renovação, por padrão não mostrar porque fica muitos dados
				if(dadoBooleano)
					dados.adicionarProrrogacao( new DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial((Date)linha[13], (Date)linha[14], (String)linha[15], (String)linha[16]));
			}
			
			quantidadeComunicacoesEmAberto = 0;
			quantidadeComunicacoesRepostosSimilar = 0;
			setQuantidadeComunicacoesRepostosEquivalente(0);
			quantidadeComunicacoesRepostosESubstituidos = 0;
			quantidadeComunicacoesNaoRepostos = 0;
			quantidadeMateriasNaoBaixados = 0;
			
			// Calcula a situação e totaliza as quantiades //
			for (DadosRelatorioOcorrenciaPerdaMaterial dados : resultadoAnalitico) {
				dados.calculaSituacaoPerda();
				
				if(dados.getSituacaoDevolucao() == SituacaoDevolucao.EM_ABERTO)
					quantidadeComunicacoesEmAberto++;
				if(dados.getSituacaoDevolucao() == SituacaoDevolucao.REPOSTO_SIMILAR)
					quantidadeComunicacoesRepostosSimilar++;
				if (dados.getSituacaoDevolucao() == SituacaoDevolucao.REPOSTO_EQUIVALENTE)
					setQuantidadeComunicacoesRepostosEquivalente(getQuantidadeComunicacoesRepostosEquivalente() + 1);
				if(dados.getSituacaoDevolucao() == SituacaoDevolucao.SUBSTITUIDO)
					quantidadeComunicacoesRepostosESubstituidos++;
				if(dados.getSituacaoDevolucao() == SituacaoDevolucao.NAO_REPOSTO)
					quantidadeComunicacoesNaoRepostos++;
				
				if( ( dados.getSituacaoDevolucao() == SituacaoDevolucao.REPOSTO_EQUIVALENTE
					|| dados.getSituacaoDevolucao() == SituacaoDevolucao.SUBSTITUIDO ) && ! dados.isBaixado())
					quantidadeMateriasNaoBaixados++;
				
			}
			
			quantidadeTotalPerdas = resultadoAnalitico.size();
			
			return forward( PAGINA_DO_RELATORIO_ANALITICO );
		
		} // else analítico
		
	}


	/**
	 * Obpem o formato de referência para o título do material a partir da lista de formatos gerados em memória, 
	 * para ser mostrado para o usuário no relatório.
	 * 
	 * @param formatosReferenciaTemp
	 * @param linha
	 * @param dados
	 */
	private String objtemFormatoReferenciaTituloMaterial(	List<Object[]> formatosReferenciaTemp, Object[] linha) {
		
		int posicao = 0;
		
		String formatoReferencia ="";
		
		forInterno:
		for ( posicao = 0 ;  posicao < formatosReferenciaTemp.size(); posicao ++ ) {
			if(   ((Integer) linha[4]).equals( formatosReferenciaTemp.get(posicao)[0] )){ // se achou o formato de referência 
				formatoReferencia = (String) formatosReferenciaTemp.get(posicao)[1]; // troca o id do título pelo seu formato de referência
				
				// Trata a exibição de negrito, no caso como a saída é uma arquivo texto normal, não tem. //
				if(StringUtils.notEmpty(formatoReferencia)){
					formatoReferencia = formatoReferencia.replaceAll(FormatosBibliograficosUtil.INICIO_NEGRITO, "");
					formatoReferencia = formatoReferencia.replaceAll(FormatosBibliograficosUtil.FINAL_NEGRITO, "");
				}
				
				break forInterno;
			}
		}
		
		formatosReferenciaTemp.remove(posicao);
		
		return formatoReferencia;
	}
	
	public List<Object[]> getResultado() {
		return resultado;
	}


	public void setResultado(List<Object[]> resultado) {
		this.resultado = resultado;
	}
	

	public List<DadosRelatorioOcorrenciaPerdaMaterial> getResultadoAnalitico() {
		return resultadoAnalitico;
	}
	
	public void setResultadoAnalitico(List<DadosRelatorioOcorrenciaPerdaMaterial> resultadoAnalitico) {
		this.resultadoAnalitico = resultadoAnalitico;
	}

	
	

	public int getQuantidadeComunicacoesEmAberto() {
		return quantidadeComunicacoesEmAberto;
	}


	public void setQuantidadeComunicacoesEmAberto(int quantidadeComunicacoesEmAberto) {
		this.quantidadeComunicacoesEmAberto = quantidadeComunicacoesEmAberto;
	}


	public int getQuantidadeComunicacoesRepostosSimilar() {
		return quantidadeComunicacoesRepostosSimilar;
	}


	public void setQuantidadeComunicacoesRepostosSimilar(int quantidadeComunicacoesRepostosSimilar) {
		this.quantidadeComunicacoesRepostosSimilar = quantidadeComunicacoesRepostosSimilar;
	}


	public int getQuantidadeComunicacoesRepostosESubstituidos() {
		return quantidadeComunicacoesRepostosESubstituidos;
	}


	public void setQuantidadeComunicacoesRepostosESubstituidos(
			int quantidadeComunicacoesRepostosESubstituidos) {
		this.quantidadeComunicacoesRepostosESubstituidos = quantidadeComunicacoesRepostosESubstituidos;
	}


	public int getQuantidadeComunicacoesNaoRepostos() {
		return quantidadeComunicacoesNaoRepostos;
	}


	public void setQuantidadeComunicacoesNaoRepostos(int quantidadeComunicacoesNaoRepostos) {
		this.quantidadeComunicacoesNaoRepostos = quantidadeComunicacoesNaoRepostos;
	}
	

	public int getQuantidadeMateriasNaoBaixados() {
		return quantidadeMateriasNaoBaixados;
	}


	public void setQuantidadeMateriasNaoBaixados(int quantidadeMateriasNaoBaixados) {
		this.quantidadeMateriasNaoBaixados = quantidadeMateriasNaoBaixados;
	}


	public int getQuantidadeTotalPerdas() {
		return quantidadeTotalPerdas;
	}


	public void setQuantidadeTotalPerdas(int quantidadeTotalPerdas) {
		this.quantidadeTotalPerdas = quantidadeTotalPerdas;
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


	public int getQuantidadeComunicacoesRepostosEquivalente() {
		return quantidadeComunicacoesRepostosEquivalente;
	}


	public void setQuantidadeComunicacoesRepostosEquivalente(
			int quantidadeComunicacoesRepostosEquivalente) {
		this.quantidadeComunicacoesRepostosEquivalente = quantidadeComunicacoesRepostosEquivalente;
	}
	
	
}






