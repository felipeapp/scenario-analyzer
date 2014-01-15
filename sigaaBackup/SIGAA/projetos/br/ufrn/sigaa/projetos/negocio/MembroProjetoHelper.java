/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe auxiliar para as operações sobre a entidade MembroProjeto
 *  
 * @author Ilueny Santos
 *
 */
public class MembroProjetoHelper {

	/**
	 * Responsável por validar os dados de um Discente ao adicioná-lo em um Projeto.
	 * 
	 * @param dao
	 * @param membro Caso o membro esteja com a data início ou fim como null, então estas serão setadas com as
	 * datas início e fim do projeto. Caso contrário, tais datas foram informadas pelo usuário no momento do cadastro.
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
				mensagens.addErro("Data de Início: Informe uma data de início maior ou igual a data de início do projeto.");
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de Início: Informe uma data de início menor ou igual a data de fim do projeto.");
			
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de Início: Informe uma data de fim menor ou igual a data de início do projeto.");
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de Início: Informe uma data de fim maior ou igual a data de fim do projeto.");
			
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
	 * Responsável por validar os dados de um Docente ao adicioná-lo em um Projeto.
	 * 
	 * @param dao
	 * @param membro Caso o membro esteja com a data início ou fim como null, então estas serão setadas com as
	 * datas início e fim do projeto. Caso contrário, tais datas foram informadas pelo usuário no momento do cadastro.
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
	 * Responsável por validar os dados de um Docente ao adicioná-lo em um Projeto sem a validação de coordenação dupla.
	 * Método utilizado para Alterar Coordenador de um Projeto e pelo método adicionarDocente
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
				mensagens.addErro("Data de Início: Informe uma data de início maior ou igual a data de início do projeto.");
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de Início: Informe uma data de início menor ou igual a data de fim do projeto.");
			
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de Início: Informe uma data de fim menor ou igual a data de início do projeto.");
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de Início: Informe uma data de fim maior ou igual a data de fim do projeto.");
			
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
	 * Responsável por validar os dados de um Servidor ao adicioná-lo em um Projeto.
	 * 
	 * @param dao
	 * @param membro Caso o membro esteja com a data início ou fim como null, então estas serão setadas com as
	 * datas início e fim do projeto. Caso contrário, tais datas foram informadas pelo usuário no momento do cadastro.
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
	 * Responsável por validar os dados de um Servidor ao adicioná-lo em um Projeto sem a validação de coordenação dupla.
	 * Método utilizado para Alterar Coordenador de um Projeto e pelo método adicionarServidor
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
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Técnico-Administrativo");	    
		}else {

			membro.setProjeto(projeto);
			if(membro.getDataInicio() == null)
				membro.setDataInicio(projeto.getDataInicio());
			if(membro.getDataFim() == null)
				membro.setDataFim(projeto.getDataFim());
			
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de Início: Informe uma data de início maior ou igual a data de início do projeto.");
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de Início: Informe uma data de início menor ou igual a data de fim do projeto.");
			
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de Início: Informe uma data de fim menor ou igual a data de início do projeto.");
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de Início: Informe uma data de fim maior ou igual a data de fim do projeto.");
			
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
	 * Responsável por validar os dados de um Participante Externo ao adicioná-lo em um Projeto.
	 * 
	 * @param dao
	 * @param membro Caso o membro esteja com a data início ou fim como null, então estas serão setadas com as
	 * datas início e fim do projeto. Caso contrário, tais datas foram informadas pelo usuário no momento do cadastro.
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
				mensagens.addErro("Data de Início: Informe uma data de início maior ou igual a data de início do projeto.");
			if ((membro.getDataInicio() != null) && membro.getDataInicio().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de Início: Informe uma data de início menor ou igual a data de fim do projeto.");
			
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() < projeto.getDataInicio().getTime())
				mensagens.addErro("Data de Início: Informe uma data de fim menor ou igual a data de início do projeto.");
			if ((membro.getDataFim() != null) && membro.getDataFim().getTime() > projeto.getDataFim().getTime())
				mensagens.addErro("Data de Início: Informe uma data de fim maior ou igual a data de fim do projeto.");
			
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
