/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 03/04/2011
 *
 */
package br.ufrn.sigaa.ava.forum.negocio;

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
 * Processador para regras de negócio do cadastro de um fórum.
 * 
 * @author Ilueny Santos
 * 
 */
public class ProcessadorForumGeral extends ProcessadorCadastro  {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		Object obj = null;

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_FORUM_GERAL)) {
			obj = criar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORUM_GERAL)) {
			obj = alterar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_FORUM_GERAL)) {
			obj = remover(mov);
		}

		return obj;
	}
	
	@Override
	protected Object criar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumGeral forum = mov.getObjMovimentado();

		anexarArquivo(forum);
		forum = (ForumGeral) super.criar(mov);

		GenericDAO dao = getGenericDAO(mov);
		try {
			//@negocio: Fórum do tipo discussão simples (que não permite criar tópico) cria um tópico único 
			// automaticamente e toda discussão é referente a esse tópico.
			dao.initialize(forum.getTipo());
			if (!forum.getTipo().isPermiteCriarTopico()) {
				ForumGeralMensagem topicoUnico = new ForumGeralMensagem();
				topicoUnico.setTopico(topicoUnico);
				topicoUnico.setForum(forum);
				topicoUnico.setMensagemPai(null); //é um tópico;
				topicoUnico.setAtivo(true);
				topicoUnico.setConteudo(forum.getDescricao());
				topicoUnico.setTitulo(forum.getTitulo());

				MovimentoCadastro mov2 = new MovimentoCadastro();
				mov2.setSistema(mov.getSistema());
				mov2.setUsuarioLogado(mov.getUsuarioLogado());
				mov2.setObjMovimentado(topicoUnico);
				mov2.setObjAuxiliar(mov.getObjAuxiliar());
				mov2.setCodMovimento(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM);
				new ProcessadorForumGeralMensagem().execute(mov2);
			}
		}finally {
			dao.close();
		}
		
		return forum;
		
	}
	
	/**
	 * Altera o fórum e arquivo anexo. 
	 */	
	@Override
	protected Object alterar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumGeral forum = mov.getObjMovimentado();
		anexarArquivo(forum);
		return super.alterar(mov);
	}
	
    /**
     * Adiciona um arquivo ao fórum.
     * 
     * @throws NegocioException 
     */
    private void anexarArquivo(ForumGeral forum) throws NegocioException {
    	try {
    		if ((forum != null) && (forum.getArquivo() != null) && (forum.getArquivo().getBytes() != null)) {
    			
    			//Removendo arquivo antigo
    			if (forum.getId() != 0 && forum.getIdArquivo() != null) {
    			    EnvioArquivoHelper.removeArquivo(forum.getIdArquivo());
    			}
    			
	    	    int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
	    	    EnvioArquivoHelper.inserirArquivo(idArquivo, forum.getArquivo().getBytes(), 
	    	    		forum.getArquivo().getContentType(), forum.getArquivo().getName());
	    	    forum.setIdArquivo(idArquivo);
    		}
    	} catch (Exception e) {
    		throw new NegocioException("Erro ao anexar arquivo.");
    	}
    }
    
    
	/** 
	 * Remove um fórum. 
	 * 
	 */
	@Override
	protected Object remover(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumGeralMensagemDao dao = getDAO(ForumGeralMensagemDao.class, mov);
		ForumGeral forum = mov.getObjMovimentado();
		try{
			
			dao.updateField(ForumGeralMensagem.class, forum.getId(), "ativo", false);
			inativarMensagensFilhas(dao, forum);
			
		}finally{
			dao.close();
		}
		return forum;
	}


	/**
	 * Inativar todas as mensagens filhas do fórum informado.
	 * 
	 * @param dao
	 * @param mensagem
	 * @throws DAOException
	 */
	private void inativarMensagensFilhas(ForumGeralMensagemDao dao, ForumGeral forum)	throws DAOException {
		List<ForumGeralMensagem> mensagensFilhas = dao.findMensagensParaRemoverByForum(forum.getId());		
		if (ValidatorUtil.isNotEmpty(mensagensFilhas)) {
			for (ForumGeralMensagem m : mensagensFilhas) {
				dao.updateField(ForumGeralMensagem.class, m.getId(), "ativo", false);
			}
		}		
	}

    

    @Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
    	MovimentoCadastro movC = (MovimentoCadastro) mov;
    	ListaMensagens mensagens = new ListaMensagens();
    	ForumGeral forum = movC.getObjMovimentado();

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_FORUM_GERAL)) {
			mensagens.addAll(forum.validate());
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORUM_GERAL)) {
			mensagens.addAll(forum.validate());			
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_FORUM_GERAL)) {
			//TODO:
		}
		checkValidation(mensagens);
	}

}
