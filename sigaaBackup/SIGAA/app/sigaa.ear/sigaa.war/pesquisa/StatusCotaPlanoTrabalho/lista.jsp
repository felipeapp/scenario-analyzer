<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Siglas Cadastradas para as Unidades </h2>

	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{ statusCotaPlanoTrabalhoMBean.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

	<h:form>
		<table class="formulario" width="70%">
		<caption> Lista dos Status das cotas </caption>
			
			<thead>
				<tr>
					<td> Estado do Plano de Trabalho </td>
					<td> Status </td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			
			<c:forEach var="linha" items="#{ statusCotaPlanoTrabalhoMBean.all }"  varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>${ linha.descricaoOrdemStatusCota }</td>
					<td>${ linha.descricaoStatus }</td>
					
					<td width="20">
						<h:commandLink action="#{ statusCotaPlanoTrabalhoMBean.atualizar }" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar"/>
							<f:param name="id" value="#{ linha.id }"/>
						</h:commandLink>
					</td>

					<td width="20">
						<h:commandLink action="#{ statusCotaPlanoTrabalhoMBean.remover }" onclick="#{ confirmDelete }" >
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
							<f:param name="id" value="#{ linha.id }"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>	
		
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>