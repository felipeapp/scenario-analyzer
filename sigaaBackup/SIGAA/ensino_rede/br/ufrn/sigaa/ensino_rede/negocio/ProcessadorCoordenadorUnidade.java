/**
 * 
 */
package br.ufrn.sigaa.ensino_rede.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Telefone;
import br.ufrn.sigaa.ensino_rede.dao.CoordenadorUnidadeDao;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável pelo cadastro de Coordenadores de Unidade
 * @author Joab
 *
 */
public class ProcessadorCoordenadorUnidade extends ProcessadorCadastro{

	@Override
	public Object execute(Movimento movimento) throws NegocioException,	ArqException, RemoteException {
		
		validate(movimento);
		
		if (movimento.getCodMovimento().equals(SigaaListaComando.CADASTRO_COORDENADOR_UNIDADE)) 
			cadastrarCoordenador(movimento);
		else if (movimento.getCodMovimento().equals(SigaaListaComando.ALTERAR_COORDENADOR_UNIDADE))
			alterarCoordenador(movimento);
		else if (movimento.getCodMovimento().equals(SigaaListaComando.INATIVAR_COORDENADOR_UNIDADE))
			inativarMembroCoordenacao(movimento);
		else if (movimento.getCodMovimento().equals(SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE))
			cadastrarSecretaria(movimento);
		return null;
	}
	
	/**
	 * Metodo que realiza a remoção do coordenador de unidade.
	 * @param mov
	 * @throws ArqException
	 */
	private void inativarMembroCoordenacao(Movimento mov) throws  ArqException{
		GenericDAO dao = getGenericDAO(mov);
		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			CoordenadorUnidade coordenador = (CoordenadorUnidade) movCad.getObjMovimentado();
			dao.updateField(CoordenadorUnidade.class, coordenador.getId(), "ativo", false);
		}finally{
			dao.close();
		}
		
	}

	/**
	 * Metodo que realiza a alteração do coordenador de unidade.
	 * @param mov
	 * @throws ArqException
	 */
	private void alterarCoordenador(Movimento mov) throws  ArqException {

		GenericDAO dao = getGenericDAO(mov);

		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			CoordenadorUnidade coordenador = (CoordenadorUnidade) movCad.getObjMovimentado();
			for(Telefone t: coordenador.getDados().getTelefones())
				dao.createOrUpdate(t);
			dao.createOrUpdate( coordenador );
		} finally {
			dao.close();
		}
	}	
	
	/**
	 * Metodo que realiza o cadastro do coordenador de unidade.
	 * 
	 * @param mov
	 * @throws ArqException
	 */
	private void cadastrarCoordenador(Movimento mov) throws  ArqException {

		GenericDAO dao = getGenericDAO(mov);

		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			CoordenadorUnidade coordenador = (CoordenadorUnidade) movCad.getObjMovimentado();
			
			for(Telefone t: coordenador.getDados().getTelefones())
				dao.createOrUpdate(t);			
			dao.create( coordenador );
		} finally {
			dao.close();
		}
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro movCad = (MovimentoCadastro) mov;		
		CoordenadorUnidade coordenador = (CoordenadorUnidade) movCad.getObjMovimentado();
		CoordenadorUnidadeDao dao = getDAO(CoordenadorUnidadeDao.class, mov);
		
		if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRO_COORDENADOR_UNIDADE)){
			checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE ,mov);
			validaCoordenacaoUnidade(coordenador,dao,false);
		}else if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_COORDENADOR_UNIDADE)){
			checkRole(new int[] {SigaaPapeis.COORDENADOR_GERAL_REDE,SigaaPapeis.COORDENADOR_UNIDADE_REDE} ,mov); 
			validaCoordenacaoUnidade(coordenador,dao,true);
		}else if((mov.getCodMovimento().equals(SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE))){
			checkRole(new int[] {SigaaPapeis.COORDENADOR_GERAL_REDE,SigaaPapeis.COORDENADOR_UNIDADE_REDE} ,mov); 
			if(coordenador.getId() == 0)
				validaCoordenacaoUnidade(coordenador,dao,false);
			else
				validaCoordenacaoUnidade(coordenador,dao,true);
		}else
			
			checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE ,mov);
	}

	/**
	 * Verifica se existem conflitos no cadastro da coordenação da Unidade..
	 * 
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void validaCoordenacaoUnidade(CoordenadorUnidade coordenador,CoordenadorUnidadeDao dao,boolean alteracao) throws ArqException, NegocioException {
		
		Collection<CoordenadorUnidade> coordenadoresUnidade = dao.findCoordenadoresByCampusIes(coordenador.getDadosCurso().getCampus().getId());
		CoordenadorUnidade coordenacaoAtiva = dao.findCoordenacaoAtivaByPessoa( coordenador.getPessoa().getId());
		if(coordenacaoAtiva != null && !alteracao)
			throw new NegocioException("Este Docente possui " + coordenacaoAtiva.getCargo().getDescricao() + " ativa na " + coordenacaoAtiva.getDadosCurso().getCampus().getDescricao() );
		if(coordenadoresUnidade != null)
			for(CoordenadorUnidade c: coordenadoresUnidade){
				if(c.getCargo().getId() == coordenador.getCargo().getId())
					if(alteracao && coordenacaoAtiva != null && c.getId() != coordenacaoAtiva.getId())
						throw new NegocioException("Esta unidade Já possui " + c.getCargo().getDescricao() +" ativa :" + c.getPessoa().getNome());
					else if (!alteracao)
						throw new NegocioException("Esta unidade Já possui " + c.getCargo().getDescricao() +" ativa :" + c.getPessoa().getNome());
			}
	}
	
	/**
	 * Método auxiliar do execute(), para a realizar cadastro de secretarios.
	 * 
	 * @param mov
	 * @return
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void cadastrarSecretaria(Movimento mov) throws NegocioException, ArqException, RemoteException {

		GenericDAO dao = getGenericDAO(mov);

		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			
			CoordenadorUnidade secretario = (CoordenadorUnidade) movCad.getObjMovimentado();
			if(secretario.getId() == 0)
				criarPessoa(dao, movCad, secretario);
			for(Telefone t: secretario.getDados().getTelefones())
				dao.createOrUpdate(t);		
			dao.createOrUpdate( secretario );
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
	private void criarPessoa(GenericDAO dao, MovimentoCadastro movCad, CoordenadorUnidade secretario) throws DAOException, NegocioException, ArqException, RemoteException {
		secretario.getPessoa().setUltimaAtualizacao(new Date());
		
		ProcessadorPessoa.anularTransientObjects(secretario.getPessoa());
		ProcessadorPessoa.persistirContaBancaria(secretario.getPessoa(), dao);
		
		// chama o processador pessoa para o cadastro da pessoa
		ProcessadorPessoa pPessoa = new ProcessadorPessoa();
		PessoaMov movPessoa = new PessoaMov();
		movPessoa.setPessoa(secretario.getPessoa());
		movPessoa.setUsuarioLogado(movCad.getUsuarioLogado());
		movPessoa.setSistema( movCad.getSistema() );
		movPessoa.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
		
		// se a pessoa já existir chama alteração
		if ( secretario.getPessoa().getId() != 0 ) {
			movPessoa.setCodMovimento(SigaaListaComando.ALTERAR_PESSOA);
		} else {
			// Sincroniza para pegar o próximo idPessoa, para evitar assim que o Docente Externo seja cadastrado com o id = 0.
			movPessoa.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);
			int idPessoa = SincronizadorPessoas.getNextIdPessoa();
			secretario.getPessoa().setId(idPessoa);
		}
		Pessoa pessoa = (Pessoa) pPessoa.execute(movPessoa);
		if (pessoa != null)
			secretario.setPessoa(pessoa);
	}
}