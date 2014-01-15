/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 14/12/2007
*
*/
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoEditalProcessoSeletivo;

/**
 * Processador responsável pela manutenção dos editais dos processos seletivos
 *
 * @author Mário Rizzi
 *
 */
public class ProcessadorEditalProcessoSeletivo extends AbstractProcessador {

	/**
	 * Executa a operação desejada de acordo com o Comando passado no Movimento
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		// Verificar permissão para realização desta operação
		verificarPermissoes(mov);

		if ( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_ALTERAR_EDITAL_PROCESSO_SELETIVO) ) {
			validate(mov);
			persistir(mov);
		} 
		
		return null;
	}

	/**
	 * Persisti processo seletivo, juntamente com seus arquivos anexados
	 *
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void persistir(Movimento mov) throws NegocioException, ArqException {
		MovimentoEditalProcessoSeletivo movEditalProcessoSeletivo = (MovimentoEditalProcessoSeletivo) mov;
		EditalProcessoSeletivo editalProcesso = movEditalProcessoSeletivo.getEditalProcessoSeletivo();

		GenericDAO dao = getGenericDAO(mov);
		try {
			// Persistir arquivo com o edital
			if (movEditalProcessoSeletivo.getEdital() != null ) {

				if (editalProcesso.getIdEdital() != null) {
					EnvioArquivoHelper.removeArquivo(editalProcesso.getIdEdital());
				}
				int idEdital = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idEdital,
						movEditalProcessoSeletivo.getEdital().getBytes(),
						movEditalProcessoSeletivo.getEdital().getContentType(),
						movEditalProcessoSeletivo.getEdital().getName());
				editalProcesso.setIdEdital( idEdital );
			}

			//	Persistir arquivo com o edital
			if (movEditalProcessoSeletivo.getManualCandidato() != null ) {

				if (editalProcesso.getIdManualCandidato() != null) {
					EnvioArquivoHelper.removeArquivo(editalProcesso.getIdManualCandidato());
				}
				int idManual = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idManual,
						movEditalProcessoSeletivo.getManualCandidato().getBytes(),
						movEditalProcessoSeletivo.getManualCandidato().getContentType(),
						movEditalProcessoSeletivo.getManualCandidato().getName());
				editalProcesso.setIdManualCandidato( idManual );
			}
			
			
			dao.createOrUpdate(editalProcesso);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			dao.close();
		}
	}



	/**
	 * Efetua as validações necessárias para a execução do Comando
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoEditalProcessoSeletivo movProcessoSeletivo = (MovimentoEditalProcessoSeletivo) mov;
		EditalProcessoSeletivo editalProcesso = movProcessoSeletivo.getEditalProcessoSeletivo();

		// Realizar validações definidas na classe de domínio
		ListaMensagens erros = editalProcesso.validate();

		// TODO Verificar se já existe um processo seletivo cadastrado para o mesmo período
		checkValidation(erros);

	}

	/**
	 * Método responsável pela verificação do acesso do usuário ao controle do
	 * processo seletivo. Necessita verificar, para isto, os papeis do usuário e 
	 * em alguns casos o curso vinculado ao processo.
	 *
	 * @param mov
	 * @throws ArqException
	 * @throws SegurancaException
	 *
	 */
	private void verificarPermissoes(Movimento mov) throws SegurancaException, ArqException {
		checkRole(new int[] {SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.SECRETARIA_POS, 
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO,
				SigaaPapeis.DAE,SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG}, mov);
	}

}
