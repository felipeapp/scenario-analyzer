/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Faz as validações referentes aos dados de alunos nível médio.
 * @author Arlindo Rodrigues
 *
 */
public class DiscenteMedioValidator extends DiscenteValidator {
	
	/**
	 * Valida a submissão dos dados específicos do aluno de médio
	 * @throws DAOException 
	 */
	public static void validarDadosDiscenteMedio(DiscenteMedio discente, boolean discenteAntigo, ListaMensagens lista) throws DAOException {

		if(discenteAntigo){
			if(!ValidatorUtil.isEmpty( discente.getMatricula()))
				ValidatorUtil.validaLong(discente.getMatricula(), "Matrícula", lista);
			else
				ValidatorUtil.validateRequired(discente.getMatricula(), "Matrícula", lista);

			validateRequiredId(discente.getStatus(), "Status", lista);			
		} else {
		
			validateRequiredId(discente.getOpcaoTurno().getId(), "Opção de Turno", lista);
			
		}
		
		// campos obrigatorios
		ValidatorUtil.validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		validateRequiredId(discente.getCurso().getId(), "Curso de Ingresso", lista);
		validateRequiredId(discente.getSerieIngresso().getId(), "Série de Ingresso", lista);		
		validateRequiredId(discente.getFormaIngresso().getId(), "Forma de Ingresso", lista);

		/**
		 * verificando se o aluno possui matrícula ativa em algum curso 
		 * só é efetuado este teste caso esteja cadastrando o discente e a pessoa já exista no banco
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
						lista.getMensagens().add( new MensagemAviso("Atenção! Não é possível continuar o processo de cadastro " +
								" pois já existe um discente com o status CADASTRADO associado a esta pessoa (mat. " + d.getMatricula() + ").",
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
