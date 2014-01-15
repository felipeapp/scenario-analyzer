<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">
	table.listagem tr.biblioteca td{
		background: #C4D2EB;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>

	<h2><ufrn:subSistema /> > Interrup��es Programadas das Bibliotecas</h2>

	<a4j:keepAlive beanName="interrupcaoBibliotecaMBean"></a4j:keepAlive>

	<h:form>
	
		<div class="descricaoOperacao" style="width:80%;">
			<p>Esta listagem exibe as interrup��es que est�o programadas para as bibliotecas no sistema.</p>
			<p><strong> IMPORTANTE: </strong> Para n�o causar transtornos aos usu�rios, somente aquelas interrup��es cadastradas que n�o causaram
			altera��o nos prazos dos empr�stimos poder�o ser removidas.</p>
		</div>

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL}  %>">
			<div class="infoAltRem" style="width:80%;">
				<h:graphicImage value="/img/adicionar.gif" />		
				<h:commandLink action="#{interrupcaoBibliotecaMBean.preCadastraNovaInterrupcao}" value="Cadastrar Nova Interrup��o" />
			
				<h:graphicImage value="/img/delete.gif" />: Remover Interrup��o
				
			</div>
		</ufrn:checkRole>
		
		<table class="listagem" style="width:80%;">
			<caption>Interrup��es Programadas (${fn:length(interrupcaoBibliotecaMBean.interrupcoesCadastradas)})</caption>
			<c:if test="${ empty interrupcaoBibliotecaMBean.interrupcoesCadastradas }">
				<tr>
					<td><p style="text-align:center;color:red;">N�o Existem interrup��es Cadastradas para nenhuma Biblioteca.</p></td>
				</tr>
			</c:if>

			<c:if test="${ not empty interrupcaoBibliotecaMBean.interrupcoesCadastradas }">
				<thead>
					<tr>
						<th style="text-align:center;width:120px;">Data</th>
						<th>Motivo</th>
						<th>Cadastrada por:</th>
						<th width="20"></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="idFiltroBibliotecaInterrupcao" scope="request" value="-1" />
					<c:forEach var="dadosInterrupcao" items="#{interrupcaoBibliotecaMBean.interrupcoesCadastradas}" varStatus="status">
						
						<c:if test="${ idFiltroBibliotecaInterrupcao != dadosInterrupcao.idBiblioteca}">
							<c:set var="idFiltroBibliotecaInterrupcao" scope="request" value="${dadosInterrupcao.idBiblioteca}" />
							<tr class="biblioteca">
								<td colspan="4">${dadosInterrupcao.descricaoBiblioteca}</td>
							</tr>
						</c:if>
						
						<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td style="text-align:center;">
								<ufrn:format type="data" valor="${dadosInterrupcao.dataInterrupcao}" />
							</td>
							<td style="width: 50%">${dadosInterrupcao.motivoInterrupcao}</td>
							<td>${dadosInterrupcao.criadoPor}</td>
							<td width="20">
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL}  %>">
									<h:commandLink title="Remover Interrup��o" action="#{interrupcaoBibliotecaMBean.remover}"
										onclick="if (!confirm('Tem certeza que deseja remover esta interrup��o para a biblioteca selecionada ?')) return false;">
										<h:graphicImage url="/img/delete.gif" alt="Remover Interrup��o" />
										<f:param name="idInterrupcaoRemocao" value="#{dadosInterrupcao.idPermisao}"></f:param>
										<f:param name="idBibliotecaRemocaoInterrupcao" value="#{dadosInterrupcao.idBiblioteca}"></f:param>
									</h:commandLink>
								</ufrn:checkRole>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center">
						<h:commandButton value="Cancelar" action="#{interrupcaoBibliotecaMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
