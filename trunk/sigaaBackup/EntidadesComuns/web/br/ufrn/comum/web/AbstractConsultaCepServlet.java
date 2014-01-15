/**
 *
 */
package br.ufrn.comum.web;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.BaseCorreiosDAO;
import br.ufrn.comum.dominio.correios.Logradouro;

/**
 * Servlet base para buscas por CEP na base dos correios. <br>
 * Deve ser extendido em cada subsistema para implementar a consulta
 * as entidades persistentes locais referentes a Municipios e Unidades Federativas
 *
 * @author Ricardo Wendell
 *
 */
public abstract class AbstractConsultaCepServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		BaseCorreiosDAO correiosDao = new BaseCorreiosDAO();

		try {
			String cepString  = req.getParameter("cep");

			// Preparar cep
			Long cep = StringUtils.extractLong(cepString);

			// Buscar logradouro
			if (!isEmpty(cep)) {
				Logradouro logradouro = correiosDao.findLogradouroByCep(cep);

				// Retornar endereço
				res.setHeader("Cache-Control","no-store");
				res.setContentType("text/x-json; charset=ISO-8859-1");
				res.getOutputStream().print( logradouroToJson(logradouro) );
				res.flushBuffer();
			}

		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			correiosDao.close();
		}
	}

	/**
	 * Gera a saída da consulta em formato JSON (Javascript Object Notation)
	 * para o método javascript responsável pelo tratamento da resposta
	 * do servidor (consultas realizadas via AJAX)
	 * 
	 * @param logradouro
	 * @return
	 * @throws DAOException
	 */
	protected String logradouroToJson(Logradouro logradouro) throws DAOException {
		if (logradouro == null) {
			return null;
		}

		Map<String, String> dadosPersistentes = buscarRegistroLogradouro(logradouro);

		// Construir objeto JSON com os dados necessários
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("'cep' : '" +  Formatador.getInstance().formatarCep( logradouro.getCep().intValue() ) + "',\n" );
		builder.append("'logradouro' : '" +  logradouro.getNome().toUpperCase() + "',\n" );
		builder.append("'bairro' : '" +  logradouro.getBairro().getNome().toUpperCase() + "'" );

		if (dadosPersistentes != null) {
			builder.append(",\n" );

			builder.append("'idUf' : '" +  dadosPersistentes.get("idUf") + "',\n" );
			builder.append("'nomeUf' : '" +  dadosPersistentes.get("nomeUf") + "',\n" );
			builder.append("'idMunicipio' : '" +  dadosPersistentes.get("idMunicipio") + "',\n" );
			builder.append("'nomeMunicipio' : '" +  dadosPersistentes.get("nomeMunicipio") + "'\n" );
		}
		builder.append("}");
		return builder.toString();
	}

	/**
	 * Buscar os dados persistentes do municipio de acordo com o
	 * banco local.
	 *
	 * O mapa de retorno deve conter valores para as seguintes chaves: <br> <br>
	 *
	 * idUf: ID da entidade referente a uma Unidade Federativa <br>
	 * idMunicipio: ID da entidade referente a um Municipio <br>
	 * nomeMunicipio:Nome do município no banco local <br>
	 *
	 * @param logradouro
	 * @return
	 * @throws DAOException
	 */
	protected abstract Map<String, String> buscarRegistroLogradouro(Logradouro logradouro) throws DAOException;

}
