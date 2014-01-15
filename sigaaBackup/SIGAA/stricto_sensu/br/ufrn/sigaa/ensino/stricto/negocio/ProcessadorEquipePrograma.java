/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * cadastro de equipe de programas de pós
 * @author Andre Dantas
 *
 */
public class ProcessadorEquipePrograma extends ProcessadorCadastro {


	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;

		EquipePrograma equipe = (EquipePrograma) mov.getObjMovimentado();
		if( !SigaaListaComando.CADASTRAR_LIMITE_BOLSISTAS_EQUIPE.equals( mov.getCodMovimento() )){
			validate(movimento);
			processarEquipe(equipe);
		}
		if (SigaaListaComando.CADASTRAR_EQUIPE_PROGRAMA.equals(movimento.getCodMovimento())) {
			return super.criar(mov);
		} else if (SigaaListaComando.ALTERAR_EQUIPE_PROGRAMA.equals(movimento.getCodMovimento())) {
			return super.alterar(mov);
		}else if (SigaaListaComando.REMOVER_EQUIPE_PROGRAMA.equals(movimento.getCodMovimento())) {
			return remover(mov);
		}else if (SigaaListaComando.CADASTRAR_LIMITE_BOLSISTAS_EQUIPE.equals(movimento.getCodMovimento())) {
			return cadastrarLimitesBolsistasEquipe(mov);
		}
		return equipe;
	}

	/**
	 * método que remove um membro da equipe do programa
	 */
	@Override
	protected Object remover(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		
		GenericDAO dao = getGenericDAO(mov);
		EquipePrograma equipe = mov.getObjMovimentado();
		
		equipe = dao.refresh(equipe);
		equipe.setAtivo(false);
		dao.update(equipe);
		
		return equipe;
	}
	
	/**
	 * este método cadastra o limite de bolsistas e orientandos dos membros do programa de pós
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	@SuppressWarnings("unchecked")
	private Object cadastrarLimitesBolsistasEquipe(MovimentoCadastro mov) throws DAOException, NegocioException {
		ArrayList<EquipePrograma> membros = (ArrayList<EquipePrograma>) mov.getColObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		
		validarLimiteOrientandos(membros);

		for( EquipePrograma equipe : membros ){
			dao.updateNoFlush( equipe );
		}
		dao.close();
		return membros;
	}
	
	/**
	 * Verifica se esta tentando salvar dados inconsistentes no banco.
	 * Provavelmente esta exceção nunca será lançada, apenas por segurança.
	 * @param membros
	 * @throws NegocioException
	 */
	private void validarLimiteOrientandos(Collection<EquipePrograma> membros) throws NegocioException {
		//Garantindo que sempre seja verdade:
		//maxOrientadosMestrado = maxOrientandoRegularMestrado + maxOrientandoEspecialMestrado
		//maxOrientadosDoutorado = maxOrientandoRegularDoutorado + maxOrientandoEspecialDoutorado		
		for(EquipePrograma e : membros) {			
			Integer maxOrientandosRegularesMestrado = ValidatorUtil.isEmpty( e.getMaxOrientandoRegularMestrado() ) ? 0 : e.getMaxOrientandoRegularMestrado(); 
			Integer maxOrientandosEspeciaisMestrado = ValidatorUtil.isEmpty( e.getMaxOrientandoEspecialMestrado() ) ? 0 : e.getMaxOrientandoEspecialMestrado();
			Integer maxOrientadosMestrado = ValidatorUtil.isEmpty( e.getMaxOrientadosMestrado() ) ? 0 : e.getMaxOrientadosMestrado();
			
			Integer maxOrientandosRegularesDoutorado = ValidatorUtil.isEmpty( e.getMaxOrientandoRegularDoutorado() ) ? 0 : e.getMaxOrientandoRegularDoutorado(); 
			Integer maxOrientandosEspeciaisDoutorado = ValidatorUtil.isEmpty( e.getMaxOrientandoEspecialDoutorado() ) ? 0 : e.getMaxOrientandoEspecialDoutorado();
			Integer maxOrientadosDoutorado = ValidatorUtil.isEmpty( e.getMaxOrientadosDoutorado() ) ? 0 : e.getMaxOrientadosDoutorado();
			
			if(((maxOrientandosRegularesMestrado + maxOrientandosEspeciaisMestrado) != maxOrientadosMestrado) || ((maxOrientandosRegularesDoutorado + maxOrientandosEspeciaisDoutorado) != maxOrientadosDoutorado)) {
				throw new NegocioException("Erro na operação. Entre em contato com a administração do sistema.");
			}			
		}
	}

	private void processarEquipe(EquipePrograma equipe) {
		if (equipe.getDocenteExterno() != null && equipe.getDocenteExterno().getId() > 0)
			equipe.setServidor(null);
		else if (equipe.getServidor() != null && equipe.getServidor().getId() > 0)
			equipe.setDocenteExterno(null);
	}

	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		EquipePrograma equipe = (EquipePrograma) mov.getObjMovimentado();
		EquipeProgramaDao dao = getDAO(EquipeProgramaDao.class, mov);
		int pessoa = 0;
		if (equipe.getDocenteExterno() != null && equipe.getDocenteExterno().getId() > 0) {
			equipe.setDocenteExterno(dao.findByPrimaryKey(equipe.getDocenteExterno().getId(), DocenteExterno.class));
			pessoa = equipe.getDocenteExterno().getPessoa().getId();
		} else if (equipe.getServidor() != null && equipe.getServidor().getId() > 0) {
			equipe.setServidor(dao.findByPrimaryKey(equipe.getServidor().getId(), Servidor.class));
			pessoa = equipe.getServidor().getPessoa().getId();
		}

		Collection<EquipePrograma> equipes = dao.findByPessoa(pessoa);
		if (equipes != null ) {
			for (EquipePrograma ep : equipes) {
				if (ep.getPrograma().getId() == equipe.getPrograma().getId() && ep.getId() != equipe.getId()) {
					throw new NegocioException("Esse programa já possui essa pessoa");
				}
			}
		}

		if (SigaaListaComando.REMOVER_EQUIPE_PROGRAMA.equals(movimento.getCodMovimento())) {
			if( !equipe.isAtivo() )
				throw new NegocioException("Este membro já foi removido do programa.");
		}
		
	}

}
