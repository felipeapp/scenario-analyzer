<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
#operacoes-subsistema.maior {
	/*width: 500px;*/
}

#operacoes-subsistema.maior .aba{
	height: 800px;
	overflow: auto;
}

#operacoes-subsistema.maior li{
	float: left;
	width: 49%;
	margin: 5px 0;
}

#operacoes-subsistema.maior li li{
	float: left;
	width: 90%;
	margin: 1px 0;
}
-->
</style>

<f:view>

<h2><ufrn:subSistema /> > Tradução de Componente(s) do Histórico</h2> 

<h:form id="infoDiscente">
<c:set value="#{historicoTraducaoMBean.obj.discente}" var="discente" />
<%@ include file="/graduacao/info_discente.jsp"%>
</h:form>

<h:form id="historico">
	<div style="text-align: right; margin-right: 5%;">
		<i>( 
		<h:commandLink action="#{historico.selecionaDiscenteForm}" value="Ver Histórico do Discente">
			<f:param name="id" value="#{historicoTraducaoMBean.obj.discente.id}"/>
		</h:commandLink>
		)</i>
	</div>
</h:form>

	<input type="hidden" name="abaHistorico" id="abaHistorico">

	<div id="operacoes-subsistema" class="maior">

		<div id="curso" class="aba">
			<%@include file="curso.jsp"%>
		</div>
		<div id="matriz" class="aba">
			<c:choose>
			<c:when test="${!historicoTraducaoMBean.obj.discente.graduacao}">
				<i><p align="center">
					<br/><br/>
					Não há dados de Matriz Curricular para Alunos de níveis de ensino diferentes de Graduação.
					<br/><br/>
				</p></i>
			</c:when>
			<c:otherwise>
				<%@include file="matriz.jsp"%>
			</c:otherwise>
			</c:choose>
		</div>
		<div id="componentes" class="aba">
			<%@include file="componente.jsp"%>
		</div>

	</div>

<c:set var="hideSubsistema" value="true" />

</f:view>
<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        	abas.addTab('curso', "Curso");
        	abas.addTab('matriz', "Matriz Curricular");
        	abas.addTab('componentes', "Componentes");
			<c:if test="${sessionScope.abaHistorico != null && sessionScope.abaHistorico != ''}">
		    	abas.activate('${sessionScope.abaHistorico}');
		    </c:if>
		    <c:if test="${sessionScope.abaHistorico == null}">
				abas.activate('curso');
			</c:if>
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAbaHistorico(aba) {
	document.getElementById('abaHistorico').value = aba;
}

</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<c:remove var="abaHistorico" scope="session"/>