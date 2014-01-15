/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/10/2010
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe utilizada para indicar quais nomes de diretores e reitor, bem como a
 * descrição das respectivas funções, serão utilizados na impressão do diploma.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "responsavel_assinatura", schema = "diploma", uniqueConstraints = {})
public class ResponsavelAssinaturaDiplomas implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_responsavel_assinatura", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Nome do Diretor do Departamento/Unidade responsável pelos cursos de Graduação. */
	@Column(name="nome_diretor_graduacao")
	private String nomeDiretorGraduacao;
	
	/** Descrição da função do Diretor do Departamento/Unidade responsável pelos cursos de Graduação. */
	@Column(name="descricao_diretor_graduacao")
	private String descricaoFuncaoDiretorGraduacao;
	
	/** Gênero do Diretor de Graduação: (M) Masculino, ou (F) Feminino. */
	@Column(name="genero_diretor_graduacao")
	private char generoDiretorGraduacao;
	
	/** Nome do Diretor do Departamento/Unidade responsável pelos cursos de Pós-Graduação. */
	@Column(name="nome_diretor_pos_graduacao")
	private String nomeDiretorPosGraduacao;
	
	/** Descrição da função do Diretor do Departamento/Unidade responsável pelos cursos de Pós-Graduação. */
	@Column(name="descricao_diretor_pos_graduacao")
	private String descricaoFuncaoDiretorPosGraduacao;
	
	/** Gênero do Diretor de Pós Graduação: (M) Masculino, ou (F) Feminino. */
	@Column(name="genero_diretor_pos_graduacao")
	private char generoDiretorPosGraduacao;
	
	/** Nome do Diretor do Departamento/Unidade responsável pela emissão dos diplomas. */
	@Column(name="nome_diretor_unidade_diploma")
	private String nomeDiretorUnidadeDiplomas;
	
	/** Descrição da função do Diretor do Departamento/Unidade responsável pela emissão dos diplomas. */
	@Column(name="descricao_diretor_unidade_diploma")
	private String descricaoFuncaoDiretorUnidadeDiplomas;
	
	/** Gênero do Diretor do Departamento/Unidade responsável pela emissão dos diplomas: (M) Masculino, ou (F) Feminino. */
	@Column(name="genero_diretor_unidade_diploma")
	private char generoDiretorUnidadeDiplomas;
	
	/** Nome do Reitor da Instituição. */
	@Column(name="nome_reitor")
	private String nomeReitor;
	
	/** Descrição da função do Reitor da Instituição. */
	@Column(name="descricao_reitor")
	private String descricaoFuncaoReitor;
	
	/** Gênero do Reitor da Instituição: (M) Masculino, ou (F) Feminino. */
	@Column(name="genero_reitor")
	private char generoReitor;
	
	/** Nome do responsável por certificados de lato sensu. */
	@Column(name="nome_responsavel_certificados_lato_sensu")
	private String nomeResponsavelCertificadosLatoSensu;
	
	/** Descrição da função do responsável por certificados de lato sensu. */
	@Column(name="descricao_responsavel_certificados_lato_sensu")
	private String descricaoFuncaoResponsavelCertificadosLatoSensu;
	
	/** Gênero do responsável por certificados de lato sensu: (M) Masculino, ou (F) Feminino. */
	@Column(name="genero_responsavel_certificados_lato_sensu")
	private char generoResponsavelCertificadosLatoSensu;
	
	/** Data de criação do registro. */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/** Registro de Entrada do usuário que cadastrou as informações. */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntradaCadastro;
	
	/** Indica se o registro é o utilizado na impressão de diplomas. */
	private boolean ativo;
	
	/** Nivel de ensino ao qual esta configuração de assinatura de diplomas está ativa. */
	private char nivel;
	
	public ResponsavelAssinaturaDiplomas() {
		ativo = true;
		generoDiretorGraduacao = Pessoa.SEXO_FEMININO;
		generoDiretorPosGraduacao = Pessoa.SEXO_FEMININO;
		generoDiretorUnidadeDiplomas = Pessoa.SEXO_FEMININO;
		generoResponsavelCertificadosLatoSensu = Pessoa.SEXO_FEMININO;
		generoReitor = Pessoa.SEXO_FEMININO;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNomeDiretorGraduacao() {
		return nomeDiretorGraduacao;
	}

	public void setNomeDiretorGraduacao(String nomeDiretorGraduacao) {
		this.nomeDiretorGraduacao = nomeDiretorGraduacao;
	}

	public String getDescricaoFuncaoDiretorGraduacao() {
		return descricaoFuncaoDiretorGraduacao;
	}

	public void setDescricaoFuncaoDiretorGraduacao(
			String descricaoFuncaoDiretorGraduacao) {
		this.descricaoFuncaoDiretorGraduacao = descricaoFuncaoDiretorGraduacao;
	}

	public String getNomeDiretorPosGraduacao() {
		return nomeDiretorPosGraduacao;
	}

	public void setNomeDiretorPosGraduacao(String nomeDiretorPosGraduacao) {
		this.nomeDiretorPosGraduacao = nomeDiretorPosGraduacao;
	}

	public String getDescricaoFuncaoDiretorPosGraduacao() {
		return descricaoFuncaoDiretorPosGraduacao;
	}

	public void setDescricaoFuncaoDiretorPosGraduacao(
			String descricaoFuncaoDiretorPosGraduacao) {
		this.descricaoFuncaoDiretorPosGraduacao = descricaoFuncaoDiretorPosGraduacao;
	}

	public String getNomeDiretorUnidadeDiplomas() {
		return nomeDiretorUnidadeDiplomas;
	}

	public void setNomeDiretorUnidadeDiplomas(
			String nomeDiretorUnidadeDiplomas) {
		this.nomeDiretorUnidadeDiplomas = nomeDiretorUnidadeDiplomas;
	}

	public String getDescricaoFuncaoDiretorUnidadeDiplomas() {
		return descricaoFuncaoDiretorUnidadeDiplomas;
	}

	public void setDescricaoFuncaoDiretorUnidadeDiplomas(
			String descricaoFuncaoDiretorUnidadeDiplomas) {
		this.descricaoFuncaoDiretorUnidadeDiplomas = descricaoFuncaoDiretorUnidadeDiplomas;
	}

	public String getNomeReitor() {
		return nomeReitor;
	}

	public void setNomeReitor(String nomeReitor) {
		this.nomeReitor = nomeReitor;
	}

	public String getDescricaoFuncaoReitor() {
		return descricaoFuncaoReitor;
	}

	public void setDescricaoFuncaoReitor(String descricaoFuncaoReitor) {
		this.descricaoFuncaoReitor = descricaoFuncaoReitor;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntradaCadastro() {
		return registroEntradaCadastro;
	}

	public void setRegistroEntradaCadastro(RegistroEntrada registroEntradaCadastro) {
		this.registroEntradaCadastro = registroEntradaCadastro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		validateRequired(nomeReitor, "Nome do Reitor", lista);
		validateRequired(descricaoFuncaoReitor, "Descrição da Função do Reitor", lista);
		if (generoReitor != Pessoa.SEXO_FEMININO && generoReitor != Pessoa.SEXO_MASCULINO)
			lista.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Gênero do Reitor da Instituição");
		
		if (nivel == NivelEnsino.GRADUACAO) {
			validateRequired(nomeDiretorGraduacao, "Nome do Diretor de Cursos de Graduação", lista);
			validateRequired(descricaoFuncaoDiretorGraduacao, "Descrição da Função do Diretor de Cursos de Graduação", lista);
			if (generoDiretorGraduacao != Pessoa.SEXO_FEMININO && generoDiretorGraduacao != Pessoa.SEXO_MASCULINO)
				lista.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Gênero do Responsável pela Graduação na Instituição");
			validateRequired(nomeDiretorUnidadeDiplomas, "Nome do Diretor da Unidade Responsável pelo Registro de Diplomas", lista);
			validateRequired(descricaoFuncaoDiretorUnidadeDiplomas, "Descrição da Função do Diretor da Unidade Responsável pelo Registro de Diplomas", lista);
			if (generoDiretorUnidadeDiplomas != Pessoa.SEXO_FEMININO && generoDiretorUnidadeDiplomas != Pessoa.SEXO_MASCULINO)
				lista.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Gênero do Diretor da Unidade de Registro de Diplomas");
		} else if (nivel == NivelEnsino.STRICTO) {
			validateRequired(nomeDiretorPosGraduacao, "Nome do Diretor de Cursos de Pós-Graduação", lista);
			validateRequired(descricaoFuncaoDiretorPosGraduacao, "Descrição da Função do Diretor de Cursos de Pós-Graduação", lista);
			if (generoDiretorPosGraduacao != Pessoa.SEXO_FEMININO && generoDiretorPosGraduacao != Pessoa.SEXO_MASCULINO)
				lista.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Gênero do Responsável pela Pós-Graduação na Instituição");
		} else if (nivel == NivelEnsino.LATO) {
			validateRequired(nomeResponsavelCertificadosLatoSensu, "Nome do Responsável pelos Certificados de Lato Sensu", lista);
			validateRequired(descricaoFuncaoResponsavelCertificadosLatoSensu, "Descrição da Função do Responsável pelos Certificados de Lato Sensu", lista);
			if (generoResponsavelCertificadosLatoSensu!= Pessoa.SEXO_FEMININO && generoResponsavelCertificadosLatoSensu != Pessoa.SEXO_MASCULINO)
				lista.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Gênero do Responsável pelos Certificados de Lato Sensu");
		}
		
		return lista;
	}

	public char getGeneroDiretorGraduacao() {
		return generoDiretorGraduacao;
	}

	public void setGeneroDiretorGraduacao(char generoDiretorGraduacao) {
		this.generoDiretorGraduacao = generoDiretorGraduacao;
	}

	public char getGeneroDiretorPosGraduacao() {
		return generoDiretorPosGraduacao;
	}

	public void setGeneroDiretorPosGraduacao(char generoDiretorPosGraduacao) {
		this.generoDiretorPosGraduacao = generoDiretorPosGraduacao;
	}

	public char getGeneroDiretorUnidadeDiplomas() {
		return generoDiretorUnidadeDiplomas;
	}

	public void setGeneroDiretorUnidadeDiplomas(char generoDiretorUnidadeDiplomas) {
		this.generoDiretorUnidadeDiplomas = generoDiretorUnidadeDiplomas;
	}

	public char getGeneroReitor() {
		return generoReitor;
	}

	public void setGeneroReitor(char generoReitor) {
		this.generoReitor = generoReitor;
	}

	public String getNomeResponsavelCertificadosLatoSensu() {
		return nomeResponsavelCertificadosLatoSensu;
	}

	public void setNomeResponsavelCertificadosLatoSensu(
			String nomeResponsavelCertificadosLatoSensu) {
		this.nomeResponsavelCertificadosLatoSensu = nomeResponsavelCertificadosLatoSensu;
	}

	public String getDescricaoFuncaoResponsavelCertificadosLatoSensu() {
		return descricaoFuncaoResponsavelCertificadosLatoSensu;
	}

	public void setDescricaoFuncaoResponsavelCertificadosLatoSensu(
			String descricaoFuncaoResponsavelCertificadosLatoSensu) {
		this.descricaoFuncaoResponsavelCertificadosLatoSensu = descricaoFuncaoResponsavelCertificadosLatoSensu;
	}

	public char getGeneroResponsavelCertificadosLatoSensu() {
		return generoResponsavelCertificadosLatoSensu;
	}

	public void setGeneroResponsavelCertificadosLatoSensu(
			char generoResponsavelCertificadosLatoSensu) {
		this.generoResponsavelCertificadosLatoSensu = generoResponsavelCertificadosLatoSensu;
	}

	public char getNivel() {
		return nivel;
	}
	
	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(nivel);
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public boolean isLatoSensu() {
		return nivel == NivelEnsino.LATO;
	}
	
	public boolean isGraduacao() {
		return nivel == NivelEnsino.GRADUACAO;
	}
	
	public boolean isStrictoSensu() {
		return nivel == NivelEnsino.STRICTO;
	}
}
