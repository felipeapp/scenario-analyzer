/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendï¿½ncia de Informï¿½tica
 * Diretoria de Sistemas
 * 
 * Criado em: 23/10/2007
 * Autor:     David Pereira 
 */
package br.ufrn.comum.dominio;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Notícias a serem publicadas nos portais dos sistemas.
 *
 * @author David Pereira
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name="noticia_portal", schema="comum")
public class NoticiaPortal implements Validatable {

	public static final int TAMANHO_MAXIMO = 300;
	public static final int TAMANHO_MAXIMO_COORDENACAO_STRICTO = 1200;
	
	/** Limita a listagem da notícias **/
	public static final int MAX_NOTICIA_PAG = 15;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id", nullable = false)
	private int id;
	
	/** Título da notícia */
	private String titulo;
	
	/** Título da notícia */
	@Transient
	private String tituloFormatado;
	
	/** Descrição da notícia */
	private String descricao;
	
	@Transient
	private String descricaoFormatada;
	
	/** Se a notícia deve ou não ser publicada nos portais */
	private boolean publicada = true;
	
	/** Se a notícia deve receber uma maior destaque. */
	private boolean destaque;
		
	/** id do curso que publicou a noticia */
	@Column(name="id_curso")
	private Integer idCurso;

	/** id do programa que publicou a noticia */
	@Column(name="id_programa")
	private Integer idPrograma;	
	
	/** Data em que a notícia irá expirar, não sendo mais apresentada nos portais (opcional) */
	@Column(name="EXPIRAR_EM")
	private Date expirarEm;

	/** Arquivo anexado à notícia */
	@Column(name="id_arquivo")
	private Integer idArquivo;
	
	/** Nome do arquivo anexado à notícia */
	@Column(name="nome_arquivo")
	private String nomeArquivo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="DATA_CADASTRO")
	private Date criadoEm;
	
	@Temporal(TemporalType.TIMESTAMP)
	@AtualizadoEm
	@Column(name="data_atualizacao")
	private Date atualizadoEm;
	
	@ManyToOne
	@JoinColumn(name="ID_USUARIO_CADASTRO")
	private UsuarioGeral criadoPor;
	
	/** Se a notícia deve ser publicada no portal docente */
	@Column(name="portal_docente")
	private boolean portalDocente;
	
	/** Se a notícia deve ser publicada no portal discente */
	@Column(name="portal_discente")
	private boolean portalDiscente;
	
	/** Se a notícia deve ser publicada no portal público do SIGAA */
	@Column(name="portal_publico_sigaa")
	private boolean portalPublicoSigaa;
	
	/** Se a notícia deve ser publicada no portal público do SIPAC */
	@Column(name="portal_publico_sipac")
	private boolean portalPublicoSipac;
	
	/** Se a notícia deve ser publicada no portal coordenador de graduação */
	@Column(name="portal_coord_graduacao")
	private boolean portalCoordGraduacao;
	
	/** Se a notícia deve ser publicada no portal coordenador stricto */
	@Column(name="portal_coord_stricto")
	private boolean portalCoordStricto;
	
	/** Se a notícia deve ser publicada no portal coordenador lato */
	@Column(name="portal_coord_lato")
	private boolean portalCoordLato;
	
	/** Se a notícia deve ser publicada no portal tutor */
	@Column(name="portal_tutor")
	private boolean portalTutor;
	
	/** Se a notícia deve ser publicada no portal da avaliação institucional */
	@Column(name="portal_avaliacao_institucional")
	private boolean portalAvaliacaoInstitucional;
	
	/** Se a notícia deve ser publicada no portal servidor */
	@Column(name="portal_servidor")
	private boolean portalServidor;
	
	/** Se a notícia deve ser publicada no portal público do SIGRH */
	@Column(name="portal_publico_sigrh")
	private boolean portalPublicoSigrh;
	
	/** Se a notícia deve ser publicada no portal plano de saúde */
	@Column(name="portal_plano_saude")
	private boolean portalPlanoSaude;
	
	/** Se a notícia deve ser publicada no portal administrativo */
	@Column(name="portal_administrativo")
	private boolean portalAdministrativo;
	
	/** Se a notícia deve ser publicada no portal consultor */
	@Column(name="portal_consultor")
	private boolean portalConsultor;
	
	/** Se a notícia deve ser publicada no portal da chefia da unidade */
	@Column(name="portal_chefia_unidade")
	private boolean portalChefiaUnidade;
	
	@Column(name="portal_sigpp")
	private boolean portalSigpp;
	
	private boolean ativo;
	
	@Transient
	private boolean imagem;
	
	@Transient
	private boolean manchete;
	
	public NoticiaPortal() {
		this.ativo = true;
	}

	public NoticiaPortal(String titulo, String descricao) {
		this.titulo = titulo;
		this.descricao = descricao;
	}
	
	public NoticiaPortal(int id, String titulo, String descricao) {
		this(titulo, descricao);
		this.id = id;
	}
	
	public NoticiaPortal(int id, String titulo, String descricao, boolean destaque) {
		this(id, titulo, descricao);
		this.destaque = destaque;
	}

	public NoticiaPortal(int id, String titulo, String descricao, boolean destaque, Date criadoEm, int idArquivo, boolean publicada) {
		this(id, titulo, descricao);
		this.destaque = destaque;
		this.criadoEm = criadoEm;
		this.idArquivo = idArquivo;
		this.publicada = publicada;
	}
	
	public NoticiaPortal(int id, String titulo, String descricao, boolean destaque, int idArquivo, String nomeArquivo) {
		this(id, titulo, descricao, destaque);
		this.idArquivo = idArquivo;
		this.nomeArquivo = nomeArquivo;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		if (descricao != null)
			descricao.replace("\n", "<br/>");
		return descricao;
	}

	public String getDescricaoSemHTML() {
		if (descricao != null)
			descricao.replace("\n", "<br/>");
		
		String descricaoSemHtml = "";
		
		if (descricao != null)
			descricaoSemHtml = StringUtils.unescapeHTML(descricao.replaceAll("\\<.*?\\>", ""));
		
		return descricaoSemHtml;
	}	
	
	public void setDescricao(String descricao) {
		if (descricao != null)
			descricao.replace("\n", "<br/>");
		this.descricao = descricao;
	}

	public boolean isPublicada() {
		return publicada;
	}

	public void setPublicada(boolean publicada) {
		this.publicada = publicada;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public UsuarioGeral getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(UsuarioGeral criadoPor) {
		this.criadoPor = criadoPor;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
				
		if (!isPortalAdministrativo() && !isPortalCoordGraduacao() && 
				 !isPortalCoordLato() && !isPortalCoordStricto() && !isPortalDiscente() &&
				 !isPortalDocente() && !isPortalPublicoSigaa() && !isPortalPublicoSigrh() && 
				 !isPortalServidor() && !isPortalTutor() && !isPortalPlanoSaude() && 
				 !isPortalChefiaUnidade() && !isPortalPublicoSipac()) {
				
				lista.addErro("Portais: Campo obrigatório não informado");
		}
		
		ValidatorUtil.validateRequired(getTitulo(), "Título", lista);
		ValidatorUtil.validateRequired(getDescricao(), "Corpo da Notícia", lista);
		ValidatorUtil.validateRequired(getExpirarEm(), "Publicar até", lista);
		
		if(getTitulo().length() > 200)
			lista.addErro("Título da Notícia: Deve possuir no máximo 200 caracteres.");
		
		if(!ValidatorUtil.isEmpty(getExpirarEm()) && getId() == 0){
			Date date = new Date();
			if(date.after(getExpirarEm()) && !DateUtils.isSameDay(date, getExpirarEm()))
			 lista.addErro("Publicar até: Campo deve ser maior ou igual a data atual.");
		}
		return lista;
	}

	/**
	 * Retorna uma rescrição resumida da notícia para
	 * uso nos portais 
	 */
	public String getDescricaoResumida() {
		
		String descricaoSemHtml = StringUtils.unescapeHTML(descricao.replaceAll("\\<.*?\\>", ""));
		
		if (descricaoSemHtml.length() > TAMANHO_MAXIMO){
			descricaoSemHtml = descricaoSemHtml.replaceAll("[\\n|\\r]", " ");
			descricaoSemHtml = descricaoSemHtml.substring(0, TAMANHO_MAXIMO)+"...";
		}
				
		return descricaoSemHtml;		
		
	}
	
	/**
	 * Retorna uma rescrição resumida da notícia para
	 * uso no portal coordenador stricto 
	 */
	public String getDescricaoCoordenacaoStricto() {
		String descricaoSemHtml = StringUtils.unescapeHTML(descricao.replaceAll("\\<.*?\\>", ""));
		
		if (descricaoSemHtml.length() > TAMANHO_MAXIMO_COORDENACAO_STRICTO)
			return descricaoSemHtml.replaceAll("[\\n|\\r]", " ").replaceAll("'", "").substring(0, TAMANHO_MAXIMO_COORDENACAO_STRICTO) + "...";
		return descricaoSemHtml.replaceAll("'", "");		
	}

	public boolean isDestaque() {
		return destaque;
	}

	public void setDestaque(boolean destaque) {
		this.destaque = destaque;
	}
	
	public Date getExpirarEm() {
		return this.expirarEm;
	}

	public void setExpirarEm(Date expirarEm) {
		this.expirarEm = expirarEm;
	}

	public static int getTAMANHO_MAXIMO() {
		return TAMANHO_MAXIMO;
	}
	
	/**
	 * Indica quando a notícia irá expirar.
	 */
	public String getDescricaoExpiracao() {
		if (getExpirarEm() == null) {
			return "INDEFINIDAMENTE";
		} else {
			return Formatador.getInstance().formatarData(getExpirarEm());
		}
	}
	
	public String getDescricaoCriada(){
		
		return Formatador.getInstance().formatarDataHora(getCriadoEm());
	}

	/**
	 * Indica os portais onde a notícia deve aparecer.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getLocalizacaoDesc() {
		MultiMap localizacoes = new MultiValueMap();

		if (portalPublicoSigaa) {
			localizacoes.put("SIGAA", "Público");
		}
		if (portalDiscente) {
			localizacoes.put("SIGAA", "Discente");
		}
		if (portalDocente) {
			localizacoes.put("SIGAA", "Docente");
		}
		if (portalCoordGraduacao) {
			localizacoes.put("SIGAA", "Coord. Graduação");
		}
		if (portalCoordStricto) {
			localizacoes.put("SIGAA", "Coord. Stricto");
		}
		if (portalCoordLato) {
			localizacoes.put("SIGAA", "Coord. Lato");
		}
		if (portalTutor) {
			localizacoes.put("SIGAA", "Tutor");
		}
		if (portalAvaliacaoInstitucional) {
			localizacoes.put("SIGAA", "Avaliação Institucional");
		}
		if (portalConsultor) {
			localizacoes.put("SIGAA", "Consultor");
		}
		if (portalAdministrativo) {
			localizacoes.put("SIPAC", "Administrativo");
		}
		if (portalPublicoSipac) {
			localizacoes.put("SIPAC", "Público");
		}
		if (portalPublicoSigrh) {
			localizacoes.put("SIGRH", "Público");
		}
		if (portalServidor ) {
			localizacoes.put("SIGRH", "Servidor");
		}
		if (portalPlanoSaude ) {
			localizacoes.put("SIGRH", "Plano de Saúde");
		}
		if (portalChefiaUnidade) {
			localizacoes.put("SIGRH", "Portal da Chefia da Unidade");
		}
		if(portalSigpp) {
			localizacoes.put("SIGPP", "Portal SIGPP");
		}
		
		StringBuilder localizacao = new StringBuilder();
		Collection<String> sistemas = localizacoes.keySet();
		
		for (String sistema : sistemas) {
			localizacao.append(sistema + ": " +StringUtils.transformaEmLista( 
					CollectionUtils.toList((Collection<String>) localizacoes.get(sistema))) + "<br>");
		}
		
		return localizacao.toString();
	}

	public Integer getIdArquivo() {
		return this.idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public String getNomeArquivo() {
		return this.nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public boolean isPortalDocente() {
		return portalDocente;
	}

	public void setPortalDocente(Boolean portalDocente) {
		this.portalDocente = portalDocente;
	}

	public boolean isPortalDiscente() {
		return portalDiscente;
	}

	public void setPortalDiscente(boolean portalDiscente) {
		this.portalDiscente = portalDiscente;
	}

	public boolean isPortalPublicoSigaa() {
		return portalPublicoSigaa;
	}

	public void setPortalPublicoSigaa(boolean portalPublicoSigaa) {
		this.portalPublicoSigaa = portalPublicoSigaa;
	}

	public boolean isPortalCoordGraduacao() {
		return portalCoordGraduacao;
	}

	public void setPortalCoordGraduacao(boolean portalCoordGraduacao) {
		this.portalCoordGraduacao = portalCoordGraduacao;
	}

	public boolean isPortalCoordStricto() {
		return portalCoordStricto;
	}

	public void setPortalCoordStricto(boolean portalCoordStricto) {
		this.portalCoordStricto = portalCoordStricto;
	}

	public boolean isPortalCoordLato() {
		return portalCoordLato;
	}

	public void setPortalCoordLato(boolean portalCoordLato) {
		this.portalCoordLato = portalCoordLato;
	}

	public boolean isPortalTutor() {
		return portalTutor;
	}

	public void setPortalTutor(boolean portalTutor) {
		this.portalTutor = portalTutor;
	}

	public boolean isPortalServidor() {
		return portalServidor;
	}

	public void setPortalServidor(boolean portalServidor) {
		this.portalServidor = portalServidor;
	}

	public boolean isPortalPublicoSigrh() {
		return portalPublicoSigrh;
	}

	public void setPortalPublicoSigrh(boolean portalPublicoSigrh) {
		this.portalPublicoSigrh = portalPublicoSigrh;
	}
	
	public Boolean isPortalPlanoSaude() {
		return portalPlanoSaude;
	}

	public void setPortalPlanoSaude(boolean portalPlanoSaude) {
		this.portalPlanoSaude = portalPlanoSaude;
	}

	public boolean isPortalAdministrativo() {
		return portalAdministrativo;
	}

	public void setPortalAdministrativo(boolean portalAdministrativo) {
		this.portalAdministrativo = portalAdministrativo;
	}

	public boolean isPortalConsultor() {
		return portalConsultor;
	}

	public void setPortalConsultor(boolean portalConsultor) {
		this.portalConsultor = portalConsultor;
	}

	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public boolean isPortalAvaliacaoInstitucional() {
		return portalAvaliacaoInstitucional;
	}

	public void setPortalAvaliacaoInstitucional(boolean portalAvaliacaoInstitucional) {
		this.portalAvaliacaoInstitucional = portalAvaliacaoInstitucional;
	}

	public Integer getIdPrograma() {
		return idPrograma;
	}

	public void setIdPrograma(Integer idPrograma) {
		this.idPrograma = idPrograma;
	}

	/**
	 * Dado um objeto do tipo UsuarioPortal, seta as informações de permissão
	 * da notícia de acordo com as permissões do UsuarioPortal.
	 * @param usuarioPortal
	 */
	public void setarPermissao(UsuarioPortal usuarioPortal) {
		portalAdministrativo = usuarioPortal.isPortalAdministrativo();
		portalAvaliacaoInstitucional = usuarioPortal.isPortalAvaliacaoInstitucional();
		portalConsultor = usuarioPortal.isPortalConsultor();
		portalCoordGraduacao = usuarioPortal.isPortalCoordGraduacao();
		portalCoordLato = usuarioPortal.isPortalCoordLato();
		portalCoordStricto = usuarioPortal.isPortalCoordStricto();
		portalDiscente = usuarioPortal.isPortalDiscente();
		portalDocente = usuarioPortal.isPortalDocente();
		portalPublicoSigaa = usuarioPortal.isPortalPublicoSigaa();
		portalPublicoSipac = usuarioPortal.isPortalPublicoSipac();
		portalPublicoSigrh = usuarioPortal.isPortalPublicoSigrh();
		portalServidor = usuarioPortal.isPortalServidor();
		portalPlanoSaude = usuarioPortal.isPortalPlanoSaude();
		portalTutor = usuarioPortal.isPortalTutor();
		portalChefiaUnidade = usuarioPortal.isPortalChefiaUnidade();
		portalSigpp = usuarioPortal.isPortalSigpp();
	}
	
	public UsuarioPortal getUsuarioPortal() {
		UsuarioPortal up = new UsuarioPortal();
		up.setPortalAdministrativo(portalAdministrativo);
		up.setPortalAvaliacaoInstitucional(portalAvaliacaoInstitucional);
		up.setPortalConsultor(portalConsultor);
		up.setPortalCoordGraduacao(portalCoordGraduacao);
		up.setPortalCoordLato(portalCoordLato);
		up.setPortalCoordStricto(portalCoordStricto);
		up.setPortalDiscente(portalDiscente);
		up.setPortalDocente(portalDocente);
		up.setPortalPublicoSigaa(portalPublicoSigaa);
		up.setPortalPublicoSipac(portalPublicoSipac);
		up.setPortalPublicoSigrh(portalPublicoSigrh);
		up.setPortalServidor(portalServidor);
		up.setPortalPlanoSaude(portalPlanoSaude);
		up.setPortalChefiaUnidade(portalChefiaUnidade);
		up.setPortalTutor(portalTutor);
		up.setPortalSigpp(portalSigpp);
		return up;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public boolean isPortalPublicoSipac() {
		return portalPublicoSipac;
	}

	public void setPortalPublicoSipac(boolean portalPublicoSipac) {
		this.portalPublicoSipac = portalPublicoSipac;
	}
	
	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(Date atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

	/**
	 * @return the portalChefiaUnidade
	 */
	public boolean isPortalChefiaUnidade() {
		return portalChefiaUnidade;
	}

	/**
	 * @param portalChefiaUnidade the portalChefiaUnidade to set
	 */
	public void setPortalChefiaUnidade(boolean portalChefiaUnidade) {
		this.portalChefiaUnidade = portalChefiaUnidade;
	}

	public void setPortalSigpp(boolean portalSigpp) {
		this.portalSigpp = portalSigpp;
	}

	public boolean isPortalSigpp() {
		return portalSigpp;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getTituloFormatado() {
		
		if(tituloFormatado == null){
			tituloFormatado = StringUtils.stripHtmlTagsAndHtmlName(getTitulo());
			if(manchete && tituloFormatado.length() > 95){
				tituloFormatado = tituloFormatado.substring(0, 85) + "...";
			}else if(imagem && tituloFormatado.length() > 120){
				tituloFormatado = tituloFormatado.substring(0, 110) + "...";
			}else if(tituloFormatado.length() > 75){
				tituloFormatado = tituloFormatado.substring(0, 65) + "...";
			}
		}
		return tituloFormatado;
	}

	public void setTituloFormatado(String tituloFormatado) {
		this.tituloFormatado = tituloFormatado;
	}

	public String getDescricaoFormatada() {
		if(descricaoFormatada == null){
			descricaoFormatada = StringUtils.stripHtmlTagsAndHtmlName(getDescricao());
			if(descricaoFormatada.length() > 120){
				descricaoFormatada = descricaoFormatada.substring(0, 110) + "...";
			}
		}
		return descricaoFormatada;
	}

	public void setDescricaoFormatada(String descricaoFormatada) {
		this.descricaoFormatada = descricaoFormatada;
	}

	public boolean isImagem() {
		return imagem;
	}

	public void setImagem(boolean imagem) {
		this.imagem = imagem;
	}

	public boolean isManchete() {
		return manchete;
	}

	public void setManchete(boolean manchete) {
		this.manchete = manchete;
	}

}