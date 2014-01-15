<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:subview id="idSubView">
	
	<rich:tabPanel switchType="client">
        <rich:tab label="Mudanças de Matriz (#{fn:length(historicoDiscente.matrizes)})" >
        	<%@include file="/graduacao/discente/include/mudancas_matriz.jsp"%>
        </rich:tab>
        <rich:tab label="Mudanças de Currículo (#{fn:length(historicoDiscente.curriculos)})">
			<%@include file="/graduacao/discente/include/mudancas_curriculo.jsp"%>
        </rich:tab>     
        <rich:tab label="Mudanças de Habilitação (#{fn:length(historicoDiscente.habilitacoes)})">
        	<%@include file="/graduacao/discente/include/mudancas_habilitacao.jsp"%>
        </rich:tab>   
        <rich:tab label="Mudanças de Modalidade (#{fn:length(historicoDiscente.modalidades)})">
        	<%@include file="/graduacao/discente/include/mudancas_modalidade.jsp"%>
        </rich:tab>            
        <rich:tab label="Mudanças de Curso (#{fn:length(historicoDiscente.cursos)})">
        	<%@include file="/graduacao/discente/include/mudancas_curso.jsp"%>
        </rich:tab>
        <rich:tab label="Mudanças de Turno (#{fn:length(historicoDiscente.turnos)})">
        	<%@include file="/graduacao/discente/include/mudancas_turno.jsp"%>
        </rich:tab>
        <rich:tab label="Mudanças de Grau Acadêmico (#{fn:length(historicoDiscente.grausAcademicos)})">
        	<%@include file="/graduacao/discente/include/mudancas_grau_academico.jsp"%>
        </rich:tab>
	</rich:tabPanel>

</f:subview>