/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/03/2008
 * 
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Entidade que modela o aluno de pós-graduação Stricto-Sensu.
 * Contém apenas as informações que são específicas da pós graduação Stricto-sensu.
 * @author Gleydson
 * @author Victor Hugo 
 */
@Entity
@Table(name = "discente_stricto", schema = "stricto_sensu", uniqueConstraints = {})
public class DiscenteStricto implements DiscenteAdapter {

	@Id 
	@Column(name="id_discente")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_discente", insertable=false, updatable=false)
	private Discente discente = new Discente();
	
	/** Área de concentração do discente */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_area_concentracao")
	private AreaConcentracao area;

	/** Linha de pesquisa do discente */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_linha_pesquisa")
	private LinhaPesquisaStricto linha;

	/** Origem do discente de pós, se ela é da UFRN, de outras instituição federal, particular, etc.. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_origem_discente")
	private OrigemDiscentePos origem;

	/** Nomes dos co-orientadores externos */
	@Column(name = "co_orientadores_externos")
	private String coOrientadoresExternos;	

	/** Mês de entada do discente no programa de pós graduação. */
	@Column(name = "mes_entrada")
	private Integer mesEntrada;

	/** Calculo da quantidade de meses que o aluno encontra-se na pós-graduação */
	@Column(name = "mes_atual")
	private Integer mesAtual;
	
	/** Somatório de variação meses que deve ser utilizado no calculo do mês atual do discente. */
	@Column(name="variacao_prazo")
	private Integer variacaoPrazo;

	/**Total de créditos obrigatórios integralizados */
	@Column(name = "cr_total_obrigatorio_integralizado")
	private Short crTotaisObrigatoriosIntegralizado;

	/** Total de créditos integralizados */
	@Column(name = "cr_total_integralizado")
	private Short crTotaisIntegralizados;

	/** Prazo máximo para a conclusão do curso. Normalmente cursos de mestrado é 24 meses e doutorado é 36 meses */
	@Column(name = "prazo_maximo_conclusao")
	private Date prazoMaximoConclusao;

	/** Média geral do discente, equivalente ao IRA na graduação */
	private Float mediaGeral = 0.0f;

	/** Data da última atualização das informações do discente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Registro de quem realizou a última atualização */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data da última atualização dos cálculos do aluno */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ultima_atualizacao_totais")
	private Date ultimaAtualizacaoTotais;
	
	/** Processo seletivo no qual o aluno ingressou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivo processoSeletivo;

	/** Banca de defesa mais recente. */
	@Transient
	private BancaPos bancaDefesa;
	
	/** Orientador atual, ou mais recente, do discente. */
	@Transient
	private OrientacaoAcademica orientacao;
	
	/** Atributo auxiliar para armazenar os créditos exigidos que são visualizados * no histórico do discente. */ 
	@Transient 
	private Integer totalCreditoCalculado;

	/** Co-Orientador atual, ou mais recente, do discente. */
	@Transient
	private OrientacaoAcademica coOrientacao;
	
	/** Dados da homologação de trabalho final. */
	@Transient
	private HomologacaoTrabalhoFinal homologacaoTrabalhoFinal;
	
	/** Instituição de Ensino, diferente da UFRN, do discente pertencente a algum programa em rede com outras instituições de ensino.*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_instituicao_ensino_rede")
	private InstituicoesEnsino instituicaoEnsinoRede;
	
	/** Total de CH integralizada para esse aluno. */
	@Column(name = "ch_total_integralizada")
	private Short chTotalIntegralizada;
	
	/** CH obrigatória integralizada para esse aluno. */
	@Column(name = "ch_obrigatoria_integralizada")
	private Short chObrigatoriaIntegralizada;
	
	/** CH optativa integralizada para esse aluno. */
	@Column(name = "ch_optativa_integralizada")
	private Short chOptativaIntegralizada;
	
	/** Atributo transiente utilizado para armazenar a quantidade de créditos exigidos 
	 * para componentes de área comum do currículo acrescentando os componentes da área de concentração específica do discente.*/
	@Transient
	private int crExigidosAreaConcentracao;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/** Retorna o cálculo da quantidade de meses que o aluno encontra-se na pós-graduação. 
	 * @return
	 */
	public Integer getMesAtual() {
		return mesAtual;
	}

	/** Seta o cálculo da quantidade de meses que o aluno encontra-se na pós-graduação. 
	 * @param mesAtual
	 */
	public void setMesAtual(Integer mesAtual) {
		this.mesAtual = mesAtual;
	}

	/** Retorna o mês de entada do discente no programa de pós graduação. 
	 * @return
	 */
	public Integer getMesEntrada() {
		return mesEntrada;
	}

	/** Seta o mês de entada do discente no programa de pós graduação. 
	 * @param mesEntrada
	 */
	public void setMesEntrada(Integer mesEntrada) {
		this.mesEntrada = mesEntrada;
	}	

	/** Retorna a origem do discente de pós, se ela é da UFRN, de outras instituição federal, particular, etc.. 
	 * @return
	 */
	public OrigemDiscentePos getOrigem() {
		return origem;
	}

	/** Seta a origem do discente de pós, se ela é da UFRN, de outras instituição federal, particular, etc..
	 * @param origem
	 */
	public void setOrigem(OrigemDiscentePos origem) {
		this.origem = origem;
	}

	/** Retorna a área de concentração do discente.
	 * @return
	 */
	public AreaConcentracao getArea() {
		return area;
	}

	/** Seta a área de concentração do discente.
	 * @param area
	 */
	public void setArea(AreaConcentracao area) {
		this.area = area;
	}

	/** Retorna o orientador atual, ou mais recente, do discente. 
	 * @return
	 */
	public OrientacaoAcademica getOrientacao() {
		return orientacao;
	}

	/** Seta o orientador atual, ou mais recente, do discente.
	 * @param orientacao
	 */
	public void setOrientacao(OrientacaoAcademica orientacao) {
		this.orientacao = orientacao;
	}

	/** Retorna a Linha de pesquisa do discente.
	 * @return
	 */
	public LinhaPesquisaStricto getLinha() {
		return linha;
	}

	/** Seta a Linha de pesquisa do discente.
	 * @param linha
	 */
	public void setLinha(LinhaPesquisaStricto linha) {
		this.linha = linha;
	}

	/** Retorna os nomes dos co-orientadores externos 
	 * @return
	 */
	public String getCoOrientadoresExternos() {
		return coOrientadoresExternos;
	}

	/** Seta o nome dos co-orientadores externos
	 * @param coOrientadoresExternos
	 */
	public void setCoOrientadoresExternos(String coOrientadoresExternos) {
		this.coOrientadoresExternos = coOrientadoresExternos;
	}

	/** Retorna uma string representando o ingresso do discente no formato: ano,
	 * seguido de "/", seguido do mês.
	 * @return
	 */
	public String getAnoMesIngresso() {
		int periodo = getPeriodoIngresso() == 1 ? 1 : 7;
		int mes = mesEntrada != null ? mesEntrada : periodo;
		return getAnoIngresso() + "/" + CalendarUtils.getMesAbreviado(mes);
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

	/** Retorna uma string representando o prazo máximo de conclusão do discente no formato: ano,
	 * seguido de "/", seguido do mês.
	 * @return
	 */
	public String getAnoMesPrazoConclusao() {
		if (prazoMaximoConclusao != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM");
			return sdf.format(prazoMaximoConclusao).toUpperCase();
		}
		return "";
	}

	/** Retorna uma string representando o prazo máximo de conclusão do discente no formato: mês,
	 * seguido de "/", seguido do ano.
	 * @return
	 */
	public String getMesAnoPrazoConclusao() {
		if (prazoMaximoConclusao != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy");
			return sdf.format(prazoMaximoConclusao).toUpperCase();
		}
		return "";
	}

	/** Retorna a data da defesa da dissertação/tese.
	 * @return
	 */
	public Date getDataDefesa() {
		return getDataColacaoGrau();
	}

	/** Retorna a banca de defesa mais recente. 
	 * @return
	 */
	public BancaPos getBancaDefesa() {
		return bancaDefesa;
	}

	/** Seta a banca de defesa mais recente.
	 * @param bancaDefesa
	 */
	public void setBancaDefesa(BancaPos bancaDefesa) {
		this.bancaDefesa = bancaDefesa;
	}

	/** Retorna o total de créditos obrigatórios integralizados 
	 * @return
	 */
	public Short getCrTotaisObrigatoriosIntegralizado() {
		return crTotaisObrigatoriosIntegralizado;
	}

	/** Seta o total de créditos obrigatórios integralizados
	 * @param crTotaisObrigatoriosIntegralizado
	 */
	public void setCrTotaisObrigatoriosIntegralizado(
			Short crTotaisObrigatoriosIntegralizado) {
		this.crTotaisObrigatoriosIntegralizado = crTotaisObrigatoriosIntegralizado;
	}

	/** Retorna o Total de créditos integralizados  
	 * @return
	 */
	public Short getCrTotaisIntegralizados() {
		return crTotaisIntegralizados;
	}

	/** Seta o Total de créditos integralizados  
	 * @param crTotaisIntegralizados
	 */
	public void setCrTotaisIntegralizados(Short crTotaisIntegralizados) {
		this.crTotaisIntegralizados = crTotaisIntegralizados;
	}

	/** Retorna o total de créditos exigidos do currículo.
	 * @return
	 */
	public Integer getCrTotalExigidos() {
		if(!ValidatorUtil.isEmpty(totalCreditoCalculado)){ 
			return totalCreditoCalculado;
		} else {
			if (getCrExigidosAreaConcentracao() > 0)
				return getCrExigidosAreaConcentracao();
			if (getCurriculo() == null)
				return null;
			return getCurriculo().getCrTotalMinimo();
		}
	}

	/** Retorna o total de créditos pendentes, exigidos do currículo.
	 * @return
	 */
	public Integer getCrTotalPendentes() {
		int total = (getCrTotalExigidos() == null ? 0 : getCrTotalExigidos())
				- (getCrTotaisIntegralizados() == null ? 0
						: getCrTotaisIntegralizados());
		if (total < 0)
			total = 0;
		return total;
	}

	/** Retorna o Prazo máximo para a conclusão do curso. Normalmente cursos de mestrado é 24 meses e doutorado é 36 meses.
	 * @return
	 */
	public Date getPrazoMaximoConclusao() {
		return prazoMaximoConclusao;
	}

	/** Seta o Prazo máximo para a conclusão do curso. Normalmente cursos de mestrado é 24 meses e doutorado é 36 meses.
	 * @param prazoMaximoConclusao
	 */
	public void setPrazoMaximoConclusao(Date prazoMaximoConclusao) {
		this.prazoMaximoConclusao = prazoMaximoConclusao;
	}

	/** Seta a data de defesa da dissertação / tese.
	 * @param data
	 */
	public void setDataDefesa(Date data) {
		setDataColacaoGrau(data);
	}

	/** Retorna a data da última atualização das informações do discente. 
	 * @return
	 */
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	/** Seta a data da última atualização das informações do discente.
	 * @param dataAtualizacao
	 */
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	/** Retorna o registro de quem realizou a última atualização 
	 * @return
	 */
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o registro de quem realizou a última atualização
	 * @param registroAtualizacao
	 */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Inicializa os atributos do discente que estão com valores nulos. */
	public void inicializarAtributosNulos() {
		if (this.getFormaIngresso() == null)
			this.setFormaIngresso(new FormaIngresso());
		if (this.getCurso() == null)
			this.setCurso(new Curso());
		if (this.getCurriculo() == null)
			this.setCurriculo(new Curriculo());
		if (this.getOrientacao() == null)
			this.setOrientacao(new OrientacaoAcademica());
		if (this.getCoOrientacao() == null)
			this.setCoOrientacao(new OrientacaoAcademica());
		if (this.getArea() == null)
			this.setArea(new AreaConcentracao());
		if (this.getLinha() == null)
			this.setLinha(new LinhaPesquisaStricto());
		if( this.getOrigem() == null )
			this.setOrigem( new OrigemDiscentePos() );
		if( this.getProcessoSeletivo() == null )
			this.setProcessoSeletivo( new ProcessoSeletivo() );
		if( this.getGestoraAcademica() == null)
			this.setGestoraAcademica(new Unidade());
		if( this.getInstituicaoEnsinoRede() == null)
			this.setInstituicaoEnsinoRede(new InstituicoesEnsino());
	}
	
	/** Zera os atributos transient. */
	public void anularAtributosTransient() {
		if (this.getFormaIngresso() != null && this.getFormaIngresso().getId() == 0)
			this.setFormaIngresso(null);
		if (this.getCurso() != null && this.getCurso().getId() == 0)
			this.setCurso(null);
		if (this.getCurriculo() != null && this.getCurriculo().getId() == 0)
			this.setCurriculo(null);
		if (this.getArea() != null && this.getArea().getId() == 0)
			this.setArea(null);
		if (this.getLinha() != null && this.getLinha().getId() == 0)
			this.setLinha(null);
		if( this.getOrigem() != null && this.getOrigem().getId() == 0 )
			this.setOrigem( null);
		if( this.getProcessoSeletivo() != null && this.getProcessoSeletivo().getId() == 0 )
			this.setProcessoSeletivo( null );
		if( this.getGestoraAcademica() != null && this.getGestoraAcademica().getId() == 0 )
			this.setGestoraAcademica(null);
		if( this.getInstituicaoEnsinoRede() != null && this.getInstituicaoEnsinoRede().getId() == 0 )
			this.setInstituicaoEnsinoRede(null);
	}
	
	@Transient
	public boolean isEmAssociacao() {
		return getDiscente().getTipo() != null && getDiscente().getTipo() == Discente.EM_ASSOCIACAO;
	}

	/** Retorna o Co-Orientador atual, ou mais recente, do discente. 
	 * @return
	 */
	public OrientacaoAcademica getCoOrientacao() {
		return coOrientacao;
	}

	/** Seta o Co-Orientador atual, ou mais recente, do discente. 
	 * @param coOrientacao
	 */
	public void setCoOrientacao(OrientacaoAcademica coOrientacao) {
		this.coOrientacao = coOrientacao;
	}

	/** Retorna a Média geral do discente, equivalente ao IRA na graduação 
	 * @return
	 */
	public Float getMediaGeral() {
		return mediaGeral;
	}

	/** Seta a Média geral do discente, equivalente ao IRA na graduação 
	 * @param mediaGeral
	 */
	public void setMediaGeral(Float mediaGeral) {
		this.mediaGeral = mediaGeral;
	}

	/** Retorna a data da última atualização dos cálculos do aluno 
	 * @return
	 */
	public Date getUltimaAtualizacaoTotais() {
		return ultimaAtualizacaoTotais;
	}

	/** Seta a data da última atualização dos cálculos do aluno
	 * @param ultimaAtualizacaoTotais
	 */
	public void setUltimaAtualizacaoTotais(Date ultimaAtualizacaoTotais) {
		this.ultimaAtualizacaoTotais = ultimaAtualizacaoTotais;
	}

	/**  Retorna o somatório de variação meses que deve ser utilizado no calculo do mês atual do discente. 
	 * @return
	 */
	public Integer getVariacaoPrazo() {
		return variacaoPrazo;
	}

	/**  Seta o somatório de variação meses que deve ser utilizado no calculo do mês atual do discente.
	 * @param variacaoPrazo
	 */
	public void setVariacaoPrazo(Integer variacaoPrazo) {
		this.variacaoPrazo = variacaoPrazo;
	}

	/** Retorna os dados da homologação de trabalho final.
	 * @return
	 */
	public HomologacaoTrabalhoFinal getHomologacaoTrabalhoFinal() {
		return homologacaoTrabalhoFinal;
	}

	/** Seta os dados da homologação de trabalho final.
	 * @param homologacaoTrabalhoFinal
	 */
	public void setHomologacaoTrabalhoFinal(
			HomologacaoTrabalhoFinal homologacaoTrabalhoFinal) {
		this.homologacaoTrabalhoFinal = homologacaoTrabalhoFinal;
	}

	public ProcessoSeletivo getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/**
	 * <p>
	 * Verifica se o discente cumpriu os trabalhos de proficiência exigidos para o seu nível (mestrado ou doutorado)
	 * a partir dos trabalhos aprovados e aproveitados.
	 * </p>
	 * <p>
	 * A quantidade de proficiências cumpridas se dá da seguinte forma:
	 * <code>quantidadeProficienciasCumpridas = proficienciasAprovadas.size() + proficienciasAproveitadas.size()</code>
	 * </p>
	 * 
	 * @param proficienciasAprovadas
	 * @param proficienciasAproveitadas
	 * @param quantidadeMinimaTrabalhosProficienciaExigida
	 * 
	 * @return <code>true</code> caso quantidadeProficienciasCumpridas >= quantidadeMinimaTrabalhosProficienciaExigida
	 */
	public boolean cumpriuTrabalhoProficiencia(Collection<MatriculaComponente> proficienciasAprovadas, Collection<MatriculaComponente> proficienciasAproveitadas, int quantidadeMinimaTrabalhosProficienciaExigida) {
		
		int quantidadeProficienciasCumpridas = proficienciasAprovadas.size() + proficienciasAproveitadas.size();
		
		return (quantidadeProficienciasCumpridas >= quantidadeMinimaTrabalhosProficienciaExigida);
	}
	
	@Override
	public String toString() {
		return (getMatricula() != null ? getMatricula() + " - " : "")
				+ (getPessoa().getNome() != null ? getPessoa().getNome() : "");
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
		return discente.getDataCadastro();
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
		discente.setDataCadastro(dataCadastro);
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

	public InstituicoesEnsino getInstituicaoEnsinoRede() {
		return instituicaoEnsinoRede;
	}

	public void setInstituicaoEnsinoRede(InstituicoesEnsino instituicaoEnsinoRede) {
		this.instituicaoEnsinoRede = instituicaoEnsinoRede;
	}
	
	public boolean isPossuiIndices() {
		
		String mesAno = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.MES_ANO_LIMITE_ALUNOS_ANTIGOS_INATIVOS);
		Calendar dataBase = CalendarUtils.getInstance("MM/yyyy", mesAno);
		
		boolean exibeIndices = true;
		if(discente.getDiscente().getMovimentacaoSaida() != null){
			if(discente.getDiscente().getMovimentacaoSaida().getTipoMovimentacaoAluno().isPermanente() && discente.getDiscente().getMovimentacaoSaida().getDataRetorno() == null 
					&& !discente.getDiscente().getMovimentacaoSaida().getDataOcorrencia().after(dataBase.getTime())){
				exibeIndices = false;
			}
		}	
		return getIndicesAcademicosExibiveis() != null && !getIndicesAcademicosExibiveis().isEmpty() && exibeIndices;
	}

	public String getTipoRendimentoAcademino(){
		return (isPossuiIndices() ? "Índices Acadêmicos" : "CR:"); 
	} 
	
	/** Retorna uma coleção de índices acadêmicos que podem ser exibidos no histórico.
	 * @return
	 */
	public Collection<IndiceAcademicoDiscente> getIndicesAcademicosExibiveis() {
		List<IndiceAcademicoDiscente> indices = new ArrayList<IndiceAcademicoDiscente>();
		if (discente.getDiscente().getIndices() != null) {
			for (IndiceAcademicoDiscente indiceAcademico : discente.getDiscente().getIndices())
				if (indiceAcademico.getIndice().isAtivo() && indiceAcademico.getIndice().isExibidoHistorico())
					indices.add(indiceAcademico);
		}
		
		Collections.sort( indices, new Comparator<IndiceAcademicoDiscente>() {

			public int compare(IndiceAcademicoDiscente o1, IndiceAcademicoDiscente o2) {
				
				Integer value1 = new Integer(o1.getIndice().getOrdem());
				Integer value2 = new Integer(o2.getIndice().getOrdem());
				
				return value1.compareTo(value2);
			}
					
		});
		
		return indices;
	}

	@Override
	public Collection<SolicitacaoApoioNee> getSolicitacoesApoioNee() {
		return null;
	}

	@Override
	public void setSolicitacoesApoioNee(
			Collection<SolicitacaoApoioNee> solicitacoesApoioNee) {
	}

	public Short getChTotalIntegralizada() {
		return chTotalIntegralizada;
	}

	public void setChTotalIntegralizada(Short chTotalIntegralizada) {
		this.chTotalIntegralizada = chTotalIntegralizada;
	}

	public Short getChObrigatoriaIntegralizada() {
		return chObrigatoriaIntegralizada;
	}

	public void setChObrigatoriaIntegralizada(Short chObrigatoriaIntegralizada) {
		this.chObrigatoriaIntegralizada = chObrigatoriaIntegralizada;
	}

	public Short getChOptativaIntegralizada() {
		return chOptativaIntegralizada;
	}

	public void setChOptativaIntegralizada(Short chOptativaIntegralizada) {
		this.chOptativaIntegralizada = chOptativaIntegralizada;
	}

	public int getCrExigidosAreaConcentracao() {
		return crExigidosAreaConcentracao;
	}

	public void setCrExigidosAreaConcentracao(int crExigidosAreaConcentracao) {
		this.crExigidosAreaConcentracao = crExigidosAreaConcentracao;
	}
	
	public Integer getTotalCreditoCalculado() { 
		return totalCreditoCalculado;	
	} 
	
	public void setTotalCreditoCalculado(Integer totalCreditoCalculado) { 
		this.totalCreditoCalculado = totalCreditoCalculado;	
	}


	
}