/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/11/2008
 *
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.StringTokenizer;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.CooperacaoTecnicaImportacaoMBean;

/**
 * 
 *  Classe que converte os dados digitados pelo usuário  de um arquivo MARC em um Título do sistema.
 * 
 *  
 *  <pre>
 *    <p> O padrão para analise da informações: </p> 
 *    Já que não têm-se as informações dos diretórios - que estão 
 *  presentes nos arquivos - para indicar a onde começam e terminam cada campo MARC.                 
 *	    Deve-se digitar cada campo em uma linha separada. O três primeiros caracteres  
 *	de uma linha são sempre a etiqueta do campo. No caso de campos de dados os próximos
 *  dois caracteres devem  ser os indicadores caso algum deles não exista, deve-se     
 *  colocar o caráter '_'.                                                             
 *      Os subcampos são limitados pelo caráter '|', ou seja, tudo que tiver entre dois
 *  caracteres '|' até o final de uma linha é considerado dado de um subcampo. Nos     
 *  dados dos subcampos não pode existir nenhum caráter '|'. </pre>
 *
 *
 * @author jadson
 * @since 14/11/2008
 * @version 1.0 criacao da classe
 *
 */
public class ParseTituloCatalograficoUtil {
	
	/**
	 * 
	 * Método que extrai os campos de Controle de Dados de um texto como esse: <br/>
	 * 
	 *  <pre>
	 *  LDR	00573nam a22001935a 450
	 *  001 	13076
	 *  005 	00000000000000.0 
	 *  100 	1_ |a Sobrenome, Nome. 
	 *  245 	14 |a Título : |b subtitulo  / |c Complem. do título. 
	 *  260 	0_ |a Local. : |b Editora, |c 2000.
	 *	</pre>
	 *
	 *  e atribui ao um Objeto <code>TituloCatalografico</code>.
	 *
	 * @param obj o título onde os campos vão ser criados
	 * @param arquivoDigitado os campos em formato de texto
	 * @param tipoEtiqueta diz se está sendo realizada uma importação bibliográfica ou de autoridades
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public static void parseTextoMARC(Object obj, String arquivoDigitado, short tipoCooperacao
			, boolean importarCamposLocais) throws DAOException, NegocioException{
		
		TituloCatalografico t = null;
		Autoridade a = null;
		
		boolean bibliografica = false;
		boolean autoridades = false;
		
		ListaEtiquetas listaEtiquetas = new ListaEtiquetas();
		
		try{
		
			if(tipoCooperacao == CooperacaoTecnicaImportacaoMBean.OPERACAO_BIBLIOGRAFICA){
				bibliografica = true;
				t  = (TituloCatalografico) obj;
			}
			
			if(tipoCooperacao == CooperacaoTecnicaImportacaoMBean.OPERACAO_AUTORIDADE){
				autoridades = true;
				a  = (Autoridade) obj;
			}
			
			StringTokenizer stringTokenizer = new StringTokenizer(arquivoDigitado, "\n");
				
			
			while(stringTokenizer.hasMoreTokens()){
				StringBuilder linha = new StringBuilder(stringTokenizer.nextToken());
	
				if(linha.indexOf("\r") >= 0){ // tire o "\r" do final da linha
					linha.deleteCharAt(linha.indexOf("\r"));
				}
				
				if(linha.length() == 0) continue; // se linha estiver vazia avança para próxima linha
				
				String tag = extraiTagEtiqueta(linha);
				
				if( new Etiqueta(tag,  (short)0).isEtiquetaControleComparandoPelaTag()){
					
					Etiqueta e = new Etiqueta(tag, bibliografica ? TipoCatalogacao.BIBLIOGRAFICA : TipoCatalogacao.AUTORIDADE);
					
					String dados = extraiDadosControle(linha);
					
					dados = limitaTamanhoDadosControle(dados, e);
					
					
					// Já adiciona o campo de controle ao título e vice-versa
					if(bibliografica)
						new CampoControle(dados, listaEtiquetas.getEtiqueta(tag, TipoCatalogacao.BIBLIOGRAFICA), -1, t);
					if(autoridades)
						new CampoControle(dados, listaEtiquetas.getEtiqueta(tag, TipoCatalogacao.AUTORIDADE), -1, a);
					
				}else{  // so pode ser campo de dado
					
					if (!  new Etiqueta(tag, (short) 0).isEquetaLocal() || importarCamposLocais ) { // retira os campo locais na importação
					
						Character indicador1 = extraiIndicador1(linha).charAt(0);
						
						if(indicador1.charValue() == '_') 
							indicador1 = new Character(' '); // não existe indicador1
						
						Character indicador2 = extraiIndicador2(linha).charAt(0);
						
						if(indicador2.charValue() == '_') 
							indicador2 = new Character(' '); // não existe indicador2
						
						CampoDados cd = null;
						
						if(bibliografica)
							cd = new CampoDados( listaEtiquetas.getEtiqueta(tag, TipoCatalogacao.BIBLIOGRAFICA), indicador1, indicador2, t, -1);
						if(autoridades)
							cd = new CampoDados( listaEtiquetas.getEtiqueta(tag, TipoCatalogacao.AUTORIDADE), indicador1, indicador2, a, -1);
						
						while(linha.length() > 0 ){
							String codigo = extraiCodigoSubcampo(linha);
							String dados = extraiDadosSubCampo(linha);
							
							new SubCampo(codigo.charAt(0), dados, cd, -1); // aqui já adiciona o sub campo ao campo dados
						}
					
					}
					
				}
				
			}
		
		}finally{
			listaEtiquetas.fechaConexao();
		}
	}
	
	
	/**
	 * Limita o tamanho dos dados de controle
	 */
	private static String limitaTamanhoDadosControle(String dados, Etiqueta e){
		
		final int LIMITE_PAGINA = 50; // limite que cabe na página
		
		if(e.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO))
			if(dados.length() >= CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO.length())
				return dados.substring(0, CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO.length());
		
		if(e.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO))
			if(dados.length() >= CampoControle.DADOS_CAMPO_006_BIBLIOGRAFICO.length())
				return dados.substring(0, CampoControle.DADOS_CAMPO_006_BIBLIOGRAFICO.length());
		
		if(e.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO))
			if(dados.length() >= CampoControle.DADOS_CAMPO_007_BIBLIOGRAFICO.length())
				return dados.substring(0, CampoControle.DADOS_CAMPO_007_BIBLIOGRAFICO.length());
		
		if(e.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO))
			if(dados.length() >= CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO.length())
				return dados.substring(0, CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO.length());
		
		
		if(e.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE))
			if(dados.length() >= CampoControle.DADOS_CAMPO_LIDER_AUTORIDADE.length())
				return dados.substring(0, CampoControle.DADOS_CAMPO_LIDER_AUTORIDADE.length());
		
		if(e.equals(Etiqueta.CAMPO_008_AUTORIDADE))
			if(dados.length() >= CampoControle.DADOS_CAMPO_008_AUTORIDADE.length())
				return dados.substring(0, CampoControle.DADOS_CAMPO_008_AUTORIDADE.length());
		
		if(dados.length() >= LIMITE_PAGINA)
			return dados.substring(0, LIMITE_PAGINA);
		else	
			return dados;
		
	}
	
	
	/**
	 * pega os dados da etiqueta
	 */
	private static String extraiTagEtiqueta(StringBuilder linha){
		return getProximoDado(0, 3, linha);
	}
	
	
	/**
	 * pega os dados do campo de controle
	 */
	private static String extraiDadosControle(StringBuilder linha){
		return getProximoDado(0, linha.length(), linha);
	}
	
	
	private static String extraiIndicador1(StringBuilder linha){
		return getProximoDado(0, 1, linha);
	}
	
	
	private static String extraiIndicador2(StringBuilder linha){
		return getProximoDado(0, 1, linha);
	}
	
	
	/**
	 * Pega o código do subcampo que vem logo em seguida retirando o '$'
	 */
	private static String extraiCodigoSubcampo(StringBuilder linha){
		return getProximoDado(1, 2, linha);
	}
	
	/**
	 * Pega o que sobrou ate o próximo "$", ou ate o final da linha
	 */
	private static String extraiDadosSubCampo(StringBuilder linha){
		
		if(linha.indexOf("|") >= 0){
			return getProximoDado(0, linha.indexOf("|")-1 , linha);
		}else{
			return getProximoDado(0, linha.length() , linha);
		}
	
	}
	
	
	/**
	 * Pega o dado desconsiderando os espaços de tabulações do usuário no começo dos dados
	 * e remove os dados de linha, ou seja.
	 */
	private static String getProximoDado(int posicaoIncialTemp, int posicaoFinalTemp, StringBuilder linha){
		int offset = calculaOffSet(linha);
		
		int posicaoInicial =  posicaoIncialTemp+offset <= linha.length() ? posicaoIncialTemp+offset : linha.length();
		int posicaoFinal = posicaoFinalTemp+offset <= linha.length() ? posicaoFinalTemp+offset : linha.length();
		
		String temp = linha.substring(posicaoInicial, posicaoFinal);
		
		linha.delete(0, posicaoFinal); // sempre paga do começo da linha
		
		return temp;
	}
	
	
	
	/**
	 * Calcula espaços e tabulações do usuário
	 */
	private static int calculaOffSet(StringBuilder linha){
		int offset = 0;
		
		for (int i = 0; (linha.charAt(i) == ' ' || linha.charAt(i) == '\t') &&  i < linha.length() ; i++) {        
			offset++;
		}
		
		return offset;
	}
	
	
	
	
	/**
	 * 
	 *   OBS para testar tem que retirar as chamadas a getEtiqueta() porque lá usa os DAOs 
	 * da arquitetura
	 * 
	 * @param args
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public static void main(String[] args) throws DAOException, NegocioException {

		parseTextoMARC(new TituloCatalografico(), "           LDR	00573nam a22001935a 450   \n001 	13076   \n                       005 	00000000000000.0   \n100 	1_ $a Sobrenome, Nome.   \n245 	14 $a Titulo : $b subtitulo  / $c Complem. do título.   \n260 	0_ $a Local. : $b Editora, $c 2000.", TipoCatalogacao.BIBLIOGRAFICA, true);
		
	}

}
