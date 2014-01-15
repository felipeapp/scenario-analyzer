package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioExternoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioExternoBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador que trata do cadastro de usu�rios externos da biblioteca.<br>
 *
 *
 * @author Fred_Castro
 *
 */
public class ProcessadorUsuarioExternoBiblioteca extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro personalMov = (MovimentoCadastro) mov;
		
		UsuarioExternoBibliotecaDao dao = null;
		
		
		try {
			dao = getDAO(UsuarioExternoBibliotecaDao.class, personalMov);
			
			
			validate(personalMov);
			
			UsuarioExternoBiblioteca uExt = (UsuarioExternoBiblioteca) personalMov.getObjMovimentado();

			
			Pessoa p = uExt.getUsuarioBiblioteca().getPessoa();
			ProcessadorPessoa.anularTransientObjects(p);
			p.anularAtributosVazios();

			
			if(p.getId() == 0){ // Pega o id da pessoa que ser� criado  //
				int idPessoa = SincronizadorPessoas.getNextIdPessoa();
				p.setId(idPessoa);
				
				/**   Sincroniza com o banco comum */
				SincronizadorPessoas.usandoDataSource(Database.getInstance().getComumDs()).sincronizarPessoa(p);
				
				if(p.getContaBancaria() != null)
					dao.createOrUpdate(p.getContaBancaria());
				
				dao.create(p);
				
			}else{
				if(p.getContaBancaria() != null)
					dao.createOrUpdate(p.getContaBancaria());
				
				dao.update(p); // Cria a pessoa apenas no banco do SIGAA e n�o sincroniza sen�o vai dar problema nos ids, porque os bancos usam id diferente.
			}
			
			// S� pode setar para nulo aqui no final, se�o dar erro
			if (uExt.getUnidade().getId() <= 0)
				uExt.setUnidade(null);
			
			if (uExt.getConvenio().getId() <= 0)
				uExt.setConvenio(null);
			
			if( uExt.getId() == 0 ){ // Est� criando
				
				UsuarioBiblioteca ub = uExt.getUsuarioBiblioteca();
				ub.setVinculo(VinculoUsuarioBiblioteca.USUARIO_EXTERNO);
				dao.create(ub);
				
				dao.create(uExt);
				
				dao.updateField(UsuarioBiblioteca.class, ub.getId(), "identificacaoVinculo", uExt.getId()  );
				
			}else{  // Est� atualizando o v�nculo de usu�rio externo
				UsuarioBiblioteca ub = uExt.getUsuarioBiblioteca();
				dao.updateField(UsuarioBiblioteca.class, ub.getId(), "senha", ub.getSenha());
		
				
				uExt.setCancelado(false); //  Se o usu�rio est� atualizando um usu�rio externo ele deve ser "re-ativado"
				uExt.setMotivoCancelamento(null);
				uExt.setDataCancelamento(null);
				uExt.setRegistroEntradaCancelamento(null);
				
				
				dao.update(uExt);
			}
			
		} finally {
			if (dao != null) dao.close();
		}
		
		return null;
		
	}

	
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		UsuarioExternoBibliotecaDao daoUsuarioExterno = null;
		try{
		
			MovimentoCadastro movimento = (MovimentoCadastro) mov;
	
			daoUsuarioExterno = getDAO(UsuarioExternoBibliotecaDao.class, movimento);
			
			
			UsuarioExternoBiblioteca uExt = (UsuarioExternoBiblioteca) movimento.getObjMovimentado();
	
			if(uExt == null)
				erros.addErro("O usu�rio externo n�o foi informado ");  
			else{
				if(uExt.getUsuarioBiblioteca() == null)
					erros.addErro("O usu�rio externo n�o est� associado a um usu�rio ");  
				else{
					erros.addAll(uExt.validate());                          // valida o usu�rio externo
					erros.addAll(uExt.getUsuarioBiblioteca().validate());   // valida o usu�rio do usu�rio externo
					
					VerificaSituacaoUsuarioBibliotecaUtil.verificaDadosPessoaCorretosUtilizarBiblioteca(uExt.getUsuarioBiblioteca().getPessoa());
					
					if(uExt.getConvenio() != null && uExt.getConvenio().getId() > 0){
						uExt.setConvenio(daoUsuarioExterno.refresh( uExt.getConvenio()));
						if(CalendarUtils.compareTo(uExt.getConvenio().getDataInicio(), new Date()) > 0){
							erros.addErro("A data de validade do conv�nio do usu�rio expirou. ");
						}
						if(CalendarUtils.compareTo(uExt.getConvenio().getDataFim(), new Date()) < 0 ){
							erros.addErro("O prazo do conv�nio do usu�rio ainda n�o come�ou ");
						}
					}
					
					if( uExt.getId() == 0 ){ // Est� criando
						
						if(uExt.getUsuarioBiblioteca().getVinculo() == null || uExt.getUsuarioBiblioteca().getVinculo() != VinculoUsuarioBiblioteca.USUARIO_EXTERNO)
							erros.addErro("A informa��o do v�nculo do usu�rio est� errada, o v�nculo do usu�rio precisaria ser "+VinculoUsuarioBiblioteca.USUARIO_EXTERNO.getDescricao());
						
					}else{   // est� atualizando
						
						// Verifica se o usu�rio tem outras contas n�o quitadas 
						UsuarioBiblioteca usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(uExt.getUsuarioBiblioteca().getPessoa().getId(), null);
						
						if(usuarioBiblioteca != null){
							if( usuarioBiblioteca.getId() != uExt.getUsuarioBiblioteca().getId()){
								erros.addErro("Usu�rio j� possui outra conta na biblioteca, por isso a conta para o v�nculo de "+VinculoUsuarioBiblioteca.USUARIO_EXTERNO.getDescricao()+" n�o pode ser atualizada");
							}
						}
					}
					
					
				}
			}
			
		}finally{
			checkValidation(erros);
			if(daoUsuarioExterno != null) daoUsuarioExterno.close();
		}
	}

	
	
	
	
}
