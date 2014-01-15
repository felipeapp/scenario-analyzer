<% request.setAttribute("res1024","true"); %>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style type="text/css">
	.fonte {
		font-size: 1.3em
	}
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	
</style>

<f:view>
	<center><h3 class="fonte">MAPA DE ACESSO AO RU</h3></center>
	
	<br/>
	
	<h2 class="fonte">ALUNOS AUTORIZADOS PELO SAE</h2>

	<table width="100%">
		<tr>
			<td>
				<b>Ano - Período:</b> 
				<h:outputText value="#{diasAlimentacaoMBean.anoRefSae.ano}"/>.<h:outputText value="#{diasAlimentacaoMBean.anoRefSae.periodo}"/>	
			</td>
		</tr>
		<tr>
			<td>
				<b>Tipo de Bolsa:</b> 
				<h:outputText value="#{diasAlimentacaoMBean.tipoBolsa.denominacao}" />	
			</td>
		</tr>
		<tr>
			<td>
				<b>Deferimento:</b> 
				<h:outputText value="#{diasAlimentacaoMBean.situacao.denominacao}" />	
			</td>
		</tr>
	</table>
	<br />

	<div>
		<table class="tabelaRelatorio" width="100%">
			<thead>
			<tr>
				<th> </th>
				<th colspan="7" style="text-align: center;"> Café </th>
				<th> </th>
				<th colspan="7" style="text-align: center;"> Almoço </th>
				<th> </th>
				<th colspan="7" style="text-align: center;"> Janta </th>
				<th> </th>
				<th colspan="7" style="text-align: center;"> Refeições </th>
			</tr>
			<tr>
				<th> Aluno </th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> T. </th>
				<th style="text-align: center;"> Q. </th>
				<th style="text-align: center;"> Q. </th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> D. </th>
				<th></th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> T. </th>
				<th style="text-align: center;"> Q. </th>
				<th style="text-align: center;"> Q. </th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> D. </th>
				<th></th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> T. </th>
				<th style="text-align: center;"> Q. </th>
				<th style="text-align: center;"> Q. </th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> S. </th>
				<th style="text-align: center;"> D. </th>
				<th></th>
				<th style="text-align: center;"> TOTAL </th>
							<th></th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="item" items="#{diasAlimentacaoMBean.listagemBolsaAuxilio}">
			<c:set var ="cont" value="0"/>
				<tr>
					<td nowrap="nowrap" style="border-bottom: 1px solid; border-right: 1px solid; border-top: 1px solid;">
						${item.discente.matricula} - ${item.discente.nome}
					</td>
					<c:forEach var="tipo" items="#{item.diasAlimentacao}">
							<td style="text-align: center; border-bottom: 1px solid; border-right: 1px solid; border-top: 1px solid;">
								<c:if test="${tipo.segunda == true}">
									X
									<c:set var ="cont" value="${cont + 1}"/>
								</c:if>
							</td>
							<td style="text-align: center; border-bottom: 1px solid; border-right: 1px solid; border-top: 1px solid;">
								<c:if test="${tipo.terca == true}">
									X
									<c:set var ="cont" value="${cont + 1}"/>
								</c:if>
							</td>
							<td style="text-align: center; border-bottom: 1px solid; border-right: 1px solid; border-top: 1px solid;">
								<c:if test="${tipo.quarta == true}">
									X
									<c:set var ="cont" value="${cont + 1}"/>
								</c:if>
							</td>
							<td style="text-align: center; border-bottom: 1px solid; border-right: 1px solid; border-top: 1px solid;">
								<c:if test="${tipo.quinta == true}">
									X
									<c:set var ="cont" value="${cont + 1}"/>
								</c:if>
							</td>
							<td style="text-align: center; border-bottom: 1px solid; border-right: 1px solid; border-top: 1px solid;">
								<c:if test="${tipo.sexta == true}">
									X
									<c:set var ="cont" value="${cont + 1}"/>
								</c:if>
							</td>
							<td style="text-align: center; border-bottom: 1px solid; border-right: 1px solid; border-top: 1px solid;">
								<c:if test="${tipo.sabado == true}">
									X
									<c:set var ="cont" value="${cont + 1}"/>
								</c:if>
							</td>
							<td style="text-align: center; border-right: 1px solid; border-bottom: 1px solid; border-top: 1px solid;">
								<c:if test="${tipo.domingo == true}">
									X
									<c:set var ="cont" value="${cont + 1}"/>
								</c:if>
							</td>	
							<td style="text-align: center; border-right: 1px solid; border-bottom: 1px solid; border-top: 1px solid;">
							</td>					
					</c:forEach>
					<td style="text-align: center; border-right: 1px solid; border-bottom: 1px solid; border-top: 1px solid;">${ cont }</td>	
					<td style="text-align: center; border-right: 1px solid; border-bottom: 1px solid; border-top: 1px solid;"></td>		
				</tr>
			</c:forEach>
		</table>
		<br>
		<table class="tabelaRelatorio" width="100%" align="center">
			<tfoot>
				<tr>
					<td style="text-align: center; font-weight: bold;">
						Total: ${fn:length(diasAlimentacaoMBean.listagemBolsaAuxilio)} discentes(s) localizado(s)
					</td>
				</tr>
			</tfoot>
		</table>
	</div>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>