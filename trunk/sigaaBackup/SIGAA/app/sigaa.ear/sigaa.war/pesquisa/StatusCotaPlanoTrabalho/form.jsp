<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Status Cota Plano de Trabalho </h2>

	<h:form id="form">
		<table class="formulario" style="width: 50%;">
			<caption>Cota Plano de Trabalho</caption>
			<h:inputHidden value="#{ statusCotaPlanoTrabalhoMBean.obj.id }" />
			
			<tr>
				<th class="obrigatorio"> Situação </th>
				<td>
					<h:selectOneMenu id="situacao" value="#{ statusCotaPlanoTrabalhoMBean.obj.ordemStatus }" >
						<f:selectItems value="#{ statusCotaPlanoTrabalhoMBean.allCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio"> Status </th>
				<td>
					<h:selectOneMenu id="status" value="#{ statusCotaPlanoTrabalhoMBean.obj.statusPlanoTrabalho }" >
						<f:selectItems value="#{ statusCotaPlanoTrabalhoMBean.allTiposPlanoTrabalho }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="#{ statusCotaPlanoTrabalhoMBean.confirmButton }" action="#{ statusCotaPlanoTrabalhoMBean.cadastrar }" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ statusCotaPlanoTrabalhoMBean.cancelar }" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>