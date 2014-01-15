/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * created on 23/11/2011
 * 
 */

package br.ufrn.sigaa.ensino.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Este classe tem o comportamento semelhante ao {@link ResultSet}
 * 
 * @author Henrique André
 *
 */
public class ProjectionResultSet {
	
	public ProjectionResultSet(List<RowProjection> r) {
		this.result = r;
		it = r.iterator();
	}

	private List<RowProjection> result = new ArrayList<RowProjection>();
	private Iterator<RowProjection> it = null;
	private RowProjection linhaAtual;

	public boolean next() {
		
		if (!it.hasNext())
			return false;
		
		linhaAtual = it.next();
		return true;
	}	
	
	public boolean getBoolean(String columnLabel) {
		return (Boolean) linhaAtual.getValor().get(columnLabel);
	}

	public short getShort(String columnLabel) {
		return (Short) linhaAtual.getValor().get(columnLabel);
	}

	public Integer getInt(String columnLabel) {
		return (Integer) linhaAtual.getValor().get(columnLabel);
	}

	public Long getLong(String columnLabel) {
		return (Long) linhaAtual.getValor().get(columnLabel);
	}

	public Date getDate(String columnLabel) {
		return (Date) linhaAtual.getValor().get(columnLabel);
	}

	public String getString(String columnLabel) {
		return String.valueOf(linhaAtual.getValor().get(columnLabel));
	}


}
