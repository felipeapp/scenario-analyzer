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

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino_rede.dao.DadosCursoRedeDao;

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
		InstituicoesEnsinoDao iedao = getDAO(InstituicoesEnsinoDao.class,mc);
		DadosCursoRedeDao cieDao = getDAO(DadosCursoRedeDao.class, mc);
		
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
			// o curso foi cadastrado como ativo. caso esteja sendo cadastrado um curso inativo, atualizar o status do curso. 
			if (mc.isCadastrarCursoInativo()) {
				iedao.updateField(Curso.class, mc.getObjMovimentado().getId(), "ativo", false);
			}
		} else if (mc.getCodMovimento().equals(SigaaListaComando.ALTERAR_CURSO_GRADUACAO)) {
			Curso c = (Curso) mc.getObjMovimentado();
			if( c.getNivel() == NivelEnsino.GRADUACAO ){
				c.setTipoCursoStricto(null);
			}
			alterar(mc);
		}
		
		/* Cadastro de Instituições de Ensino vinculadas ao curso de stricto sensu pertencente a redes de ensino dentre outras instituições.*/
		
		/*
		try {
			Collection<InstituicoesEnsino> colInstEnsino = iedao.findByCurso(mc.getObjMovimentado().getId());
			for( InstituicoesEnsino ie : mc.getInstituicoesEnsino() ){
				
				DadosCursoRede cursoInstituicao = new DadosCursoRede();
				cursoInstituicao.setCurso((Curso) mc.getObjMovimentado());
				cursoInstituicao.setInstituicaoEnsino(ie);
				
				if(!colInstEnsino.contains(ie))
					iedao.createNoFlush(cursoInstituicao);
			}
			
			Collection<DadosCursoRede> colCursoIe = cieDao.findByCurso(mc.getObjMovimentado().getId());
			for (DadosCursoRede cie : colCursoIe) {
				if(!mc.getInstituicoesEnsino().contains(cie.getInstituicaoEnsino()))
					cieDao.remove(cie);
				
			}
		} finally {	
			iedao.close();
			cieDao.close();
		}	
		*/
		return mc.getObjMovimentado();
	}

	/** Valida os dados antes de persistir.
	 *
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// verificando se não existe convênio e curso ativo com o mesmo nome
		MovimentoCursoGraduacao mc = (MovimentoCursoGraduacao) mov;
		Curso c = mc.getObjMovimentado();
		CursoDao cdao = getDAO(CursoDao.class, mov);
		try {
			if (c.getNivel() == NivelEnsino.GRADUACAO) {
				if (!mc.isCadastrarCursoInativo() && c.isAtivo() && cdao.existeCursoGraduacao(c)){
					throw new NegocioException("O curso não pode ser cadastrado. " +
					"Já existe um curso ativo com mesmo nome, município e unidade-sede.");
				}
			} else if (NivelEnsino.isAlgumNivelStricto(c.getNivel())) {
				if (cdao.existeCursoStricto(c)){
					throw new NegocioException("O curso não pode ser cadastrado. " +
					"Já existe um curso ativo com mesmo nome, município, unidade-sede e nível.");
				}
			}
		} finally {
			cdao.close();
		}	
	}
}
