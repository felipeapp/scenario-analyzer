/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dominio.EnqueteResposta;
import br.ufrn.sigaa.ava.dominio.EnqueteVotos;

/**
 * Computa os votos de uma Enquete
 * 
 * @author Edson Anibal (ambar@info.ufrn.br)
 */
@Component("enqueteVotos") @Scope("request")
public class EnqueteVotosMBean extends SigaaAbstractController<EnqueteVotos> {

	public EnqueteVotosMBean() throws DAOException, NegocioException {
		this.initObj();
	}

	/**
	 * Inicializa o objeto.
	 * @throws NegocioException
	 */
	private void initObj() throws NegocioException {
		idEnqueteResposta = getParameterInt("idEnqueteResposta");

		if (idEnqueteResposta == null)
			throw new NegocioException("Escolha uma das respostas da enquete!");
		
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		obj = new EnqueteVotos();
		try {
			EnqueteResposta enqueteResposta = turmaDao.findByPrimaryKey(idEnqueteResposta, EnqueteResposta.class);
			obj.setEnqueteResposta(enqueteResposta);
			//obj.setEnquete(enqueteResposta.getEnquete());
			obj.setUsuario(getUsuarioLogado());
			obj.setDataVoto(new Date());
		} catch (Exception e) {
		}

	}

	/**
	 * Recarrega a página passando o idEnquete como parâmetro.
	 * idEnquete está na URL e também em um <input type=hidden /> no jsp.
	 */
	private Integer idEnqueteResposta;

	public String getPegarEnqueteResposta() throws NegocioException {

		// Evita que se perca o objeto quando a página for recarregada para
		// Alterar ou Remover
		if (!"Cadastrar".equals(getConfirmButton()))
			return null;

		this.initObj();
		return null;
	}

	/**
	 * Exibe a página correta de acordo com o parâmetro <code>voltarListagem</code>.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	public String forwardCadastrar() {
		if ("true".equals(getParameter("voltarListagem")))
			return "/ava/Enquete/votar.jsf?id=" + getParameter("id");
		else
			return null;
	}

}
