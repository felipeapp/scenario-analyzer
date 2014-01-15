/*
 * Copyright (c) 2011, GALILEU BATISTA.
 * 					   Brazilian Federal Police
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * This code uses the infrastructure of 3270 emulation developed by
 *       Paul Mates - X3270 suite.  
 *     * Neither the names of Galileu Batista, Paul Mattes nor the names of his 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GALILEU BATISTA "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL PAUL MATTES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package br.gov.dpf.dpf3270;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.dominio.RegistroSERPROConexao;
import br.ufrn.arq.dominio.RegistroSERPROSincronizacao;
import br.ufrn.arq.mainframe.AlterarSenhaMainframeException;
import br.ufrn.arq.mainframe.ErroProcessamentoMainFrameException;
import br.ufrn.arq.mainframe.ErroSenhaMainframeException;
import br.ufrn.arq.mainframe.MainFrameConnectionException;
import br.ufrn.arq.mainframe.OperacaoEmManutencaoSerproException;
import br.ufrn.arq.mainframe.TelaNaoEncontradaMainFrameException;
import br.ufrn.arq.parametrizacao.ParametroHelper;

public abstract class Generic3270 {

	Process 		p;
	Socket 			s;
	protected 		String hostName;
	String          s3270Name = "s3270";
	
	BufferedReader  ws3270In;
    PrintWriter     ws3270Out;
	String 			screen, command, statusLine1, statusLine2, crlf;
	int				timeout = 5;
	/** Registro de conexão associado para a operação com o terminal do SERPRO. */
	RegistroSERPROConexao registroSERPROConexao;
	/** Registro de sincronização associado para a operação com o terminal do SERPRO. */
	RegistroSERPROSincronizacao registroSERPROSincronizacao;
	/** Registro de entrada. */
	RegistroEntrada registroEntrada;
	
	public  boolean DEBUG = false;
	
	private static  String timeoutPrefix = "data: Wait: Timed out";
	
	/** Parâmetro de ativação do log em banco nas operações com o terminal do SERPRO. */
	private boolean ativarLog = ParametroHelper.getInstance().getParametroBoolean("3_1_70");

	public Generic3270 () {	
	}
	
	public Generic3270 (String hostname) {
		this.hostName   = hostname;
		this.crlf   	= "\r\n";
	}
	
	public Generic3270 (String hostname, String crlf) {
		this.hostName   = hostname;
		this.crlf   	= crlf;
	}

	public void setHostName (String hostname) {
		this.hostName   = hostname;
	}

    public void setBin3270Name(String s3270Name) {
		this.s3270Name = s3270Name;
	}

	public void connect(int line, int col, String textToWait) throws Exception {
		p = Runtime.getRuntime().exec(s3270Name);
		ws3270In  = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    ws3270Out = new PrintWriter(p.getOutputStream());
		command("connect("+hostName+")");
		waitOutput(line, col, textToWait);
	}
	
	protected abstract void login() throws Exception;
	
	public void disconnect() throws IOException {
		if(ws3270Out != null && ws3270In != null){
			command("disconnect()");
			p.destroy();
		}
	}
	
	protected void finalize() throws Throwable {
		disconnect();
		super.finalize();
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setField(int fieldNumber, String fieldValue) throws IOException {
		gotoField(fieldNumber);
		command("deleteField()");
		command("string(\""+fieldValue+"\")");
	}

	public void gotoFirstField() throws IOException {
		waitInput();
		command("home()");
	}

	public void gotoField(int fieldNumber) throws IOException {
		if (fieldNumber > 0) { 
			gotoFirstField();
			for (int i=1; i < fieldNumber; i++)
				command("tab()");
		} else if (fieldNumber < 0) { 
			gotoLastField();
			for (int i=fieldNumber; i < -1; i++)
				command("backtab()");
		} else
			throw new IOException("Campos são numerados 1 -> N (início para fim) ou -1 -> -N (fim para início)!!!!");

	}

	public void gotoLastField() throws IOException {
		gotoFirstField();
		command("backtab()");
	}

	public void setCurrentField(String fieldValue) throws IOException {
		waitInput();
		command("string(\""+fieldValue+"\")");
	}

	public void commandNoResult(String cmdValue) throws IOException {
		command = cmdValue;
		ws3270Out.write(cmdValue+"\r\n");
		ws3270Out.flush();		
	}
	
	public void command(String cmdValue) throws IOException {
		command = cmdValue;
		ws3270Out.write(cmdValue+"\r\n");
		ws3270Out.flush();		
		getCommandResult(true);
	}

	public void waitInput() throws IOException {
		commandNoResult("wait(1, InputField)");
		statusLine1 = ws3270In.readLine();
		if (timeoutPrefix.equals(statusLine1)) {
			statusLine1 = ws3270In.readLine();
		}
		ws3270In.readLine();
		statusLine2 = "ok";
		getCommandResult(false);
	}

	public void waitOutput(int line, int col, String text) throws Exception {
		waitOutput(new TextColumn[] { new TextColumn(line, col, text) } );
	}
	
	public int waitOutput(TextColumn columnsToCheck[]) throws Exception {
		int ntries = 10;
		// Divide o tempo total de timeout em 'ntries' tentativas
		int timeToRetry = (timeout + ntries - 1) / ntries; 
		int findIt = -1;
		
		if (columnsToCheck != null) {
			do {
				String columsInScreen[] = getColumns(columnsToCheck);
				for (int col = 0; col < columnsToCheck.length; col++)
					if  ((columnsToCheck[col] == null) || 
						(columnsToCheck[col].text == null) ||
						columnsToCheck[col].text.equalsIgnoreCase(columsInScreen[col])) {
						findIt = col;
					}				
				
				if (findIt == - 1) {
				   commandNoResult("wait("+timeToRetry+", Output)");
					
				   statusLine1 = ws3270In.readLine();
				   if (timeoutPrefix.equals(statusLine1)) {
					   statusLine1 = ws3270In.readLine();
				   }
				   ws3270In.readLine();
				   statusLine2 = "ok";
				   getCommandResult(false);
				}
				
			} while ((findIt == -1) && (--ntries > 0));
		}
		
		if (findIt == -1) 
			genException(columnsToCheck[0].line, columnsToCheck[0].col, columnsToCheck[0].text);
			
		return findIt;		
	}

	void genException(int line, int col, String textToWait) throws Exception {
		throw new TelaNaoEncontradaMainFrameException ("Palavra '"+textToWait+"' nao encontrada na tela SERPRO (linha:"+line+", coluna: "+col+"). Tela mudou? " + 
				 ((crlf != null) ? crlf : "") + 
				 "Linha 1: "+getLinesWithCRLF(1, 1)+
				 "Linha "+line+": "+getLinesWithCRLF(line, line)+
				 "Linha 24: "+getLinesWithCRLF(24, 24) +
				 ((crlf != null) ? crlf : "") +
				 "FAVOR ACESSAR O SISTEMA ESTRUTURANTE DIRETAMENTE ANTES DE CONTACTAR O SUPORTE!");
	}

	public static void genException(String wantedLabel, String foundStr) throws Exception {
		throw new TelaNaoEncontradaMainFrameException ("Label '"+wantedLabel+"' nao encontrado na tela SERPRO. Tela mudou? " + 
				 "Label Encontrado: "+foundStr+" "+
				 "FAVOR ACESSAR O SISTEMA ESTRUTURANTE DIRETAMENTE ANTES DE CONTACTAR O SUPORTE!");
	}
	
	public void enter(int line, int col, String textToWait) throws Exception {
		command("enter()");
		waitOutput(line, col, textToWait);
	}

	public int enter(TextColumn success, TextColumn fail) throws Exception {
		command("enter()");
		return waitOutput(new TextColumn[] {success, fail});
	}
	
	public void pf(int num, int line, int col, String textToWait) throws Exception {
		command("pf("+num+")");
		waitOutput(line, col, textToWait);
	}

	public int pf(int num, TextColumn success, TextColumn fail) throws Exception {
		command("pf("+num+")");
		return waitOutput(new TextColumn[] {success, fail});
	}

	public void  pa(int num, int line, int col, String textToWait) throws Exception {
		command("pa("+num+")");
		waitOutput(line, col, textToWait);
	}

	public int pa(int num, TextColumn success, TextColumn fail) throws Exception {
		command("pa("+num+")");
		return waitOutput(new TextColumn[] {success, fail});
	}

	public String getTextInPos(int line, int col) throws Exception {
		return getTextInPos(line, null, col);
	}

	public String getTextInPos(int line, String fieldName, int col) throws Exception {
		return getColumn(line, fieldName, col);
	}

	public String getTextInAbsPos(int line, int begCol, int endCol) throws Exception {
		return getTextInAbsPos(line, null, begCol, endCol);
	}

	public String getTextInAbsPos(int line, String fieldName, int begCol, int endCol) throws Exception {
		return getTextInAbsPos(getLines(line, line), fieldName, begCol, endCol);
	}

	public static String getTextInAbsPos(String telaSIAFI, int line, int begCol, int endCol) throws Exception {
		line--;
		return getTextInAbsPos(telaSIAFI.substring(line * 80, line * 80 + endCol), null, begCol, endCol);
	}	
	
	public static String getTextInAbsPos(String telaSIAFI, int line, String fieldName, int begCol, int endCol) throws Exception {
		line--;
		return getTextInAbsPos(telaSIAFI.substring(line * 80, line * 80 + endCol), fieldName, begCol, endCol);
	}	

	private static String getTextInAbsPos(String lineStr, String fieldName, int begCol, int endCol) throws Exception {
		String text;
		try {
			begCol--;
			compareLabels (fieldName, lineStr.substring(0, begCol));
			
			text = lineStr.substring(begCol, endCol);
		} catch (IndexOutOfBoundsException e) {
			text = "";
		}
		return text;
		
	}	
	
	private static void compareLabels (String wantedLabel, String foundLabel) throws Exception {
		if (wantedLabel != null) {
			String wantedLabels[] = wantedLabel.split(" +");
			String foundLabels[] = foundLabel.split(" +");

			if (foundLabels.length >= wantedLabels.length) {
				int indFound  = foundLabels.length - 1;
				int indWanted = wantedLabels.length - 1;
				while (indWanted >= 0) { 					
					if (wantedLabels[indWanted].equalsIgnoreCase(foundLabels[indFound])) {
						indWanted--; indFound--;
					}
					else 
						break;
				}
				
				if (indWanted < 0) return;
			}
			
	    	genException (wantedLabel, foundLabel);
		}
	}
	
	public Integer getNumberInPos(int line, String fielName, int col) throws NumberFormatException, Exception {
		String column = getColumn(line, col);
		if (column != null) {
			return Integer.parseInt(column, 10);
		}
		return null;
	}
	
	public Integer getNumberInPos(int line, int col) throws NumberFormatException, Exception {
		return getNumberInPos(line, null, col);
	}

	private String getColumn(int line, int col) throws Exception {
		return getColumn(line, null, col);
	}

	private String getColumn(int line, String fieldName, int col) throws Exception {
		String lineStr = getLines(line, line);
		return getColumns(lineStr, line, fieldName, col, col);
	}

	private String[] getColumns(TextColumn columnsToGet[]) throws Exception {		
		String columnFound[] = new String[columnsToGet.length];
		for (int col = 0; col < columnsToGet.length; col++)
			if ((columnsToGet[col]!= null) && columnsToGet[col].text != null)
				columnFound[col] = getColumn(columnsToGet[col].line, columnsToGet[col].col);
		return columnFound;
	}
	
	public String getColumns(String lineStr, int line, String fieldName, int colBeg, int colEnd) throws Exception {
		if (lineStr != null) {
			String colStr = "";
			lineStr = lineStr.trim();
			String columns[] = lineStr.split(" +");
			if (columns != null && columns.length >= colBeg) { 
				for (int col = colBeg - 1; (col < colEnd) && (col < columns.length); col++)
				   colStr += columns[col];
				
				String colLabel = "";  
				for (int col = 0; col < (colBeg - 1); col++)
					   colLabel += columns[col] + " ";

				compareLabels(fieldName, colLabel);
				
				return colStr;
			}
		}
		return null;
	}

	public String getScreenText() throws Exception {
		return getLinesWithCRLF (1, 24);
	}		

	public String getLines (int startLine, int endLine) throws Exception {
		return getLines (startLine, endLine, false);
	}
	
	public String getLinesWithCRLF (int startLine, int endLine) throws Exception {
		return getLines (startLine, endLine, true);
	}

	public String getLines (int startLine, int endLine, boolean withCRLF) throws Exception {
		int 	curLine = 1;
		String 	lidos;
		commandNoResult("ascii()"); 
		screen = "";
		
		StringBuilder consoleScreen = new StringBuilder();
		
		do { 
			lidos = ws3270In.readLine();
			
			consoleScreen.append(lidos);
			consoleScreen.append(crlf);
			
			if ((curLine >= startLine) && (curLine <= endLine)) {
				screen += lidos.substring(6);
				if (withCRLF) screen += crlf;
			}
			curLine++;
			
			analisarErrosProcessamentoMainFrame(lidos);	   
			
		} while (lidos.startsWith("data:"));
		
		br.ufrn.arq.seguranca.log.Logger.info(consoleScreen.toString());
		if(ativarLog)
			br.ufrn.arq.seguranca.log.LoggerSERPROConexao.cadastrarLogConexao(registroSERPROConexao, registroSERPROSincronizacao, registroEntrada, consoleScreen.toString());
		
		consoleScreen = null;
		
		statusLine1 = lidos;
		statusLine2 = ws3270In.readLine();
		getCommandResult(false);
		return screen;
	}

	/**
	 * 
	 * Utilizado para verificar ocorrência de erros no processamento dos comandos MainFrame no ambiente do SERPRO.
	 * 
	 * @param statusMainFrameSERPRO
	 */
	private void analisarErrosProcessamentoMainFrame(String statusMainFrameSERPRO) {

		if (statusMainFrameSERPRO.contains("SENHA INCORRETA") || statusMainFrameSERPRO.contains("SENHA INVALIDA")){
			br.ufrn.arq.seguranca.log.Logger.info("[ERRO] MainFrame: "+statusMainFrameSERPRO);
			if(ativarLog)
				br.ufrn.arq.seguranca.log.LoggerSERPROConexao.cadastrarLogConexao(registroSERPROConexao, registroSERPROSincronizacao, registroEntrada, statusMainFrameSERPRO);
			throw new ErroSenhaMainframeException("ATENÇÃO: senha de acesso ao sistema estruturante incorreta! Confira a senha com cautela, " +
					"pois três erros sucessivos revogam o acesso deste usuário ao sistema estruturante.");
		}	
		
		if (statusMainFrameSERPRO.contains("POR FAVOR. ALTERE A SUA SENHA.") || statusMainFrameSERPRO.contains("TROQUE A SUA SENHA")){
			br.ufrn.arq.seguranca.log.Logger.info("[ERRO] MainFrame: "+statusMainFrameSERPRO);
			if(ativarLog)
				br.ufrn.arq.seguranca.log.LoggerSERPROConexao.cadastrarLogConexao(registroSERPROConexao, registroSERPROSincronizacao, registroEntrada, statusMainFrameSERPRO);
			throw new AlterarSenhaMainframeException("ATENÇÃO: é necessário alterar a senha de acesso ao sistema estruturante, pois o prazo de uso desta expirou. " +
					"Acesse o sistema estruturante diretamente para efetuar esse procedimento. Em caso de dúvida,  entre em contato com o suporte técnico do SERPRO.");
		}	
		
		if (statusMainFrameSERPRO.contains("OPCAO EM MANUTENCAO")){
			br.ufrn.arq.seguranca.log.Logger.info("[ERRO] MainFrame: "+statusMainFrameSERPRO);
			if(ativarLog)
				br.ufrn.arq.seguranca.log.LoggerSERPROConexao.cadastrarLogConexao(registroSERPROConexao, registroSERPROSincronizacao, registroEntrada, statusMainFrameSERPRO);
			throw new OperacaoEmManutencaoSerproException(
					"Esta operação está em manutenção no sistema estruturante do SERPRO. Tente novamente mais tarde.");
		}

		if (statusMainFrameSERPRO.contains("data:  NAT") && statusMainFrameSERPRO.contains("An error occurred during Open command processing")){
			br.ufrn.arq.seguranca.log.Logger.info("[ERRO] MainFrame: "+statusMainFrameSERPRO);
			if(ativarLog)
				br.ufrn.arq.seguranca.log.LoggerSERPROConexao.cadastrarLogConexao(registroSERPROConexao, registroSERPROSincronizacao, registroEntrada, statusMainFrameSERPRO);
			throw new ErroProcessamentoMainFrameException(
					"Falha temporária no processamento do comando pelo ambiente SERPRO. "
							+ "Execute a operação novamente. Caso o problema persista, contacte o suporte.");
		}	

		if (statusMainFrameSERPRO.contains("data:  VVVV") && statusMainFrameSERPRO.contains("CONTACTAR SERPRO")){
			br.ufrn.arq.seguranca.log.Logger.info("[ERRO] MainFrame: "+statusMainFrameSERPRO);
			if(ativarLog)
				br.ufrn.arq.seguranca.log.LoggerSERPROConexao.cadastrarLogConexao(registroSERPROConexao, registroSERPROSincronizacao, registroEntrada, statusMainFrameSERPRO);
			throw new ErroProcessamentoMainFrameException(
					"Falha no processamento do comando pelo ambiente SERPRO. "
							+ "Execute a operação novamente. Caso o problema persista, contacte o suporte do SERPRO.");
		}	
		
	}
	
	private int getCommandResult(boolean readInput) throws IOException {
		
		Integer resultadoCmd = -1;
		
		if (readInput) {
			statusLine1 = ws3270In.readLine();
			statusLine2 = ws3270In.readLine();
		}
		
		if (DEBUG) {
			System.out.println("Comando ("+command+") -> "+statusLine1+" "+statusLine2);
		}
		if (!"ok".equalsIgnoreCase(statusLine2))
			throw new IOException("Erro na execução de comando ("+command+") !!!");
		
		if(!statusLine1.equals("data: Wait: Not connected"))
			resultadoCmd = Integer.parseInt(statusLine1.split(" ")[6],10);
		else
			throw new MainFrameConnectionException("Erro de comunicação. (Acesso concorrente ao SIAFI?)");
			
		return resultadoCmd;
	}

	public String getStatusLine1() {
		return statusLine1;
	}

	public String getStatusLine2() {
		return statusLine2;
	}

	public void sleep(int s) {
		try {
			Thread.currentThread().sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Busca a coluna com determinado nome e retorna o conteúdo da coluna seguinte.
	 * @param siape
	 * @param lines
	 * @param first
	 * @return
	 * @throws Exception
	 */
	public String getTextAfterCol(int line, String fieldName) throws Exception{
		String lineStr = getLines(line, line).trim();
		String columns[] = lineStr.split(" +");
		int colField = 0;
		for (int col = 0; col < columns.length; col++)
			if(columns[col].equals(fieldName))
				colField = col;
		
		if(colField == columns.length-1 )
			return null;
		else
			return columns[colField+1];
	}

	/**
	 * Retorna o conteudo da linha a partir de uma determinada coluna passada como parâmetro.
	 * @param siape
	 * @param lines
	 * @param first
	 * @return
	 * @throws Exception
	 */
	public String getTextAfterPos(int line, int colBeg) throws Exception{
		String lineStr = getLines(line, line).trim();
		String colStr = "";
		String columns[] = lineStr.split(" +");
		
		for (int col = colBeg - 1; col < columns.length; col++)
			   colStr += columns[col]+" ";
		
		return colStr;
	}

	public RegistroSERPROConexao getRegistroSERPROConexao() {
		return registroSERPROConexao;
	}

	public void setRegistroSERPROConexao(RegistroSERPROConexao registroSERPROConexao) {
		this.registroSERPROConexao = registroSERPROConexao;
	}

	public RegistroSERPROSincronizacao getRegistroSERPROSincronizacao() {
		return registroSERPROSincronizacao;
	}

	public void setRegistroSERPROSincronizacao(
			RegistroSERPROSincronizacao registroSERPROSincronizacao) {
		this.registroSERPROSincronizacao = registroSERPROSincronizacao;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
}