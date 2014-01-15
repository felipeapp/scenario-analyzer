package br.ufrn.sigaa.mobile.resources;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ErrorProcessorDelegate;
import br.ufrn.arq.erros.gerencia.MovimentoErro;
import br.ufrn.arq.mobile.exception.GenericMobileException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

@Path("/error")
public class ErrorLoggerResource extends SigaaGenericResource {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response logError(Reader reader) throws IOException {
		Response response = null;

		String errorDetails = readStream(reader);

		int sistema = Sistema.SIGAA;

		MailBody mail = new MailBody();
		mail.setNome("ERRO SIGAA");
		mail.setEmail(br.ufrn.arq.parametrizacao.ParametroHelper.getInstance().getParametro(sistema + "_1_1"));
		mail.setAssunto("Erro Inesperado, Subsistema: SIGAA Mobile");
		mail.setMensagem(errorDetails);
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setRegistrarEnvio(false);

		Mail.send(mail);

		MovimentoErro mov = new MovimentoErro();
		mov.setDetails(errorDetails);
		mov.setErro(new GenericMobileException(errorDetails));
		mov.setUsuarioLogado(getUsuarioLogado());
		mov.setSistema(sistema);

		// TODO Setar subsistema correto
		mov.setSubsistema(SigaaSubsistemas.SIGAA.getId());

		ErrorProcessorDelegate.getInstance().writeError(mov);

		response = Response.created(URI.create("/"+mov.getId())).build();

		return response;
	}

}
