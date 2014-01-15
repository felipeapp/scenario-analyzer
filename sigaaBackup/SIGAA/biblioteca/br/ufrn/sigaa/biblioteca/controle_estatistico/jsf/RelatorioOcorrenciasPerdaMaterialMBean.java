/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>MBean que gerencia as ocorr�ncias de perda de material registradas no sistema. </p>
 *
 * <p> <i> Perda de material � um tipo de prorroga��o que o empr�stimos pode sofrer. Esse prorroga��o ocorre no momento que em 
 * o usu�rio comunica a perda de um material</i> </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioOcorrenciasPerdaMaterialMBean")
@Scope("request")
public class RelatorioOcorrenciasPerdaMaterialMBean extends AbstractRelatorioBibliotecaMBean{

	
	/**
	 * A p�gina onde � visualizado os dados do relat�rio
	 */
	private static final String PAGINA_DO_RELATORIO_SINTETICO = "/biblioteca/controle_estatistico/relatorioOcorrenciasPerdaMaterialSintetico.jsp";

	
	/**
	 * A p�gina onde � visualizado os dados do relat�rio
	 */
	private static final String PAGINA_DO_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioOcorrenciasPerdaMaterialAnalitico.jsp";
	
	
	/** O resultado da consulta do relat�rio. Guarda o sint�tico*/
	List<Object[]> resultado; 
	
	/** O resultado da consulta do relat�rio. Guarda o anal�tico */
	private List<DadosRelatorioOcorrenciaPerdaMaterial> resultadoAnalitico;

	/**Guarda a quantidade de comunica��es ainda em aberto. Usado no relat�rio anal�tico. */
	private int quantidadeComunicacoesEmAberto;
	/**Guarda a quantidade de comunica��es cujo material foi reposto por um similar. Usado no relat�rio anal�tico. */
	private int quantidadeComunicacoesRepostosSimilar;
	/**Guarda a quantidade de comunica��es cujo material foi reposto por um equivalente. Usado no relat�rio anal�tico. */
	private int quantidadeComunicacoesRepostosEquivalente;
	/**Guarda a quantidade de comunica��es cujo material foi reposto e substitu�do. Usado no relat�rio anal�tico.*/
	private int quantidadeComunicacoesRepostosESubstituidos;
	/**Guarda a quantidade de comunica��es cujo material n�o  foi reposto. Usado no relat�rio anal�tico. */
	private int quantidadeComunicacoesNaoRepostos;
	
	/**Guarda a quantidade materiais perdiso mas que ainda continuam no acervo. Usado no relat�rio anal�tico.*/
	private int quantidadeMateriasNaoBaixados;
	
	/**Guarda a quantidade total. Usado no relat�rio anal�tico. */
	private int quantidadeTotalPerdas;
	
	public RelatorioOcorrenciasPerdaMaterialMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	/**
	 * Configura o relat�rio.
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 */
	@Override
	public void configurar() {
		
		setTitulo("Relat�rio de Ocorr�ncias de Perda de Material ");		
		setDescricao(" <p> Neste relat�rio � poss�vel consultar as perdas de materiais comunicadas no sistema. </p> " +
				     " <p> No relat�rio <strong>sint�tico</strong> � poss�vel visualizar a quantidade de perdas de materiais no per�odo selecionado agrupadas por biblioteca. </p>"+
					 " <p> No relat�rio <strong>anal�tico</strong> � apresentada uma listagem dos T�tulos cujas perdas de materiais foram comunicadas, bem como ver o motivo da comunica��o, quem realizou a comunica��o e para qual per�odo o empr�stimo foi prorrogado. </p>" +
					 " <p> O Filtro \"Mostrar Informa��es das Prorroga��es\" s� � considerado para o relat�rio Anal�tico. </p> ");
		
		setFiltradoPorVariasBibliotecas(true);
		campoBibliotecaObrigatorio = false;
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);  // perdas do �ltimo m�s por padr�o.
		setFiltradoPorPeriodo(true);
		
		setFiltradoPorBooleano(true);
		dadoBooleano = false;
		descricaoDadoBooleano = "Mostrar Informa��es das Prorroga��es? ";
		
		filtradoPorFormaRelatorio = true;  // Sint�tico ou Anal�tico
		
	}
	
	
	/**
	 * Realiza a consulta e organiza os resultados.
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioOcorrenciasPerdaMaterialDao dao = getDAO(RelatorioOcorrenciasPerdaMaterialDao.class);
		configuraDaoRelatorio(dao); // fecha o dao automaticamente ap�s a consulta
		
		if ( formatoRelatorio == SINTETICO ){
			
			/**
			 * Object[0] = id da Biblioteca
			 * Object[1] = Descri��o da Biblioteca 
			 * Object[2] = Quantidade em Aberto
			 * Object[3] = Quantidade Reposto Similar
			 * Object[4] = Quantidade Reposto Equivalente
			 * Object[5] = Quantidade Substitu�do
			 * Object[6] = Quantidade em N�o Reposto
			 * Object[6] = Quantidade em N�o Baixado
			 */
			resultado = dao.countOcorrenciasPerdaMaterial(UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo);
		
			if( resultado.size() == 0){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			
			return forward( PAGINA_DO_RELATORIO_SINTETICO );
		
		}else{
			
			
			/*
			 * Object[0] = id do empr�timo
			 * Object[1] = c�digo de barras
			 * Object[2] = descri��o da situa��o
			 * Object[3] = se o material est� baixado
			 * Object[4] = o id do T�tulo do material
			 * Object[5] = informa��o material baixado
			 * Object[6] = biblioteca
			 * Object[7] = tipo de devolu��o, se ocorreu
			 * Object[8] = motivo da n�o entrega do material, se ele n�o foi reposto pelo usu�rio
			 * Object[9] =  o usu�rio que perdeu o material
			 * Object[10] = o c�digo de barras do material que substitu�u o mateiral baixado
			 * Object[11] = o usu�rio que devolveu
			 * Object[12] = a data que foi devolvido
			 * Object[13] = a data inicial da prorroga��o
			 * Object[14] =   a data final da prorroga��o
			 * Object[15] =   quem fez a comunica��o de perda
			 * Object[16] =   a data que a comunica��o de perda foi realizada
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
			
			// Gera o conjunto de formatos de refer�ncia de uma vez para n�o gerar v�rias consulas ao banco. ///
			List<Object[]> formatosReferenciaTemp = new FormatosBibliograficosUtil().gerarFormatosReferenciaSeparados( listaTitulosTemp , false);
			
			for (Object[] linha : resultado) {
				
				
				DadosRelatorioOcorrenciaPerdaMaterial dados = new DadosRelatorioOcorrenciaPerdaMaterial((Integer)linha[0], (String)linha[1], (String)linha[2], (Boolean)linha[3], (Integer)linha[4]
				   , (String)linha[5], (String)linha[6], (Short)linha[7], (String)linha[8], (String)linha[9], (String)linha[10], (String)linha[11], (Date)linha[12]);
				
				
				dados.setDescricaoTitulo(  objtemFormatoReferenciaTituloMaterial(formatosReferenciaTemp, linha));
				
				// Vem repetido para cada prorroga��o sofrida pelo empr�stimo //
				if(resultadoAnalitico.contains(dados)){
					dados = resultadoAnalitico.get(resultadoAnalitico.indexOf(dados));
				}else{
					resultadoAnalitico.add(dados);
				}
				
				// Se o usu�rio escolheu mostrar as informa��o de cadas renova��o, por padr�o n�o mostrar porque fica muitos dados
				if(dadoBooleano)
					dados.adicionarProrrogacao( new DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial((Date)linha[13], (Date)linha[14], (String)linha[15], (String)linha[16]));
			}
			
			quantidadeComunicacoesEmAberto = 0;
			quantidadeComunicacoesRepostosSimilar = 0;
			setQuantidadeComunicacoesRepostosEquivalente(0);
			quantidadeComunicacoesRepostosESubstituidos = 0;
			quantidadeComunicacoesNaoRepostos = 0;
			quantidadeMateriasNaoBaixados = 0;
			
			// Calcula a situa��o e totaliza as quantiades //
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
		
		} // else anal�tico
		
	}


	/**
	 * Obpem o formato de refer�ncia para o t�tulo do material a partir da lista de formatos gerados em mem�ria, 
	 * para ser mostrado para o usu�rio no relat�rio.
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
			if(   ((Integer) linha[4]).equals( formatosReferenciaTemp.get(posicao)[0] )){ // se achou o formato de refer�ncia 
				formatoReferencia = (String) formatosReferenciaTemp.get(posicao)[1]; // troca o id do t�tulo pelo seu formato de refer�ncia
				
				// Trata a exibi��o de negrito, no caso como a sa�da � uma arquivo texto normal, n�o tem. //
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


	public int getQuantidadeComunicacoesRepostosEquivalente() {
		return quantidadeComunicacoesRepostosEquivalente;
	}


	public void setQuantidadeComunicacoesRepostosEquivalente(
			int quantidadeComunicacoesRepostosEquivalente) {
		this.quantidadeComunicacoesRepostosEquivalente = quantidadeComunicacoesRepostosEquivalente;
	}
	
	
}






