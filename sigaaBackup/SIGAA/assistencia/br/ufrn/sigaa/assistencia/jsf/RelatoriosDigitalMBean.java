/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/08/2009
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;

/**
 * MBean respons�vel por gerar relat�rio de digitais dos usu�rios do sistema. 
 * 
 * @author agostinho
 *
 */
@Component("relatoriosDigitalMBean")
@Scope("request")
public class RelatoriosDigitalMBean extends SigaaAbstractController<Object> {
	
	/** Indica��o do ano referente a gera��o do relat�rio. */
	private Integer ano;
	
	/** Indica��o do per�odo referente a gera��o do relat�rio. */
	private Integer periodo;
	
	/** Indica��o do curso referente a gera��o do relat�rio. */
	private Curso curso = new Curso();
	
	/** Indica��o se o relat�rio � originado para o m�dulo de Assist�ncia ao Estudante. */
	private boolean relatorioSae;
	
	/** Indica��o para exibi��o de discentes com digital cadastrada. */
	private boolean exibirDiscentesComDigital;
	
	/** Conjunto de discentes sem digital cadastrada. */
	private Set<PessoaDto> discentesSemDigital = new LinkedHashSet<PessoaDto>();
	
	/** Conjunto de discentes com digital cadastrada. */
	private List<PessoaDto> discentesComDigital = new ArrayList<PessoaDto>();
	
	/** Lista de pessoas provinientes do SIGAA.  */
	private ArrayList<PessoaDto> listaPessoasSigaa = new ArrayList<PessoaDto>();
	
	/**
	 * Exibe form solicitando Ano/Per�odo e configura par�metros de acordo com o link
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/sae/menu.jsp
	 * 	<li>/sigaa.war/sae/relatorios/form_relatorio_digital.jsp
	 * </ul>
	 * @return
	 * @throws SQLException
	 */
	public String relatorioDiscentesContempladosSemDigital() throws SQLException {
		exibirDiscentesComDigital = false;
		relatorioSae = true;
		return forward("/sae/relatorios/form_relatorio_digital.jsp");	
	}
	
	/**
	 * Exibe form solicitando Ano/Per�odo e configura par�metros de acordo com o link
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/sae/menu.jsp
	 * 	<li>/sigaa.war/sae/relatorios/form_relatorio_digital.jsp
	 * </ul>
	 * @return
	 * @throws SQLException
	 */
	public String relatorioDiscentesContempladosComDigital() throws SQLException {
		exibirDiscentesComDigital = true;
		relatorioSae = true;
		return forward("/sae/relatorios/form_relatorio_digital.jsp");
	}
	
	/**
	 * Gera uma lista de todos os discentes que POSSUEM digital e sejam contemplados com bolsa aux�lio SAE.
	 * 
	 * Exibe listagem dos discentes COM digital.
	 *    
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio_digital.jsp </ul>
	 */	
	public String gerarRelatorioDiscentesContempladosSAEComDigital() throws SQLException {

		erros = validarAnoPeriodo();
		
		if (hasErrors()) 
			return null;

		List<PessoaDto> listaPessoasComum = findDigitaisCadastradas(true); // discentes com bolsa auxilio contemplada
		discentesComDigital = new ArrayList<PessoaDto>();
		for (int j = 0; j < listaPessoasComum.size(); j++) {
			PessoaDto comun = listaPessoasComum.get(j);
			PessoaDto sigaa = listaPessoasSigaa.get( listaPessoasSigaa.indexOf(comun) );
				if ( listaPessoasSigaa.contains(comun) )
					discentesComDigital.add( sigaa );
		}
		
		if (discentesComDigital.size() == 0){
			addMessage("Nenhum registro foi encontrado com os par�metros informados.", TipoMensagemUFRN.ERROR);
			return null;
		}
		
		return forward("/sae/relatorios/relatorio_pessoas_digital.jsf");
	}
	
	/**
	 * Gera uma lista de todos os discentes que N�O POSSUEM digital e sejam contemplados com bolsa aux�lio SAE.
	 * 
	 * Exibe listagem dos discentes SEM digital.
	 * 
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio_digital.jsp </ul>
	 */	
	public String gerarRelatorioDiscentesContempladosSAESemDigital() throws SQLException {

		erros = validarAnoPeriodo();
		
		if (hasErrors()) 
			return null;

		List<PessoaDto> listaPessoasComum = findDigitaisCadastradas(true);
		discentesSemDigital = new LinkedHashSet<PessoaDto>();
		for (int j = 0; j < listaPessoasComum.size(); j++) {
			PessoaDto comun = listaPessoasComum.get(j);
			PessoaDto sigaa = listaPessoasSigaa.get(j);
				if ( !comun.equals(sigaa) && !listaPessoasComum.contains(sigaa) )
					discentesSemDigital.add( sigaa );
		}
		
		if (discentesSemDigital.size() == 0){
			addMessage("Nenhum registro foi encontrado com os par�metros informados.", TipoMensagemUFRN.ERROR);
			return null;
		}
		
		return forward("/sae/relatorios/relatorio_pessoas_sem_digital.jsf");
		
		
	}

	/**
	 * Valida ano/per�odo
	 * @return
	 */
	private ListaMensagens validarAnoPeriodo() {
		
		if (ValidatorUtil.isEmpty(ano) ) 
			erros.addErro("� necess�rio informar o Ano.");
		
		if (ValidatorUtil.isEmpty(periodo) ) 
			erros.addErro("� necess�rio informar o Per�odo.");
		
		return erros;
	}
	
	/**
	 * Gerar relat�rio discentes COM DIGITAL que est�o associados ao Curso do coordenador logado
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/menu_coordenador.jsp </ul>
	 * @return
	 * @throws SQLException
	 */
	public String gerarRelatorioDiscentesComDigitalCadastrada() throws SQLException {
		
		discentesComDigital = new ArrayList<PessoaDto>();
		List<PessoaDto> pessoasComDigitalSistema = findDigitaisCadastradas(false); 
		
		for (int j = 0; j < pessoasComDigitalSistema.size(); j++) {
			PessoaDto comun = pessoasComDigitalSistema.get(j);
			PessoaDto sigaa = listaPessoasSigaa.get( listaPessoasSigaa.indexOf(comun) );
				if ( listaPessoasSigaa.contains(comun) )
					discentesComDigital.add( sigaa );
		}
		
		exibirDiscentesComDigital = true; 
		curso = getCursoAtualCoordenacao();
		return forward("/sae/relatorios/relatorio_pessoas_digital.jsf");
	}
	
	/**
	 * Gerar relat�rio discentes SEM DIGITAL que est�o associados ao Curso do coordenador logado
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/menu_coordenador.jsp </ul>
	 * @return
	 * @throws SQLException
	 * @throws DAOException 
	 */
	public String gerarRelatorioDiscentesSemDigitalCadastrada() throws SQLException, DAOException {
		
		discentesSemDigital = new LinkedHashSet<PessoaDto>();
		List<PessoaDto> pessoasComDigitalSistema = findDigitaisCadastradas(false);
		
		for (int j = 0; j < listaPessoasSigaa.size(); j++) {
			for (int i = 0; i < pessoasComDigitalSistema.size(); i++) {
				
				PessoaDto pessoa = listaPessoasSigaa.get(j);
				
				if ( !pessoasComDigitalSistema.contains(pessoa) )
					discentesSemDigital.add( pessoa );
			}
		}
		
		exibirDiscentesComDigital = false;
		curso = getCursoAtualCoordenacao();
		return forward("/sae/relatorios/relatorio_pessoas_sem_digital.jsf");
	}

	/**
	 * Extrai as digitais dos discentes que possuem Bolsa Aux�lio contemplada OU apenas os discentes que est�o 
	 * associados ao Curso do coordenador logado.
	 * 
	 * discentesComBolsaAuxilio = true (exibe os discentes que possuem BolsaAuxilio)
	 * discentesComBolsaAuxilio = false (exibe os discentes que est�o associados ao Curso do Coordenador de Curso logado)
	 * 
	 */
	private List<PessoaDto> findDigitaisCadastradas(boolean discentesComBolsaAuxilio) {
		
		Curso cursoAtual = getCursoAtualCoordenacao();
		int idCurso = 0;
		if (cursoAtual != null)
			idCurso = cursoAtual.getId();
		
		BolsaAuxilioDao daoComum = getDAO(BolsaAuxilioDao.class);
		
		// lista os CPFs que est�o associados as digitais no banco Comum
		 List<PessoaDto> pessoasComDigitalSistema = daoComum.findDiscentesDigitalCadastrada(Sistema.COMUM, discentesComBolsaAuxilio, 0, 0, 0);
	
		BolsaAuxilioDao daoSigaa = getDAO(BolsaAuxilioDao.class);
		// lista os discentes com bolsa aux�lio contemplada OU os discentes do Curso do Coordenador que esteja logado
		listaPessoasSigaa = (ArrayList<PessoaDto>) daoSigaa.findDiscentesDigitalCadastrada(Sistema.SIGAA, discentesComBolsaAuxilio, idCurso, ano, periodo);

		// seta o nome dos Discentes, pois � utilizado o Nome e CPF em PessoaDTO para fazer o equals/hashCode
		List<PessoaDto> listaPessoasDTO = new ArrayList<PessoaDto>();		
		for (int i = 0; i < listaPessoasSigaa.size(); i++) {
			for (int j = 0; j < pessoasComDigitalSistema.size(); j++) {
				if ( pessoasComDigitalSistema.get(j).getCpf() == listaPessoasSigaa.get(i).getCpf() ) {
					PessoaDto pessoaDTO = pessoasComDigitalSistema.get(j);
					pessoaDTO.setNome( listaPessoasSigaa.get(i).getNome() );
					pessoaDTO.setCurso( listaPessoasSigaa.get(i).getCurso() );
	 				listaPessoasDTO.add(pessoaDTO);
				}
			}
		}	
		
		return listaPessoasDTO;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isRelatorioSae() {
		return relatorioSae;
	}

	public void setRelatorioSae(boolean relatorioSae) {
		this.relatorioSae = relatorioSae;
	}

	public boolean isExibirDiscentesComDigital() {
		return exibirDiscentesComDigital;
	}

	public void setExibirDiscentesComDigital(boolean exibirDiscentesComDigital) {
		this.exibirDiscentesComDigital = exibirDiscentesComDigital;
	}

	public Set<PessoaDto> getDiscentesSemDigital() {
		return discentesSemDigital;
	}

	public void setDiscentesSemDigital(Set<PessoaDto> discentesSemDigital) {
		this.discentesSemDigital = discentesSemDigital;
	}

	public List<PessoaDto> getDiscentesComDigital() {
		return discentesComDigital;
	}

	public void setDiscentesComDigital(List<PessoaDto> discentesComDigital) {
		this.discentesComDigital = discentesComDigital;
	}
}