<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<br/>
	<h:form>
	<table class="formulario" width="30%">
	<caption>Recalcular Currículos</caption>
	<tr><th>Número de Threads: </th><td><h:inputText value="#{ recalculosMBean.numThreads }" onkeyup="return formatarInteiro(this);" size="3"/> </td></tr>
	<tr><th>Senha: </th><td><h:inputSecret value="#{ recalculosMBean.senha }" size="10"/> </td></tr>
	<tfoot>
	<tr><td colspan="2">
	<h:commandButton value="Recalcular" action="#{ recalculosMBean.recalcularCurriculos }"/>
	<h:commandButton value="Cancelar" action="#{ recalculosMBean.cancelar }"/>
	</td></tr>
	</tfoot>
	</table>
	
	<div style="width: 205px; margin: 20px auto;">
	<rich:progressBar label="#{ recalculosMBean.atualCurriculo } de #{ recalculosMBean.totalCurriculo }" minValue="0" maxValue="#{ recalculosMBean.totalCurriculo }" interval="2000" value="#{ recalculosMBean.atualCurriculo }" enabled="#{ recalculosMBean.funcionando }">
        <f:facet name="complete">
            <br />
            <h:outputText value="Recalculos finalizados" />
        </f:facet>
	</rich:progressBar>
	</div>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
