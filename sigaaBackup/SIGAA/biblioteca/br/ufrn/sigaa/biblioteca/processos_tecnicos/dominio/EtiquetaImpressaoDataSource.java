/*
 * EtiquetaImpressaoDataSource.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;


/**
 * Data Source para auxiliar na impressão de etiqueta usando o JasperReport
 *
 * @author Fred
 * @since 09/01/2009
 * @version 1.0 criacao da classe
 *
 */
public class EtiquetaImpressaoDataSource implements JRDataSource {

    private Iterator <List <Map <String, Object>>> itr;
    private Object valorAtual;

    /**
     * @param campos
     * @param itr
     */
    public EtiquetaImpressaoDataSource(List <List <Map <String, Object>>> lista) {
        super();
        this.itr = lista.iterator();
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        valorAtual = itr.hasNext() ? itr.next() : null;
        return valorAtual != null;
    }

    /**
     * Método que retorna o valor requerido.
     */
	@SuppressWarnings("unchecked")
	public Object getFieldValue(net.sf.jasperreports.engine.JRField campo) throws JRException {
		Object valor = null;
		
		List <Map  <String, Object>> o = (List <Map <String, Object>>) valorAtual;
		
		if (campo.getName().equals("codigoBarras1")) valor = o.get(0).get("codigoBarras");
		else if (campo.getName().equals("codigoBarras2")) valor = o.get(1).get("codigoBarras");
		else if (campo.getName().equals("codigoBarras3")) valor = o.get(2).get("codigoBarras");
		
		else if (campo.getName().equals("imagemCodigoBarras1")) valor = o.get(0).get("imagemCodigoBarras");
		else if (campo.getName().equals("imagemCodigoBarras2")) valor = o.get(1).get("imagemCodigoBarras");
		else if (campo.getName().equals("imagemCodigoBarras3")) valor = o.get(2).get("imagemCodigoBarras");
		
		else if (campo.getName().equals("tipo1")) valor = o.get(0).get("tipo");
		else if (campo.getName().equals("tipo2")) valor = o.get(1).get("tipo");
		else if (campo.getName().equals("tipo3")) valor = o.get(2).get("tipo");
		
		else if (campo.getName().equals("numeroChamada1")) valor = o.get(0).get("numeroChamada");
		else if (campo.getName().equals("numeroChamada2")) valor = o.get(1).get("numeroChamada");
		else if (campo.getName().equals("numeroChamada3")) valor = o.get(2).get("numeroChamada");

        return valor;
	}
}