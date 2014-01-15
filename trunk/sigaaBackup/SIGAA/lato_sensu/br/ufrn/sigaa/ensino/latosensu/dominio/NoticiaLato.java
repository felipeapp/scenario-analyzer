/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/12/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que representa uma notícia publicada pelo gestor lato
 * para ser visualizada pelos coordenadores lato
 *
 * @author Leonardo
 *
 */
@Entity
@Table(name = "noticia_lato", schema = "lato_sensu", uniqueConstraints = {})
public class NoticiaLato implements PersistDB {

		// Fields

		private int id;

		private String titulo;

		private String texto;

		private Date data;

		private boolean publicada;

		// Constructors

		/** default constructor */
		public NoticiaLato() {
		}

		/** minimal constructor */
		public NoticiaLato(int idNoticia) {
			this.id = idNoticia;
		}

		/** full constructor */
		public NoticiaLato(int idNoticia, String titulo,
				String texto, Date data, boolean publicada) {
			this.id = idNoticia;
			this.titulo = titulo;
			this.texto = texto;
			this.data = data;
			this.publicada = publicada;
		}

		// Property accessors
		@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
		@Column(name = "id_noticia_lato", unique = true, nullable = false, insertable = true, updatable = true)
		public int getId() {
			return this.id;
		}

		public void setId(int idNoticia) {
			this.id = idNoticia;
		}

		@Column(name = "data", unique = false, nullable = true, insertable = true, updatable = true)
		public Date getData() {
			return data;
		}

		public void setData(Date data) {
			this.data = data;
		}

		@Column(name = "publicada", unique = false, nullable = true, insertable = true, updatable = true)
		public boolean isPublicada() {
			return publicada;
		}

		public void setPublicada(boolean publicada) {
			this.publicada = publicada;
		}

		@Column(name = "texto", unique = false, nullable = true, insertable = true, updatable = true)
		public String getTexto() {
			return texto;
		}

		public void setTexto(String texto) {
			this.texto = texto;
		}

		@Column(name = "titulo", unique = false, nullable = true, insertable = true, updatable = true)
		public String getTitulo() {
			return titulo;
		}

		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsUtil.testEquals(this, obj, "id");
		}

		@Override
		public int hashCode() {
			return HashCodeUtil.hashAll(id);
		}


}
