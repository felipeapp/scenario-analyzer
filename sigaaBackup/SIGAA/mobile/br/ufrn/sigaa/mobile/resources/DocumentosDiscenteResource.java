package br.ufrn.sigaa.mobile.resources;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.jasperreports.engine.JRException;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

@Path("/documentosdiscente")
public class DocumentosDiscenteResource extends SigaaGenericResource{

	public DocumentosDiscenteResource(){
	}
	
	@GET
	@Path("/historico")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getHistorico(){
		Response response = null;
		GerarDocumentosMobile documentos = new GerarDocumentosMobile(request);
		
		request.setAttribute("sistema", Sistema.SIGAA);
		
		try {
			File historico = documentos.getHistoricoDiscente();
			response = Response
		            			.ok()
		            			.type(MediaType.APPLICATION_OCTET_STREAM)
		            			.entity(historico)
		            			.build();
		} catch (ArqException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NegocioException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	@GET
	@Path("/declaracao")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDeclaracao(){
		Response response = null;
		
		DiscenteDao discente = DAOFactory.getInstance().getDAO(DiscenteDao.class);
		try {
			Usuario user = (Usuario) request.getSession().getAttribute("usuario");
			Municipio municipio = discente.findByPK(getDiscenteLogado().getId()).getCurso().getUnidade().getMunicipio();
			user.getDiscenteAtivo().getCurso().getUnidade().setMunicipio(municipio);
			Municipio munic = discente.findByPK(getDiscenteLogado().getId()).getCurso().getMunicipio();
			user.getDiscenteAtivo().getCurso().setMunicipio(munic);
			request.getSession().setAttribute("usuario",user);
		} catch (DAOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		request.getSession();
		GerarDocumentosMobile documentos = new GerarDocumentosMobile(request);
		
		request.setAttribute("sistema", Sistema.SIGAA);
		
		
		try {
			File declaracao = documentos.getDeclaracaoVinculo();
			response = Response
		            			.ok()
		            			.type(MediaType.APPLICATION_OCTET_STREAM)
		            			.entity(declaracao)
		            			.build();
		} catch (ArqException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			discente.close();
		}
		
		return response;
	}
	
}
