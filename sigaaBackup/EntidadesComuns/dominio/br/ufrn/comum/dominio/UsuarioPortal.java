/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/10/2007
 * Autor:     Ricardo Wendell 
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe de domínio que armazena as permissões de usuários
 * a postarem notícias/eventos nos diversos portais dos sistemas
 * 
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name="usuario_portal",schema="site") 
public class UsuarioPortal implements Validatable{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "id_usuario")
	private UsuarioGeral usuario = new UsuarioGeral();
	
	/** Se a notícia deve ser publicada no portal docente */
	@Column(name="portal_docente")
	private Boolean portalDocente;
	
	/** Se a notícia deve ser publicada no portal discente */
	@Column(name="portal_discente")
	private Boolean portalDiscente;
	
	/** Se a notícia deve ser publicada no portal público do SIGAA */
	@Column(name="portal_publico_sigaa")
	private Boolean portalPublicoSigaa;
	
	/** Se a notícia deve ser publicada no portal público do SIGAA */
	@Column(name="portal_publico_sipac")
	private Boolean portalPublicoSipac;
	
	/** Se a notícia deve ser publicada no portal coordenador de graduação */
	@Column(name="portal_coord_graduacao")
	private Boolean portalCoordGraduacao;
	
	/** Se a notícia deve ser publicada no portal coordenador stricto */
	@Column(name="portal_coord_stricto")
	private Boolean portalCoordStricto;
	
	/** Se a notícia deve ser publicada no portal coordenador lato */
	@Column(name="portal_coord_lato")
	private Boolean portalCoordLato;
	
	/** Se a notícia deve ser publicada no portal tutor */
	@Column(name="portal_tutor")
	private Boolean portalTutor;
	
	/** Se a notícia deve ser publicada no portal da avaliação institucional */
	@Column(name="portal_avaliacao_institucional")
	private Boolean portalAvaliacaoInstitucional;
	
	/** Se a notícia deve ser publicada no portal servidor */
	@Column(name="portal_servidor")
	private Boolean portalServidor;
	
	/** Se a notícia deve ser publicada no portal público do SIGRH */
	@Column(name="portal_publico_sigrh")
	private Boolean portalPublicoSigrh;
	
	/** Se a notícia deve ser publicada no portal de plano de saúde */
	@Column(name="portal_plano_saude")
	private Boolean portalPlanoSaude;
	
	/** Se a notícia deve ser publicada no portal administrativo */
	@Column(name="portal_administrativo")
	private Boolean portalAdministrativo;
	
	/** Se a notícia deve ser publicada no portal da chefia da unidade */
	@Column(name="portal_chefia_unidade")
	private Boolean portalChefiaUnidade;
	
	/** Se a notícia deve ser publicada no portal consultor */
	@Column(name="portal_consultor")
	private Boolean portalConsultor;
	
	/** Se a notícia deve ser publicada no portal SIGPP. */
	@Column(name="portal_sigpp")
	private Boolean portalSigpp;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public Boolean getPortalDocente() {
		return portalDocente;
	}

	public void setPortalDocente(boolean portalDocente) {
		this.portalDocente = portalDocente;
	}

	public Boolean getPortalDiscente() {
		return portalDiscente;
	}

	public void setPortalDiscente(boolean portalDiscente) {
		this.portalDiscente = portalDiscente;
	}

	public Boolean getPortalPublicoSigaa() {
		return portalPublicoSigaa;
	}

	public void setPortalPublicoSigaa(boolean portalPublicoSigaa) {
		this.portalPublicoSigaa = portalPublicoSigaa;
	}

	public Boolean getPortalCoordGraduacao() {
		return portalCoordGraduacao;
	}

	public void setPortalCoordGraduacao(boolean portalCoordGraduacao) {
		this.portalCoordGraduacao = portalCoordGraduacao;
	}

	public Boolean getPortalCoordStricto() {
		return portalCoordStricto;
	}

	public void setPortalCoordStricto(boolean portalCoordStricto) {
		this.portalCoordStricto = portalCoordStricto;
	}

	public Boolean getPortalCoordLato() {
		return portalCoordLato;
	}

	public void setPortalCoordLato(boolean portalCoordLato) {
		this.portalCoordLato = portalCoordLato;
	}

	public Boolean getPortalTutor() {
		return portalTutor;
	}

	public void setPortalTutor(boolean portalTutor) {
		this.portalTutor = portalTutor;
	}

	public Boolean getPortalAvaliacaoInstitucional() {
		return portalAvaliacaoInstitucional;
	}

	public void setPortalAvaliacaoInstitucional(boolean portalAvaliacaoInstitucional) {
		this.portalAvaliacaoInstitucional = portalAvaliacaoInstitucional;
	}

	public Boolean getPortalServidor() {
		return portalServidor;
	}

	public void setPortalServidor(boolean portalServidor) {
		this.portalServidor = portalServidor;
	}

	public Boolean getPortalPublicoSigrh() {
		return portalPublicoSigrh;
	}

	public void setPortalPublicoSigrh(boolean portalPublicoSigrh) {
		this.portalPublicoSigrh = portalPublicoSigrh;
	}
	
	public Boolean getPortalPlanoSaude() {
		return portalPlanoSaude;
	}

	public void setPortalPlanoSaude(boolean portalPlanoSaude) {
		this.portalPlanoSaude = portalPlanoSaude;
	}

	public Boolean getPortalAdministrativo() {
		return portalAdministrativo;
	}

	public void setPortalAdministrativo(boolean portalAdministrativo) {
		this.portalAdministrativo = portalAdministrativo;
	}

	public Boolean getPortalConsultor() {
		return portalConsultor;
	}

	public void setPortalConsultor(boolean portalConsultor) {
		this.portalConsultor = portalConsultor;
	}
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(usuario, "Usuário", lista);
		return lista;
	}

	public boolean isPortalTutor() {
		return portalTutor != null && portalTutor == true;
	}

	public boolean isPortalServidor() {
		return portalServidor != null && portalServidor == true;
	}

	public boolean isPortalPublicoSigrh() {
		return portalPublicoSigrh != null && portalPublicoSigrh == true;
	}
	
	public boolean isPortalPlanoSaude() {
		return portalPlanoSaude != null && portalPlanoSaude == true;
	}

	public boolean isPortalPublicoSigaa() {
		return portalPublicoSigaa != null && portalPublicoSigaa == true;
	}

	public boolean isPortalDocente() {
		return portalDocente != null && portalDocente == true;
	}

	public boolean isPortalDiscente() {
		return portalDiscente != null && portalDiscente == true;
	}

	public boolean isPortalCoordStricto() {
		return portalCoordStricto != null && portalCoordStricto == true;
	}

	public boolean isPortalCoordLato() {
		return portalCoordLato != null && portalCoordLato == true;
	}

	public boolean isPortalCoordGraduacao() {
		return portalCoordGraduacao != null && portalCoordGraduacao == true;
	}

	public boolean isPortalConsultor() {
		return portalConsultor != null && portalConsultor == true;
	}

	public boolean isPortalAvaliacaoInstitucional() {
		return portalAvaliacaoInstitucional != null && portalAvaliacaoInstitucional == true;
	}

	public boolean isPortalAdministrativo() {
		return portalAdministrativo != null && portalAdministrativo == true;
	}

	public void setPortalPublicoSipac(boolean portalPublicoSipac) {
		this.portalPublicoSipac = portalPublicoSipac;
	}
	
	public boolean isPortalPublicoSipac() {
		return portalPublicoSipac != null && portalPublicoSipac == true;
	}
	
	public boolean isPortalChefiaUnidade() {
		return portalChefiaUnidade != null && portalChefiaUnidade == true;
	}
	
	public Boolean getPortalPublicoSipac() {
		return portalPublicoSipac;
	}
	
	public boolean isPortalSigpp(){
		return portalSigpp !=null && portalSigpp == true;
	}
	
	public void setPortalChefiaUnidade(boolean portalChefiaUnidade) {
		this.portalChefiaUnidade = portalChefiaUnidade;
	}

	public Boolean getPortalChefiaUnidade() {
		return portalChefiaUnidade;
	}

	public void setPortalSigpp(boolean portalSigpp) {
		this.portalSigpp = portalSigpp;
	}

	public Boolean getPortalSigpp() {
		return portalSigpp;
	}
	
}