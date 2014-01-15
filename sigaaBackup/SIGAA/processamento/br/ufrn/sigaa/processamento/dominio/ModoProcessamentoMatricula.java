/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 12/01/2009 
 *
 */
package br.ufrn.sigaa.processamento.dominio;

import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaEadDao;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoFeriasDao;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaMusicaDao;

/**
 * Tipo de processamento de matrícula a ser realizado.
 * 
 * @author David Pereira
 *
 */
public enum ModoProcessamentoMatricula {
	
	GRADUACAO(ProcessamentoMatriculaGraduacaoDao.class),
	GRADUACAO_FERIAS(ProcessamentoMatriculaGraduacaoFeriasDao.class),
	EAD(ProcessamentoMatriculaEadDao.class), 
	MUSICA(ProcessamentoMatriculaMusicaDao.class);
	
	private Class<? extends ProcessamentoMatriculaDao> classeDao; 
	
	private ModoProcessamentoMatricula(Class<? extends ProcessamentoMatriculaDao> classeDao) {
		this.classeDao = classeDao;
	}

	public Class<? extends ProcessamentoMatriculaDao> getClasseDao() {
		return classeDao;
	}

}
