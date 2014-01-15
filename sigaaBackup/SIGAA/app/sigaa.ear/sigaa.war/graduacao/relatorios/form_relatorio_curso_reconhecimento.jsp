<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2>Relatório de Cursos com Reconhecimento</h2>

	<h:form id="form">

		<table class="formulario" width="70%">
			<caption>Selecione um Centro</caption>
			<tr>
				<td>Centro:</td>
				<td><h:selectOneMenu
					value="#{ relatorioCursoReconhecimentoMBean.centro.id }"
					id="centro">
					<f:selectItem itemLabel="Todos os Centros" itemValue="-1" />
					<f:selectItems value="#{ unidade.centrosEspecificasEscolas }" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Gerar Relatório"
						action="#{ relatorioCursoReconhecimentoMBean.gerar }"
						id="btnEmitir" /> <h:commandButton value="Cancelar"
						action="#{ relatorioCursoReconhecimentoMBean.cancelar }"
						id="btnCancelar" /></td>
				</tr>
			</tfoot>
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
