/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 5, 2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pessoa.dominio.TipoDocenteExterno;

/**
 * Processador para realizar as operações sobre DocenteExterno
 * @author Victor Hugo
 *
 */
public class ProcessadorDocenteExterno extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate( mov );

		if( mov.getCodMovimento() == SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO ){
			cadastrarDocenteExterno(mov);
		} if( mov.getCodMovimento() == SigaaListaComando.ALTERAR_DOCENTE_EXTERNO ){
			alterarDocenteExterno(mov);
		} else if( mov.getCodMovimento() == SigaaListaComando.REMOVER_DOCENTE_EXTERNO ){
			removerDocenteExterno(mov);
		}

		return null;
	}

	/**
	 * Cadastra um docente externo
	 * @param mov
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	private void cadastrarDocenteExterno(Movimento mov) throws NegocioException, ArqException, RemoteException {

		GenericDAO dao = getGenericDAO(mov);

		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			
			DocenteExterno docente = (DocenteExterno) movCad.getObjMovimentado();
			
			//setando atributos default
			docente.setDataCadastro(new Date());
			docente.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			
			if(movCad.getAcao() == MovimentoCadastroDocenteExterno.ACAO_NAO_GERAR_MATRICULA)
				docente.setMatricula(null);
			else
				docente.setMatricula( gerarMatricula(docente, movCad) );
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
			
			dao.create( docente );
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Altera um docente externo
	 * @param mov
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	private void alterarDocenteExterno(Movimento mov) throws NegocioException, ArqException, RemoteException {

		GenericDAO dao = getGenericDAO(mov);

		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			
			DocenteExterno docente = (DocenteExterno) movCad.getObjMovimentado();
			
			Pessoa p = docente.getPessoa();
			p.setUltimaAtualizacao(new Date());
			Pessoa pBD = dao.findAndFetch(p.getId(), Pessoa.class, "contaBancaria", "enderecoContato");
			if ( !isEmpty( pBD ) ) {
				if ( !isEmpty( pBD.getContaBancaria() ) )
					p.setContaBancaria(pBD.getContaBancaria());
				if ( !isEmpty( pBD.getEnderecoContato() ) )
					p.setEnderecoContato(pBD.getEnderecoContato());
			}
			ProcessadorPessoa.anularTransientObjects(p);
			ProcessadorPessoa.persistirContaBancaria(docente.getPessoa(), dao);
			// chama o processador pessoa para o cadastro da pessoa
			ProcessadorPessoa pPessoa = new ProcessadorPessoa();
			PessoaMov movPessoa = new PessoaMov();
			movPessoa.setPessoa(docente.getPessoa());
			// chama alteração de pessoa
			movPessoa.setUsuarioLogado(movCad.getUsuarioLogado());
			movPessoa.setSistema( movCad.getSistema() );
			movPessoa.setCodMovimento(SigaaListaComando.ALTERAR_PESSOA);
			movPessoa.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
			Pessoa pessoa = (Pessoa) pPessoa.execute(movPessoa);
			docente.setPessoa(pessoa);
			
			dao.update( docente );
		} finally {
			dao.close();
		}
	}

	/**
	 * Gera matrícula para docente externo
	 * @param docente
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private String gerarMatricula(DocenteExterno docente, MovimentoCadastro mov) throws DAOException, NegocioException {

		if( docente.getTipoDocenteExterno() == null || docente.getTipoDocenteExterno().getId() <= 0 ){
			throw new NegocioException("É necessário que o tipo do docente externo esteja setado para cadastrá-lo.");
		}

		DocenteExternoDao dao = getDAO(DocenteExternoDao.class, mov);

		try {
			StringBuffer matricula = new StringBuffer();
			matricula.append(docente.getTipoDocenteExterno().getId());
			
			TipoDocenteExterno tipo = dao.findByPrimaryKey(docente.getTipoDocenteExterno().getId(), TipoDocenteExterno.class);
			tipo.setSequencia( tipo.getSequencia() + 1 );
			
			int sequencia = tipo.getSequencia();
			
			matricula.append( UFRNUtils.completaZeros( sequencia, 3) );
			
			dao.updateField(TipoDocenteExterno.class, tipo.getId(), "sequencia", tipo.getSequencia());
			
			return matricula.toString();
		} finally {
			dao.close();
		}
	}

	public void validate(Movimento mov) throws NegocioException, DAOException{

		if( mov.getCodMovimento() == SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO ){
			vadalidaCadastroDocente(mov);
		}

	}

	/**
	 * Valida o cadastro do docente externo
	 * @param mov
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private void vadalidaCadastroDocente(Movimento mov) throws NegocioException, DAOException {

		MovimentoCadastro movCad = (MovimentoCadastro) mov;

		DocenteExterno docente = (DocenteExterno) movCad.getObjMovimentado();
		ListaMensagens msgs = docente.validate();
		checkValidation( msgs );

		/**
		 * verificando se já possui um docente externo cadastrado com esta pessoa
		 */
		DocenteExternoDao dao = getDAO(DocenteExternoDao.class, mov);
		ServidorDao daoServidor = getDAO(ServidorDao.class, mov);
		
		try {
			if( docente.getPessoa().getId() != 0 ){
				DocenteExterno outroDocente = dao.findAtivoByPessoaUnidadeTipo(docente.getPessoa(), docente.getUnidade(), docente.getTipoDocenteExterno());
				if( outroDocente != null && outroDocente.getId() != 0 ){
					throw new NegocioException("Já existe um docente externo cadastrado para a pessoa e unidade informada.");
				}
				
				outroDocente = dao.findByPessoa(docente.getPessoa(), docente.getTipoDocenteExterno().getId(), docente.getUnidade());
				if( outroDocente != null && outroDocente.getId() != 0 && outroDocente.getNivel() != null && docente.getNivel() != null && outroDocente.getNivel() == docente.getNivel()){
					throw new NegocioException("Já existe um docente externo cadastrado para a pessoa informada com a mesma categoria e o mesmo nível de ensino.");
				}
				
				if(!docente.getTipoDocenteExterno().isPermiteServidor()) {
					/** Não deixa cadastrar se já existir um servidor associado com a pessoa informada */
					Collection<Servidor> servidores =  daoServidor.findAtivoByPessoa( docente.getPessoa().getId() );
					if( servidores != null && !servidores.isEmpty()  ){
						for( Servidor s : servidores ){
							if( s.getCategoria().getId() == Categoria.DOCENTE ){
								throw new NegocioException("Existe um docente associado com esta pessoa.");
							}
						}
					}
				}
				
			}
		} finally {
			dao.close();
			daoServidor.close();
		}
	}

	/**
	 * Remove o docente externo
	 * @param mov
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void removerDocenteExterno(Movimento mov) throws SegurancaException, ArqException, NegocioException {

		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		DocenteExterno docente = (DocenteExterno) movCad.getObjMovimentado();

		checkRole(new int[]{SigaaPapeis.DAE, SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_DAE,SigaaPapeis.GESTOR_LATO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.GESTOR_INFANTIL, SigaaPapeis.GESTOR_TECNICO}, mov);

		GenericDAO dao =  getGenericDAO(mov);
		docente = dao.refresh(docente);
		
		try {
			if( mov.getUsuarioLogado().isUserInRole( SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE ) && !isEmpty( docente.getUnidade() ) 
					&& !docente.getUnidade().isDepartamento() && !docente.getUnidade().isUnidadeAcademicaEspecializada()){
				throw new NegocioException( "Você não tem permissão para remover este docente externo pois ele não está associado a um departamento " +
				"ou a uma unidade acadêmica especializada." );
			}
			
			if( mov.getUsuarioLogado().isUserInRole( SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) && !isEmpty( docente.getUnidade() ) && !docente.getUnidade().isPrograma() ){
				throw new NegocioException( "Você não tem permissão para remover este docente externo pois ele não está associado a um programa de pós-graduação" );
			}
			
			docente.setAtivo(false);
			docente.setPrazoValidade(new Date());
			
			dao.update(docente);
		} finally {
			dao.close();
		}
	}
}