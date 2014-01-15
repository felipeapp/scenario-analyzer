<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form id="formulario">
	<h:outputText value="#{trancamentoMatricula.create}"/>

	<h2> Relat�rio de Trancamento de Matr�cula por Motivo </h2>
	
	<table class="formulario">
		<caption>Selecione as Op��es para a Gera��o do Relat�rio</caption>
		<tbody>
		<tr>
			<th class="required">Ano-Per�odo:</th>
			<td>
				<h:inputText value="#{relatorioTrancamentoTurma.ano}" id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"
				onfocus="getEl('formulario:anoPeriodoCheck').dom.checked = true;" /> - 
			    <h:inputText value="#{relatorioTrancamentoTurma.periodo}" id="periodo" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"
			    onfocus="getEl('formulario:anoPeriodoCheck').dom.checked = true;"/>
		    </td>
		</tr>
		<tr>
			<th class="required">Componente Curricular:</th>
			<td>
				<h:selectOneMenu value="#{relatorioTrancamentoTurma.idComponente}" id="componente"
				 onfocus="getEl('formulario:componenteCheck').dom.checked = true;">
					<f:selectItem itemValue="0" itemLabel="TODOS"/>
					<f:selectItems value="#{relatorioTrancamentoTurma.componenteCombo}"/>
				</h:selectOneMenu>
		    </td>
		</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton action="#{relatorioTrancamentoTurma.gerarRelatorio}" value="Gerar Relat�rio" id="gerar"/>
				<h:commandButton id="cancelar" action="#{relatorioTrancamentoTurma.cancelar}" value="Cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>