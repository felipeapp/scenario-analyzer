/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 *  Classe que converte os dados digitados pelo usu�rio  de um arquivo MARC em um T�tulo do sistema.
 * 
 *  
 *  <pre>
 *    <p> O padr�o para analise da informa��es: </p> 
 *    J� que n�o t�m-se as informa��es dos diret�rios - que est�o 
 *  presentes nos arquivos - para indicar a onde come�am e terminam cada campo MARC.                 
 *	    Deve-se digitar cada campo em uma linha separada. O tr�s primeiros caracteres  
 *	de uma linha s�o sempre a etiqueta do campo. No caso de campos de dados os pr�ximos
 *  dois caracteres devem  ser os indicadores caso algum deles n�o exista, deve-se     
 *  colocar o car�ter '_'.                                                             
 *      Os subcampos s�o limitados pelo car�ter '|', ou seja, tudo que tiver entre dois
 *  caracteres '|' at� o final de uma linha � considerado dado de um subcampo. Nos     
 *  dados dos subcampos n�o pode existir nenhum car�ter '|'. </pre>
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
	 * M�todo que extrai os campos de Controle de Dados de um texto como esse: <br/>
	 * 
	 *  <pre>
	 *  LDR	00573nam a22001935a 450
	 *  001 	13076
	 *  005 	00000000000000.0 
	 *  100 	1_ |a Sobrenome, Nome. 
	 *  245 	14 |a T�tulo : |b subtitulo  / |c Complem. do t�tulo. 
	 *  260 	0_ |a Local. : |b Editora, |c 2000.
	 *	</pre>
	 *
	 *  e atribui ao um Objeto <code>TituloCatalografico</code>.
	 *
	 * @param obj o t�tulo onde os campos v�o ser criados
	 * @param arquivoDigitado os campos em formato de texto
	 * @param tipoEtiqueta diz se est� sendo realizada uma importa��o bibliogr�fica ou de autoridades
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
				
				if(linha.length() == 0) continue; // se linha estiver vazia avan�a para pr�xima linha
				
				String tag = extraiTagEtiqueta(linha);
				
				if( new Etiqueta(tag,  (short)0).isEtiquetaControleComparandoPelaTag()){
					
					Etiqueta e = new Etiqueta(tag, bibliografica ? TipoCatalogacao.BIBLIOGRAFICA : TipoCatalogacao.AUTORIDADE);
					
					String dados = extraiDadosControle(linha);
					
					dados = limitaTamanhoDadosControle(dados, e);
					
					
					// J� adiciona o campo de controle ao t�tulo e vice-versa
					if(bibliografica)
						new CampoControle(dados, listaEtiquetas.getEtiqueta(tag, TipoCatalogacao.BIBLIOGRAFICA), -1, t);
					if(autoridades)
						new CampoControle(dados, listaEtiquetas.getEtiqueta(tag, TipoCatalogacao.AUTORIDADE), -1, a);
					
				}else{  // so pode ser campo de dado
					
					if (!  new Etiqueta(tag, (short) 0).isEquetaLocal() || importarCamposLocais ) { // retira os campo locais na importa��o
					
						Character indicador1 = extraiIndicador1(linha).charAt(0);
						
						if(indicador1.charValue() == '_') 
							indicador1 = new Character(' '); // n�o existe indicador1
						
						Character indicador2 = extraiIndicador2(linha).charAt(0);
						
						if(indicador2.charValue() == '_') 
							indicador2 = new Character(' '); // n�o existe indicador2
						
						CampoDados cd = null;
						
						if(bibliografica)
							cd = new CampoDados( listaEtiquetas.getEtiqueta(tag, TipoCatalogacao.BIBLIOGRAFICA), indicador1, indicador2, t, -1);
						if(autoridades)
							cd = new CampoDados( listaEtiquetas.getEtiqueta(tag, TipoCatalogacao.AUTORIDADE), indicador1, indicador2, a, -1);
						
						while(linha.length() > 0 ){
							String codigo = extraiCodigoSubcampo(linha);
							String dados = extraiDadosSubCampo(linha);
							
							new SubCampo(codigo.charAt(0), dados, cd, -1); // aqui j� adiciona o sub campo ao campo dados
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
		
		final int LIMITE_PAGINA = 50; // limite que cabe na p�gina
		
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
	 * Pega o c�digo do subcampo que vem logo em seguida retirando o '$'
	 */
	private static String extraiCodigoSubcampo(StringBuilder linha){
		return getProximoDado(1, 2, linha);
	}
	
	/**
	 * Pega o que sobrou ate o pr�ximo "$", ou ate o final da linha
	 */
	private static String extraiDadosSubCampo(StringBuilder linha){
		
		if(linha.indexOf("|") >= 0){
			return getProximoDado(0, linha.indexOf("|")-1 , linha);
		}else{
			return getProximoDado(0, linha.length() , linha);
		}
	
	}
	
	
	/**
	 * Pega o dado desconsiderando os espa�os de tabula��es do usu�rio no come�o dos dados
	 * e remove os dados de linha, ou seja.
	 */
	private static String getProximoDado(int posicaoIncialTemp, int posicaoFinalTemp, StringBuilder linha){
		int offset = calculaOffSet(linha);
		
		int posicaoInicial =  posicaoIncialTemp+offset <= linha.length() ? posicaoIncialTemp+offset : linha.length();
		int posicaoFinal = posicaoFinalTemp+offset <= linha.length() ? posicaoFinalTemp+offset : linha.length();
		
		String temp = linha.substring(posicaoInicial, posicaoFinal);
		
		linha.delete(0, posicaoFinal); // sempre paga do come�o da linha
		
		return temp;
	}
	
	
	
	/**
	 * Calcula espa�os e tabula��es do usu�rio
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
	 *   OBS para testar tem que retirar as chamadas a getEtiqueta() porque l� usa os DAOs 
	 * da arquitetura
	 * 
	 * @param args
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public static void main(String[] args) throws DAOException, NegocioException {

		parseTextoMARC(new TituloCatalografico(), "           LDR	00573nam a22001935a 450   \n001 	13076   \n                       005 	00000000000000.0   \n100 	1_ $a Sobrenome, Nome.   \n245 	14 $a Titulo : $b subtitulo  / $c Complem. do t�tulo.   \n260 	0_ $a Local. : $b Editora, $c 2000.", TipoCatalogacao.BIBLIOGRAFICA, true);
		
	}

}
