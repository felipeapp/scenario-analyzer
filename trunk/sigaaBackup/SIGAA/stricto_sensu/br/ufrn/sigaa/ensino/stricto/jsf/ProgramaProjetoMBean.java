/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas
 * Criado em: 11/12/2008
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.stricto.dominio.ProgramaProjeto;
import br.ufrn.sigaa.ensino.stricto.negocio.MovimentoProgramaProjeto;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/** 
 * MBean responsável por realizar o vinculo entre projetos e programas
 * @author Victor Hugo
 */
@Component("programaProjetoBean") @Scope("session")
public class ProgramaProjetoMBean extends SigaaAbstractController<ProgramaProjeto> {

	private List<ProjetoPesquisa> projetos = new ArrayList<ProjetoPesquisa>();
	private List<ProgramaProjeto> programasProjeto = new ArrayList<ProgramaProjeto>();

	/**
	 * Estes atributos  são utilizados para filtrar os projetos de pesquisa
	 */
	private Integer ano, situacao;
	private String codigo, titulo, membro;
	private boolean checkAno, checkCodigo, checkTitulo, checkMembro, checkSituacao;
	
	public ProgramaProjetoMBean() {
		initObj();
	}

	public void initObj(){
		obj = new ProgramaProjeto();
		projetos = new ArrayList<ProjetoPesquisa>();
		programasProjeto = new ArrayList<ProgramaProjeto>();
		ano = null;
		situacao = null;
		codigo = null;
		titulo = null;
		membro = null;
		checkAno = false;
		checkCodigo = false;
		checkTitulo = false;
		checkMembro = false;
		checkSituacao = false;
	}
	
	/**
	 * Inicia o caso de uso de vincular projetos de pesquisa ao programa, 
	 * realizado pela coordenação de programas de pós stricto
	 * JSP'S Chamadas: \SIGAA\app\sigaa.ear\sigaa.war\stricto\menu_coordenador.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		checkRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO );
		initObj();
		setOperacaoAtiva( SigaaListaComando.VINCULAR_PROGRAMAS_PROJETOS.getId() );
		prepareMovimento( SigaaListaComando.VINCULAR_PROGRAMAS_PROJETOS );
		return forward( getFormPage() );
	}
	
	/**
	 * Executa a busca de acordo com os critérios selecionados
	 * JSP'S Chamadas: \SIGAA\app\sigaa.ear\sigaa.war\stricto\programa_projeto\form.jsp
	 */
	@Override
	public String buscar() throws Exception {
		ProjetoPesquisaDao dao = getDAO( ProjetoPesquisaDao.class );
		
		Integer ano = null;
		Integer situacao = null;
		CodigoProjetoPesquisa codigo = null;
		String titulo = null;
		String membro = null;
		
		if( checkAno )
			ano = this.ano;
		if( checkCodigo ) {
			try {
				codigo = new CodigoProjetoPesquisa(this.codigo);
			} catch ( IllegalArgumentException e ) {
				addMensagemErro(e.getMessage());
			}			
		}
		if( checkTitulo )
			titulo = this.titulo;
		if( checkMembro )
			membro = this.membro;
		if( checkSituacao )
			situacao = this.situacao;
		
		if( ano == null && codigo == null && titulo == null && membro == null && situacao == null ){
			addMensagemErro("Entre com pelo menos um critério de busca.");
			return null;
		}
		
		Collection<ProjetoPesquisa> colProjetoPesquisa = 
			dao.findByMembrosPrograma( getProgramaStricto().getId(), ano, codigo, titulo, membro, situacao );
		
		projetos = (List<ProjetoPesquisa>) colProjetoPesquisa;
		
		programasProjeto = (ArrayList<ProgramaProjeto>) dao.findByExactField(ProgramaProjeto.class, "programa.id", getProgramaStricto().getId());

		Collections.sort( projetos, new Comparator<ProjetoPesquisa>(){
			public int compare(ProjetoPesquisa p1, ProjetoPesquisa p2) {
				int retorno = p2.getCodigo().getAno() - p1.getCodigo().getAno();
				if( retorno == 0 )
					retorno = p1.getSituacaoProjeto().getId() - p2.getSituacaoProjeto().getId();
				//if( retorno == 0 )
					//retorno = p1.getUnidade().getNome().compareTo( p2.getUnidade().getNome() );
				if( retorno == 0 )
					retorno = p1.getTitulo().compareTo( p2.getTitulo() );
				return retorno;
			}
		});

		for( ProjetoPesquisa pp : projetos ){
			for( ProgramaProjeto vinculo : programasProjeto   ){
				if( pp.equals( vinculo.getProjeto()) && vinculo.isAtivo() ){
					pp.setSelecionado(true);
					break;
				}
			}
		}
		
		return forward( getFormPage() );
	}

	/**
	 * Persiste o vínculo realizado pelo usuário
	 * JSP'S Chamadas: \SIGAA\app\sigaa.ear\sigaa.war\stricto\programa_projeto\form.jsp
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String cadastrar() throws ArqException{
		
		if( !checkOperacaoAtiva( SigaaListaComando.VINCULAR_PROGRAMAS_PROJETOS.getId() ) ){
			return null;
		}
		
		if( isEmpty(projetos) && isEmpty(programasProjeto) ){
			addMensagemErro("Você deve consultar os projetos e realizar o vínculo para poder gravar.");
			return null;
		}
		
		MovimentoProgramaProjeto mov = new MovimentoProgramaProjeto(getProgramaStricto(), projetos, programasProjeto);
		mov.setCodMovimento( SigaaListaComando.VINCULAR_PROGRAMAS_PROJETOS );
		
		try {
			execute(mov);
			addMensagemInformation("O vínculo entre entre o programa e os projetos de pesquisa foi definido com sucesso.");
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		} 
		
		return cancelar();
	}
	
	@Override
	public String getDirBase() {
		return "/stricto/programa_projeto";
	}

	public List<ProjetoPesquisa> getProjetos() {
		return projetos;
	}

	public void setProjetos(List<ProjetoPesquisa> projetos) {
		this.projetos = projetos;
	}

	public List<ProgramaProjeto> getProgramasProjeto() {
		return programasProjeto;
	}

	public void setProgramasProjeto(List<ProgramaProjeto> programasProjeto) {
		this.programasProjeto = programasProjeto;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getSituacao() {
		return situacao;
	}

	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMembro() {
		return membro;
	}

	public void setMembro(String membro) {
		this.membro = membro;
	}

	public boolean isCheckAno() {
		return checkAno;
	}

	public void setCheckAno(boolean checkAno) {
		this.checkAno = checkAno;
	}

	public boolean isCheckCodigo() {
		return checkCodigo;
	}

	public void setCheckCodigo(boolean checkCodigo) {
		this.checkCodigo = checkCodigo;
	}

	public boolean isCheckTitulo() {
		return checkTitulo;
	}

	public void setCheckTitulo(boolean checkTitulo) {
		this.checkTitulo = checkTitulo;
	}

	public boolean isCheckMembro() {
		return checkMembro;
	}

	public void setCheckMembro(boolean checkMembro) {
		this.checkMembro = checkMembro;
	}

	public boolean isCheckSituacao() {
		return checkSituacao;
	}

	public void setCheckSituacao(boolean checkSituacao) {
		this.checkSituacao = checkSituacao;
	}

}
