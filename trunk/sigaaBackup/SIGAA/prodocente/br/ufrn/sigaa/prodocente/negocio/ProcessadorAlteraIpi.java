/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Agora o IPI pode ser alterado sem necessariamente fazer todo o cálculo novamente, o usuário com a permissão vai lá 
 * e simplesmente altera, isso gera uma inconsistência no banco, para evitar isso é importante guardar o IPI original.
 * Além disso é importante guardar os motivos pelos quais o usuário alterou o IPI do outro, isso é importante pois uma 
 * alteração desse tipo requer uma explicação.
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
			emissaoOriginal.setIpiOriginal(emissaoOriginal.getIpi()); //O ipiOriginal é o primeiro valor do IPI antes de ser alterado.
		}
		
		emissaoOriginal.setIpi(emissaoRelatorio.getIpi());
		emissaoRelatorioDao.update(emissaoOriginal); //<-- Persistindo a alteração do IPI no banco.
		emissaoRelatorioDao.clearSession();
		
		//Após setar o novo valor do IPI é necessário recalcular o FPPI:
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
		
		//O IPI só deve ser alterado caso seja maior que zero.
		if (emissaoRelatorio.getIpi().equals(null) || emissaoRelatorio.getIpi()<=0)
			throw new NegocioException("O novo valor do IPI deve ser maior que zero.");
	}

}
