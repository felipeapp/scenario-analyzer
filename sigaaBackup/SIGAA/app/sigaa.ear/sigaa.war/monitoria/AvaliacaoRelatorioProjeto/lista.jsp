<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>
<f:view>
	<h:messages/>
	
	<%@include file="/portais/docente/menu_docente.jsp" %>	
	
	<h2><ufrn:subSistema /> > Lista de Relat�rios para Avaliar</h2>
	<br>

	<h:outputText value="#{avalRelatorioProjetoMonitoria.create}"/>
		
	<div class="infoAltRem">	
		<h:graphicImage value="/img/monitoria/form_green.png" style="overflow: visible;"/>: Visualizar Relat�rio
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Avalia��o
	    <h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Avaliar Relat�rio
	</div>
	
	<h:form>
	<table class="listagem" width="100">
	<caption>Lista de Relat�rios</caption>
	<thead>
	<tr>
		<th width="50%">T�tulo do Projeto</th>
		<th width="20%">Tipo Relat�rio</th>
		<th width="20%">Tipo Projeto</th>
		<th>Situa��o</th>
		<th></th>
		<th></th>
		<th></th>
	</tr>
	</thead>


	<c:if test="${empty avalRelatorioProjetoMonitoria.avaliacoes}">
            <tr> <td colspan="5" align="center"> <font color="red">N�o h� Relat�rios de Projetos cadastrados para sua avalia��o!</font> </td></tr>
	</c:if>

	<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA)%>" scope="application" />
	<c:set var="AVALIADO" value="<%= String.valueOf(StatusAvaliacao.AVALIADO)%>" scope="application" />

	<c:forEach items="#{avalRelatorioProjetoMonitoria.avaliacoes}" var="avaliacao">
		<c:if test="${avaliacao.statusAvaliacao.id != AVALIACAO_CANCELADA}">
			<tr>
				<td width="60%"> ${avaliacao.relatorioProjetoMonitoria.projetoEnsino.anoTitulo}</td>
				<td> ${avaliacao.relatorioProjetoMonitoria.tipoRelatorio.descricao} </td>
				<td> ${avaliacao.relatorioProjetoMonitoria.projetoEnsino.tipoProjetoEnsino.descricao} </td>
				<td> ${avaliacao.statusAvaliacao.descricao} </td>
	
				<td width="2%">
					<h:commandLink  title="Visualizar Relat�rio" 
							action="#{relatorioProjetoMonitoria.view}" style="border: 0;" 
							rendered="#{not empty avaliacao.relatorioProjetoMonitoria}">
							
					      <f:param name="id" value="#{avaliacao.relatorioProjetoMonitoria.id}"/>
					      <h:graphicImage url="/img/monitoria/form_green.png" />								      
					</h:commandLink>
				</td>
		
				<td width="2%">
					<h:commandLink  title="Visualizar Avalia��o" action="#{avalRelatorioProjetoMonitoria.view}" style="border: 0;" 
						rendered="#{!avaliacao.avaliacaoEmAberto}">
					      <f:param name="idAvaliacao" value="#{avaliacao.id}"/>
					      <h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</td>
	
				<td width="2%">
					<h:commandLink  title="Avaliar Relat�rio" action="#{avalRelatorioProjetoMonitoria.iniciarAvaliacao}" style="border: 0;" rendered="#{avaliacao.avaliacaoEmAberto}">
					      <f:param name="id" value="#{avaliacao.id}"/>
					      <h:graphicImage url="/img/seta.gif" />
					</h:commandLink>
				</td>
			</tr>
		</c:if>
			
	</c:forEach>
	</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>