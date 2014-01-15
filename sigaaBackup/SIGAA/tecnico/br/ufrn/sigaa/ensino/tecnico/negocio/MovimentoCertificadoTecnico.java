/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/09/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

/**
 * Movimento para o Certificado Técnico
 * @author Leonardo
 *
 */
public class MovimentoCertificadoTecnico extends AbstractMovimento {

	private int id;
	
	private DiscenteTecnico tecDiscente;
	
	public MovimentoCertificadoTecnico(){
		this.tecDiscente = null;
	}
	
	public MovimentoCertificadoTecnico(Comando comando, Usuario usuarioLogado){
		setCodMovimento(comando);
		setUsuarioLogado(usuarioLogado);
		this.tecDiscente = null;
	}
	
	/* (non-Javadoc)
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return Retorna o(a) tecDiscente.
	 */
	public DiscenteTecnico getTecDiscente() {
		return tecDiscente;
	}

	/**
	 * @param tecDiscente Altera o(a) tecDiscente.
	 */
	public void setTecDiscente(DiscenteTecnico tecDiscente) {
		this.tecDiscente = tecDiscente;
	}



}
