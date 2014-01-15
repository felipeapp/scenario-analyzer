/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/07/2010
 *
 */

package br.ufrn.sigaa.projetos.negocio;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;


/**
 * Classe auxiliar para as opera��es sobre a entidade MembroProjeto
 *  
 * @author Ilueny Santos
 *
 */
public class MembroProjetoHelper {

	/**
	 * Respons�vel por validar os dados de um Discente ao adicion�-lo em um Projeto.
	 * 
	 * @param dao
	 * @param membro Caso o membro esteja com a data in�cio ou fim como null, ent�o estas ser�o setadas com as
	 * datas in�cio e fim do projeto. Caso contr�rio, tais datas foram informadas pelo usu�rio no momento do cadastro.
	 * @param projeto
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean adicionarDiscente(GenericDAO dao, MembroProjeto membro, Projeto projeto, ListaMensagens mensagens) throws DAOException {	
		if (ValidatorUtil.isEmpty(membro.getDiscente()) || ValidatorUtil.isEmpty(membro.getDiscente().getPessoa())) {	    
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");	    
		}else {

			membro.setProjeto(projeto);
			if(membro.getDataInicio() == null)
				membro.setDataInicio(projeto.getDataInicio());
			if(membro.getDataFim() == null)
				membro.setDataFim(projeto.getDataFim());
			
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de in�cio maior ou igual a data de in�cio do projeto.");
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de in�cio menor ou igual a data de fim do projeto.");
			
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de fim menor ou igual a data de in�cio do projeto.");
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de fim maior ou igual a data de fim do projeto.");
			
			membro.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.DISCENTE));	    
			membro.setPessoa(membro.getDiscente().getPessoa());
			membro.setServidor(null);
			membro.setDocenteExterno(null);
			membro.setParticipanteExterno(null);

			dao.initialize(membro.getDiscente());	    
			dao.initialize(membro.getFuncaoMembro());
			dao.initialize(membro.getCategoriaMembro());		

			MembroProjetoValidator.validaDiscenteSemValidacaoBolsista(projeto.getEquipe(), membro, mensagens);
		}
		return mensagens.isEmpty();
	}

	/**
	 * Respons�vel por validar os dados de um Docente ao adicion�-lo em um Projeto.
	 * 
	 * @param dao
	 * @param membro Caso o membro esteja com a data in�cio ou fim como null, ent�o estas ser�o setadas com as
	 * datas in�cio e fim do projeto. Caso contr�rio, tais datas foram informadas pelo usu�rio no momento do cadastro.
	 * @param projeto
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean adicionarDocente(GenericDAO dao, MembroProjeto membro, Projeto projeto, ListaMensagens mensagens) throws DAOException {	
		adicionarDocenteSemCoordenacaoDupla(dao, membro, projeto, mensagens);
			if (membro.isCoordenador()) {
				MembroProjetoValidator.validaCooordenacaoDupla(projeto, membro, mensagens);
			}
		
		return mensagens.isEmpty();
	}
	
	/**
	 * Respons�vel por validar os dados de um Docente ao adicion�-lo em um Projeto sem a valida��o de coordena��o dupla.
	 * M�todo utilizado para Alterar Coordenador de um Projeto e pelo m�todo adicionarDocente
	 *  
	 * @param dao
	 * @param membro
	 * @param projeto
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean adicionarDocenteSemCoordenacaoDupla(GenericDAO dao, MembroProjeto membro, Projeto projeto, ListaMensagens mensagens) throws DAOException {
		if (ValidatorUtil.isEmpty(membro.getServidor()) || ValidatorUtil.isEmpty(membro.getServidor().getPessoa())) {	    
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docente");	    
		}else {

			membro.setProjeto(projeto);
			if(membro.getDataInicio() == null)
				membro.setDataInicio(projeto.getDataInicio());
			if(membro.getDataFim() == null)
				membro.setDataFim(projeto.getDataFim());
			
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de in�cio maior ou igual a data de in�cio do projeto.");
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de in�cio menor ou igual a data de fim do projeto.");
			
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de fim menor ou igual a data de in�cio do projeto.");
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de fim maior ou igual a data de fim do projeto.");
			
			membro.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.DOCENTE));	    
			membro.setPessoa(membro.getServidor().getPessoa());
			membro.setDiscente(null);
			membro.setDocenteExterno(null);		
			membro.setParticipanteExterno(null);

			dao.initialize(membro.getServidor());	    
			dao.initialize(membro.getFuncaoMembro());
			dao.initialize(membro.getCategoriaMembro());

			MembroProjetoValidator.validaServidor(projeto.getEquipe(), membro, mensagens);
			if (membro.isCoordenador()) {
				MembroProjetoValidator.validaCoordenacaoEnsinoPesquisa(membro, mensagens);
				MembroProjetoValidator.validaCoordenacaoSimultanea(membro, projeto, mensagens);
			}
		}
		return mensagens.isEmpty();
	}

	/**
	 * Respons�vel por validar os dados de um Servidor ao adicion�-lo em um Projeto.
	 * 
	 * @param dao
	 * @param membro Caso o membro esteja com a data in�cio ou fim como null, ent�o estas ser�o setadas com as
	 * datas in�cio e fim do projeto. Caso contr�rio, tais datas foram informadas pelo usu�rio no momento do cadastro.
	 * @param projeto
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean adicionarServidor(GenericDAO dao, MembroProjeto membro, Projeto projeto, ListaMensagens mensagens) throws DAOException {
		adicionarServidorSemCoordenacaoDupla(dao, membro, projeto, mensagens);
			if (membro.isCoordenador()) {
				MembroProjetoValidator.validaCooordenacaoDupla(projeto, membro, mensagens);
			}
		return mensagens.isEmpty();	
	}
	
	/**
	 * Respons�vel por validar os dados de um Servidor ao adicion�-lo em um Projeto sem a valida��o de coordena��o dupla.
	 * M�todo utilizado para Alterar Coordenador de um Projeto e pelo m�todo adicionarServidor
	 * 
	 * @param dao
	 * @param membro
	 * @param projeto
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean adicionarServidorSemCoordenacaoDupla(GenericDAO dao, MembroProjeto membro, Projeto projeto, ListaMensagens mensagens) throws DAOException {
		if (ValidatorUtil.isEmpty(membro.getServidor()) || ValidatorUtil.isEmpty(membro.getServidor().getPessoa())) {	    
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�cnico-Administrativo");	    
		}else {

			membro.setProjeto(projeto);
			if(membro.getDataInicio() == null)
				membro.setDataInicio(projeto.getDataInicio());
			if(membro.getDataFim() == null)
				membro.setDataFim(projeto.getDataFim());
			
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de in�cio maior ou igual a data de in�cio do projeto.");
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de in�cio menor ou igual a data de fim do projeto.");
			
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de fim menor ou igual a data de in�cio do projeto.");
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de fim maior ou igual a data de fim do projeto.");
			
			membro.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.SERVIDOR));	    
			membro.setPessoa(membro.getServidor().getPessoa());
			membro.setDiscente(null);
			membro.setDocenteExterno(null);
			membro.setParticipanteExterno(null);	

			dao.initialize(membro.getServidor());	    
			dao.initialize(membro.getFuncaoMembro());
			dao.initialize(membro.getCategoriaMembro());

			MembroProjetoValidator.validaServidor(projeto.getEquipe(), membro, mensagens);
			if (membro.isCoordenador()) {
				MembroProjetoValidator.validaCoordenacaoEnsinoPesquisa(membro, mensagens);
				MembroProjetoValidator.validaCoordenacaoSimultanea(membro, projeto, mensagens);
			}
		}
		return mensagens.isEmpty();	
	}

	/**
	 * Respons�vel por validar os dados de um Participante Externo ao adicion�-lo em um Projeto.
	 * 
	 * @param dao
	 * @param membro Caso o membro esteja com a data in�cio ou fim como null, ent�o estas ser�o setadas com as
	 * datas in�cio e fim do projeto. Caso contr�rio, tais datas foram informadas pelo usu�rio no momento do cadastro.
	 * @param projeto
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean adicionarParticipanteExterno(GenericDAO dao, MembroProjeto membro, Projeto projeto, ListaMensagens mensagens) throws DAOException {	
		if (membro.getParticipanteExterno() == null || ValidatorUtil.isEmpty(membro.getParticipanteExterno().getPessoa())) {	    
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Participante Externo");	    
		}else {

			membro.setProjeto(projeto);
			if(membro.getDataInicio() == null)
				membro.setDataInicio(projeto.getDataInicio());
			if(membro.getDataFim() == null)
				membro.setDataFim(projeto.getDataFim());
			
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de in�cio maior ou igual a data de in�cio do projeto.");
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de in�cio menor ou igual a data de fim do projeto.");
			
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de fim menor ou igual a data de in�cio do projeto.");
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de In�cio: Informe uma data de fim maior ou igual a data de fim do projeto.");
			
			membro.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.EXTERNO));	    
			membro.setPessoa(membro.getParticipanteExterno().getPessoa());
			membro.getPessoa().setNome(membro.getPessoa().getNome().trim().toUpperCase());
			membro.setDiscente(null);
			membro.setServidor(null);		
			membro.setDocenteExterno(null);

			dao.initialize(membro.getParticipanteExterno());	    
			dao.initialize(membro.getFuncaoMembro());
			dao.initialize(membro.getCategoriaMembro());

			MembroProjetoValidator.validaParticipanteExterno(projeto.getEquipe(), membro, mensagens, false);
		}
		return mensagens.isEmpty();
	}

}
