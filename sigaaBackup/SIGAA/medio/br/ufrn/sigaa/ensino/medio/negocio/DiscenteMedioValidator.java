/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 29/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.medio.dao.DiscenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.negocio.DiscenteValidator;

/**
 * Faz as valida��es referentes aos dados de alunos n�vel m�dio.
 * @author Arlindo Rodrigues
 *
 */
public class DiscenteMedioValidator extends DiscenteValidator {
	
	/**
	 * Valida a submiss�o dos dados espec�ficos do aluno de m�dio
	 * @throws DAOException 
	 */
	public static void validarDadosDiscenteMedio(DiscenteMedio discente, boolean discenteAntigo, ListaMensagens lista) throws DAOException {

		if(discenteAntigo){
			if(!ValidatorUtil.isEmpty( discente.getMatricula()))
				ValidatorUtil.validaLong(discente.getMatricula(), "Matr�cula", lista);
			else
				ValidatorUtil.validateRequired(discente.getMatricula(), "Matr�cula", lista);

			validateRequiredId(discente.getStatus(), "Status", lista);			
		} else {
		
			validateRequiredId(discente.getOpcaoTurno().getId(), "Op��o de Turno", lista);
			
		}
		
		// campos obrigatorios
		ValidatorUtil.validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		validateRequiredId(discente.getCurso().getId(), "Curso de Ingresso", lista);
		validateRequiredId(discente.getSerieIngresso().getId(), "S�rie de Ingresso", lista);		
		validateRequiredId(discente.getFormaIngresso().getId(), "Forma de Ingresso", lista);

		/**
		 * verificando se o aluno possui matr�cula ativa em algum curso 
		 * s� � efetuado este teste caso esteja cadastrando o discente e a pessoa j� exista no banco
		 */
		if( discente.getId() == 0 ){

			if (discenteAntigo) {
				validateMaxValue(discente.getAnoIngresso(), CalendarUtils.getAnoAtual(), "Ano de Ingresso", lista);
			} else {
				ValidatorUtil.validateMinValue(discente.getAnoIngresso(), CalendarUtils.getAnoAtual() - 1, "Ano de Ingresso", lista);

				DiscenteMedioDao dao = DAOFactory.getInstance().getDAO( DiscenteMedioDao.class );
				try {
					DiscenteMedio d = dao.findAtivoByPessoa( discente.getPessoa().getId() );
					if( d != null && d.getId() != 0 ) {
						lista.getMensagens().add( new MensagemAviso("Aten��o! N�o � poss�vel continuar o processo de cadastro " +
								" pois j� existe um discente com o status CADASTRADO associado a esta pessoa (mat. " + d.getMatricula() + ").",
								TipoMensagemUFRN.ERROR) );
					}
				} finally {
					dao.close();
				}
			}
			
		}

		if (lista.size() > 0) {
			return;
		}

	}		

}
