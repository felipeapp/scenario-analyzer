/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DadosDefesaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * MBean responsável por emitir declarações de Defesa/Qualificação dos Alunos de Pós-Graduação
 * JSP: declaracao_qualificacao_defesa.jsp
 * 
 * @author agostinho
 */
@Component("declaracaoDefesaMBean") @Scope("session")
public class DeclaracaoDefesaMBean extends SigaaAbstractController<DadosDefesa> implements OperadorDiscente {

	private DiscenteStricto discente;
	private BancaPos bancaPos;
	private String coordenadorCurso;
	
	private Collection<BancaPos> bancasDoDiscente;
	// parâmetro que recebe o id da banca selecionada
	private Integer idBanca;
	/**
	 * Construtor
	 */
	public DeclaracaoDefesaMBean() {
		initObj();
	}

	/**
	 * Método para inicializar o Obj
	 */
	private void initObj() {
		obj = new DadosDefesa();
	}
	
	/**
	 * Exibe tela com uma lista de Discentes
	 * JSP: /sigaa.war/stricto/menu_coordenador.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO );
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.BUSCAR_ALUNOS_STRICTO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Lista as bancas que o aluno possui
	 * JSP: /stricto/banca_pos/escolher_banca.jsp
	 */
	public String selecionaDiscente() throws ArqException {
		
		bancasDoDiscente = getDAO(DadosDefesaDao.class).findDadosDefesaByDiscente(discente);
		
		if (bancasDoDiscente == null || bancasDoDiscente.isEmpty()) {
			addMensagemErro("Nenhuma banca encontrada para este discente.");
			return null;
		}
		
		return forward("/stricto/banca_pos/escolher_banca.jsp");
	}

	/**
	 * Método invocado quando se seleciona um discente.
	 */
	public void setDiscente(DiscenteAdapter discente) throws DAOException {
		this.discente = (DiscenteStricto) getDAO(DiscenteDao.class).findByPK(discente.getId());
		this.discente.setOrientacao(DiscenteHelper.getOrientadorAtivo(this.discente.getDiscente()));
	}

	/**
	 * Redireciona o usuário para página em formato de 
	 * impressão que exibe a declaração.
	 * 
	 * JSP: /sigaa.war/stricto/banca_pos/escolher_banca.jsp
	 */
	public String selecionaBanca() throws DAOException {
		
		BancaPosDao bancaDao = getDAO(BancaPosDao.class);
		bancaPos = bancaDao.findByPrimaryKey(idBanca, BancaPos.class);
		
		CoordenacaoCursoDao coordenacaoDao = getDAO(CoordenacaoCursoDao.class);
		CoordenacaoCurso coordenacao = coordenacaoDao.findUltimaByPrograma(discente.getGestoraAcademica());
		
		String nomeCoordenador = "";
		
		if (!isEmpty(coordenacao)) {
			nomeCoordenador = coordenacao.getServidor().getNome();
		}
		
		setCoordenadorCurso(nomeCoordenador);
		
		if (bancaPos != null)
			return forward("/stricto/banca_pos/declaracao_qualificacao_defesa.jsp");
		else
			addMensagemWarning("O discente selecionado não passou por uma banca de defesa!");
		
		return null;		
	}
	
	public DiscenteStricto getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}
	
	public Usuario getUsuario() {
		return getUsuarioLogado();
	}

	public BancaPos getBancaPos() {
		return bancaPos;
	}

	public void setBancaPos(BancaPos bancaPos) {
		this.bancaPos = bancaPos;
	}

	public void setCoordenadorCurso(String coordenadorCurso) {
		this.coordenadorCurso = coordenadorCurso;
	}

	public String getCoordenadorCurso() {
		return coordenadorCurso;
	}

	public Collection<BancaPos> getBancasDoDiscente() {
		return bancasDoDiscente;
	}

	public void setBancasDoDiscente(Collection<BancaPos> bancasDoDiscente) {
		this.bancasDoDiscente = bancasDoDiscente;
	}

	public Integer getIdBanca() {
		return idBanca;
	}

	public void setIdBanca(Integer idBanca) {
		this.idBanca = idBanca;
	}
	
}