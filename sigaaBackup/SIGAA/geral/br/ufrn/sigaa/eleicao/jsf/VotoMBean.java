/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '03/04/2007'
 *
 */
package br.ufrn.sigaa.eleicao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.eleicao.CandidatoDao;
import br.ufrn.sigaa.arq.dao.eleicao.VotoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.eleicao.dominio.Candidato;
import br.ufrn.sigaa.eleicao.dominio.Eleicao;
import br.ufrn.sigaa.eleicao.dominio.Voto;
import br.ufrn.sigaa.eleicao.negocio.VotoValidator;

/**
 * MBean respons�vel pelas views relacionadas ao voto em um cadidato
 * 
 * @see Candidato
 * @see Eleicao
 * 
 * @author Victor Hugo
 */
@Component("voto") @Scope("request")
public class VotoMBean extends SigaaAbstractController<Voto> {

	public static final String VOTACAO_FORM = "/portais/discente/votacao.jsp";
	
	private Eleicao eleicao = new Eleicao();
	
	private Integer chapa;
	
	// usado no ajax de aparecer o candidato (include_votacao.jsp)
	private Integer idEleicao;
	
	public VotoMBean() {
		obj = new Voto();
	}
	
	/**
	 * Realiza as devidas valida��es para que seja realizada uma vota��o e caso n�o haja nenhuma contradi��o � carregado as informa��es para a vota��o.
	 * @return
	 * @throws ArqException 
	 */
	public String popularVotacao() throws ArqException{
		
		Integer id = getParameterInt("idEleicao");
		VotoDao dao = getDAO(VotoDao.class);
		
		eleicao = dao.findByPrimaryKey(id, Eleicao.class); 
		
		ListaMensagens mensagens = new ListaMensagens();
		
		if( eleicao == null ){
			mensagens.addErro("N�o h� uma elei��o selecionada.");
			//addMensagemErro("Erro de execu��o, contacte a administra��o do sistema.");
			return null;
		}
		
		if( getDiscenteUsuario() == null ){
			mensagens.addErro("Voc� n�o tem acesso a esta opera��o.");
			//addMensagemErro("Voc� n�o tem acesso a esta opera��o.");
			return null;
		}
		
		// Validar se o discente est� ativo
		if (getDiscenteUsuario().getStatus() != StatusDiscente.ATIVO
				&& getDiscenteUsuario().getStatus() != StatusDiscente.FORMANDO) {
			addMensagemErro("Somente alunos com status ATIVO ou FORMANDO podem votar.");
			return redirectMesmaPagina();
		}
		
		/**
		 * o usu�rio deve ser do centro de onde esta ocorrendo a elei��o
		 */
		//if( eleicao.getCentro().getId() != getUsuarioLogado().getUnidade().getId() )
			//mensagens.add(new MensagemAviso("Voc� n�o tem acesso a esta opera��o pois n�o pertence ao centro de onde esta ocorrendo a elei��o.", TipoMensagemUFRN.ERROR));
		
		
		VotoValidator.validarEleicao(eleicao, mensagens);
		
		VotoValidator.validarVotacao(eleicao, getDiscenteUsuario(), mensagens);

		eleicao.getCandidatos().iterator();
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
		}
		
		if( hasErrors() )
			return null;
		
		prepareMovimento(SigaaListaComando.ELEICAO_VOTAR);
		
		return forward(VOTACAO_FORM);
		
	}
	
	/**
	 * configura o bean para votar no candidato escolhido
	 * @return
	 * @throws DAOException 
	 */
	public String votar() throws DAOException{

		if( chapa == null || chapa == 0 )
			addMensagemErro("Escolha uma chapa!");
		
		if( eleicao == null || eleicao.getId() == 0 )
			addMensagemErro("N�o h� nenhuma elei��o selecionada.");
		
		if( hasErrors() )
			return null;		
		
		CandidatoDao dao = getDAO(CandidatoDao.class);
		
		//int id = getParameterInt("id");
		
//		 Popular movimento
		Candidato candidato = dao.findByChapaEleicao(chapa, eleicao.getId()); 
		
		if( candidato == null || candidato.getId() == 0 )
			return votarNulo();
			//addMensagemErro("Chapa inexistente, entre com um n�mero de chapa v�lido por favor.");
		
		
		
		//= new Candidato();
		//candidato.setId(id);
		candidato.getEleicao().getId();
		obj.setCandidato(candidato);
		obj.setEleicao( eleicao );
		obj.setDiscente( getDiscenteUsuario().getDiscente() );
		obj.setCodMovimento( SigaaListaComando.ELEICAO_VOTAR );
		
		processarVoto();
		/*try {
			
			int id = getParameterInt("id");
			
			// Popular movimento
			Candidato candidato = new Candidato();
			candidato.setId(id);
			obj.setCandidato(candidato);
			obj.setDiscente( getDiscenteUsuario() );
			obj.setCodMovimento( SigaaListaComando.ELEICAO_VOTAR );
			
			// Executar votacao
			execute(obj, getCurrentRequest());
	
			obj = new Voto();
			//foto = new UploadedFile();
	
			addMessage("Voto registrado com sucesso!", TipoMensagemUFRN.INFORMATION);
			return super.cancelar();
			
		} catch (ArqException e) {
			notifyError(e);
			e.printStackTrace();
		} catch (NegocioException e) {
			notifyError(e);
			e.printStackTrace();
		} catch (RemoteException e) {
			notifyError(e);
			e.printStackTrace();
		}*/
		return null;
		
	}
	
	/**
	 * configura o bean para votar BRANCO
	 * @return
	 * @throws DAOException
	 */
	public String votarBranco() throws DAOException{

		Candidato candidato = new Candidato();
		candidato.setId( Voto.BRANCO );
		obj.setCandidato(candidato);
		obj.setEleicao(eleicao);
		obj.setDiscente( getDiscenteUsuario().getDiscente() );
		obj.setCodMovimento( SigaaListaComando.ELEICAO_VOTAR );
		
		processarVoto();
		
		return null;
		
	}	
	
	/**
	 * configura o bean para votar NULO
	 * @return
	 * @throws DAOException
	 */
	public String votarNulo() throws DAOException{

		Candidato candidato = new Candidato();
		candidato.setId( Voto.NULO );
		obj.setCandidato(candidato);
		obj.setEleicao(eleicao);
		obj.setDiscente( getDiscenteUsuario().getDiscente() );
		obj.setCodMovimento( SigaaListaComando.ELEICAO_VOTAR );
		
		processarVoto();
		
		return null;
		
	}	
	
	/**
	 * invoca o processador para registrar o voto
	 * @return
	 */
	public String processarVoto(){
		
		try {
			
			ListaMensagens mensagens = new ListaMensagens();
			VotoValidator.validarVotacao(eleicao, getDiscenteUsuario(),	mensagens);
			
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
			}

			if (hasErrors())
				return null;			
			
			// Executar vota��o
			execute(obj, getCurrentRequest());
	
			obj = new Voto();
			//foto = new UploadedFile();
	
			addMessage("Voto registrado com sucesso!", TipoMensagemUFRN.INFORMATION);
			return super.cancelar();
			
		} catch (ArqException e) {
			notifyError(e);
			e.printStackTrace();
		} catch (NegocioException e) {
			notifyError(e);
			e.printStackTrace();
		}
		return null;
		
	}
	

	public Eleicao getEleicao() {
		return eleicao;
	}

	public void setEleicao(Eleicao eleicao) {
		this.eleicao = eleicao;
	}

	public Integer getChapa() {
		return chapa;
	}

	public void setChapa(Integer chapa) {
		this.chapa = chapa;
	}

	/**
	 * Retorna o candidato para a JSP (include_votacao.jsp)
	 * @return
	 * @throws DAOException
	 */
	public Candidato getCandidato() throws DAOException {
		CandidatoDao dao = getDAO(CandidatoDao.class);
		return dao.findByChapaEleicao(chapa, idEleicao);
		
	}

	public Integer getIdEleicao() {
		return idEleicao;
	}

	public void setIdEleicao(Integer idEleicao) {
		this.idEleicao = idEleicao;
	}
	
	
	
	
	
}
