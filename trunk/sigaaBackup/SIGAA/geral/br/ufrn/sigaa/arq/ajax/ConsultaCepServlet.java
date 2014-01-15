/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '06/09/2007'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.HashMap;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.correios.Logradouro;
import br.ufrn.comum.web.AbstractConsultaCepServlet;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Servlet para buscas por CEP na base dos correios
 *
 * @author Ricardo Wendell
 *
 */
public class ConsultaCepServlet extends AbstractConsultaCepServlet {

	/* (non-Javadoc)
	 * @see br.ufrn.comum.web.AbstractConsultaCepServlet#buscarRegistroLogradouro(br.ufrn.comum.dominio.correios.Logradouro)
	 */
	protected Map<String, String> buscarRegistroLogradouro(Logradouro logradouro) throws DAOException {
		
		// Buscar município cadastrado na base do SIGAA
		Municipio municipio = null;
		MunicipioDao municipioDao = new MunicipioDao();
		try {
			municipio = municipioDao.findUniqueByNome(logradouro.getLocalidade().getNome(), logradouro.getLocalidade().getUf());
		} finally {
			municipioDao.close();
		}
		
		if (municipio == null) {
			return null;
		}
		
		// Popular mapa de valores
		Map<String, String> dadosPersistentes = new HashMap<String, String>(3);
		dadosPersistentes.put("idUf", String.valueOf(municipio.getUnidadeFederativa().getId()));
		dadosPersistentes.put("idMunicipio", String.valueOf(municipio.getId()));
		dadosPersistentes.put("nomeMunicipio", municipio.getNome());
		return dadosPersistentes;
	}
	
}
