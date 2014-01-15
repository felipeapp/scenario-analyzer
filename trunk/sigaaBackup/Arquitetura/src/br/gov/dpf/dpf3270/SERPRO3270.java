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

import java.io.IOException;

public class SERPRO3270 extends Generic3270 {
	protected   String userSERPRO = "";
	protected 	String passWordSERPRO = "";
	protected 	String systemSERPRO = "";
	
	boolean killConnection = true;
    
	public SERPRO3270 (String hostname) {
		super(hostname);
	}
	
	public SERPRO3270 (String hostname, String crlf) {
		super(hostname, crlf);
	}

	public SERPRO3270 (String hostname, String crlf, boolean killConnection) {
		super(hostname, crlf);
		this.killConnection = killConnection;
	}

	public void setSERPROParam (String userSERPRO, String passWordSERPRO, String systemSERPRO ) {
		this.userSERPRO 	= userSERPRO;
		this.passWordSERPRO = passWordSERPRO;
		this.systemSERPRO 	= systemSERPRO;
	}
	
	protected void login() throws Exception {		
		try {
			super.connect(24, 1, "(T5)");
		} catch (Exception e) {
			System.out.println("Não é a tela inicial de informação do SERPRO");
		}
		
		enter(24, 1, "PF1=AJUDA");

		if ("".equals(userSERPRO) || "".equals(passWordSERPRO))
			throw new IOException ("Usuario e/ou senha SERPRO vazias. Configurar usando setSERPROParam!!!");
		setField(1, userSERPRO);
		setField(2, passWordSERPRO);		
  		enter(22, 1, "COMANDO");
	}
}