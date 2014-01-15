/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/03/2010
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Interface criada no refactoring de discente: mudança
 * de herança para composição. Essa interface funciona
 * como um adapter para que objetos das antigas sub-classes 
 * de discente continuem tendo acesso direto a métodos
 * da classe discente. 
 * 
 * @author David Pereira
 *
 */
public interface DiscenteAdapter extends PersistDB {

	public FormaIngresso getFormaIngresso();

	public void setFormaIngresso(FormaIngresso formaIngresso);

	public Integer getChIntegralizada();

	public void setChIntegralizada(Integer chIntegralizada);

	public Integer getPrazoConclusao();

	public void setPrazoConclusao(Integer prazoConclusao);

	public Date getDataColacaoGrau();

	public void setDataColacaoGrau(Date dataColacaoGrau);

	public Pessoa getPessoa();

	public void setPessoa(Pessoa pessoa);

	public Integer getAnoIngresso();

	public void setAnoIngresso(Integer anoIngresso);

	public Integer getPeriodoIngresso();

	public void setPeriodoIngresso(Integer periodoIngresso);

	public Long getMatricula();

	public void setMatricula(Long matricula);

	public String getObservacao();

	public void setObservacao(String observacao);

	public Collection<MatriculaComponente> getMatriculasDisciplina();

	public void setMatriculasDisciplina(
			Collection<MatriculaComponente> matriculaDisciplinas);
	
	public Collection<SolicitacaoApoioNee> getSolicitacoesApoioNee();

	public void setSolicitacoesApoioNee(
			Collection<SolicitacaoApoioNee> solicitacoesApoioNee);

	public Unidade getGestoraAcademica();

	public void setGestoraAcademica(Unidade unidadeGestora);

	public int hashCode();

	/**
	 * Retorna string contendo nome e matrícula
	 */
	public String nomeMatricula();

	/**
	 * Retorna string contendo matrícula e nome
	 */
	public String getMatriculaNome();

	public String getMatriculaNomeNivel();

	public String getMatriculaNomeStatus();

	public String getNivelDesc();

	public char getNivel();

	public String getNivelStr();

	public void setNivel(char nivel);

	public int getStatus();

	public void setStatus(int status);

	public String getNome();

	public int getAnoEntrada();

	public boolean isMatricular();

	public void setMatricular(boolean matricular);

	public Curso getCurso();

	public void setCurso(Curso curso);

	public String getStatusString();

	public Integer getTipo();

	public void setTipo(Integer tipo);

	public Curriculo getCurriculo();

	public void setCurriculo(Curriculo curriculo);

	public String getTipoString();

	public Integer getIdFoto();

	public void setIdFoto(Integer idFoto);

	public PerfilPessoa getPerfil();

	public void setPerfil(PerfilPessoa perfil);

	public Integer getIdPerfil();

	public void setIdPerfil(Integer idPerfil);

	public String getAnoPeriodoIngresso();

	public Date getDataCadastro();

	public void setDataCadastro(Date dataCadastro);

	public boolean isRegular();

	public boolean isGraduacao();

	public boolean isTecnico();
	
	public boolean isMetropoleDigital();
	
	public boolean isMedio();
	
	public boolean isFormacaoComplementar();
	
	public boolean isInfantil();

	public boolean isStricto();

	public boolean isResidencia();

	public boolean isLato();

	public boolean isDoutorado();

	public boolean isMestrado();

	public boolean isDiscenteEad();

	public boolean isAtivo();

	public boolean isTrancado();

	public boolean isDefendido();

	public String getTrancamentos();

	public void setTrancamentos(String trancamentos);

	public int getSemestreAtual();

	public void setSemestreAtual(int semestreAtual);

	public MovimentacaoAluno getMovimentacaoSaida();

	public void setMovimentacaoSaida(MovimentacaoAluno movimentacaoSaida);

	public Integer getProrrogacoes();

	public void setProrrogacoes(Integer prorrogacoes);

	public boolean isApostilamento();

	public Usuario getUsuario();

	public void setUsuario(Usuario usuario);

	public RegistroEntrada getCriadoPor();

	public void setCriadoPor(RegistroEntrada criadoPor);

	public boolean isSelecionado();

	public void setSelecionado(boolean selecionado);

	public boolean isCadastrado(int ano, int periodo);

	public boolean isConcluido();

	public void setCarente(boolean carente);

	public boolean isCarente();

	/** Retorna o nome do aluno concatenado com a matrícula */
	public String getMatriculaNomeFormatado();

	public String getMatriculaNomeSituacaoFormatado();

	public String getMatriculaNomeNivelFormatado();

	public Integer getPeriodoAtual();

	public void setPeriodoAtual(Integer periodoAtual);

	public Unidade getUnidade();

	public Integer getIdHistoricoDigital();

	public void setIdHistoricoDigital(Integer idHistoricoDigital);
	
	public Discente getDiscente();

}