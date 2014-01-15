package br.ufrn.comum.wireless;

/**
 * Entidade responsável pela AUTORIZACAO de acesso na rede Wireless
 * da UFRN quando o visitante for de fora da instituição.
 * 
 * Autenticação:
 * No 1 acesso, o visitante informa CPF, alguns dados pessoais e cria uma senha.
 * A partir do 2 acesso ele apenas precisa informar o CPF e a senha.
 * 
 * Autorização:
 * Após ser autenticado, o visitante é autorizado a acessar a Wireless por 
 * um período de tempo pré-determinado pelo departamento ao qual está 
 * vinculado (de 1 a N dias).
 * 
 * @author agostinho
 */

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;

@Entity
@Table(name="autorizacao_wireless_users_ext",schema="comum")
public class AutorizacaoUsersExt implements PersistDB, Validatable {
	
	@Id
	@Column(name="id")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	@Column(name="dias_autorizados")
	private Integer diasAutorizados;
	
	@Column(name="data_cadastro")
//	@CriadoEm
	private Date dataCadastro;
	
	private Long cpf;
	
	private String passaporte = null;

	@ManyToOne( )
	@JoinColumn(name="id_auten_wireless_users_ext")
	private AutenticacaoUsersExt autenticacao;

	@Column(name="id_registro_entrada")
	private Integer registroEntrada;
	
	@CriadoPor
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private UsuarioGeral usuario;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getDiasAutorizados() {
		return diasAutorizados;
	}

	public void setDiasAutorizados(Integer diasAutorizados) {
		this.diasAutorizados = diasAutorizados;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public AutenticacaoUsersExt getAutenticacao() {
		return autenticacao;
	}

	public void setAutenticacao(AutenticacaoUsersExt autenticacao) {
		this.autenticacao = autenticacao;
	}

	public Integer getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(Integer registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateCPF_CNPJ(cpf, "CPF", erros);
		ValidatorUtil.validateRequired(diasAutorizados, "Dias autorizados", erros);
		ValidatorUtil.validateMinValue(diasAutorizados, 1, "Dias autorizados", erros);
		ValidatorUtil.validateMaxValue(diasAutorizados, 10, "Dias autorizados", erros);
		return erros;
	}
	
}