/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 14/09/2004
 *
 */
package br.ufrn.rh.dominio;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Classe de domínio do cargo de um usuário do sistema
 *
 * @author Ricardo Medeiros
 *
 */
@Entity
@Table(schema="rh", name="cargo")
public class Cargo implements PersistDB {


		public static final int CARGO_PADRAO = 1;

		public static final int DOCENTE_SUPERIOR_EFETIVO = 60001;
		
		public static final int DOCENTE_SUPERIOR_SUBSTITUTO = 60002;
		
		public static final int DOCENTE_SUPERIOR_VISITANTE = 60003;

		public static final int DOCENTE_MEDIO_E_FUNDAMENTAL_EFETIVO = 60011;
		
		public static final int DOCENTE_ENSINO_BAS_TEC_TECNOLOGICO_EFETIVO = 702001;
		
		public static final int DOCENTE_ENSINO_BAS_TEC_TECNOLOGICO_SUBS = 702003;
		
		public static final int PROFESSOR_DO_MAGISTERIO_SUPERIOR = 705001;
		
		public static final int DOCENTE_ENSINO_BAS_TEC_TECNOLOGICO_EFETIVO_ = 707001;
		
		public static final int DOCENTE_ENSINO_BAS_EX_TERRITORIO = 709001;
		
		public static final List<Integer> DOCENTE_SUPERIOR = Arrays.asList(PROFESSOR_DO_MAGISTERIO_SUPERIOR, DOCENTE_SUPERIOR_EFETIVO,
						DOCENTE_SUPERIOR_SUBSTITUTO, DOCENTE_SUPERIOR_VISITANTE);

		public static final List<Integer> DOCENTE_PERMANENTE = Arrays.asList( PROFESSOR_DO_MAGISTERIO_SUPERIOR, DOCENTE_SUPERIOR_EFETIVO,
						DOCENTE_MEDIO_E_FUNDAMENTAL_EFETIVO, DOCENTE_ENSINO_BAS_TEC_TECNOLOGICO_EFETIVO, 
						DOCENTE_ENSINO_BAS_TEC_TECNOLOGICO_EFETIVO_, DOCENTE_ENSINO_BAS_EX_TERRITORIO);

		public static final List<Integer> DOCENTE_MEDIO_TECNICO = Arrays.asList(60011,60012,60013,702001,702003,707001);

		public static final List<Integer> DOCENTE_SUBSTITUTO = Arrays.asList(60002,60012,702003);

		
		@Id
		@Column(name = "id", nullable = false)
		@GeneratedValue(generator="seqGenerator")
		@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="cargo_seq") })
    	private int id;
		
		@Column
    	private String denominacao;

    	/** Informa se o cargo foi inativado
    	 *
    	 */
    	private boolean inativo;


    	public Cargo() {

    	}

    	public Cargo(int id) {
    		this.id = id;
    	}

        /**
         * @return Retorna codigo.
         */
        public int getId() {
            return id;
        }
        /**
         * @param codigo The codigo to set.
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * @return Retorna denominacao.
         */
        public String getDenominacao() {
            return denominacao;
        }
        /**
         * @param denominacao The denominacao to set.
         */
        public void setDenominacao(String denominacao) {
            this.denominacao = denominacao;
        }

		public boolean isInativo() {
			return inativo;
		}

		public void setInativo(boolean inativo) {
			this.inativo = inativo;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + id;
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
			final Cargo other = (Cargo) obj;
			if (id != other.id)
				return false;
			return true;
		}
}
