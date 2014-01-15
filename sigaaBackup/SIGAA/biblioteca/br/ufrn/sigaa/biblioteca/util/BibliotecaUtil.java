/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 14/10/2008
 */
package br.ufrn.sigaa.biblioteca.util;

import static br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil.retiraPontuacaoCamposBuscas;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.DescritoresDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EtiquetaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ProrrogacaoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ServicosInformacaoReferenciaBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.InventarioAcervoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorCampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorDescritorCampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.CooperacaoTecnicaExportacaoMBean;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 *
 * Classe para m�todos utilit�rios variados da biblioteca que auxiliam em tarefas complexas, formatando
 * o nome do autor de um livro ou configurando as informa��es de um usu�rio da biblioteca.
 *
 * @author Jadson
 * @author Br�ulio
 * 
 * @since 14/10/2008
 * @version 1.0 Cria��o da classe
 */
public class BibliotecaUtil {

	/** C�digo de letra 'A' na tabela ASCII. */
	private static final int LETRA_A_ASCII = 65;
	
	/** C�digo de letra 'Z' na tabela ASCII. */
	private static final int LETRA_Z_ASCII = 90;

	
	//// Usado nas exporta��o para a FGV ////
	
	/** Guarda o tamanho de cada bloco do arquivo PCS. */
	private static final int TAMANHO_BLOCO_ARQUIVO_PCS = 2048;
	
	/** Tamanho da palavra de controle de cada bloco */
	private static final int TAMANHO_PALAVRA_CONTROLE_BLOCO_ARQUIVO_PCS = 5;
	
	////////////////////////////////////////
	
	
	/** Usado no c�lculo do d�gito verificador do m�dulo 10 */
	private static final int MODULO_10 = 10;
	
	/** Usado no c�lculo do d�gito verificador do m�dulo 11 */
	private static final int MODULO_11 = 11;
	
	
	
	

	
	
	/**
	 *   <p>Formato os dados do arquivo "MARC ISO 2709" exportado para a FGV em PCS (Palavra de controle de segmento).
	 *   Formato muito usado para gravar dados em fita magn�tica.</p>
	 *   <p>Divide o arquivo em blocos de 2048 caracteres e no come�o de cada bloco deve conter uma <i>PCS</i>
	 *   de 5 bytes(caracteres) com as seguintes informa��es:</p>
	 *   <p> O byte 1 da PCS indica a situa��o do segmento que segue, podendo assumir um dos seguintes valores: <br/>
	 * 0 - O registro l�gico come�a e termina neste segmento <br/>
	 * 1 - O registro l�gico come�a mas n�o termina neste segmento <br/>
	 * 2 - O registro l�gico n�o come�a e nem termina neste segmento <br/>
	 * 3 - O registro l�gico n�o come�a mas termina neste segmento <br/><br/>
     *
     * Os bytes 2-5 da PCS indicam o tamanho do segmento, incluindo os 5 bytes da PCS</p>
	 *   <p>Usado para exporta��o de arquivos MARC para a FGV</p>
	 *
	 *
	 * @param outputStreams Cada outputStream cont�m os bytes de um registro MARC.
	 * 
	 * @return os bytes do arquivo MARC recebidos formatados em PCS.
	 */
	public static byte[] formataArquivoBibliotegraficoExportacaoParaPCS(List<ByteArrayOutputStream> outputStreams) throws IOException{
	
		ByteArrayOutputStream outputStreamTotal = new ByteArrayOutputStream();
		
		StringBuilder bloco = new StringBuilder();
		
		// Para casa registro //
		for (ByteArrayOutputStream byteArrayOutputStream : outputStreams) {
			
			byte[] registro = byteArrayOutputStream.toByteArray();
		
			String dadosSobra = preencheBloco(new String(registro), bloco, false);
			
			if(dadosSobra == null){ // Todo o registro coube dentro do bloco
				
				if(bloco.length() == TAMANHO_BLOCO_ARQUIVO_PCS){  // Bloco encheu
					outputStreamTotal.write(bloco.toString().getBytes());   // escreve na sa�da
					bloco = new StringBuilder();	// come�a um novo bloco
				}
					
			}else{ // Registro n�o coube dentro do bloco
				
				if(bloco.length() == TAMANHO_BLOCO_ARQUIVO_PCS){  // Bloco encheu
					outputStreamTotal.write(bloco.toString().getBytes());   // escreve na sa�da
					bloco = new StringBuilder();	// come�a um novo bloco
				}
				
				while(dadosSobra != null){  // enquanto o registro todo n�o for dividido em blocos
					
					dadosSobra = preencheBloco(dadosSobra, bloco, true);
					
					if(bloco.length() == TAMANHO_BLOCO_ARQUIVO_PCS){  // Bloco encheu
						outputStreamTotal.write(bloco.toString().getBytes());   // escreve na sa�da
						bloco = new StringBuilder();	// come�a um novo bloco
					}
					
				}
			}
			
		}
		
		
		///////////////   COMPLETA OS 2048 BYTES DO �LTIMO BLOCO //////////////
		
		if(bloco.length() > 0){           // se escreveram alguma coisa no �ltimo bloco
			bloco = completaBloco(bloco);
			outputStreamTotal.write(bloco.toString().getBytes());
		}
		
		return outputStreamTotal.toByteArray();
	}
	
	
	
	
	/**
	 *    M�todo que possui a l�gica de preenchimento dos blocos do arquivo PCS.<br/>
	 *    Preenche o que consegue no bloco, passando e retornando os dados que n�o couberam dentro do bloco.
	 *
	 * @param dadosRegistro        o dados que v�o ser colocados no bloco
	 * @param bloco                representa um bloco do arquivo PCS
	 * @param registroQuebrado     indica que o registro veio quebrado, ou seja, partes do registro j� est�o em outro bloco
	 * @return os caracteres que n�o couberam dentro do bloco
	 */
	private static String preencheBloco(String dadosRegistro, StringBuilder bloco, boolean registroQuebrado){
	
		final char COMECA_E_TERMINA = '0';
		final char COMECA_MAS_NAO_TERMINA = '1';
		final char NAO_COMECA_E_NEM_TERMINA = '2';
		final char NAO_COMECA_MAS_TERMINA = '3';
		
		///////////////////////  REGISTRO CABE DENTRO DO BLOCO ////////////////
		if(dadosRegistro.length()+TAMANHO_PALAVRA_CONTROLE_BLOCO_ARQUIVO_PCS+bloco.toString().length() <= TAMANHO_BLOCO_ARQUIVO_PCS ){
			
			
			if(bloco.length() >= ( TAMANHO_BLOCO_ARQUIVO_PCS - TAMANHO_PALAVRA_CONTROLE_BLOCO_ARQUIVO_PCS  ) ){ // n�o cabe mais nada no bloco
				bloco = completaBloco(bloco);
				return dadosRegistro;        // TODOS OS DADOS DO REGISTRO SOBRARAM
			}else{
			
				// GERA O PCS //
					
				if(registroQuebrado){ // se o registro est� quebrado mas cabe todo no bloco atual
					bloco.append(NAO_COMECA_MAS_TERMINA);
				}else{
					bloco.append(COMECA_E_TERMINA);
				}
				
				bloco.append(formataTamanhoBloco(dadosRegistro.length()+TAMANHO_PALAVRA_CONTROLE_BLOCO_ARQUIVO_PCS));
				
				// GERA OS DADOS //
				
				bloco.append( dadosRegistro);
			
			return null;                     // INDICA QUE N�O SOBRARAM DADOS
		
			}
			
		}else{ //////////////////////  REGISTRO N�O CABE DENTRO DO BLOCO ////////////////
			
			if(bloco.length() >= ( TAMANHO_BLOCO_ARQUIVO_PCS - TAMANHO_PALAVRA_CONTROLE_BLOCO_ARQUIVO_PCS  ) ){ // n�o cabe mais nada no bloco
				bloco = completaBloco(bloco);
				return dadosRegistro;        // TODOS OS DADOS DO REGISTRO SOBRARAM
			}else{
				
				int quatidadeLivreBloco = TAMANHO_BLOCO_ARQUIVO_PCS - (TAMANHO_PALAVRA_CONTROLE_BLOCO_ARQUIVO_PCS+bloco.length());
				
				String dadosBloco = dadosRegistro.substring(0, quatidadeLivreBloco);
				
				if(registroQuebrado){
					bloco.append(NAO_COMECA_E_NEM_TERMINA); // n�o cabe no bloco e j� feio quebrado de outro bloco
				}else{
					bloco.append(COMECA_MAS_NAO_TERMINA); // n�o cabe no bloco mas ainda est� inteiro
				}
				
				bloco.append(formataTamanhoBloco(dadosBloco.length()+TAMANHO_PALAVRA_CONTROLE_BLOCO_ARQUIVO_PCS));
				bloco.append( dadosBloco);
				
				return dadosRegistro.substring(quatidadeLivreBloco, dadosRegistro.length()); // O QUE SOBROU PARA SER COLOCADO EM OUTRO BLOCO
			}
			
		}
		
	}
	
	
	
	/**
	 * M�todo que recebe um inteiro que representa o tamanho do bloco de um arquivo PCS e retorna
	 * uma string de fixo tamanho 4 com a representa��o desse n�mero. <br/>
	 * Se o n�mero tiver menos de 4 d�gitos � preenchido com "0" � esquerda.
	 *
	 */
	private static String formataTamanhoBloco(int tamanhoBloco){
		
		String temp = new String(""+tamanhoBloco);
		
		for (int prt = temp.toString().length(); prt < 4; prt++) {
			temp = "0"+temp.toString();
		}
		
		return temp.toString();
	}
	
	
	/**
	 *  Completa o tamanho do bloco com espa�os em branco
	 */
	private static StringBuilder completaBloco(StringBuilder bloco){
		
		int tamanhoBloco =  bloco.length(); // j� contando com os 5 caracteres do PCS
		
		if(	tamanhoBloco < TAMANHO_BLOCO_ARQUIVO_PCS ){
			
			int quantidade =  TAMANHO_BLOCO_ARQUIVO_PCS - tamanhoBloco;
			
			while (quantidade > 0) {
				bloco.append(" ");
				quantidade--;
			}
		}
		return bloco;
	}
	
	
	
	/**
	 *    M�todo que calcula o d�gito verificador de acordo com a fun��o passada pelo analista
	 * da FGV.
	 *    Porque foi informado que a fun��o usava o algoritmo modulo 11 mas os resultados n�o est�o
	 * batendo.
	 * 
	 *    Na verdade o que o algoritmo deles (FGV) faz � transformar o c�digo "RN" para d�gitos ASCII,
	 * concatenar com o restante dos n�meros e fazer um "Mod 11";
	 * 
	 * 
	 * function CalculaDV(pNumControle : String) : Char;
     *
     *		var
     *			   i      : Integer;
     *			   iDV    : Integer;
     *			   iSoma  : Integer;
     *			   s1Campo: string;
     *			   s2Campo: string;
     *			   s3Campo: string;
     *			   s4Campo: string;
     *	
     *		begin
     *		    s1Campo := IntToStr (10 + Ord(pNumControle[1]) - Ord('A'));
     *		    s2Campo := IntToStr (10 + Ord(pNumControle[2]) - Ord('A'));
     *	
     *		    if (pNumControle[3] in ['A'..'Z'])
     *	
     *		      then s3Campo := IntToStr (10 + Ord(pNumControle[3]) - Ord('A'))
     *	
     *		      else s3Campo := pNumControle[3];
     *	
     *		    if (pNumControle[4] in ['A'..'Z'])
     *	
     *		      then s4Campo := IntToStr (10 + Ord(pNumControle[4]) - Ord('A'))
     *	
     *		      else s4Campo := pNumControle[4];
     *	
     *		     pNumControle := s1Campo +  s2Campo +
     *	
     *		                     s3Campo +  s4Campo +
     *	
     *		                     Copy(pNumControle, 5, 6);
     *	
     *		     iSoma := 0;
     *	
     *		     for i := 1 to 12 do
     *	
     *		       iSoma := iSoma + (StrToInt(pNumControle[i]) * (14 - i));
     *	
     *		     iDV := 11 - (iSoma mod 11);
     *	
     *		     if iDV = 11 then
     *	
     *		       iDV := 0;
     *	
     *		     if iDV = 10 then
     *	
     *		       iDV := 1;
     *	
     *		     Result := PegaPrimeiroChar(IntToStr(iDV));
     *	
     *		end;
	 * 
	 * -- fun��o Copy em Delphi
	 * var s : string;
	 *
     * s:='DELPHI';
     * s := Copy(s,2,3);
     * // resultado: s='ELP';
     *
     * -- fun��o Ord em Delphi
	 * ShowMessage('The ASCII code for "d" is ' + IntToStr(Ord('d')));
     * // resultado: 100
	 */
	public static String geraDigitoVerificadorUsandoAFuncaoDaFGV(String numeroControle){
		
		int iterador;
		int iDV;
		int iSoma;
		String s1Campo;
		String s2Campo;
		String s3Campo;
		String s4Campo;
	
		Character c0 = numeroControle.charAt(0);
		Character c1 = numeroControle.charAt(1);
		Character c2 = numeroControle.charAt(2);
		Character c3 = numeroControle.charAt(3);
		
		
		//////////// Transforma as letras da posi��o 1 e 2 em c�digo ASCII   /////////////
	    s1Campo =  ""+( 10 + ( Integer.valueOf( c0 ) - Integer.valueOf('A') ));

	    s2Campo =  ""+( 10 + ( Integer.valueOf( c1 ) - Integer.valueOf('A') ));
	    	   

	    ///////////  se a terceira e quarta posi��o foram letras, transforma tamb�m /////////////
	    if( Character.isLetter( Character.toUpperCase((numeroControle.charAt(2))) ))
	    	s3Campo = ""+( 10 + ( Integer.valueOf( c2) - Integer.valueOf('A') ));
	    else
	    	s3Campo = ""+numeroControle.charAt(2);
	    
	 
	    if( Character.isLetter( Character.toUpperCase(numeroControle.charAt(3))))
	    	s4Campo = ""+( 10 + ( Integer.valueOf( c3 ) - Integer.valueOf('A') ));
	    else
	    	s4Campo = ""+numeroControle.charAt(3);
	 
	    
        // Concatena as letras do n�mero de controle transformados em c�digo ASCII com o restante
	    // dos d�gitos (tirando as 4 primeiras posi��es que j� foram transformadas )
	    numeroControle = s1Campo +  s2Campo + s3Campo +  s4Campo + numeroControle.substring(4, numeroControle.length());

	    iSoma = 0;
	 
	    // Soma tudo multiplicando por esse fator
	    // OBS.: adapta��o de arrays: em Delphi "1 a 12"; em Java "0 a 11"
	    for (iterador = 1; iterador <= 12; iterador++) {
			iSoma = iSoma + ( Integer.parseInt(""+numeroControle.charAt(iterador-1))  * (14 - iterador) );
		}
	    
	    // Faz o m�dulo 11 ?
	    
	    iDV = 11 - iSoma % 11;
	    
	    if(iDV == 11) iDV = 0;

	    if(iDV == 10) iDV = 1;
	    
	    return ""+(""+iDV).charAt(0);
		
	}
	
	
	
	/**
	 * 
	 * M�todo que gera o digito verificador m�dulo 10
	 *
	 * @param c�digo em cima do qual vai ser gerado o d�gito
	 * @return o digito verificador calculado
	 */
	public static String geraDigitoVerificadorModulo10(String codigo) throws NegocioException{
		
		try{
			Integer.parseInt(""+codigo);
		}catch(NumberFormatException nfe){
			throw new NegocioException(" O c�digo precisa ser num�rico para se gerar o d�gito verificador ");
		}
		
		
		String resultTemp = "";
		int digito;
		
		// multiplica cada digito do c�digo pela sequ�ncia 2 1 2 1 2 1 2 1 ...
		for (int i = 0; i < codigo.length(); i++) {
			digito = Integer.parseInt(""+codigo.charAt(i));
			
			if(i % 2 == 0){
				resultTemp += Integer.toString( digito * 2);
			}else{
				resultTemp += Integer.toString ( digito * 1);
			}
		}
		
		int resultadoSoma = 0;
		
		for (int i = 0; i < resultTemp.length(); i++) {
			resultadoSoma +=  Integer.parseInt(""+ resultTemp.charAt(i));
		}
		
		resultadoSoma = resultadoSoma % MODULO_10;
		
		int digitoVerificador = 0;
		
		if(resultadoSoma == 0){
			digitoVerificador = 0;
		}else{
			digitoVerificador = MODULO_10 - resultadoSoma;
		}
		
		return String.valueOf(digitoVerificador);
	}
	
	
	
	
	
	
	
	
	/**
	 * M�todo que gera o digito verificador modulo 11
	 * 
	 * C�lculos retirados de {@link http://www.banknote.com.br/module.htm.}
	 *
	 * @param c�digo em cima do qual vai ser gerado o digito. N�o pode ter letras no c�digo, pois
	 * ser�o feitas opera��es matem�ticas em cima de cada d�gito.
	 * 
	 * @return o digito verificador calculado
	 */
	public static String geraDigitoVerificadorModulo11(String codigo) throws NegocioException{
		
		try{
			Integer.parseInt(""+codigo);
		}catch(NumberFormatException nfe){
			throw new NegocioException(" O c�digo precisa ser num�rico para se gerar o d�gito verificador ");
		}
		
		int resultadoSoma = 0;
		
		int[] sequenciaMultiplicacao = new int[]{2, 3, 4, 5, 6, 7, 8, 9};
		
		int contador = 0;
		
		// multiplica cada digito do c�digo pela sequ�ncia 2,3,4,5,6,7,8,9,2,3, ... posicionados da direita para a esquerda.
		for (int ptr = codigo.length()-1; ptr >= 0 ; ptr--) {
			
			int posicaoSequencia =  contador++ %(sequenciaMultiplicacao.length );
			
			resultadoSoma += Integer.parseInt(""+codigo.charAt(ptr)) * sequenciaMultiplicacao[posicaoSequencia];
			
		}
		
		int restoDivisao = resultadoSoma % MODULO_11;
		
		int digitoVerificador = 0;
		
		if(restoDivisao == 0 || restoDivisao == 10){
			digitoVerificador = 0;
		}else{
			digitoVerificador = MODULO_11 - restoDivisao;
		}
		
		return String.valueOf(digitoVerificador);
	}
	
	/**
	 *   Gera a data no padr�o yyyyMMddhhmmss.f para ser usado no campo 005 das entidades MARC com
	 *   a data atual.
	 *   <p>Onde: f = d�cima fra��o do segundo.</p>.
	 *   <p>Hora no formato 00-23</p>.
	 */
	public static String geraDataHoraNoFormatoANSIX3(){
		return geraDataHoraNoFormatoANSIX3(new Date() );
	}
	
	/**
	 *   Gera a data no padr�o yyyyMMddhhmmss.f para ser usado no campo 005 das entidades MARC com
	 *   a data passada.
	 *   <p>Onde: f = d�cima fra��o do segundo.</p>.
	 *   <p>Hora no formato 00-23</p>.
	 */
	public static String geraDataHoraNoFormatoANSIX3(Date data){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		String milisegundos = ""+c.get(Calendar.MILLISECOND);
		
		return sdf.format(data)+ "."+ milisegundos.charAt(0);
		
	}
	
	/**
	 *   Encontra o pr�ximo n�mero de controle para a exporta��o para o cat�logo coletivo da FGV,
	 *   entre os n�meros carregados no sistema.
	 *   <p>Obs.: Se n�o existirem mais n�meros livres retorna nulo</p>
	 */
	public static Integer encontraProximoNumeroDisponivelExportacaoFGV(List<ArquivoDeCargaNumeroControleFGV> arquivosCarregados){
		
		// j� est� ordenado pela data ent�o pega o primeiro que encontrar no arquivo,
		// se n�o tiver mais nenhum livre, pega do pr�ximo arquivo
		for (ArquivoDeCargaNumeroControleFGV arquivo : arquivosCarregados) {
			
			if(arquivo.possuiNumeroLivre())
				return arquivo.nextNumeroLivre();
			
		}
		
		return null;
	}
	
	
	
	/**
	 *    Gera os campos 040 dos registros que ser�o exportados para o FGV.
	 */
	public static void geraDadosCampo005FGVTitulo(MarcFactory marcFactory, Record record, String tag, int idTitulo) throws DAOException{
		
		TituloCatalograficoDao dao = null;
		
		try{
		
			dao = DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
			
			Date dataUltimaAlteracao = dao.findUltimaDataAlteracaoTitulo(idTitulo);
			
			ControlField c005 = marcFactory.newControlField(tag,
				geraDataHoraNoFormatoANSIX3(dataUltimaAlteracao));
			
			@SuppressWarnings("unchecked")
			List <Object> cf = record.getControlFields();

			cf.add(c005);
		
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/**
	 *    Gera os campos 040 dos registros que ser�o exportados para a FGV.
	 */
	public static void geraDadosCampo005FGVAutoridade(MarcFactory marcFactory, Record record, String tag, int idAutoridade) throws DAOException{
		
		AutoridadeDao dao = null;
		
		try{
		
			dao = DAOFactory.getInstance().getDAO(AutoridadeDao.class);
			
			Date dataUltimaAlteracao = dao.findUltimaDataAlteracaoAutoridade(idAutoridade);
			
			ControlField c005 = marcFactory.newControlField(tag,
				geraDataHoraNoFormatoANSIX3(dataUltimaAlteracao));
		
			@SuppressWarnings("unchecked")
			List <Object> cf = record.getControlFields();
			cf.add(c005);
		
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/**
	 *    Gera os campos 040 dos registros que ser�o exportados para a FGV.
	 */
	public static void geraDadosCampo040FGV(MarcFactory marcFactory, Record record, String tag){
		
		DataField c040 = marcFactory.newDataField(tag, ' ', ' ');
		
		String codigoInstituicao = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_INSTITUICAO_CATALOGACAO);
		String idioma = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.IDIOMA_CATALOGACAO);
		
		Subfield sfa = marcFactory.newSubfield(SubCampo.SUB_CAMPO_A, codigoInstituicao);
		Subfield sfb = marcFactory.newSubfield(SubCampo.SUB_CAMPO_B, idioma);
		
		c040.addSubfield(sfa);
		c040.addSubfield(sfb);
		
		@SuppressWarnings("unchecked")
		List <Object> df = record.getDataFields();
		df.add(c040);
	}
	
	/** Cria apenas uns dos subcampos do campo 040, caso o T�tulo n�o possua. */
	public static void geraSubCamposDadosCampo040FGV(MarcFactory marcFactory, Record record, String tag, boolean possuiSubCampoACampo040, boolean possuiSubCampoBCampo040) {
		
		VariableField campo040 =  record.getVariableField(tag);
		
		if(!possuiSubCampoACampo040){
			Subfield sfa = marcFactory.newSubfield(SubCampo.SUB_CAMPO_A, ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_INSTITUICAO_CATALOGACAO));
			((DataField)campo040).addSubfield(sfa);
		}
		
		if(!possuiSubCampoBCampo040){
			Subfield sfa = marcFactory.newSubfield(SubCampo.SUB_CAMPO_B, ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.IDIOMA_CATALOGACAO));
			((DataField)campo040).addSubfield(sfa);
		}	
	}
	
	/**
	 * M�todo que gera os dados MARC (Campos Bibliogr�ficos), onde esses devem ser enviados nos arquivos exportados para a FGV.
	 * 
	 * <p>Coloca o pr�ximo n�mero de controle carregado no sistema no campo 001.
	 * e criar os dado dos campos 997, 998, 999</p>
	 * 
	 * Gera os campos de dados do cat�logo da FGV.
	 */
	public static void geraDadosFGVCamposBibliograficos(MarcFactory marcFactory, Record record,
			String nomeArquivoFGV, String nomeUsuarioLogado, int proximoNumeroDisponivelExportacaoFGV
			, String codigosBibliotecaCooperantes, short tipoExportacao) {
		
		// Campo 001
		String codigoDaBiblioteca = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_DA_BIBLIOTECA_CATALOGO_COLETIVO);
		
		// COMPLETA COM "0" � esquerda os n�mero de controle at� atingir o tamanho que deve ir no arquivo.
		String dadoCampo001 = new Integer(proximoNumeroDisponivelExportacaoFGV).toString();
		for (int i = dadoCampo001.toString().length();  i < ArquivoDeCargaNumeroControleFGV.TAMANHO_NUMERO_CONTROLE_FGV ; i++) {
			dadoCampo001 = "0"+dadoCampo001;
		}
		
		String codigoCampo001 = "";
		if(tipoExportacao == CooperacaoTecnicaExportacaoMBean.OPERACAO_BIBLIOGRAFICA) // N� controle  = RN000000001
			codigoCampo001 = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_NUMERO_CONTROLE_BIBLIOGRAFICO);
		if(tipoExportacao == CooperacaoTecnicaExportacaoMBean.OPERACAO_AUTORIDADE)    // N� controle  = YY000000001
			codigoCampo001 = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_NUMERO_CONTROLE_AUTORIDADES);
		
		String digitoVerificador = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV(codigoCampo001+dadoCampo001);
		
		// Se j� tiver um campo de controle 001, remover o anterior antes de incluir o novo, n�o funcionou apenas atualizar o seu valor //
		ControlField cf001 = (ControlField) record.getVariableField("001");
		
		if( cf001 != null){
			@SuppressWarnings("unchecked")
			List <Object> cfs = record.getControlFields();
			cfs.remove(cf001);
		}
			
		cf001 = marcFactory.newControlField(Etiqueta.CAMPO_001_BIBLIOGRAFICO.getTag(), codigoCampo001+dadoCampo001+digitoVerificador);
		
		@SuppressWarnings("unchecked")
		List <Object> cfs = record.getControlFields();
		cfs.add(cf001);
		
		
		// Campo 997
		DataField df1 = marcFactory.newDataField(Etiqueta.CODIGO_DA_BIBLIOTECA.getTag(), ' ', ' ');
		df1.addSubfield( marcFactory.newSubfield(SubCampo.SUB_CAMPO_A, codigoDaBiblioteca) );
		
		@SuppressWarnings("unchecked")
		List <Object> dfs = record.getDataFields();
		dfs.add(df1);
		
		// Campo 998
		// gera o hist�rico, pega o que j� estava no campo 998 e adiciona "RN", o campo 998 deve ser guardado no sistema durante a importa��o.
		DataField df2 = marcFactory.newDataField(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES.getTag(), ' ', ' ');
		df2.addSubfield( marcFactory.newSubfield(SubCampo.SUB_CAMPO_A, codigoDaBiblioteca+" "+codigosBibliotecaCooperantes) );
		
		@SuppressWarnings("unchecked")
		List <Object> dfs2 = record.getDataFields();
		dfs2.add(df2);
		
		// Campo 999
		DataField df3 = marcFactory.newDataField(Etiqueta.INFORMACOES_DE_MOVIMENTO.getTag(), ' ', ' ');
		df3.addSubfield( marcFactory.newSubfield(SubCampo.SUB_CAMPO_A,
				ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.VERSAO_DO_PROGRAMA_REGISTRO_ALTERADO)) );
		
		//  Data da vers�o do programa de aplica��o onde o registro foi editado.
		df3.addSubfield( marcFactory.newSubfield(SubCampo.SUB_CAMPO_B, new SimpleDateFormat("dd/MM/yyyy").format(new Date()) ) );
		// Data de gera��o do registro de movimento
		df3.addSubfield( marcFactory.newSubfield(SubCampo.SUB_CAMPO_C, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) ) );
		
		// Nome do arquivo de movimento onde o registro se encontra
		if(tipoExportacao ==  CooperacaoTecnicaExportacaoMBean.OPERACAO_BIBLIOGRAFICA)
			df3.addSubfield( marcFactory.newSubfield(SubCampo.SUB_CAMPO_D, nomeArquivoFGV+".CC") );
		
		if(tipoExportacao ==  CooperacaoTecnicaExportacaoMBean.OPERACAO_AUTORIDADE)
			df3.addSubfield( marcFactory.newSubfield(SubCampo.SUB_CAMPO_D, nomeArquivoFGV+".AC") );
			
		
		// Nome do usu�rio que editou o registro de movimento.
		df3.addSubfield( marcFactory.newSubfield(SubCampo.SUB_CAMPO_E, nomeUsuarioLogado) );
		
		@SuppressWarnings("unchecked")
		List <Object> dfs3 = record.getDataFields();
		dfs3.add(df3);
	}
	
	
	
	
	
	
	
	/**
	 *    M�todo que analisa a frase que o usu�rio digitou e separa em palavras para realizar a busca.<br/>
	 * 
	 *    Se o usu�rio digitou alguma parte da frase entre aspas duplas "xxxxx xxxxx", essa parte vai
	 *    ser retornada como uma s�, para o sistema buscar toda a senten�a. <br/>
	 * 
	 *    <p>OBS.: S� retorna palavras maiores que 2 letras. Para n�o buscar por conjun��es. ex.: "de" ou "a" </p>
	 * 
	 *       <p> Ex.: <strong>"super interessante"</strong>. O sistema deve buscar por <strong>"super interessante"</strong>
	 *    em vez de <strong>"super"</strong>  AND <strong>"interessante"</strong> </p>
	 * 
	 */
	public static String[] retornaPalavrasBusca(String informacaoDigitada){

		List<String> retorno = new ArrayList<String>();

		if(StringUtils.notEmpty(informacaoDigitada)){

			StringBuilder textoTemp = new StringBuilder();

			boolean abriuAspas = false;

			for (int posicaoCaracter = 0; posicaoCaracter < informacaoDigitada.length(); posicaoCaracter++) {

				if(informacaoDigitada.charAt(posicaoCaracter) != ' ' && informacaoDigitada.charAt(posicaoCaracter) != '"'){
					textoTemp.append(informacaoDigitada.charAt(posicaoCaracter));
				}else{

					if(informacaoDigitada.charAt(posicaoCaracter) == ' ' && abriuAspas == false){  // finalizou uma palavra
						if(textoTemp.length() > 2){
							retorno.add( retiraPontuacaoCamposBuscas(textoTemp.toString()) );
						}
						textoTemp = new StringBuilder();
					}else{

						if( informacaoDigitada.charAt(posicaoCaracter) == '"' && abriuAspas == false){
							abriuAspas = true;
						}else{
							if( informacaoDigitada.charAt(posicaoCaracter) == '"' && abriuAspas == true){
								abriuAspas = false;
								if(textoTemp.length() > 2){
									retorno.add( retiraPontuacaoCamposBuscas(textoTemp.toString()));
								}
								textoTemp = new StringBuilder();
							}else{
								textoTemp.append(informacaoDigitada.charAt(posicaoCaracter));
							}
						}

					}


				}

			}  // Usu�rio abriu aspas e n�o fechou

			// Se o usu�rio n�o fechou as aspas separa o que estava dentro.
			if(abriuAspas == true){
				retorno.addAll(   Arrays.asList(retornaPalavrasBusca(textoTemp.toString()))   );
			}else{
				if(textoTemp.length() > 2){
					retorno.add( retiraPontuacaoCamposBuscas(textoTemp.toString()));
				}
			}

		}

		return retorno.toArray(new String[0]);
	}

	
	
	/**
	 *      M�todo que obt�m as bibliotecas da permiss�o do usu�rio.
	 */
	public static List<Biblioteca> obtemBibliotecasPermisaoUsuario(Usuario usuarioLogado, Papel papelUsuario) throws DAOException{
		
		PermissaoDAO permissaoDao = null;
		BibliotecaDao bibliotecaDao = null;
		
		List<Biblioteca> bibliotecas = new ArrayList<Biblioteca>();
		
		try{

			permissaoDao =  DAOFactory.getInstance().getDAO(PermissaoDAO.class);
			bibliotecaDao =  DAOFactory.getInstance().getDAO(BibliotecaDao.class);
			
			List<Permissao> permissoes = permissaoDao.findPermissoes(usuarioLogado, papelUsuario);
			
			for (Permissao p : permissoes) {
				Biblioteca biblioteca = bibliotecaDao.findBibliotecaByUnidade(p.getUnidadePapel());
				
				if(biblioteca != null) // caso o usu�rio tenha dado permiss�o na unidade errada
					bibliotecas.add(biblioteca);
			}
		
			return bibliotecas;
			
		}finally{
			if( bibliotecaDao != null ) bibliotecaDao.close();
			if( permissaoDao != null ) permissaoDao.close();
		}
	}

	/**
	 *     <p> M�todo que obt�m as bibliotecas da permiss�o do usu�rio.</p>
	 *     <p> Utilizando quando se necessita apenas dos ids das biblioteca para otimizar a busca</p>
	 */
	public static List<Integer> obtemIdsBibliotecasPermisaoUsuario(Usuario usuarioLogado, Papel... papelUsuario) throws DAOException{
		
		PermissaoDAO permissaoDao = null;
		BibliotecaDao bibliotecaDao = null;
		
		List<Integer> idsBibliotecas = new ArrayList<Integer>();
		
		try{

			permissaoDao =  DAOFactory.getInstance().getDAO(PermissaoDAO.class);
			bibliotecaDao =  DAOFactory.getInstance().getDAO(BibliotecaDao.class);
			
			List<Permissao> permissoes = permissaoDao.findPermissoesByPapeis(usuarioLogado, papelUsuario);
			
			for (Permissao p : permissoes) {
				idsBibliotecas.add(bibliotecaDao.findIdBibliotecaByUnidade(p.getUnidadePapel()));
			}
		
			return idsBibliotecas;
			
		}finally{
			if( bibliotecaDao != null ) bibliotecaDao.close();
			if( permissaoDao != null ) permissaoDao.close();
		}
	}

	/**
	 *     <p> M�todo que obt�m as bibliotecas da permiss�o do usu�rio.</p>
	 *     <p> Utilizando quando se necessita apenas dos ids das biblioteca para otimizar a busca</p>
	 */
	public static List<InventarioAcervoBiblioteca> obtemInventariosPermissaoUsuario(Usuario usuarioLogado, Papel... papelUsuario) throws DAOException{
		
		PermissaoDAO permissaoDao = null;
		InventarioAcervoBibliotecaDao inventarioAcervoBibliotecaDao = null;
		
		List<InventarioAcervoBiblioteca> inventarios = new ArrayList<InventarioAcervoBiblioteca>();
		
		try{

			permissaoDao =  DAOFactory.getInstance().getDAO(PermissaoDAO.class);
			inventarioAcervoBibliotecaDao =  DAOFactory.getInstance().getDAO(InventarioAcervoBibliotecaDao.class);
			
			List<Permissao> permissoes = permissaoDao.findPermissoesByPapeis(usuarioLogado, papelUsuario);
			InventarioAcervoBiblioteca inventario = null;
			
			for (Permissao p : permissoes) {
				inventario = inventarioAcervoBibliotecaDao.findInventarioAtivoByUnidadeBiblioteca(p.getUnidadePapel());
				
				if (inventario != null) {
					inventarios.add(inventario);
				}
			}
		
			return inventarios;			
		}finally{
			if( inventarioAcervoBibliotecaDao != null ) inventarioAcervoBibliotecaDao.close();
			if( permissaoDao != null ) permissaoDao.close();
		}
	}
	
	
	
	/**
	 * M�todo que recupera as unidades onde o usu�rio tem permiss�o para os papeis passado.
	 * 
	 */
	public static List<Integer> encontraUnidadesPermissaoDoUsuario(UsuarioGeral usuarioLogado, Integer... idsPapel) {
		
		List<Integer> idsPapelList = Arrays.asList(idsPapel); // Converte de array para lista
		
		Collection<Papel> papeisDoUsuario =  usuarioLogado.getPapeis();
		
		PermissaoDAO permissaoDao = null;
		
		List<Integer> idUnidades = new ArrayList<Integer>(); // unidades onde o usu�rio tem papel de bibliotec�rio cataloga��o.
		
		try {
			permissaoDao =  DAOFactory.getInstance().getDAO(PermissaoDAO.class);
			
			if(papeisDoUsuario != null)
			for (Papel papel : papeisDoUsuario) {
				if(idsPapelList != null && idsPapelList.contains(papel.getId()) ){ // Para todos os papeis passados
					
					List<Permissao> permissoes = permissaoDao.findPermissoes( usuarioLogado, papel); // Encontra as permissoes do papel
					
					if(permissoes != null)
					for (Permissao permissao : permissoes) {
						if(permissao.getUnidadePapel() != null){ // Se o papel possui uma unidade, esse m�todo s� faz sentido para os papeis que possuem unidade!
							idUnidades.add( permissao.getUnidadePapel().getId()); // Adiciona a unidade da permiss�o
						}
					}
				}
			}
		
		} finally {
			if(permissaoDao != null) permissaoDao.close();
		}
		
		return idUnidades;
	}
	
	/**
	 * Gera o pr�ximo car�ter do c�digo de barras de um anexo A, B, C,..., at� Z.
	 * � usado ao gerar o c�digo de barras dos anexos de
	 * exemplares e fasc�culos.
	 */
	public static String geraCaraterCorespondente(int valorAtual) throws NegocioException{

		valorAtual += LETRA_A_ASCII;

		if(valorAtual < LETRA_Z_ASCII)
			return String.valueOf( (char) valorAtual );

		throw new NegocioException("O n�mero m�ximo de anexos desse exemplar foi atingido");
	}


	/**
	 * Gera o valor inteiro correspondente ao caracter A, B, C, ... Z dos c�digos de barras dos anexos e suplementos.
	 * Onde:  A = 0
	 *        B = 1
	 *        C = 2
	 *        etc...
	 * 
	 * Essa fun��o � oposta � fun��o <code>BibliotecaUtil#geraCaraterCorespondente(int)</code>
	 */
	public static int geraValorCorespondente(char caraterAtual){
		return (int) ( caraterAtual - LETRA_A_ASCII);
	}

	
	
	/**
	 * M�todo que pega a quantidade de vezes que um empr�stimo foi renovado.
	 */
	public static int getQuantidadeRenovacoesEmprestimo(Emprestimo e) throws DAOException{

		ProrrogacaoEmprestimoDao dao = null;

		try{

			dao = DAOFactory.getInstance().getDAO(ProrrogacaoEmprestimoDao.class);

			return dao.countProrrogacoesPorRenovacaoDoEmprestimo(e);
		}finally{
			if(dao != null ) dao.close();
		}

	}

//	/**
//	 *     M�todo que retorna a pol�tica de empr�stimo espec�fica para a situa��o do empr�stimo.
//	 *     (tipo de usu�rio, status do material, etc..)
//	 * 
//	 *     Caso a biblioteca seja uma setorial e n�o tenha um pol�tica cadastrada para ela,
//	 *  pega automaticamente a da Central.
//	 */
//	public static PoliticaEmprestimo getPoliticaEmprestimoBiblioteca(Biblioteca biblioteca, Integer tipoUsuario,
//			TipoEmprestimo tipoEmprestimo, StatusMaterialInformacional  status) throws DAOException{
//
//		PoliticaEmprestimoDao dao = DAOFactory.getInstance().getDAO(PoliticaEmprestimoDao.class);
//
//		try{
//
//			if( biblioteca.isBibliotecaCentral())
//				return dao.findPoliticaEmpretimoAtivaByBibliotecaTipoUsuarioTipoEmprestimoStatusMaterial(biblioteca, tipoUsuario, tipoEmprestimo, status);
//
//			PoliticaEmprestimo politica = dao.findPoliticaEmpretimoAtivaByBibliotecaTipoUsuarioTipoEmprestimoStatusMaterial
//			(biblioteca, tipoUsuario, tipoEmprestimo, status);
//
//			if(politica == null)  // biblioteca setorial n�o tem essa pol�tica, retorna a da central
//
//				return dao.findPoliticaEmpretimoAtivaByBibliotecaTipoUsuarioTipoEmprestimoStatusMaterial
//				(new Biblioteca(ParametroHelper.getInstance().getParametroInt("BIBLIOTECA_CENTRAL")), tipoUsuario, tipoEmprestimo, status);
//
//			return politica;
//
//		}finally{
//			if(dao != null) dao.close();
//		}
//
//	}

	/**
	 *    M�todo que monta a ajuda de um campo MARC  de controle, bibliogr�fico
	 * ou de autoridades com base na Etiqueta.
	 *     Essa ajuda montada � um HTML j� no formato de ser exibido na p�gina.
	 */
	public static String  montaAjudaCamposControle(Etiqueta e, FormatoMaterial formato, String codigoCategoriaMaterial) throws DAOException{

		StringBuilder retorno = new StringBuilder();

		DescritoresDao descritotesDao = null;
		EtiquetaDao etiquetaDao = null;

		try{
			descritotesDao = DAOFactory.getInstance().getDAO(DescritoresDao.class);
			etiquetaDao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);

			e = etiquetaDao.findEtiquetaPorTagETipoAtiva(e.getTag(), e.getTipo());

			retorno.append( "<strong><h3 style=\"text-align:center\">"+e.getTag()+" - "+e.getDescricao()+"("+ (e.isRepetivel() ? "R" : "NR") +")"+" </h3></strong> <br/>");

			retorno.append( StringUtils.notEmpty(e.getInfo())? e.getInfo(): "");

			if(e.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO)){
				List<DescritorCampoControle> descritores = descritotesDao.findDescritoresCampoControleNaoDependemFormatoMaterial(e.getId());
				montaAjudaDescritoresCamposControle(retorno, descritores);
			}

			if(e.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO)){
				List<DescritorCampoControle> descritores = descritotesDao.findDescritoresCampoControleDependemFormatoMaterial(e.getId(), formato.getId());
				montaAjudaDescritoresCamposControle(retorno, descritores);
			}

			if(e.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO)){
				List<DescritorCampoControle> descritores =  descritotesDao.findDescritoresCampoControleDependemCategoriaMaterial(e.getId(), codigoCategoriaMaterial);
				montaAjudaDescritoresCamposControle(retorno, descritores);
			}

			if(e.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO)){
				List<DescritorCampoControle> descritores = descritotesDao.findDescritoresCampoControleDependemFormatoMaterial(e.getId(), formato.getId());
				montaAjudaDescritoresCamposControle(retorno, descritores);
			}

			if(e.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE)){
				List<DescritorCampoControle> descritores = descritotesDao.findDescritoresCampoControleNaoDependemFormatoMaterial(e.getId());
				montaAjudaDescritoresCamposControle(retorno, descritores);
			}

			if(e.equals(Etiqueta.CAMPO_008_AUTORIDADE)){
				List<DescritorCampoControle> descritores = descritotesDao.findDescritoresCampoControleNaoDependemFormatoMaterial(e.getId());
				montaAjudaDescritoresCamposControle(retorno, descritores);
			}

		}finally{
			if(descritotesDao != null)
				descritotesDao.close();
			if(etiquetaDao != null)
				etiquetaDao.close();

		}

		return retorno.toString();
	}


	/**
	 * Monta Ajuda para descritores referente aos campos controle.
	 * Para todos os campos de controle depois que obt�m os descri��es os passos para montar
	 * a resposta s�o os mesmos.
	 */
	private static void montaAjudaDescritoresCamposControle(StringBuilder retorno, List<DescritorCampoControle> descritores){

		Collections.sort(descritores, new DescritorCampoControleByPosicaoInicialComparator());

		retorno.append("<p style=\"margin-top: 10px; margin-bottom: 10px;\"><strong>Posi��o dos Caracteres:</strong> </p>");

		for (DescritorCampoControle descritorCampoControle : descritores) {

			if(descritorCampoControle.getPosicaoInicio() == descritorCampoControle.getPosicaoFim())
				retorno.append("<p> <strong>"+descritorCampoControle.getPosicaoInicio()+" - "+descritorCampoControle.getDescricao()+" </strong> </p>");
			else
				retorno.append("<p> <strong>"+descritorCampoControle.getPosicaoInicio()+" - "+descritorCampoControle.getPosicaoFim()+" - "+descritorCampoControle.getDescricao()+" </strong> </p>");


			retorno.append("<p style=\"font-style: italic; margin-left: 20px; \">"+descritorCampoControle.getInfo()+"</p>");

			List<ValorDescritorCampoControle> valores =  descritorCampoControle.getValoresDescritorCampoControle();

			Collections.sort(valores, new ValorDescritorCampoControleByValorComparator());

			for (ValorDescritorCampoControle valorDescritorCompoControle : valores) {
				retorno.append("<p style=\"margin-left: 40px; \"> <strong>"+valorDescritorCompoControle.getValor()+" - "+valorDescritorCompoControle.getDescricao()+" </strong> </p>");
				retorno.append("<p style=\"font-style: italic; margin-left: 60px;\">"+( StringUtils.notEmpty(valorDescritorCompoControle.getInfo()) ? valorDescritorCompoControle.getInfo() : "")+"</p>");
			}

		}

	}

	
	

	/**
	 *  Retorna as informa��es b�sicas de um material e retorna essas informa��es j� formatadas para exibi��o
	 *  para o usu�rio. <br/><br/>
	 * 
	 *  Formata as informa��es de um material no formato:  <i>"codidoBarra - Autor T�tulo - Biblioteca"  </i><br/>
	 *
	 * @param idMaterialInformacional  o id do material
	 */
	public static String obtemDadosMaterialInformacional(int idMaterialInformacional ) throws DAOException{
		
		MaterialInformacionalDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(MaterialInformacionalDao.class);
		
		
			Object[] infoMaterial = (Object[] ) dao.findInformacoesDoMaterial(idMaterialInformacional);
			
			return  (String)infoMaterial[0]+" - "+ ((String)infoMaterial[2]!= null ? (String)infoMaterial[2] : " ")
			+" "+( (String)infoMaterial[1] != null ? (String)infoMaterial[1] : " ")
			+" - "+(String)infoMaterial[3];
		
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	/**
	 *  Retorna as informa��es b�sicas de um T�tulo e retorna essas informa��es j� formatadas para exibi��o
	 *  para o usu�rio. <br/><br/>
	 * 
	 *  Formata as informa��es de um material no formato:  <i>"n�mero do sistema - Autor T�tulo Ano Edicao LocalPublicacao e Editora"</i><br/>
	 *
	 * @param idMaterialInformacional  o id do material
	 */
	public static String obtemDadosResumidosTituloDoMaterial(int idMaterialInformacional ) throws DAOException{
		
		TituloCatalograficoDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
		
			
			Object[] infoMaterial = (Object[] ) dao.findInformacoesResumidasDoTituloDoMaterial(idMaterialInformacional);
			
			StringBuilder dadosResumidoTitulo = new StringBuilder(infoMaterial[0]+" - "+ ((String)infoMaterial[1]!= null ? (String)infoMaterial[1] : " ")
					+" "+((String)infoMaterial[2] != null ? (String)infoMaterial[2] : " "));
			
			CacheEntidadesMarc cache = new CacheEntidadesMarc();
			cache.setAno( (String) infoMaterial[3]);
			cache.setEdicao( (String) infoMaterial[4]);
			cache.setLocalPublicacao( (String) infoMaterial[5]);
			cache.setEditora( (String) infoMaterial[6]);
			
			for (String ano : cache.getAnosFormatados()) {
				dadosResumidoTitulo.append(" "+ano+" ");
			} 
			
			for (String edicao : cache.getEdicoesFormatadas()) {
				dadosResumidoTitulo.append(" "+edicao+" ");
			} 
		
			for (String local : cache.getLocaisPublicacaoFormatados()) {
				dadosResumidoTitulo.append(" "+local+" ");
			} 
			
			for (String editora : cache.getEditorasFormatadas()) {
				dadosResumidoTitulo.append(" "+editora+" ");
			} 
			
			return  dadosResumidoTitulo.toString();
		
		}finally{
			if(dao != null) dao.close();
		}
		
	}

	
	/**
	 *  Retorna as informa��es b�sicas de um T�tulo do cache e retorna essas informa��es j� formatadas para exibi��o
	 *  para o usu�rio. <br/><br/>
	 * 
	 *  Formata as informa��es de um t�tulo no formato:  <i>"n�mero do sistema - Autor T�tulo ano edi��o LocalPublicacao e Editora"</i><br/>
	 *
	 * @param idMaterialInformacional  o id do material
	 */
	public static String obtemDadosResumidosTitulo(int idTitulo ) throws DAOException{
		
		TituloCatalograficoDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
		
			
			Object[] infoMaterial = (Object[] ) dao.findInformacoesResumidasDoTitulo(idTitulo);
			
			CacheEntidadesMarc cache = new CacheEntidadesMarc();
			cache.setAno( (String) infoMaterial[3]);
			cache.setEdicao( (String) infoMaterial[4]);
			cache.setLocalPublicacao( (String) infoMaterial[5]);
			cache.setEditora( (String) infoMaterial[6]);
			
			StringBuilder dadosResumidoTitulo = new StringBuilder(infoMaterial[0]+" - "
					+ ((String)infoMaterial[1]!= null ? (String)infoMaterial[1] : " ")
					+" "+((String)infoMaterial[2] != null ? (String)infoMaterial[2] : " ") );
			
			
			for (String ano : cache.getAnosFormatados()) {
				dadosResumidoTitulo.append(" "+ano+" ");
			} 
			
			for (String edicao : cache.getEdicoesFormatadas()) {
				dadosResumidoTitulo.append(" "+edicao+" ");
			} 
			
			for (String local : cache.getLocaisPublicacaoFormatados()) {
				dadosResumidoTitulo.append(" "+local+" ");
			} 
			
			for (String editora : cache.getEditorasFormatadas()) {
				dadosResumidoTitulo.append(" "+editora+" ");
			} 
			
			return dadosResumidoTitulo.toString();
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	

	/**
	 *  Retorna as informa��es b�sicas de um T�tulo e retorna essas informa��es j� formatadas para exibi��o
	 *  para o usu�rio. <br/><br/>
	 * 
	 *  Formata as informa��es de um material no formato:  <i>"n�mero do sistema - Autor T�tulo Ano Edicao LocalPublicacao e Editora"</i><br/>
	 *
	 * @param idMaterialInformacional  o id do material
	 */
	public static CacheEntidadesMarc obtemDadosTituloDoMaterial(int idMaterialInformacional ) throws DAOException{
		
		TituloCatalograficoDao dao = null;
		CacheEntidadesMarc cache = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
		
			
			Integer idTitulo =  dao.findIdTituloDoMaterial(idMaterialInformacional);
			
			cache = dao.findByExactField(CacheEntidadesMarc.class, "idTituloCatalografico", idTitulo, true);
		
			return cache;
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	/**
	 *    M�todo respons�vel por pegar o objeto cache correspondente ao t�tulo passado.
	 */
	public static CacheEntidadesMarc obtemDadosTituloCache(int idTitulo) throws DAOException{

		GenericDAO dao = null;
		CacheEntidadesMarc cache = null;

		try{
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			cache = dao.findByExactField(CacheEntidadesMarc.class, "idTituloCatalografico", idTitulo, true);
		}finally{
			if(dao != null) dao.close();
		}

		return cache;
	}




	/**
	 *    M�todo respons�vel por pegar o objeto cache correspondente ao artigo passado.
	 */
	public static CacheEntidadesMarc obtemDadosArtigoCache(int idArtigo) throws DAOException{

		GenericDAO dao = null;
		CacheEntidadesMarc cache = null;

		try{
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			cache = dao.findByExactField(CacheEntidadesMarc.class, "idArtigoDePeriodico", idArtigo, true);
		}finally{
			if(dao != null) dao.close();
		}

		return cache;
	}

	/**
	 * Retorna a autoridade em cache da autoridade passada
	 */
	public static CacheEntidadesMarc obtemDadosAutoridadeEmCache(int idAutoridade) throws DAOException{

		GenericDAO dao = null;

		try{
			dao =  DAOFactory.getGeneric(Sistema.SIGAA);
			return dao.findByExactField(CacheEntidadesMarc.class, "idAutoridade", idAutoridade, true);

		}finally{
			if(dao != null) dao.close();
		}
	}

	


	/**
	 *    M�todo que serve para decrementar a quantidade de materiais de um t�tulo que est� no
	 * objeto cache.
	 * 
	 *    Esse m�todo deve ser chamado sempre que um exemplar ou um fasc�culo for dado baixa no acervo
	 */
	public static void decrementaQuantidadesMateriaisTitulo(GenericDAO dao, int idTituloCatalografico)
			throws DAOException{
		CacheEntidadesMarc cache = obtemDadosTituloCache(idTituloCatalografico);
		if (cache != null && cache.getQuantidadeMateriaisAtivosTitulo() > 0) {
			int quantidadeMateriais = cache.getQuantidadeMateriaisAtivosTitulo() - 1;
			dao.updateField(CacheEntidadesMarc.class, cache.getId(), "quantidadeMateriaisAtivosTitulo", quantidadeMateriais);
		}
	}

	/**
	 *    M�todo que serve para incrementar a quantidade de materiais de um t�tulo que est� no
	 * objeto cache. Esse m�todo deve ser chamado sempre que um exemplar ou um fasc�culo baixado
	 * tenha a sua baixa cancelada.
	 */
	public static void incrementaQuantidadesMateriaisTitulo(GenericDAO dao, int idTituloCatalografico)throws DAOException{
		CacheEntidadesMarc cache = obtemDadosTituloCache(idTituloCatalografico);
		if (cache != null && cache.getQuantidadeMateriaisAtivosTitulo() >= 0) {
			int quantidadeMateriais = cache.getQuantidadeMateriaisAtivosTitulo() + 1;
			dao.updateField(CacheEntidadesMarc.class, cache.getId(), "quantidadeMateriaisAtivosTitulo", quantidadeMateriais);
		}
	}
	
	/**
	 * Verifica se a senha do SIGAA digitada pelo usu�rio est� correta
	 */
	public static boolean senhaSigaaCorreta(HttpServletRequest req, String senhaSigaa, Usuario usuarioLogado) throws ArqException {
		return UserAutenticacao.autenticaUsuario(req, usuarioLogado.getLogin(), senhaSigaa);
	}

	/**
	 * <p>
	 * Retorna a biblioteca setorial do usu�rio que presta o servi�o requerido, caso o usu�rio n�o
	 * tenha biblioteca setorial ou ela n�o realize o servi�o solicitado, retorna a biblioteca central do sistema.
     * </p><p>
	 * Usado para encontrar as bibliotecas quando o usu�rio vai requisitar algum servi�o da biblioteca.
	 * </p><p>
	 * <h1>Observa��es:</h1>
	 * <p></p>
	 * Geralmente � a unidade do discente do usu�rio que indica � qual biblioteca ele est� ligado. Quando � servidor,
	 * vai direto para a central.
	 * </p><p>
	 * Para determinados servi�os, as bibliotecas indicam diretamente que cursos (no caso de discentes)
	 * podem usufruir desses servi�os (atrav�s da rela��o <tt>AssociacaoCursoBiblioteca</tt>).
	 * </p>
	 * 
	 * @param discente
	 * @param servicoRequerido opera��o definida em <tt>ServicosBiblioteca.Servicos</tt>
	 */
	public static List <Biblioteca> getBibliotecasDoDiscenteByServico (Discente discente, TipoServicoInformacaoReferencia servicoRequerido) throws ArqException{

		BibliotecaDao dao = null;
		Set<Biblioteca> bibliotecas = new HashSet<Biblioteca> ();

		try {
			dao = DAOFactory.getInstance().getDAO(BibliotecaDao.class);

			/*Unidade unidade = null; Comentado por n�o estar sendo utilizado*/

			/*if ( discente != null ) Comentado por n�o estar sendo utilizado
				unidade = discente.getUnidade();*/

			// Quando um discente solicita normaliza��o ou levantamento bibliogr�fico
			if ( discente != null ) {
				Curso c = discente.getDiscente().getCurso();
				
				if(c != null){ // Alunos especiais n�o tem curso
					ArrayList<Biblioteca> l = new ArrayList<Biblioteca>( dao.findBibliotecasAssociadasAoCurso( c.getId() ) );
					
					// retira as bibliotecas que n�o fazem o servi�o requerido
					for ( Iterator<Biblioteca> it = l.iterator(); it.hasNext();  ) {
						Biblioteca b = it.next();
						ServicosInformacaoReferenciaBiblioteca serv =  dao.findByExactField(ServicosInformacaoReferenciaBiblioteca.class, "biblioteca.id", b.getId(), true);
						if ( ! serv.realizaServico(servicoRequerido) )
							it.remove();
					}
					
					bibliotecas.addAll( l );
				}
			} 

			// A Biblioteca Central � usada se n�o houver uma biblioteca espec�fica para o usu�rio
			// e se a biblioteca central oferecer o servi�o.
			if ( bibliotecas.isEmpty() ) {
				Biblioteca central = BibliotecaUtil.getBibliotecaCentral();
				ServicosInformacaoReferenciaBiblioteca serv = dao.findByExactField(ServicosInformacaoReferenciaBiblioteca.class, "biblioteca.id", central.getId(), true);
				if ( serv != null && serv.isRealizaServico( servicoRequerido ) )
					bibliotecas.add( BibliotecaUtil.getBibliotecaCentral() );
			}

			return new ArrayList<Biblioteca>( bibliotecas );

		} finally {
			if (dao != null)
				dao.close();
		}
	}

//	/**
//	 * N�o usar busca recursiava, isso � perigoso demais
//	 * Busca recursivamente por todas as bibliotecas relacionadas � unidade passada.
//	 */
//	private static List <Biblioteca> buscarBibliotecas (
//			Unidade unidade, Servico operacaoRequerida, boolean buscarNosFilhos, BibliotecaDao dao)
//			throws DAOException{
//		
//		List <Biblioteca> bs = new ArrayList <Biblioteca> ();
//
//		boolean ok = false;
//
//		Biblioteca biblioteca = dao.findByExactField(Biblioteca.class, "unidade.id", unidade.getId(), true);
//
//		// Verifica se a biblioteca encontrada realiza o trabalho solicitado.
//		if (biblioteca != null) {
//			ServicosBiblioteca serv = dao.findByExactField(ServicosBiblioteca.class, "biblioteca.id", biblioteca.getId(), true);
//			ok = serv.realizaServico( operacaoRequerida );
//		}
//
//		if (buscarNosFilhos){
//			List <Unidade> filhos = (List<Unidade>) dao.findByExactField(Unidade.class, "unidadeResponsavel.id", unidade.getId());
//
//			for (Unidade f : filhos)
//				bs.addAll(buscarBibliotecas(f, operacaoRequerida,  true, dao));
//		} else if (unidade == unidade.getUnidadeResponsavel())
//			bs.addAll(buscarBibliotecas(unidade.getUnidadeResponsavel(), operacaoRequerida, false, dao));
//
//		if (biblioteca != null && ok)
//			bs.add(biblioteca);
//
//		return bs;
//	}


	/**
	 * Retorna a �ltima data de renova��o do empr�stimo passado.
	 */
	public static Date getDataUltimaRenovacao(Emprestimo e) throws DAOException {

		ProrrogacaoEmprestimoDao dao = null;

		try {
			dao = DAOFactory.getInstance().getDAO(ProrrogacaoEmprestimoDao.class);

			List<Date> datasRenovacao =  dao.getDatasRenovacoesEmprestimo(e);

			// Retorna apenas a �ltima data de renova��o.
			if (datasRenovacao != null && !datasRenovacao.isEmpty()){
				Collections.sort(datasRenovacao);
				return datasRenovacao.get(datasRenovacao.size() -1);
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}

		return null;
	}
	
	/**
	 * Retorna a biblioteca central do sistema.
	 */
	public static Biblioteca getBibliotecaCentral(String... projecao) throws DAOException {
		int id = BibliotecaUtil.getIdBibliotecaCentral();
		
		GenericDAO dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(BibliotecaDao.class);
			if(projecao != null && projecao.length > 0)
				return dao.findByPrimaryKey( id, Biblioteca.class, projecao );
			else
				return dao.findByPrimaryKey( id, Biblioteca.class);
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	/**
	 * Retorna a descri��o da biblioteca passada.
	 */
	public static String getDescricaoBiblioteca(int idBiblioteca) throws DAOException {
		
		BibliotecaDao dao = null;
		try{
			return DAOFactory.getInstance().getDAO(BibliotecaDao.class).findDescricaoBibliotecaInternaAtiva(idBiblioteca);
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	/**
	 * Retorna a descri��o da biblioteca passada.
	 */
	public static String getDescricaoColecao(int idColecao) throws DAOException {
		
		GenericDAO dao = null;
		try{
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			return dao.findByPrimaryKey(idColecao, Colecao.class, "descricao").getDescricao();
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	/**
	 * Retorna a descri��o da biblioteca passada.
	 */
	public static String getDescricaoTipoMaterial(int idTipoMaterial) throws DAOException {
		
		GenericDAO dao = null;
		try{
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			return dao.findByPrimaryKey(idTipoMaterial, TipoMaterial.class, "descricao").getDescricao();
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	/**
	 * Retorna a descri��o da biblioteca passada.
	 */
	public static String getDescricaoStatus(int idStatus) throws DAOException {
		
		GenericDAO dao = null;
		try{
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			return dao.findByPrimaryKey(idStatus, TipoMaterial.class, "descricao").getDescricao();
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	/**
	 * Retorna o id da biblioteca central do sistema.
	 */
	public static int getIdBibliotecaCentral() {
		return ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.BIBLIOTECA_CENTRAL);
	}
	
	
	
	/**
	 * M�todo que consulta no banco, j� tratando as quest�es de abertura e fechamento dos Daos, se 
	 * uma biblioteca realiza um determinado servi�o ou n�o.
	 *
	 * @param nomeServico
	 * @param idBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public static boolean bibliotecaRealizaServico(String nomeServico, int idBiblioteca) throws DAOException{
		
		BibliotecaDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(BibliotecaDao.class);
			return dao.bibliotecaRealizaServico(nomeServico, idBiblioteca);
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	

	
	/**
	 * <p>Gera o c�digo de autentica��o que vai ser impresso nos comprovantes de devolu��o dos empr�stimos para detectar poss�veis 
	 * tentativas de falsifica��es.</p>
	 *
	 * <p>Teoricamente n�o � poss�vel gerar um c�digo valido pois o usu�rio n�o vai saber o id do empr�stimo est� sendo devolvido.</p>
	 *
	 * <p>Para saber se um comprovante � aut�ntico, basta transformar de hexadecimal para base 10 que obteremos o id do empr�stimo e
	 * a hora que a opera��o foi realizada. Basta verificar se esses dados existem no banco.</p>
	 *
	 * @param id id do objeto representado pelo comprovante
	 * @param data  (pode ser data de devolu��o ou a data de renova��o ou a data do empr�stimos, depende do para que o comprovante ser� gerado)
	 * @return
	 */
	public static String geraNumeroAutenticacaoComprovantes(int id, Date data){
		
		SimpleDateFormat format = new SimpleDateFormat("HHmmssSSS");
		
		String dataDevolucaoFormatada = format.format(data);
		
		String codigoEmprestimo = new BigInteger(""+id).toString(16);
		String codigoData = new BigInteger(dataDevolucaoFormatada).toString(16);
		
		return (codigoEmprestimo+"."+codigoData).toUpperCase();
	}




	
	
}
