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

import org.apache.commons.lang.StringUtils;

public class SIAPE3270 extends SERPRO3270 {

	public SIAPE3270 (String hostname) {
		super(hostname);
	}
	
	public SIAPE3270 (String hostname, String crlf) {
		super(hostname, crlf);
	}

	public SIAPE3270 (String hostname, String crlf, boolean killConnection) {
		super(hostname, crlf, killConnection);
	}

	public void login() throws Exception {
		super.login();
		
		if ("".equals(systemSERPRO))
			throw new IOException ("Sistema SERPRO vazio. Configurar usando setSERPROParam!!!");
		
		int ntries = 3;
		boolean logged = false;
		
		do {
			gotoLastField();
			setCurrentField(systemSERPRO);
				
			int whatScreen = enter(new TextColumn(5, 3, "SEGURANCA"),
							   new TextColumn(1, 3, "MENSAGEM)"));
			
			if (whatScreen == 0) {
				setCurrentField(passWordSERPRO);
  				whatScreen = 1;
  				try {
  					enter(1, 3, "MENSAGEM)");  					
  				} catch (Exception e) {
  					whatScreen = 0;
  				}
			}

			if (whatScreen <= 0) {
				
				if ("SEN0551".equals(getTextInPos(23, 1))) {
					setCurrentField(systemSERPRO);
					pf (4, 8, 3, "SIAPE");
					setField(1, "S");
					enter(22, 2, "LIBERADO");
					enter(22, 1, "COMANDO");
				}
				if("COMANDO.....".equals(getTextInPos(22,1))){
					logged = true;
					return;
				}
			}
			else 
				logged = true;
			
		} while ((--ntries > 0) && !logged);
		
  		pf(3, 22, 1, "COMANDO.....");
	}
	
}