package br.ufrn.sigaa.test.dao.monitoria;

import br.ufrn.sigaa.arq.test.MassaTesteIds;
import br.ufrn.sigaa.arq.test.SigaaTestCase;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EnvioFrequencia;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

public class MonitoriaTestCase extends SigaaTestCase {

	protected DiscenteMonitoria dm1;
	
	protected EnvioFrequencia ef;
	
	protected Servidor servidor;
	
	public void setUp() throws Exception {
		super.setUp();
		
		dm1 = new DiscenteMonitoria(getIntMassa(MassaTesteIds.DISCENTE_MONITORIA_1));
		
		servidor = new Servidor(getIntMassa(MassaTesteIds.ORIENTADOR_MONITORIA));
		
		ef = new EnvioFrequencia();
		ef.setMes(getIntMassa(MassaTesteIds.MES_ATIVIDADE_MONITORIA));
		ef.setAno(getIntMassa(MassaTesteIds.ANO_ATIVIDADE_MONITORIA));
	}
	
	
	
}
