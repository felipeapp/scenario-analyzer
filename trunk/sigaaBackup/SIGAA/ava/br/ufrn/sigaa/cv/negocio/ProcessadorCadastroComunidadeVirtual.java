/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 03/11/2008 
 */
package br.ufrn.sigaa.cv.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;

/**
 * Processador para cadastro de comunidades virtuais
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCadastroComunidadeVirtual extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		if (mov.getCodMovimento().equals(SigaaListaComando.VINCULAR_GRUPO_COMUNIDADE)) {
			
			MovimentoCadastroCv movc = (MovimentoCadastroCv) mov;
			vincularGrupoComunidade(movc);
		}
		else if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_MEMBRO_CONVITE)) {
			MovimentoCadastroCv movc = (MovimentoCadastroCv) mov;
			cadastrarMembroConvite(movc);
		}
		else if (mov.getCodMovimento().equals(SigaaListaComando.DESATIVAR_CONVITE_ACEITO_CV)) {
			MovimentoCadastroCv movc = (MovimentoCadastroCv) mov;
			desativarConviteAceitoCV(movc);
		}
		else { // quando cria pela 1 vez a comunidade virtual. quem cria se torna o administrador.
			
				MovimentoCadastro movc = (MovimentoCadastro) mov;
				ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class, mov);
			
				ComunidadeVirtual cv = movc.getObjMovimentado();
			
				MembroComunidade membro = new MembroComunidade(cv);
				membro.setPermissao(MembroComunidade.ADMINISTRADOR);
				membro.setPessoa(cv.getUsuario().getPessoa());
				cv.getParticipantesComunidade().add(membro);
				
				dao.create(cv);
				dao.close();
		}
		return null;
	}
	
	/**
	 * Desativa um convite aceito para entrar na turma virtual.
	 * 
	 * @param movc
	 * @throws DAOException
	 */
	private void desativarConviteAceitoCV(MovimentoCadastroCv movc) throws DAOException {
		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class, movc);
		dao.desativarConviteComunidadeVirtualByHash(movc.getHash());
		dao.close();
	}

	/**
	 * Cadastra um membro a uma comunidade.
	 * 
	 * @param movc
	 * @throws DAOException
	 */
	private void cadastrarMembroConvite(MovimentoCadastroCv movc) throws DAOException {
		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class, movc);
		MembroComunidade membro = movc.getMembroComunidade();
		dao.create(membro);
		dao.close();
	}

	/**
	 * Atualiza a comunidade para salvar os vínculos dos seus grupos.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void vincularGrupoComunidade(MovimentoCadastroCv mov) throws DAOException {
		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class, mov);
		dao.update(mov.getComunidade());
		dao.close();
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {		
	}

}
