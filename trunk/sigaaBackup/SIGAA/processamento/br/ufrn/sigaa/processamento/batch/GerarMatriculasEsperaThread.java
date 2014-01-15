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
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.processamento.dominio.ModoProcessamentoMatricula;
import br.ufrn.sigaa.processamento.negocio.PreProcessamentoMov;

/**
 * Thread que consome as turmas, responsável por gerar as matrículas no estado de espera.
 *
 * @author David Pereira
 *
 */
public class GerarMatriculasEsperaThread extends ProcessamentoBatchThread<SolicitacaoMatricula> {

	int ano, periodo;
	
	ModoProcessamentoMatricula modo;
	
	Comando comando;

	private boolean rematricula;
	
	public GerarMatriculasEsperaThread(ListaProcessamentoBatch<SolicitacaoMatricula> lista, int ano, int periodo, boolean rematricula, ModoProcessamentoMatricula modo, Comando comando) {
		this.lista = lista;
		this.ano = ano;
		this.periodo = periodo;
		this.modo = modo;
		this.comando = comando;
		this.rematricula = rematricula;
	}
	
	@Override
	public void processar(Processador processador, SolicitacaoMatricula elemento) throws NegocioException, ArqException, RemoteException {
		PreProcessamentoMov mov = new PreProcessamentoMov();
		mov.setCodMovimento(comando);
		mov.setAno(ano);
		mov.setPeriodo(periodo);
		mov.setModo(modo);
		mov.setRematricula(rematricula);
		mov.setSolicitacao(elemento);
		mov.setUsuarioLogado(new Usuario(Usuario.USUARIO_SISTEMA));
		mov.setSistema(Sistema.SIGAA);

		processador.execute(mov);
	}

}
