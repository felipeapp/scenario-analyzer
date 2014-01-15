/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/11/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.graduacao.ConfirmacaoMatriculaFeriasDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.ConfirmacaoMatriculaFerias;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;

/**
 * Processador responsável pela confirmação das matrículas em turmas de férias. 
 *
 * @author Victor Hugo
 */
public class ProcessadorConfirmacaoMatriculaFerias extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		ConfirmacaoMatriculaFerias obj = movimento.getObjMovimentado();
		ConfirmacaoMatriculaFeriasDao dao = getDAO( ConfirmacaoMatriculaFeriasDao.class, movimento );
		
		if( obj.getId() == 0 ){
			
			
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(obj.getDiscente());
			ConfirmacaoMatriculaFerias confirmacaoAnterior = dao.findByDiscenteTurmaConfirmado(obj.getDiscente(), null, cal.getAnoFeriasVigente(), cal.getPeriodoFeriasVigente());

			if( confirmacaoAnterior != null && confirmacaoAnterior.isConfirmou() ){
				dao.updateField(ConfirmacaoMatriculaFerias.class, confirmacaoAnterior.getId(), "confirmou", false);
				//dao.updateField(MatriculaComponente.class, confirmacaoAnterior.getMatriculaGerada().getId(), "confirmou", false);
				dao.updateField(MatriculaComponente.class, confirmacaoAnterior.getMatriculaGerada().getId(), "situacaoMatricula.id", SituacaoMatricula.EXCLUIDA);
				
				
			}
			
			if( obj.isConfirmou() ){
				MatriculaComponente matricula = criarMatricula(movimento);
				dao.create(matricula);
				obj.setMatriculaGerada(matricula);
			}
			
			
			
			int numero = dao.getNextSeq( "graduacao.matricula_ferias_seq" );
			obj.setNumeroConfirmacao( numero );
			dao.create(obj);
			
		}else{
			
			ConfirmacaoMatriculaFerias confirmacaoAnterior = dao.findByPrimaryKey(obj.getId(), ConfirmacaoMatriculaFerias.class);
			
			if( confirmacaoAnterior.isConfirmou() && !obj.isConfirmou() ){
				//dao.updateField(ConfirmacaoMatriculaFerias.class, confirmacaoAnterior.getId(), "matriculaGerada.id", null);
				dao.updateField(MatriculaComponente.class, confirmacaoAnterior.getMatriculaGerada().getId(), "situacaoMatricula.id", SituacaoMatricula.EXCLUIDA);
				obj.setMatriculaGerada(null);
			}
			
			//se o aluno confirmou a matricula
			if( obj.isConfirmou() ){
			
				//se ele ja havia se matriculado em outra turma é necessário cancelar a matricula
				if( confirmacaoAnterior.getTurma().getId() != obj.getTurma().getId() ){
					dao.updateField(MatriculaComponente.class, confirmacaoAnterior.getMatriculaGerada().getId(), "situacaoMatricula.id", SituacaoMatricula.EXCLUIDA);
					//obj.setMatriculaGerada(null);
					MatriculaComponente matricula = criarMatricula(movimento);
					dao.create(matricula);
					obj.setMatriculaGerada(matricula);
				} 
				
			}
			
			dao.detach(confirmacaoAnterior);
			dao.update(obj);
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		ConfirmacaoMatriculaFerias obj = movimento.getObjMovimentado();
		
		ListaMensagens erros = new ListaMensagens();
		
		if( obj.isConfirmou() ){
			MatriculaComponenteHelper.validarMatriculaComponente(obj.getDiscente(), criarMatricula(movimento), erros);
			MatriculaGraduacaoValidator.validarAlunoOutroCampus(obj.getTurma(), obj.getDiscente(), erros);
		}
		
		checkValidation(erros);

	}
	
	/**
	 * Cria uma matrícula em  Matrícula componente a partir das informações contidas no movimento
	 * passado como argumento.
	 * 
	 * @param mov
	 * @return MatriculaComponente
	 */
	private MatriculaComponente criarMatricula(MovimentoCadastro mov) {
		ConfirmacaoMatriculaFerias obj = mov.getObjMovimentado();
		
		MatriculaComponente matricula = new MatriculaComponente();
		matricula.setSituacaoMatricula( SituacaoMatricula.EM_ESPERA );
		matricula.setDiscente( obj.getDiscente().getDiscente() );
		matricula.setTurma( obj.getTurma() );
		matricula.setAno( (short) obj.getTurma().getAno() );
		matricula.setPeriodo( (byte) obj.getTurma().getPeriodo() );
		matricula.setComponente( obj.getTurma().getDisciplina() );
		matricula.setDetalhesComponente( obj.getTurma().getDisciplina().getDetalhes() );
		matricula.setDataCadastro(new Date());
		matricula.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
		return matricula;
	}

}
