<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<%@page import="br.ufrn.sigaa.ensino.jsf.DeclaracaoDisciplinasMinistradasMBean"%>

<c:set var="DOCENTE_UFRN" value="<%= DeclaracaoDisciplinasMinistradasMBean.EMITIR_DOCENTE_UFRN %>"/>
<c:set var="DOCENTE_EXTERNO" value="<%= DeclaracaoDisciplinasMinistradasMBean.EMITIR_DOCENTE_EXTERNO %>"/>

<f:view>
	<a4j:keepAlive beanName = "declaracaoDisciplinasMinistradas"/>

	<h2><ufrn:subSistema/> > Declaração de Disciplinas Ministradas </h2>

	<h:form id="form">
		<table class="formulario" width="65%">
			<caption>Buscar Docente</caption>
			
			<c:if test="${declaracaoDisciplinasMinistradas.acao eq DOCENTE_UFRN}">
				<tr>
					<th width="25%" class="required">Nome do Docente:</th>
					<td>
						<h:inputHidden id="idServidor" value="#{declaracaoDisciplinasMinistradas.parametro.docenteUFRN.id}" />
						<h:inputText id="nomeServidor" value="#{declaracaoDisciplinasMinistradas.parametro.docenteUFRN.pessoa.nome}" size="60" onkeyup="CAPS(this)" />
	
						<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
								baseUrl="/sigaa/ajaxDocente" className="autocomplete"
								indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=unidade,inativos=true"
								parser="new ResponseXmlToHtmlListParser()" />
	
						<span id="indicatorDocente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${declaracaoDisciplinasMinistradas.acao eq DOCENTE_EXTERNO}">
				<tr>
					<th width="25%" class="required">Nome do Docente:</th>
					<td>
						<h:inputHidden id="idServidorExterno" value="#{declaracaoDisciplinasMinistradas.parametro.docenteExterno.id}" />
						<h:inputText id="nomeServidorExterno" value="#{declaracaoDisciplinasMinistradas.parametro.docenteExterno.pessoa.nome}" size="60" onkeyup="CAPS(this)" />
	
						<ajax:autocomplete source="form:nomeServidorExterno" target="form:idServidorExterno"
								baseUrl="/sigaa/ajaxDocente" className="autocomplete"
								indicator="indicatorDocenteExterno" minimumCharacters="3" parameters="tipo=externo"
								parser="new ResponseXmlToHtmlListParser()" />
	
						<span id="indicatorDocenteExterno" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Emitir Declaração" action="#{ declaracaoDisciplinasMinistradas.emitir }" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ declaracaoDisciplinasMinistradas.cancelar }" immediate="true" />
					</td>
				</tr>			
			</tfoot>
		</table>
	</h:form>
	
<div class="obrigatorio">Campos de preenchimento obrigatório</div>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>