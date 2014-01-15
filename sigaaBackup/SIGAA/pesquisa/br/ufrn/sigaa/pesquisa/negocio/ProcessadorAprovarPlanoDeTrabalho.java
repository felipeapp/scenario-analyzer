/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/08/2009
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
import br.ufrn.sigaa.pesquisa.dominio.HistoricoPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;

/**
 * Entidade responsável pelo processamento de aprovação dos planos de trabalho
 * 
 * @author Geyson
 */
public class ProcessadorAprovarPlanoDeTrabalho extends AbstractProcessador {


	/**
	 *  Define status como APROVADO para planos de trabalhos.
	 */
	@SuppressWarnings("unchecked")
	public Object execute(Movimento mov) throws NegocioException, ArqException,
	RemoteException {

		validate(mov);
		Collection<PlanoTrabalho> lista = (Collection<PlanoTrabalho>) ((MovimentoCadastro) mov).getColObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);

		for (PlanoTrabalho plano : lista) {
			try {
				if ( mov.getCodMovimento().equals(SigaaListaComando.APROVAR_PLANO_TRABALHO_CORRIGIDO)){
					
					// Cadastrar histórico
					dao.updateField(PlanoTrabalho.class, plano.getId(), "status", TipoStatusPlanoTrabalho.APROVADO);
					plano.setStatus(TipoStatusPlanoTrabalho.APROVADO);
					HistoricoPlanoTrabalho historico = gerarEntradaHistorico( plano, mov);
					dao.create(historico); 
					
				}
			}  finally {
				dao.close();
			}

		}

		return null;
	}

	/**
	 * Verifica se planos de trabalho tem status corrigido.
	 */
	@SuppressWarnings("unchecked")
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);
		List<PlanoTrabalho> lista = (List<PlanoTrabalho>) ((MovimentoCadastro) mov).getColObjMovimentado();
		ListaMensagens erros = new ListaMensagens();
		
		for (PlanoTrabalho planoTrabalho : lista) {
			if(!planoTrabalho.isCorrigido()){
				erros.addErro("Plano de Trabalho" + planoTrabalho.getTitulo() + "não possui status corrigido.");
				
			}
		}
		
		checkValidation(erros);
		
	}
	
	/**
	 * Gera um histórico no Plano de Trabalho.
	 * @param planoTrabalho
	 * @param mov
	 * @return
	 */
	public static HistoricoPlanoTrabalho gerarEntradaHistorico(PlanoTrabalho planoTrabalho, Movimento mov) {
		HistoricoPlanoTrabalho historico = new HistoricoPlanoTrabalho();
		historico.setPlanoTrabalho(planoTrabalho);
		historico.setStatus(planoTrabalho.getStatus());
		historico.setData( new Date() );
		historico.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
		historico.setTipoBolsa(planoTrabalho.getTipoBolsa());

		return historico;
	}

}
