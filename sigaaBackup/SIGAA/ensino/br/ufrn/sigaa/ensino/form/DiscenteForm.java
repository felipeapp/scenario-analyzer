/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 20/09/2006
*/
package br.ufrn.sigaa.ensino.form;

import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.DiscenteLatoDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Representa os formulários para o caso de uso de
 * cadastro de alunos realizado em dois passos.
 * Nesse caso de uso o primeiro passo com os dados pessoais
 * é realizado por PessoaForm e PessoaAction
 * @author Andre M Dantas
 *
 */

public class DiscenteForm extends SigaaForm<DiscenteAdapter> {

	/** Objeto de domínio encapsulado */
	private DiscenteAdapter discente;

	/** Boleano que que serve pra indicar se o discente é antigo ou não. */
	private boolean discenteAntigo = false;
	
	/** Arquivo digitalizado do histórico do discente antigo */
	private FormFile arquivoHistorico;
	
	public DiscenteForm() {
		this.discente = new Discente();
		this.cursoLato = new CursoLato();
		registerSearchData("tipoBusca", "discente.matricula", "discente.pessoa.nome", "discente.anoIngresso", "discente.turmaEntradaTecnico.id", "cursoLato.id");
	}

	/** Atributo usado somente para busca pelo GESTOR_LATO */
	private CursoLato cursoLato;

	/**
	 * Esse método tem a finalidade de instância o objeto se não for 
	 * um objeto da classe.
	 * 
	 * @param subClasse
	 * @throws Exception
	 */
	public void initDiscente(Class<?> subClasse) throws Exception {
		if (subClasse != null && !subClasse.equals(this.discente.getClass())) {
			this.discente = (DiscenteAdapter) subClasse.newInstance();
		}
	}

	/**
	 * Esse método tem como finalidade realizar algumas verificações com o intuito
	 * de setar algumas informações necessárias para o DiscenteTecnico.
	 * 
	 * @param mapping
	 * @param request
	 */
	public void init(ActionMapping mapping, HttpServletRequest request) {
		if (getSubSistemaCorrente(request).equals(SigaaSubsistemas.TECNICO)
				|| getSubSistemaCorrente(request).equals(SigaaSubsistemas.MEDIO)
				|| getSubSistemaCorrente(request).equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)) {
			DiscenteTecnico dt = (DiscenteTecnico) discente;
			if (dt.getTurmaEntradaTecnico() == null)
				dt.setTurmaEntradaTecnico(new TurmaEntradaTecnico());
			if (dt.getFormaIngresso() == null)
				dt.setFormaIngresso(new FormaIngresso());
		}
	}

	public DiscenteAdapter getDiscente() {
		return  discente;
	}

	public void setDiscente(DiscenteAdapter disc) {
		this.discente = disc;
	}

	public DiscenteTecnico getDiscenteTecnico() {
		return (DiscenteTecnico) getDiscente();
	}

	public DiscenteLato getDiscenteLato() {
		return (DiscenteLato) getDiscente();
	}

	@SuppressWarnings("cast")
	@Override
	public Collection<Discente> customSearch(HttpServletRequest req) throws ArqException {
		String FIND_MATRICULA = "1";
		String FIND_NOME_DISCENTE = "2";
		String FIND_ANO_INGRESSO = "3";
		String FIND_TURMA_ENTRADA = "4";
		String FIND_TODOS = "5";
		String FIND_CURSO_LATO = "6";

		DadosAcesso acesso = (DadosAcesso) req.getSession().getAttribute("acesso");

		DiscenteTecnicoDao dao = (DiscenteTecnicoDao) getDAO(DiscenteTecnicoDao.class,req);
		DiscenteLatoDao daoLato = (DiscenteLatoDao) getDAO(DiscenteLatoDao.class, req);
		Collection<Discente> lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");
			if(tipoBusca == null)
				return lista;

			// se a busca for para histórico deve-se considerar todos os status do aluno
			String forward = (String) req.getSession().getAttribute("forward");

			if (FIND_MATRICULA.equals(tipoBusca)) {
				String mat = getSearchItem(req, "discente.matricula");
				if (mat != null && !mat.trim().equals("")) {
					long matricula = 0;
					try {
						matricula = Long.parseLong(mat);
					} catch (Exception e) { }
					DiscenteAdapter disc = dao.findByMatricula(matricula, getNivelEnsino(req));

					lista = new HashSet<Discente>();
					if (disc != null)
						lista.add(disc.getDiscente());
				}
			} else if (FIND_NOME_DISCENTE.equals(tipoBusca)) {
				String searchItem = getSearchItem(req, "discente.pessoa.nome");
				if(searchItem.trim().equals("")){
					addMensagemErro("Informe um nome para a busca.", req);
					return null;
				}					
				if( getNivelEnsino(req) == NivelEnsino.TECNICO || getNivelEnsino(req) == NivelEnsino.FORMACAO_COMPLEMENTAR ){
					lista = dao.findByNome(searchItem, getUnidadeGestora(req), new char[]{getNivelEnsino(req)}, null, false, false, getPaging(req));
				} else if( getNivelEnsino(req) == NivelEnsino.LATO ){
					Usuario user = (Usuario) getUsuarioLogado(req);
					if( user.isCoordenadorLato() )
						lista = dao.findByCursoNome(user.getCursoLato().getId(), searchItem, getPaging(req));
					else
						lista = dao.findByNome(searchItem, UnidadeGeral.UNIDADE_DIREITO_GLOBAL, new char[]{getNivelEnsino(req)}, null, false, !(forward != null && forward.equalsIgnoreCase("historico")), getPaging(req));
				}
			} else if (FIND_ANO_INGRESSO.equals(tipoBusca)) {
				int ano = 0;
				try{
					ano = new Integer(getSearchItem(req, "discente.anoIngresso"));
				} catch (Exception e) { }
				lista = dao.findByAnoIngresso(ano,getUnidadeGestora(req), getNivelEnsino(req), getPaging(req));
			} else if (FIND_TURMA_ENTRADA.equals(tipoBusca)) {
				int idTE = 0;
				try{
					idTE = new Integer(getSearchItem(req, "discente.turmaEntradaTecnico.id"));
				} catch (Exception e) { }
				lista = dao.findByTurmaEntrada(idTE,getUnidadeGestora(req), getNivelEnsino(req));
			} else if(FIND_CURSO_LATO.equals(tipoBusca)){
				lista = daoLato.findAllAtivosByCurso(new CursoLato(Integer.parseInt(getSearchItem(req, "cursoLato.id"))));
			}else if (FIND_TODOS.equals(tipoBusca) || req.getParameter("page") != null) {
				 if( getNivelEnsino(req) == NivelEnsino.LATO ){
					if( acesso.isCoordenadorCursoLato() || acesso.isSecretarioLato() )
						lista = daoLato.findAllAtivosByCurso((CursoLato) req.getSession().getAttribute("cursoAtual"));
				}
			}
		} finally {
			dao.close();
			daoLato.close();
		}

		return lista;
	}

	public CursoLato getCursoLato() {
		return cursoLato;
	}

	public void setCursoLato(CursoLato cursoLato) {
		this.cursoLato = cursoLato;
	}

	public boolean isDiscenteAntigo() {
		return discenteAntigo;
	}

	public void setDiscenteAntigo(boolean discenteAntigo) {
		this.discenteAntigo = discenteAntigo;
	}

	public FormFile getArquivoHistorico() {
		return arquivoHistorico;
	}

	public void setArquivoHistorico(FormFile arquivoHistorico) {
		this.arquivoHistorico = arquivoHistorico;
	}


}
