/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 03/04/2011
 *
 */
package br.ufrn.sigaa.ava.forum.negocio;

import java.io.IOException;
import java.util.List;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.forum.dao.ForumGeralMensagemDao;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralMensagem;

/**
 * Processador para regras de negócio do cadastro de mensagens do fórum.
 * 
 * @author Ilueny Santos
 * 
 */
public class ProcessadorForumGeralMensagem extends ProcessadorCadastro  {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		Object obj = null;
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM)) {
			obj = criar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORUM_GERAL_MENSAGEM)) {
			obj = alterar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_FORUM_GERAL_MENSAGEM)) {
			obj = remover(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.INTERROMPER_FORUM_GERAL_MENSAGEM)) {
			obj = interromper(mov);
		}
		return obj;
	}
	

	/** Cria uma nova mensagem ou tópico em um fórum. */
	@Override
	protected Object criar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumGeralMensagemDao dao = getDAO(ForumGeralMensagemDao.class, mov);
		ForumGeralMensagem mensagem = mov.getObjMovimentado();
		try{
			anexarArquivo(mensagem);
			mensagem = (ForumGeralMensagem) super.criar(mov);
			if (mensagem.isTipoTopico()) {
				dao.updateField(ForumGeral.class, mensagem.getForum().getId(), "totalTopicos", dao.findCountTopicosByForum(mensagem.getForum().getId()));
				dao.updateField(ForumGeral.class, mensagem.getForum().getId(), "ultimaMensagem.id", mensagem.getId());
				dao.updateField(ForumGeralMensagem.class, mensagem.getId(), "hierarquia", "." + mensagem.getId());
			} else {
				atualizarTotaisMensagemPrincipal(mensagem, mov);

				calculaTotalRespostas(mensagem.getTopico(), mov);
				dao.updateField(ForumGeralMensagem.class, mensagem.getTopico().getId(), "ultimaMensagem.id", mensagem.getId());
				dao.updateField(ForumGeralMensagem.class, mensagem.getId(), "hierarquia", mensagem.getMensagemPai().getHierarquia() + "." + mensagem.getId());
			}
						
		}finally{
			dao.close();
		}
		return mensagem;
	}
	
	/**
	 * Altera a mensagem e arquivo anexo. 
	 */	
	@Override
	protected Object alterar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumGeralMensagem mensagem = mov.getObjMovimentado();
		anexarArquivo(mensagem);		
		super.alterar(mov);

		return mensagem;
	}


	/** 
	 * Remove uma mensagem ou tópico de um fórum. 
	 * 
	 */
	@Override
	protected Object remover(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumGeralMensagemDao dao = getDAO(ForumGeralMensagemDao.class, mov);
		ForumGeralMensagem mensagem = mov.getObjMovimentado();
		try{
			
			dao.updateField(ForumGeralMensagem.class, mensagem.getId(), "ativo", false);
			inativarMensagensFilhas(dao, mensagem);
			
			if (mensagem.isTipoTopico()) {
				dao.updateField(ForumGeral.class, mensagem.getForum().getId(), "totalTopicos", dao.findCountTopicosByForum(mensagem.getForum().getId()));
				dao.updateField(ForumGeral.class, mensagem.getForum().getId(), "ultimaMensagem.id", dao.findUltimaMensagemByForum(mensagem.getForum()));
			} else {
				calculaTotalRespostas(mensagem.getTopico(), mov);
				dao.updateField(ForumGeralMensagem.class, mensagem.getTopico().getId(), "ultimaMensagem.id", mensagem.getId());
			}
			
		}finally{
			dao.close();
		}
		return mensagem;
	}


	/**
	 * Inativar todas as mensagens filhas da mensagem informada.
	 * Se mensagem for um tópico, remove todas as mensagens do tópico.
	 * 
	 * @param dao
	 * @param mensagem
	 * @throws DAOException
	 */
	private void inativarMensagensFilhas(ForumGeralMensagemDao dao, ForumGeralMensagem mensagem)	throws DAOException {
		List<ForumGeralMensagem> mensagensFilhas = dao.findMensagensParaRemoverByMensagem(mensagem.getId());		
		if (ValidatorUtil.isNotEmpty(mensagensFilhas)) {
			for (ForumGeralMensagem m : mensagensFilhas) {
				dao.updateField(ForumGeralMensagem.class, m.getId(), "ativo", false);
			}
		}		
	}
	
	/**
	 * Método responsável por converter uma mensagem em tópico.
	 * 
	 */
	protected Object interromper(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumGeralMensagemDao dao = getDAO(ForumGeralMensagemDao.class, mov);
		ForumGeralMensagem mensagem = null;
		try{
			mensagem = (ForumGeralMensagem) mov.getObjMovimentado();
			int idTopicoAntigo =  mensagem.getTopico().getId();
			dao.updateField(ForumGeralMensagem.class, mensagem.getId(), "topico.id", mensagem.getId());
			dao.updateField(ForumGeralMensagem.class, mensagem.getId(), "mensagemPai.id", null);
			dao.updateField(ForumGeralMensagem.class, mensagem.getId(), "hierarquia", "." + mensagem.getId());
			
			String hierarquiaRemovida = mensagem.getHierarquia().replaceFirst("." + mensagem.getId(), "");
			
			//Mensagens filhas agora tem um novo tópico.
			List<ForumGeralMensagem> mensagensFilhas = dao.findMensagensParaRemoverByMensagem(mensagem.getId());		
			if (!ValidatorUtil.isEmpty(mensagensFilhas)) {
				for (ForumGeralMensagem m : mensagensFilhas) {
					dao.updateField(ForumGeralMensagem.class, m.getId(), "topico.id", mensagem.getId());
					dao.updateField(ForumGeralMensagem.class, m.getId(), "hierarquia", m.getHierarquia().replaceFirst(hierarquiaRemovida, "") );
				}
			}
						
			dao.updateField(ForumGeral.class, mensagem.getForum().getId(), "totalTopicos", dao.findCountTopicosByForum(mensagem.getForum().getId()));				
			calculaTotalRespostas(mensagem.getTopico(), mov);
			//Atualizando totais da mensagem pai
			calculaTotalRespostas(dao.findByPrimaryKey(idTopicoAntigo, ForumGeralMensagem.class), mov);
		}finally{
			dao.close();
		}
		return mensagem;
	}
	
	
	/**
	 * Calcula o total de respostas do tópico do fórum.
	 * 
	 * @param topico
	 * @param mov
	 * @throws DAOException
	 */
	public void calculaTotalRespostas(ForumGeralMensagem mensagem, Movimento mov) throws DAOException {
		ForumGeralMensagemDao dao = getDAO(ForumGeralMensagemDao.class, mov);
		try{
			int total = 0;
			
			if (mensagem.isTipoTopico()) {
				total = dao.findCountMensagensByTopico(mensagem.getId());
			} else {
				total = dao.findCountMensagensByMensagemPai(mensagem.getId());
			}						
			
			dao.updateField(ForumGeralMensagem.class, mensagem.getId(), "totalRespostas", total);
			
		}finally{
			dao.close();
		}
	}
	

	/**
	 * Atualiza total de mensagens da mensagem respondida.
	 *  
	 **/
	public void atualizarTotaisMensagemPrincipal(ForumGeralMensagem mensagem, Movimento mov) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		try{
			if (!ValidatorUtil.isEmpty(mensagem.getMensagemPai())) {
				calculaTotalRespostas(mensagem.getMensagemPai(), mov);
				dao.updateField(ForumGeralMensagem.class, mensagem.getMensagemPai().getId(), "ultimaMensagem.id", mensagem.getId());
				
				int idPai = mensagem.getMensagemPai().getId();
				ForumGeralMensagem pai = dao.findByPrimaryKey(idPai, ForumGeralMensagem.class, "id", "mensagemPai.id");
				if (!ValidatorUtil.isEmpty(pai.getMensagemPai())) {
					atualizarTotaisMensagemPrincipal(pai, mov);
				}
			}
		}finally{
			dao.close();
		}
	}
	
    /**
     * Adiciona um arquivo a mensagem.
     * 
     * @throws NegocioException 
     */
    private void anexarArquivo(ForumGeralMensagem mensagem) throws NegocioException {
    	try {
    		if ((mensagem != null) && (mensagem.getArquivo() != null) && (mensagem.getArquivo().getBytes() != null)) {
    			
    			//Removendo arquivo antigo
    			if (mensagem.getId() != 0 && mensagem.getIdArquivo() != null) {
    			    EnvioArquivoHelper.removeArquivo(mensagem.getIdArquivo());
    			}
    			
	    	    int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
	    	    EnvioArquivoHelper.inserirArquivo(idArquivo, mensagem.getArquivo().getBytes(), 
	    	    		mensagem.getArquivo().getContentType(), mensagem.getArquivo().getName());
	    	    mensagem.setIdArquivo(idArquivo);
    		}
    	} catch (IOException e) {
    		throw new NegocioException("Erro ao anexar arquivo.");
    	}
    }
    @Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
    	MovimentoCadastro movC = (MovimentoCadastro) mov;
    	ListaMensagens mensagens = new ListaMensagens();
    	ForumGeralMensagem mens = movC.getObjMovimentado();

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM)) {
			mensagens.addAll(mens.validate());
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORUM_GERAL_MENSAGEM)) {
			mensagens.addAll(mens.validate());
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_FORUM_GERAL_MENSAGEM)) {
			if(!(mens.getUsuario().getId() == movC.getUsuarioLogado().getId())){
				mensagens.addErro("Apenas o dono da mensagem pode remove-la");
			}
		} else if (mov.getCodMovimento().equals(SigaaListaComando.INTERROMPER_FORUM_GERAL_MENSAGEM)) {
			//TODO:
		}
		checkValidation(mensagens);
	}

    
}
