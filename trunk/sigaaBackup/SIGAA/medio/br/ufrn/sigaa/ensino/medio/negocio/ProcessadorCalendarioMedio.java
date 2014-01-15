/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 12/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.medio.dominio.CalendarioRegra;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.negocio.CalendarioMov;

/**
 * Executa atualiza��o do calend�rio acad�mico do n�vel m�dio.
 * 
 * @author Arlindo
 *
 */
public class ProcessadorCalendarioMedio extends ProcessadorCadastro {
	
	/** Valida os objetos */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		// verificar se o calend�rio for de gradua��o se o usu�rio que est� autorizando � administrador DAE

		CalendarioMov cMov = (CalendarioMov) mov;
		String mensagem = "Voc� n�o tem permiss�o para alterar este calend�rio";
		
		CalendarioAcademico calendario = ( CalendarioAcademico) cMov.getObjMovimentado();

		if ( !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.SECRETARIA_MEDIO)) {
			throw new SegurancaException(mensagem);
		}
		
		ListaMensagens erros = new ListaMensagens();
		// verifica a 
		@SuppressWarnings("unchecked")
		Collection<CalendarioRegra> calRegras = (Collection<CalendarioRegra>) cMov.getObjAuxiliar();
		if (ValidatorUtil.isNotEmpty(calRegras)){
			for (CalendarioRegra calRegra : calRegras){
				ValidatorUtil.validaInicioFim(calRegra.getDataInicio(), calRegra.getDataFim(), calRegra.getRegra().getTitulo(), erros);
				
				if (calRegra.getDataInicio() != null && calRegra.getDataInicio().getTime() < calendario.getInicioPeriodoLetivo().getTime())
					erros.addErro("A data de in�cio da unidade n�o pode ser anterior a data de in�cio do ano letivo.");
				
				if (calRegra.getDataFim() != null && calRegra.getDataFim().getTime() > calendario.getFimPeriodoLetivo().getTime())
					erros.addErro("A data de fim da unidade n�o pode ser posterior a data de fim do ano letivo.");
			}
		}
		
		checkValidation(erros);
		
		/*
		 * verificando duplicidade de calend�rio
		 */
		if( cMov.getCodMovimento().equals( SigaaListaComando.CADASTRAR_CALENDARIO_MEDIO ) ){

			CalendarioAcademico calendarioExistente = CalendarioAcademicoHelper.getCalendarioExato( calendario.getAno(), 
					calendario.getPeriodo(), calendario.getUnidade(), calendario.getNivel(), calendario.getModalidade(), 
					calendario.getConvenio(), calendario.getCurso(), null );
			
			Unidade unidade = calendario.getUnidade();

			if( calendarioExistente != null && calendarioExistente.getUnidade().getId() == unidade.getId() )
				throw new NegocioException("J� existe um calend�rio cadastrado para esta unidade no ano selecionado.");
		}

	}


	/**
	 * Atualiza calend�rio , removendo separadamente os eventos extras
	 */	
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		CalendarioMov cMov = (CalendarioMov) movimento;
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class, cMov);
		try {
			validate(movimento);
	
			CalendarioAcademico cal = (CalendarioAcademico) cMov.getObjMovimentado();
			if (cal.isVigente()) {
				// tratando vig�ncia dos calend�rios
				CalendarioAcademico atualVigente =  dao.findByParametros(null, null, cal.getUnidade(),
						cal.getNivel(), cal.getModalidade(), cal.getConvenio(), cal.getCurso(), null);
				if ( atualVigente != null &&  atualVigente.getId() != cal.getId())
					dao.updateField(CalendarioAcademico.class, atualVigente.getId(), "vigente", false);
				else
					dao.detach(atualVigente);
			}
	
			// update
			if( cMov.getCodMovimento().equals( SigaaListaComando.CADASTRAR_CALENDARIO_MEDIO ) )
				dao.create(cal);
			else if( cMov.getCodMovimento().equals( SigaaListaComando.ALTERAR_CALENDARIO_MEDIO ) )
				dao.update(cal);
			else
				throw new NegocioException("Opera��o n�o suportada.");
			
			@SuppressWarnings("unchecked")
			Collection<CalendarioRegra> calRegra = (Collection<CalendarioRegra>) cMov.getObjAuxiliar();
			if (ValidatorUtil.isNotEmpty(calRegra))
				dao.getHibernateTemplate().saveOrUpdateAll(calRegra);
			
			return cal;
			
		} finally {
			if (dao != null)
				dao.close();
		}			
	}		

}
