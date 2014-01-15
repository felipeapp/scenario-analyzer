package br.ufrn.integracao.exceptions;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.WebFault;

@WebFault(name="NegocioRemotoFault") @XmlRootElement
public class NegocioRemotoException extends Exception {

	public NegocioRemotoException(String mensagem) {
		super(mensagem);
	}
	
}
