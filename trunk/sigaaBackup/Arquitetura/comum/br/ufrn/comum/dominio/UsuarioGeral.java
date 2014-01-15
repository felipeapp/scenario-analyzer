/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/09/2004
 */
package br.ufrn.comum.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Transient;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;

/**
 * Classe de domínio do usuário do sistema
 *
 * @author Gleydson Lima
 *
 */
public class UsuarioGeral extends AbstractMovimento implements PersistDB, Comparable<UsuarioGeral> {

	/**
	 * usuário utilizado para operações automáticas e rotinas de migração
	 * onde eh necessário registrar o usuário que realizou a operação
	 */
	public static final int USUARIO_SISTEMA = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.USUARIO_SISTEMA);
	
	/**
	 * usuário utilizado para operações realizadas dentro de um timer
	 */
	public static final int TIMER_SISTEMA = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.TIMER_SISTEMA);
	
	/** Identificador */
	protected int id;

	/** Pessoa associada ao usuário */
	protected PessoaGeral pessoa;

	private int idPessoa;

	/** Login do usuário */
	protected String login;

	/** Senha do usuário */
	protected String senha;
	
	/** Senha para acesso mobile */
	protected String senhaMobile;

	/** Data em que a senha do usuário irá expirar e ele deve mudar sua senha. */
	protected Date expiraSenha;
	
	/** Data do último acesso do usuário ao sistema */
	protected Date ultimoAcesso;

	/** Unidade do usuário. */
	protected UnidadeGeral unidade;

	/** Papéis que o usuário possui */
	protected Collection<Papel> papeis;

	/** Permissões que o usuário possui */
	protected List<Permissao> permissoes;

	/** Foto do usuário */
	protected Integer idFoto;

	/** Papéis atribuidos por designação, por exemplo */
	private Collection<Papel> papeisTemporarios = new ArrayList<Papel>();

	/** E-Mail do usuário */
	protected String email;

	/** Se o usuário é funcionário ou não */
	protected boolean funcionario;

	/**
	 * Coleção de {@link UsuarioUnidade } com as unidades adicionais do
	 * usuário.
	 */
	protected Collection<UsuarioUnidade> usuariosUnidades;

	/**
	 * Indica se o usuário está inativo no sistema. O usuário inativa
	 * não pode logar no sistema
	 */
	protected boolean inativo;

	/** Ramal do usuário */
	protected String ramal;

	/** Usado no cadastro */
	protected String objetivoCadastro;

	/** Justificativa do gestor para negar cadastro do usuario */
	protected String justificativaCadastroNegado;

	/** Usado no cadastro */
	protected String usuarioProtocolo;

	/** Usado no cadastro */
	protected String usuarioDMP;

	/** Flag indica se cadastro do usuário foi liberado pelo administrador */
	protected Boolean autorizado;

	protected String IP;

	/** Ano orçamentário do usuário */
	protected int anoOrcamentario;

	protected RegistroEntrada registroEntrada;

	/** Tipo do usuário -> Se Servidor(1), Aluno(2), Credor(3), Outros(4) */
	protected TipoUsuario tipo;

	/**
	 * Id do aluno se usuário é aluno. Usado na atualização do usuário
	 * pra saber sua matrícula
	 */
	protected Integer idAluno;

	/**
	 * Id do servidor se usuário é servidor. Usado na atualização do
	 * usuário pra saber sua matrícula
	 */
	protected Integer idServidor;

	protected List<Comando> comandosBloqueados = new ArrayList<Comando>();

	
	protected Date ultimaOperacao;

	protected Collection<SubSistema> subsistemas;

	/** Origem de cadastro do usuário: SIGAA, SIPAC */
	protected String origemCadastro;

	/** Data de cadastro do usuário */
	protected Date dataCadastro;

	/** Consultor associado ao usuário. */
	protected Integer idConsultor;

	/** Docente externo associado ao usuário. */
	private Integer idDocenteExterno;

	/** Tutor (EAD) associado ao usuário. */
	protected Integer idTutor;
	
	/** Empresa consignatária associada ao usuário. Dado utilizado no SIGRH. Incluso
	 * nesta classe devido a sincronização dos dados. */
	private Integer idConsignataria;

	// usado nos logins entre vários sistemas
	protected PassaporteLogon passaporte;
	
	private String hashConfirmacaoCadastro;

	private Date ultimaVerificacaoInatividade;	
	public UsuarioGeral(int id) {
		this.id = id;
		pessoa = new PessoaGeral();
	}

	public UsuarioGeral() {
		pessoa = new PessoaGeral();
	}

	/**
	 * Sobrecarga do construtor usado apenas em instanciaï¿½ï¿½o dinï¿½mica na
	 * consulta
	 * {@link br.ufrn.sipac.arq.dao.impl.UsuarioImpl#findAllByHierarquia(UnidadeGeral)}.
	 *
	 * @see br.ufrn.sipac.arq.dao.impl.UsuarioImpl#findAllByHierarquia(UnidadeGeral)
	 * @param id
	 * @param nome
	 * @param cpf
	 * @param login
	 * @param siglaUnidade
	 *            sigla da unidade do usuï¿½rio
	 */
	public UsuarioGeral(int id, String nome, long cpf, String login,
			String siglaUnidade) {
		this.id = id;
		this.pessoa = new PessoaGeral();
		this.pessoa.setNome(nome);
		this.pessoa.setCpf_cnpj(cpf);

		this.login = login;
		this.unidade = new UnidadeGeral();
		this.unidade.setSigla(siglaUnidade);
	}



	public Integer getIdConsultor() {
		return idConsultor;
	}

	public void setIdConsultor(Integer idConsultor) {
		this.idConsultor = idConsultor;
	}

	public int getAnoOrcamentario() {
		return anoOrcamentario;
	}

	public void setAnoOrcamentario(int anoOrcamentario) {
		this.anoOrcamentario = anoOrcamentario;
	}

	/**
	 * @return Retorna cpf.
	 */
	public Long getCpf() {
		return pessoa.getCpf_cnpj();
	}


	public void addPapelTemporario(Papel p) {
		papeisTemporarios.add(p);
	}
	
	public void addPapelTemporario(int papel) {
		addPapelTemporario(new Papel(papel));
	}
	
	public void addPapelTemporario(int[] papeis) {
		
		if (papeis != null){
			for (int i = 0; i < papeis.length; i++) {
				addPapelTemporario(new Papel(papeis[i]));
			}
		}
	}
	

	/**
	 * @param cpf
	 *            The cpf to set.
	 */
	public void setCpf(Long cpf) {
		if (cpf == null)
			cpf = 0L;
		pessoa.setCpf_cnpj(cpf);
	}

	/**
	 * @return Retorna dataNascimento.
	 */
	public Date getDataNascimento() {
		return pessoa.getDataNascimento();
	}

	/**
	 * @param dataNascimento
	 *            The dataNascimento to set.
	 */
	public void setDataNascimento(Date dataNascimento) {
		pessoa.setDataNascimento(dataNascimento);
	}

	public boolean isInativo() {
		return inativo;
	}

	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}

	/**
	 * @return Retorna sexo.
	 */
	public char getSexo() {
		return pessoa.getSexo();
	}

	/**
	 * @param sexo
	 *            The sexo to set.
	 */
	public void setSexo(char sexo) {
		pessoa.setSexo(sexo);
	}

	/**
	 * @return Retorna unidade.
	 */
	public UnidadeGeral getUnidade() {
		return unidade;
	}

	/**
	 * @param unidade
	 *            The unidade to set.
	 */
	public void setUnidade(UnidadeGeral unidade) {
		this.unidade = unidade;
	}

	/**
	 * @return Retorna expiraSenha.
	 */
	public Date getExpiraSenha() {
		return expiraSenha;
	}

	/**
	 * @param expiraSenha
	 *            The expiraSenha to set.
	 */
	public void setExpiraSenha(Date expiraSenha) {
		this.expiraSenha = expiraSenha;
	}

	/**
	 * @return Retorna id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return Retorna login.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 *            The login to set.
	 */
	public void setLogin(String login) {
		this.login = (login != null ? login.toLowerCase() : "");
	}

	/**
	 * @return Retorna matricula.
	 */
	public String getMatricula() {
		return pessoa.getMatricula();
	}

	/**
	 * @param matricula
	 *            The matricula to set.
	 */
	public void setMatricula(String matricula) {
		pessoa.setMatricula(matricula);
	}

	public void setMatricula(int matricula) {
		pessoa.setMatricula(String.valueOf(matricula));
	}

	/**
	 * @return Retorna nome.
	 */
	public String getNome() {
		if (pessoa == null){
			return "";
		}
		return pessoa.getNome();
	}

	/**
	 * @param nome
	 *            The nome to set.
	 */
	public void setNome(String nome) {
		pessoa.setNome(nome);
	}

	/**
	 * @return Retorna senha.
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * @param senha
	 *            The senha to set.
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * @return Retorna papeis.
	 */
	public Collection<Papel> getPapeis() {
		return papeis;
	}
	
	public Collection<Papel> getPapeisOrdenado() {
		Collections.sort((List<Papel>) papeis, new Comparator<Papel>() {
			public int compare(Papel o1, Papel o2) {
				return o1.getNome().compareTo(o2.getNome());
			}
		});
		return papeis;
	}

	/**
	 * @param papeis
	 *            The papeis to set.
	 */
	public void setPapeis(Collection<Papel> papeis) {
		this.papeis = papeis;
	}

	/**
	 * Checa se o usuário possui um determinado papel
	 *
	 * @param papel
	 * @return true se o usuário possuir papel informado.
	 */
	public boolean isUserInRole(int papel) {

		Papel p = new Papel();
		p.setId(papel);
		
		if (!isEmpty(papeis)) {
			if (papeis.contains(p))
				return true;
		}
		
		if (!isEmpty(papeisTemporarios)) {
			if (papeisTemporarios.contains(p))
				return true;
		}
		
		
		return false;
	}

	/**
	 * Retorna a permissão do usuário relacionada com o papel
	 * passado como parâmetro.
	 */
	public List<Permissao> getPermissao(int papel) {
		List<Permissao> result = new ArrayList<Permissao>();
		if (isUserInRole(papel)) {
			if (!isEmpty(permissoes)) {
				for (Permissao p : permissoes) {
					if (p.getPapel().getId() == papel)
						result.add(p);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Retorna a permissão do usuário relacionada com o papel
	 * passado como parâmetro.
	 */
	public Permissao getPermissao(int papel, int unidade) {
		List<Permissao> permissoes = getPermissao(papel);
		if (!isEmpty(permissoes)) {
			Permissao permissao = null;
			
			for (Permissao p : permissoes) {
				if (p.getPapel().isExigeUnidade() && p.getUnidadePapel() != null && p.getUnidadePapel().getId() == unidade) {
					permissao = p;
					break;
				} else {
					permissao = p;
				}
			}
			
			return permissao;
		}
		
		return null;
	}
	
	/**
	 * Checa se o usuário possui algum dos papeis informados
	 *
	 * @param papeis
	 * @return true se o usuário possuir algum dos papeis informados.
	 */
	public boolean isUserInRole(int... papeis) {
		for (int papel : papeis) {
			if (isUserInRole(papel))
				return true;
		}

		return false;
	}

	/**
	 * Verifica se o usuário possui acesso a um determinado SubSistema
	 *
	 * @param subsistema
	 * @return true se usuário possui acesso ao subsistema informado
	 * @see br.ufrn.arq.seguranca.SubSistemas
	 */
	public boolean isUserInSubSistema(int subsistema) {

		try {
			if (papeis != null) {
				for (Iterator<Papel> it = papeis.iterator(); it.hasNext(); ) {
					Papel p = it.next();
					if (p !=null && p.getSubSistema() != null && p.getSubSistema().getId() == subsistema)
						return true;
				}
			}
			
			if (papeisTemporarios != null) {
				for (Iterator<Papel> it = papeisTemporarios.iterator(); it.hasNext(); ) {
					Papel p = it.next();
					if (p !=null && p.getSubSistema() != null && p.getSubSistema().getId() == subsistema)
						return true;
				}
			}
			
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Verifica se o usuï¿½rio tem algum papel dentro do SIPAC. Esta
	 * verificaï¿½ï¿½o faz sentido pois o usuï¿½rio pode ser do Sistema de
	 * Protocolos e do Sistema Orï¿½amentï¿½rio
	 *
	 * @return true se usuï¿½rio possuir algum papel dentro do SIPAC
	 */

	public boolean isUserInSistema(int sistema) {
		Collection<Papel> papeis = new ArrayList<Papel>();
		
		papeis.addAll(getPapeis());
		papeis.addAll(getPapeisTemporarios());
		
		if (papeis != null && !papeis.isEmpty()) {
			for (Iterator<Papel> it = papeis.iterator(); it.hasNext(); ) {
				Papel p = it.next();
				if (p.getSistema() != null && p.getSistema().getId() == sistema)
					return true;
			}
		}
		return false;
	}


	public boolean isUserInSIPAC() {
		return isUserInSistema(Sistema.SIPAC);
	}

	/**
	 * Verifica se o usuário tem algum papel dentro do SIGAA. Esta
	 * verificação faz sentido pois o usuário pode ser do SIPAC ou RHNet
	 *
	 * @return true se usuário possuir algum papel dentro do SIGAA
	 */
	public boolean isUserInSIGAA() {
			return isUserInSistema(Sistema.SIGAA);
		}

	public boolean isUserInSIGRH() {
		return isUserInSistema(Sistema.SIGRH);

	}

	/** Retorna data do ï¿½ltimo acesso */
	public Date getUltimoAcesso() {
		return ultimoAcesso;
	}

	/** Seta data do ï¿½tlimo acesso */
	public void setUltimoAcesso(Date ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}

	/**
	 * Verifica se a unidade do usuï¿½rio estï¿½ na presente na hierarquia da
	 * unidade passada como parametro.
	 *
	 * @param und
	 * @return true se a unidade do usuï¿½rio estï¿½ na presente na hierarquia
	 *         da unidade passada como parametro.
	 */
	public boolean isInHierarquia(UnidadeGeral und) {
		return und.getHierarquia().contains("." + unidade.getId() + ".");
	}

	/**
	 * Verifica se a unidade gestora do usuï¿½rio estï¿½ na presente na
	 * hierarquia da unidade passada como parametro.
	 *
	 * @param und
	 * @return true se a unidade gestora do usuï¿½rio estï¿½ na presente na
	 *         hierarquia da unidade passada como parametro.
	 */
	public boolean isInHierarquiaGestora(UnidadeGeral und) {
		return und.getHierarquia().contains(
				"." + unidade.getGestora().getId() + ".");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioGeral other = (UsuarioGeral) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Collection<UsuarioUnidade> getUsuariosUnidades() {
		return usuariosUnidades;
	}

	public void setUsuariosUnidades(Collection<UsuarioUnidade> usuariosUnidades) {
		this.usuariosUnidades = usuariosUnidades;
	}

	/**
	 * Retorna unidades contidas em {@link #usuariosUnidades}.
	 *
	 * @return
	 */
	public Collection<UnidadeGeral> getUnidades() {
		ArrayList<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();

		if (getUsuariosUnidades() != null) {
			for (UsuarioUnidade u : getUsuariosUnidades()) {
				if(!unidades.contains(u))
					unidades.add(u.getUnidade());
			}
		}
		if(!unidades.contains(getUnidade()))
			unidades.add(getUnidade());

		return unidades;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String ip) {
		IP = ip;
	}

	public PessoaGeral getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaGeral pessoa) {
		this.pessoa = pessoa;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public TipoUsuario getTipo() {
		return tipo;
	}
	
	@Transient
	public String getTipoUsuarioDesc() {
		return TipoUsuario.getTipoUsuarioDesc(tipo.getId());
	}

	public void setTipo(TipoUsuario tipo) {
		this.tipo = tipo;
	}

	public Integer getIdAluno() {
		return idAluno;
	}

	public void setIdAluno(Integer idAluno) {
		this.idAluno = idAluno;
	}

	public Integer getIdServidor() {
		return idServidor;
	}

	public void setIdServidor(Integer idServidor) {
		this.idServidor = idServidor;
	}

	public boolean isAutorizado() {
		return autorizado == null ? false : autorizado;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

	public boolean isFuncionario() {
		return funcionario;
	}

	public void setFuncionario(boolean funcionario) {
		this.funcionario = funcionario;
	}

	public String getObjetivoCadastro() {
		return objetivoCadastro;
	}

	public void setObjetivoCadastro(String objetivoCadastro) {
		this.objetivoCadastro = objetivoCadastro;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

	public List<Comando> getComandosBloqueados() {
		return comandosBloqueados;
	}

	public void setComandosBloqueados(List<Comando> comandosBloqueados) {
		this.comandosBloqueados = comandosBloqueados;
	}

	public Date getUltimaOperacao() {
		return ultimaOperacao;
	}

	public void setUltimaOperacao(Date ultimaOperacao) {
		this.ultimaOperacao = ultimaOperacao;
	}

	public String getUsuarioDMP() {
		return usuarioDMP;
	}

	public void setUsuarioDMP(String usuarioDMP) {
		this.usuarioDMP = usuarioDMP;
	}

	public String getUsuarioProtocolo() {
		return usuarioProtocolo;
	}

	public void setUsuarioProtocolo(String usuarioProtocolo) {
		this.usuarioProtocolo = usuarioProtocolo;
	}

	public String getJustificativaCadastroNegado() {
		return justificativaCadastroNegado;
	}

	public void setJustificativaCadastroNegado(
			String justificativaCadastroNegado) {
		this.justificativaCadastroNegado = justificativaCadastroNegado;
	}

	public Collection<SubSistema> getSubsistemas() {

		if (subsistemas != null && !subsistemas.isEmpty()) {
			return subsistemas;
		} else {
			subsistemas = new ArrayList<SubSistema>();
			if (papeis != null) {
				for (Papel p : papeis) {
					/*
					 * Adiciona os subSistemas na arrayList criando uma cópia dos
					 * valores de suas propriedades, caso contrário o objeto
					 * subSistema é adicionado sem id
					 */
					if (!subsistemas.contains(p.getSubSistema())) {
						subsistemas.add(p.getSubSistema());
					}
				}
			}
			
			if (papeisTemporarios != null){
				for (Papel p : papeisTemporarios) {
					if(p != null) {
						if (!subsistemas.contains(p.getSubSistema())) {
							subsistemas.add(p.getSubSistema());
						}
					}
				}
			}
			Collections.sort((List<SubSistema>) subsistemas);
		}
		return subsistemas;
	}

	public int getUnidadeGestora() {
		return unidade.getGestora().getId();
	}

	public int getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(int idPessoa) {
		this.idPessoa = idPessoa;
	}

	public String getOrigemCadastro() {
		return origemCadastro;
	}

	public void setOrigemCadastro(String origemCadastro) {
		this.origemCadastro = origemCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getIdDocenteExterno() {
		return idDocenteExterno;
	}

	public void setIdDocenteExterno(Integer idDocenteExterno) {
		this.idDocenteExterno = idDocenteExterno;
	}

	public int compareTo(UsuarioGeral obj) {
		return getNome().compareToIgnoreCase(obj.getNome());

	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public Integer getIdTutor() {
		return idTutor;
	}

	public void setIdTutor(Integer idTutor) {
		this.idTutor = idTutor;
	}

	@Transient
	public List<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissao> permissaos) {
		this.permissoes = permissaos;
	}

	@Transient
	public PassaporteLogon getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(PassaporteLogon passaporte) {
		this.passaporte = passaporte;
	}

	public Integer getIdConsignataria() {
		return idConsignataria;
	}

	public void setIdConsignataria(Integer idConsignataria) {
		this.idConsignataria = idConsignataria;
	}

	public Collection<Papel> getPapeisTemporarios() {
		return papeisTemporarios;
	}
	
	public void setPapeisTemporarios(Collection<Papel> papeisTemporarios) {
		this.papeisTemporarios = papeisTemporarios;
	}

	public void setSubsistemas(Collection<SubSistema> subsistemas) {
		this.subsistemas = subsistemas;
	}
	
	public String toString() {
		return (pessoa != null)?(getId() + " - " + pessoa.getNome()):getId() + "";
	}

	public String getNomeLogin() {
		String result = "";
		
		String nome = getNome();
		if (nome != null && !nome.trim().isEmpty())
			result += nome;
		if (login != null && !login.trim().isEmpty())
			result += " (" + login + ")";
		return result;
	}
	
	public void setNomeLogin(String nome) { 
		setNome(nome);
	}

	public boolean possuiPapelTemporario(int papel) {
		return papeisTemporarios.contains(new Papel(papel));
	}

	public String getSenhaMobile() {
		return senhaMobile;
	}

	public void setSenhaMobile(String senhaMobile) {
		this.senhaMobile = senhaMobile;
	}

	public String getHashConfirmacaoCadastro() {
		return hashConfirmacaoCadastro;
	}

	public void setHashConfirmacaoCadastro(String hashConfirmacaoCadastro) {
		this.hashConfirmacaoCadastro = hashConfirmacaoCadastro;
	}

	public Date getUltimaVerificacaoInatividade() {
		return ultimaVerificacaoInatividade;
	}

	public void setUltimaVerificacaoInatividade(Date ultimaVerificacaoInatividade) {
		this.ultimaVerificacaoInatividade = ultimaVerificacaoInatividade;
	}
	
}