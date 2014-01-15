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

public class SIAFI3270 extends SERPRO3270 {

	private String sysUser = "";
	private String sysPW = "";
	private String sysName = "";

	
	public SIAFI3270 (String hostname) {
		super(hostname);
	}
	
	public SIAFI3270 (String hostname, String crlf) {
		super(hostname, crlf);
	}

	public SIAFI3270 (String hostname, String crlf, boolean killConnection) {
		super(hostname, crlf, killConnection);
	}

	public void setSystemParam (String sysUser, String sysPW, String sysName ) {
		this.sysUser 	= sysUser;
		this.sysPW 		= sysPW;
		this.sysName 	= sysName;
	}
	
	protected void login() throws Exception {
		super.login();
		
		if ("".equals(systemSERPRO))
			throw new IOException ("Sistema SERPRO vazio. Configurar usando setSERPROParam!!!");
		
		if ("".equals(sysUser) || "".equals(sysPW) || "".equals(sysName))
			throw new IOException ("Usuario e/ou Password e/ou Versão do SIAFI vazio(s). Configurar usando setSystemParam!!!");

		setCurrentField(systemSERPRO);
		
  		enter(23, 1, "PF1=AJUDA");

 		setField(1, sysUser);
 		setField(2, sysPW);		

 		try {
 			enter(23, 1, "PF1=AJUDA");
 		} finally {
 			String errorMessage = getLines(24, 24).trim();
 			if 	(! "".equals(errorMessage)) {
 				if ("PF10=LIBERA".equals(getTextInPos(23, 3))) {
 	 				if (! killConnection)
 	 					throw new IOException ("Erro na conexão ao SISTEMA --> USUÁRIO JÁ CONECTADO EM OUTRA SESSÃO!");
 					pf(10, 0, 0, null);		
 				} else
 					throw new IOException ("Erro na conexão ao SISTEMA --> "+errorMessage);
 			}
		}
		
		setCurrentField(sysName);
	}

	public void loginProd() throws Exception {
		login();
		enter(23, 1, "PF3=SAI");
		if (! "COMANDO:".equalsIgnoreCase(getTextInPos(22, 1)))
				pf(3, 22, 1, "COMANDO:");
	}

	public void loginEduc() throws Exception {
		login();
		enter(22, 2, "PF3=SAI");
	}

	public String getErrorMessage() throws Exception {
		return getLines(24, 24).trim();
	}
	
	
}