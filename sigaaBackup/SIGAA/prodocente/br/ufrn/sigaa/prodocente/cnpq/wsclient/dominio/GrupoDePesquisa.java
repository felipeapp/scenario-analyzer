/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 22/02/2013 
 */
/**
 * GrupoDePesquisa.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.ufrn.sigaa.prodocente.cnpq.wsclient.dominio;

public class GrupoDePesquisa  implements java.io.Serializable {
    private java.lang.String anoCenso;

    private java.lang.String dataAtualizacao;

    private java.lang.String[] erros;

    private java.lang.String id;

    private java.lang.String nome;

    private byte[] zipXMLs;

    public GrupoDePesquisa() {
    }

    public GrupoDePesquisa(
           java.lang.String anoCenso,
           java.lang.String dataAtualizacao,
           java.lang.String[] erros,
           java.lang.String id,
           java.lang.String nome,
           byte[] zipXMLs) {
           this.anoCenso = anoCenso;
           this.dataAtualizacao = dataAtualizacao;
           this.erros = erros;
           this.id = id;
           this.nome = nome;
           this.zipXMLs = zipXMLs;
    }


    /**
     * Gets the anoCenso value for this GrupoDePesquisa.
     * 
     * @return anoCenso
     */
    public java.lang.String getAnoCenso() {
        return anoCenso;
    }


    /**
     * Sets the anoCenso value for this GrupoDePesquisa.
     * 
     * @param anoCenso
     */
    public void setAnoCenso(java.lang.String anoCenso) {
        this.anoCenso = anoCenso;
    }


    /**
     * Gets the dataAtualizacao value for this GrupoDePesquisa.
     * 
     * @return dataAtualizacao
     */
    public java.lang.String getDataAtualizacao() {
        return dataAtualizacao;
    }


    /**
     * Sets the dataAtualizacao value for this GrupoDePesquisa.
     * 
     * @param dataAtualizacao
     */
    public void setDataAtualizacao(java.lang.String dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }


    /**
     * Gets the erros value for this GrupoDePesquisa.
     * 
     * @return erros
     */
    public java.lang.String[] getErros() {
        return erros;
    }


    /**
     * Sets the erros value for this GrupoDePesquisa.
     * 
     * @param erros
     */
    public void setErros(java.lang.String[] erros) {
        this.erros = erros;
    }

    public java.lang.String getErros(int i) {
        return this.erros[i];
    }

    public void setErros(int i, java.lang.String _value) {
        this.erros[i] = _value;
    }


    /**
     * Gets the id value for this GrupoDePesquisa.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this GrupoDePesquisa.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the nome value for this GrupoDePesquisa.
     * 
     * @return nome
     */
    public java.lang.String getNome() {
        return nome;
    }


    /**
     * Sets the nome value for this GrupoDePesquisa.
     * 
     * @param nome
     */
    public void setNome(java.lang.String nome) {
        this.nome = nome;
    }


    /**
     * Gets the zipXMLs value for this GrupoDePesquisa.
     * 
     * @return zipXMLs
     */
    public byte[] getZipXMLs() {
        return zipXMLs;
    }


    /**
     * Sets the zipXMLs value for this GrupoDePesquisa.
     * 
     * @param zipXMLs
     */
    public void setZipXMLs(byte[] zipXMLs) {
        this.zipXMLs = zipXMLs;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GrupoDePesquisa)) return false;
        GrupoDePesquisa other = (GrupoDePesquisa) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.anoCenso==null && other.getAnoCenso()==null) || 
             (this.anoCenso!=null &&
              this.anoCenso.equals(other.getAnoCenso()))) &&
            ((this.dataAtualizacao==null && other.getDataAtualizacao()==null) || 
             (this.dataAtualizacao!=null &&
              this.dataAtualizacao.equals(other.getDataAtualizacao()))) &&
            ((this.erros==null && other.getErros()==null) || 
             (this.erros!=null &&
              java.util.Arrays.equals(this.erros, other.getErros()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.nome==null && other.getNome()==null) || 
             (this.nome!=null &&
              this.nome.equals(other.getNome()))) &&
            ((this.zipXMLs==null && other.getZipXMLs()==null) || 
             (this.zipXMLs!=null &&
              java.util.Arrays.equals(this.zipXMLs, other.getZipXMLs())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAnoCenso() != null) {
            _hashCode += getAnoCenso().hashCode();
        }
        if (getDataAtualizacao() != null) {
            _hashCode += getDataAtualizacao().hashCode();
        }
        if (getErros() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getErros());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getErros(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getNome() != null) {
            _hashCode += getNome().hashCode();
        }
        if (getZipXMLs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getZipXMLs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getZipXMLs(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GrupoDePesquisa.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.servico.repositorio.cnpq.br/", "grupoDePesquisa"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("anoCenso");
        elemField.setXmlName(new javax.xml.namespace.QName("", "anoCenso"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataAtualizacao");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataAtualizacao"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("erros");
        elemField.setXmlName(new javax.xml.namespace.QName("", "erros"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nome");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nome"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zipXMLs");
        elemField.setXmlName(new javax.xml.namespace.QName("", "zipXMLs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
