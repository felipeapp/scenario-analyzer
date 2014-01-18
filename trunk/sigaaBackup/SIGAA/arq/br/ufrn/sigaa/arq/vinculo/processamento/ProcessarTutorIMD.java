/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoTutorIMD;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * Processar permissões de tutor do IMD
 * 
 * @author Gleydson Lima
 */
public class ProcessarTutorIMD extends ProcessarVinculoExecutor {

	/**
	 * Processa as permissões do tutor do IMD 
     * 
     * @param HttpServletRequest req, DadosProcessamentoVinculos dados
	 * @return
	 * @throws ArqException
	 */
	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {

		TutoriaIMDDao dao = getDAO(TutoriaIMDDao.class, req);

		try {

			List<TutoriaIMD> tutorias = dao.findTutorias(dados.getUsuario().getPessoa().getId());
			if (tutorias.size() > 0) {

				Unidade imd = dao.findByPrimaryKey(ParametroHelper.getInstance().getParametroInt(
						ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL), Unidade.class);

				for (TutoriaIMD tutoria : tutorias) {
					TipoVinculoTutorIMD vinculoTutoria = new TipoVinculoTutorIMD(tutoria);
					tutoria.getTurmaEntrada().getDescricao();
					dados.addVinculo(imd, false, vinculoTutoria);
					break;
				}
				
			}

		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
