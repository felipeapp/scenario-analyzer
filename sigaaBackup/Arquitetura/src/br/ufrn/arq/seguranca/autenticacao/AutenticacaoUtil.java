/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.arq.seguranca.autenticacao;

import br.ufrn.arq.dao.BloqueioDocumentoAutenticadoDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Gera o código de autenticação de acordo com os parametros informados.
 * Usa como semente um número aleatório gerado através de um PRNG (Math.random()).
 * PRNG = Pseudo Random Number Generator (ver google se não souber o que é :) ).
 *
 * @author Gleydson Lima
 *
 */
public class AutenticacaoUtil {

	public final static String TOKEN_REDIRECT = "redirectValidacao";
	
	public static String geraCodigoValidacao(EmissaoDocumentoAutenticado emissao , String semente) throws ArqException {
		String informacao = emissao.getIdentificador() +
		Formatador.getInstance().formatarData(emissao.getDataEmissao()) + emissao.getTipoDocumento() + semente
		+ emissao.getPrng().trim();

		return UFRNUtils.toSHA1Digest(informacao).substring(0,10);
	}

	public static boolean isDocumentoLiberado(int tipoDocumento, Character nivelEnsino) throws DAOException {
		BloqueioDocumentoAutenticadoDAO dao = new BloqueioDocumentoAutenticadoDAO();
		try {
			return dao.isDocumentoLiberado(tipoDocumento, nivelEnsino);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			dao.close();
		}
	}

}
