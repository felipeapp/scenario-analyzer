/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 14/12/2007
*
*/
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.EditalProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.AgendaProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoProcessoSeletivo;

/**
 * Processador responsável pela manutenção dos processos seletivos
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorProcessoSeletivo extends AbstractProcessador {

	/**
	 * Executa a operação desejada de acordo com o Comando passado no Movimento
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		// Verificar permissão para realização desta operação
		verificarPermissoes(mov);

		if ( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO) ) {
			validate(mov);
			persistir(mov);
		} else if ( mov.getCodMovimento().equals(SigaaListaComando.REMOVER_PROCESSO_SELETIVO) ) {
			remover(mov);
		}
		
		return null;
	}

	/**
	 * Persisti processo seletivo, juntamente com seus arquivos anexados
	 *
	 * @param mov
	 */
	private void persistir(Movimento mov) throws NegocioException, ArqException {
		MovimentoProcessoSeletivo movProcessoSeletivo = (MovimentoProcessoSeletivo) mov;
		ProcessoSeletivo processo = movProcessoSeletivo.getProcessoSeletivo();

		EditalProcessoSeletivoDao editalProcessoDao = getDAO(EditalProcessoSeletivoDao.class, mov);
		ProcessoSeletivoDao processoDao = getDAO(ProcessoSeletivoDao.class, mov);
		
		try {
			// Persistir arquivo com o edital
			if (movProcessoSeletivo.getEdital() != null ) {

				if (processo.getEditalProcessoSeletivo().getIdEdital() != null) {
					EnvioArquivoHelper.removeArquivo(processo.getEditalProcessoSeletivo().getIdEdital());
				}
				int idEdital = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idEdital,
						movProcessoSeletivo.getEdital().getBytes(),
						movProcessoSeletivo.getEdital().getContentType(),
						movProcessoSeletivo.getEdital().getName());
				processo.getEditalProcessoSeletivo().setIdEdital( idEdital );
			}

			//	Persistir arquivo com o edital
			if (movProcessoSeletivo.getManualCandidato() != null ) {

				if (processo.getEditalProcessoSeletivo().getIdManualCandidato() != null) {
					EnvioArquivoHelper.removeArquivo(processo.getEditalProcessoSeletivo().getIdManualCandidato());
				}
				int idManual = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idManual,
						movProcessoSeletivo.getManualCandidato().getBytes(),
						movProcessoSeletivo.getManualCandidato().getContentType(),
						movProcessoSeletivo.getManualCandidato().getName());
				processo.getEditalProcessoSeletivo().setIdManualCandidato( idManual );
			}

			// Persistir processo seletivo para cursos de Graduação
			EditalProcessoSeletivo edital = processo.getEditalProcessoSeletivo();
			if( isEmpty(edital.getCriadoPor()) ){
				edital.setCriadoPor( movProcessoSeletivo.getUsuarioLogado().getRegistroEntrada() );
			}
			if( isEmpty(edital.getCriadoEm()) ){
				edital.setCriadoEm( movProcessoSeletivo.getUsuarioLogado().getRegistroEntrada().getData() );
			}
			
			for (ProcessoSeletivo p : edital.getProcessosSeletivos()) {
				if( isEmpty(p.getCriadoPor()) ){
					p.setCriadoPor( edital.getCriadoPor() );
				}	
				if( isEmpty(p.getCriadoEm()) ){
					p.setCriadoEm( edital.getCriadoEm() );
				}	
			}
				
			//Remove as datas de agendamento caso estejam fora do período de agendamento
			if( edital.isPossuiAgendamento() )
				removerDatasAgenda(mov, edital);
			else
				edital.setAgendas(null);
			
			//Anula atributo transient
			if (edital.getRestricaoInscrito() != null && edital.getRestricaoInscrito().getId() == 0) {
				edital.setRestricaoInscrito(null);
			}
			
			
			editalProcessoDao.createOrUpdate( edital );

			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			editalProcessoDao.close();
			processoDao.close();
		}
	}

	/**
	 * Remove as datas de agendamento caso estejam fora do período de agendamento
	 * @param mov
	 * @param edital
	 * @throws DAOException
	 */
	private void removerDatasAgenda(Movimento mov, EditalProcessoSeletivo edital) throws DAOException {
	
		if(edital != null && edital.getId()>0 && edital.getAgendas() != null){
		  	Collection<AgendaProcessoSeletivo> agendasDB = getGenericDAO(mov).
		  	findByExactField(AgendaProcessoSeletivo.class, "editalProcessoSeletivo.id", edital.getId());
			for (AgendaProcessoSeletivo a : agendasDB) {
				if(!edital.getAgendas().contains(a))
					getGenericDAO(mov).remove(a);
			}
		}
		
	}
	
	/**
	 * Remove um processo seletivo
	 *
	 * @param mov
	 * @throws ArqException
	 */
	private void remover(Movimento mov) throws DAOException {
		
		MovimentoProcessoSeletivo movProcessoSeletivo = (MovimentoProcessoSeletivo) mov;
		EditalProcessoSeletivo edital = movProcessoSeletivo.getProcessoSeletivo().getEditalProcessoSeletivo();

	  	Collection<ProcessoSeletivo> psDB = getGenericDAO(mov).
	  	findByExactField(ProcessoSeletivo.class, "editalProcessoSeletivo.id", edital.getId());
		for (ProcessoSeletivo ps : psDB) {
				getDAO(ProcessoSeletivoDao.class, movProcessoSeletivo).updateField(ProcessoSeletivo.class, ps.getId(), "ativo", false);
		}
		
	}

	/**
	 * Efetua as validações necessárias para a execução do Comando
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoProcessoSeletivo movProcessoSeletivo = (MovimentoProcessoSeletivo) mov;
		ProcessoSeletivo processo = movProcessoSeletivo.getProcessoSeletivo();
		ListaMensagens erros = null;
		// Realizar validações definidas na classe de domínio
		if( processo != null)
			erros = processo.validate();

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
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
				SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.GESTOR_LATO,
				SigaaPapeis.DAE,SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG}, mov);
	}

}
