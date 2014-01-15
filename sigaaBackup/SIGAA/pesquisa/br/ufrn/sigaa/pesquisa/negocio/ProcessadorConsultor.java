/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultorDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador responsável pelo cadastro de consultores
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorConsultor extends AbstractProcessador {

	/**
	 * Método responsável pela execução do processador Consultor
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);

		validate(mov);

		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;
		Consultor consultor = (Consultor) movCadastro.getObjMovimentado();

		UsuarioDao usuarioDao = getDAO(UsuarioDao.class, mov);

		try {
			// Buscar usuário do servidor
			Usuario usuario = null;
			if ( consultor.getServidor().getId() > 0 ) {
				Servidor servidor = usuarioDao.findByPrimaryKey(consultor.getServidor().getId() , Servidor.class);
				usuario = usuarioDao.findByServidor( servidor );
			}

			// Se o usuário não existir, enviar erro ao usuário
			if ( usuario == null && consultor.isInterno() ) {
				NegocioException e = new NegocioException();
				e.addErro("O docente informado ainda não possui um usuário cadastrado no sistema. " +
						" De modo a cadastrar sua participação como consultor é necessário que o mesmo efetue seu cadastro.");
				throw e;
			}

			if ( !consultor.isInterno() ) {
				consultor.setServidor(null);
			}
			
			// Persistir consultor
			if ( consultor.getId() <= 0 ) {
				consultor.setCodigo( usuarioDao.getNextSeq("pesquisa.codigo_acesso_consultor") );
				usuarioDao.create( consultor );
			} else {
				if (consultor.getCodigo() == null) {
					consultor.setCodigo( usuarioDao.getNextSeq("pesquisa.codigo_acesso_consultor") );
				}

				usuarioDao.update( consultor );
			}

			//Associar usuário do servidor ao consultor
			if (usuario != null) {
				usuario.setConsultor( consultor );
				usuarioDao.update( usuario);
			}

		} finally {
			usuarioDao.close();
		}

		return consultor;
	}

	/**
	 * Responsável pela validação do processador Consultor
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		// Validar campos
		ListaMensagens lista = new ListaMensagens();

		// Validar se já existe um consultor cadastrado para o servidor informado
		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;
		Consultor consultor = (Consultor) movCadastro.getObjMovimentado();

		ConsultorDao consultorDao = getDAO( ConsultorDao.class , mov);

		lista = consultor.validate();
		if ( !consultorDao.findByExactField(Consultor.class, "servidor.id", consultor.getServidor().getId()).isEmpty() ) {
			lista.addErro("Este servidor já foi cadastrado como consultor");
		}
		checkValidation(lista);
		
	}

}
