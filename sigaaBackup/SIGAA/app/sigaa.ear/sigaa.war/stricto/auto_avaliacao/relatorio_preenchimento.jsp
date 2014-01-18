<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>RELAT�RIO DE PREENCHIMENTO DA AUTO AVALIA��O DA P�S-GRADUA��O ${ calendarioAplicacaoAutoAvaliacaoMBean.nivelDescricao }</h2>
	
	<div class="parametrosRelatorio">
		<table width="100%">
			<tr>
				<th class="rotulo" width="25%">Formul�rio:</th>
				<td>${ calendarioAplicacaoAutoAvaliacaoMBean.obj.questionario.titulo }</td>
			</tr>
			<tr>
				<th class="rotulo">Per�odo de Aplica��o:</th>
				<td>
					<ufrn:format type="data" valor="${calendarioAplicacaoAutoAvaliacaoMBean.obj.dataInicio}" /> 
					a 
					<ufrn:format type="data" valor="${calendarioAplicacaoAutoAvaliacaoMBean.obj.dataFim}"/>
				</td>
			</tr>
		</table>
	</div>
	<br/>
	<c:if test="${ calendarioAplicacaoAutoAvaliacaoMBean.portalPpg }" >
		<table class="tabelaRelatorioBorda" cellspacing="1" width="100%" style="font-size: 10px;">
			<thead>
				<tr>
					<th>Programa</th>
					<th>Situa��o</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{ calendarioAplicacaoAutoAvaliacaoMBean.dadosRelatorio }" var="item">
					<tr>
						<td>${ item.unidade.nome }</td>
						<td>
							${ item.situacao }
							<h:outputText value="N�O PREENCHIDO" rendered="#{ empty item.situacao }"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	<c:if test="${ calendarioAplicacaoAutoAvaliacaoMBean.portalLatoSensu }" >
		<table class="tabelaRelatorioBorda" cellspacing="1" width="100%" style="font-size: 10px;">
			<thead>
				<tr>
					<th>Programa</th>
					<th>Situa��o</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{ calendarioAplicacaoAutoAvaliacaoMBean.dadosRelatorio }" var="item">
					<tr>
						<td>${ item.curso.nome }</td>
						<td>
							${ item.situacao }
							<h:outputText value="N�O PREENCHIDO" rendered="#{ empty item.situacao }"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>