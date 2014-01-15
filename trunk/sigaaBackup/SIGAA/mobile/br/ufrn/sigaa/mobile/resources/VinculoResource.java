package br.ufrn.sigaa.mobile.resources;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mobile.utils.JSONProcessor;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.mobile.dto.ListaVinculosUsuario;
import br.ufrn.sigaa.mobile.dto.VinculoDTO;
import br.ufrn.sigaa.mobile.dto.VinculoDiscenteDTO;
import br.ufrn.sigaa.mobile.dto.VinculoDocenteDTO;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Resource para operações com vínculos de usuário
 * 
 * @author bernardo
 * 
 */
@Path("/listavinculosusuario")
public class VinculoResource extends SigaaGenericResource {

	/**
	 * Atualiza o vínculo do usuário logado de acordo como número passado.
	 * 
	 * @param numero
	 * @return
	 */

	private VinculoDTO parseVinculo(Reader reader) throws IOException {

		String vinculoJSON = readStream(reader);

		VinculoDTO vinculo = JSONProcessor.toObject(vinculoJSON, VinculoDTO.class);

		return vinculo;
	}

	@PUT
	@Produces(MediaType.WILDCARD)
	public Response atualizarVinculoUsuario(Reader reader) throws DAOException {
		Usuario usuario = getUsuarioLogado();

		Response response = Response.noContent().build();

		VinculoDTO vinculo;
		try {
			
			String vinculoJSON = readStream(reader);

			ListaVinculosUsuario vinculos = JSONProcessor.toObject(vinculoJSON, ListaVinculosUsuario.class);
			
			//Sempre vem um único vinculo
			vinculo = vinculos.getAll().get(0);

			usuario.setVinculoAtivo(vinculo.getNumero());
			
			//Necessário para acessar corretamente as páginas web a partir do aplicativo
			VinculoUsuario.popularVinculoAtivo(usuario);
			
			if (usuario.getVinculoAtivo().isVinculoServidor()) {
				usuario.setServidor(usuario.getVinculoAtivo().getServidor());
			} else if (usuario.getVinculoAtivo().isVinculoDiscente()) {
				usuario.setDiscente(usuario.getVinculoAtivo().getDiscente().getDiscente());
			} /*else if (usuario.getVinculoAtivo().isVinculoTutorOrientador()) {
				usuario.setTutor(usuario.getVinculoAtivo().getTutor());
			} else if (usuario.getVinculoAtivo().getTipoVinculo().isFamiliar()){
				usuario.setDiscente(usuario.getVinculoAtivo().getFamiliar().getDiscenteMedio().getDiscente());
			}*/
			
			request.getSession().setAttribute("usuario", usuario);

			response = Response.ok().build();

		} catch (IOException e) {
			response = Response.serverError().entity(e.getMessage()).build();
		}
		return response;

	}

	/**
	 * Realiza logon no SIGAA através de uma requisição HTTP utilizando o método
	 * POST. Retorna uma representação em JSON do usuário logado ou erro 403
	 * (Forbidden) caso o logon não tenha sido realizado com sucesso.
	 * 
	 * @param login
	 * @param senha
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws Exception
	 */

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVinculosUsuario(@QueryParam("ownerId") final Integer userId) throws ArqException, NegocioException {
		Response response = null;

		Usuario usuario;

		try {

			usuario = getUsuarioLogado();

			if (usuario.getId() == userId) {

				ListaVinculosUsuario listaVinculos = processarVinculos(usuario);

				response = Response.ok(JSONProcessor.toJSON(listaVinculos)).build();
			} else {
				response = Response.status(Status.BAD_REQUEST).entity("Código do usuário inconsistente com o usuário logado").build();
			}

		} catch (Exception e) {
			response = Response.serverError().entity(e.getMessage()).build();
		}
		return response;

	}

	public ListaVinculosUsuario processarVinculos(Usuario usuario) throws ArqException {

		List<VinculoUsuario> vinculos = getVinculosAcessoMobile(usuario);
		ListaVinculosUsuario listaVinculos = null;

		if (vinculos != null && !vinculos.isEmpty()) {

			usuario.setVinculos(vinculos);
			
			listaVinculos = montarListaVinculos(usuario, vinculos);
		}

		return listaVinculos;
	}

	public static int carregarInformacoesVinculos(Usuario usuario) throws ArqException {

		List<VinculoUsuario> vinculosMobile = getVinculosAcessoMobile(usuario);

		int numeroVinculos = (vinculosMobile != null ? vinculosMobile.size() : 0);

		if (numeroVinculos == 1) {
			usuario.setVinculoAtivo(vinculosMobile.get(0));

			if (usuario.getDiscenteAtivo() != null) {
				usuario.setNivelEnsino(usuario.getDiscenteAtivo().getNivel());
			}
		}

		return numeroVinculos;

	}

	public static List<VinculoUsuario> getVinculosAcessoMobile(Usuario usuario) throws ArqException {
		List<VinculoUsuario> vinculosAll = VinculoUsuario.processarVinculosUsuario(usuario, null);

		List<VinculoUsuario> vinculosAcesso = new ArrayList<VinculoUsuario>();

		for (VinculoUsuario vinculo : vinculosAll) {
			if (vinculo.isPrioritario() && (vinculo.isVinculoDiscente() || (vinculo.isSomenteVinculoServidor() && vinculo.getServidor().isDocente()))) {
				vinculosAcesso.add(vinculo);
			}
		}

		return vinculosAcesso;

	}

	public ListaVinculosUsuario montarListaVinculos(Usuario usuario, List<VinculoUsuario> vinculos) {
		ListaVinculosUsuario lista = new ListaVinculosUsuario();

		for (VinculoUsuario vinculo : vinculos) {
			if (vinculo.isAtivo()) {
				VinculoDTO vinculoDto = montarVinculo(usuario, vinculo);

				lista.addVinculo(vinculoDto);
			}
		}

		return lista;
	}

	public static VinculoDTO montarVinculo(Usuario usuario, VinculoUsuario vinculo) {
		VinculoDTO vinculoDto;

		if (vinculo.getTipoVinculo().isDiscente()) {
			vinculoDto = montarVinculoDiscente(usuario, vinculo);

		} else if (vinculo.getServidor() != null && vinculo.getServidor().isDocente()) {
			vinculoDto = montarVinculoDocente(usuario, vinculo);

		} else {
			vinculoDto = montarVinculoServidor(usuario, vinculo);

		}

		return vinculoDto;

	}

	public static VinculoDTO montarVinculoServidor(Usuario usuario, VinculoUsuario vinculo) {
		Servidor docente = vinculo.getServidor();

		VinculoDTO vinculoDto = new VinculoDTO();

		// Verificar quais os outros tipos de vinculo que podem
		// acessar o sistema
		vinculoDto.setNumero(vinculo.getNumero());
		vinculoDto.setIdUsuario(usuario.getId());
		vinculoDto.setId(docente.getId());

		return vinculoDto;
	}

	public static VinculoDocenteDTO montarVinculoDocente(Usuario usuario, VinculoUsuario vinculo) {
		VinculoDocenteDTO docenteDto = new VinculoDocenteDTO();
		Servidor docente = vinculo.getServidor();
		docenteDto.setIdUsuario(usuario.getId());

		docenteDto.setId(docente.getId());
		docenteDto.setNumero(vinculo.getNumero());
		docenteDto.setSiape(vinculo.getTipoVinculo().getIdentificador().toString());
		docenteDto.setLotacao(vinculo.getTipoVinculo().getOutrasInformacoes());
		return docenteDto;
	}

	public static VinculoDiscenteDTO montarVinculoDiscente(Usuario usuario, VinculoUsuario vinculo) {
		VinculoDiscenteDTO discenteDto = new VinculoDiscenteDTO();
		DiscenteAdapter discente = vinculo.getDiscente();

		discenteDto.setNumero(vinculo.getNumero());
		discenteDto.setIdUsuario(usuario.getId());
		discenteDto.setId(discente.getId());
		discenteDto.setMatricula(vinculo.getTipoVinculo().getIdentificador().toString());
		discenteDto.setCurso(vinculo.getTipoVinculo().getOutrasInformacoes());
		return discenteDto;
	}
}
