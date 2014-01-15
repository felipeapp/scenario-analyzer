/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 17/10/2006
*/
package br.ufrn.sigaa.ensino.form;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe que trabalha com os atributos necessários para os casos de uso que utilizam a 
 * classe AlteracaoMatrícula feitos em Struts.
 */
public class AlteracaoMatriculaForm extends SigaaForm<AlteracaoMatricula> {

	/** Constante responsável consulta do discente pela matrícula. */
	public final String TIPO_BUSCA_MATRICULA_ALUNO = "1";

	/** Constante responsável consulta do discente pelo nome do aluno. */
	public final String TIPO_BUSCA_NOME_ALUNO = "2";

	/** Parâmetro usado para a realização da consultas */
	private int tipoBusca;

	public AlteracaoMatriculaForm() {
		try {
			clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear() throws Exception {
		this.obj = new AlteracaoMatricula();
		obj.setMatricula(new MatriculaComponente());
		obj.getMatricula().setDiscente(new Discente());
		obj.getMatricula().setTurma(new Turma());
		obj.setSituacaoAntiga(new SituacaoMatricula());
		obj.setSituacaoNova(new SituacaoMatricula());
		obj.setUsuario(new Usuario());
	}

	@Override
	public Collection<Discente> customSearch(HttpServletRequest req) throws ArqException {

		DiscenteDao dao = getDAO(DiscenteDao.class, req);
		Collection<Discente> lista = null;

		try {
			String tipoBusca = req.getParameter("tipoBusca");
			if (TIPO_BUSCA_MATRICULA_ALUNO.equals(tipoBusca)) {
				long matricula = RequestUtils.getIntParameter(req, tipoBusca);
				try {
					matricula = Long.parseLong(req.getParameter("obj.matricula.discente.matricula"));
				} catch (Exception e) {
				}
				Discente aluno = dao.findAtivosByMatricula(matricula);
				lista = new ArrayList<Discente>();
				if (aluno != null) {
					lista.add(aluno);
				}
			} else if (TIPO_BUSCA_NOME_ALUNO.equals(tipoBusca)) {
				if( !getSubSistemaCorrente(req).equals(SigaaSubsistemas.LATO_SENSU) && !getSubSistemaCorrente(req).equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) )
					lista = dao.findByNome(req.getParameter("obj.matricula.discente.pessoa.nome"), getUnidadeGestora(req),
							getNivelEnsino(req), getPaging(req));
				else
					lista = dao.findByNome(req.getParameter("obj.matricula.discente.pessoa.nome"), 0, getNivelEnsino(req), getPaging(req));
			} 
		} finally {
			dao.close();
		}

		return lista;
	}

	/**
	 * @return the tipoBusca
	 */
	public int getTipoBusca() {
		return tipoBusca;
	}

	/**
	 * @param tipoBusca the tipoBusca to set
	 */
	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

}
