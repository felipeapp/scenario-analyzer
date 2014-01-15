/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.cv.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.cv.dao.TopicoComunidadeDao;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.cv.dominio.ArquivoComunidade;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processador para associar arquivos com o conte�do das aulas
 * no portal da turma.
 * 
 * @author David Ricardo
 *
 */
public class ProcessadorCadastroArquivoCV extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoArquivoCV aMov = (MovimentoArquivoCV) mov;
		ArquivoUsuario arquivo = aMov.getArquivo();
		ArquivoComunidade arquivoComunidade = aMov.getArquivoComunidade();
		
		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class, mov);
		
		try {
		
			if (SigaaListaComando.CADASTRAR_ARQUIVO_CV.equals(mov.getCodMovimento())) {
			
				if (arquivo.getId() == 0)
					dao.create(arquivo);
				else
					arquivo = dao.findByPrimaryKey(arquivo.getId(), ArquivoUsuario.class);
				
				if (isEmpty(aMov.getCadastrarEm())) {
					Notification n = new Notification();
					n.addError("� necess�rio selecionar ao menos um t�pico para realizar o cadastro.");
					return n;
				}

				if (!isEmpty(aMov.getCadastrarEm())) {
					for(String tid : aMov.getCadastrarEm()) {
						ComunidadeVirtual t = dao.findByPrimaryKeyOtimizado(Integer.valueOf(tid));
//						TopicoComunidade t = dao.findByPrimaryKeyOtimizado(Integer.valueOf(tid));
						
						if (arquivoComunidade.getTopico().getId() != 0) {
							TopicoComunidade topicoComunidade = dao.findByPrimaryKey(arquivoComunidade.getTopico().getId(), TopicoComunidade.class);
							topicoComunidade = getTopicoEquivalente(t, topicoComunidade, aMov);
						
							if (topicoComunidade == null) {
								throw new NegocioException("N�o � poss�vel cadastrar o arquivo na turma " + t + " pois n�o existe o t�pico de aula selecionado nela.");
							}
							
							ArquivoComunidade at = new ArquivoComunidade();
							at.setArquivo(arquivo);
							at.setTopico(topicoComunidade);
							at.setData(new Date());
							at.setDescricao(arquivoComunidade.getDescricao());
							
							// Se o docente n�o tiver cadastrado nenhum nome, usar o nome do arquivo.
							if (StringUtils.isEmpty(arquivoComunidade.getNome()))
								at.setNome(arquivo.getNome());
							else
								at.setNome(arquivoComunidade.getNome());
							
							at.setUsuarioCadastro((Usuario) mov.getUsuarioLogado());
							dao.create(at);
							
//							RegistroAtividadeTurma reg = new RegistroAtividadeTurma();
//							reg.setData(new Date());
//							reg.setTurma(t);
//							reg.setUsuario((Usuario) mov.getUsuarioLogado());
//							reg.setDescricao("Adicionado arquivo " + at.getNome());
//							dao.create(reg);
						}
					}
				}
			} 
//			else if (SigaaListaComando.ATUALIZAR_ARQUIVO.equals(mov.getCodMovimento())) {
//				dao.updateField(ArquivoUsuario.class, arquivoComunidade.getArquivo().getId(), "idArquivo", arquivoComunidade.getArquivo().getIdArquivo());
//				dao.updateField(ArquivoUsuario.class, arquivoComunidade.getArquivo().getId(), "tamanho", arquivoComunidade.getArquivo().getTamanho());
//				
//				dao.updateField(ArquivoTurma.class, arquivoComunidade.getId(), "topico", arquivoComunidade.getTopico());
//				dao.updateField(ArquivoTurma.class, arquivoComunidade.getId(), "data", arquivoComunidade.getData());
//				dao.updateField(ArquivoTurma.class, arquivoComunidade.getId(), "descricao", arquivoComunidade.getDescricao());
//				dao.updateField(ArquivoTurma.class, arquivoComunidade.getId(), "nome", arquivoComunidade.getNome());
//			}
			
		} finally {
			dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

	/**
	 * Para a comunidade virtual passada, retorna seu t�pico que tem descri��o igual � do t�pico passado.
	 * 
	 * @param t
	 * @param topicoComunidade
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private TopicoComunidade getTopicoEquivalente(ComunidadeVirtual t, TopicoComunidade topicoComunidade, Movimento mov) throws DAOException, NegocioException {
		TopicoComunidadeDao dao = getDAO(TopicoComunidadeDao.class, mov);
		topicoComunidade = dao.findByPrimaryKey(topicoComunidade.getId(), TopicoComunidade.class);
		TopicoComunidade equivalente = dao.findByDescricao(t, topicoComunidade.getDescricao());
		if (equivalente == null)
			throw new NegocioException("N�o � poss�vel cadastrar na comunidae " + t + " porque n�o existe o t�pico de comunidade " + topicoComunidade.getDescricao() + " cadastrado nela.");
		return equivalente;
	}
}
