/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Esta classe cria a cadeia e inicia a execução do processamento
 * 
 * @author Henrique André
 *
 */
public class ProcessarVinculos extends ProcessarVinculoExecutor {
	
	/**
	 * Dados do processamento
	 */
	private final DadosProcessamentoVinculos dados;
	
	public ProcessarVinculos(Usuario usuario) {
		dados = new DadosProcessamentoVinculos(usuario);
		createChain();
	}

	/**
	 * Cria a cadeia de responsabilidade
	 */
	private void createChain() {
		setNext(new ProcessarDiscente()).
		setNext(new ProcessarServidor()).
		setNext(new ProcessarResponsavel()).
		setNext(new ProcessarDocenteExterno()).
		setNext(new ProcessarTutor()).
		setNext(new ProcessarTutorIMD()).
		setNext(new ProcessarCoordenacaoPolo()).
		setNext(new ProcessarConcedente()).
		setNext(new ProcessarSecretaria()).
		setNext(new ProcessarFamiliar()).
		setNext(new ProcessarCoordenacaoGeralRede()).
		setNext(new ProcessarCoordenadorUnidadeRede()).
		setNext(new ProcessarGenerico());
		
	}

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws DAOException {
		// não faz nada
	}

	public void executar(HttpServletRequest req) throws ArqException {
		executar(req, dados);
	}

	public DadosProcessamentoVinculos getDados() {
		return dados;
	}	
	
}
