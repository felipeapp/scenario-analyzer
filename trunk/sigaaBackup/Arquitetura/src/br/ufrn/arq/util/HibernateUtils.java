/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 15/06/2007
 */
package br.ufrn.arq.util;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.WordUtils;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.proxy.HibernateProxy;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Classe com funções utilitárias e constantes para serem usadas no Hibernate. 
 *
 * @author Andre M Dantas
 * @author Gleydson Lima
 *
 */
public class HibernateUtils {

	/**
	 * Constante para ser usada como configuarção do @Column para definição do columnDefiniton 
	 * visando portabilidade do tipo TEXT.
	 */
	public static final String TEXT_COLUMN_DEFINITION = "TEXT";
	

	public static String createUpdateQuery(SessionFactory sf, Class<?> classe, Integer id, String campo, Object valor) throws Exception {
		
		AbstractEntityPersister persister = (AbstractEntityPersister) sf.getClassMetadata(classe);
		return "UPDATE " + getNomeTabela(persister, campo) + " SET " + getNomeColuna(persister, campo) + " = " + getIdValue(classe, campo, valor)
				+ " WHERE " + getPK(sf, persister, campo) + " = " + id;
	}

	public static String createUpdateQuery(SessionFactory sf, Class<?> classe, Integer id, String[] campos, Object[] valores) throws Exception {
		AbstractEntityPersister persister = (AbstractEntityPersister) sf.getClassMetadata(classe);
		String update = "UPDATE " + getNomeTabela(persister) + " SET ";

		for(int i=0; i<campos.length; i++ ){
			update += getNomeColuna(persister, campos[i]) + " = " + getIdValue(classe, campos[i], valores[i]);
		}

		return update += " WHERE " + getPK(persister) + " = " + id;
	}

	public static String createUpdateQuery(SessionFactory sf, Class<?> classe, Integer id, String[] campos, Object[] valores, Field updatedByField,
			Object updatedByValue, Field updatedAtField, Object updatedAtValue){
		
		AbstractEntityPersister persister = (AbstractEntityPersister) sf.getClassMetadata(classe);
		StringBuilder sb = new StringBuilder("UPDATE " + getNomeTabela(persister) + " SET ");

		for(int i=0; i<campos.length; i++ ){
			sb.append(getNomeColuna(persister, campos[i]) + " = " + getIdValue(classe, campos[i], valores[i]));
			if(i < campos.length - 1){
				sb.append(", ");
			}
		}

		if (updatedByField != null)
			sb.append(", " + getNomeColuna(persister, updatedByField.getName()) + " = "
					+ getIdValue(classe, updatedByField.getName(), updatedByValue));
		if (updatedAtField != null)
			sb.append(", " + getNomeColuna(persister, updatedAtField.getName()) + " = "
					+ getIdValue(classe, updatedAtField.getName(), updatedAtValue));

		sb.append(" WHERE " + getPK(persister) + " = " + id);
		return sb.toString();
	}

	public static String createUpdateQuery(SessionFactory sf, Class<?> classe, Integer id, String campo, Object valor, Field updatedByField,
			Object updatedByValue, Field updatedAtField, Object updatedAtValue) throws Exception {
		
		AbstractEntityPersister persister = (AbstractEntityPersister) sf.getClassMetadata(classe);
		
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + getNomeTabela(persister, campo) + " SET " + getNomeColuna(persister, campo) + " = " + getIdValue(classe, campo, valor));

		if (updatedByField != null)
			sb.append(", " + getNomeColuna(persister, updatedByField.getName()) + " = "
					+ getIdValue(classe, updatedByField.getName(), updatedByValue));
		if (updatedAtField != null)
			sb.append(", " + getNomeColuna(persister, updatedAtField.getName()) + " = "
					+ getIdValue(classe, updatedAtField.getName(), updatedAtValue));
		sb.append(" WHERE " + getPK(sf, persister, campo) + " = " + id);
		return sb.toString();
	}

	public static String generateDDL(SessionFactory sf, Class<?>... classe) {
		StringBuffer sb = new StringBuffer();
		for (Class<?> c : classe) {
			sb.append(generateDDL(sf, c) + "\n");
		}
		return sb.toString();
	}

	public static String generateDDL(SessionFactory sf, Class<?> classe) {
		StringBuffer sql = new StringBuffer();

		AbstractEntityPersister persister = (AbstractEntityPersister) sf.getClassMetadata(classe);
		
		sql.append("CREATE TABLE " + getNomeTabela(persister) + " (\n");
		for (Column coluna : getColunas(persister, classe)) {
			sql.append("\t" + coluna.getName() + " " + coluna.getSqlType() + ", \n");
		}
		sql.append("\t\tPRIMARY KEY (" + getPK(persister) + "), \n");
		for (ManyToOne fk : getFKs(sf, classe)) {
			sql.append("\t\tFOREIGN KEY (" + fk.getForeignKeyName() + ") REFERENCES " + fk.getReferencedEntityName() + " ("
					+ fk.getReferencedPropertyName() + "), \n");
		}
		sql.deleteCharAt(sql.lastIndexOf(","));
		sql.append(");");

		return sql.toString();
	}

	@SuppressWarnings("unchecked")
	private static String getIdValue(Class<?> classe, String campo, Object valor) {
		
		if (valor == null)
			return null;
		List<Class<?>> interfaces = ClassUtils.getAllInterfaces(valor.getClass());
		if (interfaces != null && (interfaces.contains(PersistDB.class) || interfaces.contains(Validatable.class))) {
			try {
				return PropertyUtils.getProperty(valor, "id").toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// se nao for classe persistente
		Field f = ReflectionUtils.getExpressionField(classe, campo);
		if (f.getType().equals(String.class) || f.getType().equals(Character.class) || f.getType().equals(char.class))
			return "'" + StringUtils.escapeBackSlash(valor.toString()) + "'";
		else if (f.getType().equals(Date.class)) {
			String formato = "''yyyy-MM-dd HH:mm:ss''";
			if (getTipoColuna(classe, f).equalsIgnoreCase("date"))
				formato = "''yyyy-MM-dd''";
			DateFormat df = new SimpleDateFormat(formato);
			return df.format(valor);
		} else
			return valor.toString();
	}

	public static String getNomeColuna(AbstractEntityPersister persister, String campo) {
		return persister.getPropertyColumnNames(campo)[0];
	}

	public static String getPK(SessionFactory sf, AbstractEntityPersister persister, String campo) {
		String persisterTableName = persister.getTableName();
		String propertyTableName = getNomeTabela(persister, campo);
		
		if (!persisterTableName.equals(propertyTableName) && persister.getMappedSuperclass() != null) {
			AbstractEntityPersister superclassPersister = (AbstractEntityPersister) sf.getClassMetadata(persister.getMappedSuperclass());
			return superclassPersister.getIdentifierColumnNames()[0];
		} else {
			return getPK(persister);
		}		
	}
	
	public static String getPK(AbstractEntityPersister persister) {
		return persister.getIdentifierColumnNames()[0];
	}
	
	private static ArrayList<ManyToOne> getFKs(SessionFactory sf, Class<?> classe) {
		AbstractEntityPersister persister = (AbstractEntityPersister) sf.getClassMetadata(classe);
		
		ArrayList<ManyToOne> fks = new ArrayList<ManyToOne>(0);
		for (Field att : classe.getDeclaredFields()) {
			if (isFK(classe, att)) {
				AbstractEntityPersister fkPersister = (AbstractEntityPersister) sf.getClassMetadata(att.getType());
				
				ManyToOne m = new ManyToOne(null);
				m.setReferencedEntityName(getNomeTabela(fkPersister));
				m.setReferencedPropertyName(getPK(fkPersister));
				m.setForeignKeyName(getNomeColuna(persister, att.getName()));
				fks.add(m);
			}
		}
		return fks;
	}

	private static ArrayList<Column> getColunas(AbstractEntityPersister persister, Class<?> classe) {
		ArrayList<Column> colunas = new ArrayList<Column>(0);
		for (Field att : classe.getDeclaredFields()) {
			if (!Modifier.isStatic(att.getModifiers())) {
				if (att.getAnnotation(Transient.class) != null || att.getAnnotation(OneToMany.class) != null
						|| getGetter(classe, att).getAnnotation(Transient.class) != null
						|| getGetter(classe, att).getAnnotation(OneToMany.class) != null)
					continue;

				Column c = new Column();
				c.setName(getNomeColuna(persister, att.getName()));
				c.setSqlType(getTipoColuna(classe, att));
				colunas.add(c);
			}
		}
		return colunas;
	}

	public static String getNomeTabela(AbstractEntityPersister persister) {
		return persister.getTableName();
	}
	
	public static String getNomeTabela(AbstractEntityPersister persister, String campo) {
		if (persister.getPropertyTableName(campo) == null) {
			return persister.getTableName();
		} else {
			return persister.getPropertyTableName(campo);
		}
	}

	private static String getTipoColuna(Class<?> classe, Field att) {
		if (att.getType().equals(String.class))
			return "varchar(50)";
		else if (att.getType().equals(Integer.class) || att.getType().equals(int.class))
			return "integer";
		else if (att.getType().equals(Long.class) || att.getType().equals(long.class))
			return "bigint";
		else if (att.getType().equals(Short.class) || att.getType().equals(short.class))
			return "smallint";
		else if (att.getType().equals(Float.class) || att.getType().equals(float.class))
			return "float";
		else if (att.getType().equals(Boolean.class) || att.getType().equals(boolean.class))
			return "boolean";
		// datas
		if (att.getType().equals(Date.class)) {
			Temporal t = att.getAnnotation(Temporal.class);
			if (t == null)
				t = getGetter(classe, att).getAnnotation(Temporal.class);
			if (t == null)
				return "timestamp without time zone";
			else if (t.value().equals(TemporalType.TIME))
				return "time without time zone";
			else if (t.value().equals(TemporalType.DATE))
				return "date";
			else if (t.value().equals(TemporalType.TIMESTAMP))
				return "timestamp without time zone";
		}
		return "integer";
	}

	private static Method getGetter(Class<?> classe, Field obj) {
			
		String nomeMetodo = "get" +WordUtils.capitalize(obj.getName());
		try {
			classe.getMethod(nomeMetodo, new Class[0]);
		} catch (Exception e) {
			if (obj.getType().equals(Boolean.class) || obj.getType().equals(boolean.class))
				nomeMetodo = "is" + WordUtils.capitalize(obj.getName());
		}
		try {
			return classe.getMethod(nomeMetodo, new Class[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// private static boolean isPK(Class<?> classe, Field att) {
	// return att.getAnnotation(Id.class) != null || getGetter(classe, att).getAnnotation(Id.class) != null;
	// }

	private static boolean isFK(Class<?> classe, Field att) {
		return !isStatic(att) && (att.getAnnotation(JoinColumn.class) != null || getGetter(classe, att).getAnnotation(JoinColumn.class) != null);
	}

	private static boolean isStatic(Field att) {
		return Modifier.isStatic(att.getModifiers());
	}
	
	public static Object getTarget(Object proxy) {
		if (proxy == null) {
			return null;
		}
		if (!(proxy instanceof HibernateProxy)) {
			return proxy;
		}
		return ((HibernateProxy) proxy).getHibernateLazyInitializer().getImplementation();
	}

	public static <U> Collection<U>  parseTo(List<Object[]> valores, String projecao,  Class<U> dominio) {
		return parseTo(valores, projecao, dominio, null);
	}	
	
	public static <U> Collection<U>  parseTo(List<Object[]> valores, String projecao,  Class<U> dominio, String alias) {
		projecao = HibernateUtils.retornaAliasFromProjecao(projecao);
		String[] atributos = StringUtils.split(projecao, ',');
		if (!isEmpty(alias)) alias += ".";
		
		valores = tratarAtributoUnico(valores, atributos);
		
		Collection<U> resposta = new ArrayList<U>(0);
		for (Object[] linha : valores) {
			U obj = ReflectionUtils.instantiateClass(dominio);
			if (linha != null) {
				for (int i = 0 ; i  < linha.length; i++) {
					atributos[i] = atributos[i].replaceAll("distinct", "").trim();
					if (!isEmpty(alias) && atributos[i].startsWith(alias)) {
						atributos[i] = atributos[i].substring(alias.length());
					}
					
					if (linha[i] != null) {
						instanciarNulos(obj, atributos[i]);
						ReflectionUtils.setProperty(obj, atributos[i].trim(), linha[i]);
					}
				}
			}
			resposta.add(obj);
		}
		return resposta;
	}

	/**
	 * Trata uma lista de valores únicos, transformando-os em arrays de objetos
	 * 
	 * @param valores
	 * @param atributos
	 * @return
	 */
	private static List<Object[]> tratarAtributoUnico(List<Object[]> valores, String[] atributos) {
		if (atributos.length == 1) {
			List<Object[]> arrayValores = new ArrayList<Object[]>();
			for (Object valor : valores) {
				arrayValores.add( new Object[] {valor});
			}
			valores = arrayValores;
		}
		return valores;
	}

	private static void instanciarNulos(Object obj, String att) {
		if (att.contains(".")) {
			String[] props = StringUtils.split(att, ".");
			for (int i = 0 ; i  < props.length; i++) {
				StringBuilder propB = new StringBuilder();
				for (int j = 0; j <= i; j++) {
					if (j > 0)
						propB.append(".");
					propB.append(props[j]);
				}
				instanciar(obj, propB.toString());
			}
		} else {
			instanciar(obj, att);
		}
	}

	private static void instanciar(Object obj, String prop) {
		Object propVal = ReflectionUtils.getProperty(obj, prop);
		
		Class<?> propClass = ReflectionUtils.getPropertyType(obj, prop);
		List<?> interfaces = ClassUtils.getAllInterfaces(propClass);
		
		boolean ehDominio = interfaces.contains(PersistDB.class) || interfaces.contains(Validatable.class);
		
		if (propVal == null && ehDominio) {
			propVal = ReflectionUtils.instantiateClass(propClass);
			ReflectionUtils.setProperty(obj, prop, propVal);
		}
	}
	
	/**
	 * Método que gera um trecho HQL ou SQL para verificar interseções entre dois intervalos
	 * de datas.
	 * 
	 * @param inicio1
	 * @param fim1
	 * @param inicio2
	 * @param fim2
	 * @return
	 */
	public static String generateDateIntersection(String inicio1, String fim1, String inicio2, String fim2) {
		return String.format("(%1$s = %3$s or (%1$s > %3$s and %1$s <= %4$s) or (%1$s < %3$s and %3$s <= %2$s))",
				inicio1, fim1, inicio2, fim2);
	}
	
	/**
	 * Limpa as entradas de uma determinada entidade do cache de segundo nível
	 * 
	 * @param sf
	 * @param classe
	 */
	public static void clearHibernateCache(SessionFactory sf, String classe) {
		sf.evictEntity(classe);
	}
	
	/**
	 * Limpa todos as entradas do cache de segundo nível
	 * 
	 * @param sf
	 */
	@SuppressWarnings("unchecked")
	public static void clearHibernateCache(SessionFactory sf) {
		Map<String, ClassMetadata> entityMap = sf.getAllClassMetadata();
		for (String entityName : entityMap.keySet()) {
			sf.evictEntity(entityName);
		}

		Map<String, CollectionMetadata> roleMap = sf.getAllCollectionMetadata();
		for (String roleName : roleMap.keySet()) {
			sf.evictCollection(roleName);
		}

		sf.evictQueries();
	}

	/**
	 * Trata uma String utilizada na projeção com alias e devolve uma String
	 * adequada para a utilização no método
	 * {@link HibernateUtils#parseTo(List, String, Class)}
	 * 
	 * @param projecao
	 * @return
	 */
	private static String retornaAliasFromProjecao(String projecao) {
		String resultado = null;
		if (projecao != null) {
			resultado = "";
			StringTokenizer tokenizer = new StringTokenizer(projecao, ",");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				int pos = token.indexOf(" as ");
				String alias = pos > 0 ? token.substring(pos + 3) : token; 
				resultado += "," + alias;
			}
			resultado = resultado.substring(1);
		}
		return resultado;
	}
	
	/**
	 * Trata uma String utilizada na projeção com alias e devolve uma String
	 * adequada para a utilização no método
	 * {@link HibernateUtils#parseTo(List, String, Class)}
	 * 
	 * @param projecao
	 * @return
	 */
	public static String removeAliasFromProjecao(String projecao) {
		String resultado = null;
		if (projecao != null) {
			resultado = "";
			StringTokenizer tokenizer = new StringTokenizer(projecao, ",");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				int pos = token.indexOf(" as ");
				String alias = pos > 0 ? token.substring(0, pos) : token; 
				resultado += "," + alias;
			}
			resultado = resultado.substring(1);
		}
		return resultado;
	}
}
