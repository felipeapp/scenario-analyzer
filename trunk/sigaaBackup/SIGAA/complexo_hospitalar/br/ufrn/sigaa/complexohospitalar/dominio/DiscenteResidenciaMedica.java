/* 
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/12/2009
 *
 */
package br.ufrn.sigaa.complexohospitalar.dominio;

import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Entidade que modela o aluno da Residência Médica
 * 
 * @author agostinho campos
 */
@Entity
@Table(name = "discente_residencia_medica", schema = "complexo_hospitalar", uniqueConstraints = {})
public class DiscenteResidenciaMedica implements Validatable, DiscenteAdapter {

	@Id
	@Column(name="id_discente")
	private int id;
	
	@ManyToOne 
	@JoinColumn(name="id_discente", insertable=false, updatable=false)
	private Discente discente;
	
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/** Média geral do discente, equivalente ao IRA na graduação */
	@Column(name="media_geral")
	private Float mediaGeral = 0.0f;

	/** Mês de entada do discente na residência */
	@Column(name = "mes_entrada")
	private Integer mesEntrada;
	
	private Integer crm;
	
	@Column(name="nivel_entrada_residente")
	private int nivelEntradaResidente;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="id_local_graduacao")
	private InstituicoesEnsino localGraduacao;

	public DiscenteResidenciaMedica() {
		discente = new Discente();
	}
	
	public InstituicoesEnsino getLocalGraduacao() {
		return localGraduacao;
	}

	public void setLocalGraduacao(InstituicoesEnsino localGraduacao) {
		this.localGraduacao = localGraduacao;
	}

	public Integer getCrm() {
		return crm;
	}

	public void setCrm(Integer crm) {
		this.crm = crm;
	}

	public ListaMensagens validate() {
		return null;
	}

	public int getNivelEntradaResidente() {
		return nivelEntradaResidente;
	}

	public void setNivelEntradaResidente(int nivelEntradaResidente) {
		this.nivelEntradaResidente = nivelEntradaResidente;
	}
	
	public Integer getMesEntrada() {
		return mesEntrada;
	}

	public void setMesEntrada(Integer mesEntrada) {
		this.mesEntrada = mesEntrada;
	}

	public Float getMediaGeral() {
		return mediaGeral;
	}
	
	public void setMediaGeral(float mediaGeral) {
		this.mediaGeral = mediaGeral;
	}

	/** Retorna uma string representando o ingresso do discente no formato: mês,
	 * seguido de "/", seguido do ano.
	 * @return
	 */
	public String getMesAnoIngresso() {
		int periodo = getPeriodoIngresso() == 1 ? 1 : 7;
		int mes = mesEntrada != null ? mesEntrada : periodo;
		return CalendarUtils.getMesAbreviado(mes) + "/" + getAnoIngresso();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return (getMatricula() != null ? getMatricula() + " - " : "")
				+ (getPessoa().getNome() != null ? getPessoa().getNome() : "");
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}
	
	@Override
	public int getAnoEntrada() {
		return discente.getAnoEntrada();
	}

	@Override
	public Integer getAnoIngresso() {
		return discente.getAnoIngresso();
	}

	@Override
	public String getAnoPeriodoIngresso() {
		return discente.getAnoPeriodoIngresso();
	}

	@Override
	public Integer getChIntegralizada() {
		return discente.getChIntegralizada();
	}

	@Override
	public RegistroEntrada getCriadoPor() {
		return discente.getCriadoPor();
	}

	@Override
	public Curriculo getCurriculo() {
		return discente.getCurriculo();
	}

	@Override
	public Curso getCurso() {
		return discente.getCurso();
	}

	@Override
	public Date getDataCadastro() {
		return dataCadastro;
	}

	@Override
	public Date getDataColacaoGrau() {
		return discente.getDataColacaoGrau();
	}

	@Override
	public FormaIngresso getFormaIngresso() {
		return discente.getFormaIngresso();
	}

	@Override
	public Unidade getGestoraAcademica() {
		return discente.getGestoraAcademica();
	}

	@Override
	public Integer getIdFoto() {
		return discente.getIdFoto();
	}

	@Override
	public Integer getIdHistoricoDigital() {
		return discente.getIdHistoricoDigital();
	}

	@Override
	public Integer getIdPerfil() {
		return discente.getIdPerfil();
	}

	@Override
	public Long getMatricula() {
		return discente.getMatricula();
	}

	@Override
	public String getMatriculaNome() {
		return discente.getMatriculaNome();
	}

	@Override
	public String getMatriculaNomeFormatado() {
		return discente.getMatriculaNomeFormatado();
	}

	@Override
	public String getMatriculaNomeNivel() {
		return discente.getMatriculaNomeNivel();
	}

	@Override
	public String getMatriculaNomeNivelFormatado() {
		return discente.getMatriculaNomeNivelFormatado();
	}

	@Override
	public String getMatriculaNomeSituacaoFormatado() {
		return discente.getMatriculaNomeSituacaoFormatado();
	}

	@Override
	public String getMatriculaNomeStatus() {
		return discente.getMatriculaNomeStatus();
	}

	@Override
	public Collection<MatriculaComponente> getMatriculasDisciplina() {
		return discente.getMatriculasDisciplina();
	}

	@Override
	public MovimentacaoAluno getMovimentacaoSaida() {
		return discente.getMovimentacaoSaida();
	}

	@Override
	public char getNivel() {
		return discente.getNivel();
	}

	@Override
	public String getNivelDesc() {
		return discente.getNivelDesc();
	}

	@Override
	public String getNivelStr() {
		return discente.getNivelStr();
	}

	@Override
	public String getNome() {
		return discente.getNome();
	}

	@Override
	public String getObservacao() {
		return discente.getObservacao();
	}

	@Override
	public PerfilPessoa getPerfil() {
		return discente.getPerfil();
	}

	@Override
	public Integer getPeriodoAtual() {
		return discente.getPeriodoAtual();
	}

	@Override
	public Integer getPeriodoIngresso() {
		return discente.getPeriodoIngresso();
	}

	@Override
	public Pessoa getPessoa() {
		return discente.getPessoa();
	}

	@Override
	public Integer getPrazoConclusao() {
		return discente.getPrazoConclusao();
	}

	@Override
	public Integer getProrrogacoes() {
		return discente.getProrrogacoes();
	}

	@Override
	public int getSemestreAtual() {
		return discente.getSemestreAtual();
	}

	@Override
	public int getStatus() {
		return discente.getStatus();
	}

	@Override
	public String getStatusString() {
		return discente.getStatusString();
	}

	@Override
	public Integer getTipo() {
		return discente.getTipo();
	}

	@Override
	public String getTipoString() {
		return discente.getTipoString();
	}

	@Override
	public String getTrancamentos() {
		return discente.getTrancamentos();
	}

	@Override
	public Unidade getUnidade() {
		return discente.getUnidade();
	}

	@Override
	public Usuario getUsuario() {
		return discente.getUsuario();
	}

	@Override
	public boolean isApostilamento() {
		return discente.isApostilamento();
	}

	@Override
	public boolean isAtivo() {
		return discente.isAtivo();
	}

	@Override
	public boolean isCadastrado(int ano, int periodo) {
		return discente.isCadastrado(ano, periodo);
	}

	@Override
	public boolean isCarente() {
		return discente.isCarente();
	}

	@Override
	public boolean isConcluido() {
		return discente.isConcluido();
	}

	@Override
	public boolean isDefendido() {
		return discente.isDefendido();
	}

	@Override
	public boolean isDiscenteEad() {
		return false;
	}

	@Override
	public boolean isDoutorado() {
		return discente.isDoutorado();
	}

	@Override
	public boolean isGraduacao() {
		return discente.isGraduacao();
	}

	@Override
	public boolean isInfantil() {
		return discente.isInfantil();
	}

	@Override
	public boolean isLato() {
		return discente.isLato();
	}

	@Override
	public boolean isMatricular() {
		return discente.isMatricular();
	}

	@Override
	public boolean isMestrado() {
		return discente.isMestrado();
	}

	@Override
	public boolean isRegular() {
		return discente.isRegular();
	}

	@Override
	public boolean isResidencia() {
		return discente.isResidencia();
	}

	@Override
	public boolean isSelecionado() {
		return discente.isSelecionado();
	}

	@Override
	public boolean isStricto() {
		return discente.isStricto();
	}

	@Override
	public boolean isTecnico() {
		return discente.isTecnico();
	}
	
	@Override
	public boolean isMetropoleDigital() {
		return discente.isMetropoleDigital();
	}
	
	@Override
	public boolean isMedio() {
		return discente.isMedio();
	}
	
	@Override
	public boolean isFormacaoComplementar() {
		return discente.isFormacaoComplementar();
	}

	@Override
	public boolean isTrancado() {
		return discente.isTrancado();
	}

	@Override
	public String nomeMatricula() {
		return discente.nomeMatricula();
	}

	@Override
	public void setAnoIngresso(Integer anoIngresso) {
		discente.setAnoIngresso(anoIngresso);
	}

	@Override
	public void setCarente(boolean carente) {
		discente.setCarente(carente);
	}

	@Override
	public void setChIntegralizada(Integer chIntegralizada) {
		discente.setChIntegralizada(chIntegralizada);
	}

	@Override
	public void setCriadoPor(RegistroEntrada criadoPor) {
		discente.setCriadoPor(criadoPor);
	}

	@Override
	public void setCurriculo(Curriculo curriculo) {
		discente.setCurriculo(curriculo);
	}

	@Override
	public void setCurso(Curso curso) {
		discente.setCurso(curso);
	}

	@Override
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public void setDataColacaoGrau(Date dataColacaoGrau) {
		discente.setDataColacaoGrau(dataColacaoGrau);
	}

	@Override
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		discente.setFormaIngresso(formaIngresso);
	}

	@Override
	public void setGestoraAcademica(Unidade unidadeGestora) {
		discente.setGestoraAcademica(unidadeGestora);
	}

	@Override
	public void setIdFoto(Integer idFoto) {
		discente.setIdFoto(idFoto);
	}

	@Override
	public void setIdHistoricoDigital(Integer idHistoricoDigital) {
		discente.setIdHistoricoDigital(idHistoricoDigital);
	}

	@Override
	public void setIdPerfil(Integer idPerfil) {
		discente.setIdPerfil(idPerfil);
	}

	@Override
	public void setMatricula(Long matricula) {
		discente.setMatricula(matricula);
	}

	@Override
	public void setMatricular(boolean matricular) {
		discente.setMatricular(matricular);
	}

	@Override
	public void setMatriculasDisciplina(Collection<MatriculaComponente> matriculaDisciplinas) {
		discente.setMatriculasDisciplina(matriculaDisciplinas);
	}

	@Override
	public void setMovimentacaoSaida(MovimentacaoAluno movimentacaoSaida) {
		discente.setMovimentacaoSaida(movimentacaoSaida);
	}

	@Override
	public void setNivel(char nivel) {
		discente.setNivel(nivel);
	}

	@Override
	public void setObservacao(String observacao) {
		discente.setObservacao(observacao);
	}

	@Override
	public void setPerfil(PerfilPessoa perfil) {
		discente.setPerfil(perfil);
	}

	@Override
	public void setPeriodoAtual(Integer periodoAtual) {
		discente.setPeriodoAtual(periodoAtual);
	}

	@Override
	public void setPeriodoIngresso(Integer periodoIngresso) {
		discente.setPeriodoIngresso(periodoIngresso);
	}

	@Override
	public void setPessoa(Pessoa pessoa) {
		discente.setPessoa(pessoa);
	}

	@Override
	public void setPrazoConclusao(Integer prazoConclusao) {
		discente.setPrazoConclusao(prazoConclusao);
	}

	@Override
	public void setProrrogacoes(Integer prorrogacoes) {
		discente.setProrrogacoes(prorrogacoes);
	}

	@Override
	public void setSelecionado(boolean selecionado) {
		discente.setSelecionado(selecionado);
	}

	@Override
	public void setSemestreAtual(int semestreAtual) {
		discente.setSemestreAtual(semestreAtual);
	}

	@Override
	public void setStatus(int status) {
		discente.setStatus(status);
	}

	@Override
	public void setTipo(Integer tipo) {
		discente.setTipo(tipo);
	}

	@Override
	public void setTrancamentos(String trancamentos) {
		discente.setTrancamentos(trancamentos);
	}

	@Override
	public void setUsuario(Usuario usuario) {
		discente.setUsuario(usuario);
	}

	@Override
	public Collection<SolicitacaoApoioNee> getSolicitacoesApoioNee() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSolicitacoesApoioNee(
			Collection<SolicitacaoApoioNee> solicitacoesApoioNee) {
	}
	
}