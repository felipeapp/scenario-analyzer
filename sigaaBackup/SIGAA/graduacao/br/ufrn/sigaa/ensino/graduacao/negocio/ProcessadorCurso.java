/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * created on 07/02/07
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.CursoInstituicoesEnsinoDao;
import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.CursoInstituicoesEnsino;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;

/**
 * Processador para regras do cadastro de curso de graduação e stricto sensu
 *
 * @author André
 *
 */
public class ProcessadorCurso extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);
		MovimentoCursoGraduacao mc = (MovimentoCursoGraduacao) movimento;

		// Cadastro do arquivo do projeto político-pedagógico
		if (mc.getArquivo() != null) {
			try {
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, mc.getArquivo().getBytes(), mc.getArquivo().getContentType(), mc.getArquivo().getName());

				Curso c = (Curso) mc.getObjMovimentado();
				c.setIdArquivo(idArquivo);
			} catch (Exception e) {
				throw new ArqException(e);
			}
		}


		if (mc.getCodMovimento().equals(SigaaListaComando.CADASTRAR_CURSO_GRADUACAO)) {
			criar(mc);
		} else if (mc.getCodMovimento().equals(SigaaListaComando.ALTERAR_CURSO_GRADUACAO)) {
			Curso c = (Curso) mc.getObjMovimentado();
			if( c.getNivel() == NivelEnsino.GRADUACAO ){
				c.setTipoCursoStricto(null);
			}
			alterar(mc);
		}
		
		/* Cadastro de Instituições de Ensino vinculadas ao curso de stricto sensu pertencente a redes de ensino dentre outras instituições.*/
		InstituicoesEnsinoDao iedao = getDAO(InstituicoesEnsinoDao.class,mc);
		CursoInstituicoesEnsinoDao cieDao = getDAO(CursoInstituicoesEnsinoDao.class, mc);
		try {
			Collection<InstituicoesEnsino> colInstEnsino = iedao.findByCurso(mc.getObjMovimentado().getId());
			for( InstituicoesEnsino ie : mc.getInstituicoesEnsino() ){
				
				CursoInstituicoesEnsino cursoInstituicao = new CursoInstituicoesEnsino();
				cursoInstituicao.setCurso((Curso) mc.getObjMovimentado());
				cursoInstituicao.setInstituicaoEnsino(ie);
				
				if(!colInstEnsino.contains(ie))
					iedao.createNoFlush(cursoInstituicao);
			}
			
			Collection<CursoInstituicoesEnsino> colCursoIe = cieDao.findByCurso(mc.getObjMovimentado().getId());
			for (CursoInstituicoesEnsino cie : colCursoIe) {
				if(!mc.getInstituicoesEnsino().contains(cie.getInstituicaoEnsino()))
					cieDao.remove(cie);
				
			}
		} finally {	
			iedao.close();
			cieDao.close();
		}	
		
		return mc.getObjMovimentado();
	}

	/** Valida os dados antes de persistir.
	 *
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// verificando se não existe convênio
		MovimentoCadastro mc = (MovimentoCadastro) mov;
		Curso c = (Curso) mc.getObjMovimentado();
		CursoDao cdao = getDAO(CursoDao.class, mov);
		try {
			if (c.getNivel() == NivelEnsino.GRADUACAO) {
				if (cdao.existeCursoGraduacao(c)){
					throw new NegocioException("O curso não pode ser cadastrado.<br>" +
					"Já existe um curso com mesmo nome, município e unidade-sede.");
				}
			} else if (NivelEnsino.isAlgumNivelStricto(c.getNivel())) {
				if (cdao.existeCursoStricto(c)){
					throw new NegocioException("O curso não pode ser cadastrado.<br>" +
					"Já existe um curso com mesmo nome, município, unidade-sede e nível.");
				}
			}
		} finally {
			cdao.close();
		}	
	}
}
