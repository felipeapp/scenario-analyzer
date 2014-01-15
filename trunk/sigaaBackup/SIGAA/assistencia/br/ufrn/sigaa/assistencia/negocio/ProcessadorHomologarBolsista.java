package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.InclusaoBolsaAcademicaRemoteService;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.bolsas.negocio.MovimentoBolsaAcademica;

public class ProcessadorHomologarBolsista extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoBolsaAcademica m = (MovimentoBolsaAcademica) mov;
		InclusaoBolsaAcademicaRemoteService bolsasService = getBean("inclusaoBolsaAcademicaInvoker", mov);
		
		Collection<InclusaoBolsaAcademicaDTO> solicitacoes = m.getSolicitacoes();
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class, mov);
		
		validate(mov);
		try {
			int idSolicitacaoBolsa;
			for (InclusaoBolsaAcademicaDTO inclusaoBolsa : solicitacoes) {
				idSolicitacaoBolsa = 0;
				try {
					if (mov.getCodMovimento() == SigaaListaComando.CADASTRAR_BOLSISTA_SIPAC ) {
						idSolicitacaoBolsa = bolsasService.cadastrarBolsa(inclusaoBolsa);
						dao.updateFields(BolsaAuxilio.class, inclusaoBolsa.getIdDiscenteProjeto(), 
								new String [] {"tipoBolsaSIPAC", "bolsaAtivaSIPAC"}, new Object [] {idSolicitacaoBolsa, Boolean.TRUE});
					}
				} catch (NegocioRemotoException e) {
					throw new NegocioException(e.getMessage());
				}
				
				try {
					if (mov.getCodMovimento() == SigaaListaComando.REMOVER_BOLSISTA_SIPAC ) {
						bolsasService.finalizarBolsa(inclusaoBolsa);
						dao.inativarBolsa(inclusaoBolsa);
					}
				} catch (NegocioRemotoException e) {
					throw new NegocioException("Não foi possível criar o registro devido a uma falha de comunicação com o " + RepositorioDadosInstitucionais.get("siglaSipac") + ".");
				}
			}
			
		} finally {
			dao.close();
		}

		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoBolsaAcademica m = (MovimentoBolsaAcademica) mov;
		ListaMensagens lista = new ListaMensagens();

		boolean bolsaNaoEncontrada = true;
		boolean municipioNaoEncontrado = true;
		for (InclusaoBolsaAcademicaDTO solicitacoes : m.getSolicitacoes()) {
			if ( solicitacoes.getTipoBolsa() == 0 && bolsaNaoEncontrada )  {
				lista.addErro("Não foi encontrada bolsa correspondente cadastrada no " + RepositorioDadosInstitucionais.get("siglaSipac") + ".");
				bolsaNaoEncontrada = false;
			}
			if ( municipioNaoEncontrado && (solicitacoes.getDataInicio() == null || solicitacoes.getDataFim() == null) ) {
				lista.addErro("Não foi encontrado no calendário vigente um período de bolsa para todos os municípios selecionados.");
				municipioNaoEncontrado = false;
			}
		}
		
		checkValidation(lista);
	}

}