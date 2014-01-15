<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>
	<h2><ufrn:subSistema /> &gt; Modalidade de Participante</h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo estão listadas as modalidades de participantes dos cursos e eventos de extensão. </p>
    	<br/>
    	<p> <strong> As modalidades de participantes são usadas para definir o valor a ser cobrado na inscrição dos participantes de curso e eventos de extensão. </strong></p> 
    	<br/>
    	<p> O coordenador da ação de extensão, no momento que vai abrir as inscrições, é que define o valor a ser cobrado para cada uma dessas modalidades. </p>
    	<br/>
		<p> <strong>Observação:</strong> Deve existir pelo menos uma modalidade única para ser associada àqueles cursos e eventos em que todos os participantes pagam o mesmo valor.</p>
	</div>
	
	<a4j:keepAlive beanName="modalidadeParticipanteMBean" />
	
	<h:form id="formListagemModalidadesParticipantes">

		<div class="infoAltRem" style="width:70%;">
			
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_EXTENSAO} %>">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{modalidadeParticipanteMBean.preCadastrar}" value="Cadastrar" />
			
			
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Modalidade de Participante
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Modalidade de Participante
			
			</ufrn:checkRole>
		</div> 

		<table class="listagem" style="width: 70%;">
			<caption class="listagem">Molidades de Participantes Existentes (${modalidadeParticipanteMBean.size})</caption>
			<thead>
				<tr>
					<th style="width: 98%;">Nome</th>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_EXTENSAO} %>">
						<th style="width: 1%;"></th>
						<th style="width: 1%;"></th>
					</ufrn:checkRole>
				</tr>
			</thead>
			
			<c:forEach items="#{modalidadeParticipanteMBean.all}" var="modalidade" varStatus="status">
				
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
					<td>${modalidade.nome}</td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_EXTENSAO } %>">
						<td>
							<h:commandLink action="#{modalidadeParticipanteMBean.preAtualizar}" title="Alterar Modalidade de Participante">
								<f:param name="id" value="#{modalidade.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Modalidade de Participante" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink  action="#{modalidadeParticipanteMBean.remover}" title="Remover Modalidade de Participante" 
										onclick="return confirm('Confirma Remoção dessa Modalidade? ');">
								<f:param name="id" value="#{modalidade.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Modalidade de Participante" />
							</h:commandLink>
						</td>
					</ufrn:checkRole>
					
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{modalidadeParticipanteMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>