/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 10/02/2010
 */
package br.ufrn.sigaa.bolsas.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.InclusaoBolsaAcademicaRemoteService;

/**
 * Processador utilizado na solicitação de inclusão/exclusão de bolsas no SIPAC a partir
 * do SIGAA.
 * 
 * @author Ilueny Santos
 * 
 */
public class ProcessadorBolsaAcademica extends AbstractProcessador {

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoBolsaAcademica m = (MovimentoBolsaAcademica) mov;

		InclusaoBolsaAcademicaRemoteService bolsasService = getBean("inclusaoBolsaAcademicaInvoker", mov);
        		
		JdbcTemplate template = new JdbcTemplate(Database.getInstance().getSigaaDs());

		Collection<InclusaoBolsaAcademicaDTO> solicitacoes = m.getSolicitacoes();
		for (InclusaoBolsaAcademicaDTO inclusaoBolsa : solicitacoes) {

			try {
				// incluindo solicitação no sistema administrativo (sipac)
				int idSolicitacaoBolsa;
				if (mov.getCodMovimento() == ArqListaComando.SOLICITAR_INCLUSAO_BOLSA_ACADEMICA ) {
					idSolicitacaoBolsa = bolsasService.cadastrarSolicitacaoInclusao(inclusaoBolsa);
	
					//Atualizando tabela de sincronização no sigaa
					template.update("insert into projetos.sincronizacao_bolsa_sipac "
									+ "(id_solicitacao_bolsa, id_discente_projeto, id_registro_entrada, id_tipo_bolsa) "
									+ "values ( " + idSolicitacaoBolsa + ", " + inclusaoBolsa.getIdDiscenteProjeto() + ", " + inclusaoBolsa.getIdRegistroEntrada() + ", " + inclusaoBolsa.getTipoBolsa() + ")");
				}
				if (mov.getCodMovimento() == ArqListaComando.ATUALIZAR_BOLSA_ACADEMICA ) {
					idSolicitacaoBolsa = bolsasService.atualizaSolicitacaoInclusao(inclusaoBolsa);
	
					//Atualizando tabela de sincronização no sigaa
					template.update("insert into projetos.sincronizacao_bolsa_sipac "
									+ "(id_solicitacao_bolsa, id_discente_projeto, id_registro_entrada, id_tipo_bolsa) "
									+ "values ( " + idSolicitacaoBolsa + ", " + inclusaoBolsa.getIdDiscenteProjeto() + ", " + inclusaoBolsa.getIdRegistroEntrada() + ", " + inclusaoBolsa.getTipoBolsa() + ")");
				}
				if (mov.getCodMovimento() == ArqListaComando.SOLICITAR_EXCLUSAO_BOLSA_ACADEMICA ) {
					// excluindo solicitação no sistema administrativo (SIPAC)
					 idSolicitacaoBolsa = bolsasService.cadastrarSolicitacaoExclusao(inclusaoBolsa);
				}
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());				
			} catch (Exception e) {
				throw new NegocioException("Não foi possível criar o registro devido a uma falha de comunicação com o " + RepositorioDadosInstitucionais.get("siglaSipac") +   ". Entre em contato com a administração do sistema.");
			}
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}
}