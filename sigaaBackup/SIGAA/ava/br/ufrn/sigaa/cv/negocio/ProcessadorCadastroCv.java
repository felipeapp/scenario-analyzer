/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.cv.dominio.RegistroAtividadeComunidade;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processador para cadastro de entidades na CV. Faz
 * o registro das atividades na turma.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCadastroCv extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastroCv mov = (MovimentoCadastroCv) movimento;
		Specification specification = mov.getSpecification();
		PersistDB obj = mov.getObjMovimentado();

		if (!specification.isSatisfiedBy(obj))
			return specification.getNotification();
		
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_CV)) {
			criar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ATUALIZAR_CV)) {
			alterar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_CV)) {
			remover(mov);
		} 

		return new Notification();
	}
	
	/**
	 * Atualiza o objeto da comunidade virtual.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void alterar (final MovimentoCadastroCv mov) throws DAOException {
		operacao(mov, new OperacaoCadastro() {
			public void executar(ComunidadeVirtualDao dao, PersistDB obj) throws DAOException {
				dao.update(obj);				
			}
		});
	}

	/**
	 * Cadastra o objeto da comunidade virtual.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void criar(final MovimentoCadastroCv mov) throws DAOException {
		operacao(mov, new OperacaoCadastro() {
			public void executar(ComunidadeVirtualDao dao, PersistDB obj) throws DAOException {
				if ( obj instanceof TopicoComunidade ) 
					TopicoComunidadeHelper.reOrdenarTopicosParaInsercao((TopicoComunidade) obj, mov.getComunidade());

				dao.create(obj);	
								
				if (mov.getMensagem() != null) {
					RegistroAtividadeComunidade reg = new RegistroAtividadeComunidade();
					reg.setData(new Date());
					reg.setComunidade(mov.getComunidade());
					reg.setUsuario((Usuario) mov.getUsuarioLogado());
					reg.setDescricao(mov.getMensagem());
					
					dao.create(reg);
				}
			}
		});
	}
	
	/**
	 * Remove o objeto da comunidade virtual.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void remover(final MovimentoCadastroCv mov) throws DAOException {
		operacao(mov, new OperacaoCadastro() {
			public void executar(ComunidadeVirtualDao dao, PersistDB obj) throws DAOException {
		
				if ( obj instanceof TopicoComunidade ) 
					TopicoComunidadeHelper.reOrdenarTopicosParaRemocao((TopicoComunidade) obj, mov.getComunidade());
				
				dao.remove(obj);					
			}
		});
	}
	
	/**
	 * Realiza a operação passada para o movimento passado.
	 * 
	 * @param mov
	 * @param operacao
	 * @throws DAOException
	 */
	private void operacao(MovimentoCadastroCv mov, OperacaoCadastro operacao) throws DAOException {
		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class, mov);
		
		try {
			operacao.executar(dao, mov.getObjMovimentado());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Interface que auxilia na realização de operações de persistência para objetos da comunidade virtual.
	 * 
	 * @author Administrador
	 *
	 */
	private interface OperacaoCadastro {
		void executar(ComunidadeVirtualDao dao, PersistDB obj) throws DAOException;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	
}
