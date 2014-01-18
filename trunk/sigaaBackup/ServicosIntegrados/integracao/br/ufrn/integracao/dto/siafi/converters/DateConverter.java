/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/06/2013
 * Autor:     Eric Moura
 *
 */
package br.ufrn.integracao.dto.siafi.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.ufrn.arq.util.ValidatorUtil;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Eric Moura
 * 
 */
public class DateConverter implements Converter {
	
	public boolean canConvert(Class type) {
		return type.equals(Date.class);
   }

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Date data = (Date) source;
		writer.setValue(String.valueOf(data));
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		Date data = null;
		SimpleDateFormat sdfTipo1 = new SimpleDateFormat("dd/mm/yyyy");
		try{
			data = sdfTipo1.parse(reader.getValue());
		}catch (ParseException e) {
			SimpleDateFormat sdfTipo2 = new SimpleDateFormat("yyyy-mm-ddd");
			try{
				data = sdfTipo2.parse(reader.getValue());
			}catch (ParseException e2) {
				SimpleDateFormat sdfTipo3 = new SimpleDateFormat("mm/dd/yyyy");
				try{
					data = sdfTipo3.parse(reader.getValue());
				}catch (ParseException e3) {
					e3.printStackTrace();
				}
			}
		}
		return data;
	}

	

}
