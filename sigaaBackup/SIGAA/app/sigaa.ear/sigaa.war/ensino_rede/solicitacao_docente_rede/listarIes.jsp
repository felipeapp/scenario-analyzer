<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<f:view>
	<a4j:keepAlive beanName="alterarSituacaoMatriculaRede"/>
	<h2><ufrn:subSistema /> &gt; Homologar Solicitações Cadastradas
	</h2>
	
	<h:form id="form" prependId="false">
	
	<br/>
	<div class="descricaoOperacao">
		<b>Caro usuário,</b> 
		<br/><br/>
		Nesta tela serão listadas as instituições que possuem solicitações de alteração de docentes para homologar.
	</div>	
	
	<c:if test="${not empty solicitacaoDocenteRedeMBean.itens}">
		<div class="infoAltRem" style="width:90%">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar  Instituição<br />
		</div>
		
		<table class="listagem" style="width: 90%">
			<caption>Instituições com Solicitações (${fn:length(solicitacaoDocenteRedeMBean.itens)})</caption>
			<thead>
				<tr>
					<th>Instituição</th>
					<th width="2%"></th>
				</tr>
			</thead>		
			<tbody>
					<c:forEach items="#{solicitacaoDocenteRedeMBean.itens}" var="i" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${i.sigla} - ${i.nome} <span class="tremeluzir">(${i.quantidade})</span></td>
							<td style="text-align:center">
								<h:commandLink title="Selecionar Instituição" action="#{solicitacaoDocenteRedeMBean.selecionarInstituicao}">
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