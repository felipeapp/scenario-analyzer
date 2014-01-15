/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 14/11/2006
 *
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Curso;
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
 * Entidade que armazena os dados específicos dos alunos do Ensino Infantil.
 *
 * @author Andre M Dantas
 * @author Leonardo Campos
 */
@Entity
@Table(name = "discente_infantil", schema = "infantil", uniqueConstraints = {})
public class DiscenteInfantil implements Validatable, DiscenteAdapter {

	/** chave primária da classe */
	@Id
	@Column(name = "id_discente")
	private int id;
	
	/** Atributo utilizado para representar o Discente Infantil */
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name="id_discente", insertable=false, updatable=false)
	private Discente discente = new Discente();
	
	/** Atributo utilizado para armazenar o plano de saúde */
	@Column(name = "plano_saude", unique = false, nullable = true, insertable = true, updatable = true)
	private String planoSaude;

	/** Atributo utilizado para armazenar o pronto socorro */
	@Column(name = "pronto_socorro", unique = false, nullable = true, insertable = true, updatable = true)
	private String prontoSocorro;

	/** Atributo utilizado para armazenar as alergias */
	@Column(name = "alergia_medicamentos", unique = false, nullable = true, insertable = true, updatable = true)
	private String alergias;
	
	/** Atributo utilizado para armazenar as doenças do discente */
	private String doencas;

	/** Atributo utilizado para armazenar as informações do responsável pelo discente */
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsavel_ufrn", unique = false, nullable = true)
	private ResponsavelDiscenteInfantil responsavel = new ResponsavelDiscenteInfantil();

	/** Atributo utilizado para armazenar as informações de um outro responsável pelo discente */
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_outro_responsavel", unique = false, nullable = true)
	private ResponsavelDiscenteInfantil outroResponsavel = new ResponsavelDiscenteInfantil();

	/** Atributo utilizado para armazenar a renda familiar do discente */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_renda_familiar", unique = false, nullable = true, insertable = true, updatable = true)
	private RendaFamiliar rendaFamiliar;

	public RendaFamiliar getRendaFamiliar() {
		return rendaFamiliar;
	}

	public void setRendaFamiliar(RendaFamiliar rendaFamiliar) {
		this.rendaFamiliar = rendaFamiliar;
	}

	public String getAlergias() {
		return alergias;
	}

	public void setAlergias(String alergias) {
		this.alergias = alergias;
	}

	public ResponsavelDiscenteInfantil getOutroResponsavel() {
		return outroResponsavel;
	}

	public void setOutroResponsavel(ResponsavelDiscenteInfantil outroResponsavel) {
		this.outroResponsavel = outroResponsavel;
	}

	public String getPlanoSaude() {
		return planoSaude;
	}

	public void setPlanoSaude(String planoSaude) {
		this.planoSaude = planoSaude;
	}

	public String getProntoSocorro() {
		return prontoSocorro;
	}

	public void setProntoSocorro(String prontoSocorro) {
		this.prontoSocorro = prontoSocorro;
	}

	public ResponsavelDiscenteInfantil getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(ResponsavelDiscenteInfantil responsavel) {
		this.responsavel = responsavel;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "matricula");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId(), getMatricula());
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		if ( getPessoa().getCpf_cnpj() != null && getPessoa().getCpf_cnpj() != 0 )
			ValidatorUtil.validateCPF_CNPJ( getPessoa().getCpf_cnpj(), "CPF do Discente", erros);

		ValidatorUtil.validateRequired(getPessoa().getNome(), "Nome", erros);
		ValidatorUtil.validateRequired(getPessoa().getNomeMae(), "Nome da Mãe",	erros);
		ValidatorUtil.validateRequired(getPessoa().getSexo(), "Sexo", erros);
		ValidatorUtil.validateRequired(getPessoa().getDataNascimento(), "Data de Nascimento", erros);
		ValidatorUtil.validateBirthday(getPessoa().getDataNascimento(), "Data de Nascimento", erros);
		ValidatorUtil.validateRequiredId(getRendaFamiliar().getId(), "Renda Familiar", erros);
		ValidatorUtil.validateRequiredId(getPeriodoAtual(), "Turma do Aluno", erros);
		
		ValidatorUtil.validateCEP(getPessoa().getEnderecoContato().getCep(), "CEP", erros);
		ValidatorUtil.validateRequired(getPessoa().getEnderecoContato().getCep(), "CEP", erros);
		ValidatorUtil.validateRequired(getPessoa().getEnderecoContato().getLogradouro(), "Logradouro", erros);
		ValidatorUtil.validateRequired(getPessoa().getEnderecoContato().getNumero(), "Número", erros);
		ValidatorUtil.validateRequired(getPessoa().getEnderecoContato().getBairro(), "Bairro", erros);
		ValidatorUtil.validateRequired(getPessoa().getEnderecoContato().getUnidadeFederativa(), "Unidade Federativa", erros);
		ValidatorUtil.validateRequired(getPessoa().getEnderecoContato().getMunicipio(), "Município", erros);
		ValidatorUtil.validateTelefone(getPessoa().getTelefone(), "Tel. Fixo", erros);
		
		ValidatorUtil.validateCPF_CNPJ(responsavel.getPessoa().getCpf_cnpj(), "CPF do Responsável (1)", erros);
		ValidatorUtil.validateRequired(responsavel.getPessoa().getDataNascimento(), "Data de Nascimento do Responsável (1)", erros);
        ValidatorUtil.validateRequired(responsavel.getPessoa().getNome(), "Nome do Responsável (1)", erros);
        ValidatorUtil.validateRequired(responsavel.getGrauParentesco(), "Grau de Parentesco do Responsável (1)", erros);
        ValidatorUtil.validateRequired(responsavel.getProfissao(), "Profissão do Responsável (1)", erros);
        ValidatorUtil.validateRequiredId(responsavel.getEscolaridade().getId(), "Escolaridade do Responsável (1)", erros);
        
        ValidatorUtil.validateRequired(responsavel.getPessoa().getCodigoAreaNacionalTelefoneFixo(), "Telefone Fixo (DDD) do Responsável (1)", erros);
        ValidatorUtil.validateTelefone(responsavel.getPessoa().getTelefone(), "Telefone Fixo do Responsável (1)", erros);
        ValidatorUtil.validateTelefone(responsavel.getPessoa().getCelular(), "Celular do Responsável (1)", erros);
        ValidatorUtil.validateTelefone(responsavel.getTelefoneTrabalho(), "Telefone do Trabalho do Responsável (1)", erros);
        ValidatorUtil.validateRequired(responsavel.getPessoa().getTelefone(), "Telefone Fixo do Responsável (1)", erros);
        
        if ( !responsavel.getPessoa().getEmail().equals("") )
        	ValidatorUtil.validateEmail(responsavel.getPessoa().getEmail(), "Email do Responsável (1)", erros);
   
        if ( outroResponsavel.getPessoa().getCpf_cnpj() != null && outroResponsavel.getPessoa().getCpf_cnpj() != 0 )
        	ValidatorUtil.validateCPF_CNPJ(outroResponsavel.getPessoa().getCpf_cnpj(), "CPF do Responsável (2)", erros);
        
        if ( !outroResponsavel.getPessoa().getEmail().equals("") )
        	ValidatorUtil.validateEmail(outroResponsavel.getPessoa().getEmail(), "Email do Responsável (2)", erros);

        ValidatorUtil.validateMaxLength(discente.getObservacao(), 1000, "Observações", erros);
        
        if ( erros.isEmpty() ) {
        	if (responsavel.getPessoa().getDataNascimento() != null && discente.getPessoa().getDataNascimento() != null 
        			&& responsavel.getPessoa().getDataNascimento().getTime() > discente.getPessoa().getDataNascimento().getTime()) {
        		erros.addErro("A data de Nascimento do Discente não pode ser inferior a data de Nascimento do Responsável (1).");
        	}
        	if (outroResponsavel.getPessoa().getDataNascimento() != null && discente.getPessoa().getDataNascimento() != null 
        			&& outroResponsavel.getPessoa().getDataNascimento().getTime() > discente.getPessoa().getDataNascimento().getTime()) {
        		erros.addErro("A data de Nascimento do Discente não pode ser inferior a data de Nascimento do Responsável (2).");
        	}
		}
        
		return erros;
	}

    public String getDoencas() {
        return doencas;
    }

    public void setDoencas(String doencas) {
        this.doencas = doencas;
    }

	@Override
	public int getId() {
		return id;
	}

	@Override
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
		return discente.isDiscenteEad();
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
