/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2009
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoResumoCongresso;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;

public class ProcessadorAlterarStatusResumosCIC extends AbstractProcessador {

	/**
	 *  Define um novo status para resumos de congressos.
	 */
	@SuppressWarnings("unchecked")
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		ResumoCongresso resumoCongresso = ((MovimentoCadastro) mov).getObjMovimentado();
		Collection<ResumoCongresso> lista = (Collection<ResumoCongresso>) ((MovimentoCadastro) mov).getColObjMovimentado();
		GenericDAO dao = getDAO(mov);

		for (ResumoCongresso resumo : lista) {
			try {
				if ( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_STATUS_RESUMOS_CIC)){
					
					// Cadastrar histórico
					dao.updateField(ResumoCongresso.class, resumo.getId(), "status", resumoCongresso.getStatus());
					resumo.setStatus(resumoCongresso.getStatus());
					HistoricoResumoCongresso historico = gerarEntradaHistorico( resumo, mov);
					dao.create(historico); 
					
				}
			}  finally {
				dao.close();
			}

		}

		return null;
	}

	/**
	 * Verifica se Resumo de Congresso já possuia mesmo status. 
	 */
	@SuppressWarnings("unchecked")
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(new int[]{SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.MEMBRO_COMITE_PESQUISA}, mov);
		ResumoCongresso resumoCongresso = ((MovimentoCadastro) mov).getObjMovimentado();
		List<ResumoCongresso> lista = (List<ResumoCongresso>) ((MovimentoCadastro) mov).getColObjMovimentado();
		ListaMensagens erros = new ListaMensagens();
		
		for (ResumoCongresso resumo : lista) {
			if(resumo.getStatus() == resumoCongresso.getStatus()){
				erros.addWarning("Resumo de Congresso CIC" + resumo.getTitulo() + " possui mesmo status.");
				
			}
		}
		
		checkValidation(erros);
	}
	
	/**
	 * Gera um histórico no Resumo de Congresso.
	 * @param resumo
	 * @param mov
	 * @return
	 */
	public static HistoricoResumoCongresso gerarEntradaHistorico(ResumoCongresso resumo, Movimento mov) {
		HistoricoResumoCongresso historico = new HistoricoResumoCongresso();
		historico.setResumoCongresso(resumo);
		historico.setStatus(resumo.getStatus());
		historico.setData( new Date() );
		historico.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );

		return historico;
	}

}
