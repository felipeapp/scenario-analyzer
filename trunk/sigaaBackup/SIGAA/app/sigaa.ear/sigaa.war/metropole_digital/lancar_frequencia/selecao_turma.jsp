<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:if test="${empty lancamentoFrequenciaIMD.tipoSelecao}">
	<h2><ufrn:subSistema /> > Lançar frequência da turma IMD > Seleção da Turma do IMD</h2> 
</c:if>
<c:if test="${not empty lancamentoFrequenciaIMD.tipoSelecao}">
	<h2><ufrn:subSistema /> > Relatório de frequência da turma IMD > Seleção da Turma do IMD</h2> 
</c:if>


<f:view>

	<h:form>
		<table class="formulario" width="100%">
			<h:inputHidden value="#{lancamentoFrequenciaIMD.tipoSelecao}"/>
			<caption class="listagem">Seleção da Turma do IMD </caption>
			
			<tr>
				<th class="required">Selecione a turma:</th>
				<td>
					<h:selectOneMenu value="#{ lancamentoFrequenciaIMD.idTurmaEntradaSelecionada }" id="id">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ portalTutoria.turmasTutoriaCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Avançar" action="#{lancamentoFrequenciaIMD.salvarTurma}" /> 
						<h:commandButton value="Cancelar" action="#{portalTutoria.voltarTelaPortal}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>

</f:view>



<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>