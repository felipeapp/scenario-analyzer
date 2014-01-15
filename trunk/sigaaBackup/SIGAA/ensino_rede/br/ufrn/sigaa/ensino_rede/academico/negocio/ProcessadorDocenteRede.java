package br.ufrn.sigaa.ensino_rede.academico.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class ProcessadorDocenteRede extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException,	ArqException, RemoteException {
		
		cadastrarDocente(movimento);
		
		return null;

	}
	
	private void cadastrarDocente(Movimento mov) throws NegocioException, ArqException, RemoteException {

		GenericDAO dao = getGenericDAO(mov);

		try {
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			
			DocenteRede docente = (DocenteRede) movCad.getObjMovimentado();
			
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
}
