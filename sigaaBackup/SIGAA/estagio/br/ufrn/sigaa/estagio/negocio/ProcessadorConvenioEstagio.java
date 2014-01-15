/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/09/2010
 */
package br.ufrn.sigaa.estagio.negocio;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.estagio.ConvenioEstagioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.util.PessoaHelper;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;
import br.ufrn.sigaa.estagio.dominio.ConcedentePessoaFuncao;
import br.ufrn.sigaa.estagio.dominio.ConvenioEstagio;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável pela a persistência dos dados relacionados 
 * ao cadastro de Convênios de Estágios.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorConvenioEstagio extends AbstractProcessador {

	/** Persiste os dados de um convênio de estágio.
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_CONVENIO_ESTAGIO)
				||mov.getCodMovimento().equals(SigaaListaComando.ATUALIZAR_CONVENIO_ESTAGIO)){
			gravarConvenioEstagio(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_CONVENIO_ESTAGIO)) {
			return analisarConvenioEstagio(mov);
		}
		return null;
	}

	/** Analista o convênio de estágio
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	private ConvenioEstagio analisarConvenioEstagio(Movimento mov) throws DAOException,
			ArqException {
		MovimentoCadastro movCadastro = ((MovimentoCadastro)mov);
		ConvenioEstagio convenio = movCadastro.getObjMovimentado();
		ConvenioEstagioDao dao = getDAO(ConvenioEstagioDao.class, mov);
		PessoaDao daoPessoa = getDAO(PessoaDao.class, mov);
		try {
			convenio.setDataAnalise(new Date());
			convenio.setRegistroAnalise(mov.getUsuarioLogado().getRegistroEntrada());		
			if (ValidatorUtil.isEmpty(convenio.getConcedente().getUnidade()))
				convenio.getConcedente().setUnidade(null);
			if (convenio.isRecusado()){
				convenio.getConcedente().setAtivo(false);
				convenio.getConcedente().setUnidade(null);
				convenio.getConcedente().setCodigoProjeto(null);
			} else if (convenio.isAprovado()){
				convenio.setMotivoAnalise(null);
				convenio.getConcedente().setAtivo(true);
			}
			// se existir arquivo digitalizado
			if (movCadastro.getObjAuxiliar() != null ) {
				// remove o anterior
				if (convenio.getIdArquivoTermoConvenio() != null) {
					EnvioArquivoHelper.removeArquivo(convenio.getIdArquivoTermoConvenio());
				}
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				UploadedFile arquivo = (UploadedFile) movCadastro.getObjAuxiliar() ;
				EnvioArquivoHelper.inserirArquivo(idArquivo,
						arquivo.getBytes(),
						arquivo.getContentType(),
						arquivo.getName());
				convenio.setIdArquivoTermoConvenio(idArquivo);
			}
			dao.update(convenio);
		} catch (IOException e) {
			throw new ArqException(e);
		} finally {			
			dao.close();
			daoPessoa.close();
		}		
		return convenio;
	}

	/** Grava ou atualiza os dados de um convênio de estágio.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void gravarConvenioEstagio(Movimento mov)
			throws NegocioException, ArqException, DAOException {
		/* Valida os dados */
		validate(mov);				
		
		ConvenioEstagio convenio = ((MovimentoCadastro)mov).getObjMovimentado();
		ConvenioEstagioDao dao = getDAO(ConvenioEstagioDao.class, mov);
		PessoaDao daoPessoa = getDAO(PessoaDao.class, mov);
		try {
			/* Cadastra a Empresa caso não existir */
			Pessoa pessoaDB = daoPessoa.findByCpf(convenio.getConcedente().getPessoa().getCpf_cnpj());	
			convenio.getConcedente().getPessoa().setTituloEleitor(null);
			convenio.getConcedente().getPessoa().setIdentidade(null);
			ProcessadorPessoa.anularTransientObjects(convenio.getConcedente().getPessoa());
			if (pessoaDB == null){
				int idPessoa = SincronizadorPessoas.getNextIdPessoa();					
				convenio.getConcedente().getPessoa().setId(idPessoa);
				convenio.getConcedente().getPessoa().setTipo(Pessoa.PESSOA_JURIDICA);
				convenio.getConcedente().getPessoa().setValido(true);
				
				/*  Sincroniza com o banco comum */
				SincronizadorPessoas.usandoDataSource(Database.getInstance().getComumDs()).sincronizarPessoa(convenio.getConcedente().getPessoa());						
				
				dao.create(convenio.getConcedente().getPessoa());					
			} else { // se existir atualiza dados
				boolean atualiza = false;
				// se não há endereço cadastrado
				Pessoa pessoa = convenio.getConcedente().getPessoa(); 
				if (pessoa.getEnderecoContato().getId() == 0) {
					Endereco endereco = pessoa.getEnderecoContato();
					dao.create(endereco);
					pessoaDB.setEnderecoContato(endereco);
					atualiza = true;
				} else {
					// caso contrário, atualiza o endereço
					dao.update(pessoa.getEnderecoContato());
				}
				// atualiza dados pessoais caso tenha alterado algum
				if (!(pessoa.getNome().equals(pessoaDB.getNome()) &&
						pessoa.getCodigoAreaNacionalTelefoneCelular() == pessoaDB.getCodigoAreaNacionalTelefoneCelular() &&
						pessoa.getCodigoAreaNacionalTelefoneFixo() == pessoaDB.getCodigoAreaNacionalTelefoneFixo() &&
						pessoa.getCelular().equals(pessoaDB.getCelular()) &&
						pessoa.getTelefone().equals(pessoaDB.getTelefone()))) {
					pessoaDB.setNome(pessoa.getNome());
					pessoaDB.setCodigoAreaNacionalTelefoneFixo(pessoa.getCodigoAreaNacionalTelefoneCelular());
					pessoaDB.setCodigoAreaNacionalTelefoneFixo(pessoa.getCodigoAreaNacionalTelefoneFixo());
					pessoaDB.setCelular(pessoa.getCelular());
					pessoaDB.setTelefone(pessoa.getTelefone());
					atualiza = true;
				}
				if (atualiza) {
					PessoaHelper.alteraCriaPessoa(pessoaDB, dao, ((MovimentoCadastro)mov), mov.getCodMovimento().getId());
					dao.update(pessoaDB);
					SincronizadorPessoas.usandoSistema(mov, Sistema.SIGAA).sincronizarEmailPessoa(pessoaDB);
					SincronizadorPessoas.usandoSistema(mov, Sistema.COMUM).sincronizarEmailPessoa(pessoaDB);	
				}
			}
			
			/* Cadastra o Responsável caso não existir */
			ConcedenteEstagioPessoa pep = (ConcedenteEstagioPessoa) ((MovimentoCadastro)mov).getObjAuxiliar();
			pessoaDB = daoPessoa.findByCpf(pep.getPessoa().getCpf_cnpj());
			if (pessoaDB == null){
				int idPessoa = SincronizadorPessoas.getNextIdPessoa();
				pep.getPessoa().setId(idPessoa);
				pep.getPessoa().setTipo(Pessoa.PESSOA_FISICA);
				pep.getPessoa().setValido(true);
				ProcessadorPessoa.anularTransientObjects(pep.getPessoa());
				
				/*  Sincroniza com o banco comum */
				SincronizadorPessoas.usandoDataSource(Database.getInstance().getComumDs()).sincronizarPessoa(pep.getPessoa());					
				pep.getPessoa().anularAtributosVazios();
				dao.create(pep.getPessoa());
			} else {//se existir atualiza dados pessoais caso tenha alterado algum
				Pessoa pessoa = pep.getPessoa();
				if (!(pessoa.getNome().equals(pessoaDB.getNome()) &&
						pessoa.getEmail().equals(pessoaDB.getEmail()) &&
						pessoa.getIdentidade().getNumero().equals(pessoaDB.getIdentidade().getNumero()) &&
						pessoa.getIdentidade().getOrgaoExpedicao().equals(pessoaDB.getIdentidade().getOrgaoExpedicao()) )) {
					pessoaDB.setNome(pessoa.getNome());
					pessoaDB.setEmail(pessoa.getEmail());
					pessoaDB.getIdentidade().setNumero(pessoa.getIdentidade().getNumero());
					pessoaDB.getIdentidade().setOrgaoExpedicao(pessoa.getIdentidade().getOrgaoExpedicao());
					
					PessoaHelper.alteraCriaPessoa(pessoaDB, dao, ((MovimentoCadastro)mov), mov.getCodMovimento().getId());
					dao.update(pessoaDB);
					SincronizadorPessoas.usandoSistema(mov, Sistema.SIGAA).sincronizarEmailPessoa(pessoaDB);
					SincronizadorPessoas.usandoSistema(mov, Sistema.COMUM).sincronizarEmailPessoa(pessoaDB);					
				}
				pep.setPessoa(pessoaDB);
			}
			
			/* Salva o Convênio de Estágio */
			convenio.getConcedente().setConvenioEstagio(convenio);
			
			dao.createOrUpdate(convenio);
			
			/* Salva o Responsável do Concedente de Estágio */
			if (pep.getId() == 0) {
				pep.setFuncao(new ConcedentePessoaFuncao( ConcedentePessoaFuncao.ADMINISTRADOR ));
				pep.setConcedente(convenio.getConcedente());
				dao.create(pep);
			} else {
				dao.update(pep);
			}
		} finally {			
			dao.close();
			daoPessoa.close();
		}
	}

	/** Valida os dados do convênio de estágio
	 * @param mov 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ConvenioEstagio convenio = ((MovimentoCadastro)mov).getObjMovimentado();
		ConcedenteEstagioPessoa pep = (ConcedenteEstagioPessoa) ((MovimentoCadastro)mov).getObjAuxiliar();
		ListaMensagens lista = new ListaMensagens();
		lista.addAll(convenio.validate());
		lista.addAll(pep.validate().getMensagens());
		checkValidation( lista );			
	}
	
}
