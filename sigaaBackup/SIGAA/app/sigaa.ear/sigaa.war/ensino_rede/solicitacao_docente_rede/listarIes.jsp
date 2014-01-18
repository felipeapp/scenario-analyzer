<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<f:view>
	<a4j:keepAlive beanName="alterarSituacaoMatriculaRede"/>
	<h2><ufrn:subSistema /> &gt; Homologar Solicita��es Cadastradas
	</h2>
	
	<h:form id="form" prependId="false">
	
	<br/>
	<div class="descricaoOperacao">
		<b>Caro usu�rio,</b> 
		<br/><br/>
		Nesta tela ser�o listadas as institui��es que possuem solicita��es de altera��o de docentes para homologar.
	</div>	
	
	<c:if test="${not empty solicitacaoDocenteRedeMBean.itens}">
		<div class="infoAltRem" style="width:90%">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar  Institui��o<br />
		</div>
		
		<table class="listagem" style="width: 90%">
			<caption>Institui��es com Solicita��es (${fn:length(solicitacaoDocenteRedeMBean.itens)})</caption>
			<thead>
				<tr>
					<th>Institui��o</th>
					<th width="2%"></th>
				</tr>
			</thead>		
			<tbody>
					<c:forEach items="#{solicitacaoDocenteRedeMBean.itens}" var="i" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${i.sigla} - ${i.nome} <span class="tremeluzir">(${i.quantidade})</span></td>
							<td style="text-align:center">
								<h:commandLink title="Selecionar Institui��o" action="#{solicitacaoDocenteRedeMBean.selecionarInstituicao}">
									<h:graphicImage value="/img/seta.gif"/>
									<f:param name="idCampus" value="#{i.idCampus}"></f:param>
								</h:commandLink>
							</td>
						</tr>			
					</c:forEach>
			</tbody>
		</table>
	</c:if>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>