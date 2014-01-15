package br.ufrn.sigaa.sites.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Classe de domínio que armazena os dados dos estilos dos sites.
 * 
 * @author sist-sigaa-12
 *
 */
@Entity
@Table(name = "template_site", schema = "site")
public class TemplateSite  implements Serializable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_template_site", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@OneToOne(mappedBy= "templateSite")
	private DetalhesSite detalhesSite = new DetalhesSite();
	
	/** Define a cor do texto da sigla do site*/
	@Column(name = "cor_sigla")
	private String corSigla;
	
	/** Define a cor do texto do título do site */
	@Column(name = "cor_titulo")
	private String corTitulo;
	
	/** Define a cor do texto do subtítulo do site */
	@Column(name = "cor_sub_titulo")
	private String corSubTitulo;
	
	/** Define a cor do fundo do menu */
	@Column(name = "cor_fundo_menu")
	private String corFundoMenu;
	
	/** Define a cor do fundo do submenu e separador do menu */
	@Column(name = "cor_fundo_sub_menu")
	private String corFundoSubMenu;
	
	/** Define a cor do separador do submenu */
	@Column(name = "cor_sep_sub_menu")
	private String corSepSubMenu;
	
	/** Define a cor do texto do título do menu e submenu */
	@Column(name = "cor_tit_menu")
	private String corTitMenu;

	/** Define a cor do texto do título de uma seção do site*/
	@Column(name = "cor_tit_cont")
	private String corTitCont;
	
	/** Define a cor do texto do subtítulo  de uma seção do site*/
	@Column(name = "cor_sub_tit_cont")
	private String corSubTitCont;
	
	/** Define a cor do texto do conteúdo do site */
	@Column(name = "cor_texto_cont")
	private String corTextoCont;
	
	/** Define a cor dos links */
	@Column(name = "cor_link")
	private String corLink;
	
	/** Define a cor dos links quando o evento for mouseover */
	@Column(name = "cor_link_hover")
	private String corLinkHover;
	
	/** Define a cor do fundo do título,cabeçalho e rodapé da tabela */
	@Column(name = "cor_fundo_tabela")
	private String corFundoTabela;
	
	/** Define a cor do texto do título ,cabeçalho e rodapé da tabela*/
	@Column(name = "cor_titulo_tabela")
	private String corTituloTabela;
	
	/** Define a cor do fundo do do agrupador da tabela */
	@Column(name = "cor_sub_cab_tab")
	private String corSubCabTab;
	
	/** Define a cor do texto do agrupador da tabela */
	@Column(name = "cor_tit_sub_cab_tab")
	private String corTitSubCabTab;
	
	/** Define a cor do fundo da linha alternada da tabela*/
	@Column(name = "cor_linha_impar")
	private String corLinhaImpar;
	
	public TemplateSite(){
		resetTemplate();
	}
	
	public String getCorTituloTabela() {
		return corTituloTabela;
	}

	public void setCorTituloTabela(String corTituloTabela) {
		this.corTituloTabela = corTituloTabela;
	}

	public String getCorLinhaImpar() {
		return corLinhaImpar;
	}

	public void setCorLinhaImpar(String corLinhaImpar) {
		this.corLinhaImpar = corLinhaImpar;
	}


	public String getCorSigla() {
		return corSigla;
	}
	
	public void setCorSigla(String corSigla) {
		this.corSigla = corSigla;
	}
	
	
	public String getCorTitulo() {
		return corTitulo;
	}
	
	public void setCorTitulo(String corTitulo) {
		this.corTitulo = corTitulo;
	}
	
	
	public String getCorSubTitulo() {
		return corSubTitulo;
	}
	
	public void setCorSubTitulo(String corSubTitulo) {
		this.corSubTitulo = corSubTitulo;
	}
	
	public String getCorFundoMenu() {
		return corFundoMenu;
	}
	
	public void setCorFundoMenu(String corFundoMenu) {
		this.corFundoMenu = corFundoMenu;
	}
	
	public String getCorFundoSubMenu() {
		return corFundoSubMenu;
	}
	
	public void setCorFundoSubMenu(String corFundoSubMenu) {
		this.corFundoSubMenu = corFundoSubMenu;
	}
	
	public String getCorSepSubMenu() {
		return corSepSubMenu;
	}
	
	public void setCorSepSubMenu(String corSepSubMenu) {
		this.corSepSubMenu = corSepSubMenu;
	}
	
	public String getCorTitMenu() {
		return corTitMenu;
	}
	
	public void setCorTitMenu(String corTitMenu) {
		this.corTitMenu = corTitMenu;
	}
	
	public String getCorTitCont() {
		return corTitCont;
	}
	
	public void setCorTitCont(String corTitCont) {
		this.corTitCont = corTitCont;
	}
	
	public String getCorSubTitCont() {
		return corSubTitCont;
	}
	
	public void setCorSubTitCont(String corSubTitCont) {
		this.corSubTitCont = corSubTitCont;
	}
	

	public String getCorTextoCont() {
		return corTextoCont;
	}
	
	public void setCorTextoCont(String corTextoCont) {
		this.corTextoCont = corTextoCont;
	}
	
	public String getCorLink() {
		return corLink;
	}
	
	public void setCorLink(String corLink) {
		this.corLink = corLink;
	}
	
	public String getCorLinkHover() {
		return corLinkHover;
	}
	
	public void setCorLinkHover(String corLinkHover) {
		this.corLinkHover = corLinkHover;
	}


	/**
	 * Define as cores padrões do site
	 */
	@Transient
	public void resetTemplate(){
		
		this.corSigla = "#4D5D9C";
		this.corTitulo = "#4D5D9C";
		this.corSubTitulo = "#000000";
		this.corFundoMenu = "#2F3A60";
		this.corTitMenu = "#FFFFFF";
		this.corFundoSubMenu = "#3B4878";
		this.corSepSubMenu = "#2F3A60";	
		this.corTitCont = "#4D5D9C";
		this.corSubTitCont = "#222222";
		this.corTextoCont = "#222222";
		this.corLink = "#7D97FE";
		this.corLinkHover = "#7D97FE";
		this.corFundoTabela = "#4D5D9C";
		this.corTituloTabela = "#FFFFFF";
		this.corSubCabTab = "#EDF1F8";
		this.corTitSubCabTab = "#222222";
		this.corLinhaImpar = "#F9FBFD";
		
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public DetalhesSite getDetalhesSite() {
		return detalhesSite;
	}

	public void setDetalhesSite(DetalhesSite detalhesSite) {
		this.detalhesSite = detalhesSite;
	}

	public String getCorFundoTabela() {
		return corFundoTabela;
	}

	public void setCorFundoTabela(String corFundoTabela) {
		this.corFundoTabela = corFundoTabela;
	}

	public String getCorSubCabTab() {
		return corSubCabTab;
	}

	public void setCorSubCabTab(String corSubCabTab) {
		this.corSubCabTab = corSubCabTab;
	}

	public String getCorTitSubCabTab() {
		return corTitSubCabTab;
	}

	public void setCorTitSubCabTab(String corTitSubCabTab) {
		this.corTitSubCabTab = corTitSubCabTab;
	}

}
