/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 11/04/2008
 */
package br.ufrn.sigaa.ava.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TarefaTurmaDao;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.dominio.TipoMaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Processador para cadastrar tarefas em uma turma
 * e integrá-la com a consolidação.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorTarefaTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		if (mov.getCodMovimento().equals(SigaaListaComando.MARCAR_TAREFA_LIDA)) {
			getDAO(TarefaTurmaDao.class, mov).marcarTarefaLida((RespostaTarefaTurma) ((MovimentoCadastro) mov).getObjMovimentado());
			return null;
		} else if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_TAREFA)) {
			return cadastrarTarefa(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_TAREFA)) {
			return removerTarefa(mov);
		} else {
			return alterarTarefa(mov);
		}
	}

	/**
	 * Método auxiliar do excute(), para a realização do cadastro de uma tarefa.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private Object cadastrarTarefa(Movimento mov) throws DAOException, ArqException, NegocioException {
		MovimentoCadastroAva cMov = (MovimentoCadastroAva) mov;
		TurmaDao dao = null;
		
		try {
			dao = getDAO(TurmaDao.class, mov);
	
			Specification specification = cMov.getSpecification();
			TarefaTurma tarefa = cMov.getObjMovimentado();
			
				if (!specification.isSatisfiedBy(tarefa)){
					Notification n = specification.getNotification();
					if ( n != null && n.hasMessages() )
						throw new NegocioException(n.getMessages());
					return specification.getNotification();
				}	
				
				if (isEmpty(cMov.getCadastrarEm())) {
					throw new NegocioException("É necessário selecionar ao menos uma turma para realizar o cadastro.");
				}
				
				cadastrarArquivo(tarefa);
				
				List <Turma> turmasSucesso = new ArrayList <Turma> ();
				
				for (String tid : cMov.getCadastrarEm()) {
					Turma t = dao.findByPrimaryKeyOtimizado(Integer.valueOf(tid));
	
					TarefaTurma tCopy = new TarefaTurma();
					try { 
						Double notaMaxima = tarefa.getNotaMaxima();
						BeanUtils.copyProperties(tCopy, tarefa);
						tCopy.setNotaMaxima(notaMaxima);
					} catch (Exception e) {
						throw new ArqException(e);
					}
					
					// Comentado porque o método setTurma(Turma) não está implementado na classe TarefaTurma
//					tCopy.setTurma(t);
					
					tCopy.setAula(getTopicoEquivalente(t, tarefa, cMov));
					tCopy.setId(0);
					tCopy.setMaterial(new MaterialTurma(TipoMaterialTurma.TAREFA));
					MaterialTurmaHelper.definirNovoMaterialParaTopico(tCopy, tCopy.getAula(), t);
					dao.create(tCopy);
					MaterialTurmaHelper.atualizarMaterial(dao, tCopy, true);
					
					// Passa o id do objeto criado para o passado por parâmetro para ser acessível no MBean.
					tarefa.setId(tCopy.getId());
					
					// Se a tarefa tiver nota, cadastrar avaliações para cada discente
					// para realizar integração com a consolidação
					if (tarefa.isPossuiNota()) {
						
						Collection<NotaUnidade> notas = null;
						
						if (!t.isAgrupadora())
							notas = dao.findNotasByTurmaUnidade(t.getId(), tCopy.getUnidade());
						else
							notas = dao.findNotasByTurmaAgrupadoraUnidade(t.getId(), tCopy.getUnidade());
							
						if (!isEmpty(notas)) {
							for (NotaUnidade nota : notas) {
								Avaliacao avaliacao = AvaliacaoHelper.criarAvaliacao(tCopy);
								avaliacao.setUnidade(nota);
								dao.create(avaliacao);
							}
						}
					}
					
					turmasSucesso.add(t);
				}
			
			return turmasSucesso;
		}finally {
			if (dao != null)
				dao.close();
		}	
	}
	
	/**
	 * Método auxiliar do excute(), para a realização da remoção de uma tarefa.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private Object removerTarefa(final Movimento mov) throws DAOException {

		TurmaDao dao = null;
		AvaliacaoDao aDao = null;
		
		try {
			dao = getDAO(TurmaDao.class, mov);
			aDao = getDAO(AvaliacaoDao.class, mov);
			
			MovimentoCadastroAva cMov = (MovimentoCadastroAva) mov;
			cMov.setMensagem("Tarefa Removida");
			Specification specification = cMov.getSpecification();
			TarefaTurma tarefa = cMov.getObjMovimentado();
					
			if (!specification.isSatisfiedBy(tarefa))
				return specification.getNotification();
			
			if (tarefa.isPossuiNota() ) {
	
				ArrayList<Avaliacao> avaliacoes = (ArrayList<Avaliacao>) dao.findByExactField(Avaliacao.class, "atividadeQueGerou.id", tarefa.getId());	
				
				if ( avaliacoes != null && !avaliacoes.isEmpty()){
					boolean possuiMaisDeUmaAvaliacao =  aDao.isMaisDeUmaAvaliacao(avaliacoes.get(0).getId() , avaliacoes.get(0).getUnidade().getId());
					
					for ( Avaliacao a : avaliacoes ) {
						if ( !possuiMaisDeUmaAvaliacao && a.getUnidade().getMatricula().isMatriculado() )
							dao.updateField(NotaUnidade.class, a.getUnidade().getId(), "nota", "null");
						dao.remove(a);
					}
				}
			}
			
			//Evitar erro de lazy
			tarefa = dao.findByPrimaryKey(tarefa.getId(), TarefaTurma.class, "id", "material.id", "aula.id");
			dao.updateField(TarefaTurma.class, tarefa.getId(), "ativo", false);
			dao.updateField(MaterialTurma.class, tarefa.getMaterial().getId(), "ativo", false);
			MaterialTurmaHelper.reOrdenarMateriais(tarefa.getAula());

			return new Notification();
		} finally {
			if ( dao != null )
				dao.close();
			if ( aDao != null )
				aDao.close();
		}
	}
	
	/**
	 * Método auxiliar do excute(), para a realização da alteração de uma tarefa.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private Object alterarTarefa(final Movimento mov) throws DAOException, NegocioException {

		TurmaDao dao = null;
		AvaliacaoDao aDao = null;

		try {	
			dao = getDAO(TurmaDao.class, mov);
			aDao = getDAO(AvaliacaoDao.class, mov);
			
			MovimentoCadastroAva cMov = (MovimentoCadastroAva) mov;
			cMov.setMensagem("Tarefa Alterada");
			TarefaTurma tarefa = cMov.getObjMovimentado();	
			Turma t = cMov.getTurma();
			Specification specification = cMov.getSpecification();
			
			if (!specification.isSatisfiedBy(tarefa))
				return specification.getNotification();
			
			cadastrarArquivo(tarefa);
			
			// Atualiza a tarefa
			dao.update(tarefa);
			MaterialTurmaHelper.atualizarMaterial(dao, tarefa, false);

			// Se a tarefa for alterada, é nescessário verificar as avaliações.
			ArrayList<Avaliacao> avaliacoes = (ArrayList<Avaliacao>) aDao.findAvaliacaoByTarefa(tarefa.getId());
			Collection<NotaUnidade> notas = null;
			
			Double notaMaxima = null;
			if ( tarefa.getNotaMaxima() !=  null ){
				notaMaxima = ( tarefa.getNotaMaxima() > 10 ) ? tarefa.getNotaMaxima()/10 : tarefa.getNotaMaxima();
			}
			
			// Caso esteja sendo alterados campos comuns a avaliação.
			if ( avaliacoes != null && !avaliacoes.isEmpty() ) {
				
				boolean modificouAbrev = !avaliacoes.get(0).getAbreviacao().equals(tarefa.getAbreviacao());
				boolean modificouDenominacao = !avaliacoes.get(0).getDenominacao().equals(tarefa.getTitulo());
				boolean modificouUnidade = avaliacoes.get(0).getUnidade().getUnidade() != tarefa.getUnidade();
				boolean modificouPeso = avaliacoes.get(0).getPeso() != tarefa.getPeso();
				boolean modificouNotaMaxima  = avaliacoes.get(0).getNotaMaxima() != tarefa.getNotaMaxima();
				
				// Caso a unidade seja modificada esse mapa irá conter a avaliação e a nova NotaUnidade referente a avaliação
				HashMap<Integer,NotaUnidade> map = null;
				
				// Popula o mapa com as novas unidades de cada avaliação
				if ( modificouUnidade ) {
					if (!t.isAgrupadora())
						notas = dao.findNotasByTurmaUnidade(t.getId(), tarefa.getUnidade());
					else
						notas = dao.findNotasByTurmaAgrupadoraUnidade(t.getId(), tarefa.getUnidade());
					
					map = new HashMap<Integer,NotaUnidade>();
					
					for ( Avaliacao a : avaliacoes ) {
						for ( NotaUnidade n : notas ) {	
							if ( a.getUnidade().getMatricula().getId() == n.getMatricula().getId() )
								map.put(a.getId(), n);
						}
					}	
				}				
				
				if ( modificouAbrev || modificouDenominacao || modificouPeso || modificouNotaMaxima || modificouUnidade ) {
					for ( Avaliacao a : avaliacoes ) {
						a.setDenominacao(tarefa.getTitulo());
						a.setAbreviacao(tarefa.getAbreviacao());
						a.setPeso(tarefa.getPeso());
						a.setNotaMaxima(notaMaxima);
						if ( map != null )
							a.setUnidade(map.get(a.getId()));
						dao.updateFields(Avaliacao.class, a.getId(), 
								new String [] { "denominacao","abreviacao","peso","notaMaxima","unidade" },
								new Object [] { a.getDenominacao(),a.getAbreviacao(),a.getPeso(),a.getNotaMaxima(),a.getUnidade() });
					}	
				}
				
				// Caso a tarefa seja alterada para não ter nota, as avaliações serão removidas
				if ( !tarefa.isPossuiNota() ) {
					for ( Avaliacao a : avaliacoes ) {
							dao.remove(a);
					}
				}
		
			}
				 
			// Caso o usuário esteja colocando notas na tarefa avaliações serão criadas.
			if (tarefa.isPossuiNota()) {
	
				// Se a tarefa não tem avaliações cria-se avaliações.
				if ( avaliacoes == null || avaliacoes.size() == 0) {
					
					if (!t.isAgrupadora())
						notas = dao.findNotasByTurmaUnidade(t.getId(), tarefa.getUnidade());
					else
						notas = dao.findNotasByTurmaAgrupadoraUnidade(t.getId(), tarefa.getUnidade());
						
					if (!isEmpty(notas)) {
						for (NotaUnidade nota : notas) {
							Avaliacao avaliacao = AvaliacaoHelper.criarAvaliacao(tarefa);
							avaliacao.setUnidade(nota);
							dao.create(avaliacao);
						}
					}
				}
			} 
		
			return new Notification();
			
		}finally {
			if (dao != null)
				dao.close();
			if (aDao != null)
				aDao.close();
		}	
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	/**
	 * Método que retorna o tópico de aula de uma outra turma onde será cadastrada a tarefa.
	 * Usado quando a tarefa deve ser cadasttrada em mais de uma turma.
	 * @param t
	 * @param dom
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private TopicoAula getTopicoEquivalente(Turma t, DominioTurmaVirtual dom, MovimentoCadastroAva mov) throws DAOException, NegocioException {
		TopicoAulaDao dao = null;
		
		try {
			dao = getDAO(TopicoAulaDao.class, mov);
			TopicoAula aula = (TopicoAula) ReflectionUtils.getFieldValue(dom, "aula");
			aula = dao.findByPrimaryKey(aula.getId(), TopicoAula.class, "id", "descricao" , "turma.id");
			TopicoAula equivalente = null;
			if (aula.getTurma().getId() != t.getId())
				equivalente = dao.findByDescricao(t, aula.getDescricao());
			else
				equivalente = aula;
			if (equivalente == null)
				throw new NegocioException("Não é possível cadastrar na turma " + t + " porque não existe o tópico de aula " + aula.getDescricao() + " cadastrado nela.");
			return equivalente;
		}finally{
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Método que retorna o tópico de aula de uma outra turma onde será cadastrada a tarefa.
	 * Usado quando a tarefa deve ser cadasttrada em mais de uma turma.
	 * @param t
	 * @param dom
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private void cadastrarArquivo( TarefaTurma tarefa ) throws DAOException, NegocioException {
		UploadedFile arquivo = tarefa.getArquivo();
		// Se um arquivo for selecionado para envio,
		if (arquivo != null) {
			try {							
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
				tarefa.setIdArquivo(idArquivo);
			} catch (IOException e) {
				throw new TurmaVirtualException(e);
			}
		}
	}
		
}
