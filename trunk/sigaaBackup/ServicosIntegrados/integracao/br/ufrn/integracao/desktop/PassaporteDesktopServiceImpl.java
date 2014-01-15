/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/06/2009
 */
package br.ufrn.integracao.desktop;

import java.util.Date;

import javax.jws.WebService;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;



/**
 * Serviço para criação de passaporte para sistemas Desktop.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@WebService
public class PassaporteDesktopServiceImpl implements PassaporteDesktopService {

	/**
	 * <p> Cria um passaporte para uma aplicação desktop. </p>
	 * 
	 * <p>IMPORTANTE: Mantém a compatibilidade com a maneira antiga de ser criar o passaporte.</p>
	 */
	public void criarPassaporte(int idUsuario, String login, int sistemaOrigem, int sistemaDestino) {
		
		criarPassaporteSigaa( idUsuario,  login,  sistemaOrigem,  sistemaDestino, null);
	}
	
	
	/**
	 * <p>Cria um passaporte para uma aplicação desktop.</p>
	 * 
	 * <p>IMPORTANTE: Nova maneira de se criar o passaporte, onde o redirecionamento só ocorre depois que os vínculos do usuário são tratados. <br/>
	 * É como se está utilizando no SIGAA no momento. 21/09/2011 <br/> 
	 * </p>
	 * 
	 */
	public void criarPassaporteSigaa(int idUsuario, String login, int sistemaOrigem, int sistemaDestino, String url) {
		
		PassaporteLogon passaporte = new PassaporteLogon();
		passaporte.setLogin(login);
		passaporte.setIdUsuario(idUsuario);
		passaporte.setSistemaAlvo(sistemaDestino);
		passaporte.setSistemaOrigem(sistemaOrigem);
		long tempo = System.currentTimeMillis();
		passaporte.setValidade(tempo + 60000); // 30s de timeout
		passaporte.setHora(new Date(tempo));
		
		passaporte.setUrl(url);
		
		passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);

		FacadeDelegate facade = new FacadeDelegate("ejb/ServicosFacade");
		try {
			facade.prepare(ArqListaComando.CADASTRAR_PASSAPORTE.getId(), new UsuarioGeral(idUsuario), Sistema.COMUM);
			facade.execute(passaporte, new UsuarioGeral(idUsuario), Sistema.COMUM);
		} catch (NegocioException e) {
			e.printStackTrace();
		} catch (ArqException e) {
			e.printStackTrace();
		}
	}
	

}
