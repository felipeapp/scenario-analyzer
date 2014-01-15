<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.listagem tr th { border-bottom: 1px solid #CCC; text-align: left; background-color: #BBB }
	table.listagem tr td.rotulo { background-color: #BBB }
	h3, h4 { font-size: 1.3em; font-weight: bold; text-align: center; font-variant: small-caps; }
	h4 { font-size: 1 em; margin-bottom: 15px; color: #555; }
</style>

<f:view>
	
	<h3>Comprovante de Solicita��o de Matr�cula no M�dulo Avan�ado </h3>
	<h4> Per�odo ${matriculaModuloAvancadoMBean.obj.ano}.${matriculaModuloAvancadoMBean.obj.periodo}  </h4>

	<c:set var="discente" value="#{matriculaModuloAvancadoMBean.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	
	<table class="listagem" width="100%">
		<caption>Dados da Matr�cula</caption>
		<tr>
			<td colspan="2" class="rotulo"> 1� Op��o de �nfase: ${matriculaModuloAvancadoMBean.obj.opcoesModulo[0].modulo.descricao} </td>
		</tr>
		<tr>
			<th width="15%"> Prioridade </th>
			<th> Hor�rio </th>
		</tr>
		<c:forEach items="${matriculaModuloAvancadoMBean.obj.opcoesModulo[0].opcoesHorario}" var="opcao">
			<tr>
				<td>${ opcao.ordem }</td>
				<td>${ opcao.descricao }</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="2">
				&nbsp; 
			</td>
		</tr>
		
		<c:if test="${ matriculaModuloAvancadoMBean.segundaOpcao }">
			<tr>
				<td colspan="2" class="rotulo"> 2� Op��o de �nfase: ${matriculaModuloAvancadoMBean.obj.opcoesModulo[1].modulo.descricao} </td>
			</tr>
			<tr>
				<th width="15%"> Prioridade </th>
				<th> Hor�rio </th>
			</tr>
			<c:forEach items="${matriculaModuloAvancadoMBean.obj.opcoesModulo[1].opcoesHorario}" var="opcao">
				<tr>
					<td>${ opcao.ordem }</td>
					<td>${ opcao.descricao }</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="2">
					&nbsp; 
				</td>
			</tr>
		</c:if>
		
		<c:if test="${ matriculaModuloAvancadoMBean.terceiraOpcao }">
			<tr>
				<td colspan="2" class="rotulo"> 3� Op��o de �nfase: ${matriculaModuloAvancadoMBean.obj.opcoesModulo[2].modulo.descricao} </td>
			</tr>
			<tr>
				<th width="15%"> Prioridade </th>
				<th> Hor�rio </th>
			</tr>
			<c:forEach items="${matriculaModuloAvancadoMBean.obj.opcoesModulo[2].opcoesHorario}" var="opcao">
				<tr>
					<td>${ opcao.ordem }</td>
					<td>${ opcao.descricao }</td>
				</tr>
			</c:forEach>
		</c:if>
		
		<c:if test="${ matriculaModuloAvancadoMBean.quartaOpcao }">
			<tr>
				<td colspan="2" class="rotulo"> 4� Op��o de �nfase: ${matriculaModuloAvancadoMBean.obj.opcoesModulo[3].modulo.descricao} </td>
			</tr>
			<tr>
				<th width="15%"> Prioridade </th>
				<th> Hor�rio </th>
			</tr>
			<c:forEach items="${matriculaModuloAvancadoMBean.obj.opcoesModulo[3].opcoesHorario}" var="opcao">
				<tr>
					<td>${ opcao.ordem }</td>
					<td>${ opcao.descricao }</td>
				</tr>
			</c:forEach>
		</c:if>
		
	</table>
	<br/>
	<br/>
	<div style="color: gray; text-align: center" >
		<i>Gravado em</i>: <ufrn:format type="datahorasec" valor="${matriculaModuloAvancadoMBean.obj.dataCadastro}"></ufrn:format>
		<br/>
		<i>Autentica��o</i>: ${matriculaModuloAvancadoMBean.md5}
	</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>	