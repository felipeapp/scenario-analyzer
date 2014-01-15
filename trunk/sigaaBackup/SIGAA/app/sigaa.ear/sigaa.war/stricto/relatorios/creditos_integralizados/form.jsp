<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Relat�rio de Cr�ditos Integralizados</h2>

<a4j:keepAlive beanName="relatorioCreditosIntegralizadosMBean"/>
<h:form id="form">
<table class="formulario" style="width: 70%">
<caption> Informe os Crit�rios para a Emiss�o do Relat�rio </caption>
	<tbody>
	<tr>
		<c:choose>
			<c:when test="${!acesso.ppg}">
				<th><b>Programa:</b></th> 
			</c:when>
			<c:otherwise>
				<th class="obrigatorio">Programa:</th> 
			</c:otherwise>
		</c:choose>
		<td>
		<c:if test="${acesso.ppg}">
			<h:selectOneMenu id="programa" value="#{relatorioCreditosIntegralizadosMBean.unidade.id}">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
			</h:selectOneMenu>
		</c:if>
		<c:if test="${!acesso.ppg}">
			${relatorioCreditosIntegralizadosMBean.programaStricto}
		</c:if>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">N�vel:</th>
		<td>
			<h:selectOneMenu id="nivel" value="#{relatorioCreditosIntegralizadosMBean.nivel}">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{nivelEnsino.strictoCombo}"/>
			</h:selectOneMenu> 			
		</td>
	</tr>				
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton id="btEmitir" action="#{relatorioCreditosIntegralizadosMBean.gerarRelatorio}" value="Gerar Relat�rio"/>		
			<h:commandButton id="btCancelar" action="#{relatorioCreditosIntegralizadosMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
<br />
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>