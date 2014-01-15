/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '26/06/2008'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.prodocente.EmissaoRelatorioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;

/**
 * Agora o IPI pode ser alterado sem necessariamente fazer todo o c�lculo novamente, o usu�rio com a permiss�o vai l� 
 * e simplesmente altera, isso gera uma inconsist�ncia no banco, para evitar isso � importante guardar o IPI original.
 * Al�m disso � importante guardar os motivos pelos quais o usu�rio alterou o IPI do outro, isso � importante pois uma 
 * altera��o desse tipo requer uma explica��o.
 * 
 * @author Edson Anibal (ambar@info.ufrn.br)
 */
public class ProcessadorAlteraIpi extends AbstractProcessador {

	
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		
		if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_IPI))
			return alterar((MovimentoCadastro)mov);

		return null;

	}

	protected Object alterar(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro classificacaoMovimento = mov;
		EmissaoRelatorio emissaoRelatorio = (EmissaoRelatorio) classificacaoMovimento.getObjMovimentado();
		
		//Obtendo o emissaoRelatorio Original do banco para poder pegar o IPIOriginal:
		EmissaoRelatorioDao emissaoRelatorioDao = getDAO(EmissaoRelatorioDao.class, mov);
		EmissaoRelatorio emissaoOriginal = emissaoRelatorioDao.findByPrimaryKey(emissaoRelatorio.getId(), EmissaoRelatorio.class);

		emissaoOriginal.setMotivoAlteracaoIPI(emissaoRelatorio.getMotivoAlteracaoIPI());
		if (emissaoOriginal.getIpiOriginal() == null) {
			emissaoOriginal.setIpiOriginal(emissaoOriginal.getIpi()); //O ipiOriginal � o primeiro valor do IPI antes de ser alterado.
		}
		
		emissaoOriginal.setIpi(emissaoRelatorio.getIpi());
		emissaoRelatorioDao.update(emissaoOriginal); //<-- Persistindo a altera��o do IPI no banco.
		emissaoRelatorioDao.clearSession();
		
		//Ap�s setar o novo valor do IPI � necess�rio recalcular o FPPI:
		mov.setObjMovimentado(emissaoOriginal.getClassificacaoRelatorio());
		mov.setCodMovimento(SigaaListaComando.CALCULAR_FPPI_CLASSIFICACAO);
		ProcessadorCalculoFppi recalcularFPPI = new ProcessadorCalculoFppi();
		recalcularFPPI.execute(mov);
		
		return emissaoOriginal;
	}
	//---//
	
	
	public void validate(Movimento mov)	throws NegocioException, ArqException {
		
		MovimentoCadastro classificacaoMovimento = (MovimentoCadastro) mov;
		EmissaoRelatorio emissaoRelatorio = (EmissaoRelatorio) classificacaoMovimento.getObjMovimentado();
		
		//O IPI s� deve ser alterado caso seja maior que zero.
		if (emissaoRelatorio.getIpi().equals(null) || emissaoRelatorio.getIpi()<=0)
			throw new NegocioException("O novo valor do IPI deve ser maior que zero.");
	}

}
