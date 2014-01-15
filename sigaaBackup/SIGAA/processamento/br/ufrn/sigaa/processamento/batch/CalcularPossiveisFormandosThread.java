/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 16/01/2009
 *
 */

package br.ufrn.sigaa.processamento.batch;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.Processador;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.processamento.dominio.ModoProcessamentoMatricula;
import br.ufrn.sigaa.processamento.negocio.PreProcessamentoMov;

/**
 * Thread que consome as turmas, responsável por calcular possíveis formando por ano e período.
 *
 * @author David Pereira
 *
 */
public class CalcularPossiveisFormandosThread extends ProcessamentoBatchThread<Integer> {

	int ano, periodo;
	
	/** @see {@link ModoProcessamentoMatricula} */
	ModoProcessamentoMatricula modo;
	
	Comando comando;
	
	public CalcularPossiveisFormandosThread(ListaProcessamentoBatch<Integer> lista, int ano, int periodo, ModoProcessamentoMatricula modo, Comando comando) {
		this.lista = lista;
		this.ano = ano;
		this.periodo = periodo;
		this.modo = modo;
		this.comando = comando;
	}
	
	@Override
	public void processar(Processador processador, Integer elemento) throws NegocioException, ArqException, RemoteException {
		PreProcessamentoMov mov = new PreProcessamentoMov();
		mov.setCodMovimento(comando);
		mov.setAno(ano);
		mov.setPeriodo(periodo);
		mov.setModo(modo);
		mov.setDiscente(elemento);
		mov.setUsuarioLogado(new Usuario(Usuario.USUARIO_SISTEMA));
		mov.setSistema(Sistema.SIGAA);

		processador.execute(mov);
	}

}
