/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.RotuloTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Processador para cadastro de entidades no AVA. Faz
 * o registro das atividades na turma.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCadastroAva extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastroAva mov = (MovimentoCadastroAva) movimento;
		Specification specification = mov.getSpecification();
		PersistDB obj = mov.getObjMovimentado();
		Notification aviso = null;

		if (!specification.isSatisfiedBy(obj))
			return specification.getNotification();
		
		try {
			if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_AVA)) {
				aviso = (Notification) criar(mov);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.ATUALIZAR_AVA)) {
				alterar(mov);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_AVA)) {
				remover(mov);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.INATIVAR_AVA)) {
				inativar(mov);
			} 
			
		} catch(NegocioException e) {
			Notification n = new Notification();
			n.addError(e.getMessage());
			return n;
		}

		if (isEmpty(aviso))
			return new Notification();
		else 
			return aviso;

	}
	
	/**
	 * Altera um objeto da turma virtual.
	 * @param mov
	 * @throws DAOException
	 */
	private void alterar(final MovimentoCadastroAva mov) throws DAOException {
		operacao(mov, new OperacaoCadastro() {
			public void executar(TurmaVirtualDao dao, PersistDB obj) throws DAOException {
				
				if (obj instanceof AbstractMaterialTurma) {
					((AbstractMaterialTurma) obj).getMaterial().setTopicoAula(((AbstractMaterialTurma) obj).getAula()); 
				}
				
				dao.update(obj);
				
				if (obj instanceof AbstractMaterialTurma) {
					MaterialTurmaHelper.atualizarMaterial(dao, (AbstractMaterialTurma) obj, false);
				}
				
			}
		});
	}
	
	/**
	 * Inativa um objeto da turma virtual.
	 * @param mov
	 * @throws DAOException
	 */
	private void inativar (final MovimentoCadastroAva mov) throws DAOException {
		operacao(mov, new OperacaoCadastro() {
			public void executar(TurmaVirtualDao dao, PersistDB obj) throws DAOException {
				dao.updateField(obj.getClass(), obj.getId(), "ativo", false);
				
				if (obj instanceof AbstractMaterialTurma) {
					obj = dao.findByPrimaryKey(obj.getId(), obj.getClass(), "id", "material.id", "aula.id");
					MaterialTurma m = ((AbstractMaterialTurma) obj).getMaterial();
					dao.updateField(MaterialTurma.class, m.getId(), "ativo", false);
					
					TopicoAula topicoAula = ((AbstractMaterialTurma) obj).getAula();
					MaterialTurmaHelper.reOrdenarMateriais(topicoAula);
				}
			}
		});
	}

	/**
	 * Cria um Objeto da turma virtual.
	 * @param mov
	 * @return Object (mensagem caso o cadastro que deve ser feito em várias turmas seja feito parcialmente)
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private Object criar(final MovimentoCadastroAva mov) throws DAOException, NegocioException {
		
		if (mov.isCadastrarEmVariasTurmas()) {
			
			boolean isTurmaSemTopicoEquivalente;
			List<Turma> turmasSemTopicoEquivalente = new ArrayList<Turma>();
			List<Turma> turmasSemTopicoPaiEquivalente = new ArrayList<Turma>();
			List<Turma> turmas = new ArrayList<Turma>();
			List<Integer> idsTurmas = new ArrayList<Integer>();
			Notification info = new Notification();
			String topicoErro = new String();
			
			if (isEmpty(mov.getCadastrarEm()))
				throw new NegocioException("É necessário selecionar ao menos uma turma para realizar o cadastro.");
			
			//transforma o vetor de ids das turmas no formato String para formato Integer  
			for (String tid : mov.getCadastrarEm()) {
					idsTurmas.add(Integer.valueOf(tid));			
			}
			
			turmas = getDAO(TurmaDao.class, mov).findByPrimaryKeyOtimizado(idsTurmas);
			TopicoAulaDao dao = getDAO(TopicoAulaDao.class, mov);			
			try {
					for(Turma t : turmas) {
					
						isTurmaSemTopicoEquivalente = false;
						final MovimentoCadastroAva mCopy = new MovimentoCadastroAva();
						
						try {
							BeanUtils.copyProperties(mCopy, mov);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						mCopy.setTurma(t);
						DominioTurmaVirtual dom = mCopy.getObjMovimentado();
						dom.setTurma(t);
		
						// Se o objeto for de um tipo que possa ser associado a uma aula,
						// TODO adicionar novos itens de aulas aqui.
						if (dom instanceof IndicacaoReferencia || dom instanceof ConteudoTurma || dom instanceof TarefaTurma || dom instanceof RotuloTurma) { 
							try {
								ReflectionUtils.setFieldValue(dom, "aula", getTopicoEquivalente(t, dom, mov));
							} catch (NegocioException e) {
								topicoErro = e.getMessage();
								turmasSemTopicoEquivalente.add(t);
								isTurmaSemTopicoEquivalente = true;
							}
						// Se o objeto for uma aula
						} else if (dom instanceof TopicoAula){
							
							
							TopicoAula topico = (TopicoAula) dom;
							if (!isEmpty(topico.getTopicoPai())) {
								TopicoAula pai = dao.findByPrimaryKey(topico.getTopicoPai().getId(), TopicoAula.class); 
								pai = dao.findByDescricao(t, pai.getDescricao());
								if (pai == null) {
									turmasSemTopicoPaiEquivalente.add(t);
									isTurmaSemTopicoEquivalente = true;
								}
								else
								topico.setTopicoPai(pai);
							}
						}
						if (!isTurmaSemTopicoEquivalente) {
							cadastrarParaVariasTurmas(mCopy);
							info.adicionarTurmaSucesso(t);
						}
					}
			} finally {
				if (dao != null)
					dao.close();
			}
			
			return mensagensCadastro(mov, turmasSemTopicoEquivalente,turmasSemTopicoPaiEquivalente, topicoErro,info);
			
		} else {
			cadastrarParaUnicaTurma(mov);
		}
		return null;
	}

	private Notification mensagensCadastro(final MovimentoCadastroAva mov,
			List<Turma> turmasSemTopicoEquivalente,
			List<Turma> turmasSemTopicoPaiEquivalente, String topicoErro,
			Notification info)
			throws NegocioException {
		//caso exista uma pelo menos uma turma em que o topico onde nao existe o topico equivalente
		if (!turmasSemTopicoEquivalente.isEmpty()) {
			
			if (turmasSemTopicoEquivalente.size() != mov.getCadastrarEm().size())
				info.addWarning("O cadastro foi efetuado com sucesso em turmas com o tópico "+topicoErro+". ");

			info.addWarning("Não foi possível cadastrar na(s) turma(s): ");
			for (Turma t : turmasSemTopicoEquivalente) 
				info.addWarning(t.getNome()+" ");
			
			info.addWarning("Porque não existe o tópico de aula selecionado nessa(s) turma(s) cadastrado.");
			
			//Caso não tenho sido cadastrado em pelo menos uma turma lanca a excecao.
			if (turmasSemTopicoEquivalente.size() == mov.getCadastrarEm().size())
				throw new NegocioException(info.getErrorString());
			
			//Caso o cadastro tenha sido feito em pelo menos uma turma.
			return info;
		  //Caso seja um cadastro de topico de aula pelo menos em uma turma o topoci nao foi cadastrado.
		} else if (!turmasSemTopicoPaiEquivalente.isEmpty()) {
			if (turmasSemTopicoPaiEquivalente.size() != mov.getCadastrarEm().size())
				info.addWarning("Apesar do cadastro ter sido efetuado com sucesso em turmas que possuem o tópico pai selecionado. ");

			info.addWarning("Não é possível cadastrar na(s) turma(s): ");
			for (Turma t : turmasSemTopicoPaiEquivalente) 
				info.addWarning(t.getNome()+" ");
			
				info.addWarning("Porque não existe o tópico de aula pai selecionado nessa(s) turma(s).");
			
			//Caso não tenho sido cadastrado em pelo menos uma turma lanca a excecao.
			if (turmasSemTopicoPaiEquivalente.size() == mov.getCadastrarEm().size())
				throw new NegocioException(info.getErrorString());
			
			//Caso o cadastro tenha sido feito em pelo menos uma turma.
			return info;

		}
		return info;
	}

	private void cadastrarParaUnicaTurma(final MovimentoCadastroAva mov)
			throws DAOException {
		operacao(mov, new OperacaoCadastro() {
			public void executar(TurmaVirtualDao dao, PersistDB obj) throws DAOException {
				TopicoAula topicoAula = null;
				if (obj instanceof AbstractMaterialTurma) {
					topicoAula = ((AbstractMaterialTurma) obj).getAula();
					topicoAula = dao.findByPrimaryKey(topicoAula.getId(), TopicoAula.class);
					MaterialTurmaHelper.definirNovoMaterialParaTopico((AbstractMaterialTurma) obj, topicoAula, mov.getTurma());
				}
				
				dao.create(obj);
				
				if (obj instanceof AbstractMaterialTurma) 
					MaterialTurmaHelper.atualizarMaterial(dao, (AbstractMaterialTurma) obj, true);
			}
		});
	}

	private void cadastrarParaVariasTurmas(final MovimentoCadastroAva mCopy)
			throws DAOException {
		operacao(mCopy, new OperacaoCadastro() {
			public void executar(TurmaVirtualDao dao, PersistDB obj) throws DAOException {
				obj.setId(0);
				TopicoAula topicoAula = null;
				if (obj instanceof AbstractMaterialTurma) {
					topicoAula = ((AbstractMaterialTurma) obj).getAula();
					Turma turma = (Turma) ReflectionUtils.getFieldValue(obj, "turma");
					//mudar o id do material para 0 para cadastrar no banco ids distintos.
					((AbstractMaterialTurma) obj).getMaterial().setId(0);
					if (ValidatorUtil.isNotEmpty(topicoAula)) {
						topicoAula = dao.findByPrimaryKey(topicoAula.getId(), TopicoAula.class);
					}
					MaterialTurmaHelper.definirNovoMaterialParaTopico((AbstractMaterialTurma) obj, topicoAula, turma);
				}
				
				dao.create(obj);

				if (obj instanceof AbstractMaterialTurma) 
					MaterialTurmaHelper.atualizarMaterial(dao, (AbstractMaterialTurma) obj, true);
			}
		});
	}
	
	/**
	 * Pega o TopicoAula equivalente ao um tópco passado caso exista algum na turma t.
	 * @param t
	 * @param dom
	 * @param mov
	 * @return tópico equivalente encontrado na Turma t
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private TopicoAula getTopicoEquivalente(Turma t, DominioTurmaVirtual dom, MovimentoCadastroAva mov) throws DAOException, NegocioException {
		TopicoAulaDao dao = getDAO(TopicoAulaDao.class, mov);
		TopicoAula aula = (TopicoAula) ReflectionUtils.getFieldValue(dom, "aula");
		
		if (aula != null) {
			aula = dao.findByPrimaryKey(aula.getId(), TopicoAula.class);
			TopicoAula equivalente = dao.findByDescricao(t, aula.getDescricao());
		if (equivalente == null)
			throw new NegocioException(aula.getDescricao());
			
		return equivalente;
		}
		else {
			return null;
		}
		
	}

	/**
	 * Remove um objeto da turma virtual relacionado ao movimento passado.
	 * @param mov
	 * @throws DAOException
	 */
	protected void remover(final MovimentoCadastroAva mov) throws DAOException {		
		if(ReflectionUtils.propertyExists(mov.getObjMovimentado().getClass(), "ativo")) {
			inativar(mov);
		} else {		
			operacao(mov, new OperacaoCadastro() {
				public void executar(TurmaVirtualDao dao, PersistDB obj) throws DAOException {
					dao.remove(obj);				
					if (obj instanceof AbstractMaterialTurma) {
						TopicoAula topicoAula = ((AbstractMaterialTurma) obj).getAula();
						MaterialTurmaHelper.reOrdenarMateriais(topicoAula);
					}
				}
			});
		}
	}
	
	/**
	 * Executa uma operacao generica na turma virtual.
	 * @param mov
	 * @param operacao
	 * @throws DAOException
	 */
	private void operacao(MovimentoCadastroAva mov, OperacaoCadastro operacao) throws DAOException {
		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class, mov);
		
		try {
			operacao.executar(dao, mov.getObjMovimentado());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Interface para a operação de cadastro 
	 *
	 */
	private interface OperacaoCadastro {
		/**
		 * Executa uma operacao. 
		 * @param dao
		 * @param obj
		 * @throws DAOException
		 */
		void executar(TurmaVirtualDao dao, PersistDB obj) throws DAOException;
	}

	/**
	 * Valida um movimento.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	
}
