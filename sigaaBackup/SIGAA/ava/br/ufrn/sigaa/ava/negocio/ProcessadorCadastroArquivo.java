/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ava.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Date;

import org.springframework.beans.BeanUtils;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Processador para associar arquivos com o conteœúdo das aulas
 * no portal da turma.
 * 
 * @author David Ricardo
 *
 */
public class ProcessadorCadastroArquivo extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoArquivoTurma aMov = (MovimentoArquivoTurma) mov;
		ArquivoUsuario arquivo = aMov.getArquivo();
		ArquivoTurma arquivoTurma = aMov.getArquivoTurma();
		
		TurmaDao dao = getDAO(TurmaDao.class, mov);
		
		try {
		
			if (SigaaListaComando.CADASTRAR_ARQUIVO.equals(mov.getCodMovimento())) {
			
				if (isEmpty(aMov.getCadastrarEm()) && aMov.isAssociarTurma()) {
					Notification n = new Notification();
					n.addError("É necessário selecionar ao menos uma turma para realizar o cadastro.");
					return n;
				}
				
				if (arquivo.getId() == 0)
					dao.create(arquivo);
				else
					arquivo = dao.findByPrimaryKey(arquivo.getId(), ArquivoUsuario.class);
				
				if (isEmpty (arquivo)){
					Notification n = new Notification();
					n.addError("O arquivo não foi encontrado no porta-arquivos. Por favor, tente novamente.");
					return n;
				}	

				if (!isEmpty(aMov.getCadastrarEm())) {
					Notification n = new Notification();
					for(String tid : aMov.getCadastrarEm()) {
						Turma t = dao.findByPrimaryKeyOtimizado(Integer.valueOf(tid));
						
						if (arquivoTurma.getAula().getId() != 0) {
							TopicoAula aula = dao.findByPrimaryKey(arquivoTurma.getAula().getId(), TopicoAula.class);
							if (t.getId() != aula.getTurma().getId())
								aula = getTopicoEquivalente(t, aula, aMov);
						
							if (aula == null) {
								throw new NegocioException("Não é possível cadastrar o arquivo na turma " + t + " pois não existe o tópico de aula selecionado nela.");
							}
							
							ArquivoTurma at = new ArquivoTurma();
							at.setArquivo(arquivo);
							at.setAula(aula);
							at.setData(new Date());
							at.setDescricao(arquivoTurma.getDescricao());
							
							// Se o docente não tiver cadastrado nenhum nome, usar o nome do arquivo.
							if (StringUtils.isEmpty(arquivoTurma.getNome()))
								at.setNome(arquivo.getNome());
							else
								at.setNome(arquivoTurma.getNome());
							
							at.setUsuarioCadastro((Usuario) mov.getUsuarioLogado());
							MaterialTurmaHelper.definirNovoMaterialParaTopico(at, at.getAula(), t);
							dao.create(at);							
							MaterialTurmaHelper.atualizarMaterial(dao, at, true);
						}
						n.adicionarTurmaSucesso(t);
					}
					return n;
				}
			} else if (SigaaListaComando.CADASTRAR_ARQUIVOS.equals(mov.getCodMovimento())) {
				
				if (isEmpty(aMov.getCadastrarEm()) && aMov.isAssociarTurma()) {
					Notification n = new Notification();
					n.addError("É necessário selecionar ao menos uma turma para realizar o cadastro.");
					return n;
				}
				
				if (!isEmpty(aMov.getCadastrarEm())) {
					Notification n = new Notification();
					for(String tid : aMov.getCadastrarEm()) {
						Turma t = dao.findByPrimaryKeyOtimizado(Integer.valueOf(tid));
						
						if (arquivoTurma.getAula().getId() != 0) {
							TopicoAula aula = dao.findByPrimaryKey(arquivoTurma.getAula().getId(), TopicoAula.class);
							if (t.getId() != aula.getTurma().getId()) {
								try {
									aula = getTopicoEquivalente(t, aula, aMov);
								} catch (NegocioException e) {
									n.addError("Não é possível cadastrar o(s) arquivo(s) em nenhunma turma pois não existe o tópico de aula selecionado na turma "+t+"."); 
									return n;
								}
							}
						
					
							for(ArquivoTurma at: aMov.getArquivosTurma()) {
								
								ArquivoTurma copy = new ArquivoTurma();
								BeanUtils.copyProperties(at, copy);
								
								MaterialTurma mCopy = new MaterialTurma();
								mCopy.setId(0);
								BeanUtils.copyProperties(copy.getMaterial(), mCopy);
								copy.setMaterial(mCopy);
								
								dao.create(copy.getArquivo());
								copy.setId(0);
								copy.setAula(aula);
								copy.setData(new Date());
								
								if (isEmpty(copy.getNome()))
									copy.setNome(copy.getArquivo().getNome());
								
								copy.setUsuarioCadastro((Usuario) mov.getUsuarioLogado());
								
								MaterialTurmaHelper.definirNovoMaterialParaTopico(copy, copy.getAula(), t);
								dao.create(copy);							
								MaterialTurmaHelper.atualizarMaterial(dao, copy, true);
							
							}
						}
						n.adicionarTurmaSucesso(t);
					}
					return n;
				}				
				
			} else if (SigaaListaComando.ATUALIZAR_ARQUIVO.equals(mov.getCodMovimento())) {
				dao.updateField(ArquivoUsuario.class, arquivoTurma.getArquivo().getId(), "idArquivo", arquivoTurma.getArquivo().getIdArquivo());
				dao.updateField(ArquivoUsuario.class, arquivoTurma.getArquivo().getId(), "tamanho", arquivoTurma.getArquivo().getTamanho());
				
				dao.updateField(ArquivoTurma.class, arquivoTurma.getId(), "aula", arquivoTurma.getAula());
				dao.updateField(ArquivoTurma.class, arquivoTurma.getId(), "data", arquivoTurma.getData());
				dao.updateField(ArquivoTurma.class, arquivoTurma.getId(), "descricao", arquivoTurma.getDescricao());
				dao.updateField(ArquivoTurma.class, arquivoTurma.getId(), "nome", arquivoTurma.getNome());
				
				arquivoTurma.setMaterial(dao.findByExactField(MaterialTurma.class, "idMaterial", arquivoTurma.getId(), true));
				MaterialTurmaHelper.atualizarMaterial(dao, arquivoTurma, false);
			}
			
		} finally {
			dao.close();
		}
		
		return null;
	}


	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

	/**
	 * Retorna o tópico de aula, cadastrado na turma passada, cuja descrição é igual à descrição do tópico em que se está cadastrando o arquivo.
	 * 
	 * @param t
	 * @param aula
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private TopicoAula getTopicoEquivalente(Turma t, TopicoAula aula, Movimento mov) throws DAOException, NegocioException {
		TopicoAulaDao dao = getDAO(TopicoAulaDao.class, mov);
		aula = dao.findByPrimaryKey(aula.getId(), TopicoAula.class);
		TopicoAula equivalente = dao.findByDescricao(t, aula.getDescricao());
		if (equivalente == null)
			throw new NegocioException("Não é possível cadastrar na turma " + t + " porque não existe o tópico de aula " + aula.getDescricao() + " cadastrado nela.");
		return equivalente;
	}
}
