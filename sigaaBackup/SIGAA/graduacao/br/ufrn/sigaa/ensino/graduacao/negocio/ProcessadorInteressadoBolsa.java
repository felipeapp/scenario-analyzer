/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 03/04/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.InteressadoBolsa;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoInteressadoBolsa;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.monitoria.dominio.InscricaoSelecaoMonitoria;
import br.ufrn.sigaa.projetos.dominio.InscricaoSelecaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;

/**
 * Responsável por manipular as operações de CRUD de InteressadoBolsa
 * 
 * @author Henrique André
 *
 */
public class ProcessadorInteressadoBolsa extends AbstractProcessador {

	/**
	 * Invocado pela arquitetura
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA)){
			MovimentoCadastro m = (MovimentoCadastro) mov;
			InteressadoBolsa interesse = m.getObjMovimentado();
	
			GenericDAO dao = getGenericDAO(m);
			dao.create(interesse);
			
			return interesse;
		}
		if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA_EXTENSAO)){
			
			MovimentoCadastro m = (MovimentoCadastro) mov;
			InscricaoSelecaoExtensao inscricao = m.getObjMovimentado();
			GenericDAO dao = getGenericDAO(m);
			dao.create(inscricao);
			
			InteressadoBolsa interesse = new InteressadoBolsa();
			interesse.setAtivo(true);
			interesse.setDiscente(inscricao.getDiscente());
			interesse.setIdEstagio(inscricao.getAtividade().getId());
			interesse.setIdUsuario(m.getUsuarioLogado().getId());
			interesse.setTipoBolsa(TipoInteressadoBolsa.EXTENSAO);
			
			interesse.setDados(inscricao.getDados());
			
			dao.create(interesse);
			dao.close();
			
			
			return inscricao;
		}
		if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA_MONITORIA)){
			MovimentoCadastro m = (MovimentoCadastro) mov;
			InscricaoSelecaoMonitoria inscricao = m.getObjMovimentado();
			GenericDAO dao = getGenericDAO(m);
			dao.create(inscricao);
			
			InteressadoBolsa interesse = new InteressadoBolsa();
			interesse.setAtivo(true);
			interesse.setDiscente(inscricao.getDiscente().getDiscente());
			interesse.setIdEstagio(inscricao.getProvaSelecao().getId());
			interesse.setIdUsuario(m.getUsuarioLogado().getId());
			interesse.setTipoBolsa(TipoInteressadoBolsa.MONITORIA);
			
			interesse.setDados(inscricao.getDados());
			
			dao.create(interesse);
			dao.close();
			
			
			return inscricao;
		}
		
		if(mov.getCodMovimento().equals(SigaaListaComando.RECADASTRAR_INTERESSE_BOLSA_EXTENSAO)){
			MovimentoCadastro m = (MovimentoCadastro) mov;
			InscricaoSelecaoExtensao inscricao = m.getObjMovimentado();
			GenericDAO dao = getGenericDAO(m);
			
			inscricao.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.INSCRITO_PROCESSO_SELETIVO));
			
			dao.update(inscricao);
			dao.close();
			
			return inscricao;
		}
		
		//cadastro de em ação associada
		if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA_ACAO_ASSOCIADA)){
			cadastrarInteresseAssociada(mov);
		}
		
		// recadastro em ação associada
		if(mov.getCodMovimento().equals(SigaaListaComando.RECADASTRAR_INTERESSE_BOLSA_ASSOCIADA)){
			recadastrarInteresseAssociada(mov);
		}
			
		return null;
	}

	/** recadastrar interesse em ação associada */
	private void recadastrarInteresseAssociada(Movimento mov) throws DAOException{
		MovimentoCadastro m = (MovimentoCadastro) mov;
		InscricaoSelecaoProjeto inscricao = m.getObjMovimentado();
		GenericDAO dao = getGenericDAO(m);
		try{
			inscricao.setSituacaoDiscenteProjeto(new TipoSituacaoDiscenteProjeto(TipoSituacaoDiscenteProjeto.INSCRITO_PROCESSO_SELETIVO));
			dao.update(inscricao);
		}finally{
			dao.close();
		}
		
	}
	
	/** cadastrar interesse em ação associada */
	private void cadastrarInteresseAssociada(Movimento mov) throws DAOException{
		MovimentoCadastro m = (MovimentoCadastro) mov;
		InscricaoSelecaoProjeto inscricao = m.getObjMovimentado();
		GenericDAO dao = getGenericDAO(m);
		try{
			dao.create(inscricao);
			
			InteressadoBolsa interesse = new InteressadoBolsa();
			interesse.setAtivo(true);
			interesse.setDiscente(inscricao.getDiscente());
			interesse.setIdEstagio(inscricao.getProjeto().getId());
			interesse.setIdUsuario(m.getUsuarioLogado().getId());
			interesse.setTipoBolsa(TipoInteressadoBolsa.ACAO_ASSOCIADA);
			
			interesse.setDados(inscricao.getDados());
			
			dao.create(interesse);
		}finally{
			dao.close();
		}
	}
	
	/** validate */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
