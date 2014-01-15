/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Criado em 01/02/2007 16:38:06
 * Autor: Edson Anibal de Macêdo Reis Batista (ambar@info.ufrn.br)
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.EnqueteDao;
import br.ufrn.sigaa.ava.dominio.Enquete;
import br.ufrn.sigaa.ava.dominio.EnqueteResposta;
import br.ufrn.sigaa.ava.dominio.EnqueteVotos;

@Component("enqueteResposta") @Scope("request")
public class EnqueteRespostaMBean extends SigaaAbstractController<EnqueteResposta> {

	public EnqueteRespostaMBean() throws DAOException
	{

		idEnquete = getParameterInt("idEnquete");

		TurmaDao turmaDao = getDAO(TurmaDao.class);
		obj = new EnqueteResposta();
		try
		{
			obj.setEnquete(turmaDao.findByPrimaryKey(idEnquete, Enquete.class) );
		} catch (Exception e) {  }

	}


	/**
	 * Técnica POG usada para recarregar a pagina passando o idEnquete como parâmetro.
	 * idEnquete esta na URL e também num <input type=hidden /> no jsp
	 */
	private int idEnquete;
	public String getPegarEnqueteResposta() { //Repetir tudo que estiver no construtor aqui.

		//Evita que se perca o objeto quando a pagina for recarregada para Alterar ou Remover
		if (!"Cadastrar".equals(getConfirmButton()))
			return null;

		idEnquete = getParameterInt("idEnquete");
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		obj = new EnqueteResposta();
		try
		{
			obj.setEnquete(turmaDao.findByPrimaryKey(idEnquete, Enquete.class) );
		} catch (Exception e) {  }

		return null;
	}

	/**
	 * Alterado para poder passar o parâmetro idEnquete via URL
	 * idEnquete esta na URL e também num <input type=hidden /> no jsp
	 * Edson Anibal (ambar@info.ufrn.br)
	 */
	@Override
	public String forwardCadastrar()
	{
		int idEnquete = this.idEnquete;
		String id = Integer.toString(idEnquete);
		String url = "/sigaa/portais/turma/Enquete/EnqueteResposta.jsf?idEnquete="+id;

		try
		{
			getCurrentResponse().sendRedirect(url);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {}

		return null;
	}




	/**
	 * Retorna uma Lista de todas as respostas das enquetes cadastradas para a turma atual :)
	 * @return Enquete
	 */
	public List<EnqueteResposta> getAll()
	{

		EnqueteDao Dao = getDAO(EnqueteDao.class);
		try
		{
			return Dao.findRespostasByEnquete(obj.getEnquete().getId());
		} catch(Exception e) {return null;}
	}




	@Override
	public void afterCadastrar()
	{
		obj = new EnqueteResposta();
	}


	@Override
	public String getFormPage()
	{

		String url = "/portais/turma/Enquete/EnqueteResposta.jsp?idEnquete="+this.idEnquete;
		return url;
	}



	@Override
	public String getListPage()
	{

		String url = "/sigaa/portais/turma/Enquete/EnqueteResposta.jsf?idEnquete="+this.idEnquete;

		try
		{
			getCurrentResponse().sendRedirect(url);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {}

		return null;

	}

	public void afterRemover()
	{
		setConfirmButton("Cadastrar");
		resetBean();
		obj = new EnqueteResposta();
	}

	@Override
	public String cancelar()
	{
		obj = new EnqueteResposta();
		resetBean();
		return forward("/portais/turma/Enquete/form.jsp");
	}


	/**
	 * Vota em uma das alternativas da Enquete
	 */
	public String Votar()
	{
		int idEnqueteResposta = Integer.parseInt(getParameter("idEnqueteResposta"));
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		try
		{
			EnqueteResposta enqueteResposta = turmaDao.findByPrimaryKey(idEnqueteResposta, EnqueteResposta.class);
			
			EnqueteVotos voto = new EnqueteVotos();
			voto.setEnqueteResposta(enqueteResposta);
			//voto.setEnquete(enqueteResposta.getEnquete());
			voto.setUsuario(getUsuarioLogado());

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.VOTAR_ENQUETE);
			mov.setObjMovimentado(voto);
			execute(mov, getCurrentRequest()); //Padrão de projeto: "COMMAND"

			addMessage("Voto Computado Com Sucesso.", TipoMensagemUFRN.INFORMATION);
		}
		catch (Exception e) { }


		String url = "/sigaa/ava/Enquete/votar.jsf?id="+this.idEnquete;
		return this.Redirecione(url);

	}

	
	/**
	 * Usada para carregar a pagina passando o idEnquete como parâmetro.
	 * idEnquete esta na URL e também num <input type=hidden /> no jsp
	*/
	public String Redirecione(String url)
	{
		try
		{
			getCurrentResponse().sendRedirect(url);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {}

		return null;
	}

	public String RedirectAfterVotar()
	{

		String url = "/sigaa/portais/turma/Enquete/index.jsf";
		try
		{
			getCurrentResponse().sendRedirect(url);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {}

		return null;
	}

	//Redireciona para pagina de resultados, se o usuário clicar em cancelar
	public String VerResultado()
	{
		String url = "/sigaa/portais/turma/Enquete/EnqueteResultado.jsf?idEnquete="+this.idEnquete;
		return this.Redirecione(url);
	}



}