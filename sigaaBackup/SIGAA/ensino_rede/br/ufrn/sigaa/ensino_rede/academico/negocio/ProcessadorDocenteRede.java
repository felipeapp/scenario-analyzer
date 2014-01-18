/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 14/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.academico.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoSituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.negocio.DocenteRedeHelper;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável pelas operações em docentes de ensino em rede.
 *
 * @author Henrique André
 */
public class ProcessadorDocenteRede extends AbstractProcessador {

	@Override
	public Object execute(Movimento movimento) throws NegocioException,	ArqException, RemoteException {
		
		if (movimento.getCodMovimento().equals(SigaaListaComando.CADASTRAR_DOCENTE_REDE)) 
			cadastrarDocente(movimento);
		else if (movimento.getCodMovimento().equals(SigaaListaComando.ALTERAR_DOCENTE_REDE))
			alterarDocente(movimento);

		
		return null;

	}
	
	/**
	 * Método auxiliar do execute(), para a realizar alteração de docente.
	 * 
	 * @param mov
	 * @return
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void alterarDocente(Movimento mov) throws NegocioException, ArqException, RemoteException {

		GenericDAO dao = getGenericDAO(mov);

		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			
			DocenteRede docente = (DocenteRede) movCad.getObjMovimentado();
			
			DocenteRede original = dao.findByPrimaryKey(docente.getId(), DocenteRede.class);
			SituacaoDocenteRede situacaoAntiga = original.getSituacao();
			dao.detach(original);
			
			dao.update( docente );
			
			if (situacaoAntiga.getId() != docente.getSituacao().getId()){
				AlteracaoSituacaoDocenteRede alteracao = DocenteRedeHelper.createAlteracaoDocenteRede(docente, mov, situacaoAntiga, docente.getSituacao());
				dao.create(alteracao);
			}
			
		} finally {
			dao.close();
		}
	}	
	
	/**
	 * Método auxiliar do execute(), para a realizar cadastro de docente.
	 * 
	 * @param mov
	 * @return
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void cadastrarDocente(Movimento mov) throws NegocioException, ArqException, RemoteException {

		GenericDAO dao = getGenericDAO(mov);

		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			
			DocenteRede docente = (DocenteRede) movCad.getObjMovimentado();
			
			criarPessoa(dao, movCad, docente);
			dao.create( docente );
			AlteracaoSituacaoDocenteRede alteracao = DocenteRedeHelper.createAlteracaoDocenteRede(docente, mov, null, docente.getSituacao());
			dao.create(alteracao);
			
		} finally {
			dao.close();
		}
	}

	/**
	 * Método auxiliar responsável pelo cadastro de pessoa.
	 * 
	 * @param mov
	 * @return
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void criarPessoa(GenericDAO dao, MovimentoCadastro movCad, DocenteRede docente) throws DAOException, NegocioException, ArqException, RemoteException {
		docente.getPessoa().setUltimaAtualizacao(new Date());
		
		ProcessadorPessoa.anularTransientObjects(docente.getPessoa());
		ProcessadorPessoa.persistirContaBancaria(docente.getPessoa(), dao);
		
		// chama o processador pessoa para o cadastro da pessoa
		ProcessadorPessoa pPessoa = new ProcessadorPessoa();
		PessoaMov movPessoa = new PessoaMov();
		movPessoa.setPessoa(docente.getPessoa());
		movPessoa.setUsuarioLogado(movCad.getUsuarioLogado());
		movPessoa.setSistema( movCad.getSistema() );
		movPessoa.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
		
		// se a pessoa já existir chama alteração
		if ( docente.getPessoa().getId() != 0 ) {
			movPessoa.setCodMovimento(SigaaListaComando.ALTERAR_PESSOA);
		} else {
			// Sincroniza para pegar o próximo idPessoa, para evitar assim que o Docente Externo seja cadastrado com o id = 0.
			movPessoa.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);
			int idPessoa = SincronizadorPessoas.getNextIdPessoa();
			docente.getPessoa().setId(idPessoa);
		}
		Pessoa pessoa = (Pessoa) pPessoa.execute(movPessoa);
		if (pessoa != null)
			docente.setPessoa(pessoa);
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}
	
}
