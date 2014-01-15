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


public class CTI3270 extends Generic3270 {

	private String ctiUser;
	private String ctiPW;
	protected String ctiSystem;

	protected String sysUser;
	protected String sysPW;
	protected String sysName;

	public CTI3270 (String hostname) {
		super(hostname);
	}

	public CTI3270 (String hostname, String crlf) {
		super(hostname, crlf);
	}
	
	public void setCTIParam (String netUser, String netPW, String netSystem ) {
		this.ctiUser 	= netUser;
		this.ctiPW   	= netPW;
		this.ctiSystem 	= netSystem;
	}
	
	public void setSystemParam (String sysUser, String sysPW, String sysName ) {
		this.sysUser 	= sysUser;
		this.sysPW 		= sysPW;
		this.sysName 	= sysName;
	}

	public void login() throws Exception {
		super.setTimeout(5);
		super.connect(4, 4, "FEDERAL");

		setField(1, ctiUser);
		setField(2, ctiPW);	
		try {
		   enter(6, 1, "Sessao");
		} catch (Exception e) {
  		   throw new Exception ("Erro na conexão à REDE CTI --> "+getLines(21, 21));
		}
	}

	public String getErrorMessage() throws Exception {
		return getLines(24, 24).trim();
	}
}