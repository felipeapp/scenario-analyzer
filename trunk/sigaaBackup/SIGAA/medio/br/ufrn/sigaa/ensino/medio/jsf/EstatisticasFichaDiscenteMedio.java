package br.ufrn.sigaa.ensino.medio.jsf;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jfree.data.general.DefaultPieDataset;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * Classe respons�vel pelo DatasetProducer do Cewolf 
 * para exibir as estat�sticas na ficha do aluno do m�dio.
 * @author Suelton Miguel
 *
 */
public class EstatisticasFichaDiscenteMedio implements DatasetProducer, Serializable {

	/**Veri�vel que indica que o gr�fico gerado usar� os status dos discentes da turma como dado.*/
	public static final int STATUS_DISCENTE = 1;
	
	/**Veri�vel que indica que o gr�fico gerado usar� a repet�ncia dos discentes da turma como dado.*/
	public static final int SITUACAO_REPETENCIA = 2;
	
	/**Veri�vel que indica que o gr�fico gerado usar� o sexo dos discentes da turma como dado.*/
	public static final int SEXO_DISCENTE = 3;
	
	@Override
	public String getProducerId() {
		return "EstatisticasFichaDiscenteMedio";
	}

	@Override
	public boolean hasExpired(Map params, Date data) {
		return (System.currentTimeMillis() - data.getTime()) > 5000;
	}

	@Override
	public Object produceDataset(Map params) throws DatasetProduceException {
		
		@SuppressWarnings("unchecked")
		Collection<MatriculaDiscenteSerie> alunos = (Collection<MatriculaDiscenteSerie>) params.get("alunos");
		
		Long tipoDados = (Long) params.get("tipo_dados");
		
		DefaultPieDataset ds = new DefaultPieDataset();
		
		if (tipoDados == STATUS_DISCENTE) {		
			int ativos = 0;
			int concluidos = 0;
			int trancados = 0;
			int cancelados = 0;
			int excluidos = 0;
			int ativosDependencia = 0;
			
			for(MatriculaDiscenteSerie aluno:alunos) {
				
				switch (aluno.getDiscenteMedio().getStatus()) {
				case StatusDiscente.ATIVO:
					ativos++;
					break;
				case StatusDiscente.CONCLUIDO:
					concluidos++;
					break;
				case StatusDiscente.TRANCADO:
					trancados++;
					break;
				case StatusDiscente.CANCELADO:
					cancelados++;
					break;
				case StatusDiscente.EXCLUIDO:
					excluidos++;					
					break;
				case StatusDiscente.ATIVO_DEPENDENCIA:
					ativosDependencia++;
					break;
				default:
					break;
				}
			}
			
			if (ativos > 0)
				ds.setValue("Ativos ("+ativos+")", ativos);
			
			if (concluidos > 0)
				ds.setValue("Conclu�dos ("+concluidos+")", concluidos);
				
			if (trancados > 0)
				ds.setValue("Trancados ("+trancados+")", trancados);
			
			if (cancelados > 0)
				ds.setValue("Cancelados ("+cancelados+")", cancelados);
			
			if (excluidos > 0)
				ds.setValue("Exclu�dos ("+excluidos+")", excluidos);
				
			if (ativosDependencia > 0)
				ds.setValue("Ativos em Depend�ncia ("+ativosDependencia+")", ativosDependencia);	
		
		} 
		else if (tipoDados == SITUACAO_REPETENCIA) {
			int repetentes = 0;
			int naoRepetentes = 0;
			for(MatriculaDiscenteSerie aluno:alunos) {
				if (aluno.isRepetente())
					repetentes++;
				else
					naoRepetentes++;
			}
			ds.setValue("Repetente ("+repetentes+")", repetentes);
			ds.setValue("N�o Repetente ("+naoRepetentes+")", naoRepetentes);
		}
		else if (tipoDados == SEXO_DISCENTE) {
			int masculino = 0;
			int feminino = 0;
			int naoInformado = 0;
			
			for(MatriculaDiscenteSerie aluno:alunos) {
				if (aluno.getDiscenteMedio().getPessoa().getSexo() == 'M')
					masculino++;
				else if ((aluno.getDiscenteMedio().getPessoa().getSexo() == 'F'))
					feminino++;
				else
					naoInformado++;
			}
			
			ds.setValue("Masculino ("+masculino+")", masculino);
			ds.setValue("Feminino ("+feminino+")", feminino);
			if (naoInformado > 0)
				ds.setValue("N�o Informado ("+naoInformado+")", naoInformado);
		}
		
		return ds;
	}

}
