/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.TipoCaptcaoFrequencia;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoFrequenciaPlanilha;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * Classe que implementa o processador para lan�ar todas as frequ�ncias dos
 * alunos de uma turma.
 *
 * @author Fred de Castro
 *
 */
public class ProcessadorFrequenciaPlanilha extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate(mov);
		
		MovimentoFrequenciaPlanilha personalMov = (MovimentoFrequenciaPlanilha) mov;
		
		GenericDAO dao = null;

		List <Object []> freqs = personalMov.getListagemPlanilha();
		Turma turma = personalMov.getTurma();
		
		try {
			dao = getGenericDAO(personalMov);
			
			for (Object [] f : freqs)
				if (Boolean.parseBoolean (""+f[7]) == true){
					
					int id = 0;
					if ( f[6] != null )
						id = Integer.parseInt(""+f[6]);
					
					if (id > 0){
						int faltas = Integer.parseInt(""+f[5]);
						dao.updateField(FrequenciaAluno.class, id, "faltas", faltas);
					} else {
						
						int idDiscente = Integer.parseInt(""+f[9]);
						short faltas = Short.parseShort(""+f[5]);
						short horarios = Short.parseShort(""+f[8]);
						Date data = (Date) f[10];
						
						FrequenciaAluno freq = new FrequenciaAluno ();
						freq.setDiscente(new Discente (idDiscente));
						freq.setTurma(turma);
						freq.setFaltas(faltas);
						freq.setHorarios(horarios);
						freq.setTipoCaptcaoFrequencia(TipoCaptcaoFrequencia.TURMA_VIRTUAL);
						freq.setData(data);
						
						dao.create(freq);
					}
				}
			
		} finally {
			if (dao != null)
				dao.close();
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoFrequenciaPlanilha personalMov = (MovimentoFrequenciaPlanilha) mov;
		
		if (personalMov.getTurma() == null)
			throw new NegocioException ("Uma turma deve ser selecionada.");
		
		List <Object []> freqs = personalMov.getListagemPlanilha();
		List <Object []> aux = new ArrayList <Object []> ();
		
		boolean ok = false;
		
		try {
			for (Object [] f : freqs)
				if (Boolean.parseBoolean(""+f[7]) == true && (!StringUtils.isEmpty(""+f[5]))){
					
					if (Integer.parseInt(""+f[5]) < 0)
						throw new NegocioException ("Os n�meros devem ser maiores ou iguais a zero.");
					
					if (Integer.parseInt(""+f[5]) > Integer.parseInt(""+f[8]))
						throw new NegocioException ("A aula do dia " + new SimpleDateFormat("dd/MM/yyyy").format(f[10]) + " pode ter, no m�ximo, " + f[8] + " faltas por aluno.");
					
					if ( ((Date) f[10]).after(new Date()) )
						throw new NegocioException ("N�o � poss�vel cadastrar frequ�ncias para datas posteriores � atual.");

					aux.add(f);
					ok = true;
				}
		} catch (NumberFormatException e){
			throw new NegocioException ("Digite somente n�meros na planilha.");
		}
		
		personalMov.setListagemPlanilha(aux);
		
		if (!ok)
			throw new NegocioException ("Pelo menos uma frequ�ncia deve ser alterada.");
	}
}